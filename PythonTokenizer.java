import org.python.util.PythonInterpreter;
import java.io.*;
import java.net.InterfaceAddress;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.Scanner;

public class PythonTokenizer {

    private static final String fileWithCodePath = "C:\\Users\\Aleksej\\Desktop\\in1.py";
    private static LinkedList<TToken> tokenFlow;
    private static int next = 0;

    public static void main(String[] args) {
        TToken item;
        int function_numbers = 0;
        tokenFlow = tokenize(readCodeFromFile(fileWithCodePath));
        for (int i = 0; i<tokenFlow.size();i++){
            item = tokenFlow.get(i);
            next = i;
            if (item.tokenType()==TokenType.INNER_FUNCTION || item.tokenType()==TokenType.IDENTIFIER)
                if (function()){
                    System.out.println(item.tokenValue());
                    function_numbers++;
                }
        }
        System.out.println("FFF -> "+function_numbers);
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

    private static TokenType getTokenType(String[] lineGroup){
        TokenType res = null;
        switch (lineGroup[1]){
            case "NAME"->{
                if (lineGroup[2].matches(TokensMap.keyWords)) res = TokenType.KEY_WORD;
                else if (lineGroup[2].matches(TokensMap.inner_functions)) res = TokenType.INNER_FUNCTION;
                else if (lineGroup[2].matches(TokensMap.identifiers)||(lineGroup[2].matches(TokensMap.build_in_vars))) res = TokenType.IDENTIFIER;
            }
            case "STRING"->res = TokenType.STRING;
            case "NUMBER"->res = TokenType.NUMBER;
            case "OP"->res = TokenType.OPERATION;
            case "COMMENT" -> res = TokenType.COMMENT;
            default -> res = TokenType.UNKNOWN;
        }
        return res;
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
            res.add(new TToken(lineGroup[2], getTokenType(lineGroup)));
        }

       // res.forEach(item -> {System.out.println(item.tokenValue() +" "+ item.tokenType());});

        return res;
    }

    private static boolean termOP(final String expected){
        return tokenFlow.get(next++).tokenValue().equals(expected);
    }

    private static boolean term(final TokenType expected){
        return tokenFlow.get(next++).tokenType() == expected;
    }

    private static boolean function(){
        int save = next;
        return (anyTypeOfFunction() && termOP("(") && params_enc() && termOP(")"))||
                (retToSave(save) && anyTypeOfFunction() && termOP("(") && termOP("f") && term(TokenType.STRING) && termOP(")"));
    }

    private static boolean anyTypeOfFunction(){
        int save = next;
        return (term(TokenType.IDENTIFIER)) || (retToSave(save) && term(TokenType.INNER_FUNCTION));
    }

    private static boolean retToSave(final int save) {next = save; return true;}

    private static boolean funct_params(){
        int save = next;
        return (retToSave(save) && term(TokenType.NUMBER))||
                (retToSave(save) && term(TokenType.IDENTIFIER))||
                (retToSave(save) && term(TokenType.STRING));
    }

    private static boolean params_enc(){
        int save = next;
        return (funct_params() && termOP(",") && params_enc()) ||
                (retToSave(save) && funct_params()) ||
                (retToSave(save) && Boolean.getBoolean("true"));
    }

}
record TToken(String tokenValue, TokenType tokenType){}