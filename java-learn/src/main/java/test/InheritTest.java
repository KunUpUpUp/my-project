package test;

import oop.inherit.Father;
import oop.inherit.Son;

public class InheritTest {
    public static void main(String[] args) {
        Son son = createSon();
        test(son);
    }

    private static Son createSon() {
        Son son = new Son();
        son.setName("Son");
        son.setAge(10);
        son.setToys("Ultraman");
        return son;
    }

    public static void test(Father father) {
        System.out.println(father);
        System.out.println(father.getName());
        System.out.println(father.getAge());
    }
}
