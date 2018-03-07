package functlyser.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

public class Data extends Entity {

    private String fileName;

    private List<Double> columns;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<Double> getColumns() {
        return columns;
    }

    public void setColumns(List<Double> columns) {
        this.columns = columns;
    }
}
