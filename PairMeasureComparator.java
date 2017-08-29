import java.util.Comparator;

public class PairMeasureComparator implements Comparator <PairMeasure> {

	@Override
	public int compare(PairMeasure o1, PairMeasure o2) {
		if (o1.value < o2.value) {
			return -1;
		} else if (o1.value > o2.value) {
			return 1;
		}
		return 0;
	}

}
