import java.util.Arrays;

public class Test {

  public static void main(String[] args) {

    NFAState.NFASegment<Character, Boolean> s0 = NFAState.NFASegment.fromString("abc").addEpsilonClosure();

    NFAState.NFASegment<Character, Boolean> s1 = NFAState.NFASegment.epsilonUnion(
        Arrays.asList(
            NFAState.NFASegment.fromString("abc"),
            s0
        )
    );

    NFAState.NFASegment<Character, Boolean> s2 = NFAState.NFASegment.fromString("hello");

    System.out.println();
    NFAtoDFA.convert(s1.getStart()).print();

  }


}
