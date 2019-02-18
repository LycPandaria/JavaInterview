# ArratList
如果没有特别说明，以下源码分析基于 JDK 1.8。

在 IDEA 中 double shift 调出 Search EveryWhere，查找源码文件，找到之后就可以阅读源码。

## 概览
```java
public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable
{
  /* 省略以下内容，下文会有具体代码 */
}
```
ArrayList 是一个数组队列，相当于 动态数组。与Java中的数组相比，它的容量能动态增长。它继承于AbstractList，实现了List, RandomAccess, Cloneable, java.io.Serializable这些接口。

ArrayList 继承了AbstractList，实现了List。它是一个数组队列，提供了相关的添加、删除、修改、遍历等功能。

ArrayList 实现了RandmoAccess接口，即提供了随机访问功能。RandmoAccess是java中用来被List实现，为List提供快速访问功能的。在ArrayList中，我们即可以通过元素的序号快速获取元素对象；这就是快速随机访问。

ArrayList 实现了Cloneable接口，即覆盖了函数clone()，能被克隆。

ArrayList 实现java.io.Serializable接口，这意味着ArrayList支持序列化，能通过序列化去传输。

和Vector不同，ArrayList中的操作不是线程安全的！所以，建议在单线程中才使用ArrayList，而在多线程中可以选择Vector或者CopyOnWriteArrayList。

## ArrayList属性
ArrayList属性主要就是当前数组长度size，以及存放数组的对象elementData数组，除此之外还有一个经常用到的属性就是从AbstractList继承过来的modCount属性，代表ArrayList集合的修改次数。
```java
/**
 * 默认容量
 */
private static final int DEFAULT_CAPACITY = 10;

/**
 * Shared empty array instance used for empty instances.
 */
private static final Object[] EMPTY_ELEMENTDATA = {};

/**
 * Shared empty array instance used for default sized empty instances. We
 * distinguish this from EMPTY_ELEMENTDATA to know how much to inflate when
 * first element is added.
 */
private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

/**
 * The array buffer into which the elements of the ArrayList are stored.
 * The capacity of the ArrayList is the length of this array buffer. Any
 * empty ArrayList with elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA
 * will be expanded to DEFAULT_CAPACITY when the first element is added.
 */
transient Object[] elementData; // non-private to simplify nested class access

/**
 * 数组实际大小
 */
private int size;
```

## 构造函数
ArrayList 有3个构造函数：
```java
/**
 * 构造一个指定大小的 list
 *
 * @param  initialCapacity  指定的容量
 * @throws IllegalArgumentException 如果指定容量 initialCapacity 为负数
 */
public ArrayList(int initialCapacity) {
    if (initialCapacity > 0) {
        this.elementData = new Object[initialCapacity];
    } else if (initialCapacity == 0) {
      // 注意这里用的是 EMPTY_ELEMENTDATA
        this.elementData = EMPTY_ELEMENTDATA;
    } else {
        throw new IllegalArgumentException("Illegal Capacity: "+
                                           initialCapacity);
    }
}

/**
 * 无参构造，这里用的就是 DEFAULTCAPACITY_EMPTY_ELEMENTDATA，这样的话容量是使用默认的
 * DEFAULT_CAPACITY = 10，不跟上面的有参构造混淆
 */
public ArrayList() {
    this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
}

/**
 * 1. 将collection对象转换成数组，然后将数组的地址的赋给elementData
 * 2. 更新size的值，同时判断size的大小，如果是size等于0，直接将空对象EMPTY_ELEMENTDATA的地址赋给elementData
 * 3. 如果size的值大于0，则执行Arrays.copy方法，把collection对象的内容（可以理解为深拷贝）copy到elementData中。
 */
public ArrayList(Collection<? extends E> c) {
    elementData = c.toArray();
    if ((size = elementData.length) != 0) {
        // c.toArray might (incorrectly) not return Object[] (see 6260652)
        if (elementData.getClass() != Object[].class)
            elementData = Arrays.copyOf(elementData, size, Object[].class);
    } else {
        // replace with empty array.
        this.elementData = EMPTY_ELEMENTDATA;
    }
}
```

