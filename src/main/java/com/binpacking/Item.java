package com.binpacking;

/**
 * 表示需要装箱的物品
 */
public class Item {
    private final String id;
    private final double width;
    private final double height;
    private final double depth;
    private final double weight;
    private Point3D position;
    private boolean packed;
    
    public Item(String id, double width, double height, double depth, double weight) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.weight = weight;
        this.position = null;
        this.packed = false;
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
    
    public double getWeight() {
        return weight;
    }
    
    public double getVolume() {
        return width * height * depth;
    }
    
    public Point3D getPosition() {
        return position;
    }
    
    public void setPosition(Point3D position) {
        this.position = position;
    }
    
    public boolean isPacked() {
        return packed;
    }
    
    public void setPacked(boolean packed) {
        this.packed = packed;
    }
    
    /**
     * 获取物品的包围盒
     */
    public Box3D getBoundingBox() {
        if (position == null) {
            return new Box3D(0, 0, 0, width, height, depth);
        }
        return new Box3D(position.x, position.y, position.z, width, height, depth);
    }
    
    /**
     * 检查是否可以放置在指定位置（不考虑碰撞）
     */
    public boolean canFitAt(Point3D pos, Box3D container) {
        Box3D itemBox = new Box3D(pos.x, pos.y, pos.z, width, height, depth);
        return container.contains(itemBox);
    }
    
    /**
     * 获取物品的所有可能旋转
     */
    public Item[] getRotations() {
        return new Item[] {
            new Item(id + "_rot0", width, height, depth, weight),
            new Item(id + "_rot1", width, depth, height, weight),
            new Item(id + "_rot2", height, width, depth, weight),
            new Item(id + "_rot3", height, depth, width, weight),
            new Item(id + "_rot4", depth, width, height, weight),
            new Item(id + "_rot5", depth, height, width, weight)
        };
    }
    
    @Override
    public String toString() {
        return String.format("Item(id=%s, size=%.2fx%.2fx%.2f, weight=%.2f, pos=%s, packed=%s)", 
            id, width, height, depth, weight, position, packed);
    }
}