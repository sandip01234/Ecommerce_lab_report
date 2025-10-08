package com.example.khalti.integration.dto;

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
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Data
@Builder
public class PaymentRequest {
    @JsonProperty("return_url")  // Required
    private String returnUrl;

    @JsonProperty("website_url")  // Required
    private String websiteUrl;

    @JsonProperty("amount")  // Required, in paisa
    private Long amount;  // Use Long for large amounts

    @JsonProperty("purchase_order_id")  // Required
    private String purchaseOrderId;

    @JsonProperty("purchase_order_name")  // Required
    private String purchaseOrderName;

    @JsonProperty("customer_info")  // Optional
    private CustomerInfo customerInfo;

    @JsonProperty("amount_breakdown")  // Optional, but sum must = amount
    private List<AmountBreakdown> amountBreakdown;

    @JsonProperty("product_details")  // Optional
    private List<ProductDetails> productDetails;

    // Optional merchant fields (returned in response)
    @JsonProperty("merchant_username")
    private String merchantUsername;

    @JsonProperty("merchant_extra")
    private String merchantExtra;
}