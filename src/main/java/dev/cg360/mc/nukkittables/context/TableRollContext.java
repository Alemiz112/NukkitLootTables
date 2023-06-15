package dev.cg360.mc.nukkittables.context;

import cn.nukkit.level.Location;

public class TableRollContext implements RollContext {

    protected Location origin;

    public TableRollContext(Location origin){
        this.origin = origin;
    }

    public Location getOrigin() {
        return this.origin;
    }

}
