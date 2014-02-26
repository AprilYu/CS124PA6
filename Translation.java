import java.util.*;
import java.io.*;
import java.lang.StringBuilder;

public class Translation {
	static ArrayList<ArrayList<TaggedWord>> sentences;
	static Map<String, ArrayList<String>> dictionary;
	
	public static final String DEFAULT_DICT = "data/dictionary.csv";
	public static final String DEV_SENTENCES = "data/taggedTrainSentences.txt";
	
	public static final String NO_TAG = "NO_TAG";
	
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
	public ArrayList<ArrayList<TaggedWord>> readSentences(String sentenceFile) throws IOException {
		ArrayList<ArrayList<TaggedWord>> sentences = new ArrayList<ArrayList<TaggedWord>>();
		BufferedReader rd = new BufferedReader(new FileReader(new File(sentenceFile)));
		String line = null;
		int lineNum = 0;
		while((line = rd.readLine()) != null) {
			line = line.toLowerCase();
			if ("".equals(line)) {
				continue;
			}	
			ArrayList<TaggedWord> tokenizedLine = tokenizeLine(line);
			sentences.add(tokenizedLine);
			lineNum++;
		}
		return sentences;
	}
	
	/*
	 * Tokenizes the given line by taking groups of consecutive alphabetic characters and
	 * treating them as tokens, ignoring punctuation
	 */
	private ArrayList<TaggedWord> tokenizeLine(String line) {
		ArrayList<TaggedWord> tokenizedLine = new ArrayList<TaggedWord>();
		String[] splitLine = line.split(" ");
		for (String token : splitLine) {
			List<String> cleanedToken = tokenizeOnPunctuation(token);
			for (String token2 : cleanedToken) {
				// now we should have the tagged token
				tokenizedLine.add(new TaggedWord(token2.trim()));
			}
		}
		return tokenizedLine;
	}
	
	private List<String> tokenizeOnPunctuation(String token) {
		List<String> cleanedToken = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < token.length(); i++) {
			String toCheck = token.substring(i, i+1);
			if (isPunctuation(toCheck)) {
				if (builder.length() > 0) {
					cleanedToken.add(builder.toString());
				}
				if (shouldKeepPunctuation(toCheck)) {
					cleanedToken.add(toCheck);
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
	
	private boolean shouldKeepPunctuation(String token) {
		switch (token.charAt(0)) {
			case '.':
			case ',':
			case '!':
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
	
	public static String convertTaggedListToString(ArrayList<TaggedWord> sentence) {
		String s = "";
		for(int i = 0; i < sentence.size(); i++) {
			s += sentence.get(i).word + " ";
		}
		return s;
	}

	public static String getTranslation(String foreignWord) {
		//currently gets the first English translation from the list of possible translations
		//FOR LATER: implement n-gram stuff to choose best translation of the word.
		//May need to change function header for this ^^ depending on choice of n
		ArrayList<String> possibleTranslations = dictionary.get(foreignWord);
		if (possibleTranslations == null) {
			//System.out.println("Possible Error: No entry for word " + foreignWord);
			return foreignWord;
		}
		return possibleTranslations.get(0);
	}
    
    public static ArrayList<TaggedWord> trimPastTense(ArrayList<TaggedWord> sentence){
        //look at part of speech tags and remove auxilary verbs from passe compose constructions (either look at word before word with past participle tag and remove if etre or avoir, or find tag for these auxilary verbs)

        /*Iterator<String> it = sentence.iterator();
        boolean prevVerb=false;
        while(it.hasNext()){
            TaggedWord w = it.next();
            if(w.tag.equals("_V")){
                if(prevVerb){
                    it.remove();
                }
                prevVerb=true;
            }else{
                prevVerb=false;
            }
        }*/
        boolean prevVerb=false;
        for(int i=0;i<sentence.size();i++){
        	TaggedWord w = sentence.get(i);
        	if(w.tag.equals("v")){
        		if(prevVerb){
        			//System.out.println("#################past"+w.word+" "+sentence.get(i-1).word);
                    sentence.remove(i-1);
                    sentence.add(i-1,new TaggedWord("","NO_TAG"));
                }
                prevVerb=true;
            }else{
                prevVerb=false;
        	}
        }

        return sentence;
    }

    public static ArrayList<TaggedWord> preProcess(ArrayList<TaggedWord> sentence){
    	sentence = trimPastTense(sentence);
    	return sentence;

    }
    


	public static ArrayList<TaggedWord> translateSentence(ArrayList<TaggedWord> sentence) {
		if (sentence.isEmpty()) {
            return new ArrayList<TaggedWord>();
        }

        ArrayList<TaggedWord> bestSentence = new ArrayList<TaggedWord>();
        for(int i = 0; i < sentence.size(); i++) {
        	String frenchWord = sentence.get(i).word;
        	String englishTranslation = Translation.getTranslation(frenchWord);
        	bestSentence.add(new TaggedWord(englishTranslation, sentence.get(i).tag));
        }
        return bestSentence;
	}

	public static void eval(String sentenceFile, String dictFile) {
		Translation tfe = new Translation(sentenceFile, dictFile);

		for(int i = 0; i < sentences.size(); i++) {
			ArrayList<TaggedWord> s = sentences.get(i);
			s = preProcess(s);
			ArrayList<TaggedWord> translation = translateSentence(s);
			
			translation = reorderNounAdjPairs(translation);
			
			
			System.out.println("###\nThe French Sentence:");
			System.out.println(Translation.convertTaggedListToString(s));
			System.out.println("   gets translated to:");
			System.out.println(Translation.convertTaggedListToString(translation));
			System.out.println();
		}
	}
	
	private static ArrayList<TaggedWord> reorderNounAdjPairs(ArrayList<TaggedWord> sentence) {
		System.out.println("Re-ordering adjective/noun pairs...");
		for (int i = 0; i < sentence.size() - 1; i++) {
			String tag1 = sentence.get(i).tag;
			String tag2 = sentence.get(i + 1).tag;
			if (isNoun(tag1) && isAdj(tag2)) {
				TaggedWord adj = sentence.get(i + 1);
				sentence.remove(i + 1);
				sentence.add(i, adj);
			}
		}
		return sentence;
	}
	
	private static boolean isNoun(String tag) {
		if ("n".equals(tag))
			return true;
		return false;
	}
	
	private static boolean isAdj(String tag) {
		if ("a".equals(tag))
			return true;
		return false;
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
    
    public static class TaggedWord{
    	public final String word;
    	public final String tag;
    	
    	public TaggedWord(String word, String tag) {
    		this.word = word;
    		this.tag = tag;
    	}
    	
    	public TaggedWord(String unsplitWord) {
    		if (unsplitWord.indexOf('_') == -1) {
    			this.word = unsplitWord;
    			this.tag = NO_TAG;
    		} else {
    			String[] splitWord = unsplitWord.split("_");
    			this.word = splitWord[0];
    			this.tag = splitWord[1];
    		}
    		//System.out.println("Parsed " + unsplitWord + " as " + this.word + " _ " + this.tag);
    	}
    }
}