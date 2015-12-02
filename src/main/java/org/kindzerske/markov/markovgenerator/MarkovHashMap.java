package org.kindzerske.markov.markovgenerator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Hash table, implemented for Markov text generation purpose. Under the hash
 * map is a linked list of Markov classes
 * 
 * @author matthew.kindzerske
 *
 * @param <K>
 * @param <V>
 */
public class MarkovHashMap<K, V> {

	public class HashMapEntry {
		protected K key;
		protected V value;

		// Constructor
		public HashMapEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		// Easy access to the hash code for a HashMapEntry
		public int hashCode() {
			return key.hashCode();
		}

		@SuppressWarnings("unused")
		public boolean equals(Object obj) {
			boolean returnVal = false;

			if (obj == null) {
				returnVal = false;
			} else if (this == null) {
				returnVal = false;
			}
			@SuppressWarnings("unchecked")
			HashMapEntry other = (HashMapEntry) obj;
			if (key.equals(other.key)) {
				returnVal = true;
			}

			return returnVal;
		}
	}

	private LinkedList<HashMapEntry> hashMapTable[];
	private int hashMapTableSize;
	private float loadFactor; // Used for deciding when to resizes
	private ArrayList<Integer> primes = new ArrayList<Integer>();

	@SuppressWarnings("unchecked")
	public MarkovHashMap(int capacity, float loadFactor) {
		hashMapTable = (LinkedList<HashMapEntry>[]) new LinkedList[capacity];
		this.loadFactor = loadFactor;
		this.hashMapTableSize = 0;

		for (int i = 0; i < hashMapTable.length; i++) {
			hashMapTable[i] = new LinkedList<HashMapEntry>();
		}

		setPrimesForResizing();
	}

	private void setPrimesForResizing() {
		// Used for resizing the hash when beyond the loadFactor.
		primes.add(11);
		primes.add(23);
		primes.add(47);
		primes.add(97);
		primes.add(197);
		primes.add(397);
		primes.add(797);
		primes.add(1597);
		primes.add(3203);
		primes.add(6421);
		primes.add(12853);
		primes.add(25717);
		primes.add(51437);
		primes.add(102877);
		primes.add(205759);
		primes.add(411527);
		primes.add(823117);
		primes.add(1646237);
		primes.add(3292489);
		primes.add(6584983);
		primes.add(13169977);
		primes.add(26339969);
		primes.add(52679969);
		primes.add(105359939);
		primes.add(210719881);
		primes.add(421439783);
		primes.add(842879579);
		primes.add(1685759167);
	}
	
	private void resize() {
		MarkovHashMap<K, V> tmp = new MarkovHashMap<K, V>(primeHelper(hashMapTable.length), (float) 0.75);
		
		
		for (LinkedList<HashMapEntry> hashMapEntryList : hashMapTable) {
			for (HashMapEntry hashMapEntry : hashMapEntryList) {
				K key = hashMapEntry.key;
				V value = hashMapEntry.value;
				tmp.put(key, value);
			}
		}
		this.hashMapTable = tmp.hashMapTable;
		this.hashMapTableSize = tmp.hashMapTableSize;
		this.loadFactor = tmp.loadFactor;
	}
	
	// Find the next prime for resizing the hashMapTable
	public int primeHelper(int i) {
		int ret = -1;
		for (int j = 0; j < primes.size(); j++) {
			if (primes.get(j) > i) {
				ret = primes.get(j);
				break;
			}
		}
		return ret;
	}

	// Returns the number of entries added to the hashMapTable
	public int getHashMapTableContentCount() {
		return hashMapTableSize;
	}

	// Returns the length of the hashMapTable
	public int getHashMapTableSize() {
		return hashMapTable.length;
	}

	public V put(K key, V value) {
		HashMapEntry entry = new HashMapEntry(key, value);
		int hashCode = Math.abs(key.hashCode());
		int mapping = hashCode % (hashMapTable.length);
		LinkedList<HashMapEntry> bin = hashMapTable[mapping];
		V ret = null;

		if (bin.contains(entry)) {
			int index = bin.indexOf(entry);
			ret = bin.get(index).value;
			bin.set(index, entry);
		} else {
			bin.add(entry);
		}
		hashMapTableSize++;
		if (((double) hashMapTableSize) / ((double) hashMapTable.length) >= loadFactor) {
			 resize();
		}
		return ret;
	}
	
	public String toString() {
		String returnString = "MarkovHashMap.toString()";
		for (int n=0; n<hashMapTable.length; n++) {
			returnString += "\n bin_" + n;
			LinkedList<HashMapEntry> bin = hashMapTable[n];
			for (int m=0; m<bin.size(); m++) {
				HashMapEntry hashMap = bin.get(m);
				returnString += "\n  " + hashMap.key + " " + hashMap.value.toString();
			}
		}
		return returnString;
	}
}
