package main.java.com.sqs.models;

public class User {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String userId;
    private String role;
    
    public User() {}
    
    public User(String username, String password, String fullName, String userId) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.userId = userId;
        this.role = "user";
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
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
