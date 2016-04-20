package com.hitszcerc.astar;
/**
 * �ڵ�����Ҫ������ �������Լ����ڵ�
 * @author Administrator
 *
 */
public class Node {
	private int x;// X����
	private int y;// Y����
	private Node parentNode;// ���ڵ�
	private int g;// ��ǰ�㵽�����ƶ��ķ�
	private int h;// ��ǰ�㵽�յ���ƶ��ķѣ��������پ���|x1-x2|+|y1-y2|(�����ϰ���)
	private int f;// f=g+h

	public Node(int x, int y, Node parentNode) {
		this.x = x;
		this.y = y;
		this.parentNode = parentNode;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Node getParentNode() {
		return parentNode;
	}

	public void setParentNode(Node parentNode) {
		this.parentNode = parentNode;
	}

	public int getG() {
		return g;
	}

	public void setG(int g) {
		this.g = g;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public int getF() {
		return f;
	}

	public void setF(int f) {
		this.f = f;
	}
}

