package roomescape.repository;

import java.util.List;

import roomescape.domain.Reservation;

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

}
