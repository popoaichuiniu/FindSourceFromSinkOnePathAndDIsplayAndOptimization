/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication1;

import java.util.ArrayList;
import java.util.HashSet;
import org.gephi.graph.api.Node;

/**
 *
 * @author jacy
 */
public class StronglyConnectedComponent {

    private int label;//从1开始为有效值

    private HashSet<StronglyConnectedComponent> outSccs = new HashSet<StronglyConnectedComponent>();
    private HashSet<StronglyConnectedComponent> inSccs = new HashSet<StronglyConnectedComponent>();
    private int inDegree = 0;
    private int outDegree = 0;

    private int fixedInDegree = 0;
    private int fixedOutDegree = 0;

    private int layer = 0;//从1开始为有效值
    private ArrayList<Node> arr = new ArrayList<Node>();

    public StronglyConnectedComponent(int label) {
        this.label = label;
    }

    public void addNode(Node node) {
        arr.add(node);
    }

    public void addInScc(StronglyConnectedComponent inScc) {
        if (inScc == null) {
            System.out.println("in添加的元素为空，程序出现错误");
        } else if (inSccs.add(inScc)) {
            inDegree++;
            fixedInDegree++;
        }
    }

    public void addOutScc(StronglyConnectedComponent outScc) {
        if (outScc == null) {
            System.out.println("out添加的元素为空，程序出现错误");
        }
        if (outSccs.add(outScc)) {
            outDegree++;
            fixedOutDegree++;
        }

    }

    public int getSize() {
        return arr.size();
    }

    public HashSet<StronglyConnectedComponent> getOutSccs() {
        return outSccs;
    }

    public HashSet<StronglyConnectedComponent> getInSccs() {
        return inSccs;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public int getLabel() {
        return label;
    }

    public int getInDegree() {
        return inDegree;
    }

    public int getOutDegree() {
        return outDegree;
    }

    public ArrayList<Node> getArr() {
        return arr;
    }

    public void decInDegree() {
        inDegree--;

    }

    public void decOutDegree() {
        outDegree--;
    }

    public int getFixedInDegree() {
        return fixedInDegree;
    }

    public int getFixedOutDegree() {
        return fixedOutDegree;
    }

}
