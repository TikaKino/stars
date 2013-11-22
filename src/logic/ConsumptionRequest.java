package logic;

import java.math.BigInteger;
import java.util.Set;

import logic.buildings.Building;

public class ConsumptionRequest {
	
	public String resName;
	public Building requestor; //building making the request
	public BigInteger amount; //amount being requested
	public Set<Building> subnet; // the subnet the building was determined to be in
	
	public ConsumptionRequest(String resName,BigInteger amount,Building requestor,Set<Building> subnet)
	{
		this.resName = resName;
		this.requestor = requestor;
		this.amount = amount;
		this.subnet = subnet;
	}
}
