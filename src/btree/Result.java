package btree;

public class Result {

    private BTree.Node node;//指向找到的结点

    private int index;//[1...m],在结点中，关键字的序号

    private boolean isSuccess;//是否查找成功

    public Result(BTree.Node node, int index, boolean isSuccess) {
        this.node = node;
        this.index = index;
        this.isSuccess = isSuccess;
    }

    public BTree.Node getNode() {
        return node;
    }

    public void setNode(BTree.Node node) {
        this.node = node;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
