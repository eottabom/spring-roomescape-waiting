package roomescape.domain;

public enum ReservationStatus {

	RESERVATION("예약"), WAITING("예약 대기");

	private final String status;

	ReservationStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return this.status;
	}

	public static String fromStatus(String savedValue) {
		for (ReservationStatus status : values()) {
			if (status.name().equals(savedValue)) {
				return status.getStatus();
			}
		}
		throw new IllegalArgumentException("Unknown value: " + savedValue);
	}

}
