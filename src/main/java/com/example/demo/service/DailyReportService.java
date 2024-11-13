package com.example.demo.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.example.demo.dto.DailyReportDto;
import com.example.demo.dto.NotificationsDto;
import com.example.demo.dto.TaskTypeDto;
import com.example.demo.dto.UsersDto;
import com.example.demo.entity.DailyReport;
import com.example.demo.entity.DailyReportDetail;
import com.example.demo.entity.TaskType;
import com.example.demo.entity.Users;
import com.example.demo.form.DailyReportDetailForm;
import com.example.demo.form.DailyReportForm;
import com.example.demo.form.TaskTypeForm;
import com.example.demo.mapper.DailyReportDetailMapper;
import com.example.demo.mapper.DailyReportMapper;
import com.example.demo.mapper.TaskTypeMapper;

import jakarta.validation.Valid;

@Service
public class DailyReportService {

	@Autowired
	private DailyReportDetailMapper dailyReportDetailMapper;
	@Autowired
	private DailyReportMapper dailyReportMapper;
	@Autowired
	private NotificationsService notificationsService;
	@Autowired
	private TaskTypeMapper taskTypeMapper;

	/**
	 * ステータスが1の申請一覧を取得する
	 * @return ステータスが1の月次勤怠申請のリスト
	 */
	public List<DailyReportDto> findAllReport() {
		return dailyReportMapper.findByStatus();
	}

	/**
	 * 日報登録情報を取得
	 * 
	 * @param loginUser
	 * @param selectDate
	 * @return 指定されたユーザーIDと日付に一致する日報申請情報
	 */
	public List<DailyReportDetail> searchReport(Users loginUser, String selectDate) {
		Integer userId = loginUser.getId();
		Date submitDate = Date.valueOf(selectDate);

		//日報情報を検索してIDを取得
		List<DailyReportDetail> searchReport = dailyReportDetailMapper.findByUserIdAndDate(userId, submitDate);

		return searchReport;
	}

	/**
	 * 現在日から過去一週間の日報情報を取得
	 * 
	 * @param loginUser
	 * @param start
	 * @param end
	 * @return
	 */
	public List<DailyReportDetail> searchReport(Users loginUser, LocalDate start, LocalDate end) {
		return dailyReportDetailMapper.findByUserIdAndRange(loginUser.getId(), start, end);
	}

