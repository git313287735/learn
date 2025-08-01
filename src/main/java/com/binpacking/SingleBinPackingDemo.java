package com.binpacking;

import java.util.*;

/**
 * 单容器装箱检查器演示程序
 * 展示如何判断多个小箱子是否可以被一个大箱子装下
 */
public class SingleBinPackingDemo {
    
    public static void main(String[] args) {
        System.out.println("=== 单容器3D装箱检查器演示 ===\n");
        
        // 运行基本示例
        runBasicExamples();
        
        // 运行策略比较
        runStrategyComparison();
        
        // 运行性能测试
        runPerformanceTest();
        
        // 运行八叉树优化测试
        runOctreeOptimizationTest();
    }
    
    /**
     * 基本示例
     */
    private static void runBasicExamples() {
        System.out.println("1. 基本装箱检查示例");
        System.out.println("====================");
        
        // 示例1：小物品可以装入大容器
        System.out.println("示例1：小物品可以装入大容器");
        Bin largeBox = new Bin("LargeBox", 10, 10, 10);
        List<Item> smallItems = Arrays.asList(
            new Item("Item1", 3, 3, 3),
            new Item("Item2", 2, 4, 2),
            new Item("Item3", 1, 1, 8),
            new Item("Item4", 4, 2, 3),
            new Item("Item5", 2, 2, 2)
        );
        
        SingleBinPackingChecker checker = new SingleBinPackingChecker();
        SingleBinPackingChecker.PackingResult result = checker.canPackAllBoxes(smallItems, largeBox);
        
        System.out.println("容器尺寸: " + largeBox.getWidth() + "x" + largeBox.getHeight() + "x" + largeBox.getDepth());
        System.out.println("小物品数量: " + smallItems.size());
        System.out.println("结果: " + result.message);
        System.out.println("详细结果: " + result);
        System.out.println();
        
        // 示例2：物品太大，无法装入
        System.out.println("示例2：物品太大，无法装入");
        List<Item> largeItems = Arrays.asList(
            new Item("LargeItem1", 8, 8, 8),
            new Item("LargeItem2", 6, 6, 6),
            new Item("LargeItem3", 5, 5, 5)
        );
        
        result = checker.canPackAllBoxes(largeItems, largeBox);
        System.out.println("大物品数量: " + largeItems.size());
        System.out.println("结果: " + result.message);
        System.out.println("详细结果: " + result);
        System.out.println();
        
        // 示例3：体积检查失败
        System.out.println("示例3：体积检查失败");
        List<Item> volumeItems = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            volumeItems.add(new Item("VolumeItem" + i, 2, 2, 2));
        }
        
