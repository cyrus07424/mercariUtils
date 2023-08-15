package utils;

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
		 * 最低価格(円).
		 */
		public int minimumPrice;

		/**
		 * 値下げ幅(円).
		 */
		public int decreaseStep;
	}
}