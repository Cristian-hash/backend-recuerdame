package com.upgrade.upgradecoreapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", nullable = false, unique = true)
    private String invoiceNumber;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "current_balance", nullable = false)
    private BigDecimal currentBalance;

    @Column(name = "is_delivered")
    private boolean isDelivered;

    @Column(name = "is_paid")
    private boolean isPaid;

    @Column(name = "penalty_applied")
    private boolean penaltyApplied;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @Column(name = "orden_venta_code")
    private String ordenVentaCode;

    @Column(name = "has_orden_compra")
    private boolean hasOrdenCompra;

    @Column(name = "has_guia_remision")
    private boolean hasGuiaRemision;

    @Column(name = "has_factura")
    private boolean hasFactura;

    @Column(name = "siaf_code")
    private String siafCode;

    @Column(name = "unidad_ejecutora")
    private String unidadEjecutora;

    @Column(name = "letra_firmada_path")
    private String letraFirmadaPath;

    @Column(name = "dni_path")
    private String dniPath;

    @Column(name = "utilidad_aprobada_at")
    private LocalDateTime utilidadAprobadaAt;

    @Column(name = "credito_aprobado_at")
    private LocalDateTime creditoAprobadoAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private InvoiceType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_entity_id", nullable = false)
    private BusinessEntity businessEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilidad_aprobada_por_id")
    private User utilidadAprobadaPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credito_aprobado_por_id")
    private User creditoAprobadoPor;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public enum InvoiceType {
        COMPRA,
        VENTA
    }
}
