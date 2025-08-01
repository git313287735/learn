package com.binpacking;

import java.util.*;

/**
 * 3D装箱算法实现，使用八叉树优化空间查询
 */
public class BinPacking3D {
    
    /**
     * 放置策略枚举
     */
    public enum PlacementStrategy {
        BOTTOM_LEFT_FILL,    // 底部左侧填充
        BEST_FIT,           // 最佳适应
        FIRST_FIT,          // 首次适应
        NEXT_FIT            // 下次适应
    }
    
    private final PlacementStrategy strategy;
    private final boolean allowRotation;
    
    public BinPacking3D(PlacementStrategy strategy, boolean allowRotation) {
        this.strategy = strategy;
        this.allowRotation = allowRotation;
    }
    
    public BinPacking3D() {
        this(PlacementStrategy.BOTTOM_LEFT_FILL, true);
    }
    
    /**
     * 执行装箱算法
     */
    public List<Bin> pack(List<Item> items, Bin binTemplate) {
        List<Bin> bins = new ArrayList<>();
        List<Item> remainingItems = new ArrayList<>(items);
        
        // 按体积降序排序物品
        remainingItems.sort((a, b) -> Double.compare(b.getVolume(), a.getVolume()));
        
        while (!remainingItems.isEmpty()) {
            Bin currentBin = new Bin(
                "Bin_" + bins.size(),
                binTemplate.getWidth(),
                binTemplate.getHeight(),
                binTemplate.getDepth()
            );
            
            // 为当前容器创建八叉树
            Octree octree = new Octree(currentBin.getBoundingBox());
            
            packIntoBin(currentBin, remainingItems, octree);
            bins.add(currentBin);
        }
        
        return bins;
    }
    
    /**
     * 将物品装入指定容器
     */
    private void packIntoBin(Bin bin, List<Item> items, Octree octree) {
        Iterator<Item> iterator = items.iterator();
        
        while (iterator.hasNext()) {
            Item item = iterator.next();
            Point3D bestPosition = findBestPosition(item, bin, octree);
            
            if (bestPosition != null) {
                item.setPosition(bestPosition);
                bin.addItem(item);
                octree.insert(item);
                iterator.remove();
            }
        }
    }
    
    /**
     * 为物品找到最佳放置位置
     */
    private Point3D findBestPosition(Item item, Bin bin, Octree octree) {
        List<Item> itemsToTry = new ArrayList<>();
        itemsToTry.add(item);
        
        // 如果允许旋转，添加所有旋转变体
        if (allowRotation) {
            itemsToTry.addAll(Arrays.asList(item.getRotations()));
        }
        
        for (Item testItem : itemsToTry) {
            Point3D position = findPositionForItem(testItem, bin, octree);
            if (position != null) {
                // 更新原始物品的尺寸和位置
                updateItemDimensions(item, testItem);
                return position;
            }
        }
        
        return null;
    }
    
    /**
     * 为指定物品找到合适的位置
     */
    private Point3D findPositionForItem(Item item, Bin bin, Octree octree) {
        List<Point3D> candidatePoints = generateCandidatePoints(bin);
        
        switch (strategy) {
            case BOTTOM_LEFT_FILL:
                return findBottomLeftPosition(item, bin, octree, candidatePoints);
            case BEST_FIT:
                return findBestFitPosition(item, bin, octree, candidatePoints);
            case FIRST_FIT:
                return findFirstFitPosition(item, bin, octree, candidatePoints);
            case NEXT_FIT:
                return findNextFitPosition(item, bin, octree, candidatePoints);
            default:
                return findBottomLeftPosition(item, bin, octree, candidatePoints);
        }
    }
    
    /**
     * 生成候选放置点
     */
    private List<Point3D> generateCandidatePoints(Bin bin) {
        List<Point3D> points = bin.getPossiblePlacementPoints();
        
        // 按照底部左侧优先的顺序排序
        points.sort((a, b) -> {
            if (Math.abs(a.z - b.z) > 0.001) return Double.compare(a.z, b.z);
            if (Math.abs(a.y - b.y) > 0.001) return Double.compare(a.y, b.y);
            return Double.compare(a.x, b.x);
        });
        
        return points;
    }
    
