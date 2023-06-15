package dev.cg360.mc.nukkittables.executors;

import cn.nukkit.item.Item;
import com.google.gson.JsonObject;
import dev.cg360.mc.nukkittables.Utility;
import dev.cg360.mc.nukkittables.context.RollContext;
import dev.cg360.mc.nukkittables.types.TableCondition;

public abstract class TableFunctionExecutor {

    public final Item applyFunction(Item item, RollContext context, TableCondition[] conditions, JsonObject data){ //TODO: And conditions
        return Utility.compileConditions(conditions, context) ? applyFunctionToItem(item, data) : item;
    }

    protected abstract Item applyFunctionToItem(Item item, JsonObject data);

    public abstract String getFunctionType();
}
