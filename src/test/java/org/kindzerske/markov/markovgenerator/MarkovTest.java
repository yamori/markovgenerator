package org.kindzerske.markov.markovgenerator;

import junit.framework.TestCase;

/**
 * Test the Markov class
 * @author matthew.kindzerske
 *
 */
public class MarkovTest extends TestCase {
	
	public void testConstructor() {
		String testSubString = "test";
		Character testChar = 'c';
		Markov m = new Markov(testSubString, testChar);
		
		assertTrue(m.getCount() == 1);
		assertTrue(m.getSubString().equalsIgnoreCase(testSubString));
		assertTrue(m.getFrequencyCount(testChar) == 1);
	}

}
