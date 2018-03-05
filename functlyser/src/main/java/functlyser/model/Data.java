package functlyser.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document
public class Data {

    @Id
    private String id;

    private ObjectId profileId;

    private String fileName;

    private Map<String, Double> columns;

    public String getId() {
        return id;
    }

    public ObjectId getProfileId() {
        return profileId;
    }

    public void setProfileId(ObjectId profileId) {
        this.profileId = profileId;
    }

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
}
