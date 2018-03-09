package functlyser.service;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import functlyser.exception.ApiException;
import functlyser.model.Data;
import functlyser.model.GroupedData;
import functlyser.model.Regression;
import functlyser.repository.ArangoOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import java.security.acl.Group;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@Component
public class GridService extends Service {

    private ArangoOperation arangoOperation;

    @Autowired
    public GridService(ArangoOperation arangoOperation) {
        this.arangoOperation = arangoOperation;
    }

    public long groupByNdimensionAndInsert(List<Double> tolerances) {
        Data any = arangoOperation.findAny(Data.class);
        if (any == null) {
            throw new ApiException("No data found in the database!");
        }
        if (any.getColumns().size() != tolerances.size()) {
            throw new ApiException(
                    format("Number of tolerance must be equal to the data columns. (expected: %d actual: %d)",
                            any.getColumns().size(), tolerances.size()));
        }

        // deleting all the records in grouped data
        arangoOperation.collection(GroupedData.class).truncate();

        StringBuilder builder = new StringBuilder();
        builder.append("FOR r in @@collection\n");
        builder.append("COLLECT grid_index = [ \n");
        // ignoring first tolerance as that is for ouput column
        for (int i = 1; i < tolerances.size(); i++) {
            builder.append(format("FLOOR(r.columns[%1$d] / @tolerance%1$d)", i));
            if (i < tolerances.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append(" ] INTO values = r\n");
        builder.append("INSERT {\n");
        builder.append("gridIndex: grid_index, \n");
        builder.append("dataMembers: values \n");
        builder.append("} INTO @@newCollection\n");
        builder.append("RETURN { count: 1}\n");

        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@collection", arangoOperation.collectionName(Data.class));
        bindVar.put("@newCollection", arangoOperation.collectionName(GroupedData.class));
        // ignoring first tolerance as that is for ouput column
        for (int i = 1; i < tolerances.size(); i++) {
            bindVar.put(format("tolerance%1$d", i), fixTolerance(tolerances.get(i)));
        }

        ArangoCursor<BaseDocument> datas = arangoOperation.query(builder.toString(), bindVar, BaseDocument.class);

        return datas.asListRemaining().size();
    }

    public List<GroupedData> getFunctionTerminator(double tolerance) {
        Data any = arangoOperation.findAny(Data.class);
        if (any == null) {
            throw new ApiException("No data found in the database!");
        }
        GroupedData anyGrouped = arangoOperation.findAny(GroupedData.class);
        if (anyGrouped == null) {
            throw new ApiException("Data has not been grouped!");
        }

        StringBuilder builder = new StringBuilder();
        builder.append("FOR r in @@collection\n");
        builder.append("FILTER COUNT(r.dataMembers) > 1\n");
        builder.append("LET grouped_y = \n")
                .append("( FOR member IN r.dataMembers\n")
                .append("COLLECT y = FLOOR(member.columns[0] / @tolerance) INTO values\n")
                .append("RETURN { values: values} )\n");
        builder.append("FILTER COUNT(grouped_y) > 1\n");
        builder.append("RETURN r\n");

        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@collection", arangoOperation.collectionName(GroupedData.class));
        bindVar.put("tolerance", fixTolerance(tolerance));
        ArangoCursor<GroupedData> datas = arangoOperation.query(builder.toString(), bindVar, GroupedData.class);
        return datas.asListRemaining();
    }

    public List<Regression> analyseColumn(int column) {
        Data any = arangoOperation.findAny(Data.class);
        if (any == null) {
            throw new ApiException("No data found in the database!");
        }
        GroupedData anyGrouped = arangoOperation.findAny(GroupedData.class);
        if (anyGrouped == null) {
            throw new ApiException("Data has not been grouped!");
        }
        if (column == 0) {
            throw new ApiException("Choose parameter column to be analyser cannot analyse output!");
        }
        if (column < 0 || column >= any.getColumns().size()) {
            throw new ApiException(format("Column number doesnot exist! (Expected: < %d and > 0 and got: )",
                    any.getColumns().size(), column));
        }

        StringBuilder builder = new StringBuilder();
        builder.append("FOR r IN @@collection\n");
        builder.append("COLLECT index = APPEND(SLICE(r.gridIndex, 0, @col-1), SLICE(r.gridIndex, @col))\n");
        builder.append("INTO list = r.dataMembers\n");
        builder.append("let members = FLATTEN(list)\n");
        builder.append("FILTER COUNT(members) > 1\n");
        builder.append("let v = ( FOR m IN members COLLECT AGGREGATE\n")
                .append("sX = SUM(m.columns[@col]),\n")
                .append("sY = SUM(m.columns[0]),\n")
                .append("sXX = SUM(POW(m.columns[@col], 2)),\n")
                .append("sXY = SUM(m.columns[@col] * m.columns[0]),\n")
                .append("n = LENGTH(m)\n")
                .append("return { \n")
                .append("sX: sX,\n")
                .append("sY: sY,\n")
                .append("sXX: sXX,\n")
                .append("sXY: sXY,\n")
                .append("n: n\n")
                .append("})[0]\n");
        // ax + b = y where a is a1/a2 and b is b1/b2
        builder.append("let a1 = (v.sX * v.sY) - (v.n * v.sXY)\n");
        builder.append("let a2 = pow(v.sX, 2) - (v.n * v.sXX)\n");
        builder.append("let b1 = (v.sX * v.sXY) - (v.sXX * v.sY)\n");
        builder.append("let b2 = pow(v.sX, 2) - (v.n * v.sXX)\n");
        builder.append("RETURN {\n");
        builder.append("col: @col,\n");
        builder.append("a: a1 / a2,\n");
        builder.append("b: b1 / b2\n");
        builder.append("}\n");

        Map<String, Object> bindVar = new HashMap<>();
        bindVar.put("@collection", arangoOperation.collectionName(GroupedData.class));
        bindVar.put("col", column);
        ArangoCursor<Regression> datas = arangoOperation.query(builder.toString(), bindVar, Regression.class);

        return datas.asListRemaining();
    }

    private double fixTolerance(double tolerance) {
        return tolerance == 0.0 ? 1.0 : Math.abs(tolerance);
    }
}
