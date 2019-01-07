package javaapplication1;

import org.gephi.graph.spi.LayoutData;

/**
 * LayoutData for the DagLayout
 *
 * @author Maik Niggemann
 */
class DagLayoutData implements LayoutData {

    private int layer;
    private int slot;
    private int unresolvedInDegree;
    private int ignoredLoopEdges;

    public DagLayoutData() {
        layer = 0;
        slot = 0;
        ignoredLoopEdges = 0;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public int getLayer() {
        return layer;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    public void setUnresolvedInDegree(int unresolvedInDegree) {
        this.unresolvedInDegree = unresolvedInDegree - ignoredLoopEdges;
    }

    public int getUnresolvedInDegree() {
        return unresolvedInDegree;
    }

    public void resolvePredecessor() {
        unresolvedInDegree--;
    }

    public boolean predecessorsResolved() {
        return unresolvedInDegree == 0;
    }

    public void ignoreLoopEdge() {
        this.ignoredLoopEdges++;
        resolvePredecessor();
    }
}