	/**
	 * 作業種別用プルダウン情報取得
	 * @return 作業種別用のリスト
	 */
	public List<TaskTypeDto> findAll(Integer userId) {
		// データベースから全てのレコードを取得
	    List<TaskTypeDto> taskTypeList = taskTypeMapper.findAll(userId);
	    
	    // listNumberを基準に昇順でソート
	    return taskTypeList.stream()
	                       .sorted(Comparator.comparingInt(TaskTypeDto::getListNumber))
	                       .collect(Collectors.toList());
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
		if (searchReport == null) {
			report.setStatus(0);
		} else {
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
		if (selectDate == null || selectDate.isEmpty()) {
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
	public String submitDailyReport(DailyReportForm dailyReportForm, Users loginUser, String selectDate) {
		List<DailyReportDetailForm> dailyReportDetailFormList = dailyReportForm.getDailyReportDetailFormList();
		Integer userId = loginUser.getId();
		Date submitDate = Date.valueOf(selectDate);

		for (DailyReportDetailForm dailyForm : dailyReportDetailFormList) {
			if (dailyForm.getTime() != null && dailyForm.getContent() != null) {

				//新しい日報オブジェクトを作成
				DailyReportDetail dailyReportDetail = new DailyReportDetail();
				dailyReportDetail.setId(dailyForm.getId());
				dailyReportDetail.setUserId(userId);
				dailyReportDetail.setDate(submitDate);
				dailyReportDetail.setTime(dailyForm.getTime());
				dailyReportDetail.setContent(dailyForm.getContent());
				dailyReportDetail.setWorkTypeId(dailyForm.getWorkTypeId());
				System.out.println("確認用"+ dailyReportDetail);

				//idが存在しない場合は新規登録
				if (dailyReportDetail.getId() == null || dailyReportDetail.getId() == 0) {
					//日報情報を登録
					dailyReportDetailMapper.insert(dailyReportDetail);
					System.out.println("インサートできてる");
				} else {
					//日報情報を更新
					dailyReportDetailMapper.update(dailyReportDetail);
					System.out.println("アップデートできてる");
				}
			}
			if (dailyForm.getTime() == null && (dailyForm.getContent() == null || dailyForm.getContent().isEmpty())
					&& dailyForm.getId() != null) {
				DailyReportDetail dailyReportDetail = new DailyReportDetail();
				dailyReportDetail.setId(dailyForm.getId());

				//日報情報を削除
				dailyReportDetailMapper.delete(dailyReportDetail);
				System.out.println("削除できてる");
			}
			// 既存の UserNotifications を userId と targetDate,notificationType で検索
			String notificationType = "日報未提出";
			notificationsService.checkNotifications(userId, submitDate, notificationType);
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
	public Boolean changeStatus(Users loginUser, String selectDate, int status) {
		Integer userId = loginUser.getId();
		Date submitDate = Date.valueOf(selectDate);

		//loginUserとdateで申請があるか確認
		DailyReport searchReport = dailyReportMapper.findByUserIdAndDate(userId, submitDate);

		//新しい日報オブジェクトを作成
		DailyReport report = new DailyReport();
		report.setId(searchReport != null ? searchReport.getId() : null);
		report.setUserId(userId);
		report.setDate(submitDate);
		report.setStatus(status);
		report.setUpdateDate(java.sql.Date.valueOf(LocalDate.now()));

		if (report.getId() == null) {
			//日報申請を登録
			dailyReportMapper.insert(report);
		} else {
			//日報申請を更新
			dailyReportMapper.update(report);
		}
		return true;
	}

	/**
	 * 指定されたユーザーIDと日付に基づいて日報フォームを設定する。
	 * 
	 * @param userId ユーザーのID。
	 * @param date 日報の対象日付。
	 * @return 指定されたユーザーと日付に関連する日報の詳細を含むオブジェクト。
	 */
	public DailyReportForm setForm(Integer userId, Date date) {
		// 日報情報を取得
		List<DailyReportDetail> details = dailyReportDetailMapper.findByUserIdAndDate(userId, date);

		// DailyReportForm のインスタンスを作成
		DailyReportForm dailyReportForm = new DailyReportForm();

		// DailyReportDetail を DailyReportDetailForm に変換する
		List<DailyReportDetailForm> detailFormList = new ArrayList<>();
		for (DailyReportDetail detail : details) {
			DailyReportDetailForm detailForm = new DailyReportDetailForm();
			detailForm.setId(detail.getId());
			detailForm.setUserId(detail.getUserId());
			detailForm.setDate(detail.getDate());
			detailForm.setTime(detail.getTime());
			detailForm.setContent(detail.getContent());
			detailForm.setWorkTypeId(detail.getWorkTypeId());
			detailFormList.add(detailForm);
		}

		// DailyReportForm に変換したリストを設定
		dailyReportForm.setDailyReportDetailFormList(detailFormList);

		return dailyReportForm;
	}

	/**
	 * 日報提出の有無確認(ユーザー用)
	 * 
	 */
	public void checkDailyReport() {
		// 前日の日付を取得
		LocalDate previousDay = LocalDate.now().minusDays(1);
		Date date = Date.valueOf(previousDay);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日");
		String formattedDate = previousDay.format(formatter);

		//土日かどうかチェック
		Boolean weekEnd = notificationsService.notWeekend(previousDay);
		if (weekEnd) {
			// 前日の日報を提出していないユーザーを検索
			List<UsersDto> users = dailyReportMapper.findUsersWithoutReport(date);

			// 日報未提出の通知を作成し、全ユーザーに通知を紐付け
			String notificationType = "日報未提出";
			String content = formattedDate + "の日報が提出されていません";
			notificationsService.createNotificationForUsers(users, previousDay, notificationType, content, date);
		}
	}

	/**
	 * 日報提出の有無確認(マネージャー用)
	 * 
	 * @return お知らせ情報
	 */
	public List<NotificationsDto> checkManagerDailyReport() {
		// 前日の日付を取得
		LocalDate previousDay = LocalDate.now().minusDays(1);
		Date date = Date.valueOf(previousDay);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日");
		String formattedDate = previousDay.format(formatter);

		// 土日かどうかチェック
		Boolean weekEnd = notificationsService.notWeekend(previousDay);

		List<NotificationsDto> notifications = new ArrayList<>();
		if (weekEnd) {
			// 前日の日報を提出していないユーザーを検索
			List<UsersDto> usersWithoutReport = dailyReportMapper.findUsersWithoutReport(date);

			// マネージャーに未提出ユーザー数のお知らせを作成
			int missingReportCount = usersWithoutReport.size();
			if (missingReportCount > 0) {
				String managerContent = formattedDate + "の日報が" + missingReportCount + "人未提出です ("
						+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "時点" + ")";

				// マネージャーの日報未提出通知を作成
				NotificationsDto notificationsDto = new NotificationsDto();
				notificationsDto.setContent(managerContent);
				notificationsDto.setNotificationType("日報未提出");
				notificationsDto.setCreatedAt(LocalDateTime.now());

				// 未提出ユーザーのリストを設定
				notificationsDto.setUsers(usersWithoutReport);

				notifications.add(notificationsDto);
			}
		}

		return notifications;
	}

	/**
	 * DtoをFormにセットする
	 * 
	 * @param taskTypes
	 * @return TaskTypeFormのリスト
	 */
	public List<TaskTypeForm> getTaskTypeForms(List<TaskTypeDto> taskTypes) {
		// taskTypeDtoをtaskTypeFormに変換し、listNumberでソート
	    return taskTypes.stream()
	        .map(taskType -> {
	            TaskTypeForm taskTypeForm = new TaskTypeForm();
	            taskTypeForm.setWorkTypeId(taskType.getWorkTypeId());
	            taskTypeForm.setListNumber(taskType.getListNumber());
	            taskTypeForm.setWorkTypeName(taskType.getWorkTypeName());
	            return taskTypeForm;
	        })
	        .sorted(Comparator.comparing(TaskTypeForm::getListNumber))  // listNumber順に並べ替え
	        .collect(Collectors.toList());
	}

	/**
	 * 登録前入力チェック
	 * 
	 * @param dailyReportForm
	 * @param bindingResult
	 * @return エラーメッセージ
	 */
	public String validTaskType(DailyReportForm dailyReportForm, BindingResult bindingResult) {
		Set<String> errorMessages = new LinkedHashSet<>();
		List<TaskTypeForm> taskTypeFormList = dailyReportForm.getTaskTypeFormList();

		// listNumberとworkTypeNameの重複を確認
		Set<Integer> listNumberSet = new HashSet<>();
		Set<String> workTypeNameSet = new HashSet<>();

		for (TaskTypeForm taskTypeForm : taskTypeFormList) {
			// listNumberの重複チェック
			if (!listNumberSet.add(taskTypeForm.getListNumber())) {
				errorMessages.add("表示順番が重複しています");
			}

			// workTypeNameの重複チェック
			if (!workTypeNameSet.add(taskTypeForm.getWorkTypeName())) {
				errorMessages.add("作業種名が重複しています");
			}

			//新規nullチェック
			if(taskTypeForm.getListNumber()== null && taskTypeForm.getWorkTypeName().isEmpty()) {
				errorMessages.add("リスト番号と作業タイプ名の両方を入力してください。");
			}
		}
		// BindingResultのエラーメッセージを取得
		for (ObjectError error : bindingResult.getAllErrors()) {
			// FieldErrorのエラーメッセージを追加
			if (error instanceof FieldError) {
				errorMessages.add(error.getDefaultMessage());
			}
		}
		return String.join("<br>", errorMessages);
	}

	/**
	 * 作業タイプ　登録処理
	 * 
	 * @param userId
	 * @param dailyReportForm
	 * @return　完了メッセージ
	 */
	@Transactional
	public String registEditType(Integer userId, @Valid DailyReportForm dailyReportForm) {
		List<TaskTypeForm> taskTypeFormList = dailyReportForm.getTaskTypeFormList();
		
        List<TaskType> taskTypes = taskTypeFormList.stream()
            .map(form -> {
                TaskType taskType = new TaskType();
                taskType.setWorkTypeId(form.getWorkTypeId() != null ? form.getWorkTypeId() : null);  // IDが存在すれば更新、なければnullで
                taskType.setUserId(userId);
                taskType.setListNumber(form.getListNumber());
                taskType.setWorkTypeName(form.getWorkTypeName());
                
                return taskType;
            })
            .collect(Collectors.toList());
        //upsert 処理を行う
        taskTypeMapper.upsert(taskTypes);
		return "編集内容を保存しました。";
	}

}
