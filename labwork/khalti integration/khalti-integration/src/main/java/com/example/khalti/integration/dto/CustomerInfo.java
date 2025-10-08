package com.example.khalti.integration.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * ███████╗ █████╗ ███╗   ██╗██████╗ ██╗██████╗
 * ██╔════╝██╔══██╗████╗  ██║██╔══██╗██║██╔══██╗
 * ███████╗███████║██╔██╗ ██║██║  ██║██║██████╔╝
 * ╚════██║██╔══██║██║╚██╗██║██║  ██║██║██╔═══╝
 * ███████║██║  ██║██║ ╚████║██████╔╝██║██║
 * ╚══════╝╚═╝  ╚═╝╚═╝  ╚═══╝╚═════╝ ╚═╝╚═╝
 *
 * @author HP VICTUS on 10/2/2025
 */ // Nested class for customer

@Builder
@Getter
@Setter

public class CustomerInfo {
    private String name;
    private String email;
    private String phone;
}
