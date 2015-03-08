package sim;

import model.ControlModel;
import model.SimpleControlModel;
import util.Constants;
import util.RandomSingleton;
import vehicle.VehiclePath;
import vehicle.VehiclePose;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * Created by Yang ZHANG on 2014/10/27.
 */
public class CarDrivingSim {

    private static final Random random = RandomSingleton.instance;
    private final Timer timer = new Timer(50, new TimerListener());
    private VehiclePose car;
    private ControlModel control = new SimpleControlModel(0, 0, 0);

    private static final double MAX_VX = 100;
    private static final double MAX_VY = 0;
    private static final double MAX_OMEGA = 0.4;
    private static final double TO_DEGREE = 180.0 / Math.PI;

    private double accX = 0.5; // TODO: 目前是加速度变化率
    private double accY = 0.5;
    private double accOmega = 0.9;
    private double updatePeriod = 0.2;

    private double vX = 0;
    private double vY = 0;
    private double omega = 0;

    private boolean accelerate = false;
    private boolean turnLeft = false;
    private boolean turnRight = false;
    private double time = 0;
    private VehiclePath path = null;

    ActionListener listener = null;
    private ArrayList<ActionListener> listeners = new ArrayList<>();

    public CarDrivingSim() { // TODO: 道路类
        this.car = new VehiclePose(0, 0, 0);
        this.timer.setCoalesce(true);
    }

    public void setUpdate(int delay) {
        timer.setDelay(delay);
    }

    public VehiclePose getPose() {
        return car;
    }

    public void run() {
        timer.restart();
    }

    public void pause() {
        timer.stop();
    }

    public boolean isRunning() {
        return timer.isRunning();
    }

    public void accelerate() {
        accelerate = true;
    }

    public void decelerate() {
        accelerate = false;
    }

    public void turnLeft() {
        turnLeft = true;
        turnRight = false;
    }

    public void turnRight() {
        turnLeft = false;
        turnRight = true;
    }

    public void goStraight() {
        turnLeft = false;
        turnRight = false;
    }

    public void paint(Graphics2D g) {
        car.paint(g, Constants.scaleDown);

        g.setColor(Color.black);
        int size = 5000;
        g.drawOval((int)(car.getX() - size / 2), (int)(car.getY() - size / 2), size, size);

        AffineTransform transform = new AffineTransform();
        transform.translate(car.getX(), car.getY());
        transform.rotate(car.getTheta());
    }

    private class TimerListener implements ActionListener {
        @Override
        public synchronized void actionPerformed(ActionEvent e) {
            simpleUpdate(updatePeriod);
            path = new VehiclePath(car, path);
            fireActionEvent();
        }
    }

    private synchronized void simpleUpdate(double dt) {
        updateVx(dt);
        updateOmega(dt);

        double c = Math.cos(car.getTheta());
        double s = Math.sin(car.getTheta());
        double newX = car.getX() + dt * c * vX + dt * s * vY;
        double newY = car.getY() + dt * s * vX + dt * c * vY;
        double newOmega = car.getTheta() + dt * omega;
        car = new VehiclePose(newX, newY, newOmega);
        time += dt;
    }

    private void updateVx(double dt) {
        double rate = dt * accX;
        if (accelerate) {
            vX = (1.0 - rate) * vX + rate * MAX_VX;
        } else {
            vX = (1.0 - rate) * vX;
        }
    }

    private void updateOmega(double dt) {
        double rate = dt * accOmega;
        if (turnLeft) {
            omega = (1 - rate) * omega + rate * MAX_OMEGA;
        } else if (turnRight) {
            omega = (1 - rate) * omega - rate * MAX_OMEGA;
        } else {
            omega = (1 - rate) * omega;
        }
    }

    public void fireActionEvent() {
        ActionEvent event = new ActionEvent(this, 0, "timestep");
        ActionListener[] l = listeners.toArray(new ActionListener[listeners.size()]);
        for (ActionListener aL : l) {
            aL.actionPerformed(event);
        }
    }

    public void addActionListener(ActionListener l) {
        listeners.add(l);
    }

    public void removeActionListener(ActionListener l) {
        listeners.remove(l);
    }
}
