package cmsc420.meeshquest.part2;



import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class BSTree {

	private BSTNode root;
	private Document resultsDoc;
	
	public BSTree(Document resultsDoc) {
		this.resultsDoc = resultsDoc;
		root = null;
	}
	
	public boolean isEmpty() {
		return root == null;
	}
	
	public void clear() {
		root = null;
	}
	
	public void insert(City city) {

		if(root == null) {
			root = new BSTNode(city);
		} else {
			BSTNode p = root;
			while(p != null) {
				if(city.getName().compareTo(p.city.getName()) < 0) {
					if(p.leftChild == null) {
						p.leftChild = new BSTNode(city);
						p = null;
					} else {
						p = p.leftChild;
					}
				} else {
					if(p.rightChild == null) {
						p.rightChild = new BSTNode(city);
						p = null;
					} else {
						p = p.rightChild;
					}
				}
			}
		}
	}
	
	public boolean containsCity(String cityName) {
		BSTNode p = root;
		while(p != null) {
			if(p.city.getName().compareTo(cityName) == 0) {
				return true;
			} else if(cityName.compareTo(p.city.getName()) < 0) {
				p = p.leftChild;
			} else {
				p = p.rightChild;
			}
		}
		return false;
	}
	
	public Element print() {
		return preorderTraverse(root);
	}
	private Element preorderTraverse(BSTNode p) {
		Element node = resultsDoc.createElement("node");
		node.setAttribute("name", p.city.getName());
		node.setAttribute("x", ""+p.city.getX());
		node.setAttribute("y", ""+p.city.getY());
		if(p.leftChild != null) {
			node.appendChild(preorderTraverse(p.leftChild));
		}
		if(p.rightChild != null) {
			node.appendChild(preorderTraverse(p.rightChild));
		}
		return node;

	}
	
	public void delete(String cityName) {
		root = deleteBSTNode(cityName, root);
	}
	
	private BSTNode deleteBSTNode(String cityName, BSTNode p) {
		
		if(cityName.compareTo(p.city.getName()) < 0) {
			p.leftChild = deleteBSTNode(cityName, p.leftChild);
		} else if(cityName.compareTo(p.city.getName()) > 0) {
			p.rightChild = deleteBSTNode(cityName, p.rightChild);
		} else {
			if(p.leftChild == null) {
				return p.rightChild;
			} else if(p.rightChild == null) {
				return p.leftChild;
			} else {
				p.city = getMin(p.rightChild);
				p.rightChild = deleteBSTNode(p.city.getName(), p.rightChild);
			}
		}
		return p;
	}

	private City getMin(BSTNode p) {
		while(p.leftChild != null) {
			p = p.leftChild;
		}
		return p.city;
	}
	
	public City getCity(String cityName) {
		return getCityHelp(cityName, root);
	}
	
	private City getCityHelp(String cityName, BSTNode p) {
		if(p.city.getName().equals(cityName)) {
			return p.city;
		} else if(cityName.compareTo(p.city.getName()) < 0) {
			return getCityHelp(cityName, p.leftChild);
		} else {
			return getCityHelp(cityName, p.rightChild);
		}
	}
	
	public void inorderBST(ArrayList<City> a) {
		inorderBSTHelp(root, a);
	}
	
	private void inorderBSTHelp(BSTNode p, ArrayList<City> a) {
		if(p == null) {
			return;
		} else {
			inorderBSTHelp(p.leftChild, a);
			a.add(p.city);
			inorderBSTHelp(p.rightChild, a);
		}
		
	}
	
	class BSTNode {
		public City city;
		public BSTNode leftChild, rightChild;
		
		public BSTNode(City city) {
			this.city = city;
			leftChild = null;
			rightChild = null;
		}
	}
}
