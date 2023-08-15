package mains;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;

import utils.PlaywrightHelper;

/**
 * ステートを復元してメルカリを表示.
 *
 * @author cyrus
 */
public class OpenMercariWithState {

	/**
	 * メイン.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("■start.");

		// Playwrightを作成
		try (Playwright playwright = Playwright.create()) {
			// ブラウザ起動オプションを取得
			BrowserType.LaunchOptions launchOptions = PlaywrightHelper.getLaunchOptions();

			// ブラウザを起動
			try (Browser browser = playwright.chromium().launch(launchOptions)) {
				System.out.println("■launched");

				// ブラウザコンテキストオプションを取得
				Browser.NewContextOptions newContextOptions = PlaywrightHelper.getNewContextOptions(true);

				// ブラウザコンテキストを取得
				try (BrowserContext context = browser.newContext(newContextOptions)) {
					// ページを取得
					try (Page page = context.newPage()) {
						// FIXME ページを設定
						page.onDialog(dialog -> {
							dialog.dismiss();
						});

						// トップ画面を表示
						page.navigate("https://jp.mercari.com/");

						// 読み込み完了まで待機
						page.waitForLoadState(LoadState.NETWORKIDLE);

						// アカウントボタンをクリック
						page.locator(
								".merMenu > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > button:nth-child(1)")
								.click();

						// アカウント名を取得
						String accountName = page.locator("#tippy-3 .merText").textContent();
						System.out.println("accountName: " + accountName);

						// FIXME 確認のためウエイト
						page.waitForTimeout(10000);
					}
				}
			}
		} finally {
			System.out.println("■done.");
		}
	}
}