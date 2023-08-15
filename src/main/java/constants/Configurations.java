package constants;

import java.io.File;
import java.nio.file.Path;

/**
 * 環境設定.
 *
 * @author cyrus
 */
public interface Configurations {

	/**
	 * ブラウザをヘッドレスモードで使用するか.
	 */
	boolean USE_HEADLESS_MODE = true;

	/**
	 * ブラウザのステートの出力先.
	 */
	Path STATE_PATH = new File("data/state.json").toPath();

	/**
	 * 商品の値下げ設定のファイル.
	 */
	File ITEM_PRICE_DECREASE_SETTINGS_FILE = new File("data/settings.json");
}