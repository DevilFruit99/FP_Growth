import FileManager.CsVParser;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import java.io.File;
import java.util.*;

/**
 * Created by sushal on 4/16/16.
 */
public class FP_Growth {
    protected static int min_sup =2;
    protected static boolean output = true;
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        File inputData = new File(args[0]);
        CsVParser myParser = new CsVParser(inputData.getAbsolutePath());
        List<Transaction> transTable = populateTransactionTable(myParser);
        List<Item> F = populateFreqItems(transTable);

        List<Item> L = (ArrayList)((ArrayList)F).clone();
        Collections.sort(L, (o1, o2) -> o1.getCount() - o2.getCount());
        Collections.reverse(L);
        Tree fpTree = new Tree("null");
        fpTree.header = L;

        for (Transaction transaction: transTable) {
            //sort elements in transTable according to order in L
            Collections.sort(transaction.getItems(), (o1, o2) -> {
                int count1 = L.get(L.indexOf(o1)).count;
                int count2 = L.get(L.indexOf(o2)).count;
                int diff = count2 - count1; //for descending order
                if (diff!=0)
                    return diff;
                return o1.getItemName().compareTo(o2.getItemName());
            });

            //Insert Tree function
            insertTree(L,transaction, fpTree.root, 0);

        }

        //mining
        fpGrowth(fpTree,fpTree.root);
        long endTime = System.nanoTime();
        System.out.println("done... "+(endTime-startTime)/1000000+"ms.");


    }

    private static void fpGrowth(Tree fpTree, Tree.Node node) {
        if (hasOnePath(node)) { //checking if it has one prefix path
            List<Item> path = new ArrayList<>();
            Tree.Node ptr = node;
            while (ptr.children.size()!=0){
                path.add(new Item(ptr.children.get(0).itemName, ptr.children.get(0).count));
                ptr = ptr.children.get(0);
            }
            ICombinatoricsVector initialSet = Factory.createVector(path);
            Generator<Item> gen = Factory.createSubSetGenerator(initialSet);
            for(ICombinatoricsVector<Item> beta:gen){
                //output result of beta (Union) node
                if(beta.getSize()!=0) {
                    int support = Integer.MAX_VALUE;
                    for (Item i : beta) { //find min count
                        if (support > i.getCount())
                            support = i.getCount();
                    }
                    if (output) System.out.println(beta.getVector().toString() + "," + node.itemName.substring(4) + ". count = " + support);
                }
            }
        } else {
            for (int i=fpTree.header.size()-1;i>=0;i--){
                //generate pattern β = ai ∪ α with support count = ai.support count;
                Item betaItem = fpTree.header.get(i);
                if (output)
                    if(betaItem.count>=min_sup)
                    System.out.println(betaItem.getItemName()+","+node.itemName.substring(4)+". count = "+betaItem.count);
                //Construct β's conditional pattern base
                List<Transaction> conditionalPatternBase = new ArrayList<>();
                Tree.Node ptr = betaItem.nodeLink;
                while(ptr!=null){
                    Tree.Node ptr2 = ptr.parent;
                    Transaction onePath = new Transaction(""+ptr2.hashCode());
                    while(ptr2.parent!=null){
                        onePath.insertItem(ptr2.itemName,ptr.count);
                        ptr2 = ptr2.parent;
                    }
                    conditionalPatternBase.add(onePath);
                    ptr = ptr.nodeLink;
                }
                //Construct β's conditional FP_tree Treeβ
                List<Item> f = populateFreqItems(conditionalPatternBase);
                List<Item> l = (ArrayList)((ArrayList)f).clone();
                Collections.sort(l, (o1, o2) -> o1.getCount() - o2.getCount());
                Collections.reverse(l);
                Tree treeβ = new Tree("null"+betaItem.itemName);
                //prune items that don't satisfy minsup from conditionalPatternBase
                for(Transaction transaction:conditionalPatternBase) {
                    if(transaction.items.size()!=0)
                        insertTree2(l, transaction, treeβ.root, 0);
                }
                treeβ.header = l;
                if(treeβ.isNotEmpty()){
                    fpGrowth(treeβ,treeβ.root);
                }

            }

        }
    }

    private static boolean hasOnePath(Tree.Node node) {
        Tree.Node ptr = node;
        while (ptr.children.size()!=0){
            if(ptr.children.size()!=1){
                return false;
            }
            ptr = ptr.children.get(0);
        }
        return true;
    }

    private static void insertTree(List<Item> L, Transaction transaction, Tree.Node N, int startPos) {
        Tree.Node node;
        if(N.children.contains(transaction.get(startPos))) {
            //N.incrementCount();
            int i = N.children.indexOf(transaction.get(startPos));
            node = N.children.get(i);
            node.incrementCount();
        } else {
            Item i = transaction.get(startPos);
            node = new Tree.Node(i.getItemName(),1);
            N.addChild(node);
            for (Item j:L){ //insert new node link
                if(j.getItemName().equalsIgnoreCase(i.getItemName())){
                    j.insertNodeLink(node);
                    break;
                }
            }
        }
        if (startPos<(transaction.getItems().size()-1)) {
            insertTree(L,transaction, node, startPos+1);
        }
    }

    private static void insertTree2(List<Item> L, Transaction transaction, Tree.Node N, int startPos) {
        Tree.Node node=N;
        if(N.children.contains(transaction.get(startPos))) {
            //N.incrementCount();
            int i = N.children.indexOf(transaction.get(startPos));
            node = N.children.get(i);
            node.incrementCount();
        } else /*if(L.get(L.indexOf(transaction.get(startPos))).count>=min_sup) */{
            Item i = transaction.get(startPos);
            int count = L.get(L.indexOf(i)).count;
            if (count >= min_sup) {
                node = new Tree.Node(i.getItemName(), 1);
                N.addChild(node);
                for (Item j : L) { //insert new node link
                    if (j.getItemName().equalsIgnoreCase(i.getItemName())) {
                        j.insertNodeLink(node);
                        break;
                    }
                }
            }
        }
        if (startPos<(transaction.getItems().size()-1)) {
            insertTree2(L,transaction, node, startPos+1);
        }
    }

    private static List<Transaction> populateTransactionTable(CsVParser myParser) {
        List<Transaction> table = new ArrayList<>();
        myParser.next();
        while(myParser.hasNext()){
            List<String> row = myParser.next();
            String id = row.get(0);
            String item = row.get(1);
            int itemIndex = table.indexOf(new Transaction(id));
            if (itemIndex >= 0) {
                table.get(itemIndex).addItem(new Item(item, 1));
            } else {
                //add new transaction
                Transaction transaction = new Transaction(id);
                Item newItem = new Item(item, 1);
                transaction.addItem(newItem);
                table.add(transaction);
            }
        }
        return table;
    }

    private static List<Item> populateFreqItems(List<Transaction> transactionTable) {
        List<Item> listOfItems = new ArrayList<>();
        //dont consider header
        for (Transaction t: transactionTable) {
            for (Item item: t.getItems()) {
                int index = listOfItems.indexOf(item);
                if (index >= 0) {
                    listOfItems.get(index).incrementCount();
                } else {
                    Item newItem = new Item(item.getItemName(), 1);
                    listOfItems.add(newItem);
                }
            }
        }
        return listOfItems;
    }
}
