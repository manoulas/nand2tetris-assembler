import java.io.*;
import java.util.ArrayList;

public class Parser {
  BufferedReader input;
  String line;
  String nextLine;
  ArrayList<String> lines = new ArrayList<>();
  
  public Parser(String input_path) {
    try {
      input = new BufferedReader(new FileReader(input_path));
      peekNextLine();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
  
  private void peekNextLine() {
    try {
      String l;
      while ((l = input.readLine()) != null) {
        l = l.split("//")[0].trim().replaceAll("\\s+", "");
        if (!l.isEmpty()) {
          nextLine = l;
          return;
        }
      }
      nextLine = null;
    } catch (IOException e) {
      nextLine = null;
    }
  }
  
  public boolean hasMoreCommands() {
    return nextLine != null;
  }
  
  public void advance() {
    line = nextLine;
    lines.add(line);
    peekNextLine();
  }
  
  public String commandType() {
    if (line == null) {
      return null;
    }
    if (line.startsWith("@")) {
      return "A_COMMAND";
    } else if (line.startsWith(("(")) && line.endsWith(")")) {
      return "L_COMMAND";
    } else {
      return "C_COMMAND";
    }
  }
  
  public String symbol() {
    if (line.startsWith("@"))
      return line.substring(1);
    else if (line.startsWith(("(")) && line.endsWith(")"))
      return line.substring(1, line.length() - 1);
    else
      return null;
  }
  
  public String dest() {
    if (line.contains("="))
      return line.split("=")[0];
    else
      return null;
  }
  
  public String comp() {
    if (line.contains("="))
      return line.split("=")[1].split(";")[0];
    else
      return line.split(";")[0];
  }
  
  public String jump() {
    if (line.contains(";"))
      return line.split(";")[1];
    else
      return null;
  }
}
