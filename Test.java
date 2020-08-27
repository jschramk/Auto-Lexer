package dfa.utils;

import java.util.List;

public class Test {

  public static void main(String[] args) {

    MultiDFAConstructor<Character, String> c = new MultiDFAConstructor<>();

    c.add(Regex.parse("<0>^<0>"), "0");

    InputClassifier<Character, String> ic = new InputClassifier<>(c.buildDFA());

    List<Character> input = InputSection.characterListOf("<0>^<0>^<0>");

    System.out.println(ic.findLast(input));



  }


}
