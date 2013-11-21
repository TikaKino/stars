package logic.buildings;

import java.math.BigInteger;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class PowerPlant extends Building {	
	
	protected BigInteger powerpertick;
	protected BigInteger maxbattery;
	
	public PowerPlant(String name)
	{
		super(name,"power_plant",10,10);
		this.registerResource("power_pool");
		this.registerResource("power_store");
		this.powerpertick = new BigInteger("30");
		this.maxbattery = new BigInteger("1000000");
	}
	
	public void produce(int delta)
	{
		BigInteger produced = this.powerpertick.multiply(BigInteger.valueOf(delta));
		this.pushResource("power_pool", produced);
	}
	
	public void consume(int delta)
	{
		
	}
	
	public void store()
	{
		BigInteger stored = this.getResource("power_pool");
		this.pushResourceWithMax("power_store", stored,this.maxbattery);
		this.setResource("power_pool", BigInteger.ZERO);
	}
	
	public void render(GameContainer gc, Graphics g)
	{
		super.render(gc,g);
		int x = this.posX*10;
		int y = this.posY*10;
		//stored power
		BigInteger store = this.getResource("power_store");
		store = store.divide(new BigInteger("1000"));
		g.drawString("Battery: ", x+5, y+25);
		g.drawString(store.toString(), x+5, y+40);
	}
}
