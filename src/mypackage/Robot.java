package mypackage;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

public class Robot extends Agent implements Runnable {

	public final static int TRAINING_STEPS = 5000, PLAY_STEPS = 500;
	private final static int SPAN = 20; // Steps between right and left
	private final static int STEP_SIZE = 1; // Step size

	private Leg rightLeg, leftLeg;
	private int offset;

	public Robot(double position, int offset, double epsilon, String name) {
		this.rightLeg = new Leg(false, Side.RIGHT, (int) position);
		this.leftLeg = new Leg(false, Side.LEFT, (int) position);
		this.epsilon = epsilon;
		this.offset = offset;
		this.name = name;
	}
	
	public Leg getRightLeg() {
		return rightLeg;
	}

	public Leg getLeftLeg() {
		return leftLeg;
	}

	public int getOffset() {
		return offset;
	}


	// Specific to the robot, a bit of noise to avoid many equivalent choices,
	// and more real
	public double getPosition() {
		return ((double) rightLeg.getPosition() + (double) leftLeg.getPosition()) / 2 + Math.random() * 0.05;
	}

	// Specific to the robot states
	public void move() throws InterruptedException {
		State current = new State(this);
		Double pos = (Double) getPosition();

		// We get all possible actions
		ArrayList<Movement> moves = getLegalMovements(current);

		Movement action = null;
		// If epsilon, random move, else take from QValues
		if (Math.random() < this.epsilon) {
			// Exploration
			action = moves.get((int) Math.ceil(Math.random() * moves.size()) - 1);
		} else {
			// Exploitation
			action = getActionFromQValues(map, current);
		}

		// Perform action
		doMovement(action);

		// Update Q-Value from experience
		State newState = new State(this);
		Double newPos = (Double) getPosition();

		double reward = newPos - pos;

		update(map, current, newState, action, reward);

		// Animation
		Thread.sleep(floatWaitMs);
	}

	// Perform a movement specific to the robot state
	public void doMovement(Movement movement) {
		switch (movement) {

		case backwardLeft:
			leftLeg.setPosition(leftLeg.getPosition() - STEP_SIZE);
			break;
		case backwardRight:
			rightLeg.setPosition(rightLeg.getPosition() - STEP_SIZE);
			break;
		case forwardLeft:
			leftLeg.setPosition(leftLeg.getPosition() + STEP_SIZE);
			break;
		case forwardRight:
			rightLeg.setPosition(rightLeg.getPosition() + STEP_SIZE);
			break;
		case downLeft:
			leftLeg.setUp(false);
			break;
		case downRight:
			rightLeg.setUp(false);
			break;
		case liftLeft:
			leftLeg.setUp(true);
			break;
		case liftRight:
			rightLeg.setUp(true);
			break;
		}

		MainQLearning.panel.repaint();
	}

	// Get movements specific to the robot states
	public ArrayList<Movement> getLegalMovements(State current) {
		ArrayList<Movement> legalMoves = new ArrayList<Movement>();

		if (current.getLegState() == State.GROUNDED) {
			// Both legs on the ground
			legalMoves.add(Movement.liftLeft);
			legalMoves.add(Movement.liftRight);
		} else if (current.getLegState() == State.LEFTUP) {
			 // Left lifted
			legalMoves.add(Movement.downLeft);

			if (-current.getRightToLeft() < SPAN)
				legalMoves.add(Movement.forwardLeft);
			if (current.getRightToLeft() < SPAN)
				legalMoves.add(Movement.backwardLeft);
		} else {
			// Right lifted
			legalMoves.add(Movement.downRight);

			if (current.getRightToLeft() < SPAN)
				legalMoves.add(Movement.forwardRight);
			if (-current.getRightToLeft() < SPAN)
				legalMoves.add(Movement.backwardRight);
		}

		return legalMoves;
	}

	// Trains the robot
	public void train(int trainingSteps) throws InterruptedException{
		startTraining();

		map = new HashMap<Integer, HashMap<Integer, Double>>();

		// Add start state
		State currentState = new State(this);
		map.put(currentState.hashCode(), new HashMap<Integer, Double>());

		for (int i = 0; i < trainingSteps; i++) {
			move();
		}

		stopTraining();
	}

	// Reset position and state to default
	private void resetPositionAndState() {
		rightLeg.setPosition(100);
		rightLeg.setUp(false);
		leftLeg.setPosition(100);
		leftLeg.setUp(false);
	}

	// Runs the whole sequence of training and testing
	public void run() {
		try {
			// Train the robot
			train(TRAINING_STEPS);

			// Reset position
			resetPositionAndState();
			MainQLearning.panel.repaint();

			// Wait
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Test the robots and see how far they go
			for (int i = 0; i < PLAY_STEPS; i++) {
				move();

				// Animation
				Thread.sleep(5);
			}

			System.out.println("        ==> Robot " + name + " made it to " + getPosition());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
