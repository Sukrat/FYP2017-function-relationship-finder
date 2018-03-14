package functlyser.model;

import java.util.List;

public class GridData extends Entity {

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
