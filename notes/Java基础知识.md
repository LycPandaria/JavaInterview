
[TOC]  

## 基本概念

### main(String[] args) 方法
1. main函数必须由public和static修饰，也可以加上final，synchronized，但是不可以用abstract
2. 每个类都可以申请main函数，但只有和文件名相同的类的main函数会在启动时候调用
3. main函数返回值必须是void

### 如何在main()方法前输出hello
静态块会在类被加载前调用
```
public class Test{
	static{System.out.println("Hello")};
	public static void main(String[] args){
		System.out.println("main")
	}
}
```

### Java程序初始化
父类静态变量--父类静态代码块--子类静态变量--子类静态代码块--父类非静态变量--父类非静态代码块--父类构造函数--子类非静态变量--子类非静态块--子类构造函数。  
例题见书50页。

### 构造函数
1. 构造函数与类名相同，不能有返回值，也不能用void
2. 每个类可以有多个构造函数
3. 构造函数可以有0个，1个及以上个参数
4. 主要完成对象的初始化工作
5. 构造函数不能被继承，因此，不能被覆盖(override)，但是构造函数能被重载(overload)
6. 子类可以通过super关键字显式调用父类构造函数，当父类没有提供无参数的构造函数时候，子类的构造函数必须显式调用父类的构造函数。
7. 如果父类和子类都没有定义构造函数，编译器会为父类和子类默认生产一个无参构造。

### 有些接口没有任何方法
1. 接口中成员的作用域修饰符都是public，接口中的常亮默认使用public static final修饰
2. 有些接口内部没有声明任何办法，叫做标识接口
3. Java中已存在的标识接口有Cloneable和Serializable等
4. 主要的作用是配合instanceof来判断对象的类型是否实现了一个给定的标识接口  
例子见书54页

### clone
1.Java在处理基本数据类型（int,char,double）时候，采用按值传递，除此之外其他类型都是采用引用传递。
2.对象除了在函数调用时候是按引用传递，在使用“=”赋值时候也是引用传递。
3.在不影响原因对象的情况下创建一个具有相同状态的对象，就需要使用clone方法。
   1.实现clone的类需要继承Cloneable接口（这是一个标识接口）
   2.在类中重写Object类的clone方法
   3.在clone方法中调用super.clone()方法。
   4.把浅复制的引用指向原型对象新的克隆体。
例子：
1.浅复制
```
class Obj implements Cloneable{
	private a=0;
	public int getA(){return a;}
	public void setA(int b){this.a=b;}
	public void changeA(int c){this.a=c;}
	//override
	public Object clone(){
		Object o = null;
		try{
		//在clone方法中调用super.clone()方法
			**o = (Obj)super.clone();**
		}catch(CloneNotSupportedException e) {
			e.printStackTrack();
		}
		return o;
	}
}
```
2.深复制--在用clone方法复制完后，对对象中的非基本类型的属性也调用clone方法完成深复制。
```
class Obj implements Cloneable{
	private Data data = new Date();
	...
	public Objec clone(){
		Object o = null;
		try{
		//在clone方法中调用super.clone()方法
			**o = (Obj)super.clone();**
		}catch(CloneNotSupportedException e) {
			e.printStackTrack();
		}
		//对对象中的非基本类型的属性也调用clone方法
		**o.date = (Date)this.getDate().clone();**
		return o;
	}
}
```
3. clone() 的替代方案
使用 clone() 方法来拷贝一个对象即复杂又有风险，它会抛出异常，并且还需要类型转换。Effective Java 书上讲到，最好不要去使用 clone()，可以使用拷贝构造函数或者拷贝工厂来拷贝一个对象。
```java
public class CloneConstructorExample {
    private int[] arr;
    public CloneConstructorExample() {
        arr = new int[10];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
    }
	//拷贝构造函数
    public CloneConstructorExample(CloneConstructorExample original) {
        arr = new int[original.arr.length];
        for (int i = 0; i < original.arr.length; i++) {
            arr[i] = original.arr[i];
        }
    }
    public void set(int index, int value) {
        arr[index] = value;
    }
    public int get(int index) {
        return arr[index];
    }
}
```


