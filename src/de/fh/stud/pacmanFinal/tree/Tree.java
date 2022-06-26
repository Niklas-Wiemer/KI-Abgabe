package de.fh.stud.pacmanFinal.tree;

import de.fh.kiServer.util.Vector2;
import de.fh.pacman.enums.PacmanTileType;
import de.fh.stud.pacmanFinal.Direction;

import java.util.LinkedList;
import java.util.List;

public class Tree {

    private final int radius;
    private final PacmanTileType[][] welt;
    private final Node wurzel;

    public Tree(PacmanTileType[][] welt, Vector2 vector2, Direction direction, int radius) {
        this.welt = welt;
        this.radius = radius;

        Node node;

        switch (direction) {
            case NORTH -> node = new Node(welt, new Vector2(vector2.getX(), vector2.getY() - 1), Direction.SOUTH);
            case EAST -> node = new Node(welt, new Vector2(vector2.getX() + 1, vector2.getY()), Direction.WEST);
            case SOUTH -> node = new Node(welt, new Vector2(vector2.getX(), vector2.getY() + 1), Direction.NORTH);
            case WEST -> node = new Node(welt, new Vector2(vector2.getX() - 1, vector2.getY()), Direction.EAST);
            default -> node = new Node(welt, vector2, direction);
        }

        this.wurzel = node;
    }

    private boolean createTree(Direction ignoreDirection) {
        LinkedList<Node> openList = new LinkedList<>(wurzel.expand());

        Node current;
        while (!openList.isEmpty()) {
            current = openList.removeFirst();

            if (current.equals(wurzel)) {
                return false;
            }

            openList.addAll(current.expand());
        }
        return true;
    }

    public boolean contains(Node node) {
        List<Node> nodes = new LinkedList<>();
        nodes.add(wurzel);

        Node current;

        while (!nodes.isEmpty()) {
            current = nodes.remove(0);

            if (current == node) return true;

            for (Node children : current.getNodes()) {
                if (children == null) break;
                nodes.add(children);
            }
        }
        return false;
    }

    public int getDeep(Direction ignoreDirection) {
        if (!createTree(ignoreDirection)) return -1;

        return depthOfTree(wurzel);
    }

    private int depthOfTree(Node node) {
        if (node == null)
            return 0;

        int maxdepth = 0;
        for (Node it : node.getNodes())
            maxdepth = Math.max(maxdepth,
                    depthOfTree(it));

        return maxdepth + 1;
    }

}
