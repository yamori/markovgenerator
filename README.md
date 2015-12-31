# MarkovGenerator
(Java) Markov chain text generator.
## Data Structure
A sample text file is supplied which is used to train the dictionary of keys (of user determined order 'k'), and subsequent next characters. The training from the sample text is exhaustive and is by character (not words).

For performance, the key is is hashed and placed into a respective linked-list of Markov 'mappings.'  During training the hash table will resize once a load factor has been hit.

## Usage

### CLI (using project sample texts)
`java -jar MarkovGenerator-with-dependencies.jar -i`

The `i` flag is for 'interactive' and takes precedence over all other flags.

A Java scanner will walk the user through execution.  (Note how the verbose mode will indicate bifurcations.)

```
 Enter kOrder (usually ranged 6..8): 7
 Enter desired output length (i.g. 300): 20
 Verbose (y/n)?: y
[0] PaulGraham_September2013.txt
[1] BarackObama_2008.txt
[2] PaulGraham_September2013_veryshort.txt
 Choose from above texts: 0

long as
long as
  Bifurcation: 'ong as '; [t, y]
long as t
long as th
long as the
  Bifurcation: ' as the'; [ ,  ,  ,  , s, y, y, y]
long as the
  Bifurcation: 'as the '; [b, f, n, n]
long as the n
  Bifurcation: 's the n'; [e, o]
long as the no
  Bifurcation: ' the no'; [ ,  , t]
long as the not
long as the note
long as the note
long as the note w
long as the note wi
  Bifurcation: 'note wi'; [l, t]
*** Final Generated Text (kOrder=7, textLength=20, textFileLocation='/sample_texts/PaulGraham_September2013.txt' )
long as the note wit
```
