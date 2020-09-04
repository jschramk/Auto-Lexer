package dfa.utils;


import java.util.List;


public class Test {

  public static void main(String[] args) {

    System.out.println(Regex.getNextChoices("(a)*|((b)*|(c)*)"));

    System.out.println(Regex.parse("(a)*|((b)*|(c)*)"));
    System.out.println();

    DFAConstructor<Character, String> constructor = new DFAConstructor<>();

    //constructor.add(Regex.parse(RegexCollection.NUMBER), "number");
    constructor.add(Regex.parse(RegexCollection.SCIENTIFIC_NOTATION), "number");
    constructor.add(Regex.parse(RegexCollection.SCIENTIFIC_NOTATION_SIGNED), "number");
    constructor.add(Regex.parse(RegexCollection.NUMBER), "test");

    DFA<Character, String> dfa = constructor.buildDFA();

    //dfa.print();

    Lexer<Character, String> lexer = new Lexer<>(dfa);

    List<Character> input = InputSection.characterListOf(" 1 1.5e-5.4 1e+6 1.5 ");

    List<InputSection<Character, String>> all = lexer.findAll(input);

    for (InputSection<Character, String> is : all) {
      if(is.getLabel() == null) continue;
      System.out.println(InputSection.stringOf(is.sectionOf(input)));
    }

  }


}
