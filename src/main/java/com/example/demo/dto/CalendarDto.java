package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
@Data
public class CalendarDto {
	
	//日付DTOリスト
	private List<CalendarDto> calendarDto;
	/**日付*/
	private LocalDate date;
	/**曜日*/
	private String dayOfWeek;
	
	public CalendarDto(LocalDate date,String dayOfWeek) {
        this.date = date;
        this.dayOfWeek = dayOfWeek;
    }
	
}
