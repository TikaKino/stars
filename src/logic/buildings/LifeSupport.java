package logic.buildings;

import java.math.BigInteger;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class LifeSupport extends PowerConsumingBuilding {

	protected int supportCapacity;
	
	public LifeSupport(String name)
	{
		super(name,"life_support",5,5,new BigInteger("10"));
		
		this.registerResource("life_support");
		this.supportCapacity = 500;
		this.powered = true;
	}
	
	public void produce(int delta)
	{
		super.produce(delta);
		this.setResource("life_support", BigInteger.valueOf(this.supportCapacity));
	}
	
	public void consume(int delta)
	{
		super.consume(delta);
	}
	
	public void render(GameContainer gc, Graphics g)
	{
		super.render(gc,g);
		int x = this.posX*10;
		int y = this.posY*10;
		//mark if unpowered
		if(!this.powered)
			g.drawString("!Pow", x+5, y+25);
	}
}
