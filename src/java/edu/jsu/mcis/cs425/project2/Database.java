package edu.jsu.mcis.cs425.project2;



import java.sql.ResultSetMetaData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.simple.JSONArray;


import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Database {
    
    String sessID;
    
    
    public HashMap getUserInfo(String username) throws SQLException {
        
        HashMap<String, String> results = null;
       
        
        try { 
        
            Connection conn = getConnection();
            
            String query = "SELECT * FROM  user WHERE username = ?";
            PreparedStatement prepstatement = conn.prepareStatement(query);
            prepstatement.setString(1, username);
            
            boolean hasresults = prepstatement.execute();
            
            if (hasresults){
               
                HashMap hm = new HashMap<>();
                
                ResultSet resultset = prepstatement.getResultSet();
                
                if (resultset.next()){
                    
                   String id = String.valueOf(resultset.getInt("id"));
                   String displayName = resultset.getString("displayname");
                   results = new HashMap<>();
                   results.put("id", id);
                   results.put("displayname", displayName);                    
                }                
            }
            return results;
        }
       finally{}          
    }
    
    Connection getConnection() {
        
        Connection conn = null;
        
        try {
            
            Context envContext = new InitialContext();
            Context initContext  = (Context)envContext.lookup("java:/comp/env");
            DataSource ds = (DataSource)initContext.lookup("jdbc/db_pool");
            conn = ds.getConnection();  
        }        
        catch (Exception bigsad) { bigsad.printStackTrace(); }
        
        return conn;
    }
    public String getSkillsListAsHTML(int userid) throws SQLException {

        StringBuilder skills = new StringBuilder();
        String skillsList;
        
        try {
            Connection conn = getConnection();
            
            String query = "SELECT skills.*, a.userid FROM skills LEFT JOIN ( SELECT * FROM applicants_to_skills WHERE userid = 1) AS a ON skills.id = a.skillsid; ";
            
            PreparedStatement pstatement = conn.prepareStatement(query);
            
            boolean hasresults = pstatement.execute();
            
            if (hasresults) {
                ResultSet resultSet = pstatement.getResultSet();
                
                while (resultSet.next()) {
                    String description = resultSet.getString("description");
                    int id = resultSet.getInt("id");
                    int user =resultSet.getInt("userid");
                    
                    
                    skills.append("<input type =\"checkbox\" name=\"skills\" value=\"");
                    skills.append("id");
                    skills.append("\" id=\"skills_").append(id).append("\" ");
                    
                    if(user != 0) {
                        skills.append("checked ");
                    }
                    
                    skills.append(">\n");
                    
                    skills.append("<label for=\"skills_").append(id).append("\">");
                    skills.append(description);
                    skills.append("</label><br />\n\n");   
                }   
            }       
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        skillsList = skills.toString();
        
        return skillsList;
    }
    
    public void setSkillsList(int userid, String[] skills){
        
        try{
               Connection conn = getConnection();
               PreparedStatement pstmt = conn.prepareStatement("DELETE FROM applicants_to_skills WHERE userid = ?");
               pstmt.setInt(1, userid);
               pstmt.execute();
               PreparedStatement pstatement = conn.prepareStatement("INSERT INTO applicants_to_skills (userid, skillsid) VALUES(?,?)" );
                for (String skill : skills) {
                    int skillsid = Integer.parseInt(skill);
                    pstatement.setInt(1, userid);
                    pstatement.setInt(2, skillsid);
                    pstatement.addBatch();
                }
                int[] r= pstatement.executeBatch(); 
        }
        catch(Exception bigsad){
            bigsad.printStackTrace();
        }
    }
    
    public String getJobsListAsHTML(int userid){
        StringBuilder jobsList = new StringBuilder();
        try {
               Connection conn = getConnection();
               
               String query = "SELECT jobs.id, jobs.name, a.userid FROM\n "+
                       "jobs LEFT JOIN (SELECT * FROM applicants_to_jobs WHERE userid= 1) AS a\n"+
                       "ON jobs.id = a.jobsid\n"+
                       "WHERE jobs.id  IN\n"+
                       "( SELECT jobsid AS id FROM\n"+
                       "(applicants_to_skills JOIN skills_to_jobs\n"+
                       "ON applicants_to_skills.skillsid = skills_to_jobs.skillsid)\n"+
                       "WHERE applicants_to_skills.userid= 1)\n"+
                       "ORDER BY jobs.name;";
               PreparedStatement pstatement = conn.prepareStatement(query);
               
               boolean hasresults = pstatement.execute();
               
               if( hasresults){
                   ResultSet resultset = pstatement.getResultSet();
                   while(resultset.next()){
                       String description = resultset.getString("name");
                       int id = resultset.getInt("id");
                       int user = resultset.getInt("userid");
                       
                       jobsList.append("<input type=\"checkbox\" name=\"skills\" value=\"");
                       jobsList.append(id);
                       jobsList.append("\" id=\"skills_").append(id).append("\" ");
                       if(user !=0){
                           jobsList.append("checked  ");
                       }
                       
                       jobsList.append(">\n");
                       jobsList.append("<label for=\"skills_").append(id).append("\">");
                       jobsList.append(description);
                       jobsList.append("</label><br />\n\n");
                    }  
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return jobsList.toString();
    }
    
    public void setJobsList(int userid, String[] jobs){
        try{
            Connection conn = getConnection();
  
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM applicants_to_jobs WHERE userid = ?");
            stmt.setInt(1, userid);
            stmt.execute();
            PreparedStatement pstatement = conn.prepareStatement("INSERT INTO applicants_to_jobs (userid, jobsid) VALUES(?,?)" );
            for (String job : jobs) {
                int jobsid = Integer.parseInt(job);
                pstatement.setInt(1, userid);
                pstatement.setInt(2, jobsid);
                pstatement.addBatch();
            }
            int[] r= pstatement.executeBatch();
        }
        catch(Exception bigsad){
            bigsad.printStackTrace();
        }
     }
}