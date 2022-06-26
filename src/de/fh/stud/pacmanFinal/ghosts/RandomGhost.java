package de.fh.stud.pacmanFinal.ghosts;

import de.fh.pacman.PacmanPercept;
import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.pacmanFinal.Direction;

public class RandomGhost extends Ghost {

    public RandomGhost(int posX, int posY) {
        super(posX, posY);
    }

    @Override
    public void nextMove(PacmanPercept percept, PacmanTileType[][] welt) {
        updateNewPosition(percept);
        Direction direction = calcNextDirection();
        updateWelt(welt, direction);
    }

}