### 反射机制
1.它允许程序在运行时进行自我检查，同时也允许对其内部的成员进行操作
2.主要作用：
   1.得到一个对象所属的类
   2.获取一个类的所有成员变量和方法
   3.在运行时创建对象
   4.在运行时调用对象的方法
```
class Base{
	public void f() {System.out.println("Base")};
}
class Sub{
	public void f() {System.out.println("Sub")};
}
public class Test{
	public static void main(String[] args){
		try{//使用反射机制加载类
			**Class c = Class.forName("Sub");**
			Base b = (Base)c.newInstance();
			b.f();	// 输出 Sub
		}catch(Exception e){e.printStackTrack();}
	}
}
```
3.获得Class类方法：
   1.Class.forName
   2.类名.Class
   3.实例.getClass

### 如何实现C语言的函数指针
1.可以先定义一个接口
2.然后在接口中声明要调用的方法
3.接着用不同类实现这个接口
4.最后把这个实体类的一个对象作为参数传递给调用程序，程序可以用参数对象的方法。
```
//接口中定义了一个用来比较大小的方法
interface IntCmp{
	public int cmp(int a, int b);
}
//用不同类实现接口
class CmpASC implements IntCmp{
	public int cmp(int a, int b){...}
}
class CmpDESC implements IntCmp{
	public int cmp(int a, int b){...}
}

public class Test{
	//在排序函数中用接口作为参数
	public static void insertSort(int[] array, **IntCmp cmp***){...}
	
	public static void main(String[] args){
		// 在调用函数时候传入实例对象
		int[] array=[7,3,11,26,4,2,56];
		**insertSort(array, new CmpASC());**
		**insertSort(array, new CmpDESC());**
		...
	}
}
```

## 面向对象技术
### 面向对象的主要特征：抽象，继承，封装和多态。

### 继承
1.Java不支持多重继承，但是可以通过实现多个接口
2.子类只能继承父类的非私有（public和protected）成员变量和方法
3.子类成员变量或是函数签名（相同的方法名，参数个数和类型）与父类相同时，覆盖父类的成员变量和方法。

### 多态的实现机制
1.重载
2.覆盖。Java中，基类的引用变量变量不仅可以指向基类的实例对象，也可以指向妻子类的实例对象。接口的引用变量也可以指向其实现类的实例对象。
3.程序调用的方法在运行期才动态绑定，就是引用变量所指向的具体实例对象的方法，是内存里那个正在运行的那个对象的方法，而不是引用变量的类型中定义的方法。（运行时多态）
4.成员变量无法实现多态的，成员变量的取值是父类的还是子类不取决于创建对象的类型，而是取决于所定义变量的类型，是在编译期间决定的。   
**java提供了两种多态机制：
1.编译时多态--重载
2.运行时多态--方法覆盖
**

### 重载和覆盖
1.使用重载时：
   1.重载通过不同的方法参数来区分，例不同的参数个数，不同参数类型，不同的参数顺序
   2.不能通过方法的**访问权限，返回值类型和抛出的异常类**进行重载
   3.如果基类的方法是private，就不能重载。子类中定义的同名方法只能算一个新的方法。
2.使用覆盖：
   1.派生类中的覆盖方法必须和基类中覆盖的方法有相同的函数名和参数
   2.派生类中的覆盖方法的返回值必须和基类中被覆盖方法的返回值相同
   3.相同的抛出异常
   4.基类中被覆盖的方法不能为private

### 接口与抽象类
1.相同点：
   1.都不能被实例化
   2.接口的实现类或者抽象类的子类都只有实现了方法后才能被实例化
