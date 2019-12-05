package edu.jsu.mcis.cs425.project2;

import java.util.HashMap;
import java.sql.SQLException;

public class BeanApplicant {
    
    private String username;
    private int userid;
    private String displayName;
    private String[] skills;
    private String[] jobs;
    
    public String[] getJobs() {
        return jobs;
    }
    
    public void setJobsList() {
        Database db = new Database();
        db.setJobsList(userid, jobs);
    }
    
    public void setUserInfo() throws SQLException {
        Database db = new Database();
        HashMap<String, String> userinfo = db.getUserInfo(username);
        userid = Integer.parseInt(userinfo.get("userid"));
        displayName = userinfo.get("displayname");
    }
    
    public String getSkillsList() throws SQLException {
        Database db = new Database();
        return ( db.getSkillsListAsHTML(userid) );
    }
    
    public void setSkillsList() {
        Database db = new Database();
        db.setSkillsList(userid, skills);
    }
    
    public String getJobsList() {
        Database db = new Database();
        return ( db.getJobsListAsHTML(userid) );
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return userid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setId(int id) {
        this.userid = id;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }    
}