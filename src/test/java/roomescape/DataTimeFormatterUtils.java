package roomescape;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class DataTimeFormatterUtils {

	public static final String TOMORROW_DATE = DataTimeFormatterUtils.getFormattedTomorrowDate();

	public static final String YESTERDAY_DATE = DataTimeFormatterUtils.getFormattedYesterdayDate();

	private DataTimeFormatterUtils() {

	}

	private static String getFormattedTomorrowDate() {
		LocalDate tomorrow = LocalDate.now().plusDays(1);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return tomorrow.format(formatter);
	}

	private static String getFormattedYesterdayDate() {
		LocalDate tomorrow = LocalDate.now().minusDays(1);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return tomorrow.format(formatter);
	}

}
