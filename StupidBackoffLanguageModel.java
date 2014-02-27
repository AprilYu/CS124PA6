import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StupidBackoffLanguageModel extends LaplaceBigramLanguageModel {

    /**
     * Initialize your data structures in the constructor.
     */
    public StupidBackoffLanguageModel(HolbrookCorpus corpus) {
    	super(corpus);
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
            	score = scoreForBigram(currentBigram, score);
            }
        }
        return score;
    }
    
    protected double scoreForBigram(List<String> bigram, double score) {
    	Pair<String, String> pair = new Pair<String, String> (bigram.get(0), bigram.get(1));
    	String word = bigram.get(1);
        if (bigramCounts.containsKey(pair)) {
            score += Math.log(bigramCounts.get(pair));
            score -= Math.log(unigramCounts.get(bigram.get(0)));
        } else {
            score += Math.log(0.4);
            if (unigramCounts.containsKey(word)) {
                score += Math.log(unigramCounts.get(word) + 1);
                score -= Math.log(totalTokens + unigramCounts.keySet().size()); // normalize
          	} else {
            	score += Math.log(1);
            	score -= Math.log(totalTokens + unigramCounts.keySet().size() + 1);
            }
        }
        return score;
    }
}
