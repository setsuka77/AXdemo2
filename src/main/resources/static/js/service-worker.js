/**
 * プッシュ通知登録 サービスワーカー
 */

// サービスワーカーのインストールイベントを監視
self.addEventListener('install', function(event) {
	console.log('Service Worker installing.');
});

// サービスワーカーのアクティベートイベントを監視
self.addEventListener('activate', function(event) {
	console.log('Service Worker activating.');
});

// プッシュ通知を受信した際の処理
self.addEventListener('push', function(event) {
	console.log('Push event received.');
	// プッシュ通知のデータを取得。データがない場合はデフォルト値を使用
	const data = event.data ? event.data.json() : {};
	const options = {
		body: data.body || 'Default body', // 通知の本文
		icon: data.icon || '/default-icon.png', // 通知のアイコン
		data: {
			url: 'http://localhost:8080/login/index' // 通知クリック時に遷移するURL
		}
	};
	// 通知を表示する
	event.waitUntil(
		self.registration.showNotification(data.title || 'Default title', options)
	);
});

// 通知をクリックした際の処理
self.addEventListener('notificationclick', function(event) {
	console.log('Notification click received.');
	// 通知に含まれるデータからURLを取得。ない場合はデフォルトURLを使用
	const url = event.notification.data.url || 'http://localhost:8080/login/index'; // デフォルトのURL
	event.notification.close(); // 通知をクリックした後、通知を閉じる
	// クリック時に指定されたURLを新しいウィンドウまたはタブで開く
	event.waitUntil(
		clients.openWindow(url)
	);
});

// fetchイベントを監視し、リクエストを処理
self.addEventListener('fetch', function(event) {
	console.log('fetchイベントが呼び出されました: ', event.request.url);
	// 必要に応じて、fetch イベントの応答を制御するロジックを追加可能
});