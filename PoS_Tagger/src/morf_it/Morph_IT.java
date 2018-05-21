package morf_it;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Morph_IT {

	public static HashMap<String, HashSet<PairTermPoS>> morf_it = null;
	public static HashMap<String,String> mappingPos= null;
	
	public static boolean inizialize(String urlMorphIT,String urlMapping) {
		//System.setProperty("file.encoding","UTF-8");
				
		boolean result = false;
				
		File file = new File(urlMorphIT);
		if (file.canRead()) {
			BufferedReader br;
			try {
				result = true;
				mappingPos=learnMap(urlMapping);
				br = new BufferedReader(new FileReader(file));
				morf_it = new HashMap<String, HashSet<PairTermPoS>>();
				for (String line; (line = br.readLine()) != null;) {
					String[] token_line = line.split("	");
					if (token_line.length == 3){
						String[] split=token_line[2].split(":");
						String posM=split[0].split("-")[0];
						String pos=mappingPos.get(posM);
						if (!morf_it.containsKey(token_line[0])){							
							HashSet<PairTermPoS> set=new HashSet<PairTermPoS>();
							set.add(new PairTermPoS(token_line[1],pos));
							morf_it.put(token_line[0], set);
						}else{
							HashSet<PairTermPoS> set=morf_it.get(token_line[0]);
							set.add(new PairTermPoS(token_line[1],pos));
						}
					}
				}
				br.close();
			} catch (FileNotFoundException e) {
				System.err.println("Errore nella lettura del file!!!");
				e.printStackTrace();
			} catch (IOException e) {
				System.err
						.println("Errore nella creazione del buffer di lettura!!!");
				e.printStackTrace();
			}
		} else {
			System.err.println("Il file non puo' essere letto!!!");

		}
		return result;
	}
	
	
	private static HashMap<String, String> learnMap(String urlMapping) {
		HashMap<String, String> result=null;
		File file = new File(urlMapping);
		if (file.canRead()) {
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(file));
				result=new HashMap<>();
				for (String line; (line = br.readLine()) != null;) {
					String[] split=line.split(" ");
					result.put(split[0], split[1]);
				}
				br.close();
			} catch (FileNotFoundException e) {
				System.err.println("Errore nella lettura del file!!!");
				e.printStackTrace();
			} catch (IOException e) {
				System.err
						.println("Errore nella creazione del buffer di lettura!!!");
				e.printStackTrace();
			}
		} else {
			System.err.println("Il file di mapping non puo' essere letto!!!");

		}
		return result;
	}

	private static HashSet<String> lemming(HashSet<String> set) {
		HashSet<String> newSet = null;
		if (morf_it != null) {
			newSet = new HashSet<String>();
			for (String term : set) {
				if (morf_it.containsKey(term)) {
					HashSet<PairTermPoS> setPair=morf_it.get(term);
					String term_to_add="NaL";
					
						for(PairTermPoS x:setPair){
							if(x.PoS.equalsIgnoreCase("NOUN")){
								term_to_add=x.Term;
								break;
							}
						}
						if(term_to_add.equals("NaL")){
							term_to_add=((PairTermPoS)setPair.toArray()[0]).Term;
						}
					
					newSet.add(term_to_add);
				}else{
					System.out.println(term+" NON LEMMA!!!");
				}
			}
		}
		return newSet;

	}
	
	public static ArrayList<String> pos_term(String term){
		ArrayList<String> result=null;
		if(morf_it!=null){
			if(morf_it.containsKey(term)){
				HashSet<PairTermPoS> posMorph=morf_it.get(term);
				result=new ArrayList<String>();
				for(PairTermPoS p:posMorph){
					String pos=p.PoS;
					if(!result.contains(pos)){
						result.add(pos);
					}
				}
			}
		}
		return result;
	}
	
	public static HashSet<String> getAllPos(){
		HashSet<String> result=null;
		if(morf_it!=null){
			result=new HashSet<>();
			for(String term:morf_it.keySet()){
				HashSet<PairTermPoS> set=morf_it.get(term);
				for(PairTermPoS p:set){
					if(!result.contains(p.PoS)){
						result.add(p.PoS);
					}
				}
			}
		}
		return result;
	}

}
