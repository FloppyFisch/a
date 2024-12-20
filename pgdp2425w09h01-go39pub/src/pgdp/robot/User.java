package pgdp.robot;

// import static pgdp.robot.RobotFactory.*;

import static pgdp.robot.RobotFactory.makePanicPenguin;

public class User {

	private static final String PLAY_GROUND_1 = """
			#######
			#  0  #
			#Don't#
			#PANIC#
			#     #
			#######
			""";

	// Execute this main to launch the game
	public static void main(String[] args) {
		createExample();
	}

	public static void createExample() {
		World world = new World(PLAY_GROUND_1);

		// If you have created these robots you can just uncomment these lines
		makePanicPenguin("Panic!", Math.toRadians(5), new Robot.SimpleFailureSimulator(69)).spawnInWorld(world, '0');
//		makeLineFollower("Line Follower", 0, new SimpleFailureSimulator(42)).spawnInWorld(world, '1');
//		makeMazeRunner("Maze Runner", 0, new SimpleFailureSimulator(1337)).spawnInWorld(world, '2');

		world.run();
	}
}
