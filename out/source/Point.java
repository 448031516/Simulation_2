

import java.text.DecimalFormat;

public class Point {

    float x;
    float y;
    int num;
    public Point(float lx, float ly) {
        x = lx;
        y = ly;
    }
    public Point(int n, float lx, float ly) {
        // TODO Auto-generated constructor stub
        x = lx;
        y = ly;
        num = n;
    }
    public Point(){
        x=0;
        y=0;
        num=0;
    }
    //求平面两点之间的距离
    public static float getDistance(Point p1, Point p2) {
        float distance = 0.0f;
        //X轴的距离平方
        double dx = Math.pow(p1.x-p2.x, 2);
        //Y轴的距离平方
        double dy = Math.pow(p1.y-p2.y, 2);
        distance = (float) (Math.sqrt(dx+dy));
        return distance;
    }


    //计算一组坐标点的平均值
    public static Point getAverage(Point...points) {
        //点的个数
        int n = points.length;
        if(n == 1) return points[0];
        //坐标点的平均值
        float x = 0;
        float y = 0;
        //遍历边界点求边界点坐标的算术平均数
        for (Point p : points) {
            x += p.x;
            y += p.y;
        }

        x = x/n;
        y = y/n;

        //新建格式化器,保留两位小数
        DecimalFormat Dformat = new DecimalFormat("0.000");
        x = Float.parseFloat(Dformat.format(x));
        y = Float.parseFloat(Dformat.format(y));

        return new Point(x,y);
    }


    //计算一组数的平方和
    public static double getSquareSum(double...ds) {
        double sum = 0;
        for (double d : ds) {
            //System.out.println(d);
            sum += Math.pow(d, 2);
        }
        return sum;
    }

    public static void test(Sensor...sensors) {
        System.out.println(sensors.length);
    }

    public static void main(String[] args) {
        System.out.println(getDistance(new Point(10,7),new Point(13, 11)));

        test();

        Point p1 = new Point(10,7);
        Point p2 = new Point(10,7);
        System.out.println(p1.equals(p2));

        int[] ptest = new int[6];
        for (int p : ptest) {
            System.out.println(p);
        }
    }
}
