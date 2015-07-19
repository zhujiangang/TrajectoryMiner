package edu.sjtu.trajectoryminer.pattern.swarm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.sjtu.easyml.Instance;
import edu.sjtu.easyml.clustering.Cluster;
import edu.sjtu.easyml.clustering.DBSCAN;
import edu.sjtu.trajectoryminer.utils.DateUtils;
import edu.sjtu.trajectoryminer.utils.FileUtils;

public class SwarmDataGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwarmDataGenerator sdg = new SwarmDataGenerator();
		sdg.loadTrajectoryData();
		//sdg.generateByTime(new File(
		//		"D:\\Research\\Car\\swarm\\swarmTrajectory\\ip2420150301_time.txt"));
		sdg.generate(new File(
				"D:\\Research\\Car\\swarm\\swarmTrajectory\\ip2420150301_cluster.txt"));
	}

	private String trajectoryFile = "D:\\Research\\Car\\swarm\\swarmTrajectory\\ip2420150301.txt";
	private Map<String, Map<Date, String[]>> trajectoryById;
	private Set<Date> allTimes;
	private double eps = 0.08;
	private int minPts = 1;

	public void loadTrajectoryData() {
		List<String> lines = FileUtils.readLine(new File(trajectoryFile));
		trajectoryById = new HashMap<String, Map<Date, String[]>>();
		allTimes = new HashSet<Date>();

		for (String line : lines) {
			String[] arr = line.split("\\$\\$");
			String id = arr[0].trim();
			arr = arr[1].split("\t");
			Map<Date, String[]> trajectory = new HashMap<Date, String[]>();
			for (String str : arr) {
				String[] arr1 = str.split("-_-");
				Date date = DateUtils.strToDate(arr1[0], "yyyy-MM-dd HH:mm:ss");

				allTimes.add(date);

				String[] pos = new String[2];
				pos[0] = arr1[1];
				pos[1] = arr1[2];
				trajectory.put(date, pos);
			}
			trajectoryById.put(id, trajectory);
		}
	}

	public void generateSwarmData(File clusterFile,File outFile,File objectIdFile){
		List<String> lines = FileUtils.readLine(clusterFile);
		int tid = 0;
		Set<String> allObject = new HashSet<String>();
		for(String line : lines){
			String[] arr = line.split("-_-");
			for(String str : arr){
				String[] arr1 = str.split(":");
				allObject.add(arr1[0]);
			}
			tid++;
		}
		BufferedWriter writer = FileUtils.getBufferedWriter(outFile);
		try{
			writer.write(allObject.size()+" "+tid+"\n");
			tid = 0;
			for(String line : lines){
				String[] arr = line.split("-_-");
				for(String str : arr){
					String[] arr1 = str.split(":");
					allObject.add(arr1[0]);
				}
				tid++;
			}
		}catch(Exception e){
			
		}finally{
			FileUtils.closeWriter(writer);
		}
	}
	public void generate(File outFile) {
		List<String> lines = FileUtils.readLine(new File(
				"D:\\Research\\Car\\swarm\\swarmTrajectory\\ip2420150301_time.txt"));
		BufferedWriter writer = FileUtils.getBufferedWriter(outFile);
		try{
			for (String line : lines) {
				String[] arr = line.split("\\$\\$");
				String time = arr[0].trim();
				arr = arr[1].split("\t");
				List<Instance> points = new ArrayList<Instance>();
				for (String str : arr) {
					String[] arr1 = str.split("-_-");
					String id = arr1[0];

					double[] pos = new double[2];
					pos[0] = Double.parseDouble(arr1[1]);
					pos[1] = Double.parseDouble(arr1[2]);
					Instance instance = new Instance(-1, pos);
					instance.setId(id);
					points.add(instance);
				}
				DBSCAN dbscan = new DBSCAN(eps, minPts);
				List<Cluster> clusters = dbscan.cluster(points);
				String clusterRes = "";
				for(int i=0; i<clusters.size(); i++){
					Cluster cluster = clusters.get(i);
					Collections.sort(cluster.getPoints());
					for(Instance instance : cluster.getPoints()){
						clusterRes += instance.getId()+":"+i+"\t";
					}
				}
				writer.write(time+"-_-"+clusterRes+"\n");
			}
		}catch(Exception e){
			
		}finally{
			FileUtils.closeWriter(writer);
		}
		
	}

	public void generateByTime(File outFile) {
		BufferedWriter writer = FileUtils.getBufferedWriter(outFile);
		List<Date> timeList = new ArrayList<Date>(allTimes);
		Collections.sort(timeList);
		try {
			for (Date date : timeList) {
				String timestamp = "";
				for (Map.Entry<String, Map<Date, String[]>> entry : trajectoryById
						.entrySet()) {
					if (entry.getValue().keySet().contains(date)) {
						timestamp += entry.getKey() + "-_-"
								+ entry.getValue().get(date)[0] + "-_-"
								+ entry.getValue().get(date)[1] + "\t";
					}
				}
				writer.write(DateUtils.dateToStr(date) + "$$" + timestamp
						+ "\n");
			}
		} catch (Exception e) {

		} finally {
			FileUtils.closeWriter(writer);
		}
	}
}
