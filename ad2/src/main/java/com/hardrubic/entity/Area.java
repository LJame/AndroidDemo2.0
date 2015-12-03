package com.hardrubic.entity;

/**
 * Created by heng on 12/3/15.
 */
public class Area {
    private Long areaid;
    private String name;
    private Long fatherid;
    private Integer type;   //0:省 1：城市 2：县区 3：街道

    public Area(Long areaid, String name, Long fatherid, Integer type) {
        this.areaid = areaid;
        this.name = name;
        this.fatherid = fatherid;
        this.type = type;
    }

    public Long getAreaid() {
        return areaid;
    }

    public void setAreaid(Long areaid) {
        this.areaid = areaid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getFatherid() {
        return fatherid;
    }

    public void setFatherid(Long fatherid) {
        this.fatherid = fatherid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}
