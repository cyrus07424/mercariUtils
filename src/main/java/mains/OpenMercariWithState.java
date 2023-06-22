package mains;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import constants.Configurations;

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
			// ブラウザ起動オプションを設定
			BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
					.setHeadless(Configurations.USE_HEADLESS_MODE);

			// ブラウザを起動
			try (Browser browser = playwright.chromium().launch(launchOptions)) {
				System.out.println("■launched");

				// ブラウザコンテキストオプションを設定
				Browser.NewContextOptions newContextOptions = new Browser.NewContextOptions()
						.setStorageStatePath(Configurations.STATE_PATH);

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

						// アカウントボタンをクリック
						page.locator(
								".merMenu > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > button:nth-child(1)")
								.click();

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