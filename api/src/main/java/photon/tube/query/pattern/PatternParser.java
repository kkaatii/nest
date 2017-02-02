package photon.tube.query.pattern;

import photon.util.PStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class PatternParser<T> {

    private final static char OPEN_BRACKET = '[';
    private final static char CLOSE_BRACKET = ']';
    private final static char GREATER_THAN = '>';
    private final static char UNDERSCORE = '_';
    private final static char COLON = ':';
    private final static char ASTERISK = '*';
    private final static char HYPHEN = '-';
    private final static char COMMA = ',';
    private final static char SPACE = ' ';

    private final PStack<Integer> vIdStack;
    private final List<Character> currLexeme;
    private final List<T> unitList;
    private final List<int[]> timesOptionsList;
    private ParsingState state;

    public PatternParser() {
        vIdStack = new PStack<>();
        currLexeme = new LinkedList<>();
        state = ParsingState.NONE;
        unitList = new ArrayList<>();
        timesOptionsList = new ArrayList<>();
    }

    public Pattern<T> parse(String queryString) throws IllegalArgumentException {
        int length = queryString.length();
        for (int i = 0; i < length; i++) {
            char ch = queryString.charAt(i);
            switch (ch) {
                case OPEN_BRACKET:
                    changeState(ParsingState.VERTEX_BEGIN);
                    break;
                case CLOSE_BRACKET:
                    changeState(ParsingState.VERTEX_END);
                    break;
                case GREATER_THAN:
                    changeState(ParsingState.EDGE_LABEL);
                case COLON:
                    changeState(ParsingState.EDGE_TIMES);
                    break;
                case SPACE:
                    continue;
                default:
                    append(ch);
            }
        }
        return null;
    }

    private void append(char ch) throws IllegalArgumentException {
        if (!state.validChar(ch))
            throw new IllegalArgumentException();
    }

    private void changeState(ParsingState nextState) {

    }

    static Predicate<Character> isChar(char predefined) {
        return ch -> ch == predefined;
    }

    private enum ParsingState {
        NONE(null),
        VERTEX_BEGIN(Character::isDigit, isChar(COMMA), isChar(SPACE)),
        VERTEX_END,
        EDGE_LABEL(Character::isLetter, isChar(HYPHEN), isChar(UNDERSCORE)),
        EDGE_TIMES(Character::isDigit, isChar(ASTERISK), isChar(COMMA), isChar(SPACE));

        private final Predicate<Character>[] validCharPredicates;

        @SafeVarargs
        ParsingState(Predicate<Character>... validCharPredicates) {
            this.validCharPredicates = validCharPredicates;
        }

        public boolean validChar(char ch) {
            return Arrays.stream(validCharPredicates).map(p -> p.test(ch)).reduce(false, Boolean::logicalOr);
        }
    }
}
