package functlyser.service;

import functlyser.BaseSpringTest;
import functlyser.Faker;
import functlyser.exception.ApiException;
import functlyser.model.Data;
import functlyser.model.GroupedData;
import functlyser.model.Regression;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;


public class GridServiceTest extends BaseSpringTest {

    @Autowired
    private GridService sut;

    @Test
    public void testGroupByNdimensionAndInsert() {
        List<Double> tolerances = Arrays.asList(2.0, 2.0, 2.0, 2.0, 2.0);
        List<Data> perfectDataFor = getPerfectDataFor(20, "test.csv", 5);
        arangoOperation.insert(perfectDataFor, Data.class);

        long result = sut.groupByNdimensionAndInsert(tolerances);

        assertTrue(arangoOperation.collection(GroupedData.class).exists());
        assertThat(result, is(4L));
        assertThat(arangoOperation.collection(GroupedData.class).count().getCount(), is(4L));
    }

    @Test
    public void testGroupByNdimensionAndInsert_withZeroTolerance() {
        List<Double> tolerances = Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0);
        List<Data> perfectDataFor = getPerfectDataFor(20, "test.csv", 5);
        arangoOperation.insert(perfectDataFor, Data.class);

        long result = sut.groupByNdimensionAndInsert(tolerances);

        assertTrue(arangoOperation.collection(GroupedData.class).exists());
        assertThat(result, is(4L));
        assertThat(arangoOperation.collection(GroupedData.class).count().getCount(), is(4L));
    }

    @Test
    public void testGroupByNdimensionAndInsert_withNegativeTolerance() {
        List<Double> tolerances = Arrays.asList(-1.0, -1.0, -1.0, -1.0, -1.0);
        List<Data> perfectDataFor = getPerfectDataFor(20, "test.csv", 5);
        arangoOperation.insert(perfectDataFor, Data.class);

        long result = sut.groupByNdimensionAndInsert(tolerances);

        assertTrue(arangoOperation.collection(GroupedData.class).exists());
        assertThat(result, is(4L));
        assertThat(arangoOperation.collection(GroupedData.class).count().getCount(), is(4L));
    }

    @Test
    public void testGroupByNdimensionAndInsert_mustTruncateBeforeInsertingAgain() {
        List<Double> tolerances = Arrays.asList(1.0, 1.0, 1.0, 1.0, 1.0);
        List<Data> perfectDataFor = getPerfectDataFor(30, "test.csv", 5);
        arangoOperation.insert(perfectDataFor, Data.class);

        sut.groupByNdimensionAndInsert(tolerances);
        long result = sut.groupByNdimensionAndInsert(tolerances);

        assertTrue(arangoOperation.collection(GroupedData.class).exists());
        assertThat(result, is(6L));
        assertThat(arangoOperation.collection(GroupedData.class).count().getCount(), is(6L));
    }

    @Test(expected = ApiException.class)
    public void testGroupByNdimensionAndInsert_ThrowIfToleranceAndColumnSizeNotSame() {
        List<Double> tolerances = Arrays.asList(1.0, 1.0, 1.0, 1.0, 1.0);
        List<Data> perfectDataFor = getPerfectDataFor(10, "test.csv", 7);
        arangoOperation.insert(perfectDataFor, Data.class);

        sut.groupByNdimensionAndInsert(tolerances);
    }

    @Test(expected = ApiException.class)
    public void testGroupByNdimensionAndInsert_ThrowIfNoData() {
        List<Double> tolerances = Arrays.asList(0.5, 0.5, 0.5, 0.5, 0.5);

        sut.groupByNdimensionAndInsert(tolerances);
    }

    @Test
    public void testGetFunctionTerminator() {
        List<Data> perfectDataFor = getPerfectDataFor(30, "test.csv", 5);
        arangoOperation.insert(perfectDataFor, Data.class);
        GroupedData groupedData = getPerfectGroupedData(Arrays.asList(4l, 5l, 6l));
        arangoOperation.insert(groupedData);
        groupedData = getPerfectGroupedData(Arrays.asList(5l, 6l, 7l));
        arangoOperation.insert(groupedData);

        List<GroupedData> result = sut.getFunctionTerminator(2.0);

        assertThat(result.size(), is(0));
    }

    @Test
    public void testGetFunctionTerminator_WhereItIsNotAFunction() {
        List<Data> perfectDataFor = getPerfectDataFor(30, "test.csv", 5);
        arangoOperation.insert(perfectDataFor, Data.class);
        GroupedData groupedData = getPerfectGroupedData(Arrays.asList(4l, 5l, 6l));
//        groupedData.getDataMembers().get(0).getColumns().set(0, 5.0);
        arangoOperation.insert(groupedData);
        GroupedData groupedData2 = getPerfectGroupedData(Arrays.asList(5l, 6l, 7l));
        arangoOperation.insert(groupedData2);

        List<GroupedData> result = sut.getFunctionTerminator(2.0);

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(groupedData.getId()));
    }

    @Test(expected = ApiException.class)
    public void testGetFunctionTerminator_NoDataShouldThrow() {
        List<GroupedData> result = sut.getFunctionTerminator(2.0);
    }

    @Test(expected = ApiException.class)
    public void testGetFunctionTerminator_NoGridDataShouldThrow() {
        List<Data> perfectDataFor = getPerfectDataFor(30, "test.csv", 5);
        arangoOperation.insert(perfectDataFor, Data.class);

        List<GroupedData> result = sut.getFunctionTerminator(2.0);

        assertThat(result.size(), is(1));
    }

    @Test
    public void testAnalyseColumn() {
        List<Data> perfectDataFor = getPerfectDataFor(30, "test.csv", 5);
        arangoOperation.insert(perfectDataFor, Data.class);
        GroupedData groupedData = getPerfectGroupedData(Arrays.asList(4l, 5l, 6l));
        arangoOperation.insert(groupedData);
        groupedData = getPerfectGroupedData(Arrays.asList(5l, 6l, 7l));
        arangoOperation.insert(groupedData);

        List<Regression> result = sut.analyseColumn(1);

        assertThat(result.size(), is(2));
        assertTrue(result.stream().allMatch(m->m.getCol() == 1));
    }

    @Test(expected = ApiException.class)
    public void testAnalyseColumn_throwWhenIndexMoreThanColumns() {
        List<Data> perfectDataFor = getPerfectDataFor(30, "test.csv", 4);
        arangoOperation.insert(perfectDataFor, Data.class);
        GroupedData groupedData = getPerfectGroupedData(Arrays.asList(4l, 5l, 6l));
        arangoOperation.insert(groupedData);
        groupedData = getPerfectGroupedData(Arrays.asList(5l, 6l, 7l));
        arangoOperation.insert(groupedData);

        List<Regression> result = sut.analyseColumn(4);
    }

    @Test(expected = ApiException.class)
    public void testAnalyseColumn_throwWhenIndexLessThanColumns() {
        List<Data> perfectDataFor = getPerfectDataFor(30, "test.csv", 5);
        arangoOperation.insert(perfectDataFor, Data.class);
        GroupedData groupedData = getPerfectGroupedData(Arrays.asList(4l, 5l, 6l));
        arangoOperation.insert(groupedData);
        groupedData = getPerfectGroupedData(Arrays.asList(5l, 6l, 7l));
        arangoOperation.insert(groupedData);

        List<Regression> result = sut.analyseColumn(-1);
    }

    @Test(expected = ApiException.class)
    public void testAnalyseColumn_throwWhenIndexIsOutputColumns() {
        List<Data> perfectDataFor = getPerfectDataFor(30, "test.csv", 5);
        arangoOperation.insert(perfectDataFor, Data.class);
        GroupedData groupedData = getPerfectGroupedData(Arrays.asList(4l, 5l, 6l));
        arangoOperation.insert(groupedData);
        groupedData = getPerfectGroupedData(Arrays.asList(5l, 6l, 7l));
        arangoOperation.insert(groupedData);

        List<Regression> result = sut.analyseColumn(0);
    }


    private List<Data> getPerfectDataFor(int num, String filename) {
        return getPerfectDataFor(num, filename, 3);
    }

    private List<Data> getPerfectDataFor(int num, String filename, int numColumn) {
        List<Data> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Data data = new Data();
            data.setFileName(filename);
//            data.setColumns(new ArrayList<>());
            for (int j = 0; j < numColumn; j++) {
//                data.getColumns().add(j + Faker.nextDouble() + i / 5);
            }
            list.add(data);
        }
        return list;
    }

    private GroupedData getPerfectGroupedData(List<Long> gridIndex) {
        GroupedData groupedData = new GroupedData();
        groupedData.setGridIndex(gridIndex);
        List<Data> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Data data = new Data();
            data.setFileName("asdf");
//            data.setColumns(new ArrayList<>());
//            data.getColumns().add(Faker.nextDouble());
//            for (int j = 0; j < gridIndex.size(); j++) {
//                data.getColumns().add(gridIndex.get(j) + Faker.nextDouble());
//            }
            list.add(data);
        }
        groupedData.setDataMembers(list);
        return groupedData;
    }
}
