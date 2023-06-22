package dev.cg360.mc.nukkittables.conditions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.cg360.mc.nukkittables.context.RollContext;
import dev.cg360.mc.nukkittables.executors.TableConditionExecutor;

import java.util.concurrent.ThreadLocalRandom;

public class ConditionExecutorRandomChance extends TableConditionExecutor<RollContext> {

    @Override
    public boolean isConditionPassed0(RollContext context, JsonObject data) {
        JsonElement element = data.get("chance");
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
            return false;
        }
        return element.getAsDouble() < ThreadLocalRandom.current().nextDouble();
    }

    @Override
    public String getConditionType() {
        return "minecraft:random_chance";
    }
}
