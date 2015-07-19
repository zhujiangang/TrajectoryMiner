package edu.sjtu.trajectoryminer.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Date date1 = strToDate("2015-03-01 00:00:10", "yyyy-MM-dd HH:mm:ss");
		Date date2 = strToDate("2015-01-02 01:01:03", "yyyy-MM-dd HH:mm:ss");
		System.out.println(getHours(date1));
//		String time = "2015-01-02 01:01:26"; 
//		String[] timeArr = time.split(":");
//		int second1 = Integer.parseInt(timeArr[timeArr.length-1].substring(1, 2));
//		int second2 = Integer.parseInt(timeArr[timeArr.length-1].substring(0, 1));
//		if(second1<5)
//			time = time.substring(0, time.length()-2)+second2+"0";
//		else
//			time = time.substring(0, time.length()-2)+(second2+1)+"0";
//		System.out.println(time);
	}

	public static String calculateTime(long start) {
		return calculateTime(start, System.currentTimeMillis(),
				"${d}d ${h}h${m}m${s}s");
	}

	public static String calculateTime(long start, String format) {
		return calculateTime(start, System.currentTimeMillis(), format);
	}

	public static String calculateTime(long start, long end) {
		return calculateTime(start, end, "${d}d ${h}h${m}m${s}s");
	}

	public static String calculateTime(long start, long end, String format) {
		if (format == null)
			format = "${d}d ${h}h${m}m${s}s";
		long between = (end - start);// 得到两者的毫秒数
		long d = between / (24 * 60 * 60 * 1000);
		long h = (between / (60 * 60 * 1000) - d * 24);
		long m = ((between / (60 * 1000)) - d * 24 * 60 - h * 60);
		long s = (between / 1000 - d * 24 * 60 * 60 - h * 60 * 60 - m * 60);

		return format.replace("${d}", String.valueOf(d))
				.replace("${h}", String.valueOf(h))
				.replace("${m}", String.valueOf(m))
				.replace("${s}", String.valueOf(s));
	}

	public static Float toSeconds(String strTime) {
		Float time = 0F;
		for (String s : strTime.split(" ")) {
			time += _toSeconds(s);
		}

		return time;
	}

	private static Float _toSeconds(String strTime) {
		Float time = 0F;
		try {
			if (strTime.endsWith("s")) {
				time = Float.parseFloat(strTime.replace("s", "")) * 1;
			} else if (strTime.endsWith("m")) {
				time = Float.parseFloat(strTime.replace("m", "")) * 60;
			} else if (strTime.endsWith("h")) {
				time = Float.parseFloat(strTime.replace("h", "")) * 60 * 60;
			} else if (strTime.endsWith("d")) {
				time = Float.parseFloat(strTime.replace("d", "")) * 60 * 60 * 24;
			} else
				time = Float.parseFloat(strTime);
		} catch (Throwable e) {

		}

		return time;
	}

	public static long[] changeSecondsToTime(long seconds) {
		long hour = seconds / 3600;
		long minute = (seconds - hour * 3600) / 60;
		long second = (seconds - hour * 3600 - minute * 60);

		return new long[] { hour, minute, second };
	}

	public static int getDayOfYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		return c.get(Calendar.DAY_OF_YEAR);
	}

	public static int getLastDayOfYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		return c.getActualMaximum(Calendar.DAY_OF_YEAR);
	}

	public static int getDayOfMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		return c.get(Calendar.DAY_OF_MONTH);
	}

	public static int getLastDayOfMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		return c.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	// 判断日期为星期几,0为星期六,依此类推
	public static int getDayOfWeek(Date date) {
		// 首先定义一个calendar，必须使用getInstance()进行实例化
		Calendar aCalendar = Calendar.getInstance();
		// 里面野可以直接插入date类型
		aCalendar.setTime(date);
		// 计算此日期是一周中的哪一天
		int x = aCalendar.get(Calendar.DAY_OF_WEEK);
		return x;
	}

	public static int getLastDayOfWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		return c.getActualMaximum(Calendar.DAY_OF_WEEK);
	}

	public static long difference(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);

		if (cal2.after(cal1)) {
			return cal2.getTimeInMillis() - cal1.getTimeInMillis();
		}

		return cal1.getTimeInMillis() - cal2.getTimeInMillis();
	}

	public static Date addSecond(Date source, int s) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(source);
		cal.add(Calendar.SECOND, s);

		return cal.getTime();
	}

	public static Date addMinute(Date source, int min) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(source);
		cal.add(Calendar.MINUTE, min);

		return cal.getTime();
	}

	public static Date addHour(Date source, int hour) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(source);
		cal.add(Calendar.HOUR_OF_DAY, hour);

		return cal.getTime();
	}

	public static Date addDay(Date source, int day) {
		return addDate(source, day);
	}

	public static Date addDate(Date source, int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(source);
		cal.add(Calendar.DAY_OF_MONTH, day);

		return cal.getTime();
	}

	public static Date addMonth(Date source, int month) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(source);
		cal.add(Calendar.MONTH, month);

		return cal.getTime();
	}

	public static Date addYear(Date source, int year) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(source);
		cal.add(Calendar.YEAR, year);

		return cal.getTime();
	}

	public static Date parse(String format, String source) {
		return parse(format, source, Locale.getDefault());
	}

	public static Date parse(String format, String source, Locale locale) {
		int aaIndex = format.indexOf(" aa");
		if (aaIndex > -1) {
			String apm = source.substring(aaIndex + 1, aaIndex + 1 + 2);
			format = format.replace(" aa", "");
			return parse(format, source.substring(0, aaIndex), apm, locale);
		}

		SimpleDateFormat sdf = new java.text.SimpleDateFormat(format, locale);
		try {
			return sdf.parse(source);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static Date parse(String format, String source, String amOrPm) {
		return parse(format, source, amOrPm, Locale.getDefault());
	}

	public static Date parse(String format, String source, String amOrPm,
			Locale locale) {
		SimpleDateFormat sdf = new java.text.SimpleDateFormat(format, locale);
		try {
			Date date = sdf.parse(source);
			int HH = CommonUtils.toInt(formatTime("HH", date));
			if ("PM".equalsIgnoreCase(amOrPm)) {
				if (HH <= 12)
					date = addHour(date, 12);
			} else if ("AM".equalsIgnoreCase(amOrPm)) {
				if (HH >= 12)
					date = addHour(date, -12);
			}
			return date;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static Date parse(String source) {
		return parse(source, Locale.getDefault());
	}

	public static Date parse(String source, Locale locale) {
		return parse("yyyy-MM-dd HH:mm:ss", source, locale);
	}

	/**
	 * 格式化时间 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String formatTime(Date date) {
		return formatTime(null, date);
	}

	/**
	 * 格式化时间
	 * 
	 * @param format
	 *            格式，默认yyyy-MM-dd HH:mm:ss
	 * @param date
	 * @return
	 */
	public static String formatTime(String format, Date date) {
		if (format == null) {
			format = "yyyy-MM-dd HH:mm:ss";
		}

		String time = new java.text.SimpleDateFormat(format).format(date);
		return time;
	}

	public static Date newDate() {
		return new Date();
	}

	public static Date newDate(String pattern, String time) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.parse(time);
		} catch (ParseException e) {
			throw new RuntimeException();
		}
	}

	public static Date strToDate(String source, String pattern) {
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		try {
			date = format.parse(source);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static Date strToDate(String source) {
		return strToDate(source, "yyyy-MM-dd HH:mm:ss");
	}
	

	public static String dateToStr(Date source, String pattern) {
		String result = null;
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		result = format.format(source);
		return result;
	}

	public static String dateToStr(Date source) {
		return dateToStr(source,"yyyy-MM-dd HH:mm:ss");
	}
	
	public static String resoveTime(final String time) {
		String[] array = time.split(":");
		StringBuilder sb = new StringBuilder();
		for (String a : array) {
			if (sb.length() > 0)
				sb.append(":");

			if (a.length() == 1)
				a = new StringBuilder("0").append(a).toString();

			sb.append(a);
		}

		return sb.toString() + ":00";
	}

	public static Date resoveDate(final String date) throws Exception {
		Date d = null;
		try {
			d = parse("yyyy-MM-dd", date);
		} catch (Throwable e1) {
			try {
				d = parse("yyyy-M-dd", date);
			} catch (Throwable e2) {
				try {
					d = parse("yyyy-MM-d", date);
				} catch (Throwable e3) {
					try {
						d = parse("yyyy-M-d", date);
					} catch (Throwable e4) {
						try {
							d = parse("MM/dd/yyyy", date);
						} catch (Throwable e5) {
							try {
								d = parse("MM/d/yyyy", date);
							} catch (Throwable e6) {
								try {
									d = parse("M/dd/yyyy", date);
								} catch (Throwable e7) {
									try {
										d = parse("M/d/yyyy", date);
									} catch (Throwable e8) {
										throw new Exception(e8);
									}
								}
							}
						}
					}
				}
			}
		}

		return d;
	}

	public static boolean isValidTime(String str) {
		return str.matches("^\\d{2}:\\d{2}:\\d{2}$");
	}

	public static boolean isValidDate(String str) {
		return str != null ? str
				.matches("^\\d{4}(\\-|\\/|\\.)\\d{1,2}\\1\\d{1,2}$") : false;
	}

	public static boolean isValidDateTime(String source) {
		return isValidDateTime(source, Locale.getDefault());
	}

	public static boolean isValidDateTime(String source, Locale locale) {
		return isValidDateTime(source, "yyyy-MM-dd HH:mm:ss", locale);
	}

	public static boolean isValidDateTime(String source, String format) {
		return isValidDateTime(source, format, Locale.getDefault());
	}

	public static boolean isValidDateTime(String source, String format,
			Locale locale) {
		try {
			Date date = parse(format, source, locale);
			return date != null;
		} catch (Throwable e) {
			return false;
		}
	}

	public static int getYears(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}

	public static int getMonths(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH)+1;
	}

	public static int getDays(Date date, int type) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(type);
	}

	public static int getHours(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	public static int getMinute(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MINUTE);
	}

	public static int getSecond(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.SECOND);
	}

	public static Long getNow() {
		return System.currentTimeMillis();
	}

	public static Long getNow(int length) {
		return getTime(length, new Date());
	}

	public static Long getTime(int length, Date date) {
		String time = String.valueOf(date.getTime()).substring(0, length);
		return Long.parseLong(time);
	}

	/**
	 * @功能 取得当前时间,给定格式
	 * @return
	 */
	public static String getNowTime(String format, Locale loc) {
		if (format == null) {
			format = "yyyy-MM-dd HH:mm:ss";
		}

		if (loc == null)
			return new java.text.SimpleDateFormat(format)
					.format(java.util.Calendar.getInstance().getTime());

		return new java.text.SimpleDateFormat(format, loc)
				.format(java.util.Calendar.getInstance().getTime());
	}

	public static String getNowTime(String format) {
		return getNowTime(format, null);
	}

	/**
	 * @功能 取得当前时间
	 * @return
	 */
	public static String getNowTime() {
		return getNowTime(null);
	}
}
