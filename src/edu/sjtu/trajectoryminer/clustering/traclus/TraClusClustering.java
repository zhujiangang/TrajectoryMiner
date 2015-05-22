package edu.sjtu.trajectoryminer.clustering.traclus;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import edu.sjtu.trajectoryminer.Point;
import edu.sjtu.trajectoryminer.Trajectory;
import edu.sjtu.trajectoryminer.clustering.Cluster;
import edu.sjtu.trajectoryminer.utils.CommonUtils;
import edu.sjtu.trajectoryminer.utils.FileUtils;
import edu.sjtu.trajectoryminer.utils.MathUtils;
import edu.sjtu.trajectoryminer.utils.VectorUtils;

public class TraClusClustering {

	private List<Trajectory> trajectoryList;
	private List<Cluster> clusterList;
	private TraClusConfig config;

	private int maxNPoints;// max number of points in a trajectory
	private int nTotalPoints;// the number of all points in all trajectories

	private double epsParam;
	private int minLnsParam;
	private int nTotalLineSegments;
	private int currComponentId;
	// the number of dense components discovered until now
	private int[] componentIdArray;
	// the list of line segment clusters
	private LineSegmentCluster[] lineSegmentClusters;
	// programming trick: avoid frequent execution of the new and delete
	// operations
	private Point vector1;
	private Point vector2;
	private Point projectionPoint;
	private Point startPoint1, endPoint1, startPoint2, endPoint2;

	private List<LineSegmentId> idArray;
	private List<Point> lineSegmentPointArray;
	private double coefficient;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Point s = new Point(2);
		Point e = new Point(2);
		Point p = new Point(2);
		
		s.setCoordinate(0, 5);
		s.setCoordinate(1,6);
		
		e.setCoordinate(0, 7);
		e.setCoordinate(1, 9);
		
		p.setCoordinate(0, 1);
		p.setCoordinate(1, 10);
		
