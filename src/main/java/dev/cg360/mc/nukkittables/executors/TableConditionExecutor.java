package dev.cg360.mc.nukkittables.executors;

import com.google.gson.JsonObject;
import dev.cg360.mc.nukkittables.context.RollContext;
import io.netty.util.internal.TypeParameterMatcher;

public abstract class TableConditionExecutor<I> {

    private final TypeParameterMatcher matcher;

    public TableConditionExecutor() {
        this.matcher = TypeParameterMatcher.find(this, TableConditionExecutor.class, "I");
    }

    public boolean allowContext(RollContext context) {
        return this.matcher.match(context);
    }

    public final boolean isConditionPassed(RollContext context, JsonObject data) {
        return this.allowContext(context) && this.isConditionPassed0((I) context, data);
    }

    protected abstract boolean isConditionPassed0(I context, JsonObject data);

    public abstract String getConditionType();
}
