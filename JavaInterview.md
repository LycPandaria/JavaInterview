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

## 面向对象技术
### 面向对象的主要特征：抽象，继承，封装和多态。

###继承
1.Java不支持多重继承，但是可以通过实现多个接口
2.子类只能继承父类的非私有（public和protected）成员变量和方法
3.子类成员变量或是函数签名（相同的方法名，参数个数和类型）与父类相同时，覆盖父类的成员变量和方法。

###多态的实现机制
1.重载
2.覆盖。Java中，基类的引用变量变量不仅可以指向基类的实例对象，也可以指向妻子类的实例对象。接口的引用变量也可以指向其实现类的实例对象。
3.程序调用的方法在运行期才动态绑定，就是引用变量所指向的具体实例对象的方法，是内存里那个正在运行的那个对象的方法，而不是引用变量的类型中定义的方法。（运行时多态）
4.成员变量无法实现多态的，成员变量的取值是父类的还是子类不取决于创建对象的类型，而是取决于所定义变量的类型，是在编译期间决定的。   
**java提供了两种多态机制：
1.编译时多态--重载
2.运行时多态--方法覆盖
**

###重载和覆盖
1.使用重载时：
   1.重载通过不同的方法参数来区分，例不同的参数个数，不同参数类型，不同的参数顺序
   2.不能通过方法的**访问权限，返回值类型和抛出的异常类**进行重载
   3.如果基类的方法是private，就不能重载。子类中定义的同名方法只能算一个新的方法。
2.使用覆盖：
   1.派生类中的覆盖方法必须和基类中覆盖的方法有相同的函数名和参数
   2.派生类中的覆盖方法的返回值必须和基类中被覆盖方法的返回值相同
   3.相同的抛出异常
   4.基类中被覆盖的方法不能为private

###接口与抽象类
1.相同点：
   1.都不能被实例化
   2.接口的实现类或者抽象类的子类都只有实现了方法后才能被实例化
2.不同：
   1.接口只有定义
   2.接口用implements，抽象类用extends
   3.接口中定义的成员变量默认为**public static final修饰,而且必须赋值,所以方法只能用public，abstract修饰** 4.抽象类中可以有自己的成员数据变量，默认为default，可以用private，protected，public，方法不能用private, static,synchronized,native等修饰
   
###内部类
1.静态内部类只能访问外部类中的静态成员和静态方法
2.非静态内部类可以自由引用外部类的属性和方法，但是不可以定义静态的属性和方法。**非静态内部类中不能有静态成员**
3.局部内部类就像局部变量，不能被public protected，private和static修饰，只能访问方法中定义为final类型的局部变量。
例：
```
class OuterClass{
	public void f(){ class innerClass{} } 	//	局部内部类
}
4.匿名内部类
   1.没有类名，不使用关键字class,extends,implements
   2.不能有构造函数
   3.不能定义静态成员，方法和类
   4.不能是public,protected,private,static
   5.一个匿名内部类一定是在new后面，这个匿名类必须继承一个父类或实现一个接口。
```



