package logic.buildings;

import java.math.BigInteger;

public class LifeSupport extends PowerConsumingBuilding {

	protected int supportCapacity;
	
	public LifeSupport(String name)
	{
		super(name,"life_support",5,5,new BigInteger("10"));
		
		this.registerResource("life_support");
		this.supportCapacity = 50;
		this.powered = true;
	}
	
	public void produce(int delta)
	{
		super.produce(delta);
		if(this.powered)
			this.setResource("life_support", BigInteger.valueOf(this.supportCapacity));
	}
	
	public void consume(int delta)
	{
		super.consume(delta);
	}
}
