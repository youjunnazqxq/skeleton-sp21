package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();

        int currentN = 1000;
        int maxN = 128000; // 根据 Lab 需求设置最大 N 值

        int M = 10000; // 固定进行 10000 次 getLast 操作

        // 循环直到 currentN 超过 maxN
        while (currentN <= maxN) {
            // 步骤 1 & 2: 创建 SLList 并添加 N 个元素
            SLList<Integer> testList = new SLList<>();
            for (int i = 0; i < currentN; i += 1) {
                testList.addLast(i); // 构建大小为 N 的列表
            }

            // 步骤 3: 启动计时器
            Stopwatch sw = new Stopwatch();

            // 步骤 4: 执行 M 次 getLast 操作
            for (int i = 0; i < M; i += 1) {
                testList.getLast();
            }

            // 步骤 5: 检查计时器
            double timeInSeconds = sw.elapsedTime();

            // 收集数据
            Ns.addLast(currentN);
            times.addLast(timeInSeconds);
            opCounts.addLast(M); // 操作数固定为 M

            currentN *= 2; // N 值每次翻倍
        }

        // 打印计时表格
        printTimingTable(Ns, times, opCounts);
    }
    }


