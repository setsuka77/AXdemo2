/**
 * プッシュ通知サブスクリプション登録設定
 */
// サービスワーカーの登録
	if ('serviceWorker' in navigator) {
		// サービスワーカーのスクリプトを登録
		navigator.serviceWorker.register('/login/js/service-worker.js')
			.then(function (registration) {
				// 登録成功時にスコープをログに表示
				console.log('ServiceWorker registration successful with scope: ', registration.scope);
				// プッシュ通知の初期化
				initializePush(registration);
			})
			.catch(function (error) {
				// 登録失敗時にエラーをログに表示
				console.error('ServiceWorker registration failed: ', error);
			});
	}

	// プッシュ通知の初期化処理
	function initializePush(registration) {
		// ブラウザがPushManagerに対応しているか確認
		if ('PushManager' in window) {
			// 既存のプッシュ通知のサブスクリプションを取得
			registration.pushManager.getSubscription()
				.then(function (subscription) {
					if (subscription === null) {
						// サブスクリプションがない場合、新たにサブスクリプションを作成
						registerPushSubscription(registration);
					} else {
						// 既にサブスクリプションが存在する場合
						console.log('既にサブスクリプション登録情報があります。');
					}
				})
				.catch(function (err) {
					// サブスクリプション取得エラー時の処理
					console.error('Error during getSubscription(): ', err);
				});
		} else {
			// PushManagerが利用できない場合のエラーログ
			console.error('ブラウザがPushManagerに対応していません。');
		}
	}

	// 新しいプッシュ通知サブスクリプションを作成
	function registerPushSubscription(registration) {
		const PUBLIC_VAPID_KEY = '[[${publicVapidKey}]]'; // 公開鍵（VAPID Key）
		// VAPIDキーをUint8Array形式に変換
		const applicationServerKey = urlBase64ToUint8Array(PUBLIC_VAPID_KEY);

		// サブスクリプションの作成
		registration.pushManager.subscribe({
			userVisibleOnly: true, // ユーザーに通知が見えるようにする
			applicationServerKey: applicationServerKey
		})
			.then(function (subscription) {
				// サブスクリプション成功時のログ
				console.log('User is subscribed.');
				// サーバにサブスクリプションを送信
				sendSubscriptionToServer(subscription);
			})
			.catch(function (err) {
				// サブスクリプション失敗時のエラーログ
				console.error('Failed to subscribe: ', err);
			});
	}

	// サーバーサイドから埋め込まれた`loginUser.id`を取得
	var userId = /*[[${loginUser.id}]]*/ '';

	// サーバーにサブスクリプション情報を送信
	function sendSubscriptionToServer(subscription) {
		fetch('/login/subscribe', {
			method: 'POST', // HTTPメソッドはPOSTを使用
			headers: {
				'Content-Type': 'application/json' // リクエストヘッダーにJSON形式を指定
			},
			// サブスクリプションデータをサーバに送信
			body: JSON.stringify({
				userId: userId,
				endpoint: subscription.endpoint,
				keys: {
					p256dh: arrayBufferToBase64(subscription.getKey('p256dh')),
					auth: arrayBufferToBase64(subscription.getKey('auth'))
				}
			})
		})
			.then(response => {
				if (!response.ok) {
					// レスポンスがOKでない場合にエラーをスロー
					throw new Error('Network response was not ok.');
				}
				// サブスクリプションの送信成功時のログ
				console.log('Subscription sent to server.');
			})
			.catch(error => {
				// サブスクリプション送信失敗時のエラーログ
				console.error('Failed to send subscription to server: ', error);
			});
	}

	// Base64URL形式の文字列をUint8Arrayに変換
	function urlBase64ToUint8Array(base64String) {
		const padding = '='.repeat((4 - base64String.length % 4) % 4); // 必要なパディングを追加
		const base64 = (base64String + padding).replace(/\-/g, '+').replace(/\_/g, '/'); // Base64URLエンコーディングを修正
		const rawData = window.atob(base64); // base64をデコード
		const outputArray = new Uint8Array(rawData.length); // Uint8Arrayを作成
		for (let i = 0; i < rawData.length; ++i) {
			outputArray[i] = rawData.charCodeAt(i); // デコードしたデータをバイト配列に変換
		}
		return outputArray;
	}

	// ArrayBufferをBase64に変換
	function arrayBufferToBase64(buffer) {
		let binary = '';
		const bytes = new Uint8Array(buffer); // バイト配列を作成
		const len = bytes.byteLength;
		for (let i = 0; i < len; i++) {
			binary += String.fromCharCode(bytes[i]); // バイナリ文字列に変換
		}
		return window.btoa(binary); // Base64エンコードを実行
	}