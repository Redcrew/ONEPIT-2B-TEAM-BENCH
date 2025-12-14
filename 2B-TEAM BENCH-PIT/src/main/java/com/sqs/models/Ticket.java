 package main.java.com.sqs.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket {
    private String ticketNumber;
    private String userId;
    private String userName;
    private String purpose;
    private String status;
    private LocalDateTime requestTime;
    private LocalDateTime startTime;
    private LocalDateTime completionTime;
    private String serviceType;
    
    public Ticket() {}
    
    public Ticket(String ticketNumber, String userName, String purpose, String serviceType, String userId) {
        this.ticketNumber = ticketNumber;
        this.userName = userName;
        this.purpose = purpose;
        this.serviceType = serviceType;
        this.userId = userId;
        this.status = "Waiting";
        this.requestTime = LocalDateTime.now();
    }
    
    // Getters and setters
    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    
    public LocalDateTime getRequestTime() { return requestTime; }
    public void setRequestTime(LocalDateTime requestTime) { this.requestTime = requestTime; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getCompletionTime() { return completionTime; }
    public void setCompletionTime(LocalDateTime completionTime) { this.completionTime = completionTime; }
    
    public String getRequestTimeFormatted() {
        return requestTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
    
    public long getServiceDuration() {
        if (startTime != null && completionTime != null) {
            return java.time.Duration.between(startTime, completionTime).toMinutes();
        }
        return 0;
    }
    
    public Object[] toTableRow() {
        return new Object[]{ticketNumber, userName, userId, purpose, status};
    }
} 
    

