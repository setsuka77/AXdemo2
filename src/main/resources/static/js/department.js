
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


