/**
 * ヘッダー用Jsファイル
 */

document.addEventListener('DOMContentLoaded', () => {
	const body = document.body;
	const checkbox = document.getElementById('switch');

	// ページ読み込み時にローカルストレージからテーマを適用
	const theme = localStorage.getItem('theme');
	if (theme === 'dark') {
		body.classList.add('dark-mode');
		checkbox.checked = true; // チェックボックスもダークモード状態に合わせる
	} else {
		body.classList.remove('dark-mode');
		checkbox.checked = false; // ライトモードの場合、チェックを外す
	}

	// ダークモードの切り替え
	checkbox.addEventListener('change', () => {
		if (checkbox.checked) {
			body.classList.add('dark-mode');
			localStorage.setItem('theme', 'dark'); // ローカルストレージに保存
		} else {
			body.classList.remove('dark-mode');
			localStorage.setItem('theme', 'light'); // ローカルストレージを更新
		}
	});
});
