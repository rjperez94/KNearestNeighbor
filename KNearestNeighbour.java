import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class KNearestNeighbour {
	private static JFileChooser fileChooser = new JFileChooser();
	private static final String TRAINSET_FILENAME = "iris-training.txt";
	private static final String TESTSET_FILENAME = "iris-test.txt";

	private static ArrayList<Iris> training = new ArrayList<>();
	private static ArrayList<Iris> testing = new ArrayList<>();
	private static final int K = 3;
	
	//used for ranges
	private static double maxSepalLen = 0;
	private static double minSepalLen = Double.MAX_VALUE;
	private static double maxSepalWid = 0;
	private static double minSepalWid = Double.MAX_VALUE;
	private static double maxPetalLen = 0;
	private static double minPetalLen = Double.MAX_VALUE;
	private static double maxPetalWid = 0;
	private static double minPetalWid = Double.MAX_VALUE;

	public static void main(String[] args) {
		chooseDir();
		setRange();
		ArrayList<String> predictions = new ArrayList<>();
		System.out.println(String.format("%-20s %s", "Predicted", "Actual"));
		for (int i = 0; i < testing.size();i++) {
			ArrayList<Iris> neighbours = neigbours(testing.get(i));
			String result = predict(neighbours);
			predictions.add(result);
			System.out.println(String.format("%-20s %s", result, testing.get(i).type));
		}
		double accuracy = accuracy(testing, predictions);
		System.out.println("Accuracy: " + accuracy + "%");
	}

	private static void chooseDir() {
		File train = null, test = null;

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
				if (f.getName().equals(TRAINSET_FILENAME)) {
					train = f;
				} else if (f.getName().equals(TESTSET_FILENAME)) {
					test = f;
				}
			}

			// check none of the files are missing, and call the load
			// method in your code.
			if (train == null || test == null) {
				JOptionPane.showMessageDialog(null, "Directory does not contain correct files", "Error",
						JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			} else {
				readFile(train, test);
			}
		}
	}

	private static void readFile(File train, File test) {
		try {
			String line = null;

			System.out.println("Loading training.");
			BufferedReader data = new BufferedReader(new FileReader(train));
			while ((line = data.readLine()) != null) {
				String[] values = line.split("\\s+");
				training.add(new Iris(values[0], values[1], values[2], values[3], values[4]));
			}
			data.close();

			System.out.println("Loading test.");
			data = new BufferedReader(new FileReader(test));
			while ((line = data.readLine()) != null) {
				String[] values = line.split("\\s+");
				testing.add(new Iris(values[0], values[1], values[2], values[3], values[4]));
			}
			data.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void setRange() {
		for (Iris ir: training) {
			sepalRange(ir.sepallen, ir.sepalwid);
			petalRange(ir.petallen, ir.petalwid);
		}
	}
	
	private static void sepalRange(double sepallen, double sepalwid) {
		if(sepallen>maxSepalLen)	maxSepalLen = sepallen;
		if(sepallen<minSepalLen)	minSepalLen = sepallen;
		if(sepalwid>maxSepalWid)	maxSepalWid = sepalwid;
		if(sepalwid<minSepalWid)	minSepalWid = sepalwid;
	}
	
	private static void petalRange(double petallen, double petalwid) {
		if(petallen>maxPetalLen)	maxPetalLen = petallen;
		if(petallen<minPetalLen)	minPetalLen = petallen;
		if(petalwid>maxPetalWid)	maxPetalWid = petalwid;
		if(petalwid<minPetalWid)	minPetalWid = petalwid;
	}

	private static double distance(Iris ir1, Iris ir2) {
		double sum = 0;
		double num = 0;
		double den = 0;
		
		num = Math.pow((ir1.sepallen - ir2.sepallen), 2);
		den = Math.pow(maxSepalLen-minSepalLen, 2);
		sum += (num / den);
		
		num = Math.pow((ir1.sepalwid - ir2.sepalwid), 2);
		den = Math.pow(maxSepalWid-minSepalWid, 2);
		sum += (num / den);
		
		num = Math.pow((ir1.petallen - ir2.petallen), 2);
		den = Math.pow(maxPetalLen-minPetalLen, 2);
		sum += (num / den);
		
		num = Math.pow((ir1.petalwid - ir2.petalwid), 2);
		den = Math.pow(maxPetalWid-minPetalWid, 2);
		sum += (num / den);
		
		return Math.sqrt(sum);
	}

	private static ArrayList<Iris> neigbours(Iris test) {
		ArrayList<PairMeasure> distances = new ArrayList<PairMeasure>();
		for (Iris i : training) {
			distances.add(new PairMeasure(i, distance(test, i)));
		}
		Collections.sort(distances, new PairMeasureComparator());
		
		ArrayList<Iris> chosen = new ArrayList<Iris>();
		for (int i = 0; i < distances.size() && i<=K-1; i++) {
			chosen.add(distances.get(i).iris);
		}
		return chosen;
	}

	private static String predict(ArrayList<Iris> neighbours) {
		ArrayList<PairMeasure> votes = new ArrayList<PairMeasure>();
		ArrayList<Iris> iris = new ArrayList<Iris>();
		
		for (int i = 0;  i < neighbours.size(); i++) {
			if (iris.contains(neighbours.get(i))) {
				votes.get(i).value++;
			} else {
				votes.add(new PairMeasure(neighbours.get(i), 1.0));
				iris.add(neighbours.get(i));
			}
		}

		Collections.sort(votes, new PairMeasureComparator());
		Collections.reverse(votes);
		return votes.get(0).iris.type;
	}

	private static double accuracy(ArrayList<Iris> test, ArrayList<String> predict) {
		double correct = 0;
		for (int i = 0; i < test.size(); i++) {
			if (test.get(i).type.equals(predict.get(i))) {
				correct += 1;
			}
		}
		return (correct / test.size()) * 100.0;
	}
}
