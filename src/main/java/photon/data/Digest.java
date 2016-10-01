package photon.data;

/**
 * Created by Dun Liu on 5/21/2016.
 */
public class Digest {

    public static String digest(Node node) {

        int MAX_WORD_COUNT = 32;

        String content = node.getContent();
        if (content == null || content.length() < MAX_WORD_COUNT){
            node.setContent(content);
            return content;}
        else {
            content = content.substring(0, MAX_WORD_COUNT - 4) + "...";
            node.setContent(content);
            return content;
        }
    }
}
