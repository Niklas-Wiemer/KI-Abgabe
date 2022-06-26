package de.fh.stud.pacmanFinal.ghosts;

import de.fh.pacman.PacmanPercept;
import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.pacmanFinal.Direction;

public class EagerGhost extends Ghost {

    private int oldX;
    private int oldY;
    private Direction direction;

    public EagerGhost(int posX, int posY) {
        super(posX, posY);
        this.direction = Direction.UNKNOWN;
    }

    private void calcDirection() {
        if (posX > oldX) {
            direction = Direction.EAST;
        } else if (posX < oldX) {
            direction = Direction.WEST;
        } else if (posY > oldY) {
            direction = Direction.SOUTH;
        } else if (posY < oldY) {
            direction = Direction.NORTH;
        }
    }

    @Override
    public void nextMove(PacmanPercept percept, PacmanTileType[][] welt) {
        oldX = posX;
        oldY = posY;
        updateNewPosition(percept);
        calcDirection();

        Direction nextAction = null;

        switch (direction) {
            case NORTH:
                if (welt[posX][posY - 1] != PacmanTileType.WALL) {
                    if (welt[posX + 1][posY] == PacmanTileType.WALL && welt[posX - 1][posY] == PacmanTileType.WALL) {
                        nextAction = Direction.NORTH;
                    } else {
                        nextAction = calcNextDirection();
                        while (checkForWall(welt, nextAction)) {
                            nextAction = calcNextDirection();
                        }
                    }
                } else {
                    nextAction = calcNextDirection();
                    while (checkForWall(welt, nextAction)) {
                        nextAction = calcNextDirection();
                    }
                }
                break;
            case EAST:
                if (welt[posX + 1][posY] != PacmanTileType.WALL) {
                    if (welt[posX][posY - 1] == PacmanTileType.WALL && welt[posX][posY + 1] == PacmanTileType.WALL) {
                        nextAction = Direction.EAST;
                    } else {
                        nextAction = calcNextDirection();
                        while (checkForWall(welt, nextAction)) {
                            nextAction = calcNextDirection();
                        }
                    }
                } else {
                    nextAction = calcNextDirection();
                    while (checkForWall(welt, nextAction)) {
                        nextAction = calcNextDirection();
                    }
                }
                break;
            case SOUTH:
                if (welt[posX][posY + 1] != PacmanTileType.WALL) {
                    if (welt[posX + 1][posY] == PacmanTileType.WALL && welt[posX - 1][posY] == PacmanTileType.WALL) {
                        nextAction = Direction.SOUTH;
                    } else {
                        nextAction = calcNextDirection();
                        while (checkForWall(welt, nextAction)) {
                            nextAction = calcNextDirection();
                        }
                    }
                } else {
                    nextAction = calcNextDirection();
                    while (checkForWall(welt, nextAction)) {
                        nextAction = calcNextDirection();
                    }
                }
                break;
            case WEST:
                if (welt[posX - 1][posY] != PacmanTileType.WALL) {
                    if (welt[posX][posY - 1] == PacmanTileType.WALL && welt[posX][posY + 1] == PacmanTileType.WALL) {
                        nextAction = Direction.WEST;
                    } else {
                        nextAction = calcNextDirection();
                        while (checkForWall(welt, nextAction)) {
                            nextAction = calcNextDirection();
                        }
                    }
                } else {
                    nextAction = calcNextDirection();
                    while (checkForWall(welt, nextAction)) {
                        nextAction = calcNextDirection();
                    }
                }
                break;
            case UNKNOWN:
                nextAction = calcNextDirection();
                while (checkForWall(welt, nextAction))
                    nextAction = calcNextDirection();
                break;
        }
        direction = nextAction;
        updateWelt(welt, direction);
    }
}
