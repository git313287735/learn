package com.binpacking;

import java.util.*;

/**
 * 3D装箱算法演示程序
 */
public class BinPackingDemo {
    
    public static void main(String[] args) {
        System.out.println("=== 3D装箱算法演示 ===\n");
        
        // 运行基本示例
        runBasicExample();
        
        // 运行性能测试
        runPerformanceTest();
        
        // 运行策略比较
        runStrategyComparison();
        
        // 运行八叉树性能测试
        runOctreePerformanceTest();
    }
    
    /**
     * 基本示例
     */
    private static void runBasicExample() {
        System.out.println("1. 基本装箱示例");
        System.out.println("================");
        
        // 创建容器模板
        Bin binTemplate = new Bin("Template", 10, 10, 10);
        
        // 创建物品列表
        List<Item> items = Arrays.asList(
            new Item("Item1", 3, 3, 3),
            new Item("Item2", 2, 4, 2),
            new Item("Item3", 1, 1, 8),
            new Item("Item4", 4, 2, 3),
            new Item("Item5", 2, 2, 2),
            new Item("Item6", 1, 3, 4),
            new Item("Item7", 3, 1, 2),
            new Item("Item8", 2, 3, 1)
        );
        
        // 执行装箱
        BinPacking3D packer = new BinPacking3D();
        List<Bin> bins = packer.pack(items, binTemplate);
        
        // 显示结果
        displayResults(bins, items, packer);
        System.out.println();
    }
    
    /**
     * 性能测试
     */
    private static void runPerformanceTest() {
        System.out.println("2. 性能测试");
        System.out.println("===========");
        
        int[] itemCounts = {50, 100, 200, 500};
        Bin binTemplate = new Bin("Template", 20, 20, 20);
        
        for (int count : itemCounts) {
            System.out.printf("测试 %d 个物品:\n", count);
            
            // 生成随机物品
            List<Item> items = generateRandomItems(count);
            
            // 测试装箱时间
            long startTime = System.currentTimeMillis();
            BinPacking3D packer = new BinPacking3D();
            List<Bin> bins = packer.pack(items, binTemplate);
            long endTime = System.currentTimeMillis();
            
            BinPacking3D.PackingResult result = packer.getPackingResult(bins, items);
            
            System.out.printf("  时间: %d ms\n", endTime - startTime);
            System.out.printf("  结果: %s\n", result);
            System.out.println();
        }
    }
    
    /**
     * 策略比较
     */
    private static void runStrategyComparison() {
        System.out.println("3. 装箱策略比较");
        System.out.println("===============");
        
        Bin binTemplate = new Bin("Template", 15, 15, 15);
        List<Item> items = generateRandomItems(100);
        
        BinPacking3D.PlacementStrategy[] strategies = {
            BinPacking3D.PlacementStrategy.BOTTOM_LEFT_FILL,
            BinPacking3D.PlacementStrategy.BEST_FIT,
            BinPacking3D.PlacementStrategy.FIRST_FIT,
            BinPacking3D.PlacementStrategy.NEXT_FIT
        };
        
        for (BinPacking3D.PlacementStrategy strategy : strategies) {
            System.out.printf("策略: %s\n", strategy);
            
            long startTime = System.currentTimeMillis();
            BinPacking3D packer = new BinPacking3D(strategy, true);
            List<Bin> bins = packer.pack(new ArrayList<>(items), binTemplate);
            long endTime = System.currentTimeMillis();
            
            BinPacking3D.PackingResult result = packer.getPackingResult(bins, items);
            
            System.out.printf("  时间: %d ms\n", endTime - startTime);
            System.out.printf("  结果: %s\n", result);
            System.out.println();
        }
    }
    
