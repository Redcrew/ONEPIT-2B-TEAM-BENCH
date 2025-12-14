package main.java.com.sqs.models.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import main.java.com.sqs.DatabaseConnection;
import main.java.com.sqs.models.Ticket;

public class TicketDAO {
    
    public boolean saveTicket(Ticket ticket) {
        String sql = "INSERT INTO tickets (ticket_number, user_id, user_name, purpose, service_type, status, request_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ticket.getTicketNumber());
            pstmt.setString(2, ticket.getUserId());
            pstmt.setString(3, ticket.getUserName());
            pstmt.setString(4, ticket.getPurpose());
            pstmt.setString(5, ticket.getServiceType());
            pstmt.setString(6, ticket.getStatus());
            pstmt.setTimestamp(7, Timestamp.valueOf(ticket.getRequestTime()));
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving ticket: " + e.getMessage());
            return false;
        }
    }
    
    public String generateNextTicketNumber() {
        String sql = "SELECT MAX(ticket_number) FROM tickets WHERE ticket_number LIKE '0-%'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                String lastTicket = rs.getString(1);
                if (lastTicket != null) {
                    String[] parts = lastTicket.split("-");
                    int num = Integer.parseInt(parts[1]) + 1;
                    return String.format("0-%03d", num);
                }
            }
            
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error generating ticket number: " + e.getMessage());
        }
        
        return "0-001";
    }
    
    public boolean updateTicketStatus(String ticketNumber, String status) {
        String sql = "UPDATE tickets SET status = ? WHERE ticket_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setString(2, ticketNumber);
            
            if (status.equals("Serving")) {
                String updateTimeSql = "UPDATE tickets SET start_time = ? WHERE ticket_number = ?";
                try (PreparedStatement pstmt2 = conn.prepareStatement(updateTimeSql)) {
                    pstmt2.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                    pstmt2.setString(2, ticketNumber);
                    pstmt2.executeUpdate();
                }
            } else if (status.equals("Completed")) {
                String updateTimeSql = "UPDATE tickets SET completion_time = ? WHERE ticket_number = ?";
                try (PreparedStatement pstmt2 = conn.prepareStatement(updateTimeSql)) {
                    pstmt2.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                    pstmt2.setString(2, ticketNumber);
                    pstmt2.executeUpdate();
                }
            }
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating ticket status: " + e.getMessage());
            return false;
        }
    }
    
    public Ticket getNextWaitingTicket(String serviceType) {
        String sql = "SELECT * FROM tickets WHERE service_type = ? AND status = 'Waiting' ORDER BY request_time ASC LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, serviceType);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractTicketFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting next ticket: " + e.getMessage());
        }
        
        return null;
    }
    
    public Ticket getCurrentlyServing(String serviceType) {
        String sql = "SELECT * FROM tickets WHERE service_type = ? AND status = 'Serving' LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, serviceType);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractTicketFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting serving ticket: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<Ticket> getActiveTickets(String serviceType) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE service_type = ? AND status IN ('Waiting', 'Serving') ORDER BY request_time ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, serviceType);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                tickets.add(extractTicketFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting active tickets: " + e.getMessage());
        }
        
        return tickets;
    }
    
    public List<Ticket> getCompletedTickets(String serviceType) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE service_type = ? AND status = 'Completed' ORDER BY completion_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, serviceType);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                tickets.add(extractTicketFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting completed tickets: " + e.getMessage());
        }
        
        return tickets;
    }
    
    public Ticket getTicket(String ticketNumber) {
        String sql = "SELECT * FROM tickets WHERE ticket_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ticketNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractTicketFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting ticket: " + e.getMessage());
        }
        
        return null;
    }
    
    public int getPositionInQueue(String ticketNumber, String serviceType) {
        String sql = """
            SELECT COUNT(*) as position 
            FROM tickets 
            WHERE service_type = ? 
            AND status = 'Waiting' 
            AND request_time < (SELECT request_time FROM tickets WHERE ticket_number = ?)
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, serviceType);
            pstmt.setString(2, ticketNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("position") + 1; // Add 1 because position starts from 1
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting position: " + e.getMessage());
        }
        
        return 0;
    }
    
    public int getTotalServicesToday(String serviceType) {
        String sql = "SELECT COUNT(*) FROM tickets WHERE service_type = ? AND DATE(request_time) = CURDATE()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, serviceType);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total services: " + e.getMessage());
        }
        
        return 0;
    }
    
    public int getWaitingCount(String serviceType) {
        String sql = "SELECT COUNT(*) FROM tickets WHERE service_type = ? AND status = 'Waiting'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, serviceType);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting waiting count: " + e.getMessage());
        }
        
        return 0;
    }
    
    private Ticket extractTicketFromResultSet(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setTicketNumber(rs.getString("ticket_number"));
        ticket.setUserId(rs.getString("user_id"));
        ticket.setUserName(rs.getString("user_name"));
        ticket.setPurpose(rs.getString("purpose"));
        ticket.setServiceType(rs.getString("service_type"));
        ticket.setStatus(rs.getString("status"));
        
        Timestamp ts = rs.getTimestamp("request_time");
        if (ts != null) {
            ticket.setRequestTime(ts.toLocalDateTime());
        }
        
        ts = rs.getTimestamp("start_time");
        if (ts != null) {
            ticket.setStartTime(ts.toLocalDateTime());
        }
        
        ts = rs.getTimestamp("completion_time");
        if (ts != null) {
            ticket.setCompletionTime(ts.toLocalDateTime());
        }
        
        return ticket;
    }
}
