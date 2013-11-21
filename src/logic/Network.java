package logic;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import logic.buildings.Building;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 * defines a network of buildings - essentially, a map of the colony's construction.
 * Handles all the important building-to-building stuff - what's next to what, where you
 * can build, what's connected to what. 
 * @author Mikael Suominen
 */
public class Network {

	protected Building[][] buildingGrid = null; //grid for fitting stuff in and determining what is adjacent to what, buildings may be in multiple squares
	protected int gridXMax;
	protected int gridYMax;
	
	protected ArrayList<Building> buildings; //list of buildings for cycling each building once
	protected UndirectedGraph<Building,DefaultEdge> buildingGraph; //building graph for determining connections (for delivering resources)
	protected ConnectivityInspector<Building,DefaultEdge> ci;
	
	/**
	 * Creates a new network with a given size in units. Not related to screen
	 * size directly, though early versions have a 1 unit = 10 pixels correlation
	 * @param x		Width of the network in units
	 * @param y		Height of the network in units
	 */
	public Network(int x, int y)
	{
		//Initialise the list and graph of buildings
		this.buildings = new ArrayList<Building>(30);
		this.buildingGraph = new SimpleGraph<Building,DefaultEdge>(DefaultEdge.class);
		this.ci = new ConnectivityInspector<Building,DefaultEdge>(this.buildingGraph);
		
		//initialise the grid
		this.buildingGrid = new Building[x][y];
		this.gridXMax = x - 1;
		this.gridYMax = y - 1;
		for(int ix = 0;ix<x;ix++)
			for (int iy = 0;iy<y;iy++)
				this.buildingGrid[ix][iy] = null;
	}
	
	/**
	 * Attempts to add a Building at co-ordinates (x,y). If successful, adds the building
	 * onto the grid and into the list and graph. If not, returns false.
	 * @param b The building to add
	 * @param x x coordinate to attempt to place b
	 * @param y y coordinate to attempt to place b
	 * @return true on successful add, false otherwise. No other info yet.
	 */
	public boolean addBuilding(Building b, int x, int y)
	{
		//Check we're within bounds
		if(x < 0 || x + b.getWidth() > this.gridXMax)
			return false;
		if(y < 0 || y + b.getHeight() > this.gridYMax)
			return false;
		
		//Check there's space in the grid
		for(int w = x; w < x + b.getWidth(); w++)
			for(int h = y; h < y + b.getHeight(); h++)
				if(this.buildingGrid[w][h] != null)
					return false;
		
		//Fill the grid
		for(int w = x; w < x + b.getWidth(); w++)
			for(int h = y; h < y + b.getHeight(); h++)
				this.buildingGrid[w][h] = b;
		
		//Add to the list and graph
		this.buildings.add(b);
		this.buildingGraph.addVertex(b);
		
		//Add adjacencies to the graph
		//top row
		if(y-1 > 0) //check this isn't the top row!
			for(int i = x; i < x + b.getWidth(); i++) //do the width of the row 
				if(i > 0 && i < this.gridXMax && this.buildingGrid[i][y-1] != null) //check we're in bounds, see if there's an unfilled grid square
					if(!this.buildingGraph.containsEdge(b,this.buildingGrid[i][y-1])) //see if we're already connected to what's in that grid square
						this.buildingGraph.addEdge(b,this.buildingGrid[i][y-1]); //add an adjacency to that building
		//bottom row
		if(y + b.getHeight() < this.gridYMax) //check we're not touching the bottom
			for(int i = x; i < x + b.getWidth(); i++) //do the width of the row
				if(i > 0 && i < this.gridXMax && this.buildingGrid[i][y + b.getHeight()] != null) //check we're in bounds, see if there's an unfilled grid square
					if(!this.buildingGraph.containsEdge(b,this.buildingGrid[i][y + b.getHeight()])) //see if we're already connected to what's in that grid square
						this.buildingGraph.addEdge(b,this.buildingGrid[i][y + b.getHeight()]); //add an adjacency to that building
		//left column
		if(x-1 > 0) //check we're not already on the far left
			for(int i = y; i < y + b.getHeight(); i++) // do the height of the building
				if(i > 0 && i < this.gridYMax && this.buildingGrid[x-1][i] != null)
					if(!this.buildingGraph.containsEdge(b,this.buildingGrid[x-1][i]))
						this.buildingGraph.addEdge(b,this.buildingGrid[x-1][i]);
		//right column
		if(x + b.getWidth() < this.gridXMax) //check we're not already on the far right
			for(int i = y; i < y + b.getHeight(); i++) // do the height of the building
				if(i > 0 && i < this.gridYMax && this.buildingGrid[x + b.getWidth()][i] != null)
					if(!this.buildingGraph.containsEdge(b,this.buildingGrid[x + b.getWidth()][i]))
						this.buildingGraph.addEdge(b,this.buildingGrid[x + b.getWidth()][i]);
		
		b.setNetwork(this);
		b.setPos(x, y);
		return true;
	}
	
