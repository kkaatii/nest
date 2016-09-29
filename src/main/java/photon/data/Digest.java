package photon.data;

/**
 * Created by Dun Liu on 5/21/2016.
 */
public class Digest {
    static int MAX_WORD_COUNT = 32;

    public static String digest(String content) {
        if (content == null || content.length() < MAX_WORD_COUNT)
            return content;
        else {
            return content.substring(0, MAX_WORD_COUNT - 4) + "...";
        }
    }
}
