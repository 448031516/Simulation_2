import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import controlP5.ControlP5; 
import processing.core.PApplet; 
import java.util.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class GeneticP5Visaul extends PApplet {







//new
float networkSize = 1000;
        //传感器节点个数
        int nodenum = 1500;
        //系统当前时间初始为0s
        int systemTime = 0;
        //能量消耗率最小值
        float minECR = 0.01f;
        //能量消耗率最大值
        float maxECR = 0.06f;
        //多跳阈值
        float THR_erRateEFF = 0.1f;
        
/**
 * GeneticVisual
 *
 * @author: onlylemi
 */


Point[] points;
ControlP5 cp5;

boolean running = false;

LinkedList<Point> lists;
Sensor[] allSensor = WsnFunction.initSensors(networkSize, nodenum, minECR, maxECR);
LinkedList<Sensor> cluster;

public void settings() {
	size(1500, 1500);
}

public void setup() {
	lists = new LinkedList<Point>();
	cluster= new LinkedList<Sensor>();
//	addPoint(50);



	cp5 = new ControlP5(this);
	cp5.addButton("onAdd").setPosition(5, 100);
	cp5.addButton("onFind").setPosition(5, 130);
	cp5.addButton("onStop").setPosition(5, 160);
	cp5.addButton("reStart").setPosition(5, 190);
	cp5.addButton("onClear").setPosition(5, 220);
}

public void draw() {
	background(220);


		//stroke(255, 0, 0);     //线条颜色 rgb
		//strokeWeight(2);   //线条宽度
	
	fill(0);
	noStroke();
	for (int i = 0; i < lists.size(); i++) {
		ellipse(lists.get(i).x, lists.get(i).y, 5,5 );
	}
	// if(cluster.size()!=0){
	// 	fill(255,0,0);
	// 	noStroke();
	// 	for (int i = 0; i < lists.size(); i++) {
	// 		ellipse(cluster.get(i).location.x, cluster.get(i).location.y, 30,30 );
	// 	}
	// }
	stroke(0);
	fill(0);
	textSize(12);
	text("point length: " + lists.size(), 5, 20);
	
}



public void onAdd() {
	if (!running) {
		addPoint();
		points = null;
	}
}
public void onFind(){
	cluster = WsnFunction.findSensors(100,allSensor);
	for(int i=0;i < cluster.size();i++){
		lists.remove(cluster.get(i).location);
	}
}
public void onStop() {
	running = false;
}

public void reStart() {
	if (points != null) {
		
		running = true;
	}
}

public void onClear() {
	lists.clear();


	running = false;
	points = null;
}


public void mousePressed() {
	if (!running && mouseX > 150) {
		addMousePoint();

		points = null;
	}
}

public void addPoint() {
	//Sensor[] newsensor = getPoint();
	for (int i = 0; i < allSensor .length; i++) {
	      lists.add(allSensor[i].location);
	}
}

public void addMousePoint() {
	Point point = new Point();
	point.x = mouseX;
	point.y = mouseY;

	lists.add(point);
}

public float[][] getDist(Point[] points) {
	float[][] dist = new float[points.length][points.length];
	for (int i = 0; i < points.length; i++) {
		for (int j = 0; j < points.length; j++) {
			dist[i][j] = distance(points[i], points[j]);
		}
	}
	return dist;
}

public float distance(Point p1, Point p2) {
	return (float) Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
}



  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "GeneticP5Visaul" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
