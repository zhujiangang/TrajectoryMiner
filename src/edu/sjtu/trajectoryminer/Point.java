package edu.sjtu.trajectoryminer;

public class Point {
	private int nDimensions;// the number of dimensions of a point
	private double[] coordinate;// the coordinate of a point

	public Point() {
		super();
		nDimensions = 2;
		coordinate = new double[nDimensions];
		for (int i = 0; i < nDimensions; i++)
			coordinate[i] = 0.0d;
	}

	public Point(int nDimensions) {
		super();
		this.nDimensions = nDimensions;
		coordinate = new double[nDimensions];
		for (int i = 0; i < nDimensions; i++)
			coordinate[i] = 0.0d;
	}

	public double getCoordinate(int nth) {
		return coordinate[nth];
	}

	public void setCoordinate(int nth, double value) {
		coordinate[nth] = value;
	}

	public int getnDimensions() {
		return nDimensions;
	}

	public void setnDimensions(int nDimensions) {
		this.nDimensions = nDimensions;
	}

	public double[] getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(double[] coordinate) {
		this.coordinate = coordinate;
	}

}
