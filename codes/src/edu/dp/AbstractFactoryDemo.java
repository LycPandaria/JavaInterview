package edu.dp;

public class AbstractFactoryDemo {
    public class AbstractProductA {
    }
    public class AbstractProductB {
    }
    public class ProductA1 extends AbstractProductA {
    }
    public class ProductA2 extends AbstractProductA {
    }
    public class ProductB1 extends AbstractProductB {
    }
    public class ProductB2 extends AbstractProductB {
    }

    // 对象家族，也就是很多对象而不是一个对象，并且这些对象是相关的，也就是说必须一起创建出来
    public abstract class AbstractFactory {
        abstract AbstractProductA createProductA();
        abstract AbstractProductB createProductB();
    }

    // 具体的工厂类1,这个工厂类用于实例化产品A1和B1
    public class ConcreteFactory1 extends AbstractFactory{
        AbstractProductA createProductA() {
            return new ProductA1();
        }
        AbstractProductB createProductB() {
            return new ProductB1();
        }
    }
    // 具体的工厂类2,这个工厂类用于实例化产品A2和B2
    public class ConcreteFactory2 extends AbstractFactory{
        AbstractProductA createProductA() {
            return new ProductA2();
        }
        AbstractProductB createProductB() {
            return new ProductB2();
        }
    }
}
