/**
 * プッシュ通知登録 サービスワーカー
 */

self.addEventListener('install', function(event) {
    console.log('Service Worker installing.');
    // インストール時の処理があればここに記述
});

self.addEventListener('activate', function(event) {
    console.log('Service Worker activating.');
    // アクティベート時の処理があればここに記述
});

self.addEventListener('push', function(event) {
    console.log('Push event received.');
    const data = event.data ? event.data.json() : {};
    const options = {
        body: data.body || 'Default body',
        icon: data.icon || '/default-icon.png',
        data: {
            url: 'http://localhost:8080/login/index' // 通知クリック時に遷移するURL
        }
    };
    event.waitUntil(
        self.registration.showNotification(data.title || 'Default title', options)
    );
});

self.addEventListener('notificationclick', function(event) {
    console.log('Notification click received.');
    const url = event.notification.data.url || 'http://localhost:8080/login/index'; // デフォルトのURL
    event.notification.close(); // 通知を閉じる
    event.waitUntil(
        clients.openWindow(url) // URLを開く
    );
});

self.addEventListener('fetch', function(event) {
    console.log('fetchイベントが呼び出されました: ', event.request.url);
    // 必要に応じて、fetch イベントの応答を制御するロジックを追加できます。
});