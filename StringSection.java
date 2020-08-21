import java.util.ArrayList;
import java.util.List;

public class StringSection extends InputSection<Character> {

  public StringSection(int start, int end) {
    super(start, end);
  }

  public static String listToString(List<Character> characters){
    StringBuilder s = new StringBuilder();
    for(char c : characters){
      s.append(c);
    }
    return s.toString();
  }

  public static List<Character> toCharacterList(String s) {
    List<Character> characters = new ArrayList<>();
    for (char c : s.toCharArray()) {
      characters.add(c);
    }
    return characters;
  }

}
