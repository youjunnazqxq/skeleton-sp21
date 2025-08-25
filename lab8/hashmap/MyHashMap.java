package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }
    private HashSet<K> myset;
    private int size;
    private double maxload;
    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    /** Constructors */
    public MyHashMap() {
        this(16,0.75);
    }

    public MyHashMap(int initialSize) {
        this(initialSize,0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
    this.maxload=maxLoad;
    size=0;
    buckets=createTable(initialSize);
    myset=new HashSet<K>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key,value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<Node>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    @Override
     public void put(K key,V value){
        if(maxload<=(double)size/buckets.length){
            resize(buckets.length*2);
        }
        Node tempnode=createNode(key,value);
        int bucketIndex=Math.abs(key.hashCode())%buckets.length;
        if(buckets[bucketIndex]==null){
            buckets[bucketIndex]=createBucket();
        }
        for (Node temp_node:buckets[bucketIndex]){
            if(temp_node.key.equals(key)){
                temp_node.value=value;
                return;
            }
        }
        buckets[bucketIndex].add(tempnode);
        size++;
        myset.add(key);
    }
    private void resize (int newsize){
         Collection<Node>[] temp_collect=createTable(newsize);
         Collection<Node>[] old_collect=buckets;
         buckets=temp_collect;
         size=0;
        for (Collection<Node> bucket:old_collect){
            if (bucket!=null){
                for (Node temp_node:bucket){
                   this.put(temp_node.key,temp_node.value);
                }
            }
        }
    }
    @Override
    public  V get (K key){
        int bucketIndex=Math.abs(key.hashCode())%buckets.length;
        if(buckets[bucketIndex]==null){
            return null;
        }
        for (Node temp_node:buckets[bucketIndex]){
            if(key.equals(temp_node.key)){
                return temp_node.value;
            }
        }
        return null;
    }
    @Override
    public int size(){
        return size;
    }
    @Override
    public void clear(){
        buckets=createTable(buckets.length);
        size=0;
        myset.clear();
    }
    @Override
    public boolean containsKey(K key){
      return get(key)!=null;
        }
    @Override
    public Set<K> keySet(){
        return myset;
    }
    @Override
    public Iterator<K> iterator(){
        return myset.iterator();
    }
    @Override
    public V remove(K key){
        int bucketIndex=Math.abs(key.hashCode())%buckets.length;
        if(buckets[bucketIndex]==null){
            return null;
        }
        Iterator<Node> it=buckets[bucketIndex].iterator();
        while(it.hasNext()){
            Node temp_node=it.next();
            if(temp_node.key.equals(key)){
                it.remove();
                size--;
                myset.remove(temp_node.key);
                return temp_node.value;
            }
        }
        return null;
    }
    @Override
    public V remove(K key,V value){
        if(get(key)==null){
            return null;
        }else if(get(key).equals(value)){
            return remove(key);
        }else{
            return null;
        }
    }
}
