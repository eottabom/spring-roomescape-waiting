package roomescape.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import roomescape.DataTimeFormatterUtils;
import roomescape.domain.ReservationTime;

import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationTimeJpaRepositoryTests extends AbstractRepositoryTests {

	@Autowired
	private ReservationTimeJpaRepository reservationTimeJpaRepository;

	@BeforeEach
	void setup() {
		createTheme();
	}

	@Test
	void saveReservationTime() {
		// given
		ReservationTime reservationTime = generateReservationTime(1L, "10:00");

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
		createReservationsWithReservationTime(1);

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
		createReservationsWithReservationTime(3);
		long themeId = 1L;
		List<Long> expectedIds = Arrays.asList(1L, 2L, 3L);

		// when
		List<Long> reservedTimeIds = this.reservationTimeJpaRepository
			.findReservedTimeIds(DataTimeFormatterUtils.TOMORROW_DATE, themeId);

		// then
		assertThat(reservedTimeIds).isNotNull();
		assertThat(reservedTimeIds).hasSize(expectedIds.size());
		assertThat(reservedTimeIds).containsExactlyElementsOf(expectedIds);
	}

	@Test
	void existsById() {
		// given
		createReservationsWithReservationTime(1);

		// when
		var existsById = this.reservationTimeJpaRepository.existsById(1L);

		// then
		assertThat(existsById).isTrue();
	}

}
