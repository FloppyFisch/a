package pgdp.searchengine.testing;

import pgdp.searchengine.pagerepository.*;

import java.util.Arrays;

public class SearchEngineTesting {



    public static void main(String[] args) {
        LinkedDocument a = new LinkedDocument("a","","",null,null,"a",4);
        LinkedDocument b = new LinkedDocument("b","","",null,null,"b",4);
        LinkedDocument c = new LinkedDocument("c","","",null,null,"c",4);
        LinkedDocument d = new LinkedDocument("d","","",null,null,"d",4);

        LinkedDocumentCollection collection = new LinkedDocumentCollection(3);

        collection.add(a);
        collection.add(b);
        collection.add(c);
        collection.add(d);

        a.addOutgoingLink(c);
        a.addIncomingLink(b);
        a.addOutgoingLink(b);

        b.addOutgoingLink(a);
        b.addOutgoingLink(c);
        b.addOutgoingLink(d);
        b.addIncomingLink(a);

        c.addOutgoingLink(d);
        c.addIncomingLink(a);
        c.addIncomingLink(b);
        c.addIncomingLink(d);

        d.addIncomingLink(b);
        d.addIncomingLink(c);
        d.addOutgoingLink(c);

        System.out.println(Arrays.toString(collection.pageRank(0.85, 1)));
    }
}
