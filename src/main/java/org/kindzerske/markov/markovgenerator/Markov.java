package org.kindzerske.markov.markovgenerator;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * Represents a k-character string, a count of all occurrences, and a TreeMap of
 * all encountered occurrences with subsequent char
 * 
 * @author matthew.kindzerske
 * 
 */
public class Markov {

	private String subString;
	private int count = 0;
	private TreeMap<Character, Integer> subsequentMap;

	/**
	 * Constructor using a substring and subsequent char. (i.e. 'the plac' and
	 * 'e')
	 * 
	 * @param subString
	 * @param subsequentChar
	 */
	public Markov(String subString, Character subsequentChar) {
		this.subString = subString;
		subsequentMap = new TreeMap<Character, Integer>();
		// Add the char
		add(subsequentChar);
	}

	/**
	 * Adds the occurrence of the char, keeps track if it already exists.
	 * 
	 * @param subsequentChar
	 */
	public void add(Character subsequentChar) {
		// Add a subsequentChat to the mapping and keep accounting
		addOneToCount();
		if (subsequentMap.containsKey(subsequentChar)) {
			// Update the count for this char
			int charCount = subsequentMap.get(subsequentChar);
			subsequentMap.put(subsequentChar, charCount + 1);
		} else {
			// Add the new char
			subsequentMap.put(subsequentChar, 1);
		}
	}

	/**
	 * Using the frequency counts, properly weight each char and sample
	 * appropriately to return a subsequent character.
	 * 
	 * @return subsequent char based on frequency counts
	 */
	public char getRandomSubsequentChar() {
		Character returnChar = null;
		ArrayList<Character> weightedSubsequentCharArray = new ArrayList<Character>();

		for (Entry<Character, Integer> entry : subsequentMap.entrySet()) {
			// Iterate over each character
			char currentChar = entry.getKey();
			for (int n = 0; n < entry.getValue(); n++) {
				// Iterate over number of occurrences
				weightedSubsequentCharArray.add(currentChar);
			}
		}

		// Print out any opportunities for the generated text to bifurcate
		if (subsequentMap.size() > 1) {
			System.out.println("  Bifurcation: '" + this.subString + "'; " + weightedSubsequentCharArray.toString());
		}

		Random rand = new Random();
		int retIndex = rand.nextInt(weightedSubsequentCharArray.size());
		returnChar = weightedSubsequentCharArray.get(retIndex);

		return returnChar;
	}

	private void addOneToCount() {
		count++;
	}

	/**
	 * Get total count of all subsequentChar occurences for this Markov instance
	 * 
	 * @return int count
	 */
	public int getCount() {
		return count;
	}

	public String getSubString() {
		return this.subString;
	}

	/**
	 * Return the frequency count of a specified char, if not found returns -1
	 * 
	 * @param subsequentChar
	 *            Queried subsequentChar
	 * @return int Count of occurrences, or -1 if not found.
	 */
	public int getFrequencyCount(Character subsequentChar) {
		if (!subsequentMap.containsKey(subsequentChar)) {
			return -1;
		} else {
			return subsequentMap.get(subsequentChar);
		}
	}

	/**
	 * TreeMap representation of the subsequentChars
	 * 
	 * @return TreeMap
	 */
	public TreeMap<Character, Integer> getSubsequentMap() {
		return this.subsequentMap;
	}

	/**
	 * String formatted representation of the Markov instance.
	 */
	public String toString() {
		String returnString = this.subString + ":";
		for (Entry<Character, Integer> entry : subsequentMap.entrySet()) {
			returnString += "\n  " + entry.getKey() + " (" + entry.getValue() + ")";
		}
		return returnString;
	}

}
