package roomescape.domain;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Reservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String date;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "time_id")
	private ReservationTime time;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "theme_id")
	private Theme theme;

	public static Builder builder() {
		return new Builder();
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public String getDate() {
		return this.date;
	}

	public ReservationTime getTime() {
		return this.time;
	}

	public Theme getTheme() {
		return this.theme;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Reservation that = (Reservation) o;
		return Objects.equals(this.id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	@Override
	public String toString() {
		// @formatter:off
		return "Reservation{" +
				"id=" + this.id +
				", name='" + this.name + '\'' +
				", date='" + this.date + '\'' +
				", time=" + this.time +
				", theme=" + this.theme +
				'}';
		// @formatter:on
	}

	public static final class Builder {

		private final Reservation reservation;

		public Builder() {
			this.reservation = new Reservation();
		}

		public Builder id(long id) {
			this.reservation.id = id;
			return this;
		}

		public Builder name(String name) {
			this.reservation.name = name;
			return this;
		}

		public Builder date(String date) {
			this.reservation.date = date;
			return this;
		}

		public Builder time(ReservationTime time) {
			this.reservation.time = time;
			return this;
		}

		public Builder theme(Theme theme) {
			this.reservation.theme = theme;
			return this;
		}

		public Reservation build() {
			return this.reservation;
		}

	}

}
