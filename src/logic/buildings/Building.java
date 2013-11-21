package logic.buildings;

import java.math.BigInteger;
import java.util.HashMap;

import logic.Network;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class Building {

	protected String buildingName;
	
	protected int posX = 0;
	protected int posY = 0;
	
	protected int width = 10;
	protected int height = 10;
	
	protected HashMap<String,BigInteger> resourcePools;
	
	protected Network network;
	
	public Building(String name, int width, int height)
	{
		this.resourcePools = new HashMap<String,BigInteger>(20);
		this.network = null;
		
		this.buildingName = name;
		this.setSize(width,height);
	}
	
	protected void setSize(int width, int height)
	{
		if(width < 1)
			width = 1;
		if(height < 1)
			height = 1;
		
		this.width = width;
		this.height = height;
	}
	
	public int getWidth()
	{
		return this.width;
	}
	
	public int getHeight()
	{
		return this.height;
	}
	
	public void setPos(int x, int y)
	{
		this.posX = x;
		this.posY = y;
	}
	
	public int getPosX()
	{
		return this.posX;
	}
	
	public int getPosY()
	{
		return this.posY;
	}
	
	public void render(GameContainer gc, Graphics g)
	{
		int x = this.posX*10;
		int y = this.posY*10;
		
		int w = this.width*10;
		int h = this.height*10;
		
		//2-wide rectangle
		g.drawRect(x+1, y+1, w-2, h-2);
		g.drawRect(x+2, y+2, w-4, h-4);
		
		//name
		g.drawString(this.buildingName, x+5, y+5);
	}
	
	public void produce(int delta)
	{
		
	}
	
	public void consume(int delta)
	{
		
	}
	
	public void store()
	{
		
	}
	
	public void registerResource(String resName)
	{
		this.resourcePools.put(resName, BigInteger.ZERO);
	}
	
	public boolean providesResource(String resName)
	{
		return this.resourcePools.containsKey(resName);
	}
	
	public BigInteger getResource(String resName)
	{
		if(this.providesResource(resName))
			return this.resourcePools.get(resName);
		return new BigInteger("-1");
	}
	
	protected void setResource(String resName, BigInteger amount)
	{
		if(this.providesResource(resName))
			this.resourcePools.put(resName, amount);
	}
	
	public BigInteger pushResource(String resName,BigInteger amount)
	{
		if(!this.providesResource(resName))
			return BigInteger.ZERO;
		if(amount.compareTo(BigInteger.ZERO) < 0)
			return BigInteger.ZERO;
		
		BigInteger current = this.resourcePools.get(resName);
		current = amount.add(current);
		this.resourcePools.put(resName, current);
		return amount;
	}
	
	public BigInteger pushResourceWithMax(String resName,BigInteger amount,BigInteger maxvalue)
	{
		if(!this.providesResource(resName))
			return BigInteger.ZERO;
		if(amount.compareTo(BigInteger.ZERO) < 0)
			return BigInteger.ZERO;
		
		BigInteger current = this.resourcePools.get(resName);
		BigInteger tmp = amount.add(current);
		if(tmp.compareTo(maxvalue) > 0)
		{
			BigInteger overflow = tmp.subtract(maxvalue);
			if(overflow.compareTo(amount) > 0)
				return BigInteger.ZERO;
			current = maxvalue;
			amount = amount.subtract(overflow);
		}
		else
		{
			current = tmp;
		}
		
		this.resourcePools.put(resName, current);
		return amount;
	}
	
	public BigInteger pullResource(String resName, BigInteger amount)
	{
		if(!this.providesResource(resName))
			return BigInteger.ZERO;
		if(amount.compareTo(BigInteger.ZERO) < 0)
			return BigInteger.ZERO;
		
		BigInteger current = this.resourcePools.get(resName);
		
		if(current.compareTo(BigInteger.ZERO) <= 0)
			return BigInteger.ZERO;
		else if(current.compareTo(amount) < 0)
		{
			BigInteger tmp = amount.subtract(current);
			amount = amount.subtract(tmp);
			current = BigInteger.ZERO;
		}
		else
			current = current.subtract(amount);
		
		this.resourcePools.put(resName, current);
		return amount;
	}
	
	public String toString()
	{
		return this.buildingName;
	}
	
	public void setNetwork(Network n)
	{
		this.network = n;
	}
}