## 扩容
添加元素时使用 ensureCapacityInternal() 方法来保证容量足够，如果不够时，需要使用 grow() 方法进行扩容，新容量的大小为 oldCapacity + (oldCapacity >> 1)，也就是旧容量的 1.5 倍。

扩容操作需要调用 Arrays.copyOf() 把原数组整个复制到新数组中，这个操作代价很高，因此最好在创建 ArrayList 对象时就指定大概的容量大小，减少扩容操作的次数。
```java
public boolean add(E e) {
    ensureCapacityInternal(size + 1);  // Increments modCount!!
    elementData[size++] = e;
    return true;
}
```

确保添加的元素有地方存储，当第一次添加元素的时候this.size+1 的值是1，所以第一次添加的时候会将当前elementData数组的长度变为10
```java
private void ensureCapacityInternal(int minCapacity) {
    ensureExplicitCapacity(calculateCapacity(elementData, minCapacity));
}

private static int calculateCapacity(Object[] elementData, int minCapacity) {
    // 当 数组为 DEFAULTCAPACITY_EMPTY_ELEMENTDATA 时候，会将容量设置为 10
    // DEFAULTCAPACITY_EMPTY_ELEMENTDATA 是通过无参构造得到的
    if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
        return Math.max(DEFAULT_CAPACITY, minCapacity);
    }
    return minCapacity;
}
```

将修改次数（modCount）自增1，判断是否需要扩充数组长度,判断条件就是用当前所需的数组最小长度(minCapacity)与数组的长度对比，如果大于0，则增长数组长度。
```java
private void ensureExplicitCapacity(int minCapacity) {
    modCount++;
    // overflow-conscious code
    if (minCapacity - elementData.length > 0)
        grow(minCapacity);
}

/**
 * 增大容量确保数组能存储下 minCapacity 个元素
 *
 * @param minCapacity 所需的最小容量要求
 */
private void grow(int minCapacity) {
    // overflow-conscious code
    int oldCapacity = elementData.length;
    int newCapacity = oldCapacity + (oldCapacity >> 1); // 扩容1.5倍
    if (newCapacity - minCapacity < 0)
        newCapacity = minCapacity;
    if (newCapacity - MAX_ARRAY_SIZE > 0)
        newCapacity = hugeCapacity(minCapacity);
    // minCapacity is usually close to size, so this is a win:
    elementData = Arrays.copyOf(elementData, newCapacity);
}
```

add还有一个函数是给定位置的插入,方法跟上面类似，只是要做 index 的检测，然后像上面一样确认
容量，必要时进行容量扩容，最后再把 index 开始的元素往后移一个位置 System.arraycopy，最后把元素
插入到指定位置中
```java
public void add(int index, E element) {
    rangeCheckForAdd(index);

    ensureCapacityInternal(size + 1);  // Increments modCount!!
    System.arraycopy(elementData, index, elementData, index + 1,
                     size - index);
    elementData[index] = element;
    size++;
}
```

## Remove
Remove 有两个函数，一个是根据 index， 一个根据元素

需要调用 System.arraycopy() 将 index+1 后面的元素都复制到 index 位置上，该操作的时间复杂度为 O(N)，可以看出 ArrayList 删除元素的代价是非常高的。
```java
/**
 * @param index 要删除的位置
 * @return 删除完后的 list
 * @throws IndexOutOfBoundsException {@inheritDoc}
 */
public E remove(int index) {
    rangeCheck(index);

    modCount++;
    E oldValue = elementData(index);

    int numMoved = size - index - 1;
    if (numMoved > 0)
        System.arraycopy(elementData, index+1, elementData, index,
                         numMoved);
    elementData[--size] = null; // clear to let GC do its work

    return oldValue;
}
```

那元素来进行删除,从这里可以看出，ArrayList 是可以存 null 值的。有一点需要注意的就是它只
删除第一个匹配的元素（如果数组中含有该元素）
```java
/**
 * @param o 要删除的元素
 * @return true if this list contained the specified element
 */
public boolean remove(Object o) {
    if (o == null) {
        for (int index = 0; index < size; index++)
            if (elementData[index] == null) {
                fastRemove(index);
                return true;
            }
    } else {
        for (int index = 0; index < size; index++)
            if (o.equals(elementData[index])) {
                fastRemove(index);
                return true;
            }
    }
    return false;
}

/*
 * 不进行 index 检查，不返回删除的元素
 */
private void fastRemove(int index) {
    modCount++;
    int numMoved = size - index - 1;
    if (numMoved > 0)
        System.arraycopy(elementData, index+1, elementData, index,
                         numMoved);
    elementData[--size] = null; // clear to let GC do its work
}
```

