package model;

/**
 *
 * Created by Yang ZHANG on 2014/10/27.
 */
public class SimpleActionModel implements ActionModel {

    private final double vx;
    private final double vy;
    private final double omega;
    private final double dt;

    public SimpleActionModel(double vx, double vy, double omega, double dt) {
        this.vx = vx;
        this.vy = vy;
        while (omega > Math.PI * dt) {
            omega -= Math.PI * dt;
        }
        while (omega < -Math.PI * dt) {
            omega += Math.PI * dt;
        }
        this.omega = omega;
        this.dt = dt;
    }

    @Override
    public double getX() {
        return vx;
    }

    @Override
    public double getY() {
        return vy;
    }

    @Override
    public double getNormalAngle() {
        return omega;
    }

    @Override
    public long getTimeStamp() {
        return 0;
    }

    @Override
    public double getElapsedTime() {
        return 0;
    }

    @Override
    public ActionModel nextAction() {
        return null;
    }
}
