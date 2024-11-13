let myPieChart; // グローバルで宣言

//labelのデータ更新
function generateChartData(reportDetailList) {
	const labels = [];
	const chartData = [];

	reportDetailList.forEach(report => {
		const workType = report.workTypeName;
		const time = parseFloat(report.time); // 数値に変換

		const existingIndex = labels.indexOf(workType); // 見つかったらそのインデックス番号が、なければ-1が入る
		if (existingIndex >= 0) {
			chartData[existingIndex] += time; // 既存の作業タイプの時間を合計
		} else {
			labels.push(workType); // 新しい作業タイプを追加
			chartData.push(time);   // 初回の時間をセット
		}
	});

	return { labels, chartData };
}

//グラフの背景色の生成
function generateRandomColors(labels) {
	return labels.map(() => getRandomColor());
}
//グラフ作成・更新
function updateChart(labels, chartData) {
	const backgroundColors = generateRandomColors(labels);

	if (myPieChart) {
		myPieChart.destroy();
	}

	const ctx = document.getElementById("myPieChart");
	myPieChart = new Chart(ctx, {
		type: 'doughnut',
		data: {
			labels: labels,
			datasets: [{
				backgroundColor: backgroundColors,
				data: chartData
			}]
		},
		options: {
			title: {
				display: true,
				text: 'タイプ別 作業時間'
			},
			tooltips: {
				callbacks: {
					label: function(tooltipItem, data) {
						const index = tooltipItem.index; // 選択されたデータポイントのインデックス
						const workType = data.labels[index]; // workType（作業タイプ）のラベル
						const time = data.datasets[tooltipItem.datasetIndex].data[index]; // 対応する時間
						return workType + ': ' + time + '時間'; // 作業タイプと時間を表示
					}
				}
			}
		}
	});
}

//fetchで週間の日報情報を取得
function fetchWeekReport() {
	const dateRange = document.getElementById('dateRange').value;
	console.log(dateRange);
	const startDate = dateRange.split("から")[0].trim();

	function formatDateToYYYYMMDD(dateString) {
		const [year, month, day] = dateString.split(".").map(num => num.padStart(2, '0')); // "YYYY.MM.DD"形式から分割
		return `${year}-${month}-${day}`; // "YYYY-MM-DD"形式に変換
	}
	const selectDate = formatDateToYYYYMMDD(startDate);
	console.log(selectDate);

	// サーバーに選択された日付を送信し、対応するレポートデータを取得する
	fetch('/login/report/dailyReportList', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({ selectDate })
	})
		.then(response => response.json())
		.then(data => handleReportData(data))
		.catch(error => console.error('Error fetching data:', error));
}

//fetchで月間の日報情報を取得
function fetchMonthReport() {
	const initialDate = document.getElementById('month').value;
	const selectMonth = `${initialDate}-01`;
	console.log(selectMonth);

	// サーバーに選択された月を送信し、対応するレポートデータを取得する
	fetch('/login/report/dailyReportList', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({ selectMonth })
	})
		.then(response => response.json())
		.then(data => handleReportData(data))
		.catch(error => console.error('Error fetching data:', error));
}

