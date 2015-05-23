package edu.sjtu.trajectoryminer.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RandomUtils {

	public static List<Double> random(double min,double max,int n){
		List<Double> ret = new ArrayList<Double>();
		double delta=max-min;
		double r,rand;
		for(int i=0; i<n; i++){
			r=Math.random()*delta;
			rand=r+min;
			ret.add(rand);
		}
		return ret;
	}
	public static List<Integer> random(int min,int max,int n){
		List<Integer> ret = new ArrayList<Integer>();
		int delta=max-min;
		int r,rand;
		for(int i=0; i<n; i++){
			r=(int) (Math.random()*delta);
			rand=r+min;
			ret.add(rand);
		}
		return ret;
	}
	
	public static Set<Integer> randomDiff(int min,int max,int n){
		Set<Integer> ret = new HashSet<Integer>();
		int delta=max-min;
		int r,rand;
		while(ret.size()<n){
			r=(int) (Math.random()*delta);
			rand=r+min;
			ret.add(rand);
		}
		return ret;
	}
	public static <T> List<T> randomSelect(List<T> list,int cnt){
		if(list==null)
			return null;
		if(cnt>=list.size() || list.size()==0)
			return list;
		List<T> ret = new ArrayList<T>();
		Set<Integer> set = randomDiff(0, list.size(), cnt);
		for(Integer index : set){
			ret.add(list.get(index));
		}
		return ret;
		//List<Double> randomList = random(0, , n)
	}
	
	public static <T> List<T> randomSelect(List<T> list,double ratio){
		if(list==null)
			return null;
		int cnt = (int) (list.size()*ratio);
		if(cnt>=list.size() || list.size()==0)
			return list;
		List<T> ret = new ArrayList<T>();
		Set<Integer> set = randomDiff(0, list.size(), cnt);
		for(Integer index : set){
			ret.add(list.get(index));
		}
		return ret;
		//List<Double> randomList = random(0, , n)
	}
	
	public static <T> Collection<T> randomSelect(Collection<T> set,int cnt){
		List<T> list = new ArrayList<T>(set);
		return randomSelect(list, cnt);
		//List<Double> randomList = random(0, , n)
	}
	
	public static <T> List<T> randomSelect(Collection<T> set,double ratio){
		List<T> list = new ArrayList<T>(set);
		return randomSelect(list, ratio);
		//List<Double> randomList = random(0, , n)
	}
	public static double next(double min,double max){
		double delta=max-min;
		double r,rand;
		
		r=Math.random()*delta;
		rand=r+min;
		return rand;
	}
	
	public static int next(int min,int max){
		double delta=max-min;
		int r,rand;
		
		r=(int) (Math.random()*delta);
		rand=r+min;
		return rand;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for(int i=0; i<100; i++)
			System.out.println(next(0, 100));
	}

}
