package dev.cg360.mc.nukkittables.types;

import cn.nukkit.item.Item;
import com.google.gson.*;
import dev.cg360.mc.nukkittables.Utility;
import dev.cg360.mc.nukkittables.context.RollContext;
import dev.cg360.mc.nukkittables.math.FloatRange;
import dev.cg360.mc.nukkittables.math.IntegerRange;
import dev.cg360.mc.nukkittables.types.entry.TableEntry;

import java.util.*;

public class LootTable {

    protected String type;
    protected TablePool[] pools;

    public LootTable(String type, TablePool... pools){
        this.type = type.toLowerCase().trim();
        this.pools = pools;
    }

    public Collection<Item> rollLootTable(RollContext context){
        List<Item> items = new ArrayList<>();
        for(TablePool pool: pools){
            if(Utility.compileConditions(pool.getConditions(), context)){
                items.addAll(pool.rollPool(context));
            }
        }
        return items;
    }

    public static Optional<LootTable> createLootTableFromString(JsonElement rootElement) {
        if(rootElement instanceof JsonObject){
            JsonObject rootObject = (JsonObject) rootElement;
            JsonElement tableTypeElement = rootObject.get("type");
            JsonElement poolsElement = rootObject.get("pools");

            if(poolsElement instanceof JsonArray){
                JsonArray poolsArray = (JsonArray) poolsElement;
                ArrayList<TablePool> pools = new ArrayList<>();

                for(JsonElement poolElement: poolsArray){
                    if(poolElement instanceof JsonObject){
                        JsonObject poolObject = (JsonObject) poolElement;

                        JsonElement conditionsElement = poolObject.get("conditions");
                        JsonElement functionsElement = poolObject.get("functions");
                        JsonElement rollsElement = poolObject.get("rolls");
                        JsonElement bonusRollsElement = poolObject.get("bonus_rolls");
                        JsonElement entriesElement = poolObject.get("entries");

                        if(entriesElement instanceof JsonArray){
                            JsonArray entriesArray = (JsonArray) entriesElement;
                            ArrayList<TableEntry> entries = new ArrayList<>();

                            for(JsonElement entryElement: entriesArray){
                                if(entryElement instanceof JsonObject){
                                    JsonObject entryObject = (JsonObject) entryElement;
                                    Utility.parseEntry(entryObject).ifPresent(entries::add);
                                }
                            }

                            if(entriesArray.size() > 0){
                                ArrayList<TableCondition> conditions = new ArrayList<>();
                                ArrayList<TableFunction> functions = new ArrayList<>();

                                if(conditionsElement instanceof JsonArray){
                                    JsonArray conditionsArray = (JsonArray) conditionsElement;
                                    for(JsonElement c: conditionsArray){
                                        if(c instanceof JsonObject){
                                            TableCondition.loadConditionFromJsonObject((JsonObject) c).ifPresent(conditions::add);
                                        }
                                    }
                                }

                                if(functionsElement instanceof JsonArray){
                                    JsonArray functionsArray = (JsonArray) functionsElement;
                                    for(JsonElement c: functionsArray){
                                        if(c instanceof JsonObject){
                                            TableFunction.loadFromJsonObject((JsonObject) c).ifPresent(functions::add);
                                        }
                                    }
                                }

                                TablePool pool = new TablePool(
                                        conditions.toArray(new TableCondition[0]),
                                        functions.toArray(new TableFunction[0]),
                                        entries.toArray(new TableEntry[0])
                                );

                                if(bonusRollsElement instanceof JsonPrimitive){
                                    JsonPrimitive bonusRollsPrimitive = (JsonPrimitive) bonusRollsElement;

                                    if(bonusRollsPrimitive.isNumber()){
                                        pool.fixedBonusRolls = bonusRollsPrimitive.getAsFloat();
                                    }
                                } else if (bonusRollsElement instanceof JsonObject){
                                    JsonObject bonusRollsObject = (JsonObject) bonusRollsElement;
                                    JsonElement minElement = bonusRollsObject.get("min");
                                    JsonElement maxElement = bonusRollsObject.get("max");

                                    if(minElement instanceof JsonPrimitive && maxElement instanceof JsonPrimitive){
                                        JsonPrimitive minPrimitive = (JsonPrimitive) minElement;
                                        JsonPrimitive maxPrimitive = (JsonPrimitive) maxElement;

                                        if(minPrimitive.isNumber() && maxPrimitive.isNumber()){
                                            pool.variableBonusRolls = new FloatRange(minPrimitive.getAsFloat(), maxPrimitive.getAsFloat());
                                        }
                                    }
                                }

                                if(rollsElement instanceof JsonPrimitive){
                                    JsonPrimitive rollsPrimitive = (JsonPrimitive) rollsElement;

                                    if(rollsPrimitive.isNumber()){
                                        pool.fixedRolls = rollsPrimitive.getAsInt();
                                        pools.add(pool);
                                    }
                                } else if (rollsElement instanceof JsonObject){
                                    JsonObject rollsObject = (JsonObject) rollsElement;
                                    JsonElement minElement = rollsObject.get("min");
                                    JsonElement maxElement = rollsObject.get("max");

                                    if(minElement instanceof JsonPrimitive && maxElement instanceof JsonPrimitive){
                                        JsonPrimitive minPrimitive = (JsonPrimitive) minElement;
                                        JsonPrimitive maxPrimitive = (JsonPrimitive) maxElement;

                                        if(minPrimitive.isNumber() && maxPrimitive.isNumber()){
                                            pool.variableRolls = new IntegerRange(minPrimitive.getAsInt(), maxPrimitive.getAsInt());
                                            pools.add(pool);
                                        }
                                    }
                                }

                            }
                        }
                    }
                }

                String tableType = "generic";
                if(tableTypeElement instanceof JsonPrimitive){
                    JsonPrimitive typePrimitive = (JsonPrimitive) tableTypeElement;

                    if(typePrimitive.isString()) {
                        tableType = typePrimitive.getAsString();
                    }
                }

                LootTable table = new LootTable(tableType, pools.toArray(new TablePool[0]));
                return Optional.of(table);
            }
        }
        //TODO:
        // - Load type (String)
        // - Load pools.
        //   - load conditions inside each pool.
        //   - load functions inside each pool.
        //   - Load entries inside each pool.
        return Optional.empty();
    }

    public String getType() { return type; }
    public TablePool[] getPools() { return pools; }

    @Override
    public String toString() {
        return "LootTable{" +
                "type='" + type + '\'' +
                ", pools=" + Arrays.toString(pools) +
                '}';
    }
}
