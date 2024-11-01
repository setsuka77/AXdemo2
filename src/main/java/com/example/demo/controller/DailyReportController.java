package com.example.demo.controller;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.TasktypeDto;
import com.example.demo.entity.DailyReport;
import com.example.demo.entity.DailyReportDetail;
import com.example.demo.entity.Users;
import com.example.demo.form.DailyReportDetailForm;
import com.example.demo.form.DailyReportForm;
import com.example.demo.mapper.DailyReportMapper;
import com.example.demo.service.DailyReportListService;
import com.example.demo.service.DailyReportService;

import jakarta.servlet.http.HttpSession;

@Controller
public class DailyReportController {

	@Autowired
	private DailyReportService dailyReportService;
	@Autowired
	private DailyReportMapper dailyReportMapper;
	@Autowired
	private DailyReportListService dailyReportListService;

	/**
	 * 日報登録画面 初期表示
	 *
	 * @param model
	 * @param session
	 * @return 日報登録画面
	 */
	@GetMapping("/report/dailyReport")
	public String showDailyReport(Model model, HttpSession session) {
		// ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");
		String role = loginUser.getRole();

		// 初期表示用に3行の空のフォームを準備する
		List<DailyReportDetailForm> dailyReportDetailFormList = new ArrayList<>();
		if (!"2".equals(role)) {
			for (int i = 0; i < 3; i++) {
				dailyReportDetailFormList.add(new DailyReportDetailForm());
			}
		} else {
			//roleが2の時、申請一覧を表示
			model.addAttribute("dailyReport", dailyReportService.findAllReport());
			//却下と承認ボタンを非活性に設定
			model.addAttribute("checkReject", false);
			model.addAttribute("checkApproval", false);
		}

		//全ての作業種別情報を取得(プルダウン用)
		List<TasktypeDto> taskType = dailyReportService.findAll();
		model.addAttribute("taskType", taskType);

		// DailyReportForm を初期化してモデルに追加
		DailyReportForm dailyReportForm = new DailyReportForm();
		dailyReportForm.setDailyReportDetailFormList(dailyReportDetailFormList);

		model.addAttribute("loginUser", loginUser);
		model.addAttribute("dailyReportForm", dailyReportForm);
		return "report/dailyReport";
	}

	/**
	 * 日付が選択されたときに日報情報を取得して返す
	 *
	 * @param session
	 * @param selectDate
	 * @return 選択された日付の日報情報
	 */
	@ResponseBody
	@RequestMapping(value = "/report/dailyReport", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
	public ResponseEntity<Map<String, Object>> searchReport(HttpSession session,
			@RequestBody Map<String, String> requestBody) {
		// セッションからユーザー情報を取得
		Users loginUser = (Users) session.getAttribute("user");
		// リクエストボディから日付を取得
		String selectDate = requestBody.get("selectDate");

		// 日報情報を取得
		List<DailyReportDetail> reportDetail = dailyReportService.searchReport(loginUser, selectDate);

		//全ての作業種別情報を取得(プルダウン用)
		List<TasktypeDto> taskType = dailyReportService.findAll();

		// ステータスを取得
		Integer status = dailyReportService.searchReportStatus(loginUser, selectDate);
		String statusText = getStatusText(status);
		session.setAttribute("statusText", statusText);

		Map<String, Object> response = new HashMap<>();
		response.put("reportDetails", reportDetail);
		response.put("statusText", statusText);
		response.put("taskType", taskType);

		return ResponseEntity.ok(response);
	}

	/**
	 * 上部ステータス表示の設定
	 */
	public String getStatusText(Integer status) {
		if (status == null) {
			return "未申請";
		}

		switch (status) {
		case 1:
			return "提出済承認前";
		case 2:
			return "承認済み";
		default:
			return "未提出";
		}
	}

	/**
	 * お知らせから遷移した場合の画面
	 * 
	 * @param model
	 * @param session
	 * @param selectDate
	 * @return 日報登録画面
	 */
	@PostMapping(path = "/report/dailyReport", params = "display")
	public String displayReport(HttpSession session, String selectDate, Model model) {
		// selectDate が yyyy-MM-dd 形式でない場合に補正する
		String formattedDate = formatDate(selectDate);
		// セッションからユーザー情報を取得
		Users loginUser = (Users) session.getAttribute("user");

		// 初期表示用に3行の空のフォームを準備する
		List<DailyReportDetailForm> dailyReportDetailFormList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			dailyReportDetailFormList.add(new DailyReportDetailForm());
		}
		// DailyReportForm を初期化してモデルに追加
		DailyReportForm dailyReportForm = new DailyReportForm();
		dailyReportForm.setDailyReportDetailFormList(dailyReportDetailFormList);

		// ステータスを取得
		Integer status = dailyReportService.searchReportStatus(loginUser, formattedDate);
		String statusText = getStatusText(status);

		//全ての作業種別情報を取得(プルダウン用)
		List<TasktypeDto> taskType = dailyReportService.findAll();
		model.addAttribute("taskType", taskType);

		model.addAttribute("loginUser", loginUser);
		model.addAttribute("dailyReportForm", dailyReportForm);
		model.addAttribute("selectDate", formattedDate);
		model.addAttribute("statusText", statusText);

		return "report/dailyReport";
	}

