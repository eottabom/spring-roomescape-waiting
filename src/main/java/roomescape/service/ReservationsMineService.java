package roomescape.service;

import java.util.List;
import java.util.stream.Collectors;

import roomescape.domain.LoginMember;
import roomescape.domain.ReservationWithRank;
import roomescape.repository.ReservationJpaRepository;
import roomescape.web.controller.dto.ReservationsMineResponse;

import org.springframework.stereotype.Service;

@Service
public class ReservationsMineService {

	private final ReservationJpaRepository reservationJpaRepository;

	public ReservationsMineService(ReservationJpaRepository reservationJpaRepository) {
		this.reservationJpaRepository = reservationJpaRepository;
	}

	public List<ReservationsMineResponse> getReservationsMine(LoginMember loginMember) {
		List<ReservationWithRank> reservationWithRanks = this.reservationJpaRepository
			.findReservationsWithRankByMemberName(loginMember.getName());
		return reservationWithRanks.stream()
			.map((reservationWithRank) -> ReservationsMineResponse.from(reservationWithRank.reservation(),
					reservationWithRank.rank()))
			.collect(Collectors.toList());
	}

}
