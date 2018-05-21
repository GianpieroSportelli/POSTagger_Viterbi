package algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import hmm.HMM;

public class Viterbi {

	public static ArrayList<String> run(String context, HMM model) {
		ArrayList<String> result = new ArrayList<>();
//////////////////////////////////////////////////////////////////////////////////////////////////
		// Inizializzazione
		String[] split_context;
		String[] pos;
		Cell[][] viterbi;

		split_context = context.split(" ");
		pos = new String[model.n_pos];
		pos = model.pos_tag.toArray(pos);
		viterbi = new Cell[split_context.length][pos.length];

		String first = split_context[0];

		for (int c = 0; c < pos.length; c++) {
			String pos_now = pos[c];
			Double t_p = model.term_to_pos(first, pos_now);
			// caso iniziale, probabilità di transizione da stato
			// iniziale in pos_now
			Double p_p = model.pos_to_pos(HMM.start, pos_now);
			// calcolo contenuto cella prima riga
			viterbi[0][c] = new Cell(t_p * p_p, -1);
		}
		// Fine inizializzazione
//////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Ciclo sul contesto (righe della matrice)
		for (int row = 1; row < viterbi.length; row++) {
			// Prossimo termine
			String term = split_context[row];
			
			// Ciclo su i possibili pos Tag
			for (int c = 0; c < pos.length; c++) {
				// Pos tag in esame
				String pos_now = pos[c];
				
				// Bj probilità di emissione del termine dato il pos
				Double t_p = model.term_to_pos(term, pos_now);
				
				int fr = -1;
				Double best = 0d;
				
				Cell[] prev_row = viterbi[row - 1];
				
				for (int before = 0; before < prev_row.length; before++) {
					//Seleziono pos precedente candidato
					String prev_pos = pos[before];
					
					// Aij probabilità di transizione dal pos_prev a pos_now
					Double p_p = model.pos_to_pos(prev_pos, pos_now);
					
					// Valore di viterbi precedente
					Double v_prev = prev_row[before].p;
					
					//Calcolo valore attuale
					Double actual = v_prev * p_p;
					
					if (actual > best || fr == -1) {
						fr = before;
						best = actual;
					}

				}
				viterbi[row][c] = new Cell(t_p * best, fr);
			}
		}
		//Fine popolamento matrice
/////////////////////////////////////////////////////////////////////////////////////////////////////////////		
		
		/*
		 * int np = 0; for (String p : hmm) { System.out.print(np + ":" + p +
		 * "  "); np++; } System.out.println("");
		 * 
		 * for (Cell[] row : viterbi) { int num_pos=0; for (Cell item : row) {
		 * System.out.print("("+hmm[num_pos]+","+item + ") "); num_pos++; }
		 * System.out.println(""); }
		 */
		// Inizio ricerca del percorso che massimiza la probabilità
		
		Cell[] fin = viterbi[split_context.length - 1];
		Cell best = fin[0];
		int best_pos = 0;
		
		for (int i = 0; i < fin.length; i++) {
			Cell act = fin[i];
			Double aij = model.pos_to_pos(pos[i], HMM.stop);
			act.p = act.p * aij;
			if (act.p > best.p) {
				best = act;
				best_pos = i;
			}
		}

		int[] father_array = new int[split_context.length];
		if (best.father != -1) {
			father_array[father_array.length - 1] = best_pos;
			for (int j = split_context.length - 2; j >= 0; j--) {
				father_array[j] = best.father;
				// passo a riga precedente
				fin = viterbi[j];
				// prendo il migliore indicato da best (best.father)
				best = fin[father_array[j]];
			}
		}
		
		//Memorizzazione risultato in result
		for (int i = 0; i < split_context.length; i++) {
			// System.out.println(split_context[i] + "," +
			// hmm[father_array[i]]);
			result.add(pos[father_array[i]]);
		}
		
		return result;
	}

}
