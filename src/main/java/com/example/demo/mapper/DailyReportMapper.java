package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.entity.DailyReportDetail;

@Mapper
public interface DailyReportMapper {
	
	/*
	 * 提出ボタン押下　日報登録
	 */
	void insert(DailyReportDetail dailyReportDetail);
	
	/*
	 * 提出ボタン押下　日報更新
	 */
	void update(DailyReportDetail dailyReportDetail);

}
