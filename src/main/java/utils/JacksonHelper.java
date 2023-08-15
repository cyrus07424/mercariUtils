package utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

/**
 * Jacksonヘルパー.
 *
 * @author cyrus
 */
public class JacksonHelper {

	/**
	 * Get ObjectMapper.
	 *
	 * @return
	 */
	public static final ObjectMapper getObjectMapper() {
		return new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
	}
}