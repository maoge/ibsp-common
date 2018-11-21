package ibsp.common.utils;

import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class LocalCache<K, V> {
    
    private ScheduledExecutorService cleanExpiredPool = new ScheduledThreadPoolExecutor(1);
    
    private ReentrantLock lock;
    private ConcurrentHashMap<K, Node> cache;
    private PriorityQueue<Node> expireQueue;
    private RomoveListener<K, V> removeListener;
    
    private final int CLEAN_INTERVAL = 1;
    
    public LocalCache() {
        lock = new ReentrantLock();
        cache = new ConcurrentHashMap<>(10000, 100);
        expireQueue = new PriorityQueue<>(10000);
    }
    
    public int getSize() {
        return cache.size();
    }
    
    public void removalListener(RomoveListener<K, V> removeListener) {
        cleanExpiredPool.scheduleWithFixedDelay(new CleanExpiredNodeWork(removeListener), CLEAN_INTERVAL, CLEAN_INTERVAL, TimeUnit.SECONDS);
    }
    
    public V putIfAbsent(K key, V value, long expireTime) {
        lock.lock();
        Node node = null;
        try {
            node = cache.get(key);
            // 过期数据由检测线程回调处理
            if (node == null) {
                node = new Node(key, value, expireTime);
                cache.put(key, node);
                expireQueue.add(node);
            }
        } finally {
            lock.unlock();
        }
        
        return node == null ? null : node.value;
    }
    
    // ttl unit: second
    public Object set(K key, V value, long expireTime) {
        Node newNode = new Node(key, value, expireTime);
        lock.lock();
        try {
            Node old = cache.put(key, newNode);
            expireQueue.add(newNode);
            // 如果该key存在数据，还要从过期时间队列删除
            if (old != null) {
                expireQueue.remove(old);
                return old.value;
            }
            return null;
        } finally {
            lock.unlock();
        }
    }
    
    public Object get(String key) {
        Node n = cache.get(key);
        return n == null ? null : n.value;
    }
    
    public Object get(String key, int retry) {
        int i = 0;
        Object obj = null;
        while (obj == null && i++ < retry) {
            obj = get(key);
            if (obj != null) {
                break;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    
                }
            }
        }
        
        return obj;
    }
    
    public Object remove(String key) {
        lock.lock();
        try {
            Node n = cache.remove(key);
            if (n == null) {
                return null;
            } else {
                expireQueue.remove(n);
                return n.value;
            }
        } finally {
            lock.unlock();
        }
    }
    
    private class CleanExpiredNodeWork implements Runnable {
        
        private RomoveListener<K, V> removeListener;
        
        public CleanExpiredNodeWork(RomoveListener<K, V> removeListener) {
            this.removeListener = removeListener;
        }
 
        @Override
        public void run() {
 
            long now = System.currentTimeMillis();
            while (true) {
                lock.lock();
                try {
                    Node node = expireQueue.peek();
                    //没有数据了，或者数据都是没有过期的了
                    if (node == null || node.expireTime > now) {
                        return;
                    }
                    cache.remove(node.key);
                    expireQueue.poll();
                    
                    removeListener.onRemoval(new RemoveNotification<K, V>(node.key, node.value));
 
                } finally {
                    lock.unlock();
                }
            }
        }
    }
    
    private class Node implements Comparable<Node> {
        private K key;
        private V value;
        private long expireTime;
        
        public Node(K key, V value, long expireTime) {
            this.key = key;
            this.value = value;
            this.expireTime = expireTime;
        }

        /**
         * @see SwapExpiredNodeWork
         */
        @Override
        public int compareTo(Node o) {
            long r = this.expireTime - o.expireTime;
            if (r > 0) {
                return 1;
            }
            if (r < 0) {
                return -1;
            }
            return 0;
        }
    }
    
    public static interface RomoveListener<K, V> {
        
        void onRemoval(RemoveNotification<K, V> notification);
        
    }
    
    public static class RemoveNotification<K, V> {
        
        private K key;
        private V value;
        
        private RemoveNotification(K key, V value) {
            this.key = key;
            this.value = value;
        }
        
        public K getKey() {
            return key;
        }
        
        public V getValue() {
            return value;
        }
        
    }

}
