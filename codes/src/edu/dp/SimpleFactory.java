package edu.dp;

/**
 * SimpleFactory 设计模式简单实现
 * 里面的类都是 static 是为了把代码写在一个 Java 类里而造成的，与实际运用无关
 */
public class SimpleFactory {
    // Product 接口，用于各类 Product 继承
    public interface Product {
    }

    public static class ConcreteProduct implements Product {
    }

    public static class ConcreteProduct1 implements Product {
    }

    public static class ConcreteProduct2 implements Product {
    }

    public static class Factory{
        public Product createProduct(int type) {
            if (type == 1) {
                return new ConcreteProduct1();
            } else if (type == 2) {
                return new ConcreteProduct2();
            }
            return new ConcreteProduct();
        }
    }

    // Client 传参给 ProductFactory 得到不同类型的 Product
    public static void main(String[] args) {
        Factory simpleFactory = new Factory();
        Product product = simpleFactory.createProduct(1);
    }

}
