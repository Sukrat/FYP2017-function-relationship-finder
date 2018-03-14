package functlyser.model;

import java.util.Map;

import static java.lang.String.format;

public class Data extends Entity {
    public static String prefixColumn = "col";

    private String fileName;

    private Map<String, Double> columns;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Map<String, Double> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, Double> columns) {
        this.columns = columns;
    }

    public static String colName(int num) {
        return format("%s%d", prefixColumn, num);
    }
}
