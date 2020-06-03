import java.util.LinkedList;

public class grid {
    public Point a,b; //a,b分别为网格左下角和右上角
    public LinkedList<Sensor> include_Sensor;//所包含的传感器节点

    public grid() {
    }

    public grid(Point a, Point b) {
        this.a = a;
        this.b = b;
        include_Sensor = new LinkedList<Sensor>();
    }

    public void setInclude_Sensor(Sensor[] allSensor) {
        for (Sensor sensor:allSensor) {
            if (sensor.location.x>a.x && sensor.location.y<a.y && sensor.location.x<b.x && sensor.location.y>b.y) {
                this.include_Sensor.add(sensor);
            }
        }
    }


}
