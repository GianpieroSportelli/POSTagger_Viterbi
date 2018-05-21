package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import algorithm.Viterbi;
import hmm.LearnCorpus;
import hmm.HMM;

public class test_viterbi {

	public static void main(String[] args) {
		String url = "resources/ud12_for_POS_TAGGING-160229-train.txt";
		String url_dev = "resources/ud12_for_POS_TAGGING-160229-dev.txt";
		HMM res = LearnCorpus.inizialize(url);
		//res.init_MorphIT();
		
		// res.development(url_dev);
		/*
		 * res.al.a2=0.6d; res.al.a1=0.2d; res.al.a3=0.2d;
		 */
		boolean cambio = false;
		if (!cambio) {
			String urlTest = "resources/ud12_for_POS_TAGGING-160229-test.txt";
			File file = new File(urlTest);

			int n_pos = 0;
			int positive = 0;
			int n_frasi = 0;
			Double acc_media = 0d;

			HashMap<String, ArrayList<String>> testSet = createTest(file);

			for (String frase : testSet.keySet()) {

				System.out.println("-----------------------------------------------");
				System.out.println("Frase in esame: " + frase);

				n_frasi++;

				ArrayList<String> pos_candidate = Viterbi.run(frase, res);
				
				System.out.println("numero Pos candidati: " + pos_candidate.size());
				
				ArrayList<String> pos_true = testSet.get(frase);
				
				System.out.println("numero Pos veri: " + pos_true.size());
				
				n_pos += pos_true.size();
				
				int plus = 0;
				for (int i = 0; i < pos_true.size(); i++) {
					String candidate = pos_candidate.get(i);
					String real = pos_true.get(i);
					System.out.print("(C: " + candidate + ",T: " + real + ") ");
					if (candidate.equals(real)) {
						plus++;
					}
				}
				
				System.out.println("");
				Double accuratezza = ((double) plus / (double) pos_true.size()) * 100;
				System.out.println("Accuratezza nella frase: " + accuratezza + "%");
				acc_media += accuratezza;
				positive += plus;
				
			}
			
			
			System.out.println("-----------------------------------------------------------");
			System.out.println("Viterbi");
			System.out.println("Numero casi di test, singolo PoS tag: " + n_pos);
			System.out.println("Numero frasi utilizzate:" + n_frasi);
			System.out.println("Positivi:" + positive);
			System.out.println("Accuratezza:" + ((double) positive / (double) n_pos) * 100 + "%");
			System.out.println("Accuratezza media per frase: " + (acc_media / (double) n_frasi) + "%");
			System.out.println("-----------------------------------------------------------");
			
		} else {
			String frase = "Il ventilatore non funziona, sarà caduto";
			ArrayList<String> pos_candidate = Viterbi.run(frase, res);
			for (String pos : pos_candidate) {
				System.out.println(pos);
			}
		}
	}

	public static HashMap<String, ArrayList<String>> createTest(File file) {
		HashMap<String, ArrayList<String>> testSet = new HashMap<>();
		if (file.canRead()) {
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(file));
				String[] line_prec = null;
				String frase = "";
				ArrayList<String> pos_frase = new ArrayList<>();
				for (String line; (line = br.readLine()) != null;) {
					// System.out.println(line);
					if (!line.equalsIgnoreCase("")) {
						String[] line_split = line.split("	");
						if (line_split.length != 2) {
							System.out.println("errore dimensione errata lo split non va!! " + line_split.length);
						} else {
							String term = line_split[0];// UPPERCASE
							String pos = line_split[1];
							if (pos.contains("_") && pos.length() > 1) {
								String[] split = pos.split("_");
								pos = split[0];
							}
							// System.out.println("concat");
							frase += term + " ";
							pos_frase.add(pos);
						}
					} else {
						// System.out.println("linea vuota"+frase.length());
						if (frase.length() > 0) {
							frase = frase.substring(0, frase.length() - 1);
							// System.out.println("Insert");
							testSet.put(frase, pos_frase);
						}
						pos_frase = new ArrayList<>();
						frase = "";
					}
				}
				return testSet;
			} catch (FileNotFoundException e) {
				System.err.println("Errore nella lettura del file!!!");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Errore nella creazione del buffer di lettura!!!");
				e.printStackTrace();
			}

		} else {
			System.err.println("Il file non puo' essere letto!!!");
			return null;
		}
		return null;
	}

}
