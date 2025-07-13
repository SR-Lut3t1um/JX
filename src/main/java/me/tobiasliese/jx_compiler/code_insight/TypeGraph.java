package me.tobiasliese.jx_compiler.code_insight;

import java.lang.constant.ClassDesc;
import java.util.*;

public class TypeGraph {
    private static final Map<ClassDesc, CodeModel> map = new HashMap<>();
    private final Map<String, ClassDesc> nodes = new HashMap<>();
    private final List<String> nodeList = new ArrayList<>();

    public static void addEntry(ClassDesc desc, CodeModel cm) {
        map.put(desc, cm);
    }

    public static boolean contains(ClassDesc desc) {
        return map.containsKey(desc);
    }

    public void addNode(String key, ClassDesc desc) {
        nodeList.add(key);
        nodes.put(key, desc);
        if (desc == null) {
            return;
        }
        if (desc.isArray() || desc.isPrimitive())
            return;
        if (! map.containsKey(desc)) {
            CodeModel cm = null;
            try {
                cm = CodeModel.of(desc);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            map.put(desc, cm);
        }
    }

    public List<ClassDesc> getNodes() {
        return nodeList.stream().map(nodes::get).toList();
    }

    public Set<Map.Entry<String, ClassDesc>> getNodesEntrySet() {
        return nodes.entrySet();
    }

    public ClassDesc getNode(String node) {
        return nodes.get(node);
    }

    public int getNodeIndex(String node) {
        return nodeList.indexOf(node);
    }

    public static CodeModel getCodeModel(ClassDesc classDesc) {
        return map.get(classDesc);
    }
}

