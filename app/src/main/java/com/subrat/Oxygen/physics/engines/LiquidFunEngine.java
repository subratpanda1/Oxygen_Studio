package com.subrat.Oxygen.physics.engines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.PointF;
import android.util.Log;

import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.graphics.object.DrawableCircle;
import com.subrat.Oxygen.physics.object.PhysicsCircle;
import com.subrat.Oxygen.physics.object.PhysicsLine;
import com.subrat.Oxygen.physics.object.PhysicsObject;
import com.subrat.Oxygen.physics.object.PhysicsWaterParticle;
import com.subrat.Oxygen.utilities.Configuration;
import com.subrat.Oxygen.utilities.MathUtils;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.CircleShape;
import com.google.fpl.liquidfun.ParticleDef;
import com.google.fpl.liquidfun.ParticleFlag;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.ParticleSystemDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.World;
import com.google.fpl.liquidfun.Body;

public class LiquidFunEngine implements PhysicsEngineInterface {
	private World world;
	private ParticleSystem particleSystem;

	private Map<Integer, Body> objectMap;
	private Map<Integer, Integer> particleMap;

	public LiquidFunEngine() {
		objectMap = new HashMap<>();
		particleMap = new HashMap<>();
	}
	
	public void clearWorld() {
		if (world != null) {
			for(Map.Entry<Integer, Body> entry : objectMap.entrySet()) {
				Body body = entry.getValue();
				world.destroyBody(body);
			}
			world.delete();
			world = null;
			particleSystem.delete();
		}
		objectMap.clear();
		particleMap.clear();
	}
	
	public void initWorld() {
		clearWorld();
		world = new World(0, 0);
		createParticleSystem();
	}
	
	public void stepWorld(float stepDuration) {
		if (world == null) return;
		world.step(stepDuration,
				Configuration.VELOCITY_ITERATIONS,
				Configuration.POSITION_ITERATIONS,
				Configuration.PARTICLE_ITERATIONS);
	}

	public void createObjectInWorld(PhysicsObject object) {
		if (objectMap.containsKey(object.getObjectId())) {
			Log.e("Subrat", "Object " + object.getObjectId() + " already exists");
			return;
		}

		if (particleMap.containsKey(object.getObjectId())) {
			Log.e("Subrat", "Particle " + object.getObjectId() + " already exists");
			return;
		}

		if (object instanceof PhysicsCircle) {
			PhysicsCircle physicsCircle = (PhysicsCircle)object;
			Body circleBody = createCircle(physicsCircle);
			objectMap.put(physicsCircle.getObjectId(), circleBody);
		} else if (object instanceof PhysicsLine) {
			PhysicsLine physicsLine = (PhysicsLine)object;
			Body lineBody = createLine(physicsLine);
			objectMap.put(physicsLine.getObjectId(), lineBody);
		} else if (object instanceof PhysicsWaterParticle) {
			int particleIndex = createParticle((PhysicsWaterParticle) object);
			particleMap.put(object.getObjectId(), particleIndex);
		}
	}

	public void deleteObjectFromWorld(PhysicsObject object) {
		if (object instanceof PhysicsCircle) {
			Body circleBody = objectMap.get(object.getObjectId());
			circleBody.delete();
			objectMap.remove(object.getObjectId());
		} else if (object instanceof PhysicsLine) {
			Body lineBody = objectMap.get(object.getObjectId());
			lineBody.delete();
			objectMap.remove(object.getObjectId());
		} else if (object instanceof PhysicsWaterParticle) {
			// TODO: Particle deletion needs reindexing in particleMap
			// particleSystem.destroyParticle(object.getObjectId(), false);
			// particleMap.remove(object.getObjectId());
		}
	}

	public void updateAllPhysicsObjectsFromWorld(ArrayList<PhysicsObject> objects) {
		for (PhysicsObject object : objects) {
			updatePhysicsObjectFromWorldObject(object);
		}
	}

