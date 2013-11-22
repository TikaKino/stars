package logic;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import logic.buildings.Building;
import logic.buildings.LifeSupport;
import logic.buildings.LivingQuarters;
import logic.buildings.PowerPlant;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.BasicCommand;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProvider;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.command.KeyControl;
import org.newdawn.slick.command.MouseButtonControl;


public class GameBase extends BasicGame implements InputProviderListener {

	protected Network network;
	protected InputProvider inputProvider;
	protected Input input;
	
	protected int input_state;
	protected Building preparedBuilding;
	protected int buildingNumber;
	
	public static final int INPUT_STATE_BASIC = 0;
	public static final int INPUT_STATE_BUILD = 1;
	
	public GameBase(String gamename) {
		super(gamename);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		
		gc.setTargetFrameRate(60);
		
		this.network = new Network(80,60);
		this.preparedBuilding = null;
		this.buildingNumber = 1;
		
		this.network.addBuilding(new LivingQuarters("LQ1"), 0, 18);
		this.network.addBuilding(new PowerPlant("Plant"),0,3);
		this.network.addBuilding(new LifeSupport("LS1"), 0, 13);
		this.network.addBuilding(new LifeSupport("LS2"), 5, 13);
		
		this.input_state = INPUT_STATE_BASIC;
		this.input = gc.getInput();
		this.inputProvider = new InputProvider(this.input);
		this.inputProvider.addListener(this);
		Command c = null;
		
		c = new BasicCommand("build_power_plant");
		this.inputProvider.bindCommand(new KeyControl(Input.KEY_P), c);
		
		c = new BasicCommand("build_life_support");
		this.inputProvider.bindCommand(new KeyControl(Input.KEY_L), c);
		
		c = new BasicCommand("build_living_quarters");
		this.inputProvider.bindCommand(new KeyControl(Input.KEY_Q), c);
		
		c = new BasicCommand("cancel");
		this.inputProvider.bindCommand(new KeyControl(Input.KEY_ESCAPE), c);
		
		c = new BasicCommand("click");
		this.inputProvider.bindCommand(new MouseButtonControl(0), c);
	}

	@Override
	public void update(GameContainer gc, int i) throws SlickException {
		
		//Handle addition of buildings
		
		
		//Update current resource status
		Iterator<Building> it = this.network.getBuildingsIterator();
		while(it.hasNext())
			it.next().produce(i);
		it = this.network.getBuildingsIterator();
		while(it.hasNext())
			it.next().consume(i);
		it = this.network.getBuildingsIterator();
		while(it.hasNext())
			it.next().store();
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		
		Iterator<Building> it = this.network.getBuildingsIterator();
		while(it.hasNext())
			it.next().render(gc, g);
		
		if(this.input_state == INPUT_STATE_BUILD)
		{
			g.drawString("Build Mode", 700, 10);
			if(this.preparedBuilding != null)
			{
				g.drawString(this.preparedBuilding.toString(), 600, 25);
			}
		}
	}

	public static void main(String[] args) {
		try {
			AppGameContainer appgc;
			appgc = new AppGameContainer(new GameBase("Simple Slick Game"));
			appgc.setDisplayMode(800, 600, false);
			appgc.start();
		} catch (SlickException ex) {
			Logger.getLogger(GameBase.class.getName()).log(Level.SEVERE, null, ex);
		}
		
	}

	@Override
	public void controlPressed(Command command) {
		// TODO Auto-generated method stub
		
		if(command.toString().equals("[Command=cancel]"))
		{
			this.input_state = INPUT_STATE_BASIC;
			this.preparedBuilding = null;
			return;
		}
		else if(command.toString().equals("[Command=build_power_plant]"))
		{
			this.input_state = INPUT_STATE_BUILD;
			this.preparedBuilding = new PowerPlant("PP"+this.buildingNumber);
			this.buildingNumber++;
			return;
		}
		else if(command.toString().equals("[Command=build_life_support]"))
		{
			this.input_state = INPUT_STATE_BUILD;
			this.preparedBuilding = new LifeSupport("LS"+this.buildingNumber);
			this.buildingNumber++;
			return;
		}
		else if(command.toString().equals("[Command=build_living_quarters]"))
		{
			this.input_state = INPUT_STATE_BUILD;
			this.preparedBuilding = new LivingQuarters("LQ"+this.buildingNumber);
			this.buildingNumber++;
			return;
		}
		else if(command.toString().equals("[Command=click]"))
		{
			//Do click event stuff
			//System.out.println("("+this.input.getMouseX()+","+this.input.getMouseY()+")");
			
			if(this.input_state == INPUT_STATE_BUILD)
			{
				int x = this.input.getMouseX() / 10;
				int y = this.input.getMouseY() / 10;
				if(this.network.addBuilding(this.preparedBuilding, x, y))
				{
					this.input_state = INPUT_STATE_BASIC;
					this.preparedBuilding = null;
				}
				else
				{
					
				}
			}
		}
	}

	@Override
	public void controlReleased(Command command) {
		// TODO Auto-generated method stub
		
	}
}