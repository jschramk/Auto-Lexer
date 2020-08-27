package dfa.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DFA<I, O> {

  private DFAState<I, O> root;

  public DFA(DFAState<I, O> root) {
    this.root = root;
  }

  public O getOutput(List<I> input) {

    DFAState<I, O> curr = root;

    for (I in : input) {

      if (curr.getTransitions().contains(in)) {
        curr = curr.getDestination(in);
      } else {
        return null;
      }

    }

    return curr.getOutput();

  }

  public int computeSize() {
    return getAllStates(root, new HashSet<>()).size();
  }

  private Set<DFAState<I, O>> getAllStates(DFAState<I, O> curr, Set<DFAState<I, O>> visited) {

    visited.add(curr);

    for (I in : curr.getTransitions()) {
      DFAState<I, O> next = curr.getDestination(in);
      if(!visited.contains(next))
      visited.addAll(getAllStates(next, visited));
    }

    return visited;
  }

  public DFAState<I, O> getRoot() {
    return root;
  }

  public void print() {
    root.print();
  }

  public String test = "";


}
