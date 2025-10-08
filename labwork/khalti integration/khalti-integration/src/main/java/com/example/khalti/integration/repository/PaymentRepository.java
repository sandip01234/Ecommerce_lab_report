package com.example.khalti.integration.repository;

import com.example.khalti.integration.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * ███████╗ █████╗ ███╗   ██╗██████╗ ██╗██████╗
 * ██╔════╝██╔══██╗████╗  ██║██╔══██╗██║██╔══██╗
 * ███████╗███████║██╔██╗ ██║██║  ██║██║██████╔╝
 * ╚════██║██╔══██║██║╚██╗██║██║  ██║██║██╔═══╝
 * ███████║██║  ██║██║ ╚████║██████╔╝██║██║
 * ╚══════╝╚═╝  ╚═╝╚═╝  ╚═══╝╚═════╝ ╚═╝╚═╝
 *
 * @author HP VICTUS on 10/2/2025
 */
public interface PaymentRepository extends JpaRepository<Payment,Long> {


    Optional<Payment> findByTransactionId(String transactionId);
    Optional<Payment> findByOrderId(String orderId);
}
