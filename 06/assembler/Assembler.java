package assembler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Assembler {

    private final List<String> destCode = new ArrayList<>(Arrays.asList("", "M", "D", "MD", "A", "AM", "AD", "AMD"));
    private final List<String> jmpCode = new ArrayList<>(Arrays.asList("", "JGT", "JEQ", "JGE", "JLT", "JNE", "JLE", "JMP"));
    private final HashMap<String, Integer> compCode = new HashMap<>() {
        {
            put("0", 0b0101010);
            put("1", 0b0111111);
            put("-1", 0b0111010);
            put("D", 0b0001100);
            put("A", 0b0110000);
            put("M", 0b1110000);
            put("!D", 0b0001101);
            put("!A", 0b0110001);
            put("!M", 0b1110001);
            put("-D", 0b0001111);
            put("-A", 0b0110011);
            put("-M", 0b1110011);
            put("D+1", 0b0011111);
            put("A+1", 0b0110111);
            put("M+1", 0b1110111);
            put("D-1", 0b0001110);
            put("A-1", 0b0110010);
            put("M-1", 0b1110010);
            put("D+A", 0b0000010);
            put("D+M", 0b1000010);
            put("D-A", 0b0010011);
            put("D-M", 0b1010011);
            put("A-D", 0b0000111);
            put("M-D", 0b1000111);
            put("D&A", 0b0000000);
            put("D&M", 0b1000000);
            put("D|A", 0b0010101);
            put("D|M", 0b1010101);
        }
    };
    private HashMap<String, Integer> symbolMap = new HashMap<>() {
        {
            for (int i = 0; i < 16; i++) {
                put("R" + i, i);
            }
            put("SP", 0);
            put("LCL", 1);
            put("ARG", 2);
            put("THIS", 3);
            put("THAT", 4);
            put("SCREEN", 0x4000);
            put("KBD", 0x6000);
        }
    };

    private List<String> program;
    private List<Integer> usedAddresses;
    private String fileName;

    private void writeToFile() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(String.format("%s.hack", fileName)));
        for (String line : program) {
            writer.write(line);
            writer.write('\n');
        }
        writer.flush();
        writer.close();
    }

    private String removeComments(String in) {
        int index = in.indexOf("//");
        return in.substring(0, index < 0 ? in.length() : index).strip();
    }

    private Integer parseCInstruction(String instruction) {
        int equalSignIndex = instruction.indexOf('=');
        int semicolonIndex = instruction.indexOf(';');

        int dest = destCode.indexOf(instruction.substring(0, equalSignIndex != -1 ? equalSignIndex : 0));
        int jmp = jmpCode.indexOf(instruction.substring(semicolonIndex != -1 ? semicolonIndex + 1 : instruction.length()));
        int comp = compCode.get(instruction.substring(dest == 0 ? 0 : equalSignIndex + 1, jmp != 0 ? semicolonIndex : instruction.length()));

        int code = 0;
        code |= (comp << 6);
        code |= (dest << 3);
        code |= jmp;

        return code;
    }

    private boolean isAlNum(String string) {
        for (Character c : string.toCharArray()) {
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                return true;
            }
        }
        return false;
    }

    private Integer getAvailableAddress() {
        for (int i = 16; i < 256; i++) {
            if (!usedAddresses.contains(i)) {
                return i;
            }
        }
        return 0;
    }

    private Integer parseAInstruction(String instruction) {
        String sub = instruction.substring(1);
        if (isAlNum(sub)) {
            if (symbolMap.containsKey(sub)) {
                return symbolMap.get(sub);
            } else {
                int address = getAvailableAddress();
                symbolMap.put(sub, address);
                usedAddresses.add(address);
                return address;
            }
        } else {
            return Integer.parseInt(sub);
        }
    }

    private void assemble() {
        String line;
        for (int i = 0; i < program.size(); i++) {
            line = program.get(i);
            int opcode = 0;

            if (line.charAt(0) == '@') {
                opcode |= parseAInstruction(line);
                opcode &= ~(1 << 15);
            } else {
                opcode |= parseCInstruction(line);
                opcode |= (7 << 13);
            }
            program.set(i, String.format("%16s", Integer.toBinaryString(opcode)).replace(' ', '0'));
        }
        System.out.println(program);
    }

    private void firstPass() throws FileNotFoundException {
        int programCounter = 0;
        Scanner scanner = new Scanner(new File(String.format("%s.asm", fileName)));
        program = new ArrayList<>();
        usedAddresses = new ArrayList<>();
        String line;

        while (scanner.hasNext()) {
            line = removeComments(scanner.nextLine());
            if (!(line.isBlank())) {
                if (line.contains("(") && line.contains(")")) {
                    symbolMap.put(line.substring(1, line.length() - 1), programCounter);
                } else {
                    program.add(line);
                    programCounter++;
                }
            }
        }
        System.out.println(programCounter);
        System.out.println(program);
    }

    private void start(String file) throws IOException {
        fileName = file;
        firstPass();
        assemble();
        writeToFile();
    }

}
