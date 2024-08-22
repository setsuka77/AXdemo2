package com.example.demo.service;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.DailyReport;
import com.example.demo.entity.DailyReportDetail;
import com.example.demo.entity.Users;
import com.example.demo.form.DailyReportDetailForm;
import com.example.demo.form.DailyReportForm;
import com.example.demo.mapper.DailyReportDetailMapper;
import com.example.demo.mapper.DailyReportMapper;

@Service
public class DailyReportService {
	
	@Autowired
	private DailyReportDetailMapper dailyReportDetailMapper;
	@Autowired
	private DailyReportMapper dailyReportMapper;
	
	/*
	 * 上部ステータス表示の設定
	 */
	/*public String getStatusText(Users loginUser) {
		
		if(loginUser.isEmpty()) {
			return "未提出";
		}
		
		DailyReport reportDto = 
	}*/
	
	/**
     * 入力チェックメソッド
     * @param dailyReportForm
     * @param selectDate
     * @return エラーメッセージ（エラーがない場合は空文字列）
     */
	public String validateDailyReport(DailyReportForm dailyReportForm, String selectDate) {
		StringBuilder errorMessages = new StringBuilder();

		//対象日付のチェック
		if (selectDate == null || selectDate.isEmpty()) {
			errorMessages.append("日付が選択されていません。<br>");
		}

		//作業時間と作業内容のチェック
		List<DailyReportDetailForm> dailyReportDetailFormList = dailyReportForm.getDailyReportDetailFormList();
		for (DailyReportDetailForm detailForm : dailyReportDetailFormList) {
		    boolean hasTime = detailForm.getTime() != null;
		    boolean hasContent = detailForm.getContent() != null && !detailForm.getContent().isEmpty();

		    // 片方だけ入力されている場合
		    if (hasTime && !hasContent) {
		        errorMessages.append("作業内容が入力されていません。<br>");
		    } else if (!hasTime && hasContent) {
		        errorMessages.append("作業時間が入力されていません。<br>");
		    }
		}
		return errorMessages.toString();
	}
	
	
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
					dailyReportDetailMapper.insert(dailyReportDetail);
				}else {
					//日報情報を更新
					dailyReportDetailMapper.update(dailyReportDetail);
				}
			}
		}
		return "日報が提出されました。";
	}
	
	/**
	 * ステータス変更
	 */
	public Boolean changeStatus(Users loginUser, String selectDate,int status) {
		Integer userId = loginUser.getId();
		Date submitDate= Date.valueOf(selectDate);
		
		//loginUserとdateで申請があるか確認
		DailyReport searchReport = dailyReportMapper.findByUserIdAndDate(userId, submitDate);
		
		//新しい日報オブジェクトを作成
		DailyReport report = new DailyReport();
		report.setId(searchReport != null ? searchReport.getId() : null);
		report.setUserId(userId);
		report.setDate(submitDate);
		//daily_reportのstatusを変更する
		report.setStatus(status);
		
		if(report.getId() == null) {
			//日報申請を登録
			dailyReportMapper.insert(report);
		}else {
			//日報申請を更新
			dailyReportMapper.update(report);
		}
		return true;
	}

}
