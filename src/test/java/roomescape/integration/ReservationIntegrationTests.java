package roomescape.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReservationIntegrationTests extends AbstractIntegrationTests {

	@DisplayName("예약 컨트롤러 및 내 예약 목록 조회 통합테스트")
	@Test
	void reservationControllerEndpoints() {
		// create reservation time
		var reservationTimeResponse = createReservationTime();

		// create theme
		var themeResponse = createTheme();

		// create reservation
		var reservationResponse = createReservation();

		// get reservations
		getReservations();

		// get reservations mine
		getReservationsMine();

		// cancel reservation
		cancelReservation(reservationResponse.id());

		// check reservation
		checkReservation();

		// delete reservation time
		deleteReservationTime(reservationTimeResponse.id());

		// delete theme
		deleteTheme(themeResponse.id());
	}

}
