package pgdp.searchengine.pagerepository;

import pgdp.searchengine.util.WordCount;

import java.util.Arrays;
import java.util.HashMap;

public class LinkedDocumentCollection extends DocumentCollection {

    public LinkedDocumentCollection(int numberOfBuckets) {
        super(numberOfBuckets);
    }

    /**
     * Überschreibende Implementierung der add()-Funktion: Lässt nur
     * AbstractLinkedDocument-Objekte in die Collection und entscheidet den Bucket,
     * in den es einsortiert wird, anhand der URL anstelle der ID
     *
     * @param linkedDocument Document-Objekt, das in die Collection hinzufügt werden
     *                           soll
     * @return true gdw. tatsächlich etwas in die Collection eingefügt wurde
     */
    @Override
    public boolean add(Document linkedDocument) {
        if (linkedDocument == null) {
            return false;
        }

        if (linkedDocument instanceof AbstractLinkedDocument ald) {
            if (ald.getAddress() == null) {
                return false;
            }

            return this.getBuckets()[this.indexFunction(ald.getAddress())].add(ald);
        }

        return false;
    }

    // Die neue Index-Funktion, die die URL in den Bucket-Index verwandelt, statt
    // der ID.
    private int indexFunction(String address) {
        if (address == null) {
            return -1;
        }
        return (address.hashCode() % this.getNumberOfBuckets() + this.getNumberOfBuckets()) % this.getNumberOfBuckets();
    }

    /**
     * Sucht in der Collection nach einem AbstractLinkedDocument-Objekt mit der
     * übergebenen URL. Gibt 'null' zurück, wenn keines gefunden wird.
     *
     * @param address Die gesuchte URL
     * @return Das AbstractLinkedDocument-Objekt in der Collection mit der
     *         übergebenen URL
     */
    public AbstractLinkedDocument find(String address) {
        if (address == null) {
            return null;
        }

        Bucket bucket = this.getBuckets()[this.indexFunction(address)];

        if (bucket.getHead() == null) {
            return null;
        }

        DocumentListElement dle = bucket.getHead();

        while (dle != null) {
            if (dle.getDocument() instanceof AbstractLinkedDocument ald && ald.getAddress().equals(address)) {
                return ald;
            }
            dle = dle.getNext();
        }
        return null;
    }

    /**
     * Entfernt das übergebene DummyLinkedDocument-Objekt aus der Collection
     *
     * @param dld Das zu entfernende DummyLinkedDocument
     * @return true gdw. tatsächlich etwas entfernt wurde
     */
    public boolean removeDummy(DummyLinkedDocument dld) {
        if (dld == null || dld.getAddress() == null) {
            return false;
        }

        int bucketIndex = indexFunction(dld.getAddress());

        DocumentListElement listElementToRemove = this.getBuckets()[bucketIndex].find(dld.getDocumentId());
        return this.getBuckets()[bucketIndex].remove(listElementToRemove);
    }

    /**
     * Entfernt das AbstractLinked Document-Objekt mit der übergebenen URL aus der
     * Collection.
     *
     * @param address Die zu entfernende URL
     * @return true gdw. tatsächlich etwas entfernt wurde
     */
    public boolean removeDocument(String address) {
        if (address == null) {
            return false;
        }

        for (DocumentListElement cur = this.getBuckets()[this.indexFunction(address)].getHead(); cur != null; cur = cur.getNext()) {
            if (cur.getDocument() instanceof AbstractLinkedDocument ald && ald.getAddress().equals(address)) {
                return this.getBuckets()[this.indexFunction(address)].remove(cur);
            }
        }
        return false;
    }

    /**
     * Bestimmt, ob das übergebene AbstractLinkedDocument-Objekt (d.h. eines mit der
     * gleichen URL) bereits in der Collection vorhanden ist.
     *
     * @param ald Das gesuchte AbstractLinkedDocument-Objekt
     * @return true gdw. es gefunden wurde
     */
    public boolean contains(AbstractLinkedDocument ald) {
        if (ald == null) {
            return false;
        }
        return find(ald.getAddress()) != null;
    }

