package gh2;
import deque.Deque;
import deque.ArrayDeque;
//Note: This file will not compile until you complete the Deque implementations
public class GuitarString {
    /** Constants. Do not change. In case you're curious, the keyword final
     * means the values cannot be changed at runtime. We'll discuss this and
     * other topics in lecture on Friday. */
    private static final int SR = 44100;      // Sampling Rate
    private static final double DECAY = .996; // energy decay factor

    /* Buffer for storing sound data. */
    /* 用于存储声音数据的缓冲区。*/
    private Deque<Double> buffer;

    /* Create a guitar string of the given frequency.  */
    public GuitarString(double frequency) {
        int capacity=(int)Math.round(SR/frequency);
        buffer= new ArrayDeque<>();
        for (int i=0;i<capacity;i++)
            buffer.addLast(0.0);
    }


    /* Pluck the guitar string by replacing the buffer with white noise. */
    public void pluck() {
        //   请确保您的随机数是互不相同的。这并不是说您需要检查这些数字是否彼此不同，
        //      而是指您应该为每个数组索引重复调用 Math.random() - 0.5 来生成新的随机数。
        int capacity= buffer.size();
        for(int i=0;i<capacity;i++)
        {
            buffer.removeLast();
        }
        for(int i=0;i<capacity;i++)
        {
            double temp=Math.random()-0.5;
            buffer.addLast(temp);
        }

    }

    /* Advance the simulation one time step by performing one iteration of
     * the Karplus-Strong algorithm.
     */
    public void tic() {
        double front_sample=buffer.removeFirst();
        double new_front=buffer.get(0);
        double new_sample=(front_sample+new_front)/2*DECAY;
        buffer.addLast(new_sample);
    }

    /* Return the double at the front of the buffer. */
    public double sample() {
        return buffer.get(0);
    }
}
