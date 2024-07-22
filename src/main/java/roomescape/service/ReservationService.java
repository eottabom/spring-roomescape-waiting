package roomescape.service;

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
import roomescape.web.controller.dto.ReservationWaitingRequest;
import roomescape.web.controller.dto.WaitingResponse;

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

		checkDuplicateReservation(date, themeId);

		var theme = this.themeService.getThemeById(themeId);
		var reservationStatus = checkReservationExists(date, reservationTime, themeId) ? ReservationStatus.WAITING
				: ReservationStatus.RESERVATION;

		var reservation = Reservation.builder()
			.name(createReservationRequest.getMemberName())
			.date(date)
			.time(reservationTime)
			.theme(theme)
			.status(reservationStatus)
			.member(createReservationRequest.getMember())
			.build();

		var savedReservation = this.reservationJpaRepository.save(reservation);

		return ReservationResponse.from(savedReservation, reservationTime, theme);
	}

	private void checkDuplicateReservation(String date, long themeId) {
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

	@Transactional
	public WaitingResponse createReservationWaiting(ReservationWaitingRequest request, LoginMember loginMember) {
		var member = this.memberService.findByEmail(loginMember.getEmail());
		var date = request.date();
		var reservationTime = this.reservationTimeService.getReservationTimeById(request.timeId());
		var themeId = request.themeId();
		var theme = this.themeService.getThemeById(themeId);

		if (this.reservationJpaRepository.existsByMemberAndDateAndTimeAndThemeAndStatus(member.getId(), date,
				reservationTime.getId(), themeId, ReservationStatus.WAITING)) {
			throw new RoomEscapeException(ErrorCode.DUPLICATE_RESERVATION);
		}

		var reservation = Reservation.builder()
			.name(loginMember.getName())
			.date(date)
			.time(reservationTime)
			.theme(theme)
			.status(ReservationStatus.WAITING)
			.member(member)
			.build();

		var savedReservation = this.reservationJpaRepository.save(reservation);

		return WaitingResponse.from(savedReservation, member.getId());
	}

}
