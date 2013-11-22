package logic.buildings;

import java.math.BigInteger;

public class Mine extends PowerConsumingBuilding {

	protected BigInteger metalsPerTick;
	
	public Mine(String name)
	{
		super(name,"mine",10,10,new BigInteger("15"));
		this.registerResource("mined_metals");
		this.metalsPerTick = new BigInteger("1");
	}
	
	public void produce(int delta)
	{
		if(this.powered)
		{
			BigInteger product = this.metalsPerTick.multiply(BigInteger.valueOf(delta));
			this.pushResource("mined_metals", product);
		}
	}
}
