package roomescape.domain;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ReservationTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "start_at")
	private String startAt;

	public static Builder builder() {
		return new Builder();
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStartAt() {
		return this.startAt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ReservationTime that = (ReservationTime) o;
		return Objects.equals(this.id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	@Override
	public String toString() {
		// @formatter:off
		return "ReservationTime{" +
				"id=" + this.id +
				", startAt='" + this.startAt + '\'' +
				'}';
		// @formatter:on
	}

	public static final class Builder {

		private final ReservationTime reservationTime;

		public Builder() {
			this.reservationTime = new ReservationTime();
		}

		public Builder id(long id) {
			this.reservationTime.id = id;
			return this;
		}

		public Builder startAt(String startAt) {
			this.reservationTime.startAt = startAt;
			return this;
		}

		public ReservationTime build() {
			return this.reservationTime;
		}

	}

}
