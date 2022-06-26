package de.fh.stud.pacmanFinal.tree;

import de.fh.pacman.PacmanPercept;
import de.fh.stud.pacmanFinal.Direction;

public class Sackgasse {

    // ToDo max. radius hinzufügen

    /**
     * @param percept
     * @param direction
     * @param radius
     * @return -1 wenn es keine Sackgasse ist, ansonsten die Größe der Sackgasse (max. radius Größe)
     */

    public static int isSackgasse(PacmanPercept percept, Direction direction, int radius) {
        Tree tree = new Tree(percept.getView(), percept.getPosition(), direction, radius);

        return tree.getDeep(direction);
    }
}
