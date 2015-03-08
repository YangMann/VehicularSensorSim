package vehicle;

import model.ActionModel;
import model.SimpleActionModel;
import util.Constants;
import util.RandomSingleton;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.NumberFormat;
import java.util.Random;

/**
 *
 * Created by Yang ZHANG on 2014/10/26.
 */
public final class VehiclePose extends Point2D implements Cloneable {

    private static final double PI2 = 2 * Math.PI;
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();
    static {
        NUMBER_FORMAT.setGroupingUsed(false);
        NUMBER_FORMAT.setMaximumFractionDigits(3);
        NUMBER_FORMAT.setMinimumFractionDigits(3);
    }

    private double x;
    private double y;
    private double theta;
    private Color color = Color.BLACK;

    public VehiclePose(VehiclePose copy) {
        this(copy.getX(), copy.getY(), copy.getTheta());
    }

    public VehiclePose(double x, double y, double theta) {
        this.x = x;
        this.y = y;
        while (theta > Math.PI) {
            theta -= PI2;
        }
        while (theta < -Math.PI) {
            theta += PI2;
        }
        this.theta = theta;
    }

    public VehiclePose(VehiclePose pose, ActionModel action) {
        this.x = pose.getX() + action.getX();
        this.y = pose.getY() + getY(); // TODO: 不知道是不是要写成 action.getY()
        this.theta = pose.getTheta() + getTheta(); // TODO: 同上
    }

    public AffineTransform getTransform() {
        AffineTransform transform = new AffineTransform();
        transform.translate(x, y);
//        transform.rotate(theta); // TODO: 试试看
        return transform;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    public double getTheta() {
        return theta;
    }

    public Point2D getLocation() {
        return new Double(x, y);
    }

    public SimpleActionModel getActionFrom(VehiclePose previous, double dt) {
        // car actions are in the coordinate space of the previous pose
        // +x is the front of the car
        // +y is the left of the car
        double dtheta = this.getTheta() - previous.getTheta();
        while (dtheta > Math.PI) {
            dtheta -= 2 * Math.PI;
        }
        while (dtheta < -Math.PI) {
            dtheta += 2 * Math.PI;
        }
        double c = Math.cos(previous.getTheta());
        double s = Math.sin(previous.getTheta());
        double dx = this.getX() - previous.getX();
        double dy = this.getY() - previous.getY();

        return new SimpleActionModel(c * dx + s * dy, s * dx - c * dy, dtheta, dt);
    }

    public VehiclePose getNoisyPose(double varX, double varY, double varTheta) {
        Random r = RandomSingleton.instance;
        return new VehiclePose(x + varX * r.nextGaussian(), y + varY * r.nextGaussian(), theta + varTheta * r.nextGaussian());
    }

    @Override
    public void setLocation(double x, double y) {
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public Object clone() {
        super.clone();
        return new VehiclePose(this);
    }

    public void paint(Graphics2D g, double scale) {
        AffineTransform transform = getTransform();
        AffineTransform lastTransform = g.getTransform();
        g.transform(transform);
//        g.translate(x, y); // TODO: 试试看
//        g.rotate(theta);
        g.setColor(color);
        double c = Math.cos(theta);
        double s = Math.sin(theta);
        Shape s1 = new Line2D.Double(-5 * c, -5 * s, +8 * c, +8 * s);
        Shape s2 = new Line2D.Double(-5 * s, +5 * c, +5 * s, -5 * c);
        g.scale(scale, scale);
        g.draw(s1);
        g.draw(s2);
        System.out.println("Drawing car at " + theta);
        g.setTransform(lastTransform);
    }

    public void paintTiny(Graphics2D g) {
        AffineTransform transform = getTransform();
        AffineTransform lastTransform = g.getTransform();
        g.transform(transform);
        g.setColor(color);
        double c = Math.cos(theta);
        double s = Math.sin(theta);
        Shape s1 = new Line2D.Double(-5 * c, -5 * s, +8 * c, +8 * s);
        Shape s2 = new Line2D.Double(-5 * s, +5 * c, +5 * s, -5 * c);
        g.scale(Constants.scaleDown / 2, Constants.scaleDown /2);
        g.draw(s1);
        g.draw(s2);
        g.setTransform(lastTransform);
    }

    @Override
    public String toString() {
        return "(" + NUMBER_FORMAT.format(this.getX()) + ", " + NUMBER_FORMAT.format(this.getY()) + ", " + this.getTheta() + ")";
    }
}
