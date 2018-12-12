package btree;

public class BTree {

    private Node root;//根结点

    public static class Node {

        private int m;//阶层

        private int keyNum;//关键字的数量

        private Record[] records;//记录集合,0号单元闲置

        private Node parent;//指向双亲结点

        private Node[] child;//孩子结点

        public Node(int m) {
            this.m = m;
            this.child = new Node[m + 1];
            this.records = new Record[m + 1];
        }

        public int getM() {
            return m;
        }

        public int getMin() {
            return (m - 1) / 2;
        }

        public int getMax() {
            return m - 1;
        }

        public int getKeyNum() {
            return keyNum;
        }

        public void setKeyNum(int keyNum) {
            this.keyNum = keyNum;
        }

        public Record[] getRecords() {
            return records;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public Node[] getChild() {
            return child;
        }
    }

    /**
     * 构建一个m阶B树
     * @param m
     */
    public BTree(int m) {
        this.root = new Node(m);
    }

    /**
     * 返回B树中的结点数
     * @return
     */
    private int size(Node node) {
        if (node == null) {
            return 0;
        }
        int num = 0;
        //遍历拿到子树的元素个数
        for (int i = 0; i <= node.getKeyNum(); i++) {
            num += size(node.getChild()[i]);
        }
        num += node.getKeyNum();
        return num;
    }

    /**
     * 返回B树中的结点数
     * @return
     */
    public int size() {
        return size(root);
    }

    /**
     * 存在对应的Record,返回该record,否则返回null。
     * @param key
     * @return 对应的record or null
     */
    public Record searchBTree(int key) {
        Result result = searchBTree(root, key);
        if (result.isSuccess()) {//查找到了
            return result.getNode().getRecords()[result.getIndex()];
        }
        return null;
    }

    /**
     * 在m阶B树tree查找关键字key,用result返回
     * 若查找成功，则标记isSuccess=true，指针pt所指结点中的第index个记录为所查记录
     * 否则isSuccess=false，若要插入关键字为key的记录，应位于pt结点中第i-1个和第i个关键字之间
     * @param node
     * @param key
     * @return
     */
    private Result searchBTree(Node node, int key) {
        int i = 0;
        boolean found = false;
        //初始，p指向根结点；p将用于指向待查结点，q指向其双亲
        Node p = node;
        Node q = null;

        while (p != null && !found) {
            //在keys[1...p.keyNum]中查找key
            i = searchInNode(p, key);
            //找到待查关键字
            if (i <= p.getKeyNum() && p.getRecords()[i].getKey() == key) {
                found = true;
            } else {
                q = p;
                p = p.getChild()[i - 1];
            }
        }

        if (found) {
            return new Result(p, i, true);
        } else {
            return new Result(q, i, false);
        }
    }

