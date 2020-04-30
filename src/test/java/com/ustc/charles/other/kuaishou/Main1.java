package com.ustc.charles.other.kuaishou;

import java.util.*;

/**
 * @author charles
 * @date 2020/4/26 16:04
 */
public class Main1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        sc.nextLine();
        Map<String, Set<String>> map = new HashMap<>();
        for (int i = 0; i < n; i++) {
            String url = sc.nextLine();
            String host = url.substring(7);
            int split = host.indexOf('/');
            String path = "V";
            if (split != -1) {
                path = host.substring(split);
                host = host.substring(0, split);
            }
            Set<String> set = map.getOrDefault(host, null);
            if (set == null) {
                set = new HashSet<>();
                set.add(path);
                map.put(host, set);
            } else {
                set.add(path);
            }
        }
        int sum = 0;
        List<List<String>> ans = new ArrayList<>();
        Set<String> stringSet = new HashSet<>(map.keySet());

        for (String pre : map.keySet()) {
            if (stringSet.contains(pre)) {
                continue;
            }
            List<String> list = new ArrayList<>();
            list.add(pre);
            Set<String> preSet = map.get(pre);
            stringSet.add(pre);
            for (String current : map.keySet()) {
                if (stringSet.contains(current)) {
                    continue;
                }
                Set<String> cSet = map.get(current);
                if (preSet.equals(cSet)) {
                    stringSet.add(current);
                    list.add(current);
                }
            }
            if (list.size() > 1) {
                sum++;
                ans.add(list);
            }
        }
        System.out.println(sum);
        for (List<String> list : ans) {
            for (String s : list) {
                System.out.print("http://" + s + " ");
            }
            System.out.println();
        }
    }
}
