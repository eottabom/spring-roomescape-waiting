package roomescape.repository;

import java.util.List;

import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.ReservationWithRank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

	boolean existsById(long id);

	@Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.date = :date AND r.time.id = :timeId")
	boolean isDuplicateReservation(@Param("date") String date, @Param("timeId") long timeId);

	@Query("""
					SELECT r FROM Reservation r
					JOIN r.time rt
					JOIN r.theme t
					JOIN Member m ON r.name = m.name
					WHERE m.id = :memberId
					AND t.id = :themeId
					AND r.date BETWEEN :dateFrom AND :dateTo
			""")
	List<Reservation> findReservations(@Param("memberId") long memberId, @Param("themeId") long themeId,
			@Param("dateFrom") String dateFrom, @Param("dateTo") String dateTo);

	List<Reservation> findByName(String name);

	List<Reservation> findByDateAndTimeAndThemeId(String date, ReservationTime time, Long theme_id);

	@Query("""
			SELECT new roomescape.domain.ReservationWithRank(
				r,
				(SELECT COUNT(r2) * 1L
					FROM Reservation r2
				WHERE r2.theme = r.theme
					AND r2.date = r.date
					AND r2.time = r.time
				AND r2.id < r.id))
			FROM Reservation r
			WHERE r.name = :memberName
			""")
	List<ReservationWithRank> findReservationsWithRankByMemberName(@Param("memberName") String memberName);

	@Query("""
			SELECT COUNT(r) > 0
			FROM Reservation r
			WHERE r.member.id = :memberId
			AND r.date = :date
			AND r.time.id = :timeId
			AND r.theme.id = :themeId
			AND r.status = :status
			""")
	boolean existsByMemberAndDateAndTimeAndThemeAndStatus(@Param("memberId") Long memberId, @Param("date") String date,
			@Param("timeId") Long timeId, @Param("themeId") Long themeId, @Param("status") String status);

}
