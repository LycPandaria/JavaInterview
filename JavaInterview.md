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

###clone
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

###反射机制
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

###如何实现C语言的函数指针
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




