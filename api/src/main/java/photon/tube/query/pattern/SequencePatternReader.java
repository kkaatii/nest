package photon.tube.query.pattern;

import java.io.PushbackReader;

public class SequencePatternReader {

    static TokenParser[] parsers = new TokenParser[128];



    class IntArrayTokenParser implements TokenParser<int[]> {
        @Override
        public int[] parse(PushbackReader reader) {
            return new int[0];
        }
    }


}
