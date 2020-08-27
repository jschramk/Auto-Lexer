package dfa.utils;

import java.util.ArrayList;
import java.util.List;

public class InputSection<I, O> {

  private final O label;
  protected final int start, end;

  public InputSection(int start, int end, O label) {
    this.start = start;
    this.end = end;
    this.label = label;
  }

  public int length() {
    return end - start;
  }

  public int getStart() {
    return start;
  }

  public int getEnd() {
    return end;
  }

  public List<I> sectionOf(List<I> input) {
    return input.subList(start, end);
  }

  public List<I> inverseSectionOf(List<I> input) {

    List<I> section = new ArrayList<>();

    section.addAll(input.subList(0, start));

    section.addAll(input.subList(end, input.size()-1));

    return section;
  }

  public static <I, O> List<I> replace(List<I> input, List<InputSection<I, O>> sections,
      List<List<I>> replacements) {

    if (replacements.size() != sections.size()) {
      throw new IllegalArgumentException("Lists must be the same size");
    }
    List<List<I>> fragments = new ArrayList<>();
    int size = replacements.size();
    if (size > 0) {
      for (int i = 0; i < size; i++) {
        int start = i >= 1 ? sections.get(i - 1).end : 0;
        fragments.add(input.subList(start, sections.get(i).start));
      }
      fragments.add(input.subList(sections.get(size - 1).end, input.size()));

      List<I> result = new ArrayList<>();

      int num = fragments.size() - 1;
      for (int i = 0; i < num; i++) {
        result.addAll(fragments.get(i));
        result.addAll(replacements.get(i));
      }
      result.addAll(fragments.get(num));
      return result;

    } else {
      return input;
    }

  }


  public static <I> List<I> replace(List<I> input, int start, int end, List<I> replacement) {
    List<I> result = new ArrayList<>();
    result.addAll(input.subList(0, start));
    result.addAll(replacement);
    result.addAll(input.subList(end, input.size()));
    return result;
  }

  public static <I, O> List<I> replace(List<I> input, InputSection<I, O> section, List<I> replacement) {
    return replace(input, section.start, section.end, replacement);
  }

  @Override public String toString() {
    return "[" + start + "," + end + "]: " + label;
  }

  public O getLabel() {
    return label;
  }

  public static String stringOf(List<Character> characters) {
    StringBuilder s = new StringBuilder();
    for (char c : characters) {
      s.append(c);
    }
    return s.toString();
  }

  public static List<Character> characterListOf(String s) {
    List<Character> characters = new ArrayList<>();
    for (char c : s.toCharArray()) {
      characters.add(c);
    }
    return characters;
  }

  public static <I, O> List<O> labelListOf(List<InputSection<I, O>> list) {
    List<O> result = new ArrayList<>();
    for(InputSection<I, O> section : list) {
      result.add(section.label);
    }
    return result;
  }

}
