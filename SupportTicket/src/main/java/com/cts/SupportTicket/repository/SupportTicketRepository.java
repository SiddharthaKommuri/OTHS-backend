package com.cts.SupportTicket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.SupportTicket.entity.SupportTicket;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Integer> {
    List<SupportTicket> findByUserId(int userId);
    List<SupportTicket> findByStatus(SupportTicket.TicketStatus status);

    long count();
    long countByUserId(int userId);
}
