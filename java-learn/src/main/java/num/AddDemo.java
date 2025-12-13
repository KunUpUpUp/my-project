package num;

public class AddDemo {
    public static void main(String[] args) {
        // 10000000 为 -128，byte 类型范围为 -128 ~ 127
        byte a = 127;
        System.out.println((byte) (a + 2));
    }
}
