package dev.cg360.mc.nukkittables.types.entry;

import cn.nukkit.item.Item;
import cn.nukkit.item.RuntimeItemMapping;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.network.protocol.ProtocolInfo;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.cg360.mc.nukkittables.context.RollContext;
import dev.cg360.mc.nukkittables.types.TableCondition;
import dev.cg360.mc.nukkittables.types.TableFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class TableEntryItem extends TableEntry implements NamedTableEntry {

    protected String name;

    public TableEntryItem(){ }
    public TableEntryItem(String type, String id, int weight, int quality, TableCondition[] conditions, TableFunction[] functions) {
        super(type, weight, quality, conditions, functions);
        this.name = id;
    }

    @Override
    public ArrayList<Item> gatherEntryItems(RollContext context) {
        if(name == null) return new ArrayList<>();
        ArrayList<Item> item = new ArrayList<>();
        getItem().ifPresent(item::add);
        return item;
    }

    private Optional<Item> getItem(){
        Item item;
        try {
            RuntimeItemMapping mapping = RuntimeItems.getMapping(ProtocolInfo.CURRENT_PROTOCOL);
            RuntimeItemMapping.LegacyEntry entry = mapping.fromIdentifier(this.name);
            if (entry == null) {
                item = Item.fromString(this.name);
            } else {
                item = Item.get(entry.getLegacyId(), entry.getDamage(), 1);
            }
        } catch (Exception err){
            return Optional.empty();
        }
       return Optional.of(item);
    }

    @Override
    protected boolean loadCustomPropertiesFromJson(JsonObject object) {
        JsonElement nameElement = object.get("name");


        if(nameElement instanceof JsonPrimitive){
            JsonPrimitive namePrimitive = (JsonPrimitive) nameElement;

            if(!(namePrimitive.isNumber() || namePrimitive.isString())) return false;
            this.name = namePrimitive.getAsString();

            return true;
        }

        return false;
    }

    @Override
    public String getName() { return name; }

    @Override
    public String toString() {
        return "TableEntryItem{" +
                "name='" + name + '\'' +
                ", DEFAULT_LUCK=" + DEFAULT_LUCK +
                ", type='" + type + '\'' +
                ", conditions=" + Arrays.toString(conditions) +
                ", weight=" + weight +
                ", quality=" + quality +
                '}';
    }
}
