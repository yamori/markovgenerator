package org.kindzerske.markov.markovgenerator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

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

	// CLI options
	private final static String INTERACTIVE_FLAG = "i";
	private final static String FILE_PATH_FLAG = "f";
	private final static String MARKOV_ORDER_FLAG = "k";
	private final static String TEXT_LENGTH_FLAG = "m";
	private final static String VERBOSITY_FLAG = "v";
	

	public static void main(String[] args) {

		// @formatter:off
		/**
		 * Handles the CLI arguments, there are two modes: 
		 *   1) CLI params for execution 
		 *   2) -i interactive flag for Scanner input (demo purposes)
		 */
		//@formatter:on
		Options options = new Options();
		options.addOption(INTERACTIVE_FLAG,
				"interactive mode for demo purposes, this flag will take precedence over other cli flags");
		options.addOption(FILE_PATH_FLAG, true, "path to text file with sample text for markov generation");
		options.addOption(MARKOV_ORDER_FLAG, true, "markov parameter for key length");
		options.addOption(TEXT_LENGTH_FLAG, true,
				"desired length of output text which may not be fulfilled if sample text is not sufficiently large");
		options.addOption(VERBOSITY_FLAG,"indicates verbose command line output");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmdLine = null;
		try {
			cmdLine = parser.parse(options, args);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		// Parameters needed for execution
		int markovKeyLength = 0;
		int desiredTextLength = 0;
		String fileNamePath = "";
		boolean verboseFlag = true;

		// Deciding on interactive/scanner mode, or if all params were included
		// via command line
		if (cmdLine.hasOption(INTERACTIVE_FLAG)) {
			Scanner scanner = new Scanner(System.in);

			// Get basic parameters
			System.out.print(" Enter kOrder (usually ranged 6..8): ");
			markovKeyLength = scanner.nextInt();
			System.out.print(" Enter desired output length (i.g. 300): ");
			desiredTextLength = scanner.nextInt();
			System.out.print(" Verbose (y/n)?: ");
			verboseFlag = scanner.next().trim().contains("y") ? true : false;

			// List texts for selection
			String[] sampleTexts = {};
			try {
				sampleTexts = Utilities.getResourceListing(TextGenerator.class, Utilities.SAMPLE_TEXTS_DIR);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (int n = 0; n < sampleTexts.length; n++) {
				System.out.println("[" + n + "] " + sampleTexts[n]);
			}

			// Get choice
			System.out.print(" Choose from above texts: ");
			int textN = scanner.nextInt();
			fileNamePath = "/" + Utilities.SAMPLE_TEXTS_DIR + sampleTexts[textN].toString();
			System.out.println();

			scanner.close();
		} else if (cmdLine.hasOption(FILE_PATH_FLAG) & cmdLine.hasOption(MARKOV_ORDER_FLAG)
				& cmdLine.hasOption(TEXT_LENGTH_FLAG)) {
			// Get all the params from the CLI
			markovKeyLength = Integer.parseInt(cmdLine.getOptionValue(MARKOV_ORDER_FLAG));
			desiredTextLength = Integer.parseInt(cmdLine.getOptionValue(TEXT_LENGTH_FLAG));
			fileNamePath = cmdLine.getOptionValue(FILE_PATH_FLAG);
			verboseFlag = cmdLine.hasOption(VERBOSITY_FLAG)? true : false;
		} else {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar MarkovGenerator.jar [-flags]", " Use -i alone, or {-f,-k,-m} together",
					options, "");
			// Parameters not correctly determined, terminate.
			return;
		}

		// Manage the verbosity of TextGenerator
		PrintStream originalStream = System.out;
		PrintStream dummyStream = new PrintStream(new OutputStream() {
			public void write(int b) {
				// NO-OP
			}
		});
		if (!verboseFlag) {
			System.setOut(dummyStream);
		}

		TextGenerator textGenerator = new TextGenerator();
		String generatedText = textGenerator.generateString(markovKeyLength, desiredTextLength, fileNamePath);

		// Revert the System.out after verbosity management
		System.setOut(originalStream);

		// Print results
		System.out.println(String.format("*** Final Generated Text (kOrder=%d, textLength=%d, textFileLocation='%s' )",
				markovKeyLength, desiredTextLength, fileNamePath));
		System.out.println(generatedText); // Also can be access with
											// getGenerated
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
		readTextStreamToSampleText();

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

	/**
	 * Takes the text location and tries to read as a project resource, if not
	 * found then tries to read as a system file. Method appends entire file to
	 * class var (while turning carriage returns into whitespace)
	 */
	private void readTextStreamToSampleText() {
		// Accumulates the sample text.
		StringBuilder stringBuilder = new StringBuilder();

		InputStream in = this.getClass().getResourceAsStream(this.textFileLocation);
		// Need to infer whether the location is a project resource, or a path
		// to an external file
		if (in != null) {
			// Found a project resource
			int r;
			try {
				while ((r = in.read()) != -1) {
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
		} else {
			// textFileLocation does not describe a project resource, instead
			// attempt to read as a File
			FileReader fileReader = null;
			try {
				fileReader = new FileReader(this.textFileLocation);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
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
