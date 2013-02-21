package crisisresponseteam.simulation;

import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import uk.ac.ed.gamedevsoc.collisions.Collider;

import nlib.components.ComponentRenderable;
import nlib.physics.steering.BasicVehicle;
import nlib.physics.steering.Steering;

public strictfp final class Pedestrian extends BasicVehicle implements ComponentRenderable, Collider {
	
	private final static float MASS = 1f;
	private final static float DRAG_COEFFICIENT = 0.01f;
	private final static float MAX_SPEED = 0.5f;
	private final static float MAX_FORCE = 0.1f;
	private final static float TURN_SPEED = 4f;
	
	private final static float RADIUS = 4f;
	
	private static final float WANDER_CIRCLE_RADIUS = 256f;
	private static final float WANDER_CIRCLE_DISTANCE = 16f;
	private static final float WANDER_CIRCLE_CHANGE = 5f;
	
	private final GoreManager goreManager;
	
	private final Random random;
	
	private Image image;
	
	private float wander;
	
	private boolean isAlive;
	
	@Override
	public float getDepth() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public Pedestrian(final long id, final Vector2f initialPosition, final GoreManager goreManager) {
		
		super(id, initialPosition, MASS, DRAG_COEFFICIENT, MAX_SPEED, MAX_FORCE, TURN_SPEED);
		
		this.goreManager = goreManager;
		
		this.random = new Random();
		
		this.isAlive = false;
	}
	
	@Override
	public void init(final GameContainer gameContainer) throws SlickException {
		
		super.init(gameContainer);
		
		this.image = new Image("assets/gfx/Ped1.png");
		
		this.image.setCenterOfRotation(this.image.getWidth() / 2f, this.image.getHeight() / 2f);
		
		this.wander = this.random.nextFloat() * 360f;
		
		this.isAlive = true;
	}
	
	@Override
	public void update(GameContainer gameContainer, int delta) throws SlickException {
		
		super.update(gameContainer, delta);
		
		this.image.setRotation(this.getRotation());
		
		this.addSteering(this.wander());
		
		this.turn(Steering.turn(this.getRotation(), (float) this.getVelocity().getTheta()));
	}
	
	@Override
	public void render(final GameContainer gameContainer, final Graphics graphics) throws SlickException {
		
		graphics.drawImage(this.image, this.getPosition().getX(), this.getPosition().getY());
	}

	@Override
	public float getRadius() {
		
		return RADIUS;
	}

	@Override
	public boolean canCollide(Collider other) {
		
		return true;
	}
	
	public void runOver() {
		
		if (!this.isAlive) {
			
			return;
		}
		
		this.triggerDestroy();
		
		this.isAlive = false;
		
		this.goreManager.emit(this.getPosition());
	}
	
	private Vector2f wander() {
		
		final Vector2f circlePosition = this.getVelocity();
		
		circlePosition.normalise();
		circlePosition.scale(WANDER_CIRCLE_DISTANCE);
		circlePosition.add(this.getPosition());
		
		this.wander += this.random.nextFloat() * WANDER_CIRCLE_CHANGE * 2f - WANDER_CIRCLE_CHANGE;
		
		final Vector2f target = circlePosition.add(new Vector2f(this.wander).scale(WANDER_CIRCLE_RADIUS));
		
		return Steering.seek(this.getPosition(), this.getVelocity(), target);
	}
}