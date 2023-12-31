package mains;

import java.util.Scanner;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;

import utils.PlaywrightHelper;

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
			// ブラウザ起動オプションを取得
			BrowserType.LaunchOptions launchOptions = PlaywrightHelper.getLaunchOptions();

			// ブラウザを起動
			try (Browser browser = playwright.chromium().launch(launchOptions)) {
				System.out.println("■launched");

				// ブラウザコンテキストオプションを取得
				Browser.NewContextOptions newContextOptions = PlaywrightHelper.getNewContextOptions(false);

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

						// ログインボタンをクリック
						page.locator(
								"#__next > div > header > div > div > div.navigationSection__6d8930f1.endSection__6d8930f1 > nav > div > div.desktopContainer__c74efeb1 > div:nth-child(1) > div > button")
								.click();

						// 読み込み完了まで待機
						page.waitForLoadState(LoadState.NETWORKIDLE);

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

						// 読み込み完了まで待機
						page.waitForLoadState(LoadState.NETWORKIDLE);
					} finally {
						// コンテキストのステートを出力
						PlaywrightHelper.storageState(context);
					}
				}
			}
		} finally {
			System.out.println("■done.");
		}
	}
}