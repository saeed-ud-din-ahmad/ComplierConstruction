
import java.util.*;
import java.util.regex.*;

public class BhaiCharaParser {

    private static final Map<String, String> KEYWORDS = new HashMap<>();
    private static final List<Token> tokens = new ArrayList<>();
    private static final List<String> errors = new ArrayList<>();
    private static final SymbolTable symbolTable = new SymbolTable();
    private static int lineNumber = 1;

    static {
        // Initialize keywords and operators
        KEYWORDS.put("bhaiagar", "IF");
        KEYWORDS.put("agarwarna", "ELSE_IF");
        KEYWORDS.put("bhaiwarna", "ELSE");
        KEYWORDS.put("tillWhenBro", "LOOP");
        KEYWORDS.put("dekhana", "PRINT");
        KEYWORDS.put("inputdo", "INPUT");
        KEYWORDS.put("bbool", "BOOL_TYPE");
        KEYWORDS.put("bint", "INT_TYPE");
        KEYWORDS.put("bfloat", "FLOAT_TYPE");
        KEYWORDS.put("bchar", "CHAR_TYPE");
        KEYWORDS.put("jamaIlfaz", "STRING_TYPE");
        KEYWORDS.put("chaleye", "FUNCTION");
    }

    static class Token {

        String type;
        String value;
        int line;

        Token(String type, String value, int line) {
            this.type = type;
            this.value = value;
            this.line = line;
        }
    }

    static class SymbolTableEntry {

        String name;
        String type;
        int declaredLine;

        SymbolTableEntry(String name, String type, int line) {
            this.name = name;
            this.type = type;
            this.declaredLine = line;
        }
    }

    static class SymbolTable {

        List<SymbolTableEntry> entries = new ArrayList<>();

        void addEntry(String name, String type, int line) {
            if (exists(name)) {
                errors.add("Error at line " + line + ": Redeclaration of variable '" + name + "'");
                return;
            }
            entries.add(new SymbolTableEntry(name, type, line));
        }

        boolean exists(String name) {
            return entries.stream().anyMatch(e -> e.name.equals(name));
        }
    }

    public static void main(String[] args) {
        String program = "?? Sample program\n"
                + "bint X = 5;\n"
                + "bfloat Pi = 3.14;\n"
                + "dekhana(\"Hello World\");\n"
                + "bhaiagar (X > 3) {\n"
                + "    dekhana(\"x is great!\");\n"
                + "}";

        tokenize(program);
        parse();
        displayTokenSequence();  // New display method
        displaySymbolTable();
        displayErrors();
    }

    private static void displayTokenSequence() {
        System.out.println("\nTokenization Sequence:");

        Map<Integer, List<Token>> tokensByLine = new TreeMap<>();
        for (Token token : tokens) {
            tokensByLine.computeIfAbsent(token.line, k -> new ArrayList<>()).add(token);
        }

        // Display tokens per line
        for (Map.Entry<Integer, List<Token>> entry : tokensByLine.entrySet()) {
            StringBuilder lineOutput = new StringBuilder();
            for (Token token : entry.getValue()) {
                String displayType = getDisplayType(token.type);
                lineOutput.append("[").append(displayType).append("]");
            }
            System.out.printf("Line %2d: %s%n", entry.getKey(), lineOutput.toString());
        }
    }

    private static String getDisplayType(String tokenType) {
        return switch (tokenType) {
            case "BOOL_TYPE", "INT_TYPE", "FLOAT_TYPE", "CHAR_TYPE", "STRING_TYPE" ->
                "type";
            case "IF", "ELSE_IF", "ELSE", "LOOP", "PRINT", "INPUT", "FUNCTION" ->
                "keyword";
            case "IDENTIFIER" ->
                "identifier";
            case "NUMBER" ->
                "number";
            case "STRING" ->
                "string";
            case "OPERATOR" ->
                "operator";
            case "COMMENT" ->
                "Comment";
            default ->
                "unknown";
        };
    }

    private static void displayTokens() {
        System.out.println("\nTokenization Results:");
        System.out.println("+----------------+-------------------+-------+");
        System.out.println("| Token Type     | Value             | Line  |");
        System.out.println("+----------------+-------------------+-------+");

        for (Token t : tokens) {
            String formattedValue = t.value.length() > 15
                    ? t.value.substring(0, 12) + "..." : t.value;
            System.out.printf("| %-14s | %-17s | %-5d |\n",
                    t.type, formattedValue, t.line);
        }

        System.out.println("+----------------+-------------------+-------+");
    }

