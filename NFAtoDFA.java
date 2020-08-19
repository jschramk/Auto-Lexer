import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NFAtoDFA<I, O> {

  private NFAState<I, O> root;
  private Map<NFAState<I, O>, Map<I, Set<NFAState<I, O>>>> NFATable = new HashMap<>();
  private Map<NFAState<I, O>, Set<NFAState<I, O>>> NFAEpsilonTable = new HashMap<>();
  private Map<Set<NFAState<I, O>>, DFAState<I, O>> analogTable = new HashMap<>();
  private Map<DFAState<I, O>, Map<I, DFAState<I, O>>> DFATable = new HashMap<>();

  private NFAtoDFA() {
  }

  public static <I, O> DFAState<I, O> convert(NFAState<I, O> root) {
    NFAtoDFA<I, O> converter = new NFAtoDFA<>();
    converter.addNFARoot(root);
    return converter.constructDFA();
  }

  private void addNFARoot(NFAState<I, O> root) {
    this.root = root;
    addNFARecur(root);
  }

  private void addNFARecur(NFAState<I, O> state) {

    if (!NFATable.containsKey(state)) {
      NFATable.put(state, state.getTransitions());
      for (I input : state.getTransitions().keySet()) {
        for (NFAState<I, O> successor : state.getTransitions().get(input)) {
          addNFARecur(successor);
        }
      }
    }

    if (!NFAEpsilonTable.containsKey(state) && state.getEpsilonTransitions().size() > 0) {
      NFAEpsilonTable.put(state, state.getEpsilonTransitions());
      for (NFAState<I, O> successor : state.getEpsilonTransitions()) {
        addNFARecur(successor);
      }
    }

  }



  private void addAnalogState(Set<NFAState<I, O>> merge, NFAState<I, O> epsilonPredecessor) {
    Set<NFAState<I, O>> merged = new HashSet<>(merge);
    merged.add(epsilonPredecessor);
    DFAState<I, O> mergedState = new DFAState<>();
    analogTable.put(merged, mergedState);
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

  private DFAState<I, O> constructDFA() {
    Set<NFAState<I, O>> currNFAs = new HashSet<>();
    currNFAs.add(root);
    addAnalogState(root);
    return constructDFA(new HashSet<>(), getAnalogState(root), currNFAs);
  }

  private DFAState<I, O> constructDFA(Set<DFAState<I, O>> builtDFAStates,
      DFAState<I, O> currDFAState, Set<NFAState<I, O>> currNFAStates) {

    if (builtDFAStates.contains(currDFAState)) {
      return currDFAState;
    } else {
      builtDFAStates.add(currDFAState);
    }

    // add all epsilon-connected states to the set of current states
    Set<NFAState<I, O>> currPlusEpsilon = new HashSet<>();
    for (NFAState<I, O> state : currNFAStates) {
      currPlusEpsilon.addAll(getEpsilonClosure(state));
    }
    currNFAStates = currPlusEpsilon;

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
      currDFAState.addTransition(input, constructDFA(builtDFAStates, analog, successors));
    }

    return currDFAState;

  }

  private Set<NFAState<I, O>> getEpsilonClosure(NFAState<I, O> state) {
    Set<NFAState<I, O>> set = new HashSet<>();
    set.add(state);
    for (NFAState<I, O> connected : state.getEpsilonTransitions()) {
      set.addAll(getEpsilonClosure(connected));
    }
    return set;
  }

  public void printNFATable() {

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

  public void printDFATable() {

    for (DFAState<I, O> state : DFATable.keySet()) {
      System.out.print(state + " { ");
      int i = 0;
      for (I input : DFATable.get(state).keySet()) {
        DFAState<I, O> successor = DFATable.get(state).get(input);
        if (i > 0)
          System.out.print(", ");
        System.out.print("[" + input + "] -> " + successor);
        i++;
      }

      System.out.println(" }");

    }


  }


  public void printAnalogTable() {

    for (Set<NFAState<I, O>> stateSet : analogTable.keySet()) {
      System.out.println(stateSet + ": " + analogTable.get(stateSet));
    }

  }



}
