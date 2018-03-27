package webapp.model;

import com.arangodb.entity.DocumentEntity;
import com.arangodb.springframework.annotation.Document;

import java.util.List;

@Document
public class GridData extends DocumentEntity {

    private List<Long> boxIndex;

    private List<String> members;

    public List<Long> getBoxIndex() {
        return boxIndex;
    }

    public void setBoxIndex(List<Long> boxIndex) {
        this.boxIndex = boxIndex;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
