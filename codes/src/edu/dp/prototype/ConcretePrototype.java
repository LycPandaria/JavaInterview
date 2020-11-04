package edu.dp.prototype;

public class ConcretePrototype extends Prototype {
    private String filed;
    public ConcretePrototype(String filed) {
        this.filed = filed;
    }
    @Override
    Prototype myClone() {
        return new ConcretePrototype(filed);
    }
    @Override
    public String toString() {
        return filed;
    }
}