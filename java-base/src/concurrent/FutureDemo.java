package concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class FutureDemo {
    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        List<Thread> threads = new ArrayList<Thread>();
        Runnable callable = () -> {
            threads.add(Thread.currentThread());
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("flag为true");
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };


        threadPool.execute(callable);

        try {
            Thread.sleep(10000L);
            threads.forEach(Thread::interrupt);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            threadPool.shutdown();
        }


        System.out.println(111);
    }
}
