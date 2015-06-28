package edu.sjtu.trajectoryminer.speedchange;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.sjtu.trajectoryminer.utils.FileUtils;

public class SpeedChangeAnalyser {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String dir = "D:\\Research\\Car\\急变速\\导出数据\\导出数据";
		File outFile = new File("D:\\Research\\Car\\急变速\\导出数据\\x.csv");
		//generateInterval(dir, outFile, 0);
		computeVariance(dir, 4);
	}

	public static void computeVariance(String dir,int index) {
		File[] files = FileUtils.getFiles(dir);
		for (File file : files) {
			if (!file.getName().endsWith("txt"))
				continue;
			
			List<String> lines = FileUtils.readLine(file);
			lines.remove(0);
			Double sum = 0d;
			for (int i = 1; i < lines.size(); i++) {
				String last = lines.get(i - 1);
				String cur = lines.get(i);
				String v1Str = last.split(",")[index];
				String v2Str = cur.split(",")[index];

				if (v1Str.contains("经度") || v1Str.contains("纬度")
						|| v1Str.contains("海拔"))
					v1Str = v1Str.substring(2, v1Str.length());
				if (v2Str.contains("经度") || v2Str.contains("纬度")
						|| v2Str.contains("海拔"))
					v2Str = v2Str.substring(2, v2Str.length());
				Double v1 = null;
				Double v2 = null;
				if (v1Str.trim().equals(""))
					v1 = 0d;
				else
					v1 = Double.parseDouble(v1Str);

				if (v2Str.trim().equals(""))
					v2 = 0d;
				else
					v2 = Double.parseDouble(v2Str);

				Double interval = v2 - v1;
				sum += interval;
			}
			sum /=(lines.size()-1);
			Double v = 0d;
			for (int i = 1; i < lines.size(); i++) {
				String last = lines.get(i - 1);
				String cur = lines.get(i);
				String v1Str = last.split(",")[index];
				String v2Str = cur.split(",")[index];

				if (v1Str.contains("经度") || v1Str.contains("纬度")
						|| v1Str.contains("海拔"))
					v1Str = v1Str.substring(2, v1Str.length());
				if (v2Str.contains("经度") || v2Str.contains("纬度")
						|| v2Str.contains("海拔"))
					v2Str = v2Str.substring(2, v2Str.length());
				Double v1 = null;
				Double v2 = null;
				if (v1Str.trim().equals(""))
					v1 = 0d;
				else
					v1 = Double.parseDouble(v1Str);

				if (v2Str.trim().equals(""))
					v2 = 0d;
				else
					v2 = Double.parseDouble(v2Str);

				Double interval = v2 - v1;
				v += (sum-interval)*(sum-interval);
			}
			System.out.println(v/(lines.size()-1));
		}
	}

	public static void generateInterval(String dir, File outFile, int index) {
		File[] files = FileUtils.getFiles(dir);
		List<String> contentList = new ArrayList<String>();
		for (File file : files) {
			if (!file.getName().endsWith("txt"))
				continue;
			List<String> lines = FileUtils.readLine(file);
			lines.remove(0);
			String content = "";
			for (int i = 1; i < lines.size() && i < 100; i++) {
				String last = lines.get(i - 1);
				String cur = lines.get(i);
				String v1Str = last.split(",")[index];
				String v2Str = cur.split(",")[index];

				if (v1Str.contains("经度") || v1Str.contains("纬度")
						|| v1Str.contains("海拔"))
					v1Str = v1Str.substring(2, v1Str.length());
				if (v2Str.contains("经度") || v2Str.contains("纬度")
						|| v2Str.contains("海拔"))
					v2Str = v2Str.substring(2, v2Str.length());
				Double v1 = null;
				Double v2 = null;
				if (v1Str.trim().equals(""))
					v1 = 0d;
				else
					v1 = Double.parseDouble(v1Str);

				if (v2Str.trim().equals(""))
					v2 = 0d;
				else
					v2 = Double.parseDouble(v2Str);

				Double interval = v2 - v1;
				content += interval.toString() + ",";
			}
			if (content.endsWith(","))
				content = content.substring(0, content.length() - 1);

			contentList.add(content);
		}
		FileUtils.writeFile(outFile, contentList);
	}
}
