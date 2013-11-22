package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import logic.buildings.Building;

public class ConsumptionRequests {

	protected HashMap<String,ArrayList<ConsumptionRequest>> pools; //map of resource names to all requests for that resource name
	protected Set<Building> subnet; //the subnet we're dealing with, for ease of access
	
	public ConsumptionRequests(Set<Building> subnet)
	{
		this.pools = new HashMap<String,ArrayList<ConsumptionRequest>>();
		this.subnet = subnet;
	}
	
	public Set<Building> getSubnet()
	{
		return this.subnet;
	}
	
	public void addRequest(ConsumptionRequest cr)
	{
		if(!this.pools.containsKey(cr.resName))
			this.pools.put(cr.resName, new ArrayList<ConsumptionRequest>());
		this.pools.get(cr.resName).add(cr);
	}
	
	public boolean resourcesHaveBeenRequested()
	{
		if(this.pools.keySet().size() > 0)
			return true;
		return false;
	}
	
	public Set<String> getResourcesRequested()
	{
		return this.pools.keySet();
	}
	
	public ArrayList<ConsumptionRequest> getRequestsForResource(String resName)
	{
		return this.pools.get(resName);
	}
}
