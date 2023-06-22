package mains;

import java.util.Scanner;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import constants.Configurations;

/**
 * メルカリにログイン.
 *
 * @author cyrus
 */
public class LoginMercari {

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
				Browser.NewContextOptions newContextOptions = new Browser.NewContextOptions();

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
						page.waitForTimeout(1000);

						// ログインボタンをクリック
						page.locator(
								"div.merNavigationTopMenuItem:nth-child(2) > div:nth-child(1) > button:nth-child(1)")
								.click();
						page.waitForTimeout(1000);

						// メール・電話番号でログインボタンをクリック
						page.locator(".style_email__T3Zi1 > a:nth-child(1)").click();
						page.waitForTimeout(1000);

						// Scanner
						try (Scanner scanner = new Scanner(System.in)) {
							System.out.print("メールまたは電話番号を入力してください: ");
							String emailOrPhone = scanner.nextLine();

							// メールまたは電話番号を入力
							page.locator("input[name='emailOrPhone']").fill(emailOrPhone);

							System.out.print("パスワードを入力してください: ");
							String password = scanner.nextLine();

							// メールまたは電話番号を入力
							page.locator("input[name='password']").fill(password);

							// ログインボタンをクリック
							page.locator("button[type='submit']").click();
							page.waitForTimeout(1000);

							System.out.print("認証番号を入力してください: ");
							String code = scanner.nextLine();

							// 認証番号を入力
							page.locator("input[name='code']").fill(code);
						}

						// ボタンをクリック
						page.locator("button[type='submit']").click();

						// FIXME 確認のためウエイト
						page.waitForTimeout(10000);

						// ステートを出力
						context.storageState(
								new BrowserContext.StorageStateOptions().setPath(Configurations.STATE_PATH));

						// FIXME 確認のためウエイト
						page.waitForTimeout(1000);
					}
				}
			}
		} finally {
			System.out.println("■done.");
		}
	}
}