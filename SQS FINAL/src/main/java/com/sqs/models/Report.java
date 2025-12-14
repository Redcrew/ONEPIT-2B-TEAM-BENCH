package main.java.com.sqs.models;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

public class Report {
    private LocalDate reportDate;
    private String serviceType;
    private int totalTickets;
    private int completedTickets;
    private int waitingTickets;
    private int servingTickets;
    private double averageServiceTime;
    private Map<String, Integer> hourlyDistribution;
    private Map<String, Integer> purposeDistribution;
    private Map<String, Integer> waitTimeDistribution;
    private int peakHour;
    
    public Report() {
        this.reportDate = LocalDate.now();
        this.hourlyDistribution = new HashMap<>();
        this.purposeDistribution = new HashMap<>();
        this.waitTimeDistribution = new HashMap<>();
    }
    
    public Report(String serviceType) {
        this();
        this.serviceType = serviceType;
    }
    
    // Getters and Setters
    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    
    public int getTotalTickets() { return totalTickets; }
    public void setTotalTickets(int totalTickets) { this.totalTickets = totalTickets; }
    
    public int getCompletedTickets() { return completedTickets; }
    public void setCompletedTickets(int completedTickets) { this.completedTickets = completedTickets; }
    
    public int getWaitingTickets() { return waitingTickets; }
    public void setWaitingTickets(int waitingTickets) { this.waitingTickets = waitingTickets; }
    
    public int getServingTickets() { return servingTickets; }
    public void setServingTickets(int servingTickets) { this.servingTickets = servingTickets; }
    
    public double getAverageServiceTime() { return averageServiceTime; }
    public void setAverageServiceTime(double averageServiceTime) { this.averageServiceTime = averageServiceTime; }
    
    public Map<String, Integer> getHourlyDistribution() { return hourlyDistribution; }
    public void setHourlyDistribution(Map<String, Integer> hourlyDistribution) { this.hourlyDistribution = hourlyDistribution; }
    
    public Map<String, Integer> getPurposeDistribution() { return purposeDistribution; }
    public void setPurposeDistribution(Map<String, Integer> purposeDistribution) { this.purposeDistribution = purposeDistribution; }
    
    public Map<String, Integer> getWaitTimeDistribution() { return waitTimeDistribution; }
    public void setWaitTimeDistribution(Map<String, Integer> waitTimeDistribution) { this.waitTimeDistribution = waitTimeDistribution; }
    
    public int getPeakHour() { return peakHour; }
    public void setPeakHour(int peakHour) { this.peakHour = peakHour; }
    
    // Utility methods
    public double getCompletionRate() {
        if (totalTickets == 0) return 0.0;
        return (completedTickets * 100.0) / totalTickets;
    }
    
    public int getAverageWaitTime() {
        // Simplified calculation - in real implementation, calculate from actual data
        return waitingTickets * 2;
    }
    
    public String getPeakHourFormatted() {
        if (peakHour >= 0 && peakHour <= 23) {
            return String.format("%02d:00 - %02d:00", peakHour, (peakHour + 1) % 24);
        }
        return "N/A";
    }
    
    public String getBusiestHour() {
        if (hourlyDistribution.isEmpty()) return "N/A";
        
        String busiestHour = "";
        int maxCount = 0;
        
        for (Map.Entry<String, Integer> entry : hourlyDistribution.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                busiestHour = entry.getKey();
            }
        }
        
        return busiestHour;
    }
    
    public String getMostCommonPurpose() {
        if (purposeDistribution.isEmpty()) return "N/A";
        
        String mostCommon = "";
        int maxCount = 0;
        
        for (Map.Entry<String, Integer> entry : purposeDistribution.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostCommon = entry.getKey();
            }
        }
        
        return mostCommon;
    }
    
    public String generateSummary() {
        return String.format(
            "Report for %s on %s\n" +
            "Total Tickets: %d\n" +
            "Completed: %d (%.1f%%)\n" +
            "Waiting: %d\n" +
            "Serving: %d\n" +
            "Avg Service Time: %.1f min\n" +
            "Peak Hour: %s\n" +
            "Most Common Purpose: %s",
            serviceType, reportDate, totalTickets, completedTickets, getCompletionRate(),
            waitingTickets, servingTickets, averageServiceTime,
            getPeakHourFormatted(), getMostCommonPurpose()
        );
    }
}