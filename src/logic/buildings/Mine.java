package logic.buildings;

import java.math.BigInteger;

public class Mine extends PowerConsumingBuilding {

	
	
	public Mine(String name)
	{
		super(name,"mine",10,10,new BigInteger("15"));
	}
	
	public void produce(int delta)
	{
		
	}
}
