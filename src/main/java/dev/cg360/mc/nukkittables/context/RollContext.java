package dev.cg360.mc.nukkittables.context;

import dev.cg360.mc.nukkittables.types.entry.TableEntry;

public interface RollContext {

    default float getLuck() {
        return TableEntry.DEFAULT_LUCK;
    }
}
