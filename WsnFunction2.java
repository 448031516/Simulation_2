import java.util.LinkedList;

public class WsnFunction2 {


    public static LinkedList<Sensor> findSensors(double V,double R,Sensor[] allSensor,float networkSize){
        Point[] p = new Point[200*200];
        int k = 0 ;
        for (double i=0;i < networkSize;i+=networkSize/200 ){
            for (double j=0;j < networkSize;j+=networkSize/200 ){
                p[k] = new Point(i,j);
                k++;
            }
        }
        int[]temp_1 = new int[41000*360];
        int temp_2 = 0;
        int max = 0;
        int flag_Charger = 0;
        double flag_Charger_angle = 0;
        for (int i=0; i<k;i++){     //充电器位置
            for (double Charger_angle = 0;Charger_angle <2*Math.PI;Charger_angle+=Math.PI/360){  //充电器角度

                for (int j=0;j < allSensor.length;j++){
                    if (IFclovered(p[i],allSensor[j],Charger_angle,V,R))   temp_1[temp_2]++;
                }
                //记录覆盖最多的节点的充电器位置和角度
                if (max < temp_1[temp_2]){
                    max = temp_1[temp_2];
                    flag_Charger = i;
                    flag_Charger_angle = Charger_angle;
                }
                temp_2++;
            }
        }
        LinkedList<Sensor> clovered = new LinkedList<>();
        for (int j=0;j < allSensor.length;j++){
            if (IFclovered(p[flag_Charger],allSensor[j],flag_Charger_angle,V,R)) {
                clovered.add(allSensor[j]);
            }
        }
        return clovered;
    }



    public static boolean IFclovered(Point Charger, Sensor s,double Charger_angle ,double angle , double R){
        //angle为充电器的覆盖角度，Charger_angle为充电器的摆放角度
        double Sensor_angle = getAngleByPoint(s.location.x-Charger.x,s.location.y-Charger.y);
        if (Sensor_angle <= Charger_angle + angle/2 && Sensor_angle >= Charger_angle - angle/2 && Point.getDistance(Charger,s.location) <= R){
            return true;
        }else return false;
    }




    public static double getAngleByPoint(float x1, float y1)
    {
        double d = Math.acos(x1/Math.sqrt(x1*x1+y1*y1));
//        logger.info(String.format("x: %f, y: %f,d: %f",x1,y1,(float)d));
        return y1>0 ? d : 2*Math.PI-d;
    }

    public  static  Sensor[] update_allSensors(LinkedList<Sensor> cloveredSensor,Sensor[] allSensors){
        int new_allSensors_length = allSensors.length - cloveredSensor.size();
        Sensor[] new_allSensors = new Sensor[new_allSensors_length];
        for (int i=0;i < cloveredSensor.size();i++){
            for(int j=0;j < allSensors.length; j++){
                if(cloveredSensor.get(i) == allSensors[j]){
                    for (int k=j;k < allSensors.length-1;k++){
                        allSensors[k]=allSensors[k+1];
                    }
                    // allSensors[allSensors.length - 1].location = new Point(-1,-1);
                    break;
                }

            }
        }
        System.arraycopy(allSensors, 0, new_allSensors, 0, new_allSensors.length);
        return new_allSensors ;
    }

}



