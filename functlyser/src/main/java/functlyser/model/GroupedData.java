package functlyser.model;

import java.util.List;

public class GroupedData extends Entity {

    private List<Long> gridIndex;

    private List<Data> dataMembers;

    public List<Long> getGridIndex() {
        return gridIndex;
    }

    public void setGridIndex(List<Long> gridIndex) {
        this.gridIndex = gridIndex;
    }

    public List<Data> getDataMembers() {
        return dataMembers;
    }

    public void setDataMembers(List<Data> dataMembers) {
        this.dataMembers = dataMembers;
    }
}
