package dfa.utils;

import java.util.List;

public class Example {

  public static void main(String[] args) {

    MultiDFAConstructor<Character, String> constructor = new MultiDFAConstructor<>();

    constructor.add(Regex.parse("(abc)*"), "(abc)*").add(Regex.parse("abcabc"), "abcabc")
        .add(Regex.parse("(ab|cd)*"), "(ab|cd)*");

    DFA<Character, String> dfa = constructor.buildDFA();

    InputClassifier<Character, String> classifier = new InputClassifier<>(dfa);

    String text = "abc abcabc ababcdabcdab";

    List<Character> input = InputSection.characterListOf(text);
    List<InputSection<Character, String>> classifications =
        classifier.findAll(input);

    System.out.println("Input: \"" + text + "\"\n");
    for (InputSection<Character, String> c : classifications) {
      if (c.getLabel() == null)
        continue;
      System.out.println(String.format("\"%s\": %s",
          InputSection.stringOf(c.sectionOf(InputSection.characterListOf(text))),
          c.getLabel()));
    }

  }



}
