package edu.dp.iterator;

public class ConcreteAggregate implements Aggregate {
    private Integer[] items;

    public ConcreteAggregate(){
        items = new Integer[10];
        for(int i = 0; i < 10; i ++){
            items[i] = i;
        }
    }

    @Override
    public Iterator createIterator() {
        return new ConcreteIterator(items);
    }
}
