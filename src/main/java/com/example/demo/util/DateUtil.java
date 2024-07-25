package com.example.demo.util;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
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
     * LocalDate を Date に変換
     * 
     * @param localDate
     * @return Date
     */
    public Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Date を LocalDate に変換
     * 
     * @param date
     * @return LocalDate
     */
    public LocalDate dateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * LocalTime を String に変換
     * 
     * @param localTime
     * @return String
     */
    public String localTimeToString(LocalTime localTime) {
        return localTime != null ? localTime.toString() : "";
    }

    /**
     * String を LocalTime に変換
     * 
     * @param timeStr
     * @return LocalTime
     */
    public LocalTime stringToLocalTime(String timeStr) {
        return timeStr != null && !timeStr.isEmpty() ? LocalTime.parse(timeStr) : null;
    }
    
    /**
     * String を Time に変換
     * 
     * @param timeStr 時刻を表す文字列 (例: "HH:mm:ss")
     * @return Time 型のオブジェクト
     */
    public Time stringToTime(String timeStr) {
    	 if (timeStr == null || timeStr.isEmpty()) {
             return null;
         }
         try {
             SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
             java.util.Date parsedDate = sdf.parse(timeStr);
             return new Time(parsedDate.getTime());
         } catch (ParseException e) {
             e.printStackTrace();
             return null; // エラーハンドリングを適切に行ってください
         }
    }

    /**
     * Time を String に変換
     * 
     * @param time Time 型のオブジェクト
     * @return 時刻を表す文字列 (例: "HH:mm:ss")
     */
    public String timeToString(Time time) {
        if (time == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(time);
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
