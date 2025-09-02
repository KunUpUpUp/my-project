package num;

public class FloatDemo {
    public static void main(String[] args) {
        double a = 10;
        System.out.println(a / 3);
        double b = a / 3.0 * 3;
        System.out.println(b);

        float c = 0;
        for (int i = 0; i < 10; i++) {
            c += 0.1f;
        }
        System.out.println(c);

        double d = 0;
        for (int i = 0; i < 10; i++) {
            d += 0.1;
        }
        System.out.println(d);
    }
}