    /**
     * 在keys[1...p.keyNum]中查找key(折半查找)
     * @param node
     * @param key
     * @return
     */
    private int searchInNode(Node node, int key) {
        int low = 1;
        int high = node.getKeyNum();
        int mid;

        while (low <= high) {
            mid = (low + high) / 2;
            if (key == node.getRecords()[mid].getKey()) {
                return mid;
            } else if (key < node.getRecords()[mid].getKey()){
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return low;
    }

    /**
     * 将对应的记录插入到B树中
     * 如果B树中不存在给定的项,插入并返回true
     * 否则更新并返回false
     * @param record
     * @return
     */
    public boolean insertBTree(Record record) {
        if (record == null) {
            return false;
        }
        Result result = searchBTree(root, record.getKey());
        if (result.isSuccess()) {
            updateRecord(result.getNode(), result.getIndex(), record);
            return false;
        } else {
            insertBTree(result.getNode(), result.getIndex(), record);
            return true;
        }
    }

    /**
     * 更新查找到的结果
     * @param node
     * @param index
     * @param record
     */
    private void updateRecord(Node node, int index, Record record) {
        node.getRecords()[index] = record;
    }

    private void insertBTree(Node node, int index, Record record) {
        boolean finished = false;
        boolean needNewRoot = false;

        if (null == node) {
            //需要生成新的结点
            newRoot(node, record, null);
        } else {
            Node aq = null;
            while (!needNewRoot && !finished) {
                //record0和ap分别插入到node.records[i]和node.child[i]
                insertInNode(node, index, record, aq);
                if (node.getKeyNum() <= node.getMax()) {
                    finished = true;
                } else {
                    //分裂node结点
                    int s = (node.getM() + 1) / 2;
                    aq = splitNode(node, s);
                    record = node.getRecords()[s];
                    if (node.getParent() != null) {
                        node = node.getParent();
                        //在双亲结点中查找record的插入位置
                        index = searchInNode(node, record.getKey());
                    } else {
                        needNewRoot = true;
                    }
                }
            }
            //tree是空树或者根结点已分裂为q和ap结点
            if (needNewRoot) {
                newRoot(node, record, aq);
            }
        }
    }

    /**
     * 生成新的根结点
     * @param left 左子树
     * @param record 记录
     * @param right 右子树
     */
    private void newRoot(Node left, Record record, Node right) {
        root = new Node(root.getM());
        root.setKeyNum(1);
        root.getRecords()[1] = record;
        root.getChild()[0] = left;
        root.getChild()[1] = right;

        if (left != null)left.setParent(root);
        if (right != null) right.setParent(root);
    }

    /**
     * record和ap分别插入到node.records[i]和node.child[i]
     * @param node 结点
     * @param index 位置
     * @param record 记录
     * @param child 孩子结点
     */
    private void insertInNode(Node node, int index, Record record, Node child) {
        for (int j = node.getKeyNum(); j >= index; j--) {
            node.getRecords()[j + 1] = node.getRecords()[j];
            node.getChild()[j + 1] = node.getChild()[j];
        }
        //插入
        node.getRecords()[index] = record;
        node.getChild()[index] = child;
        if (child != null) child.setParent(node);
        node.setKeyNum(node.getKeyNum() + 1);
    }

    /**
     * 将src结点分裂成两个结点，前一半保留在原结点，后一半移入target
     * @param oldNode 原节点
     * @param s 分裂的位标
     * @return 返回新生成的结点
     */
    private Node splitNode(Node oldNode, int s) {
        Node newNode = new Node(oldNode.getM());

        //后一半移入target结点
        int keyNum = oldNode.getKeyNum();
        int i,j;
        for (i = s + 1, j = 1; i <= keyNum; i++, j++) {
            newNode.getRecords()[j] = oldNode.getRecords()[i];
            newNode.getChild()[j] = oldNode.getChild()[i];
        }

        newNode.setKeyNum(keyNum - s);
        newNode.setParent(oldNode.getParent());

        //修改新结点的子结点的parent域
        for (i = 0; i <= newNode.getKeyNum(); i++) {
            if (newNode.getChild()[i] != null) {
                newNode.getChild()[i].setParent(newNode);
            }
        }

        //修改原的keyNum
        oldNode.setKeyNum(s - 1);
        return newNode;
    }

    /**
     * 如果B树中存在给定键关联的项，则返回删除的项，否则NULL。
     * @param key
     * @return
     */
    public Record deleteBTree(int key) {
        boolean result = deleteNode(root, key);
        if (!result) {
            System.out.println("关键字" + key + "不在B树中");
        } else if (root.getKeyNum() == 0) {
            root = root.getChild()[0];
        }
        return null;
    }

    /**
     * 在结点node中查找并删除关键字为key的记录
     * @param node
     * @param key
     * @return
     */
    private boolean deleteNode(Node node, int key) {
        if (node == null) {
            return false;
        } else {
            Result result = findInNode(node, key);
            int i = result.getIndex();
            boolean foundTag = result.isSuccess();

            if (foundTag) {
//                if (node.getChild()[i - 1] != null) { 出现删除错误，修改一下寻找替换值的条件
                if (node.getChild()[i] != null) {
                    substitution(node, i);
                    deleteNode(node.getChild()[i], node.getRecords()[i].getKey());
                } else {
                    removeInNode(node, i);
                }
            } else {
                foundTag = deleteNode(node.getChild()[i], key);
            }

            if (node.getChild()[i] != null) {
                if (node.getChild()[i].getKeyNum() < node.getMin()) {
                    adjustBTree(node, i);
                }
            }
            return foundTag;
        }
    }


    /**
     * 反映是否在结点p中是否查找关键字i
     * @param node
     * @param key
     * @return
     */
    private Result findInNode(Node node, int key) {
        if (key < node.getRecords()[1].getKey()) {
            return new Result(node, 0, false);
        } else {
            int low = 1;
            int high = node.getKeyNum();
            int mid;

            while (low <= high) {
                mid = (low + high) / 2;
                if (key == node.getRecords()[mid].getKey()) {
                    return new Result(node, mid, true);
                } else if (key < node.getRecords()[mid].getKey()) {
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            }
            return new Result(node, low - 1, false);
        }
    }

    /**
     * 从结点中删除node.records[index]和他的孩子node.child[index]
     * @param node
     * @param index
     */
    private void removeInNode(Node node, int index) {
        for (int i = index + 1; i <= node.getKeyNum(); i++) {
            node.getRecords()[i - 1] = node.getRecords()[i];
            node.getChild()[i - 1] = node.getChild()[i];
        }
        node.setKeyNum(node.getKeyNum() - 1);
    }

    /**
     * 查找被删关键字node.record[index](在非叶子结点中的)替代叶子节点(右子树中值最小的记录)
     * @param node
     * @param index
     */
    private void substitution(Node node, int index) {
        Node q;
        q = node.getChild()[index];
        while (q.getChild()[0] != null) {
            q = q.getChild()[0];
        }
        //复制关键值
        node.getRecords()[index] = q.getRecords()[1];
    }

    /**
     * 将双亲结点p中的最后一个关键字移入右结点中q中
     * 将左结点ap中的最后一个关键字移入双亲结点中
     * @param node
     * @param index
     */
    private void moveRight(Node node, int index) {
        Node q = node.getChild()[index];//右结点
        Node aq = node.getChild()[index - 1];//左结点

        //将右兄弟q中所有关键字向后移一位
        for (int i = q.getKeyNum(); i > 0; i--) {
            q.getRecords()[i + 1] = q.getRecords()[i];
            q.getChild()[i + 1] = q.getChild()[i];
        }

        //从双亲结点中移动关键字到q中
        q.getChild()[1] = q.getChild()[0];
        q.getRecords()[1] = node.getRecords()[index];
        q.setKeyNum(q.getKeyNum() + 1);

        //将左兄弟aq中最后一个结点移到双亲结点中
        node.getRecords()[index] = aq.getRecords()[aq.getKeyNum()];
        q.getChild()[0] = aq.getChild()[aq.getKeyNum()];
        aq.setKeyNum(aq.getKeyNum() - 1);
    }

    /**
     * 将双亲结点p中的第一个关键字移入左结点ap中
     * 将右结点p中的第一个关键字移入双亲结点中
     * @param node
     * @param index
     */
    private void moveLeft(Node node, int index) {
        Node q = node.getChild()[index];//右结点
        Node aq = node.getChild()[index - 1];//左结点

        //把双亲结点中的关键字移动到左结点aq中
        aq.setKeyNum(aq.getKeyNum() + 1);
        aq.getRecords()[aq.getKeyNum()] = node.getRecords()[index];
        aq.getChild()[aq.getKeyNum()] = node.getChild()[index].getChild()[0];

        //把右兄弟q中关键字移动到双亲结点node中
        node.getRecords()[index] = q.getRecords()[1];
        q.getChild()[0] = q.getChild()[1];
        q.setKeyNum(q.getKeyNum() - 1);

        //将右兄弟q中所有关键字向前移动一位
        for (int i = 1; i <= q.getKeyNum(); i++) {
            q.getRecords()[i] = q.getRecords()[i + 1];
            q.getChild()[i] = q.getChild()[i + 1];
        }
    }

    /**
     * 将双亲结点p、右结点q合并入左结点ap
     * 并调整双亲结点p中的剩余关键字
     * @param node
     * @param index
     */
    private void combine(Node node, int index) {
        Node q = node.getChild()[index];
        Node aq = node.getChild()[index - 1];

        if (aq == null) {
            aq = new Node(node.getM());
            node.getChild()[index - 1] = aq;
        }

        //空指针抛错,修改
        if (q == null) {
            q = new Node(node.getM());
            node.getChild()[index] = q;
        }

        //将双亲结点的记录node.records[index]插入到左结点aq中
        aq.setKeyNum(aq.getKeyNum() + 1);
        aq.getRecords()[aq.getKeyNum()] = node.getRecords()[index];
        aq.getChild()[aq.getKeyNum()] = q.getChild()[0];

        //将有结点q中的所有关键字插入到左结点aq中
        for (int i = 1; i <= q.getKeyNum(); i++) {
            aq.setKeyNum(aq.getKeyNum() + 1);
            aq.getRecords()[aq.getKeyNum()] = q.getRecords()[i];
            aq.getChild()[aq.getKeyNum()] = q.getChild()[i];
        }

        //将双亲结点node中node.records[index]后的所有关键字向前移动一位
        for (int i = index; i < node.getKeyNum(); i++) {
            node.getRecords()[i] = node.getRecords()[i + 1];
            node.getChild()[i] = node.getChild()[i + 1];
        }

        node.setKeyNum(node.getKeyNum() - 1);
    }

    /**
     * 删除节点p中的第i个关键字后，调整B树
     * @param node
     * @param index
     */
    private void adjustBTree(Node node, int index) {
        if (index == 0) {//删除的是最左边关键字
            if (node.getChild()[1]!= null
                    && node.getChild()[1].getKeyNum() > node.getMin()) {//右结点可以借
                moveLeft(node, 1);
            } else {//右结点不够借
                combine(node, 1);
            }
        } else if (index == node.getKeyNum()) {//删除的是最右边关键字
            if (node.getChild()[index - 1] != null
                    && node.getChild()[index - 1].getKeyNum() > node.getMin()) {//左结点可以借
                moveRight(node, index);
            } else {//左结点不够借
                combine(node, index);
            }
        } else if (node.getChild()[index - 1] != null
                && node.getChild()[index - 1].getKeyNum() > node.getMin()) {
            //删除关键字在中部，而且左结点够借
            moveRight(node, index);
        } else if (node.getChild()[index + 1] != null
                && node.getChild()[index + 1].getKeyNum() > node.getMin()) {
            //删除关键字在中部，而且右结点够借
            moveLeft(node, index + 1);
        } else { //删除关键字在中部，且左右结点都不够借
            combine(node, index);
        }
    }

    /**
     * 输出
     */
    public void output() {
        output(root, 1);
        System.out.println();
    }

    /**
     * 输出结点
     * @param node
     * @param i 当前第几层
     */
    private void output(Node node, int i) {
        StringBuilder base = new StringBuilder();
    	for (int j = 0; j < i; j++) {
    		base.append("\t");
    	}
    	if(node == null) return;

        System.out.print(base.toString());
        for(int j = 0; j < node.getKeyNum(); j++) {
            System.out.print(node.getRecords()[j + 1].getKey() + ",");
        }
        System.out.println();

        for (int j = 0; j <= node.getKeyNum(); j++) {
            output(node.getChild()[j], i + 1);
        }



       /* StringBuilder base = new StringBuilder();
        for (int j = 0; j < i; j++) {
            base.append("\t");
        }
        if (node == null) {
            return;
        }
        for (int j = 0; j < node.getKeyNum(); j++) {
        	output(node.getChild()[j], i + 1);
            System.out.println(base.toString() + node.getRecords()[j + 1].getKey());
        }
        output(node.getChild()[node.getKeyNum()], i + 1);*/
    }

    /**
     * 前序遍历B树
     */
    public void preOrder() {
        preOrder(root);
    }

    private void preOrder(Node node) {
        if (node == null) return;
        for (int i = 1; i <= node.getKeyNum(); i++)
            System.out.print(node.getRecords()[i].getKey() + ",");
        for (int i = 0; i <= node.getKeyNum(); i++)
            preOrder(node.getChild()[i]);
    }

    /**
     * 后序遍历B树
     */
    public void postOrder() {
        postOrder(root);
    }

    private void postOrder(Node node) {
        if (node == null) return;
        for (int i = 0; i <= node.getKeyNum(); i++)
            postOrder(node.getChild()[i]);
        for (int i = 1; i <= node.getKeyNum(); i++)
            System.out.print(node.getRecords()[i].getKey() + ",");
    }
}
