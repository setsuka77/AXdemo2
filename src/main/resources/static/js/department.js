
// リアルタイムで検索結果を表示
document.getElementById('newDepartment').addEventListener('input', function() {
	const name = this.value;
	const currentDepartment = document.getElementById('currentDepartment').value;

	// プルダウンが選択されていないときのみ検索を実行
	if (currentDepartment === "") {
		// Fetch APIで検索リクエストを送信
		fetch(`/login/department/search?name=${name}`)
			.then(response => response.json())
			.then(data => {
				const select = document.getElementById('currentDepartment');
				select.innerHTML = '<option value="">選択してください</option>'; // クリア

				// 検索結果をプルダウンに表示
				data.forEach(department => {
					const option = document.createElement('option');
					option.value = department.name;
					option.textContent = department.name;
					
					// [停止中] を含む場合はクラスを追加
                    if (department.name.includes('[停止中]')) {
                        option.classList.add('stop');
                    }
					
					select.appendChild(option);
				});
			})
			.catch(error => console.error('Error:', error));
	}
});

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


