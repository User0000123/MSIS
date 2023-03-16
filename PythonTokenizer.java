import org.python.util.PythonInterpreter;
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class PythonTokenizer {

    private static final String fileWithCodePath = "C:\\Users\\Aleksej\\Desktop\\in1.py";
    private static LinkedList<TToken> tokenFlow;
    private static int next = 0;
    private static int LIMIT = 0;

    public static void main(String[] args) {
        System.out.println("All operators: "+countOperators());
    }

    private static void countLIMIT(){LIMIT = tokenFlow.size() - 1;}

    private static int countOperators() {
        HashMap<TToken, Integer> operators = new HashMap<>();
        int operators_dictionary_size = 0;

        tokenFlow = tokenize(readCodeFromFile(fileWithCodePath));
        countLIMIT();
        TToken item;
        for (int i = 0; i < tokenFlow.size(); i++) {
            item = tokenFlow.get(i);
          /*  switch (item.tokenType()){
                case OPERATION -> {
                    if (item.tokenValue().matches(TokensMap.operations) || item.tokenValue().matches(TokensMap.delimiters)){
                        addToMap(operators, item);
                        operators_dictionary_size++;
                    }
                }
                case INNER_FUNCTION, IDENTIFIER -> {
                    if (retToSave(i) && formattedOutput()) {
                        tokenFlow.addAll(i + 5, getTokenFlowFromString(tokenFlow.get(i + 3).tokenValue()));
                        countLIMIT();
                    }
                    /*else if (retToSave(i) && function()) {
                        System.out.println(item.tokenValue());
                        addToMap(operators, item);
                        operators_dictionary_size++;
                    }
                    else if (retToSave(i) && assignment()) {
                        System.out.println(item.tokenValue());
                        addToMap(operators, tokenFlow.get(i));
                        operators_dictionary_size++;
                     }
                    else if (retToSave(i) && ifStatement()){
                        addToMap(operators, tokenFlow.get(i));
                        System.out.println(tokenFlow.get(i+1).tokenValue());
                        operators_dictionary_size++;
                    }
                    else if (item.tokenValue().matches(TokensMap.keyWords)){
                        addToMap(operators, tokenFlow.get(i));
                        System.out.println(tokenFlow.get(i).tokenValue());
                        operators_dictionary_size++;
                    }
                }
            }*/
        }
//            else if (retToSave(i+1) && expressionWithBraces()) operators_dictionary_size++;

//            if (item.tokenValue().equals("if") && ifChecking()){
//                System.out.println(item.tokenValue()+tokenFlow.get(i+1).tokenValue());
//                function_numbers++;
//    }
//        printMap(operators);
        return operators_dictionary_size;
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

    private static void addToMap(HashMap<TToken, Integer> map, TToken item){
        if (map.putIfAbsent(item, 1) != null) map.replace(item,map.get(item)+1);
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

        res.forEach(item -> {System.out.println(item.tokenValue() +" "+ item.tokenType());});

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
                (retToSave(save) && formattedOutput())||
                (retToSave(save) && anyTypeOfFunction() && termOP("(") && term(TokenType.STRING) && termOP("*") && term(TokenType.NUMBER) && termOP(")"));
    }

    private static boolean formattedOutput(){
        return (termOP("print") && termOP("(") && termOP("f") && term(TokenType.STRING) && termOP(")"));
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

}
record TToken(String tokenValue, TokenType tokenType){}