package edu.sjtu.trajectoryminer.clustering.traclus;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import edu.sjtu.trajectoryminer.Point;

public class LineSegmentCluster {
	public int lineSegmentClusterId;
	public int nLineSegments;
	public Point avgDirectionVector;
	public double cosTheta;
	public double sinTheta;
	public ArrayDeque<CandidateClusterPoint> candidatePointList;
	public int nClusterPoints;
	public List<Point> clusterPointArray;
	public int nTrajectories;
	public List<Integer> trajectoryIdList;
	public boolean enabled;
	

	public LineSegmentCluster() {
		super();
		avgDirectionVector = new Point();
		candidatePointList = new ArrayDeque<CandidateClusterPoint>();
		clusterPointArray = new ArrayList<Point>();
		trajectoryIdList = new ArrayList<Integer>();
	}
}
