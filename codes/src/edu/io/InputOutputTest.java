package edu.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class InputOutputTest {

    /**
     * 用 FileInputStream 和 FileOutputStream 完成文件复制
     * @param src
     * @param dist
     * @throws IOException
     */
    public static void copyFile(String src, String dist) throws IOException{
        FileInputStream in = new FileInputStream(src);
        //FileOutputStream out = new FileOutputStream(dist);
        //续写：FileOutputStream构造方法,的第二个参数中，加入true
        FileOutputStream out = new FileOutputStream(dist, true);

        byte[] buffer = new byte[20 * 1024];    // 20MB
        int cnt = 0;

        // read() 最多读取 buffer.length 个字节
        // 返回的是实际读取的个数
        // 返回 -1 时候表示读到 eof
        while((cnt = in.read(buffer, 0, buffer.length)) != -1 )
            out.write(buffer, 0, cnt);

        in.close();
        out.close();
    }

    public static void main(String[] args) throws IOException{

        // 注意这里的路径
        // 如果找不到源文件，会抛出 FileNotFoundException
        // 如果目标文件不存在，会新建。如果存在，覆盖与否取决于 FileOutputStream 的构造方式，见上
        copyFile("resources/in.txt", "resources/out.txt");
        System.out.println("OK!");
    }
}
