package mains;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Locator.ClickOptions;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.MouseButton;

import constants.Configurations;
import utils.Dtos;
import utils.JacksonHelper;
import utils.PlaywrightHelper;

/**
 * 出品中の商品を一括で値下げ.
 *
 * @author cyrus
 */
public class DecreaseAllItemPrice {

	/**
	 * メイン.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("■start.");
		try {
			// 商品の値下げ設定のマップを取得
			Map<String, Dtos.ItemPriceDecreaseSettings> settingsMap = getItemPriceDecreaseSettingsMap();

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

							// 出品した商品画面を表示
							page.navigate("https://jp.mercari.com/mypage/listings");

							// 読み込み完了まで待機
							page.waitForLoadState(LoadState.NETWORKIDLE);

							// 全ての出品した商品に対して実行
							for (Locator itemLocator : page.locator("#currentListing .merListItem").all()) {
								try {
									// containerを取得
									Locator containerLocator = itemLocator.locator(".container");
									System.out.println(containerLocator.textContent());

									// 公開停止中であるかを判定
									if (StringUtils.contains(containerLocator.textContent(), "公開停止中")) {
										System.out.println("公開停止中です: " + itemLocator.locator("a").getAttribute("href"));
									} else {
										// 商品をクリック
										itemLocator.locator("a")
												.click(new ClickOptions().setButton(MouseButton.MIDDLE));

										// 商品詳細画面を取得
										try (Page itemDetailPage = context.waitForPage(() -> {
											// NOP
										})) {
											// ページ(タブ)をアクティブに変更
											itemDetailPage.bringToFront();

											// 読み込み完了まで待機
											itemDetailPage.waitForLoadState(LoadState.NETWORKIDLE);

											// URLを取得
											String itemDetailPageUrl = itemDetailPage.url();
											System.out.println("itemDetailPageUrl: " + itemDetailPageUrl);

											// 商品IDを取得
											String itemId = FilenameUtils.getName(itemDetailPageUrl);
											System.out.println("itemId: " + itemId);

											// 商品の値下げ設定を取得
											Dtos.ItemPriceDecreaseSettings settings = settingsMap.get(itemId);

											// 商品の値下げ設定が存在しない場合はデフォルト値を設定
											if (settings == null) {
												settings = new Dtos.ItemPriceDecreaseSettings();
												settingsMap.put(itemId, settings);
											}
											if (settings.minimumPrice == null) {
												// FIXME
												settings.minimumPrice = 1000;
											}
											if (settings.decreaseStep == null) {
												// FIXME
												settings.decreaseStep = 100;
											}
											if (settings.crawlIntervalHour == null) {
												// FIXME
												settings.crawlIntervalHour = 24;
											}

											// 商品名を取得
											String itemName = itemDetailPage.locator("h1.heading__a7d91561")
													.textContent();
											settings.itemName = itemName;
											System.out.println("itemName: " + itemName);

											// 現在の価格を取得
											Locator priceLocator = itemDetailPage
													.locator("[data-testid='price'] > span:nth-child(2)");
											int currentPrice = Integer
													.parseInt(priceLocator.textContent().replaceAll(",", ""));
											settings.currentPrice = currentPrice;
											System.out.println("currentPrice: " + currentPrice);

											// 現在の価格が最低価格より大きい場合
											if (settings.minimumPrice < currentPrice) {
												// 最終値下げ実行日時が未設定または最終値下げ実行日時から値下げ間隔(時間)以上経過している場合
												if (settings.lastDecreaseDate == null || DateUtils
														.addHours(settings.lastDecreaseDate, settings.crawlIntervalHour)
														.getTime() <= System.currentTimeMillis()) {
													// 商品の編集ボタンをクリック
													itemDetailPage.getByText("商品の編集").click();

													// 読み込み完了まで待機
													itemDetailPage.waitForLoadState(LoadState.NETWORKIDLE);

													try {
														// FIXME モーダルが表示されている場合はクリック
														itemDetailPage.locator("#modal button").click();
													} catch (Exception e) {
														// NOP
													}

													// 値下げ後の価格を計算
													int newPrice = Math.max(currentPrice - settings.decreaseStep,
															settings.minimumPrice);
													System.out.println("newPrice: " + newPrice);

													// 値下げ後の価格を入力
													itemDetailPage.locator("input[name='price']")
															.fill(String.valueOf(newPrice));

													// 変更するボタンをクリック
													itemDetailPage
															.locator("button[type='submit'][data-testid='edit-button']")
															.click();

													// 読み込み完了まで待機
													itemDetailPage.waitForLoadState(LoadState.NETWORKIDLE);

													// 現在の価格を更新
													settings.currentPrice = newPrice;

													// 最終値下げ実行日時を設定
													settings.lastDecreaseDate = new Date();
												}
											}
										} catch (Exception e) {
											System.err.println("catch itemDetailPage");
											e.printStackTrace();
										}
									}
								} catch (Exception e) {
									System.err.println("catch itemLocator");
									e.printStackTrace();
								}
							}
						} finally {
							// コンテキストのステートを出力
							PlaywrightHelper.storageState(context);
						}
					} catch (Exception e) {
						System.err.println("catch context");
						e.printStackTrace();
					}
				} catch (Exception e) {
					System.err.println("catch browser");
					e.printStackTrace();
				}
			} catch (Exception e) {
				System.err.println("catch playwright");
				e.printStackTrace();
			} finally {
				// 商品の値下げ設定のマップを出力
				JacksonHelper.getObjectMapper().writeValue(Configurations.ITEM_PRICE_DECREASE_SETTINGS_FILE,
						JacksonHelper.getObjectMapper().valueToTree(settingsMap));
			}
		} catch (Exception e) {
			System.err.println("catch main");
			e.printStackTrace();
		} finally {
			System.out.println("■done.");
		}
	}

	/**
	 * 商品の値下げ設定のマップを取得.
	 * 
	 * @return
	 */
	private static Map<String, Dtos.ItemPriceDecreaseSettings> getItemPriceDecreaseSettingsMap() {
		try {
			return JacksonHelper.getObjectMapper().readValue(Configurations.ITEM_PRICE_DECREASE_SETTINGS_FILE,
					new TypeReference<Map<String, Dtos.ItemPriceDecreaseSettings>>() {
					});
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<>();
		}
	}
}