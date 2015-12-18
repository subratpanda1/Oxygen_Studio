package com.subrat.Oxygen.physics;

import android.graphics.PointF;

import com.subrat.Oxygen.graphics.HadaGraphicsEngine;
import com.subrat.Oxygen.utilities.ObjectMapper;
import com.subrat.Oxygen.graphics.object.DrawableLine;
import com.subrat.Oxygen.physics.object.PhysicsCircle;
import com.subrat.Oxygen.physics.object.PhysicsLine;
import com.subrat.Oxygen.physics.object.PhysicsObject;
import com.subrat.Oxygen.utilities.Configuration;
import com.subrat.Oxygen.utilities.MathUtils;

/**
 * Created by subrat.panda on 18/12/15.
 */
public class HadaPhysicsEngine {
    private static HadaPhysicsEngine hadaPhysicsEngine = null;

    public static HadaPhysicsEngine getHadaPhysicsEngine() {
        if (hadaPhysicsEngine == null) hadaPhysicsEngine = new HadaPhysicsEngine();
        return hadaPhysicsEngine;
    }

    public boolean checkOverlap(PhysicsObject object1, PhysicsObject object2) {
        if (object1 instanceof PhysicsCircle) {
            if (object1.getObjectId() == object2.getObjectId()) return false;
            PhysicsCircle circle1 = (PhysicsCircle) object1;
            if (object2 instanceof PhysicsCircle) {
                PhysicsCircle circle2 = (PhysicsCircle) object2;
                float threshold = MathUtils.getDistance(circle2.getCenter(), circle1.getCenter()) - (circle2.getRadius() + circle1.getRadius());
                if (threshold < Configuration.COLLISION_THRESHOLD) return true;
            } else if (object2 instanceof PhysicsLine) {
                PhysicsLine line = (PhysicsLine) object2;
                float threshold = MathUtils.getDistance(circle1, line) - circle1.getRadius();
                if (threshold < Configuration.COLLISION_THRESHOLD) {
                    return true;
                }
            }

            return false;
        } else if (object1 instanceof PhysicsLine && object2 instanceof PhysicsCircle) {
            return checkOverlap(object2, object1);
        }

        return false;
    }

    public boolean checkCollision(PhysicsObject object1, PhysicsObject object2) throws Exception {
        if (object1 instanceof PhysicsCircle) {
            if (object1.getObjectId() == object2.getObjectId()) return false;
            PhysicsCircle circle1 = (PhysicsCircle) object1;
            if (object2 instanceof PhysicsCircle) {
                PhysicsCircle circle2 = (PhysicsCircle) object2;
                if (circle1.isStill() && circle2.isStill()) return false;
                float threshold = MathUtils.getDistance(circle2.getCenter(), circle1.getCenter()) - (circle2.getRadius() + circle1.getRadius());
                if (threshold < Configuration.COLLISION_THRESHOLD) {
                    // Check if circles get closer in next frame
                    PointF thisPositionChange = MathUtils.scalePoint(circle1.getVelocity(), Configuration.REFRESH_INTERVAL);
                    PointF objectPositionChange = MathUtils.scalePoint(circle2.getVelocity(), Configuration.REFRESH_INTERVAL);
                    PointF newThisCenter = MathUtils.addPoint(circle1.getCenter(), thisPositionChange);
                    PointF newObjectCenter = MathUtils.addPoint(circle2.getCenter(), objectPositionChange);
                    float newThreshold = MathUtils.getDistance(newObjectCenter, newThisCenter) - (circle2.getRadius() + circle1.getRadius());
                    if (newThreshold < threshold) {
                        return true;
                    }
                }
            } else if (object2 instanceof PhysicsLine) {
                if (circle1.isStill()) return false;
                PhysicsLine line2 = (PhysicsLine) object2;
                PointF positionChange = MathUtils.scalePoint(circle1.getVelocity(), Configuration.REFRESH_INTERVAL);
                PointF newThisCenter = MathUtils.addPoint(circle1.getCenter(), positionChange);

                // Detect if circle is within bounding rectangle of the line
                PointF transformedCenter = MathUtils.transformPointToAxis(circle1.getCenter(), line2);
                float lineLength = MathUtils.getDistance(line2.getStart(), line2.getEnd());
                if (transformedCenter.x > 0 && transformedCenter.x < lineLength) {
                    float threshold = Math.abs(transformedCenter.y) - circle1.getRadius();
                    if (threshold < Configuration.COLLISION_THRESHOLD) {
                        // Check if circles get closer in next frame
                        float newThreshold = MathUtils.getDistance(newThisCenter, line2) - circle1.getRadius();
                        if (newThreshold < threshold) {
                            return true;
                        }
                    }
                } else {
                    // Detect if circle is going to hit the corner
                    float threshold = MathUtils.getDistance(circle1.getCenter(), line2.getStart()) - circle1.getRadius();
                    if (threshold < Configuration.COLLISION_THRESHOLD) {
                        // Check if circles get closer in next frame
                        float newThreshold = MathUtils.getDistance(newThisCenter, line2.getStart()) - circle1.getRadius();
                        if (newThreshold < threshold) {
                            return true;
                        }
                    }

                    threshold = MathUtils.getDistance(circle1.getCenter(), line2.getEnd()) - circle1.getRadius();
                    if (threshold < Configuration.COLLISION_THRESHOLD) {
                        // Check if circles get closer in next frame
                        float newThreshold = MathUtils.getDistance(newThisCenter, line2.getEnd()) - circle1.getRadius();
                        if (newThreshold < threshold) {
                            return true;
                        }
                    }
                }
            } else {
                throw (new Exception("Collision between " + object1.getClass().toString() + " and " + object2.getClass().toString() + " could not be handled."));
            }
        } else if (object1 instanceof PhysicsLine && object2 instanceof PhysicsCircle) {
           return checkCollision(object2, object1);
        } else {
            throw (new Exception("Collision between " + object1.getClass().toString() + " and " + object2.getClass().toString() + " could not be handled."));
        }

        return false;
    }

