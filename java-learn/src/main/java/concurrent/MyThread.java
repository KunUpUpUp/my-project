package concurrent;

public class MyThread extends Thread{
    @Override
    public void run() {
        System.out.println("I am a customized thread");
    }
}
