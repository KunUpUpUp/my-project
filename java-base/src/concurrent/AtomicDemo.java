package concurrent;

import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicDemo {
    public static void main(String[] args) {
        AtomicBoolean judge = new AtomicBoolean();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                if (!judge.get()) {
                    judge.compareAndSet(false, true);
                    // 不会阻塞，没得到就继续往下执行
                    System.out.println("你好：" + Thread.currentThread().getName());
                }
            }).start();
        }
    }
}
