package tests;

import java.math.BigInteger;

import logic.buildings.Building;


public class ResourceTest {

	
	
	public static void main (String[] args)
	{
		Building b = new Building("Test Building",30,30);
		BigInteger amount = BigInteger.ZERO;
		
		b.registerResource("testResource");
		
		System.out.println("Pool: "+b.getResource("testResource"));
		
		amount = b.pushResource("testResource", new BigInteger("10"));
		
		System.out.println("Pushed 10 to testResource Pool: "+amount+" successfully pushed");
		System.out.println("Pool: "+b.getResource("testResource"));
		
		amount = b.pushResource("testResource", new BigInteger("10"));
		
		System.out.println("Pushed 10 to testResource Pool: "+amount+" successfully pushed");
		System.out.println("Pool: "+b.getResource("testResource"));
		
		amount = b.pullResource("testResource", new BigInteger("5"));
		
		System.out.println("Pulled 5 from testResource Pool: "+amount+" successfully pulled");
		System.out.println("Pool: "+b.getResource("testResource"));
		
		amount = b.pullResource("testResource", new BigInteger("20"));
		
		System.out.println("Pulled 20 from testResource Pool: "+amount+" successfully pulled");
		System.out.println("Pool: "+b.getResource("testResource"));
		
		amount = b.pushResource("testResource", new BigInteger("10"));
		
		System.out.println("Pushed 10 to testResource Pool: "+amount+" successfully pushed");
		System.out.println("Pool: "+b.getResource("testResource"));
		
		amount = b.pullResource("testResource", new BigInteger("10"));
		
		System.out.println("Pulled 10 from testResource Pool: "+amount+" successfully pulled");
		System.out.println("Pool: "+b.getResource("testResource"));
		
		amount = b.pushResourceWithMax("testResource", new BigInteger("20"),new BigInteger("18"));
		
		System.out.println("Pushed 20 to testResource Pool with a max of 18: "+amount+" successfully pushed");
		System.out.println("Pool: "+b.getResource("testResource"));
		
		amount = b.pushResourceWithMax("testResource", new BigInteger("20"),new BigInteger("15"));
		
		System.out.println("Pushed 20 to testResource Pool with a max of 15: "+amount+" successfully pushed");
		System.out.println("Pool: "+b.getResource("testResource"));
	}
}
