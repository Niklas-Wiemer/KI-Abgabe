package de.fh.stud.pacmanFinal.ghosts;

import de.fh.pacman.GhostInfo;
import de.fh.pacman.PacmanPercept;
import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.pacmanFinal.Direction;

public abstract class Ghost {
    protected int posX;
    protected int posY;

    public Ghost(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public abstract void nextMove(PacmanPercept percept, PacmanTileType[][] welt);

    public Direction calcNextDirection() {
        double chance = Math.random();
        if (chance <= 0.25) {
            return Direction.NORTH;
        } else if (chance <= 0.5) {
            return Direction.EAST;
        } else if (chance <= 0.75) {
            return Direction.SOUTH;
        } else {
            return Direction.WEST;
        }
    }

    protected void updateNewPosition(PacmanPercept percept) {
        for (GhostInfo info : percept.getGhostInfos()) {
            if (this instanceof HunterGhost && info.getType().equalsIgnoreCase("ghost_hunter")) {
                this.posX = info.getPos().getX();
                this.posY = info.getPos().getY();
                return;
            }
            if (this instanceof RandomGhost && info.getType().equalsIgnoreCase("ghost_random")) {
                this.posX = info.getPos().getX();
                this.posY = info.getPos().getY();
                return;
            }
            if (this instanceof EagerGhost && info.getType().equalsIgnoreCase("ghost_eager")) {
                this.posX = info.getPos().getX();
                this.posY = info.getPos().getY();
                return;
            }
        }
    }

    public boolean checkForWall(PacmanTileType[][] welt, Direction direction) {
        return switch (direction) {
            case NORTH -> welt[posX][posY - 1] == PacmanTileType.WALL;
            case EAST -> welt[posX + 1][posY] == PacmanTileType.WALL;
            case SOUTH -> welt[posX][posY + 1] == PacmanTileType.WALL;
            case WEST -> welt[posX - 1][posY] == PacmanTileType.WALL;
            default -> false;
        };
    }

    protected void updateWelt(PacmanTileType[][] welt, Direction nextAction) {
        int newX = posX;
        int newY = posY;

        if (checkForWall(welt, nextAction)) return;

        switch (nextAction) {
            case NORTH -> newY--;
            case EAST -> newX++;
            case SOUTH -> newY++;
            case WEST -> newX--;
        }

        switch (welt[newX][newY]) {
            case EMPTY -> welt[newX][newY] = PacmanTileType.GHOST;
            case DOT -> welt[newX][newY] = PacmanTileType.GHOST_AND_DOT;
            case POWERPILL -> welt[newX][newY] = PacmanTileType.GHOST_AND_POWERPILL;
        }

        switch (welt[posX][posY]) {
            case GHOST -> welt[posX][posY] = PacmanTileType.EMPTY;
            case GHOST_AND_DOT -> welt[posX][posY] = PacmanTileType.DOT;
            case GHOST_AND_POWERPILL -> welt[posX][posY] = PacmanTileType.POWERPILL;
        }
    }
}