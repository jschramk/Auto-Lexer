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
    return new Regex<>(Type.CHOOSE, subComponents, null, star);
  }

  public static <I> Regex<I> sequence(List<Regex<I>> subComponents, boolean star) {
    return new Regex<>(Type.SEQUENCE, subComponents, null, star);
  }

  public List<Regex<I>> components() {
    if (type.equals(Type.SINGLE))
      throw new RuntimeException("Cannot be type single");
    return subComponents;
  }

  public static <I> Regex<I> parseConversion(String input, Map<Character, I> conversion) {

    if (input.length() == 0) {
      return null;
    }

    int[] parens = nextParentheses(input);

    if (parens != null) {

      Regex<I> left = parseConversion(input.substring(0, parens[0]), conversion);
      Regex<I> middle = parseConversion(input.substring(parens[0] + 1, parens[1] - 1), conversion);

      Regex<I> right;
      if (input.charAt(parens[1]) == '*') {
        right = parseConversion(input.substring(parens[1] + 1), conversion);
        middle.star = true;
      } else {
        right = parseConversion(input.substring(parens[1]), conversion);
      }

      List<Regex<I>> sequence = new ArrayList<>();

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

      List<Regex<I>> choices = new ArrayList<>();
      for (String s : choiceStrings) {
        choices.add(parseConversion(s, conversion));
      }

      return choose(choices, false);

    } else {
      List<Regex<I>> sequence = new ArrayList<>();
      for (char c : input.toCharArray()) {

        if(!conversion.containsKey(c)){
          throw new IllegalArgumentException("Conversion map has no value for char '"+c+"'");
        }

        sequence.add(single(conversion.get(c)));
      }
      return sequence(sequence, false);
    }

  }

  public static  Regex<Character> parse(String input) {

    if (input.length() == 0) {
      return null;
    }

    int[] parens = nextParentheses(input);

    if (parens != null) {

      Regex<Character> left = parse(input.substring(0, parens[0]));
      Regex<Character> middle = parse(input.substring(parens[0] + 1, parens[1] - 1));

      Regex<Character> right;
      if (input.charAt(parens[1]) == '*') {
        right = parse(input.substring(parens[1] + 1));
        middle.star = true;
      } else {
        right = parse(input.substring(parens[1]));
      }

      List<Regex<Character>> sequence = new ArrayList<>();

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

      List<Regex<Character>> choices = new ArrayList<>();
      for (String s : choiceStrings) {
        choices.add(parse(s));
      }

      return choose(choices, false);

    } else {
      List<Regex<Character>> sequence = new ArrayList<>();
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


}
