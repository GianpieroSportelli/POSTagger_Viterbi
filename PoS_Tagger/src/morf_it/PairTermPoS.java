package morf_it;

public class PairTermPoS {

	String Term;
	String PoS;

	PairTermPoS(String t, String p) {
		Term = t;
		PoS = p;
	}

	public boolean equals(PairTermPoS x) {
		return ((this.Term.equalsIgnoreCase(x.Term)) && (this.PoS.equalsIgnoreCase(x.PoS)));

	}

}
