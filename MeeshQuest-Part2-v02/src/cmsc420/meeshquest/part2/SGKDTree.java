package cmsc420.meeshquest.part2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;






public class SGKDTree {

	private City maxCity = new City("Max", Integer.MAX_VALUE, Integer.MAX_VALUE, 0, "color");
	private Node root;
	private Document resultsDoc;
	private int n, m;

	private CompareXY compXY = new CompareXY();
	private CompareYX compYX = new CompareYX();

	private double log() {
		return (Math.log((double) m)) / (Math.log((double) 1.5));
	}

	public SGKDTree(Document resultsDoc) {
		this.resultsDoc = resultsDoc;
		root = null;
		n = 0;
		m = 0;
	}

	// -----------------------------------------------------------
	// Tree Utilities
	// ----------------------------------------------------------

	public boolean isEmpty() {
		return root == null;
	}

	public void clear() {
		root = null;
		n = 0;
		m = 0;
	}

	public int size() {
		return n;
	}

	int getSize(Node p) {
		if (p.isExternal())
			return 1;
		else
			return ((InternalNode) p).size;
	}

	int getHeight(Node p) {
		if (p.isExternal())
			return 0;
		else
			return ((InternalNode) p).height;
	}

	public City find(City c) {
		if (root == null) {
			return null;
		} else {
			return root.find(c);
		}
	}

	public void insert(City c) {
		if(root == null) {
			root = new ExternalNode(c);
		} else {
			root = root.insert(c);
		}
		n++;
		m++;
		if(getHeight(root) > log()) {
			root = root.rebalance(c);
		}
	}

	public void delete(City c) {
		if(root != null) {
			if(getSize(root) == 1) {
				root = null;
			} else if(getSize(root) == 2) {
				InternalNode p = (InternalNode) root;
				if(p.splitDim == 0) {
					if(compXY.compare(c, p.splitter) <= 0) {
						root = p.rightChild;
					} else {
						root = p.leftChild;
					}
				} else {
					if(compYX.compare(c, p.splitter) <= 0) {
						root = p.rightChild;
					} else {
						root = p.leftChild;
					}
				}
			} else {
				root.delete(c);
				n--;
				if(2*n < m) {
					root = rebuild(root);
					m = n;
				}
			}
		}
	}

	public ArrayList<City> entryList() {
		ArrayList<City> list = new ArrayList<City>();
		if(root != null) {
			root.entryList(list);
		}
		return list;
	}

	public void print(Element element) {
		Element out = resultsDoc.createElement("KdTree");
		element.appendChild(out);
		if (root != null)
			root.print(out);
	}

	Node rebuild(Node p) {
		if(p.isExternal()) {
			return p;
		} else {
			ArrayList<City> list = new ArrayList<City>(); // place to store points
			p.entryList(list); // generate the list
			Node t = buildTree(list);
			return t;
		}
	}

	Node buildTree(List<City> list) {
		int k = list.size();
		if (k == 0) { // no points at all
			return null;
		} else if (k == 1) { // a single point
			return new ExternalNode(list.get(0));
		} else {
			int dim = findDim(list);
			if(dim == 0) {
				Collections.sort(list, compXY);
			} else {
				Collections.sort(list, compYX);
			}
			int m = (int) Math.ceil((float) k / 2); // size of left subtree
			City splitter = list.get(m - 1); // splitter value
			// recursively build left and right subtrees
			Node left = buildTree(list.subList(0, m));
			Node right = buildTree(list.subList(m, k));
			// combine the lists under median (median goes into left subtree)
			InternalNode p = new InternalNode(splitter, left, right, dim);
			p.updateSizeAndHeight(); // update p's information
			return p;
		}
	}

	private int findDim(List<City> list) {
		int maxx, minx, maxy, miny;
		maxx = maxy = -1;
		minx = miny = Integer.MAX_VALUE;
		for(City c : list) {
			if(c.getX() > maxx) {
				maxx = c.getX();
			}
			if(c.getX() < minx) {
				minx = c.getX();
			}
			if(c.getY() > maxy) {
				maxy = c.getY();
			}
			if(c.getY() < miny) {
				miny = c.getY();
			}
		}
		int dx = maxx - minx;
		int dy = maxy - miny;
		if(dx >= dy) {
			return 0;
		} else {
			return 1;
		}
	}
	
	public City nearNeigh(City q) {
		if (root == null) {
			return null;
		}
		if(root.isExternal()) {
			return ((ExternalNode)root).city;
		}
		return root.nearNeighbor(q, new Cell(entryList()), maxCity);
	}

