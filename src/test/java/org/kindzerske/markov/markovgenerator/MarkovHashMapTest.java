package org.kindzerske.markov.markovgenerator;

import junit.framework.TestCase;

/**
 * Test the MarkovHashMap class
 * 
 * @author matthew.kindzerske
 *
 */
public class MarkovHashMapTest extends TestCase {

	public void testConstructor() {
		MarkovHashMap<String, Markov> markovHashMap = new MarkovHashMap<String, Markov>(11, (float) 0.75);
		assertTrue(markovHashMap.getHashMapTableContentCount() == 0);
	}

	public void testResizingFeature() {
		int startingHashMapSize = 11;
		MarkovHashMap<String, Integer> markovHashMap = new MarkovHashMap<String, Integer>(startingHashMapSize,
				(float) 0.5);
		assertTrue(markovHashMap.getHashMapTableSize() == startingHashMapSize);

		for (int n = 0; n < 15; n++) {
			markovHashMap.put("" + n, n);
		}

		System.out.println(markovHashMap.toString());
		// The amortization should take this to the next prime number at least
		// twice as large as previous size.
		assertTrue(markovHashMap.getHashMapTableSize() == 23);
	}

	public void testContainsKeyMethod() {
		String existingKey = "abc";
		String nonExistingKey = "def";
		
		MarkovHashMap<String, Integer> markovHashMap = new MarkovHashMap<String, Integer>(11, (float) 0.5);
		
		markovHashMap.put(existingKey, 1);
		// nonExistingKey is not 'put' into the hash
		
		assertTrue(markovHashMap.containsKey(nonExistingKey)==false);
		assertTrue(markovHashMap.containsKey(existingKey)==true);
	}
	
	public void testGetMethod() {
		String existingKey = "abc";
		int existingValue = 2;
		MarkovHashMap<String, Integer> markovHashMap = new MarkovHashMap<String, Integer>(11, (float) 0.5);
		markovHashMap.put(existingKey, existingValue);
		assertTrue(markovHashMap.get(existingKey)==existingValue);
	}

}
