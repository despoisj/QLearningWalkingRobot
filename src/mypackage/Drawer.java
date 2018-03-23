package mypackage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Drawer extends JPanel {
	private final static File backgroundFile = new File("img/ground.png");

	private final static File coreFile = new File("img/core.png");
	private final static File footFile = new File("img/foot.png");
	private final static File footUpFile = new File("img/footUp.png");

	private final static File core2File = new File("img/core2.png");
	private final static File foot2File = new File("img/foot2.png");
	private final static File footUp2File = new File("img/footUp2.png");

	private static Image backgroundImage;

	private static Image coreImage;
	private static Image footImage;
	private static Image footUpImage;

	private static Image core2Image;
	private static Image foot2Image;
	private static Image footUp2Image;

	private ArrayList<Robot> robots;

	// Constructor
	public Drawer() {
		try {
			// Load images
			backgroundImage = ImageIO.read(backgroundFile);

			coreImage = ImageIO.read(coreFile);
			footImage = ImageIO.read(footFile);
			footUpImage = ImageIO.read(footUpFile);

			core2Image = ImageIO.read(core2File);
			foot2Image = ImageIO.read(foot2File);
			footUp2Image = ImageIO.read(footUp2File);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Setter for robots
	public void setRobots(ArrayList<Robot> robots) {
		this.robots = robots;
	}

	// Draws the frame
	public void paintComponent(Graphics g) {
		try {
			g.drawImage(backgroundImage, 0, 0, null);

			for (Robot robot : this.robots) {
				drawRobot(robot, g);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Draws a robot
	private void drawRobot(Robot robot, Graphics g) {
		Image coreImg = null;
		Image footImg = null;
		Image footUpImg = null;
		Color linkColor = null;

		if (robot.name == "Exploration") {
			// Blue robot
			coreImg = coreImage;
			footImg = footImage;
			footUpImg = footUpImage;
			linkColor = new Color(100, 100, 240);
		} else {
			// Yellow robot
			coreImg = core2Image;
			footImg = foot2Image;
			footUpImg = footUp2Image;
			linkColor = new Color(230, 195, 94);
		}

		Leg rightLeg = robot.getRightLeg();
		Leg leftLeg = robot.getLeftLeg();
		Image leftLegImg = leftLeg.isUp() ? footUpImg : footImg;
		Image rightLegImg = rightLeg.isUp() ? footUpImg : footImg;

		// Right leg
		int rightLegX = robot.getOffset() - 40;
		int rightLegY = rightLeg.getPosition() % this.getHeight();
		g.drawImage(rightLegImg, rightLegX - 10, rightLegY - 10, null);

		// Left leg
		int leftLegX = robot.getOffset() + 40;
		int leftLegY = leftLeg.getPosition() % this.getHeight();
		g.drawImage(leftLegImg, leftLegX - 10, leftLegY - 10, null);

		// Links and Core computations
		int centerX = robot.getOffset();
		int centerY = (int) ((leftLeg.getPosition() + rightLeg.getPosition()) / 2.0) % this.getHeight();

		// Links
		g.setColor(linkColor);
		g.drawLine(centerX, centerY, leftLegX, leftLegY);
		g.drawLine(centerX, centerY, rightLegX, rightLegY);

		// Core
		g.drawImage(coreImg, centerX - 20, centerY - 30, null);

		// Name
		g.setColor(Color.WHITE);
		g.setFont(new Font("Calibri", 1, 15));
		g.drawString(robot.name, robot.getOffset() - 40,
				(int) (0.5 * leftLeg.getPosition() + 0.5 * rightLeg.getPosition() - 25) % this.getHeight() - 20);
	}
}
