import java.util.List;

public class TranslateFrenchToEnglish {

	public String getTranslation(String foreignWord) {

	}

	public List<String> translateSentence(List<String> sentence) {
		if (sentence.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> bestSentence = new ArrayList<String>();
        for(int i = 0; i < sentence.size(); i++) {
        	String frenchWord = sentence[i];
        	String englishTranslation = TranslateFrenchToEnglish.getTranslation(frenchWord);
        	bestSentence.append(englishTranslation);
        }
        return bestSentence;
	}

	public static void eval() {

	}

	public static void main(String[] args) {
        TranslateFrenchToEnglish.eval();
    }
}