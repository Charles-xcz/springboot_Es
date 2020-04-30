package com.ustc.charles.other.kuaishou;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author charles
 * @date 2020/4/26 16:12
 */
public class Main2 {

    static Map<String, Long> map = new HashMap<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        sc.nextLine();
        for (int i = 0; i < n; i++) {
            int event = sc.nextInt();
            if (event == 1) {
                long a = sc.nextLong();
                long b = sc.nextLong();
                long w = sc.nextLong();
                traceAdd(a, b, w);
            } else {
                long a = sc.nextLong();
                long b = sc.nextLong();
                System.out.println(trace(a, b));
            }
        }
    }

    private static void traceAdd(long a, long b, long w) {
        while (a != b) {
            if (a > b) {
                String key = getKey(a, a / 2);
                map.put(key, map.getOrDefault(key, 0L) + w);
                a /= 2;
            } else {
                String key = getKey(b, b / 2);
                map.put(key, map.getOrDefault(key, 0L) + w);
                b /= 2;
            }
        }
    }

    private static long trace(long a, long b) {
        long sum = 0;
        while (a != b) {
            if (a > b) {
                String key = getKey(a, a / 2);
                sum += map.getOrDefault(key, 0L);
                a /= 2;
            } else {
                String key = getKey(b, b / 2);
                sum += map.getOrDefault(key, 0L);
                b /= 2;
            }
        }
        return sum;
    }

    private static String getKey(long a, long b) {
        if (a > b) {
            return b + "." + a;
        } else {
            return a + "." + b;
        }
    }
}
