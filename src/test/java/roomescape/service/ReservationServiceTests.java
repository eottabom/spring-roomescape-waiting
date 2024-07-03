package roomescape.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.ReservationJpaRepository;
import roomescape.repository.ReservationTimeJpaRepository;
import roomescape.web.controller.dto.ReservationAdminRequest;
import roomescape.web.controller.dto.ReservationRequest;
import roomescape.web.controller.dto.ReservationResponse;
import roomescape.web.controller.dto.ReservationSearchRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class ReservationServiceTests {

	@InjectMocks
	private ReservationService reservationService;

	@Mock
	private ReservationTimeService reservationTimeService;

	@Mock
	private ThemeService themeService;

	@Mock
	private MemberService memberService;

	@Mock
	private ReservationJpaRepository reservationJpaRepository;

	@Mock
	private ReservationTimeJpaRepository reservationTimeJpaRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getReservations() {
		// given
		List<Reservation> reservations = new ArrayList<>();

		var reservationTime = ReservationTime.builder().id(1L).startAt("10:00").build();
		var theme = Theme.builder().id(1L).name("테마1").description("첫번째테마").thumbnail("테마이미지").build();
		var member = Member.builder()
			.id(1L)
			.name("tester")
			.email("tester@gmail.com")
			.role(MemberRole.USER.name())
			.build();

		var reservation = Reservation.builder()
			.id(1L)
			.name("tester")
			.date("2024-06-06")
			.time(reservationTime)
			.theme(theme)
			.member(member)
			.status(ReservationStatus.RESERVATION.name())
			.build();
		reservations.add(reservation);

		given(this.reservationJpaRepository.findAll()).willReturn(reservations);

		// when
		var resultReservations = this.reservationService.getReservations();

		// then
		assertThat(resultReservations).isNotEmpty();
		assertThat(resultReservations).hasSize(1);
		assertThat(resultReservations).allSatisfy((reservationResponse) -> {
			assertThat(reservationResponse.id()).isEqualTo(1L);
			assertThat(reservationResponse.name()).isEqualTo("tester");
			assertThat(reservationResponse.date()).isEqualTo("2024-06-06");
			assertThat(reservationResponse.time().id()).isEqualTo(1L);
			assertThat(reservationResponse.time().startAt()).isEqualTo("10:00");
			assertThat(reservationResponse.theme().id()).isEqualTo(1L);
			assertThat(reservationResponse.theme().name()).isEqualTo("테마1");
			assertThat(reservationResponse.theme().description()).isEqualTo("첫번째테마");
			assertThat(reservationResponse.theme().thumbnail()).isEqualTo("테마이미지");
		});
	}

	@Test
	void createReservation() {

		// given
		var reservationRequest = new ReservationRequest("tester", DataTimeFormatterUtils.TOMORROW_DATE, 1L, 1L);
		var reservationTime = ReservationTime.builder().id(1L).startAt("10:00").build();
		var theme = Theme.builder().id(1L).name("테마1").description("첫번째테마").thumbnail("테마이미지").build();
		var member = Member.builder()
			.id(1L)
			.name("tester")
			.email("tester@gmail.com")
			.password("encodedPassword")
			.role(MemberRole.USER.name())
			.build();

		var loginMember = LoginMember.builder()
			.name("tester")
			.email("tester@gmail.com")
			.role(MemberRole.USER.name())
			.build();

		var reservation = Reservation.builder()
			.id(1L)
			.name("tester")
			.date("2024-06-06")
			.time(reservationTime)
			.theme(theme)
			.member(member)
			.status(ReservationStatus.RESERVATION.name())
			.build();

		given(this.themeService.getThemeById(1L)).willReturn(theme);
		given(this.reservationTimeService.getReservationTimeById(1L)).willReturn(reservationTime);
		given(this.reservationTimeJpaRepository.findById(1L)).willReturn(Optional.ofNullable(reservationTime));
		given(this.reservationJpaRepository.save(any(Reservation.class))).willReturn(reservation);

		var createdReservation = this.reservationService.create(reservationRequest, loginMember);

		// then
		assertThat(createdReservation).isNotNull();
		assertThat(createdReservation.id()).isEqualTo(1L);
		assertThat(createdReservation.name()).isEqualTo("tester");
		assertThat(createdReservation.date()).isEqualTo("2024-06-06");
		assertThat(createdReservation.time()).isNotNull();
		assertThat(createdReservation.time().id()).isEqualTo(1L);
		assertThat(createdReservation.time().startAt()).isEqualTo("10:00");
		assertThat(createdReservation.theme()).isNotNull();
		assertThat(createdReservation.theme().id()).isEqualTo(1L);
		assertThat(createdReservation.theme().name()).isEqualTo("테마1");
		assertThat(createdReservation.theme().description()).isEqualTo("첫번째테마");
		assertThat(createdReservation.theme().thumbnail()).isEqualTo("테마이미지");
	}

	@DisplayName("해당 날짜, 시간, 테마에 이미 예약이 되어 있는 경우 예약 대기 상태여야 한다.")
	@Test
	void createReservationWhenReservationAlreadyExists() {
		// given
		var loginMember = LoginMember.builder()
			.name("tester")
			.email("tester@gmail.com")
			.role(MemberRole.USER.name())
			.build();

		ReservationRequest request = new ReservationRequest("tester", DataTimeFormatterUtils.TOMORROW_DATE, 1L, 1L);

		var foundMember = Member.builder()
			.id(1L)
			.name("tester")
			.email("tester@gmail.com")
			.password("encodedPassword")
			.role(MemberRole.USER.name())
			.build();

		var reservationTime = ReservationTime.builder().id(1L).startAt("10:00").build();

		var theme = Theme.builder().id(1L).name("테마1").description("첫번째테마").thumbnail("테마이미지").build();

		var existingReservation = Reservation.builder()
			.id(2L)
			.name("anotherUser")
			.date("2024-07-04")
			.time(reservationTime)
			.theme(theme)
			.member(foundMember)
			.status(ReservationStatus.RESERVATION.name())
			.build();

		given(this.memberService.findByEmail("tester@gmail.com")).willReturn(foundMember);
		given(this.reservationTimeService.getReservationTimeById(1L)).willReturn(reservationTime);
		given(this.themeService.getThemeById(1L)).willReturn(theme);
		given(this.reservationJpaRepository.findByDateAndTimeAndThemeId("2024-07-04", reservationTime, 1L))
			.willReturn(List.of(existingReservation));
		given(this.reservationJpaRepository.save(any())).willAnswer((invocation) -> {
			Reservation reservation = invocation.getArgument(0);

			// then
			assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.WAITING.name());
			reservation.setId(1L);
			return reservation;
		});

		// when
		var reservationResponse = this.reservationService.create(request, loginMember);

		// then
		assertThat(reservationResponse).isNotNull();
		assertThat(reservationResponse).isNotNull();
		assertThat(reservationResponse.id()).isEqualTo(1L);
		assertThat(reservationResponse.name()).isEqualTo("tester");
		assertThat(reservationResponse.date()).isEqualTo(DataTimeFormatterUtils.TOMORROW_DATE);
		assertThat(reservationResponse.time().id()).isEqualTo(1L);
		assertThat(reservationResponse.time().startAt()).isEqualTo("10:00");
		assertThat(reservationResponse.theme().id()).isEqualTo(1L);
		assertThat(reservationResponse.theme().name()).isEqualTo("테마1");
	}

	@Test
	void createReservationByAdmin() {
		// given
		ReservationAdminRequest request = new ReservationAdminRequest("예약자이름", DataTimeFormatterUtils.TOMORROW_DATE, 1L,
				1L, 1L);

		var member = Member.builder().id(1L).name("예약자이름").email("admin@nextstep.com").role("ADMIN").build();
		var reservationTime = ReservationTime.builder().id(1L).startAt("10:00").build();
		var theme = Theme.builder().id(1L).name("테마1").description("테마 설명").thumbnail("테마 이미지").build();

		given(this.memberService.findById(1L)).willReturn(member);
		given(this.reservationTimeService.getReservationTimeById(1L)).willReturn(reservationTime);
		given(this.themeService.getThemeById(1L)).willReturn(theme);
		given(this.reservationJpaRepository.save(any())).willAnswer((invocation) -> {
			Reservation reservation = invocation.getArgument(0);
			reservation.setId(1L);
			return reservation;
		});

		// when
		var createdReservation = this.reservationService.createByAdmin(request);

		// then
		assertThat(createdReservation).isNotNull();
		assertThat(createdReservation.name()).isEqualTo("예약자이름");
		assertThat(createdReservation.date()).isEqualTo(DataTimeFormatterUtils.TOMORROW_DATE);
		assertThat(createdReservation.time().id()).isEqualTo(1L);
		assertThat(createdReservation.theme().id()).isEqualTo(1L);
	}

	@Test
	void createReservationWhenPastDate() {
		// given
		ReservationRequest request = new ReservationRequest("tester", DataTimeFormatterUtils.YESTERDAY_DATE, 1L, 1L);

		var reservationTime = ReservationTime.builder().id(1L).startAt("10:00").build();
		var theme = Theme.builder().id(1L).name("테마1").description("첫번째테마").thumbnail("테마이미지").build();
		var member = Member.builder()
			.id(1L)
			.name("tester")
			.email("tester@gmail.com")
			.role(MemberRole.USER.name())
			.build();

		var reservation = Reservation.builder()
			.id(1L)
			.name("tester")
			.date("2024-06-06")
			.time(reservationTime)
			.theme(theme)
			.member(member)
			.status(ReservationStatus.RESERVATION.name())
			.build();

		given(this.themeService.getThemeById(1L)).willReturn(theme);
		given(this.reservationTimeService.getReservationTimeById(1L)).willReturn(reservationTime);
		given(this.reservationTimeJpaRepository.findById(1L)).willReturn(Optional.ofNullable(reservationTime));
		given(this.reservationJpaRepository.save(any(Reservation.class))).willReturn(reservation);

		var loginMember = LoginMember.builder()
			.name("tester")
			.email("tester@gmail.com")
			.role(MemberRole.USER.name())
			.build();

		// when, then
		assertThatThrownBy(() -> this.reservationService.create(request, loginMember))
			.isInstanceOf(RoomEscapeException.class)
			.hasMessage(ErrorCode.PAST_RESERVATION.getMessage());
	}

	@Test
	void createReservationByAdminWhenPastDate() {
		// given
		ReservationAdminRequest request = new ReservationAdminRequest("예약자이름", DataTimeFormatterUtils.YESTERDAY_DATE,
				1L, 1L, 1L);

		var member = Member.builder().id(1L).name("예약자이름").email("admin@nextstep.com").role("ADMIN").build();
		var reservationTime = ReservationTime.builder().id(1L).startAt("10:00").build();
		var theme = Theme.builder().id(1L).name("테마1").description("테마 설명").thumbnail("테마 이미지").build();

		given(this.memberService.findById(1L)).willReturn(member);
		given(this.reservationTimeService.getReservationTimeById(1L)).willReturn(reservationTime);
		given(this.themeService.getThemeById(1L)).willReturn(theme);
		given(this.reservationJpaRepository.save(any())).willAnswer((invocation) -> {
			Reservation reservation = invocation.getArgument(0);
			reservation.setId(1L);
			return reservation;
		});

		// when, then
		assertThatThrownBy(() -> this.reservationService.createByAdmin(request)).isInstanceOf(RoomEscapeException.class)
			.hasMessage(ErrorCode.PAST_RESERVATION.getMessage());
	}

	@Test
	void cancelReservationException() {
		// given
		long id = 1L;

		// when, then
		assertThatThrownBy(() -> this.reservationService.cancel(id)).isInstanceOf(RoomEscapeException.class)
			.hasMessage(ErrorCode.NOT_FOUND_RESERVATION.getMessage());
	}

	@Test
	void searchReservations() {
		// given
		long memberId = 1L;
		long themeId = 1L;
		String dateFrom = "2024-06-01";
		String dateTo = "2024-06-30";

		var reservationTime = ReservationTime.builder().id(1L).startAt("10:00").build();
		var theme = Theme.builder().id(1L).name("테마1").description("첫번째테마").thumbnail("테마이미지").build();
		var member = Member.builder()
			.id(1L)
			.name("tester")
			.email("tester@gmail.com")
			.role(MemberRole.USER.name())
			.build();

		var reservation = Reservation.builder()
			.id(1L)
			.name("tester")
			.date("2024-06-06")
			.time(reservationTime)
			.theme(theme)
			.member(member)
			.status(ReservationStatus.RESERVATION.name())
			.build();

		List<Reservation> reservations = List.of(reservation);

		ReservationSearchRequest request = new ReservationSearchRequest(memberId, themeId, dateFrom, dateTo);
		given(this.reservationJpaRepository.findReservations(anyLong(), anyLong(), anyString(), anyString()))
			.willReturn(reservations);

		// when
		List<ReservationResponse> searchReservations = this.reservationService.searchReservations(request);

		// then
		assertThat(searchReservations).hasSize(1);
		assertThat(searchReservations).allSatisfy((resultReservation) -> {
			assertThat(resultReservation.name()).isEqualTo("tester");
			assertThat(resultReservation.theme().id()).isEqualTo(1L);
			assertThat(resultReservation.date()).isBetween("2024-06-01", "2024-06-30");
		});
	}

	@Test
	void searchReservationsEmptyResult() {

		// given
		long memberId = 1L;
		long themeId = 1L;
		String dateFrom = "2024-06-01";
		String dateTo = "2024-06-30";

		ReservationSearchRequest request = new ReservationSearchRequest(memberId, themeId, dateFrom, dateTo);

		given(this.reservationJpaRepository.findReservations(anyLong(), anyLong(), anyString(), anyString()))
			.willReturn(Collections.emptyList());

		// when
		List<ReservationResponse> searchReservations = this.reservationService.searchReservations(request);

		// then
		assertThat(searchReservations).isEmpty();
	}

}
