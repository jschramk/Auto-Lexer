import java.util.*;

public class StringClassifier<I, O> {

  private DFAState<I, O> forwardInit;
  private DFAState<I, O> reverseInit = null;
  private boolean useReverseTree = false;

  /*
  public StringClassifier(boolean useReverseTree) {
    this.useReverseTree = useReverseTree;
  }
   */

  public StringClassifier(DFA<I, O> dfa){
    forwardInit = dfa.getRoot();
    //this(false);
  }

  public static class Classification<O> extends StringSection {

    private final O result;

    public Classification(O result, int start, int end) {
      super(start, end);
      this.result = result;
    }

    @Override public String toString() {
      return "["+start+","+end+"], classification: "+result;
    }

    public O classification(){
      return result;
    }

  }

  /*
  public void putClassification(List<I> input, O result){

    if(input.size() == 0){
      throw new IllegalArgumentException(
          "Recognized inputs must be at least one entry long. "
              + "To set the default result, use setDefault()"
      );
    }

    DFAState<I, O> curr = forwardInit;
    for(int i = 0; i < input.size(); i++){
      curr = curr.addTransition(input.get(i), new DFAState<>());
    }
    curr.setOutput(result);

    if(useReverseTree){
      curr = reverseInit;
      for(int i = input.size()-1; i >= 0; i--){
        curr = curr.addTransition(input.get(i), new DFAState<>());
      }
      curr.setOutput(result);
    }

  }

  public void setDefault(O result){
    forwardInit.setOutput(result);
    if(useReverseTree){
      reverseInit.setOutput(result);
    }
  }
   */

  public O classifyWhole(List<I> input){

    DFAState<I, O> curr = forwardInit;

    for (I thisChar : input) {

      if (!curr.getTransitions().contains(thisChar)) {
        return null;
      }

      curr = curr.getDestination(thisChar);

    }

    return curr.getOutput();

  }

  public Classification<O> classifyFirst(List<I> input){
    return classifyFirst(input, 0);
  }

  public Classification<O> classifyFirst(List<I> input, int startIndex){

    if(startIndex > input.size()){
      throw new IllegalArgumentException("Start index: "+startIndex+", length: "+input.size());
    }

    int end = startIndex;
    O result = forwardInit.getOutput();

    DFAState<I, O> current = forwardInit;

    for (int i = startIndex; i < input.size(); i++) {

      I thisInput = input.get(i);

      if(!current.getTransitions().contains(thisInput)){
        return new Classification<>(result, startIndex, i);
      }

      current = current.getDestination(thisInput);

      if(current.getOutput() != null){
        end = i+1;
        result = current.getOutput();
      }

    }

    return new Classification<>(result, startIndex, end);

  }

  private Classification<O> classifyLast(List<I> input){

    List<DFAState<I, O>> activeDFAStates = new ArrayList<>();

    int start = input.size(), end = input.size();

    for (int i = 0; i < input.size(); i++) {

      I thisInput = input.get(i);

      Iterator<DFAState<I, O>> iterator = activeDFAStates.iterator();

      int index = 0;

      while (iterator.hasNext()){

        DFAState<I, O> s = iterator.next();

        if(s.getTransitions().contains(thisInput)){
          s = s.getDestination(thisInput);
          activeDFAStates.set(index, s);
          if(index++ == 0){
            end = i+1;
          }
        } else {
          iterator.remove();
        }

      }

      if(this.forwardInit.getTransitions().contains(thisInput)){
        if(activeDFAStates.size() == 0){
          start = i;
          end = i+1;
        }
        activeDFAStates.add(this.forwardInit.getDestination(thisInput));
      }

    }


    if(activeDFAStates.size() == 0){

      return new Classification<>(this.forwardInit.getOutput(), start, end);

    } else {

      O testResult = activeDFAStates.get(0).getOutput();

      O result = testResult == null ? this.forwardInit.getOutput() : testResult;

      return new Classification<>(result, start, end);

    }

  }




  public List<Classification<O>> segmentWhole(List<I> input){

    List<Classification<O>> classifications = new ArrayList<>();

    int currPos = 0;
    int unknownSectionStartPos = 0;

    while (currPos < input.size()){

      Classification<O> first = classifyFirst(input, currPos);

      if(first.classification() == null){
        currPos++;
      } else {
        if(currPos-unknownSectionStartPos > 0){
          classifications.add(new Classification<>(null, unknownSectionStartPos, currPos));
        }
        currPos = first.end;
        unknownSectionStartPos = currPos;
        classifications.add(first);
      }

    }

    if(currPos-unknownSectionStartPos > 0){
      classifications.add(new Classification<>(null, unknownSectionStartPos, currPos));
    }

    return classifications;

  }









}
