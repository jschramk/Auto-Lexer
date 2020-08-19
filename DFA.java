import java.util.List;

public class DFA<I, O> {

  private DFAState<I, O> root;

  public DFA(DFAState<I, O> root) {
    this.root = root;
  }

  public O getOutput(List<I> input){

    DFAState<I, O> curr = root;

    for(I in : input){

      if(curr.getTransitions().contains(in)){
        curr = curr.getDestination(in);
      } else {
        return null;
      }

    }

    return curr.getOutput();

  }

  public DFAState<I, O> getRoot(){
    return root;
  }

  public void print(){
    root.print();
  }


}
