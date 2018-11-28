package test;

import btree.BTree;
import btree.Record;

import java.util.Random;
import java.util.Scanner;

public class Test {

    /**
     * 主函数
     * @param args
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        BTree tree;
        int select = 0;
        //随机产生阶乘数 3-8
        int m = random.nextInt(5) + 3;
        tree = new BTree(m);
        //随机插入10个数据到BTree中
        for (int i=0; i < 10; i++) {
            int key = random.nextInt(100);
            String value = "value" + key;
            tree.insertBTree(new Record(key, value));
        }
        do {
            tree.output();
            System.out.println("select 1 求结点个数size()");
            System.out.println("select 2 取值searchBTree()");
            System.out.println("select 3 插入值insertBTree()");
            System.out.println("select 4 删除值deleteBTree()");
            System.out.println("select 5 前序遍历preOrder");
            System.out.println("select 6 后序遍历postOrder");
            select = scanner.nextInt();
            switch (select) {
                case 1 :
                    int size = tree.size();
                    System.out.println("结点数: " + size);
                    break;
                case 2 :
                    System.out.println("请输入要查找的key(整型)");
                    int searchKey = scanner.nextInt();
                    Record record = tree.searchBTree(searchKey);
                    System.out.println("查找结果" + record);
                    break;
                case 3 :
                    System.out.println("请输入要插入的key(整型)");
                    int insertKey = scanner.nextInt();
                    System.out.println("请输入要插入的value");
                    String insertValue = scanner.next();
                    boolean b = tree.insertBTree(new Record(insertKey, insertValue));
                    System.out.println("插入结果: " + b);
                    break;
                case 4 :
                    System.out.println("请输入要删除的key(整型)");
                    int delKey = scanner.nextInt();
                    tree.deleteBTree(delKey);
                    break;
                case 5 :
                    tree.preOrder();
                    System.out.println();
                    break;
                case 6 :
                    tree.postOrder();
                    System.out.println();
                    break;
            }
        } while (select > 0 && select < 7);
    }
}
