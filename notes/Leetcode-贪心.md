<!-- GFM-TOC -->
* [1. 分配饼干](#1-分配饼干)
* [2. 不重叠的区间个数](#2-不重叠的区间个数)
* [3. 投飞镖刺破气球](#3-投飞镖刺破气球)
* [4. 根据身高和序号重组队列](#4-根据身高和序号重组队列)
* [5. 买卖股票最大的收益](#5-买卖股票最大的收益)
* [6. 买卖股票的最大收益 II](#6-买卖股票的最大收益-ii)
* [7. 种植花朵](#7-种植花朵)
* [9. 修改一个数成为非递减数组](#9-修改一个数成为非递减数组)
* [10. 子数组最大的和](#10-子数组最大的和)
* [11. 分隔字符串使同种字符出现在一起](#11-分隔字符串使同种字符出现在一起)
<!-- GFM-TOC -->


保证每次操作都是局部最优的，并且最后得到的结果是全局最优的。

# 1. 分配饼干

455\. Assign Cookies (Easy)

[Leetcode](https://leetcode.com/problems/assign-cookies/description/) / [力扣](https://leetcode-cn.com/problems/assign-cookies/description/)

```html
Input: grid[1,3], size[1,2,4]
Output: 2
```

题目描述：每个孩子都有一个满足度 grid，每个饼干都有一个大小 size，只有饼干的大小大于等于一个孩子的满足度，该孩子才会获得满足。求解最多可以获得满足的孩子数量。

1. 给一个孩子的饼干应当尽量小并且又能满足该孩子，这样大饼干才能拿来给满足度比较大的孩子。
2. 因为满足度最小的孩子最容易得到满足，所以先满足满足度最小的孩子。

在以上的解法中，我们只在每次分配时饼干时选择一种看起来是当前最优的分配方法，但无法保证这种局部最优的分配方法最后能得到全局最优解。我们假设能得到全局最优解，并使用反证法进行证明，即假设存在一种比我们使用的贪心策略更优的最优策略。如果不存在这种最优策略，表示贪心策略就是最优策略，得到的解也就是全局最优解。

证明：假设在某次选择中，贪心策略选择给当前满足度最小的孩子分配第 m 个饼干，第 m 个饼干为可以满足该孩子的最小饼干。假设存在一种最优策略，可以给该孩子分配第 n 个饼干，并且 m < n。我们可以发现，经过这一轮分配，贪心策略分配后剩下的饼干一定有一个比最优策略来得大。因此在后续的分配中，贪心策略一定能满足更多的孩子。也就是说不存在比贪心策略更优的策略，即贪心策略就是最优策略。

<div align="center"> <img src="https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/e69537d2-a016-4676-b169-9ea17eeb9037.gif" width="430px"> </div><br>

```java
// 用贪心法
// 先将两个数组排序，然后依次用饼干数组 s[] 去满足小孩 g[]
public int findContentChildren(int[] g, int[] s) {
    if(g == null || s == null) return 0;
    Arrays.sort(g);
    Arrays.sort(s);
    int gi = 0, si = 0;
    while(gi < g.length && si < s.length){
        if(g[gi] <= s[si]) gi++;
        si++;
    }
    return gi;
}
```

# 2. 不重叠的区间个数

435\. Non-overlapping Intervals (Medium)

[Leetcode](https://leetcode.com/problems/non-overlapping-intervals/description/) / [力扣](https://leetcode-cn.com/problems/non-overlapping-intervals/description/)

```html
Input: [ [1,2], [1,2], [1,2] ]

Output: 2

Explanation: You need to remove two [1,2] to make the rest of intervals non-overlapping.
```

```html
Input: [ [1,2], [2,3] ]

Output: 0

Explanation: You don't need to remove any of the intervals since they're already non-overlapping.
```

题目描述：计算让一组区间不重叠所需要移除的区间个数。

先计算最多能组成的不重叠区间个数，然后用区间总个数减去不重叠区间的个数。

在每次选择中，区间的结尾最为重要，选择的区间结尾越小，留给后面的区间的空间越大，那么后面能够选择的区间个数也就越大。

按区间的结尾进行排序，每次选择结尾最小，并且和前一个区间不重叠的区间。

```java
public int eraseOverlapIntervals(int[][] intervals) {
    if(intervals.length < 2) return 0;
    // 先区间结尾排序
    Arrays.sort(intervals, new Comparator<int[]>(){
        @Override
        public int compare(int[] o1, int[] o2){
            return o1[1] - o2[1];
        }
    });

    int cnt = 1;    // 不重复区间
    int end = intervals[0][1];  // 第一个区间的结尾

    for(int i = 1; i < intervals.length; i++){
        // 取下一个区间的开头，如果小于上一个区间的结尾，则说明重叠，这个区间就应该删去
        if(intervals[i][0] < end)
            continue;
        // 到这来说明没有重叠，增加不重复区间 cnt 个数，更新 end 为该区间的结尾
        cnt++;
        end = intervals[i][1];  //  
    }
    return intervals.length - cnt;
}

```

# 3. 投飞镖刺破气球

452\. Minimum Number of Arrows to Burst Balloons (Medium)

[Leetcode](https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/description/) / [力扣](https://leetcode-cn.com/problems/minimum-number-of-arrows-to-burst-balloons/description/)

```
Input:
[[10,16], [2,8], [1,6], [7,12]]

Output:
2
```

题目描述：气球在一个水平数轴上摆放，可以重叠，飞镖垂直投向坐标轴，使得路径上的气球都被刺破。求解最小的投飞镖次数使所有气球都被刺破。

也是计算不重叠的区间个数，不过和 Non-overlapping Intervals 的区别在于，[1, 2] 和 [2, 3] 在本题中算是重叠区间。

```java
public int findMinArrowShots(int[][] points) {
    if (points.length == 0) {
        return 0;
    }
    Arrays.sort(points, Comparator.comparingInt(o -> o[1]));
    int cnt = 1, end = points[0][1];
    for (int i = 1; i < points.length; i++) {
        if (points[i][0] <= end) {
            continue;
        }
        cnt++;
        end = points[i][1];
    }
    return cnt;
}
```

# 4. 根据身高和序号重组队列

406\. Queue Reconstruction by Height(Medium)

[Leetcode](https://leetcode.com/problems/queue-reconstruction-by-height/description/) / [力扣](https://leetcode-cn.com/problems/queue-reconstruction-by-height/description/)

```html
Input:
[[7,0], [4,4], [7,1], [5,0], [6,1], [5,2]]

Output:
[[5,0], [7,0], [5,2], [6,1], [4,4], [7,1]]
```

题目描述：一个学生用两个分量 (h, k) 描述，h 表示身高，k 表示排在前面的有 k 个学生的身高比他高或者和他一样高。

为了使插入操作不影响后续的操作，身高较高的学生应该先做插入操作，否则身高较小的学生原先正确插入的第 k 个位置可能会变成第 k+1 个位置。

身高 h 降序、个数 k 值升序，然后将某个学生插入队列的第 k 个位置中。

```java
public int[][] reconstructQueue(int[][] people) {
    if (people == null || people.length == 0 || people[0].length == 0) {
        return new int[0][0];
    }
    Arrays.sort(people, (a, b) -> (a[0] == b[0] ? a[1] - b[1] : b[0] - a[0]));
    List<int[]> queue = new ArrayList<>();
    for (int[] p : people) {
        queue.add(p[1], p);
    }
    return queue.toArray(new int[queue.size()][]);
}
```

# 5. 买卖股票最大的收益

121\. Best Time to Buy and Sell Stock (Easy)

[Leetcode](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/description/) / [力扣](https://leetcode-cn.com/problems/best-time-to-buy-and-sell-stock/description/)

题目描述：一次股票交易包含买入和卖出，只进行一次交易，求最大收益。

只要记录前面的最小价格，将这个最小价格作为买入价格，然后将当前的价格作为售出价格，查看当前收益是不是最大收益。

```java
public int maxProfit(int[] prices) {
    if(prices.length == 0) return 0;

    int min = prices[0];
    int profit = 0;
    for(int i = 1; i < prices.length; i++){
        if(prices[i] < min) min = prices[i];
        else profit = Math.max(profit, prices[i] - min);
    }
    return profit;
}
```


# 6. 买卖股票的最大收益 II

122\. Best Time to Buy and Sell Stock II (Easy)

[Leetcode](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/description/) / [力扣](https://leetcode-cn.com/problems/best-time-to-buy-and-sell-stock-ii/description/)

题目描述：可以进行多次交易，多次交易之间不能交叉进行，可以进行多次交易。

对于 [a, b, c, d]，如果有 a <= b <= c <= d ，那么最大收益为 d - a。而 d - a = (d - c) + (c - b) + (b - a) ，因此当访问到一个 prices[i] 且 prices[i] - prices[i-1] > 0，那么就把 prices[i] - prices[i-1] 添加到收益中。

```java
public int maxProfit(int[] prices) {
    if(prices.length == 0)  return 0;
    int profit = 0;
    for(int i = 1; i < prices.length; i++){
        if(prices[i] - prices[i-1] > 0) profit += (prices[i] - prices[i-1]);
    }
    return profit;
}
```


# 7. 种植花朵

605\. Can Place Flowers (Easy)

[Leetcode](https://leetcode.com/problems/can-place-flowers/description/) / [力扣](https://leetcode-cn.com/problems/can-place-flowers/description/)

```html
Input: flowerbed = [1,0,0,0,1], n = 1
Output: True
```

题目描述：flowerbed 数组中 1 表示已经种下了花朵。花朵之间至少需要一个单位的间隔，求解是否能种下 n 朵花。

```java
public boolean canPlaceFlowers(int[] flowerbed, int n) {
    int length = flowerbed.length;
    int index = 0;

    for(int i = 0; i < length; i ++){
        if(n == 0) return true;
        if(flowerbed[i] == 1) continue; // 已经有花

        int pre = i == 0 ? 0 : flowerbed[i-1];  // i-1位置是否为0，考虑i==0的情况
        int next = i == length - 1? 0 : flowerbed[i+1];

        if(pre == 0 && next == 0){  // 满足种植条件
            n--;
            flowerbed[i] = 1;
        }
    }
    return n == 0;
}
```

# 9. 修改一个数成为非递减数组

665\. Non-decreasing Array (Easy)

[Leetcode](https://leetcode.com/problems/non-decreasing-array/description/) / [力扣](https://leetcode-cn.com/problems/non-decreasing-array/description/)

```html
Input: [4,2,3]
Output: True
Explanation: You could modify the first 4 to 1 to get a non-decreasing array.
```

题目描述：判断一个数组是否能只修改一个数就成为非递减数组。

在出现 nums[i] < nums[i - 1] 时，需要考虑的是应该修改数组的哪个数，使得本次修改能使 i 之前的数组成为非递减数组，并且   **不影响后续的操作**  。优先考虑令 nums[i - 1] = nums[i]，因为如果修改 nums[i] = nums[i - 1] 的话，那么 nums[i] 这个数会变大，就有可能比 nums[i + 1] 大，从而影响了后续操作。还有一个比较特别的情况就是 nums[i] < nums[i - 2]，修改 nums[i - 1] = nums[i] 不能使数组成为非递减数组，只能修改 nums[i] = nums[i - 1]。

```java
public boolean checkPossibility(int[] nums) {
    int cnt = 0;    // 修改次数
    for(int i = 1; i < nums.length && cnt < 2; i++){
        if(nums[i] >= nums[i-1])    continue;   // 符合条件

        cnt ++;     // 需要进行修改
        // 这里需要分两种情况来讨论：
        // 1.正常情况下让 nums[i-1] = nums[i], 这样 nums[i] 不会变大，也就不会影响后面的数
        // 2.但是存在 nums[i-2] > nums[i] 情况的时候，如果让 nums[i-1] = nums[i]
        // nums[i-1] 便会小于 nums[i-2]，不符合条件
        // 例如：[4,2,1] 如果一直用 nums[i-1] = nums[i] 的方式
        // [4,2,1] -> [2,2,1] -> [2,1,1] 就不符合条件
        // 所以在 2 情况下，只能让 nums[i] = nums[i-1];
        // [4,2,1] -> [2,2,1] -> [2,2,2]
        if(i - 2 >= 0 && nums[i-2] > nums[i]){
            nums[i] = nums[i-1];
        }else {
            nums[i-1] = nums[i];    
        }
    }
    return cnt <= 1;
}
```



# 10. 子数组最大的和

53\. Maximum Subarray (Easy)

[Leetcode](https://leetcode.com/problems/maximum-subarray/description/) / [力扣](https://leetcode-cn.com/problems/maximum-subarray/description/)

另见：[剑指offer-连续子数组的最大和](./剑指offer.md#42连续子数组的最大和)

```html
For example, given the array [-2,1,-3,4,-1,2,1,-5,4],
the contiguous subarray [4,-1,2,1] has the largest sum = 6.
```

```java
public int maxSubArray(int[] nums) {
    if(nums == null || nums.length == 0) return 0;

    int preMax = nums[0];
    int max = preMax;

    for(int i=1; i < nums.length; i++){
        preMax = Math.max(nums[i], preMax + nums[i]);
        if(max < preMax){
            max = preMax;
        }
    }

    return max;
}
```

# 11. 分隔字符串使同种字符出现在一起

763\. Partition Labels (Medium)

[Leetcode](https://leetcode.com/problems/partition-labels/description/) / [力扣](https://leetcode-cn.com/problems/partition-labels/description/)

```html
Input: S = "ababcbacadefegdehijhklij"
Output: [9,7,8]
Explanation:
The partition is "ababcbaca", "defegde", "hijhklij".
This is a partition so that each letter appears in at most one part.
A partition like "ababcbacadefegde", "hijhklij" is incorrect, because it splits S into less parts.
```

策略就是不断地选择从最左边起最小的区间。可以从第一个字母开始分析，假设第一个字母是 'a'，那么第一个区间一定包含最后一次出现的 'a'。但第一个出现的 'a' 和最后一个出现的 'a' 之间可能还有其他字母，这些字母会让区间变大。举个例子，在 "abccaddbeffe" 字符串中，第一个最小的区间是 "abccaddb"。
通过以上的分析，我们可以得出一个算法：对于遇到的每一个字母，去找这个字母最后一次出现的位置，用来更新当前的最小区间。

```java
public List<Integer> partitionLabels(String S) {
    // 构建 last 数组，存储每个字符的最后出现位置
    int last[] = new int[26];
    for(int i = 0; i < S.length(); i++){
        last[S.charAt(i) - 'a'] = i;
    }

    ArrayList<Integer> res = new ArrayList<>();
    // 从第一个字符起到它的最后出现位置作为初始区间
    // 然后遍历字符串，根据最后出现位置来判断是否需要扩大分区或者新开分区
    int j = 0, index = 0;
    for(int i = 0; i < S.length() ; i++){
        //  根据字符最后出现位置来判断是否需要扩大区间
        j = Math.max(j, last[S.charAt(i) - 'a']);   
        if(i == j){
            // 这里说明已经确定分区，保存结果并新分区
            res.add(j - index + 1);
            index = j + 1;
        }
    }

    return res;
}
```
