package edu.sjtu.trajectoryminer.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	
	public static String removerRoundBrackets(String str){
		if(isBlank(str))
			return str;
		return str.replaceAll( "\\(.+\\)", "");
	}
	
	public static Map<String, Double> getKgram(String content, int k) {
		String[] arr = content.toLowerCase().split("\\s+");
		Map<String, Double> allWords = new HashMap<String, Double>();
		for (int i = 0; i < arr.length - k + 1; i++) {
			String word = "";
			for (int j = i; j < i + k; j++) {
				word += arr[j] + " ";
			}
			//word = Utils.singularize(word.trim());
			allWords = CommonUtils.addToMap(allWords, word.trim());
		}
		return allWords;
	}
	
	public boolean ngram(String wiki, String concept) {
		String[] arr = wiki.toLowerCase().split("\\s+");
		int length = concept.split("-").length;
		List<String> allWords = new ArrayList<String>();
		for (int i = 0; i < arr.length - length + 1; i++) {
			String word = "";
			for (int j = i; j < i + length; j++) {
				word += arr[j] + " ";
			}
			allWords.add(word.trim());
		}
		concept = concept.replace("-", " ");
		if (allWords.contains(concept))
			return true;
		else
			return false;
	}
	public static List<String> splitAsCamel(String str) {
		// str = underScoreToCamel(str);
		str = normalize(str);
		str = camelToUnderScore(str);
		str = normalize(str);
		String[] arr = str.split(" ");
		List<String> list = new ArrayList<String>();
		for(String s : arr){
			if(!isBlank(s) && !s.equals("\\u") && !s.equals("\\n"))
				list.add(s.toLowerCase().trim());
		}
		return list;
	}
	
	public static List<String> sortByLength(List<String> dic,boolean isDesc){
		Map<Integer,Double> indexLenMap = new HashMap<Integer, Double>();
		if(dic==null)
			return null;
		if(dic.size()==0)
			return dic;
		for(int i=0; i<dic.size(); i++){
			indexLenMap.put(i, dic.get(i).length()+0.0d);
		}
		indexLenMap = CommonUtils.sortMapByValue(indexLenMap, isDesc);
		List<String> retList = new ArrayList<String>();
		for(Map.Entry<Integer, Double> entry : indexLenMap.entrySet()){
			retList.add(dic.get(entry.getKey()));
		}
		return retList;
	}
	public static boolean containCamel(String src,String str){
//		while(src.contains(str)){
//			int index = src.indexOf(str);
//			String left = src.substring(0, index);
//			String right = src.substring(index+str.length(), src.length());
//			if(isFirstLowCase(str) && isEmpty(left) && (isEmpty(right)||!isFirstLowCase(right)))
//				return true;
//			if(!isFirstLowCase(str) && (isEmpty(left) || isLastLowCase(left)) && )
//		}
		return false;
	}
	public static List<String> splitAsCamel(String str,List<String> dic) {
		// str = underScoreToCamel(str);
		//System.out.println(str);
		str = normalize(str);
		dic = CommonUtils.set2List(new HashSet<String>(dic));
		dic = sortByLength(dic, true);
		String origin = str.toLowerCase();
		List<String> list = new ArrayList<String>();
		for(String s : dic){
			if(s.equals("?") || StringUtils.isBlank(s))
				continue;
			s = s.replaceAll("\\?", "").replaceAll("\\$", "");
			while(str.contains(toUpCaseFirst(s))){
				str = str.replaceFirst(toUpCaseFirst(s), "_");
				list.add(toUpCaseFirst(s).toLowerCase());
			}
			while(str.contains(toLowCaseFirst(s))){
				str = str.replaceFirst(toLowCaseFirst(s), "_");
				list.add(toLowCaseFirst(s).toLowerCase());
			}
		}
		str = camelToUnderScore(str);
		str = normalize(str);
		String[] arr = str.split(" ");
		//List<String> list = new ArrayList<String>();
		for(String s : arr){
			if(!isBlank(s) && !s.equals("\\u") && !s.equals("\\n"))
				list.add(s.trim());
		}
		Map<Integer,Double> indexMap= new HashMap<Integer,Double>();
		Map<String,Integer> strMapIndex = new HashMap<String, Integer>();
		for(int i=0; i<list.size(); i++){
			if(strMapIndex.get(list.get(i))==null){
				indexMap.put(i, origin.indexOf(list.get(i))+0.0d);
				strMapIndex.put(list.get(i), origin.indexOf(list.get(i)));
			}else{
				int lastStart = strMapIndex.get(list.get(i));
				String tmp = origin.substring(lastStart+list.get(i).length());
				int newIndex = tmp.indexOf(list.get(i));
				newIndex = lastStart+list.get(i).length()+newIndex;
				indexMap.put(i, newIndex+0.0d);
				strMapIndex.put(list.get(i), newIndex);
			}
		}
		indexMap = CommonUtils.sortMapByValue(indexMap, false);
		List<String> retList = new ArrayList<String>();
		for(Map.Entry<Integer, Double> entry : indexMap.entrySet()){
			retList.add(list.get(entry.getKey()));
		}
		return retList;
	}

	public static String splitAsCamelString(String str) {
		// str = underScoreToCamel(str);
		List<String> list = splitAsCamel(str);
		String retStr = "";
		for(String s : list){
			retStr += s+" ";
		}
		return retStr.trim();
	}
	
	/**
	 * @param filePathAndName
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static String clearComment(String filecontent) {
		//str = str.replaceAll("\\/\\/[^\\n]*|\\/\\*([^\\*^\\/]*|[\\*^\\/*]*|[^\\**\\/]*)*\\*\\/", "");
		// 1、清除单行的注释，如： //某某，正则为 ：\/\/.*
		// 2、清除单行的注释，如：/** 某某 */，正则为：\/\*\*.*\*\/
		// 3、清除单行的注释，如：/* 某某 */，正则为：\/\*.*\*\/
		// 4、清除多行的注释，如:
		// /* 某某1
		// 某某2
		// */
		// 正则为：.*/\*(.*)\*/.*
		// 5、清除多行的注释，如：
		// /** 某某1
		// 某某2
		// */
		// 正则为：/\*\*(\s*\*\s*.*\s*?)*
		Map<String, String> patterns = new HashMap<String, String>();
		patterns.put("([^:])\\/\\/.*", "$1");// 匹配在非冒号后面的注释，此时就不到再遇到http://
		patterns.put("\\s+\\/\\/.*", "");// 匹配“//”前是空白符的注释
		patterns.put("^\\/\\/.*", "");
		patterns.put("^\\/\\*\\*.*\\*\\/$", "");
		patterns.put("\\/\\*.*\\*\\/", "");
		patterns.put("/\\*(\\s*\\*\\s*.*\\s*?)*\\*\\/", "");
		// patterns.put("/\\*(\\s*\\*?\\s*.*\\s*?)*", "");
		Iterator<String> keys = patterns.keySet().iterator();
		String key = null, value = "";
		while (keys.hasNext()) {
			// 经过多次替换
			key = keys.next();
			value = patterns.get(key);
			filecontent = replaceAll(filecontent, key, value);
		}
		return filecontent;
	}

	/**
	 * @param fileContent
	 *            内容
	 * @param patternString
	 *            匹配的正则表达式
	 * @param replace
	 *            替换的内容
	 * @return
	 */
	public static String replaceAll(String fileContent, String patternString,
			String replace) {
		String str = "";
		Matcher m = null;
		Pattern p = null;
		try {
			p = Pattern.compile(patternString);
			m = p.matcher(fileContent);
			str = m.replaceAll(replace);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			m = null;
			p = null;
		}
		// 获得匹配器对象
		return str;
	}

	/**
	 * 汉字转Unicode
	 * 
	 * @param gbString
	 * @return
	 */
	public static String str2Unicode(final String gbString) {
		char[] utfBytes = gbString.toCharArray();
		String unicodeBytes = "";
		for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
			String hexB = Integer.toHexString(utfBytes[byteIndex]);
			if (hexB.length() <= 2) {
				hexB = "00" + hexB;
			}
			unicodeBytes = unicodeBytes + "\\u" + hexB;
		}
		return unicodeBytes;
	}

	public static String normalize(String str) {
		str = URLDecoder.decode(str);

		str = trimRight(str);
		str = replaceUnderScore(str);
		str = replaceHyphens(str);
		str = splitOnCapitalPhrase(str);
		return str.toLowerCase();
	}

	public static boolean isFirstLowCase(String str) {
		if (isBlank(str))
			return false;
		char first = str.charAt(0);
		StringBuilder sb = new StringBuilder();
		sb.append(first);
		if (sb.toString().toLowerCase().equals(sb.toString()))
			return true;
		else
			return false;
	}

