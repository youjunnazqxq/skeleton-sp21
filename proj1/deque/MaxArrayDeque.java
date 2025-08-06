package deque;
import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T>{
    private Comparator<T> defaultComparator;
    public MaxArrayDeque(Comparator<T> c){
    super();
    this.defaultComparator = c;
    }
    public T max(){
       if(isEmpty()){
           return null;
       }
       T maxItem=get(0);
       for(int i=1;i<size();i++){
           T currItem=get(i);
           if (defaultComparator.compare(maxItem,currItem)<=0){
               maxItem=currItem;
           }
       }
       return maxItem;
    }
    public T max(Comparator<T> c){
        if(isEmpty()){
            return null;
        }
        T maxItem=get(0);
        for(int i=1;i<size();i++){
            T currItem=get(i);
            if (c.compare(maxItem,currItem)<=0){
                maxItem=currItem;
            }
        }
        return maxItem;
    }

}
