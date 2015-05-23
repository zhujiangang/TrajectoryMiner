package edu.sjtu.trajectoryminer.clustering.traclus;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.sjtu.trajectoryminer.Point;

public class LineSegmentCluster {
	public int lineSegmentClusterId;
	public int nLineSegments;
	public Point avgDirectionVector;
	public int nDimensions;
	public double cosTheta;
	public double sinTheta;
	public ArrayDeque<CandidateClusterPoint> candidatePointList;
	public int nClusterPoints;
	public List<Point> clusterPointArray;
	public int nTrajectories;
	public Set<Integer> trajectoryIdSet;
	public boolean enabled;
	

	public LineSegmentCluster(int nDimensions) {
		super();
		this.nDimensions = nDimensions;
		avgDirectionVector = new Point(nDimensions);
		candidatePointList = new ArrayDeque<CandidateClusterPoint>();
		clusterPointArray = new ArrayList<Point>();
		trajectoryIdSet = new HashSet<Integer>();
	}
}
