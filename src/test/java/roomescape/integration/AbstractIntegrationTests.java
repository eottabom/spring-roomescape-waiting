package roomescape.integration;

import java.util.List;

import jakarta.servlet.http.Cookie;
import roomescape.DataTimeFormatterUtils;
import roomescape.auth.JwtCookieManager;
import roomescape.auth.JwtTokenProvider;
import roomescape.domain.MemberRole;
import roomescape.domain.ReservationStatus;
import roomescape.web.controller.dto.MemberResponse;
import roomescape.web.controller.dto.ReservationRequest;
import roomescape.web.controller.dto.ReservationResponse;
import roomescape.web.controller.dto.ReservationTimeRequest;
import roomescape.web.controller.dto.ReservationTimeResponse;
import roomescape.web.controller.dto.ReservationWaitingRequest;
import roomescape.web.controller.dto.ThemeRequest;
import roomescape.web.controller.dto.ThemeResponse;
import roomescape.web.controller.dto.WaitingResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:testdb")
public abstract class AbstractIntegrationTests {

	public final TestRestTemplate restTemplate = new TestRestTemplate();

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@LocalServerPort
	public int port;

	ReservationTimeResponse createReservationTime() {
		// given
		var reservationTimeRequest = new ReservationTimeRequest("10:00");

		// when
		var createReservationTime = this.restTemplate.postForEntity("http://localhost:" + this.port + "/times",
				reservationTimeRequest, ReservationTimeResponse.class);

		// then
		assertThat(createReservationTime.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		var reservationTimeResponse = createReservationTime.getBody();
		assertThat(reservationTimeResponse).isNotNull();
		assertThat(reservationTimeResponse.startAt()).isEqualTo("10:00");
		return reservationTimeResponse;
	}

	ThemeResponse createTheme() {
		// given
		var themeRequest = new ThemeRequest("테마1", "첫번째테마", "테마이미지");

		// when
		var createTheme = this.restTemplate.postForEntity("http://localhost:" + this.port + "/themes", themeRequest,
				ThemeResponse.class);

		// then
		assertThat(createTheme.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		var themeResponse = createTheme.getBody();
		assertThat(themeResponse).isNotNull();
		assertThat(themeResponse.id()).isEqualTo(1L);
		assertThat(themeResponse.name()).isEqualTo("테마1");
		assertThat(themeResponse.description()).isEqualTo("첫번째테마");
		assertThat(themeResponse.thumbnail()).isEqualTo("테마이미지");
		return themeResponse;
	}

	ReservationResponse createReservation() {
		// given
		var reservationRequest = new ReservationRequest("tester", DataTimeFormatterUtils.TOMORROW_DATE, 1L, 1L);
		var memberResponse = new MemberResponse(1L, "tester", "tester@gmail.com", MemberRole.USER.name());

		String token = this.jwtTokenProvider.createToken(memberResponse);
		Cookie cookie = JwtCookieManager.createCookie(token, 3600);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.COOKIE, cookie.getName() + "=" + cookie.getValue());

		HttpEntity<ReservationRequest> requestEntity = new HttpEntity<>(reservationRequest, headers);

		// when
		var createReservation = this.restTemplate.exchange("http://localhost:" + this.port + "/reservations",
				HttpMethod.POST, requestEntity, ReservationResponse.class);

		// then
		assertThat(createReservation.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		var reservationResponse = createReservation.getBody();
		assertThat(reservationResponse).isNotNull();
		assertThat(reservationResponse.name()).isEqualTo("tester");
		return reservationResponse;
	}

	void getReservations() {
		// given, when
		var getReservations = this.restTemplate.getForEntity("http://localhost:" + this.port + "/reservations",
				List.class);

		// then
		assertThat(getReservations.getStatusCode()).isEqualTo(HttpStatus.OK);
		var reservations = getReservations.getBody();
		assertThat(reservations).isNotNull();
		assertThat(reservations.size()).isGreaterThan(0);
	}

	void getReservationsMine() {
		// given
		var memberResponse = new MemberResponse(1L, "tester", "tester@gmail.com", MemberRole.USER.name());
		String token = this.jwtTokenProvider.createToken(memberResponse);
		Cookie cookie = JwtCookieManager.createCookie(token, 3600);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.COOKIE, cookie.getName() + "=" + cookie.getValue());

		HttpEntity<?> requestEntity = new HttpEntity<>(headers);

		// when
		var getReservationsMine = this.restTemplate.exchange("http://localhost:" + this.port + "/reservations-mine",
				HttpMethod.GET, requestEntity, List.class);

		// then
		assertThat(getReservationsMine.getStatusCode()).isEqualTo(HttpStatus.OK);
		var reservationsMine = getReservationsMine.getBody();
		assertThat(reservationsMine).isNotNull();
		assertThat(reservationsMine.size()).isGreaterThan(0);
	}

	void cancelReservation(long reservationId) {
		// given, when
		ResponseEntity<Void> cancelResponse = this.restTemplate.exchange(
				"http://localhost:" + this.port + "/reservations/" + reservationId, HttpMethod.DELETE, null,
				Void.class);

		// then
		assertThat(cancelResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	void checkReservation() {
		// given, when
		var getReservations = this.restTemplate.getForEntity("http://localhost:" + this.port + "/reservations",
				List.class);

		// then
		assertThat(getReservations.getStatusCode()).isEqualTo(HttpStatus.OK);
		var reservations = getReservations.getBody();
		assertThat(reservations).isNotNull();
		assertThat(reservations.size()).isEqualTo(0);
	}

	void deleteReservationTime(long reservationTimeId) {
		// given, when
		var deleteReservationTime = this.restTemplate.exchange(
				"http://localhost:" + this.port + "/times/" + reservationTimeId, HttpMethod.DELETE, null, Void.class);

		// then
		assertThat(deleteReservationTime.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	void deleteTheme(long themeId) {
		// given, when
		var deleteTheme = this.restTemplate.exchange("http://localhost:" + this.port + "/themes/" + themeId,
				HttpMethod.DELETE, null, Void.class);

		// then
		assertThat(deleteTheme.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	WaitingResponse createReservationWaiting() {
		// given
		var reservationWaitingRequest = new ReservationWaitingRequest("2024-06-06", 1L, 1L);
		var memberResponse = new MemberResponse(1L, "tester", "tester@gmail.com", MemberRole.USER.name());

		String token = this.jwtTokenProvider.createToken(memberResponse);
		Cookie cookie = JwtCookieManager.createCookie(token, 3600);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.COOKIE, cookie.getName() + "=" + cookie.getValue());

		HttpEntity<ReservationWaitingRequest> requestEntity = new HttpEntity<>(reservationWaitingRequest, headers);

		// when
		var createReservationWaiting = this.restTemplate.exchange("http://localhost:" + this.port + "/reservations-mine/waiting",
				HttpMethod.POST, requestEntity, WaitingResponse.class);

		// then
		assertThat(createReservationWaiting.getStatusCode()).isEqualTo(HttpStatus.OK);
		var waitingResponse = createReservationWaiting.getBody();
		assertThat(waitingResponse).isNotNull();
		assertThat(waitingResponse.status()).isEqualTo(ReservationStatus.WAITING.name());
		return waitingResponse;
	}

}
