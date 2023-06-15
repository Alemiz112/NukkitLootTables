package dev.cg360.mc.nukkittables.types.entry;

import cn.nukkit.item.Item;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.cg360.mc.nukkittables.Utility;
import dev.cg360.mc.nukkittables.context.RollContext;
import dev.cg360.mc.nukkittables.types.TableCondition;
import dev.cg360.mc.nukkittables.types.TableFunction;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class TableEntry {

    public final float DEFAULT_LUCK = 0;

    protected String type;

    protected TableCondition[] conditions;
    protected TableFunction[] functions;

    protected int weight;
    protected int quality;

    protected TableEntry() { }
    public TableEntry(String type, int weight, TableCondition[] conditions, TableFunction[] functions){ this(type, weight, 1, conditions, functions); }
    public TableEntry(String type, int weight, int quality, TableCondition[] conditions, TableFunction[] functions) {
        this.type = type.toLowerCase();
        this.conditions = conditions;
        this.functions = functions;
        this.weight = weight;
        this.quality = quality;
    }

    public final ArrayList<Item> rollEntry(RollContext context){
        if(Utility.compileConditions(conditions, context)){
            ArrayList<Item> items = gatherEntryItems(context);
            ArrayList<Item> newItems = new ArrayList<>();
            for(Item item: items){
                newItems.add(Utility.applyFunctions(functions, item, context));
            }
            return newItems;
        }
        return new ArrayList<>();
    }

    public abstract ArrayList<Item> gatherEntryItems(RollContext context);

    public final boolean loadPropertiesFromJson(JsonObject entryObject){
        JsonElement elementType = entryObject.get("type");
        JsonElement elementConditions = entryObject.get("conditions");
        JsonElement functionsElement = entryObject.get("functions");
        JsonElement elementWeight = entryObject.get("weight");
        JsonElement elementQuality = entryObject.get("quality");

        if(elementType instanceof JsonPrimitive && elementWeight instanceof JsonPrimitive){
            JsonPrimitive primitiveType = (JsonPrimitive) elementType;
            JsonPrimitive primitiveWeight = (JsonPrimitive) elementWeight;

            if(primitiveType.isString() && primitiveWeight.isNumber()) {
                ArrayList<TableCondition> approvedConditions = new ArrayList<>();
                int q = 0;

                if(elementConditions instanceof JsonArray){
                    JsonArray arrayConditions = (JsonArray) elementConditions;
                    for(JsonElement condition: arrayConditions){
                        if(condition instanceof JsonObject){
                            TableCondition.loadConditionFromJsonObject((JsonObject) condition).ifPresent(approvedConditions::add);
                        }
                    }
                }

                ArrayList<TableFunction> funcs = new ArrayList<>();

                if(functionsElement instanceof JsonArray) {
                    JsonArray functionsArray = (JsonArray) functionsElement;

                    for(JsonElement f: functionsArray){
                        if(f.isJsonObject()){
                            JsonObject func = (JsonObject) f;
                            TableFunction.loadFromJsonObject(func).ifPresent(funcs::add);
                        }
                    }
                }

                if(elementQuality instanceof JsonPrimitive){
                    JsonPrimitive primitiveQuality = (JsonPrimitive) elementQuality;
                    if(primitiveQuality.isNumber()){
                        q = primitiveQuality.getAsNumber().intValue();
                    }
                }

                this.type = elementType.getAsString().toLowerCase();
                this.conditions = approvedConditions.toArray(new TableCondition[0]);
                this.functions = funcs.toArray(new TableFunction[0]);
                this.weight = primitiveWeight.getAsNumber().intValue();
                this.quality = q;

                return loadCustomPropertiesFromJson(entryObject);
            }
        }
        return false;
    }

    protected abstract boolean loadCustomPropertiesFromJson(JsonObject object);

    public String getType() { return type; }
    public TableCondition[] getConditions() { return conditions; }
    public TableFunction[] getFunctions() { return functions; }
    public int getBaseWeight() { return weight; }
    public int getQuality() { return quality; }

    public int getModifiedWeight(){ return getModifiedWeight(DEFAULT_LUCK); }
    public int getModifiedWeight(float luck){
        return Math.max((int) Math.floor(weight + (quality * luck)), 0);
    }

    public void setBaseWeight(int weight) { this.weight = weight; }
    public void setQuality(int quality) { this.quality = quality; }

    @Override
    public String toString() {
        return "TableEntry{" +
                "DEFAULT_LUCK=" + DEFAULT_LUCK +
                ", type='" + type + '\'' +
                ", conditions=" + Arrays.toString(conditions) +
                ", functions=" + Arrays.toString(functions) +
                ", weight=" + weight +
                ", quality=" + quality +
                '}';
    }
}
