package com.ustc.charles.other;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author charles
 * @date 2020/4/14 18:01
 */
public class Main {
    public static void main(String[] args) {
        Solution s = new Solution();

    }
}

class Solution {

}

class Data<T> {
    private BlockingQueue<T> queue = new LinkedBlockingQueue<>();

    public void add(T t) {
        try {
            queue.put(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("生产了一个商品:" + t);
    }

    public T remove() {
        T t = null;
        try {
            t = queue.take();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }
}



