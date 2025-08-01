package com.binpacking;

/**
 * 表示三维空间中的轴对齐包围盒
 */
public class Box3D {
    public final Point3D min;
    public final Point3D max;
    
    public Box3D(Point3D min, Point3D max) {
        this.min = min;
        this.max = max;
    }
    
    public Box3D(double x, double y, double z, double width, double height, double depth) {
        this.min = new Point3D(x, y, z);
        this.max = new Point3D(x + width, y + height, z + depth);
    }
    
    public double getWidth() {
        return max.x - min.x;
    }
    
    public double getHeight() {
        return max.y - min.y;
    }
    
    public double getDepth() {
        return max.z - min.z;
    }
    
    public double getVolume() {
        return getWidth() * getHeight() * getDepth();
    }
    
    public Point3D getCenter() {
        return new Point3D(
            (min.x + max.x) / 2,
            (min.y + max.y) / 2,
            (min.z + max.z) / 2
        );
    }
    
    /**
     * 检查是否包含指定点
     */
    public boolean contains(Point3D point) {
        return point.x >= min.x && point.x <= max.x &&
               point.y >= min.y && point.y <= max.y &&
               point.z >= min.z && point.z <= max.z;
    }
    
    /**
     * 检查是否与另一个盒子相交
     */
    public boolean intersects(Box3D other) {
        return !(max.x < other.min.x || min.x > other.max.x ||
                 max.y < other.min.y || min.y > other.max.y ||
                 max.z < other.min.z || min.z > other.max.z);
    }
    
    /**
     * 检查是否完全包含另一个盒子
     */
    public boolean contains(Box3D other) {
        return min.x <= other.min.x && max.x >= other.max.x &&
               min.y <= other.min.y && max.y >= other.max.y &&
               min.z <= other.min.z && max.z >= other.max.z;
    }
    
    /**
     * 获取与另一个盒子的交集
     */
    public Box3D intersection(Box3D other) {
        if (!intersects(other)) {
            return null;
        }
        
        Point3D newMin = new Point3D(
            Math.max(min.x, other.min.x),
            Math.max(min.y, other.min.y),
            Math.max(min.z, other.min.z)
        );
        
        Point3D newMax = new Point3D(
            Math.min(max.x, other.max.x),
            Math.min(max.y, other.max.y),
            Math.min(max.z, other.max.z)
        );
        
        return new Box3D(newMin, newMax);
    }
    
    @Override
    public String toString() {
        return String.format("Box3D(min=%s, max=%s, size=%.2fx%.2fx%.2f)", 
            min, max, getWidth(), getHeight(), getDepth());
    }
}