package edu.sjtu.trajectoryminer.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import edu.sjtu.trajectoryminer.Point;
import edu.sjtu.trajectoryminer.clustering.traclus.TraClusClustering;
import edu.sjtu.trajectoryminer.clustering.traclus.TraClusConfig;
import edu.sjtu.trajectoryminer.utils.DateUtils;
import edu.sjtu.trajectoryminer.utils.FileUtils;
import edu.sjtu.trajectoryminer.utils.StringUtils;

public class TraClusTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File originFile = new File("D:\\Research\\Car\\Trajectory Data\\ip2420150301.txt");
		File traFile = new File("D:\\Research\\Car\\exp\\traclus\\ip2420150301.tra");
		File userIdIndexMap = new File("D:\\Research\\Car\\exp\\traclus\\ip2420150301.map");
		//generateTrajectoryFile(originFile, traFile, userIdIndexMap);
		TraClusConfig config = new TraClusConfig();
		config.setClusterFileName("D:\\Research\\Car\\exp\\traclus\\ip2420150301.cluster");
		config.setTraFileName("D:\\Research\\Car\\exp\\traclus\\ip2420150301.tra");
		TraClusClustering tcc = new TraClusClustering(config);
		tcc.process();
	}

	public static void generateTrajectoryFile(File originFile, File traFile, File userIdIndexMap) {
		String data = FileUtils.readFile(originFile);
		// BufferedWriter writer = FileUtils.getBufferedWriter(new File(
		// "D:\\Research\\Car\\poi\\" + originFile.getName() + ".poi"));
		Map<String, JSONArray> map = (Map<String, JSONArray>) JSON.parse(data);
		System.out.println(map.size());
		Map<String, List<Point>> userTrajectoryMap = new HashMap<String, List<Point>>();
		for (Map.Entry<String, JSONArray> entry : map.entrySet()) {
			Map<Date, String[]> trajectory = new HashMap<Date, String[]>();
			for (int i = 0; i < entry.getValue().size(); i++) {
				// System.out.println(entry.getValue().get(i));
				JSONArray timegeo = (JSONArray) entry.getValue().get(i);
				String time = (String) timegeo.get(0);
				// System.out.println(time);
				JSONArray geo = (JSONArray) timegeo.get(1);
				String lng = (String) geo.get(0);
				String lat = (String) geo.get(1);
				if(StringUtils.isBlank(lng) || StringUtils.isBlank(lat))
					continue;
				if(Double.parseDouble(lng)==0.0d || Double.parseDouble(lat)==0.0d)
					continue;
				Date date = DateUtils.strToDate(time, "yyyy-MM-dd HH:mm:ss");
				String[] arr = new String[2];
				arr[0] = lng;
				arr[1] = lat;
				trajectory.put(date, arr);
			}
			trajectory = sortMapByKey(trajectory);
			if(trajectory==null)
				continue;
			List<Point> trajectoryPoints = new ArrayList<Point>();
			for(Map.Entry<Date, String[]> entry1 : trajectory.entrySet()){
				Point point = new Point(entry1.getValue().length);
				for(int i=0; i<entry1.getValue().length; i++){
					Double v = Double.parseDouble(entry1.getValue()[i]);
					point.setCoordinate(i, v);
				}			
				trajectoryPoints.add(point);
			}
			userTrajectoryMap.put(entry.getKey(), trajectoryPoints);
		}
		
		BufferedWriter writer1 = FileUtils.getBufferedWriter(traFile);
		BufferedWriter writer2 = FileUtils.getBufferedWriter(userIdIndexMap);
		Integer index = 0;
		try{
			for(Map.Entry<String, List<Point>> entry : userTrajectoryMap.entrySet()){
				writer2.write(index+" "+entry.getKey()+"\n");
				writer1.write(index.toString());
				for(Point point : entry.getValue()){
					writer1.write(" ");
					writer1.write(pointToStr(point));
					//writer1.flush();
				}
				writer1.write("\n");
				index++;
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			FileUtils.closeWriter(writer1);
			FileUtils.closeWriter(writer2);
		}
		
	}

	public static String pointToStr(Point point) {
		String ret = "";
		for (Double v : point.getCoordinate()) {
			ret += v + ",";
		}
		if(ret.endsWith(","))
			ret = ret.substring(0, ret.length()-1);
		return ret.trim();
	}

	public static Map<Date, String[]> sortMapByKey(Map<Date, String[]> oriMap) {
		if (oriMap == null || oriMap.isEmpty()) {
			return null;
		}
		Map<Date, String[]> sortedMap = new TreeMap<Date, String[]>(
				new Comparator<Date>() {
					public int compare(Date key1, Date key2) {
						return key1.compareTo(key2);
					}
				});
		sortedMap.putAll(oriMap);
		return sortedMap;
	}
}
