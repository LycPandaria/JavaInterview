
# Kth Element 和 topK

## 快排

用于求解   **Kth Element**   问题，也就是第 K 个元素的问题。

可以使用快速排序的 partition() 进行实现。需要先打乱数组，否则最坏情况下时间复杂度为 O(N<sup>2</sup>)。

## 堆

用于求解   **TopK Elements**   问题，也就是 K 个最小元素的问题。可以维护一个大小为 K 的最小堆，最小堆中的元素就是最小元素。最小堆需要使用大顶堆来实现，大顶堆表示堆顶元素是堆中最大元素。这是因为我们要得到 k 个最小的元素，因此当遍历到一个新的元素时，需要知道这个新元素是否比堆中最大的元素更小，更小的话就把堆中最大元素去除，并将新元素添加到堆中。所以我们需要很容易得到最大元素并移除最大元素，大顶堆就能很好满足这个要求。

堆也可以用于求解 Kth Element 问题，得到了大小为 k 的最小堆之后，因为使用了大顶堆来实现，因此堆顶元素就是第 k 大的元素。

快速选择也可以求解 TopK Elements 问题，因为找到 Kth Element 之后，再遍历一次数组，所有小于等于 Kth Element 的元素都是 TopK Elements。

可以看到，快速选择和堆排序都可以求解 Kth Element 和 TopK Elements 问题。

## 1. Kth Element

