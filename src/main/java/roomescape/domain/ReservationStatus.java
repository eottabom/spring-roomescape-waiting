package roomescape.domain;

import java.util.Arrays;

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
		return Arrays.stream(values())
			.filter((status) -> status.name().equals(savedValue))
			.findFirst()
			.map(ReservationStatus::getStatus)
			.orElseThrow(() -> new IllegalArgumentException("Unknown value: " + savedValue));
	}

}
