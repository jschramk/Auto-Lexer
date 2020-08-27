package dfa.utils;

import java.util.*;

public class InputClassifier<I, O> {

  private DFAState<I, O> root;

  public InputClassifier(DFA<I, O> dfa) {
    root = dfa.getRoot();
  }

  public O classify(List<I> input) {

    DFAState<I, O> curr = root;

    for (I thisChar : input) {

      if (!curr.getTransitions().contains(thisChar)) {
        return null;
      }

      curr = curr.getDestination(thisChar);

    }

    return curr.getOutput();

  }

  public InputSection<I, O> findFirst(List<I> input) {
    return findFirst(input, 0);
  }

  public InputSection<I, O> findFirst(List<I> input, int startIndex) {

    if (startIndex > input.size()) {
      throw new IllegalArgumentException(
          "Start index: " + startIndex + ", length: " + input.size());
    }

    int end = startIndex;
    O result = null;

    DFAState<I, O> current = root;

    for (int i = startIndex; i < input.size(); i++) {

      I thisInput = input.get(i);

      if (!current.getTransitions().contains(thisInput)) {
        return new InputSection<>(startIndex, i, result);
      }

      current = current.getDestination(thisInput);

      if (current.getOutput() != null) {
        end = i + 1;
        result = current.getOutput();
      }

    }

    return new InputSection<>(startIndex, end, result);

  }

  public InputSection<I, O> findLast(List<I> input) {

    Queue<InputSection<I, O>> labels = new LinkedList<>();

    int currPos = 0;

    while (currPos < input.size()) {

      InputSection<I, O> first = findFirst(input, currPos);

      currPos++;
      if(first.getLabel() != null) {
        if (!labels.isEmpty()){
          labels.remove();
        }
        labels.add(first);
      }

    }

    return labels.peek();

  }



  public List<InputSection<I, O>> findAll(List<I> input) {

    List<InputSection<I, O>> labels = new ArrayList<>();

    int currPos = 0;
    int unknownSectionStartPos = 0;

    while (currPos < input.size()) {

      InputSection<I, O> first = findFirst(input, currPos);

      if (first.getLabel() == null) {
        currPos++;
      } else {
        if (currPos - unknownSectionStartPos > 0) {
          labels.add(new InputSection<>(unknownSectionStartPos, currPos, null));
        }
        currPos = first.end;
        unknownSectionStartPos = currPos;
        labels.add(first);
      }

    }

    if (currPos - unknownSectionStartPos > 0) {
      labels.add(new InputSection<>(unknownSectionStartPos, currPos, null));
    }

    return labels;

  }



}
