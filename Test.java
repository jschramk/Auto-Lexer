

public class Test {

  public static void main(String[] args) {

    MultiDFAConstructor<Character, String> c = new MultiDFAConstructor<>();

    c.add(Regex.parse(RegexCollection.INTEGER), "int")
        .add(Regex.parse(RegexCollection.VARIABLE), "var")
        .add(Regex.parse(RegexCollection.FLOAT), "float")
        .add(Regex.parse("<"+RegexCollection.INTEGER+">"), "operand");

    DFA<Character, String> dfa = c.buildDFA();

    System.out.println(dfa.computeSize());


  }


}
