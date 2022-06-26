package de.fh.stud.pacmanFinal;

import de.fh.kiServer.agents.Agent;
import de.fh.kiServer.util.Util;
import de.fh.pacman.*;
import de.fh.pacman.enums.PacmanAction;
import de.fh.pacman.enums.PacmanActionEffect;
import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.p2.Knoten;
import de.fh.stud.p3.Suche;
import de.fh.stud.pacmanFinal.ghosts.EagerGhost;
import de.fh.stud.pacmanFinal.ghosts.Ghost;
import de.fh.stud.pacmanFinal.ghosts.HunterGhost;
import de.fh.stud.pacmanFinal.ghosts.RandomGhost;

import java.util.LinkedList;
import java.util.List;

public class MyAgent_Final extends PacmanAgent {

    private final List<de.fh.stud.pacmanFinal.ghosts.Ghost> ghosts = new LinkedList<>();

    private Knoten startKnoten;
    private int anzahlDotsGesamt = 0;
    Direction nextStep;

    /**
     * Konstruktor für die Klasse MyAgent_P3, mit namen für den Agenten
     *
     * @param name Der Name des Agenten, der angezeigt werden soll
     */
    public MyAgent_Final(String name) {
        super(name);
    }

    /**
     * main Funktion die beim Start des Programms ausgeführt wird.
     *
     * @param args Ein Array vom Typ String mit den Argumenten, welche beim Programmstart übergeben werden.
     */
    public static void main(String[] args) {
        MyAgent_Final agent = new MyAgent_Final("Agent Moritz und Niklas");
        Agent.start(agent, "127.0.0.1", 5000);
    }

    /**
     * Eine Methode, welche die nächste durchzuführende Aktion berechnet.
     * <p>
     * Funktionsweise:
     * Ist noch kein Lösungsknoten gefunden, so wird eine neue Suche initialisiert,
     * und diese auch Mithilfe der Methode start() der Klasse Suche gestartet.
     * <p>
     * Findet die Suche einen Lösungsknoten, so wird der Lösungsweg für diesen Knoten in der Liste actions gespeichert.
     * Wird kein Lösungsknoten gefunden, so wird die Liste actions Sicherheitshalber geleert.
     * <p>
     * Nachdem die Suche abgeschlossen ist, bzw wenn schon ein Lösungsknoten da ist,
     * wird aus der Liste actions die nächste durchzuführende Aktion rausgeholt und zurück gegeben.
     *
     * @param percept
     * @param actionEffect
     * @return Ein PacmanAction wert, welcher die nächste durchzuführende Aktion wiederspiegelt
     */
    @Override
    public PacmanAction action(PacmanPercept percept, PacmanActionEffect actionEffect) {
        PacmanTileType[][] newWorld = weltKopieren(percept.getView());

        for (Ghost ghost : ghosts)
            ghost.nextMove(percept, newWorld);

        startKnoten = new Knoten(percept, null);
        anzahlDotsGesamt = startKnoten.getAnzahlDots();

        //Util.printView(percept.getView());

        if (actionEffect == PacmanActionEffect.ATE_POWERPILL) {
            nextStep = mdpProSchritt(startKnoten, 5, true, 100);
        } else {
            nextStep = mdpProSchritt(startKnoten, 10, false, 100);
        }

        PacmanAction nextAction = null;

        switch (nextStep) {
            case NORTH -> nextAction = PacmanAction.GO_NORTH;
            case EAST -> nextAction = PacmanAction.GO_EAST;
            case SOUTH -> nextAction = PacmanAction.GO_SOUTH;
            case WEST -> nextAction = PacmanAction.GO_WEST;
        }
        startKnoten = Knoten.movePacman(startKnoten, nextStep);

        return nextAction;
    }

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

    @Override
    protected void onGameStart(PacmanStartInfo startInfo) {
        for (GhostInfo ghostInfo : startInfo.getPercept().getGhostInfos()) {
            switch (ghostInfo.getType()) {
                case "ghost_hunter" -> ghosts.add(new HunterGhost(ghostInfo.getPos().getX(), ghostInfo.getPos().getY()));
                case "ghost_eager" -> ghosts.add(new EagerGhost(ghostInfo.getPos().getX(), ghostInfo.getPos().getY()));
                case "ghost_random" -> ghosts.add(new RandomGhost(ghostInfo.getPos().getX(), ghostInfo.getPos().getY()));
            }
        }
        System.out.println("Found " + ghosts.size() + " Ghosts");
    }

