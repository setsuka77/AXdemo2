// 日付を選択した瞬間にDBから情報を取得する
function fetchReport() {
	removeMessages();
	const selectDate = document.getElementById('dateInput').value;
	// サーバーに選択された日付を送信し、対応するレポートデータを取得する
	fetch('/login/report/dailyReport', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({ selectDate })
	})
		.then(response => response.json())
		.then(data => handleReportData(data))
		.catch(error => console.error('Error fetching data:', error));
}

// 取得したデータを処理する
function handleReportData(data) {
	if (!data) {
		clearForm();
		return;
	}

	const { reportDetails, statusText } = data;
	console.log(reportDetails);
	clearForm()
	populateForm(reportDetails || []);
	updateStatusText(statusText);
	checkFormValidity();
	toggleSubmitButton(statusText);
}

// エラーメッセージと成功メッセージ、エラーFlagを削除する
function removeMessages() {
	// エラーメッセージ要素を非表示にする
	const errorElements = document.querySelectorAll('.alert-danger');
	errorElements.forEach(function(element) {
		element.textContent = '';
		element.style.display = 'none';
	});
	// 登録メッセージ要素を非表示にする
	const registerMessageElement = document.querySelector('.alert-success');
	if (registerMessageElement) {
		registerMessageElement.textContent = '';
		registerMessageElement.style.display = 'none';
	}
	// エラーがあったフィールドからエラーFlagを削除する
	const errorFields = document.querySelectorAll('.error-field');
	errorFields.forEach(field => {
		field.classList.remove('error-field');
	});
}

// レポートのステータスを更新する
function updateStatusText(statusText) {
	const statusTextElement = document.getElementById('statusText');
	if (statusText) {
		statusTextElement.textContent = statusText;
	}
}

// レポートが承認済みの場合、提出ボタンを無効化する
function toggleSubmitButton(statusText) {
	const submitButton = document.getElementById('submit');
	const isApproved = statusText === '承認済み';

	if (isApproved == true) {
		disableButton(submitButton);
	}
	// ボタンを非活性化する関数
	function disableButton(button) {
		button.disabled = true;
		button.classList.add('disabled-form');
	}

}

// フォームに取得したデータを埋め込む
function populateForm(data) {
	// 必要な行数を追加
	autoAddRows(data.length);

	// データをフォームに代入
	data.forEach((detail, index) => {
		const timeInput = document.querySelector(`input[name="dailyReportDetailFormList[${index}].time"]`);
		const contentTextarea = document.querySelector(`textarea[name="dailyReportDetailFormList[${index}].content"]`);
		const workTypeSelect = document.querySelector(`select[name="dailyReportDetailFormList[${index}].workTypeId"]`);
		const idInput = document.querySelector(`input[name="dailyReportDetailFormList[${index}].id"]`);

		if (timeInput) timeInput.value = detail.time;
		if (contentTextarea) {
			contentTextarea.value = detail.content;
			updateCharCounter(contentTextarea, detail.content.length);
			adjustHeight(contentTextarea);
		}
		if (workTypeSelect) {
			workTypeSelect.value = detail.workTypeId || '';
		}
		if (idInput) {
			idInput.value = detail.id ? detail.id : null;
		}
	});

}

// フォームをクリアする
function clearForm() {
	const tbody = document.getElementById('reportBody');
	const timeInputs = document.querySelectorAll('input[name^="dailyReportDetailFormList"]');
	const contentTextareas = document.querySelectorAll('textarea[name^="dailyReportDetailFormList"]');
	const workTypeSelects = document.querySelectorAll('select[name^="dailyReportDetailFormList"]');

	timeInputs.forEach(input => input.value = '');
	contentTextareas.forEach(textarea => {
		textarea.value = '';
		updateCharCounter(textarea, 0);
		adjustHeight(textarea);
	});

	// select要素を初期状態にリセット
	workTypeSelects.forEach(select => select.selectedIndex = 0);

	// 行を初期状態にリセット
	while (tbody.rows.length > 3) {
		tbody.deleteRow(3);
	}
}


// テキストエリアの文字数カウンターを更新する
function updateCharCounter(textarea, length) {
	const charCounter = textarea.nextElementSibling;
	if (charCounter) {
		// 改行コード(\n)を2文字分としてカウント
		const adjustedLength = textarea.value.replace(/\n/g, "\r\n").length;
		charCounter.textContent = `${adjustedLength} / 50`;
	}
}

