package top.takefly;

import java.util.TreeMap;

public class asList {

    public static void main(String[] args) {
        TreeMap<String, String> tree = new TreeMap<String, String>();
        tree.put("a", "I");
        tree.put("aa", "Love");
        tree.put("aaaaa", "You");
        tree.put("aaaa", "Baby!");
        System.out.println(tree);
        int i = tree.hashCode();
        long l = 1;
        int index = 1;
        while((l*=2)>0) index++;
        System.out.println(index);
    }
}
