package edu.io;

import java.io.File;

/**
 * File 类用来表示文件和目录的信息，但是它不表示文件的内容
 * 递归一个目录下文件
 */
public class FileTest {

    public static void listAllFiles(File dir){
        if(dir == null || !dir.exists())
            return;

        if(dir.isFile()){
            System.out.println(dir.getName());
            return;
        }

        for (File file: dir.listFiles()) {
            listAllFiles(file);
        }
    }

    public static void main(String[] args) {
        File dir = new File("D:\\Studying\\JavaInterview\\notes");
        listAllFiles(dir);
    }
}