2.不同：
   1.接口只有定义
   2.接口用implements，抽象类用extends
   3.接口中定义的成员变量默认为**public static final修饰,而且必须赋值,所以方法只能用public，abstract修饰** 4.抽象类中可以有自己的成员数据变量，默认为default，可以用private，protected，public，方法不能用private, static,synchronized,native等修饰
   
### 内部类
1.静态内部类只能访问外部类中的静态成员和静态方法
2.非静态内部类可以自由引用外部类的属性和方法，但是不可以定义静态的属性和方法。**非静态内部类中不能有静态成员**
3.局部内部类就像局部变量，不能被public protected，private和static修饰，只能访问方法中定义为final类型的局部变量。
例：
```
class OuterClass{
	public void f(){ class innerClass{} } 	//	局部内部类
}
```
4.匿名内部类
   1.没有类名，不使用关键字class,extends,implements
   2.不能有构造函数
   3.不能定义静态成员，方法和类
   4.不能是public,protected,private,static
   5.一个匿名内部类一定是在new后面，这个匿名类必须继承一个父类或实现一个接口。
   
## 关键字
### static关键字有什么用
1.static成员变量：静态变量属于类，在内存中只有一个复制（所以实例都指向同一个内存地址）--达到一种全局的效果
2.static成员方法：static方法中不能使用this和super，只能访问所属类的静态成员变量和成员方法。  
static方法一个重要的应用是**单例模式**：隐藏构造函数，只能通过类的方法获取类的唯一对象。
```
class Singleton{
	private static Singleton instance = null;
	private Singleton() {} //private构造函数
	public static getInstance() {
		if(instance==null)
			instance=new Singleton();
		return instance;
	}
}
```
3.static代码块：独立于成员变量和成员函数的代码块。常用来初始化静态变量。**只会执行一次**
4.static内部类：可以不依赖于外部类实例对象而被实例化，只能访问外部类中的静态成员和静态方法。
```
public class Outer{
	static int n = 5;
	static class Inner{
		void access(){System.out.println("n="+n);}
	}
	public static void main(String[] args){
		**Outer.Inner inner = new Outer.Inner()**;	//不依赖于外部类实例对象而被实例化
		inner.access();		//可以访问到 n=5
	}
}
```
 
### switch的使用
1.由于byte，short，char可以隐式转换为int，所以这些类以及他们的包装类都可以作为swith的变量。但是long，float，double不行。
2.Java 7开始支持String类，实现是通过hashcode实现的

### volatile
1.在Java中，编译器为了提高效率，把经常被访问的变量缓存（比如寄存器中）起来，程序读取这个变量的时候直接去缓存中读取，而不是内存中。在多线程编程中，另外的线程如果改了该指（在内存中），就会引起问题。
2.volatile是一个类型修饰符，被volatile类型定义的变量，系统每次用到它都是直接从对应的内存中提取，所以所有线程在任何时候所看到的变量的值都是相同的。
3.可以用来停止线程。
```
...
private volatile Boolean flag;
public void stop(){flag = false;}
public void run(){
	whilc(flag)
		//do something
} 
```
### strictfp
strictfp是strict float point的缩写。JVM在计算浮点数时候，如果没有指定strictfp，浮点计算可能不精确，而且结果在不同平台上可能不一样。  
可以用strictfp来修饰一个类，接口或者方法，那么在该范围内，浮点计算都是精确的。  
当一个类被strictfp修饰时候，它的所有方法都会被strictfp修饰。  
```
public strictfp class Test{...}

```

