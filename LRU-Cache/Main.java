import java.util.HashMap;

public class Main {
    public static class Node<K,V> {
        K key;
        V value;
        Node<K,V> prev;
        Node <K,V> next;
        public Node(K key, V value){
            this.key = key;
            this.value = value;
        }
    }
    public static class LRUCache<K,V>{
        private final HashMap<K, Node<K,V>> cache;
        private final int capacity;
        private final Node<K,V> head;
        private final Node<K,V> tail;

        public LRUCache(int capacity){
            this.cache = new HashMap<>();
            this.capacity = capacity;
            this.head = new Node<>(null, null);
            this.tail = new Node<>(null, null);
            head.prev = tail;
            tail.next = head;
        }

        private void addToHead(Node<K,V> node){
            Node<K,V> prev = this.head.prev;
            prev.next = node;
            node.prev = prev;
            node.next = this.head;
            this.head.prev = node;
        }
        private void removeNode(Node<K,V> node){
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        private void moveToHead(Node<K,V> node){
            removeNode(node);
            addToHead(node);
        }
        private Node<K,V> removeTail(){
            Node<K,V> node = this.tail.next;
            removeNode(node);
            return node;
        }
        public synchronized V get(K key){
            Node<K,V> node = cache.get(key);
            if(node == null){
               return null;
            }else{
                moveToHead(node);
                return node.value;
            }
        }
        public synchronized void put(K key, V value){
            Node<K,V> node = cache.get(key);
            if(node != null){
                node.value = value;
                moveToHead(node);
            }else{
                node = new Node<>(key, value);
                cache.put(key, node);
                addToHead(node);
                if(cache.size()> this.capacity){
                    Node<K,V> removedNode = removeTail();
                    cache.remove(removedNode.key);
                }
            }
        }
    }

    public static void main(String[] args) {
        LRUCache<Integer, String> cache = new LRUCache<>(3);

        cache.put(1, "Value 1");
        cache.put(2, "Value 2");
        cache.put(3, "Value 3");

        System.out.println(cache.get(1)); // Output: Value 1
        System.out.println(cache.get(2)); // Output: Value 2

        cache.put(4, "Value 4");

        System.out.println(cache.get(3)); // Output: null
        System.out.println(cache.get(4)); // Output: Value 4

        cache.put(2, "Updated Value 2");

        System.out.println(cache.get(1)); // Output: Value 1
        System.out.println(cache.get(2)); // Output: Updated Value 2
    }
}
