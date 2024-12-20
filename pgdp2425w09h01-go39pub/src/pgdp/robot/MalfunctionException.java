package pgdp.robot;

@SuppressWarnings("serial")
public class MalfunctionException extends Exception {
	private Sensor<?> sensor;

	public MalfunctionException(Sensor<?> sensor) {
		this.sensor = sensor;
	}

	public Sensor<?> getSensor() {
		return sensor;
	}

	@Override
	public String toString() {
		return sensor + " malfunctioned and needs to be replaced";
	}
}
