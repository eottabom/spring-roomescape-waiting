package roomescape.integration;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationTimeIntegrationTests extends AbstractIntegrationTests {

	@Test
	void reservationTimeControllerEndpoints() {
		// create reservation time
		var createReservationTime = createReservationTime();

		// get reservation times
		// when
		var getReservationTimes = super.restTemplate.getForEntity("http://localhost:" + super.port + "/times",
				List.class);

		// then
		assertThat(getReservationTimes.getStatusCode()).isEqualTo(HttpStatus.OK);
		var reservationTimes = getReservationTimes.getBody();
		assertThat(reservationTimes).isNotNull();
		assertThat(reservationTimes.size()).isGreaterThan(0);

		// delete reservation time
		// given
		long reservationTimeId = createReservationTime.id();

		// when
		deleteReservationTime(reservationTimeId);

		// check reservation time
		getReservationTimes = this.restTemplate.getForEntity("http://localhost:" + this.port + "/times", List.class);

		// then
		assertThat(getReservationTimes.getStatusCode()).isEqualTo(HttpStatus.OK);
		reservationTimes = getReservationTimes.getBody();
		assertThat(reservationTimes).isNotNull();
		assertThat(reservationTimes.size()).isLessThan(1);

	}

}
