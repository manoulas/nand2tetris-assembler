import java.io.*;

public class Assembler {
  public static void main(String[] args) {
    Parser parser = new Parser(args[0]);
    Code code = new Code();
    String outPath = args[0].split(".asm")[0] + ".hack";
    SymbolTable symbolTable = new SymbolTable();
    
    try (BufferedWriter output = new BufferedWriter(new FileWriter(outPath))) {
      // First-Pass
      int romAddr = 0;
      while (parser.hasMoreCommands()) {
        parser.advance();
        if (parser.commandType().equals("A_COMMAND") || parser.commandType().equals("C_COMMAND"))
          romAddr++;
        else if (parser.commandType().equals("L_COMMAND")) {
          symbolTable.addEntry(parser.symbol(), romAddr);
        }
      }
      // Second-Pass
      parser = new Parser(args[0]);
      boolean isFirstLine = true;
      int ramSlot = 16;
      while (parser.hasMoreCommands()) {
        parser.advance();
        String commandType = parser.commandType();
        String binary = "";
        switch (commandType) {
          case "A_COMMAND" -> {
            try {
              // integer
              int i = Integer.parseInt(parser.symbol());
              binary = Integer.toBinaryString(i);
              binary = String.format("%16s", binary).replace(" ", "0");
            } catch (NumberFormatException e) {
              // variable
              try { // exists in hashmap
                int addr = symbolTable.GetAddress(parser.symbol());
                binary = Integer.toBinaryString(addr);
                binary = String.format("%16s", binary).replace(" ", "0");
              } catch (Exception ex) { // not in hashmap
                symbolTable.addEntry(parser.symbol(), ramSlot);
                binary = Integer.toBinaryString(ramSlot);
                binary = String.format("%16s", binary).replace(" ", "0");
                ramSlot++;
              }
            }
          }
          case "C_COMMAND" -> {
            String comp = parser.comp();
            String dest = parser.dest();
            String jump = parser.jump();
            binary = "111" + code.comp(comp) + code.dest(dest) + code.jump(jump);
          }
          case null, default -> {
            continue;
          }
        }
        if (!isFirstLine) {
          output.write("\n");
        }
        output.write(binary);
        isFirstLine = false;
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}
