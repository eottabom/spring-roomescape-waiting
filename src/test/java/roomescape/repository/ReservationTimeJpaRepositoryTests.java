package roomescape.repository;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReservationTimeJpaRepositoryTests {

	@Autowired
	private ReservationJpaRepository reservationJpaRepository;

	@Autowired
	private ThemeJpaRepository themeJpaRepository;

	@Autowired
	private ReservationTimeJpaRepository reservationTimeJpaRepository;

	@Test
	void saveReservationTime() {
		// given
		ReservationTime reservationTime = ReservationTime.builder().id(1L).startAt("10:00").build();

		// when
		var savedReservationTime = this.reservationTimeJpaRepository.save(reservationTime);

		// then
		assertThat(savedReservationTime).isNotNull();
		assertThat(savedReservationTime.getId()).isEqualTo(1L);
	}

	@Test
	void deleteReservationTime() {
		// given
		long id = 1L;

		// when
		this.reservationTimeJpaRepository.deleteById(id);

		// delete verification
		var foundReservationTime = this.reservationTimeJpaRepository.findById(1L);

		// then
		SoftAssertions.assertSoftly((softly) -> {
			softly.assertThat(foundReservationTime).isNotNull();
			softly.assertThat(foundReservationTime.isPresent()).isFalse();
		});
	}

	@Test
	void findReservationTimeById() {
		// given
		createThemeAndNReservationTimeAndNReservation(1);

		// when
		Optional<ReservationTime> foundReservationTime = this.reservationTimeJpaRepository.findById(1L);

		// then
		SoftAssertions.assertSoftly((softly) -> {
			softly.assertThat(foundReservationTime).isNotNull();
			softly.assertThat(foundReservationTime.isPresent()).isTrue();
		});
	}

	@Test
	void findReservedTimeIds() {
		// given
		createThemeAndNReservationTimeAndNReservation(3);
		String date = "2024-06-18";
		long themeId = 1L;
		List<Long> expectedIds = Arrays.asList(1L, 2L, 3L);

		// when
		List<Long> reservedTimeIds = this.reservationTimeJpaRepository.findReservedTimeIds(date, themeId);

		// then
		assertThat(reservedTimeIds).isNotNull();
		assertThat(reservedTimeIds).hasSize(expectedIds.size());
		assertThat(reservedTimeIds).containsExactlyElementsOf(expectedIds);
	}

	@Test
	void existsById() {
		// given
		createThemeAndNReservationTimeAndNReservation(1);

		// when
		var existsById = this.reservationTimeJpaRepository.existsById(1L);

		// then
		assertThat(existsById).isTrue();
	}

	private void createThemeAndNReservationTimeAndNReservation(int n) {
		Theme theme = Theme.builder().id(1L).name("Theme1").description("First theme").thumbnail("Theme image").build();
		this.themeJpaRepository.save(theme);

		for (int i = 1; i <= n; i++) {
			LocalTime startTime = LocalTime.parse("10:00").plusMinutes(i);
			ReservationTime reservationTime = ReservationTime.builder()
				.startAt(startTime.format(DateTimeFormatter.ofPattern("HH:mm")))
				.build();
			this.reservationTimeJpaRepository.save(reservationTime);

			Reservation reservation = Reservation.builder()
				.date("2024-06-18")
				.theme(theme)
				.time(reservationTime)
				.build();
			this.reservationJpaRepository.save(reservation);
		}
	}

}
