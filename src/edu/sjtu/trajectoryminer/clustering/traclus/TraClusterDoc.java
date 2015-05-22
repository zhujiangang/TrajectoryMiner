package edu.sjtu.trajectoryminer.clustering.traclus;

import java.util.List;

import edu.sjtu.trajectoryminer.Trajectory;
import edu.sjtu.trajectoryminer.clustering.Cluster;

public class TraClusterDoc {
	private int nDimensions;
	private int nTrajectories;
	private int nClusters;
	private double clusterRatio;
	private int maxNPoints;
	private List<Trajectory> trajectoryList;
	private List<Cluster> clusterList;
}
