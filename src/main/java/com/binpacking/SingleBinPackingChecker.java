package com.binpacking;

import java.util.*;

/**
 * 单容器装箱检查器
 * 专门用于判断多个小箱子是否可以被一个大箱子装下
 * 使用八叉树优化空间查询和碰撞检测
 */
public class SingleBinPackingChecker {
    
    /**
     * 装箱策略枚举
     */
    public enum PackingStrategy {
        BOTTOM_LEFT_FILL,    // 底部左侧填充
        BEST_FIT,           // 最佳适应
        FIRST_FIT,          // 首次适应
        GREEDY_HEURISTIC    // 贪心启发式
    }
    
    private final PackingStrategy strategy;
    private final boolean allowRotation;
    private final boolean enableOctreeOptimization;
    
    public SingleBinPackingChecker(PackingStrategy strategy, boolean allowRotation) {
        this(strategy, allowRotation, true);
    }
    
    public SingleBinPackingChecker(PackingStrategy strategy, boolean allowRotation, boolean enableOctreeOptimization) {
        this.strategy = strategy;
        this.allowRotation = allowRotation;
        this.enableOctreeOptimization = enableOctreeOptimization;
    }
    
    public SingleBinPackingChecker() {
        this(PackingStrategy.GREEDY_HEURISTIC, true, true);
    }
    
    /**
     * 检查多个小箱子是否可以被一个大箱子装下
     * 
     * @param smallBoxes 小箱子列表
     * @param largeBox 大箱子
     * @return 装箱结果
     */
    public PackingResult canPackAllBoxes(List<Item> smallBoxes, Bin largeBox) {
        // 快速体积检查
        if (!volumeCheck(smallBoxes, largeBox)) {
            return new PackingResult(false, 0, 0, 0.0, 0.0, 0.0, 
                "总体积超过容器体积");
        }
        
        // 创建容器副本
        Bin container = new Bin(largeBox.getId(), largeBox.getWidth(), 
            largeBox.getHeight(), largeBox.getDepth());
        
        // 创建八叉树用于优化碰撞检测
        Octree octree = null;
        if (enableOctreeOptimization) {
            octree = new Octree(container.getBoundingBox());
        }
        
        // 复制物品列表
        List<Item> itemsToPack = new ArrayList<>();
        for (Item item : smallBoxes) {
            itemsToPack.add(new Item(item.getId(), item.getWidth(), 
                item.getHeight(), item.getDepth()));
        }
        
        // 按策略排序物品
        sortItemsByStrategy(itemsToPack);
        
        // 尝试装箱
        int packedCount = 0;
        List<Item> packedItems = new ArrayList<>();
        
        for (Item item : itemsToPack) {
            Point3D position = findPositionForItem(item, container, octree);
            
            if (position != null) {
                item.setPosition(position);
                item.setPacked(true);
                container.addItem(item);
                packedItems.add(item);
                
                if (octree != null) {
                    octree.insert(item);
                }
                
                packedCount++;
            } else {
                // 无法放置当前物品
                break;
            }
        }
        
        boolean success = packedCount == smallBoxes.size();
        double utilization = success ? container.getVolumeUtilization() : 0.0;
        
        String message = success ? 
            String.format("成功装入所有 %d 个箱子，利用率 %.2f%%", packedCount, utilization * 100) :
            String.format("只能装入 %d/%d 个箱子", packedCount, smallBoxes.size());
        
        return new PackingResult(success, packedCount, smallBoxes.size(), 
            container.getVolume(), container.getUsedVolume(), utilization, message);
    }
    
    /**
     * 快速体积检查
     */
    private boolean volumeCheck(List<Item> items, Bin container) {
        double totalVolume = 0.0;
        for (Item item : items) {
            totalVolume += item.getVolume();
        }
        return totalVolume <= container.getVolume();
    }
    
