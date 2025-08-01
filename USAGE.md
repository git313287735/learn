# 3D装箱算法使用说明

## 快速开始

### 1. 基本使用

```java
import com.binpacking.*;
import java.util.*;

// 定义大箱子（容器）
Bin largeBox = new Bin("Container", 10, 10, 10);

// 定义小箱子列表
List<Item> smallBoxes = Arrays.asList(
    new Item("Box1", 3, 3, 3),
    new Item("Box2", 2, 4, 2),
    new Item("Box3", 1, 1, 8),
    new Item("Box4", 4, 2, 3),
    new Item("Box5", 2, 2, 2)
);

// 创建装箱检查器
SingleBinPackingChecker checker = new SingleBinPackingChecker();

// 执行装箱检查
SingleBinPackingChecker.PackingResult result = checker.canPackAllBoxes(smallBoxes, largeBox);

// 输出结果
System.out.println("是否成功: " + result.success);
System.out.println("装入数量: " + result.packedCount + "/" + result.totalCount);
System.out.println("空间利用率: " + String.format("%.2f%%", result.utilization * 100));
System.out.println("详细信息: " + result.message);
```

### 2. 使用不同策略

```java
// 使用贪心启发式策略（推荐）
SingleBinPackingChecker greedyChecker = new SingleBinPackingChecker(
    SingleBinPackingStrategy.GREEDY_HEURISTIC, true);

// 使用最佳适应策略
SingleBinPackingChecker bestFitChecker = new SingleBinPackingChecker(
    SingleBinPackingStrategy.BEST_FIT, true);

// 使用底部左侧填充策略
SingleBinPackingChecker bottomLeftChecker = new SingleBinPackingChecker(
    SingleBinPackingStrategy.BOTTOM_LEFT_FILL, true);
```

### 3. 控制旋转和优化

```java
// 允许旋转，启用八叉树优化（默认）
SingleBinPackingChecker checker1 = new SingleBinPackingChecker(
    SingleBinPackingStrategy.GREEDY_HEURISTIC, true, true);

// 不允许旋转，启用八叉树优化
SingleBinPackingChecker checker2 = new SingleBinPackingChecker(
    SingleBinPackingStrategy.GREEDY_HEURISTIC, false, true);

// 允许旋转，禁用八叉树优化（使用传统碰撞检测）
SingleBinPackingChecker checker3 = new SingleBinPackingChecker(
    SingleBinPackingStrategy.GREEDY_HEURISTIC, true, false);
```

## 装箱策略说明

### 1. GREEDY_HEURISTIC（贪心启发式）- 推荐
- 按体积降序排序，然后按最小边升序排序
- 综合考虑位置和物品特性
- 通常能获得较好的空间利用率

### 2. BEST_FIT（最佳适应）
- 按表面积与体积比降序排序
- 优先放置"扁平"的物品
- 适合处理扁平物品较多的场景

### 3. BOTTOM_LEFT_FILL（底部左侧填充）
- 按体积降序，然后按最长边降序排序
- 优先填充底部和左侧空间
- 简单高效，适合快速装箱

### 4. FIRST_FIT（首次适应）
- 按最长边降序排序
- 找到第一个可放置的位置
- 速度最快，但利用率可能较低

## 运行示例

### 编译和运行

```bash
# 编译项目
javac -d target/classes -cp . src/main/java/com/binpacking/*.java

# 运行快速开始示例
java -cp target/classes com.binpacking.QuickStartExample

# 运行完整演示
java -cp target/classes com.binpacking.SingleBinPackingDemo
```

## 核心类说明

### SingleBinPackingChecker
主要的装箱检查器类，提供以下功能：
- `canPackAllBoxes(List<Item> smallBoxes, Bin largeBox)`: 检查装箱可行性
- 支持多种装箱策略
- 支持物品旋转
- 支持八叉树优化

### Item
表示需要装箱的物品：
- 包含尺寸信息（长、宽、高）
- 支持位置设置和状态管理
- 提供旋转变体生成

### Bin
表示装箱容器：
- 定义容器尺寸
- 管理已装入的物品
- 提供碰撞检测功能

### Octree
八叉树数据结构：
- 优化空间查询性能
- 加速碰撞检测
- 支持动态插入和删除

## 性能特点

### 时间复杂度
- 基本装箱算法：O(n²)
- 使用八叉树优化：O(n log n)
- 其中 n 为物品数量

### 空间复杂度
- 八叉树：O(n)
- 物品存储：O(n)

### 优化效果
- 八叉树优化可显著提升大规模装箱问题的性能
- 对于100个物品的装箱问题，性能提升可达3-5倍

## 适用场景

- **物流装箱**：判断货物是否可以装入指定容器
- **仓库管理**：优化存储空间利用
- **运输规划**：车辆装载优化
- **3D打印**：模型切片和布局优化
- **游戏开发**：3D场景中的物体放置