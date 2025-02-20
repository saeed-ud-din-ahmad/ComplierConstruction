import java.util.*;
import java.util.stream.Collectors;

public class RegexToDFA {

    private static class Fragment {

        int start;
        int accept;

        Fragment(int start, int accept) {
            this.start = start;
            this.accept = accept;
        }
    }

    private static int stateCounter = 0;
    private static Map<Integer, Map<Character, Set<Integer>>> nfaTransitions = new HashMap<>();
    private static int nfaStart;
    private static int nfaAccept;

    private static int createState() {
        return stateCounter++;
    }

    private static void addTransition(int from, char symbol, int to) {
        nfaTransitions.putIfAbsent(from, new HashMap<>());
        Map<Character, Set<Integer>> transitions = nfaTransitions.get(from);
        transitions.putIfAbsent(symbol, new HashSet<>());
        transitions.get(symbol).add(to);
    }

    private static String addConcat(String regex) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < regex.length(); i++) {
            char c1 = regex.charAt(i);
            sb.append(c1);
            if (i < regex.length() - 1) {
                char c2 = regex.charAt(i + 1);
                boolean c1IsOperator = isOperator(c1) || c1 == '(' || c1 == ')';
                boolean c2IsOperator = isOperator(c2) || c2 == '(' || c2 == ')';
                boolean c1NeedsConcat = !c1IsOperator || c1 == ')' || c1 == '*';
                boolean c2NeedsConcat = !c2IsOperator || c2 == '(';
                if (c1NeedsConcat && c2NeedsConcat) {
                    sb.append('·');
                }
            }
        }
        return sb.toString();
    }

    private static String regexToPostfix(String regex) {
        String modifiedRegex = addConcat(regex);
        StringBuilder output = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        for (char c : modifiedRegex.toCharArray()) {
            if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    output.append(stack.pop());
                }
                stack.pop();
            } else if (isOperator(c)) {
                while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(c)) {
                    output.append(stack.pop());
                }
                stack.push(c);
            } else {
                output.append(c);
            }
        }

        while (!stack.isEmpty()) {
            output.append(stack.pop());
        }

        return output.toString();
    }

    private static boolean isOperator(char c) {
        return c == '|' || c == '·' || c == '*' || c == '(' || c == ')';
    }

    private static int precedence(char op) {
        switch (op) {
            case '*':
                return 3;
            case '·':
                return 2;
            case '|':
                return 1;
            default:
                return 0;
        }
    }

    private static void buildNFA(String postfix) {
        Stack<Fragment> stack = new Stack<>();

        for (char c : postfix.toCharArray()) {
            switch (c) {
                case '·':
                    Fragment f2 = stack.pop();
                    Fragment f1 = stack.pop();
                    addTransition(f1.accept, 'ε', f2.start);
                    stack.push(new Fragment(f1.start, f2.accept));
                    break;
                case '|':
                    Fragment f2Or = stack.pop();
                    Fragment f1Or = stack.pop();
                    int s0 = createState();
                    int s3 = createState();
                    addTransition(s0, 'ε', f1Or.start);
                    addTransition(s0, 'ε', f2Or.start);
                    addTransition(f1Or.accept, 'ε', s3);
                    addTransition(f2Or.accept, 'ε', s3);
                    stack.push(new Fragment(s0, s3));
                    break;
                case '*':
                    Fragment fStar = stack.pop();
                    int s0Star = createState();
                    int s1Star = createState();
                    addTransition(s0Star, 'ε', fStar.start);
                    addTransition(s0Star, 'ε', s1Star);
                    addTransition(fStar.accept, 'ε', fStar.start);
                    addTransition(fStar.accept, 'ε', s1Star);
                    stack.push(new Fragment(s0Star, s1Star));
                    break;
                default:
                    int s0Lit = createState();
                    int s1Lit = createState();
                    addTransition(s0Lit, c, s1Lit);
                    stack.push(new Fragment(s0Lit, s1Lit));
            }
        }

        Fragment nfaFragment = stack.pop();
        nfaStart = nfaFragment.start;
        nfaAccept = nfaFragment.accept;
    }

    private static Set<Character> getAlphabet(String regex) {
        Set<Character> alphabet = new HashSet<>();
        for (char c : regex.toCharArray()) {
            if (!isOperator(c) && c != '(' && c != ')') {
                alphabet.add(c);
            }
        }
        return alphabet;
    }

    private static Set<Integer> epsilonClosure(Set<Integer> states) {
        Set<Integer> closure = new HashSet<>(states);
        Queue<Integer> queue = new LinkedList<>(states);
        while (!queue.isEmpty()) {
            int s = queue.poll();
            Map<Character, Set<Integer>> transitions = nfaTransitions.getOrDefault(s, new HashMap<>());
            Set<Integer> epsilonTrans = transitions.getOrDefault('ε', new HashSet<>());
            for (int next : epsilonTrans) {
                if (!closure.contains(next)) {
                    closure.add(next);
                    queue.add(next);
                }
            }
        }
        return closure;
    }

    private static Set<Integer> move(Set<Integer> states, char symbol) {
        Set<Integer> result = new HashSet<>();
        for (int s : states) {
            Map<Character, Set<Integer>> transitions = nfaTransitions.getOrDefault(s, new HashMap<>());
            Set<Integer> symbolTrans = transitions.getOrDefault(symbol, new HashSet<>());
            result.addAll(symbolTrans);
        }
        return result;
    }

    private static Map<Integer, Set<Integer>> dfaStates = new HashMap<>();
    private static Map<Integer, Map<Character, Integer>> dfaTransitions = new HashMap<>();
    private static Set<Integer> dfaAccepts = new HashSet<>();
    private static int dfaStart;
    private static Set<Character> alphabet;

    private static void buildDFA(Set<Character> alphabet) {
        dfaStates.clear();
        dfaTransitions.clear();
        dfaAccepts.clear();

        Set<Integer> initialState = epsilonClosure(Collections.singleton(nfaStart));
        dfaStart = 0;
        dfaStates.put(dfaStart, initialState);

        Queue<Integer> queue = new LinkedList<>();
        queue.add(dfaStart);

        Map<Set<Integer>, Integer> stateMap = new HashMap<>();
        stateMap.put(initialState, dfaStart);

        while (!queue.isEmpty()) {
            int currentStateId = queue.poll();
            Set<Integer> currentNfaStates = dfaStates.get(currentStateId);

            for (char symbol : alphabet) {
                Set<Integer> moved = move(currentNfaStates, symbol);
                Set<Integer> closure = epsilonClosure(moved);

                if (closure.isEmpty()) {
                    continue;
                }

                if (!stateMap.containsKey(closure)) {
                    int newStateId = dfaStates.size();
                    stateMap.put(closure, newStateId);
                    dfaStates.put(newStateId, closure);
                    queue.add(newStateId);
                }

                int targetStateId = stateMap.get(closure);
                dfaTransitions.putIfAbsent(currentStateId, new HashMap<>());
                dfaTransitions.get(currentStateId).put(symbol, targetStateId);
            }
        }

        for (Map.Entry<Integer, Set<Integer>> entry : dfaStates.entrySet()) {
            if (entry.getValue().contains(nfaAccept)) {
                dfaAccepts.add(entry.getKey());
            }
        }
    }

    private static void displayNFA() {
        System.out.println("NFA Transition Table:");
        System.out.println("Start State: " + nfaStart);
        System.out.println("Accept State: " + nfaAccept);
        System.out.println("State\tSymbol\tNext States");
        List<Integer> states = new ArrayList<>(nfaTransitions.keySet());
        Collections.sort(states);
        for (int state : states) {
            Map<Character, Set<Integer>> transitions = nfaTransitions.get(state);
            List<Character> symbols = new ArrayList<>(transitions.keySet());
            Collections.sort(symbols);
            for (char symbol : symbols) {
                Set<Integer> nextStates = transitions.get(symbol);
                System.out.printf("%d\t%c\t%s\n", state, symbol, nextStates);
            }
        }
    }

    private static void displayDFA() {
        System.out.println("\nDFA Transition Table:");
        System.out.println("Start State: S" + dfaStart);
        System.out.println("Accept States: " + dfaAccepts.stream().map(s -> "S" + s).collect(Collectors.toList()));
        System.out.println("State\tNFA States\tSymbol\tNext State");
        List<Integer> dfaStateIds = new ArrayList<>(dfaStates.keySet());
        Collections.sort(dfaStateIds);
        for (int stateId : dfaStateIds) {
            String stateLabel = "S" + stateId;
            String nfaStates = dfaStates.get(stateId).toString();
            Map<Character, Integer> transitions = dfaTransitions.getOrDefault(stateId, new HashMap<>());
            List<Character> symbols = new ArrayList<>(alphabet);
            Collections.sort(symbols);
            for (char symbol : symbols) {
                if (transitions.containsKey(symbol)) {
                    int nextState = transitions.get(symbol);
                    System.out.printf("%s\t%s\t%c\tS%d\n", stateLabel, nfaStates, symbol, nextState);
                }
            }
        }
    }

    // public static void main(String[] args) {
    //     Scanner scanner = new Scanner(System.in);
    //     System.out.print("Enter regular expression: ");
    //     String regex = scanner.nextLine().trim();

    //     String postfix = regexToPostfix(regex);
    //     System.out.println("Postfix expression: " + postfix);

    //     buildNFA(postfix);
    //     alphabet = getAlphabet(regex);
    //     buildDFA(alphabet);

    //     displayNFA();
    //     displayDFA();

    //     scanner.close();
    // }
}