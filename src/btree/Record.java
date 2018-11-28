package btree;

/**
 * B树结果记录
 */
public class Record {
    //键
    private int key;
    //值
    private String value;

    public Record(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Record(" +
                "key=" + key +
                ", value='" + value + '\'' +
                ')';
    }
}
