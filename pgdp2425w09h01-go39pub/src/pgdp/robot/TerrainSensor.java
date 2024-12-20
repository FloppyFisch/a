package pgdp.robot;

public class TerrainSensor extends Sensor<Character>{

    public TerrainSensor(String name, double reliability){
        super(name,reliability);
    }

    @Override
    public Character getData() throws MalfunctionException {
        if(owner.failureSimulator.getNextRandom() > reliability){
            throw new MalfunctionException(this);
        }
        return owner.getWorld().getTerrain(owner.getPosition());
    }

    @Override
    public Sensor<Character> createNewSensor() {
        TerrainSensor temp = new TerrainSensor(name,reliability);
        temp.setProcessor(processor);
        return temp;
    }

    public String toString(){
        return "TerrainSensor " + name;
    }
}
