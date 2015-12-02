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

		TextGenerator textGenerator = new TextGenerator(k, m, files[textN].toString());
	}

	public TextGenerator(int kOrder, int textLength, String textFileLocation) {
		this.kOrder = kOrder;
		this.textLength = textLength;
		this.textFileLocation = textFileLocation;

		readTextFile();

		this.markovHashMap = new MarkovHashMap<String, Markov>(11, (float) 0.75);

		constructMarkovHashMap();
		//System.out.println(this.markovHashMap.toString());
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
				stringBuilder.append(ch);
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
			
			System.out.println(subString + "," + subsequentChat);

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
}
