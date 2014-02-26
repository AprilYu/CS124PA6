import java.util.*;
import java.io.*;
import java.lang.StringBuilder;

public class Translation {
	static ArrayList<ArrayList<String>> sentences;
	static Map<String, ArrayList<String>> dictionary;
	
	public static final String DEFAULT_DICT = "data/dictionary.csv";
	public static final String DEV_SENTENCES = "data/trainSentences.txt";

	public Translation(String corpusFileName, String dictionaryFileName) {
		try {
			sentences = readSentences(corpusFileName);
			dictionary = readInDictionary(dictionaryFileName);
		} catch (IOException e) {
			System.out.println("Error while reading sentence or dict file: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/*
	 * Reads in a csv file, where each line has the french word as the first csv value
	 * and all possible translations as the next csv values in the line
	 */
	public Map<String, ArrayList<String>> readInDictionary(String dictFile) throws IOException {
		Map<String, ArrayList<String>> dict = new HashMap<String, ArrayList<String>>();
		BufferedReader rd = new BufferedReader(new FileReader(new File(dictFile)));
		String line = null;
		int lineNum = 0;
		while((line = rd.readLine()) != null) {
			line = line.toLowerCase();
			String[] splitLine = line.split(",");
			String word = splitLine[0].trim();
			if (splitLine.length < 2)
				throw new IOException("Error: no provided translations for word " + word + " on line " + lineNum);
			ArrayList<String> possibleTranslations = new ArrayList<String>();
			for (int i = 1; i < splitLine.length; i++) {
				String trans = splitLine[i].trim();
				if (!"".equals(trans))
					possibleTranslations.add(trans);
			}
			dict.put(word, possibleTranslations);
			lineNum++;
		}
		return dict;
	}
	
	/*
	 * Reads in sentences from a file. Sentences should be written one per line in the specified file.
	 */
	public ArrayList<ArrayList<String>> readSentences(String sentenceFile) throws IOException {
		ArrayList<ArrayList<String>> sentences = new ArrayList<ArrayList<String>>();
		BufferedReader rd = new BufferedReader(new FileReader(new File(sentenceFile)));
		String line = null;
		int lineNum = 0;
		while((line = rd.readLine()) != null) {
			line = line.toLowerCase();
			if ("".equals(line)) {
				continue;
			}	
			ArrayList<String> tokenizedLine = tokenizeLine(line);
			sentences.add(tokenizedLine);
			lineNum++;
		}
		return sentences;
	}
	
	/*
	 * Tokenizes the given line by taking groups of consecutive alphabetic characters and
	 * treating them as tokens, ignoring punctuation
	 */
	private ArrayList<String> tokenizeLine(String line) {
		ArrayList<String> tokenizedLine = new ArrayList<String>();
		String[] splitLine = line.split(" ");
		for (String token : splitLine) {
			List<String> cleanedToken = removePunctuation(token);
			for (String token2 : cleanedToken) {
				tokenizedLine.add(token2.trim());
			}
		}
		return tokenizedLine;
	}
	
	private List<String> removePunctuation(String token) {
		List<String> cleanedToken = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < token.length(); i++) {
			String toCheck = token.substring(i, i+1);
			if (isPunctuation(toCheck)) {
				if (builder.length() > 0) {
					cleanedToken.add(builder.toString());
				}
				builder = new StringBuilder();
			} else {
				builder.append(toCheck);
			}
		}
		if (builder.length() > 0) {
			cleanedToken.add(builder.toString());
		}
		return cleanedToken;
	}
	
	private boolean isPunctuation(String token) {
		if (token.length() == 0)
			return true;
		switch (token.charAt(0)) {
			case '.':
			case ',':
			case '!':
			case '\'':
			case '"':
			case '+':
			//case '-':
			//case "_":
			case '&':
			case '$':
			case '#':
			case '@':
			case '~':
			case '`':
			case '(':
			case ')':
			case '{':
			case '}':
			case '[':
			case ']':
			case '?':
			case ';':
			case ':':
			case '^':
			case '*':
				return true;
			default:
				return false;
		}
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
		if (possibleTranslations == null) {
			System.out.println("Error: No entry for word " + foreignWord);
			return "NO_TRANS";
		}
		return possibleTranslations.get(0);
	}
    
    public static ArrayList<String> trimPastTense(ArrayList<String> sentence){
        //look at part of speech tags and remove auxilary verbs from passe compose constructions (either look at word before word with past participle tag and remove if etre or avoir, or find tag for these auxilary verbs)
		return null;
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

	public static void eval(String sentenceFile, String dictFile) {
		Translation tfe = new Translation(sentenceFile, dictFile);

		for(int i = 0; i < sentences.size(); i++) {
			ArrayList<String> s = sentences.get(i);
			ArrayList<String> translation = translateSentence(s);

			System.out.println("###\nThe French Sentence:");
			System.out.println(Translation.convertListToString(s));
			System.out.println("   gets translated to:");
			System.out.println(Translation.convertListToString(translation));
			System.out.println();
		}
	}


	// args[0] is the sentence file to translate (defaults to the dev sentences)
	// args[1] is an optional alternate dictionary file
	public static void main(String[] args) {
		String sentenceFile = DEV_SENTENCES;
		String dictFile = DEFAULT_DICT;
		if (args.length > 0)
			sentenceFile = args[0];
		if (args.length > 1)
			dictFile = args[1];
		System.out.println("Translating sentences in " + sentenceFile + " using dict " + dictFile);
        Translation.eval(sentenceFile, dictFile);
    }
}