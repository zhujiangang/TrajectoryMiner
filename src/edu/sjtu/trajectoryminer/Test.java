package edu.sjtu.trajectoryminer;

import edu.sjtu.easytm.similarity.EuclideanDistanceSimilarity;

public class Test {
	public static void main(String[] args) {
		Double[] v1 = {114.47192,38.077124};
		Double[] v2 = {114.422032,38.033416};
		
		EuclideanDistanceSimilarity sim = new EuclideanDistanceSimilarity();
		System.out.println(sim.sim(v1, v2));
		String s ="a:b";
		System.out.println(s.split(":").length);
	}
}
