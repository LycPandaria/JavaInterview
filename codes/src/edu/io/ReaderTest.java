package edu.io;

import java.io.*;

public class ReaderTest {

    public static void readFileContent(String src, String dist) throws IOException{

        FileReader fileReader = new FileReader(src);
        //FileWriter fileWriter = new FileWriter(dist);
        // 续写
        FileWriter fileWriter = new FileWriter(dist, true);

        // 装饰者模式
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        String line;
        while((line = bufferedReader.readLine()) != null){
            System.out.println(line);
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }

        // 装饰者模式使得 BufferedReader 组合了一个 Reader 对象
        // 在调用 BufferedReader 的 close() 方法时会去调用 Reader 的 close() 方法
        // 因此只要一个 close() 调用即可
        bufferedReader.close();
        bufferedWriter.close();
    }

    public static void main(String[] args) throws IOException{
        // 如果找不到源文件，会抛出 FileNotFoundException
        // 如果目标文件不存在，会新建。如果存在，覆盖与否取决于 FileWriter 的构造方式，见上
        readFileContent("resources/reader_in.txt", "resources/writer_out.txt");
        System.out.println("OK!");
    }
}
