package com.example.demo.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.DailyReportDetail;
import com.example.demo.entity.Users;
import com.example.demo.mapper.DailyReportDetailMapper;

@Service
public class DailyReportListService {

	@Autowired
	private DailyReportDetailMapper dailyReportDetailMapper;

	private Map<LocalDate, Map<Integer, List<DailyReportDetail>>> groupedReport = new TreeMap<>();

	public List<DailyReportDetail> searchReport(Users loginUser, LocalDate start, LocalDate end) {
	    return dailyReportDetailMapper.findByUserIdAndRange(loginUser.getId(), start, end);
	}

	public void addWorkContent(LocalDate date, DailyReportDetail reportDetail) {
		 // 指定された日付のマップを取得
	    Map<Integer, List<DailyReportDetail>> workTypeMap = groupedReport.computeIfAbsent(date, k -> new HashMap<>());
	    System.out.println("1"+workTypeMap);
	    // 指定された作業種別IDのリストを取得
	    List<DailyReportDetail> reportDetails = workTypeMap.computeIfAbsent(reportDetail.getWorkTypeId(), k -> new ArrayList<>());
	    System.out.println("2"+reportDetails);
	    // reportDetailのidがすでにリストに存在するかチェック
	    boolean exists = reportDetails.stream()
	        .anyMatch(existingDetail -> existingDetail.getId().equals(reportDetail.getId()));

	    // 存在しない場合のみreportDetailを追加
	    if (!exists) {
	        reportDetails.add(reportDetail);
	    }
	}

	public Map<LocalDate, Map<Integer, List<DailyReportDetail>>> getGroupedReport() {
	    return groupedReport;
	}


}
