Java程序员面试笔试宝典
# 1.Java 基础知识

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
![运算符优先级](/pic/operator.jpg)

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



