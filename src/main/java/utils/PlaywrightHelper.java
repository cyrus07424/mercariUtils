package utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;

import constants.Configurations;

/**
 * Playwrightヘルパー.
 *
 * @author cyrus
 */
public class PlaywrightHelper {

	/**
	 * ブラウザ起動オプションを取得.
	 *
	 * @return
	 */
	public static BrowserType.LaunchOptions getLaunchOptions() {
		// FIXME
		return new BrowserType.LaunchOptions()
				.setHeadless(Configurations.USE_HEADLESS_MODE);
	}

	/**
	 * ブラウザコンテキストオプションを取得.
	 *
	 * @param withState
	 * @return
	 */
	public static Browser.NewContextOptions getNewContextOptions(boolean withState) {
		// FIXME
		Browser.NewContextOptions newContextOptions = new Browser.NewContextOptions();
		if (withState) {
			newContextOptions.setStorageStatePath(Configurations.STATE_PATH);
		}
		return newContextOptions;
	}
}