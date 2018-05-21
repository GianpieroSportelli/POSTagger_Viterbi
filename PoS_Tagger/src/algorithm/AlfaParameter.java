package algorithm;

import hmm.Antecedent;
import hmm.HMM;
import hmm.LearnCorpus;

public class AlfaParameter {
	public Double a1 = 0d;
	public Double a2 = 0d;
	public Double a3 = 0d;

	public void inc(int i, Double pr) {
		switch (i) {
		case 1:
			a1 += pr;
			break;
		case 2:
			a2 += pr;
			break;
		case 3:
			a3 += pr;
			break;
		default:
			System.out.println("Errore in this.inc");
		}
	}

	public void normalize() {
		Double tot = (a1 + a2 + a3);
		a1 = a1 / tot;
		a2 = a2 / tot;
		a3 = a3 / tot;
	}

	public void init() {
		a1 = 0d;
		a2 = 0d;
		a3 = 0d;
	}

	public String toString() {
		return a3 + "-" + a2 + "-" + a1;
	}

	// Deleted interpolation algorithm
	public void development(String url) {
		HMM dev = LearnCorpus.inizialize(url);
		this.init();

		for (String t1 : dev.pos_tag) {
			Double count1=dev.countUnigram(t1);
			
			for (String t2 : dev.pos_tag) {
				
				Double count2 = dev.countUnigram(t2);
				
				Antecedent x = new Antecedent();

				x.pos_2 = t1;
				x.pos_1 = t2;

				Double count12 = dev.countBigram(t1, t2);

				for (String t3 : dev.pos_tag) {

					Double count123 = dev.countTrigram(x, t3);
					Double count23= dev.countBigram(t2, t3);
					Double count3 = dev.countUnigram(t3);

					if (count123 > 0d) {

						Double p3 = (count123 - 1d) / (count12 - 1d);
						Double p2 = (count23 - 1d) / (count2 - 1d);
						Double p1 = (count3 - 1d) / ((double) dev.N - 1d);
						
						double[] arr={p3,p2,p1};
						
						double max=0d;
						for(double arg:arr){
							if(arg>max){
								max=arg;
							}
						}

						boolean case3 = max==p3;
						boolean case2 = max==p2;
						boolean case1 = max==p1;

						if (case3) {
							this.inc(3, count123);
						} else if (case2) {
							this.inc(2, count123);
						} else if (case1) {
							this.inc(1, count123);
						}

					}
				}
			}

		}

		this.normalize();
	}

}