    /**
     * Das gleiche wie add(). Updatet zusätzlich noch alle incoming und outgoing
     * Links aller Objekte in der Collection entsprechend den im übergebenen
     * AbstractLinkedDocument vorhandenen Links.
     *
     * @param abstractLinkedDocument Das hinzuzufügende AbstractLinkedDocument
     * @return true gdw. etwas hinzugefügt wurde
     */
    public boolean addToResultCollection(AbstractLinkedDocument abstractLinkedDocument, String[] outgoingAddresses) {
        if (abstractLinkedDocument == null) {
            return false;
        }

        if (find(abstractLinkedDocument.getAddress()) instanceof LinkedDocument) {
            return false;
        }

        this.updateOutgoingLinks(abstractLinkedDocument);
        if (abstractLinkedDocument instanceof LinkedDocument) {
            this.updateIncomingLinks((LinkedDocument) abstractLinkedDocument, ((LinkedDocument) abstractLinkedDocument).getOutgoingAddresses());
        }
        return this.add(abstractLinkedDocument);
    }

    /**
     * Fügt allen Documents, auf die 'linkedDocument' einen Link hat (die URLs
     * dieser werden bereits separat in 'outgoingAddresses' übergeben),
     * 'linkedDocument' als incoming Link hinzu. Außerdem werden alle diese
     * Documents 'linkedDocument' als outgoing Links hinzugefügt. Wenn einer der
     * Links noch nicht in der Collection präsent ist, wird ein neues
     * DummyLinkedDocument erzeugt und in die Collection eingefügt.
     *
     * @param linkedDocument    Das neue LinkedDocument
     * @param outgoingAddresses Alle URLs, die in 'linkedDocument' als Links
     *                              vorhanden sind.
     */
    private void updateIncomingLinks(LinkedDocument linkedDocument, String[] outgoingAddresses) {
        if (linkedDocument == null || outgoingAddresses == null) {
            return;
        }

        Bucket[] buckets = this.getBuckets();
        boolean contains;

        for (int i = 0; i < outgoingAddresses.length; i++) {
            if (outgoingAddresses[i].equals(linkedDocument.getAddress())) {
                continue;
            }
            contains = false;
            for (int j = 0; j < buckets.length; j++) {
                DocumentListElement dle = buckets[j].getHead();
                while (dle != null) {
                    if (dle.getDocument() instanceof AbstractLinkedDocument ld && ld.getAddress().equals(outgoingAddresses[i])) {
                        linkedDocument.addOutgoingLink(ld);
                        ld.addIncomingLink(linkedDocument);
                        contains = true;
                        break;
                    }
                    dle = dle.getNext();
                }
                if (contains) {
                    break;
                }
            }
            if (!contains) {
                DummyLinkedDocument dummy = new DummyLinkedDocument(outgoingAddresses[i], this.getNumberOfBuckets());
                dummy.addIncomingLink(linkedDocument);
                linkedDocument.addOutgoingLink(dummy);
                this.add(dummy);
            }
        }
    }

    /**
     * Durchsucht alle LinkedDocuments in der Collection, ob sie einen outgoing Link
     * auf die URL des übergebenen AbstractLinkedDocument enthalten. Wenn ja - d.h.
     * es ist bereits ein DummyLinkedDocument mit der URL von
     * 'abstractLinkedDocument' vorhanden - soll der outgoing Link von diesem
     * DummyLinkedDocument auf 'abstractLinkedDocument' geändert werden. Am Ende
     * soll das DummyLinkedDocument mit der gleichen URL wie
     * 'abstractLinkedDocument' auch aus der Collection entfernt werden. Außerdem
     * soll jedes LinkedDocument, das einen outgoing Link auf
     * 'abstractLinkedDocument' hat, auch in dessen incoming Link Collection
     * aufgenommen werden.
     *
     * @param abstractLinkedDocument Das neue abstractLinkedDocument
     */
    private void updateOutgoingLinks(AbstractLinkedDocument abstractLinkedDocument) {
        if (abstractLinkedDocument == null) {
            return;
        }

        if (this.isEmpty()) {
            return;
        }

        LinkedDocumentCollection incomingLinksDummy;

        AbstractLinkedDocument ald = this.find(abstractLinkedDocument.getAddress());

        if (ald != null && ald.getAddress().equals(abstractLinkedDocument.getAddress())) {
            incomingLinksDummy = ald.getIncomingLinks();
            Bucket[] bucketsIncoming = incomingLinksDummy.getBuckets();
            for (int j = 0; j < bucketsIncoming.length; j++) {
                DocumentListElement dleIncoming = bucketsIncoming[j].getHead();
                while (dleIncoming != null) {
                    abstractLinkedDocument.addIncomingLink((LinkedDocument) dleIncoming.getDocument());
                    ((LinkedDocument) dleIncoming.getDocument()).addOutgoingLink(abstractLinkedDocument);
                    dleIncoming = dleIncoming.getNext();
                }
            }
            this.removeDummy((DummyLinkedDocument) ald);
        }
    }

