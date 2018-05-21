package hmm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import morf_it.Morph_IT;

//hunpos postagger nltk
public class HMM {

	private String urlMorph = "/home/gianpiero/Documenti/SistemiCognitivi/esercizioM/morph-it_048_UTF8.txt";
	private String urlMapping = "/home/gianpiero/Documenti/SistemiCognitivi/esercizioM/mapping.txt";
	// private String urlCorpus =
	// "/home/gianpiero/Documenti/SistemiCognitivi/esercizioM/ud12_for_POS_TAGGING-160229-train.txt";

	public HashMap<String, HashMap<String, Double>> Antecedent_to_pos;
	public HashMap<String, HashMap<String, Double>> pos_to_pos;
	public HashMap<String, HashMap<String, Double>> term_to_pos;

	public HashMap<String, HashMap<String, Double>> count_PP;
	public HashMap<String, HashMap<String, Double>> count_TP;
	public HashMap<String, HashMap<String, Double>> count_P2P;

	public HashMap<String, Integer> freq;
	public HashMap<String, Integer> freq2pos;
	public HashSet<String> pos_tag;
	public int n_pos;
	public int N;

	public static final String stop = "1*1";
	public static final String start = "0*0";

	public HMM(HashMap<String, HashMap<String, Double>> c_PP, HashMap<String, HashMap<String, Double>> c_TP,
			HashMap<String, HashMap<String, Double>> c_P2P, HashMap<String, HashMap<String, Double>> last2pos,
			HashMap<String, HashMap<String, Double>> pos_to_pos, HashMap<String, HashMap<String, Double>> term_to_pos,
			HashMap<String, Integer> freq, HashMap<String, Integer> freq2pos, HashSet<String> pos_tag, int n_pos,
			int N) {

		count_PP = c_PP;
		count_TP = c_TP;
		count_P2P = c_P2P;

		this.freq2pos = freq2pos;
		this.pos_tag = pos_tag;
		this.N = N;

		Antecedent_to_pos = last2pos;
		this.pos_to_pos = pos_to_pos;
		this.term_to_pos = term_to_pos;

		this.freq = freq;
		this.n_pos = n_pos;

		Morph_IT.inizialize(urlMorph, urlMapping);
	}

	public Double countBigram(String prev, String pos) {
		Double result = 0d;
		if (count_PP.containsKey(prev)) {
			HashMap<String, Double> map = count_PP.get(prev);
			if (map.containsKey(pos)) {
				result = map.get(pos);
			}
		}
		return result;
	}
	
	public Double countTrigram(Antecedent x, String pos) {
		Double result = 0d;
		if (count_P2P.containsKey(x.toString())) {
			HashMap<String, Double> map = count_P2P.get(x.toString());
			if (map.containsKey(pos)) {
				result = map.get(pos);
			}
		}
		return result;
	}
	
	public Double countUnigram(String pos){
		Double result=0d;
		if(freq.containsKey(pos)){
			result=(double)freq.get(pos);
		}
		return result;
	}

	public Double pos_to_pos(String prev, String pos) {

		Double result = 1d / ((double) this.N + 1d); // Smoothing
		if (pos_to_pos.containsKey(prev)) {
			HashMap<String, Double> map = pos_to_pos.get(prev);
			if (map.containsKey(pos)) {
				return map.get(pos);
			}
		}

		return result;
	}
	
	public Double antecedent_to_pos(Antecedent x, String pos) {
		Double result = 1d / ((double) this.N + 1d);
		if (Antecedent_to_pos.containsKey(x.toString())) {
			HashMap<String, Double> map = Antecedent_to_pos.get(x.toString());
			if (map.containsKey(pos)) {
				return map.get(pos);
			}
		}
		return result;
	}

	public Double term_to_pos(String term, String pos) {

		Double result = 0d;

		if (term_to_pos.containsKey(term)) {
			HashMap<String, Double> map = term_to_pos.get(term);
			if (map.containsKey(pos)) {
				return map.get(pos);
			}
		} else {
			ArrayList<String> possible = Morph_IT.pos_term(term);
			if (possible == null) {
				result = Double.valueOf("" + 1 / (double) n_pos);
			} else {
				for (String x : possible) {
					if (x.equals(pos)) {
						result = Double.valueOf("" + 1 / (double) possible.size());
						break;
					}
				}
			}
		}

		return result;

	}

	public String bestPoS(String term) {

		ArrayList<String> possible = Morph_IT.pos_term(term);

		String best = "NOUN";
		if (possible != null) {
			for (String p : possible) {
				if (!p.equals("_")) {
					best = p;
					break;
				}
			}
		}

		if (term_to_pos.containsKey(term)) {
			HashMap<String, Double> map = term_to_pos.get(term);
			Double bestP = 0d;
			for (String pos : map.keySet()) {
				if (map.get(pos) > bestP) {
					best = pos;
				}
			}
		}
		return best;
	}

	

}
