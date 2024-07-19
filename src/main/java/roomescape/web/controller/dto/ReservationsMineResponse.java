package roomescape.web.controller.dto;

import roomescape.domain.Reservation;
import roomescape.domain.ReservationStatus;

public record ReservationsMineResponse(long reservationId, String themeName, String date, String time, String status, long waitingOrder) {

	public static ReservationsMineResponse from(Reservation reservation, long waitingOrder) {
		return new ReservationsMineResponse(reservation.getId(), reservation.getTheme().getName(), reservation.getDate(),
				reservation.getTime().getStartAt(), ReservationStatus.fromStatus(reservation.getStatus()), waitingOrder);
	}

}
