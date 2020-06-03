
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;


public class run3 {
    public static void main(String[] args) {
        //网络规模
        float networkSize = 150;
        //传感器节点个数
        int nodenum = 100;
        //网格初始大小
        int girdSize = 8;
        //系统当前时间初始为0s
        int systemTime = 0;
        //能量消耗率最小值
        float minECR = 0.01f;
        //能量消耗率最大值
        float maxECR = 0.06f;
        //多跳阈值
        float THR_erRateEFF = 0.1f;

        double V = Math.PI/2 ; //充电器角度
        double R = 20;//充电器半径
        float Cp = 0.05f;
        int cluterNum = 25;//子区域个数

        Sensor[] allSensor = new Sensor[nodenum];
        allSensor = WsnFunction.initSensors(networkSize, nodenum, minECR, maxECR);
        LinkedList<Sensor>[] cluster = new LinkedList[cluterNum];

/*

        //网格划分,记录各个网格包含的节点
        int grid_edge= (int) Math.floor(networkSize/girdSize);
        grid[][] Grid = new grid[grid_edge][grid_edge];
        int xx=0,yy=0;
//        int sum=0;
        for (int y=girdSize;y<=networkSize;y+=girdSize){
            for (int x=girdSize;x<=networkSize;x+=girdSize,xx++){
                Grid[yy][xx] = new grid(new Point(x-girdSize,y), new Point(x,y-girdSize));
                Grid[yy][xx].setInclude_Sensor(allSensor);
*/
/*
                sum+=Grid[yy][xx].include_Sensor.size();   //用于测试是否网格包含全部传感器节点
                System.out.println(sum);
    *//*

                System.out.println(Grid[yy][xx].include_Sensor.size());
            }
            xx=0;
            yy++;
        }
*/
        String path = "C:\\Users\\Administrator\\IdeaProjects\\Simulation_2\\out1.txt";
        KMeans kMeans = new KMeans(path, cluterNum);//参数cluterNum为子区域数量
        kMeans.doKMeans();
//        System.out.println(kMeans.clusters.get(24).get(1).getX());

        for (int i=0; i<kMeans.centerPoints.size(); i++) {
            System.out.print(MessageFormat.format("子区域{0}的中心点: ({1}, {2})", (i + 1), kMeans.centerPoints.get(i).getX(), kMeans.centerPoints.get(i).getY()));
            List<Point2> lists = kMeans.clusters.get(i);
            cluster[i] = new LinkedList<Sensor>();
            System.out.print("其中包含的传感器节点为：");
            for (int j = 0; j < lists.size(); j++) {
                Sensor s = new Sensor();
                s.location.x = (float) lists.get(j).getX();
                s.location.y = (float) lists.get(j).getY();
                cluster[i].add(s);
                System.out.print(s.location);
            }
            System.out.println();
        }


    }

}