    public void updateCollision(PhysicsObject object1, PhysicsObject object2) throws Exception {
        if (object1 instanceof PhysicsCircle) {
            PhysicsCircle circle1 = (PhysicsCircle) object1;
            if (circle1.getObjectId() == object2.getObjectId()) return;
            if (object2 instanceof PhysicsCircle) {
                PhysicsCircle circle2 = (PhysicsCircle) object2;
                updateCircleToCircleCollisionVelocity(circle1, circle2);
            } else if (object2 instanceof PhysicsLine) {
                PhysicsLine line2 = (PhysicsLine) object2;
                updateCircleToLineCollisionVelocity(circle1, line2);
            } else {
                throw (new Exception("Collision between " + object1.getClass().toString() + " and " + object2.getClass().toString() + " could not be handled."));
            }
        } else if (object1 instanceof PhysicsLine && object2 instanceof PhysicsCircle) {
            updateCollision(object2, object1);
        } else {
            throw (new Exception("Collision between " + object1.getClass().toString() + " and " + object2.getClass().toString() + " could not be handled."));
        }
    }

    private static PointF computeOneDimensionalCollisionVelocities(PointF mass, PointF velocity) {
        float v1 = velocity.x;
        float v2 = velocity.y;

        float m1 = mass.x;
        float m2 = mass.y;

        float e = Configuration.RESTITUTION;

        float V1 = ( (m1 * v1 + m2 * v2) - m2 * e * (v1 - v2) ) / (m1 + m2);
        float V2 = ( (m1 * v1 + m2 * v2) + m1 * e * (v1 - v2) ) / (m1 + m2);

        return new PointF(V1, V2);
    }