    /**
     * Erzeugt ein Array mit Referenzen auf alle DocumentListElement-Objekte in der
     * Collection.
     *
     * @return Das Array mit den Elementen der Collection
     */
    public DocumentListElement[] toArray() {
        DocumentListElement[] allListElements = new DocumentListElement[this.size()];

        Bucket[] buckets = this.getBuckets();
        int pos = 0;

        for (int j = 0; j < buckets.length; j++) {
            DocumentListElement dle = buckets[j].getHead();
            while (dle != null) {
                allListElements[pos++] = dle;
                dle = dle.getNext();
            }
        }

        return allListElements;
    }

    /**
     * Bestimmt den PageRank für alle Dokumente in der Collection und gibt sie als
     * Array zurück, wobei der erste Wert im Array der PageRank des Dokuments mit
     * der kleinsten ID in der Collection ist, der zweite der PageRank des Dokuments
     * mit der zweitkleinsten ID in der Collection usw.
     *
     * @param d        Dämpfungsfaktor
     * @param recDepth Rekursionstiefe
     * @return Array mit allen PageRanks nach Document-ID sortiert
     */
    public double[] pageRank(double d, int recDepth) {
        DocumentListElement[] indices = this.toArray();

        for(int i = 0; i < indices.length; i++){
            indices[i].setPageRank(pageRankRec(indices,i,d,recDepth));
        }

        HashMap<Integer,Double> idToElement = new HashMap<>();
        int[] ids = new int[indices.length];

        for(int i = 0; i < indices.length; i++){
            idToElement.put(indices[i].getDocumentId(), indices[i].getPageRank());
            ids[i] = indices[i].getDocumentId();
        }

        Arrays.sort(ids);
        double[] pageRankIdSort = new double[ids.length];

        for(int i = 0; i < ids.length; i++){
            pageRankIdSort[i] = idToElement.get(ids[i]);
        }

        return pageRankIdSort;
    }

    /**
     * Bestimmt den PageRank-Wert für das DocumentListElement in 'indices' an Stelle
     * 'i'.
     *
     * @param indices  Ordnung aller DocumentListElements in der Collection
     * @param i        Der Index des Elements in 'indices', für das der PageRank
     *                     berechnet werden soll
     * @param d        Dämpfungsfaktor
     * @param recDepth Rekursionstiefe
     * @return Der berechnete PageRank-Wert
     */
    public double pageRankRec(DocumentListElement[] indices, int i, double d, int recDepth) {
        if(recDepth <= 0){
            return 1.0/indices.length;
        }

        AbstractLinkedDocument document = (AbstractLinkedDocument) indices[i].getDocument();

        DocumentCollection incomingLinks = document.getIncomingLinks();
        double temp = 0;

        for(int j = 0; j < indices.length; j++){
            if(i == j){
                continue;
            }

            AbstractLinkedDocument documentJ = (AbstractLinkedDocument) indices[j].getDocument();

            if(incomingLinks.contains(documentJ)){
                temp += (pageRankRec(indices,j,d,recDepth-1))/((LinkedDocument) documentJ).getOutgoingLinks().size();
            }else if(documentJ instanceof DummyLinkedDocument || (documentJ instanceof LinkedDocument && ((LinkedDocument) documentJ).getOutgoingLinks().size() == 0)){
                temp += pageRankRec(indices,j,d,recDepth-1)/(indices.length-1);
            }
        }

        double pageRank;
        pageRank = (1-d)/indices.length;
        pageRank += d*temp;

        System.out.println(document.getTitle() + ", Pagerank: " + pageRank + ", Depth :" + recDepth);

        return pageRank;
    }

