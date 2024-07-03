package roomescape.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import roomescape.domain.LoginMember;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationStatus;
import roomescape.domain.ReservationTime;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.ReservationJpaRepository;
import roomescape.web.controller.dto.CreateReservationRequest;
import roomescape.web.controller.dto.ReservationAdminRequest;
import roomescape.web.controller.dto.ReservationRequest;
import roomescape.web.controller.dto.ReservationResponse;
import roomescape.web.controller.dto.ReservationSearchRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

	private final ReservationJpaRepository reservationJpaRepository;

	private final ReservationTimeService reservationTimeService;

	private final ThemeService themeService;

	private final MemberService memberService;

	ReservationService(ReservationJpaRepository reservationJpaRepository, ReservationTimeService reservationTimeService,
			ThemeService themeService, MemberService memberService) {
		this.reservationJpaRepository = reservationJpaRepository;
		this.reservationTimeService = reservationTimeService;
		this.themeService = themeService;
		this.memberService = memberService;
	}

	@Transactional(readOnly = true)
	public List<ReservationResponse> getReservations() {
		return this.reservationJpaRepository.findAll().stream().map(ReservationResponse::from).toList();
	}

	@Transactional
	public ReservationResponse create(ReservationRequest request, LoginMember loginMember) {
		var foundMember = this.memberService.findByEmail(loginMember.getEmail());
		var createReservationRequest = CreateReservationRequest.builder()
			.date(request.date())
			.timeId(request.timeId())
			.themeId(request.themeId())
			.memberName(loginMember.getName())
			.member(foundMember)
			.build();
		return createReservation(createReservationRequest);
	}

	@Transactional
	public ReservationResponse createByAdmin(ReservationAdminRequest request) {
		var foundMember = this.memberService.findById(request.memberId());
		var createReservationRequest = CreateReservationRequest.builder()
			.date(request.date())
			.timeId(request.timeId())
			.themeId(request.themeId())
			.memberName(foundMember.getName())
			.member(foundMember)
			.build();
		return createReservation(createReservationRequest);
	}

	private ReservationResponse createReservation(CreateReservationRequest createReservationRequest) {
		var reservationTime = this.reservationTimeService.getReservationTimeById(createReservationRequest.getTimeId());
		var date = createReservationRequest.getDate();
		var themeId = createReservationRequest.getThemeId();

		checkReservationAvailability(date, reservationTime.getStartAt(), themeId);

		var theme = this.themeService.getThemeById(themeId);
		var reservationStatus = checkReservationExists(date, reservationTime, themeId) ? ReservationStatus.WAITING
				: ReservationStatus.RESERVATION;

		var reservation = Reservation.builder()
			.name(createReservationRequest.getMemberName())
			.date(date)
			.time(reservationTime)
			.theme(theme)
			.status(reservationStatus.name())
			.member(createReservationRequest.getMember())
			.build();

		var savedReservation = this.reservationJpaRepository.save(reservation);

		return ReservationResponse.from(savedReservation, reservationTime, theme);
	}

	private void checkReservationAvailability(String date, String time, long themeId) {
		LocalDate reservationDate = LocalDate.parse(date);
		LocalTime reservationTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
		if (reservationDate.isBefore(LocalDate.now())
				|| (reservationDate.isEqual(LocalDate.now()) && reservationTime.isBefore(LocalTime.now()))) {
			throw new RoomEscapeException(ErrorCode.PAST_RESERVATION);
		}

		if (this.reservationJpaRepository.isDuplicateReservation(date, themeId)) {
			throw new RoomEscapeException(ErrorCode.DUPLICATE_RESERVATION);
		}
	}

	private boolean checkReservationExists(String date, ReservationTime time, Long themeId) {
		List<Reservation> existsReservations = this.reservationJpaRepository.findByDateAndTimeAndThemeId(date, time,
				themeId);
		return !existsReservations.isEmpty();
	}

	@Transactional
	public void cancel(long id) {
		var isExist = this.reservationJpaRepository.existsById(id);
		if (!isExist) {
			throw new RoomEscapeException(ErrorCode.NOT_FOUND_RESERVATION);
		}
		this.reservationJpaRepository.deleteById(id);
	}

	@Transactional(readOnly = true)
	public List<ReservationResponse> searchReservations(ReservationSearchRequest request) {
		return ReservationResponse.from(this.reservationJpaRepository.findReservations(request.memberId(),
				request.themeId(), request.dateFrom(), request.dateTo()));
	}

}
