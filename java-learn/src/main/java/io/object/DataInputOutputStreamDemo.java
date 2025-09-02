package io.object;

import java.io.*;

public class DataInputOutputStreamDemo {
    public static void main(String[] args) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("D:\\test.txt"));
//        BufferedOutputStream bos = new BufferedOutputStream();

        DataOutputStream os = new DataOutputStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                System.out.write(b); // 将字节输出到控制台
            }

            @Override
            public void close() throws IOException {
                System.out.close(); // 关闭输出流
            }

            @Override
            public void flush() throws IOException {
                System.out.flush(); // 刷新输出流
            }
        });

        try {
            os.writeUTF("Hello World"); // 写入UTF字符串
//            os.writeInt(123); // 写入整数
//            os.writeBoolean(true); // 写入布尔值
            os.flush(); // 刷新输出流
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close(); // 关闭输出流
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