    /**
     * Berechnet die Ähnlichkeit des Query-Strings zu jedem Dokument in der
     * Collection und schreibt diesen jeweils in das 'similarity'-Attribut des
     * zugehörigen DocumentListElement-Objekts. Außerdem wird der PageRank für alle
     * Dokumente berechnet. Für diesen wird der übergebene 'dampingFactor'
     * verwendet. Aus diesem und der 'similarity' wird für jedes DocumentListElement
     * die 'relevance' als weightingFactor * similarity + (1.0 - weightingFactor) *
     * pageRank berechnet. Zurückgegeben wird letztlich ein Array aller Dokumente
     * absteigend nach 'relevance' (und bei gleicher 'relevance' aufsteigend nach
     * ID) sortiert
     *
     * @param query           Der gesuchte String
     * @param dampingFactor   Damping-Factor für PageRank
     * @param weightingFactor Gewichtung von Ähnlichkeit gegenüber PageRank
     * @param recDepth        Maximale Rekursionstiefe für PageRank
     * @return Array aller Dokumente nach 'relevance' sortiert
     */
    public AbstractLinkedDocument[] query(String query, double dampingFactor, double weightingFactor, int recDepth) {
        if (query == null) {
            return new AbstractLinkedDocument[] {};
        }

        LinkedDocument queryDoc = new LinkedDocument("", "", query, null, null, "query", this.getNumberOfBuckets());
        String queryAddr = queryDoc.getAddress();
        this.add(queryDoc);
        this.equalizeAllWordCountArrays();
        WordCount[] queryWordCount = Document.equalizeWordCountArray(queryDoc.getWordCountArray(), getCompleteWordCountArray());
        Document.sort(queryWordCount);
        this.calculateWeights(queryWordCount);

        DocumentListElement[] allDocuments = this.toArray();

        for (DocumentListElement dle : allDocuments) {
            this.calculateWeights(dle.getWordCountArray());
            dle.setSimilarity(Document.complexSimilarity(queryWordCount, dle.getWordCountArray()));
        }

        this.removeDocument(queryAddr);

        allDocuments = this.toArray();
        this.pageRank(dampingFactor, recDepth);

        for (DocumentListElement dle : allDocuments) {
            dle.setRelevance(weightingFactor * dle.getSimilarity() + (1.0 - weightingFactor) * dle.getPageRank());
        }

        return this.relevanceRanking();
    }

    /**
     * Gibt ein Array aller Dokumente in der Collection zurück. Diese sind
     * absteigend nach 'relevance' sortiert.
     *
     * @return Das sortierte Array aller Dokumente
     */
    public AbstractLinkedDocument[] relevanceRanking() {
        this.sortBucketRelevance();
        AbstractLinkedDocument[] ranked = new AbstractLinkedDocument[this.size()];
        DocumentListElement[] allHeads = new DocumentListElement[this.getBuckets().length];
        double highestSimilarity;
        int highestSimIndex;

        for (int i = 0; i < allHeads.length; i++) {
            allHeads[i] = this.getBuckets()[i].getHead();
        }

        for (int i = 0; i < ranked.length; i++) {
            highestSimIndex = -1;
            highestSimilarity = 0;

            for (int j = 0; j < allHeads.length; j++) {
                if (highestSimIndex == -1 && allHeads[j] != null) {
                    highestSimIndex = j;
                    highestSimilarity = allHeads[j].getRelevance();
                }
                if (allHeads[j] != null && allHeads[j].getRelevance() > highestSimilarity) {
                    highestSimIndex = j;
                    highestSimilarity = allHeads[j].getRelevance();
                }
                if (allHeads[j] != null && allHeads[j].getRelevance() == highestSimilarity && allHeads[j].getDocumentId() < allHeads[highestSimIndex].getDocumentId()) {
                    highestSimIndex = j;
                    highestSimilarity = allHeads[j].getRelevance();
                }
            }

            if (allHeads[highestSimIndex].getDocument() instanceof AbstractLinkedDocument ald) {
                ranked[i] = ald;
            }

            allHeads[highestSimIndex] = allHeads[highestSimIndex].getNext();
        }
        return ranked;
    }

    /**
     * Sortiert alle Buckets der Collections absteigend nach 'relevance' (und bei
     * gleicher 'relevance' aufsteigend nach ID)
     */
    public void sortBucketRelevance() {
        for (int i = 0; i < this.getBuckets().length; i++) {
            if (this.getBuckets()[i].getHead() != null) {
                DocumentListElement currentListElement;
                boolean sorted = false;
                while (!sorted) {
                    currentListElement = this.getBuckets()[i].getHead();
                    sorted = true;
                    while (currentListElement.getNext() != null) {
                        if (currentListElement.getRelevance() < currentListElement.getNext().getRelevance()) {
                            this.getBuckets()[i].swapListElements(currentListElement, currentListElement.getNext());
                            sorted = false;
                        }
                        if (currentListElement.getRelevance() == currentListElement.getNext().getRelevance()
                                && currentListElement.getDocumentId() > currentListElement.getNext().getDocumentId()) {
                            this.getBuckets()[i].swapListElements(currentListElement, currentListElement.getNext());
                            sorted = false;
                        }
                        currentListElement = currentListElement.getNext();
                    }
                }
            }
        }
    }

    public String getNextUncrawledAddress() {
        throw new UnsupportedOperationException("Not implemented!");
    }
}
