import java.util.List;

public interface DFA<I, O> {

  O output(List<I> input);

  void print();

}
