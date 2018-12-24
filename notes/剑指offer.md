# 1.单例模式
[单例模式](https://github.com/LycPandaria/JavaInterview/blob/master/notes/%E8%AE%BE%E8%AE%A1%E6%A8%A1%E5%BC%8F.md#Singleton-%E5%8D%95%E4%BE%8B)

# 数组
## 2.数组中重复的数字
[NowCode](https://www.nowcoder.com/practice/623a5ac0ea5b4e5f95552655361ae0a8?tpId=13&tqId=11203&tPage=1&rp=1&ru=/ta/coding-interviews&qru=/ta/coding-interviews/question-ranking)

### 问题描述
在一个长度为n的数组里的所有数字都在0到n-1的范围内。 数组中某些数字是重复的，但不知道有几个数字是重复的。也不知道每个数字重复几次。请找出数组中任意一个重复的数字。 例如，如果输入长度为7的数组{2,3,1,0,2,5,3}，那么对应的输出是第一个重复的数字2。

### 解题思路
解决这个问题最简单的做法是把输入的数组排序，然后遍历数组，判断 arr[i] 是否等于 arr[i+1]即可，这样的时间复杂度为 O(nlogn).

还可以利用哈希表来解决问题，遍历数组并将其尝试放入到哈希表中，如果在哈希表中已经存在，则说明重复了。这样的时间复杂度和空间复杂度都是O(n).

更好的解法是：从头到尾依次扫描这个数组，当扫描到下标为 i 的数字时，首先比较这个数字 arr[i] 是不是等于 i。如果是，则接着扫描下一个数字；如果不是，则拿它和第 arr[i] 个数字进行比较，如果相等，就找到了一个重复的数字，如果不等，就把第 i 个数字和第 arr[i] 个数字进行交换，重复此过程。**相当于每次将值为 i 的数调整到第 i 位**

以 (2, 3, 1, 0, 2, 5) 为例：
```text
position-0 : (2,3,1,0,2,5) // 2 <-> 1
             (1,3,2,0,2,5) // 1 <-> 3
             (3,1,2,0,2,5) // 3 <-> 0
             (0,1,2,3,2,5) // already in position
position-1 : (0,1,2,3,2,5) // already in position
position-2 : (0,1,2,3,2,5) // already in position
position-3 : (0,1,2,3,2,5) // already in position
position-4 : (0,1,2,3,2,5) // nums[i] == nums[nums[i]], exit
```
```java
public boolean duplicate(int numbers[],int length,int [] duplication) {
    // 检查
    if(numbers==null || length <=0)
        return false;
    for(int i = 0; i < length; i++)
        if(numbers[i] < 0 || numbers[i] > length - 1)
            return false;

    for(int i = 0; i < length; i++){
        while(numbers[i] != i){
            if(numbers[i] == numbers[numbers[i]]){
                duplication[0]=numbers[i];
                return true;
            }
            // 交换
            int tmp = numbers[i];
            numbers[i] = numbers[tmp];
            numbers[tmp] = tmp;
        }
    }
    return false;
}
```

## 3.二维数组中的查找
[NowCode](https://www.nowcoder.com/practice/abc3fe2ce8e146608e868a70efebf62e?tpId=13&tqId=11154&tPage=1&rp=1&ru=/ta/coding-interviews&qru=/ta/coding-interviews/question-ranking)
### 问题描述
在一个二维数组中（每个一维数组的长度相同），每一行都按照从左到右递增的顺序排序，每一列都按照从上到下递增的顺序排序。请完成一个函数，输入这样的一个二维数组和一个整数，判断数组中是否含有该整数。
```text
Consider the following matrix:
[
  [1,   4,  7, 11, 15],
  [2,   5,  8, 12, 19],
  [3,   6,  9, 16, 22],
  [10, 13, 14, 17, 24],
  [18, 21, 23, 26, 30]
]

Given target = 5, return true.
Given target = 20, return false.
```

### 解决思路
首先选取数组中右上角的数字 n 如果该数字等于要查找的数字 m，则查找过程结束；如果该数字 n 大于要查找的数字 m，则剔除这个 n 所在的列，因为 n 是该列的最小值，如果 n 小于 要查找的数字 m，则剔除 m 所在的行，因为 n 已经是所在行的最大值。

复杂度：O(M + N) + O(1)

```java
public boolean Find(int target, int [][] array) {
    if(array == null || array.length == 0 || array[0].length == 0)
        return false;
    int rows = array.length;
    int cols = array[0].length;
    int row = 0;
    int col = cols - 1;    // 从右上角开始
    while(col >=0 && row < rows){
        if(array[row][col] == target)
            return true;
        else if(array[row][col] > target){
            col--;
        }else
            row++;
    }
    return false;
}
```

## 4.替换空格
[NowCode](https://www.nowcoder.com/practice/4060ac7e3e404ad1a894ef3e17650423?tpId=13&tqId=11155&tPage=1&rp=1&ru=/ta/coding-interviews&qru=/ta/coding-interviews/question-ranking)
### 问题描述
请实现一个函数，将一个字符串中的每个空格替换成“%20”。例如，当字符串为We Are Happy.则经过替换之后的字符串为We%20Are%20Happy。

### 解题思路
最直观的做法是从头到尾扫描字符串，每次碰到空格字符的时候进行替换，但是这样，我们必须把后面的所有字符往后移两个字节。这样的方法时间复杂度为O(n^2)

更好的解法是：我们先遍历一次字符串，统计出空格的个数，然后计算出替换后的长度。准备两个指针 P1 和 P2，P1 指向原始字符串的末尾，P2 指向替换后的字符串末尾。接下来我们向前移动 P1， 逐个把它复制到 P2 所在位置，然后再遇到空格的时候 P1 向前移动一格，P2 前移3格并插入 "%20" 。**当 P1 和 P2 指向同一个位置，表明所以的空格已经替换完毕**

```java
public String replaceSpace(StringBuffer str) {
  int P1 = str.length() - 1;
    // 计算空格数量，遇到一个空格就在字符串后加"  "2个空格
    // 因为要替换掉 空格 -> %20 所以遇到一个空格，让字符串长度加2即可
    for(int i= 0; i <= P1; i++)
        if(str.charAt(i) == ' ')
            str.append("  ");   //  两个空格
    int P2 = str.length() - 1;
    while(P1 >=0 && P1 < P2){
        char c = str.charAt(P1--);
        if(c == ' '){
            str.setCharAt(P2--,'0');
            str.setCharAt(P2--,'2');
            str.setCharAt(P2--,'%');
        }else
            str.setCharAt(P2--,c);
    }
    return str.toString();
}
```
# 链表
## 5.从尾到头打印链表
[NowCode](https://www.nowcoder.com/practice/d0267f7f55b3412ba93bd35cfa8e8035?tpId=13&tqId=11156&tPage=1&rp=1&ru=/ta/coding-interviews&qru=/ta/coding-interviews/question-ranking)

### 问题描述
输入一个链表，按链表值从尾到头的顺序返回一个ArrayList。

### 解题思路
1. 栈结构
  每经过一个节点，把该节点放到一个栈中，遍历完整个链表。
  ```java
  public ArrayList<Integer> printListFromTailToHead(ListNode listNode) {
        Stack<Integer> stack = new Stack<>();
        while(listNode != null){
            stack.add(listNode.val);
            listNode = listNode.next;
        }
        ArrayList<Integer> result = new ArrayList<>();
        while(!stack.isEmpty())
            result.add(stack.pop());
        return result;
  }
  ```
2. 递归
  递归在本质上也是一个栈。我们每访问一个节点，先递归到它后面的节点，最后再输出节点的值。
  ```java
  public ArrayList<Integer> result = new ArrayList<>();
    public ArrayList<Integer> printListFromTailToHead(ListNode listNode) {
        if(listNode != null){
            printListFromTailToHead(listNode.next);
            result.add(listNode.val);
        }
        return result;
  }
  ```
