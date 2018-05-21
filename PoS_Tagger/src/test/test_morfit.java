package test;

import java.util.HashSet;

import hmm.HMM;
import hmm.LearnCorpus;
import morf_it.Morph_IT;

public class test_morfit {

	public static void main(String[] args) {
		String url="resources/morph-it_048_UTF8.txt";
		String urlMapping="resources/mapping.txt";
		String url_corpus="resources/ud12_for_POS_TAGGING-160229-train.txt";
		Morph_IT.inizialize(url,urlMapping);
		System.out.println("Fine init");
		System.out.println("Fine mapping");
		
		/*for(String posM:Morph_IT.mappingPos.keySet()){
			String pos=Morph_IT.mappingPos.get(posM);
			System.out.println(posM+": "+pos);
		}*/
		HashSet<String> set=Morph_IT.getAllPos();
		/*System.out.println("Morph-it");
		for(String pos:set){
			System.out.println(pos);
		}*/
		System.out.println("--------------------------------------------------------------");
//		String url_corpus="D:/SistemiCognitivi/esercizioM/ud12_for_POS_TAGGING-160229-train.txt";
		HMM res=LearnCorpus.inizialize(url_corpus);
		HashSet<String> posGoogle=res.pos_tag;
		System.out.println("Corpus");
		for(String pos:posGoogle){
			System.out.println(pos);
		}
		System.out.println("--------------------------------------------------------------");
		HashSet<String> contenuti=new HashSet<>();
		HashSet<String> no_C=(HashSet<String>) posGoogle.clone();
		HashSet<String> no_M=new HashSet<>();
		
		for(String posM:set){
			if(no_C.contains(posM)){
				contenuti.add(posM);
				no_C.remove(posM);
			}else{
				no_M.add(posM);
			}
		}
		
		System.out.println("Uguali");
		for(String p:contenuti)
			System.out.println(p);
		
		System.out.println("--------------------------------");
		
		System.out.println("Diversi Morph-it");
		for(String p:no_M){
			System.out.println(p);
		}
		System.out.println("--------------------------------");
		System.out.println("Diversi Corpus");
		for(String p:no_C){
			System.out.println(p);
		}
		System.out.println("--------------------------------");
	}

}
