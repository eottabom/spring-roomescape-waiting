package roomescape.repository;

import java.util.List;

import roomescape.domain.ReservationTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationTimeJpaRepository extends JpaRepository<ReservationTime, Long> {

	boolean existsById(long id);

	@Query("""
					SELECT rt.id
					FROM ReservationTime rt
					INNER JOIN Reservation r ON rt.id = r.time.id
					WHERE r.date = :date AND r.theme.id = :themeId
			""")
	List<Long> findReservedTimeIds(@Param("date") String date, @Param("themeId") long themeId);

}
