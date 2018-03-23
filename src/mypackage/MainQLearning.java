package mypackage;
import java.util.ArrayList;

import javax.swing.JFrame;

/*
 * The purpose of this program is to show the behavior of the Epsilon-Greedy algorithm
 * in the context of a walking robot learning with the reinforcement learning technique 
 * known as Q-Learning.
 * 
 * The post associated can be found here: https://hackernoon.com/using-q-learning-to-teach-a-robot-how-to-walk-a-i-odyssey-part-3-5285237cc3b1
 * 
 * This code is not production code, and could certainly be improved, but I think it is still clear.
 * 
 * */

public class MainQLearning {
	
	public static Drawer panel;
	private static ArrayList<Robot> robots;

	private final static int WIDTH = 500, HEIGHT = 500;

	public static void main(String[] args) {
		/* Drawing part */
		JFrame frame = new JFrame("Henry the Robot by Julien Despois");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(WIDTH, HEIGHT);
		panel = new Drawer();

		frame.setContentPane(panel);
		frame.setVisible(true);
		/* End drawing part */
		
		// Create robots
		robots = new ArrayList<Robot>();
		robots.add(new Robot(HEIGHT/4, WIDTH/3, 0.6, "Exploration"));
		robots.add(new Robot(HEIGHT/4, 2*WIDTH/3, 0.01, "Exploitation"));
		
		// Add robots to panel
		panel.setRobots(robots);

		// Start robots
		for (Robot hex : robots){
			Thread t = new Thread(hex);
			t.start();
		}
	}
}
