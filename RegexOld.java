import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegexOld {

  private boolean base = false;
  private char arg;

  private List<RegexOld> options = new ArrayList<>();
  private RegexOld next;
  private boolean star;

  public RegexOld() {
  }

  public RegexOld(char c) {
    base = true;
    arg = c;
  }

  boolean isBase(){
    return base;
  }

  char getArg(){
    return arg;
  }

  boolean isStar(){
    return star;
  }

  List<RegexOld> getOptions(){
    return options;
  }

  void setNext(RegexOld regexOld) {
    next = regexOld;
  }

  RegexOld getNext(){
    return next;
  }

  boolean hasNext() {
    return next != null;
  }

  public static RegexOld choose(boolean star, RegexOld... regexOlds) {

    RegexOld r = new RegexOld();

    r.star = star;

    r.options = Arrays.asList(regexOlds);

    return r;

  }

  public static RegexOld choose(boolean star, String chars) {

    RegexOld[] regexOlds = new RegexOld[chars.length()];

    for (int i = 0; i < regexOlds.length; i++) {

      regexOlds[i] = new RegexOld(chars.charAt(i));

    }

    return RegexOld.choose(star, regexOlds);

  }

  public static RegexOld sequence(boolean star, RegexOld... regexOlds) {

    RegexOld main = new RegexOld();
    main.star = star;

    for (int i = 1; i < regexOlds.length; i++) {

      regexOlds[i - 1].setNext(regexOlds[i]);

    }

    main.options.add(regexOlds[0]);

    return main;

  }

  public static RegexOld sequence(boolean star, String chars) {

    RegexOld[] regexOlds = new RegexOld[chars.length()];

    for (int i = 0; i < regexOlds.length; i++) {

      regexOlds[i] = new RegexOld(chars.charAt(i));

    }

    return RegexOld.sequence(star, regexOlds);

  }

  @Override public String toString() {

    StringBuilder s = new StringBuilder();



    if (base) {
      s.append(arg);
    } else {

      s.append("(");
      s.append(options.get(0));

      for (int i = 1; i < options.size(); i++) {
        s.append("|");
        s.append(options.get(i));
      }

      s.append(")");

    }

    if (star) {
      s.append("*");
    }

    if (next != null) {
      s.append(next.toString());
    }

    return s.toString();

  }

}
