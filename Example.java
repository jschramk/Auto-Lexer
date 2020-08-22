import java.util.List;

public class Example {

  public static void main(String[] args) {

    MultiDFAConstructor<Character, String> constructor = new MultiDFAConstructor<>();

    constructor.add(Regex.parse("(abc)*"), "(abc)*").add(Regex.parse("abcabc"), "abcabc")
        .add(Regex.parse("(ab|cd)*"), "(ab|cd)*");

    DFA<Character, String> dfa = constructor.buildDFA();

    InputClassifier<Character, String> classifier = new InputClassifier<>(dfa);

    String text = "abc abcabc ababcdabcdab";

    List<Character> input = StringSection.toCharacterList(text);
    List<InputClassifier<Character, String>.Classification> classifications =
        classifier.findAll(input);

    System.out.println("Input: \"" + text + "\"\n");
    for (InputClassifier<Character, String>.Classification c : classifications) {
      if (c.classification() == null)
        continue;
      System.out.println(String.format("\"%s\": %s",
          StringSection.listToString(c.sectionOf(StringSection.toCharacterList(text))),
          c.classification()));
    }

  }



}
