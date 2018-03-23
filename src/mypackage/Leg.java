package mypackage;
public class Leg {
	private boolean isUp;
	private Side side;
	private int position;

	public Leg(boolean isUp, Side side, int position) {
		this.isUp = isUp;
		this.side = side;
		this.position = position;
	}

	public boolean isUp() {
		return isUp;
	}

	public void setUp(boolean isUp) {
		this.isUp = isUp;
	}

	public Side getSide() {
		return side;
	}

	public void setSide(Side side) {
		this.side = side;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
