package com.example.demo.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.Data;
@Data
public class CalendarDto {
	
	/**日付*/
	private LocalDate date;
	/**曜日*/
	private String dayOfWeek;
	
	public CalendarDto(LocalDate date,String dayOfWeek) {
        this.date = date;
        this.dayOfWeek = dayOfWeek;
    }

	// 日付を M/d 形式で表示するためのメソッド
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d");
        return date.format(formatter);
    }
    
    //土日祝日のメソッドを追加
    private boolean isSaturday;
    private boolean isSunday;
}
