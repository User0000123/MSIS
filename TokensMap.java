import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokensMap {
    private HashMap<String, TokenType> mainKeywords;
    static String keyWords = "(def|break|continue|if|elif|for|return|while)";
    static String logicalOPs = "(as|and|with|in|is|not)";
    static String inner_functions = "(abs|bool|bytes|chr|hash|len|pow|print|sorted|type|input|isdigit|int|range)";
    static String identifiers = "[a-zA-Z_]+[a-zA-Z0-9_]*";
    static String build_in_vars = "(__name__|False|True|None)";
    static String operations = "(\\+|\\-|\\/|\\*|\\>\\=|\\>|\\<\\=|\\<|\\=\\=|\\!\\=|\\%)";
    static String delimiters = "(\\.|\\;|\\:|\\,)";
}
