
import java.util.List;

public class Test {

  public static void main(String[] args) {

    MultiDFAConstructor<String> constructor = new MultiDFAConstructor<>();

    constructor.addRegex(Regex.parse("abc"), "ABC");
    constructor.addRegex(Regex.parse(RegexCollection.INTEGER), "int");
    constructor.addRegex(Regex.parse(RegexCollection.FLOAT), "float");
    constructor.addRegex(Regex.parse(RegexCollection.VARIABLE), "var");

    DFA<Character, String> dfa = constructor.buildDFA();
    //dfa.print();

    StringClassifier<Character, String> classifier = new StringClassifier<>(dfa);


    String text = "3.1415 0.5 1 1.5 x1 1x x2 var0 thisIsAwesome abc";

    List<Character> input = StringSection.toCharacterList(text);

    List<StringClassifier.Classification<String>> all = classifier.segmentWhole(input);

    for(StringClassifier.Classification<String> c : all){
      System.out.println("[\""+c.sectionOf(text)+"\": "+c.classification()+"]");
    }


    System.out.println("Input: \""+text+"\"");
    System.out.println("Output: "+dfa.getOutput(input));



  }


}
