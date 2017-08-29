public class Iris {
	double sepallen;
	double sepalwid;
	double petallen;
	double petalwid;
	String type;
	
	//used for kNN
	public Iris(String sepallen, String sepalwid, String petallen, String petalwid, String type) {
		this.sepallen = Double.parseDouble(sepallen);
		this.sepalwid = Double.parseDouble(sepalwid);
		this.petallen = Double.parseDouble(petallen);
		this.petalwid = Double.parseDouble(petalwid);
		this.type = type;
	}

	//used for k-means-clustering
	public Iris(double sepallen, double sepalwid, double petallen, double petalwid, String type) {
		this.sepallen = sepallen;
		this.sepalwid = sepalwid;
		this.petallen = petallen;
		this.petalwid = petalwid;
		this.type = type;
	}
	
}
