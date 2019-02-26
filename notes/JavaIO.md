# 概述
Java 的 IO 大致可以分为：
  - 磁盘操作：File
  - 字节操作：InputStream 和 OutputStream
  - 字符操作：Reader 和Writer
  - 对象操作：Serializable
  - 网络操作：Socket
  - 新的IO：NIO

# 磁盘操作
File 类用来表示文件和目录的信息，但是它不表示文件的内容
递归一个目录下文件：
```java
public static void listAllFiles(File dir){
  if(dir == null || !dir.exists()) return;

  if(dir.isFile()){
    System.out.println(dir.getName());
    return;
  }
  for(File file : dir.listFiles())
    listAllFiles(file);
}
```

# 字节操作
## 实现文件复制
```java
public static void copyFile(String src, String dist) throws IOException{
  FileInputStream in = new FileInputStream(src);
  FileOutputStream out = new FileOutputStream(dist);

  byte[] buffer = new byte[20 * 1024];  // 20MB
  int cnt;

  // read() 最多读取 buffer.length 个字节
  // 返回的是实际读取的个数
  // 返回 -1 时候表示读到 eof
  while((cnt = in.read(buffer, 0, buffer.length)) != -1)
    out.write(buffer, 0, cnt);

  in.close();
  out.close();
}
```

## 装饰者模式
Java I/O 使用了装饰者模式。以 InputStream 为例：
  - InputStream 是抽象组件
  - FileInputStream 是 InputStream 的子类，属于具体组件，提供了字节流的输入操作
  - FilterInputStream 属于抽象装饰者，装饰者用于装饰组件，为组件提供额外的功能。例如 BufferInputStram 为 FileInputStream 提供缓存的功能
![IO](../pic/IO-1.png)

实例化一个具有缓存功能的字节流对象时，只需要在 FileInputStream 对象上再套一层 BufferedInputStream 对象即可。
```java
FileInputStream in = new FileInputStream(filePath);
BufferedInputStream buff = new BufferedInputStream(in);
```
DataInputStram 装饰者提供了对更多数据类型的输入操作，比如 int，double 等基本类型。

# 字符操作
## 编码与解码
- 编码：把字符转换为字节
- 解码：把字节重新组合成字符
- 如果编码和解码过程使用不同的编码方式就会出现乱码

- GBK 编码中，中文字符占 2 个字节，英文字符占 1 个字节
- UTF-8 中，中文字符占 3 个字节，英文字符占 1 个字节
- UTF-16be 中，中文和英文都占 2 个字节

Java 的内存编码使用双字节编码 UTF-16be，这不是指 Java 只支持这一种编码方式，而是说 char 这种类型使用 UTF-16be 进行编码。char 类型占 16 位，也就是两个字节，Java 使用这种双字节编码是为了让一个中文或者一个英文都能使用一个 char 来存储。

## String 的编码方式
String 可以看成一个字符序列，可以指定一个编码方式将它编码为字节序列，
也可以指定一个编码方式将一个字节序列解码为 String
```java
String str1 = "中文";
byte[] bytes = str1.getBytes("UTF-8");
String str2 = new String(bytes, "UTF-8");
System.out.println(str2);
```
```text
"中文"
```
在调用无参数 getBytes() 方法时，默认的编码方式不是 UTF-16be。双字节编码的好处是可以使用一个 char 存储中文和英文，而将 String 转为 bytes[] 字节数组就不再需要这个好处，因此也就不再需要双字节编码。getBytes() 的默认编码方式与平台有关，一般为 UTF-8。

## Reader 和 Writer
不管是磁盘还是网络传输，最小的存储单元都是字节，而不是字符。但是在程序中操作的通常是字符形式的数据，因此需要提供对字符进行操作的方法。
- InputStreamReader 实现从字节流解码成字符流；
- OutputStreamWriter 实现字符流编码成为字节流。

## 实现逐行输出文本文件的内容
```java
public static void readFileContent(String filePath) throws IOException {

    FileReader fileReader = new FileReader(filePath);
    // 装饰者模式
    BufferedReader bufferedReader = new BufferedReader(fileReader);

    String line;
    while ((line = bufferedReader.readLine()) != null) {
        System.out.println(line);
    }

    // 装饰者模式使得 BufferedReader 组合了一个 Reader 对象
    // 在调用 BufferedReader 的 close() 方法时会去调用 Reader 的 close() 方法
    // 因此只要一个 close() 调用即可
    bufferedReader.close();
}
```
