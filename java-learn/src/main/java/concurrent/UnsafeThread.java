package concurrent;

public class UnsafeThread {
    public static boolean ready;
    public static int number;

    public static class ReaderThread extends Thread {
        @Override
        public void run() {
            while (!ready) {
                Thread.yield();
            }
            System.out.println(number);
        }
    }


    public static void main(String[] args) {
        new ReaderThread().start();
        number = 42;
        ready = true;
    }
}
