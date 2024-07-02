package roomescape.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import roomescape.DataTimeFormatterUtils;
import roomescape.domain.LoginMember;
import roomescape.domain.Member;
import roomescape.domain.MemberRole;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationStatus;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.repository.ReservationJpaRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class ReservationMineServiceTests {

	@InjectMocks
	private ReservationsMineService reservationsMineService;

	@Mock
	private ReservationJpaRepository reservationJpaRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getReservationsMine() {
		// given
		var loginMember = LoginMember.builder()
			.name("tester")
			.email("tester@gmail.com")
			.role(MemberRole.USER.name())
			.build();
		var reservationTime = ReservationTime.builder().id(1L).startAt("10:00").build();
		var theme = Theme.builder().id(1L).name("테마1").description("첫번째테마").thumbnail("테마이미지").build();
		var member = Member.builder()
			.id(1L)
			.name("tester")
			.email("tester@gmail.com")
			.password("encodedPassword")
			.role(MemberRole.USER.name())
			.build();

		Reservation reservation = Reservation.builder()
			.id(1L)
			.name("tester")
			.date(DataTimeFormatterUtils.TOMORROW_DATE)
			.time(reservationTime)
			.theme(theme)
			.member(member)
			.status(ReservationStatus.RESERVATION.name())
			.build();

		List<Reservation> reservations = new ArrayList<>();
		reservations.add(reservation);

		given(this.reservationJpaRepository.findByName("tester")).willReturn(reservations);

		// when
		var reservationsMineResponses = this.reservationsMineService.getReservationsMine(loginMember);

		// then
		assertThat(reservationsMineResponses).isNotEmpty();
		assertThat(reservationsMineResponses).hasSize(1);
		assertThat(reservationsMineResponses).allSatisfy((reservationsMineResponse) -> {
			assertThat(reservationsMineResponse.reservationId()).isEqualTo(1L);
			assertThat(reservationsMineResponse.themeName()).isEqualTo("테마1");
			assertThat(reservationsMineResponse.date()).isEqualTo(DataTimeFormatterUtils.TOMORROW_DATE);
			assertThat(reservationsMineResponse.time()).isEqualTo("10:00");
			assertThat(reservationsMineResponse.status()).isEqualTo(ReservationStatus.RESERVATION.getStatus());
		});

	}

}
