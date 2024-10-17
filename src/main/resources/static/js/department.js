
document.addEventListener('DOMContentLoaded', function() {
	// ページロード時に全ての部署を表示
	updateDepartments('');
});

document.getElementById('newDepartment').addEventListener('input', function() {
	const name = this.value;
	updateDepartments(name);
});

function updateDepartments(name) {
	const select = document.getElementById('currentDepartment');
	const currentDepartment = select.value;  // 現在の選択値を保持

	// Fetch APIで検索リクエストを送信
	fetch(`/login/department/search?name=${name}`)
		.then(response => response.json())
		.then(data => {

			// すべてのオプションを削除
			select.innerHTML = '';

			// 「選択してください」のオプションを追加
			const defaultOption = document.createElement('option');
			defaultOption.value = '';
			defaultOption.textContent = '選択してください';
			select.appendChild(defaultOption);

			let foundCurrentOption = false; // 現在の選択オプションが見つかったかのフラグ

			data.forEach(department => {
				const option = document.createElement('option');
				option.value = department.name;
				option.textContent = department.name;

				// [停止中] を含む場合にクラスを追加
				if (department.name.includes('[停止中]')) {
					option.classList.add('stop');
				}

				// 以前選択されていた部署を再設定
				if (department.name === currentDepartment) {
					option.selected = true;
					foundCurrentOption = true;
				}

				select.appendChild(option);
			});

			// 現在の選択オプションが見つからない場合に追加する
			if (!foundCurrentOption && currentDepartment) {
				const option = document.createElement('option');
				option.value = currentDepartment;
				option.textContent = currentDepartment;
				option.selected = true;
				select.appendChild(option);
			}
		})
		.catch(error => console.error('Error:', error));
}


//ボタンの活性、非活性
document.addEventListener("DOMContentLoaded", function() {
	const searchInput = document.getElementById("newDepartment");
	const dropdown = document.getElementById("currentDepartment");

	const registerButton = document.getElementById("register");
	const updateButton = document.getElementById("update");
	const deactivateButton = document.getElementById("deactivate");
	const restartButton = document.getElementById("restart");

	// 初期化
	disableAllButtons();

	// 入力や選択の監視
	searchInput.addEventListener("input", handleFormState);
	dropdown.addEventListener("change", handleFormState);

	function handleFormState() {
		const searchValue = searchInput.value.trim();
		const dropdownValue = dropdown.value;

		disableAllButtons();

		// 検索欄のみ入力されている場合、新規登録ボタンを活性化
		if (searchValue !== "" && dropdownValue === "") {
			enableButton(registerButton);
		}

		// 検索欄が入力されていて、プルダウンも選択されている場合
		if (searchValue !== "" && dropdownValue !== "") {
			enableButton(registerButton);
			enableButton(updateButton);
			enableButton(deactivateButton);

			// 検索欄が入力されていて、プルダウンも選択されているが同じ値の場合
			if (searchValue == dropdownValue) {
				disableButton(registerButton);
				disableButton(updateButton);
			}
		}

		// プルダウンで「[停止中]」の名前が含まれている場合は再開ボタンを活性化
		if (dropdownValue.includes("[停止中]")) {
			disableAllButtons(); // 他のボタンをすべて無効に
			enableButton(restartButton);
		}

		// 検索欄が入力されていて、プルダウンも選択されている場合
		if (searchValue === "" && dropdownValue !== "") {
			enableButton(deactivateButton);
		}
	}


	// すべてのボタンを無効化する関数
	function disableAllButtons() {
		disableButton(registerButton);
		disableButton(updateButton);
		disableButton(deactivateButton);
		disableButton(restartButton);
	}

	// ボタンを活性化する関数
	function enableButton(button) {
		button.disabled = false;
		button.classList.remove('disabled-form');
	}

	// ボタンを非活性化する関数
	function disableButton(button) {
		button.disabled = true;
		button.classList.add('disabled-form');
	}
});


// 部署のプルダウンメニューから選択された値で所属社員を取得
document.getElementById('currentDepartment').addEventListener('change', function() {
	const selectedDepartment = this.value;
	fetchWorkers(selectedDepartment); // 選択した部署に基づいて社員情報を取得
});


let originalUsers = []; // 元のユーザーの配列を保持

