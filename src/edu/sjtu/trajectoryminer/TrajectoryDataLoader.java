package edu.sjtu.trajectoryminer;

import java.io.BufferedWriter;
import java.io.File;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import edu.sjtu.trajectoryminer.utils.DateUtils;
import edu.sjtu.trajectoryminer.utils.FileUtils;

public class TrajectoryDataLoader {

	private String dataDir = "D:\\Research\\Car\\Trajectory Data";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File swarmId = new File("D:\\Research\\Car\\swarm\\石家庄VIN.txt");
		List<String> idList = FileUtils.readLine(swarmId);
		String outFileDir = "D:\\Research\\Car\\swarm\\swarmTrajectory";
		TrajectoryDataLoader tdl = new TrajectoryDataLoader();
		File traFile = new File("D:\\Research\\Car\\Trajectory Data\\ip2420150301.txt");
		tdl.extract(traFile,new HashSet<String>(idList), outFileDir);
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

	private String trajectory2String(Map<Date, String[]> trajectory, Set<Date> times){
		String traStr = "";
		for(Map.Entry<Date, String[]> entry : trajectory.entrySet()){
			if(!times.contains(entry.getKey()))
				continue;
			if(DateUtils.getHours(entry.getKey())<9)
				continue;
			traStr += DateUtils.dateToStr(entry.getKey(), "yyyy-MM-dd HH:mm:ss")+"-_-"+entry.getValue()[0]+"-_-"+entry.getValue()[1]+"\t";
		}
		return traStr.trim();
	}
	public void extract(File traFile, Set<String> ids, String outFileDir){
		System.out.println(traFile.getName());
		String data = FileUtils.readFile(traFile);
		BufferedWriter writer = FileUtils.getBufferedWriter(new File(outFileDir+"/"+traFile.getName()+".9"));

		Map<String, JSONArray> map = (Map<String, JSONArray>) JSON
				.parse(data);
		System.out.println(map.size());
		try {
			Map<String,Map<Date,String[]>> trajectoryById = new HashMap<String, Map<Date,String[]>>();
			Set<Date> allTimes = new HashSet<Date>();
			for (Map.Entry<String, JSONArray> entry : map.entrySet()) {
				if(!ids.contains(entry.getKey()))
					continue;
					//writer.write(entry.getKey() + "\n");
				Map<Date, String[]> trajectory = new HashMap<Date, String[]>();
				boolean flag = false;
				for (int i = 0; i < entry.getValue().size(); i++) {
					// System.out.println(entry.getValue().get(i));
					JSONArray timegeo = (JSONArray) entry.getValue().get(i);
					String time = (String) timegeo.get(0);
					// System.out.println(time);
					JSONArray geo = (JSONArray) timegeo.get(1);
					String lng = (String) geo.get(0);
					String lat = (String) geo.get(1);
					
					/***********对时间做修改，全部归到0，5两档****************/
					String[] timeArr = time.split(":");
					int second1 = Integer.parseInt(timeArr[timeArr.length-1].substring(1, 2));
					int second2 = Integer.parseInt(timeArr[timeArr.length-1].substring(0, 1));
					if(second1<5)
						time = time.substring(0, time.length()-2)+second2+"0";
					else
						time = time.substring(0, time.length()-2)+(second2+1)+"0";
											
					Date date = DateUtils.strToDate(time,
							"yyyy-MM-dd HH:mm:ss");
					int month = DateUtils.getMonths(date);
					if(month!=3){
						flag = true;
						break;
					}
					String[] arr = new String[2];
					arr[0] = lng;
					arr[1] = lat;
					trajectory.put(date, arr);
					
					allTimes.add(date);
				}
				if(flag)
					continue;
				trajectory = sortMapByKey(trajectory);
				/********************只要其实为0点的车**************************/
//				flag = false;
//				for(Map.Entry<Date, String[]> entry1 : trajectory.entrySet()){
//					Date date = entry1.getKey();
//					int hour = DateUtils.getHours(date);
//					if(hour!=0){
//						flag = true;
//					}
//					break;	
//				}
//				if(flag)
//					continue;
				trajectoryById.put(entry.getKey(), trajectory);
			}
			Set<Date> timesDel = new HashSet<Date>();
			for(Map.Entry<String, Map<Date,String[]>> entry : trajectoryById.entrySet()){
				for(Date date : allTimes){
					if(!entry.getValue().keySet().contains(date))
						timesDel.add(date);
				}
			}
			
			//allTimes.removeAll(timesDel);
			
			for(Map.Entry<String, Map<Date,String[]>> entry : trajectoryById.entrySet()){
				writer.write(entry.getKey()+"$$"+trajectory2String(entry.getValue(), allTimes)+"\n");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			FileUtils.closeWriter(writer);
		}
	}
	public void extract(Set<String> ids, String outFileDir) {
		File[] files = FileUtils.getFiles(dataDir);
		for (File file : files) {
			extract(file, ids, outFileDir);
		}
	}
}
