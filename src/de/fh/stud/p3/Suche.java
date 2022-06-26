package de.fh.stud.p3;

import de.fh.stud.p2.Knoten;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Suche {

    public static LinkedList<Knoten> openList = new LinkedList<>();
    public static HashSet<Integer> closedList = new HashSet<>();
    private final Knoten startZustand;

    /**
     * Konstruktor f�r die Klasse Suche.
     *
     * @param startZustand der Wurzel-Knoten f�r den Suchbaum
     */
    public Suche(Knoten startZustand) {
        this.startZustand = startZustand;
    }

    /**
     * Die Methode f�hrt die Suche nach dem L�sungs-Knoten durch und gibt diesen am Ende zur�ck.
     * <p>
     * Funktionsweise:
     * Solange die openList mindestens ein Element enth�lt, wird der erste Knoten der openList expandiert.
     * Wenn die Welt des zu expandierenden Knoten keine Dots mehr hat, so ist der L�sungs-Knoten gefunden.
     * Ist das nicht der Fall, wird �berpr�ft, ob der zu expandierende Knoten in der Closed List.
     * Wenn das nicht der Fall ist wird der Knoten expandiert (insert wird aufgerufen).
     * <p>
     * Ist der L�sungs-Knoten gefunden, werden die Actionen die zu diesem Knoten f�hren berechnet
     * und in dem L�sungsknoten gespeichert. Alles zusammen wird dann zur�ck gegeben.
     *
     * @param verfahren Eines der 5 Suchverfahren, welche in der enum Suchverfahren definiert sind.
     * @return ein Knoten, welcher den L�sungs-Knoten repres�ntiert.
     */
    public Knoten start(Suchverfahren verfahren) {
        openList.add(startZustand);

        while (!openList.isEmpty()) {
            Knoten expKand = openList.removeFirst();
            if (expKand.getAnzahlDots() > 0) {
                if (!closedList.contains(expKand.hashCode())) {
                    insert(expKand, verfahren);
                }
            } else {
                expKand.berechnePacmanActions();
                return expKand;
            }
        }
        return null;
    }

    /**
     * Die Methode f�hrt die expansion des �bergebenen Knoten anhand des gegebenen Suchverfahrens aus.
     * <p>
     * Funktionsweise:
     * Der Knoten wird expandiert, sprich die Methode expand() der Klasse Knoten wird aufgerufen.
     * Alle zur�ckgegebenen Knoten-Elemente werden in einer Liste gespeichert.
     * Der expandierte Knoten wird zur Closed List hinzugef�gt.
     * Die Liste der Kinder-Knoten wird gegeben des Suchverfahrens zur Open List hinzugef�gt.
     * <p>
     * Tiefensuche: Die Knoten werden vorne angef�gt.
     * Breitensuche: Die Knoten werden hinten angef�gt.
     * Greedy Suche, Uniform Cost Search, A*: Die Knoten werden eingef�gt und die Liste wird dann der bewertung nach aufsteigend Sortiert.
     *
     * @param expKand       ein Knoten, der expandiert werden soll.
     * @param suchverfahren Eines der 5 Suchverfahren, welche in der enum Suchverfahren definiert sind.
     */
    private void insert(Knoten expKand, Suchverfahren suchverfahren) {
        List<Knoten> expand = expKand.expand(suchverfahren);
        closedList.add(expKand.hashCode());
        switch (suchverfahren) {
            case TIEFENSUCHE:
                for (Knoten k : expand)
                    openList.addFirst(k);
                break;
            case BREITENSUCHE:
                openList.addAll(expand);
                break;
            case GREEDY_SUCHE:
            case UCS:
            case A_STERN:
                openList.addAll(expand);
                openList.sort(new KnotenVergleich());
                break;
        }
    }
}