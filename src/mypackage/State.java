package mypackage;
public class State {
	public final static int GROUNDED = 1, RIGHTUP = 2, LEFTUP = 3;

	private int legState;
	private int rightToLeft;
	
	public int getLegState() {
		return legState;
	}

	public int getRightToLeft() {
		return rightToLeft;
	}

	public State(Robot hex) {
		if (!hex.getRightLeg().isUp() && !hex.getLeftLeg().isUp()) {
			legState = GROUNDED;
		} else if (hex.getRightLeg().isUp()) {
			legState = RIGHTUP;
		} else {
			legState = LEFTUP;
		}
		rightToLeft = hex.getRightLeg().getPosition() - hex.getLeftLeg().getPosition();
	}

	@Override
	public int hashCode() {
		return 1000 * legState + rightToLeft;
	}

	@Override
	public String toString() {
		return legState + "," + rightToLeft;
	}
}