	/**
	 * Gets an iterator over all the buildings in the network. Primarily used by
	 * the logic loop to get every building producing, consuming and storing. 
	 * @return this.buildings.iterator();
	 */
	public Iterator<Building> getBuildingsIterator()
	{
		return this.buildings.iterator();
	}
	
	/**
	 * Pulls a building out of the network, killing its connections to all other
	 * buildings.
	 * @param b the building to remove.
	 */
	public void removeBuilding(Building b)
	{
		this.buildings.remove(b);
		this.buildingGraph.removeVertex(b);
		for(int i=0;i<this.gridXMax;i++)
			for(int j=0;j<this.gridYMax;j++)
				if(this.buildingGrid[i][j] == b)
					this.buildingGrid[i][j] = null;
	}
	
	/**
	 * Adds an adjacency between b1 and b2. Usually used by the network itself to 
	 * tell the graph what building is next to what, but could be used by other 
	 * buildings to add strange adjacencies - such as for a helipad or portal.
	 * @param b1
	 * @param b2
	 */
	public void addAdjacency(Building b1, Building b2)
	{
		this.buildingGraph.addEdge(b1, b2);
	}
	
	/**
	 * determines whether two buildings are adjacent to each other from the network's
	 * point of view. 
	 * @param b1
	 * @param b2
	 * @return true if they are adjacent, or false if not.
	 */
	//Are these two buildings adjacent to each other?
	public boolean areAdjacent(Building b1, Building b2)
	{
		return this.buildingGraph.containsEdge(b1, b2);
	}
	
	/**
	 * determines whether two buildings are connected to each other from the network's
	 * point of view - that is, if an unblocked path can be traced from b1 to b2 
	 * through adjacent buildings.
	 * @param b1
	 * @param b2
	 * @return true if they are conected, or false if not.
	 */
	//Are these two buildings connected by a chain of adjacent buildings?
	public boolean areConnected(Building b1, Building b2)
	{
		if(this.ci.pathExists(b1, b2))
			return true;
		return false;
	}
	
	/**
	 * Gets all the buildings connected to b1 by an unbroken chain of adjacent buildings.
	 * Regularly used for a building to check all the buildings it can draw resources from.
	 * @param b1	the building to grab connected buildings for
	 * @return		a Set of Building objects connected to b1
	 */
	public Set<Building> connectedBuildings(Building b1)
	{
		return this.ci.connectedSetOf(b1);
	}
	
	public void _debugPrintGraph()
	{
		System.out.println(this.buildingGraph.toString());
	}
	
	/**
	 * Attempts to consume a generic resource (resName) from all buildings connected to 
	 * a specific building (target) - used for example to take from any available power_pool
	 * sources. Order it will hit buildings is undefined; if order is important, craft a
	 * more specific version of this function.
	 * @param amount	The amount of resource to attempt to draw
	 * @param resName	The name (e.g. "power_pool") of the resource to draw from
	 * @param target	The building drawing the resource - all sources must be connected to
	 * this building.
	 * @return			The amount of resource successfully drawn.
	 */
	public BigInteger consumeFromNetwork(BigInteger amount, String resName, Building target)
	{
		BigInteger toConsume = amount.add(BigInteger.ZERO);
		BigInteger consumed = BigInteger.ZERO;
		
		//go through our network trying to find resName, then take from it
		Set<Building> connected = this.connectedBuildings(target);
		Iterator<Building> it = connected.iterator();
		while(it.hasNext())
		{
			Building b = it.next();
			if(!b.providesResource(resName))
				continue;
			consumed = b.pullResource(resName, toConsume);
			toConsume = toConsume.subtract(consumed);
		}
		
		consumed = amount.subtract(toConsume);
		
		return consumed;
	}
}
