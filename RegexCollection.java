package dfa.utils;

public class RegexCollection {

  private static String digits = "(0|1|2|3|4|5|6|7|8|9)";

  public static String FLOAT = String.format("%1$s*.%1$s%1$s*", digits);
  public static String INTEGER = String.format("%1$s%1$s*", digits);
  public static String NUMBER = String.format("(%s|%s)", INTEGER, FLOAT);
  public static String SCIENTIFIC_NOTATION = String.format("%s(E|e)%s", RegexCollection.NUMBER, RegexCollection.NUMBER);
  public static String SCIENTIFIC_NOTATION_SIGNED = String.format("%s(E|e)(+|-|−)%s", RegexCollection.NUMBER, RegexCollection.NUMBER);
  public static String VARIABLE = "(_|a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z"
      + "|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z)"
      + "(_|a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z"
      + "|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z|1|2|3|4|5|6|7|8|9|0)*";

}
