package logic.buildings;

import java.math.BigInteger;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class LivingQuarters extends PowerConsumingBuilding {
	
	protected int population;
	protected int populationMax;
	protected int populationProgress;
	protected int supportPerPop;
	protected int progressPerPop;
	
	protected boolean supported;
	
	protected BigInteger supportThisUpdate;
	
	public LivingQuarters(String name)
	{
		super(name,"living_quarters",10,10,new BigInteger("5"));
		
		this.population = 1;
		this.populationMax = 100;
		this.populationProgress = 0;
		this.supportPerPop = 1;
		this.progressPerPop = 1000;

		this.supported = true;
	}
	
	public void consume(int delta)
	{
		super.consume(delta);
		
		//Attempt to consume life support from the network.
		BigInteger requiredSupport = BigInteger.valueOf(this.population).multiply(BigInteger.valueOf(this.supportPerPop));
		BigInteger consumedSupport = this.network.consumeFromNetwork(requiredSupport, "life_support", this);
		requiredSupport = requiredSupport.subtract(consumedSupport);
		
		if(requiredSupport.compareTo(BigInteger.ZERO) > 0)
		{
			if(this.supported = true)
				this.populationProgress = 0;
			this.supported = false;
		}
		else
		{
			if(this.supported = false)
				this.populationProgress = 0;
			this.supported = true;
		}
		
		if(this.supported)
		{
			if(this.powered)
			{
				this.populationProgress += delta;
				
				if(this.populationProgress > this.progressPerPop)
				{
					this.populationProgress -= this.progressPerPop;
					this.population++;
				}
			}
		}
		else
		{
			//pop trending down, increase population progress
			this.populationProgress += delta;
			if(this.populationProgress > this.progressPerPop)
			{
				this.populationProgress -= this.progressPerPop;
				this.population--;
			}
		}
	}
	
	public void render(GameContainer gc, Graphics g)
	{
		super.render(gc,g);
		int x = this.posX*10;
		int y = this.posY*10;
		
		//population 
		g.drawString("P:"+this.population, x+5, y+40);
		
		//power and support flags
		if(!this.supported)
			g.drawString("!Supp", x+5, y+55);
	}
}
