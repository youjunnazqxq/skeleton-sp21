package bstmap;
import edu.princeton.cs.algs4.BST;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

public class BSTMap <K extends Comparable<K>,V>implements Map61B<K,V>{
    private  class BSTNode{
        K key;
        V value;
        BSTNode left;
        BSTNode right;
        public BSTNode(K key ,V value){
            this.key=key;
            this.value=value;
            this.left=null;
            this.right=null;
            }
        }
        BSTNode root;
        int size;
        public BSTMap(){
            root=null;
            size=0;
        }
        @Override
        public void put(K key,V value){
           root=put_help(root,key,value);
        }
        private BSTNode put_help(BSTNode node,K key,V value){
            if(node==null){
                node=new BSTNode(key,value);
                size++;
                return node;
            }
            int cmp=node.key.compareTo(key);
            if(cmp>0){
                node.left=put_help(node.left,key,value);
            }else if(cmp==0){
                node.value=value;
                return node;
            }else{
                node.right=put_help(node.right,key,value);
            }
            return node;
        }

        @Override
        public void clear(){
            root=null;
            size=0;
        }

        @Override
        public int size(){
            return size;
        }

        @Override
        public V get(K key){
            V value=get_help(root,key);
            return value;
        }
        private V get_help(BSTNode current,K key){
            if(current==null){
                return null;
            }
            if (key.equals(current.key)){
                return current.value;
            }
            int cmp=key.compareTo(current.key);
            if (cmp<0){
                return get_help(current.left,key);
            }else{
                return get_help(current.right,key);
            }
        }

        @Override
        public boolean containsKey(K key){
            boolean judge=containsKey_help(root,key);
            return judge;
        }
        private boolean containsKey_help(BSTNode current,K key){
            if (current==null){
                return false;
            }else if(current.key.equals(key)){
                return true;
            }
            int cmp=key.compareTo(current.key);
            if (cmp<0){
                return containsKey_help(current.left,key);
            }else if(cmp>0){
                return containsKey_help(current.right,key);
            }else{
                return true;
            }
        }
    @Override
    public Set<K> keySet(){
        Set<K> keys = new HashSet<>();
        keySetHelper(root,keys);
        return keys;
    }
    private void keySetHelper(BSTNode node, Set<K> keys) {
        if (node == null) {
            return;
        }
        keySetHelper(node.left, keys);
        keys.add(node.key);
        keySetHelper(node.right, keys);
    }
    @Override
    public Iterator<K> iterator(){
            return keySet().iterator();
    }
    @Override
    public V remove(K key){
        if(!containsKey(key)){
            return null;
        }
        V temp_value=get(key);
        root=remove_helper(root,key);
        size--;
        return temp_value;
    }
    private BSTNode remove_helper(BSTNode current,K key){
            if(current==null){
                return null;
            }
            int cmp=key.compareTo(current.key);
            if(cmp>0){
                current.right=remove_helper(current.right,key);
            }else if(cmp<0){
                current.left=remove_helper(current.left,key);
            }else{
                if(current.left==null){
                    return current.right;
                }else if(current.right==null){
                    return current.left;
                }else{
                    BSTNode middle_Node=find_left(current.right);
                    current.key=middle_Node.key;
                    current.value=middle_Node.value;
                    current.right=remove_helper(current.right,middle_Node.key);
                }
            }
            return current;
    }
    private BSTNode find_left(BSTNode node){
            while(node.left!=null){
                node=node.left;
            }
            return node;
    }

    @Override
    public V remove(K key, V value) {
       V currentValue=get(key);
       boolean value_judge=currentValue.equals(value);
       if(currentValue==null||!value_judge){
           return null;
       }
       return remove(key);
    }

}