		TraClusClustering tcc = new TraClusClustering(null);
		System.out.println(tcc.measureDistanceFromPointToLineSegment(s, e, p));
		System.out.println(tcc.coefficient);
		System.out.println(tcc.measureAngleDisntance(s, e, s, p));
	}

	public TraClusClustering(TraClusConfig config) {
		super();
		this.config = config;
		trajectoryList = new ArrayList<Trajectory>();
		clusterList = new ArrayList<Cluster>();

		idArray = new ArrayList<LineSegmentId>();
		lineSegmentPointArray = new ArrayList<Point>();

		vector1 = new Point();
		vector2 = new Point();
		projectionPoint = new Point();
		startPoint1 = new Point();
		startPoint2 = new Point();
		endPoint1 = new Point();
		endPoint2 = new Point();
	}

	/**
	 * read trajectories from file each line is a trajectory id x1,x2,...,xn
	 * x1,x2,...,xn id is the identifier of each trajectory. n is the dimension
	 * 
	 * @return
	 */
	public boolean loadTrajectory() {
		List<String> lines = FileUtils.readLine(new File(config
				.getTraFileName()));
		this.maxNPoints = Integer.MIN_VALUE;
		this.nTotalPoints = 0;

		for (String line : lines) {
			String[] arr = line.split("\\s+");
			Integer id = Integer.parseInt(arr[0]);
			Trajectory trajectory = new Trajectory(id, config.getnDimensions());
			int nPoints = arr.length - 1;
			if (nPoints > maxNPoints)
				maxNPoints = nPoints;
			nTotalPoints += nPoints;
			if (nPoints <= 0) {
				System.err
						.println("At least one point should be included in a trajectory!");
				return false;
			}
			for (int i = 1; i < arr.length; i++) {
				Point point = new Point(config.getnDimensions());
				String[] arr1 = arr[i].split(",");
				if (arr1.length != config.getnDimensions()) {
					System.err
							.println("The number of dimensions of this point is not the same as the nDimemsion given in config file!");
					return false;
				}
				for (int j = 0; j < arr1.length; j++) {
					point.setCoordinate(j, Double.parseDouble(arr1[j]));
				}
				trajectory.addPoint(point);
			}
			trajectoryList.add(trajectory);
		}
		return true;
	}

	/**
	 * Step1 : Trajectory Partitioning
	 * 
	 * @return
	 */
	public boolean partitionTrajectory() {
		for (Trajectory trajectory : trajectoryList) {
			findOptimalPartition(trajectory);
		}
		if (!storeClusterComponentIntoIndex())
			return false;
		return true;
	}

	private boolean storeClusterComponentIntoIndex() {
		// int nDimensions = m_document.m_nDimensions;
		Point startPoint;
		Point endPoint;

		nTotalLineSegments = 0;

		for (Trajectory trajectory : trajectoryList) {
			for (int i = 0; i < trajectory.getnPartitionPoints() - 1; i++) {
				// convert an n-dimensional line segment into a 2n-dimensional
				// point
				// i.e., the first n-dimension: the start point
				// the last n-dimension: the end point
				startPoint = trajectory.getPartitionPointList().get(i);
				endPoint = trajectory.getPartitionPointList().get(i + 1);

				// the line segment is too short
				if (MathUtils.computeEuclideanDistance(
						startPoint.getCoordinate(), endPoint.getCoordinate()) < TraClusConfig.MIN_LINESEGMENT_LENGTH)
					continue;

				nTotalLineSegments++;
				int nDimensions = config.getnDimensions();
				// merge start point and end point into one single point
				Point lineSegmentPoint = new Point(nDimensions * 2);
				for (int j = 0; j < nDimensions; j++) {
					lineSegmentPoint.setCoordinate(j,
							startPoint.getCoordinate(j));
					lineSegmentPoint.setCoordinate(nDimensions + j,
							endPoint.getCoordinate(j));
				}

				LineSegmentId id = new LineSegmentId();
				id.trajectoryId = trajectory.getTrajectoryId();
				id.order = i;

				idArray.add(id);
				lineSegmentPointArray.add(lineSegmentPoint);
			}
		}

		return true;
	}

	/**
	 * Find the optimal partition of a trajectory
	 * 
	 * @param trajectory
	 */
	public void findOptimalPartition(Trajectory trajectory) {
		int nPoints = trajectory.getnPoints();
		int startIndex = 0, length;
		int fullPartitionMDLCost, partialPartitionMDLCost;

		// add the start point of a trajectory
		trajectory.addPartitionPoint(trajectory.getPointList().get(0));

		while (true) {
			fullPartitionMDLCost = partialPartitionMDLCost = 0;

			for (length = 1; startIndex + length < nPoints; length++) {
				// compute the total length of a trajectory
				fullPartitionMDLCost += computeModelCost(trajectory, startIndex
						+ length - 1, startIndex + length);

				// compute the sum of (1) the length of a cluster component and
				// (2) the perpendicular and angle distances
				partialPartitionMDLCost = computeModelCost(trajectory,
						startIndex, startIndex + length)
						+ computeEncodingCost(trajectory, startIndex,
								startIndex + length);

				if (fullPartitionMDLCost + TraClusConfig.MDL_COST_ADVANTAGE < partialPartitionMDLCost) {
					trajectory.addPartitionPoint(trajectory.getPointList().get(
							startIndex + length - 1));
					startIndex = startIndex + length - 1;
					length = 0;
					break;
				}
			}

			// if we reach at the end of a trajectory
			if (startIndex + length >= nPoints)
				break;
		}
		// add the end point of a trajectory
		trajectory
				.addPartitionPoint(trajectory.getPointList().get(nPoints - 1));
	}

	/**
	 * Just use the Euclidean Distance between start point and end point to
	 * measure the cost of this partition
	 * 
	 * @param trajectory
	 * @param startPIndex
	 * @param endPIndex
	 * @return
	 */
	private int computeModelCost(Trajectory trajectory, int startPIndex,
			int endPIndex) {
		Point lineSegmentStart = trajectory.getPointList().get(startPIndex);
		Point lineSegmentEnd = trajectory.getPointList().get(endPIndex);

		double distance = MathUtils.computeEuclideanDistance(
				lineSegmentStart.getCoordinate(),
				lineSegmentEnd.getCoordinate());
		if (distance < 1.0)
			distance = 1.0; // to take logarithm

		return (int) Math.ceil(MathUtils.logBase2(distance));
	}

	/**
	 * the distance between the line segment and the sub-line segment 
	 * beween startPIndex and endPIndex.
	 * We use the sum of Perpendicular Distance and the Angle Disntance
	 * @param trajectory
	 * @param startPIndex
	 * @param endPIndex
	 * @return
	 */
	private int computeEncodingCost(Trajectory trajectory, int startPIndex,
			int endPIndex) {
		Point clusterComponentStart;
		Point clusterComponentEnd;
		Point lineSegmentStart;
		Point lineSegmentEnd;
		double perpendicularDistance;
		double angleDistance;
		int encodingCost = 0;

		clusterComponentStart = trajectory.getPointList().get(startPIndex);
		clusterComponentEnd = trajectory.getPointList().get(endPIndex);

		for (int i = startPIndex; i < endPIndex; i++) {
			lineSegmentStart = trajectory.getPointList().get(i);
			lineSegmentEnd = trajectory.getPointList().get(i + 1);

			perpendicularDistance = measurePerpendicularDistance(
					clusterComponentStart, clusterComponentEnd,
					lineSegmentStart, lineSegmentEnd);
			angleDistance = measureAngleDisntance(clusterComponentStart,
					clusterComponentEnd, lineSegmentStart, lineSegmentEnd);

			if (perpendicularDistance < 1.0)
				perpendicularDistance = 1.0; // to take logarithm
			if (angleDistance < 1.0)
				angleDistance = 1.0; // to take logarithm
			encodingCost += ((int) Math.ceil(MathUtils
					.logBase2(perpendicularDistance)) + (int) Math
					.ceil(MathUtils.logBase2(angleDistance)));
		}

		return encodingCost;
	}

	/**
	 * perform clustering using DBSCAN
	 * @param eps
	 * @param minLns
	 * @return
	 */
	public boolean performDBSCAN(double eps, int minLns) {
		epsParam = eps;
		minLnsParam = minLns;

		currComponentId = 0;

		//initialize the line segment with UNCLASSIFIED
		componentIdArray = new int[nTotalLineSegments];
		for (int i = 0; i < nTotalLineSegments; i++)
			componentIdArray[i] = TraClusConfig.UNCLASSIFIED;

		for (int i = 0; i < nTotalLineSegments; i++) {
			if (componentIdArray[i] == TraClusConfig.UNCLASSIFIED)
				if (expandDenseComponent(i, currComponentId, eps, minLns))
					currComponentId++;
		}

		return true;
	}

	private boolean expandDenseComponent(int index, int componentId,
			double eps, int minDensity) {
		Queue<Integer> seeds = new LinkedList<Integer>();
		Set<Integer> seedsSet = new HashSet<Integer>();
		// Queue<Integer> seedResult = new LinkedList<Integer>();
		Set<Integer> seedResultSet = new HashSet<Integer>();
		int currIndex;

		//the start point end end point are merged in lineSegmentPointArray
		//s1,s2,...,sn,e1,e2,...,en
		extractStartAndEndPoints(index, startPoint1, endPoint1);
		computeEPSNeighborhood(startPoint1, endPoint1, eps, seedsSet);

		if (seedsSet.size() < minDensity) // not a core line segment
		{
			componentIdArray[index] = TraClusConfig.NOISE;
			return false;
		} else {

			for (Integer id : seedsSet)
				componentIdArray[id] = componentId;

			seedsSet.remove(index);
			seeds.addAll(seedsSet);
			while (!seeds.isEmpty()) {
				currIndex = seeds.poll();
				seedsSet.remove(currIndex);
				extractStartAndEndPoints(currIndex, startPoint1, endPoint1);
				computeEPSNeighborhood(startPoint1, endPoint1, eps,
						seedResultSet);

				if (seedResultSet.size() >= minDensity) {
					for (Integer id : seedResultSet) {
						if (componentIdArray[id] == TraClusConfig.UNCLASSIFIED
								|| componentIdArray[id] == TraClusConfig.NOISE) {
							if (componentIdArray[id] == TraClusConfig.UNCLASSIFIED) {
								if (!seedsSet.contains(id)) {
									seeds.offer(id);
									seedsSet.add(id);
								}
							}
							componentIdArray[id] = componentId;
						}
					}
				}
			}
			return true;
		}
	}

	private void computeEPSNeighborhood(Point startPoint, Point endPoint,
			double eps, Set<Integer> result) {
		result.clear();
		startPoint2 = new Point();
		endPoint2 = new Point();
		for (int j = 0; j < nTotalLineSegments; j++) {
			extractStartAndEndPoints(j, startPoint2, endPoint2);
			double distance = computeDistanceBetweenTwoLineSegments(startPoint,
					endPoint, startPoint2, endPoint2);
			// if the distance is below the threshold, this line segment belongs
			// to the eps-neighborhood
			if (distance <= eps)
				result.add(j);
		}

		return;
	}

	private void extractStartAndEndPoints(int index, Point startPoint,
			Point endPoint) { // for speedup
		// compose the start and end points of the line segment
		for (int i = 0; i < config.getnDimensions(); i++) {
			startPoint.setCoordinate(i, lineSegmentPointArray.get(index)
					.getCoordinate(i));
			endPoint.setCoordinate(i, lineSegmentPointArray.get(index)
					.getCoordinate(config.getnDimensions() + i));
		}
	}

	private double computeDistanceBetweenTwoLineSegments(Point startPoint1,
			Point endPoint1, Point startPoint2, Point endPoint2) {
		Distance distance = new Distance();

		subComputeDistanceBetweenTwoLineSegments(startPoint1, endPoint1,
				startPoint2, endPoint2, distance);

		return (distance.perpendicularDistance + distance.parallelDistance + distance.angleDistance);
	}

	/**
	 * perpendicular distance (d1^2 + d2^2) / (d1 + d2) as the perpendicular
	 * distance
	 * 
	 * @param s1
	 * @param e1
	 * @param s2
	 * @param e2
	 * @return
	 */
	private double measurePerpendicularDistance(Point s1, Point e1, Point s2,
			Point e2) {
		// we assume that the first line segment is longer than the second one
		double distance1; // the distance from a start point to the cluster
							// component
		double distance2; // the distance from an end point to the cluster
							// component

		distance1 = measureDistanceFromPointToLineSegment(s1, e1, s2);
		distance2 = measureDistanceFromPointToLineSegment(s1, e1, e2);

		// if the first line segment is exactly the same as the second one, the
		// perpendicular distance should be zero
		if (distance1 == 0.0 && distance2 == 0.0)
			return 0.0;

		// return (d1^2 + d2^2) / (d1 + d2) as the perpendicular distance
		return ((Math.pow(distance1, 2) + Math.pow(distance2, 2)) / (distance1 + distance2));
	}

	private void subComputeDistanceBetweenTwoLineSegments(Point startPoint1,
			Point endPoint1, Point startPoint2, Point endPoint2,
			Distance distance) {
		double perDistance1, perDistance2;
		double parDistance1, parDistance2;
		double length1, length2;

		// the length of the first line segment
		length1 = MathUtils.computeEuclideanDistance(
				startPoint1.getCoordinate(), endPoint1.getCoordinate());
		// the length of the second line segment
		length2 = MathUtils.computeEuclideanDistance(
				startPoint2.getCoordinate(), endPoint2.getCoordinate());

		// compute the perpendicular distance and the parallel distance
		// START ...
		if (length1 > length2) {
			perDistance1 = measureDistanceFromPointToLineSegment(startPoint1,
					endPoint1, startPoint2);
			if (coefficient < 0.5)
				parDistance1 = MathUtils.computeEuclideanDistance(
						startPoint1.getCoordinate(),
						projectionPoint.getCoordinate());
			else
				parDistance1 = MathUtils.computeEuclideanDistance(
						endPoint1.getCoordinate(),
						projectionPoint.getCoordinate());

			perDistance2 = measureDistanceFromPointToLineSegment(startPoint1,
					endPoint1, endPoint2);
			if (coefficient < 0.5)
				parDistance2 = MathUtils.computeEuclideanDistance(
						startPoint1.getCoordinate(),
						projectionPoint.getCoordinate());
			else
				parDistance2 = MathUtils.computeEuclideanDistance(
						endPoint1.getCoordinate(),
						projectionPoint.getCoordinate());
		} else {
			perDistance1 = measureDistanceFromPointToLineSegment(startPoint2,
					endPoint2, startPoint1);
			if (coefficient < 0.5)
				parDistance1 = MathUtils.computeEuclideanDistance(
						startPoint2.getCoordinate(),
						projectionPoint.getCoordinate());
			else
				parDistance1 = MathUtils.computeEuclideanDistance(
						endPoint2.getCoordinate(),
						projectionPoint.getCoordinate());

			perDistance2 = measureDistanceFromPointToLineSegment(startPoint2,
					endPoint2, endPoint1);
			if (coefficient < 0.5)
				parDistance2 = MathUtils.computeEuclideanDistance(
						startPoint2.getCoordinate(),
						projectionPoint.getCoordinate());
			else
				parDistance2 = MathUtils.computeEuclideanDistance(
						endPoint2.getCoordinate(),
						projectionPoint.getCoordinate());
		}

		// compute the perpendicular distance; take (d1^2 + d2^2) / (d1 + d2)
		if (!(perDistance1 == 0.0 && perDistance2 == 0.0))
			distance.perpendicularDistance = ((Math.pow(perDistance1, 2) + Math
					.pow(perDistance2, 2)) / (perDistance1 + perDistance2));
		else
			distance.perpendicularDistance = 0.0d;

		// compute the parallel distance; take the minimum
		distance.parallelDistance = Math.min(parDistance1, parDistance2);
		// ... END

		// compute the angle distance
		// START ...
		// MeasureAngleDisntance() assumes that the first line segment is longer
		// than the second one
		if (length1 > length2)
			distance.angleDistance = measureAngleDisntance(startPoint1,
					endPoint1, startPoint2, endPoint2);
		else
			distance.angleDistance = measureAngleDisntance(startPoint2,
					endPoint2, startPoint1, endPoint1);
		// ... END

		return;
	}

	public boolean constructCluster() {
		// this step consists of two substeps
		// notice that the result of the previous substep is used in the
		// following substeps

		if (!constructLineSegmentCluster())
			return false;

		if (!storeLineSegmentCluster())
			return false;

		return true;
	}

	private boolean constructLineSegmentCluster() {
		int nDimensions = config.getnDimensions();

		lineSegmentClusters = new LineSegmentCluster[currComponentId];

		// initialize the list of line segment clusters
		// START ...
		for (int i = 0; i < currComponentId; i++) {
			lineSegmentClusters[i].lineSegmentClusterId = i;
			lineSegmentClusters[i].nLineSegments = 0;
			lineSegmentClusters[i].nClusterPoints = 0;
			lineSegmentClusters[i].nTrajectories = 0;
			lineSegmentClusters[i].enabled = false;
		}
		// ... END

		// accumulate the direction vector of a line segment
		for (int i = 0; i < nTotalLineSegments; i++) {
			int componentId = componentIdArray[i];
			if (componentId >= 0) {
				for (int j = 0; j < nDimensions; j++) {
					double difference = lineSegmentPointArray.get(i)
							.getCoordinate(nDimensions + j)
							- lineSegmentPointArray.get(i).getCoordinate(j);
					double currSum = lineSegmentClusters[componentId].avgDirectionVector
							.getCoordinate(j) + difference;
					lineSegmentClusters[componentId].avgDirectionVector
							.setCoordinate(j, currSum);
				}
				lineSegmentClusters[componentId].nLineSegments++;
			}
		}

		// compute the average direction vector of a line segment cluster
		// START ...
		double vectorLength1, vectorLength2, innerProduct;
		double cosTheta, sinTheta;

		vector2.setCoordinate(0, 1.0);
		vector2.setCoordinate(1, 0.0);

		for (int i = 0; i < currComponentId; i++) {
			LineSegmentCluster clusterEntry = lineSegmentClusters[i];

			for (int j = 0; j < nDimensions; j++)
				clusterEntry.avgDirectionVector.setCoordinate(j,
						clusterEntry.avgDirectionVector.getCoordinate(j)
								/ (double) clusterEntry.nLineSegments);

			vectorLength1 = VectorUtils
					.computeVectorLength(clusterEntry.avgDirectionVector
							.getCoordinate());
			vectorLength2 = 1.0;

			innerProduct = VectorUtils.computeInnerProduct(
					clusterEntry.avgDirectionVector.getCoordinate(),
					vector2.getCoordinate());
			cosTheta = innerProduct / (vectorLength1 * vectorLength2);
			if (cosTheta > 1.0)
				cosTheta = 1.0;
			if (cosTheta < -1.0)
				cosTheta = -1.0;
			sinTheta = Math.sqrt(1 - Math.pow(cosTheta, 2));

			if (clusterEntry.avgDirectionVector.getCoordinate(1) < 0)
				sinTheta = -sinTheta;

			clusterEntry.cosTheta = cosTheta;
			clusterEntry.sinTheta = sinTheta;
		}
		// ... END

		// summarize the information about line segment clusters
		// the structure for summarization is as follows
		// [lineSegmentClusterId, nClusterPoints, clusterPointArray,
		// nTrajectories, { trajectoryId, ... }]
		for (int i = 0; i < nTotalLineSegments; i++) {
			if (componentIdArray[i] >= 0) // if the componentId < 0, it is a
											// noise
				registerAndUpdateLineSegmentCluster(componentIdArray[i], i);
		}

		Set<Integer> trajectories = new HashSet<Integer>();
		for (int i = 0; i < currComponentId; i++) {
			LineSegmentCluster clusterEntry = lineSegmentClusters[i];

			// a line segment cluster must have trajectories more than the
			// minimum threshold
			if (clusterEntry.nTrajectories >= minLnsParam) {
				clusterEntry.enabled = true;

				// DEBUG: count the number of trajectories that belong to
				// clusters
				for (int j = 0; j < clusterEntry.trajectoryIdList.size(); j++)
					trajectories.add(clusterEntry.trajectoryIdList.get(j));
				// ... DEBUG

				computeRepresentativeLines(clusterEntry);
			} else {
				clusterEntry.candidatePointList.clear();
				clusterEntry.clusterPointArray.clear();
				clusterEntry.trajectoryIdList.clear();
			}
		}

		// DEBUG: compute the ratio of trajectories that belong to clusters
		config.setClusterRatio((double) trajectories.size()
				/ (double) trajectoryList.size());

		return true;
	}

	private void registerAndUpdateLineSegmentCluster(int componentId,
			int lineSegmentId) {
		LineSegmentCluster clusterEntry = lineSegmentClusters[componentId];

		// the start and end values of the first dimension (e.g., the x value in
		// the 2-dimension)
		// NOTE: this program code works only for the 2-dimensional data

		Point aLineSegment = lineSegmentPointArray.get(lineSegmentId);
		double orderingValue1 = MathUtils.getXRotation(
				aLineSegment.getCoordinate(0), aLineSegment.getCoordinate(1),
				clusterEntry.cosTheta, clusterEntry.sinTheta);
		double orderingValue2 = MathUtils.getXRotation(
				aLineSegment.getCoordinate(2), aLineSegment.getCoordinate(3),
				clusterEntry.cosTheta, clusterEntry.sinTheta);

		CandidateClusterPoint existingCandidatePoint = new CandidateClusterPoint();
		CandidateClusterPoint newCandidatePoint = new CandidateClusterPoint();
		;
		int i, j;

		// sort the line segment points by the coordinate of the first dimension
		// simply use the insertion sort algorithm
		// START ...
		Iterator<CandidateClusterPoint> iter1 = clusterEntry.candidatePointList
				.iterator();
		int index1 = 0;
		for (i = 0; i < (int) clusterEntry.candidatePointList.size(); i++) {
			existingCandidatePoint = iter1.next();
			if (existingCandidatePoint.orderingValue < orderingValue1) {
				iter1.next();
				index1++;
			} else
				break;
		}

		newCandidatePoint.orderingValue = orderingValue1;
		newCandidatePoint.lineSegmentId = lineSegmentId;
		newCandidatePoint.startPointFlag = true;
		if (i == 0)
			clusterEntry.candidatePointList.offerFirst(newCandidatePoint);
		else if (i >= (int) clusterEntry.candidatePointList.size())
			clusterEntry.candidatePointList.offerLast(newCandidatePoint);
		else {
			CandidateClusterPoint[] arrTmp = (CandidateClusterPoint[]) clusterEntry.candidatePointList
					.toArray();
			arrTmp[index1] = newCandidatePoint;
			clusterEntry.candidatePointList = new ArrayDeque<CandidateClusterPoint>(
					CommonUtils.arr2List(arrTmp));
		}

		Iterator<CandidateClusterPoint> iter2 = clusterEntry.candidatePointList
				.iterator();
		int index2 = 0;
		for (j = 0; j < (int) clusterEntry.candidatePointList.size(); j++) {
			existingCandidatePoint = iter2.next();
			if (existingCandidatePoint.orderingValue < orderingValue2) {
				iter2.next();
				index2++;
			} else
				break;
		}

		newCandidatePoint.orderingValue = orderingValue2;
		newCandidatePoint.lineSegmentId = lineSegmentId;
		newCandidatePoint.startPointFlag = false;
		if (j == 0)
			clusterEntry.candidatePointList.offerFirst(newCandidatePoint);
		else if (j >= (int) clusterEntry.candidatePointList.size())
			clusterEntry.candidatePointList.offerLast(newCandidatePoint);
		else {
			CandidateClusterPoint[] arrTmp = (CandidateClusterPoint[]) clusterEntry.candidatePointList
					.toArray();
			arrTmp[index2] = newCandidatePoint;
			clusterEntry.candidatePointList = new ArrayDeque<CandidateClusterPoint>(
					CommonUtils.arr2List(arrTmp));
		}
		// ... END

		int trajectoryId = idArray.get(lineSegmentId).trajectoryId;

		// store the identifier of the trajectories that belong to this line
		// segment cluster
		if (!clusterEntry.trajectoryIdList.contains(trajectoryId)) {
			clusterEntry.trajectoryIdList.add(trajectoryId);
			clusterEntry.nTrajectories++;
		}

		return;
	}

	private void computeRepresentativeLines(LineSegmentCluster clusterEntry) {
		Set<Integer> lineSegments = new HashSet<Integer>();
		Set<Integer> insertionList = new HashSet<Integer>();
		Set<Integer> deletionList = new HashSet<Integer>();
		CandidateClusterPoint candidatePoint = new CandidateClusterPoint();
		CandidateClusterPoint nextCandidatePoint = new CandidateClusterPoint();
		double prevOrderingValue = 0.0;

		int nClusterPoints = 0;
		lineSegments.clear();

		// sweep the line segments in a line segment cluster
		// list<CandidateClusterPoint>::iterator iter =
		// clusterEntry.candidatePointList.begin();
		for (CandidateClusterPoint candidateClusterPoint : clusterEntry.candidatePointList) {
			insertionList.clear();
			deletionList.clear();

			do {
				candidatePoint = candidateClusterPoint;

				// check whether this line segment has begun or not
				// iter1 = lineSegments.find(candidatePoint.lineSegmentId);
				if (!lineSegments.contains(candidatePoint.lineSegmentId)) { // if
																			// there
																			// is
																			// no
																			// matched
																			// element,
					insertionList.add(candidatePoint.lineSegmentId); // this
																		// line
																		// segment
																		// begins
																		// at
																		// this
																		// point
					lineSegments.add(candidatePoint.lineSegmentId);
				} else
					// if there is a matched element,
					deletionList.add(candidatePoint.lineSegmentId); // this line
																	// segment
																	// ends at
																	// this
																	// point

				// check whether the next line segment begins or ends at the
				// same point
				// if (iter != clusterEntry.candidatePointList.end())
				// nextCandidatePoint = *iter;
				// else
				// break;
			} while (candidatePoint.orderingValue == nextCandidatePoint.orderingValue);

			// check if a line segment is connected to another line segment in
			// the same trajectory
			// if so, delete one of the line segments to remove duplicates
			for (Integer iter2 : insertionList) {
				for (Integer iter3 : deletionList) {
					if (iter2 == iter3) {
						lineSegments.remove(iter3);
						deletionList.remove(iter3); // now deleted
						break;
					}
				}
				for (Integer iter3 : deletionList) {
					if (idArray.get(iter2).trajectoryId == idArray.get(iter3).trajectoryId) {
						lineSegments.remove(iter3);
						deletionList.remove(iter3); // now deleted
						break;
					}
				}
			}

			// if the current density exceeds a given threshold
			if ((int) (lineSegments.size()) >= minLnsParam) {
				if (Math.abs(candidatePoint.orderingValue - prevOrderingValue) > ((double) TraClusConfig.MIN_LINESEGMENT_LENGTH / 1.414)) {
					computeAndRegisterClusterPoint(clusterEntry,
							candidatePoint.orderingValue, lineSegments);
					prevOrderingValue = candidatePoint.orderingValue;
					nClusterPoints++;
				}
			}

			// delete the line segment that is not connected to another line
			// segment
			for (Integer iter3 : deletionList)
				lineSegments.remove(iter3);
		}

		if (nClusterPoints >= 2)
			clusterEntry.nClusterPoints = nClusterPoints;
		else {
			// there is no representative trend in this line segment cluster
			clusterEntry.enabled = false;
			clusterEntry.candidatePointList.clear();
			clusterEntry.clusterPointArray.clear();
			clusterEntry.trajectoryIdList.clear();
		}

		return;
	}

	private void computeAndRegisterClusterPoint(
			LineSegmentCluster clusterEntry, double currValue,
			Set<Integer> lineSegments) {
		int nDimensions = config.getnDimensions();
		int nLineSegmentsInSet = lineSegments.size();
		Point clusterPoint = new Point(nDimensions);
		Point sweepPoint = new Point(nDimensions);

		for (Integer id : lineSegments) {
			// get the sweep point of each line segment
			// this point is parallel to the current value of the sweeping
			// direction
			getSweepPointOfLineSegment(clusterEntry, currValue, id, sweepPoint);
			// compute the average of all the sweep points
			for (int i = 0; i < nDimensions; i++)
				clusterPoint
						.setCoordinate(
								i,
								clusterPoint.getCoordinate(i)
										+ (sweepPoint.getCoordinate(i) / (double) nLineSegmentsInSet));
		}

		// NOTE: this program code works only for the 2-dimensional data
		double origX, origY;
		origX = MathUtils.getXRevRotation(clusterPoint.getCoordinate(0),
				clusterPoint.getCoordinate(1), clusterEntry.cosTheta,
				clusterEntry.sinTheta);
		origY = MathUtils.getYRevRotation(clusterPoint.getCoordinate(0),
				clusterPoint.getCoordinate(1), clusterEntry.cosTheta,
				clusterEntry.sinTheta);
		clusterPoint.setCoordinate(0, origX);
		clusterPoint.setCoordinate(1, origY);

		// register the obtained cluster point (i.e., the average of all the
		// sweep points)
		clusterEntry.clusterPointArray.add(clusterPoint);

		return;
	}

	private void getSweepPointOfLineSegment(LineSegmentCluster clusterEntry,
			double currValue, int lineSegmentId, Point sweepPoint) {
		Point lineSegmentPoint = lineSegmentPointArray.get(lineSegmentId); // 2n-dimensional
																			// point
		double coefficient;

		// NOTE: this program code works only for the 2-dimensional data
		double newStartX, newEndX, newStartY, newEndY;
		newStartX = MathUtils.getXRotation(lineSegmentPoint.getCoordinate(0),
				lineSegmentPoint.getCoordinate(1), clusterEntry.cosTheta,
				clusterEntry.sinTheta);
		newEndX = MathUtils.getXRotation(lineSegmentPoint.getCoordinate(2),
				lineSegmentPoint.getCoordinate(3), clusterEntry.cosTheta,
				clusterEntry.sinTheta);
		newStartY = MathUtils.getYRotation(lineSegmentPoint.getCoordinate(0),
				lineSegmentPoint.getCoordinate(1), clusterEntry.cosTheta,
				clusterEntry.sinTheta);
		newEndY = MathUtils.getYRotation(lineSegmentPoint.getCoordinate(2),
				lineSegmentPoint.getCoordinate(3), clusterEntry.cosTheta,
				clusterEntry.sinTheta);

		coefficient = (currValue - newStartX) / (newEndX - newStartX);
		sweepPoint.setCoordinate(0, currValue);
		sweepPoint.setCoordinate(1, newStartY + coefficient
				* (newEndY - newStartY));

		return;
	}

	private boolean storeLineSegmentCluster() {
		int currClusterId = 0;

		for (int i = 0; i < currComponentId; i++) {
			if (lineSegmentClusters[i].enabled) {
				// store the clusters finally identified
				// START ...
				Cluster pClusterItem = new Cluster(currClusterId,
						config.getnDimensions());
				clusterList.add(pClusterItem);

				for (int j = 0; j < lineSegmentClusters[i].nClusterPoints; j++)
					pClusterItem
							.addPoint(lineSegmentClusters[i].clusterPointArray
									.get(j));

				pClusterItem
						.setnTrajectories(lineSegmentClusters[i].nTrajectories);

				currClusterId++; // increase the number of final clusters
				// ... END
			}
		}

		// m_document.m_nClusters = currClusterId;

		return true;
	}

	public boolean estimateParameterValue(double epsParam, int minLnsParam) {
		double entropy, minEntropy = Double.MAX_VALUE;
		double eps, minEps = Double.MAX_VALUE;
		int totalSize, minTotalSize = Integer.MAX_VALUE;
		Set<Integer> seeds = new HashSet<Integer>();

		int[] epsNeighborhoodSize = new int[nTotalLineSegments];

		for (eps = (double) 20.0; eps <= (double) 40.0; eps = eps
				+ (double) 1.0) {
			entropy = (double) 0.0;
			totalSize = 0;
			seeds.clear();

			for (int i = 0; i < nTotalLineSegments; i++) {
				extractStartAndEndPoints(i, startPoint1, endPoint1);
				computeEPSNeighborhood(startPoint1, endPoint1, eps, seeds);
				epsNeighborhoodSize[i] = (int) seeds.size();
				totalSize += (int) seeds.size();
				seeds.clear();
			}

			for (int i = 0; i < nTotalLineSegments; i++)
				entropy += ((double) epsNeighborhoodSize[i] / (double) totalSize)
						* MathUtils.logBase2((double) epsNeighborhoodSize[i]
								/ (double) totalSize);
			entropy = -entropy;

			if (entropy < minEntropy) {
				minEntropy = entropy;
				minTotalSize = totalSize;
				minEps = eps;
			}

			// fprintf(stdout, "."); fflush(stdout);
		}
		// setup output arguments
		epsParam = minEps;
		minLnsParam = (int) Math.ceil((double) minTotalSize
				/ (double) nTotalLineSegments);

		// fprintf(stdout, "\n"); fflush(stdout);

		return true;
	}

	public double measureDistanceFromPointToLineSegment(Point s, Point e,
			Point p) {
		int nDimensions = p.getnDimensions();

		// NOTE: the variables m_vector1 and m_vector2 are declared as member
		// variables

		// construct two vectors as follows
		// 1. the vector connecting the start point of the cluster component and
		// a given point
		// 2. the vector representing the cluster component
		for (int i = 0; i < nDimensions; i++) {
			vector1.setCoordinate(i, p.getCoordinate(i) - s.getCoordinate(i));
			vector2.setCoordinate(i, e.getCoordinate(i) - s.getCoordinate(i));
		}

		// a coefficient (0 <= b <= 1)
		// the projection of vector1 on vector2
		coefficient = VectorUtils.computeInnerProduct(vector1.getCoordinate(),
				vector2.getCoordinate())
				/ VectorUtils.computeInnerProduct(vector2.getCoordinate(),
						vector2.getCoordinate());

		// the projection on the cluster component from a given point
		// NOTE: the variable projectionPoint is declared as a member variable

		for (int i = 0; i < nDimensions; i++)
			projectionPoint.setCoordinate(i, s.getCoordinate(i) + coefficient
					* vector2.getCoordinate(i));

		// return the distance between the projection point and the given point
		return MathUtils.computeEuclideanDistance(p.getCoordinate(),
				projectionPoint.getCoordinate());
	}

	/**
	 * Angle Disntance
	 * @param s1
	 * @param e1
	 * @param s2
	 * @param e2
	 * @return
	 */
	public double measureAngleDisntance(Point s1, Point e1, Point s2, Point e2) {
		int nDimensions = s1.getnDimensions();

		// NOTE: the variables m_vector1 and m_vector2 are declared as member
		// variables

		// construct two vectors representing the cluster component and a line segment, respectively
		for (int i = 0; i < nDimensions; i++) {
			vector1.setCoordinate(i, e1.getCoordinate(i) - s1.getCoordinate(i));
			vector2.setCoordinate(i, e2.getCoordinate(i) - s2.getCoordinate(i));
		}
		// we assume that the first line segment is longer than the second one
		// i.e., vectorLength1 >= vectorLength2
		double vectorLength1 = VectorUtils.computeVectorLength(vector1
				.getCoordinate());
		double vectorLength2 = VectorUtils.computeVectorLength(vector2
				.getCoordinate());

		// if one of two vectors is a point, the angle distance becomes zero
		if (vectorLength1 == 0.0 || vectorLength2 == 0.0)
			return 0.0;

		// compute the inner product of the two vectors
		double innerProduct = VectorUtils.computeInnerProduct(
				vector1.getCoordinate(), vector2.getCoordinate());

		// compute the angle between two vectors by using the inner product
		double cosTheta = innerProduct / (vectorLength1 * vectorLength2);
		// compensate the computation error (e.g., 1.00001)
		// cos(theta) should be in the range [-1.0, 1.0]
		// START ...
		if (cosTheta > 1.0)
			cosTheta = 1.0;
		if (cosTheta < -1.0)
			cosTheta = -1.0;
		// ... END
		double sinTheta = Math.sqrt(1 - Math.pow(cosTheta, 2));
		// if 90 <= theta <= 270, the angle distance becomes the length of the
		// line segment
		// if (cosTheta < -1.0) sinTheta = 1.0;

		return (vectorLength2 * sinTheta);
	}
}
