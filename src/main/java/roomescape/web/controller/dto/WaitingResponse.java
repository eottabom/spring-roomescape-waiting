package roomescape.web.controller.dto;

import roomescape.domain.Reservation;

public record WaitingResponse(long id, long memberId, long timeId, long themeId, String date, String status) {

	public static WaitingResponse from(Reservation reservation, long memberId) {
		return new WaitingResponse(reservation.getId(), memberId, reservation.getTime().getId(),
				reservation.getTheme().getId(), reservation.getDate(), reservation.getStatus().name());
	}
}
