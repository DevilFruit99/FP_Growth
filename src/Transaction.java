import java.util.ArrayList;
import java.util.List;

/**
 * Created by sushal on 4/16/16.
 */
public class Transaction {

    String transID;
    List<Item> items;

    public Transaction(String id){
        this.transID = id;
        items = new ArrayList<>();
    }

    public boolean equals(Object o) {
        return ((Transaction)o).getTransID().equalsIgnoreCase(this.getTransID());
    }


    public String getTransID() {
        return transID;
    }

    public void setTransID(String transID) {
        this.transID = transID;
    }

    public String toString(){
        return this.getTransID()+" "+this.getItems().size() +
                "items: "+this.getItems().toString();
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void addItem(Item item){
        this.items.add(item);
    }

    public Item get(int i) {
        return items.get(i);
    }


    public void insertItem(String itemName,int count) {
        this.items.add(0,new Item(itemName,count));
    }
}
