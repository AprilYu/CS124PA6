import java.util.*;

public class Translation {
	static ArrayList<ArrayList<String>> sentences;
	static Map<String, ArrayList<String>> dictionary;

	public Translation(String corpusFileName, String dictionaryFileName) {
		sentences = new ArrayList<ArrayList<String>>();
		dictionary = new HashMap<String, ArrayList<String>>();
		//need to tokenize provided files and populate data structures with the information

		//below is just hard-coded data to test function implementation.
		ArrayList<String> HelloWorldSentence = new ArrayList<String>(Arrays.asList("Hello", "World"));
		ArrayList<String> PieSentence = new ArrayList<String>(Arrays.asList("I", "like", "pie"));
		sentences.add(HelloWorldSentence);
		sentences.add(PieSentence);

		ArrayList<String> hello = new ArrayList<String>(Arrays.asList("hi", "howdy"));
		ArrayList<String> world = new ArrayList<String>(Arrays.asList("planet", "earth"));
		ArrayList<String> i = new ArrayList<String>(Arrays.asList("me", "myself"));
		ArrayList<String> like = new ArrayList<String>(Arrays.asList("love", "adore"));
		ArrayList<String> pie = new ArrayList<String>(Arrays.asList("cake", "dessert", "cupcake"));

		dictionary.put("Hello", hello);
		dictionary.put("World", world);
		dictionary.put("I", i);
		dictionary.put("like", like);
		dictionary.put("pie", pie);
	}

	public static String convertListToString(ArrayList<String> sentence) {
		String s = "";
		for(int i = 0; i < sentence.size(); i++) {
			s += sentence.get(i) + " ";
		}
		return s;
	}

	public static String getTranslation(String foreignWord) {
		//currently gets the first English translation from the list of possible translations
		//FOR LATER: implement n-gram stuff to choose best translation of the word.
		//May need to change function header for this ^^ depending on choice of n
		ArrayList<String> possibleTranslations = dictionary.get(foreignWord);
		return possibleTranslations.get(0);
	}
    
    public static ArrayList<String> trimPastTense(ArrayList<String> sentence){
        //look at part of speech tags and remove auxilary verbs from passe compose constructions (either look at word before word with past participle tag and remove if etre or avoir, or find tag for these auxilary verbs)
    }

	public static ArrayList<String> translateSentence(ArrayList<String> sentence) {
		if (sentence.isEmpty()) {
            return new ArrayList<String>();
        }

        ArrayList<String> bestSentence = new ArrayList<String>();
        for(int i = 0; i < sentence.size(); i++) {
        	String frenchWord = sentence.get(i);
        	String englishTranslation = Translation.getTranslation(frenchWord);
        	bestSentence.add(englishTranslation);
        }
        return bestSentence;
	}

	public static void eval() {
		Translation tfe = new Translation("", "");

		for(int i = 0; i < sentences.size(); i++) {
			ArrayList<String> s = sentences.get(i);
			ArrayList<String> translation = translateSentence(s);

			System.out.println("The French Sentence");
			System.out.println(Translation.convertListToString(s));
			System.out.println("gets translated to");
			System.out.println(Translation.convertListToString(translation));
			System.out.println();
		}
	}

	public static void main(String[] args) {
        Translation.eval();
    }
}