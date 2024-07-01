package roomescape.repository;

import java.util.List;
import java.util.Optional;

import roomescape.domain.Member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberJpaRepository extends JpaRepository<Member, Long> {

	boolean existsByName(String name);

	Optional<Member> findByEmail(String email);

	List<Member> findByRole(String role);

}
