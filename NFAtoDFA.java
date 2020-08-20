import java.util.*;

public class NFAtoDFA<I, O> {

  private NFAState<I, O> root;
  private Map<NFAState<I, O>, Map<I, Set<NFAState<I, O>>>> NFATable = new HashMap<>();
  private Map<NFAState<I, O>, Set<NFAState<I, O>>> NFAEpsilonTable = new HashMap<>();
  private Map<Set<NFAState<I, O>>, DFAState<I, O>> analogTable = new HashMap<>();
  private List<O> outputRanking;

  private NFAtoDFA(List<O> outputRanking) {
    this.outputRanking = outputRanking;
  }

  public static <I, O> DFAState<I, O> convert(NFAState<I, O> root) {
    return convert(root, null);
  }

  public static <I, O> DFAState<I, O> convert(NFAState<I, O> root, List<O> outputRanking) {
    NFAtoDFA<I, O> converter = new NFAtoDFA<>(outputRanking);
    converter.addNFAByRoot(root);
    return converter.buildDFA();
  }

  private void addNFAByRoot(NFAState<I, O> root) {
    this.root = root;
    recursiveAddNFA(root);
  }

  private void recursiveAddNFA(NFAState<I, O> state) {

    if (!NFATable.containsKey(state)) {
      NFATable.put(state, state.getTransitions());
      for (I input : state.getTransitions().keySet()) {
        for (NFAState<I, O> successor : state.getTransitions().get(input)) {
          recursiveAddNFA(successor);
        }
      }
    }

    if (!NFAEpsilonTable.containsKey(state) && !state.getEpsilonTransitions().isEmpty()) {
      NFAEpsilonTable.put(state, state.getEpsilonTransitions());
      for (NFAState<I, O> successor : state.getEpsilonTransitions()) {
        recursiveAddNFA(successor);
      }
    }

  }

  private void addAnalogState(NFAState<I, O> state) {
    Set<NFAState<I, O>> set = new HashSet<>();
    set.add(state);
    addAnalogState(set);
  }

  private void addAnalogState(Set<NFAState<I, O>> merge) {
    DFAState<I, O> mergedState = new DFAState<>();
    analogTable.put(merge, mergedState);
  }

  private DFAState<I, O> getAnalogState(NFAState<I, O> state) {
    Set<NFAState<I, O>> set = new HashSet<>();
    set.add(state);
    return getAnalogState(set);
  }

  private DFAState<I, O> getAnalogState(Set<NFAState<I, O>> states) {
    return analogTable.get(states);
  }

  private DFAState<I, O> buildDFA() {
    Set<NFAState<I, O>> currNFAs = new HashSet<>();
    currNFAs.add(root);
    addAnalogState(root);
    return recursiveBuildDFA(new HashSet<>(), getAnalogState(root), currNFAs);
  }

  private DFAState<I, O> recursiveBuildDFA(
      Set<DFAState<I, O>> completedDFAStates,
      DFAState<I, O> currDFAState,
      Set<NFAState<I, O>> currNFAStates
  ) {

    // stop recursion if current DFA state was already built
    if (completedDFAStates.contains(currDFAState)) {
      return currDFAState;
    } else {
      completedDFAStates.add(currDFAState);
    }

    // add all epsilon-connected states to the set of current states
    Set<NFAState<I, O>> currPlusEpsilon = new HashSet<>();
    for (NFAState<I, O> state : currNFAStates) {
      currPlusEpsilon.addAll(state.getEpsilonStates());
    }
    currNFAStates = currPlusEpsilon;

    // set output of analog state based on highest priority in case of overlap
    O desiredOutput = null;
    for(NFAState<I, O> state : currNFAStates){
      if(desiredOutput == null){
        if(state.getOutput() != null) {
          desiredOutput = state.getOutput();
        }
      } else {
        if(state.getOutput() != null && outputRanking != null) {
          int currPos = outputRanking.indexOf(desiredOutput);
          int newPos = outputRanking.indexOf(state.getOutput());
          if(newPos > currPos){
            desiredOutput = state.getOutput();
          }
        }
      }
    }
    currDFAState.setOutput(desiredOutput);

    // populate map with sets of successors for each input
    Map<I, Set<NFAState<I, O>>> inputSuccessorMap = new HashMap<>();
    for (NFAState<I, O> state : currNFAStates) {
      for (I input : state.getTransitions().keySet()) {
        if (!inputSuccessorMap.containsKey(input)) {
          inputSuccessorMap.put(input, new HashSet<>());
        }
        inputSuccessorMap.get(input).addAll(state.getTransitions().get(input));
      }
    }

    // construct subtree for each analogous DFA state and
    // connect it as the DFA state successor for each input
    for (I input : inputSuccessorMap.keySet()) {
      Set<NFAState<I, O>> successors = inputSuccessorMap.get(input);
      if (getAnalogState(successors) == null) {
        addAnalogState(successors);
      }
      DFAState<I, O> analog = getAnalogState(successors);
      currDFAState.addTransition(input, recursiveBuildDFA(completedDFAStates, analog, successors));
    }

    return currDFAState;

  }

  private void printNFATable() {

    for (NFAState<I, O> state : NFATable.keySet()) {
      if (state == root)
        System.out.print("-> ");
      System.out.print(state + " { ");
      int i = 0;
      for (I input : NFATable.get(state).keySet()) {
        Set<NFAState<I, O>> successors = NFATable.get(state).get(input);
        if (i > 0)
          System.out.print(", ");
        System.out.print("[" + input + "] -> " + successors);
        i++;
      }

      if (NFAEpsilonTable.containsKey(state)) {
        Set<NFAState<I, O>> successors = NFAEpsilonTable.get(state);
        if (i > 0)
          System.out.print(", ");
        System.out.print("[EPSILON] -> " + successors);
      }

      System.out.println(" }");

    }


  }

  private void printAnalogTable() {

    for (Set<NFAState<I, O>> stateSet : analogTable.keySet()) {
      System.out.println(stateSet + ": " + analogTable.get(stateSet));
    }

  }



}
