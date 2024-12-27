package pgdp.searchengine.networking;

public class HTMLToken {

    private TokenType tokenType;

    public HTMLToken(TokenType tokenType) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    public TokenType getTokenType() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    public String getContentAsString() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    public void addCharacter(char c) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    public int getStatus() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    public enum TokenType {
        TAG, TEXT
    }
}
