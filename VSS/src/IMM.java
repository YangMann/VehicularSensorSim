import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import util.RandomSingleton;
import util.jama.Matrix;
import util.jkalman.JKalman;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Yang ZHANG on 2014/12/3.
 */
public class IMM {

    private DataProcessor dp;
    private int length;
    private int step;
    private JKalman kalman;
    private JKalman kalman1;
    private ArrayList<Double> ACC_X;
    private ArrayList<Double> ACC_Y;
    private ArrayList<Double> ACC_Z;
    private ArrayList<Double> DIR_X;
    private ArrayList<Double> DIR_Y;
    private ArrayList<Double> DIR_Z;
    private ArrayList<Double> GRO_X;
    private ArrayList<Double> GRO_Y;
    private ArrayList<Double> GRO_Z;
    private ArrayList<HashMap<String, Double>> GPS;
    private ArrayList<HashMap<String, Double>> GPS_CHANGE;

    private Random random;

    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_VELOCITY = "velocity";
    private static final String KEY_DIRECTION = "direction";

    public IMM() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
            dp = new DataProcessor(fileChooser.getSelectedFile().getAbsolutePath());
            dp.readFiles();
        }
        ACC_X = new ArrayList<>(dp.getACC_X());
        ACC_Y = new ArrayList<>(dp.getACC_Y());
        ACC_Z = new ArrayList<>(dp.getACC_Z());
        DIR_X = new ArrayList<>(dp.getDIR_X());
        DIR_Y = new ArrayList<>(dp.getDIR_Y());
        DIR_Z = new ArrayList<>(dp.getDIR_Z());
        GRO_X = new ArrayList<>(dp.getGRO_X());
        GRO_Y = new ArrayList<>(dp.getGRO_Y());
        GRO_Z = new ArrayList<>(dp.getGRO_Z());
        GPS = new ArrayList<>(dp.getGPS());
        GPS_CHANGE = new ArrayList<>(dp.getGPS_CHANGE());


        length = GPS.size();
        int sensorLength = Math.min(Math.min(ACC_X.size(), DIR_X.size()), GRO_X.size());
        step = sensorLength / length;
        random = RandomSingleton.instance;

        try {
            kalman = new JKalman(7, 7);
            kalman1 = new JKalman(7, 7);
/*            double[][] tr = {
                    {1, 0, 1, 0, 0.5, 0, 0},
                    {0, 1, 0, 1, 0, 0.5, 0},
                    {0, 0, 1, 0, 1, 0, 0},
                    {0, 0, 0, 1, 0, 1, 0},
                    {0, 0, 0, 0, 1, 0, 0},
                    {0, 0, 0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 1}
            };*/
            double[][] tr = {
                    {1, 0, 1, 0, 0.5, 0, 0},
                    {0, 1, 0, 1, 0, 0.5, 0},
                    {0, 0, 1, 0, 1, 0, 0},
                    {0, 0, 0, 1, 0, 1, 0},
                    {0, 0, 0, 0, 1, 0, 0},
                    {0, 0, 0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0}
            };
            double[][] tr1 = {
                    {0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 1, 0, 1, 0, 0},
                    {0, 0, 0, 1, 0, 1, 0},
                    {0, 0, 0, 0, 1, 0, 0},
                    {0, 0, 0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0}
            };
            kalman.setTransition_matrix(new Matrix(tr));
            kalman.setError_cov_post(kalman.getError_cov_post().identity());
            kalman1.setTransition_matrix(new Matrix(tr1));
            kalman1.setError_cov_post(kalman1.getError_cov_post().identity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fill() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Matrix m = new Matrix(7, 1);
        Matrix s, c;
        double[] a = new double[length];
        double[] b = new double[length];
        double[] V = new double[length];
        double v, angle;
        for (int i = 0; i < length; i++) {
            v = GPS.get(i).get(KEY_VELOCITY);
            angle = GPS.get(i).get(KEY_DIRECTION);
            m.set(0, 0, GPS.get(i).get(KEY_LONGITUDE) + random.nextGaussian() / 10000);
            m.set(1, 0, GPS.get(i).get(KEY_LATITUDE) + random.nextGaussian() / 10000);
            m.set(2, 0, v * Math.cos(angle) / 110540);
            m.set(3, 0, v * Math.sin(angle) / 110320 / Math.cos(GPS.get(i).get(KEY_LATITUDE)));
            m.set(4, 0, ACC_X.get(i * step) / 110540);
            m.set(5, 0, ACC_Y.get(i * step) / 110320 / Math.cos(GPS.get(i).get(KEY_LATITUDE)));
            m.set(6, 0, angle);
//            System.out.println(m.toString());
            s = kalman.Predict();
            c = kalman.Correct(m);
//            dataset.addValue(m.get(2, 0), "raw data", "" + i);
//            dataset.addValue(c.get(2, 0), "sensor-fusioned data", "" + i);
            m.set(0, 0, GPS.get(i).get(KEY_LONGITUDE) + random.nextGaussian() / 10000);
            m.set(1, 0, GPS.get(i).get(KEY_LATITUDE) + random.nextGaussian() / 10000);
            m.set(2, 0, v * Math.cos(angle));
            m.set(3, 0, v * Math.sin(angle));
            m.set(4, 0, ACC_X.get(i * step));
            m.set(5, 0, ACC_Y.get(i * step));
            m.set(6, 0, angle);
            s = kalman1.Predict();
            c = kalman1.Correct(m);
            V[i] = Math.sqrt((c.get(2, 0) * c.get(2, 0)) + (c.get(3, 0) * c.get(3, 0)));
            a[i] = f(V[i], 6);
            b[i] = 1 - a[i];
//            dataset.addValue(m.get(3, 0), "raw data", "" + i);
//            dataset.addValue(c.get(3, 0), "sensor-fusioned data", "" + i);
            dataset.addValue(V[i], "V", "" + i);
            dataset.addValue(a[i], "a", "" + i);
            dataset.addValue(b[i], "b", "" + i);
        }
        for (int i = 0; i < dataset.getColumnCount(); i++) {
            System.out.print(dataset.getValue(1, i));
            System.out.print(" ");
        }
        System.out.println();
        for (int i = 0; i < dataset.getColumnCount(); i++) {
            System.out.print(dataset.getValue(2, i));
            System.out.print(" ");
        }
        System.out.println();

        LineChartFrame lineChartFrame = new LineChartFrame("", dataset);
        lineChartFrame.pack();
        RefineryUtilities.centerFrameOnScreen(lineChartFrame);
        lineChartFrame.setVisible(true);
    }

    private double f(double v, double T) {
        return 1 / (1 + Math.exp(-v + T)) - 1 / (1 + Math.exp(T));
    }

    private void vfill() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        double[] m = new double[length];
        double[] ac = new double[length];
        double[] a = new double[length];
        double[] b = new double[length];
        double ratio = 0.2;
        for (int i = 0; i < length / 4; i++) {
            ac[i] = 2.0;
            m[i] = 2.0 + random.nextGaussian() * ratio;
            a[i] = f(m[i], 10);
            b[i] = 1 - a[i];
            dataset.addValue(m[i], "actual", "" + i);
            dataset.addValue(a[i], "a", "" + i);
            dataset.addValue(b[i], "b", "" + i);
        }
        for (int i = length / 4; i < 2 * length / 4; i++) {
            ac[i] = 2 + 18.0 * 4 / length * (i - length / 4);
            m[i] = 2 + 18.0 * 4 / length * (i - length / 4) + random.nextGaussian() * ratio;
            a[i] = f(m[i], 10);
            b[i] = 1 - a[i];
            dataset.addValue(m[i], "actual", "" + i);
            dataset.addValue(a[i], "a", "" + i);
            dataset.addValue(b[i], "b", "" + i);
        }
        for (int i = 2 * length / 4; i < 6 * length / 10; i++) {
            ac[i] = 20;
            m[i] = 20.0 + random.nextGaussian() * ratio;
            a[i] = f(m[i], 10);
            b[i] = 1 - a[i];
            dataset.addValue(m[i], "actual", "" + i);
            dataset.addValue(a[i], "a", "" + i);
            dataset.addValue(b[i], "b", "" + i);
        }
        for (int i = 6 * length / 10; i < 6 * length / 10 + length / 4; i++) {
            ac[i] = 20;
            m[i] = 20 - 18.0 * 4 / length * (i - 6 * length / 10) + random.nextGaussian() * ratio;
            a[i] = f(m[i], 10);
            b[i] = 1 - a[i];
            dataset.addValue(m[i], "actual", "" + i);
            dataset.addValue(a[i], "a", "" + i);
            dataset.addValue(b[i], "b", "" + i);
        }
        for (int i = 6 * length / 10 + length / 4; i <length; i++) {
            ac[i] = 2;
            m[i] = 2 + random.nextGaussian() * ratio;
            a[i] = f(m[i], 10);
            b[i] = 1 - a[i];
            dataset.addValue(m[i], "actual", "" + i);
            dataset.addValue(a[i], "a", "" + i);
            dataset.addValue(b[i], "b", "" + i);
        }
        for (int i = length / 4; i < 9 * length / 20 + length / 4; i++) {
            ac[i] = 2 + 18.0 / (9 * length / 20) * (i - length / 4);
            m[i] = 2 + 18.0 / (9 * length / 20) * (i - length / 4) + random.nextGaussian() * ratio;
            a[i] = f(m[i], 10);
            b[i] = 1 - a[i];
            dataset.setValue(m[i], "actual", "" + i);
        }
        for (int i = 9 * length / 20 + length / 4; i < length; i++) {
            ac[i] = 20;
            m[i] = 20 + random.nextGaussian() * ratio;
            dataset.setValue(m[i], "actual", "" + i);
        }
        for (int i = length / 4; i < 13 * length / 20; i++) {
            ac[i] = 2 + 18.0 / (13 * length / 20 - length / 4) * (i - length / 4);
            m[i] = ac[i] + random.nextGaussian() * ratio;
            dataset.setValue(m[i], "actual", "" + i);
        }
        for (int i = 13 * length / 20; i < length; i++) {
            ac[i] = 20.0;
            m[i] = ac[i] + random.nextGaussian() * ratio;
            dataset.addValue(m[i], "actual", "" + i);
        }


        for (int i = 0; i < dataset.getColumnCount(); i++) {
            System.out.print(dataset.getValue(0, i));
            System.out.print(" ");
        }
        System.out.println();
        for (int i = 0; i < dataset.getColumnCount(); i++) {
            System.out.print(ac[i]);
            System.out.print(" ");
        }
        LineChartFrame lineChartFrame = new LineChartFrame("", dataset);
        lineChartFrame.pack();
        RefineryUtilities.centerFrameOnScreen(lineChartFrame);
        lineChartFrame.setVisible(true);
    }

    public static void main(String[] args) {
        IMM imm = new IMM();
        imm.vfill();
    }

    private class LineChartFrame extends ApplicationFrame {

        public LineChartFrame(String title, CategoryDataset dataset) {
            super(title);
            JFreeChart lineChart = ChartFactory.createLineChart(
                    "TEST",
                    "time", "",
                    dataset, PlotOrientation.VERTICAL, true, true, false
            );
            ChartPanel chartPanel = new ChartPanel(lineChart);
            chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
            setContentPane(chartPanel);
        }
    }

}
