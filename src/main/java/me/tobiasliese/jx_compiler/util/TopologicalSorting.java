package me.tobiasliese.jx_compiler.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A helper class containing algorithms for topologicial sorting
 * @see <a href="https://en.wikipedia.org/wiki/Topological_sorting">wiki</a>
 */
public class TopologicalSorting {
    public static <T> List<T> kahnsAlgorithm(Map<T, List<T>> graph) {
        List<T> l = new ArrayList<>();
        List<T> s = setupNoInc(graph);

        while (! s.isEmpty()) {
            var n = s.removeFirst();
            l.add(n);
            for (var m: List.copyOf(graph.get(n))) {
                graph.get(n).remove(m);
                if (hasNoMoreIncomingEdge(m, graph))
                    s.add(m);
            }
        }

        for (var list: graph.values()) {
            if (! list.isEmpty())
                throw new IllegalStateException("Graph has at least one cycle");
        }
        return l.reversed();
    }

    private static <T> List<T> setupNoInc(Map<T, List<T>> graph) {
        List<T> noInc = new ArrayList<>(graph.keySet());
        for (var entry: graph.entrySet()) {
            for (var target: entry.getValue()) {
                noInc.remove(target);
            }
        }
        return noInc;
    }

    private static <T> boolean hasNoMoreIncomingEdge(T node, Map<T, List<T>> graph) {
        for (var list: graph.values()) {
            if (list.contains(node))
                return false;
        }
        return true;
    }
}
