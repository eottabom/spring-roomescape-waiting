package roomescape.web.controller;

import java.util.List;

import roomescape.domain.LoginMember;
import roomescape.service.ReservationService;
import roomescape.service.ReservationsMineService;
import roomescape.web.controller.dto.ReservationWaitingRequest;
import roomescape.web.controller.dto.ReservationsMineResponse;
import roomescape.web.controller.dto.WaitingResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservations-mine")
public class ReservationMineController {

	private final ReservationsMineService reservationsMineService;

	private final ReservationService reservationService;

	ReservationMineController(ReservationsMineService reservationsMineService, ReservationService reservationService) {
		this.reservationsMineService = reservationsMineService;
		this.reservationService = reservationService;
	}

	@GetMapping
	public ResponseEntity<List<ReservationsMineResponse>> getReservationsMine(LoginMember loginMember) {
		return ResponseEntity.ok().body(this.reservationsMineService.getReservationsMine(loginMember));
	}

	@PostMapping("/waiting")
	public ResponseEntity<WaitingResponse> reservationWaiting(@RequestBody ReservationWaitingRequest request,
			LoginMember loginMember) {
		return ResponseEntity.ok().body(this.reservationService.createReservationWaiting(request, loginMember));
	}

}
