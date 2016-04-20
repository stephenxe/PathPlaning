package com.hitszcerc.astar;
import java.util.*;

/**
 * 
 * Astar�࣬��ʼ����Ҫ��ͼ�����в���
 * ��������״̬��
 * @author Administrator
 *
 */
public class AStar {
	private int[][] map;// ��ͼ(1��ͨ�� 0����ͨ��)
	private List<Node> openList;// �����б�
	private List<Node> closeList;// �ر��б�
	private final int COST_STRAIGHT = 10;// ��ֱ�����ˮƽ�����ƶ���·������
//	private final int COST_DIAGONAL = 14;// б�����ƶ���·������
	
	/**
	 * ����8�����߷�ʽ����
	 */
	
	private final int GO_UP=0;
	private final int GO_DOWN=1;
	private final int GO_RIGHT=2;
	private final int GO_LEFT=3;
	private final int GO_UP_RIGHT=4;
	private final int GO_DOWN_RIGHT=5;
	private final int GO_UP_LEFT=6;
	private final int GO_DOWN_LEFT=7;
	
	
	private int row;// ��
	private int column;// ��
	public static List<Node> resultList;//�����õ���·���б���Node�б�

	/**
	 * ���캯����Ҫ��ͼ�����������ڹ��캯����ʵ���������ر��б�
	 * @param map
	 * @param row
	 * @param column
	 */
	public AStar(int[][] map, int row, int column) {
		this.map = map;
		this.row = row;
		this.column = column;
		openList = new ArrayList<Node>();
		closeList = new ArrayList<Node>();
		
	}

	/**
	 *  �������꣨-1������0��û�ҵ���1���ҵ��ˣ�
	 *  ����·������������״̬��
	 * @param ��ʼ�ڵ������
	 * @param ��ʼ�ڵ�������
	 * @param �սڵ������
	 * @param �սڵ�������
	 * @return �������״̬��
	 */
	public int search(int x1, int y1, int x2, int y2) {
		//��ʼ�������ͼ
		if (x1 < 0 || x1 >= row || x2 < 0 || x2 >= row || y1 < 0
				|| y1 >= column || y2 < 0 || y2 >= column) {
			return -1;
		}
		//��ʼ�����ϰ�����
		if (map[x1][y1] == 1 || map[x2][y2] == 1) {
			return -1;
		}
		Node sNode = new Node(x1, y1, null);
		Node eNode = new Node(x2, y2, null);
		//�����б�
		openList.add(sNode);
		//����б�
		resultList = search(sNode, eNode);
		
		//����б�Ϊ��
		if (resultList.size() == 0) {
			return 0;
		}
		//�����Ϊ���򽫽ڵ�����Ϊ-1
		/*for (Node node : resultList) {
			map[node.getX()][node.getY()] = -1;
		}*/
		return 1;
	}

	// ���Һ����㷨
	private List<Node> search(Node sNode, Node eNode) {
		List<Node> resultList = new ArrayList<Node>();
		boolean isFind = false;
		Node node = null;
		while (openList.size() > 0) {
			// ȡ�������б������Fֵ������һ���洢��ֵ��FΪ��͵�
			node = openList.get(0);
			// �ж��Ƿ��ҵ�Ŀ���

			if (node.getX() == eNode.getX() && node.getY() == eNode.getY()) {
				isFind = true;
				break;
			}
			// ��
			if ((node.getY() - 1) >= 0) {
				checkPath(node.getX(), node.getY() - 1, node, eNode,
						COST_STRAIGHT);
			}
			// ��
			if ((node.getY() + 1) < column) {
				checkPath(node.getX(), node.getY() + 1, node, eNode,
						COST_STRAIGHT);
			}
			// ��
			if ((node.getX() - 1) >= 0) {
				checkPath(node.getX() - 1, node.getY(), node, eNode,
						COST_STRAIGHT);
			}
			// ��
			if ((node.getX() + 1) < row) {
				checkPath(node.getX() + 1, node.getY(), node, eNode,
						COST_STRAIGHT);
			}
		/*	// ����
			if ((node.getX() - 1) >= 0 && (node.getY() - 1) >= 0) {
				checkPath(node.getX() - 1, node.getY() - 1, node, eNode,
						COST_DIAGONAL);
			}
			// ����
			if ((node.getX() - 1) >= 0 && (node.getY() + 1) < column) {
				checkPath(node.getX() - 1, node.getY() + 1, node, eNode,
						COST_DIAGONAL);
			}

			// ����
			if ((node.getX() + 1) < row && (node.getY() - 1) >= 0) {
				checkPath(node.getX() + 1, node.getY() - 1, node, eNode,
						COST_DIAGONAL);
			}
			// ����
			if ((node.getX() + 1) < row && (node.getY() + 1) < column) {
				checkPath(node.getX() + 1, node.getY() + 1, node, eNode,
						COST_DIAGONAL);
			}*/

			// �ӿ����б���ɾ��
			// ��ӵ��ر��б���
			closeList.add(openList.remove(0));
			// �����б������򣬰�Fֵ��͵ķŵ���׶�
			Collections.sort(openList, new NodeFComparator());
		}
		if (isFind) {
			getPath(resultList, node);
		}
		return resultList;
	}

