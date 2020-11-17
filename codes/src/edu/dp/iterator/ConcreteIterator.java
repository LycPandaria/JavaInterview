package edu.dp.iterator;

public class ConcreteIterator<Item> implements Iterator {

    private Item[] items;
    private int pos = 0;

    public ConcreteIterator(Item[] items){
        this.items = items;
    }

    @Override
    public Object next() {
        return items[pos++];
    }

    @Override
    public boolean hasNext() {
        return pos < items.length;
    }
}
