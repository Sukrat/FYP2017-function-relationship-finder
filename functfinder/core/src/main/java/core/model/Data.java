package core.model;

import core.arango.Entity;

import java.util.Map;

import static java.lang.String.format;

public class Data extends Entity {

    private String fileName;

    private Map<String, Double> rawColumns;

    private Map<String, Double> workColumns;

    public static String prefixColumn = "col";

    public static String colName(int num) {
        return format("%s%d", prefixColumn, num);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Map<String, Double> getRawColumns() {
        return rawColumns;
    }

    public void setRawColumns(Map<String, Double> rawColumns) {
        this.rawColumns = rawColumns;
    }

    public Map<String, Double> getWorkColumns() {
        return workColumns;
    }

    public void setWorkColumns(Map<String, Double> workColumns) {
        this.workColumns = workColumns;
    }
}