    /**
     * Eine Methode die aufgerufen wird, wenn das Spiel vorbei ist.
     * <p>
     * Funktionsweise:
     * Diese Methode dient uns zur Ausgabe der Anzahl der Züge,
     * sowie der Anzahl der Elemente in sowohl der open- als auch der closed-List
     */
    @Override
    protected void onGameover(PacmanGameResult gameResult) {
        gameResult.print();
        System.out.println("Open List: " + Suche.openList.size());
        System.out.println("Closed List: " + Suche.closedList.size());
    }

    /**
     * Eine Methode, die mithilfe eines MDPs eine Policy für jedes Feld berechnet und dann die Policy für die aktuelle Position des Pacmans zurückgibt
     *
     * @param startKnoten Der Knoten, der die aktuelle Spielsituation wiedergibt. Aus dem Knoten werden Position des Pacmans und der weltzustand genommen
     * @param radius      Der Radius, für welchen, abhängig von der Position, die Policies berechnet werden.
     *                    Es wird nur ein Teil des Spielfelds berechnet, damit die Berechnung schneller ist.
     * @param powerPill   Ein boolean Wert, welcher darstellt, ob der Pacman aktuell den Powerpill Bonus hat oder nicht.
     * @param iterations  Die Anzahl der Durchläufe, welche der MDP durchlaufen soll
     * @return Die Policy für die aktuelle Position des Pacmans als Direction Wert.
     */
    private Direction mdpProSchritt(Knoten startKnoten, int radius, boolean powerPill, int iterations) {
        int xCoord = startKnoten.getxCoord();
        int yCoord = startKnoten.getyCoord();
        PacmanTileType[][] weltAusschnitt = kopiereWeltAusschnitt(startKnoten.getWelt(), radius, xCoord, yCoord);
        Direction[][] policies = new Direction[weltAusschnitt.length][weltAusschnitt[0].length];

        for (int i = 0; i < iterations; i++) {
            policies = policiesBerechnen(weltAusschnitt, powerPill);
        }

        Direction[][] policiesGanzeWelt = new Direction[startKnoten.getWelt().length][startKnoten.getWelt()[0].length];


        if (yCoord - radius <= 0) {
            if (yCoord + radius >= startKnoten.getWelt()[0].length) {
                for (int y = 0; y < startKnoten.getWelt()[0].length; y++) {
                    if (xCoord - radius <= 0) {
                        if (xCoord + radius >= startKnoten.getWelt().length) {
                            for (int x = 0; x < startKnoten.getWelt().length; x++) {
                                policiesGanzeWelt[x][y] = policies[x][y];
                            }
                        } else {
                            for (int x = 0; x < xCoord + radius; x++) {
                                policiesGanzeWelt[x][y] = policies[x][y];
                            }
                        }
                    } else {
                        if (xCoord + radius >= startKnoten.getWelt().length) {
                            for (int x = xCoord - radius; x < startKnoten.getWelt().length; x++) {
                                policiesGanzeWelt[x][y] = policies[x][y];
                            }
                        } else {
                            for (int x = xCoord - radius; x < xCoord + radius; x++) {
                                policiesGanzeWelt[x][y] = policies[x][y];
                            }
                        }
                    }
                }
            } else {
                for (int y = 0; y < yCoord + radius; y++) {
                    if (xCoord - radius <= 0) {
                        if (xCoord + radius >= startKnoten.getWelt().length) {
                            for (int x = 0; x < startKnoten.getWelt().length; x++) {
                                policiesGanzeWelt[x][y] = policies[x][y];
                            }
                        } else {
                            for (int x = 0; x < xCoord + radius; x++) {
                                policiesGanzeWelt[x][y] = policies[x][y];
                            }
                        }
                    } else {
                        if (xCoord + radius >= startKnoten.getWelt().length) {
                            for (int x = xCoord - radius; x < startKnoten.getWelt().length; x++) {
                                policiesGanzeWelt[x][y] = policies[x][y];
                            }
                        } else {
                            for (int x = xCoord - radius; x < xCoord + radius; x++) {
                                policiesGanzeWelt[x][y] = policies[x][y];
                            }
                        }
                    }
                }
            }
        } else {
            for (int y = yCoord - radius; y < yCoord + radius; y++) {
                if (xCoord - radius <= 0) {
                    for (int x = 0; x < xCoord + radius; x++) {
                        policiesGanzeWelt[x][y] = policies[x][y];
                    }
                } else {
                    for (int x = xCoord - radius; x < xCoord + radius; x++) {
                        policiesGanzeWelt[x][y] = policies[x][y];
                    }
                }
            }
        }


        //System.out.println("GANZE WELT");
        //printArray(policiesGanzeWelt);
        //Util.printView(startKnoten.getWelt());

        //printArray(policies);

        printArray(policies);
        System.out.println("Pos: " + policies[radius][radius]);

        return policiesGanzeWelt
                [10]
                [10];

        //return policies[xCoord][yCoord];
    }

