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
	boolean USE_HEADLESS_MODE = false;

	/**
	 * ブラウザのステートの出力先.
	 */
	Path STATE_PATH = new File("data/state.json").toPath();
}