    /**
     * 底部左侧填充策略
     */
    private Point3D findBottomLeftPosition(Item item, Bin bin, Octree octree, 
                                          List<Point3D> candidatePoints) {
        for (Point3D point : candidatePoints) {
            if (canPlaceAt(item, point, bin, octree)) {
                return point;
            }
        }
        return null;
    }
    
    /**
     * 最佳适应策略
     */
    private Point3D findBestFitPosition(Item item, Bin bin, Octree octree, 
                                       List<Point3D> candidatePoints) {
        Point3D bestPosition = null;
        double bestWaste = Double.MAX_VALUE;
        
        for (Point3D point : candidatePoints) {
            if (canPlaceAt(item, point, bin, octree)) {
                double waste = calculateWaste(item, point, bin);
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
    private Point3D findFirstFitPosition(Item item, Bin bin, Octree octree, 
                                        List<Point3D> candidatePoints) {
        for (Point3D point : candidatePoints) {
            if (canPlaceAt(item, point, bin, octree)) {
                return point;
            }
        }
        return null;
    }
    
    /**
     * 下次适应策略
     */
    private Point3D findNextFitPosition(Item item, Bin bin, Octree octree, 
                                       List<Point3D> candidatePoints) {
        // 简化实现，与首次适应相同
        return findFirstFitPosition(item, bin, octree, candidatePoints);
    }
    
    /**
     * 检查是否可以在指定位置放置物品
     */
    private boolean canPlaceAt(Item item, Point3D position, Bin bin, Octree octree) {
        // 检查是否在容器范围内
        if (!item.canFitAt(position, bin.getBoundingBox())) {
            return false;
        }
        
        // 使用八叉树检查碰撞
        Box3D itemBox = new Box3D(position.x, position.y, position.z,
            item.getWidth(), item.getHeight(), item.getDepth());
        
        return !octree.hasCollision(itemBox);
    }
    
    /**
     * 计算放置浪费的空间
     */
    private double calculateWaste(Item item, Point3D position, Bin bin) {
        // 简单的浪费计算：距离容器角落的距离
        return position.x + position.y + position.z;
    }
    
    /**
     * 更新物品尺寸（用于旋转）
     */
    private void updateItemDimensions(Item original, Item rotated) {
        original.setDimensions(rotated.getWidth(), rotated.getHeight(), rotated.getDepth());
    }
    
    /**
     * 获取装箱结果统计
     */
    public PackingResult getPackingResult(List<Bin> bins, List<Item> originalItems) {
        int totalItems = originalItems.size();
        int packedItems = 0;
        double totalVolume = 0;
        double usedVolume = 0;
        
        for (Bin bin : bins) {
            packedItems += bin.getItems().size();
            totalVolume += bin.getVolume();
            usedVolume += bin.getUsedVolume();
        }
        
        return new PackingResult(
            bins.size(),
            totalItems,
            packedItems,
            totalVolume,
            usedVolume,
            usedVolume / totalVolume
        );
    }
    
    /**
     * 装箱结果类
     */
    public static class PackingResult {
        public final int binCount;
        public final int totalItems;
        public final int packedItems;
        public final double totalVolume;
        public final double usedVolume;
        public final double utilization;
        
        public PackingResult(int binCount, int totalItems, int packedItems,
                           double totalVolume, double usedVolume, double utilization) {
            this.binCount = binCount;
            this.totalItems = totalItems;
            this.packedItems = packedItems;
            this.totalVolume = totalVolume;
            this.usedVolume = usedVolume;
            this.utilization = utilization;
        }
        
        @Override
        public String toString() {
            return String.format(
                "PackingResult(bins=%d, items=%d/%d, volume=%.2f/%.2f, utilization=%.2f%%)",
                binCount, packedItems, totalItems, usedVolume, totalVolume, utilization * 100
            );
        }
    }
}