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

  public InputSection<I, O> findNext(List<I> input, int startIndex) {

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
        break;
      }

      current = current.getDestination(thisInput);

      if (current.getOutput() != null) {
        end = i+1;
        result = current.getOutput();
      }

    }

    return result == null ? null : new InputSection<>(startIndex, end, result);

  }

  public InputSection<I, O> findFirst(List<I> input) {
    for (int i = 0; i < input.size(); i++) {
      InputSection<I, O> first = findNext(input, i);
      if (first != null) {
        return first;
      }
    }
    return null;
  }

  public InputSection<I, O> findLast(List<I> input) {

    Queue<InputSection<I, O>> sections = new LinkedList<>();

    int currPos = 0;

    while (currPos < input.size()) {

      InputSection<I, O> first = findNext(input, currPos);

      if (first != null && first.getLabel() != null) {
        if (!sections.isEmpty()) {
          sections.remove();
        }
        sections.add(first);
      }

      currPos++;

    }

    return sections.peek();

  }

  public List<InputSection<I, O>> findAll(List<I> input) {

    List<InputSection<I, O>> labels = new ArrayList<>();

    int currPos = 0;
    int unknownSectionStartPos = 0;

    while (currPos < input.size()) {

      InputSection<I, O> first = findNext(input, currPos);

      if (first == null || first.getLabel() == null) {
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