	// ��ѯ��·�Ƿ�����ͨ
	private boolean checkPath(int x, int y, Node parentNode, Node eNode,
			int cost) {
		Node node = new Node(x, y, parentNode);
		// ���ҵ�ͼ���Ƿ���ͨ��
		if (map[x][y] == 1) {
			closeList.add(node);
			return false;
		}
		// ���ҹر��б����Ƿ����
		if (isListContains(closeList, x, y) != -1) {
			return false;
		}

		// ���ҿ����б����Ƿ����
		int index = -1;
		if ((index = isListContains(openList, x, y)) != -1) {
			// Gֵ�Ƿ��С�����Ƿ����G��Fֵ
			if ((parentNode.getG() + cost) < openList.get(index).getG()) {
				node.setParentNode(parentNode);
				countG(node, eNode, cost);
				countF(node);
				openList.set(index, node);
			}
		} else {
			// ��ӵ������б���
			node.setParentNode(parentNode);
			count(node, eNode, cost);
			openList.add(node);
		}
		return true;
	}

	// �������Ƿ����ĳ��Ԫ��(-1��û���ҵ������򷵻����ڵ�����)
	private int isListContains(List<Node> list, int x, int y) {
		for (int i = 0; i < list.size(); i++) {
			Node node = list.get(i);
			if (node.getX() == x && node.getY() == y) {
				return i;
			}
		}
		return -1;
	}

	// ���յ������ص���㣬�ݹ飬nodeĿ��������ҵ�
	private void getPath(List<Node> resultList, Node node) {
		if (node.getParentNode() != null) {
			getPath(resultList, node.getParentNode());
		}
		resultList.add(node);
	}
	

	// ����G,H,Fֵ
	private void count(Node node, Node eNode, int cost) {
		countG(node, eNode, cost);
		countH(node, eNode);
		countF(eNode);
	}

	// ����Gֵ o
	private void countG(Node node, Node eNode, int cost) {
		if (node.getParentNode() == null) {
			node.setG(cost);
		} else {
			node.setG(node.getParentNode().getG() + cost);
		}
	}

	// ����Hֵ
	private void countH(Node node, Node eNode) {
		node.setH(length(node, eNode));
	}

	// ����Fֵ
	private void countF(Node node) {
		node.setF(node.getG() + node.getH());
	}
	
	private int length(Node a, Node b ){
		int X1=Math.abs (a.getX()-b.getX());
        int Y1=Math.abs (a.getY()-b.getY());

        int l;

        if(X1>Y1){
            l=14*Y1+10*(X1-Y1);
        }else{
            l=14*X1+10*(Y1-X1);
        }
        return l;
	}
	
	/**
	 * ��ӡ·��
	 * �Ƚ������ڵ����ɷ�����Ϣ
	 * @return 
	 */
	public List<Integer>  generateComand(){
		List<Integer> result=new ArrayList<Integer>();
		int r=-1;
		for (Iterator<Node> iter=resultList.iterator();iter.hasNext();){
			
			Node a = (Node)iter.next();
			Node p = a.getParentNode();
			if(p!=null){
				//��
				int h1 = a.getX()-p.getX();
				//��
				int s1 = a.getY()-p.getY();
				if(s1==1){
					switch (h1) {
					case -1:
						r=GO_UP_RIGHT;
						System.out.println("GO_UP_RIGHT");
						break;
					case 0:
						r=GO_RIGHT;
						System.out.println("GO_RIGHT");
						break;
					case 1:
						r=GO_DOWN_RIGHT;
						System.out.println("GO_DOWN_RIGHT");
						break;
					default:
						break;
					}
				}else if(s1==0){
					switch (h1) {
					case -1:
						r=GO_UP;
						System.out.println("GO_UP");
						break;
					case 1:
						r=GO_DOWN;
						System.out.println("GO_DOWN");
						break;
					default:
						break;
					}
				}else if(s1==-1){
					switch (h1) {
					case -1:
						r=GO_UP_LEFT;
						System.out.println("GO_UP_LEFT");
						break;
					case 0:
						r=GO_LEFT;
						System.out.println("GO_LEFT");
						break;
					case 1:
						r=GO_DOWN_LEFT;
						System.out.println("GO_DOWN_LEFT");
						break;
					default:
						break;
					}
				}
				if(r!=-1)
				result.add(r);
				
			}
			System.out.println("aX:"+a.getX()+"  "+"aY:"+a.getY());
		}
		return result;
	}
}