	/**
	 * お知らせから遷移する場合、Dateのフォーマットを整える
	 * @param date
	 * @return フォーマット化されたDate
	 */
	private String formatDate(String date) {
		String[] parts = date.split("-");
		if (parts.length == 3) {
			String year = parts[0];
			String month = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
			String day = parts[2].length() == 1 ? "0" + parts[2] : parts[2];
			return year + "-" + month + "-" + day;
		}
		return date;
	}

	/**
	 * 「提出」ボタン押下
	 * 
	 * @param session
	 * @param dailyReportForm
	 * @param model
	 * @param selectDate
	 * @return 日報登録画面
	 */
	@PostMapping(path = "/report/dailyReport", params = "submit")
	public String submitReport(HttpSession session, DailyReportForm dailyReportForm, BindingResult result,
			Model model, String selectDate) {
		// ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");

		// 入力チェック
		String validationErrors = dailyReportService.validateDailyReport(dailyReportForm, selectDate);

		// エラーが存在する場合
		if (!validationErrors.isEmpty()) {
			// ステータスを取得
			String statusText = (String) session.getAttribute("statusText");

			model.addAttribute("registerError", validationErrors);
			model.addAttribute("loginUser", loginUser);
			model.addAttribute("dailyReportForm", dailyReportForm);
			model.addAttribute("statusText", statusText);
			model.addAttribute("selectDate", selectDate);
			return "report/dailyReport";
		}

		// 登録処理
		String message = dailyReportService.submitDailyReport(dailyReportForm, loginUser, selectDate);
		model.addAttribute("message", message);
		model.addAttribute("loginUser", loginUser);
		model.addAttribute("dailyReportForm", dailyReportForm);
		model.addAttribute("selectDate", selectDate);
		model.addAttribute("statusText", "提出済承認前");
		// status変更
		dailyReportService.changeStatus(loginUser, selectDate, 1);

		//全ての作業種別情報を取得(プルダウン用)
		List<TasktypeDto> taskType = dailyReportService.findAll();
		model.addAttribute("taskType", taskType);

		return "report/dailyReport";
	}

	/**
	 * 日報登録画面 マネージャ権限 日報申請の詳細表示
	 * 
	 * @param id      日報ID
	 * @param model
	 * @param session
	 * @return 日報登録画面
	 */
	@GetMapping(path = "/report/dailyReport/detail")
	public String getDailyReport(@RequestParam("id") Integer id, Model model, HttpSession session) {
		// ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");

		// 申請Idで日報情報を取得
		DailyReport dailyReport = dailyReportMapper.findById(id);
		// 日報情報から日付とユーザーIDを取得
		Integer userId = dailyReport.getUserId();
		java.util.Date utilDate = dailyReport.getDate();
		java.sql.Date date = new java.sql.Date(utilDate.getTime());

		// 日報情報を取得
		DailyReportForm dailyReportForm = dailyReportService.setForm(userId, date);

		// モデルに属性を追加
		model.addAttribute("dailyReport", dailyReport);
		model.addAttribute("dailyReportForm", dailyReportForm);
		model.addAttribute("loginUser", loginUser);

		//全ての作業種別情報を取得(プルダウン用)
		List<TasktypeDto> taskType = dailyReportService.findAll();
		model.addAttribute("taskType", taskType);

		return "report/dailyReport";
	}