    /**
     * Eine Methode, welche den gegebenen Weltausschnitt durchitteriert und für jedes Feld die policy berechnen lässt.
     *
     * @param weltAusschnitt Der Weltausschnitt, für welchen die Policies berechnet werden sollen.
     * @param powerpill      Ein boolean Wert der angibt, ob der Pacman den Powerpill Bonus hat oder nicht.
     * @return Ein Array des Typs Direction, in welchem pro Stelle die policies der einzelnen Felder gespeichert sind.
     */
    private Direction[][] policiesBerechnen(PacmanTileType[][] weltAusschnitt, boolean powerpill) {
        Direction[][] policies = new Direction[weltAusschnitt.length][weltAusschnitt[0].length];

        for (int i = 0; i < policies.length; i++) {
            for (int j = 0; j < policies[i].length; j++) {
                policies[i][j] = fromInt(maxIndex(werteProFeld(weltAusschnitt, i, j, powerpill)));
            }
        }
        //System.out.println("POLICY BERECHNEN");
        // printArray(policies);
        return policies;
    }

    /**
     * Eine Methode, welche für das Aktuelle Feld die qValues berechnet und in einem Array gespeichert zurück gibt.
     *
     * @param weltAusschnitt Der Weltausschnitt welcher betrachtet wird
     * @param xCoord         die x-Coordinate des Pacmans
     * @param yCoord         die y-Coordinate des Pacmans
     * @param powerpill      Ein boolean Wert der angibt, ob der Pacman den Powerpill Bonus hat oder nicht.
     * @return Ein Array vom Type double, welches für das aktuelle Feld die qValues beinhaltet. Das Format ist [Oben, Rechts, Unten, Links]
     */
    private double[] werteProFeld(PacmanTileType[][] weltAusschnitt, int xCoord, int yCoord, boolean powerpill) {
        double[] qValues = new double[4]; //[Nord, Ost, Süd, West]
        double schrittkosten = 0;
        double rauschen = 0;
        double discount = 0.1;
        if (yCoord - 1 >= 0) {
            qValues[0] = discount * ((1 - rauschen) * wertEinesFeldes(weltAusschnitt[xCoord][yCoord - 1], powerpill)) - schrittkosten;
        } else {
            qValues[0] = 0;
        }
        if (xCoord + 1 < weltAusschnitt.length) {
            qValues[1] = discount * ((1 - rauschen) * wertEinesFeldes(weltAusschnitt[xCoord + 1][yCoord], powerpill)) - schrittkosten;
        } else {
            qValues[1] = 0;
        }
        if (yCoord + 1 < weltAusschnitt[xCoord].length) {
            qValues[2] = discount * ((1 - rauschen) * wertEinesFeldes(weltAusschnitt[xCoord][yCoord + 1], powerpill)) - schrittkosten;
        } else {
            qValues[2] = 0;
        }
        if (xCoord - 1 >= 0) {
            qValues[3] = discount * ((1 - rauschen) * wertEinesFeldes(weltAusschnitt[xCoord - 1][yCoord], powerpill)) - schrittkosten;
        } else {
            qValues[3] = 0;
        }
        return qValues;
    }

