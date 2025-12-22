package time;

public class TimerDemo {
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        System.out.println(System.nanoTime() - startTime);
    }
}
