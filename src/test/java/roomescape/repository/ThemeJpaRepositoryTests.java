package roomescape.repository;

import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import roomescape.domain.Theme;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ThemeJpaRepositoryTests {

	@Autowired
	private ThemeJpaRepository themeJpaRepository;

	@Test
	void findAll() {
		// given
		createTheme();

		// when
		var foundThemes = this.themeJpaRepository.findAll();

		// then
		assertThat(foundThemes).isNotEmpty().allSatisfy((theme) -> {
			assertThat(theme).isNotNull();
			assertThat(theme.getId()).isEqualTo(1L);
			assertThat(theme.getName()).isEqualTo("테마1");
			assertThat(theme.getDescription()).isEqualTo("첫번째테마");
			assertThat(theme.getThumbnail()).isEqualTo("썸네일이미지");
		});
	}

	@Test
	void delete() {
		// given
		createTheme();

		// when
		this.themeJpaRepository.deleteById(1L);

		// delete verification
		// when
		Optional<Theme> foundTheme = this.themeJpaRepository.findById(1L);

		// then
		SoftAssertions.assertSoftly((softly) -> {
			softly.assertThat(foundTheme).isNotNull();
			softly.assertThat(foundTheme.isPresent()).isFalse();
		});
	}

	@Test
	void findById() {
		// given
		createTheme();

		// when
		Optional<Theme> foundTheme = this.themeJpaRepository.findById(1L);

		// then
		SoftAssertions.assertSoftly((softly) -> {
			softly.assertThat(foundTheme).isNotNull();
			softly.assertThat(foundTheme.isPresent()).isTrue();
			foundTheme.ifPresent((theme) -> {
				softly.assertThat(theme.getName()).isEqualTo("테마1");
				softly.assertThat(theme.getDescription()).isEqualTo("첫번째테마");
				softly.assertThat(theme.getThumbnail()).isEqualTo("썸네일이미지");
			});
		});
	}

	@Test
	void existsById() {
		// given
		createTheme();

		// when
		var existsById = this.themeJpaRepository.existsById(1L);

		// then
		assertThat(existsById).isTrue();
	}

	@Test
	void existsByName() {
		// given
		createTheme();

		// when
		var existsByName = this.themeJpaRepository.existsByName("테마1");

		// then
		assertThat(existsByName).isTrue();
	}

	private void createTheme() {
		Theme theme = Theme.builder().id(1L).name("테마1").description("첫번째테마").thumbnail("썸네일이미지").build();
		this.themeJpaRepository.save(theme);
	}

}
