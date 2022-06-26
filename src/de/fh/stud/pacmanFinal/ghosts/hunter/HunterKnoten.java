package de.fh.stud.pacmanFinal.ghosts.hunter;

import de.fh.pacman.enums.PacmanAction;
import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.p3.Suchverfahren;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class HunterKnoten {
    private final PacmanTileType[][] welt;
    private final int posX;
    private final int posY;
    private final HunterKnoten vorgaenger;
    private final Stack<PacmanAction> actions = new Stack<>();

    private final int kosten;
    private int bewertung;

    public HunterKnoten(PacmanTileType[][] view, int posX, int posY, HunterKnoten vorgaenger) {
        this.welt = view;
        this.posX = posX;
        this.posY = posY;
        this.vorgaenger = vorgaenger;
        if (vorgaenger != null)
            this.kosten = vorgaenger.kosten + 1;
        else
            this.kosten = 0;
    }

    public List<HunterKnoten> expand(Suchverfahren suchverfahren, int pacmanPosX, int pacmanPosY) {
        List<HunterKnoten> kinder = new LinkedList<>();

        // rechts / Osten
        if (getWelt()[getPosX() + 1][getPosY()] != PacmanTileType.WALL) {
            HunterKnoten temp = new HunterKnoten(weltKopieren(this.getWelt()), getPosX() + 1, getPosY(), this);
            temp.bewerten(suchverfahren, pacmanPosX, pacmanPosY);
            kinder.add(temp);
        }
        // links / Westen
        if (getWelt()[getPosX() - 1][getPosY()] != PacmanTileType.WALL) {
            HunterKnoten temp = new HunterKnoten(weltKopieren(this.getWelt()), getPosX() - 1, getPosY(), this);
            temp.bewerten(suchverfahren, pacmanPosX, pacmanPosY);
            kinder.add(temp);
        }
        // unten / S�den
        if (getWelt()[getPosX()][getPosY() + 1] != PacmanTileType.WALL) {
            HunterKnoten temp = new HunterKnoten(weltKopieren(this.getWelt()), getPosX(), getPosY() + 1, this);
            temp.bewerten(suchverfahren, pacmanPosX, pacmanPosY);
            kinder.add(temp);
        }
        // oben / Norden
        if (getWelt()[getPosX()][getPosY() - 1] != PacmanTileType.WALL) {
            HunterKnoten temp = new HunterKnoten(weltKopieren(this.getWelt()), getPosX(), getPosY() - 1, this);
            temp.bewerten(suchverfahren, pacmanPosX, pacmanPosY);
            kinder.add(temp);
        }

        return kinder;
    }

    /**
     * Diese Methode berechnet die Abfolge der Aktionen, welche zu dem Knoten f�hren, auf welchem diese Methode aufgerufen wurde.
     * <p>
     * Funktionsweise:
     * Es werden tempor�re Variablen f�r den aktuellen Knoten und den Vorg�nger Knoten erstellt.
     * Dazu erstellen wir eine tempor�re Liste f�r die Aktionen.
     * <p>
     * In einer Schleife, die l�uft solange der tempor�ren Vorg�nger-Knoten nicht null ist, werden r�ckwirkend die Aktionen berechnet.
     * Pro Schleifendurchlauf ermitteln wir auf Basis der Vorg�nger Koordinaten und der Koordinaten des tempAktuellen Knotens.
     * Am Ende jeden Schleifendurchlaufs wird der Vorg�nger-Knoten in der aktueller Variable gespeichert und der Vorg�nger-Knoten
     * wird der Vorg�nger des Vorg�ngers.
     * <p>
     * Am Ende der Methode wird die tempor�re Liste dann in der Liste actions gespeichert.
     */
    public void berechnePacmanActions() {
        HunterKnoten vorgaenger = this.vorgaenger;
        HunterKnoten aktueller = this;

        Stack<PacmanAction> list = new Stack<>();
        while (vorgaenger != null) {
            if (vorgaenger.posY < aktueller.posY) {
                list.push(PacmanAction.GO_SOUTH);
            } else if (vorgaenger.posX > aktueller.posX) {
                list.push(PacmanAction.GO_WEST);
            } else if (vorgaenger.posY > aktueller.posY) {
                list.push(PacmanAction.GO_NORTH);
            } else if (vorgaenger.posX < aktueller.posX) {
                list.push(PacmanAction.GO_EAST);
            }
            aktueller = vorgaenger;
            vorgaenger = vorgaenger.vorgaenger;
        }
        actions.addAll(list);
    }

    /**
     * Diese Methode wei�t der Variable bewertung einen Wert anhand des �bergebenen Sucheverfahrens.
     * <p>
     * Funktionsweise:
     * Es wird Mithilfe von switch-case f�r jedes Suchverfahren der Variable bewertung ein Wert zugewiesen.
     * Greedy-Suche: Es wird der R�ckgabewert der Methode heuristik() zugewiesen.
     * UCS: Es werden die Kosten des Knoten zugewiesen.
     * A*: Es wird die Summe aus den Kosten des Knoten und des R�ckgabewerts der Methode heuristik() zugewiesen.
     * default: bewertung wird 0 zugewiesen.
     *
     * @param s Eines der 5 Suchverfahren, welche in der enum Suchverfahren definiert sind.
     */
    public void bewerten(Suchverfahren s, int pacmanPosX, int pacmanPosY) {
        switch (s) {
            case GREEDY_SUCHE -> bewertung = heuristik(pacmanPosX, pacmanPosY);
            case UCS -> bewertung = kosten;
            case A_STERN -> bewertung = heuristik(pacmanPosX, pacmanPosY) + kosten;
            default -> bewertung = 0;
        }
    }

    /**
     * Eine Heuristik-Methode, welche die Kosten des Knoten sch�tzt.
     * <p>
     * Heuristik:
     * Anzahl Dots
     * <p>
     * Funktionsweise:
     * F�r die Heuristik nehmen wir einfach die Anzahl der verbleibenden Dots,
     * da reicht es die Variable dots zur�ck zu geben.
     *
     * @return Ein int Wert, welcher den gesch�tzten Kosten des Knoten entspricht.
     */
    private int heuristik(int pacmanPosX, int pacmanPosY) {
        int manhattenEntfernung = 0;
        manhattenEntfernung += Math.abs(posX - pacmanPosX);
        manhattenEntfernung += Math.abs(posY - pacmanPosY);
        return manhattenEntfernung;
    }

    /**
     * Diese Methode kopiert die gegebene "alte Welt" by value und gibt die Kopy am Ende zur�ck.
     * <p>
     * Funktionsweise:
     * Es wird ein zwei Dimensionales Array erstellt, mit der l�nge und breite des �bergebenen 2D Arrays.
     * Danach wird durch dieses Array iteriert und dem Feld der Wert zugewiesen,
     * welcher an der gleichen Position im alten Array steht.
     * Diese Funktion ist notwendig, damit wir die Welt nicht "by reference" �bergeben sondern "by value"
     *
     * @param weltAlt Ein 2D Array vom Typ PacmanTileTyp, welches die zu kopierende Welt enth�lt
     * @return Ein 2D Array vom Typ PacmanTileTyp, welches die kopierte Welt enth�lt
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

    /*
     * hashCode und equals funktion, von eclipse generiert
     */

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(getWelt());
        result = prime * result + getPosX();
        result = prime * result + getPosY();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HunterKnoten other = (HunterKnoten) obj;
        if (getPosX() != other.getPosX())
            return false;
        if (getPosY() != other.getPosY())
            return false;
        return Arrays.deepEquals(getWelt(), other.getWelt());
    }

    /*
     * Getter und Setter f�r die Attribute, wo ben�tigt
     */

    public Stack<PacmanAction> getLoesungsweg() {
        return actions;
    }

    public PacmanTileType[][] getWelt() {
        return welt;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getBewertung() {
        return bewertung;
    }
}