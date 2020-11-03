package edu.dp;

/**
 * 静态内部类实现单例
 */
public class Singleton {

    // 私有构造函数
    private Singleton() {}

    // 静态内部类
    private static class SingletonHolder {
        private static final Singleton INSTANCE = new Singleton();
    }

    public static Singleton getInstance(){
        return SingletonHolder.INSTANCE;
    }
}
