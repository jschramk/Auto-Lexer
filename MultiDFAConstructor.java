import java.util.ArrayList;
import java.util.List;

public class MultiDFAConstructor<O> {

  private List<Regex> addedRegexps = new ArrayList<>();
  private List<O> addedOutputs = new ArrayList<>();

  public void addRegex(Regex regex, O output){
    addedRegexps.add(regex);
    addedOutputs.add(output);
  }

  public DFA<Character, O> buildDFA(){

    List<NFAState.NFASegment<Character, O>> segments = new ArrayList<>();
    for (int i = 0; i < addedRegexps.size(); i++) {
      segments.add(NFAState.NFASegment.fromRegex(addedRegexps.get(i), addedOutputs.get(i)));
    }

    NFAState.NFASegment<Character, O> root = NFAState.NFASegment.epsilonUnion(
        segments
    );

    return new DFA<>(NFAtoDFA.convert(root.getStart()));

  }



}
