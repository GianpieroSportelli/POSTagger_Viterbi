package test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import algorithm.AlfaParameter;
import algorithm.ViterbiSpecial;
import hmm.LearnCorpus;
import hmm.HMM;

public class test_viterbiSpecial {
	public static void main(String[] args) {
		String url = "resources/ud12_for_POS_TAGGING-160229-train.txt";
		String url_dev = "resources/ud12_for_POS_TAGGING-160229-Sum.txt";
		
		HMM res = LearnCorpus.inizialize(url);
		AlfaParameter alfa=new AlfaParameter();
		
		//alfa.development(url_dev);
		
		alfa.a3=0.4d; alfa.a2=0.6d; alfa.a1=0d; 
		 
		boolean cambio = false;
		if (!cambio) {
			String urlTest = "resources/ud12_for_POS_TAGGING-160229-test.txt";
			File file = new File(urlTest);
			int n_pos = 0;
			int positive = 0;
			int n_frasi = 0;
			Double acc_media = 0d;
			HashMap<String, ArrayList<String>> testSet = test_viterbi.createTest(file);
			for (String frase : testSet.keySet()) {
				System.out.println("-----------------------------------------------");
				System.out.println("Frase in esame: " + frase);
				n_frasi++;
				ArrayList<String> pos_candidate = ViterbiSpecial.run(frase, res,alfa);
				//System.out.println("numero Pos candidati: " + pos_candidate.size());
				ArrayList<String> pos_true = testSet.get(frase);
				//System.out.println("numero Pos veri: " + pos_true.size());
				n_pos += pos_true.size();
				int plus = 0;
				for (int i = 0; i < pos_true.size(); i++) {
					String candidate = pos_candidate.get(i);
					String real = pos_true.get(i);
					//System.out.print("(C: " + candidate + ",T: " + real + ") ");
					if (candidate.equals(real)) {
						plus++;
					}
				}
				//System.out.println("");
				Double accuratezza = ((double) plus / (double) pos_true.size()) * 100;
				System.out.println("Accuratezza nella frase: " + accuratezza + "%");
				acc_media += accuratezza;
				positive += plus;
				// positive+=plus;
			}
			
			System.out.println("-----------------------------------------------------------");
			System.out.println("Viterbi Special");
			System.out.println("Alfa: "+alfa);
			System.out.println("Numero casi di test, singolo pos: " + n_pos);
			System.out.println("Numero frasi utilizzate:" + n_frasi);
			System.out.println("Positivi:" + positive);
			System.out.println("Accuratezza:" + ((double) positive / (double) n_pos) * 100 + "%");
			System.out.println("Accuratezza media per frase: " + (acc_media / (double) n_frasi) + "%");

			System.out.println("-----------------------------------------------------------");
		} else {
			String frase = "Il ventilatore non funziona , sarà caduto .";
			ArrayList<String> pos_candidate = ViterbiSpecial.run(frase, res,alfa);
			for (String pos : pos_candidate) {
				System.out.println(pos);
			}
		}
	}

}
