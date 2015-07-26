package edu.sjtu.trajectoryminer.vis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import edu.sjtu.trajectoryminer.pattern.swarm.SwarmDataGenerator;
import edu.sjtu.trajectoryminer.utils.CommonUtils;
import edu.sjtu.trajectoryminer.utils.FileUtils;

public class VisUtils {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Map<String, Double> gpsFreqMap = new HashMap<String, Double>();
		// gpsFreqMap.put("116.418261,39.921984", 50d);
//		List<String> users = new ArrayList<String>();
//		users.add("LSJW26767ES060790");
//		users.add("LSJW26764FS014660");
//		users.add("LSJW26766ES056701");
//		//generateTrajectoryDataSet(users);
//		toId();
		SwarmDataGenerator sdg = new SwarmDataGenerator();
		sdg.loadTrajectoryData();
		VisUtils.generatePath(sdg.getTrajectoryById().get("LSJW26765ES055538"), "LSJW26765ES055538");
	}

	public static void toId(){
		List<String> lines = FileUtils.readLine(new File("D:/trajectoryDis.csv"));
		List<String> res = new ArrayList<String>();
		for(String line : lines){
			String[] arr = line.split(",");
			line = arr[0]+","+arr[1]+".001,"+arr[2]+"."+arr[3];
			res.add(line);
		}
		FileUtils.writeFile(new File("D:/trajectoryDis1.csv"), res);
	}
	public static void generateTrajectoryDataSet(List<String> users) {
		File[] files = FileUtils.getFiles("D:\\Research\\Car\\Trajectory Data");
		Map<String, Map<String, String>> trajectoryByUser = new HashMap<String, Map<String, String>>();
		int cnt = 17;
		for (File file : files) {
			if (cnt-- < 0)
				break;
			System.out.println(file.getName());
			String data = FileUtils.readFile(file);
			Map<String, JSONArray> map = (Map<String, JSONArray>) JSON
					.parse(data);
			System.out.println(map.size());
			for (Map.Entry<String, JSONArray> entry : map.entrySet()) {
				if (!users.contains(entry.getKey()))
					continue;
				Map<String, String> trajectory = new HashMap<String, String>();
				for (int i = 0; i < entry.getValue().size(); i++) {
					// System.out.println(entry.getValue().get(i));
					JSONArray timegeo = (JSONArray) entry.getValue().get(i);
					String time = (String) timegeo.get(0);
					// System.out.println(time);
					JSONArray geo = (JSONArray) timegeo.get(1);
					String lng = (String) geo.get(0);
					String lat = (String) geo.get(1);
					lng = String.format("%.5f", Double.parseDouble(lng));
					lat = String.format("%.5f", Double.parseDouble(lat));
					trajectory.put(time, lng + "," + lat);
				}
				Map<String, String> trajectory0 = trajectoryByUser.get(entry
						.getKey());
				if (trajectory0 == null)
					trajectoryByUser.put(entry.getKey(), trajectory);
				else {
					for (Map.Entry<String, String> entry1 : trajectory
							.entrySet()) {
						trajectory0.put(entry1.getKey(), entry1.getValue());
					}
					trajectoryByUser.put(entry.getKey(), trajectory0);
				}
			}
		}
		BufferedWriter writer = FileUtils.getBufferedWriter(new File(
				"D:/trajectoryDis.csv"));
		try {
			writer.write("individual-local-identifier,timestamp,location-long,location-lat\n");
			for (Map.Entry<String, Map<String, String>> entry : trajectoryByUser
					.entrySet()) {
				for (Map.Entry<String, String> entry1 : entry.getValue()
						.entrySet()) {
					writer.write(entry.getKey() + "," + entry1.getKey() + ","
							+ entry1.getValue().split(",")[0] + ","
							+ entry1.getValue().split(",")[1]+"\n");
				}
			}
		} catch (IOException e) {

		} finally {
			FileUtils.closeWriter(writer);
		}

	}

	public static void loadAllTrajectory(List<String> users) {
		File[] files = FileUtils.getFiles("D:\\Research\\Car\\Trajectory Data");
		Map<String, Map<String, String>> trajectoryByUser = new HashMap<String, Map<String, String>>();
		int cnt = 17;
		for (File file : files) {
			if (cnt-- < 0)
				break;
			System.out.println(file.getName());
			String data = FileUtils.readFile(file);
			Map<String, JSONArray> map = (Map<String, JSONArray>) JSON
					.parse(data);
			System.out.println(map.size());
			for (Map.Entry<String, JSONArray> entry : map.entrySet()) {
				if (!users.contains(entry.getKey()))
					continue;
				Map<String, String> trajectory = new HashMap<String, String>();
				for (int i = 0; i < entry.getValue().size(); i++) {
					// System.out.println(entry.getValue().get(i));
					JSONArray timegeo = (JSONArray) entry.getValue().get(i);
					String time = (String) timegeo.get(0);
					// System.out.println(time);
					JSONArray geo = (JSONArray) timegeo.get(1);
					String lng = (String) geo.get(0);
					String lat = (String) geo.get(1);
					lng = String.format("%.5f", Double.parseDouble(lng));
					lat = String.format("%.5f", Double.parseDouble(lat));
					trajectory.put(time, lng + "," + lat);
				}
				Map<String, String> trajectory0 = trajectoryByUser.get(entry
						.getKey());
				if (trajectory0 == null)
					trajectoryByUser.put(entry.getKey(), trajectory);
				else {
					for (Map.Entry<String, String> entry1 : trajectory
							.entrySet()) {
						trajectory0.put(entry1.getKey(), entry1.getValue());
					}
					trajectoryByUser.put(entry.getKey(), trajectory0);
				}
			}
		}

		for (String user : users) {
			Map<String, String> mapByUser = trajectoryByUser.get(user);
			Map<String, Double> pointFreq = new HashMap<String, Double>();
			for (Map.Entry<String, String> entry : mapByUser.entrySet()) {
				pointFreq = CommonUtils.addToMap(pointFreq, entry.getValue());
			}
			generateHeatMap(pointFreq, user);
		}

	}

	public static Map<String, Double> loadStaticPoint() {
		File[] files = FileUtils.getFiles("D:\\Research\\Car\\StaticAnalysis");
		Map<String, Map<String, String>> staticPoints = new HashMap<String, Map<String, String>>();

		for (File file : files) {
			List<String> lines = FileUtils.readLine(file);
			String carNum = "";
			for (String line : lines) {
				if (!line.startsWith("20")) {
					carNum = line;
				} else {
					String[] arr = line.split("-_-");
					String time = arr[0];
					String point = arr[1].substring(0, arr[1].indexOf(":"));
					staticPoints = CommonUtils.addToMap(staticPoints, carNum,
							time, point);
				}
			}
		}
		Map<String, String> mapByUser = staticPoints.get("LSJW26766ES056701");
		Map<String, Double> pointFreq = new HashMap<String, Double>();
		for (Map.Entry<String, String> entry : mapByUser.entrySet()) {
			pointFreq = CommonUtils.addToMap(pointFreq, entry.getValue());
		}
		return pointFreq;
	}

	public static void generateHeatMap(Map<String, Double> gpsFreqMap,
			String name) {
		String json = "";
		for (Map.Entry<String, Double> entry : gpsFreqMap.entrySet()) {
			String[] arr = entry.getKey().split(",");
			String s = "{\"lng\":" + arr[0] + ",\"lat\":" + arr[1]
					+ ",\"count\":" + entry.getValue() + "},";
			json += s;
		}
		json = json.substring(0, json.length() - 1);
		File template = new File("./data/template/heatmap");
		String content = FileUtils.readFile(template);
		content = content.replace("$GPS$", json);
		File heatmapFile = new File("./result/" + name + ".html");
		FileUtils.createFile(heatmapFile, true);
		FileUtils.writeFile(heatmapFile, content);
	}
	
	public static void generatePath(Map<Date,String[]> trajectory,String name){
		String points = "";
		for(Map.Entry<Date, String[]> entry : trajectory.entrySet()){
			points += "new BMap.Point("+entry.getValue()[0]+", "+entry.getValue()[1]+"),";
		}
		if(points.endsWith(","))
			points = points.substring(0, points.length()-1);
		
		File template = new File("./data/template/path2");
		String content = FileUtils.readFile(template);
		content = content.replace("$Points$", points);
		File heatmapFile = new File("./result/" + name + ".html");
		FileUtils.createFile(heatmapFile, true);
		FileUtils.writeFile(heatmapFile, content);
	}
}
