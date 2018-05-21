package test;

import java.util.HashMap;

import hmm.Antecedent;
import hmm.LearnCorpus;
import hmm.HMM;

public class test_learnCorpus {

	public static void main(String[] args) {
		String url_tree="resources/ud12_for_POS_TAGGING-160229-train.txt";
		HMM res=LearnCorpus.inizialize(url_tree);
		System.out.println("Pos to Pos");
		HashMap<String, HashMap<String, Double>> ptp=res.pos_to_pos;
		HashMap<String, HashMap<String, Double>> wtp=res.term_to_pos;
		HashMap<String, HashMap<String, Double>> l2p=res.Antecedent_to_pos;
		for(String pos:ptp.keySet()){
			System.out.println(pos);
		}
		/*for(String prev_pos:ptp.keySet()){
			System.out.println("PoS t(i-1): "+prev_pos);
			HashMap<String, Double> map=ptp.get(prev_pos);
			for(String hmm:map.keySet()){
				System.out.println("	freq("+hmm+"|"+prev_pos+"): "+map.get(hmm));
			}
			
		}*/
		
		/*for(String word:wtp.keySet()){
			System.out.println("Termine: "+word);
			HashMap<String, Double> map=wtp.get(word);
			for(String hmm:map.keySet()){
				System.out.println("	freq("+hmm+"|"+word+"): "+map.get(hmm));
			}
			
		}*/
		/*for(Antecedent prev_pos:l2p.keySet()){
			System.out.println("PoS t(i-2,i-1): "+prev_pos);
			HashMap<String, Double> map=l2p.get(prev_pos);
			for(String hmm:map.keySet()){
				System.out.println("	freq("+hmm+"|"+prev_pos+"): "+map.get(hmm));
			}
			
		}*/
		Antecedent a=new Antecedent();
		Antecedent b=new Antecedent();
		a.pos_2="2";b.pos_2="2";
		a.pos_1="1";b.pos_1="1";
		System.out.println(a.equals(b));
	}

}