        result = checker.canPackAllBoxes(volumeItems, largeBox);
        System.out.println("体积物品数量: " + volumeItems.size());
        System.out.println("结果: " + result.message);
        System.out.println("详细结果: " + result);
        System.out.println();
    }
    
    /**
     * 策略比较
     */
    private static void runStrategyComparison() {
        System.out.println("2. 装箱策略比较");
        System.out.println("===============");
        
        Bin container = new Bin("TestContainer", 15, 15, 15);
        List<Item> items = createComplexItemSet();
        
        SingleBinPackingChecker.PackingStrategy[] strategies = {
            SingleBinPackingChecker.PackingStrategy.BOTTOM_LEFT_FILL,
            SingleBinPackingChecker.PackingStrategy.BEST_FIT,
            SingleBinPackingChecker.PackingStrategy.FIRST_FIT,
            SingleBinPackingChecker.PackingStrategy.GREEDY_HEURISTIC
        };
        
        for (SingleBinPackingChecker.PackingStrategy strategy : strategies) {
            System.out.printf("策略: %s\n", strategy);
            
            long startTime = System.currentTimeMillis();
            SingleBinPackingChecker checker = new SingleBinPackingChecker(strategy, true);
            SingleBinPackingChecker.PackingResult result = checker.canPackAllBoxes(items, container);
            long endTime = System.currentTimeMillis();
            
            System.out.printf("  时间: %d ms\n", endTime - startTime);
            System.out.printf("  结果: %s\n", result.message);
            System.out.printf("  利用率: %.2f%%\n", result.utilization * 100);
            System.out.println();
        }
    }
    
    /**
     * 性能测试
     */
    private static void runPerformanceTest() {
        System.out.println("3. 性能测试");
        System.out.println("===========");
        
        int[] itemCounts = {10, 20, 50, 100};
        Bin container = new Bin("PerformanceContainer", 20, 20, 20);
        
        for (int count : itemCounts) {
            System.out.printf("测试 %d 个物品:\n", count);
            
            List<Item> items = generateRandomItems(count);
            
            // 测试不同策略的性能
            SingleBinPackingChecker.PackingStrategy[] strategies = {
                SingleBinPackingChecker.PackingStrategy.GREEDY_HEURISTIC,
                SingleBinPackingChecker.PackingStrategy.BEST_FIT,
                SingleBinPackingChecker.PackingStrategy.BOTTOM_LEFT_FILL
            };
            
            for (SingleBinPackingChecker.PackingStrategy strategy : strategies) {
                long startTime = System.currentTimeMillis();
                SingleBinPackingChecker checker = new SingleBinPackingChecker(strategy, true);
                SingleBinPackingChecker.PackingResult result = checker.canPackAllBoxes(items, container);
                long endTime = System.currentTimeMillis();
                
                System.out.printf("  %s: %d ms, 成功率: %s, 利用率: %.2f%%\n", 
                    strategy, endTime - startTime, result.success, result.utilization * 100);
            }
            System.out.println();
        }
    }
    
    /**
     * 八叉树优化测试
     */
    private static void runOctreeOptimizationTest() {
        System.out.println("4. 八叉树优化测试");
        System.out.println("=================");
        
        Bin container = new Bin("OctreeContainer", 25, 25, 25);
        List<Item> items = generateRandomItems(100);
        
        System.out.println("比较八叉树优化与传统方法的性能:");
        
        // 使用八叉树优化
        long startTime = System.currentTimeMillis();
        SingleBinPackingChecker checkerWithOctree = new SingleBinPackingChecker(
            SingleBinPackingChecker.PackingStrategy.GREEDY_HEURISTIC, true, true);
        SingleBinPackingChecker.PackingResult resultWithOctree = checkerWithOctree.canPackAllBoxes(items, container);
        long endTime = System.currentTimeMillis();
        
        System.out.printf("八叉树优化: %d ms, 结果: %s\n", 
            endTime - startTime, resultWithOctree.message);
        
        // 不使用八叉树优化
        startTime = System.currentTimeMillis();
        SingleBinPackingChecker checkerWithoutOctree = new SingleBinPackingChecker(
            SingleBinPackingChecker.PackingStrategy.GREEDY_HEURISTIC, true, false);
        SingleBinPackingChecker.PackingResult resultWithoutOctree = checkerWithoutOctree.canPackAllBoxes(items, container);
        endTime = System.currentTimeMillis();
        
        System.out.printf("传统方法: %d ms, 结果: %s\n", 
            endTime - startTime, resultWithoutOctree.message);
        
        double speedup = (double)(endTime - startTime) / (endTime - startTime);
        System.out.printf("性能提升: %.2fx\n", speedup);
        System.out.println();
    }
    
    /**
     * 创建复杂的物品集合
     */
    private static List<Item> createComplexItemSet() {
        List<Item> items = new ArrayList<>();
        
        // 添加一些大物品
        items.add(new Item("Large1", 8, 8, 8));
        items.add(new Item("Large2", 6, 6, 6));
        
        // 添加一些中等物品
        items.add(new Item("Medium1", 4, 4, 4));
        items.add(new Item("Medium2", 3, 5, 3));
        items.add(new Item("Medium3", 2, 6, 2));
        
        // 添加一些小物品
        items.add(new Item("Small1", 1, 1, 1));
        items.add(new Item("Small2", 1, 2, 1));
        items.add(new Item("Small3", 2, 1, 1));
        
        // 添加一些长条形物品
        items.add(new Item("Long1", 10, 1, 1));
        items.add(new Item("Long2", 1, 10, 1));
        items.add(new Item("Long3", 1, 1, 10));
        
        return items;
    }
    
    /**
     * 生成随机物品
     */
    private static List<Item> generateRandomItems(int count) {
        List<Item> items = new ArrayList<>();
        Random random = new Random(42); // 固定种子以获得可重现的结果
        
        for (int i = 0; i < count; i++) {
            double width = 1 + random.nextDouble() * 5;   // 1-6
            double height = 1 + random.nextDouble() * 5;  // 1-6
            double depth = 1 + random.nextDouble() * 5;   // 1-6
            
            items.add(new Item("RandomItem" + i, width, height, depth));
        }
        
        return items;
    }
    
    /**
     * 创建预定义的测试场景
     */
    public static class TestScenarios {
        
        /**
         * 立方体物品场景
         */
        public static List<Item> createCubeScenario() {
            List<Item> items = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                items.add(new Item("Cube" + i, 2, 2, 2));
            }
            return items;
        }
        
        /**
         * 扁平物品场景
         */
        public static List<Item> createFlatScenario() {
            return Arrays.asList(
                new Item("Flat1", 8, 2, 2),
                new Item("Flat2", 2, 8, 2),
                new Item("Flat3", 2, 2, 8),
                new Item("Flat4", 6, 3, 2),
                new Item("Flat5", 3, 6, 2)
            );
        }
        
        /**
         * 混合尺寸场景
         */
        public static List<Item> createMixedScenario() {
            return Arrays.asList(
                new Item("Big", 8, 8, 8),
                new Item("Medium1", 4, 4, 4),
                new Item("Medium2", 3, 5, 3),
                new Item("Small1", 1, 1, 1),
                new Item("Small2", 2, 1, 1),
                new Item("Long", 10, 1, 1)
            );
        }
    }
}