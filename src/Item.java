/**
 * Created by sushal on 4/16/16.
 */
public class Item {
    String itemName;
    int count;
    Tree.Node nodeLink;
    public Item(String itemName, int i) {
        this.itemName = itemName;
        this.count = i;
    }

    public Item(String itemName) {
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }



    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void incrementCount () {
        this.count ++;
    }



    public boolean equals(Object o) {
        if (o instanceof  Item) {
            return ((Item)o).getItemName().equalsIgnoreCase(this.itemName);
        } else if (o instanceof Tree.Node) {
            return ((Tree.Node)o).itemName.equalsIgnoreCase(this.itemName);
        }
        else return false;
    }

    public String toString() {
        return this.getItemName();
    }
    public void insertNodeLink(Tree.Node node) {
        if (this.nodeLink==null) {
            this.nodeLink = node;
        } else {
            Tree.Node nl = this.nodeLink;
            this.nodeLink = node;
            node.nodeLink = nl;
        }
    }
}
