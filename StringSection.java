import java.util.ArrayList;
import java.util.List;

public class StringSection {

  protected final int start, end;

  public StringSection(int start, int end) {
    this.start = start;
    this.end = end;
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

  public String sectionOf(String input) {
    return input.substring(start, end);
  }

  public String inverseSectionOf(String input) {
    return input.substring(0, start) + input.substring(end);
  }

  public static StringSection trimOf(String input) {

    char[] chars = input.toCharArray();

    int start = 0;
    int end = chars.length;

    for (int i = end - 1; i >= 0; i--) {
      if (chars[i] == ' ' || chars[i] == '\n') {
        end--;
      } else {
        break;
      }
    }

    for (int i = start; i < end; i++) {
      if (chars[i] == ' ' || chars[i] == '\n') {
        start++;
      } else {
        break;
      }
    }

    return new StringSection(start, end);

  }

  public static String replace(String input, List<StringSection> sections,
      List<String> replacements) {

    if (replacements.size() != sections.size()) {
      throw new IllegalArgumentException("Lists must be the same size");
    }
    List<String> fragments = new ArrayList<>();
    int size = replacements.size();
    if (size > 0) {
      for (int i = 0; i < size; i++) {
        int start = i >= 1 ? sections.get(i - 1).end : 0;
        fragments.add(input.substring(start, sections.get(i).start));
      }
      fragments.add(input.substring(sections.get(size - 1).end));
      StringBuilder s = new StringBuilder();
      int num = fragments.size() - 1;
      for (int i = 0; i < num; i++) {
        s.append(fragments.get(i));
        s.append(replacements.get(i));
      }
      s.append(fragments.get(num));
      return s.toString();

    } else {
      return input;
    }


  }

  public static List<Character> toCharacterList(String s) {
    List<Character> characters = new ArrayList<>();
    for (char c : s.toCharArray()) {
      characters.add(c);
    }
    return characters;
  }

  public static String replace(String input, StringSection section, String replacement) {
    return input.substring(0, section.start) + replacement + input.substring(section.end);
  }

  @Override public String toString() {
    return "[" + start + "," + end + "]";
  }
}
