package deque;

public interface  Deque <T> {
    public void addFirst(T item);
    public void addLast(T item);
    public default boolean isEmpty() {
        return size()==0;
    }
    public T removeFirst ();
    public T removeLast ();
    public int size();
    public T get ( int index);
    public void printDeque();
}

