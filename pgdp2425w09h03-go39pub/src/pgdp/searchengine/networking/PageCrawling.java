package pgdp.searchengine.networking;

import pgdp.searchengine.pagerepository.LinkedDocumentCollection;

public final class PageCrawling {

    // Useless constructor of uselessness
    private PageCrawling() {

    }

    public static void crawlPages(LinkedDocumentCollection collection, int number) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    public static void crawlPages(LinkedDocumentCollection collection, int number, String startingAddress) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    public static boolean crawlPage(LinkedDocumentCollection collection, String address) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    // -------- main() zum Testen -------- //

    public static void main(String... args) {
        HTTPRequest request = new HTTPRequest("man1.pgdp.sse.in.tum.de", "");
        HTTPResponse response = request.send(443);
        int x = 0;
    }

    public static void test1() {
        String host = "man7.pgdp.sse.in.tum.de";
        String path = "iso_8859_1.7.html";

        LinkedDocumentCollection ldc = new LinkedDocumentCollection(1000);
        crawlPages(ldc, 5, host + "/" + path);
        int x = 0;
    }

    public static void test2() {
        String host = "pgdp.sse.in.tum.de";
        String path = "index.html";

        LinkedDocumentCollection ldc = new LinkedDocumentCollection(10);
        crawlPages(ldc, 1, host + "/" + path);
        int x = 0;
    }

}
