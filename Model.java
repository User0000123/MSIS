import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Model{
    private static HashMap<String, TokenType> mainKeywords;

    public static void main(String[] args) {
        initMainKeywords();
        System.out.println(getTokensFromFile("C:\\Users\\Aleksej\\Desktop\\in.txt"));
    }

    public static TokenType getTokenType(String key){
        return mainKeywords.get(key);
    }

    private static void initMainKeywords(){
        mainKeywords = new HashMap<>();
        mainKeywords.put("for", TokenType.FOR);
        mainKeywords.put("while", TokenType.WHILE);
        mainKeywords.put("if", TokenType.IF);
        mainKeywords.put("else", TokenType.ELSE);
        mainKeywords.put("elif", TokenType.ELIF);
        //mainKeywords.put("print", TokenType);
        mainKeywords.put("None", TokenType.NONE);
        mainKeywords.put("+", TokenType.PLUS);
        mainKeywords.put("-", TokenType.MINUS);
        mainKeywords.put("*", TokenType.MULTIPLICATION);
        mainKeywords.put("/", TokenType.DIVIDE);
        mainKeywords.put("=", TokenType.ASSIGNMENT);
        mainKeywords.put(":", TokenType.COLON);
        mainKeywords.put(";", TokenType.SEMICOLON);
        mainKeywords.put("(", TokenType.LEFT_BRACE);
        mainKeywords.put(")", TokenType.RIGHT_BRACE);
        //mainKeywords.put(">=", TokenType.FOR.GREATER_OR_EQUALS);
        //mainKeywords.put("<=", TokenType.FOR.LOWER_OR_EQUALS);
        //mainKeywords.put(">", TokenType.FOR.GREATER_THAN);
        //mainKeywords.put("<", TokenType.FOR.LOWER_THAN);
//        mainKeywords.put("<>", TokenType.FOR.NOT_EQUALS);
//        mainKeywords.put(":=", TokenType.FOR.ASSIGNMENT_OPERATOR);
//        mainKeywords.put("@", TokenType.FOR.AT_SIGN);
        mainKeywords.put("{", TokenType.LEFT_CBRACE);
        mainKeywords.put("}", TokenType.RIGHT_CBRACE);
    }

    public static String getTokensFromFile(String filePath){
        File input = new File(filePath);
        StringBuilder rawInput = new StringBuilder();

        try (BufferedReader reader = Files.newBufferedReader(input.toPath())) {
            String line;
            while ((line = reader.readLine()) != null) rawInput.append(line).append('\n');
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rawInput.toString();
    }

    private static String lexicalAnalyzer(String rawInput){
        Pattern operators = Pattern.compile("(?<=\s|^)(as|and|break|continue|def|elif|else|if|False|True|None|with|for|in|is|nor|not|return|while)(?=\s|:)");
        Pattern comments = Pattern.compile("#.+");
        Pattern identifiers = Pattern.compile("[a-zA-Z_]+[a-zA-Z0-9_]*");
        Pattern delimiters = Pattern.compile("(\\[|\\]|\\(|\\)|:|-|\\*|\\/|\\{|\\}|\\||\\')");

        Matcher m_comments = comments.matcher(rawInput);
        rawInput = m_comments.replaceAll("");

//        Matcher m_operators = operators.matcher(rawInput);

        StringBuilder res = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for (String line: rawInput.lines().toList()){
            for (char chr:line.toCharArray()){
                temp.append(chr);
            }
        }

        return res.toString();
    }
}
