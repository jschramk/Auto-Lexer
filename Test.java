
import java.util.List;

public class Test {

  public static void main(String[] args) {

    MultiDFAConstructor<String> constructor = new MultiDFAConstructor<>();

    constructor.addRegex(Regex.parse(RegexCollection.INTEGER), "int");
    constructor.addRegex(Regex.parse(RegexCollection.FLOAT), "float");
    constructor.addRegex(Regex.parse(RegexCollection.VARIABLE), "var");
    constructor.addRegex(Regex.parse("(abc)*"), "ABC");
    constructor.addRegex(Regex.parse("abcd"), "ABCD");
    constructor.addRegex(Regex.parse("*"), "multiply");
    constructor.addRegex(Regex.parse("^|**"), "power");
    constructor.addRegex(Regex.parse("+"), "add");
    constructor.addRegex(Regex.parse("-"), "subtract");
    constructor.addRegex(Regex.parse(" ( )*"), "space");
    constructor.addRegex(Regex.parse("3.14"), "pi");
    constructor.addRegex(Regex.parse("pi"), "pi");

    DFA<Character, String> dfa = constructor.buildDFA();

    StringClassifier<Character, String> classifier = new StringClassifier<>(dfa);


    String text = "3.14^2 .1 pi ";

    List<Character> input = StringSection.toCharacterList(text);

    List<StringClassifier.Classification<String>> classifications = classifier.segmentWhole(input);

    System.out.println("Input: \""+text+"\"\n");

    for(StringClassifier.Classification<String> c : classifications){
      System.out.println("\""+c.sectionOf(text)+"\": "+c.classification());
    }


  }


}
