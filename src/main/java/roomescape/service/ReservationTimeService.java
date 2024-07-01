package roomescape.service;

import java.util.List;
import java.util.stream.Collectors;

import roomescape.domain.ReservationTime;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.ReservationTimeJpaRepository;
import roomescape.web.controller.dto.AvailableReservationTimeResponse;
import roomescape.web.controller.dto.ReservationTimeRequest;
import roomescape.web.controller.dto.ReservationTimeResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationTimeService {

	private final ReservationTimeJpaRepository reservationTimeJpaRepository;

	ReservationTimeService(ReservationTimeJpaRepository reservationTimeJpaRepository) {
		this.reservationTimeJpaRepository = reservationTimeJpaRepository;
	}

	@Transactional
	public ReservationTimeResponse create(ReservationTimeRequest request) {
		var reservationTime = ReservationTime.builder().startAt(request.startAt()).build();
		var savedReservationTime = this.reservationTimeJpaRepository.save(reservationTime);
		return ReservationTimeResponse.from(savedReservationTime);
	}

	@Transactional(readOnly = true)
	public List<ReservationTimeResponse> getReservationTimes() {
		return this.reservationTimeJpaRepository.findAll()
			.stream()
			.map(ReservationTimeResponse::from)
			.collect(Collectors.toList());
	}

	@Transactional
	public void delete(long id) {
		var isExist = this.reservationTimeJpaRepository.existsById(id);
		if (!isExist) {
			throw new RoomEscapeException(ErrorCode.NOT_FOUND_RESERVATION_TIME);
		}
		this.reservationTimeJpaRepository.deleteById(id);
	}

	@Transactional(readOnly = true)
	public ReservationTime getReservationTimeById(long id) {
		return this.reservationTimeJpaRepository.findById(id)
			.orElseThrow(() -> new RoomEscapeException(ErrorCode.NOT_FOUND_RESERVATION_TIME));
	}

	@Transactional(readOnly = true)
	public List<AvailableReservationTimeResponse> getAvailableReservationTimes(String date, long themeId) {
		var reservationTimes = this.reservationTimeJpaRepository.findAll();
		var reservedTimeIds = this.reservationTimeJpaRepository.findReservedTimeIds(date, themeId);

		return reservationTimes.stream()
			.map((reservationTime) -> AvailableReservationTimeResponse.from(reservationTime,
					reservedTimeIds.contains(reservationTime.getId())))
			.collect(Collectors.toList());
	}

}
