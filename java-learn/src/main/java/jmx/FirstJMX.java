package jmx;

public class FirstJMX implements FirstJMXMBean {
    private int counter = 0;

    @Override
    public int getCounter() {
        return counter;
    }

    @Override
    public void setCounter(int value) {
        this.counter = value;
    }

    @Override
    public void sayHello() {
        System.out.println("Hello from JMX!");
    }

    @Override
    public String greet(String name) {
        return "Hello, " + name + "!";
    }
}