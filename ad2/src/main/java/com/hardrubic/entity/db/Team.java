package com.hardrubic.entity.db;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "TEAM".
 */
public class Team {

    private Long id;
    /** Not-null value. */
    private String name;
    private Long parent_id;
    private Long updated;
    private Long deleted;

    public Team() {
    }

    public Team(Long id) {
        this.id = id;
    }

    public Team(Long id, String name, Long parent_id, Long updated, Long deleted) {
        this.id = id;
        this.name = name;
        this.parent_id = parent_id;
        this.updated = updated;
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(String name) {
        this.name = name;
    }

    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
    }

    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }

    public Long getDeleted() {
        return deleted;
    }

    public void setDeleted(Long deleted) {
        this.deleted = deleted;
    }

}