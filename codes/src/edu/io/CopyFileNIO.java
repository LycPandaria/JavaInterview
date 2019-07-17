package edu.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class CopyFileNIO {

    public static void fastCopy(String src, String dist) throws IOException{

        // 文件输入流
        FileInputStream fin = new FileInputStream(src);
        // 获取输入字节文件通道
        FileChannel fcin = fin.getChannel();
        // 获取目标文件输出流
        FileOutputStream fout = new FileOutputStream(dist);
        // 过去输出字节流通道
        FileChannel fcout = fout.getChannel();
        // 1024个字节的缓冲区
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

        while(true){
            // 从输入通道读取数据到缓冲区
            int r = fcin.read(buffer);
            if( r == -1) break; // read() 返回 -1 表示 EOS(end-of-stram)
            // 读写切换
            buffer.flip();
            // 缓冲区写入输出文件
            fcout.write(buffer);
            // 清空缓存
            buffer.clear();
        }
    }

    public static void main(String[] args) throws IOException{
        fastCopy("resources/nio_src.txt", "resources/nio_dist.txt");

    }
}
