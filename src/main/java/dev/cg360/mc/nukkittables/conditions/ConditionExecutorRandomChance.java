package dev.cg360.mc.nukkittables.conditions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.cg360.mc.nukkittables.context.RollContext;
import dev.cg360.mc.nukkittables.executors.TableConditionExecutor;

public class ConditionExecutorRandomChance extends TableConditionExecutor<RollContext> {

    @Override
    public boolean isConditionPassed0(RollContext context, JsonObject data) {
        JsonElement element = data.get("chance");
        if(element instanceof JsonPrimitive){
            JsonPrimitive primitive = (JsonPrimitive) element;
            if(primitive.isBoolean()){
                return primitive.getAsBoolean();
            }
        }
        return true;
    }

    @Override
    public String getConditionType() {
        return "minecraft:random_chance";
    }
}
