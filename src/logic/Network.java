package logic;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import logic.buildings.Building;
import logic.buildings.PowerPlant;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class Network {

	protected Building[][] buildingGrid = null; //grid for fitting stuff in and determining what is adjacent to what, buildings may be in multiple squares
	protected int gridXMax;
	protected int gridYMax;
	
	protected ArrayList<Building> buildings; //list of buildings for cycling each building once
	protected UndirectedGraph<Building,DefaultEdge> buildingGraph; //building graph for determining connections (for delivering resources)
	protected ConnectivityInspector<Building,DefaultEdge> ci;
	
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
	
	public Iterator<Building> getBuildingsIterator()
	{
		return this.buildings.iterator();
	}
	
	public void removeBuilding(Building b)
	{
		this.buildings.remove(b);
		this.buildingGraph.removeVertex(b);
	}
	
	public void addAdjacency(Building b1, Building b2)
	{
		this.buildingGraph.addEdge(b1, b2);
	}
	
	//Are these two buildings adjacent to each other?
	public boolean areAdjacent(Building b1, Building b2)
	{
		return this.buildingGraph.containsEdge(b1, b2);
	}
	
	//Are these two buildings connected by a chain of adjacent buildings?
	public boolean areConnected(Building b1, Building b2)
	{
		if(this.ci.pathExists(b1, b2))
			return true;
		return false;
	}
	
	public Set<Building> connectedBuildings(Building b1)
	{
		return this.ci.connectedSetOf(b1);
	}
	
	public void _debugPrintGraph()
	{
		System.out.println(this.buildingGraph.toString());
	}
	
	public BigInteger consumeFromNetwork(BigInteger amount, String resName, Building target)
	{
		BigInteger toConsume = amount.add(BigInteger.ZERO);
		BigInteger consumed = BigInteger.ZERO;
		
		//go through our network trying to find power_pool, then take from it
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
	
	public static void main(String[] args)
	{
		Network n = new Network(400,400);
		boolean test = false;
		Building b = null;
		Building pp1 = null;
		Building pp4 = null;
		Building c2 = null;
		Building c3 = null;
		
		//create a power plant [10x10]
		b = new PowerPlant("pp1");
		pp1 = b;
		test = n.addBuilding(b, -1, -1); //at (-1,-1) - should fail!
		System.out.println("Attempted to add "+b+" at (-1,-1): "+test);
		n._debugPrintGraph();
		test = n.addBuilding(b, 0, 0); //at (0,0) - should succeed
		System.out.println("Attempted to add "+b+" at (0,0): "+test);
		n._debugPrintGraph();
		
		//create another power plant [10x10]
		b = new PowerPlant("pp2");
		test = n.addBuilding(b, 9, 9); //at (9,9) - should fail, space already taken up by pp1
		System.out.println("Attempted to add "+b+" at (9,9): "+test);
		n._debugPrintGraph();
		test = n.addBuilding(b, 10, 0); //at (10,0) - should succeed and add an edge
		System.out.println("Attempted to add "+b+" at (10,0): "+test);
		n._debugPrintGraph();
		
		//create a custom [5x5] to add below the power plant
		b = new Building("c1",5,5);
		test = n.addBuilding(b, 0, 10); //at (0,10) - should succeed and add an edge
		System.out.println("Attempted to add "+b+" at (0,10): "+test);
		n._debugPrintGraph();
		//create a custom [5x5] to add below the power plant
		b = new Building("c2",5,5);
		c2 = b;
		test = n.addBuilding(b, 5, 10); //at (5,10) - should succeed and add edges to pp1 and c1
		System.out.println("Attempted to add "+b+" at (5,10): "+test);
		n._debugPrintGraph();
		
		//create another power plant [10x10]
		b = new PowerPlant("pp3");
		test = n.addBuilding(b, 0, 15); //at (0,15) - should succeed and add edges to c1 and c2
		System.out.println("Attempted to add "+b+" at (0,15): "+test);
		n._debugPrintGraph();
		
		//create another power plant [10x10]
		b = new PowerPlant("pp4");
		pp4 = b;
		test = n.addBuilding(b, 10, 10); //at (10,10) - should succeed and add edges to pp2, c2 and pp3
		System.out.println("Attempted to add "+b+" at (10,10): "+test);
		n._debugPrintGraph();
		
		//create a custom [5x5] to add out of the way
		b = new Building("c3",5,5);
		c3 = b;
		test = n.addBuilding(b,40,40); //at (40,40) - should succeed and add no edges
		System.out.println("Attempted to add "+b+" at (40,40): "+test);
		n._debugPrintGraph();
		
		//A couple more tests: pp1 and pp4 should be connected but not adjacent:
		System.out.println("pp1 and pp4 are adjacent: "+n.areAdjacent(pp1, pp4)+", pp1 and pp4 are connected: "+n.areConnected(pp1, pp4));
		System.out.println("c2 and pp4 are adjacent: "+n.areAdjacent(c2, pp4)+", c2 and pp4 are connected: "+n.areConnected(c2, pp4));
		System.out.println("c3 and pp4 are adjacent: "+n.areAdjacent(c3, pp4)+", c3 and pp4 are connected: "+n.areConnected(c3, pp4));
	}
}
