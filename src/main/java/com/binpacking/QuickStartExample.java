package com.binpacking;

import java.util.Arrays;
import java.util.List;

/**
 * 3D装箱算法快速开始示例
 * 展示如何简单使用算法判断多个小箱子是否可以被一个大箱子装下
 */
public class QuickStartExample {
    
    public static void main(String[] args) {
        System.out.println("=== 3D装箱算法快速开始示例 ===\n");
        
        // 示例1：简单的装箱检查
        simpleExample();
        
        // 示例2：带旋转的装箱检查
        rotationExample();
        
        // 示例3：不同策略的装箱检查
        strategyExample();
    }
    
    /**
     * 简单示例：检查小箱子是否可以装入大箱子
     */
    private static void simpleExample() {
        System.out.println("示例1：简单装箱检查");
        System.out.println("==================");
        
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
        System.out.println("容器尺寸: " + largeBox.getWidth() + "x" + largeBox.getHeight() + "x" + largeBox.getDepth());
        System.out.println("小箱子数量: " + smallBoxes.size());
        System.out.println("检查结果: " + result.message);
        System.out.println("是否成功: " + result.success);
        System.out.println("装入数量: " + result.packedCount + "/" + result.totalCount);
        System.out.println("空间利用率: " + String.format("%.2f%%", result.utilization * 100));
        System.out.println();
    }
    
    /**
     * 旋转示例：允许箱子旋转的装箱检查
     */
    private static void rotationExample() {
        System.out.println("示例2：带旋转的装箱检查");
        System.out.println("=====================");
        
        // 定义大箱子
        Bin largeBox = new Bin("Container", 8, 8, 8);
        
        // 定义一些长条形的小箱子
        List<Item> longBoxes = Arrays.asList(
            new Item("LongBox1", 6, 2, 2),  // 长条形，可以旋转
            new Item("LongBox2", 2, 6, 2),  // 长条形，可以旋转
            new Item("LongBox3", 2, 2, 6),  // 长条形，可以旋转
            new Item("CubeBox", 3, 3, 3)    // 立方体
        );
        
        // 创建允许旋转的装箱检查器
        SingleBinPackingChecker checkerWithRotation = new SingleBinPackingChecker(
            SingleBinPackingChecker.PackingStrategy.GREEDY_HEURISTIC, true);
        
        // 创建不允许旋转的装箱检查器
        SingleBinPackingChecker checkerWithoutRotation = new SingleBinPackingChecker(
            SingleBinPackingChecker.PackingStrategy.GREEDY_HEURISTIC, false);
        
        // 执行装箱检查（允许旋转）
        SingleBinPackingChecker.PackingResult resultWithRotation = checkerWithRotation.canPackAllBoxes(longBoxes, largeBox);
        
        // 执行装箱检查（不允许旋转）
        SingleBinPackingChecker.PackingResult resultWithoutRotation = checkerWithoutRotation.canPackAllBoxes(longBoxes, largeBox);
        
        // 输出结果
        System.out.println("容器尺寸: " + largeBox.getWidth() + "x" + largeBox.getHeight() + "x" + largeBox.getDepth());
        System.out.println("长条形箱子数量: " + longBoxes.size());
        System.out.println();
        
        System.out.println("允许旋转:");
        System.out.println("  结果: " + resultWithRotation.message);
        System.out.println("  利用率: " + String.format("%.2f%%", resultWithRotation.utilization * 100));
        System.out.println();
        
        System.out.println("不允许旋转:");
        System.out.println("  结果: " + resultWithoutRotation.message);
        System.out.println("  利用率: " + String.format("%.2f%%", resultWithoutRotation.utilization * 100));
        System.out.println();
    }
    
    /**
     * 策略示例：比较不同装箱策略的效果
     */
    private static void strategyExample() {
        System.out.println("示例3：不同策略的装箱检查");
        System.out.println("=======================");
        
        // 定义大箱子
        Bin largeBox = new Bin("Container", 12, 12, 12);
        
        // 定义混合尺寸的小箱子
        List<Item> mixedBoxes = Arrays.asList(
            new Item("BigBox", 8, 8, 8),      // 大箱子
            new Item("MediumBox1", 4, 4, 4),  // 中等箱子
            new Item("MediumBox2", 3, 5, 3),  // 中等箱子
            new Item("SmallBox1", 1, 1, 1),   // 小箱子
            new Item("SmallBox2", 2, 1, 1),   // 小箱子
            new Item("LongBox", 10, 1, 1)     // 长条形箱子
        );
        
        // 定义不同的装箱策略
        SingleBinPackingChecker.PackingStrategy[] strategies = {
            SingleBinPackingChecker.PackingStrategy.BOTTOM_LEFT_FILL,
            SingleBinPackingChecker.PackingStrategy.BEST_FIT,
            SingleBinPackingChecker.PackingStrategy.FIRST_FIT,
            SingleBinPackingChecker.PackingStrategy.GREEDY_HEURISTIC
        };
        
        System.out.println("容器尺寸: " + largeBox.getWidth() + "x" + largeBox.getHeight() + "x" + largeBox.getDepth());
        System.out.println("混合箱子数量: " + mixedBoxes.size());
        System.out.println();
        
        // 测试每种策略
        for (SingleBinPackingChecker.PackingStrategy strategy : strategies) {
            SingleBinPackingChecker checker = new SingleBinPackingChecker(strategy, true);
            SingleBinPackingChecker.PackingResult result = checker.canPackAllBoxes(mixedBoxes, largeBox);
            
            System.out.println("策略: " + strategy);
            System.out.println("  结果: " + result.message);
            System.out.println("  利用率: " + String.format("%.2f%%", result.utilization * 100));
            System.out.println();
        }
    }
    
    /**
     * 实用方法：快速检查装箱可行性
     */
    public static boolean canPackBoxes(List<Item> smallBoxes, Bin largeBox) {
        SingleBinPackingChecker checker = new SingleBinPackingChecker();
        SingleBinPackingChecker.PackingResult result = checker.canPackAllBoxes(smallBoxes, largeBox);
        return result.success;
    }
    
    /**
     * 实用方法：获取装箱详细信息
     */
    public static SingleBinPackingChecker.PackingResult getPackingDetails(List<Item> smallBoxes, Bin largeBox) {
        SingleBinPackingChecker checker = new SingleBinPackingChecker();
        return checker.canPackAllBoxes(smallBoxes, largeBox);
    }
}