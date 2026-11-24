package org.me.gcu.kerr_anthony_s2324906;

public class Situation {
    private String id;
    private String category;
    private String recordType;
    private String detailType;
    private String comment;
    private String road;
    private String direction;
    private String startTime;
    private String endTime;

   private RoadLocation location;
   private Impact impact;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getDetailType() {
        return detailType;
    }

    public void setDetailType(String detailType) {
        this.detailType = detailType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public RoadLocation getLocation() {
        return location;
    }

    public void setLocation(RoadLocation location) {
        this.location = location;
    }

    public Impact getImpact() {
        return impact;
    }

    public void setImpact(Impact impact) {
        this.impact = impact;
    }

    @Override
    public String toString() {
        return "Situation{" +
                "id='" + id + '\'' +
                ", category='" + category + '\'' +
                ", road='" + road + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
