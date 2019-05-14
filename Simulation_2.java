import controlP5.ControlP5;
import processing.core.PApplet;

import java.util.*;


public class Simulation_2 extends PApplet {

	public static void main(String... args) {
		Simulation_2 pt = new Simulation_2();
		PApplet.runSketch(new String[]{"ProcessingTest"}, pt);
	}

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
	LinkedList<Sensor>[] cluster = new LinkedList[1000];
	int cluster_NUM  ;
	Sensor[][] allSensor = new Sensor[1000][];
	int allSensor_level = 0;


	public void settings() {
		size(1500, 1500);
	}

	public void setup() {
		lists = new LinkedList<Point>();
		cluster_NUM = 0;
		cluster[cluster_NUM] = new LinkedList<Sensor>();
		allSensor[0] = WsnFunction.initSensors(networkSize, nodenum, minECR, maxECR);
		//cluster = new LinkedList<Sensor>();
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
			ellipse(lists.get(i).x, lists.get(i).y, 5, 5);
		}
		if (cluster.length > 0){
			for (int i = 0;i < cluster_NUM;i++){
			 if(cluster[i].size()!=0){
				fill((float)(255*Math.random()),(float)(255*Math.random()),(float)(255*Math.random()));
				noStroke();
				for (int j = 0; j < cluster[i].size(); j++) {
						ellipse(cluster[i].get(j).location.x, cluster[i].get(j).location.y, 5,5 );
				}
			 }
			}
		}
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

	public void onFind() {

		cluster[cluster_NUM] = new LinkedList<Sensor>();
		cluster[cluster_NUM] = WsnFunction.findSensors(100, allSensor[allSensor_level]);
		++allSensor_level;
		allSensor[allSensor_level] = new Sensor[allSensor[allSensor_level-1].length - cluster[cluster_NUM].size()];
		allSensor[allSensor_level] = WsnFunction.update_allSensors(cluster[cluster_NUM],allSensor[allSensor_level-1]);
		for (int i = 0; i < cluster[cluster_NUM].size(); i++) {
			lists.remove(cluster[cluster_NUM].get(i).location);
		}
		 ++cluster_NUM;
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
		for (int i = 0; i < allSensor[0].length; i++) {
			lists.add(allSensor[0][i].location);
		}
	}

	public void addMousePoint() {
		Point point = new Point();
		point.x = mouseX;
		point.y = mouseY;

		lists.add(point);
	}

	float[][] getDist(Point[] points) {
		float[][] dist = new float[points.length][points.length];
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < points.length; j++) {
				dist[i][j] = distance(points[i], points[j]);
			}
		}
		return dist;
	}

	float distance(Point p1, Point p2) {
		return (float) Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
	}


}



