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
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProviderListener;


public class GameBase extends BasicGame implements InputProviderListener {

	protected Network network;
	protected int input_state;
	
	public GameBase(String gamename) {
		super(gamename);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		
		gc.setTargetFrameRate(60);
		
		this.network = new Network(80,60);
		
		this.network.addBuilding(new PowerPlant("Plant"),0,3);
		this.network.addBuilding(new LifeSupport("LS1"), 0, 13);
		this.network.addBuilding(new LifeSupport("LS2"), 5, 13);
		//this.network.addBuilding(new LifeSupport("LS3"), 10, 13);
		//this.network.addBuilding(new LifeSupport("LS4"), 15, 13);
		this.network.addBuilding(new LivingQuarters("LQ1"), 0, 18);
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
		
	}

	@Override
	public void controlReleased(Command command) {
		// TODO Auto-generated method stub
		
	}
}