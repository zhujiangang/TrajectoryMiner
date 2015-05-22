package edu.sjtu.trajectoryminer.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MathUtils {
	
	public static double computeEuclideanDistance(double[] point1,double[] point2){
		if(point1==null || point2==null)
			return Double.MAX_VALUE;
		int nDimensions = point1.length;
		double squareSum = 0.0;

		for (int i = 0; i < nDimensions; i++)
			squareSum += Math.pow(
					(point2[i] - point1[i]), 2);

		return Math.sqrt(squareSum);
	}
	public static double getXRotation(double x,double y, double cos,double sin) {
		return (x)*(cos) + (y)*(sin);
	}
	public static double getYRotation(double x,double y, double cos,double sin) {
		return -(x)*(sin) + (y)*(cos);
	}
	public static double getXRevRotation(double x,double y, double cos,double sin) {
		return (x)*(cos) - (y)*(sin);
	}
	public static double getYRevRotation(double x,double y, double cos,double sin) {
		return (x)*(sin) + (y)*(cos);
	}
	
	public static double logBase2(double value)
	{
		return Math.log(value)/Math.log(2);
	}
	public static double logBase10(double value)
	{
		return Math.log(value)/Math.log(10);
	}
	public static double ln(double value)
	{
		return Math.log(value)/Math.log(Math.E);
	}
	public static int min(int a, int b)
	{
		return (a>b)?b:a;
	}
	public static double p(long count, long total)
	{
		return ((double)count+0.5)/(total+1);
	}
	public static double round(double val)
	{
		int precision = 10000; //keep 4 digits
		return Math.floor(val * precision +.5)/precision;
	}
	public static double round(float val)
	{
		int precision = 10000; //keep 4 digits
		return Math.floor(val * precision +.5)/precision;
	}
	public static double round(double val, int n)
	{
		int precision = 1; 
		for(int i=0;i<n;i++)
			precision *= 10;
		return Math.floor(val * precision +.5)/precision;
	}
	public static float round(float val, int n)
	{
		int precision = 1; 
		for(int i=0;i<n;i++)
			precision *= 10;
		return (float) (Math.floor(val * precision +.5)/precision);
	}
	
	public static int max(Collection<Integer> collection){
		List<Integer> list = new ArrayList<Integer>(collection);
		Collections.sort(list);
		return list.get(list.size()-1);
	}
}
