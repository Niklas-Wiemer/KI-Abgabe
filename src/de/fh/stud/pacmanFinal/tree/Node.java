package de.fh.stud.pacmanFinal.tree;

import de.fh.kiServer.util.Vector2;
import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.pacmanFinal.Direction;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Node {

    private final PacmanTileType[][] welt;
    private final Vector2 vector2;
    private final Direction ignoreDirection;

    private final Node[] nodes = new Node[3];

    public Node(PacmanTileType[][] welt, Vector2 vector2, Direction ignoreDirection) {
        this.welt = welt;
        this.vector2 = vector2;
        this.ignoreDirection = ignoreDirection;
    }

    public List<Node> expand() {
        int index = 0;
        Direction direction;
        for (int i = 0; i < 4; i++) {
            direction = Direction.values()[i];
            if (direction == ignoreDirection) continue;
            switch (direction) {
                case NORTH -> {
                    if (welt[vector2.getX()][vector2.getY() - 1] != PacmanTileType.WALL) {
                        nodes[index++] = new Node(welt, new Vector2(vector2.getX(), vector2.getY() - 1), Direction.SOUTH);
                    }
                }
                case EAST -> {
                    if (welt[vector2.getX() + 1][vector2.getY()] != PacmanTileType.WALL) {
                        nodes[index++] = new Node(welt, new Vector2(vector2.getX() + 1, vector2.getY()), Direction.WEST);
                    }
                }
                case SOUTH -> {
                    if (welt[vector2.getX()][vector2.getY() + 1] != PacmanTileType.WALL) {
                        nodes[index++] = new Node(welt, new Vector2(vector2.getX(), vector2.getY() + 1), Direction.NORTH);
                    }
                }
                case WEST -> {
                    if (welt[vector2.getX() - 1][vector2.getY()] != PacmanTileType.WALL) {
                        nodes[index++] = new Node(welt, new Vector2(vector2.getX() - 1, vector2.getY()), Direction.EAST);
                    }
                }
            }
        }

        List<Node> list = new LinkedList<>();
        for (Node node : nodes) {
            if (node == null) break;
            list.add(node);
        }
        return list;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Node node)) return false;
        return node.getVector2().getX() == getVector2().getX() && node.getVector2().getY() == getVector2().getY();
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(vector2, ignoreDirection);
        result = 31 * result + Arrays.deepHashCode(welt);
        result = 31 * result + Arrays.hashCode(nodes);
        return result;
    }

    public Vector2 getVector2() {
        return vector2;
    }

    public Node[] getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        return "Node{" +
                "vector2=" + vector2 +
                ", ignoreDirection=" + ignoreDirection +
                '}';
    }
}