	abstract class Node {
		abstract boolean isExternal();
		abstract Node insert(City c);
		abstract Node delete(City c);
		abstract City find(City c);
		abstract Node rebalance(City c);
		abstract void entryList(List<City> list);
		abstract void print(Element result);
		abstract City nearNeighbor(City c, Cell cell, City best);
	}

	// -------------------------------------------------
	// Internal Node
	// -------------------------------------------------------
	class InternalNode extends Node {
		public City splitter;
		public int height, size, splitDim;
		public Node leftChild, rightChild;

		public InternalNode(City c, Node left, Node right, int splitDim) {
			this.splitter = c;
			this.splitDim = splitDim;
			this.leftChild = left;
			this.rightChild = right;
			updateSizeAndHeight();
		}

		City find(City c) {
			if(splitDim == 0) {
				if(compXY.compare(c, this.splitter) <= 0) {
					return this.leftChild.find(c);
				} else {
					return this.rightChild.find(c);
				}
			} else {
				if(compYX.compare(c, this.splitter) <= 0) {
					return this.leftChild.find(c);
				} else {
					return this.rightChild.find(c);
				}
			}
		}

		Node insert(City c) { 
			if(this.splitDim == 0) {
				if(compXY.compare(c, this.splitter) <= 0) {
					this.leftChild = leftChild.insert(c);
					updateSizeAndHeight();
				} else {
					this.rightChild = rightChild.insert(c);
					updateSizeAndHeight();
				}
			} else {
				if(compYX.compare(c, this.splitter) <= 0) {
					this.leftChild = leftChild.insert(c);
					updateSizeAndHeight();
				} else {
					this.rightChild = rightChild.insert(c);
					updateSizeAndHeight();
				}
			}
			return this;
		}

		Node rebalance(City c) {
			if(this.splitDim == 0) {
				if (compXY.compare(c, this.splitter) <= 0) { // pt is less or equal
					if (2 * getSize(this) < 3 * getSize(this.leftChild)) { // too unbalanced?
						return rebuild(this); // this is the scapegoat
					} else { // balance is okay
						this.leftChild = this.leftChild.rebalance(c); // continue the search
						updateSizeAndHeight(); // update this node's information
						return this;
					}
				} else { // pt is larger
					if (2 * getSize(this) < 3 * getSize(this.rightChild)) { // too unbalanced?
						return rebuild(this); // this is the scapegoat
					} else { // balance is okay
						this.rightChild = this.rightChild.rebalance(c); // continue the search
						updateSizeAndHeight(); // update this node's information
						return this;
					}
				}
			} else {
				if (compYX.compare(c, this.splitter) <= 0) { // pt is less or equal
					if (2 * getSize(this) < 3 * getSize(this.leftChild)) { // too unbalanced?
						return rebuild(this); // this is the scapegoat
					} else { // balance is okay
						this.leftChild = this.leftChild.rebalance(c); // continue the search
						updateSizeAndHeight(); // update this node's information
						return this;
					}
				} else { // pt is larger
					if (2 * getSize(this) < 3 * getSize(this.rightChild)) { // too unbalanced?
						return rebuild(this); // this is the scapegoat
					} else { // balance is okay
						this.rightChild = this.rightChild.rebalance(c); // continue the search
						updateSizeAndHeight(); // update this node's information
						return this;
					}
				}
			}
		}

		@Override
		void print(Element element) {
			Element internal = resultsDoc.createElement("internal");
			internal.setAttribute("splitDim", ""+this.splitDim);
			internal.setAttribute("x", ""+this.splitter.getX());
			internal.setAttribute("y", ""+this.splitter.getY());
			element.appendChild(internal);

			this.leftChild.print(internal);
			this.rightChild.print(internal);
		}

		@Override
		boolean isExternal() {
			return false;
		}

		@Override
		Node delete(City c) {
			if(splitDim == 0) {
				if(compXY.compare(c, this.splitter) <= 0) {
					this.leftChild = this.leftChild.delete(c);
					if(this.leftChild == null) {
						return this.rightChild;
					} else {

						updateSizeAndHeight();
						return this;
					}
				} else {
					this.rightChild = this.rightChild.delete(c);
					if(this.rightChild == null) {
						return this.leftChild;
					} else {
						updateSizeAndHeight();
						return this;
					}
				}
			} else {
				if(compYX.compare(c, this.splitter) <= 0) {
					this.leftChild = this.leftChild.delete(c);
					if(this.leftChild == null) {

						return this.rightChild;
					} else {
						updateSizeAndHeight();
						return this;
					}
				} else {
					this.rightChild = this.rightChild.delete(c);
					if(this.rightChild == null) {
						return this.leftChild;
					} else {
						updateSizeAndHeight();
						return this;
					}
				}
			}
		}

