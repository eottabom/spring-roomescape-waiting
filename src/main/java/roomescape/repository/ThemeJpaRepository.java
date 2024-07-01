package roomescape.repository;

import roomescape.domain.Theme;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ThemeJpaRepository extends JpaRepository<Theme, Long> {

	boolean existsById(long id);

	boolean existsByName(String name);

}
