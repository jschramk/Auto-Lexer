import java.util.Arrays;

public class Test {

  public static void main(String[] args) {

    NFAState.NFASegment<Character, Boolean> s1 = NFAState.NFASegment.epsilonUnion(
        Arrays.asList(
            NFAState.NFASegment.fromString("abc"),
            NFAState.NFASegment.fromString("abc").addEpsilonClosure()
        )
    );

    System.out.println();
    NFAtoDFA.convert(s1.getStart()).print();

  }


}
