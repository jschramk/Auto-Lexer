import java.util.*;

public class InputClassifier<I, O> {

  private DFAState<I, O> root;

  public InputClassifier(DFA<I, O> dfa){
    root = dfa.getRoot();
  }

  public class Classification extends InputSection<I> {

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

  public O classifyWhole(List<I> input){

    DFAState<I, O> curr = root;

    for (I thisChar : input) {

      if (!curr.getTransitions().contains(thisChar)) {
        return null;
      }

      curr = curr.getDestination(thisChar);

    }

    return curr.getOutput();

  }

  public Classification classifyFirst(List<I> input){
    return classifyFirst(input, 0);
  }

  public Classification classifyFirst(List<I> input, int startIndex){

    if(startIndex > input.size()){
      throw new IllegalArgumentException("Start index: "+startIndex+", length: "+input.size());
    }

    int end = startIndex;
    O result = null;

    DFAState<I, O> current = root;

    for (int i = startIndex; i < input.size(); i++) {

      I thisInput = input.get(i);

      if(!current.getTransitions().contains(thisInput)){
        return new Classification(result, startIndex, i);
      }

      current = current.getDestination(thisInput);

      if(current.getOutput() != null){
        end = i+1;
        result = current.getOutput();
      }

    }

    return new Classification(result, startIndex, end);

  }

  private Classification classifyLast(List<I> input){

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

      if(this.root.getTransitions().contains(thisInput)){
        if(activeDFAStates.size() == 0){
          start = i;
          end = i+1;
        }
        activeDFAStates.add(this.root.getDestination(thisInput));
      }

    }


    if(activeDFAStates.size() == 0){

      return new Classification(this.root.getOutput(), start, end);

    } else {

      O testResult = activeDFAStates.get(0).getOutput();

      O result = testResult == null ? this.root.getOutput() : testResult;

      return new Classification(result, start, end);

    }

  }




  public List<Classification> segmentWhole(List<I> input){

    List<Classification> classifications = new ArrayList<>();

    int currPos = 0;
    int unknownSectionStartPos = 0;

    while (currPos < input.size()){

      Classification first = classifyFirst(input, currPos);

      if(first.classification() == null){
        currPos++;
      } else {
        if(currPos-unknownSectionStartPos > 0){
          classifications.add(new Classification(null, unknownSectionStartPos, currPos));
        }
        currPos = first.end;
        unknownSectionStartPos = currPos;
        classifications.add(first);
      }

    }

    if(currPos-unknownSectionStartPos > 0){
      classifications.add(new Classification(null, unknownSectionStartPos, currPos));
    }

    return classifications;

  }









}
