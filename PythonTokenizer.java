import jnr.ffi.annotations.In;
import org.python.util.PythonInterpreter;
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class PythonTokenizer {

    private static final String fileWithCodePath = "C:\\Users\\Aleksej\\Desktop\\in1.txt";
    public static LinkedList<TToken> tokenFlow;
    private static int next = 0;
    private static int LIMIT = 0;
    public static HashMap<TToken, Integer> operators = new HashMap<>();
    public static HashMap<TToken, Integer> operands = new HashMap<>();

    private static void countLIMIT(){LIMIT = tokenFlow.size() - 1;}

    public static void count() {
        operators.clear();
        operands.clear();
        //tokenFlow = tokenize(readCodeFromFile(fileWithCodePath));
        countLIMIT();

        TToken item;
        for (int i = 0; i < tokenFlow.size(); i++) {
            item = tokenFlow.get(i);
            switch (item.tokenType()){
                case OPERATION -> {
                    if ((item.tokenValue().matches(TokensMap.operations)) ||
                            (item.tokenValue().matches(TokensMap.delimiters)) ||
                            (item.tokenValue().matches(TokensMap.logicalOPs)))
                    {
                        addToMap(operators, item);
                    }
                    else if (retToSave(i) && (tokenFlow.get(i-1).tokenType() != TokenType.IDENTIFIER && tokenFlow.get(i-1).tokenType() != TokenType.INNER_FUNCTION) && expressionWithBraces())
                        addToMap(operators, new TToken("()",TokenType.OPERATION));
                    else if (retToSave(i) && curlyBracketsEnc()) addToMap(operators,new TToken("{}",TokenType.OPERATION));
                    else if (retToSave(i) && (tokenFlow.get(i-1).tokenType() != TokenType.IDENTIFIER) && arrayCreating()) addToMap(operators, new TToken("[]",TokenType.OPERATION));
                }
                case INNER_FUNCTION, IDENTIFIER -> {
                    if (retToSave(i) && formattedIO()) {
                        tokenFlow.addAll(i + 5, getTokenFlowFromString(tokenFlow.get(i + 3).tokenValue()));
                        countLIMIT();
                    }
                    if (!tokenFlow.get(i-1).tokenValue().equals("def")){
                        if (retToSave(i) && function()) {
                            addToMap(operators, item);
                            if (!(tokenFlow.get(i-1).tokenType() == TokenType.UNKNOWN)) addToMap(operands, item);
                        }
                        else if (retToSave(i) && operand()) addToMap(operands, item);
                    }
                    if (retToSave(i) && assignment()) addToMap(operators, new TToken("=",TokenType.OPERATION));
                    if (retToSave(i) && getArrayElem()) addToMap(operators, new TToken("[]",TokenType.OPERATION));
                }
                case KEY_WORD -> {
                    if (retToSave(i) && ifStatement()) addToMap(operators,new TToken("if..else(include elif)",TokenType.KEY_WORD));
                    else if (retToSave(i) && whileStatement() ||
                            (retToSave(i) && forStatement())
                            )
                    {
                        addToMap(operators,item);
                    }
                    else if (item.tokenValue().equals("break") || item.tokenValue().equals("return") || item.tokenValue().equals("continue")) addToMap(operators, item);
                }
                case NUMBER, STRING, BUILD_IN_VAR -> {
                    addToMap(operands,item);
                }
            }
        }
    }

    public static int getSum(HashMap<TToken,Integer> hashMap){
        int sum=0;
        for (Integer count:hashMap.values()) sum+=count;
        return sum;
    }

    private static LinkedList<TToken> getTokenFlowFromString(String string){
        StringBuilder params = new StringBuilder(string);
        StringBuilder result = new StringBuilder();
        int start, end;

        while ((start = params.indexOf("{")) >= 0 && (end = params.indexOf("}")) >= 0){
            result.append(params.substring(start,end+1));
            params.delete(start, end+1);
        }

        return tokenize(result.toString());
    }

    private static void printMap(HashMap<TToken, Integer> map){
        map.forEach((key, value) -> {System.out.println(key.tokenValue() + " count: "+ value);});
    }

    private static boolean addToMap(HashMap<TToken, Integer> map, TToken item){
        if (map.putIfAbsent(item, 1) != null) map.replace(item,map.get(item)+1);
        return true;
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
                else if (lineGroup[2].matches(TokensMap.logicalOPs)) res = TokenType.OPERATION;
                else if (lineGroup[2].matches(TokensMap.inner_functions)) res = TokenType.INNER_FUNCTION;
                else if (lineGroup[2].matches(TokensMap.build_in_vars)) res = TokenType.BUILD_IN_VAR;
                else if (lineGroup[2].matches(TokensMap.identifiers)) res = TokenType.IDENTIFIER;
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

//        res.forEach(item -> {System.out.println(item.tokenValue() +" "+ item.tokenType());});

        return res;
    }

    private static boolean termOP(final String expected){
        if (next>LIMIT) return false;
        return tokenFlow.get(next++).tokenValue().equals(expected);
    }

    private static boolean term(final TokenType expected){
        if (next>LIMIT) return false;
        return tokenFlow.get(next++).tokenType() == expected;
    }
    /*
        @@functions detecting
    */
    private static boolean function(){
        int save = next;
        return (anyTypeOfFunction() && termOP("(") && params_enc() && termOP(")"))||
                (retToSave(save) && formattedIO())||
                (retToSave(save) && anyTypeOfFunction() && termOP("(") && term(TokenType.STRING) && termOP("*") && term(TokenType.NUMBER) && termOP(")"));
    }

    private static boolean formattedIO(){
        int save = next;
        return (termOP("input") && termOP("(") && termOP("f") && term(TokenType.STRING) && termOP(")"))||
                (retToSave(save) && termOP("print") && termOP("(") && termOP("f") && term(TokenType.STRING) && termOP(")"));
    }

    private static boolean anyTypeOfFunction(){
        int save = next;
        return (term(TokenType.IDENTIFIER)) || (retToSave(save) && term(TokenType.INNER_FUNCTION));
    }

    private static boolean retToSave(final int save) {next = save; return true;}

    private static boolean getArrayElem(){
        return (term(TokenType.IDENTIFIER) && termOP("[") && expression() && termOP("]"));
    }

    private static boolean expression(){
        int save = next;
        return expressionWithBraces() ||
                (retToSave(save) && operand() && anyOperation() && anyOperation() && expression()) ||
                (retToSave(save) && operand() && anyOperation() && expression()) ||
                (retToSave(save) && operand());
    }

    private static boolean expressionWithBraces(){
        int save = next;
        return (retToSave(save) && termOP("(") && expression() && termOP(")") && anyOperation() && expression()) ||
                (retToSave(save) && termOP("(") && expression() && termOP(")"));
    }

    private static boolean anyOperation(){
        return (checkArifmOperation());
    }

    private static boolean operand(){
        int save = next;
        return (retToSave(save) && term(TokenType.NUMBER))||
                (retToSave(save) && method())||
                (retToSave(save) && function())||
                (retToSave(save) && getArrayElem()) ||
                (retToSave(save) && term(TokenType.BUILD_IN_VAR)) ||
                (retToSave(save) && term(TokenType.KEY_WORD)) ||
                (retToSave(save) && term(TokenType.IDENTIFIER))||
                (retToSave(save) && term(TokenType.STRING))||
                (retToSave(save) && termOP("[") && term(TokenType.STRING) && termOP("]") && termOP("*") && term(TokenType.NUMBER))||
                (retToSave(save) && arrayCreating());
    }

    private static boolean method(){
        return term(TokenType.IDENTIFIER) && termOP(".") && function();
    }

    private static boolean arrayCreating(){
        return (termOP("[") && params_enc() && termOP("]"));
    }

    private static boolean checkArifmOperation(){
        int save = next;
        return (retToSave(save) && tokenFlow.get(next++).tokenValue().matches(TokensMap.operations))||
                (retToSave(save) && tokenFlow.get(next++).tokenValue().matches(TokensMap.logicalOPs));
    }

    private static boolean params_enc(){
        int save = next;
        return (operand() && termOP(",") && params_enc()) ||
                (retToSave(save) && operand()) ||
                (retToSave(save));
    }

    /*
        @@assignments detecting
    */

    private static boolean assignment(){
        int save = next;
        return (retToSave(save) && operand() && termOP("=") && expression());
    }

    /*
        @@recursive if detecting
    */

    private static boolean ifStatement(){
        int save = next;
        return (retToSave(save) && termOP("if") && expression() && termOP(":")) ||
             (retToSave(save) && termOP("elif") && expression() && termOP(":"));
    }
    private static boolean whileStatement(){
        int save = next;
        return (retToSave(save) && termOP("while") && expression() && termOP(":"));
    }
    private static boolean forStatement(){
        int save = next;
        return (retToSave(save) && termOP("for") && identifierSequence() && termOP("in") && sequenceSN() && termOP(":"))||
            (retToSave(save) && termOP("for") && identifierSequence() && termOP("in") && term(TokenType.IDENTIFIER) && termOP(":"))||
            (retToSave(save) && termOP("for") && identifierSequence() && termOP("in") && termOP("range") && termOP("(") && params_enc() && termOP(")") && termOP(":"));
    }

    private static boolean sequenceSN(){
        int save = next;
        return  (retToSave(save) && term(TokenType.NUMBER) && termOP(",") && sequenceSN())||
                (retToSave(save) && term(TokenType.STRING) && termOP(",") && sequenceSN())||
                (retToSave(save) && term(TokenType.STRING))||
                (retToSave(save) && term(TokenType.NUMBER));
    }

    private static boolean identifierSequence(){
        int save = next;
        return (retToSave(save) && term(TokenType.IDENTIFIER) && termOP(",") && identifierSequence()) ||
                (retToSave(save) && term(TokenType.IDENTIFIER));
    }

    private static boolean curlyBracketsEnc(){
        return (termOP("{") && params_enc() && termOP("}"));
    }

}
record TToken(String tokenValue, TokenType tokenType){}