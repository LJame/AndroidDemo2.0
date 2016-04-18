package com.hardrubic.entity.response;


import com.hardrubic.entity.db.Project;
import com.hardrubic.entity.db.Team;

import java.util.ArrayList;
import java.util.List;

public class ProjectListResponse {

    private List<Project> projects = new ArrayList<>();
    private List<Team> teams = new ArrayList<>();
    private Long timestamp;

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
