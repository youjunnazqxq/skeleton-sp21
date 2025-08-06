package deque;

public class LinkedListDeque<T> implements Deque<T> {
    private  class Link{
        Link next;
        Link prev;
        T value;
    }
     private Link sentinel;
     private int size;
     public LinkedListDeque(){
        sentinel=new Link();
        sentinel.next=sentinel;
        sentinel.prev=sentinel;
        size=0;
     }
     @Override
     public void addFirst(T item){
        Link newLink=new Link();
        newLink.value=item;
        Link oldLink=sentinel.next;
        newLink.prev=sentinel;
        newLink.next=oldLink;
        sentinel.next=newLink;
        oldLink.prev=newLink;
        size++;
     }
     @Override
     public void addLast(T item){
         Link newnode=new Link();
         newnode.value=item;
         Link oldnode=sentinel.prev;
         newnode.next=sentinel;
         sentinel.prev=newnode;
         newnode.prev=oldnode;
         oldnode.next=newnode;
         size++;
     }
    @Override
    public int size(){
         return size;
    }
    @Override
    public void printDeque(){
         int i=1;
         Link temp=sentinel.next;
         while(i<=size){
             System.out.print(temp.value+"");
             temp=temp.next;
             i++;
         }
         System.out.println();
    }
    @Override
    public T removeFirst(){
         if(isEmpty()){
             return null;
         } else{
             Link temp=sentinel.next;
             sentinel.next=temp.next;
             temp.next.prev=sentinel;
             temp.prev=null;
             temp.next=null;
             size--;
             return temp.value;

         }
    }
    @Override
    public T removeLast(){
        if(isEmpty()){
            return null;
        } else{
            Link temp=sentinel.prev;
            sentinel.prev=temp.prev;
            temp.prev.next=sentinel;
            temp.prev=null;
            temp.next=null;
            size--;
            return temp.value;
        }
    }
    @Override
    public T get(int index){
         if(isEmpty()){
             return null;
         }else{
             int i=1;
             Link temp=sentinel.next;
             while(i<=size){
                 temp=temp.next;
                 i++;
             }
             return temp.value;
         }
    }
    public T getRecursive(int index){
         if(isEmpty()){
             return null;
         }else{
             return gethelp(sentinel.next,index);
         }
    }
    public T gethelp(Link temp,int index){
         if(index==0){
             return temp.value;
         }else{
             return gethelp(temp.next,index-1);
         }
    }
}
