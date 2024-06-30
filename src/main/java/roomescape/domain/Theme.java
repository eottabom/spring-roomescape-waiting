package roomescape.domain;

import java.util.Objects;

public class Theme {

	private Long id;

	private String name;

	private String description;

	private String thumbnail;

	public static Builder builder() {
		return new Builder();
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public String getThumbnail() {
		return this.thumbnail;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Theme that = (Theme) o;
		return Objects.equals(this.id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	@Override
	public String toString() {
		// @formatter:off
		return "Theme{" +
				"id=" + this.id +
				", name='" + this.name + '\'' +
				", description='" + this.description + '\'' +
				", thumbnail='" + this.thumbnail + '\'' +
				'}';
		// @formatter:on
	}

	public static final class Builder {

		private final Theme theme;

		public Builder() {
			this.theme = new Theme();
		}

		public Builder id(long id) {
			this.theme.id = id;
			return this;
		}

		public Builder name(String name) {
			this.theme.name = name;
			return this;
		}

		public Builder description(String description) {
			this.theme.description = description;
			return this;
		}

		public Builder thumbnail(String thumbnail) {
			this.theme.thumbnail = thumbnail;
			return this;
		}

		public Theme build() {
			return this.theme;
		}

	}

}
