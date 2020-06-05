
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;


public class run3 {
    public static void main(String[] args) throws FileNotFoundException {
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
        LinkedList<Sensor>[] cluster = new LinkedList[cluterNum+1];
        circle[] cluster_circle = new circle[cluterNum+1];//簇圆  辅助用
        LinkedList<Point>[] maodian_serial = new LinkedList[100]; //将Centroid和cluster_circle[i].center的连线等分为若干个点，辅助用
        Point[] maodian = new Point[cluterNum+1]; //充电器摆放位置
        double[] maodian_V = new double[100]; //充电器摆放角度
        LinkedList<Point>[] cluster_point = new LinkedList[cluterNum+1];		//簇，但将簇内Sensor转换成Point
        LinkedList<Point>[] cluster_edge = new LinkedList[cluterNum+1] ;
        int cluster_Point_NUM ;

        cluster_Point_NUM=0;
        cluster_point[cluster_Point_NUM] = new LinkedList<Point>();

        PrintStream out = new PrintStream("out2.txt");
        PrintStream old = System.out;
//
        // 设置系统的打印流流向,输出到ps.txt
        System.setOut(out);
        // 调用系统的打印流,ps.txt中输出97
        for (int i=0;i < allSensor.length;i++){
            System.out.println(allSensor[i].location.x+" "+allSensor[i].location.y);
        }
        System.setOut(old);
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
        //子区域划分
        String path = "C:\\Users\\Administrator\\IdeaProjects\\Simulation_2\\out2.txt";
        KMeans kMeans = new KMeans(path, cluterNum);//参数cluterNum为子区域数量
        kMeans.doKMeans();
//        System.out.println(kMeans.clusters.get(24).get(1).getX());
        LinkedList<Point> Centroid = new LinkedList<Point>();   //将子区域中心的kMeans.centerPoints的类型从Point2转换为Point

        for (int i=0; i<kMeans.centerPoints.size(); i++) {
            System.out.print(MessageFormat.format("子区域{0}的中心点: ({1}, {2})", (i + 1), kMeans.centerPoints.get(i).getX(), kMeans.centerPoints.get(i).getY()));
            Point p = new Point(kMeans.centerPoints.get(i).getX(),kMeans.centerPoints.get(i).getY());   //将子区域中心的kMeans.centerPoints的类型从Point2转换为Point
            Centroid.add(p);                                                                            //将子区域中心的kMeans.centerPoints的类型从Point2转换为Point
            List<Point2> lists = kMeans.clusters.get(i);
            cluster[i] = new LinkedList<Sensor>();
            System.out.print("其中包含的传感器节点为：");

            for (int j = 0; j < lists.size(); j++) {
                Sensor s = new Sensor();
                s.location.x = (float) lists.get(j).getX();
                s.location.y = (float) lists.get(j).getY();
                s.cluster = i;
                s.charger =new direct_Charger(-1,new Point((float) kMeans.centerPoints.get(i).getX(),(float) kMeans.centerPoints.get(i).getY()));
//                s.charger.location.x = (float) kMeans.centerPoints.get(i).getX();
//                s.charger.location.y = (float) kMeans.centerPoints.get(i).getY();
                cluster[i].add(s);
                System.out.print(s.location);
            }
            System.out.println();
        }

        for (int i=0;cluster[i]!=null;i++){
            cluster_point[i] = new LinkedList<Point>();
            for (int j = 0;j < cluster[i].size();j++){
                cluster_point[i].add(cluster[i].get(j).location);
            }
            cluster_edge[i] = MinimumBoundingPolygon.findSmallestPolygon(cluster_point[i]);
            cluster_circle[i] = WsnFunction.find_cirle(cluster_edge[i]);
            System.out.println(cluster_circle[i].center);
        }

        //充电器部署点选取
        for (int i=0;cluster[i]!=null;i++){
            if (cluster[i].size()>2){
                Point a = new Point();
                Point b = new Point();
                a = WsnFunction.getPoint(cluster_circle[i],cluster_circle[i].center,Centroid.get(i));
                double k = Math.atan2((Centroid.get(i).y-a.y),(Centroid.get(i).x-a.x));
                b.x = (float)(a.x - cluster_circle[i].r*Math.cos(k));
                b.y = (float)(a.y - cluster_circle[i].r*Math.sin(k));
                Point c = new Point((float)(a.x + cluster_circle[i].r*Math.cos(k)),(float)(a.y + cluster_circle[i].r*Math.sin(k)));
                maodian_serial[i] = WsnFunction.divided_line(c,b,200);
            }else if (cluster[i].size()==2){
                Point a = new Point((cluster[i].get(0).location.x+cluster[i].get(1).location.x)/2,(cluster[i].get(0).location.y+cluster[i].get(1).location.y)/2);
                double k = Math.PI/2 + Math.atan2((cluster[i].get(1).location.y-cluster[i].get(0).location.y),(cluster[i].get(1).location.x-cluster[i].get(0).location.x));
                Point b = new Point();
                b.x = (float)(a.x - cluster_circle[i].r*Math.cos(k));
                b.y = (float)(a.y - cluster_circle[i].r*Math.sin(k));
                Point c = new Point((float)(a.x + cluster_circle[i].r*Math.cos(k)),(float)(a.y + cluster_circle[i].r*Math.sin(k)));
                maodian_serial[i] = WsnFunction.divided_line(c,b,200);
            }else {
                maodian[i] = cluster[i].get(0).location;
            }

        }


        for (int i=0;cluster[i]!=null;i++){

            if (cluster[i].size() < 2){
                cluster[i].get(0).erRateEFF=1;
                cluster[i].get(0).isClover = true;
                continue;
            }
            double EFF = 0,max = 0;
            //确定充电器摆放位置
            for (int maodian_ = 0;maodian_< maodian_serial[i].size();maodian_++) {
                for (double Charger_angle = 0;Charger_angle <2*Math.PI;Charger_angle+=Math.PI/360) {
                    for (int j = 0; j < cluster[i].size(); j++) {
                        cluster[i].get(j).isClover = WsnFunction2.IFclovered(maodian_serial[i].get(maodian_),cluster[i].get(j), Charger_angle, V, R);
                    }
                    for (Sensor S : cluster[i]) {
                        if (S.isClover) {
                            S.erRate = (float) (100 / Math.pow(40 + Point.getDistance(S.location, maodian_serial[i].get(maodian_)), 2));
                            S.erRateEFF = Math.min(S.erRate * (1 / Cp), 1);
                            S.multihop = -2;
                        }
                    }
                    int Clover_num = 0;
                    for (Sensor S : cluster[i]) {
                        if (S.isClover) Clover_num++;
                    }
                    if (Clover_num > 0) {
//                        while (WsnFunction.IF_noPATH(cluster[i])) {
//                            cluster[i] = WsnFunction.multihop_PATH(cluster[i], Cp);
//                            //从得到的所有未被覆盖节点中选取erRateEFF最大的节点及其路径（下一跳）
//                            double maxERrate = 0;
//                            int sensor_maxERrate = -1;
//                            for (int f1 = 0; f1 < cluster[i].size(); f1++) {
//                                if (!cluster[i].get(f1).isClover && cluster[i].get(f1).erRateEFF > maxERrate) {
//                                    maxERrate = cluster[i].get(f1).erRateEFF;
//                                    sensor_maxERrate = f1;
//                                }
//                            }
//                            if (sensor_maxERrate >= 0) {
//                                cluster[i].get(sensor_maxERrate).isClover = true;//将最大erRateEFF的节点加入到已覆盖的集合中
//                            }
//                        }
                    } else {
                        for (Sensor S : cluster[i]) {
                            S.erRateEFF = 0;
                            S.erRate = 0;
                            S.isClover = false;
                        }
                        continue;
                    }
                    ;
                    double SumEREFF = 0;
                    for (Sensor S : cluster[i]) {
                        //                     System.out.print(S.erRate + "+");
                        //                     System.out.println("");
                        SumEREFF += S.erRateEFF;
                    }
                    if (SumEREFF > max) {
                        max = SumEREFF;
                        maodian[i] = maodian_serial[i].get(maodian_);          //最终确定充电器的位置和方向
                        maodian_V[i] = Charger_angle ;
                    }
                    for (Sensor S : cluster[i]) {
                        S.erRateEFF = 0;
                        S.erRate = 0;
                        S.isClover = false;
                        S.multihop = -1;
                    }
                }
            }
        }
        System.out.println("***********************");
        System.out.println("充电器位置和角度");
        for (int i=0;cluster[i]!=null;i++){
            System.out.println(maodian[i] + ", "+ Math.toDegrees(maodian_V[i]));
        }
        System.out.println("***********************");
        System.out.println("充电器直接覆盖情况");
        for (int i=0;cluster[i]!=null;i++){
            for (int j=0; j < cluster[i].size(); j++){
//                if (cluster[i].size() < 2){
//                    cluster[i].get(0).erRateEFF=1;
//                    cluster[i].get(0).multihop=-2;
//                    cluster[i].get(0).isClover = true;
//                    System.out.println(cluster[i].get(j).location+"from cluster No."+ i +" if clovered?:"+ cluster[i].get(j).isClover);
//                    continue;
//                }
                cluster[i].get(j).isClover = WsnFunction2.IFclovered(maodian[i],cluster[i].get(j), maodian_V[i], V, R);
                System.out.println(cluster[i].get(j).location+"from cluster No."+ i +" if clovered?:"+ cluster[i].get(j).isClover);
            }
        }

        //确定充电器部署点后再执行一次算法以更新充电效率和节点覆盖率


        double Sum = 0;
        for (int i=0;cluster[i]!=null;i++){

            if (cluster[i].size() < 2){
//                    for (Sensor S : cluster[i]){
//                        System.out.print(S.erRateEFF + "+");
//                    }
//                    System.out.println(" ");
                continue;
            }
            for (Sensor S : cluster[i]){
                if (S.isClover) {
                    S.erRate = (float) (100 / Math.pow(40 + Point.getDistance(S.location, maodian[S.cluster]), 2));
                    S.erRateEFF = Math.min(S.erRate * (1/Cp), 1);
                }
            }
        }

/*


        double Sum =0;
        for (int i=0;cluster[i]!=null&&i<cluterNum;i++) {
            for (Sensor S : cluster[i]) {
                S.erRate = (float) (100 / Math.pow(40 + Point.getDistance(S.location, S.charger.location), 2));
                S.erRateEFF = Math.min(S.erRate * (1 / Cp), 1);
//                Sum += S.erRateEFF;
            }
            if (i==cluterNum-1) break;
        }
        for (int i=0;i <cluterNum;i++) {
            for (Sensor S : cluster[i]) {
                Sum += S.erRateEFF;
            }
        }
        System.out.println("总充电效用：" + Sum / nodenum);


*/


        System.out.println("***********************");
        System.out.println("各节点的充电效率");
        for (int i=0;cluster[i]!=null;i++) {
            for (Sensor S : cluster[i]){
                System.out.print(S.erRateEFF + "+");
            }
            System.out.println(" ");
        }
        for (int i=0;cluster[i]!=null;i++){
            for (Sensor S : cluster[i]){
                Sum += S.erRateEFF;
            }
        }
        System.out.println("总充电效用："+Sum/nodenum);
    }

}