	public void updatePhysicsObjectFromWorldObject(PhysicsObject object) {
		if (object instanceof PhysicsCircle) {
			PhysicsCircle physicsCircle = (PhysicsCircle)object;
			Body circleBody = objectMap.get(physicsCircle.getObjectId());
			if (circleBody == null) return;
			PointF worldCircleCenter = new PointF(circleBody.getPositionX(), circleBody.getPositionY());
			PointF physicsCircleCenter = getPhysicalPositionFromEnginePosition(worldCircleCenter);
			physicsCircle.setCenter(physicsCircleCenter);
			float rad = circleBody.getAngle();
			int deg = MathUtils.getMathUtils().getDegreeFromRadian(rad);
			physicsCircle.setRotation(deg);
		} else if (object instanceof PhysicsLine) {
			PhysicsLine physicsLine = (PhysicsLine)object;
			Body lineBody = objectMap.get(physicsLine.getObjectId());
			if (lineBody == null) return;
			// TODO: Update physicsLine from lineBody
		} else if (object instanceof PhysicsWaterParticle) {
			PhysicsWaterParticle physicsWaterParticle = (PhysicsWaterParticle)object;
			Integer particleIndex = particleMap.get(object.getObjectId());
			if (particleIndex == null) return;
			PointF worldParticlePosition = new PointF(particleSystem.getParticlePositionX(particleIndex), particleSystem.getParticlePositionY(particleIndex));
			PointF physicsParticlePosition = getPhysicalPositionFromEnginePosition(worldParticlePosition);
			physicsWaterParticle.setPosition(physicsParticlePosition);
		}
	}

	public void updateWorldObjectFromPhysicsObject(PhysicsObject object) {
		if (object instanceof PhysicsCircle) {
			PhysicsCircle physicsCircle = (PhysicsCircle)object;
			Body circleBody = objectMap.get(physicsCircle.getObjectId());
			if (circleBody == null) return;
			PointF worldCircleCenter = getEnginePositionFromPhysicalPosition(physicsCircle.getCenter());
			circleBody.setTransform(worldCircleCenter.x, worldCircleCenter.y, physicsCircle.getRotation());
		} else if (object instanceof PhysicsLine) {
			PhysicsLine physicsLine = (PhysicsLine)object;
			Body lineBody = objectMap.get(physicsLine.getObjectId());
			if (lineBody == null) return;
			PointF startPointInWorld = getEnginePositionFromPhysicalPosition(physicsLine.getStart());
			PointF endPointInWorld   = getEnginePositionFromPhysicalPosition(physicsLine.getEnd());
			lineBody.setTransform((startPointInWorld.x + endPointInWorld.x) / 2,
					(startPointInWorld.y + endPointInWorld.y) / 2,
					MathUtils.getMathUtils().getRadian(startPointInWorld, endPointInWorld));
		} else if (object instanceof PhysicsWaterParticle) {
			PhysicsWaterParticle physicsWaterParticle = (PhysicsWaterParticle)object;
			Integer worldParticleIndex = particleMap.get(physicsWaterParticle.getObjectId());
			if (worldParticleIndex == null) return;
			PointF positionInWorld = getEnginePositionFromPhysicalPosition(physicsWaterParticle.getPosition());
			// TODO: Update particle position
		}
	}
	
	public void setGravity(PointF gravity) {
		PointF worldGravity = getEnginePositionFromPhysicalPosition(gravity);
		world.setGravity(worldGravity.x, worldGravity.y);
	}
	
	private Body createCircle(PhysicsCircle physicsCircle) {
		PointF centerInWorld = getEnginePositionFromPhysicalPosition(physicsCircle.getCenter());

		CircleShape circleShape = new CircleShape();
		circleShape.setPosition(0,0);
		circleShape.setRadius(physicsCircle.getRadius());
		
		BodyDef circleBodyDef = new BodyDef();
		circleBodyDef.setType(BodyType.dynamicBody);
		circleBodyDef.setPosition(centerInWorld.x, centerInWorld.y);
		circleBodyDef.setAllowSleep(false);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.setShape(circleShape);
		fixtureDef.setDensity(2F);
		fixtureDef.setRestitution(Configuration.RESTITUTION);
		fixtureDef.setFriction(1F);
		
		Body circleBody = world.createBody(circleBodyDef);
		circleBody.createFixture(fixtureDef);
		
		circleShape.delete();
		circleBodyDef.delete();
		fixtureDef.delete();

		return circleBody;
	}
	
	private Body createLine(PhysicsLine physicsLine) {
		PointF startPointInWorld = getEnginePositionFromPhysicalPosition(physicsLine.getStart());
		PointF endPointInWorld = getEnginePositionFromPhysicalPosition(physicsLine.getEnd());
		
		PolygonShape lineShape = new PolygonShape();
		float length = MathUtils.getMathUtils().getDistance(physicsLine.getStart(), physicsLine.getEnd());
		lineShape.setAsBox(length / 2, Configuration.LINE_THICKNESS / 2);
		
		BodyDef lineBodyDef = new BodyDef();
		lineBodyDef.setType(BodyType.staticBody);
		lineBodyDef.setPosition((startPointInWorld.x + endPointInWorld.x) / 2, (startPointInWorld.y + endPointInWorld.y) / 2);
		lineBodyDef.setAngle(MathUtils.getMathUtils().getRadian(startPointInWorld, endPointInWorld));

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.setShape(lineShape);
		fixtureDef.setDensity(0F);
		
		Body lineBody = world.createBody(lineBodyDef);
		lineBody.createFixture(fixtureDef);
		
		lineShape.delete();
		lineBodyDef.delete();
		fixtureDef.delete();

		return lineBody;
	}

