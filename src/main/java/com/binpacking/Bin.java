package com.binpacking;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示装箱容器
 */
public class Bin {
    private final String id;
    private final double width;
    private final double height;
    private final double depth;
    private final double maxWeight;
    private final List<Item> items;
    private double currentWeight;
    
    public Bin(String id, double width, double height, double depth, double maxWeight) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.maxWeight = maxWeight;
        this.items = new ArrayList<>();
        this.currentWeight = 0.0;
    }
    
    public String getId() {
        return id;
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }
    
    public double getDepth() {
        return depth;
    }
    
    public double getMaxWeight() {
        return maxWeight;
    }
    
    public double getCurrentWeight() {
        return currentWeight;
    }
    
    public double getVolume() {
        return width * height * depth;
    }
    
    public double getUsedVolume() {
        return items.stream().mapToDouble(Item::getVolume).sum();
    }
    
    public double getVolumeUtilization() {
        return getUsedVolume() / getVolume();
    }
    
    public List<Item> getItems() {
        return new ArrayList<>(items);
    }
    
    public Box3D getBoundingBox() {
        return new Box3D(0, 0, 0, width, height, depth);
    }
    
    /**
     * 检查是否可以添加物品
     */
    public boolean canAddItem(Item item) {
        return currentWeight + item.getWeight() <= maxWeight;
    }
    
    /**
     * 添加物品到容器
     */
    public boolean addItem(Item item) {
        if (!canAddItem(item)) {
            return false;
        }
        
        items.add(item);
        currentWeight += item.getWeight();
        item.setPacked(true);
        return true;
    }
    
    /**
     * 移除物品
     */
    public boolean removeItem(Item item) {
        if (items.remove(item)) {
            currentWeight -= item.getWeight();
            item.setPacked(false);
            item.setPosition(null);
            return true;
        }
        return false;
    }
    
    /**
     * 检查指定位置是否有碰撞
     */
    public boolean hasCollision(Item newItem, Point3D position) {
        Box3D newItemBox = new Box3D(position.x, position.y, position.z, 
            newItem.getWidth(), newItem.getHeight(), newItem.getDepth());
        
        for (Item existingItem : items) {
            if (existingItem.getPosition() != null) {
                Box3D existingBox = existingItem.getBoundingBox();
                if (newItemBox.intersects(existingBox)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 获取所有可能的放置点（基于现有物品的角点）
     */
    public List<Point3D> getPossiblePlacementPoints() {
        List<Point3D> points = new ArrayList<>();
        points.add(new Point3D(0, 0, 0)); // 原点
        
        for (Item item : items) {
            if (item.getPosition() != null) {
                Box3D box = item.getBoundingBox();
                // 添加物品的8个角点作为候选位置
                points.add(new Point3D(box.max.x, box.min.y, box.min.z));
                points.add(new Point3D(box.min.x, box.max.y, box.min.z));
                points.add(new Point3D(box.min.x, box.min.y, box.max.z));
                points.add(new Point3D(box.max.x, box.max.y, box.min.z));
                points.add(new Point3D(box.max.x, box.min.y, box.max.z));
                points.add(new Point3D(box.min.x, box.max.y, box.max.z));
                points.add(new Point3D(box.max.x, box.max.y, box.max.z));
            }
        }
        
        return points;
    }
    
    @Override
    public String toString() {
        return String.format("Bin(id=%s, size=%.2fx%.2fx%.2f, items=%d, weight=%.2f/%.2f, utilization=%.2f%%)", 
            id, width, height, depth, items.size(), currentWeight, maxWeight, getVolumeUtilization() * 100);
    }
}