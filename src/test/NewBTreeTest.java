package test;

import btree.Record;
import btree.BTree;

import java.util.Random;

public class NewBTreeTest {

    private static void testInsert() {
        BTree tree = new BTree(4);
        Random random = new Random();

        for (int i = 0; i < 15; i++) {
            int key = random.nextInt(30) + 1;
            String value = "value" + key;
            System.out.println(tree.insertBTree(new Record(key, value)) + ":" + key);
        }
        tree.output();
        System.out.println("size:" + tree.size());
        System.out.println("查找:" + tree.searchBTree(10));
        System.out.println("查找:" + tree.searchBTree(15));
        System.out.println("查找:" + tree.searchBTree(20));
        System.out.println("查找:" + tree.searchBTree(25));
        System.out.println("查找:" + tree.searchBTree(30));
    }


    private static void testDelete() {
        BTree tree = new BTree(4);
        int[] keys = {15, 68, 25, 1, 9, 47, 16, 90, 55, 65, 10, 40, 60, 85, 95, 100, 14};
        for (int key : keys) {
            String value = "value" + key;
            tree.insertBTree(new Record(key, value));
        }
        System.out.println("************原先的************");
        tree.output();
        tree.deleteBTree(10);
        System.out.println("************删除10之后************");
        tree.output();
        System.out.println("************删除68之后************");
        tree.deleteBTree(68);
        tree.output();
        System.out.println("************删除60之后************");
        tree.deleteBTree(60);
        tree.output();
        System.out.println("************删除1之后************");
        tree.deleteBTree(1);
        tree.output();
        System.out.println("************删除85之后************");
        tree.deleteBTree(85);
        tree.output();
        System.out.println("************删除65之后************");
        tree.deleteBTree(65);
        tree.output();
        System.out.println("************删除25之后************");
        tree.deleteBTree(25);
        tree.output();
        System.out.println("************删除55之后************");
        tree.deleteBTree(55);
        tree.output();
    }

    private static void testOrder() {
        BTree tree = new BTree(3);
        Random random = new Random();
        int key;
        String value;
        for (int i = 0; i < 15; i++) {
            key = random.nextInt(1000);
            value = "value" + key;
            tree.insertBTree(new Record(key, value));
        }
        tree.output();
        System.out.println("**************前序遍历**************");
        tree.preOrder();
        System.out.println();
        System.out.println("**************后序遍历**************");
        tree.postOrder();
    }

    public static void main(String[] args) {
//        testDelete();
        testOrder();
    }


}
