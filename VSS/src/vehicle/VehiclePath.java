package vehicle;

import model.SimpleActionModel;
import util.Constants;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * Created by Yang ZHANG on 2014/10/26.
 */
public class VehiclePath {

    private VehiclePose current;
    private VehiclePath previous;
    private double error;

    private final static double PREV_ERROR_RATIO = 0.9;

    public VehiclePath(VehiclePose current) {
        this(current, null);
    }

    public VehiclePath(VehiclePose current, VehiclePath previous) {
        this.current = current;
        this.previous = previous;
    }

    public double getError() {
        return error;
    }

    public VehiclePose getLatestPose() {
        return current;
    }

    public VehiclePath getPreviousPath() {
        return previous;
    }

    public SimpleActionModel getPreviousAction() {
        if (previous == null) {
            return new SimpleActionModel(0, 0, 0, 0);
        } else if (current == null || previous.current == null) {
            double dt = 0; // TODO: dt应由地标获得
            return new SimpleActionModel(0, 0, 0, dt);
        } else {
            double dt = 0; // TODO: 同上
            return current.getActionFrom(previous.getLatestPose(), dt);
        }
    }

    public final VehiclePose[] getPoseArray() {
        ArrayList<VehiclePose> poseArrayList = new ArrayList<>();
        VehiclePath current = this;
        while (current != null) {
            poseArrayList.add(current.getLatestPose());
            current = current.previous;
        }
        VehiclePose[] r = new VehiclePose[poseArrayList.size()];
        poseArrayList.toArray(r);
        return r;
    }

    public void setError(double error) {
        if (previous != null) {
            this.error = error + PREV_ERROR_RATIO * previous.getError();
        } else {
            this.error = error;
        }
    }

    public void paint(Graphics2D g) {
        g.setColor(new Color(0f, 0f, 0f, 1f));
        current.paint(g, Constants.scaleDown);
    }

    public void paintPath(Graphics2D g, int length, float decay) {
        Point2D last = current.getLocation();
        VehiclePath next = previous;
        float alpha = 1f;
        Stroke oldStroke = g.getStroke();
        g.setStroke(Constants.basicStroke);
        for (int i = 0; i < length; i++) {
            if (next != null) {
                alpha *= decay;
                g.setColor(new Color(0f, 0f, 0f, alpha));
                g.draw(new Line2D.Double(last, next.current.getLocation()));
                last = next.current.getLocation();
                next = next.previous;
            }
        }
        g.setStroke(oldStroke);
    }

    public Iterator iterator() {
        return new VehiclePathIterator(this);
    }

    public void adjustPose(double dx, double dy, double dtheta) {
        current = new VehiclePose(current.getX() + dx, current.getY() + dy, current.getTheta() + dtheta);
    }
}
