package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
      AListNoResizing<Integer> correct = new AListNoResizing<>();
      BuggyAList<Integer> broken = new BuggyAList<>();

      correct.addLast(5);
      correct.addLast(10);
      correct.addLast(15);

      broken.addLast(5);
      broken.addLast(10);
      broken.addLast(15);

      assertEquals(correct.size(), broken.size());

      assertEquals(correct.removeLast(), broken.removeLast());
      assertEquals(correct.removeLast(), broken.removeLast());
      assertEquals(correct.removeLast(), broken.removeLast());
    }

  @Test
  public void randomizedTest() {
    AListNoResizing<Integer> correct = new AListNoResizing<>();
    BuggyAList<Integer> broken = new BuggyAList<>();

    int N = 5000; // 增加 N 的值以增加发现错误的几率，Lab 建议增加到 5000
    for (int i = 0; i < N; i += 1) {
      // StdRandom.uniform(0, 4) 会生成 0, 1, 2, 3 四个整数
      // 0: addLast, 1: size, 2: getLast, 3: removeLast
      int operationNumber = StdRandom.uniform(0, 4);

      if (operationNumber == 0) {
        // addLast
        int randVal = StdRandom.uniform(0, 100);
        correct.addLast(randVal);
        broken.addLast(randVal);
        // System.out.println("addLast(" + randVal + ")"); // 调试时可以打印
      } else if (operationNumber == 1) {
        // size
        int correctSize = correct.size();
        int brokenSize = broken.size();
        // System.out.println("size: " + correctSize); // 调试时可以打印
        assertEquals("Size mismatch after operations", correctSize, brokenSize); // 添加断言，比较大小是否一致
      } else if (operationNumber == 2) {
        // getLast
        // 只有当列表不为空时才执行 getLast，否则会崩溃
        if (correct.size() > 0) {
          // System.out.println("getLast()"); // 调试时可以打印
          assertEquals("getLast mismatch", correct.getLast(), broken.getLast()); // 添加断言，比较 getLast 结果是否一致
        }
      } else if (operationNumber == 3) {
        // removeLast
        // 只有当列表不为空时才执行 removeLast，否则会崩溃
        if (correct.size() > 0) {
          // System.out.println("removeLast()"); // 调试时可以打印
          assertEquals("removeLast mismatch", correct.removeLast(), broken.removeLast()); // 添加断言，比较 removeLast 结果是否一致
          assertEquals("Size mismatch after removeLast", correct.size(), broken.size()); // 确保移除后大小一致
        }
      }
    }



  }

  }
