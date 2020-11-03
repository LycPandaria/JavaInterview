package edu.dp;

/**
 * 工厂方法 设计模式简单实现
 * 相当于在简单工厂之上把工厂也做一个抽象化
 * 里面的类都是 static 是为了把代码写在一个 Java 类里而造成的，与实际运用无关
 */
public class FactoryMethod {

    public interface Product {}

    public static class ConcreteProduct implements Product {
    }

    public static class ConcreteProduct1 implements Product {
    }

    public abstract static class Factory{
        abstract public Product factoryMethod();	//抽象方法
    }
    // 实例化的决定权交给子类
    public static class ConcreteFactory extends Factory {
        public Product factoryMethod() {
            return new ConcreteProduct();
        }
    }
    public static class ConcreteFactory1 extends Factory {
        public Product factoryMethod() {
            return new ConcreteProduct1();
        }
    }
}
