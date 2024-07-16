package com.example.demo.util;
 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
 
public class DateUtil {
	/**
	 * Date型日付を任意の日付形式にフォーマット
	 * 
	 * @param date
	 * @param fmt
	 * @return 任意の形式の日付
	 */
	public String toString(Date date, String fmt) {
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		return sdf.format(date);
	}
	/**
	 * String型日付を任意の日付形式にフォーマット
	 * 
	 * @param date
	 * @param fmt
	 * @return 任意の形式の日付
	 */
	public Date parse(String date, String fmt) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		return sdf.parse(date);
	}
 
	/**
	 * 曜日を取得する
	 *
	 * @param baseDate 基準日
	 * @return 基準日の曜日
	 */
	public static String getDayOfWeek(LocalDate baseDate) {
		if (baseDate == null) {
			return "";
		}
 
		DayOfWeek dayOfWeek = baseDate.getDayOfWeek();
		switch (dayOfWeek) {
	      case SUNDAY:
	        return "日";
	      case MONDAY:
	        return "月";
	      case TUESDAY:
	        return "火";
	      case WEDNESDAY:
	        return "水";
	      case THURSDAY:
	        return "木";
	      case FRIDAY:
	        return "金";
	      case SATURDAY:
	        return "土";
	      default:
	        return "";
	    }
	}
}