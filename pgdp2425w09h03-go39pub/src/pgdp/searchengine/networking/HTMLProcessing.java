package pgdp.searchengine.networking;

import java.util.*;

public final class HTMLProcessing {

    // Useless constructor of uselessness
    private HTMLProcessing() {

    }

    public static List<HTMLToken> tokenize(String rawHTML) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    private enum TokenizingState {
        INITIAL, TAG, TEXT, SINGLE_QUOTE_STRING, DOUBLE_QUOTE_STRING
    }

    public static String[] filterLinks(List<HTMLToken> tokens, String host) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    public static String filterText(List<HTMLToken> tokens) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    public static String filterTitle(List<HTMLToken> tokens) {
        throw new UnsupportedOperationException("Not implemented!");
    }

}
