package org.kindzerske.markov.markovgenerator;

import junit.framework.TestCase;

public class TextGeneratorTest extends TestCase {

	public void testSmallOriginalTextScenario() {
		// When the original text is very small, the resulting MarkovHashMap
		// may not be able to fulfill large values of textLength but the
		// class should be robust to handle it the scenario.
		int desiredTextLength = 10000;
		TextGenerator textGenerator = new TextGenerator();

		String generatedText = textGenerator.generateString(5, desiredTextLength,
				Utilities.SAMPLE_TEXTS_DIR + "PaulGraham_September2013_veryshort.txt");

		System.out.println(generatedText);
		assertTrue(generatedText.length() < desiredTextLength);
	}
}