    public void updateCircleToCircleCollisionVelocity(PhysicsCircle circle1, PhysicsCircle circle2) {
        if (circle1.getCenter().x == circle2.getCenter().x) {
            // Circles are vertically aligned
            PointF finalVelocity = computeOneDimensionalCollisionVelocities(new PointF(circle1.getMass(), circle2.getMass()),
                    new PointF(circle1.getVelocity().y, circle2.getVelocity().y));
            circle1.getVelocity().y = finalVelocity.x;
            circle2.getVelocity().y = finalVelocity.y;
        } else if (circle1.getCenter().y == circle2.getCenter().y) {
            // Circles are horizontally aligned
            PointF finalVelocity = computeOneDimensionalCollisionVelocities(new PointF(circle1.getMass(), circle2.getMass()),
                    new PointF(circle1.getVelocity().x, circle2.getVelocity().x));
            circle1.getVelocity().x = finalVelocity.x;
            circle2.getVelocity().x = finalVelocity.y;
        } else {
            float sinTheta = MathUtils.getSinTheta(circle1.getCenter(), circle2.getCenter());
            float cosTheta = MathUtils.getCosTheta(circle1.getCenter(), circle2.getCenter());

            // Calculate projected velocities along the axes that go through centers of circles
            float projectedVelocityXOfA = circle1.getVelocity().x * cosTheta + circle2.getVelocity().y * sinTheta;
            float projectedVelocityYOfA = circle1.getVelocity().y * cosTheta - circle2.getVelocity().x * sinTheta;

            float projectedVelocityXOfB = circle1.getVelocity().x * cosTheta + circle2.getVelocity().y * sinTheta;
            float projectedVelocityYOfB = circle1.getVelocity().y * cosTheta - circle2.getVelocity().x * sinTheta;

            // Compute post-collision velocities
            PointF finalVelocity = computeOneDimensionalCollisionVelocities(new PointF(circle1.getMass(), circle2.getMass()),
                    new PointF(projectedVelocityXOfA, projectedVelocityXOfB));
            projectedVelocityXOfA = finalVelocity.x;
            projectedVelocityXOfB = finalVelocity.y;

            // Calculate back projected velocities to normal axis
            circle1.getVelocity().x = projectedVelocityXOfA * cosTheta - projectedVelocityYOfA * sinTheta;
            circle1.getVelocity().y = projectedVelocityYOfA * cosTheta + projectedVelocityXOfA * sinTheta;

            circle2.getVelocity().x = projectedVelocityXOfB * cosTheta - projectedVelocityYOfB * sinTheta;
            circle2.getVelocity().y = projectedVelocityYOfB * cosTheta + projectedVelocityXOfB * sinTheta;

            // Also update circle position so that circle does not sink through the line
            // Update should be proportional to their speed
            float threshold = MathUtils.getDistance(circle1.getCenter(), circle2.getCenter()) - (circle1.getRadius() + circle2.getRadius());
            if (threshold < 0) {
                float circle1Speed = MathUtils.getAbsolute(circle1.getVelocity());
                float circle2Speed = MathUtils.getAbsolute(circle2.getVelocity());
                DrawableLine drawableLine = HadaGraphicsEngine.getHadaGraphicsEngine().constructDrawableLine(circle1.getCenter(), circle2.getCenter());
                PhysicsLine line = (PhysicsLine)ObjectMapper.getObjectMapper().getPhysicsObjectFromDrawableObject(drawableLine);
                PointF circle1TransformedCenter = MathUtils.transformPointToAxis(circle1.getCenter(), line);
                PointF circle2TransformedCenter = MathUtils.transformPointToAxis(circle2.getCenter(), line);
                float circle1Shift = (circle1Speed / (circle1Speed + circle2Speed)) * threshold; // threshold is -ve, so shift is -ve
                float circle2Shift = (circle2Speed / (circle1Speed + circle2Speed)) * threshold * -1; // threshold is -ve, so shift is +ve

                circle1TransformedCenter.x += circle1Shift;
                circle2TransformedCenter.x += circle2Shift;
                PointF circle1BackTransformedCenter = MathUtils.transformPointFromAxis(circle1TransformedCenter, line);
                PointF circle2BackTransformedCenter = MathUtils.transformPointFromAxis(circle2TransformedCenter, line);
                circle1.setCenter(circle1BackTransformedCenter);
                circle2.setCenter(circle2BackTransformedCenter);
            }
        }
    }