// 取得したデータを処理する[月も週も共通]
function handleReportData(data) {
	const reportDetailList = data.reportDetailList;
	const countByDate = data.countByDate;
	const countByDateAndWorkId = data.countByDateAndWorkId;
	const { labels, chartData } = generateChartData(reportDetailList);

	// チャートを更新
	updateChart(labels, chartData);

	const tbody = document.querySelector('tbody');
	//tbodyの内容削除
	tbody.innerHTML = '';

	reportDetailList.forEach((report, index) => {
		const row = document.createElement('tr');

		//日付データ挿入（現在のreport.dateと前の行の日付が異なる場合、行作成）
		if (index === 0 || report.date !== reportDetailList[index - 1].date) {
			const dateCell = document.createElement('td');
			dateCell.textContent = new Date(report.date).toLocaleDateString('ja-JP', {
				year: 'numeric',
				month: 'long',
				day: 'numeric'
			});
			dateCell.setAttribute('rowspan', countByDate[report.date]);
			row.appendChild(dateCell);
		}

		// 作業タイプ挿入(現在の日付と前の行の日付が異なり、さらに作業種別Idが異なる場合は行作成)
		if (index === 0 || report.workTypeId !== reportDetailList[index - 1].workTypeId || report.date !== reportDetailList[index - 1].date) {
			const workTypeCell = document.createElement('td');
			workTypeCell.textContent = report.workTypeName;
			workTypeCell.setAttribute('rowspan', countByDateAndWorkId[report.date][report.workTypeId]);
			row.appendChild(workTypeCell);
		}

		// 作業内容挿入
		const contentCell = document.createElement('td');
		contentCell.textContent = report.content;
		row.appendChild(contentCell);
		//	作業時間挿入
		const timeCell = document.createElement('td');
		timeCell.textContent = report.time;
		row.appendChild(timeCell);

		//編集ボタン挿入
		if (index === 0 || report.date !== reportDetailList[index - 1].date) {
			const editCell = document.createElement('td');
			const editForm = document.createElement('form');
			editForm.setAttribute('method', 'post');
			editForm.setAttribute('action', '/login/report/dailyReport?edit=true');

			const formatDate = (date) => {
				const parsedDate = new Date(date);
				parsedDate.setHours(parsedDate.getHours() + 10);
				return isNaN(parsedDate) ? '' : parsedDate.toISOString().split('T')[0];
			};

			// 例: report.dateの値を確認し、hiddenDateInputに設定する
			const hiddenDateInput = document.createElement('input');
			hiddenDateInput.setAttribute('type', 'hidden');
			hiddenDateInput.setAttribute('name', 'selectDate');

			// フォーマットを変更してhiddenDateInputの値を設定
			hiddenDateInput.value = formatDate(report.date);

			const editButton = document.createElement('button');
			editButton.classList.add('editButton');
			editButton.textContent = '編集';

			editForm.appendChild(hiddenDateInput);
			editForm.appendChild(editButton);
			editCell.appendChild(editForm);
			row.appendChild(editCell);
		} else {
			// 編集ボタンが必要ない場合は空のセルを追加
			const emptyEditCell = document.createElement('td');
			row.appendChild(emptyEditCell);
		}

		//生成した行をtbodyに挿入
		tbody.appendChild(row);
	});
}

let previousHue = null; // 前回の色相を保持する変数

function getRandomColor() {
	// 彩度を70〜100%の範囲に設定
	const saturation = Math.floor(Math.random() * 31) + 70;
	// 明度（lightness）は明るすぎず暗すぎずに調整
	const lightness = Math.floor(Math.random() * 21) + 70; // 70%〜90%の範囲

	let hue;
	// 色相が20度以上離れるまでループ
	do {
		hue = Math.floor(Math.random() * 360);  // 新しい色相を生成
	} while (previousHue !== null && Math.abs(hue - previousHue) < 15); // 前回の色相と20以上離れているか確認

	previousHue = hue; // 現在の色相を記録

	// HSLのカラーを生成して返す
	return `hsl(${hue}, ${saturation}%, ${lightness}%)`;
}


document.addEventListener("DOMContentLoaded", function() {
	// Thymeleaf から埋め込んだ reportDetailList を利用
	if (typeof reportDetailList !== 'undefined') {
		console.log(reportDetailList); // コンソールで確認
		//グラフ用のデータを生成
		const { labels, chartData } = generateChartData(reportDetailList);
		// グラフを更新
		updateChart(labels, chartData);
	}

	// HTML要素からstartDateとendDateを取得
	const weekInput = document.querySelector('#weekInput');
	// HTML要素からstartDateとendDateを取得
	const startDate = weekInput.getAttribute('data-startDate');
	const endDate = weekInput.getAttribute('data-endDate');

	flatpickr('#dateRange', {
		mode: "range",
		locale: 'ja',
		dateFormat: 'Y.m.d',
		defaultDate: [startDate, endDate],
		onChange: function(selectedDates) {
			if (selectedDates.length === 1) {
				// 選択した日付を取得
				const start = selectedDates[0];
				const end = new Date(start);
				end.setDate(end.getDate() + 7); // 7日後の日付を計算

				// 日付範囲を設定
				this.setDate([start, end]);
			}
			close();
		}
	});

	const editButtons = document.querySelectorAll('.editButton');
	editButtons.forEach(button => {
		button.addEventListener('click', function(event) {
			event.preventDefault(); // デフォルトのフォーム送信を防ぐ

			// 親フォームを取得
			const form = button.closest('form');
			const dateInput = form.querySelector('input[name="selectDate"]');

			// フォームを送信
			form.submit();
		});
	});
});

function toggleDateInput() {
	const period = document.getElementById('period').value;
	const weekInput = document.getElementById('weekInput');
	const monthInput = document.getElementById('monthInput');

	if (period === 'week') {
		weekInput.style.display = 'block';
		monthInput.style.display = 'none';
	} else if (period === 'month') {
		weekInput.style.display = 'none';
		monthInput.style.display = 'block';
	}
}


