package com.ustc.charles.other;

import java.util.*;
/**
 * 给定按火星字典序排列的单词，求单词中出现过的字符的字典序
 * wrt wrf er ett rftt
 * x z x
 * abc bbc
 * hij ikjk kih jkca jkaa jj
 * 构造一个字符的有向无环图，再找拓扑序列
 */
public class Main2 {
    // 保存图，定义如果字符i在字符j前，那么存在i到j路径，即map[i][j]=1
    private static int[][] map = new int[26][26];
    // 保存每个节点的入度
    private static int[] indegree = new int[26];
    // 记录出现过的字符
    private static boolean[] flag = new boolean[26];
    private static Set<Character> set = new HashSet<>();
    // 保存结果
    private static List<Character> ans = new ArrayList<>();

    public static void main(String[] args) {
        // wrt wrf er ett rftt
        Scanner sc = new Scanner(System.in);
        String[] input = sc.nextLine().split(" ");
        int maxLen = 0;
        // 创建有向图
        build(input);
        // 找拓扑序列
        topology();
        if(ans.size() == set.size()) {
            for (Character c : ans) {
                System.out.print(c);
            }
        } else {
            System.out.println("invalid");
        }
    }

    public static void topology() {
        while(ans.size() < set.size()) {
            // 是否在一个遍历中找到入度为0的节点，如果没找到，要break;如果一次遍历找到两个入度为0的点，说明是无法确认顺序的
            int cnt = 0;
            for(int i = 0; i < indegree.length; i++) {
                if(indegree[i] == 0 && flag[i]) cnt++;
            }
            if (cnt != 1) break;
            // 找没有入度的节点，加入序列，在有向图中删除这个节点和从这个节点出发的边
            for (int i = 0; i < indegree.length; i++) {
                if (indegree[i] == 0 && flag[i]) {
                    ans.add((char) ('a' + i));
                    flag[i] = false;    // 删除节点
                    for (int j = 0; j < 26; j++) {       // 删除从这个节点出发的边
                        if (map[i][j] == 0) continue;
                        indegree[j]--;
                        map[i][j] = 0;
                    }
                }
            }
        }
    }


    //[wrt wrf er ett rftt]
    public static void build(String[] strs) {
        // 找到相邻的两个字符串，第一个不相同的字符可以确定字符的顺序
        // 如wrt和wrf可以得到：t->f
        String pre = strs[0];
        for (char c : pre.toCharArray()) {
            set.add(c);
        }
        for(int i = 1; i < strs.length; i++) {
            String cur = strs[i];
            for (char c : cur.toCharArray()) {
                set.add(c);
            }
            for(int j = 0; j < Math.min(pre.length(), cur.length()); j++) {
                if(pre.charAt(j) == cur.charAt(j)) continue;
                if(map[pre.charAt(j)-'a'][cur.charAt(j)-'a'] == 1) break;

                map[pre.charAt(j)-'a'][cur.charAt(j)-'a'] = 1;

                indegree[cur.charAt(j)-'a'] += 1;
                flag[pre.charAt(j)-'a'] = true;
                flag[cur.charAt(j)-'a'] = true;
                break;
            }
            pre = cur;
        }
    }
}
