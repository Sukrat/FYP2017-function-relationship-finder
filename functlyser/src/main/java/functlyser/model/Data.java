package functlyser.model;

import java.util.List;

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
