
import java.text.DecimalFormat;

public class Sensor {


    int number = 0;//节点编号
    Point location = null;//节点的位置
    float maxCapacity = 50;//电池容量默认50J,所有节点都一样
    float ecRate = 0;//平均能量消耗率J/s,距离基站越近消耗越大
    float erRate = 0;//能量接受率[1,5],与小车位置有关,需要确定好小车充电停止点后,才能确定节点能量接受率
    float erRateEFF = 0;//能量接收效率，erRate=erRateEFF*maxP
    float remainingE = 50;//初始剩余能量等于电池容量
    boolean isCharging = false;//是否需要充电,默认为false表示不需要充电
    boolean isFailure = false;//表示节点是否死亡,false表示未死亡
    int inHoneycomb ; //节点所属的簇
    boolean isClover = false;//是否已经被inHoneycomb内的锚点直接覆盖
    int multihop = -1;    //未被直接覆盖的节点，其多跳充电的下一条

    //初始化传感器编号位置,节点能耗为指定值
    public Sensor(){
        location = new Point(-1,-1);
        
    }
    public Sensor(int number, float lx, float ly, float ecr) {
        this.number = number;
        location = new Point(lx, ly);
        ecRate = ecr;
    }
    //初始化传感器编号、位置、能耗
    public Sensor(int number, float lx, float ly, float networkSize, float minECR, float maxECR) {
        this.number = number;
        location = new Point(lx, ly);
        //根据节点与基站的距离计算出节点的能耗
        ecRate = calECRate(Point.getDistance(location,new Point(networkSize/2, networkSize/2)), networkSize, minECR, maxECR);
    }

    //初始化编号、位置、能耗和容量
    public Sensor(int number, float lx, float ly, int networkSize, float minECR, float maxECR, int maxC) {
        this.number = number;
        location = new Point(lx, ly);
        ecRate = calECRate(Point.getDistance(location,new Point(networkSize/2, networkSize/2)), networkSize, minECR, maxECR);
        maxCapacity = maxC;//初始能量
        remainingE = maxC;//初始剩余能量等于电池容量
    }
    public static float getDistance(Sensor p1, Sensor p2) {
        float distance = 0.0f;
        //X轴的距离平方
        double dx = Math.pow(p1.location.x-p2.location.x, 2);
        //Y轴的距离平方
        double dy = Math.pow(p1.location.y-p2.location.y, 2);
        distance = (float)(Math.sqrt(dx+dy));
        return distance;
    }

    //根据节点与基站之间的距离以及网络大小和能量消耗区间,计算某个节点的能量消耗率
    private float calECRate(double distance, double networkSize, double minECR, double maxECR) {
        //基站位置在正方形区域的中心
        double bs = networkSize/2.0;
        //节点与基站之间的最大距离;最小距离为0
        double maxDistance = Math.sqrt(2)*bs;
        //节点的能耗与其和基站的距离是线性关系,且两者成反比:与基站距离越小,能量消耗率越大
        float ec = (float) (minECR + (1-(distance/maxDistance))*(maxECR-minECR));
        //新建格式化器，设置格式
                            //        DecimalFormat Dformat = new DecimalFormat("0.00");
//        String string = Dformat.format(ec);
//        ec = Double.parseDouble(string);
        return ec;
    }


    public float getERRate(float distance) {
        float nta;
        if (distance == MCV.maxRadius) nta = 0.2f;
        else {
            //能量传递效率[0.2,1]
            nta = (float) (-0.0958 * Math.pow(distance*2.7/16, 2) - 0.0377 * (distance*2.7/16) + 1.0);//nta是效率                  //此处有问题
        }
        if (nta > 0.2) return nta;
        else return nta = -1;
    }

