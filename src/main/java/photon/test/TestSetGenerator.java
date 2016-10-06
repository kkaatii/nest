package photon.test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import photon.data.Arrow;
import photon.data.ArrowType;
import photon.data.Extension;
import photon.data.Node;
import photon.util.BinTree;
import photon.util.EQueue;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Manual testing.
 */

@Component
public class TestSetGenerator {
    final Collection<Node> nodes;
    final Collection<Arrow> arrows;
    final Collection<Extension> extensions;

    public Collection<Node> getNodes() {
        return nodes;
    }

    public Collection<Arrow> getArrows() {
        return arrows;
    }

    public Collection<Extension> getExtensions() {
        return extensions;
    }

    static final double SQRT_5 = Math.sqrt(5);
    static final double LGR = (SQRT_5 + 1) / 2;
    static final double SGR = (1 - SQRT_5) / 2;

    private TestSetGenerator() {
        nodes = new ArrayList<>();
        arrows = new ArrayList<>();
        extensions = new ArrayList<>();
    }

    public TestSetGenerator buildFromGraphDescriptionFile(String filepath) {
        TestSetGenerator tsg = new TestSetGenerator();
        filepath = "photon/test/" + filepath + ".gdf";
        Resource resource = new ClassPathResource(filepath);

        try {
            InputStream is = resource.getInputStream();
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return tsg;
    }

    public static TestSetGenerator buildDefault(int depth, int startId) {
        if (depth <= 0) return null;

        TestSetGenerator tsg = new TestSetGenerator();
        FibTree tree = FibTree.createFibTree(depth, startId);
        traverseTree(tree, tsg.nodes, tsg.arrows);

        return tsg;
    }

    private static void traverseTree(BinTree tree, Collection<Node> nodes, Collection<Arrow> arrows) {
        if (tree == null) return;

        Node n = new Node("Node #" + String.valueOf(tree.getId()));
        n.setId(tree.getId());
        nodes.add(n);

        if (tree.getLeft() == null) return;

        Arrow al = new Arrow(n.getId(), ArrowType.PARENT_OF, tree.getLeft().getId());

        if (tree.getRight() != null) {
            Arrow ar = new Arrow(n.getId(), ArrowType.PARENT_OF, tree.getRight().getId());
            arrows.add(ar);
            traverseTree(tree.getRight(), nodes, arrows);
        }

        arrows.add(al);
        traverseTree(tree.getLeft(), nodes, arrows);

    }

    private static class FibTree extends BinTree {
        boolean spawning = false;
        int id;

        FibTree(int id, boolean b) {
            this.id = id;
            spawning = b;
        }

        static FibTree createFibTree(int depth, int startId) {
            if (depth <= 0) return null;
            FibTree root = new FibTree(startId, false);
            EQueue<BinTree> q = new EQueue<>();
            q.enqueue(root);
            Map<BinTree, Integer> depthMap = new HashMap<>();
            depthMap.put(root, depth);
            int id = startId, d;

            while (!q.isEmpty()) {
                FibTree fibTree = (FibTree) q.dequeue();
                d = depthMap.get(fibTree);
                if (d > 0) {
                    if (fibTree.spawning) {
                        fibTree.left = new FibTree(++id, false);
                        fibTree.right = new FibTree(++id, true);
                        q.enqueue(fibTree.left);
                        q.enqueue(fibTree.right);
                        depthMap.put(fibTree.left, d - 1);
                        depthMap.put(fibTree.right, d - 1);
                    } else {
                        fibTree.left = new FibTree(++id, true);
                        q.enqueue(fibTree.left);
                        depthMap.put(fibTree.left, d - 1);
                    }
                }
            }
            return root;
        }
    }
}
