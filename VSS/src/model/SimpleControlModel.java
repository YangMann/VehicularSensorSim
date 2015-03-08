package model;

import vehicle.VehiclePose;

import java.util.Random;

/**
 *
 * Created by Yang ZHANG on 2014/10/27.
 */
public class SimpleControlModel implements ControlModel {

    private double varX = 20;
    private double varY = 60;
    private double varTheta = 1.0;

    public SimpleControlModel() {}

    public SimpleControlModel(double varX, double varY, double varTheta) {
        this.varX = varX;
        this.varY = varY;
        this.varTheta = varTheta;
    }

    @Override
    public VehiclePose nextPose(Random rand, VehiclePose pose, ActionModel action) {
        return nextPose(pose, action, rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian());
    }

    public VehiclePose nextPose(VehiclePose pose, ActionModel action, double f1, double f2, double f3) {
        double dt = action.getElapsedTime();
        double dx = dt * varX * f1 + action.getX();
        double dy = dt * varY * f2 + action.getY();
        double dtheta = dt * varTheta * f3;

        double s = Math.sin(pose.getTheta());
        double c = Math.cos(pose.getTheta());

        return new VehiclePose(dx * c + dy * s + pose.getX(),
                dx * s + dy * c + pose.getY(),
                dtheta + pose.getTheta() + action.getNormalAngle());
    }
}
