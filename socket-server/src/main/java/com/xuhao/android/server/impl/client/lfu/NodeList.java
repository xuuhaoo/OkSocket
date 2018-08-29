package com.xuhao.android.server.impl.client.lfu;

import java.util.*;

public class NodeList {

    Node mHead;

    Node mTail;

    int mLength;

    public NodeList() {
        mHead = null;
        mTail = null;
        mLength = 0;
    }

    public void prepend(Node node) {
        if (mHead == null) {
            mTail = node;
            node.mNext = null;
        } else {
            node.mNext = mHead;
            mHead.mPrev = node;
        }
        mHead = node;
        node.mPrev = null;
        mLength++;
    }

    public void append(Node node) {
        if (mTail == null) {
            prepend(node);
        } else {
            mTail.mNext = node;
            node.mNext = null;
            node.mPrev = mTail;
            mTail = node;
            mLength++;
        }
    }

    public void insertAfter(Node position, Node node) {
        if (position == mTail) {
            append(node);
        } else {
            node.mNext = position.mNext;
            node.mPrev = position;
            position.mNext = node;
            node.mNext.mPrev = node;
            mLength++;
        }
    }

    public void remove(Node node) {
        if (node == mTail && node == mHead) { /* single node in LinkedList */
            mHead = null;
            mTail = null;
        } else if (node == mTail) {
            mTail = mTail.mPrev;
            mTail.mNext = null;
        } else if (node == mHead) {
            mHead = mHead.mNext;
            mHead.mPrev = null;
        } else {
            node.mNext.mPrev = node.mPrev;
            node.mPrev.mNext = node.mNext;
        }
        node.mNext = null;
        node.mPrev = null;
        mLength--;
    }


    public void printList() {
        Node walk = mHead;
        while (walk != null) {
            System.out.print("[" + walk + "] -> ");
            walk = walk.mNext;
        }
        System.out.println();
    }


    public static void test() {
        NodeList list = new NodeList();
        ArrayList<FrequencyNode> alist = new ArrayList<FrequencyNode>();
        alist.add(new FrequencyNode(0));
        alist.add(new FrequencyNode(1));
        alist.add(new FrequencyNode(2));
        alist.add(new FrequencyNode(3));
        alist.add(new FrequencyNode(4));
        alist.add(new FrequencyNode(5));
        alist.add(new FrequencyNode(6));
        list.append(alist.get(0));
        list.append(alist.get(1));
        list.prepend(alist.get(2));
        list.prepend(alist.get(3));
        list.printList();
        list.insertAfter(alist.get(3), alist.get(4));
        list.printList();
        list.insertAfter(alist.get(2), alist.get(5));
        list.printList();
        list.insertAfter(alist.get(1), alist.get(6));
        list.printList();
        list.remove(alist.get(1));
        list.remove(alist.get(6));
        list.remove(alist.get(3));
        list.printList();

        /* Output should be */
    	/*
    	[3] -> [2] -> [0] -> [1] -> 
    	[3] -> [4] -> [2] -> [0] -> [1] -> 
    	[3] -> [4] -> [2] -> [5] -> [0] -> [1] -> 
    	[3] -> [4] -> [2] -> [5] -> [0] -> [1] -> [6] -> 
    	[4] -> [2] -> [5] -> [0] -> 
    	*/
    }

}

