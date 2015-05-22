package edu.sjtu.trajectoryminer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Trajectory {
	private int trajectoryId;// the identifier of this trajectory
	private int nDimensions;// the dimensionality of this trajectory
	private int nPoints;// the number of points constituting a trajectory 

	private List<Point> pointList;// the array of the trajectory points

	private int nPartitionPoints;		// the number of partition points in a trajectory
	private List<Point> partitionPointList;	// the array of the partition points
	
	public Trajectory() {
		super();
		trajectoryId = -1;
		nDimensions = 2;
		nPoints = 0;
		nPartitionPoints = 0;
	}

	public Trajectory(int clusterId, int nDimensions) {
		super();
		this.trajectoryId = clusterId;
		this.nDimensions = nDimensions;
		nPoints = 0;
		nPartitionPoints = 0;
	}

	public void addPoint(Point point) {
		pointList.add(point);
		nPoints = pointList.size();
	}

	public void addPartitionPoint(Point point){
		partitionPointList.add(point);
		nPartitionPoints = partitionPointList.size();
	}
	
	public int getTrajectoryId() {
		return trajectoryId;
	}

	public void setTrajectoryId(int trajectoryId) {
		this.trajectoryId = trajectoryId;
	}

	public int getnDimensions() {
		return nDimensions;
	}

	public void setnDimensions(int nDimensions) {
		this.nDimensions = nDimensions;
	}

	public int getnPoints() {
		return nPoints;
	}

	public void setnPoints(int nPoints) {
		this.nPoints = nPoints;
	}

	public List<Point> getPointList() {
		return pointList;
	}

	public void setPointList(List<Point> pointList) {
		this.pointList = pointList;
	}

	public int getnPartitionPoints() {
		return nPartitionPoints;
	}

	public void setnPartitionPoints(int nPartitionPoints) {
		this.nPartitionPoints = nPartitionPoints;
	}

	public List<Point> getPartitionPointList() {
		return partitionPointList;
	}

	public void setPartitionPointList(List<Point> partitionPointList) {
		this.partitionPointList = partitionPointList;
	}
}
