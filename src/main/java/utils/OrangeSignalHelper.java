package utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.SameSiteAttribute;
import com.orangesignal.csv.Csv;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.handlers.StringArrayListHandler;

/**
 * OrangeSignalヘルパー.
 *
 * @author cyrus
 */
@Deprecated
public class OrangeSignalHelper {

	/**
	 * クッキー一覧のCSVを読み込み.
	 *
	 *@param file
	 *@param cookieList
	 * @return
	 * @throws IOException
	 */
	public static List<Cookie> readCookieCsv(File file) throws IOException {
		List<Cookie> cookieList = new ArrayList<>();

		// 全ての行に対して実行
		for (String[] row : Csv.load(file, getCsvConfig(), new StringArrayListHandler())) {
			Cookie cookie = new Cookie(row[0], row[1]);
			cookie.setUrl(row[2]);
			cookie.setDomain(row[3]);
			cookie.setPath(row[4]);
			cookie.setExpires(Double.parseDouble(row[5]));
			cookie.setHttpOnly(Boolean.parseBoolean(row[6]));
			cookie.setSameSite(SameSiteAttribute.valueOf(row[7]));
			cookieList.add(cookie);
		}

		return cookieList;
	}

	/**
	 * クッキー一覧のCSVを作成.
	 *
	 *@param file
	 *@param cookieList
	 * @return
	 * @throws IOException
	 */
	public static void createCookieCsv(File file, List<Cookie> cookieList) throws IOException {
		// ディレクトリを作成
		FileUtils.forceMkdirParent(file);

		List<String[]> csvRowList = new ArrayList<>();

		// 全てのクッキーに対して実行
		for (Cookie cookie : cookieList) {
			// データ行を作成
			List<String> csvRow = new ArrayList<>();
			csvRow.add(cookie.name);
			csvRow.add(cookie.value);
			csvRow.add(cookie.url);
			csvRow.add(cookie.domain);
			csvRow.add(cookie.path);
			csvRow.add(String.valueOf(cookie.expires));
			csvRow.add(String.valueOf(cookie.httpOnly));
			csvRow.add(String.valueOf(cookie.sameSite));
			csvRowList.add(csvRow.toArray(new String[0]));
		}
		Csv.save(csvRowList, file, StandardCharsets.UTF_8.name(), getCsvConfig(), new StringArrayListHandler());
	}

	/**
	 * CsvConfigを取得.
	 *
	 * @return
	 */
	public static CsvConfig getCsvConfig() {
		CsvConfig csvConfig = new CsvConfig();
		csvConfig.setQuoteDisabled(false);
		csvConfig.setEscapeDisabled(false);
		csvConfig.setIgnoreEmptyLines(true);
		return csvConfig;
	}
}