    /**
     * 八叉树性能测试
     */
    private static void runOctreePerformanceTest() {
        System.out.println("4. 八叉树性能测试");
        System.out.println("=================");
        
        // 创建一个大容器
        Bin bin = new Bin("TestBin", 100, 100, 100);
        Octree octree = new Octree(bin.getBoundingBox());
        
        // 生成大量物品
        List<Item> items = generateRandomItems(1000);
        
        System.out.println("插入 1000 个物品到八叉树...");
        long startTime = System.currentTimeMillis();
        
        for (Item item : items) {
            // 随机放置物品
            double x = Math.random() * (100 - item.getWidth());
            double y = Math.random() * (100 - item.getHeight());
            double z = Math.random() * (100 - item.getDepth());
            item.setPosition(new Point3D(x, y, z));
            octree.insert(item);
        }
        
        long endTime = System.currentTimeMillis();
        System.out.printf("插入时间: %d ms\n", endTime - startTime);
        
        // 获取八叉树统计信息
        Octree.OctreeStats stats = octree.getStats();
        System.out.printf("八叉树统计: %s\n", stats);
        
        // 测试查询性能
        System.out.println("\n执行 1000 次碰撞检测查询...");
        startTime = System.currentTimeMillis();
        
        int collisionCount = 0;
        for (int i = 0; i < 1000; i++) {
            Box3D testBox = new Box3D(
                Math.random() * 95, Math.random() * 95, Math.random() * 95,
                5, 5, 5
            );
            if (octree.hasCollision(testBox)) {
                collisionCount++;
            }
        }
        
        endTime = System.currentTimeMillis();
        System.out.printf("查询时间: %d ms\n", endTime - startTime);
        System.out.printf("发现碰撞: %d 次\n", collisionCount);
        System.out.println();
    }
    
    /**
     * 生成随机物品
     */
    private static List<Item> generateRandomItems(int count) {
        List<Item> items = new ArrayList<>();
        Random random = new Random(42); // 固定种子以获得可重现的结果
        
        for (int i = 0; i < count; i++) {
            double width = 1 + random.nextDouble() * 4;  // 1-5
            double height = 1 + random.nextDouble() * 4; // 1-5
            double depth = 1 + random.nextDouble() * 4;  // 1-5
            
            items.add(new Item("Item" + i, width, height, depth));
        }
        
        return items;
    }
    
    /**
     * 显示装箱结果
     */
    private static void displayResults(List<Bin> bins, List<Item> originalItems, BinPacking3D packer) {
        System.out.printf("使用了 %d 个容器:\n", bins.size());
        
        for (int i = 0; i < bins.size(); i++) {
            Bin bin = bins.get(i);
            System.out.printf("  容器 %d: %s\n", i + 1, bin);
            
            // 显示容器中的物品
            for (Item item : bin.getItems()) {
                System.out.printf("    - %s\n", item);
            }
        }
        
        // 显示总体统计
        BinPacking3D.PackingResult result = packer.getPackingResult(bins, originalItems);
        System.out.printf("\n总体结果: %s\n", result);
        
        // 显示未装入的物品
        List<Item> unpackedItems = new ArrayList<>();
        for (Item item : originalItems) {
            if (!item.isPacked()) {
                unpackedItems.add(item);
            }
        }
        
        if (!unpackedItems.isEmpty()) {
            System.out.printf("未装入的物品 (%d):\n", unpackedItems.size());
            for (Item item : unpackedItems) {
                System.out.printf("  - %s\n", item);
            }
        }
    }
    
    /**
     * 创建预定义的测试场景
     */
    public static class TestScenarios {
        
        /**
         * 小物品密集装箱场景
         */
        public static List<Item> createSmallItemsScenario() {
            List<Item> items = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                items.add(new Item("Small" + i, 1, 1, 1));
            }
            return items;
        }
        
        /**
         * 大小物品混合场景
         */
        public static List<Item> createMixedSizeScenario() {
            return Arrays.asList(
                new Item("Large1", 8, 8, 8),
                new Item("Large2", 6, 6, 6),
                new Item("Medium1", 4, 4, 4),
                new Item("Medium2", 3, 5, 3),
                new Item("Medium3", 2, 6, 2),
                new Item("Small1", 1, 1, 1),
                new Item("Small2", 1, 2, 1),
                new Item("Small3", 2, 1, 1),
                new Item("Thin1", 8, 1, 1),
                new Item("Thin2", 1, 8, 1),
                new Item("Thin3", 1, 1, 8)
            );
        }
        
        /**
         * 长条形物品场景
         */
        public static List<Item> createLongItemsScenario() {
            return Arrays.asList(
                new Item("Long1", 10, 1, 1),
                new Item("Long2", 8, 1, 2),
                new Item("Long3", 6, 2, 1),
                new Item("Long4", 9, 1, 1),
                new Item("Long5", 7, 1, 3),
                new Item("Cube1", 2, 2, 2),
                new Item("Cube2", 3, 3, 3)
            );
        }
    }
}