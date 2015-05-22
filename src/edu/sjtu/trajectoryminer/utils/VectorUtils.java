package edu.sjtu.trajectoryminer.utils;

public class VectorUtils {

	public static double computeVectorLength(double[] vector){
		double squareSum = 0.0;
		for (int i = 0; i < vector.length; i++)
			squareSum += Math.pow(vector[i], 2);
		return Math.sqrt(squareSum);
	}

	public static double computeInnerProduct(double[] vector1, double[] vector2){
		int nDimensions = vector1.length;
		double innerProduct = 0.0;

		for (int i = 0; i < nDimensions; i++)
			innerProduct += (vector1[i] * vector2[i]);

		return innerProduct;
	}
}