## get
```java
public E get(int index) {
    rangeCheck(index);
    checkForComodification();
    return ArrayList.this.elementData(offset + index);
}
```
checkForComodification()
```java
private void checkForComodification() {
    if (ArrayList.this.modCount != this.modCount)
        throw new ConcurrentModificationException();
}
```
## fast-fail 快速失败
modCount 用来记录 ArrayList 结构发生变化的次数。结构发生变化是指添加或者删除至少一个元素的所有操作，或者是调整内部数组的大小，仅仅只是设置元素的值不算结构发生变化。

在进行序列化或者迭代等操作时，需要比较操作前后 modCount 是否改变，如果改变了需要抛出 ConcurrentModificationException。

```java
private void writeObject(java.io.ObjectOutputStream s)
    throws java.io.IOException{
    // Write out element count, and any hidden stuff
    int expectedModCount = modCount;
    s.defaultWriteObject();

    // Write out size as capacity for behavioural compatibility with clone()
    s.writeInt(size);

    // Write out all elements in the proper order.
    for (int i=0; i<size; i++) {
        s.writeObject(elementData[i]);
    }

    if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
    }
}
```

# Vector
## 概览
```java
public class Vector<E>
    extends AbstractList<E>
    implements List<E>, RandomAccess, Cloneable, java.io.Serializable{ // 略去其他部分
    }
```
跟 ArrayList 一致，Vector 继承了AbstractList，实现了List。它是一个数组队列，提供了相关的添加、删除、修改、遍历等功能。

Vector  实现了RandmoAccess接口，即提供了随机访问功能。RandmoAccess是java中用来被List实现，为List提供快速访问功能的。在 Vector 中，我们即可以通过元素的序号快速获取元素对象；这就是快速随机访问。

Vector  实现了Cloneable接口，即覆盖了函数 clone() ，能被克隆。

ArrayList 实现 Serializable 接口，这意味着ArrayList支持序列化，能通过序列化去传输。

## 属性
Vector 主要属性如下:
```java
    // 存储元素的数字
    protected Object[] elementData;
    // count
    protected int elementCount;
    // 每次增加的增量(如果不指定则:容量每次增大一倍)
    protected int capacityIncrement;
```

## 构造函数
Vector 一共有 4 个构造函数
```java
/**
     * 指定初始容量和自增量
     * @param   initialCapacity     初始容量
     * @param   capacityIncrement   自增量
     * @throws IllegalArgumentException if the specified initial capacity is negative
     */
public Vector(int initialCapacity, int capacityIncrement) {
    super();
    if (initialCapacity < 0)
        throw new IllegalArgumentException("Illegal Capacity: "+
                                           initialCapacity);
    this.elementData = new Object[initialCapacity];
    this.capacityIncrement = capacityIncrement;
}

/**
 * 指定初始容量，设置 自增量为 0，这样在扩容时候容量会 *2
 * @param   initialCapacity   初始容量
 * @throws IllegalArgumentException if the specified initial capacity is negative
 */
public Vector(int initialCapacity) {
    this(initialCapacity, 0);
}

/**
 * 无参构造，指定初始容量为 10，
 */
public Vector() {
    this(10);
}

/**
 * 用一个 Collection 对象构造 Vector
 * @param c Collection 对象
 * @throws NullPointerException if the specified collection is null
 */
public Vector(Collection<? extends E> c) {
    elementData = c.toArray();
    elementCount = elementData.length;
    // c.toArray might (incorrectly) not return Object[] (see 6260652)
    if (elementData.getClass() != Object[].class)
        elementData = Arrays.copyOf(elementData, elementCount, Object[].class);
}
```

## 扩容
Vector 的扩容机制和 ArrayList 基本一样，不同在于 Vector 中的方法都有 synchronized 保证线程安全

