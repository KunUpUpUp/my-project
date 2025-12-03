package generic;

import java.util.ArrayList;
import java.util.List;

public class GenericDemo {
    private int a;

    public static void main(String[] args) {
//        C c = new C();
//        c.setA(10);
//        B<Integer> b = new B<>();
//        Integer t = b.getT(10);
//        System.out.println(t);

        // 由于参数接收的是List<?>，所以可以传入List<Integer和其他所有类型
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        System.out.println(B.checkListEleIsEmpty(list));

        List<String> list1 = new ArrayList<>();
        list1.add("1");
        list1.add("2");
        list1.add("3");
        System.out.println(B.checkListEleIsEmpty(list1));
    }


    static class A{

    }

    // 泛型类
    static class B<T> extends A{
        private T t;

        // 使用类泛型的方法，相当于省略了<T>
        public T getT(T t) {
            return t;
        }

        // 泛型方法，可以在非泛型类中定义
        public <G> G getG(G t) {
            return t;
        }

        public static boolean checkListEleIsEmpty(List<?> list){
            for (Object o : list) {
                if (o != null) {
                    return true;
                }
            }
            return false;
        }
    }

    static class C{
        // 静态内部类可以拥有自己的实例变量
        private int a;

        public void setA(int a) {
            this.a = a;
            System.out.println(a);
        }
    }
}
