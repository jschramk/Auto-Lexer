package dfa.utils;

import java.util.ArrayList;
import java.util.List;

public class DFAConstructor<I, O> {

  private List<Regex<I>> addedRegexps = new ArrayList<>();
  private List<O> addedOutputs = new ArrayList<>();

  public DFAConstructor<I, O> add(Regex<I> input, O output) {
    addedRegexps.add(input);
    addedOutputs.add(output);
    return this;
  }

  public DFA<I, O> buildDFA() {

    List<NFAState.NFASegment<I, O>> segments = new ArrayList<>();
    for (int i = 0; i < addedRegexps.size(); i++) {
      segments.add(NFAState.NFASegment.fromRegex(addedRegexps.get(i), addedOutputs.get(i)));
    }

    NFAState.NFASegment<I, O> root = NFAState.NFASegment.epsilonUnion(segments);

    return new DFA<>(NFAtoDFA.convert(root.getStart(), addedOutputs));

  }



}
