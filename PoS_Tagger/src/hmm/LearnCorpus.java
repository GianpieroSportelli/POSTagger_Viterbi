package hmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LearnCorpus {

	public static HMM inizialize(String urlFile) {
		//System.setProperty("file.encoding", "UTF-8");
		
		// Struttura che mantiene (prev2_pos,prev1_pos,pos)=freq
		HashMap<String, HashMap<String, Double>> pos2_pos = new HashMap<>();
		HashMap<String,HashMap<String,Double>>count_P2P=new HashMap<>();
		
		// Struttura che mantiene (lastPos,pos)=freq
		HashMap<String, HashMap<String, Double>> pos_pos = new HashMap<>();
		HashMap<String,HashMap<String,Double>>count_PP=new HashMap<>();
		
		// Struttura che mantiene (term,pos)=freq
		HashMap<String, HashMap<String, Double>> term_pos = new HashMap<>();
		HashMap<String,HashMap<String,Double>>count_TP=new HashMap<>();
		
		// Struttura che contiene le frequenze dei singoli pos Tag
		HashMap<String, Integer> freq = new HashMap<>();
		// Struttura che mantiene l frequenze delle coppie di pos Tag
		HashMap<String, Integer> freq2pos = new HashMap<>();
		// numero di pos_Tag
		int n_pos = 0;
		// numero di Parole che compongono il corpus
		int N = 0;
		//Insieme dei pos Tag
		HashSet<String> posTag = new HashSet<>();

		File file = new File(urlFile);
		
		if (file.canRead()) {
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(file));
				// Contiene la line precedente (termine,Pos)
				String[] line_prec = null;
				// memorizza la coppia di ultimi pos
				Antecedent last2pos = new Antecedent();
				// Memorizza l'ultimo pos
				String lastPos = HMM.start;
				// Memorizza l'ultimo termine
				String lastTerm = "";

				// Lettura per righe del file
				for (String line; (line = br.readLine()) != null;) {
					// Linea vuota=Linea demarcazione tra frasi
					if (!line.equalsIgnoreCase("")) {
						// Spilt della linea in termine pos Tag
						String[] line_split = line.split("	");
						// se lo split non ha dimensione 2: Errore
						if (line_split.length != 2) {
							System.err.println("errore dimensione errata lo split non va!! " + line_split.length);
						} else {
							// separazione in due var.
							String term = line_split[0];//.toUpperCase();
							String pos = line_split[1];
							N++;

							// Generalizzazione dei Pos Tag compositi esempio: DET_A
							if (pos.contains("_") && pos.length() > 1) {
								String[] split = pos.split("_");
								pos = split[0];
								line_split[1] = pos;
							}

							// Aggiunta del pos Tag a insieme dei pos Tag
							if (!posTag.contains(pos)) {
								posTag.add(pos);
							}

							// Aggiornamento frequenza pos tag
							boolean in = freq.containsKey(pos);
							if (in) {
								// Pos già contenuto
								Integer f = freq.get(pos);
								f++;
								freq.put(pos, f);
							} else {
								// Nuovo Pos Tag
								n_pos++;
								freq.put(pos, 1);
							}

							// Aggiornamento frequenza per simbolo iniziale
							// Aggiornamento Pos precedente
							if (line_prec == null) {
								lastPos = HMM.start;
								in = freq.containsKey(lastPos);
								if (in) {
									Integer f = freq.get(lastPos);
									f++;
									freq.put(lastPos, f);
								} else {
									freq.put(lastPos, 1);
								}
							}/* else {
								lastPos = line_prec[1];
							}*/

							// Riempiemento P(pos|pos_prev)=freq
							pos_pos = updateMemory(pos_pos, lastPos, pos);
							// Riempiemento P(term|pos)=freq
							term_pos = updateMemory(term_pos, term, pos);

							line_prec = line_split;
							lastPos = pos;
							lastTerm = term;

							if (freq2pos.containsKey(last2pos.toString())) {
								Integer f = freq2pos.get(last2pos.toString());
								f++;
								freq2pos.put(last2pos.toString(), f);
							} else {
								freq2pos.put(last2pos.toString(), 1);
							}
							pos2_pos = updateMemory2pos(pos2_pos, last2pos, pos);
							Antecedent New = new Antecedent();
							New.pos_2 = last2pos.pos_1;
							New.pos_1 = pos;
							last2pos = New;
						}
						
					} else {
						// Linea Vuota
						line_prec = null;
						//aggiornameno frequenza stato finale
						boolean in = freq.containsKey(HMM.stop);
						if (in) {
							Integer f = freq.get(HMM.stop);
							f++;
							freq.put(HMM.stop, f);
						} else {
							freq.put(HMM.stop, 1);
						}
						pos_pos = updateMemory(pos_pos, lastPos, HMM.stop);
						last2pos = new Antecedent();
					}
				}
				// Calcolo probabilità
				count_PP=(HashMap<String, HashMap<String, Double>>) pos_pos.clone();
				pos_pos = procProbability(pos_pos, freq, true);
				count_TP=(HashMap<String, HashMap<String, Double>>) term_pos.clone();
				term_pos = procProbability(term_pos, freq, false);
				count_P2P=(HashMap<String, HashMap<String, Double>>) pos2_pos.clone();
				pos2_pos = procProbability2pos(pos2_pos, freq2pos);

				br.close();
			} catch (FileNotFoundException e) {
				System.err.println("Errore nella lettura del file!!!");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Errore nella creazione del buffer di lettura!!!");
				e.printStackTrace();
			}
		} else {
			System.err.println("Il file non puo' essere letto!!!");

		}
		HMM res = new HMM(count_PP,count_TP,count_P2P,pos2_pos, pos_pos, term_pos, freq, freq2pos, posTag, n_pos, N);
		return res;
	}

	private static HashMap<String, HashMap<String, Double>> updateMemory(
			HashMap<String, HashMap<String, Double>> memory, String X, String Y) {
		if (!memory.containsKey(X)) {
			HashMap<String, Double> map = new HashMap<>();
			map.put(Y, 1d);
			memory.put(X, map);
		} else {
			HashMap<String, Double> map = (HashMap<String, Double>) memory.get(X);
			if (map.containsKey(Y)) {
				Double f = map.get(Y);
				f += 1d;
				map.put(Y, f);
				memory.put(X, map);
			} else {
				map.put(Y, 1d);
				memory.put(X, map);
			}
		}
		return memory;
	}

	private static HashMap<String, HashMap<String, Double>> updateMemory2pos(
			HashMap<String, HashMap<String, Double>> memory, Antecedent X, String Y) {
		if (!memory.containsKey(X.toString())) {
			HashMap<String, Double> map = new HashMap<>();
			map.put(Y, 1d);
			memory.put(X.toString(), map);
		} else {
			HashMap<String, Double> map = (HashMap<String, Double>) memory.get(X.toString());
			if (map.containsKey(Y)) {
				Double f = map.get(Y);
				f += 1d;
				map.put(Y, f);
				memory.put(X.toString(), map);
			} else {
				map.put(Y, 1d);
				memory.put(X.toString(), map);
			}
		}
		return memory;
	}

	private static HashMap<String, HashMap<String, Double>> procProbability(
			HashMap<String, HashMap<String, Double>> memory, HashMap<String, Integer> freq, boolean keyInFreq) {
		for (String X : memory.keySet()) {
			HashMap<String, Double> map = memory.get(X);
			Set<String> key = map.keySet();
			for (String Y : key) {
				Double f = map.get(Y);
				Integer fr = 0;
				
				if (keyInFreq) {
					fr = freq.get(X);
				} else {
					fr = freq.get(Y);
				}
				Double pr = f / fr;
				Double value = pr;// 1d / (-1 * Math.log(pr));
				map.put(Y, value);
			}
			
			memory.put(X, map);
		}
		return memory;
	}

	private static HashMap<String, HashMap<String, Double>> procProbability2pos(
			HashMap<String, HashMap<String, Double>> memory, HashMap<String, Integer> freq) {
		for (String X : memory.keySet()) {
			HashMap<String, Double> map = memory.get(X);
			Set<String> key = map.keySet();
			for (String Y : key) {
				Double f = map.get(Y);
				Integer fr = freq.get(X);
				Double pr = f / (double) fr;
				Double value = pr;// 1d / (-1 * Math.log(pr));
				map.put(Y, value);
			}
			memory.put(X, map);
		}
		return memory;
	}

}
