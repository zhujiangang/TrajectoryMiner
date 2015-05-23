package edu.sjtu.trajectoryminer.clustering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.sjtu.trajectoryminer.Point;
import edu.sjtu.trajectoryminer.utils.FileUtils;

public class Cluster {
	private int clusterId;// the identifier of this cluster
	private int nDimensions;// the dimensionality of this cluster
	private int nTrajectories;// the number of trajectories belonging to this
								// cluster
	private int nPoints;// the number of points constituting a cluster

	private List<Point> pointList;// the array of the cluster points

	public Cluster() {
		super();
		clusterId = -1;
		nDimensions = 2;
		nTrajectories = 0;
		nPoints = 0;
	}

	public Cluster(int clusterId, int nDimensions) {
		super();
		this.clusterId = clusterId;
		this.nDimensions = nDimensions;
		nTrajectories = 0;
		nPoints = 0;
	}

	public void addPoint(Point point) {
		pointList.add(point);
		nPoints = pointList.size();
	}

	public void writeCluster(BufferedWriter writer) throws IOException {
		// BufferedWriter writer = FileUtils.getBufferedWriter(outfile);
		writer.write(clusterId + " " + nPoints + " ");
		for (Point point : pointList) {
			writer.write(point.getCoordinate(0) + " " + point.getCoordinate(1)
					+ " ");
		}
		writer.write("\n");
	}

	public int getClusterId() {
		return clusterId;
	}

	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
	}

	public int getnDimensions() {
		return nDimensions;
	}

	public void setnDimensions(int nDimensions) {
		this.nDimensions = nDimensions;
	}

	public int getnTrajectories() {
		return nTrajectories;
	}

	public void setnTrajectories(int nTrajectories) {
		this.nTrajectories = nTrajectories;
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
}
