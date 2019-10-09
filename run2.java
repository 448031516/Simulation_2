import java.util.LinkedList;

public class run2 {

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


        double V = Math.PI/2 ; //充电器角度
        double R = 20;//充电器半径
        float Cp = 0.05f;
        int n = 22;//充电器部署个数

        Sensor[][] allSensor = new Sensor[1000][];
        int allSensor_level = 0;
        final LinkedList<Sensor>[] cluster = new LinkedList[1000];
        int cluster_NUM = 0  ;

        allSensor[0] = WsnFunction.initSensors(networkSize, nodenum, minECR, maxECR);

        while (allSensor[allSensor_level].length!=0){
            cluster[cluster_NUM] = WsnFunction2.findSensors(V,R, allSensor[allSensor_level],networkSize);
            ++allSensor_level;
            allSensor[allSensor_level] = new Sensor[allSensor[allSensor_level-1].length - cluster[cluster_NUM].size()];
            allSensor[allSensor_level] = WsnFunction2.update_allSensors(cluster[cluster_NUM],allSensor[allSensor_level-1]);
            for (Sensor s : cluster[cluster_NUM]) {
                s.cluster = cluster_NUM;
//                System.out.println(s.location + "from charger No." + cluster_NUM + "位置：" + s.charger.location +"角度"+ s.charger.V);
            }
            ++cluster_NUM;

        }
        double Sum = 0;
        for (int i=0;cluster[i]!=null;i++) {
            for (Sensor S : cluster[i]) {
                S.erRate = (float) (100 / Math.pow(40 + Point.getDistance(S.location, S.charger.location), 2));
                S.erRateEFF = Math.min(S.erRate * (1 / Cp), 1);
//                Sum += S.erRateEFF;
            }
        }
        for (int i=0;i < Math.min(n,cluster_NUM);i++) {
            for (Sensor S : cluster[i]) {
                Sum += S.erRateEFF;
            }
        }

        System.out.println("总充电效用：" + Sum / nodenum);



    }

}