		void updateSizeAndHeight() {
			this.size = getSize(this.leftChild) + getSize(this.rightChild);
			this.height = 1 + Math.max(getHeight(this.leftChild), getHeight(this.rightChild));
		}

		@Override
		void entryList(List<City> list) {
			this.leftChild.entryList(list);
			this.rightChild.entryList(list);
		}

		@Override
		City nearNeighbor(City q, Cell cell, City best) {
			Cell leftCell = cell.leftPart(this.splitDim, q);
			Cell rightCell = cell.rightPart(this.splitDim, q);
			
			if(splitDim == 0) {
				if(q.getX() <= this.splitter.getX()) {
					best = this.leftChild.nearNeighbor(q, leftCell, best);
					if(rightCell.distanceTo(q) < eucDist(q, best)) {
						best = this.rightChild.nearNeighbor(q, rightCell, best);
					}
				} else {
					best = this.rightChild.nearNeighbor(q, rightCell, best);
					if(leftCell.distanceTo(q) < eucDist(q, best)) {
						best = this.leftChild.nearNeighbor(q, leftCell, best);
					}
				}
			} else {
				if(q.getY() <= this.splitter.getY()) {
					best = this.leftChild.nearNeighbor(q, leftCell, best);
					if(rightCell.distanceTo(q) < eucDist(q, best)) {
						best = this.rightChild.nearNeighbor(q, rightCell, best);
					}
				} else {
					best = this.rightChild.nearNeighbor(q, rightCell, best);
					if(leftCell.distanceTo(q) < eucDist(q, best)) {
						best = this.leftChild.nearNeighbor(q, leftCell, best);
					}
				}
			}
			return best;
		}
	}


	// ---------------------------------------------------
	// External Node
	// ---------------------------------------------------

	class ExternalNode extends Node {
		public City city;

		public ExternalNode(City city) {
			this.city = city;
		}

		Node insert(City c) {
			ArrayList<City> list = new ArrayList<City>(); // array list for points
			list.add(c); // add points to list
			list.add(this.city);

			return buildTree(list);
		} // insertion at external node

		@Override
		void print(Element element) {
			Element external = resultsDoc.createElement("external");
			external.setAttribute("name", this.city.getName());
			external.setAttribute("x", ""+this.city.getX());
			external.setAttribute("y", ""+this.city.getY());
			element.appendChild(external);
		}

		@Override
		City find(City c) {
			if(compXY.compare(c, this.city) == 0) {
				return this.city;
			} else {
				return null;
			}
		}

		@Override
		boolean isExternal() {
			return true;
		}

		@Override
		Node delete(City c) {
			if(compXY.compare(c, this.city) == 0) {
				return null;
			} else {
				System.out.println("Someone made a fucky wucky");
				return null;
			}
		}



		Node rebalance(City c) {
			assert (false); // should never get here
			return null;
		}

		@Override
		void entryList(List<City> list) {
			list.add(this.city);

		}

		@Override
		City nearNeighbor(City q, Cell cell, City best) {
			if(best.getX() == Integer.MAX_VALUE) {
				return this.city;
			} else if(eucDist(this.city, q) < eucDist(best, q)) {
				return this.city;
			} else {
				return best;
			}
		}

	}

	class CompareXY implements Comparator<City> {

		@Override
		public int compare(City c1, City c2) {
			if(c1.getX() < c2.getX()) {
				return -1;
			} else if(c1.getX() > c2.getX()) {
				return 1;
			} else {
				if(c1.getY() < c2.getY()) {
					return -1;
				} else if(c1.getY() > c2.getY()) {
					return 1;
				} else {
					return 0;
				}
			}
		}



	}

	class CompareYX implements Comparator<City> {

		@Override
		public int compare(City c1, City c2) {
			if(c1.getY() < c2.getY()) {
				return -1;
			} else if(c1.getY() > c2.getY()) {
				return 1;
			} else {
				if(c1.getX() < c2.getX()) {
					return -1;
				} else if(c1.getX() > c2.getX()) {
					return 1;
				} else {
					return 0;
				}
			}
		}



	}

	private double eucDist(City city, City q) {
		// TODO Auto-generated method stub
		return Math.sqrt(      ((city.getX() - q.getX())*(city.getX() - q.getX()))  +   ((city.getY() - q.getY())*(city.getY() - q.getY()))         );
		
	}



}
