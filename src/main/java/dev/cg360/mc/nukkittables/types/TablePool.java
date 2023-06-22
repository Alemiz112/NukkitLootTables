package dev.cg360.mc.nukkittables.types;

import cn.nukkit.item.Item;
import cn.nukkit.math.MathHelper;
import dev.cg360.mc.nukkittables.Utility;
import dev.cg360.mc.nukkittables.context.RollContext;
import dev.cg360.mc.nukkittables.math.FloatRange;
import dev.cg360.mc.nukkittables.math.IntegerRange;
import dev.cg360.mc.nukkittables.types.entry.TableEntry;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TablePool {

    protected TableCondition[] conditions;
    protected TableFunction[] functions;

    protected int fixedRolls;
    protected IntegerRange variableRolls;

    protected float fixedBonusRolls;
    protected FloatRange variableBonusRolls;

    protected TableEntry[] entries;

    public TablePool(int rolls, int bonusRolls, TableEntry... entries){ this(new TableCondition[0], new TableFunction[0], rolls, bonusRolls, entries); }
    public TablePool(TableCondition[] conditions, TableFunction[] functions, int rolls, int bonusRolls, TableEntry... entries){
        this(conditions, functions, entries);
        this.fixedRolls = rolls;
        this.fixedBonusRolls = bonusRolls;
    }
    public TablePool(IntegerRange rolls, int bonusRolls, TableEntry... entries){ this(new TableCondition[0], new TableFunction[0], rolls, bonusRolls, entries); }
    public TablePool(TableCondition[] conditions, TableFunction[] functions, IntegerRange rolls, int bonusRolls, TableEntry... entries){
        this(conditions, functions, entries);
        this.variableRolls = rolls;
        this.fixedBonusRolls = bonusRolls;
    }
    public TablePool(int rolls, FloatRange bonusRolls, TableEntry... entries){ this(new TableCondition[0], new TableFunction[0], rolls, bonusRolls, entries); }
    public TablePool(TableCondition[] conditions, TableFunction[] functions, int rolls, FloatRange bonusRolls, TableEntry... entries){
        this(conditions, functions, entries);
        this.fixedRolls = rolls;
        this.variableBonusRolls = bonusRolls;
    }
    public TablePool(IntegerRange rolls, FloatRange bonusRolls, TableEntry... entries){ this(new TableCondition[0], new TableFunction[0], rolls, bonusRolls, entries); }
    public TablePool(TableCondition[] conditions, TableFunction[] functions, IntegerRange rolls, FloatRange bonusRolls, TableEntry... entries){
        this(conditions, functions, entries);
        this.variableRolls = rolls;
        this.variableBonusRolls = bonusRolls;
    }

    protected TablePool(TableCondition[] conditions, TableFunction[] functions, TableEntry[] entries){
        this.conditions = conditions;
        this.functions = functions;

        this.fixedRolls = 0;
        this.variableRolls = null;

        this.fixedBonusRolls = 0;
        this.variableBonusRolls = null;

        this.entries = entries;
    }

    protected List<Item> rollPoolOnce(RollContext context, int maxWeight, List<TableEntry> passedEntries){
        int selection = maxWeight > 0 ? ThreadLocalRandom.current().nextInt(maxWeight) : 0;
        int cumulativeWeightChecked = 1;
        for(TableEntry entry: passedEntries){
            if(selection <= (cumulativeWeightChecked + entry.getModifiedWeight(context.getLuck()))){
                return entry.rollEntry(context);
            }
            cumulativeWeightChecked += entry.getModifiedWeight();
        }
        return Collections.emptyList();
    }

    public Collection<Item> rollPool(RollContext context){
        if(Utility.compileConditions(conditions, context)) {
            ArrayList<TableEntry> passedEntries = getPassedEntries(context);
            int maxWeight = 0;
            for(TableEntry entry : passedEntries) maxWeight += entry.getModifiedWeight(context.getLuck());

            if(passedEntries.size() > 0) {
                int rollAmount = getRandomRolls(context.getLuck());
                List<Item> fullitems = new ArrayList<>();

                for (int i = 0; i < rollAmount; i++) {
                    List<Item> items = rollPoolOnce(context, maxWeight, passedEntries);
                    for(Item item: items){
                        Item finalItem = Utility.applyFunctions(functions, item, context);
                        fullitems.add(finalItem);
                    }
                }
                return fullitems;
            }
        }
        return Collections.emptyList();
    }

    protected ArrayList<TableEntry> getPassedEntries(RollContext context){
        ArrayList<TableEntry> passedEntries = new ArrayList<>();
        for(TableEntry entry : entries){
            if(Utility.compileConditions(entry.getConditions(), context)) passedEntries.add(entry);
        }
        return passedEntries;
    }

    public int getRandomBaseRolls(){
        if(variableRolls == null){
            return fixedRolls;
        } else {
            return Utility.getRandomIntBetweenInclusiveBounds(variableRolls.getMin(), variableRolls.getMax());
        }
    }

    public float getRandomBonusRolls(float luck){
        if(variableBonusRolls == null){
            return fixedBonusRolls * luck;
        } else {
            float difference = variableBonusRolls.getMax() - variableBonusRolls.getMin();
            float minoffset = difference * ThreadLocalRandom.current().nextFloat();
            return variableRolls.getMin()+minoffset;
        }
    }

    public int getRandomRolls(float luck){
        return Math.max(MathHelper.floor_float_int(getRandomBaseRolls() - getRandomBonusRolls(luck)), 0);
    }

    public TableCondition[] getConditions() { return conditions; }
    public TableFunction[] getFunctions() { return functions; }

    public IntegerRange getBaseRolls() { return variableRolls == null ? new IntegerRange(fixedRolls, fixedRolls) : variableRolls; }
    public FloatRange getBonusRolls() { return variableBonusRolls == null ? new FloatRange(fixedBonusRolls, fixedBonusRolls) : variableBonusRolls; }

    public TableEntry[] getEntries() { return entries; }

    @Override
    public String toString() {
        return "TablePool{" +
                "conditions=" + Arrays.toString(conditions) +
                ", functions=" + Arrays.toString(functions) +
                ", fixedRolls=" + fixedRolls +
                ", variableRolls=" + variableRolls +
                ", fixedBonusRolls=" + fixedBonusRolls +
                ", variableBonusRolls=" + variableBonusRolls +
                ", entries=" + Arrays.toString(entries) +
                '}';
    }
}
