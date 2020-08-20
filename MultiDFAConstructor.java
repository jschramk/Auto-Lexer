import java.util.ArrayList;
import java.util.List;

public class MultiDFAConstructor<O> {

  private List<Regex> addedRegexps = new ArrayList<>();
  private List<O> addedOutputs = new ArrayList<>();

  public MultiDFAConstructor<O> add(Regex input, O output){
    addedRegexps.add(input);
    addedOutputs.add(output);
    return this;
  }

  public DFA<Character, O> buildDFA(){

    List<NFAState.NFASegment<Character, O>> segments = new ArrayList<>();
    for (int i = 0; i < addedRegexps.size(); i++) {
      segments.add(NFAState.NFASegment.fromRegex(addedRegexps.get(i), addedOutputs.get(i)));
    }

    NFAState.NFASegment<Character, O> root = NFAState.NFASegment.epsilonUnion(segments);

    return new DFA<>(NFAtoDFA.convert(root.getStart(), addedOutputs));

  }



}
