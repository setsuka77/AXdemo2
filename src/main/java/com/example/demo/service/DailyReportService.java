package com.example.demo.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
	
	/**
	 * 日報登録情報を取得
	 * 
	 * @param loginUser
	 * @param selectDate
	 * @return 指定されたユーザーIDと日付に一致する日報申請情報
	 */
	public List<DailyReportDetail> searchReport(Users loginUser,String selectDate){
		Integer userId = loginUser.getId();
		Date submitDate= Date.valueOf(selectDate);
		
		//日報情報を検索してIDを取得
		List<DailyReportDetail> searchReport = dailyReportDetailMapper.findByUserIdAndDate(userId, submitDate);

		return searchReport;
	}
	
	/**
	 * 日報ステータスの取得
	 * 
	 * @param loginUser
	 * @param selectDate
	 * @return 日報ステータス
	 */
	public Integer searchReportStatus(Users loginUser, String selectDate) {
		Integer userId = loginUser.getId();
		Date submitDate = Date.valueOf(selectDate);

		// loginUserとdateで申請があるか確認
		DailyReport searchReport = dailyReportMapper.findByUserIdAndDate(userId, submitDate);
		
		DailyReport report = new DailyReport();
		if(searchReport == null) {
			report.setStatus(0);
		}else {
			report.setStatus(searchReport.getStatus());
		}
		
		return report.getStatus();
	}
	
	/**
     * 入力チェックメソッド
     * @param dailyReportForm
     * @param selectDate
     * @return エラーメッセージ（エラーがない場合は空文字列）
     */
	public String validateDailyReport(DailyReportForm dailyReportForm, String selectDate) {
		  Set<String> errorMessages = new LinkedHashSet<>();

		//対象日付のチェック
		if (selectDate == null|| selectDate.isEmpty()) {
			errorMessages.add("日付が選択されていません。");
		}
		//対象日付が現在の日付より未来かどうかのチェック
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate selectedDate = LocalDate.parse(selectDate, formatter);
        LocalDate today = LocalDate.now();
        if (selectedDate.isAfter(today)) {
            errorMessages.add("本日か過去の日付を選択してください。");
        }

		//作業時間と作業内容のチェック
		List<DailyReportDetailForm> dailyReportDetailFormList = dailyReportForm.getDailyReportDetailFormList();
		for (DailyReportDetailForm detailForm : dailyReportDetailFormList) {
		    boolean hasTime = detailForm.getTime() != null;
		    boolean hasContent = detailForm.getContent() != null && !detailForm.getContent().isEmpty();

		    // 片方だけ入力されている場合
		    if (hasTime && !hasContent) {
		        errorMessages.add("作業内容が入力されていません。");
		        detailForm.setErrorFlag(true);
		    } else if (!hasTime && hasContent) {
		        errorMessages.add("作業時間が入力されていません。");
		        detailForm.setErrorFlag(true);
		    }
		    
		    //50字以上入力されている場合
		    if (hasContent && detailForm.getContent().length() > 50) {
		        errorMessages.add("作業内容は50字以内で入力してください。");
		        detailForm.setErrorFlag(true);
		    }
		    
		}
		return String.join("<br>", errorMessages);
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
				
				//日報情報を検索してIDを取得
				//DailyReportDetail searchReport = dailyReportDetailMapper.findByReport(userId, submitDate);
				
				//新しい日報オブジェクトを作成
				DailyReportDetail dailyReportDetail = new DailyReportDetail();
				//dailyReportDetail.setId(searchReport != null ? searchReport.getId() : null);
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
	 * 
	 * @param loginUser
	 * @param selectDate
	 * @param status
	 * @return 申請結果
	 * 
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
