package com.example.demo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	
	
}