    /**
     * Eine Methode die den Index des Maximalen wertes eines Arrays vom Typ double ausgibt
     *
     * @param a Ein Array vom Typ double, von welchem der Index des maximal Wertes ausgegeben werden soll
     * @return Der Index eines Maximums im Array
     */
    private int maxIndex(double[] a) {
        int index = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] > a[index]) {
                index = i;
            }
        }
        return index;
    }


    /**
     * Eine Methode, welche aus dem integer zwischen 0 und 3 die dazugehörige Richtung zurückgibt.
     * Die Methode dient der Umrechnung eines Indexes des Arrays, welches von der Methode werteProFeld zurückgegeben wird.
     *
     * @param i Ein Integer wert, welcher den Index des Arrays wiedergibt
     * @return Ein Wert vom Typ Direction, wecher der Position des Array indexes entspricht
     */
    public static Direction fromInt(int i) {
        return switch (i) {
            case 0 -> Direction.NORTH;
            case 1 -> Direction.EAST;
            case 2 -> Direction.SOUTH;
            case 3 -> Direction.WEST;
            default -> null;
        };
    }

    /**
     * Methode, welche den Wert eines Feldes vor dem Ersten Itterations schritt zurückgibt.
     * Der Wert ist jeweils abhängig vom Typ des Feldes
     *
     * @param feld      Das aktuelle Feld welches betrachtet wird als PacmanTileType
     * @param powerpill Ein boolean Wert der angibt, ob der Pacman den Powerpill Bonus hat oder nicht.
     * @return einen Integerwert, welcher den Wert des Feldes vor dem ersten Itterationschritt angibt
     */
    private int wertEinesFeldes(PacmanTileType feld, boolean powerpill) {
        if (powerpill) {
            switch (feld) {
                case DOT, POWERPILL, GHOST, GHOST_AND_DOT, GHOST_AND_POWERPILL -> {
                    return 5;
                }
                case WALL -> {
                    return -100;
                }
                case EMPTY -> {
                    return 1;
                }
                default -> {
                    return 0;
                }
            }
        } else {

            switch (feld) {
                case DOT, POWERPILL -> {
                    return 5;
                }
                case GHOST, GHOST_AND_DOT, GHOST_AND_POWERPILL -> {
                    return -Integer.MAX_VALUE;
                }
                case WALL -> {
                    return -100;
                }
                case EMPTY -> {
                    return 1;
                }
                default -> {
                    return 0;
                }
            }
        }
    }

    //TODO implementierung fehlt
    private boolean istSackgasse() {
        return false;
    }

    /**
     * Eine Methode, welche aus einer Welt den ausschnitt zu dem Gewünschten Radius um die Position des Pacmans zurückgibt
     *
     * @param welt   Die komplette Welt, aus welcher der Ausschnitt gemacht werden soll
     * @param radius Der Radius für den Ausschnitt
     * @param xCoord Die X-Koordinate des Pacman
     * @param yCoord Die Y-Koordinate des Pacman
     * @return Ein Zweidimensionales Array vom Type PacmanTileType
     */
    private PacmanTileType[][] kopiereWeltAusschnitt(PacmanTileType[][] welt, int radius, int xCoord, int yCoord) {
        PacmanTileType[][] weltAusschnitt = new PacmanTileType[radius * 2 + 1][radius * 2 + 1];
        for (int i = 0; i < weltAusschnitt.length; i++) {
            for (int j = 0; j < weltAusschnitt[i].length; j++) {
                if (xCoord - (radius - i) >= 0 && yCoord - (radius - j) >= 0 && xCoord - (radius - i) < welt.length && yCoord - (radius - j) < welt[0].length) {
                    weltAusschnitt[i][j] = welt[xCoord - (radius - i)][yCoord - (radius - j)];
                } else {
                    weltAusschnitt[i][j] = PacmanTileType.WALL;
                }
            }
        }
        Util.printView(weltAusschnitt);

        return weltAusschnitt;


    }

    private void printArray(Direction[][] a) {
        String firstLine = " ";
        for (int m = 0; m < a.length; m++) {
            firstLine = firstLine.concat(String.format("|%d|", m));
        }
        System.out.println(firstLine);

        for (int i = 0; i < a.length; i++) {
            System.out.printf("%2d", i);
            for (int j = 0; j < a[i].length; j++) {
                if (a[i][j] == null) continue;
                switch (a[i][j]) {
                    case NORTH -> System.out.print("|N|");
                    case EAST -> System.out.print("|E|");
                    case SOUTH -> System.out.print("|S|");
                    case WEST -> System.out.print("|W|");
                    default -> System.out.print("|#|");
                }
            }
            System.out.println();
        }
        System.out.println("=========================================");
    }
}