// 新しい行を追加する
function addRow() {
	const tbody = document.getElementById('reportBody');
	const rowCount = tbody.rows.length;

	const newRow = document.createElement('tr');
	newRow.innerHTML = `
        <td>
            <input type="number" class="working-time" name="dailyReportDetailFormList[${rowCount}].time" min="0" step="1"> h
        </td>
       <td>
            <div class="input-group">
                <select name="dailyReportDetailFormList[${rowCount}].workTypeId" id="workTypeId">
                    <option value="">選択してください</option>
                </select>
            </div>
        </td>
        <td class="textarea-wrapper">
            <textarea name="dailyReportDetailFormList[${rowCount}].content" class="textarea note" rows="2" cols="60"></textarea>
            <div class="char-counter">0 / 50</div>
        </td>
        <input type="hidden" name="dailyReportDetailFormList[${rowCount}].id" value="0">
    `;

	// data-task-type属性から値を取得する
	const taskTypeButton = document.getElementById('add');  // 'add' ボタンの取得
    const taskTypesData = taskTypeButton.getAttribute('data-task-type');

	// 正規表現を使ってパースし、余分な括弧を除去
	const taskTypes = taskTypesData.match(/TaskTypeDto\((.*?)\)/g).map(entry => {
		const values = {};
		entry.replace(/(\w+)=([^,]+)(?:, )?/g, (_, key, value) => {
			values[key] = isNaN(value) ? value.replace(/\)$/, '') : Number(value); // 余分な括弧を削除
		});
		return values;
	});

	// <select>要素にオプションを追加
	const selectElement = newRow.querySelector('select[name="dailyReportDetailFormList[' + rowCount + '].workTypeId"]');
	taskTypes.forEach(taskType => {
		const option = document.createElement('option');
		option.value = taskType.workTypeId;
		option.textContent = taskType.workTypeName;
		selectElement.appendChild(option);
	});

	tbody.appendChild(newRow);

	// textareaのイベントリスナー追加
	const textArea = newRow.querySelector('textarea');
	textArea.addEventListener('input', () => {
		const currentLength = textArea.value.length;
		updateCharCounter(textArea, currentLength);
		adjustHeight(textArea);
	});
}


// 必要な行数を自動で追加する
function autoAddRows(count) {
	const tbody = document.getElementById('reportBody');
	const rowCount = tbody.rows.length;
	const rowsToAdd = Math.max(0, count - rowCount);

	for (let i = 0; i < rowsToAdd; i++) {
		addRow();
	}
}

// テキストエリアの高さを調整する
function adjustHeight(textarea) {
	textarea.style.height = 'auto';
	textarea.style.height = `${textarea.scrollHeight}px`;
}

// 日付選択時にフォームをチェックし、ボタンを活性化する関数
function checkFormValidity() {
	const dateInput = document.getElementById('dateInput');
	const submitButton = document.getElementById('submit');
	const timeInputs = document.querySelectorAll("input[name^='dailyReportDetailFormList'][name$='.time']");
	const contentTextareas = document.querySelectorAll("textarea[name^='dailyReportDetailFormList'][name$='.content']");

	// 日付が入力されているか確認
	const dateValid = dateInput.value.trim() !== '';
	let isAnyFormFilled = false;

	// 作業時間と作業内容がすべて入力されているか確認
	timeInputs.forEach((input, index) => {
		const content = contentTextareas[index];
		if (input.value.trim() !== '' && content.value.trim() !== '') {
			isAnyFormFilled = true;
		}
	});

	// 日付が選択され、少なくとも1行が入力されている場合にボタンを活性化
	const isValid = dateValid && isAnyFormFilled;
	submitButton.disabled = !isValid;
	submitButton.classList.toggle('disabled-form', !isValid);
}

function setEditFlag() {
    sessionStorage.setItem('fromDailyReport', 'true');  // フラグを設定
  }

// 初期設定
window.addEventListener('DOMContentLoaded', () => {
	checkFormValidity(); // 初期ロード時にボタンの状態をチェック

	const dateInput = document.getElementById('dateInput');
	const timeInputs = document.querySelectorAll("input[name^='dailyReportDetailFormList'][name$='.time']");
	const contentTextareas = document.querySelectorAll("textarea[name^='dailyReportDetailFormList'][name$='.content']");

	// 入力イベントを監視してボタンの状態を更新
	dateInput.addEventListener('input', checkFormValidity);
	timeInputs.forEach(input => input.addEventListener('input', checkFormValidity));
	contentTextareas.forEach(textarea => textarea.addEventListener('input', checkFormValidity));

	// 行追加ボタンのクリックイベント
	document.getElementById('add').addEventListener('click', addRow);

	// テキストエリアの高さ調整と文字数カウンター
	document.querySelectorAll(".textarea").forEach(textarea => {
		const currentLength = textarea.value.length;
		updateCharCounter(textarea, currentLength);  // 文字数カウントを更新
		adjustHeight(textarea);
		textarea.addEventListener('input', () => {
			updateCharCounter(textarea, currentLength);
			adjustHeight(textarea);
		});
		window.addEventListener('resize', () => adjustHeight(textarea));
	});
});