    private static void displaySymbolTable() {
        System.out.println("\nSymbol Table:");
        System.out.println("+----------+------------+----------------+");
        System.out.println("| Variable | Type       | Declared Line  |");
        System.out.println("+----------+------------+----------------+");

        for (SymbolTableEntry e : symbolTable.entries) {
            System.out.printf("| %-8s | %-10s | %-14d |\n",
                    e.name, e.type, e.declaredLine);
        }

        System.out.println("+----------+------------+----------------+");
    }

    private static void displayErrors() {
        if (!errors.isEmpty()) {
            System.out.println("\nSyntax Errors:");
            System.out.println("+--------------------------------------------+");
            errors.forEach(err -> System.out.println("| " + err));
            System.out.println("+--------------------------------------------+");
        }
    }

    private static void tokenize(String input) {
        String[] lines = input.split("\n");
        Pattern pattern = Pattern.compile(
                "(?<COMMENT>\\?\\?.*|<<.*?>>)|"
                + "(?<KEYWORD>" + String.join("|", KEYWORDS.keySet()) + ")|"
                + "(?<IDENTIFIER>[A-Z][A-Z0-9]*)|"
                + "(?<NUMBER>\\d+(\\.\\d+)?)|"
                + "(?<STRING>\"[^\"]*\")|"
                + "(?<OPERATOR>[+\\-*/%=(){}<>;])|"
                + "(?<INVALID>[a-z][a-z0-9]*)"
        );

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                if (matcher.group("COMMENT") != null) {
                    continue;
                }
                if (matcher.group("KEYWORD") != null) {
                    String keyword = matcher.group("KEYWORD");
                    tokens.add(new Token(KEYWORDS.get(keyword), keyword, lineNumber));
                } else if (matcher.group("IDENTIFIER") != null) {
                    String identifier = matcher.group("IDENTIFIER");
                    tokens.add(new Token("IDENTIFIER", identifier, lineNumber));
                } else if (matcher.group("NUMBER") != null) {
                    tokens.add(new Token("NUMBER", matcher.group("NUMBER"), lineNumber));
                } else if (matcher.group("STRING") != null) {
                    tokens.add(new Token("STRING", matcher.group("STRING"), lineNumber));
                } else if (matcher.group("OPERATOR") != null) {
                    tokens.add(new Token("OPERATOR", matcher.group("OPERATOR"), lineNumber));
                } else if (matcher.group("INVALID") != null) {
                    errors.add("Error at line " + lineNumber + ": Invalid identifier '"
                            + matcher.group("INVALID") + "'. Must start with capital letter.");
                }
            }
            lineNumber++;
        }
    }

    private static void parse() {
        Stack<String> blockStack = new Stack<>();
        boolean expectSemicolon = false;

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            // Variable declaration check
            if (token.type.endsWith("_TYPE")) {
                if (i + 1 >= tokens.size() || tokens.get(i + 1).type != "IDENTIFIER") {
                    errors.add("Error at line " + token.line + ": Invalid variable declaration");
                } else {
                    String type = token.type.replace("_TYPE", "");
                    String name = tokens.get(i + 1).value;
                    symbolTable.addEntry(name, type, token.line);
                    i += 2; // Skip identifier and possible assignment
                }
            }

            // Syntax structure checks
            switch (token.type) {
                case "IF":
                case "ELSE_IF":
                    if (i + 1 >= tokens.size() || !tokens.get(i + 1).value.equals("(")) {
                        errors.add("Error at line " + token.line + ": Missing '(' after " + token.value);
                    }
                    blockStack.push("IF");
                    break;
                case "ELSE":
                    if (!blockStack.isEmpty() && blockStack.peek().equals("IF")) {
                        blockStack.pop();
                    } else {
                        errors.add("Error at line " + token.line + ": Else without if");
                    }
                    break;
                case "LOOP":
                    blockStack.push("LOOP");
                    break;
                case "PRINT":
                case "INPUT":
                    expectSemicolon = true;
                    break;
                case ";":
                    if (!expectSemicolon) {
                        errors.add("Error at line " + token.line + ": Unexpected semicolon");
                    }
                    expectSemicolon = false;
                    break;
            }
        }

        // Check for unclosed blocks
        while (!blockStack.isEmpty()) {
            errors.add("Error: Unclosed " + blockStack.pop() + " block");
        }
    }
}
