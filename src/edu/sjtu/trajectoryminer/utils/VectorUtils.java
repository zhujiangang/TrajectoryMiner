package edu.sjtu.trajectoryminer.utils;

public class VectorUtils {

	public static boolean isEqual(double[] v1, double[] v2) {
		if (v1.length != v2.length)
			return false;
		for (int i = 0; i < v1.length; i++)
			if (v1[i] != v2[i])
				return false;
		return true;
	}

	public static int isSubset(double[] v1, double[] v2) {
		// if v1 is a subset of v2, return -1
		// if v2 is a subset of v1, return 1
		// if v1 == v2, return 0
		// if v1 and v2 have no superset/subset relations, return -2
		int skipa = 0, skipb = 0;
		int ka = 0, kb = 0;
		while ((ka < v1.length) && (kb < v2.length)) {
			if (v1[ka] == v2[kb]) {
				ka++;
				kb++;
			} else if (v1[ka] < v2[kb]) {
				ka++;
				skipa++;
				if (skipb != 0)
					return 0; // no relations
			} else {
				kb++;
				skipb++;
				if (skipa != 0)
					return 0; // no relations
			}
		}
		skipa += v1.length - ka;
		skipb += v2.length - kb;
		if ((skipa != 0) && (skipb != 0))
			return 0; // no relations
		if ((skipa == 0) && (skipb == 0))
			return 0; // a = b
		if (skipb != 0) { // a < b
			return -1;
		} else {
			return 1; // a > b
		}
	}

	public static double computeVectorLength(double[] vector) {
		double squareSum = 0.0;
		for (int i = 0; i < vector.length; i++)
			squareSum += Math.pow(vector[i], 2);
		return Math.sqrt(squareSum);
	}

	public static double computeInnerProduct(double[] vector1, double[] vector2) {
		int nDimensions = vector1.length;
		double innerProduct = 0.0;

		for (int i = 0; i < nDimensions; i++)
			innerProduct += (vector1[i] * vector2[i]);

		return innerProduct;
	}
}
