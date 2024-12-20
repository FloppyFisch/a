package pgdp.robot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class RobotFactory {

    public static Robot makePanicPenguin(String name, double rotation, Robot.FailureSimulator failureSimulator){
        Robot panic = new Robot(name,0,0.8,failureSimulator);

        Memory<Character> memory = new Memory<>("Terrain",' ');
        panic.createMemory(memory);

        Consumer<Character> processor = new Consumer<Character>() {
            @Override
            public void accept(Character c) {
                memory.set(c);
            }
        };

        TerrainSensor sensor = new TerrainSensor("TerrainSensor",0.97);
        panic.attachSensor(sensor);
        sensor.setProcessor(processor);



        Function<Robot,List<Command>> program = new Function<Robot, List<Command>>() {
            private Command sayWorld = new Command() {
                @Override
                public boolean execute(Robot robot) {
                    char terrain = memory.get();
                    robot.getWorld().say(robot, String.valueOf(terrain));
                    return true;
                }
            };

            private Command rotate = new Command() {
                @Override
                public boolean execute(Robot robot) {
                    robot.turnBy(rotation);
                    return true;
                }
            };

            private Command go = new Command() {
                @Override
                public boolean execute(Robot robot) {
                    robot.go(0.1);
                    return true;
                }
            };


            private List<Command> commandList = List.of(new Command[]{sayWorld, rotate, go});

            @Override
            public List<Command> apply(Robot robot) {
                for(Command command : commandList){
                    command.execute(panic);
                }
                return List.of();
            }
        };

        panic.setProgram(program);
        return panic;
    }
}
