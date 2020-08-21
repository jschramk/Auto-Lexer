import java.util.Arrays;
import java.util.List;

public class Test2 {

  public static void main(String[] args) {

    List<Character> input = StringSection.toCharacterList("hello world");

    InputSection<Character> section = new InputSection<>(0, 5);

    System.out.println(section.sectionOf(input));

    List<InputSection<Character>> sections =
        Arrays.asList(new InputSection<>(0, 0), new InputSection<>(5, 7));

    List<List<Character>> replacements = Arrays.asList(
        StringSection.toCharacterList("123"),
        StringSection.toCharacterList("456")
    );

    System.out.println(InputSection.replace(input, sections, replacements));

  }



}