而且每次扩容是 \*2(在不指定自增量的情况下)
```java
public synchronized void addElement(E obj) {
    modCount++;
    ensureCapacityHelper(elementCount + 1);
    elementData[elementCount++] = obj;
}
private void ensureCapacityHelper(int minCapacity) {
    // overflow-conscious code
    if (minCapacity - elementData.length > 0)
        grow(minCapacity);
}
private void grow(int minCapacity) {
    // overflow-conscious code
    int oldCapacity = elementData.length;
    // 在不指定自增量的情况下,扩容为原容量 *2
    int newCapacity = oldCapacity + ((capacityIncrement > 0) ?
                                     capacityIncrement : oldCapacity);
    if (newCapacity - minCapacity < 0)
        newCapacity = minCapacity;
    if (newCapacity - MAX_ARRAY_SIZE > 0)
        newCapacity = hugeCapacity(minCapacity);
    elementData = Arrays.copyOf(elementData, newCapacity);
}
```

## get
```java
/**
 * 获取 index 位置的元素
 * @param index
 * @return index 位置的元素
 * @throws ArrayIndexOutOfBoundsException if the index is out of range ({@code index < 0 || index >= size()})
 */
public synchronized E get(int index) {
    if (index >= elementCount)
        throw new ArrayIndexOutOfBoundsException(index);

    return elementData(index);
}
```

## Vector与ArrayList的区别
- Vector 是同步的，因此开销就比 ArrayList 要大，访问速度更慢。最好使用 ArrayList 而不是 Vector，因为同步操作完全可以由程序员自己来控制；
- Vector 每次扩容请求其大小的 2 倍空间，而 ArrayList 是 1.5 倍。

## 替代方案
可以使用 Collections.synchronizedList(); 得到一个线程安全的 ArrayList。
```java
List<String> list = new ArrayList<>();
List<String> synList = Collections.synchronizedList(list);
```
也可以使用 concurrent 并发包下的 CopyOnWriteArrayList 类。
```java
List<String> list = new CopyOnWriteArrayList<>();
```

# synchronizedList
synchronizedList 是 Collections 类中的一个静态类，它和 Vector 主要的区别是 synchronizedList 使用静态块

synchronizedList中的部分代码
```java
public E get(int index) {
    synchronized (mutex) {return list.get(index);}
}
public E set(int index, E element) {
    synchronized (mutex) {return list.set(index, element);}
}
public void add(int index, E element) {
    synchronized (mutex) {list.add(index, element);}
}
public E remove(int index) {
    synchronized (mutex) {return list.remove(index);}
}
```

# CopyOnWriteArrayList
## 读写分离
写操作在一个复制的数组上进行，读操作还是在原始数组中进行，读写分离，互不影响。

写操作需要加锁，防止并发写入时导致写入数据丢失。

写操作结束之后需要把原始数组指向新的复制数组。
```java
/**
 * 增加元素
 * @param e element to be appended to this list
 * @return {@code true} (as specified by {@link Collection#add})
 */
public boolean add(E e) {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
        Object[] elements = getArray();   
        int len = elements.length;
        // 创建一个新的数组，然后复制原始数组，长度+1
        Object[] newElements = Arrays.copyOf(elements, len + 1);
        newElements[len] = e;
        // 将新数组赋予回原始数组
        setArray(newElements);
        return true;
    } finally {
        lock.unlock();
    }
}

final void setArray(Object[] a) {
    array = a;
}
```

读操作还是在原始数组中进行
```java
@SuppressWarnings("unchecked")
private E get(Object[] a, int index) {
    return (E) a[index];
}
```

## 适用场景
CopyOnWriteArrayList 在写操作的同时允许读操作，大大提高了读操作的性能，因此很适合读多写少的应用场景。

但是 CopyOnWriteArrayList 有其缺陷：
  - 内存占用：在写操作时需要复制一个新的数组，使得内存占用为原来的两倍左右；
  - 数据不一致：读操作不能读取实时性的数据，因为部分写操作的数据还未同步到读数组中。

所以 CopyOnWriteArrayList 不适合内存敏感以及对实时性要求很高的场景。