//	public static String[] splitAsCamel(String str) {
//		// str = underScoreToCamel(str);
//		str = camelToUnderScore(str);
//		str = normalize(str);
//		return str.split(" ");
//	}

	public static String trimRight(String str) {
		if (str.length() > 9) {
			String number = str.substring(str.length() - 9);
			if (CommonUtils.isNumeric(number))
				return str.substring(0, str.length() - 9);
		}
		return str;
	}

	private static String replaceUnderScore(String str) {
		str = str.replaceAll("_", " ");
		return str;
	}

	private static String replaceHyphens(String str) {
		str = str.replaceAll("-", " ");
		return str;
	}

	private static String splitOnCapitalPhrase(String str) {
		str = str.replaceAll("([A-Z])(?![A-Z])", " $1");
		str = str.trim();
		return str;
	}

	public static String underScoreToCamel(String column) {
		StringBuilder result = new StringBuilder();
		// 快速检查
		if (column == null || column.isEmpty()) {
			// 没必要转换
			return "";
		} else if (!column.contains("_")) {
			// 不含下划线，仅将首字母小写
			return column.substring(0, 1).toLowerCase() + column.substring(1);
		} else {
			// 用下划线将原始字符串分割
			String[] columns = column.split("_");
			for (String columnSplit : columns) {
				// 跳过原始字符串中开头、结尾的下换线或双重下划线
				if (columnSplit.isEmpty()) {
					continue;
				}
				// 处理真正的驼峰片段
				if (result.length() == 0) {
					// 第一个驼峰片段，全部字母都小写
					result.append(columnSplit.toLowerCase());
				} else {
					// 其他的驼峰片段，首字母大写
					result.append(columnSplit.substring(0, 1).toUpperCase())
							.append(columnSplit.substring(1).toLowerCase());
				}
			}
			return result.toString();
		}

	}

	/**
	 * 驼峰转下划线
	 */
	public static String camelToUnderScore(String property) {
		if (property == null || property.isEmpty()) {
			return "";
		}
		StringBuilder column = new StringBuilder();
		column.append(property.substring(0, 1));
		for (int i = 1; i < property.length(); i++) {
			String s = property.substring(i, i + 1);
			if (i < property.length() - 1) {
				String next = property.substring(i + 1, i + 2);
				// 在小写字母前添加下划线
				if (!Character.isDigit(s.charAt(0))
						&& s.equals(s.toUpperCase())
						&& next.equals(next.toLowerCase())
						&& !Character.isDigit(next.charAt(0))
						&& !next.equals("_")) {
					if (!column.toString().endsWith("_"))
						column.append("_");
				}
			}
			// 其他字符直接转成小写
			column.append(s.toLowerCase());
		}

		return column.toString();
	}

	public static String encode(String src) {
		return null;
	}

	public static String decode(String src) {
		return null;
	}

	public static char toUpperCase(char ch) {
		StringBuffer sb = new StringBuffer();
		sb.append(ch);
		char[] arr = sb.toString().toUpperCase().toCharArray();
		return arr[0];
	}

	public static char toLowerCase(char ch) {
		StringBuffer sb = new StringBuffer();
		sb.append(ch);
		char[] arr = sb.toString().toLowerCase().toCharArray();
		return arr[0];
	}

	public static boolean checkRegex(String str, String regex) {
		Pattern pattern = Pattern.compile(regex);
		// String str ="中国China美国";
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	public static String findOneByRegex(String input, String regex) {
		List<String> list = findByRegex(input, regex);
		if (list == null)
			return null;

		return list.get(0);
	}

	public static List<String> findByRegex(String input, String regex) {
		List<String> result = new ArrayList<String>();
		Pattern p = Pattern.compile(regex, Pattern.DOTALL);
		Matcher m = p.matcher(input);
		while (m.find()) {
			result.add(m.group());
		}
		if (result.isEmpty())
			return null;
		return result;
	}

	/**
	 * 将字符串转换为数字
	 * 
	 * @param source
	 *            被转换的字符串
	 * @return int 型值
	 */
	public static int strToInt(String source) {
		int result = 0;
		try {
			result = Integer.parseInt(source);
		} catch (NumberFormatException e) {
			result = 0;
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * @功能 将字符串首字母转为大写
	 * @param str
	 *            要转换的字符串
	 * @return String 型值
	 */
	public static String toUpCaseFirst(String str) {
		if (str == null || "".equals(str)) {
			return str;
		} else {
			char[] temp = str.toCharArray();
			temp[0] = str.toUpperCase().toCharArray()[0];
			str = String.valueOf(temp);
		}

		return str;
	}

	public static String toLowCaseFirst(String str) {
		if (str == null || "".equals(str)) {
			return str;
		} else {
			char[] temp = str.toCharArray();
			temp[0] = str.toLowerCase().toCharArray()[0];
			str = String.valueOf(temp);
		}

		return str;
	}

	/**
	 * 批量将英文字符串首字母转为大写
	 * 
	 * @param str
	 *            要转换的字符串数组
	 * @return 字符数组
	 */
	public static String[] toUpCaseFirst(String[] str) {
		if (str == null || str.length == 0) {
			return str;
		} else {
			String[] result = new String[str.length];
			for (int i = 0; i < result.length; ++i) {
				result[i] = toUpCaseFirst(str[i]);
			}

			return result;
		}
	}

	public static String[] toLowCaseFirst(String[] str) {
		if (str == null || str.length == 0) {
			return str;
		} else {
			String[] result = new String[str.length];
			for (int i = 0; i < result.length; ++i) {
				result[i] = toLowCaseFirst(str[i]);
			}

			return result;
		}
	}

	public static String htmlFilter(String htmlStr) {
		if (htmlStr == null || "".equals(htmlStr))
			return "";
		String textStr = "";
		java.util.regex.Pattern pattern;
		java.util.regex.Matcher matcher;

		try {
			String regEx_remark = "<!--.+?-->";
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
																										// }
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
																									// }
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
			String regEx_html1 = "<[^>]+";
			htmlStr = htmlStr.replaceAll("\n", " ");
			htmlStr = htmlStr.replaceAll("\t", " ");
			pattern = Pattern.compile(regEx_remark);// 过滤注释标签
			matcher = pattern.matcher(htmlStr);
			htmlStr = matcher.replaceAll(" ");

			pattern = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(htmlStr);
			htmlStr = matcher.replaceAll(" "); // 过滤script标签

			pattern = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(htmlStr);
			htmlStr = matcher.replaceAll(" "); // 过滤style标签

			pattern = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(htmlStr);
			htmlStr = matcher.replaceAll(" "); // 过滤html标签

			pattern = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(htmlStr);
			htmlStr = matcher.replaceAll(" "); // 过滤html标签

			textStr = htmlStr.trim();

		} catch (Exception e) {
			System.out.println("获取HTML中的text出错:");
			e.printStackTrace();
		}

		return trimOne(textStr);// 返回文本字符串
	}

	/**
	 * 替换SQL中的特殊字符
	 * 
	 * @param input
	 * @return
	 */
	public static String sqlReplace(String input) {
		try {
			return input.replaceAll("'", "''").replaceAll("\\\\", "\\\\\\\\");
		} catch (Exception e) {
			return input;
		}
	}

	public static String sqlFilter(String sql) {
		if (isBlank(sql))
			return sql;
		// 过滤特殊字符，用全角代替
		sql = sql.replace("'", "‘");
		sql = sql.replace(";", "；");
		sql = sql.replace(",", ",");
		sql = sql.replace("?", "?");
		sql = sql.replace("<", "＜");
		sql = sql.replace(">", "＞");
		sql = sql.replace("(", "(");
		sql = sql.replace(")", ")");
		sql = sql.replace("@", "＠");
		sql = sql.replace("=", "＝");
		sql = sql.replace("+", "＋");
		sql = sql.replace("*", "＊");
		sql = sql.replace("&", "＆");
		sql = sql.replace("#", "＃");
		sql = sql.replace("%", "％");
		sql = sql.replace("\\\\", "\\\\\\\\");
		// sql = sql.replace("$", "￥");

		// 去除执行存储过程的命令关键字
		sql = sql.replace("Exec", "Exec_");
		sql = sql.replace("Execute", "Execute_");
		sql = sql.replace("xp_", "xp__");
		sql = sql.replace("select", "select_");
		sql = sql.replace("insert", "insert_");
		sql = sql.replace("delete", "delete_");
		sql = sql.replace("count", "count_");
		sql = sql.replace("drop", "drop_");
		sql = sql.replace("truncate", "truncate_");
		sql = sql.replace("script", "script_");

		// 防止16进制注入
		sql = sql.replace("0x", "0x_");

		return sql;
	}

	public static boolean isBlank(final String str) {
		if (null == str)
			return true;
		if (str.isEmpty())
			return true;
		return str.trim().isEmpty();
	}

	public static boolean hasValue(String s) {
		return s != null && s.length() > 0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// String sql = "insert into 'category' value(1,'abc',2,3,4)";
		// String regex = "(?:\\w[-._\\w]*\\w@\\w[-._\\w]*\\w\\.\\w{2,3}$)";
		String str = "fasdfasdf((fasdf(fasdf)a)";
		System.out.println(removerRoundBrackets(str));
	}

	/**
	 * 除去字符串中所有的空格
	 * 
	 * @param str
	 * @return
	 */
	public static String trimAll(String str) {
		str = str.replace("&nbsp;", "");
		str = str.replace(".", "");
		str = str.replace("\"", "‘");
		str = str.replace("'", "‘");
		str = str.replaceAll("\\s*|\t|\r|\n", "");// 去除字符串中的空格,回车,换行符,制表符
		return str.trim();
	}
	
	public static String trimAll(String str,String ch) {
		str = str.replace("&nbsp;", ch);
		str = str.replace(".", ch);
		str = str.replace("\"", "‘");
		str = str.replace("'", "‘");
		str = str.replaceAll("\\s*|\t|\r|\n", ch);// 去除字符串中的空格,回车,换行符,制表符
		return str.trim();
	}

	public static String trimOne(String str) {
		if (str == null)
			return null;
		return str.replaceAll("\\s+", " ");
	}

}
