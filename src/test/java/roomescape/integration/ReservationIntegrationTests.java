package roomescape.integration;

import org.junit.jupiter.api.Test;

class ReservationIntegrationTests extends AbstractIntegrationTests {

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
