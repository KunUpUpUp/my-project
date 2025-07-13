package concurrent;

public class ThreadDemo {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            // 传入其他线程的变量无法再由主线程更改
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("flag为true");
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    System.out.println("线程结束");
                    Thread.currentThread().interrupt();
                }
            }
        });
        thread.start();

        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        thread.interrupt();
//        try {
//            thread.join();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }


        // 传入其他线程的变量无法再由主线程更改
//        flag = false;
    }
}
