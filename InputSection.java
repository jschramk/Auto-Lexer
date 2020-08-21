import java.util.ArrayList;
import java.util.List;

public class InputSection<I> {

  protected final int start, end;

  public InputSection(int start, int end) {
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

  public List<I> sectionOf(List<I> input) {
    return input.subList(start, end);
  }

  public List<I> inverseSectionOf(List<I> input) {

    List<I> section = new ArrayList<>();

    section.addAll(input.subList(0, start));

    section.addAll(input.subList(end, input.size()-1));

    return section;
  }

  public static <I> List<I> replace(List<I> input, List<InputSection<I>> sections,
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

  public static <I> List<I> replace(List<I> input, InputSection<I> section, List<I> replacement) {
    List<I> result = new ArrayList<>();
    result.addAll(input.subList(0, section.start));
    result.addAll(replacement);
    result.addAll(input.subList(section.end, input.size()));
    return result;
  }

  @Override public String toString() {
    return "[" + start + "," + end + "]";
  }
}
