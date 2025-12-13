package num;

public class SumDemo {
    /***
     * 十进制数                 double类型中的实际存储值（近似）
     * 0.1                     0.1000000000000000055511151231257827021181583404541015625
     * 0.2                     0.200000000000000011102230246251565404236316680908203125
     * 0.1 + 0.2的结果          0.3000000000000000444089209850062616169452667236328125
     * 0.3                     0.299999999999999988897769753748434595763683319091796875
     */
    public static void main(String[] args) {
        floatTest();
        doubleTest();
    }

    private static void floatTest() {
        float a = 0.1f;
        float b = 0.2f;
        System.out.println(a + b);
        System.out.printf("%.10f%n", a + b);
        System.out.println(a + b == 0.3); // 这里的0.3是double类型
        System.out.println(a + b == 0.3f);
    }


    private static void doubleTest() {
        double a = 0.1;
        double b = 0.2;
        System.out.println(a + b == 0.3);
    }
}