function fetchWorkers(selectedDepartment) {
	const userManagementForm = document.getElementById('userManagementForm');
	const tableBody = document.querySelector('#userManagementForm table tbody');
	const noWorker = document.getElementById('noWorker');
	const worker = document.getElementById('worker');
	const roleButton = document.getElementById('role');
	const idButton = document.getElementById('id');
	tableBody.innerHTML = ''; // テーブルをクリア

	// サーバーに選択された日付を送信し、対応するレポートデータを取得する
	fetch(`/login/department/worker?selectedDepartment=${encodeURIComponent(selectedDepartment)}`, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' }
	})

		.then(response => response.json())
		.then(data => {
			// データを受け取った後の処理
			const users = data.users;
			// 取得したデータの元の順序を保持
			originalUsers = [...users]; 

			// ユーザーが存在しない場合、テーブルを非表示
			if (users.length === 0) {
				userManagementForm.style.display = ''; // 表示
				noWorker.style.display = ''; // 該当する職員がいないメッセージを表示
				worker.style.display = 'none'; // テーブルを非表示
			} else {
				userManagementForm.style.display = ''; // 表示
				noWorker.style.display = 'none'; // 該当する職員がいないメッセージを非表示
				worker.style.display = ''; // 表示
				users.forEach(user => {
					const row = document.createElement('tr');
					row.innerHTML = `
                    <td>${user.id}</td>
                    <td>${user.name}</td>
                    <td>${user.role}</td>
                    <td>${user.workPlace}</td>
                   	<td><a href="/login/userManagement/manage/${user.id}/${user.name}?from=employeeList">変更</a></td>

                `;
					tableBody.appendChild(row); // テーブルに行を追加

					// ボタンのスタイルを更新
					idButton.style.backgroundColor = ' rgb(200, 200, 200)';
					roleButton.style.backgroundColor = '#FFA41D';
				});
			}
		})
		.catch(error => console.error('Error fetching workersエラー！:', error));
}


//idのボタンを押下したときidで並び替える
function sortTableById() {
	const tableBody = document.getElementById('workerTableBody');
	const rows = Array.from(tableBody.getElementsByTagName('tr'));
	const roleButton = document.getElementById('role');
	const idButton = document.getElementById('id');

	// IDを基準に並び替え
	rows.sort((rowA, rowB) => {
		const idA = parseInt(rowA.getElementsByTagName('td')[0].textContent);
		const idB = parseInt(rowB.getElementsByTagName('td')[0].textContent);
		return idA - idB;  // 昇順に並び替え
	});

	// 並び替え後の行をテーブルに再追加
	tableBody.innerHTML = '';  // クリア
	rows.forEach(row => tableBody.appendChild(row));  // 再追加

	// ボタンのスタイルを更新
	idButton.style.backgroundColor = '#FFA41D';
	roleButton.style.backgroundColor = ' rgb(200, 200, 200)';
}


//役職のボタンを押下したとき役職で並び替える
function sortTableByRole() {
	const tableBody = document.getElementById('workerTableBody');
	const rows = Array.from(tableBody.getElementsByTagName('tr'));
	const roleButton = document.getElementById('role');
	const idButton = document.getElementById('id');

	// 役職に基づいて元のユーザーの順序を復元
	const sortedRows = originalUsers.map(user => {
		const matchingRow = rows.find(row => row.cells[1].textContent === user.name); // 名前で一致する行を探す
		return matchingRow; // 一致する行を返す
	});

	// 並び替え後の行をテーブルに再追加
	tableBody.innerHTML = '';  // クリア
	sortedRows.forEach(row => {
			tableBody.appendChild(row); // 再追加
	});
	// ボタンのスタイルを更新
	idButton.style.backgroundColor = ' rgb(200, 200, 200)';
	roleButton.style.backgroundColor = '#FFA41D';
}



// 部署のプルダウンメニューから選択された値をテキストフィールドに反映
document.getElementById('currentDepartment').addEventListener('change', function() {
	var selectedDepartment = this.value;
	document.getElementById('newDepartment').value = selectedDepartment; // テキストフィールドに反映
});


//登録ボタン押下時確認ダイアログ
function confirmRegistration() {
	const newDepartment = document.getElementById('newDepartment').value;
	return confirm(`部署名: ${newDepartment}\nこの内容で登録します。よろしいですか？`);
}

//部署名変更ボタン押下時確認ダイアログ
function confirmUpdate() {
	const newDepartment = document.getElementById('newDepartment').value;
	const currentDepartment = document.getElementById('currentDepartment').value;
	return confirm(`部署名: ${newDepartment}\n選択中の登録済部署: ${currentDepartment}\n選択中の部署名を変更します。よろしいですか？`);
}

//停止ボタン押下時確認ダイアログ
function confirmDeactivate() {
	const currentDepartment = document.getElementById('currentDepartment').value;
	return confirm(`選択中の部署: ${currentDepartment}\n選択中の部署を停止します。よろしいですか？`);
}

//再開ボタン押下時確認ダイアログ
function confirmRestart() {
	const currentDepartment = document.getElementById('currentDepartment').value;
	return confirm(`選択中の部署: ${currentDepartment}\n選択中の部署を再開します。よろしいですか？`);
}


