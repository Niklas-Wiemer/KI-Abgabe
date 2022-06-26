package pacmanFinal.testing;

import de.fh.pacman.PacmanPercept;
import de.fh.stud.p3.Suchverfahren;
import de.fh.stud.pacmanFinal.ghosts.hunter.HunterKnoten;
import de.fh.stud.pacmanFinal.ghosts.hunter.HunterKnotenVergleich;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class HunterSuche {

    public static LinkedList<HunterKnoten> openList = new LinkedList<>();
    public static HashSet<Integer> closedList = new HashSet<>();
    private final PacmanPercept percept;
    private final HunterKnoten startZustand;

    /**
     * Konstruktor f�r die Klasse Suche.
     *
     * @param startZustand der Wurzel-HunterKnoten f�r den Suchbaum
     */
    public HunterSuche(PacmanPercept percept, HunterKnoten startZustand) {
        this.percept = percept;
        this.startZustand = startZustand;
    }

    /**
     * Die Methode f�hrt die Suche nach dem L�sungs-HunterKnoten durch und gibt diesen am Ende zur�ck.
     * <p>
     * Funktionsweise:
     * Solange die openList mindestens ein Element enth�lt, wird der erste HunterKnoten der openList expandiert.
     * Wenn die Welt des zu expandierenden HunterKnoten keine Dots mehr hat, so ist der L�sungs-HunterKnoten gefunden.
     * Ist das nicht der Fall, wird �berpr�ft, ob der zu expandierende HunterKnoten in der Closed List.
     * Wenn das nicht der Fall ist wird der HunterKnoten expandiert (insert wird aufgerufen).
     * <p>
     * Ist der L�sungs-HunterKnoten gefunden, werden die Actionen die zu diesem HunterKnoten f�hren berechnet
     * und in dem L�sungsknoten gespeichert. Alles zusammen wird dann zur�ck gegeben.
     *
     * @return ein HunterKnoten, welcher den L�sungs-HunterKnoten repres�ntiert.
     */
    public HunterKnoten start(Suchverfahren suchverfahren, int pacmanPosX, int pacmanPosY) {
        openList.add(startZustand);

        while (!openList.isEmpty()) {
            HunterKnoten expKand = openList.removeFirst();
            if (percept.getPosX() != expKand.getPosX() ||
                    percept.getPosY() != expKand.getPosY()) {
                if (!closedList.contains(expKand.hashCode())) {
                    insert(expKand, suchverfahren, pacmanPosX, pacmanPosY);
                }

            } else {
                expKand.berechnePacmanActions();
                return expKand;
            }
        }
        return null;
    }

    /**
     * Die Methode f�hrt die expansion des �bergebenen HunterKnoten anhand des gegebenen Suchverfahrens aus.
     * <p>
     * Funktionsweise:
     * Der HunterKnoten wird expandiert, sprich die Methode expand() der Klasse HunterKnoten wird aufgerufen.
     * Alle zur�ckgegebenen HunterKnoten-Elemente werden in einer Liste gespeichert.
     * Der expandierte HunterKnoten wird zur Closed List hinzugef�gt.
     * Die Liste der Kinder-HunterKnoten wird gegeben des Suchverfahrens zur Open List hinzugef�gt.
     * <p>
     * Tiefensuche: Die HunterKnoten werden vorne angef�gt.
     * Breitensuche: Die HunterKnoten werden hinten angef�gt.
     * Greedy Suche, Uniform Cost Search, A*: Die HunterKnoten werden eingef�gt und die Liste wird dann der bewertung nach aufsteigend Sortiert.
     *
     * @param expKand ein HunterKnoten, der expandiert werden soll.
     */
    private void insert(HunterKnoten expKand, Suchverfahren suchverfahren, int pacmanPosX, int pacmanPosY) {
        List<HunterKnoten> expand = expKand.expand(suchverfahren, pacmanPosX, pacmanPosY);
        closedList.add(expKand.hashCode());
        switch (suchverfahren) {
            case TIEFENSUCHE:
                for (HunterKnoten k : expand)
                    openList.addFirst(k);
                break;
            case BREITENSUCHE:
                openList.addAll(expand);
                break;
            case GREEDY_SUCHE:
            case UCS:
            case A_STERN:
                openList.addAll(expand);
                openList.sort(new HunterKnotenVergleich());
                break;
        }
    }
}