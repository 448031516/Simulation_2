
/**
 * 二维平面，坐标点
 */

public class Point2 {
    private double x;
    private double y;

    public Point2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    //读文件初始化时需要用到
    public Point2(String x, String y){
        this.x = Double.parseDouble(x);
        this.y = Double.parseDouble(y);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
