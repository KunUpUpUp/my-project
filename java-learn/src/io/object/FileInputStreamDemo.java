package io.object;

import java.io.*;

public class FileInputStreamDemo {
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("E:\\工作文档\\[PPT]Amazon Bedrock Top Use Case 最新场景与案例分享_25.3.20 v2.pdf");
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);

        File fileOut = new File("E:\\工作文档\\[PPT]Amazon Bedrock Top Use Case 最新场景与案例分享_25.3.20 v2.copy.pdf");
        FileOutputStream fos = new FileOutputStream(fileOut);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        try {
            byte[] b = new byte[1024];
            while (true) {
                int read = bis.read(b);
                if (read == -1) break;
                bos.write(b, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (bis != null) {
                    bis.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
