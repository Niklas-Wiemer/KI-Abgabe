package de.fh.stud.p2;

import de.fh.pacman.PacmanPercept;
import de.fh.pacman.enums.PacmanAction;
import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.p3.Suchverfahren;
import de.fh.stud.pacmanFinal.Direction;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Knoten {

    private final PacmanTileType[][] welt;
    private final int xCoord;
    private final int yCoord;
    private final Knoten vorgaenger;
    private int dots;
    private final Stack<PacmanAction> actions = new Stack<>();
    private final int kosten;
    private int bewertung;

    /**
     * Konstruktor für die Klasse Knoten mit einem PacmanPercept und einem Vorgänger Knoten.
     * <p>
     * Die Anzahl der Dots werden vom Vorgänger übernommen, für die Kosten werden die Kosten des Vorgängers iteriert.
     * Ist der Vorgänger-Knoten null, so wird die Anzahl mit der Methode countDots() der Klasse Knoten berechnet und die Kosten auf 0 gesetzt.
     * Am ende wird nochmal überprüft ob auf der aktuellen Position des Pacman ein Dots ist.
     * Ist das der Fall, so wird die Anzahl der Dots decrementiert und der Dot auf der Karte entfernt.
     *
     * @param percept    PacmanPercept Objekt, das den Weltzustand der PacmanWelt wiedergibt.
     * @param vorgaenger Ein Vorgänger-Knoten, wenn es keinen gibt, einfach null angeben.
     */
    public Knoten(PacmanPercept percept, Knoten vorgaenger) {
        this.welt = percept.getView();
        this.xCoord = percept.getPosX();
        this.yCoord = percept.getPosY();
        this.vorgaenger = vorgaenger;
        if (vorgaenger != null) {
            this.dots = vorgaenger.dots;
            this.kosten = vorgaenger.kosten + 1;
        } else {
            dots = countDots();
            this.kosten = 0;
        }
        if (getWelt()[getxCoord()][getyCoord()] == PacmanTileType.DOT) {
            getWelt()[getxCoord()][getyCoord()] = PacmanTileType.EMPTY;
            dots--;
        }
    }

    /**
     * Konstruktor für die Klasse Knoten. Übergeben werden die Welt als 2D Array des Typs PacmanTileTyp,
     * Die Position des Pacmans in der Welt mit x und y Koordinaten als int werte, sowie der vorgänger Knoten
     * und eine Collection mit den actions.
     * <p>
     * Die Anzahl der Dots werden vom Vorgänger übernommen, für die Kosten werden die Kosten des Vorgängers iteriert.
     * Ist der Vorgänger-Knoten null, so wird die Anzahl mit der Methode countDots() der Klasse Knoten berechnet und die Kosten auf 0 gesetzt.
     * Am ende wird nochmal überprüft ob auf der aktuellen Position des Pacman ein Dots ist.
     * Ist das der Fall, so wird die Anzahl der Dots decrementiert und der Dot auf der Karte entfernt.
     *
     * @param view
     * @param xCoord
     * @param yCoord
     * @param vorgaenger
     * @param actions
     */
    public Knoten(PacmanTileType[][] view, int xCoord, int yCoord, Knoten vorgaenger) {
        this.welt = view;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.vorgaenger = vorgaenger;
        if (vorgaenger != null) {
            this.dots = vorgaenger.dots;
            this.kosten = vorgaenger.kosten + 1;
        } else {
            dots = countDots();
            this.kosten = 0;
        }
        if (getWelt()[getxCoord()][getyCoord()] == PacmanTileType.DOT) {
            getWelt()[getxCoord()][getyCoord()] = PacmanTileType.EMPTY;
            dots--;
        }
    }

    public static Knoten movePacman(Knoten k, Direction d) {
        return switch (d) {
            case NORTH -> new Knoten(k.getWelt(), k.getxCoord(), k.getyCoord() - 1, k);
            case EAST -> new Knoten(k.getWelt(), k.getxCoord() + 1, k.getyCoord(), k);
            case SOUTH -> new Knoten(k.getWelt(), k.getxCoord(), k.getyCoord() + 1, k);
            case WEST -> new Knoten(k.getWelt(), k.getxCoord() - 1, k.getyCoord(), k);
            default -> null;
        };
    }

    /**
     * Die Methode expandiert den Knoten, auf welchem diese Methode aufgerufen wird.
     * <p>
     * Funktionsweise:
     * Es wird eine temporäre Liste für die Kinder erstellt.
     * Nacheinander wird, jeweils für rechts, links, unter und über dem Pacman eine Mauer in der Welt ist.
     * Ist das bei einer der vier Richtungen nicht der Fall, so wird für diese Richtung ein Kind Knoten erstellt,
     * in welchem der Pacman auch in die Richtung bewegt wird.
     * Dieser Knoten wird dann direkt bewertet, indem auf dem Knoten die bewerten() Methode der Klasse Knoten aufgerufen wird.
     * Dieser wird auch das Suchverfahren übergeben. Dann wird der Kind-Knoten in die temporäre Liste aufgenommen.
     * Wurden alle vier Richtungen überprüft und eventuelle Kind-Knoten erzeugt, so wird die temporäre Liste zurückgegeben
     *
     * @param suchverfahren Eines der 5 definierten Suchverfahren definiert in dem Enum Suchverfahren.
     * @return eine Liste von Knoten, welche Kindknoten des expandierten Knotens sind.
     */
    public List<Knoten> expand(Suchverfahren suchverfahren) {
        List<Knoten> kinder = new LinkedList<>();

        // rechts / Osten
        if (getWelt()[getxCoord() + 1][getyCoord()] != PacmanTileType.WALL) {
            Knoten temp = new Knoten(weltKopieren(this.getWelt()), getxCoord() + 1, getyCoord(), this);
            temp.bewerten(suchverfahren);
            kinder.add(temp);
        }
        // links / Westen
        if (getWelt()[getxCoord() - 1][getyCoord()] != PacmanTileType.WALL) {
            Knoten temp = new Knoten(weltKopieren(this.getWelt()), getxCoord() - 1, getyCoord(), this);
            temp.bewerten(suchverfahren);
            kinder.add(temp);
        }
        // unten / Süden
        if (getWelt()[getxCoord()][getyCoord() + 1] != PacmanTileType.WALL) {
            Knoten temp = new Knoten(weltKopieren(this.getWelt()), getxCoord(), getyCoord() + 1, this);
            temp.bewerten(suchverfahren);
            kinder.add(temp);
        }
        // oben / Norden
        if (getWelt()[getxCoord()][getyCoord() - 1] != PacmanTileType.WALL) {
            Knoten temp = new Knoten(weltKopieren(this.getWelt()), getxCoord(), getyCoord() - 1, this);
            temp.bewerten(suchverfahren);
            kinder.add(temp);
        }

        return kinder;
    }

    /**
     * Diese Methode berechnet die Abfolge der Aktionen, welche zu dem Knoten führen, auf welchem diese Methode aufgerufen wurde.
     * <p>
     * Funktionsweise:
     * Es werden temporäre Variablen für den aktuellen Knoten und den Vorgänger Knoten erstellt.
     * Dazu erstellen wir eine temporäre Liste für die Aktionen.
     * <p>
     * In einer Schleife, die läuft solange der temporären Vorgänger-Knoten nicht null ist, werden rückwirkend die Aktionen berechnet.
     * Pro Schleifendurchlauf ermitteln wir auf Basis der Vorgänger Koordinaten und der Koordinaten des tempAktuellen Knotens.
     * Am Ende jeden Schleifendurchlaufs wird der Vorgänger-Knoten in der aktueller Variable gespeichert und der Vorgänger-Knoten
     * wird der Vorgänger des Vorgängers.
     * <p>
     * Am Ende der Methode wird die temporäre Liste dann in der Liste actions gespeichert.
     */
    public void berechnePacmanActions() {
        Knoten vorgaenger = this.vorgaenger;
        Knoten aktueller = this;

        Stack<PacmanAction> list = new Stack<>();
        while (vorgaenger != null) {
            if (vorgaenger.yCoord < aktueller.yCoord) {
                list.push(PacmanAction.GO_SOUTH);
            } else if (vorgaenger.xCoord > aktueller.xCoord) {
                list.push(PacmanAction.GO_WEST);
            } else if (vorgaenger.yCoord > aktueller.yCoord) {
                list.push(PacmanAction.GO_NORTH);
            } else if (vorgaenger.xCoord < aktueller.xCoord) {
                list.push(PacmanAction.GO_EAST);
            }
            aktueller = vorgaenger;
            vorgaenger = vorgaenger.vorgaenger;
        }
        actions.addAll(list);
    }

    /**
     * Die Methode zählt alle Dots in der welt des Knoten, für den die Methode aufgerufen wurde.
     * <p>
     * Funktionsweise:
     * Die Methode iteriert das ganze 2D Array und überprüft jedes Feld, ob ein Dot vorhanden ist.
     * Ist ein Dot vorhanden wird ein counter incrementiert.
     * Am ende wird der Counter zurück gegeben.
     *
     * @return ein in Wert, welcher der Anzahl der Dots entspricht
     */
    private int countDots() {
        int dots = 0;
        for (int x = 0; x < getWelt().length; x++) {
            for (int y = 0; y < getWelt()[x].length; y++) {
                if (getWelt()[x][y] == PacmanTileType.DOT ||
                        getWelt()[x][y] == PacmanTileType.GHOST_AND_DOT) {
                    dots++;
                }
            }
        }
        return dots;
    }

    /**
     * Diese Methode kopiert die gegebene "alte Welt" by value und gibt die Kopy am Ende zurück.
     * <p>
     * Funktionsweise:
     * Es wird ein zwei Dimensionales Array erstellt, mit der länge und breite des übergebenen 2D Arrays.
     * Danach wird durch dieses Array iteriert und dem Feld der Wert zugewiesen,
     * welcher an der gleichen Position im alten Array steht.
     * Diese Funktion ist notwendig, damit wir die Welt nicht "by reference" übergeben sondern "by value"
     *
     * @param weltAlt Ein 2D Array vom Typ PacmanTileTyp, welches die zu kopierende Welt enthält
     * @return Ein 2D Array vom Typ PacmanTileTyp, welches die kopierte Welt enthält
     */
    private static PacmanTileType[][] weltKopieren(PacmanTileType[][] weltAlt) {
        int breite = weltAlt.length;
        int hoehe = weltAlt[breite - 1].length;
        PacmanTileType[][] weltNeu = new PacmanTileType[breite][hoehe];
        for (int x = 0; x < breite; x++) {
            for (int y = 0; y < hoehe; y++) {
                weltNeu[x][y] = weltAlt[x][y];
            }
        }
        return weltNeu;
    }

    /**
     * Diese Methode weißt der Variable bewertung einen Wert anhand des übergebenen Sucheverfahrens.
     * <p>
     * Funktionsweise:
     * Es wird Mithilfe von switch-case für jedes Suchverfahren der Variable bewertung ein Wert zugewiesen.
     * Greedy-Suche: Es wird der Rückgabewert der Methode heuristik() zugewiesen.
     * UCS: Es werden die Kosten des Knoten zugewiesen.
     * A*: Es wird die Summe aus den Kosten des Knoten und des Rückgabewerts der Methode heuristik() zugewiesen.
     * default: bewertung wird 0 zugewiesen.
     *
     * @param s Eines der 5 Suchverfahren, welche in der enum Suchverfahren definiert sind.
     */
    public void bewerten(Suchverfahren s) {
        switch (s) {
            case GREEDY_SUCHE -> bewertung = heuristik();
            case UCS -> bewertung = kosten;
            case A_STERN -> bewertung = heuristik() + kosten;
            default -> bewertung = 0;
        }
    }

    /**
     * Eine Heuristik-Methode, welche die Kosten des Knoten schätzt.
     * <p>
     * Heuristik:
     * Anzahl Dots
     * <p>
     * Funktionsweise:
     * Für die Heuristik nehmen wir einfach die Anzahl der verbleibenden Dots,
     * da reicht es die Variable dots zurück zu geben.
     *
     * @return Ein int Wert, welcher den geschätzten Kosten des Knoten entspricht.
     */
    private int heuristik() {
        int manhattenEntfernung = 0;
        for (int x = 0; x < welt.length; x++) {
            for (int y = 0; y < welt[x].length; y++) {
                if (welt[x][y].equals(PacmanTileType.DOT)) {
                    int temp = 0;
                    if (xCoord > x) {
                        temp += xCoord - x;
                    } else if (x > xCoord) {
                        temp += x - xCoord;
                    } else {
                        temp += 0;
                    }
                    if (yCoord > y) {
                        temp += yCoord - y;
                    } else if (y > yCoord) {
                        temp += y - yCoord;
                    } else {
                        temp += 0;
                    }
                    manhattenEntfernung += temp;
                }
            }
        }
        if (dots > 0) {
            return manhattenEntfernung / dots;
        } else {
            return manhattenEntfernung;
        }
        //return dots;
    }

    /*
     * hashCode und equals funktion, von eclipse generiert
     */

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + dots;
        result = prime * result + Arrays.deepHashCode(getWelt());
        result = prime * result + getxCoord();
        result = prime * result + getyCoord();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Knoten other = (Knoten) obj;
        if (dots != other.dots)
            return false;
        if (getxCoord() != other.getxCoord())
            return false;
        if (getyCoord() != other.getyCoord())
            return false;
        return Arrays.deepEquals(getWelt(), other.getWelt());
    }

    /*
     * Getter und Setter für die Attribute, wo benötigt
     */
    public int getAnzahlDots() {
        return dots;
    }

    public Stack<PacmanAction> getLoesungsweg() {
        return actions;
    }

    public PacmanTileType[][] getWelt() {
        return welt;
    }

    public int getxCoord() {
        return xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public int getBewertung() {
        return bewertung;
    }
}