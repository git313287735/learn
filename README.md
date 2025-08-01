# 3D装箱算法 (3D Bin Packing Algorithm)

一个高效的3D装箱算法实现，使用八叉树优化空间查询和碰撞检测。

## 功能特性

- **多种装箱策略**: 底部左侧填充、最佳适应、首次适应、下次适应
- **八叉树优化**: 使用八叉树数据结构优化空间查询，提高大规模装箱的性能
- **物品旋转支持**: 支持物品的6种旋转方向，提高空间利用率
- **实时碰撞检测**: 高效的碰撞检测算法，确保物品不重叠
- **性能分析**: 提供详细的装箱结果统计和性能分析

## 项目结构

```
src/main/java/com/binpacking/
├── Point3D.java          # 3D点坐标类
├── Box3D.java            # 3D包围盒类
├── Item.java             # 物品类
├── Bin.java              # 容器类
├── Octree.java           # 八叉树实现
├── BinPacking3D.java     # 主装箱算法
└── BinPackingDemo.java   # 演示程序
```

## 核心算法

### 八叉树优化
- 将3D空间递归分割为8个子空间
- 动态调整树结构，适应物品分布
- O(log n) 时间复杂度的空间查询

### 装箱策略
1. **底部左侧填充**: 优先放置在容器底部和左侧
2. **最佳适应**: 选择浪费空间最少的位置
3. **首次适应**: 选择第一个可用位置
4. **下次适应**: 基于上次位置继续搜索

### 物品旋转
支持物品的6种旋转方向：
- (w, h, d) - 原始方向
- (w, d, h) - 绕X轴旋转
- (h, w, d) - 绕Z轴旋转
- (h, d, w) - 复合旋转
- (d, w, h) - 复合旋转
- (d, h, w) - 复合旋转

## 快速开始

### 环境要求
- Java 11 或更高版本
- Maven 3.6 或更高版本

### 编译和运行

```bash
# 编译项目
mvn compile

# 运行演示程序
mvn exec:java

# 或者直接运行主类
java -cp target/classes com.binpacking.BinPackingDemo

# 打包为可执行JAR
mvn package

# 运行打包后的JAR
java -jar target/3d-bin-packing-1.0.0-jar-with-dependencies.jar
```

## 使用示例

### 基本用法

```java
// 创建容器模板
Bin binTemplate = new Bin("Container", 10, 10, 10);

// 创建物品列表
List<Item> items = Arrays.asList(
    new Item("Item1", 3, 3, 3),
    new Item("Item2", 2, 4, 2),
    new Item("Item3", 1, 1, 8)
);

// 创建装箱算法实例
BinPacking3D packer = new BinPacking3D(
    BinPacking3D.PlacementStrategy.BOTTOM_LEFT_FILL, 
    true // 允许旋转
);

// 执行装箱
List<Bin> bins = packer.pack(items, binTemplate);

// 获取结果统计
BinPacking3D.PackingResult result = packer.getPackingResult(bins, items);
System.out.println(result);
```

### 高级用法

```java
// 使用不同策略
BinPacking3D bestFitPacker = new BinPacking3D(
    BinPacking3D.PlacementStrategy.BEST_FIT, 
    true
);

// 性能测试
long startTime = System.currentTimeMillis();
List<Bin> bins = bestFitPacker.pack(items, binTemplate);
long endTime = System.currentTimeMillis();

System.out.println("装箱时间: " + (endTime - startTime) + " ms");
```

## 性能特点

### 时间复杂度
- 基本装箱: O(n²) 其中 n 是物品数量
- 八叉树查询: O(log m) 其中 m 是已装入物品数量
- 整体复杂度: O(n² log m)

### 空间复杂度
- 八叉树存储: O(m)
- 总空间复杂度: O(n + m)

### 性能优化
- 使用八叉树减少碰撞检测时间
- 智能候选点生成减少搜索空间
- 物品预排序提高装箱效率

## 测试结果

演示程序包含以下测试场景：

1. **基本装箱示例**: 8个不同尺寸物品的装箱
2. **性能测试**: 50-500个物品的装箱性能测试
3. **策略比较**: 不同装箱策略的效果对比
4. **八叉树性能**: 1000个物品的八叉树性能测试

典型测试结果：
- 100个随机物品: ~50ms 装箱时间
- 500个随机物品: ~200ms 装箱时间
- 空间利用率: 通常在70-85%之间

## 扩展功能

### 自定义装箱策略
可以通过继承和实现自定义的装箱策略：

```java
// 实现自定义策略需要修改BinPacking3D类
// 添加新的PlacementStrategy枚举值
// 实现对应的查找位置方法
```

### 约束条件
当前实现支持：
- 容器尺寸约束
- 物品旋转约束
- 碰撞检测

可扩展支持：
- 重量约束
- 堆叠规则
- 物品分组

## 算法优势

1. **高效性**: 八叉树优化使大规模装箱成为可能
2. **灵活性**: 多种策略适应不同应用场景
3. **准确性**: 精确的碰撞检测确保结果正确
4. **可扩展性**: 模块化设计便于功能扩展

## 应用场景

- 物流仓储优化
- 集装箱装载规划
- 3D打印空间优化
- 游戏物品管理
- 建筑空间规划

## 许可证

本项目采用 MIT 许可证。详见 LICENSE 文件。