package model;

/**
 *
 * Created by Yang ZHANG on 2014/10/26.
 */
public interface ActionModel {

    public double getX();
    public double getY();
    public double getNormalAngle();
    public long getTimeStamp();
    public double getElapsedTime();
    public ActionModel nextAction();

}
