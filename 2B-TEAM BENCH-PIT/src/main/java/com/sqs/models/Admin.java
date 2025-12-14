package main.java.com.sqs.models;

public class Admin {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String adminId;
    private String department;
    
    public Admin() {}
    
    public Admin(String username, String password, String fullName, String adminId, String department) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.adminId = adminId;
        this.department = department;
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
