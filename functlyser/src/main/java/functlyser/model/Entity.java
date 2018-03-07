package functlyser.model;

import com.arangodb.entity.DocumentField;

public class Entity {

    @DocumentField(DocumentField.Type.KEY)
    protected String key;

    @DocumentField(DocumentField.Type.ID)
    protected String id;

    @DocumentField(DocumentField.Type.REV)
    protected String rev;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

}
