package concurrent;

public class QuestionSync {
    // Integer类是被final修饰的
    private static Integer count = 0;

    public static void main(String[] args) {
        new Thread(() -> {
            while (true) {
                System.out.println(System.identityHashCode(count));
                count++;
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}