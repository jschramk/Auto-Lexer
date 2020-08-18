import java.util.List;

public class StringDFA implements DFA<Character, Boolean> {

  private State<Boolean> init;

  public StringDFA(State<Boolean> init) {
    this.init = init;
  }

  @Override public Boolean output(List<Character> input) {

    State<Boolean> curr = init;
    for(char c : input){
      if(curr.hasTransition(c)){
        curr = curr.nextState(c);
      } else {
        return null;
      }
    }

    return curr.output();
  }

  @Override public void print() {
    init.print();
  }


}
