package de.fh.stud.p3;

import de.fh.stud.p2.Knoten;

import java.util.Comparator;

public class KnotenVergleich implements Comparator<Knoten> {

    /**
     * Methode die zwei gegebene Knoten anhand der bewertung vergleicht.
     * Ist k1 > k2 wird 1 zur�ckgegeben
     * Ist k1 < k2 wird -1 zur�ckgegeben
     * Ist k1 = k2 wird 0 zur�ckgegeben
     *
     * @param k1 Der erste zu vergleichenden Knoten
     * @param k2 Der zweite zu vergleichende Knoten
     * @return int Wert, welcher den vergleich der Knoten repr�sentiert.
     */
    @Override
    public int compare(Knoten k1, Knoten k2) {
        return Integer.compare(k1.getBewertung(), k2.getBewertung());
    }

}
