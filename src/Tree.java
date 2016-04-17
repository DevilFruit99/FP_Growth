import java.util.ArrayList;
import java.util.List;

/**
 * Created by susha on 3/19/2016.
 */
public class Tree {
    public Node root;
    public List<Item> header;
    public Tree(String rootName) {
        root = new Node();
        root.itemName = rootName;
    }

    public boolean isNotEmpty() {
        return root.children.size()>0;
    }


    public static class Node {
        //public StarNode data;

        public String itemName;
        public int count;
        public Node parent;
        public Node nodeLink;
        public List<Node> children = new ArrayList<Node>();


        public Node(){}
        public Node(String itemName, int count){
            this.itemName = itemName;
            this.count = count;
        }

        public boolean equals(Object o) {
            if(o instanceof Node ) {
                return (itemName.equalsIgnoreCase(((Node) o).itemName));
            } else if ( o instanceof Item) {
                return (itemName.equalsIgnoreCase(((Item)o).getItemName()));
            }
            return false;
        }

        public boolean isLeaf() {
            return children.size() == 0;
        }

        public boolean hasSibling() {
            if (this.parent == null)
                return false;
            return this.parent.children.size() - 1 > this.parent.children.indexOf(this);
        }

        public Node getNextSibling() {
            return this.parent.children.get(this.parent.children.indexOf(this) + 1);
        }

        public String toString() {
            return itemName +" "+count;
        }

        public void incrementCount() {
            this.count++;
        }

        public void addChild(Node node) {
            this.children.add(node);
            node.parent = this;
        }
    }


}
