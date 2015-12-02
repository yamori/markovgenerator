package org.kindzerske.markov.markovgenerator;

import java.io.File;
import java.io.FilenameFilter;
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
	
	private int kOrder;
	private int textLength;
	private File textFile;

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		// Get basic parameters
		System.out.print(" Enter kOrder (usually ranged [6..8]): ");
		int k = scanner.nextInt();
		System.out.print(" Enter desired output length: ");
		int m = scanner.nextInt();
		
		// List texts and get selection
		File dir = new File("src\\main\\resources\\");
		File [] files = dir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".txt");
		    }
		});

		for (int n=0; n<files.length; n++) {
			System.out.println("[" + n + "] " + files[n]);
		}
		System.out.print(" Choose from above texts: ");
		int textN = scanner.nextInt();
		System.out.println();

		
		TextGenerator textGenerator = new TextGenerator(k,m,files[textN]);
	}
	
	public TextGenerator(int kOrder, int textLength, File textFile) {
		this.kOrder = kOrder;
		this.textLength = textLength;
		this.textFile = textFile;
	}
}
