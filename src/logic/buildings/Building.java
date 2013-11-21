package logic.buildings;

import java.math.BigInteger;
import java.util.HashMap;

import logic.Network;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * The base class of all buildings in the game. Provides resource pools, 
 * naming and basic rendering of a building.
 * @author Mikael Suominen
 */
public abstract class Building {

	protected String buildingName;
	protected String buildingType;
	
	protected int posX = 0;
	protected int posY = 0;
	
	protected int width = 10;
	protected int height = 10;
	
	protected HashMap<String,BigInteger> resourcePools;
	
	protected Network network;
	
	public Building(String name, String type, int width, int height)
	{
		this.resourcePools = new HashMap<String,BigInteger>(20);
		this.network = null;
		
		this.buildingName = name;
		this.buildingType = type;
		this.setSize(width,height);
	}
	
	/**
	 * Sets the size of the building in game units
	 * @param width
	 * @param height
	 */
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
	
	/**
	 * Sets the position of the building in game units
	 * @param x
	 * @param y
	 */
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
	
	/**
	 * Does a basic render of the building, assuming 1 unit = 10 pixels
	 * @param gc
	 * @param g
	 */
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
	
	/**
	 * Tells the building to produce resources
	 * @param delta	Ticks (milliseconds) since last production
	 */
	public void produce(int delta)
	{
		
	}
	
	/**
	 * Tells the building to consume resources
	 * @param delta	Ticks (milliseconds) since last consumption
	 */
	public void consume(int delta)
	{
		
	}
	
	/**
	 * Tells the building to store any excess resources it can store. Mostly
	 * used by power plants to shove excess unused power into batteries.
	 */
	public void store()
	{
		
	}
	
	/**
	 * Creates a new resource pool in the building with the given name. This must
	 * be done before that pool can be set, pushed or pulled.
	 * @param resName	The name of the new resource pool - must be unique to this building.
	 */
	public void registerResource(String resName)
	{
		this.resourcePools.put(resName, BigInteger.ZERO);
	}
	
	/**
	 * Checks whether this building provides the named resource
	 * @param resName	The name of the resource pool to check for
	 * @return			True if this pool exists, false otherwise.
	 */
	public boolean providesResource(String resName)
	{
		return this.resourcePools.containsKey(resName);
	}
	
	/**
	 * Gets the current level of a given resource pool.
	 * @param resName	The name of the resource pool to check 
	 * @return			the current value of the pool, or -1 if it doesn't exist
	 */
	public BigInteger getResource(String resName)
	{
		if(this.providesResource(resName))
			return this.resourcePools.get(resName);
		return new BigInteger("-1");
	}
	
	/**
	 * Sets the level of a given resource pool
	 * @param resName	The name of the resource pool to set
	 * @param amount	The new value to set it to
	 */
	protected void setResource(String resName, BigInteger amount)
	{
		if(this.providesResource(resName))
			this.resourcePools.put(resName, amount);
	}
	
	/**
	 * Attempts to add resource to a given resource pool. Always succeeds by default
	 * if the pool exists and the amount is positive. Returns 0 if pushing is for some
	 * reason impossible.
	 * @param resName	The resource pool to add to
	 * @param amount	The amount to add
	 * @return			The amount successfully added - 0 on a failure.
	 */
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
	
	/**
	 * Attempts to add resource to a given pool with a maximum value the pool cannot exceed.
	 * Intended so subclasses can override pushResource() to send requests to pushResource()
	 * or pushResourceWithMax() depending on the resource pool, for batteries or stores 
	 * for example. If the pool is already above its max value this method will not reduce its
	 * value, just not add any more.
	 * @see	  Building.pushResource(String resName,BigInteger amount) 
	 * @param resName	The resource pool to add to
	 * @param amount	The amount to attempt to add
	 * @param maxvalue	The maximum value of the pool.
	 * @return			The amount successfully added to the pool.
	 */
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
	
	/**
	 * Attempts to pull resource from a pool. Returns the amount successfully taken,
	 * or 0 if the attempt fails - because the pool doesn't exist or is empty, for
	 * example.
	 * @param resName	The pool to take resource from
	 * @param amount	The amount to attempt to take
	 * @return			The amount successfully taken
	 */
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
	
	/**
	 * String representation of this Building - just gives its name.
	 */
	public String toString()
	{
		return this.buildingType+": "+this.buildingName;
	}
	
	/**
	 * Set the Network this building belongs to. Used by the Network class so
	 * Buildings can call back to it to find out about their neighbors.
	 * @param n	The Network this building is contained in.
	 */
	public void setNetwork(Network n)
	{
		this.network = n;
	}
}
