package edu.sjtu.trajectoryminer.pattern.swarm;

public class Cluster {

	private int n;
	private int m;
	private int[][] cluster_ot;
	private int[][] object_num;
	// 不同时刻的聚类数
	private int[] cluster_num;

	public Cluster(int nObject, int nTime) {
		n = nObject;
		m = nTime;
		cluster_num = new int[m];
		object_num = new int[m][];
		cluster_ot = new int[n][];
		for (int i = 0; i < n; i++)
			cluster_ot[i] = new int[m];
	}

	/**
	 * 添加tid时刻的聚类数n
	 * 
	 * @param tid
	 * @param n
	 */
	public void setClusterNum(int tid, int n) {
		cluster_num[tid] = n;
		object_num[tid] = new int[n];
	}

	/**
	 * 枚举所有的object和时刻，任意一个object在任意一个时刻的cluster，
	 * 然后看这个时刻下这个cluster的object数目是不是小于最小的，如果小于则置为-1
	 * 
	 * @param min_o
	 */
	public void deleteSmallCluster(int min_o) {
		for (int oid = 0; oid < n; oid++) {
			for (int tid = 0; tid < m; tid++) {
				int cid = cluster_ot[oid][tid];
				if ((cid >= 0) && (object_num[tid][cid] < min_o)) {
					cluster_ot[oid][tid] = -1;
				}
			}
		}
	}

	/**
	 * 添加oid在tid时刻所在的cluster为cid，并且将cid在tid时刻的数目+1
	 * 
	 * @param oid
	 * @param tid
	 * @param cid
	 */
	public void addClusterPoint(int oid, int tid, int cid) {
		cluster_ot[oid][tid] = cid;
		if (cid >= 0)
			object_num[tid][cid]++;
	}

	public int getClusterID(int oid, int tid) {
		return cluster_ot[oid][tid];
	}

	public int getObjectNumInCluster(int tid, int cid) {
		return object_num[tid][cid];
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public int getM() {
		return m;
	}

	public void setM(int m) {
		this.m = m;
	}

}
