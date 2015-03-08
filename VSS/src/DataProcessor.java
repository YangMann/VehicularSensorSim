import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * Created by Yang ZHANG on 2014/11/26.
 */
public class DataProcessor {

    private String dir = null;
    private ArrayList<Double> ACC_X = new ArrayList<>();
    private ArrayList<Double> ACC_Y = new ArrayList<>();
    private ArrayList<Double> ACC_Z = new ArrayList<>();
    private ArrayList<Double> DIR_X = new ArrayList<>();
    private ArrayList<Double> DIR_Y = new ArrayList<>();
    private ArrayList<Double> DIR_Z = new ArrayList<>();
    private ArrayList<Double> GRO_X = new ArrayList<>();
    private ArrayList<Double> GRO_Y = new ArrayList<>();
    private ArrayList<Double> GRO_Z = new ArrayList<>();
    private ArrayList<HashMap<String, Double>> GPS = new ArrayList<>();
    private ArrayList<HashMap<String, Double>> GPS_CHANGE = new ArrayList<>();

    public ArrayList<Double> getACC_X() {
        return ACC_X;
    }

    public ArrayList<Double> getACC_Y() {
        return ACC_Y;
    }

    public ArrayList<Double> getACC_Z() {
        return ACC_Z;
    }

    public ArrayList<Double> getDIR_X() {
        return DIR_X;
    }

    public ArrayList<Double> getDIR_Y() {
        return DIR_Y;
    }

    public ArrayList<Double> getDIR_Z() {
        return DIR_Z;
    }

    public ArrayList<Double> getGRO_X() {
        return GRO_X;
    }

    public ArrayList<Double> getGRO_Y() {
        return GRO_Y;
    }

    public ArrayList<Double> getGRO_Z() {
        return GRO_Z;
    }

    public ArrayList<HashMap<String, Double>> getGPS() {
        return GPS;
    }

    public ArrayList<HashMap<String, Double>> getGPS_CHANGE() {
        return GPS_CHANGE;
    }

    public DataProcessor(String dir) {
        this.dir = dir;
    }

    private void readFile(File f, ArrayList<Double> a) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String line = reader.readLine();
            String[] raw = line.split(" ");
            for (String data : raw) {
                a.add(Double.parseDouble(data));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readGPSFile(File f, ArrayList<HashMap<String, Double>> a) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String line;
            HashMap<String, Double> map;
            while ((line = reader.readLine()) != null) {
                String[] raw = line.split(" ");
//                System.out.println(Arrays.toString(raw));
                map = new HashMap<>();
                map.put("longitude", Double.parseDouble(raw[0]));
                map.put("latitude", Double.parseDouble(raw[1]));
                map.put("altitude", Double.parseDouble(raw[3]));
                map.put("velocity", Double.parseDouble(raw[5]));
                map.put("direction", Double.parseDouble(raw[7]));
                a.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFiles() {
        if (dir == null) {
            System.out.println("dir == null");
        } else {
            File[] files = (new File(dir)).listFiles();
            assert files != null;
            for (File f : files) {
                switch (f.getName()) {
                    case "ACC_X.txt":
                        readFile(f, ACC_X);
                        break;
                    case "ACC_Y.txt":
                        readFile(f, ACC_Y);
                        break;
                    case "ACC_Z.txt":
                        readFile(f, ACC_Z);
                        break;
                    case "DIR_X.txt":
                        readFile(f, DIR_X);
                        break;
                    case "DIR_Y.txt":
                        readFile(f, DIR_Y);
                        break;
                    case "DIR_Z.txt":
                        readFile(f, DIR_Z);
                        break;
                    case "GRO_X.txt":
                        readFile(f, GRO_X);
                        break;
                    case "GRO_Y.txt":
                        readFile(f, GRO_Y);
                        break;
                    case "GRO_Z.txt":
                        readFile(f, GRO_Z);
                        break;
                    case "GPS.txt":
                        readGPSFile(f, GPS);
                        break;
                    case "GPS_CHANGE.txt":
                        readGPSFile(f, GPS_CHANGE);
                        break;
                }
            }
        }
    }

    public static void main(String[] args) {
        process();
    }

    private static void process() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
            DataProcessor dp = new DataProcessor(fileChooser.getSelectedFile().getAbsolutePath());
            dp.readFiles();
        }
    }
}
