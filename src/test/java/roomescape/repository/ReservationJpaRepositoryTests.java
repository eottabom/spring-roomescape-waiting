package roomescape.repository;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;

import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationJpaRepositoryTests extends AbstractRepositoryTests {

	@Autowired
	private ReservationJpaRepository reservationJpaRepository;

	@Autowired
	private ThemeJpaRepository themeJpaRepository;

	@Autowired
	private ReservationTimeJpaRepository reservationTimeJpaRepository;

	@Test
	void findAllReservation() {
		// given
		createThemeAndReservationTimeAndReservation();

		// when
		var foundReservations = this.reservationJpaRepository.findAll();

		ReservationTime reservationTime = ReservationTime.builder().id(1L).startAt("10:00").build();
		Theme theme = Theme.builder().id(1L).name("테마1").description("첫번째테마").thumbnail("테마이미지").build();

		// then
		assertThat(foundReservations).isNotEmpty().allSatisfy((reservation) -> {
			assertThat(reservation).isNotNull();
			assertThat(reservation.getId()).isEqualTo(1L);
			assertThat(reservation.getName()).isEqualTo("tester");
			assertThat(reservation.getDate()).isEqualTo("2024-06-06");
			assertThat(reservation.getTheme()).isEqualTo(theme);
			assertThat(reservation.getTime()).isEqualTo(reservationTime);
		});
	}

	@Test
	void saveReservation() {
		// given
		ReservationTime reservationTime = ReservationTime.builder().id(1L).startAt("10:00").build();
		this.reservationTimeJpaRepository.save(reservationTime);

		Theme theme = Theme.builder().id(1L).name("테마1").description("첫번째테마").thumbnail("테마이미지").build();
		this.themeJpaRepository.save(theme);

		Reservation reservation = Reservation.builder()
			.id(1L)
			.name("tester")
			.date("2024-06-06")
			.time(reservationTime)
			.theme(theme)
			.build();

		this.reservationJpaRepository.save(reservation);

		// when
		Reservation savedReservation = this.reservationJpaRepository.save(reservation);

		// then
		SoftAssertions.assertSoftly((softly) -> {
			softly.assertThat(savedReservation).isNotNull();
			softly.assertThat(savedReservation.getId()).isEqualTo(1L);
			softly.assertThat(savedReservation.getName()).isEqualTo("tester");
			softly.assertThat(savedReservation.getDate()).isEqualTo("2024-06-06");
			softly.assertThat(savedReservation.getTheme()).isEqualTo(theme);
			softly.assertThat(savedReservation.getTime()).isEqualTo(reservationTime);
		});
	}

	@Test
	void deleteReservation() {
		// given
		createThemeAndReservationTimeAndReservation();

		// when
		this.reservationJpaRepository.deleteById(1L);

		// delete verification
		// when
		Optional<Reservation> foundReservation = this.reservationJpaRepository.findById(1L);

		// then
		SoftAssertions.assertSoftly((softly) -> {
			softly.assertThat(foundReservation).isNotNull();
			softly.assertThat(foundReservation.isPresent()).isFalse();
		});
	}

	@Test
	void findReservations() {
		// given
		createThemeAndReservationTimeAndReservation();

		long memberId = 1L;
		long themeId = 1L;
		String dateFrom = "2024-06-01";
		String dateTo = "2024-06-30";

		ReservationTime reservationTime = ReservationTime.builder().id(1L).startAt("10:00").build();
		Theme theme = Theme.builder().id(1L).name("테마1").description("첫번째테마").thumbnail("테마이미지").build();

		Reservation reservation = Reservation.builder()
			.id(1L)
			.name("tester")
			.date("2024-06-06")
			.time(reservationTime)
			.theme(theme)
			.build();

		List<Reservation> reservations = List.of(reservation);

		// when
		var foundReservations = this.reservationJpaRepository.findReservations(memberId, themeId, dateFrom, dateTo);

		// then
		assertThat(foundReservations).isEqualTo(reservations);
		assertThat(foundReservations).allSatisfy((resultReservation) -> {
			assertThat(resultReservation.getName()).isEqualTo("tester");
			assertThat(resultReservation.getTheme().getId()).isEqualTo(1L);
			assertThat(resultReservation.getDate()).isBetween("2024-06-01", "2024-06-30");
		});
	}

	@Test
	void isDuplicateReservation() {
		// given
		createThemeAndReservationTimeAndReservation();

		// when
		var duplicateReservation = this.reservationJpaRepository.isDuplicateReservation("2024-06-06", 1L);

		// then
		assertThat(duplicateReservation).isTrue();
	}

}
