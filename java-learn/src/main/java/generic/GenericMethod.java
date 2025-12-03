package generic;

public class GenericMethod {
    public static void main(String[] args) {
        Integer three = new Integer(3);
        System.out.println(three);
        Class clazz = getT(three);
        System.out.println(clazz);
        String s = "而我却一再对你微笑";
        System.out.println(getT(s));
    }

    // 这里没有泛型类，但是可以使用泛型，因为是泛型方法，<T>没啥作用，就是给出这个方法的泛型T
    public static <T> Class getT(T t) {
        return t.getClass();
    }
}
