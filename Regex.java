import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Regex {

  public static void main(String[] args) {

    System.out.println(Regex.parse("(abc|def)*"));

  }

  enum Type {
    SINGLE, CHOOSE, SEQUENCE
  }


  private List<Regex> subComponents;
  private Type type;
  private Character character;
  private boolean star;

  private Regex(Type type, List<Regex> subComponents, Character character, boolean star) {
    this.subComponents = subComponents;
    this.type = type;
    this.character = character;
    this.star = star;
  }

  public Type type() {
    return type;
  }

  public Character getCharacter() {
    if (!type.equals(Type.SINGLE))
      throw new RuntimeException("Type must be single");
    return character;
  }

  public boolean isStar() {
    return star;
  }

  public static Regex single(char c) {
    return new Regex(Type.SINGLE, null, c, false);
  }

  public static Regex choose(List<Regex> subComponents, boolean star) {
    return new Regex(Type.CHOOSE, subComponents, null, star);
  }

  public static Regex sequence(List<Regex> subComponents, boolean star) {
    return new Regex(Type.SEQUENCE, subComponents, null, star);
  }

  public List<Regex> components() {
    if (type.equals(Type.SINGLE))
      throw new RuntimeException("Cannot be type single");
    return subComponents;
  }

  public static Regex parse(String input) {

    //System.out.println("\"" + input + "\"");

    if (input.length() == 0) {
      return null;
    }

    int[] parens = nextParentheses(input);

    if (parens != null) {

      Regex left = parse(input.substring(0, parens[0]));
      Regex middle = parse(input.substring(parens[0] + 1, parens[1] - 1));

      Regex right;
      if (input.charAt(parens[1]) == '*') {
        right = parse(input.substring(parens[1] + 1));
        middle.star = true;
      } else {
        right = parse(input.substring(parens[1]));
      }

      List<Regex> sequence = new ArrayList<>();

      if (left != null)
        sequence.add(left);
      if (middle != null)
        sequence.add(middle);
      if (right != null)
        sequence.add(right);

      if (sequence.size() > 1) {
        return sequence(sequence, false);
      } else {
        return middle;
      }

    } else if (input.contains("|")) {

      String[] choiceStrings = input.split("\\|");

      List<Regex> choices = new ArrayList<>();
      for (String s : choiceStrings) {
        choices.add(parse(s));
      }

      return choose(choices, false);

    } else {
      List<Regex> sequence = new ArrayList<>();
      for (char c : input.toCharArray()) {
        sequence.add(single(c));
      }
      return sequence(sequence, false);
    }

  }

  private static int[] nextParentheses(String input) {
    int[] pos = new int[] {-1, -1};

    int level = 0;

    for (int i = 0; i < input.length(); i++) {
      if (input.charAt(i) == '(') {
        if (pos[0] == -1)
          pos[0] = i;
        level++;
      } else if (input.charAt(i) == ')') {
        level--;
        if (level == 0) {
          pos[1] = i + 1;
          return pos;
        }
      }
    }

    return null;
  }

  @Override public String toString() {

    switch (type) {

      case SINGLE: {
        return character + "";
      }
      case CHOOSE: {
        StringBuilder s = new StringBuilder();

        s.append("(");
        for (int i = 0; i < subComponents.size(); i++) {
          if (i > 0)
            s.append("|");
          s.append(subComponents.get(i));
        }
        s.append(")");
        if (star)
          s.append("*");

        return s.toString();
      }
      case SEQUENCE: {
        StringBuilder s = new StringBuilder();

        s.append("(");
        for (int i = 0; i < subComponents.size(); i++) {
          s.append(subComponents.get(i));
        }
        s.append(")");
        if (star)
          s.append("*");

        return s.toString();
      }
    }

    throw new RuntimeException("Undefined type");

  }


}
