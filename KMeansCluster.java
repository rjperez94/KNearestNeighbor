package part1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Adapted from http://www.dataonfocus.com/k-means-clustering-java-code/
 * @author josh
 *
 */
 
public class KMeansCluster {
	private static JFileChooser fileChooser = new JFileChooser();
    private static final String TESTSET_FILENAME = "iris-test.txt";
    
    private static final int CLUSTERS = 3;

	private static ArrayList<Cluster> clusters = new ArrayList<>();
	private static ArrayList<Iris> testing = new ArrayList<>();
    
    public static void main(String[] args) {
    	chooseDir();
    	
    	for (int i = 0; i < CLUSTERS; i++) {
    		Cluster cluster = new Cluster();
    		Iris centre = testing.get(new Random().nextInt(testing.size()));
    		cluster.centre = centre;
    		clusters.add(cluster);
    	}
    	
    	boolean finish = false;
        int cycle = 0;
        
        while(!finish) {
        	clear();
        	ArrayList<Iris> lastCentroids = centres();
        	assignCluster();
        	calculateCentre();
        	cycle++;
        	ArrayList<Iris> currentCentroids = centres();
        	
        	double distance = 0;
        	for(int i = 0; i < lastCentroids.size(); i++) {
        		distance += distance(lastCentroids.get(i),currentCentroids.get(i));
        	}
        	System.out.println("#################");
        	System.out.println("Cycles: " + cycle);
        	System.out.println("Centre distances: " + distance);
        	        	
        	if(distance == 0) {
        		finish = true;
        	}
        }
        
        for (int i=0; i < clusters.size();i++) {
        	System.out.println("CLUSTER "+(i+1));
        	System.out.println(clusters.get(i).toString());
        }
    }
    
    private static void chooseDir() {
		File test = null;

		// set up the file chooser
		fileChooser.setCurrentDirectory(new File("."));
		fileChooser.setDialogTitle("Select input directory");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		// run the file chooser and check the user didn't hit cancel
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			// get the files in the selected directory and match them to
			// the files we need.
			File directory = fileChooser.getSelectedFile();
			File[] files = directory.listFiles();

			for (File f : files) {
				if (f.getName().equals(TESTSET_FILENAME)) {
					test = f;
				}
			}

			// check none of the files are missing, and call the load
			// method in your code.
			if (test == null) {
				JOptionPane.showMessageDialog(null, "Directory does not contain correct files", "Error",
						JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			} else {
				readFile(test);
			}
		}
	}

	private static void readFile(File test) {
		try {
			String line = null;
			
			System.out.println("Loading test.");
			BufferedReader data = new BufferedReader(new FileReader(test));
			while ((line = data.readLine()) != null) {
				String[] values = line.split("\\s+");
				//ignore type
				testing.add(new Iris(values[0], values[1], values[2], values[3], values[4]));
			}
			data.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    private static double distance(Iris ir1, Iris ir2) {
		double dist = 0;
		dist += Math.pow((ir1.sepallen - ir2.sepallen), 2);
		dist += Math.pow((ir1.sepalwid - ir2.sepalwid), 2);
		dist += Math.pow((ir1.petallen - ir2.petallen), 2);
		dist += Math.pow((ir1.petalwid - ir2.petalwid), 2);
		return Math.sqrt(dist);
	}
    
    private static void clear() {
    	for(Cluster cluster : clusters) {
    		cluster.group.clear();
    	}
    }
    
    private static ArrayList <Iris> centres() {
    	ArrayList <Iris> centres = new ArrayList <Iris> ();
    	for(Cluster cluster : clusters) {
    		centres.add(new Iris(cluster.centre.sepallen, cluster.centre.sepalwid, cluster.centre.petallen, cluster.centre.petalwid, cluster.centre.type));
    	}
    	return centres;
    }
    
    private static void assignCluster() {
    	double max = Double.MAX_VALUE;
        double min = max; 
        
        int cluster = 0;                 
        double distance = 0.0; 
        
        for(Iris iris : testing) {
            for(int i = 0; i < CLUSTERS; i++) {
            	Cluster c = clusters.get(i);
                distance = distance(iris, c.centre);
                if(distance < min){
                    min = distance;
                    cluster = i;
                }
            }
            
            clusters.get(cluster).group.add(iris);
        }
        
    }
    
    private static void calculateCentre() {
        for(Cluster cluster : clusters) {
            double sepallenSum = 0;
            double sepalwidSum = 0;
            double petallenSum = 0;
            double petalwidSum = 0;
            int n_points = cluster.group.size();
            
            for(Iris iris : cluster.group) {
            	sepallenSum += iris.sepallen;
                sepalwidSum += iris.sepalwid;
                petallenSum += iris.petallen;
                petalwidSum += iris.petalwid;
            }
            
            Iris centre = cluster.centre;
            if(n_points > 0) {
            	double newSepallen = sepallenSum / n_points;
            	double newSepalwid = sepalwidSum / n_points;
            	double newPetallen = petallenSum / n_points;
            	double newPetalWid = petalwidSum / n_points;
            	centre.sepallen = newSepallen;
            	centre.sepalwid = newSepalwid;
            	centre.petallen = newPetallen;
            	centre.petalwid = newPetalWid;
            }
        }
    }
}
