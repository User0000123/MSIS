import org.python.util.PythonInterpreter;
import java.io.*;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.Scanner;

public class PythonTokenizer {

    private static final String fileWithCodePath = "C:\\Users\\Aleksej\\Desktop\\in1.py";
    private static TokensMap tokensMap = new TokensMap();

    public static void main(String[] args) {
        tokenize(readCodeFromFile(fileWithCodePath));
    }

    private static String readCodeFromFile(String path){
        File f = new File(path);
        StringBuilder res = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(Files.newBufferedReader(f.toPath()))) {
            String tempLine;
            while ((tempLine = reader.readLine()) != null) res.append(tempLine).append('\n');
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return res.toString();
    }

    private static TokenType getTokenType(String key){
        return tokensMap.getMap().get(key);
    }

    public static LinkedList<TToken> tokenize(String code) {
        StringWriter a = new StringWriter(100);
        LinkedList<TToken> res = new LinkedList<>();

        try (PythonInterpreter interpreter = new PythonInterpreter()) {
            interpreter.setIn(new StringReader(code));
            interpreter.setOut(a);
            interpreter.execfile("C:\\Users\\Aleksej\\Downloads\\jython2.7.3\\Lib\\tokenize.py");
        }

        Scanner vConsole = new Scanner(a.toString());
        String[] lineGroup;
        while (vConsole.hasNextLine()){
            lineGroup = vConsole.nextLine().split("\t");
            lineGroup[2] = lineGroup[2].substring(1, lineGroup[2].length()-1);
            if (lineGroup[1].compareTo("NAME") == 0) res.add(new TToken(lineGroup[2], getTokenType(lineGroup[2])));
        }

        res.forEach(item -> {System.out.println(item.tokenValue() +" "+ item.tokenType());});

        return null;
    }
}
record TToken(String tokenValue, TokenType tokenType){}