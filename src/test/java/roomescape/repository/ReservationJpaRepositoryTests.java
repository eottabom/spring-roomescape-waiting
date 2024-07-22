package roomescape.repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import roomescape.DataTimeFormatterUtils;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationStatus;
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
			assertThat(reservation.getDate()).isEqualTo(DataTimeFormatterUtils.TOMORROW_DATE);
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
			.date(DataTimeFormatterUtils.TOMORROW_DATE)
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
			softly.assertThat(savedReservation.getDate()).isEqualTo(DataTimeFormatterUtils.TOMORROW_DATE);
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
		String dateFrom = LocalDate.now().minusMonths(1L).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String dateTo = LocalDate.now().plusMonths(1L).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		ReservationTime reservationTime = ReservationTime.builder().id(1L).startAt("10:00").build();
		Theme theme = Theme.builder().id(1L).name("테마1").description("첫번째테마").thumbnail("테마이미지").build();

		Reservation reservation = Reservation.builder()
			.id(1L)
			.name("tester")
			.date(DataTimeFormatterUtils.TOMORROW_DATE)
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
			assertThat(resultReservation.getDate()).isBetween(dateFrom, dateTo);
		});
	}

	@Test
	void isDuplicateReservation() {
		// given
		createThemeAndReservationTimeAndReservation();

		// when
		var duplicateReservation = this.reservationJpaRepository.isDuplicateReservation(DataTimeFormatterUtils.TOMORROW_DATE, 1L);

		// then
		assertThat(duplicateReservation).isTrue();
	}

	@Test
	void findByName() {
		// given
		createThemeAndReservationTimeAndReservation();
		String name = "tester";

		// when
		var foundReservationByUserName = this.reservationJpaRepository.findByName(name);

		// then
		assertThat(foundReservationByUserName).isNotEmpty();
		assertThat(foundReservationByUserName).hasSize(1);
		assertThat(foundReservationByUserName).allSatisfy((reservationsMineResponse) -> {
			assertThat(reservationsMineResponse.getId()).isEqualTo(1L);
			assertThat(reservationsMineResponse.getName()).isEqualTo("tester");
			assertThat(reservationsMineResponse.getDate()).isEqualTo(DataTimeFormatterUtils.TOMORROW_DATE);
			assertThat(reservationsMineResponse.getTime().getStartAt()).isEqualTo("10:00");
			assertThat(reservationsMineResponse.getTheme().getName()).isEqualTo("테마1");
			assertThat(reservationsMineResponse.getStatus()).isEqualTo(ReservationStatus.RESERVATION);
		});
	}

	@Test
	void findByDateAndTimeAndThemeIdWhenNotEmpty() {
		// given
		createThemeAndReservationTimeAndReservation();

		var reservationTime = ReservationTime.builder().id(1L).startAt("10:00").build();

		// when
		var existsReservations = this.reservationJpaRepository.findByDateAndTimeAndThemeId(DataTimeFormatterUtils.TOMORROW_DATE,
				reservationTime, 1L);

		assertThat(existsReservations).isNotEmpty();
		assertThat(existsReservations).allSatisfy((reservation) -> {
			assertThat(reservation.getId()).isEqualTo(1L);
			assertThat(reservation.getName()).isEqualTo("tester");
			assertThat(reservation.getDate()).isEqualTo(DataTimeFormatterUtils.TOMORROW_DATE);
			assertThat(reservation.getTime().getStartAt()).isEqualTo("10:00");
			assertThat(reservation.getTheme().getName()).isEqualTo("테마1");
			assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.RESERVATION);
		});
	}

	@Test
	void findByDateAndTimeAndThemeIdWhenEmpty() {
		// given
		var reservationTime = ReservationTime.builder().id(1L).startAt("10:00").build();

		// when
		var existsReservations = this.reservationJpaRepository.findByDateAndTimeAndThemeId("2024-06-06",
				reservationTime, 1L);

		assertThat(existsReservations).isEmpty();
	}

	@Test
	void findReservationsWithRankByMemberName() {
		// given
		createThemeAndReservationTimeAndReservation();
		String memberName = "tester";

		// when
		var foundReservationsWithRank = this.reservationJpaRepository.findReservationsWithRankByMemberName(memberName);

		// then
		assertThat(foundReservationsWithRank).isNotEmpty();
		assertThat(foundReservationsWithRank).allSatisfy((reservationWithRank) -> {
			assertThat(reservationWithRank.reservation().getId()).isEqualTo(1L);
			assertThat(reservationWithRank.reservation().getName()).isEqualTo("tester");
			assertThat(reservationWithRank.reservation().getDate()).isEqualTo(DataTimeFormatterUtils.TOMORROW_DATE);
			assertThat(reservationWithRank.reservation().getTime().getStartAt()).isEqualTo("10:00");
			assertThat(reservationWithRank.reservation().getTheme().getName()).isEqualTo("테마1");
			assertThat(reservationWithRank.rank()).isEqualTo(0L);
		});
	}

}
