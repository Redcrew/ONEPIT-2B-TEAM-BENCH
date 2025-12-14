package main.java.com.sqs.models;

import java.util.List;

import main.java.com.sqs.models.dao.TicketDAO;

public class ServiceQueue {
    private String serviceName;
    private TicketDAO ticketDAO;
    
    public ServiceQueue(String serviceName) {
        this.serviceName = serviceName;
        this.ticketDAO = new TicketDAO();
    }
    
    public Ticket generateTicket(String userName, String purpose, String userId) {
        String ticketNum = ticketDAO.generateNextTicketNumber();
        Ticket ticket = new Ticket(ticketNum, userName, purpose, serviceName, userId);
        if (ticketDAO.saveTicket(ticket)) {
            return ticket;
        }
        return null;
    }
    
    public void serveNextTicket() {
        Ticket nextTicket = ticketDAO.getNextWaitingTicket(serviceName);
        if (nextTicket != null) {
            ticketDAO.updateTicketStatus(nextTicket.getTicketNumber(), "Serving");
        }
    }
    
    public void completeTicket(String ticketNumber) {
        ticketDAO.updateTicketStatus(ticketNumber, "Completed");
    }
    
    public List<Ticket> getActiveTickets() {
        return ticketDAO.getActiveTickets(serviceName);
    }
    
    public List<Ticket> getCompletedTickets() {
        return ticketDAO.getCompletedTickets(serviceName);
    }
    
    public Ticket getTicket(String ticketNumber) {
        return ticketDAO.getTicket(ticketNumber);
    }
    
    public int getPositionInQueue(String ticketNumber) {
        return ticketDAO.getPositionInQueue(ticketNumber, serviceName);
    }
    
    public String getCurrentlyServing() {
        Ticket serving = ticketDAO.getCurrentlyServing(serviceName);
        return serving != null ? serving.getTicketNumber() : null;
    }
    
    public String getServiceName() { return serviceName; }
    
    public int getTotalServicesToday() {
        return ticketDAO.getTotalServicesToday(serviceName);
    }
    
    public int getWaitingCount() {
        return ticketDAO.getWaitingCount(serviceName);
    }
}