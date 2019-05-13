
public class MCV {

    static Point location = new Point(0,0);//小车的位置
    static float maxRadius = 8;//充电半径
    static float v = 2;//小车速度2m/s
    static float transPower = 5;//充电能耗为5W=5J/s
    static float travelPower= 8;//行驶能耗J/m
    static Point[] Anchor={ };

    /**
    * Don't let anyone instantiate this class.
   */
   private MCV() {
        // TODO Auto-generated constructor stub
    }

   /* //筛选出需要充电的节点集合
    public static Sensor[] getChargingSet(Sensor... allSensor) {
        //统计需要充电的节点数
        int n = 0;
        for(Sensor s:allSensor) {
            if(s.isCharging) n++;//先算出有多少个节点需要充电
        }
        Sensor[] chargingNode = new Sensor[n];
        int k = 0;//数组chargingNode的索引
        for(Sensor sensor:allSensor) {
            if(sensor.isCharging) {
                chargingNode[k++] = sensor;
            }
        }
        return chargingNode;
    }


    //求第k个分组之前的k-1个分组的充电时间之和
    public static double getBeforeChargingTime(int k,Group[] tspgroup) {
        double cTime = 0;
        for(int i=1;i<=k-1;i++) {//前k-1个分组的充电时间
            cTime += tspgroup[i].ctime;
        }
        return cTime;
    }

    //求第k个分组充电之前从基站出发行驶到第k个分组所用的行驶时间
    public static double getBeforeTravelTime(int k,Group[] tspgroup) {
        double distance = 0;
        for(int i=0;i<=k-1;i++) {
            distance += Point.getDistance(tspgroup[i].stop, tspgroup[i+1].stop);
        }
        return distance/MCV.v;
    }

    //对每个分组充满电,并更新充电后节点的剩余能量
    public static void multiNodeCharging(Group[] tspgroup) {
        *//*  1.对每个分组充电   *//*
        //统计按照TSP路径行驶时,充电过程中死亡节点的个数
        int fnum = 0;
        //按照TSP顺序对分组充电,由于小车到达每个分组之前需要一定的时间,即节点会消耗一定的能量,
        //故需要更新节点的剩余能量:re = re - pi*time,其中time=前面分组的充电时间+行驶到该组的行驶时间
        for(int i=1;i<=tspgroup.length-2;i++) {
            //求从基站到达第i个组所需的时间=前面分组的充电时间+行驶到改分组的行驶时间
            double bcTime = getBeforeChargingTime(i,tspgroup);//前面分组的充电时间之和
            double btTime = getBeforeTravelTime(i,tspgroup);//行驶到改分组的行驶时间
            double beforeTime = bcTime+btTime;//小车到达第i个分组的总时间
            //更新第i个分组经过时间beforeTime后组内节点的剩余能量,并统计节点死亡个数
            Sensor.updateRE(beforeTime,tspgroup[i].sensor);
            fnum += WsnFunction.getFailureNumber(tspgroup[i].sensor);
//			System.out.println("本组节点死亡数:"+WsnFunction.getFailureNumber(tspgroup[i].sensor));
            //求分组的充电时间
            tspgroup[i].setChargingTime();
            //求MCV对该组节点充满电消耗的能量
            tspgroup[i].setChargingEnergy();
            //System.out.println("第"+i+"组");
            //对当前组内的每个节点充满电
            for(int j=0;j<tspgroup[i].sensor.length;j++) {
                Sensor node = tspgroup[i].sensor[j];
                node.remainingE = node.maxCapacity;
                node.isFailure = false;//激活节点
            }
        }

        System.out.println("按照TSP充电的过程中,死亡的节点数:"+fnum);
        fnum = 0;

        *//* 2.小车对分组充电完成后,离开分组至本轮充电完成期间,分组内节点也会消耗一定的能量,因此需要更新每个分组内节点剩余能量     *//*
        for(int i=1;i<=tspgroup.length-2;i++) {
            Sensor[] node =tspgroup[i].sensor;//组内节点集合
            //求从第k个分组回到基站所需总时间=之后所有组充电时间之和+回到基站所用的行驶时间
            double acTime = getAfterChargingTime(i,tspgroup);//充电时间
            double atTime = getAfterTravelTime(i,tspgroup);//行驶时间
            double afterTime = acTime + atTime;//花费总时间
            //更新经过时间afterTime组内节点的剩余能量
            for(int j=0;j<node.length;j++) {
                node[j].remainingE -= node[j].ecRate * afterTime;
            }
        }

    }


    //求第k个分组之后的所有分组充电时间之和
    public static double getAfterChargingTime(int k,Group[] tspgroup) {
        double cTime = 0;
        for(int i=k+1;i<=tspgroup.length-2;i++) {
            cTime += tspgroup[i].ctime;
        }
        return cTime;
    }

    //求第k个分组充电完成之后,从第k个分组回到基站所用的行驶时间
    public static double getAfterTravelTime(int k,Group[] tspgroup) {
        double distance = 0;
        for(int i=k;i<=tspgroup.length-2;i++) {
            distance += Point.getDistance(tspgroup[i].stop, tspgroup[i+1].stop);
        }
        return distance/MCV.v;
    }

    //充电完成后,重置每个传感器的状态为false,表示不需要充电
    public static void resetStatus(Sensor...allsensor) {
        for(int i=0;i<allsensor.length;i++) {
            allsensor[i].isCharging = false;
        }
    }*/

}
