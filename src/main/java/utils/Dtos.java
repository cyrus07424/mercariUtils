package utils;

import java.util.Date;

/**
 * DTO用クラス.
 *
 * @author cyrus
 */
public class Dtos {

	/**
	 * 商品の値下げ設定.
	 * 
	 * @author cyrus
	 *
	 */
	public static class ItemPriceDecreaseSettings {

		/**
		 * 商品名.
		 */
		public String itemName;

		/**
		 * 現在の価格(円).
		 */
		public Integer currentPrice;

		/**
		 * 最低価格(円).
		 */
		public Integer minimumPrice;

		/**
		 * 値下げ幅(円).
		 */
		public Integer decreaseStep;

		/**
		 * 値下げ間隔(時間).
		 */
		public Integer crawlIntervalHour;

		/**
		 * 最終値下げ実行日時.
		 */
		public Date lastDecreaseDate;
	}
}