package com.binpacking;

import java.util.ArrayList;
import java.util.List;

/**
 * 八叉树实现，用于优化3D空间查询和碰撞检测
 */
public class Octree {
    private static final int MAX_OBJECTS_PER_NODE = 10;
    private static final int MAX_DEPTH = 5;
    
    private final Box3D bounds;
    private final int depth;
    private final List<Item> objects;
    private Octree[] children;
    private boolean divided;
    
    public Octree(Box3D bounds) {
        this(bounds, 0);
    }
    
    private Octree(Box3D bounds, int depth) {
        this.bounds = bounds;
        this.depth = depth;
        this.objects = new ArrayList<>();
        this.children = null;
        this.divided = false;
    }
    
    /**
     * 插入物品到八叉树
     */
    public boolean insert(Item item) {
        Box3D itemBox = item.getBoundingBox();
        
        // 检查物品是否在当前节点范围内
        if (!bounds.intersects(itemBox)) {
            return false;
        }
        
        // 如果当前节点未分割且物品数量未超过阈值，直接添加
        if (!divided && objects.size() < MAX_OBJECTS_PER_NODE) {
            objects.add(item);
            return true;
        }
        
        // 如果未分割且达到最大深度，强制添加到当前节点
        if (!divided && depth >= MAX_DEPTH) {
            objects.add(item);
            return true;
        }
        
        // 分割节点
        if (!divided) {
            subdivide();
        }
        
        // 尝试插入到子节点
        boolean inserted = false;
        for (Octree child : children) {
            if (child.insert(item)) {
                inserted = true;
            }
        }
        
        // 如果无法插入到任何子节点，添加到当前节点
        if (!inserted) {
            objects.add(item);
        }
        
        return true;
    }
    
    /**
     * 分割当前节点为8个子节点
     */
    private void subdivide() {
        if (divided) return;
        
        Point3D center = bounds.getCenter();
        double halfWidth = bounds.getWidth() / 2;
        double halfHeight = bounds.getHeight() / 2;
        double halfDepth = bounds.getDepth() / 2;
        
        children = new Octree[8];
        
        // 创建8个子节点
        children[0] = new Octree(new Box3D(bounds.min.x, bounds.min.y, bounds.min.z, 
            halfWidth, halfHeight, halfDepth), depth + 1);
        children[1] = new Octree(new Box3D(center.x, bounds.min.y, bounds.min.z, 
            halfWidth, halfHeight, halfDepth), depth + 1);
        children[2] = new Octree(new Box3D(bounds.min.x, center.y, bounds.min.z, 
            halfWidth, halfHeight, halfDepth), depth + 1);
        children[3] = new Octree(new Box3D(center.x, center.y, bounds.min.z, 
            halfWidth, halfHeight, halfDepth), depth + 1);
        children[4] = new Octree(new Box3D(bounds.min.x, bounds.min.y, center.z, 
            halfWidth, halfHeight, halfDepth), depth + 1);
        children[5] = new Octree(new Box3D(center.x, bounds.min.y, center.z, 
            halfWidth, halfHeight, halfDepth), depth + 1);
        children[6] = new Octree(new Box3D(bounds.min.x, center.y, center.z, 
            halfWidth, halfHeight, halfDepth), depth + 1);
        children[7] = new Octree(new Box3D(center.x, center.y, center.z, 
            halfWidth, halfHeight, halfDepth), depth + 1);
        
        divided = true;
        
        // 重新分配现有物品到子节点
        List<Item> currentObjects = new ArrayList<>(objects);
        objects.clear();
        
        for (Item item : currentObjects) {
            insert(item);
        }
    }
    
    /**
     * 查询与指定区域相交的所有物品
     */
    public List<Item> query(Box3D range) {
        List<Item> result = new ArrayList<>();
        query(range, result);
        return result;
    }
    
    private void query(Box3D range, List<Item> result) {
        // 检查查询范围是否与当前节点相交
        if (!bounds.intersects(range)) {
            return;
        }
        
        // 添加当前节点中与查询范围相交的物品
        for (Item item : objects) {
            if (item.getBoundingBox().intersects(range)) {
                result.add(item);
            }
        }
        
        // 递归查询子节点
        if (divided) {
            for (Octree child : children) {
                child.query(range, result);
            }
        }
    }
    
    /**
     * 检查指定位置是否与任何物品发生碰撞
     */
    public boolean hasCollision(Box3D testBox) {
        List<Item> candidates = query(testBox);
        
        for (Item item : candidates) {
            if (item.getBoundingBox().intersects(testBox)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 移除物品
     */
    public boolean remove(Item item) {
        if (objects.remove(item)) {
            return true;
        }
        
        if (divided) {
            for (Octree child : children) {
                if (child.remove(item)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 清空八叉树
     */
    public void clear() {
        objects.clear();
        if (divided) {
            for (Octree child : children) {
                child.clear();
            }
            children = null;
            divided = false;
        }
    }
    
    /**
     * 获取八叉树中的总物品数量
     */
    public int size() {
        int count = objects.size();
        
        if (divided) {
            for (Octree child : children) {
                count += child.size();
            }
        }
        
        return count;
    }
    
    /**
     * 获取八叉树的深度
     */
    public int getMaxDepth() {
        if (!divided) {
            return depth;
        }
        
        int maxChildDepth = depth;
        for (Octree child : children) {
            maxChildDepth = Math.max(maxChildDepth, child.getMaxDepth());
        }
        
        return maxChildDepth;
    }
    
    /**
     * 获取八叉树统计信息
     */
    public OctreeStats getStats() {
        return new OctreeStats(size(), getMaxDepth(), countNodes(), countLeafNodes());
    }
    
    private int countNodes() {
        int count = 1;
        if (divided) {
            for (Octree child : children) {
                count += child.countNodes();
            }
        }
        return count;
    }
    
    private int countLeafNodes() {
        if (!divided) {
            return 1;
        }
        
        int count = 0;
        for (Octree child : children) {
            count += child.countLeafNodes();
        }
        return count;
    }
    
    /**
     * 八叉树统计信息类
     */
    public static class OctreeStats {
        public final int totalObjects;
        public final int maxDepth;
        public final int totalNodes;
        public final int leafNodes;
        
        public OctreeStats(int totalObjects, int maxDepth, int totalNodes, int leafNodes) {
            this.totalObjects = totalObjects;
            this.maxDepth = maxDepth;
            this.totalNodes = totalNodes;
            this.leafNodes = leafNodes;
        }
        
        @Override
        public String toString() {
            return String.format("OctreeStats(objects=%d, maxDepth=%d, totalNodes=%d, leafNodes=%d)", 
                totalObjects, maxDepth, totalNodes, leafNodes);
        }
    }
}