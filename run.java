import java.util.LinkedList;

public class run {

    public static void main(String[] args) {
        //网络规模
        float networkSize = 150;
        //传感器节点个数
        int nodenum = 100;
        //系统当前时间初始为0s
        int systemTime = 0;
        //能量消耗率最小值
        float minECR = 0.01f;
        //能量消耗率最大值
        float maxECR = 0.06f;
        //多跳阈值
        float THR_erRateEFF = 0.1f;

        float D =11.7f;//聚类圆半径(R=20时，D取值为5.8~11.7)
        double V = Math.PI/15 ; //充电器角度
        double R = 20;//充电器半径
        float Cp = 0.05f;

        circle[] cluster_circle = new circle[100];		//簇圆
        Point[] maodian = new Point[100]; //充电器摆放位置
        double[] maodian_V = new double[100]; //充电器摆放角度
        LinkedList<Point>[] maodian_serial = new LinkedList[100];
        int cluster_circle_NUM;
        LinkedList<Point> lists;
        LinkedList<Point>[] cluster_point = new LinkedList[100];		//簇，但将簇内Sensor转换成Point
        final LinkedList<Sensor>[] cluster = new LinkedList[1000];		//簇，元素为Sensor
        LinkedList<Point> Centroid = new LinkedList<Point>();               //簇心
        int cluster_NUM  ;
        Sensor[][] allSensor = new Sensor[1000][];
        int allSensor_level = 0;
        LinkedList<Point>[] cluster_edge = new LinkedList[100] ;
        int cluster_Point_NUM ;

        lists = new LinkedList<Point>();
        cluster_NUM = 0;
        cluster_Point_NUM=0;
        cluster_circle_NUM = 0;
//		cluster[cluster_NUM] = new LinkedList<Sensor>();
        cluster_point[cluster_Point_NUM] = new LinkedList<Point>();
        allSensor[0] = WsnFunction.initSensors(networkSize, nodenum, minECR, maxECR);
        cluster_edge[0] = new LinkedList<Point>();

//**************************分簇********************************
        System.out.println("***********************");
        System.out.println("分簇情况如下");
        while (allSensor[allSensor_level].length!=0){
            cluster[cluster_NUM] = WsnFunction.findSensors(D, allSensor[allSensor_level]);
            ++allSensor_level;
            allSensor[allSensor_level] = new Sensor[allSensor[allSensor_level-1].length - cluster[cluster_NUM].size()];
            allSensor[allSensor_level] = WsnFunction.update_allSensors(cluster[cluster_NUM],allSensor[allSensor_level-1]);
//            for (int i = 0; i < cluster[cluster_NUM].size(); i++) {
//                lists.remove(cluster[cluster_NUM].get(i).location);
//            }
            //输出各个簇所包含的节点
            if (cluster[cluster_NUM].size()>1) {
                for (Sensor s : cluster[cluster_NUM]) {
                    s.cluster = cluster_NUM;
                    System.out.println(s.location + "from cluster No." + cluster_NUM);
                }
                ++cluster_NUM;
            }else System.out.println(cluster[cluster_NUM].get(0).location + "此节点暂时孤立" );
        }
        System.out.println("***********************");
//**************************充电器摆放位置*********************************
        for (int i=0;cluster[i]!=null;i++){
            float x=0,y=0;
            for (Sensor S : cluster[i]){
                x+=S.location.x;
                y+=S.location.y;
            }
            Point a = new Point();
            a.x = x/cluster[i].size();
            a.y = y/cluster[i].size();
            Centroid.add(a);
        }
//        for (Point s : Centroid) {
//            System.out.println(s+"+");
//        }
        System.out.println("簇心位置");
        for (int i=0;cluster[i]!=null;i++){
            cluster_point[i] = new LinkedList<Point>();
            for (int j = 0;j < cluster[i].size();j++){
                cluster_point[i].add(cluster[i].get(j).location);
            }
            cluster_edge[i] = MinimumBoundingPolygon.findSmallestPolygon(cluster_point[i]);
            cluster_circle[i] = WsnFunction.find_cirle(cluster_edge[i]);
            System.out.println(cluster_circle[i].center);
        }

        //获取充电器摆放位置
/*        for (int i=0;cluster[i]!=null;i++){
            if (cluster_circle[i].center.x!=Centroid.get(i).x||cluster_circle[i].center.y!=Centroid.get(i).y){
                maodian[i] = WsnFunction.getPoint(cluster_circle[i],cluster_circle[i].center,Centroid.get(i));
                System.out.println(maodian[i]);
            }else {
                maodian[i] = new Point();
                maodian[i].x = cluster_circle[i].center.x; maodian[i].y = cluster_circle[i].center.y+D;
                System.out.println(maodian[i]);
            }
        }*/
        for (int i=0;cluster[i]!=null;i++){
            if (cluster[i].size()>2){
                Point a = new Point();
                Point b = new Point();
                a = WsnFunction.getPoint(cluster_circle[i],cluster_circle[i].center,Centroid.get(i));
                double k = Math.atan2((Centroid.get(i).y-a.y),(Centroid.get(i).x-a.x));
                b.x = (float)(a.x - D*Math.cos(k));
                b.y = (float)(a.y - D*Math.sin(k));
                Point c = new Point((float)(a.x + D*Math.cos(k)),(float)(a.y + D*Math.sin(k)));
                maodian_serial[i] = WsnFunction.divided_line(c,b,200);
            }else if (cluster[i].size()==2){
                Point a = new Point((cluster[i].get(0).location.x+cluster[i].get(1).location.x)/2,(cluster[i].get(0).location.y+cluster[i].get(1).location.y)/2);
                double k = Math.PI/2 + Math.atan2((cluster[i].get(1).location.y-cluster[i].get(0).location.y),(cluster[i].get(1).location.x-cluster[i].get(0).location.x));
                Point b = new Point();
                b.x = (float)(a.x - D*Math.cos(k));
                b.y = (float)(a.y - D*Math.sin(k));
                Point c = new Point((float)(a.x + D*Math.cos(k)),(float)(a.y + D*Math.sin(k)));
                maodian_serial[i] = WsnFunction.divided_line(c,b,200);
            }else {
                maodian[i] = cluster[i].get(0).location;
            }

        }
/*
        System.out.println("***********************");
        System.out.println("节点覆盖情况");
        for (int i=0;cluster[i]!=null;i++){
            for (int j=0; j < cluster[i].size(); j++){
                cluster[i].get(j).isClover = WsnFunction.IFclovered(maodian[i],cluster_circle[i].center,V,R,cluster[i].get(j));
                System.out.println(cluster[i].get(j).location+"from cluster No."+ i +" if clovered?:"+ cluster[i].get(j).isClover);
            }
        }
*/



        double SumER = 0;//总充电效率
        for (int i=0;cluster[i]!=null;i++){
//            System.out.println("簇"+i+"各节点的接受功率");
 /*           for (Sensor S : cluster[i]){
                if (S.isClover) {
                    S.erRate = (float) (100 / Math.pow(40 + Point.getDistance(S.location, maodian[i]), 2));
                    S.erRateEFF = Math.min( S.erRate * Cp,1);
                    S.multihop = -2 ;
                    System.out.print(S.erRate + "+");
                }else {
                    //此处初选多跳路径
                    int     nextHOP=-1 ;
                    double maxERrate=0 ;
                    boolean change =false;
                    for (int f=0;f < cluster[i].size();f++){
                        if (cluster[i].get(f).isClover && S.getERRate(Sensor.getDistance(S,cluster[i].get(f)))*cluster[i].get(f).erRateEFF > maxERrate ){     //如果选择的下一跳节点为MC直接覆盖节点，且以此为其中继节点能量传输效率高，则记录此中继节点
                            maxERrate = S.getERRate(Sensor.getDistance(S,cluster[i].get(f)))*cluster[i].get(f).erRateEFF;
                            nextHOP = f;
                            change  = true;
                        }
                    }
                    if (nextHOP >= 0) {
                        S.erRateEFF = S.getERRate(Sensor.getDistance(S, cluster[i].get(nextHOP))) * cluster[i].get(nextHOP).erRateEFF;     //多跳效率为多跳路径中每一段效率累乘。
                        S.multihop = cluster[i].get(nextHOP).number;                                                                                  //确定该节点i的下一跳节点。
                    }
                }
//                    SumEREFF += Math.min(S.erRate * 20, 1);

            }*/
            if (cluster[i].size() < 2){
                cluster[i].get(0).erRateEFF=1;
                cluster[i].get(0).multihop=-2;
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
                         while (WsnFunction.IF_noPATH(cluster[i])) {
                             cluster[i] = WsnFunction.multihop_PATH(cluster[i], Cp);
                             //从得到的所有未被覆盖节点中选取erRateEFF最大的节点及其路径（下一跳）
                             double maxERrate = 0;
                             int sensor_maxERrate = -1;
                             for (int f1 = 0; f1 < cluster[i].size(); f1++) {
                                 if (!cluster[i].get(f1).isClover && cluster[i].get(f1).erRateEFF > maxERrate) {
                                     maxERrate = cluster[i].get(f1).erRateEFF;
                                     sensor_maxERrate = f1;
                                 }
                             }
                             if (sensor_maxERrate >= 0) {
                                 cluster[i].get(sensor_maxERrate).isClover = true;//将最大erRateEFF的节点加入到已覆盖的集合中
                             }
                         }
                     } else {
                         for (Sensor S : cluster[i]) {
                             S.erRateEFF = 0;
                             S.erRate = 0;
                             S.isClover = false;
                             S.multihop = -1;
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
            System.out.println(maodian[i] + ", "+ maodian_V);
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

        //确定充电器部署点后再执行一次多跳选择算法以更新充电效率和节点覆盖率


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
                        S.multihop = -2;
                    }
                }
                while (WsnFunction.IF_noPATH(cluster[i])) {
                    cluster[i] = WsnFunction.multihop_PATH(cluster[i],Cp);
                    //从得到的所有未被覆盖节点中选取erRateEFF最大的节点及其路径（下一跳）
                    double maxERrate = 0;
                    int sensor_maxERrate = -1;
                    for (int f1 = 0; f1 < cluster[i].size(); f1++) {
                        if (!cluster[i].get(f1).isClover && cluster[i].get(f1).erRateEFF > maxERrate) {
                            maxERrate = cluster[i].get(f1).erRateEFF;
                            sensor_maxERrate = f1;
                        }
                    }
                    if (sensor_maxERrate >= 0) {
                        cluster[i].get(sensor_maxERrate).isClover = true;//将最大erRateEFF的节点加入到已覆盖的集合中
                    }
                }

//                for (Sensor S : cluster[i]){
//                    System.out.print(S.erRateEFF + "+");
//                }
//                System.out.println(" ");
            }
//            for (int i=0;cluster[i]!=null;i++){
//                for (Sensor S : cluster[i]){
//                    Sum += S.erRateEFF;
//                }
//            }
//        System.out.println("总充电效用："+Sum/nodenum);

            //处理孤立节点
            for (int i=0;cluster[i]!=null;i++) {
                if (cluster[i].size() > 1){
                    continue;
                }else {
                    double max_2 = 0;
                    int flag_1 = -1;
                    for (int j=0;cluster[j].size()>1;j++){
                        for (Sensor S : cluster[j]){
                            if (max_2 < 100 / Math.pow(40 + Point.getDistance(S.location, cluster[i].get(0).location), 2)) {
                                max_2 = 100 / Math.pow(40 + Point.getDistance(S.location, cluster[i].get(0).location), 2);
                                flag_1 = S.number;
                            }
                        }
                    }
                    cluster[i].get(0).erRateEFF = (float) max_2;
                    cluster[i].get(0).multihop = flag_1;
                }

            }
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

//        System.out.println("总充电效用");
    }




/*

        Sensor[] allSensor = WsnFunction.initSensors(networkSize, nodenum, minECR, maxECR);
//        System.out.println("随机创建的节点信息如下");
//        for(Sensor node:allSensor) {
//            System.out.println("编号:"+node.number+" 坐标:("+node.location.x+","+node.location.y+") 剩余能量阈值:"+node.remainingE/node.maxCapacity +" 剩余寿命:"+node.remainingE/node.ecRate);
//        }
        honeycomb test = new honeycomb(8f, 210.00f);    //创建蜂窝
        Point[] k = test.creat_honeycomb(8f, 210.00f);//获取每个蜂窝的中心坐标

        for (int i = 0; i < allSensor.length; i++) {                //将节点分簇，每个节点的inHoneycomb值表示其所在的簇
            allSensor[i].inHoneycomb = WsnFunction.judgeHoneycomb(allSensor[i], test);
            //System.out.println("编号:"+allSensor[i].number+" 坐标:("+allSensor[i].location.x+","+allSensor[i].location.y+")属于编号为"+allSensor[i].inHoneycomb+"的正六边形，其中心坐标为"+"("+k[WsnFunction.judgeHoneycomb(allSensor[i],test)].x+","+k[WsnFunction.judgeHoneycomb(allSensor[i],test)].y+")");
        }

        //========================选取锚点========================
        Point[] Anchor = new Point[k.length];   //存放每个簇的锚点坐标，Anchor[0]表示第0个正六边形区域内的锚点位置
        Point[][] initialAnchor = WsnFunction.initialAnchor(test);     //初选锚点

//        for(int i=0;i<Anchor.length;i++)
//            for(int j=0;j<6;j++)
//            System.out.println(initialAnchor[i][j].x+","+initialAnchor[i][j].y);
//            //测试，输出某个锚点覆盖的节点数量
//        int number = WsnFunction.cloverNUM(initialAnchor[11][3],allSensor);
//        System.out.println(number);

        for (int i = 0; i < Anchor.length; i++) {
            int num = 0, temp_1 = 0;
            for (int j = 0; j < 6; j++) {
                if (num < WsnFunction.cloverNUM(initialAnchor[i][j], allSensor)) {
                    num = WsnFunction.cloverNUM(initialAnchor[i][j], allSensor);
                    temp_1 = j;
                }
            }
//            System.out.println(i+","+temp_1+","+num);
            if(num==0)  Anchor[i] = new Point(-1,-1,-1);
             else   Anchor[i] = initialAnchor[i][temp_1];    //确定第i个正六边形区域的锚点坐标

            //测试输出
            //System.out.println("第"+Anchor[i].num+"个簇的锚点坐标为（"+Anchor[i].x+"，"+Anchor[i].y+"),且以此为锚点，MC能覆盖到的节点个数为："+num);
        }


        //========================整理，删除只能覆盖0个节点的锚点========================
        int h = 0; // 设置一个变量作为增量
        // 循环读取Anchor数组的值
        for (Point b : Anchor) {
            // 判断，如果Anchor数组的num值不为0那么h就加1
            if (b.num != -1) {
                h++;
            }
        }
        // 得到了数组里不为0的个数，以此个数定义一个新数组，长度就是h
        Point[] newAnchor = new Point[h];
        // 偷个懒，不想重新定义增量了，所以把增量的值改为0
        h = 0;
        // 在次循环读取Anchor数组的值
        for (Point c : Anchor) {
            // 把不为0的值写入到newAnchor数组里面
            if (c.num != -1) {
                newAnchor[h] = c;
                h++;// h作为newArr数组的下标，没写如一个值，下标h加1
            }
        }

//        for (Point node:newAnchor
//             ) {
//            System.out.println("第"+node.num+"个簇的锚点坐标为（"+node.x+"，"+node.y+"),且以此为锚点，MC能覆盖到的节点个数为：");
//
//        }

    //========================更新节点被覆盖的情况，将能够直接被MC覆盖的节点isCloverDirect置为true========================
    for(int i=0;i<newAnchor.length;i++){
        WsnFunction.cloverNODE(newAnchor[i],allSensor);
    }
//        System.out.println("更新节点信息如下");
//        for(Sensor node:allSensor) {
//            System.out.println("编号:"+node.number+" 坐标:("+node.location.x+","+node.location.y+"）属于序号为"+node.inHoneycomb+"的簇，其能否被直接覆盖到："+node.isClover+"剩余能量阈值:"+node.remainingE/node.maxCapacity +" 剩余寿命:"+node.remainingE/node.ecRate);
//        }
    //========================多跳开始，确定多跳路径========================
        //首先，将每一个簇的节点归类放到cluster数组中
        Sensor[][] cluster= new Sensor[Anchor.length][];
        for (int i=0; i < Anchor.length;i++){
            int f=0,num=0;
           for (int j=0; j < allSensor.length;j++){
               if(allSensor[j].inHoneycomb==i) num++;
           }
           cluster[i] = new Sensor[num];
            for (int j=0; j < allSensor.length;j++) {
                if(allSensor[j].inHoneycomb==i) {
                    cluster[i][f] = allSensor[j]; f++;
                }
            }
        }
//        for (int i=0;i < cluster.length; i++){
//            System.out.println( cluster[i].length);
//        }
       // System.out.println(cluster.length);
    // 第i个簇中

        */
/*
        *                                 确定多跳路径的算法：
        * 1.首先确定单个簇内的多跳路径，遍历簇内所有节点，区分被MC直接覆盖的节点集合A与未被MC覆盖的集合B。
        *
        * 2.循环遍历集合B中的所有节点，且在每次访问集合B中节点i时，执行以下步骤：
        *               完整遍历集合A中的每个节点，计算该节点与B中节点i的距离，并确定以此节点为多跳中继节
        *            点时，B的多跳效率。当遍历完A中每个节点以后，从中找出最大效率的那个节点j，初步将B中i节
        *            点的下一跳设置为j
        *
        * 3.此时已经得到集合B中每个节点的下一跳节点，遍历这些路径，找到具有最大多跳效率的那个路径，并将此路径
        *   中原属于集合B的节点i加入到集合A中，得到新的集合B与集合A。
        *
        * 4.继续循环遍历集合B中的所有节点，循环退出条件：集合B为空。
        *
        * 5.此时已经得到该簇的所有多跳路径，根据设置的阈值，排除多跳充电效率低于阈值的部分多跳路径
        *
        * 6.根据以上算法遍历所有簇
        *
        * *//*

    for (int i=0;i < cluster.length;i++) {


        for (int j=0;j < cluster[i].length; j++) {
            if (cluster[i][j].isClover){
                cluster[i][j].erRateEFF = cluster[i][j].getERRate(Point.getDistance(cluster[i][j].location, Anchor[i]));
                cluster[i][j].multihop = -2 ;           //MC直接覆盖的节点，其下一跳标记为-2
            }
        }

        while (WsnFunction.IF_noPATH(cluster[i])) {
            cluster[i] = WsnFunction.multihop_PATH(cluster[i]); //初选cluster[i]的多跳路径


            //从得到的所有未被覆盖节点中选取erRateEFF最大的节点及其路径（下一跳）
            double maxERrate = 0;
            int sensor_maxERrate = -1;
            for (int f1 = 0; f1 < cluster[i].length; f1++) {
                if (!cluster[i][f1].isClover && cluster[i][f1].erRateEFF > maxERrate) {
                    maxERrate = cluster[i][f1].erRateEFF;
                    sensor_maxERrate = f1;
                }
            }
            if (sensor_maxERrate >= 0) {
                cluster[i][sensor_maxERrate].isClover = true;//将最大erRateEFF的节点加入到已覆盖的集合中
            }
        }
       // 排除多跳效率过低的路径
        for (int j=0;j < cluster[i].length; j++) {
            if (cluster[i][j].erRateEFF < THR_erRateEFF) {
                cluster[i][j].isClover = false;
                cluster[i][j].multihop = -1;
            }
        }
    }
//    for (int i=0;i < cluster.length; i++){
//        for (int j=0; j < cluster[i].length;j++)
//        System.out.println(cluster.length+"..."+cluster[i][j].multihop);}




        //========================TSP开始========================
        //使用遗传算法
        int[] best; //best[]中存放tsp输出顺序

//        //=======================method 1=======================
//        GeneticAlgorithm ga = new GeneticAlgorithm();
//        best = ga.tsp(getDist(newAnchor));
//
//        int n = 0;
//        while (n++ < 100) {
//            best = ga.nextGeneration();
//
//            System.out.println("best distance:" + ga.getBestDist() +
//                    " current generation:" + ga.getCurrentGeneration() +
//                    " mutation times:" + ga.getMutationTimes());
//            System.out.print("best path:");
//            for (int i = 0; i < best.length; i++) {
//                System.out.print(best[i] + " ");
//            }
//            System.out.println();
//        }

        //=======================method 2========================
        GeneticAlgorithm ga = GeneticAlgorithm.getInstance();

        ga.setMaxGeneration(1000);
        ga.setAutoNextGeneration(true);
        best = ga.tsp(getDist(newAnchor));
        System.out.print("best path:");
        for (int i = 0; i < best.length; i++) {
            System.out.print(best[i] + " ");
        }
        System.out.println("..............."+best.length);



    }




    private static float[][] getDist(Point[] points) {
        float[][] dist = new float[points.length][points.length];
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points.length; j++) {
                dist[i][j] = (float) Point.getDistance(points[i], points[j]);
            }
        }
        return dist;
    }
*/

//    private static float distance(com.onlylemi.genetictsp.Point p1, com.onlylemi.genetictsp.Point p2) {
//        return (float) Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
//    }