    //根据节点与停止点的距离计算节点的能量接受率[1,5]
    public double setERRate(double distance,double maxP) {
        double nta,rp;
        if(distance == MCV.maxRadius) rp = 1;
        else {
            //能量传递效率[0.2,1]
            nta = -0.0958*Math.pow(distance, 2) - 0.0377*distance + 1.0;//nta是效率
            rp = nta * maxP;
            //新建格式化器，设置格式,对rp保留两位小数
            DecimalFormat Dformat = new DecimalFormat("0.000");
            //根据格式化器格式化数据
            String rpStr = Dformat.format(rp);
            //将String转为double
            rp = Double.parseDouble(rpStr);
            //得到节点的能量接受率

        }
        return rp;
    }

    /*
     * 无论何时,根据当前节点剩余能量和消耗率,以及两个阈值,找出需要充电的节点
     */
    public static int requestCharging(double lr,double lc,Sensor...allsensor) {
        int time = 0;
        boolean isreachlc = false;
        if(lr <= lc) return -1;//阈值设置有误
        while(!isreachlc) {
            //根据当前节点的剩余能量阈值,遍历所有节点,找出需要充电的节点
            for(int i=0;i<allsensor.length;i++) {
                //计算当前节点的阈值
                double threshold = allsensor[i].remainingE/allsensor[i].maxCapacity;

                if(threshold >= lr) {
                    //未达到阈值,该节点不需要充电,继续遍历下一个节点
                    continue;
                }

                else if(threshold < lr && threshold >= lc) {
                    //该节点需要被充电
                    allsensor[i].isCharging = true;
                }

                else {//threshold < lc
                    //threshold < lc < lr,该节点需要被充电
                    allsensor[i].isCharging = true;
                    //有节点阈值达到lc,对需要充电的节点启动充电之旅
                    isreachlc = true;
                    //跳出循环,开始启动充电之旅
                    break;
                }
            }

            if(!isreachlc) {//遍历全部节点后,当前还没有任何节点能量阈值到达lc,那么就继续等待,并更新节点的剩余能量
                //循环一次代表时间增加1s
                time = time + 1;
                //每经过1s更新一次节点剩余能量
                for(int i=0;i<allsensor.length;i++) {
                    allsensor[i].remainingE -= allsensor[i].ecRate * 1;
                }
                //updateRE(1,allsensor);
            }

        }
        return time;//返回启动下一轮充电,需要的时间
    }

    //更新传感器集合sensors经过时间time秒后的剩余能量
    public static void updateRE(double time,Sensor...sensors) {

        for(int i=0;i<sensors.length;i++) {

            if(sensors[i].remainingE <= 0) {//该节点之前已经死亡
                //System.out.println("节点死亡状态:"+sensors[i].isFailure);
                //求该节点总死亡时间=之前死亡时间+现在死亡时间time;
                //double beforeft = Math.abs(sensors[i].remainingE)/sensors[i].ecRate;//之前死亡时间
                //double totalft = beforeft + time;//该节点总死亡时间
                //System.out.println("编号为"+sensors[i].number+"的节点已死亡时间:"+ totalft +"s");
                //总死亡时间再加上该节点现在的死亡时间time
                WsnFunction.failureTime += time;
                //更新经过时间time后每个节点的剩余能量
                sensors[i].remainingE -= sensors[i].ecRate * time;
                sensors[i].isFailure = true;//节点设置为死亡状态(貌似没必要在这里置为死亡状态,只是为了放心!)
            }
            else {//该节点之前没有死亡
                //更新经过时间time后每个节点的剩余能量
                sensors[i].remainingE -= sensors[i].ecRate * time;
                if(sensors[i].remainingE <= 0) {//更新节点剩余能量后,节点死亡了
                    sensors[i].isFailure = true;//节点设置为死亡状态
                    //求节点死亡时间
                    double ft = Math.abs(sensors[i].remainingE)/sensors[i].ecRate;
                    //累加总死亡时间
                    WsnFunction.failureTime += ft;
                    //System.out.println("编号为"+sensors[i].number+"的节点已死亡时间:"+ ft +"s");
                }

            }

        }

    }



}
