package io.github.nambach.excelutil.style;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


class Node<T> {
    private final String id;
    private final Map<String, Node<T>> children;
    @Setter
    @Getter
    private T data;

    public Node(String id, T data) {
        this.id = id;
        this.data = data;
        this.children = new HashMap<>();
    }

    public Node<T> getChild(String id) {
        return children.get(id);
    }

    public void addChild(String id, T data) {
        if (children.containsKey(id)) {
            return;
        }
        Node<T> child = new Node<>(id, data);
        children.put(id, child);
    }

    public void addChild(Node<T> node) {
        if (children.containsKey(node.id)) {
            return;
        }
        children.put(node.id, node);
    }

    public Node<T> lookup(List<String> path) {
        if (path == null || path.size() == 0) {
            return null;
        }
        Node<T> current = this;
        for (String route : path) {
            if (current == null) {
                break;
            }
            current = current.children.getOrDefault(route, null);
        }
        return current;
    }

    public void updatePath(List<String> path, T newData) {
        Node<T> current = this;
        for (String route : path) {
            Node<T> child = current.children.get(route);
            if (child == null) {
                child = new Node<>(route, null);
                current.children.put(child.id, child);
            }
            current = child;
        }
        current.setData(newData);
    }

    public int countAllChildren() {
        int total = data != null ? 1 : 0;
        for (Node<T> child : children.values()) {
            total += child.countAllChildren();
        }
        return total;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%d)", id, data.toString(), children.size());
    }

//    public static void main(String[] args) {
//        String root = "root";
//        String a = "a";
//        String b = "b";
//        String c = "c";
//        String d = "d";
//        Node<String> tree = new Node<>(root, root);
//        tree.addChild(b, b);
//        tree.lookup(b).addChild(c, c);
//        tree.lookup(b).addDeepChildren(Arrays.asList(
//                new Node<>(d, d),
//                new Node<>(a, a)
//        ));
//        Node<String> childA = tree.lookup(b, d, a);
//        tree.lookup(b).addDeepChildren(Arrays.asList(
//                new Node<>(d, d),
//                new Node<>(a, a),
//                new Node<>(c, c)
//        ));
//        Node<String> childA1 = tree.lookup(b, d, a);
//        System.out.println(childA == childA1);
//        tree.lookup();
//        System.out.println(tree.lookup(b, d, a, c));
//        System.out.println(tree.countAllChildren());
//    }
}
