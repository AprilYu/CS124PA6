import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LaplaceBigramLanguageModel implements LanguageModel {
	protected Map<Pair<String,String>, Integer> bigramCounts;
	protected Map<String, Integer> unigramCounts;
	protected int vocabSize;
	protected int totalTokens;

    /**
     * Initialize your data structures in the constructor.
     */
    public LaplaceBigramLanguageModel(HolbrookCorpus corpus) {
    	bigramCounts = new HashMap<Pair<String,String>, Integer>();
    	unigramCounts = new HashMap<String, Integer>();
    	vocabSize = corpus.getVocabulary().size();
    	totalTokens = 0;
        train(corpus);
    }

    /**
     * Takes a corpus and trains your language model.
     * Compute any counts or other corpus statistics in this function.
     */
    public void train(HolbrookCorpus corpus) {
        for (Sentence sentence : corpus.getData()) {
        	List<String> currentBigram = new ArrayList<String>();
            for (Datum datum : sentence) {
                String word = datum.getWord();
                currentBigram.add(word);
                if (currentBigram.size() > 2)
                	currentBigram.remove(0);
                if (currentBigram.size() == 2) {
                	Pair<String, String> pair = new Pair<String, String> (currentBigram.get(0), currentBigram.get(1));
                	if (bigramCounts.containsKey(pair)) {
                		bigramCounts.put(pair, bigramCounts.get(pair) + 1);
                	} else {
                		bigramCounts.put(pair, 1);
                	}
                }
                if (unigramCounts.containsKey(word)) {
                    unigramCounts.put(word, unigramCounts.get(word) + 1);
                } else {
                    unigramCounts.put(word, 1);
                }
                totalTokens++;
            }
        }
    }


    /**
     * Takes a list of strings as argument and returns the log-probability of the
     * sentence using your language model. Use whatever data you computed in train() here.
     */
    public double score(List<String> sentence) {
        double score = 0.0;
        List<String> currentBigram = new ArrayList<String>();
        for (String word : sentence) {
        	currentBigram.add(word);
            if (currentBigram.size() > 2)
                currentBigram.remove(0);
            if (currentBigram.size() == 2) {
            	Pair<String, String> pair = new Pair<String, String> (currentBigram.get(0), currentBigram.get(1));
            	if (bigramCounts.containsKey(pair)) {
            		score += Math.log(bigramCounts.get(pair) + 1);
            		if (unigramCounts.containsKey(currentBigram.get(0))) {
            			score -= Math.log(unigramCounts.get(currentBigram.get(0)) + vocabSize);
            		} else {
            			score -= Math.log(1 + vocabSize);
            		}
            	} else {
            		score += Math.log(1);
            		if (unigramCounts.containsKey(currentBigram.get(0))) {
            			score -= Math.log(unigramCounts.get(currentBigram.get(0)) + vocabSize);
            		} else {
            			score -= Math.log(1 + vocabSize);
            		}
            	}
            }
        }
        return score;
    }
    
    public class Pair<E,F> {
    	protected E first;
    	protected F second;
    	
    	public Pair(E first, F second) {
    		this.first = first;
    		this.second = second;
    	}
    	
    	public E getFirst() {
    		return first;
    	}
    	
    	public F getSecond() {
    		return second;
    	}
    	
    	public String toString() {
    		return first.toString() + "|" + second.toString();
    	}
    	
    	@Override
   		public boolean equals(Object other) {
        	if (other == null) return false;
       	 	if (other == this) return true;
       		if (!(other instanceof Pair )) return false;
       		Pair otherPair = (Pair)other;
       		return first.equals(otherPair.getFirst()) &&
                second.equals(otherPair.getSecond());
    	}

    	@Override
    	public int hashCode() {
        	return first.hashCode() + second.hashCode();
    	}
    }
}