    public void updateCircleToLineCollisionVelocity(PhysicsCircle circle, PhysicsLine line) {
        // Detect if circle is within bounding rectangle of the line
        PointF transformedCenter = MathUtils.transformPointToAxis(circle.getCenter(), line);
        float lineLength = MathUtils.getDistance(line.getStart(), line.getEnd());
        if (transformedCenter.x > 0 && transformedCenter.x < lineLength) {
            float sinTheta = MathUtils.getSinTheta(line.getStart(), line.getEnd());
            float cosTheta = MathUtils.getCosTheta(line.getStart(), line.getEnd());

            // Calculate projected velocities with the collision tangent as x axis
            float projectedVelocityX = circle.getVelocity().x * cosTheta + circle.getVelocity().y * sinTheta;
            float projectedVelocityY = circle.getVelocity().y * cosTheta - circle.getVelocity().x * sinTheta;

            // Compute post-collision velocities (Note that velocity along projected Y axis will be inverted)
            projectedVelocityY = -1F * Configuration.RESTITUTION * projectedVelocityY;

            // Calculate back projected velocities to normal axis
            circle.getVelocity().x = projectedVelocityX * cosTheta - projectedVelocityY * sinTheta;
            circle.getVelocity().y = projectedVelocityY * cosTheta + projectedVelocityX * sinTheta;

            // Also update circle position so that circle does not sink through the line
            if (Math.abs(transformedCenter.y) < circle.getRadius()) {
                transformedCenter.y = transformedCenter.y > 0 ? circle.getRadius() : -circle.getRadius();
                PointF backTransformedCenter = MathUtils.transformPointFromAxis(transformedCenter, line);
                circle.setCenter(backTransformedCenter);
            }
        } else {
            // In this case, circle is colliding with the end point of line. Treat it as a collision with line
            // orthogonal to line joining center of circle and end point of line.

            // If circle is going to hit the starting corner
            float threshold = MathUtils.getDistance(circle.getCenter(), line.getStart()) - circle.getRadius();
            if (threshold < Configuration.COLLISION_THRESHOLD) {
                float sinTheta = MathUtils.getSinTheta(circle.getCenter(), line.getStart());
                float cosTheta = MathUtils.getCosTheta(circle.getCenter(), line.getStart());

                // Calculate projected velocities with the line joining circle center and endpoint as x axis
                float projectedVelocityX = circle.getVelocity().x * cosTheta + circle.getVelocity().y * sinTheta;
                float projectedVelocityY = circle.getVelocity().y * cosTheta - circle.getVelocity().x * sinTheta;

                // Compute post-collision velocities (Note that velocity along projected X axis will be inverted)
                projectedVelocityX = -1F * Configuration.RESTITUTION * projectedVelocityX;

                // Calculate back projected velocities to normal axis
                circle.getVelocity().x = projectedVelocityX * cosTheta - projectedVelocityY * sinTheta;
                circle.getVelocity().y = projectedVelocityY * cosTheta + projectedVelocityX * sinTheta;

                // Also update circle position so that circle does not sink through the line
                if (threshold < 0) {
                    DrawableLine drawableLine = HadaGraphicsEngine.getHadaGraphicsEngine().constructDrawableLine(line.getStart(), circle.getCenter());
                    PhysicsLine line1 = (PhysicsLine)ObjectMapper.getObjectMapper().getPhysicsObjectFromDrawableObject(drawableLine);
                    PointF transformedCenter1 = MathUtils.transformPointToAxis(circle.getCenter(), line1);
                    transformedCenter1.x = circle.getRadius();  // transformedCenter.y should be approximately 0;
                    PointF backTransformedCenter = MathUtils.transformPointFromAxis(transformedCenter1, line1);
                    circle.setCenter(backTransformedCenter);
                }
            }

            // If circle is going to hit the ending corner
            threshold = MathUtils.getDistance(circle.getCenter(), line.getEnd()) - circle.getRadius();
            if (threshold < Configuration.COLLISION_THRESHOLD) {
                float sinTheta = MathUtils.getSinTheta(circle.getCenter(), line.getEnd());
                float cosTheta = MathUtils.getCosTheta(circle.getCenter(), line.getEnd());

                // Calculate projected velocities with the line joining circle center and endpoint as x axis
                float projectedVelocityX = circle.getVelocity().x * cosTheta + circle.getVelocity().y * sinTheta;
                float projectedVelocityY = circle.getVelocity().y * cosTheta - circle.getVelocity().x * sinTheta;

                // Compute post-collision velocities (Note that velocity along projected X axis will be inverted)
                projectedVelocityX = -1F * Configuration.RESTITUTION * projectedVelocityX;

                // Calculate back projected velocities to normal axis
                circle.getVelocity().x = projectedVelocityX * cosTheta - projectedVelocityY * sinTheta;
                circle.getVelocity().y = projectedVelocityY * cosTheta + projectedVelocityX * sinTheta;

                // Also update circle position so that circle does not sink through the line
                if (threshold < 0) {
                    DrawableLine drawableLine = HadaGraphicsEngine.getHadaGraphicsEngine().constructDrawableLine(line.getEnd(), circle.getCenter());
                    PhysicsLine line1 = (PhysicsLine)ObjectMapper.getObjectMapper().getPhysicsObjectFromDrawableObject(drawableLine);
                    PointF transformedCenter1 = MathUtils.transformPointToAxis(circle.getCenter(), line1);
                    transformedCenter1.x = circle.getRadius();  // transformedCenter.y should be approximately 0;
                    PointF backTransformedCenter = MathUtils.transformPointFromAxis(transformedCenter1, line1);
                    circle.setCenter(backTransformedCenter);
                }
            }
        }
    }
}
