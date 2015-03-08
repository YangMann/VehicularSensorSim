import sim.CarDrivingSim;
import util.Constants;
import util.RandomSingleton;
import vehicle.VehiclePose;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.util.Random;

/**
 *
 * Created by Yang ZHANG on 2014/10/27.
 */
public class CarDrivingSimPanel extends JPanel {

    private CarDrivingSim sim;

    public KeyController keyListener = new KeyController();

    public CarDrivingSimPanel() {
        sim = new CarDrivingSim();
        setOpaque(true);
        setBackground(Color.white);
        setFocusable(true);
        addKeyListener(keyListener);

        sim.addActionListener(e -> repaint());

        setFocusable(true);
    }

    private class KeyController implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            switch (e.getKeyChar()) {
                case KeyEvent.VK_SPACE: {
                    if (sim.isRunning()) {
                        System.out.println("pause");
                        sim.pause();
                    } else {
                        System.out.println("run");
                        sim.run();
                    }
                } break;
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    sim.accelerate();
                    break;
                case KeyEvent.VK_LEFT:
                    sim.turnLeft();
                    break;
                case KeyEvent.VK_RIGHT:
                    sim.turnRight();
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    sim.decelerate();
                    break;
                case KeyEvent.VK_LEFT:
                    sim.goStraight();
                    break;
                case KeyEvent.VK_RIGHT:
                    sim.goStraight();
                    break;
            }
        }
    }

    protected CarDrivingSim getSim() {
        return sim;
    }

    public static double getPointSample(Random rand, double min, double max) {
        double delta = max - min;
        return delta * rand.nextDouble() + min;
    }

    public void addActionListener(ActionListener l) {
        sim.addActionListener(l);
    }

    public void removeActionListener(ActionListener l) {
        sim.removeActionListener(l);
    }

    public void cleanup() {
        sim.pause();
        sim = null;
    }

    @Override
    public synchronized void paint(Graphics g) {
        synchronized (sim) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g;
            AffineTransform lastTransform = setupTransform(g2);
            sim.paint(g2);
            g2.setTransform(lastTransform);
        }
    }

    protected AffineTransform setupTransform(Graphics2D g2) {
        AffineTransform lastTransform = g2.getTransform();
        VehiclePose car = sim.getPose();
        double xOffset = car.getX() - getWidth() / 2;
        double yOffset = car.getY() - getHeight() / 2;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(getWidth() / 2, getHeight() / 2);
        g2.scale(1.0 / Constants.scaleDown, -1.0 / Constants.scaleDown);
        g2.translate(-xOffset, -yOffset);

        return lastTransform;
    }

    public static void main(String[] args) {
        Random rand = RandomSingleton.instance;
        JFrame f = new JFrame("Car driving simulation");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(new CarDrivingSimPanel());
        f.setSize(500, 500);
        f.setVisible(true);
    }

}
