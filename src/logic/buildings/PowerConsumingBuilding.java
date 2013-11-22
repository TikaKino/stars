package logic.buildings;

import java.math.BigInteger;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class PowerConsumingBuilding extends Building {

	protected boolean drainingPower = true;
	protected BigInteger powerPerTick;
	protected boolean powered;
	
	public PowerConsumingBuilding(String name, String type, int x, int y, BigInteger powerPerTick)
	{
		super(name,type,x,y);
		this.powerPerTick = powerPerTick;
		this.powered = true;
	}
	
	public void consume(int delta)
	{
		super.consume(delta);
		
		BigInteger toConsume = this.powerPerTick.multiply(BigInteger.valueOf(delta));
		BigInteger consumed = BigInteger.ZERO;
		
		consumed = this.network.consumeFromNetwork(toConsume, "power_pool", this);
		toConsume = toConsume.subtract(consumed);
		if(toConsume.compareTo(BigInteger.ZERO) > 0)
		{
			consumed = this.network.consumeFromNetwork(toConsume, "power_store", this);
			toConsume = toConsume.subtract(consumed);
		}
		
		if(toConsume.compareTo(BigInteger.ZERO) > 0)
			this.powered = false;
		else
			this.powered = true;
	}
	
	public void render(GameContainer gc, Graphics g)
	{
		super.render(gc,g);
		int x = this.posX*10;
		int y = this.posY*10;
		
		if(!this.powered)
			g.drawString("!Pow", x+5, y+25);
	}
}
