package roomescape.service;

import java.util.List;

import roomescape.domain.Theme;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.ThemeJpaRepository;
import roomescape.web.controller.dto.ThemeRequest;
import roomescape.web.controller.dto.ThemeResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThemeService {

	private final ThemeJpaRepository themeJpaRepository;

	ThemeService(ThemeJpaRepository themeJpaRepository) {
		this.themeJpaRepository = themeJpaRepository;
	}

	public List<ThemeResponse> getThemes() {
		return this.themeJpaRepository.findAll().stream().map(ThemeResponse::from).toList();
	}

	@Transactional
	public ThemeResponse create(ThemeRequest request) {
		var theme = Theme.builder()
			.name(request.name())
			.description(request.description())
			.thumbnail(request.thumbnail())
			.build();
		var isExistName = this.themeJpaRepository.existsByName(request.name());
		if (isExistName) {
			throw new RoomEscapeException(ErrorCode.DUPLICATE_THEME_NAME);
		}

		var savedTheme = this.themeJpaRepository.save(theme);
		return ThemeResponse.from(savedTheme);
	}

	@Transactional
	public void delete(long id) {
		var isExist = this.themeJpaRepository.existsById(id);
		if (!isExist) {
			throw new RoomEscapeException(ErrorCode.NOT_FOUND_THEME);
		}
		this.themeJpaRepository.deleteById(id);
	}

	@Transactional(readOnly = true)
	public Theme getThemeById(long id) {
		return this.themeJpaRepository.findById(id)
			.orElseThrow(() -> new RoomEscapeException(ErrorCode.NOT_FOUND_THEME));
	}

}
