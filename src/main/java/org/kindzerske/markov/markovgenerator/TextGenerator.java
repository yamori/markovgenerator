package org.kindzerske.markov.markovgenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Beginning point for the program, accepts basic parameters for execution
 * before reading file, creating markov hash map, and generating text using the
 * markov hash map.
 * 
 * @author matthew.kindzerske
 * 
 */
public class TextGenerator {

	// At construction/instantiation
	private int kOrder;
	private int textLength;
	// Subsequent member vars
	private String textFileLocation;
	private String originalTextFile;
	MarkovHashMap<String, Markov> markovHashMap;
	private String generatedText;

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		// Get basic parameters
		System.out.print(" Enter kOrder (usually ranged [6..8]): ");
		int k = scanner.nextInt();
		System.out.print(" Enter desired output length: ");
		int m = scanner.nextInt();

		// List texts and get selection
		File dir = new File("src\\main\\resources\\");
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt");
			}
		});

		for (int n = 0; n < files.length; n++) {
			System.out.println("[" + n + "] " + files[n]);
		}
		System.out.print(" Choose from above texts: ");
		int textN = scanner.nextInt();
		System.out.println();

		scanner.close();
		
		TextGenerator textGenerator = new TextGenerator();
		String generatedText = textGenerator.generateString(k, m, files[textN].toString());
		System.out.println(String.format("*** Final Generated Text (kOrder=%d, textLength=%d, textFileLocation='%s' )",
				k, m, files[textN].toString()));
		System.out.println(generatedText); // Also can be access with getGenerated
	}

	/**
	 * Generates the markov hashmap using the included paramaters, and also
	 * generates the markov text (of length textLength) using the created markov
	 * hashmap.
	 * 
	 * @param kOrder
	 *            Number of chars used for substrings for the markov to key off
	 *            of (usually 6..8)
	 * @param textLength
	 *            Length of the desired generated text
	 * @param textFileLocation
	 *            Path+filename to the source text used for constructing the
	 *            markov hashmap
	 * @return
	 */
	public String generateString(int kOrder, int textLength, String textFileLocation) {
		// Capture as instance vars.
		this.kOrder = kOrder;
		this.textLength = textLength;
		this.textFileLocation = textFileLocation;

		// Read the file
		readTextFile();

		// Instantiate the hashmp, and populate it
		this.markovHashMap = new MarkovHashMap<String, Markov>(11, (float) 0.75);
		constructMarkovHashMap();

		// Ultimate the generated string.
		StringBuilder generatedStringBuilder = new StringBuilder();

		// Start with a random K key from the existing MarkovHashMap. This
		// ensures a valid starting point which will have at least one
		// subsequent character. Depending on the nature and length of the
		// original text, this method will attempt to generate a text of length
		// this.textLength
		String subString = this.markovHashMap.getRandomKey();
		generatedStringBuilder.append(subString);

		Markov markov = this.markovHashMap.get(subString);
		while (markov != null && generatedStringBuilder.length() < this.textLength) {
			System.out.println(generatedStringBuilder);
			// Exits if no subsequent Markov is found for the given subString
			generatedStringBuilder.append(markov.getRandomSubsequentChar());
			subString = generatedStringBuilder.substring(generatedStringBuilder.length() - this.kOrder,
					generatedStringBuilder.length());
			// Find the next Markov
			markov = this.markovHashMap.get(subString);
		}

		return generatedStringBuilder.toString();
	}

	private void readTextFile() {
		// Setup the FileReader
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(this.textFileLocation);
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			e.printStackTrace();
		}

		StringBuilder stringBuilder = new StringBuilder();
		int r;
		try {
			while ((r = fileReader.read()) != -1) {
				char ch = (char) r;
				if (ch != '\n' && ch != '\r') {
					// Non-newline characters.
					stringBuilder.append(ch);
				} else {
					// Substitute white space for newlines
					stringBuilder.append(' ');
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.originalTextFile = stringBuilder.toString();
	}

	private void constructMarkovHashMap() {
		// Iterate through the entire this.originalTextFile (wrt kOrder) and put
		// the substring with subsequent char into the hash

		for (int n = 0; n < (this.originalTextFile.length() - this.kOrder); n++) {

			String subString = this.originalTextFile.substring(n, n + this.kOrder);
			Character subsequentChat = this.originalTextFile.charAt(n + this.kOrder);

			if (!this.markovHashMap.containsKey(subString)) {
				// MarkovHashMap does not contain the subString, simple add
				this.markovHashMap.put(subString, new Markov(subString, subsequentChat));
			} else {
				// MarkovHashMap already has this key, update the already
				// existing Markov, then reattach to the MarkovHashMap
				Markov markov = this.markovHashMap.get(subString);
				markov.add(subsequentChat);
				this.markovHashMap.put(subString, markov);
			}
		}
	}

	public String getGeneratedText() {
		return generatedText;
	}
}
