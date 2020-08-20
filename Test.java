import java.util.List;

public class Test {

  public static void main(String[] args) {

    MultiDFAConstructor<String> constructor = new MultiDFAConstructor<>();

    constructor
        .add(Regex.parse(RegexCollection.INTEGER), "int")
        .add(Regex.parse(RegexCollection.FLOAT), "float")
        .add(Regex.parse(RegexCollection.VARIABLE), "var")
        .add(Regex.parse("*"), "multiply")
        .add(Regex.parse("/"), "divide")
        .add(Regex.parse("^|**"), "power")
        .add(Regex.parse("+"), "plus")
        .add(Regex.parse("-"), "minus")
        .add(Regex.parse(" ( )*"), "space")
        .add(Regex.parse("sin"), "func")
        .add(Regex.parse("cos"), "func")
        .add(Regex.parse("tan"), "func")
        .add(Regex.parse("exp"), "func")
        .add(Regex.parse("log"), "func")
        .add(Regex.parse("<"+RegexCollection.INTEGER+">"), "operand");


    DFA<Character, String> dfa = constructor.buildDFA();

    StringClassifier<Character, String> classifier = new StringClassifier<>(dfa);

    String text = "log(2, 5)";

    List<Character> input = StringSection.toCharacterList(text);

    List<StringClassifier.Classification<String>> classifications = classifier.segmentWhole(input);

    System.out.println("Input: \"" + text + "\"\n");

    for (StringClassifier.Classification<String> c : classifications) {
      System.out.println("\"" + c.sectionOf(text) + "\": " + c.classification());
    }


  }


}
