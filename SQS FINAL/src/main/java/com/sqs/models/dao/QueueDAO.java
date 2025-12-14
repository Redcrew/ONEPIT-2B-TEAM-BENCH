package main.java.com.sqs.models.dao;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import main.java.com.sqs.DatabaseConnection;

public class QueueDAO {
    
    public Map<String, Integer> getQueueStatistics(String serviceType) {
        Map<String, Integer> stats = new HashMap<>();
        String sql = """
            SELECT 
                COUNT(CASE WHEN status = 'Waiting' THEN 1 END) as waiting,
                COUNT(CASE WHEN status = 'Serving' THEN 1 END) as serving,
                COUNT(CASE WHEN status = 'Completed' THEN 1 END) as completed
            FROM tickets 
            WHERE service_type = ? AND DATE(request_time) = CURDATE()
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, serviceType);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                stats.put("waiting", rs.getInt("waiting"));
                stats.put("serving", rs.getInt("serving"));
                stats.put("completed", rs.getInt("completed"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting queue statistics: " + e.getMessage());
        }
        
        return stats;
    }
    
    public Map<String, Integer> getHourlyDistribution(String serviceType) {
        Map<String, Integer> distribution = new HashMap<>();
        String sql = """
            SELECT 
                HOUR(request_time) as hour,
                COUNT(*) as count
            FROM tickets 
            WHERE service_type = ? AND DATE(request_time) = CURDATE()
            GROUP BY HOUR(request_time)
            ORDER BY hour
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, serviceType);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int hour = rs.getInt("hour");
                int count = rs.getInt("count");
                distribution.put(String.format("%02d:00", hour), count);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting hourly distribution: " + e.getMessage());
        }
        
        return distribution;
    }
    
    public double getAverageServiceTime(String serviceType) {
        String sql = """
            SELECT AVG(TIMESTAMPDIFF(MINUTE, start_time, completion_time)) as avg_time
            FROM tickets 
            WHERE service_type = ? 
            AND status = 'Completed'
            AND start_time IS NOT NULL 
            AND completion_time IS NOT NULL
            AND DATE(request_time) = CURDATE()
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, serviceType);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("avg_time");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting average service time: " + e.getMessage());
        }
        
        return 0.0;
    }
    
    public int getPeakHour(String serviceType) {
        String sql = """
            SELECT 
                HOUR(request_time) as hour,
                COUNT(*) as count
            FROM tickets 
            WHERE service_type = ? AND DATE(request_time) = CURDATE()
            GROUP BY HOUR(request_time)
            ORDER BY count DESC
            LIMIT 1
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, serviceType);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("hour");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting peak hour: " + e.getMessage());
        }
        
        return 10; // Default peak hour
    }
}
