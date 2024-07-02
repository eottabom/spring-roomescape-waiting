package roomescape.repository;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.assertj.core.api.SoftAssertions;
import roomescape.DataTimeFormatterUtils;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class AbstractRepositoryTests {

	@Autowired
	private ReservationJpaRepository reservationJpaRepository;

	@Autowired
	private ThemeJpaRepository themeJpaRepository;

	@Autowired
	private ReservationTimeJpaRepository reservationTimeJpaRepository;

	void createThemeAndReservationTimeAndReservation() {
		Theme theme = createTheme();
		ReservationTime reservationTime = createReservationTime();
		createReservation(theme, reservationTime);
	}

	Theme createTheme() {
		// given
		Theme theme = Theme.builder().id(1L).name("테마1").description("첫번째테마").thumbnail("썸네일이미지").build();

		// when
		var savedTheme = this.themeJpaRepository.save(theme);

		// then
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(savedTheme).isNotNull();
			softly.assertThat(savedTheme.getId()).isNotNull();
			softly.assertThat(savedTheme.getName()).isEqualTo("테마1");
			softly.assertThat(savedTheme.getDescription()).isEqualTo("첫번째테마");
			softly.assertThat(savedTheme.getThumbnail()).isEqualTo("썸네일이미지");
		});

		return savedTheme;
	}

	ReservationTime createReservationTime() {
		// given
		ReservationTime reservationTime = ReservationTime.builder().startAt("10:00").build();

		// when
		var savedReservationTime = this.reservationTimeJpaRepository.save(reservationTime);

		// then
		assertThat(savedReservationTime).isNotNull();
		assertThat(savedReservationTime.getStartAt()).isEqualTo("10:00");

		return savedReservationTime;
	}

	void createReservation(Theme theme, ReservationTime reservationTime) {
		// given
		Reservation reservation = Reservation.builder()
			.name("tester")
			.date("2024-06-06")
			.time(reservationTime)
			.theme(theme)
			.build();

		// when
		var savedReservation = this.reservationJpaRepository.save(reservation);

		// then
		SoftAssertions.assertSoftly((softly) -> {
			softly.assertThat(savedReservation).isNotNull();
			softly.assertThat(savedReservation.getId()).isNotNull();
			softly.assertThat(savedReservation.getName()).isEqualTo("tester");
			softly.assertThat(savedReservation.getDate()).isEqualTo("2024-06-06");
			softly.assertThat(savedReservation.getTheme()).isEqualTo(theme);
			softly.assertThat(savedReservation.getTime()).isEqualTo(reservationTime);
		});
	}

	ReservationTime generateReservationTime(Long id, String startAt) {
		return ReservationTime.builder().id(id).startAt(startAt).build();
	}

	void createReservationsWithReservationTime(int createCount) {
		for (int i = 1; i <= createCount; i++) {
			String startTime = calculateStartTime("10:00", i);

			ReservationTime reservationTime = generateReservationTime((long) i, startTime);
			var savedReservationTime = this.reservationTimeJpaRepository.save(reservationTime);

			assertReservationTime(savedReservationTime, startTime);

			Theme foundTheme = this.themeJpaRepository.findById(1L).orElseThrow();
			assertFoundTheme(foundTheme);

			createAndSaveReservation(foundTheme, savedReservationTime);
		}
	}

	private String calculateStartTime(String baseTime, int minutesToAdd) {
		LocalTime startTime = LocalTime.parse(baseTime).plusMinutes(minutesToAdd);
		return startTime.format(DateTimeFormatter.ofPattern("HH:mm"));
	}

	private void assertReservationTime(ReservationTime savedReservationTime, String expectedStartTime) {
		assertThat(savedReservationTime).isNotNull();
		assertThat(savedReservationTime.getStartAt()).isEqualTo(expectedStartTime);
	}

	private void assertFoundTheme(Theme foundTheme) {
		SoftAssertions.assertSoftly((softly) -> {
			softly.assertThat(foundTheme).isNotNull();
			softly.assertThat(foundTheme.getId()).isEqualTo(1L);
			softly.assertThat(foundTheme.getName()).isEqualTo("테마1");
			softly.assertThat(foundTheme.getDescription()).isEqualTo("첫번째테마");
			softly.assertThat(foundTheme.getThumbnail()).isEqualTo("썸네일이미지");
		});
	}

	private void createAndSaveReservation(Theme theme, ReservationTime reservationTime) {

		// given
		Reservation reservation = Reservation.builder()
			.name("tester")
			.date(DataTimeFormatterUtils.TOMORROW_DATE)
			.theme(theme)
			.time(reservationTime)
			.build();

		// when
		var savedReservation = this.reservationJpaRepository.save(reservation);

		// then
		SoftAssertions.assertSoftly((softly) -> {
			softly.assertThat(savedReservation).isNotNull();
			softly.assertThat(savedReservation.getId()).isNotNull();
			softly.assertThat(savedReservation.getName()).isEqualTo("tester");
			softly.assertThat(savedReservation.getDate()).isEqualTo(DataTimeFormatterUtils.TOMORROW_DATE);
			softly.assertThat(savedReservation.getTheme()).isEqualTo(theme);
			softly.assertThat(savedReservation.getTime()).isEqualTo(reservationTime);
		});
	}

}
