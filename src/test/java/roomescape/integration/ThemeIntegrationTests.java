package roomescape.integration;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ThemeIntegrationTests extends AbstractIntegrationTests {

	private final TestRestTemplate restTemplate = new TestRestTemplate();

	@LocalServerPort
	private int port;

	@Test
	void themeControllerEndpoints() {
		// create theme
		var createdTheme = createTheme();

		// get themes
		// when
		var themesResponse = this.restTemplate.getForEntity("http://localhost:" + this.port + "/themes", List.class);

		// then
		assertThat(themesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		var themes = themesResponse.getBody();
		assertThat(themes).isNotNull();
		assertThat(themes.size()).isEqualTo(1);

		// delete theme
		// given
		long themeId = createdTheme.id();

		// when
		deleteTheme(themeId);

		// check theme delete
		// when
		themesResponse = this.restTemplate.getForEntity("http://localhost:" + this.port + "/themes", List.class);

		// then
		assertThat(themesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		themes = themesResponse.getBody();
		assertThat(themes).isNotNull();
		assertThat(themes.size()).isEqualTo(0);
	}

}
