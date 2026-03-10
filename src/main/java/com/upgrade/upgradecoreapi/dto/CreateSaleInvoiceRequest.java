package com.upgrade.upgradecoreapi.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateSaleInvoiceRequest {

    @NotNull(message = "El ID del cliente es obligatorio.")
    private Long clientId;

    @NotBlank(message = "El número de factura es obligatorio.")
    private String invoiceNumber;

    @NotNull(message = "La fecha de emisión es obligatoria.")
    private LocalDate issueDate;

    @NotNull(message = "La fecha de vencimiento es obligatoria.")
    @FutureOrPresent(message = "La fecha de vencimiento no puede ser en el pasado.")
    private LocalDate dueDate;

    @NotNull(message = "El monto total es obligatorio.")
    @Positive(message = "El monto total debe ser positivo.")
    private BigDecimal totalAmount;

    private boolean isDelivered = false;

    // --- CAMPOS PARA GOBIERNO (H3) ---
    private String siafCode;
    private String unidadEjecutora;
    private String ordenCompraFilePath;

    // --- CAMPOS PARA OBSERVACIÓN AUTOMÁTICA (H3) ---
    private String ocNumber;
    private BigDecimal commissionPercentage;
    private BigDecimal commissionAmount;

    private String observations;
}
