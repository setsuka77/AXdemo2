package com.example.demo.service;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.DailyReportDetail;
import com.example.demo.entity.Users;
import com.example.demo.form.DailyReportDetailForm;
import com.example.demo.form.DailyReportForm;
import com.example.demo.mapper.DailyReportMapper;

@Service
public class DailyReportService {
	
	@Autowired
	private DailyReportMapper dailyReportMapper;
	
   /**
	* 日報登録画面　日報登録処理
	* 
	* @param dailyReportForm 
	* @param loginUser 
	* @param selectDate 
	* @return 処理結果のメッセージ
	*/
	public String submitDailyReport(DailyReportForm dailyReportForm, Users loginUser,String selectDate) {
		List<DailyReportDetailForm> dailyReportDetailFormList = dailyReportForm.getDailyReportDetailFormList();
		Integer userId = loginUser.getId();
		Date submitDate= Date.valueOf(selectDate);
		
		for(DailyReportDetailForm dailyForm : dailyReportDetailFormList) {
			if(dailyForm.getTime() != null && dailyForm.getContent() != null) {
				DailyReportDetail dailyReportDetail = new DailyReportDetail();
				dailyReportDetail.setId(null);
				dailyReportDetail.setUserId(userId);
				dailyReportDetail.setDate(submitDate);
				dailyReportDetail.setTime(dailyForm.getTime());
				dailyReportDetail.setContent(dailyForm.getContent());
				
				//idが存在しない場合は新規登録
				if(dailyReportDetail.getId() == null) {
					//日報情報を登録
					dailyReportMapper.insert(dailyReportDetail);
				}else {
					//日報情報を更新
					dailyReportMapper.update(dailyReportDetail);
				}
			}
		}
		return "日報が提出されました。";
	}

}
