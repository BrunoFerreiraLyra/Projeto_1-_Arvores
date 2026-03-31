class WAVLTree {

    class Node {
        int key, rank;
        Node left, right;

        Node(int key) {
            this.key = key;
            this.rank = 0;
        }
    }

    Node root;

    int rank(Node n) {
        return n == null ? -1 : n.rank;
    }

    Node insert(Node node, int key) {
        if (node == null)
            return new Node(key);

        if (key < node.key)
            node.left = insert(node.left, key);
        else if (key > node.key)
            node.right = insert(node.right, key);

        return rebalanceInsert(node);
    }

    Node rebalanceInsert(Node node) {
        if (rank(node) - rank(node.left) == 0) {
            node.rank++;
        } else if (rank(node) - rank(node.right) == 0) {
            node.rank++;
        }
        return node;
    }

    Node delete(Node node, int key) {
        if (node == null)
            return null;

        if (key < node.key)
            node.left = delete(node.left, key);
        else if (key > node.key)
            node.right = delete(node.right, key);
        else {
            if (node.left == null && node.right == null)
                return null;
            else if (node.left == null)
                return node.right;
            else if (node.right == null)
                return node.left;
            else {
                Node successor = min(node.right);
                node.key = successor.key;
                node.right = delete(node.right, successor.key);
            }
        }

        return rebalanceDelete(node);
    }

    Node rebalanceDelete(Node node) {
        if (rank(node) - rank(node.left) == 3 || rank(node) - rank(node.right) == 3) {
            node.rank--;
        }
        return node;
    }

    Node min(Node node) {
        while (node.left != null)
            node = node.left;
        return node;
    }

    void preOrder(Node node) {
        if (node != null) {
            System.out.print(node.key + "(" + node.rank + ") ");
            preOrder(node.left);
            preOrder(node.right);
        }
    }

    public static void main(String[] args) {
        WAVLTree tree = new WAVLTree();

        int[] values = {50, 25, 75, 10, 30, 60, 80, 5, 15, 27, 35};

        for (int v : values)
            tree.root = tree.insert(tree.root, v);

        System.out.println("Antes da remoção:");
        tree.preOrder(tree.root);

        tree.root = tree.delete(tree.root, 80);

        System.out.println("\nDepois da remoção do 80:");
        tree.preOrder(tree.root);
    }
}