	/**
	 * 日報一覧画面　初期表示
	 * 
	 * @param model
	 * @param session
	 * @return 日報一覧画面
	 */
	@GetMapping("/report/dailyReportList")
	public String showReportList(Model model, HttpSession session) {
		// セッションからユーザー情報を取得
		Users loginUser = (Users) session.getAttribute("user");
		model.addAttribute("loginUser", loginUser);

		// 現在から過去一週間分の日報を取得
		LocalDate end = LocalDate.now();
		LocalDate start = end.minusWeeks(1);
		model.addAttribute("startDate", start.toString());
		model.addAttribute("endDate", end.toString());
		System.out.println(end);

		// 日報のリストを取得
		List<DailyReportDetail> reportDetailList = dailyReportListService.searchReport(loginUser, start, end);
		// 日付とIDでソート
		Collections.sort(reportDetailList, Comparator.comparing(DailyReportDetail::getDate)
				.thenComparing(DailyReportDetail::getWorkTypeId));
		System.out.println(reportDetailList);
		//その日にいくつ日報があるかカウント
		Map<Date, Long> countByDate = reportDetailList.stream()
				.collect(Collectors.groupingBy(DailyReportDetail::getDate, Collectors.counting()));
		// 日付ごとにグループ化し、各日付の中で作業種別IDをカウント
		Map<Date, Map<Integer, Long>> countByDateAndWorkId = reportDetailList.stream()
				.collect(Collectors.groupingBy(DailyReportDetail::getDate,
						Collectors.groupingBy(DailyReportDetail::getWorkTypeId, Collectors.counting())));

		System.out.println("countByDateAndWorkId" + countByDateAndWorkId);
		model.addAttribute("reportDetailList", reportDetailList);
		model.addAttribute("countByDate", countByDate);
		model.addAttribute("countByDateAndWorkId", countByDateAndWorkId);

		return "report/dailyReportList";
	}

	/**
	 * 日報一覧画面から遷移時の日報登録画面
	 * @param model
	 * @param session
	 * @param selectDate
	 * @return 日報登録画面
	 */
	@PostMapping(path = "/report/dailyReport", params = "edit")
	public String displayEditReport(HttpSession session, java.sql.Date selectDate, Model model) {
		// セッションからユーザー情報を取得
		Users loginUser = (Users) session.getAttribute("user");
		Integer userId = loginUser.getId();

		// 日報情報を取得
		DailyReportForm dailyReportForm = dailyReportService.setForm(userId, selectDate);
		
		// ステータスを取得
		String dateString = selectDate.toString();
		Integer status = dailyReportService.searchReportStatus(loginUser, dateString);
		String statusText = getStatusText(status);

		//全ての作業種別情報を取得(プルダウン用)
		List<TasktypeDto> taskType = dailyReportService.findAll();
		model.addAttribute("taskType", taskType);

		model.addAttribute("loginUser", loginUser);
		model.addAttribute("dailyReportForm", dailyReportForm);
		model.addAttribute("selectDate", selectDate);
		model.addAttribute("statusText", statusText);

		return "report/dailyReport";
	}
	
	/**
	 * 日付が選択されたときに日報情報を取得して返す
	 *
	 * @param session
	 * @param selectDate
	 * @return 選択された日付の日報情報
	 */
	@ResponseBody
	@RequestMapping(value = "/report/dailyReportList", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
	public ResponseEntity<Map<String, Object>> searchDayReport(HttpSession session,
			@RequestBody Map<String, LocalDate> requestBody) {
		// セッションからユーザー情報を取得
		Users loginUser = (Users) session.getAttribute("user");
		// リクエストボディから日付を取得
		LocalDate selectDate = requestBody.get("selectDate");
		LocalDate selectMonth = requestBody.get("selectMonth");
		
		 LocalDate start = null;
		 LocalDate end = null;
		
		// 選択日から過去の日報を取得
	    if (selectDate != null) {
	        start = selectDate; // 初日
	        end = selectDate.plusDays(6); // 一週間後の日付
	    } else if (selectMonth != null) {
	        start = selectMonth.withDayOfMonth(1); // 月の初日
	        end = selectMonth.with(TemporalAdjusters.lastDayOfMonth()); // 月の最終日
	    }
	    System.out.println("start="+start+","+"end="+end);

		// 日報のリストを取得
		List<DailyReportDetail> reportDetailList = dailyReportListService.searchReport(loginUser, start, end);
		// 日付とIDでソート
		Collections.sort(reportDetailList, Comparator.comparing(DailyReportDetail::getDate)
				.thenComparing(DailyReportDetail::getWorkTypeId));
		System.out.println(reportDetailList);
		//その日にいくつ日報があるかカウント
		Map<Date, Long> countByDate = reportDetailList.stream()
				.collect(Collectors.groupingBy(DailyReportDetail::getDate, Collectors.counting()));
		// 日付ごとにグループ化し、各日付の中で作業種別IDをカウント
		Map<Date, Map<Integer, Long>> countByDateAndWorkId = reportDetailList.stream()
				.collect(Collectors.groupingBy(DailyReportDetail::getDate,
						Collectors.groupingBy(DailyReportDetail::getWorkTypeId, Collectors.counting())));

		Map<String, Object> response = new HashMap<>();
		response.put("reportDetailList", reportDetailList);
		response.put("countByDate", countByDate);
		response.put("countByDateAndWorkId", countByDateAndWorkId);

		return ResponseEntity.ok(response);
	}

}
