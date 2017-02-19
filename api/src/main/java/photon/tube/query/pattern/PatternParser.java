package photon.tube.query.pattern;

import photon.tube.model.ArrowType;
import photon.util.PStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class PatternParser {

    private final static char OPEN_BRACKET = '[';
    private final static char CLOSE_BRACKET = ']';
    private final static char GREATER_THAN = '>';
    private final static char UNDERSCORE = '_';
    private final static char COLON = ':';
    private final static char ASTERISK = '*';
    private final static char HYPHEN = '-';
    private final static char COMMA = ',';
    private final static char SPACE = ' ';

    private final PStack<Integer> vertexIds;
    private StringBuilder currLexeme;
    private final List<ArrowType> unitList;
    private final List<int[]> timesOptionsList;
    private final List<Integer> timesOptions;
    private ParsingState state;

    public PatternParser() {
        vertexIds = new PStack<>();
        currLexeme = new StringBuilder();
        state = ParsingState.NONE;
        unitList = new ArrayList<>();
        timesOptionsList = new ArrayList<>();
        timesOptions = new ArrayList<>();
    }

    public SequencePattern<ArrowType> parse(String queryString) throws IllegalArgumentException {
        int length = queryString.length();
        loop:
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
                    break;
                case COLON:
                    changeState(ParsingState.EDGE_TIMES);
                    break;
                case SPACE:
                    continue loop;
                default:
                    append(ch);
            }
        }
        changeState(ParsingState.NONE);
        return new SequencePattern<>(unitList, timesOptionsList);
    }

    private void append(char ch) throws IllegalArgumentException {
        if (!state.validChar(ch))
            throw new IllegalArgumentException("illegal char for " + state + " - '" + ch + "'");
        currLexeme.append(ch);
    }

    private void changeState(ParsingState nextState) {
        if (currLexeme.length() > 0) {
            switch (state) {
                case VERTEX_BEGIN:
                    vertexIds.push(Integer.valueOf(currLexeme.toString()));
                    currLexeme = new StringBuilder();
                    break;
                case EDGE_LABEL:
                    unitList.add(ArrowType.extendedValueOf(currLexeme.toString()));
                    currLexeme = new StringBuilder();
                    break;
                case EDGE_TIMES:
                    timesOptions.add(Integer.valueOf(currLexeme.toString()));
                    currLexeme = new StringBuilder();
            }
        }
        if (nextState == ParsingState.EDGE_LABEL || nextState == ParsingState.NONE) {
            int[] timesOptionsArr = timesOptions.stream().mapToInt(i -> i).toArray();
            if (timesOptionsArr.length > 0) {
                timesOptionsList.add(timesOptionsArr);
                timesOptions.clear();
            }
        }
        state = nextState;
    }

    static Predicate<Character> isChar(char predefined) {
        return ch -> ch == predefined;
    }

    private enum ParsingState {
        NONE(ch -> false),
        VERTEX_BEGIN(Character::isDigit, isChar(COMMA)),
        VERTEX_END(ch -> false),
        EDGE_LABEL(Character::isLetter, isChar(HYPHEN), isChar(UNDERSCORE), isChar(ArrowType.REVERSE_SIGN)),
        EDGE_TIMES(Character::isDigit, isChar(ASTERISK), isChar(COMMA));

        private final List<Predicate<Character>> validCharPredicates;

        @SafeVarargs
        ParsingState(Predicate<Character>... validCharPredicates) {
            this.validCharPredicates = Arrays.asList(validCharPredicates);
        }

        public boolean validChar(char ch) {
            return validCharPredicates.stream().map(p -> p.test(ch)).reduce(false, Boolean::logicalOr);
        }
    }
}
