package com.hitszcerc.astar;
import java.util.*;

/**
 * 
 * Astar类，初始化需要地图和行列参数
 * 搜索返回状态码
 * @author Administrator
 *
 */
public class AStar {
	private int[][] map;// 地图(1可通过 0不可通过)
	private List<Node> openList;// 开启列表
	private List<Node> closeList;// 关闭列表
	private final int COST_STRAIGHT = 10;// 垂直方向或水平方向移动的路径评分
//	private final int COST_DIAGONAL = 14;// 斜方向移动的路径评分
	
	/**
	 * 定义8种行走方式编码
	 */
	
	private final int GO_UP=0;
	private final int GO_DOWN=1;
	private final int GO_RIGHT=2;
	private final int GO_LEFT=3;
	private final int GO_UP_RIGHT=4;
	private final int GO_DOWN_RIGHT=5;
	private final int GO_UP_LEFT=6;
	private final int GO_DOWN_LEFT=7;
	
	
	private int row;// 行
	private int column;// 列
	public static List<Node> resultList;//搜索得到的路径列表，是Node列表

	/**
	 * 构造函数需要地图和行列数，在构造函数中实例化开启关闭列表
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
	 *  查找坐标（-1：错误，0：没找到，1：找到了）
	 *  查找路径函数，返回状态码
	 * @param 起始节点横坐标
	 * @param 起始节点纵坐标
	 * @param 终节点横坐标
	 * @param 终节点纵坐标
	 * @return 搜索结果状态码
	 */
	public int search(int x1, int y1, int x2, int y2) {
		//起始点脱离地图
		if (x1 < 0 || x1 >= row || x2 < 0 || x2 >= row || y1 < 0
				|| y1 >= column || y2 < 0 || y2 >= column) {
			return -1;
		}
		//起始点在障碍点上
		if (map[x1][y1] == 1 || map[x2][y2] == 1) {
			return -1;
		}
		Node sNode = new Node(x1, y1, null);
		Node eNode = new Node(x2, y2, null);
		//开启列表
		openList.add(sNode);
		//结果列表
		resultList = search(sNode, eNode);
		
		//结果列表为空
		if (resultList.size() == 0) {
			return 0;
		}
		//如果不为空则将节点设置为-1
		/*for (Node node : resultList) {
			map[node.getX()][node.getY()] = -1;
		}*/
		return 1;
	}

	// 查找核心算法
	private List<Node> search(Node sNode, Node eNode) {
		List<Node> resultList = new ArrayList<Node>();
		boolean isFind = false;
		Node node = null;
		while (openList.size() > 0) {
			// 取出开启列表中最低F值，即第一个存储的值的F为最低的
			node = openList.get(0);
			// 判断是否找到目标点

			if (node.getX() == eNode.getX() && node.getY() == eNode.getY()) {
				isFind = true;
				break;
			}
			// 上
			if ((node.getY() - 1) >= 0) {
				checkPath(node.getX(), node.getY() - 1, node, eNode,
						COST_STRAIGHT);
			}
			// 下
			if ((node.getY() + 1) < column) {
				checkPath(node.getX(), node.getY() + 1, node, eNode,
						COST_STRAIGHT);
			}
			// 左
			if ((node.getX() - 1) >= 0) {
				checkPath(node.getX() - 1, node.getY(), node, eNode,
						COST_STRAIGHT);
			}
			// 右
			if ((node.getX() + 1) < row) {
				checkPath(node.getX() + 1, node.getY(), node, eNode,
						COST_STRAIGHT);
			}
		/*	// 左上
			if ((node.getX() - 1) >= 0 && (node.getY() - 1) >= 0) {
				checkPath(node.getX() - 1, node.getY() - 1, node, eNode,
						COST_DIAGONAL);
			}
			// 左下
			if ((node.getX() - 1) >= 0 && (node.getY() + 1) < column) {
				checkPath(node.getX() - 1, node.getY() + 1, node, eNode,
						COST_DIAGONAL);
			}

			// 右上
			if ((node.getX() + 1) < row && (node.getY() - 1) >= 0) {
				checkPath(node.getX() + 1, node.getY() - 1, node, eNode,
						COST_DIAGONAL);
			}
			// 右下
			if ((node.getX() + 1) < row && (node.getY() + 1) < column) {
				checkPath(node.getX() + 1, node.getY() + 1, node, eNode,
						COST_DIAGONAL);
			}*/

			// 从开启列表中删除
			// 添加到关闭列表中
			closeList.add(openList.remove(0));
			// 开启列表中排序，把F值最低的放到最底端
			Collections.sort(openList, new NodeFComparator());
		}
		if (isFind) {
			getPath(resultList, node);
		}
		return resultList;
	}

	// 查询此路是否能走通
	private boolean checkPath(int x, int y, Node parentNode, Node eNode,
			int cost) {
		Node node = new Node(x, y, parentNode);
		// 查找地图中是否能通过
		if (map[x][y] == 1) {
			closeList.add(node);
			return false;
		}
		// 查找关闭列表中是否存在
		if (isListContains(closeList, x, y) != -1) {
			return false;
		}

		// 查找开启列表中是否存在
		int index = -1;
		if ((index = isListContains(openList, x, y)) != -1) {
			// G值是否更小，即是否更新G，F值
			if ((parentNode.getG() + cost) < openList.get(index).getG()) {
				node.setParentNode(parentNode);
				countG(node, eNode, cost);
				countF(node);
				openList.set(index, node);
			}
		} else {
			// 添加到开启列表中
			node.setParentNode(parentNode);
			count(node, eNode, cost);
			openList.add(node);
		}
		return true;
	}

	// 集合中是否包含某个元素(-1：没有找到，否则返回所在的索引)
	private int isListContains(List<Node> list, int x, int y) {
		for (int i = 0; i < list.size(); i++) {
			Node node = list.get(i);
			if (node.getX() == x && node.getY() == y) {
				return i;
			}
		}
		return -1;
	}

	// 从终点往返回到起点，递归，node目标点往回找到
	private void getPath(List<Node> resultList, Node node) {
		if (node.getParentNode() != null) {
			getPath(resultList, node.getParentNode());
		}
		resultList.add(node);
	}
	

	// 计算G,H,F值
	private void count(Node node, Node eNode, int cost) {
		countG(node, eNode, cost);
		countH(node, eNode);
		countF(eNode);
	}

	// 计算G值 o
	private void countG(Node node, Node eNode, int cost) {
		if (node.getParentNode() == null) {
			node.setG(cost);
		} else {
			node.setG(node.getParentNode().getG() + cost);
		}
	}

	// 计算H值
	private void countH(Node node, Node eNode) {
		node.setH(length(node, eNode));
	}

	// 计算F值
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
	 * 打印路径
	 * 比较两个节点生成方向信息
	 * @return 
	 */
	public List<Integer>  generateComand(){
		List<Integer> result=new ArrayList<Integer>();
		int r=-1;
		for (Iterator<Node> iter=resultList.iterator();iter.hasNext();){
			
			Node a = (Node)iter.next();
			Node p = a.getParentNode();
			if(p!=null){
				//行
				int h1 = a.getX()-p.getX();
				//列
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