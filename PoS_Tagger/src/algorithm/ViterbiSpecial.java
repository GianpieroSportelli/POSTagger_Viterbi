package algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import hmm.Antecedent;
import hmm.HMM;

public class ViterbiSpecial {

	public static ArrayList<String> run(String context, HMM model, AlfaParameter a) {

		ArrayList<String> result = new ArrayList<>();
		
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
			t_p = transform(t_p);
			Antecedent X = new Antecedent();
			X.pos_2 = HMM.start;
			X.pos_1 = HMM.start;
			Double p2_p = model.antecedent_to_pos(X, pos_now);
			Double p_p = a.a3 * p2_p + a.a2 * model.pos_to_pos(HMM.start, pos_now)
					+ a.a1 * (double) model.freq.get(pos_now) / model.N;
			p_p = transform(p_p);
			viterbi[0][c] = new Cell(op(t_p, p_p), -1);
		}
		
		// Fine inizializzazione
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
		
		
		
		// Ciclo sul contesto
		for (int row = 1; row < viterbi.length; row++) {
			
			String term = split_context[row];
			
			// Ciclo su i possibili hmm
			for (int c = 0; c < pos.length; c++) {
				
				// Pos tag in esame
				String pos_now = pos[c];
				
				// Bj probilità di osservere il termine dato il pos Tag
				Double t_p = model.term_to_pos(term, pos_now);
				t_p = transform(t_p);
				
				int fr = -1;
				Double best = smallest();
				
				Cell[] prev_row = viterbi[row - 1];
				
				for (int before = 0; before < prev_row.length; before++) {
					String prev_pos = pos[before];
					Double p_p = 0d;
					
					if (prev_row[before].father != -1) {
						
						String prev2_pos = pos[prev_row[before].father];
						Antecedent X = new Antecedent();
						X.pos_2 = prev2_pos;
						X.pos_1 = prev_pos;
						
						Double p2_p = model.antecedent_to_pos(X, pos_now);
						
						p_p = a.a3 * p2_p + a.a2 * model.pos_to_pos(prev_pos, pos_now)
								+ a.a1 * (double) model.freq.get(pos_now) / model.N;
					} else {
						
						Antecedent X = new Antecedent();
						X.pos_2 = HMM.start;
						X.pos_1 = prev_pos;
						
						Double p2_p = model.antecedent_to_pos(X, pos_now);
						p_p = a.a3 * p2_p + a.a2 * model.pos_to_pos(prev_pos, pos_now)
								+ a.a1 * (double) model.freq.get(pos_now) / model.N;
					}
					
					// Valore di viterbi precedente
					p_p = transform(p_p);
					Double v_prev = prev_row[before].p;
					Double actual = op(v_prev, p_p);
					
					if (actual > best) {
						fr = before;
						best = actual;
					}

				}
				viterbi[row][c] = new Cell(op(t_p, best), fr);
			}
		}
		
		//Fine riempimento matrice
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		

		/*
		 * int np = 0; for (String p :hmm) { System.out.print(np + ":" +
		 * p.toString() + "  "); np++; } System.out.println("");
		 * 
		 * for (Cell[] row : viterbi) { int num_pos = 0; for (Cell item : row) {
		 * System.out.print("(" + num_pos + "," + item + ") "); num_pos++; }
		 * System.out.println(""); }
		 */

		Cell[] fin = viterbi[split_context.length - 1];
		Cell best = fin[0];
		int best_pos = 0;
		
		for (int i = 0; i < fin.length; i++) {
			Cell act = fin[i];
			Double aij = 0d;
			
			if (act.father != -1) {
				String l2pos = pos[act.father];
				String lpos = pos[i];
				
				Antecedent prec = new Antecedent();
				prec.pos_1 = lpos;
				prec.pos_2 = l2pos;
				
				aij = model.antecedent_to_pos(prec, HMM.stop);
			}
			
			aij = transform(aij);
			
			act.p = op(act.p, aij);
			
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
		
		for (int i = 0; i < split_context.length; i++) {
			// System.out.println(split_context[i] + "," +
			// pos[father_array[i]]);
			result.add(pos[father_array[i]]);
		}
		return result;
	}

	private static Double smallest() {
		// TODO Auto-generated method stub
		return  0d;//Math.log(0d);
	}

	private static Double op(Double p, Double aij) {
		return p * aij;//+
	}

	private static Double transform(Double pr) {
		return pr;//Math.log(pr);//1d /Math.log(pr);
	}
}
