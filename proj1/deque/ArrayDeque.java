package deque;

public class ArrayDeque <T> {
   private T[] items;
   private int size;
   private int nextFirst;
   private int nextLast;

   public ArrayDeque() {
      items = (T[]) new Object[8];
      size = 0;
      nextFirst = 3;
      nextLast = 4;
   }

   public T get(int index) {
      if (index < 0 || index >=size) {
         return null;
      } else {
         int real_index = (nextFirst + 1 + index) % items.length;
         return items[real_index];
      }
   }

   public void addFirst(T item) {
      if (size == items.length) {
         resize(items.length * 3);
      }
         items[nextFirst] = item;
         nextFirst = (nextFirst - 1 + items.length) % items.length;
         size++;

   }

   public void addLast(T item) {
      if (size == items.length) {
         resize(items.length * 3);
      }
         items[nextLast] = item;
         nextLast = (nextLast + 1 + items.length) % items.length;
         size++;

   }

   public void resize(int newSize) {
      T[] newitems = (T[]) new Object[newSize];
      for (int i = 0; i < size; i++) {
         newitems[i] = get(i);
      }
      items = newitems;
      nextFirst = items.length - 1;
      nextLast = size;
   }

   public int size() {
      return size;
   }

   public void printDeque() {
      for (int i = 0; i < size; i++) {
         System.out.print(get(i) + " ");
      }
      System.out.println();
   }

   public boolean isEmpty() {
      return size == 0;
   }

   public T removeFirst() {
      if (isEmpty()) {
         return null;
      }
      if (items.length >= 16 && (double) size / items.length < 0.25) {
         resize(items.length / 2);
      }
      int indexToRemove = (nextFirst + 1) % items.length;
      T itemToReturn = items[indexToRemove];
      items[indexToRemove] = null;
      nextFirst = indexToRemove;
      size--;
      return itemToReturn;
   }

   public T removeLast() {
      if (isEmpty()) {
         return null;
      }
      if (items.length >= 16 && (double) size / items.length < 0.25) {
         resize(items.length / 2);
      }
      int indexToRemove = (nextLast - 1 + items.length) % items.length;
      T itemToReturn = items[indexToRemove];
      items[indexToRemove] = null;
      nextLast = indexToRemove;
      size--;
      return itemToReturn;
   }
}