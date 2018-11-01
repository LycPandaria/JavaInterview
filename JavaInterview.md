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



