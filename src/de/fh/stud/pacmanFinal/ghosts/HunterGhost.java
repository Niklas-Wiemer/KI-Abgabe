package de.fh.stud.pacmanFinal.ghosts;

import de.fh.pacman.PacmanPercept;
import de.fh.pacman.enums.PacmanAction;
import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.p3.Suchverfahren;
import de.fh.stud.pacmanFinal.Direction;
import de.fh.stud.pacmanFinal.ghosts.hunter.HunterKnoten;
import pacmanFinal.testing.HunterSuche;

import java.util.Stack;

public class HunterGhost extends Ghost {

    public HunterGhost(int posX, int posY) {
        super(posX, posY);
    }

    @Override
    public void nextMove(PacmanPercept percept, PacmanTileType[][] welt) {
        updateNewPosition(percept);
        Direction nextDirection = Direction.UNKNOWN;

        HunterSuche suche = new HunterSuche(percept, new HunterKnoten(welt, posX,
                posY, null));

        HunterKnoten knoten = suche.start(Suchverfahren.GREEDY_SUCHE, percept.getPosX(), percept.getPosY());
        if (knoten != null) {
            Stack<PacmanAction> loesung = knoten.getLoesungsweg();

            if (!loesung.isEmpty()) {
                switch (loesung.pop()) {
                    case GO_NORTH -> nextDirection = Direction.NORTH;
                    case GO_EAST -> nextDirection = Direction.EAST;
                    case GO_SOUTH -> nextDirection = Direction.SOUTH;
                    case GO_WEST -> nextDirection = Direction.WEST;
                }
            }
        }
        updateWelt(welt, nextDirection);
    }
}
