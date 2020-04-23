package com.ustc.charles.other;

import java.util.*;

/**
 * @author charles
 * @date 2020/4/14 18:01
 */
public class Main {
    //26个字母 map[i][j]=1代表有i指向j的边
    static int[][] map = new int[26][26];
    //inDegree[i]代表节点i的入度
    static int[] inDegree = new int[26];
    static Set<Character> set = new HashSet<>();
    static int size = 0;
    //拓扑路径
    static List<Character> ans = new ArrayList<>();

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String[] input = sc.nextLine().split(" ");
        if (input.length == 0) {
            return;
        }
        buildMap(input);
        topPath();
        if (ans.size() == size) {
            for (Character an : ans) {
                System.out.print(an);
            }
        } else {
            System.out.println("invalid");
        }
    }

    private static void topPath() {
        size = set.size();
        while (ans.size() < size) {
            //找出入度为0的节点
            int inD = 0;
            for (int i = 0; i < 26; i++) {
                if (inDegree[i] == 0 && set.contains((char) (i + 'a'))) {
                    inD++;
                }
            }
            if (inD != 1) break;
            for (int i = 0; i < 26; i++) {
                if (inDegree[i] == 0 && set.contains((char) (i + 'a'))) {
                    ans.add((char) (i + 'a'));
                    set.remove((char) (i + 'a'));
                    for (int j = 0; j < 26; j++) {
                        if (map[i][j] == 0) continue;
                        map[i][j] = 0;
                        inDegree[j]--;
                    }
                }
            }
        }
    }

    private static void buildMap(String[] input) {
        String pre = input[0];
        for (char c : pre.toCharArray()) {
            set.add(c);
        }

        for (int i = 1; i < input.length; i++) {
            String current = input[i];
            for (char c : current.toCharArray()) {
                set.add(c);
            }
            for (int j = 0; j < Math.min(pre.length(), current.length()); j++) {
                if (pre.charAt(j) == current.charAt(j)) {
                    continue;
                }
                if (map[pre.charAt(j) - 'a'][current.charAt(j) - 'a'] == 1) break;
                //不相等则pre.charAt(j)指向current.charAt(j),且节点j的入度++
                set.add(current.charAt(j));
                map[pre.charAt(j) - 'a'][current.charAt(j) - 'a'] = 1;
                inDegree[current.charAt(j) - 'a']++;
                break;
            }
            pre = current;
        }
    }
}
