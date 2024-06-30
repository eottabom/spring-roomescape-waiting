package roomescape.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ReservationRepositoryTests {

	@InjectMocks
	private ReservationRepository reservationRepository;

	@Mock
	private JdbcTemplate jdbcTemplate;

	@Mock
	private SimpleJdbcInsert jdbcInsert;

	@Mock
	private DataSource dataSource;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		given(this.jdbcTemplate.getDataSource()).willReturn(this.dataSource);

		this.jdbcInsert = mock(SimpleJdbcInsert.class);

		ReflectionTestUtils.setField(this.reservationRepository, "jdbcInsert", this.jdbcInsert);
	}

	@Test
	void findAllReservation() {
		// given
		List<Reservation> reservations = new ArrayList<>();

		given(this.jdbcTemplate.query(anyString(), any(RowMapper.class))).willReturn(reservations);

		// when
		List<Reservation> result = this.reservationRepository.findAll();

		// then
		assertThat(result).isEqualTo(reservations);
	}

	@Test
	void saveReservation() {

		// given
		ReservationTime reservationTime = ReservationTime.builder().id(1L).startAt("10:00").build();
		Theme theme = Theme.builder().id(1L).name("테마1").description("첫번째테마").thumbnail("테마이미지").build();

		Reservation reservation = Reservation.builder()
			.id(1L)
			.name("tester")
			.date("2024-06-06")
			.time(reservationTime)
			.theme(theme)
			.build();

		given(this.jdbcInsert.executeAndReturnKey(any(Map.class))).willReturn(1L);

		// when
		Reservation savedReservation = this.reservationRepository.save(reservation);

		// then
		assertThat(savedReservation).isNotNull();
		assertThat(savedReservation.getId()).isEqualTo(1L);
	}

	@Test
	void deleteReservation() {
		// given
		long id = 1L;
		given(this.jdbcTemplate.update(anyString(), any(Object[].class))).willReturn(1);

		// when
		this.reservationRepository.delete(id);

		// then
		verify(this.jdbcTemplate).update(anyString(), eq(id));
	}

	@Test
	void findReservations() {
		// given
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

		given(this.jdbcTemplate.query(anyString(), any(RowMapper.class), eq(memberId), eq(themeId), eq(dateFrom), eq(dateTo)))
				.willReturn(reservations);

		// when
		var foundReservations = this.reservationRepository.findReservations(memberId, themeId, dateFrom, dateTo);

		// then
		assertThat(foundReservations).isEqualTo(reservations);
		assertThat(foundReservations).isEqualTo(reservations);
		assertThat(foundReservations).allSatisfy((resultReservation) -> {
			assertThat(resultReservation.getName()).isEqualTo("tester");
			assertThat(resultReservation.getTheme().getId()).isEqualTo(1L);
			assertThat(resultReservation.getDate()).isBetween("2024-06-01", "2024-06-30");
		});
	}

}
