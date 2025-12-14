package main.java.com.sqs.models.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import main.java.com.sqs.DatabaseConnection;

import java.util.HashMap;

public class ReportDAO {
    
    public Map<String, Object> generateDailyReport(String serviceType, LocalDate date) {
        Map<String, Object> report = new HashMap<>();
        
        String sql = """
            SELECT 
                COUNT(*) as total_tickets,
                COUNT(CASE WHEN status = 'Completed' THEN 1 END) as completed,
                COUNT(CASE WHEN status = 'Serving' THEN 1 END) as serving,
                COUNT(CASE WHEN status = 'Waiting' THEN 1 END) as waiting,
                AVG(TIMESTAMPDIFF(MINUTE, start_time, completion_time)) as avg_service_time,
                MAX(TIMESTAMPDIFF(MINUTE, start_time, completion_time)) as max_service_time,
                MIN(TIMESTAMPDIFF(MINUTE, start_time, completion_time)) as min_service_time
            FROM tickets 
            WHERE service_type = ? AND DATE(request_time) = ?
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, serviceType);
            pstmt.setDate(2, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                report.put("date", date.toString());
                report.put("service_type", serviceType);
                report.put("total_tickets", rs.getInt("total_tickets"));
                report.put("completed", rs.getInt("completed"));
                report.put("serving", rs.getInt("serving"));
                report.put("waiting", rs.getInt("waiting"));
                report.put("avg_service_time", rs.getDouble("avg_service_time"));
                report.put("max_service_time", rs.getInt("max_service_time"));
                report.put("min_service_time", rs.getInt("min_service_time"));
            }
            
            // Get hourly distribution
            report.put("hourly_distribution", getHourlyDistribution(serviceType, date));
            
            // Get top purposes
            report.put("top_purposes", getTopPurposes(serviceType, date));
            
        } catch (SQLException e) {
            System.err.println("Error generating daily report: " + e.getMessage());
        }
        
        return report;
    }
    
    private Map<String, Integer> getHourlyDistribution(String serviceType, LocalDate date) {
        Map<String, Integer> distribution = new HashMap<>();
        String sql = """
            SELECT 
                HOUR(request_time) as hour,
                COUNT(*) as count
            FROM tickets 
            WHERE service_type = ? AND DATE(request_time) = ?
            GROUP BY HOUR(request_time)
            ORDER BY hour
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, serviceType);
            pstmt.setDate(2, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                distribution.put(String.format("%02d:00", rs.getInt("hour")), rs.getInt("count"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting hourly distribution: " + e.getMessage());
        }
        
        return distribution;
    }
    
    private List<Map<String, Object>> getTopPurposes(String serviceType, LocalDate date) {
        List<Map<String, Object>> purposes = new ArrayList<>();
        String sql = """
            SELECT 
                purpose,
                COUNT(*) as count,
                ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tickets WHERE service_type = ? AND DATE(request_time) = ?), 2) as percentage
            FROM tickets 
            WHERE service_type = ? AND DATE(request_time) = ?
            GROUP BY purpose
            ORDER BY count DESC
            LIMIT 10
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, serviceType);
            pstmt.setDate(2, Date.valueOf(date));
            pstmt.setString(3, serviceType);
            pstmt.setDate(4, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> purposeData = new HashMap<>();
                purposeData.put("purpose", rs.getString("purpose"));
                purposeData.put("count", rs.getInt("count"));
                purposeData.put("percentage", rs.getDouble("percentage"));
                purposes.add(purposeData);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting top purposes: " + e.getMessage());
        }
        
        return purposes;
    }
    
    public List<Map<String, Object>> getServiceTimeAnalysis(String serviceType, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> analysis = new ArrayList<>();
        String sql = """
            SELECT 
                DATE(request_time) as date,
                COUNT(*) as total_tickets,
                AVG(TIMESTAMPDIFF(MINUTE, start_time, completion_time)) as avg_time,
                MIN(TIMESTAMPDIFF(MINUTE, start_time, completion_time)) as min_time,
                MAX(TIMESTAMPDIFF(MINUTE, start_time, completion_time)) as max_time
            FROM tickets 
            WHERE service_type = ? 
            AND request_time BETWEEN ? AND ?
            AND status = 'Completed'
            GROUP BY DATE(request_time)
            ORDER BY date
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, serviceType);
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> dailyData = new HashMap<>();
                dailyData.put("date", rs.getDate("date").toString());
                dailyData.put("total_tickets", rs.getInt("total_tickets"));
                dailyData.put("avg_time", rs.getDouble("avg_time"));
                dailyData.put("min_time", rs.getInt("min_time"));
                dailyData.put("max_time", rs.getInt("max_time"));
                analysis.add(dailyData);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting service time analysis: " + e.getMessage());
        }
        
        return analysis;
    }
    
    public Map<String, Integer> getWaitTimeDistribution(String serviceType) {
        Map<String, Integer> distribution = new HashMap<>();
        String sql = """
            SELECT 
                CASE 
                    WHEN TIMESTAMPDIFF(MINUTE, request_time, start_time) <= 5 THEN '0-5 min'
                    WHEN TIMESTAMPDIFF(MINUTE, request_time, start_time) <= 10 THEN '6-10 min'
                    WHEN TIMESTAMPDIFF(MINUTE, request_time, start_time) <= 15 THEN '11-15 min'
                    WHEN TIMESTAMPDIFF(MINUTE, request_time, start_time) <= 20 THEN '16-20 min'
                    ELSE '20+ min'
                END as wait_range,
                COUNT(*) as count
            FROM tickets 
            WHERE service_type = ? 
            AND status = 'Completed'
            AND start_time IS NOT NULL
            AND DATE(request_time) = CURDATE()
            GROUP BY wait_range
            ORDER BY wait_range
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, serviceType);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                distribution.put(rs.getString("wait_range"), rs.getInt("count"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting wait time distribution: " + e.getMessage());
        }
        
        return distribution;
    }
}