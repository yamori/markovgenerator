package org.kindzerske.markov.markovgenerator;

import junit.framework.TestCase;

/**
 * Test the Markov class
 * @author matthew.kindzerske
 *
 */
public class MarkovTest extends TestCase {
	
	String testSubString = "test";
	Character testChar = 'c';
	Character secondTestChar = 'd';
	Markov markov;
	
	public void testConstructor() {
		markov = new Markov(testSubString, testChar);
		
		assertTrue(markov.getCount() == 1);
		assertTrue(markov.getSubString().equalsIgnoreCase(testSubString));
		assertTrue(markov.getFrequencyCount(testChar) == 1);
	}
	
	public void testAdd() {
		markov = new Markov(testSubString, testChar);
		markov.add(secondTestChar);
		assertTrue(markov.getCount() == 2);
		assertTrue(markov.getSubsequentMap().size()==2);
		System.out.println(markov.toString());
	}
	
	public void testRandomChar() {
		markov = new Markov(testSubString, testChar);
		assertTrue(markov.getRandomSubsequentChar()==testChar);
		for (int n=0; n<10; n++) {
			markov.add(testChar);
		}
		// Still should return the same char
		assertTrue(markov.getRandomSubsequentChar()==testChar);
	}

}
