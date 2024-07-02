package roomescape.web.controller;

import java.util.List;

import roomescape.domain.LoginMember;
import roomescape.service.ReservationsMineService;
import roomescape.web.controller.dto.ReservationsMineResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservations-mine")
public class ReservationMineController {

	private final ReservationsMineService reservationsMineService;

	ReservationMineController(ReservationsMineService reservationsMineService) {
		this.reservationsMineService = reservationsMineService;
	}

	@GetMapping
	public ResponseEntity<List<ReservationsMineResponse>> getReservationsMine(LoginMember loginMember) {
		return ResponseEntity.ok().body(this.reservationsMineService.getReservationsMine(loginMember));
	}

}