    /**
     * 根据策略排序物品
     */
    private void sortItemsByStrategy(List<Item> items) {
        switch (strategy) {
            case BOTTOM_LEFT_FILL:
                // 按体积降序，然后按最长边降序
                Collections.sort(items, new Comparator<Item>() {
                    @Override
                    public int compare(Item a, Item b) {
                        int volumeCompare = Double.compare(b.getVolume(), a.getVolume());
                        if (volumeCompare != 0) return volumeCompare;
                        
                        double maxDimA = Math.max(Math.max(a.getWidth(), a.getHeight()), a.getDepth());
                        double maxDimB = Math.max(Math.max(b.getWidth(), b.getHeight()), b.getDepth());
                        return Double.compare(maxDimB, maxDimA);
                    }
                });
                break;
                
            case BEST_FIT:
                // 按表面积与体积比降序（优先放置"扁平"的物品）
                Collections.sort(items, new Comparator<Item>() {
                    @Override
                    public int compare(Item a, Item b) {
                        double ratioA = (a.getWidth() * a.getHeight() + a.getWidth() * a.getDepth() + 
                                       a.getHeight() * a.getDepth()) / a.getVolume();
                        double ratioB = (b.getWidth() * b.getHeight() + b.getWidth() * b.getDepth() + 
                                       b.getHeight() * b.getDepth()) / b.getVolume();
                        return Double.compare(ratioB, ratioA);
                    }
                });
                break;
                
            case FIRST_FIT:
                // 按最长边降序
                Collections.sort(items, new Comparator<Item>() {
                    @Override
                    public int compare(Item a, Item b) {
                        double maxDimA = Math.max(Math.max(a.getWidth(), a.getHeight()), a.getDepth());
                        double maxDimB = Math.max(Math.max(b.getWidth(), b.getHeight()), b.getDepth());
                        return Double.compare(maxDimB, maxDimA);
                    }
                });
                break;
                
            case GREEDY_HEURISTIC:
                // 贪心策略：按体积降序，然后按最小边升序
                Collections.sort(items, new Comparator<Item>() {
                    @Override
                    public int compare(Item a, Item b) {
                        int volumeCompare = Double.compare(b.getVolume(), a.getVolume());
                        if (volumeCompare != 0) return volumeCompare;
                        
                        double minDimA = Math.min(Math.min(a.getWidth(), a.getHeight()), a.getDepth());
                        double minDimB = Math.min(Math.min(b.getWidth(), b.getHeight()), b.getDepth());
                        return Double.compare(minDimA, minDimB);
                    }
                });
                break;
        }
    }
    
    /**
     * 为物品找到合适的位置
     */
    private Point3D findPositionForItem(Item item, Bin container, Octree octree) {
        List<Item> itemsToTry = new ArrayList<>();
        itemsToTry.add(item);
        
        // 如果允许旋转，添加所有旋转变体
        if (allowRotation) {
            itemsToTry.addAll(Arrays.asList(item.getRotations()));
        }
        
        for (Item testItem : itemsToTry) {
            Point3D position = findBestPosition(testItem, container, octree);
            if (position != null) {
                // 更新原始物品的尺寸
                updateItemDimensions(item, testItem);
                return position;
            }
        }
        
        return null;
    }
    
    /**
     * 找到最佳放置位置
     */
    private Point3D findBestPosition(Item item, Bin container, Octree octree) {
        List<Point3D> candidatePoints = generateCandidatePoints(container);
        
        switch (strategy) {
            case BOTTOM_LEFT_FILL:
                return findBottomLeftPosition(item, container, octree, candidatePoints);
            case BEST_FIT:
                return findBestFitPosition(item, container, octree, candidatePoints);
            case FIRST_FIT:
                return findFirstFitPosition(item, container, octree, candidatePoints);
            case GREEDY_HEURISTIC:
                return findGreedyPosition(item, container, octree, candidatePoints);
            default:
                return findBottomLeftPosition(item, container, octree, candidatePoints);
        }
    }
    
    /**
     * 生成候选放置点
     */
    private List<Point3D> generateCandidatePoints(Bin container) {
        List<Point3D> points = new ArrayList<>();
        points.add(new Point3D(0, 0, 0)); // 原点
        
        // 添加现有物品的角点作为候选位置
        for (Item item : container.getItems()) {
            if (item.getPosition() != null) {
                Box3D box = item.getBoundingBox();
                // 添加物品的8个角点
                points.add(new Point3D(box.max.x, box.min.y, box.min.z));
                points.add(new Point3D(box.min.x, box.max.y, box.min.z));
                points.add(new Point3D(box.min.x, box.min.y, box.max.z));
                points.add(new Point3D(box.max.x, box.max.y, box.min.z));
                points.add(new Point3D(box.max.x, box.min.y, box.max.z));
                points.add(new Point3D(box.min.x, box.max.y, box.max.z));
                points.add(new Point3D(box.max.x, box.max.y, box.max.z));
            }
        }
        
        // 如果没有现有物品，添加一些基础候选点
        if (container.getItems().isEmpty()) {
            // 添加容器边界上的点
            points.add(new Point3D(0, 0, 0));
            points.add(new Point3D(container.getWidth() / 2, 0, 0));
            points.add(new Point3D(0, container.getHeight() / 2, 0));
            points.add(new Point3D(0, 0, container.getDepth() / 2));
        }
        
        // 添加一些额外的候选点，确保覆盖更多位置
        if (!container.getItems().isEmpty()) {
            // 添加一些额外的候选点
            points.add(new Point3D(0, 0, 0));
            points.add(new Point3D(container.getWidth() / 2, 0, 0));
            points.add(new Point3D(0, container.getHeight() / 2, 0));
            points.add(new Point3D(0, 0, container.getDepth() / 2));
        }
        
        // 按底部左侧优先排序
        Collections.sort(points, new Comparator<Point3D>() {
            @Override
            public int compare(Point3D a, Point3D b) {
                if (Math.abs(a.z - b.z) > 0.001) return Double.compare(a.z, b.z);
                if (Math.abs(a.y - b.y) > 0.001) return Double.compare(a.y, b.y);
                return Double.compare(a.x, b.x);
            }
        });
        
        return points;
    }
    
