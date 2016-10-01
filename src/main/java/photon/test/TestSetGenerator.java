package photon.test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import photon.data.Arrow;
import photon.data.ArrowType;
import photon.data.Extension;
import photon.data.Node;
import photon.util.EQueue;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Dun Liu on 9/30/2016.
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
  /*      ArrowType[] arrTypes = ArrowType.values();
        int arrTypeCount = arrTypes.length;
        Set<ArrowType> testedArrTypes = new HashSet<>();
        int uArrTypeCount = Arrays.stream(arrTypes).mapToInt(at -> {
            if (testedArrTypes.add(at)) {
                testedArrTypes.add(at.reverse());
                return 1;
            } else return 0;
        }).sum();
*/
        Tree tree = Tree.createFibTree(depth, startId);

        traverseTree(tree, tsg.nodes, tsg.arrows);

        return tsg;
    }

    private static void traverseTree(Tree tree, Collection<Node> nodes, Collection<Arrow> arrows) {
        if (tree == null) return;

        Node n = new Node("Node #" + String.valueOf(tree.id));
        n.setId(tree.id);
        nodes.add(n);

        if (tree.left == null) return;

        Arrow al = new Arrow(n.getId(), ArrowType.PARENT_OF, tree.left.id);

        if (tree.spawning) {
            Arrow ar = new Arrow(n.getId(), ArrowType.PARENT_OF, tree.right.id);
            arrows.add(ar);
            traverseTree(tree.right, nodes, arrows);
        }

        arrows.add(al);
        traverseTree(tree.left, nodes, arrows);

    }

    private static class Tree {
        boolean spawning = false;
        Tree left = null, right = null;
        int id;

        Tree(int id, boolean b) {
            this.id = id;
            spawning = b;
        }

        public static Tree createFibTree(int depth, int startId) {
            if (depth <= 0) return null;
            Tree root = new Tree(startId, false);
            EQueue<Tree> q = new EQueue<>();
            q.enqueue(root);
            Map<Tree, Integer> depthMap = new HashMap<>();
            depthMap.put(root, depth);
            int id = startId, d;

            while (!q.isEmpty()) {
                Tree tree = q.dequeue();
                d = depthMap.get(tree);
                if (d > 0) {
                    if (tree.spawning) {
                        tree.left = new Tree(++id, false);
                        tree.right = new Tree(++id, true);
                        q.enqueue(tree.left);
                        q.enqueue(tree.right);
                        depthMap.put(tree.left, d - 1);
                        depthMap.put(tree.right, d - 1);
                    } else {
                        tree.left = new Tree(++id, true);
                        q.enqueue(tree.left);
                        depthMap.put(tree.left, d - 1);
                    }
                }
            }
            return root;
        }
    }
}
