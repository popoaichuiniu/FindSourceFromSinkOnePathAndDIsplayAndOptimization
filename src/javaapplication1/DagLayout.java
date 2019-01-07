package javaapplication1;

import java.awt.Color;
import java.util.Iterator;
import java.util.Random;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;

/**
 * Simple Layout for directed acyclic graphs (DAGs). The nodes are arranged in
 * discrete layers so that the edges will always point downwards (if no loop
 * exists). The nodes are arranged as far to the top as possible. The horizontal
 * layout is done by assigning the nodes to discrete slots in each layer. While
 * running, slots are chosen randomly and swapped if this would make the edges
 * shorter.
 *
 * @author Maik Niggemann
 */
public class DagLayout implements Layout {

    //Architecture
    private final LayoutBuilder builder;
    private GraphModel graphModel;
    //Flags
    private boolean executing = false;

    private int nodeCount;
    private float radius;
    private int minNodeSize = 10;
    private int maxNodeSize = 100;

    private int minColor = 0x000033;
    private int maxColor = 0x0000FF;

    private DirectedGraph diGraph;
    // private boolean visitedCell[][];

    public DagLayout(DagLayoutBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void resetPropertiesValues() {

    }

    @Override
    public void initAlgo() {
        executing = true;

        diGraph = graphModel.getDirectedGraph();
        nodeCount = diGraph.getNodeCount();

        radius = (float) nodeCount * maxNodeSize;//radius随着nodeCount和maxNodeSize的大小变化而变化
        // visitedCell = new boolean[2 * nodeCount][2 * nodeCount];//2*radius/maxNodeSize 每个格子的大小为maxNodeSize

        diGraph.readLock();

        initLayoutData(diGraph);

        diGraph.readUnlock();

    }

    @Override
    public void goAlgo() {
        diGraph = graphModel.getDirectedGraph();

        diGraph.readLock();

        int maxInDegree = -1;
        int maxOutDegree = -1;
        for (Iterator<Node> iterator = diGraph.getNodes().iterator(); iterator.hasNext();) {
            Node node = iterator.next();
            int inDegree = diGraph.getInDegree(node);
            int outDegree = diGraph.getOutDegree(node);
            if (inDegree > maxInDegree) {
                maxInDegree = inDegree;
            }

            if (outDegree > maxOutDegree) {
                maxOutDegree = outDegree;
            }

        }
        float deltSize = (maxNodeSize - minNodeSize) / (maxOutDegree + 1);
        int deltColor = (maxColor - minColor) / (maxInDegree + 1);

        for (Iterator<Node> iterator = diGraph.getNodes().iterator(); iterator.hasNext();) {
            Node node = iterator.next();
            node.setSize(minNodeSize + deltSize * diGraph.getOutDegree(node));
            node.setColor(new Color(minColor + deltColor * diGraph.getInDegree(node)));

        }
        for (Iterator<Node> iterator = diGraph.getNodes().iterator(); iterator.hasNext();) {
            Node node = iterator.next();

            float x = new Random().nextInt((int) radius) * (float) Math.pow(-1, new Random().nextInt(2) + 1);

            float y = new Random().nextInt((int) (Math.pow(Math.pow(radius, 2) - Math.pow(x, 2), 0.5)))
                    * (float) Math.pow(-1,
                            new Random().nextInt(2) + 1);

//            float x=0;
//            float y=0;
//
//           
//
//            int i = -1;//i,j是大于等于0的
//            int j = -1;
//            boolean flag = false;
//
//            int countX = 0;
//            int countY = 0;
//            while (!flag) {
//                
//                
//                x = new Random().nextInt((int) radius) * (float) Math.pow(-1, new Random().nextInt(2) + 1);
//                y = new Random().nextInt((int) (Math.pow(Math.pow(radius, 2) - Math.pow(x, 2), 0.5)))
//                        * (float) Math.pow(-1,
//                                new Random().nextInt(2) + 1);
//
//                countX = (int) (x / maxNodeSize);
//                countY = (int) (y / maxNodeSize);
//                i = (int) (x / maxNodeSize) + nodeCount - 1;
//                j = (int) (y / maxNodeSize) + nodeCount - 1;
//
//                if (x > 0 && y > 0) {
//                    if ((x - node.size() / 2) > maxNodeSize * countX && (x + node.size() / 2) < maxNodeSize * (countX + 1)
//                            && (y - node.size() / 2) > maxNodeSize * countY && (y + node.size() / 2) < maxNodeSize * (countY + 1) && !visitedCell[i][j]) {
//                        flag = true;
//                        visitedCell[i][j] = true;
//                        break;
//                    }
//
//                }
//                if (x > 0 && y < 0) {
//                    if ((x - node.size() / 2) > maxNodeSize * countX && (x + node.size() / 2) < maxNodeSize * (countX + 1)
//                            && (y - node.size() / 2) > maxNodeSize * (countY - 1) && (y + node.size() / 2) < maxNodeSize * countY && !visitedCell[i][j]) {
//                        flag = true;
//                        visitedCell[i][j] = true;
//                        break;
//                    }
//
//                }
//                if (x < 0 && y > 0) {
//                    if ((x - node.size() / 2) > maxNodeSize * (countX - 1) && (x + node.size() / 2) < maxNodeSize * countX
//                            && (y - node.size() / 2) > maxNodeSize * countY && (y + node.size() / 2) < maxNodeSize * (countY + 1) && !visitedCell[i][j]) {
//                        flag = true;
//                        visitedCell[i][j] = true;
//                        break;
//                    }
//
//                }
//                if (x < 0 && y < 0) {
//                    if ((x - node.size() / 2) > maxNodeSize * (countX -1) && (x + node.size() / 2) < maxNodeSize * countX
//                            && (y - node.size() / 2) > maxNodeSize * (countY - 1) && (y + node.size() / 2) < maxNodeSize * countY && !visitedCell[i][j]) {
//                        flag = true;
//                        visitedCell[i][j] = true;
//                        break;
//                    }
//
//                }
//
//               
//
//            }
            // float z=(float)Math.pow(Math.pow(radius, 2)-Math.pow(x, 2)-Math.pow(y, 2), 0.5)*(float)Math.pow(-1, new Random().nextInt(2)+1);
            float z = 0;

            node.setX(x);
            node.setY(y);
            node.setZ(z);
        }

        diGraph.readUnlock();
    }

    @Override
    public void endAlgo() {
        executing = false;
    }

    @Override
    public boolean canAlgo() {
        return executing;
    }

    @Override
    public LayoutBuilder getBuilder() {
        return builder;
    }

    @Override
    public void setGraphModel(GraphModel gm) {
        this.graphModel = gm;
    }

    private DagLayoutData getLayoutData(Node node) {
        if (node == null) {
            return null;
        }

        return (DagLayoutData) node.getLayoutData();
    }

    private void initLayoutData(DirectedGraph diGraph) {

    }

    @Override
    public LayoutProperty[] getProperties() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
