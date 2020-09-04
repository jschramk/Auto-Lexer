package dfa.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Regex<I> {

  enum Type {
    SINGLE, CHOOSE, SEQUENCE
  }


  private List<Regex<I>> subComponents;
  private Type type;
  private I input;
  private boolean star;

  private Regex(Type type, List<Regex<I>> subComponents, I input, boolean star) {
    this.subComponents = subComponents;
    this.type = type;
    this.input = input;
    this.star = star;
  }

  public Type type() {
    return type;
  }

  public I getInput() {
    if (!type.equals(Type.SINGLE))
      throw new RuntimeException("Type must be single");
    return input;
  }

  public boolean isStar() {
    return star;
  }

  public static <I> Regex<I> single(I input) {
    return new Regex<>(Type.SINGLE, null, input, false);
  }

  public static <I> Regex<I> choose(List<Regex<I>> subComponents, boolean star) {

    if (subComponents.size() == 1) {
      return sequence(subComponents, star);
    }

    List<Regex<I>> allSubComponents = new ArrayList<>();
    for (Regex<I> r : subComponents) {
      if (r.type == Type.CHOOSE && !r.star) {
        allSubComponents.addAll(r.subComponents);
      } else {
        allSubComponents.add(r);
      }
    }
    return new Regex<>(Type.CHOOSE, allSubComponents, null, star);
  }

  public static <I> Regex<I> sequence(List<Regex<I>> subComponents, boolean star) {

    List<Regex<I>> allSubComponents = new ArrayList<>();
    for (Regex<I> r : subComponents) {
      if (r.type == Type.SEQUENCE && !r.star) {
        allSubComponents.addAll(r.subComponents);
      } else {
        allSubComponents.add(r);
      }
    }
    return new Regex<>(Type.SEQUENCE, allSubComponents, null, star);
  }

  public List<Regex<I>> components() {
    if (type.equals(Type.SINGLE))
      throw new RuntimeException("Cannot be type single");
    return subComponents;
  }



  public static <I> Regex<I> parseConversion(String input, Map<Character, I> conversion) {

    if (input.length() == 0) {
      throw new IllegalArgumentException("Regular expression cannot be of length 0");
    }

    //System.out.println("PARSING: " + input);

    Regex<I> result;

    String[] stringChoices = input.split("\\|(?![^()]*\\))");

    if (stringChoices.length > 1) {

      List<Regex<I>> choices = new ArrayList<>();

      for (String s : stringChoices) {
        choices.add(parseConversion(s, conversion));
      }

      result = choose(choices, false);

    } else {

      int[] parens = nextParentheses(input);

      if (parens != null) {

        String leftString = input.substring(0, parens[0]);
        String middleString = input.substring(parens[0] + 1, parens[1] - 1);
        String rightString;

        boolean middleStar = false;

        if (input.length() > parens[1] && input.charAt(parens[1]) == '*') {
          rightString = input.substring(parens[1] + 1);
          middleStar = true;
        } else {
          rightString = input.substring(parens[1]);
        }

        List<Regex<I>> sequence = new ArrayList<>();

        if (leftString.length() > 0) {
          sequence.add(parseConversion(leftString, conversion));
        }

        Regex<I> middle = parseConversion(middleString, conversion);
        middle.star = middleStar;
        sequence.add(middle);

        if (rightString.length() > 0) {
          sequence.add(parseConversion(rightString, conversion));
        }

        if (sequence.size() > 1) {
          result = sequence(sequence, false);
        } else {
          result = middle;
        }

      } else {
        List<Regex<I>> sequence = new ArrayList<>();
        for (char c : input.toCharArray()) {
          sequence.add(single(conversion.get(c)));
        }
        result = sequence(sequence, false);
      }

    }

    //System.out.println("RESULT FOR " + input + ": " + result);

    return result;

  }


  public static Regex<Character> parse(String input) {

    if (input.length() == 0) {
      throw new IllegalArgumentException("Regular expression cannot be of length 0");
    }

    //System.out.println("PARSING: \"" + input + "\"");

    Regex<Character> result;

    List<String> stringChoices = getNextChoices(input);

    if (stringChoices.size() > 1) {

      List<Regex<Character>> choices = new ArrayList<>();

      for (String s : stringChoices) {
        choices.add(parse(s));
      }

      result = choose(choices, false);

    } else {

      int[] parentheses = nextParentheses(input);

      if (parentheses != null) {

        String leftString = input.substring(0, parentheses[0]);
        String middleString = input.substring(parentheses[0] + 1, parentheses[1] - 1);
        String rightString;

        boolean middleStar = false;

        if (input.length() > parentheses[1] && input.charAt(parentheses[1]) == '*') {
          rightString = input.substring(parentheses[1] + 1);
          middleStar = true;
        } else {
          rightString = input.substring(parentheses[1]);
        }

        System.out.println("\tleft: \"" + leftString + "\"");
        System.out.println("\tmiddle: \"" + middleString + "\"");
        System.out.println("\tright: \"" + rightString + "\"");

        List<Regex<Character>> sequence = new ArrayList<>();

        if (leftString.length() > 0) {
          sequence.add(parse(leftString));
        }

        Regex<Character> middle = parse(middleString);
        middle.star = middleStar;
        sequence.add(middle);

        if (rightString.length() > 0) {
          sequence.add(parse(rightString));
        }

        if (sequence.size() > 1) {
          result = sequence(sequence, false);
        } else {
          result = middle;
        }

      } else {
        List<Regex<Character>> sequence = new ArrayList<>();
        for (char c : input.toCharArray()) {
          sequence.add(single(c));
        }
        result = sequence(sequence, false);
      }

    }

    //System.out.println("RESULT FOR \"" + input + "\": " + result);

    return result;

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
        return input + "";
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

  public static List<String> getNextChoices(String input) {

    List<String> choices = new ArrayList<>();

    int level = 0;

    int last = 0;

    for (int i = 0; i < input.length(); i++) {

      char thisChar = input.charAt(i);

      if (thisChar == '(') {
        level++;
      } else if (thisChar == ')') {
        level--;
      } else if (level == 0 && thisChar == '|') {
        choices.add(input.substring(last, i));
        last = i + 1;
      }

    }

    if (choices.isEmpty()) {
      choices.add(input);
    } else {
      choices.add(input.substring(last));
    }

    return choices;

  }


}
