package org.me.gcu.kerr_anthony_s2324906;

public class Impact {
    private String delaysType;

    private int lanesWorking;

    private int lanesUnavailable;

    public Impact(String delaysType, int lanesWorking, int lanesUnavailable){
        this.delaysType = delaysType;
        this.lanesWorking = lanesWorking;
        this.lanesUnavailable = lanesUnavailable;
    }

    public String getDelaysType() {
        return delaysType;
    }

    public int getLanesWorking() {
        return lanesWorking;
    }

    public int getLanesUnavailable() {
        return lanesUnavailable;
    }
}

