package roomescape.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomEscapeException;

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	private ReservationStatus status;

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

	public Member getMember() {
		return this.member;
	}

	public ReservationStatus getStatus() {
		return this.status;
	}

	private static void checkReservationAvailability(String date, String time) {
		LocalDate reservationDate = LocalDate.parse(date);
		LocalTime reservationTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));

		if (reservationDate.isBefore(LocalDate.now())
				|| (reservationDate.isEqual(LocalDate.now()) && reservationTime.isBefore(LocalTime.now()))) {
			throw new RoomEscapeException(ErrorCode.PAST_RESERVATION);
		}
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
				", member=" + this.member +
				", status='" + this.status + '\'' +
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

		public Builder member(Member member) {
			this.reservation.member = member;
			return this;
		}

		public Builder status(ReservationStatus status) {
			this.reservation.status = status;
			return this;
		}

		public Reservation build() {
			checkReservationAvailability(this.reservation.date, this.reservation.time.getStartAt());
			return this.reservation;
		}

	}

}
