import org.python.util.PythonInterpreter;
import java.io.*;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.Scanner;

public class PythonTokenizer {

    private static final String fileWithCodePath = "C:\\Users\\Aleksej\\Desktop\\in1.py";

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

    public static LinkedList<Record> tokenize(String code) {
        StringWriter a = new StringWriter(100);
        LinkedList<Record> res = new LinkedList<>();

        try (PythonInterpreter interpreter = new PythonInterpreter()) {
            interpreter.setIn(new StringReader(code));
            interpreter.execfile("C:\\Users\\Aleksej\\Downloads\\jython2.7.3\\Lib\\tokenize.py");
            interpreter.setOut(a);
        }

        Scanner vConsole = new Scanner(a.toString());
        String[] lineGroup;
        while (vConsole.hasNextLine()){
            lineGroup = vConsole.nextLine().split(" ");
            if (lineGroup != null) for (int i = 0; i<lineGroup.length;i++) System.out.println(lineGroup[i]);
//            if (lineGroup.contains("NAME")) res.add(new TToken(Model.getTokenType()))
        }

        return null;
    }
}
record TToken(String tokenValue, TokenType tokenType){}