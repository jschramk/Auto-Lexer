import java.util.List;

public class Test {

  public static void main(String[] args) {

    MultiDFAConstructor<Character, String> constructor = new MultiDFAConstructor<>();

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
        .add(Regex.parse("<O"+RegexCollection.INTEGER+">"), "operand");


    DFA<Character, String> dfa = constructor.buildDFA();

    InputClassifier<Character, String> classifier = new InputClassifier<>(dfa);

    String text = "log(2, 2^5) * <O1>";

    List<Character> input = StringSection.toCharacterList(text);

    List<InputClassifier<Character, String>.Classification> classifications = classifier.segmentWhole(input);

    System.out.println("Input: \"" + text + "\"\n");

    for (InputClassifier<Character, String>.Classification c : classifications) {
      System.out.println("\"" + StringSection.listToString(c.sectionOf(StringSection.toCharacterList(text))) + "\": " + c.classification());
    }


  }


}