### 泛型
```java
public class Box<T> {
    // T stands for "Type"
    private T t;
    public void set(T t) { this.t = t; }
    public T get() { return t; }
}
```
[Java 泛型详解](http://www.importnew.com/24029.html)

## 基本类型和运算
### Java基本类型
8种：byte,short,int,long,float,double,char,boolean  
他们都有相应的封装类  
区别：
1.原始数据类型在传递参数时候是传值传递，封装类型是引用传递
2.默认值不同：原始数据类型默认值根据类型不同，封装类型实例变量默认值为null

### 不可变类
1.不可变类是指创建了这个类的实例后，不允许修改他的值，它的成员变量就不能被修改了。
2.如何创建不可变类：
   1.类中所有成员变量被private修饰
   2.类中没有写或者修改成员变量的方法
   3.确保类中所有方法不会被子类覆盖
   4.如果一个类成员不是不可变量，在成员初始化或者使用get方法获得该成员变量时候，通过clone方法确保类的不可变性。
   5.如果有必要，覆盖Object类中的equals和hashcode方法。
   6.由于类的不可变性，在创建对象时候就需要初始化成员变量，因此最好提供一个带参数的构造函数来初始化成员变量
```
class ImmutableClass{
	private Date d;
	public ImmutableClass(Date d){
		this.d=(Date)d.clone();	//解除引用关系
	}
	public Date getDate(){return (Date)d.clone();}
}
```

### 强制类型转换注意事项
1. 在涉及byte,short, char类型运算时候，首先会把变量的值强制转化为int类型。所以两个short类型相加，最后得到的结果是int型，对byte和char同样。所以对于语句short s1=1；s1=s1+1；时候编译器会报错，因为在执行s1+1时候，得到的结果是int型，要**s1=(short)(s1+1)**才行
2. 有一个例外，+=为Java规定的运算法，使用+=时候并不涉及到类型转换

### 运算符优先级
![运算符优先级](../pic/operator.jpg)

### "<<" 和 ">>"异同
1. "<<"运算符标识左移，左移n位表示原来的值乘2的n次方，可以用来代替乘法，因为更快。
2. ">>"右移，分为">>"有符号右移，若参与运算的数字为正数，则在高位补0，若为负数，在高位补1,。">>>"为无符号右移，不管正负数，都在高位补0

### char型变量是否可以存储一个汉字
1. 可以，因为Java默认使用Unicode编码方式，每个字符占两个字节
2. 在String中，英文字符占用一个字符，中文占两个字符，以减少存储空间，提高效率。

## 字符串与数组
### new String("abc")创建了几个对象？
1个或2个，取决于字符串常量池中是否已经存在"abc".

### "==",equals 和 hashCode
1."=="可以用来比较两个基础数据类型的值，但如果是两个对象（引用类型），==可以比较两个变量是否指向同一个地址，但是内容就不能比较了。
2. Object类中的equals是直接调用==的，它和==的区别就是equals是可以覆盖的，通过覆盖达到比较内容的目的。
3.hashCode()是从Object类继承过来，hashCode()返回对象在内存中地址转换成的一个int值，所以如果不重写，任何对象的hashCode()方法都是不相等的。
   1. 一般来讲，equals是给用户调用的，hashCode()一般不会，它更多用在hashmap，hashset，hashtable中判断key是不是重复的
   2. 一般在覆盖equals方法的同时就应该覆盖hashCode方法，不然会导致该类在和基于三列的集合类结合一起使用时候会出问题。

### String，StringBuffer，StringBuilder，StringTokenizer
String是不可变类，StringBuffer是可变类。当一个字符串需要经常被修改的时候，使用StringBuffer比String好很多。  

String实例化有两种方法：
```
String s = "Hello"；
String s = new String("Hello");
```
StringBuffer实例化只有一种方法：
```
StringBuffer sb = new StringBuffer("Hello");
```
String字符串修改实现原理：  
首先先创建一个StringBuffer,其次调用StringBuffer的append()方法，最后调用toString()方法。示例如下：
```
String s="Hello"; s+="World";
等价于：  
StringBuffer sb = new StringBuffer(s);
sb.append("World");
s=sb.toString();
```
  
StringBuilder和StringBuffer一样都是字符串缓冲区，StringBuilder不是线程安全的，在单线程环境下，Stringbuilder效率会更高一点。  
StringTokenizer是用来分割字符串的工具类。

### Java中数组是不是对象
数组是对象，数组不仅有其自己的属性，也有一些方法可以调用。  
每个数组类型都有其对应的类型，可以使用instanceof来判断数据类型，例如:
```
int[] a = {1,2};
int[][] b = new int[2][4];
String[] args = {"a","b"};

a instanceof int[]
b instanceof int[][]
c instanceof String[]
```

### Java数组初始化方式：
1. int[] a = new int[5];
2. int[] a = {1,2,3,4,5};
3. int[] a; a = new int[5];
4. int[] a; a = new int[]{1,2,3,4,5};
二维数组：  
声明：
1. type name[][]
2. type[][] name
3. type[] namep[]
初始化：
1.int[][] arr = {{1,3},{3,4,5}};
2.int[][] a = new int[2][];
a[0] = new int[]{1,2};
a[1] = new int[]{3,4,5};

### length属性和length()方法
1. 在数组中，数组是一个对象，里面含有length属性来获取数组的长度
2. length()方法是针对字符串而言的，String提供了length()方法来计算字符串的长度。

## 异常处理
### finally块中代码什么时候执行
finally块的代码也会在return之前执行。  
如果try-finally或者catch-finally中都有return，finally中的return会覆盖别处的return，最终返回到调用者那里的是finally中的return值。  
finally是不是一定执行？   
不一定。
1. 当程序进入到try之前就出现异常。
2. 程序在try中强制退出时候也不会执行finally代码。  

### 运行时异常和普通异常
Java提供了两种错误异常类：Error和Exception,父类都是Throwable
1. Error表示程序在运行期间出了非常严重的错误，这种错误是不克恢复的，属于JVM层次的错误。会导致程序终止执行。也不推荐程序去捕捉Error，因为这是应该被修复的错误。OutOfMemoryError，ThreadDeath都属于错误。
2.Exception包含checked exception和runtime exception
   1.检查异常 checked exception
   常见于IO和SQL异常，这种异常都发生在编译阶段，Java编译器强制程序去捕捉此类型异常，一般在如下几种情况中使用：
      1. 异常发生不会导致程序出错，进行处理之后可以继续。比如：连接数据库失败之后，重新尝试连接
	  2. 程序依赖于不可靠的外部条件，例如系统IO
   2. runtime exception
   编译器没有强制对其进行捕获并处理。如果不对这类异常进行处理，当出现这种异常时候，JVM会来处理。常见的包括：NullPointerException,ClassCastException,ArrayIndexOutOfBoundsException,ArithmeticException等。
   出现运行时异常时候，系统会把异常一直往上层抛，知道遇到处理代码为止。如果没有处理快，就会由线程或者main抛出，线程或程序也就退出了。
3. 在处理异常时候，还要注意：
   1. 先捕获子类，再捕获基类的异常处理
   2. 尽早抛出异常，同时对捕获的异常进行处理
   3. 可以根据实际的需求自定义异常类
   4. 异常能处理就处理，不能处理就抛出
   
## 输入输出流
###Java IO流的实现机制是什么？
流可以分为两大类：字节流和字符流。
1. 字节流以字节（8bit）为单位，包含两个抽象类：InputStream和OutputStream。
2. 字符流以字符（16bit）为单位，一次可以读取多个字节，它包含两个抽象类：Reader和Writer
3. 两者最主要的区别为：在处理输入输出时候，字节流不会用到缓存，而字符流用到缓存。
4. Java IO类在涉及时候采用了Decorator 装饰者设计模式。

### 管理文件和目录的类：
File类，常用的几个方法为：File(String pathname), createNewFile(), delete(), isFile(), isDirectory(), listFiles(), mkdir(), exists()
题：如何列出某个目录下的所有目录和文件？
```
class Test{
	public static void main(String[] args){
		File file = new File("C:\\testDir");
		if(!file.exists()){ // 判断目录是否存在
			System.out.println("dirctory is empty");
			return;
		}
		File[] fileList = file.listFiles();
		for(int i=0; i < fileList.length; i++){
			if(fileList[i].isDirectory())
				System.out.println("dictory is: " + fileList[i].getName());
			else
				System.out.println("file is: " + fileList[i].getName());
		}
	}
}
```

### Java NIO(待写，不了解)

### System.out.println
```
System.out.println(1+2+"");	// 3
System.out.println(""+1+2);	// 12
```

## Java平台与内存管理
### JVM加载class文件机制
当运行指定程序时，JVM会将编译生成的.class文件按照需求和一定的规则加载到内存中。
这个过程是由类加载器来完成的，ClassLoader和它的子类。
Java的三种类加载器：
1. Bootstrap Loader（C++写的）：负责加载系统类（jre/lib/rt.jar的类）
2. ExtClass Loader：负责加载扩展类（jar/lib/ext/*/jar的类）
3. APPClassLoader：负责加载应用类

### 什么是GC--垃圾回收器
GC主要负责：分配内存，确保被引用对象的内存不被错误地回收，回收不再被引用的对象的内存空间。  
对于垃圾回收器来说，它使用有向图来记录和管理堆内存中的所有对象，通过这个有向图可以识别哪些对象是可达的，哪些是不可达的。  
几种垃圾回收器算法：
1. 引用计数算法 reference counting collector
简单但是效率低下。在堆中对每个对象都有个引用计数器；当对象被引用时，计数器加1，当引用被置空或者离开作用域时，计数器减1.JVM并没有采用这种方法，因为它没法解决互相引用问题。
2. 追踪回收算法  tracing collector
追踪回收算法利用JVM维护的对象引用图，从根节点开始遍历对象的引用图，同时标记遍历到的对象，当遍历结束后收回未被标记的对象。
3. 压缩回收算法  compacting collector
把堆中活动的对象移动到堆的一端，这样就会在堆中另一端留出很大的空闲区域，相当于对堆中碎片进行了处理，但是代价是性能损失。
4. 复制回收算法  coping collector
把堆分成两个大小相同的区域，在任何时候，只有一个区域被使用，知道该区域被用完，此时GC中断程序执行，通过变量方式把活动对象复制到另一个区域，在复制过程中他们是紧挨着分布的，这样可以消除碎片。
5.按代回收算法 generation collector
复制回收算法主要缺点是复制次数太多。由于程序有“程序创建的大部分对象的生命周期都很短，只有一部分对象有很长的生命周期”的特点，按代回收将堆分成两个或者多个子堆，每个子堆代表一代。算法在运行过程中优先收集“年幼的对象”，如果一个对象经过多次收集仍然存活，就把该对象转移到高一级的堆中，减少对其的扫描次数。  

开发人员可以通过System.gc()通知垃圾回收器运行，但不推荐，因为该方法会停止所有响应。

### Java是否存在内存泄漏
Java中容易引起内存泄漏的几个方面：
1. 静态集合类，例如hashmap和vector。如果这些容器是静态的，由于他们生命周期与程序一致，容易引起内存泄漏。
2. 各种连接没有关闭，比如Connection，Statement，ResultSet。数据库链接，网络和IO。
3. 没有关闭监听器
4. 变量不合理的作用域。
5. 单例模式可能会造成内存泄漏。例子看书131页。主要是说：在Singleton中存在一个对对象BigClass的引用，由于单例对象是以静态变量的方式存储，所以导致BigClass类的对象不能够被回收。

### Java中堆和栈
栈内存主要用来存放基本数据类型和引用变量。  
堆内存用来存放运行时创建的对象，通过new关键字创建出来的对象都存放在堆中，一个JVM实例维护一个堆，多线程也是共享这个堆。


   


