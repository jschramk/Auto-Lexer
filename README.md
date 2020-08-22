# Multi-Output-DFA
### What's this?
At its core, this project is a Regular Expression to Deterministic Finite Automaton converter. But wait, there's more! Typically, DFAs are only capable of binary classification of input strings, but this project allows for the automatic construction of DFAs with multiple recognized Regular Expressions, each with its own classification. For example, one could create a DFA that accepts the input "cat" as class 1 and "(dog|bird)\*" as class 2. This allows input classification to be done in O(n) time where n is the length of the input, as opposed to O(n*d) where d is the number of separate DFAs needed to specify different classifications. Additionally, there is an enclosed class which can take some input and find each subsection therein that is recognized by the DFA and label it with its corresponding classification. What's more is that the input and output types are generic, allowing for sequence recognition of any reference type with any type of output.
### How does it work?
Regular Expressions can be parsed from Strings or constructed manually. They are then converted to an Epsilon Non-deterministic Finite Automaton which is subsequently used to construct a min-DFA with multiple outputs. In the case of overlapping expressions (e.g. "abcabc" being a subset of "(abc)*"), preference is given to the output that was added last. This allows for general case expressions with multiple special cases added on top.
### What is it good for?
This project is the ultimate tool for Regular Language processing. Due to its generic nature, it can be used on multiple levels of abstraction. For example, it can be used to find recognized words in a string of text, and those words could be fed into a higher layer to find phrases of different kinds, which could then be used to detect more abstract meaning and so on. Please note that this differs from Natural Language processing, which cannot be easily defined by Regular Expressions. This project is best for applications with strict syntax such as mathematics or programming languages.
### Just get to the good part.
Fine. Here's a super simple example of what this project is capable of. Here, we are simply labelling substrings with the Regular Expressions they match.
~~~
MultiDFAConstructor<Character, String> constructor = new MultiDFAConstructor<>();

constructor
    .add(Regex.parse("(abc)*"), "(abc)*")
    .add(Regex.parse("abcabc"), "abcabc")
    .add(Regex.parse("(ab|cd)*"), "(ab|cd)*");
    
DFA<Character, String> dfa = constructor.buildDFA();

InputClassifier<Character, String> classifier = new InputClassifier<>(dfa);

String text = "abc abcabc ababcdabcdab";

List<Character> input = StringSection.toCharacterList(text);
List<InputClassifier<Character, String>.Classification> classifications =
    classifier.findAll(input);

System.out.println("Input: \"" + text + "\"\n");
for (InputClassifier<Character, String>.Classification c : classifications) {
  if(c.classification() == null) continue;
  System.out.println(String.format("\"%s\": %s",
      StringSection.listToString(c.sectionOf(StringSection.toCharacterList(text))),
      c.classification()));
}
~~~
**Output:**
~~~
Input: "abc abcabc ababcdabcdab"

"abc": (abc)*
"abcabc": abcabc
"ababcdabcdab": (ab|cd)*
~~~
