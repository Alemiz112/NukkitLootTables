package dev.cg360.mc.nukkittables.conditions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.cg360.mc.nukkittables.context.RollContext;
import dev.cg360.mc.nukkittables.executors.TableConditionExecutor;
import dev.cg360.mc.nukkittables.types.TableCondition;

import java.util.Optional;

public class ConditionExecutorInverted extends TableConditionExecutor<RollContext> {

    @Override
    public boolean isConditionPassed0(RollContext context, JsonObject data) {
        JsonElement conditionElement = data.get("term");
        if(conditionElement instanceof JsonObject){
            JsonObject conditionObject = (JsonObject) conditionElement;
            Optional<TableCondition> pc = TableCondition.loadConditionFromJsonObject(conditionObject);
            if(pc.isPresent()){
                return !pc.get().isConditionPassed(context);
            }
        }
        return true;
    }

    @Override
    public String getConditionType() {
        return "minecraft:inverted";
    }
}
