package hmm;

public class Antecedent extends Object {
	public String pos_2 = HMM.start;
	public String pos_1 = HMM.start;

	public boolean equals(Object x) {
		if (x == null)
			return false;
		/*if (x == this)
			return true;*/
		if (!(x instanceof Antecedent))
			return false;
		Antecedent ant = (Antecedent) x;
		return this.pos_2.equals(ant.pos_2) && pos_1.equals(ant.pos_1);
	}

	public String toString() {
		return pos_2 + "&" + pos_1;
	}

	/*
	 * @Override public int compareTo(Antecedent x) { if
	 * (this.pos_2.equals(x.pos_2) && pos_1.equals(x.pos_1)) return 0; return
	 * -1; }
	 */

}
