package model;

import vehicle.VehiclePose;

import java.util.Random;

/**
 *
 * Created by Yang ZHANG on 2014/10/27.
 */
public interface ControlModel {
    public VehiclePose nextPose(Random rand, VehiclePose pose, ActionModel action);
}
