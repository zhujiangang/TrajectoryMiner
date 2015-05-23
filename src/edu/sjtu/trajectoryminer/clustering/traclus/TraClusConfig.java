package edu.sjtu.trajectoryminer.clustering.traclus;

public class TraClusConfig {

	public static final double CONNECTABLE_DISTANCE = 5.0;

	public static final int MDL_COST_ADVANTAGE = 25;

	public static final double MIN_LINESEGMENT_LENGTH = 50.0;

	public static final int UNCLASSIFIED = -2;
	public static final int NOISE = -1;

	private int nDimensions;
	private int nClusters;
	private double clusterRatio;

	private String traFileName;
	private String clusterFileName;
	
	private double eps;
	private int minLen;

	public TraClusConfig() {
		super();
		nDimensions = 2;
		traFileName = "";
		clusterFileName = "";
		eps = 0.5;
		minLen = 2;
	}

	public double getEps() {
		return eps;
	}

	public void setEps(double eps) {
		this.eps = eps;
	}

	public int getMinLen() {
		return minLen;
	}

	public void setMinLen(int minLen) {
		this.minLen = minLen;
	}

	public int getnDimensions() {
		return nDimensions;
	}

	public void setnDimensions(int nDimensions) {
		this.nDimensions = nDimensions;
	}

	public int getnClusters() {
		return nClusters;
	}

	public void setnClusters(int nClusters) {
		this.nClusters = nClusters;
	}

	public double getClusterRatio() {
		return clusterRatio;
	}

	public void setClusterRatio(double clusterRatio) {
		this.clusterRatio = clusterRatio;
	}

	public String getTraFileName() {
		return traFileName;
	}

	public void setTraFileName(String traFileName) {
		this.traFileName = traFileName;
	}

	public String getClusterFileName() {
		return clusterFileName;
	}

	public void setClusterFileName(String clusterFileName) {
		this.clusterFileName = clusterFileName;
	}
}
