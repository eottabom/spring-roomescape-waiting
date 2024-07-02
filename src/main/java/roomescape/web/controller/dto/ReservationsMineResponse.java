package roomescape.web.controller.dto;

import roomescape.domain.Reservation;
import roomescape.domain.ReservationStatus;

public record ReservationsMineResponse(long reservationId, String themeName, String date, String time, String status) {

	public static ReservationsMineResponse from(Reservation reservation) {
		return new ReservationsMineResponse(reservation.getId(), reservation.getName(), reservation.getDate(),
				reservation.getTime().getStartAt(), ReservationStatus.fromStatus(reservation.getStatus()));
	}

}
