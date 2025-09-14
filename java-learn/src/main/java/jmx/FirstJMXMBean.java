package jmx;

public interface FirstJMXMBean {
    // 暴露属性
    int getCounter();
    void setCounter(int value);

    // 暴露方法
    void sayHello();
    String greet(String name);
}