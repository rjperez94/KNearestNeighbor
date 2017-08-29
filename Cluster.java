import java.util.ArrayList;

public class Cluster {
	public ArrayList<Iris> group = new ArrayList<>();
	public Iris centre;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Iris iris: group) {
			sb.append("[ PLen: "+iris.petallen+", PWid: "+iris.petalwid+", SLen: "+iris.sepallen+", SWid: "+iris.sepalwid+" T: "+iris.type+"]\n");
		}
		return sb.toString();
	}
}
