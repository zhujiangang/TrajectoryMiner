package edu.sjtu.trajectoryminer.clustering.traclus;

import edu.sjtu.trajectoryminer.Point;

/**
 * 
 * @author Zhu
 *
 */
public class LineSegment {
	int trajectoryId; //the trajectory that the line segment belongs to
	int order;
	int lingSegmentId;
	Point startPoint;
	Point endPoint;
	
	public LineSegment() {
		super();
		startPoint = new Point();
		endPoint = new Point();
	}
	
	
}
