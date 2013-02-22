package crisisresponseteam.simulation;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import nlib.components.BasicComponentRenderable;
import nlib.components.Component;
import nlib.components.ComponentManager;

public strictfp final class Map extends BasicComponentRenderable {
	
	private final ComponentManager<Component> componentManager;
	private final GoreManager goreManager;
	
	private final String ref;
	
	private TiledMap map;
	
	private boolean[][] isSolid;
	
	@Override
	public float getDepth() {
		
		return Constants.DEPTH_MAP;
	}
	
	public float getWidth() {
		
		return this.map.getWidth() * this.map.getTileWidth();
	}
	
	public float getHeight() {
		
		return this.map.getHeight() * this.map.getTileHeight();
	}
	
	public int getTileWidth() {
		
		return this.map.getTileWidth();
	}
	
	public int getTileHeight() {
		
		return this.map.getTileHeight();
	}
	
	public boolean isSolid(final int x, final int y) {
		
		if (x < 0) {
			
			return true;
		}
		
		if (y < 0) {
			
			return true;
		}
		
		if (x >= this.map.getWidth()) {
			
			return true;
		}
		
		if (y >= this.map.getHeight()) {
			
			return true;
		}
		
		return this.isSolid[y][x];
	}
	
	public Map(final long id, final ComponentManager<Component> componentManager, final GoreManager goreManager, final String ref) {
		
		super(id);
		
		this.componentManager = componentManager;
		this.goreManager = goreManager;
		
		this.ref = ref;
	}
	
	@Override
	public void init(final GameContainer gameContainer) throws SlickException {
		
		super.init(gameContainer);
		
		this.map = new TiledMap(this.ref);
		
		this.isSolid = new boolean[this.map.getHeight()][this.map.getWidth()];
		
		for (int y = 0; y < this.isSolid.length; y++) {
			
			for (int x = 0; x < this.isSolid[0].length; x++) {
				
				this.isSolid[y][x] = false;
			}
		}
		
		for (int l = 0; l < this.map.getLayerCount(); l++) {
			
			for (int y = 0; y < this.map.getHeight(); y++) {
				
				for (int x = 0; x < this.map.getWidth(); x++) {
					
					final int tileID = this.map.getTileId(x, y, l);
					
					if (Boolean.parseBoolean(this.map.getTileProperty(tileID, "Solid", "false"))) {
						
						this.isSolid[y][x] = true;
					}
				}
			}
		}
		
		for (int g = 0; g < this.map.getObjectGroupCount(); g++) {
			
			for (int o = 0; o < this.map.getObjectCount(g); o++) {
				
				final String type = this.map.getObjectType(g, o);
				
				if (type.equals("PedestrianSpawn")) {
					
					final float x = this.map.getObjectX(g, o);
					final float y = this.map.getObjectY(g, o);
					
					final int interval = Integer.parseInt(this.map.getObjectProperty(g, o, "Interval", "10000"));
					
					final PedestrianSpawn pedestrianSpawn = new PedestrianSpawn(
							this.componentManager.takeId(), 
							this.componentManager, 
							this.goreManager, 
							new Vector2f(x, y), 
							interval);
					
					this.componentManager.addComponent(pedestrianSpawn);
				}
				else if (type.equals("CrisisSite")) {
					
					final float x = this.map.getObjectX(g, o);
					final float y = this.map.getObjectY(g, o);
					
					final String name = this.map.getObjectProperty(g, o, "Name", "???");
					
					final CrisisSite crisisSite = new CrisisSite(
							this.componentManager.takeId(), 
							new Vector2f(x, y), 
							name);
					
					this.componentManager.addComponent(crisisSite);
				}
			}
		}
	}
	
	@Override
	public void update(final GameContainer gameContainer, final int delta) throws SlickException {
		
		super.update(gameContainer, delta);
		
		
	}
	
	@Override
	public void render(GameContainer gameContainer, Graphics graphics) throws SlickException {
		
		super.render(gameContainer, graphics);
		
		this.map.render(0, 0);
	}
}
