package org.kindzerske.markov.markovgenerator;

import java.util.TreeMap;

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

	public Markov(String subString, Character subsequentChar) {
		this.subString = subString;
		subsequentMap = new TreeMap<Character, Integer>();
		// Add the char
		add(subsequentChar);
	}

	public void add(Character subsequentChar) {
		addOneToCount();
		if (subsequentMap.containsKey(subsequentChar)) {
			// Update the count for this char
			int charCount = subsequentMap.get(subsequentChar);
			subsequentMap.put(subsequentChar, charCount);
		} else {
			// Add the new char
			subsequentMap.put(subsequentChar, 1);
		}
	}
	
	private void addOneToCount() {
		count++;
	}
	
	public int getCount() {
		return count;
	}
	
	public String getSubString() {
		return this.subString;
	}
	
	public int getFrequencyCount(Character subsequentChar) {
		if (!subsequentMap.containsKey(subsequentChar)) {
			return -1;
		} else {
			return subsequentMap.get(subsequentChar);
		}
	}

}