	private int createParticle(PhysicsWaterParticle physicsWaterParticle) {
		ParticleDef particleDef = new ParticleDef();
		particleDef.setFlags(ParticleFlag.waterParticle);
		PointF particlePositionInWorld = getEnginePositionFromPhysicalPosition(physicsWaterParticle.getPosition());
		particleDef.setPosition(particlePositionInWorld.x, particlePositionInWorld.y);
		int particleIndex = particleSystem.createParticle(particleDef);
		particleDef.delete();
		return particleIndex;
	}

	public void createParticleSystem() {
		ParticleSystemDef psDef = new ParticleSystemDef();
        psDef.setRadius(Configuration.PARTICLE_RADIUS);
        psDef.setDampingStrength(Configuration.PARTICLE_DAMPING);
        psDef.setRepulsiveStrength(Configuration.PARTICLE_REPULSIVE_STRENGTH);
        psDef.setDensity(Configuration.PARTICLE_DENSITY);
        // psDef.setStrictContactCheck(true);
        
        particleSystem = world.createParticleSystem(psDef);
        particleSystem.setMaxParticleCount(Configuration.MAX_PARTICLE_COUNT);
        psDef.delete();
        // addGroundWater();
	}

	/*
	public void addGroundWater() {
        for (int x = 1; x < 20; ++x) {
        	for (int y = 1; y < 20; ++y) {
        		ParticleDef particleDef = new ParticleDef();
        		particleDef.setFlags(ParticleFlag.waterParticle);
        		particleDef.setPosition(2 * Configuration.CANVAS_MARGIN + 2 * Configuration.LINE_THICKNESS + ((float)x)/7,
        				                2 * Configuration.CANVAS_MARGIN + 2 * Configuration.LINE_THICKNESS + ((float)y)/7);
        		particleSystem.createParticle(particleDef);
        		particleDef.delete();
        	}
        }
	}

	public void addWater(ArrayList<PhysicsWaterParticle> particleList) {
		float shift = MathUtils.getMathUtils().getRandom(OxygenActivity.getWorldWidth() / 5, (OxygenActivity.getWorldWidth() * 3) / 5);
		for (int x = 1; x < 5; ++x) {
			for (int y = 1; y < 5; ++y) {
        		ParticleDef particleDef = new ParticleDef();
        		particleDef.setFlags(ParticleFlag.waterParticle);
        		float borderDistance = 2 * Configuration.CANVAS_MARGIN + 2 * Configuration.LINE_THICKNESS;
        		particleDef.setPosition(borderDistance + shift + ((float)x)/7,
        				                OxygenActivity.getWorldHeight() - borderDistance - ((float)y)/7);
        		particleSystem.createParticle(particleDef);
        		particleDef.delete();
			}
		}

		for (PhysicsWaterParticle particle : particleList) {
			ParticleDef particleDef = new ParticleDef();
			particleDef.setFlags(ParticleFlag.waterParticle);
			PointF particleWorldPosition = getEnginePositionFromPhysicalPosition(particle.getPosition());
			particleDef.setPosition(particleWorldPosition.x, particleWorldPosition.y);
			particleSystem.createParticle(particleDef);
			particleDef.delete();
		}
	}
	*/

	private PointF getPhysicalPositionFromEnginePosition(PointF point) {
		if (point == null) return null;
		return new PointF(point.x, point.y);

	}

	private PointF getEnginePositionFromPhysicalPosition(PointF point) {
		if (point == null) return null;
		return new PointF(point.x, point.y);
	}

	private ArrayList<PointF> getPhysicalPositionFromEnginePosition(ArrayList<PointF> points) {
		if (points == null) return null;

		ArrayList<PointF> newPoints = new ArrayList<>();
		for (PointF point : points) {
			newPoints.add(new PointF(point.x, point.y));
		}

		return newPoints;
	}

	private ArrayList<PointF> getEnginePositionFromPhysicalPosition(ArrayList<PointF> points) {
		if (points == null) return null;

		ArrayList<PointF> newPoints = new ArrayList<>();
		for (PointF point : points) {
			newPoints.add(new PointF(point.x, point.y));
		}

		return newPoints;
	}
}