    /**
     * 底部左侧填充策略
     */
    private Point3D findBottomLeftPosition(Item item, Bin container, Octree octree, 
                                          List<Point3D> candidatePoints) {
        for (Point3D point : candidatePoints) {
            if (canPlaceAt(item, point, container, octree)) {
                return point;
            }
        }
        return null;
    }
    
    /**
     * 最佳适应策略
     */
    private Point3D findBestFitPosition(Item item, Bin container, Octree octree, 
                                       List<Point3D> candidatePoints) {
        Point3D bestPosition = null;
        double bestWaste = Double.MAX_VALUE;
        
        for (Point3D point : candidatePoints) {
            if (canPlaceAt(item, point, container, octree)) {
                double waste = calculateWaste(item, point, container);
                if (waste < bestWaste) {
                    bestWaste = waste;
                    bestPosition = point;
                }
            }
        }
        
        return bestPosition;
    }
    
    /**
     * 首次适应策略
     */
    private Point3D findFirstFitPosition(Item item, Bin container, Octree octree, 
                                        List<Point3D> candidatePoints) {
        for (Point3D point : candidatePoints) {
            if (canPlaceAt(item, point, container, octree)) {
                return point;
            }
        }
        return null;
    }
    
    /**
     * 贪心策略
     */
    private Point3D findGreedyPosition(Item item, Bin container, Octree octree, 
                                      List<Point3D> candidatePoints) {
        Point3D bestPosition = null;
        double bestScore = Double.MAX_VALUE;
        
        for (Point3D point : candidatePoints) {
            if (canPlaceAt(item, point, container, octree)) {
                double score = calculateGreedyScore(item, point, container);
                if (score < bestScore) {
                    bestScore = score;
                    bestPosition = point;
                }
            }
        }
        
        return bestPosition;
    }
    
    /**
     * 检查是否可以在指定位置放置物品
     */
    private boolean canPlaceAt(Item item, Point3D position, Bin container, Octree octree) {
        // 检查是否在容器范围内
        if (!item.canFitAt(position, container.getBoundingBox())) {
            return false;
        }
        
        // 使用八叉树或传统方法检查碰撞
        if (octree != null) {
            Box3D itemBox = new Box3D(position.x, position.y, position.z,
                item.getWidth(), item.getHeight(), item.getDepth());
            return !octree.hasCollision(itemBox);
        } else {
            return !container.hasCollision(item, position);
        }
    }
    
    /**
     * 计算放置浪费的空间
     */
    private double calculateWaste(Item item, Point3D position, Bin container) {
        // 计算到容器角落的距离作为浪费指标
        return position.x + position.y + position.z;
    }
    
    /**
     * 计算贪心评分
     */
    private double calculateGreedyScore(Item item, Point3D position, Bin container) {
        // 综合考虑位置和物品特性
        double distanceToOrigin = position.x + position.y + position.z;
        double aspectRatio = Math.max(Math.max(item.getWidth(), item.getHeight()), item.getDepth()) /
                           Math.min(Math.min(item.getWidth(), item.getHeight()), item.getDepth());
        
        return distanceToOrigin + aspectRatio * 0.1;
    }
    
    /**
     * 更新物品尺寸（用于旋转）
     */
    private void updateItemDimensions(Item original, Item rotated) {
        original.setDimensions(rotated.getWidth(), rotated.getHeight(), rotated.getDepth());
    }
    
    /**
     * 装箱结果类
     */
    public static class PackingResult {
        public final boolean success;
        public final int packedCount;
        public final int totalCount;
        public final double containerVolume;
        public final double usedVolume;
        public final double utilization;
        public final String message;
        
        public PackingResult(boolean success, int packedCount, int totalCount,
                           double containerVolume, double usedVolume, double utilization, String message) {
            this.success = success;
            this.packedCount = packedCount;
            this.totalCount = totalCount;
            this.containerVolume = containerVolume;
            this.usedVolume = usedVolume;
            this.utilization = utilization;
            this.message = message;
        }
        
        @Override
        public String toString() {
            return String.format(
                "PackingResult(success=%s, packed=%d/%d, volume=%.2f/%.2f, utilization=%.2f%%, message='%s')",
                success, packedCount, totalCount, usedVolume, containerVolume, utilization * 100, message
            );
        }
    }
}