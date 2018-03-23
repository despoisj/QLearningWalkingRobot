package mypackage;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Agent {
	// State -> Q-Values
	protected HashMap<Integer, HashMap<Integer, Double>> map;
	// Probability of the epsilon-greedy to act randomly
	protected double epsilon;
	// Decay value of future rewards
	protected double gamma = 0.8;
	// Learning rate
	protected double alpha = 0.2;
	protected String name;
	protected int floatWaitMs = 0;

	// Methods
	public Movement getActionFromQValues(HashMap<Integer, HashMap<Integer, Double>> QValuesMap, State state) {
		ArrayList<Movement> moves = getLegalMovements(state);

		// State is unknown
		if (!QValuesMap.containsKey(state.hashCode())) { 
			// Do random move
			return moves.get((int) Math.ceil(Math.random() * moves.size()) - 1);
		}
		
		//Otherwise, perform one of the best actions
		ArrayList<Movement> bestMoves = new ArrayList<Movement>();
		double maxValue = -Double.MAX_VALUE;
		
		for (Movement action : moves) {
			double expectedQValue = getQValue(QValuesMap, state, action);
			if (expectedQValue > maxValue) {
				// New best, remove others
				bestMoves.clear();
				bestMoves.add(action);
				
				// Update max expected value
				maxValue = expectedQValue;
			} else if (expectedQValue == maxValue) {
				// Another best ex-aequo, add it
				bestMoves.add(action);
			}
		}

		// Return one of the ex-aequo best moves if found
		return bestMoves.get((int) Math.ceil(Math.random() * bestMoves.size()) - 1);
	}

	public double getQValue(HashMap<Integer, HashMap<Integer, Double>> QValuesMap, State state, Movement action) {
		// We know the state
		if (map.containsKey(state.hashCode())) {
			HashMap<Integer, Double> actionToValue = map.get(state.hashCode());

			// We know the action
			if (actionToValue.containsKey(action.hashCode())) {
				return actionToValue.get(action.hashCode());
			}
		}
		// We don't know the Q-Value of this State-Action pair
		return 0.0;
	}
		
	public void update(HashMap<Integer, HashMap<Integer, Double>> QValuesMap, State oldState, State newState, Movement action, double reward) {
		// New state (never encountered)
		if (!QValuesMap.containsKey(newState.hashCode())) {
			QValuesMap.put(newState.hashCode(), new HashMap<Integer, Double>());
		}

		// What we expected
		double oldQValue = getQValue(QValuesMap ,oldState, action);

		// Compute maximal expected future value
		ArrayList<Movement> moves = getLegalMovements(newState);
		double futureMax = -Double.MAX_VALUE;

		// Get max of qValues of the new state
		for (Movement newAction : moves) {
			if (getQValue(QValuesMap, newState, newAction) > futureMax) {
				futureMax = getQValue(QValuesMap, newState, newAction);
			}
		}

		// Bellman's equation
		double newQValue = reward + gamma * futureMax;

		// Calculations of the new value with learning rate alpha
		double updatedValue = (1 - alpha) * oldQValue + alpha * newQValue;

		HashMap<Integer, Double> temp = QValuesMap.get(oldState.hashCode());

		// If oldstate is not known (first state?)
		if (temp == null)
			temp = new HashMap<Integer, Double>();
			
		temp.put(action.hashCode(), updatedValue);
	}

	// Starts the training phase
	public void startTraining() {
		System.out.println("    Start of training phase for " + name);
		alpha = 0.2;
		floatWaitMs = 0;
	}

	// Stops the training phase
	public void stopTraining() {
		System.out.println("    End of training phase for " + name);
		// Stop trying random things
		epsilon = 0;
		// Stop learning
		alpha = 0;
		floatWaitMs = 10;
	}

	
	// Abstract
	public abstract void doMovement(Movement movement);
	public abstract void move() throws InterruptedException;	
	public abstract ArrayList<Movement> getLegalMovements(State current);
}
