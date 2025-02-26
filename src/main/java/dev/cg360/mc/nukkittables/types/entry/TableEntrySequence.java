package dev.cg360.mc.nukkittables.types.entry;

import cn.nukkit.item.Item;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.cg360.mc.nukkittables.Utility;
import dev.cg360.mc.nukkittables.context.RollContext;
import dev.cg360.mc.nukkittables.types.TableCondition;
import dev.cg360.mc.nukkittables.types.TableFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableEntrySequence extends TableEntry implements ParentTableEntry {

    protected TableEntry[] children;

    public TableEntrySequence(){ }
    public TableEntrySequence(String type, TableEntry[] children, int weight, int quality, TableCondition[] conditions, TableFunction[] functions) {
        super(type, weight, quality, conditions, functions);
        this.children = children;
    }

    @Override
    public List<Item> gatherEntryItems(RollContext context) {
        ArrayList<Item> collectedItems = new ArrayList<>();
        for(TableEntry entry: children){
            // Overriding the default roll handling
            // Conditions and functions being within the generic code were a mistake.
            // But they're nice there ig.

            if(Utility.compileConditions(entry.conditions, context)){
                List<Item> items = entry.gatherEntryItems(context);
                for(Item item: items){
                    collectedItems.add(Utility.applyFunctions(functions, item, context));
                }
            }
        }

        return collectedItems;
    }

    @Override
    protected boolean loadCustomPropertiesFromJson(JsonObject object) {
        JsonElement childrenElement = object.get("children");

        if(childrenElement instanceof JsonArray){
            JsonArray childrenArray = (JsonArray) childrenElement;

            ArrayList<TableEntry> foundChildren = new ArrayList<>();
            for(JsonElement childElement: childrenArray){

                if(childElement instanceof JsonObject){
                    JsonObject childObject = (JsonObject) childElement;
                    Utility.parseEntry(childObject).ifPresent(foundChildren::add);
                }
            }

            if(foundChildren.size() > 0){
                this.children = foundChildren.toArray(new TableEntry[0]);
                return true;
            }
        }

        return false;
    }

    @Override
    public TableEntry[] getChildren() { return children; }

    @Override
    public String toString() {
        return "TableEntrySequence{" +
                "children='" + Arrays.toString(children) + '\'' +
                ", DEFAULT_LUCK=" + DEFAULT_LUCK +
                ", type='" + type + '\'' +
                ", conditions=" + Arrays.toString(conditions) +
                ", weight=" + weight +
                ", quality=" + quality +
                '}';
    }
}
