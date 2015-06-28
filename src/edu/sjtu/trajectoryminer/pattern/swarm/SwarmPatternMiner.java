package edu.sjtu.trajectoryminer.pattern.swarm;

import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.sjtu.trajectoryminer.utils.FileUtils;

public class SwarmPatternMiner {

	private String output_filename;
	private int swarms_n;
	private int avg_o, avg_t;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @param clusterFile
	 * @param cluster
	 * @param max_n
	 * @param max_m
	 */
	public void readFile(File clusterFile, Cluster cluster, int max_n, int max_m) {
		List<String> lines = FileUtils.readLine(clusterFile);
		int n, m;
		// 第一行是所有的object数和时刻数
		String[] arr = lines.get(0).split("\\s+");
		n = Integer.parseInt(arr[0]);
		m = Integer.parseInt(arr[1]);
		if (max_n != -1)
			cluster = new Cluster(max_n, max_m);
		else
			cluster = new Cluster(n, m);

		System.out.println("Reading...");
		// 接下来的每一行都是一个时刻
		for (int i = 0; i < m; i++) {
			int tid, n_cluster;
			if ((max_m != -1) && (i >= max_m))
				break;
			// 每一行，一开始是时刻的id，然后是这个时刻有多少个聚类
			String line = lines.get(i + 1);
			arr = line.split("\t");
			String[] arr1 = arr[0].split("\\s+");
			tid = Integer.parseInt(arr1[0]);
			n_cluster = Integer.parseInt(arr1[1]);
			if (tid % 100 == 0)
				System.out.println(tid);
			cluster.setClusterNum(tid, n_cluster);
			// 接下来是n个对象
			for (int j = 0; j < n; j++) {
				int oid, cluster_id;
				int x, y;
				arr1 = arr[j + 1].split(",");
				oid = Integer.parseInt(arr1[0]);
				cluster_id = Integer.parseInt(arr1[1]);
				x = Integer.parseInt(arr1[2]);
				y = Integer.parseInt(arr1[3]);
				if ((max_n == -1) || (oid < max_n))
					cluster.addClusterPoint(oid, tid, cluster_id);
			}
		}
	}

	public void printClosedSwarms(List<Integer> objectset, List<Integer> timeset) {
		BufferedWriter writer = FileUtils.getBufferedWriter(new File(
				output_filename));

		try {
			writer.write("#" + swarms_n + "\r\n");

			// O
			writer.write(objectset.size() + "\t");
			for (int i = 0; i < objectset.size(); i++) {
				writer.write(objectset.get(i) + "\t");
			}
			// T
			writer.write(timeset.size() + "\t");
			for (int i = 0; i < timeset.size(); i++) {
				writer.write(timeset.get(i) + "\t");
			}
			writer.write("\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			FileUtils.closeWriter(writer);
		}
	}

	public void generateNewTimeSet(int oid, Cluster cluster,
			List<Integer> timeset, List<Integer> timeset_new, int o) {
		timeset_new.clear();
		for (int i = 0; i < timeset.size(); i++) {
			int tid = timeset.get(i);
			if (cluster.getClusterID(oid, tid) == cluster.getClusterID(o, tid)) {
				timeset_new.add(tid);
			}
		}
	}

	public boolean closureCheck(Cluster cluster, List<Integer> objectset,
			List<Integer> timeset) {
		int oid = 0, p = 0;
		int o = objectset.get(0);
		while (p < (int) objectset.size()) {
			while (oid < objectset.get(p)) {
				boolean sameSize = true;
				for (int i = 0; i < timeset.size(); i++) {
					int tid = timeset.get(i);
					if (cluster.getClusterID(oid, tid) != cluster.getClusterID(
							o, tid)) {
						sameSize = false;
						break;
					}
				}
				// add oid that is not in objectset, and the timeset does not
				// decrease by
				// adding this oid, (O,T) is not closed
				if (sameSize)
					return false;
				oid++;
			}
			p++;
			oid++;
		}
		return true;
	}

	public void objectGrowth(int last_oid, int min_o, int min_t,
			Cluster cluster, List<Integer> objectset, List<Integer> timeset) {
		// check min_t condition
		if (timeset.size() < min_t)
			return;
		// check min_o condition
		int n = cluster.getN();
		if (objectset.size() + (n - last_oid - 1) < min_o)
			return;
		// check closure by oid
		if (!closureCheck(cluster, objectset, timeset))
			return;

		boolean closure = true;
		for (int oid = last_oid + 1; oid < n; oid++) {
			// objectset_new = objectset + {oid}
			// timeset_new = TIME(objectset_new)
			List<Integer> timeset_new = new ArrayList<Integer>();
			generateNewTimeSet(oid, cluster, timeset, timeset_new,
					objectset.get(0));
			// closure check
			if (timeset_new.size() == timeset.size())
				closure = false;

			// next step
			objectset.add(oid);
			objectGrowth(oid, min_o, min_t, cluster, objectset, timeset_new);
			objectset.remove(objectset.size()-1);
		}
		if ((closure) && ((int) objectset.size() >= min_o)) {
			printClosedSwarms(objectset, timeset);
			swarms_n++;
			avg_o += objectset.size();
			avg_t += timeset.size();
		}
	}

	public void calcSwarms(int min_o, int min_t, Cluster cluster) {

		int n = cluster.getN();
		int m = cluster.getM();
		
		for (int oid = 0; oid < n; oid++) {
			List<Integer> timeset = new ArrayList<Integer>();
			List<Integer> objectset = new ArrayList<Integer>();
			//找出这个object所有出现在聚类中的时刻
			for (int tid = 0; tid < m; tid++) {
				int cid = cluster.getClusterID(oid, tid);
				if (cid >= 0)
					timeset.add(tid);
			}
			objectset.add(oid);
			objectGrowth(oid, min_o, min_t, cluster, objectset, timeset);
		}

		int avgO, avgT;
		if (swarms_n == 0) {
			avgO = 0;
			avgT = 0;
		} else {
			avgO = (int) avg_o / swarms_n;
			avgT = (int) avg_t / swarms_n;
		}
	}
}
