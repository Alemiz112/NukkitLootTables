package dev.cg360.mc.nukkittables.math;

public class IntegerRange {

    protected int min;
    protected int max;

    public IntegerRange (int min, int max){
        if(min <= max) {
            this.min = min;
            this.max = max;
        } else {
            //Inverse for if someone has them the wrong way round.
            this.min = max;
            this.max = min;
        }
    }

    public boolean isWithinRange(int value){ return isWithinRange(value, true, true); }
    public boolean isWithinRange(int value, boolean inclusiveMin, boolean inclusiveMax){
        boolean minCheck = inclusiveMin ? value >= min : value > min;
        boolean maxCheck = inclusiveMax ? value <= max : value < max;
        return minCheck && maxCheck;
    }

    public int getMin() { return min; }
    public int getMax() { return max; }
}