类似问题，剑指offer第40题[最小的 K 个数](./剑指offer.md#40最小的-k-个数)
215\. Kth Largest Element in an Array (Medium)

[Leetcode](https://leetcode.com/problems/kth-largest-element-in-an-array/description/) / [力扣](https://leetcode-cn.com/problems/kth-largest-element-in-an-array/description/)

```text
Input: [3,2,1,5,6,4] and k = 2
Output: 5
```

题目描述：找到倒数第 k 个的元素。

**排序**  ：时间复杂度 O(NlogN)，空间复杂度 O(1)

```java
public int findKthLargest(int[] nums, int k) {
    Arrays.sort(nums);
    return nums[nums.length - k];
}
```

**堆**  ：时间复杂度 O(NlogK)，空间复杂度 O(K)。

```java
public int findKthLargest(int[] nums, int k) {
    PriorityQueue<Integer> pq = new PriorityQueue<>(); // 小顶堆
    for (int val : nums) {
        pq.add(val);
        if (pq.size() > k)  // 维护堆的大小为 K
            pq.poll();
    }
    return pq.peek();
}
```

**快速选择**  ：时间复杂度 O(N)，空间复杂度 O(1)

```java
public int findKthLargest(int[] nums, int k) {
    /* 快速选择 */
    if(k > nums.length || k <= 0)
        return Integer.MIN_VALUE;

    // 排序为升序排序，求第 k 大的元素，就是找 nums[nums.length - k]
    k = nums.length - k;    
    int high = nums.length - 1;
    int low = 0;

    while(low < high){
        // 得到 nums[low] 在数组中的位置
        int j = partition(nums, low, high);
        if(j == k) break;
        // 如果 j > k, 说明 nums[low] < nums[k] < nums[j]
        if(j > k) high = j - 1;
        else low = j + 1;   
    }
    return nums[k];
}

public int partition(int[] arr, int low, int high){
    // j = high + 1 是为了下面的 a[--j] 第一次得到的 a[high] 而不会越界
    int i = low, j = high + 1;
    int p = arr[low];  // 哨兵
    // 把哨兵放在 nums[low .. high] 合适的位置上
    // 即把小于 p 的元素放在左边，大于 p 的元素放在右边，返回 p 的位置
    while(true){
        // 从左边寻找第一个比 p 大的数，否则一直 ++i
        while(arr[++i] < p && i < high);
        // 从右边寻找第一个比 p 小的数，否则一直 --j
        while(arr[--j] > p && j > low);
        // j 左边的元素都比 p 小，j 右边的元素都比 p 大，可以退出
        if(i >= j) break;
        // 找到一个元素在高位但是比 p 小，将其交换到低位，再进入循环
        swap(arr, i, j);
    }
    // p 归位
    swap(arr, low, j);
    return j;
}

private void swap(int[] arr, int i, int j){
    int tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
}
}
```

# 桶排序

## 1. 出现频率最多的 k 个元素

347\. Top K Frequent Elements (Medium)

[Leetcode](https://leetcode.com/problems/top-k-frequent-elements/description/) / [力扣](https://leetcode-cn.com/problems/top-k-frequent-elements/description/)

```html
Given [1,1,1,2,2,3] and k = 2, return [1,2].
```

设置若干个桶，每个桶存储出现频率相同的数。桶的下标表示数出现的频率，即第 i 个桶中存储的数出现的频率为 i。

把数都放到桶之后，从后向前遍历桶，最先得到的 k 个数就是出现频率最多的的 k 个数。

```java
// 桶排序
public int[] topKFrequent(int[] nums, int k) {
    // 首先遍历数组，建立 num--frequency 的 map
    Map<Integer, Integer> freqMap = new HashMap<>();
    for(int num : nums){
        freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
    }

    // 将 map 转换成桶，bucket[i] 即为 freq 为 i 的数字的集合
    List<Integer>[] buckets = new ArrayList[nums.length + 1]; // 确保桶的数量足够,所以要 + 1
    for(int key : freqMap.keySet()){
        int freq = freqMap.get(key);    // 获取频率
        if(buckets[freq] == null) buckets[freq] = new ArrayList<>();
        buckets[freq].add(key);     // 将频率为 freq 的整数放入桶 buckets[freq] 中
    }

    // itor
    List<Integer> topK = new ArrayList<>();
    for(int i = buckets.length - 1; i >= 0; i --){
        if(buckets[i] == null)   continue;
        // 出现频率为 i 的元素个数 <= k - topK.size()
        // 就把元素全部放入 topK
        if(buckets[i].size() <= (k - topK.size()))
            topK.addAll(buckets[i]);
        else{
            // topK 中的元素个数不足以容纳出现频率为 i 的元素个数
            // 例如 出现1次的元素有3个，但是topK中只剩一个位置
            topK.addAll(buckets[i].subList(0, k - topK.size()));
            break;
            }
    }
    // List<Integer> -> int[]
    return topK.stream().mapToInt(Integer::valueOf).toArray();
}
```

## 2. 按照字符出现次数对字符串排序

451\. Sort Characters By Frequency (Medium)

[Leetcode](https://leetcode.com/problems/sort-characters-by-frequency/description/) / [力扣](https://leetcode-cn.com/problems/sort-characters-by-frequency/description/)

```html
Input:
"tree"

Output:
"eert"

Explanation:
'e' appears twice while 'r' and 't' both appear once.
So 'e' must appear before both 'r' and 't'. Therefore "eetr" is also a valid answer.
```

```java
public String frequencySort(String s) {
    Map<Character, Integer> frequencyForNum = new HashMap<>();
    for (char c : s.toCharArray())
        frequencyForNum.put(c, frequencyForNum.getOrDefault(c, 0) + 1);

    List<Character>[] frequencyBucket = new ArrayList[s.length() + 1];
    for (char c : frequencyForNum.keySet()) {
        int f = frequencyForNum.get(c);
        if (frequencyBucket[f] == null) {
            frequencyBucket[f] = new ArrayList<>();
        }
        frequencyBucket[f].add(c);
    }
    StringBuilder str = new StringBuilder();
    for (int i = frequencyBucket.length - 1; i >= 0; i--) {
        if (frequencyBucket[i] == null) {
            continue;
        }
        for (char c : frequencyBucket[i]) {
            for (int j = 0; j < i; j++) {
                str.append(c);
            }
        }
    }
    return str.toString();
}
```
