package functlyser.model;


import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Max;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Document
public class Profile {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private Date createdOn = new Date();

    private Map<String, ProfileInfo> columns;

    private String outputColumn;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Map<String, ProfileInfo> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, ProfileInfo> columns) {
        this.columns = columns;
    }

    public String getOutputColumn() {
        return outputColumn;
    }

    public void setOutputColumn(String outputColumn) {
        this.outputColumn = outputColumn;
    }
}
