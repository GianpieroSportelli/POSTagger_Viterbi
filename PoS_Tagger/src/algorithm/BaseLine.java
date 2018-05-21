package algorithm;

import java.util.ArrayList;

import hmm.HMM;

public class BaseLine {

	public static ArrayList<String> run(String context, HMM model) {
		// Inizializzazione
		String[] split_context=context.split(" ");
		ArrayList<String> result=new ArrayList<>();
		for(int i=0;i<split_context.length;i++){
			result.add(model.bestPoS(split_context[i]));
		}
		return result;
	}
}
