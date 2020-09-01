package dfa.utils;

import java.util.List;


public class Test {

  public static void main(String[] args) {

    MultiDFAConstructor<Character, String> constructor = new MultiDFAConstructor<>();

    constructor.add(Regex.parse(RegexCollection.INTEGER), "int");
    constructor.add(Regex.parse(RegexCollection.FLOAT), "float");

    DFA<Character, String> dfa = constructor.buildDFA();



    InputClassifier<Character, String> classifier = new InputClassifier<>(dfa);

    List<Character> input = InputSection.characterListOf("5. 1.5 .5 1.5.");

    List<InputSection<Character, String>> all = classifier.findAll(input);

    for (InputSection<Character, String> section : all) {
      System.out.println(section.sectionOf(input) + ": " + section.getLabel());
    }

  }


}