# LinkedList
## 概述
```java
public class LinkedList<E>
    extends AbstractSequentialList<E>
    implements List<E>, Deque<E>, Cloneable, java.io.Serializable
    {}
```
概括的说，LinkedList 是线程不安全的，允许元素为null的双向链表。
其底层数据结构是链表，它实现List<E>, Deque<E>, Cloneable, java.io.Serializable接口，它实现了Deque<E>,所以它也可以作为一个双端队列。和ArrayList比，没有实现RandomAccess所以其以下标，随机访问元素速度较慢。

因其底层数据结构是链表，所以可想而知，它的增删只需要移动指针即可，故时间效率较高。不需要批量扩容，也不需要预留空间，所以空间效率比ArrayList高。

缺点就是需要随机访问元素时，时间效率很低，虽然底层在根据下标查询Node的时候，会根据index判断目标Node在前半段还是后半段，然后决定是顺序还是逆序查询，以提升时间效率。不过随着n的增大，总体时间效率依然很低。

当每次增、删时，都会修改modCount。

## 属性和构造函数
```java
transient int size = 0;
// 头结点
transient Node<E> first;
// 尾节点
transient Node<E> last;
/**
 * 无参构造，啥都没干
 */
public LinkedList() {
}

/**
 * 将一个 Collection 对象加到链表中，从尾部加
 * @param  c the collection whose elements are to be placed into this list
 * @throws NullPointerException if the specified collection is null
 */
public LinkedList(Collection<? extends E> c) {
    this();
    addAll(c);
}
```

## Node
可以看出，这是一个双向链表。
```java
private static class Node<E> {
    E item;
    Node<E> next;
    Node<E> prev;

    Node(Node<E> prev, E element, Node<E> next) {
        this.item = element;
        this.next = next;
        this.prev = prev;
    }
}
```

## 增
### addAll
```java
//addAll ,在尾部批量增加
public boolean addAll(Collection<? extends E> c) {
    return addAll(size, c);
}
//以index为插入下标，插入集合c中所有元素
public boolean addAll(int index, Collection<? extends E> c) {
    checkPositionIndex(index);   // 检查 index 在 [0,size] 中
    // 先将 collection 转成数组
    Object[] a = c.toArray();
    int numNew = a.length;
    if (numNew == 0)  // 空数组则返回
        return false;

    Node<E> pred, succ;
    if (index == size) {  // 从尾部加入
        succ = null;
        pred = last;
    } else {  // 从 index 位置加入
        succ = node(index);
        pred = succ.prev;
    }

    for (Object o : a) {
        @SuppressWarnings("unchecked") E e = (E) o;
        Node<E> newNode = new Node<>(pred, e, null);
        if (pred == null)
            first = newNode;
        else
            pred.next = newNode;
        pred = newNode; //步进，当前的节点为前置节点了，为下次添加节点做准备
    }

    if (succ == null) { //循环结束后，判断，如果后置节点是null。 说明此时是在队尾append的。
        last = pred;  // 设置尾节点
    } else {
        pred.next = succ;
        succ.prev = pred;
    }

    size += numNew;
    modCount++;
    return true;
}
```

### add
```java
/**
 * 在链表尾部加入新节点
 * @param e element to be appended to this list
 * @return {@code true} (as specified by {@link Collection#add})
 */
public boolean add(E e) {
    linkLast(e);
    return true;
}
/**
 * 添加 e 为尾节点
 */
void linkLast(E e) {
    final Node<E> l = last;
    final Node<E> newNode = new Node<>(l, e, null);
    last = newNode;
    if (l == null)  // 空链表
        first = newNode;  // 设置头结点
    else
        l.next = newNode;
    size++;
    modCount++;
}
```

## get
```java
/**
  * 返回特定位置的节点
  *
  * @param index index of the element to return
  * @return the element at the specified position in this list
  * @throws IndexOutOfBoundsException {@inheritDoc}
  */
 public E get(int index) {
     checkElementIndex(index);
     return node(index).item;
 }
 /**
  * Returns the (non-null) Node at the specified element index.
  * 通过下标获取某个node 的时候，（add select），会根据index处于前半段还是后半段 进行一个折半，以提升查询效率
  */
 Node<E> node(int index) {
     if (index < (size >> 1)) {
         Node<E> x = first;
         for (int i = 0; i < index; i++)
             x = x.next;
         return x;
     } else {
         Node<E> x = last;
         for (int i = size - 1; i > index; i--)
             x = x.prev;
         return x;
     }
 }
```
