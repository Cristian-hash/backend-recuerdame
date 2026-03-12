package com.upgrade.upgradecoreapi.service;

import com.upgrade.upgradecoreapi.dto.CreateSaleInvoiceRequest;
import com.upgrade.upgradecoreapi.dto.RegisterPurchaseRequest;
import com.upgrade.upgradecoreapi.model.BusinessEntity;
import com.upgrade.upgradecoreapi.model.Invoice;
import com.upgrade.upgradecoreapi.model.User;
import com.upgrade.upgradecoreapi.repository.BusinessEntityRepository;
import com.upgrade.upgradecoreapi.repository.InvoiceRepository;
import com.upgrade.upgradecoreapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final BusinessEntityRepository businessEntityRepository;

    @Transactional
    public Invoice createSaleInvoice(CreateSaleInvoiceRequest request, User createdBy) {
        BusinessEntity client = businessEntityRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + request.getClientId()));

        if (!request.isDelivered()) {
            throw new IllegalStateException("No se puede facturar si el producto no ha sido entregado (Descargado = NO).");
        }

        String finalObservations = request.getObservations() != null ? request.getObservations() : "";

        if (client.getType() == BusinessEntity.EntityType.GOBIERNO) {
            if (request.getSiafCode() == null || request.getSiafCode().isBlank()) {
                throw new IllegalArgumentException("El código SIAF es obligatorio para ventas a Gobierno.");
            }
            if (request.getUnidadEjecutora() == null || request.getUnidadEjecutora().isBlank()) {
                throw new IllegalArgumentException("La Unidad Ejecutora es obligatoria para ventas a Gobierno.");
            }
            if (request.getOrdenCompraFilePath() == null || request.getOrdenCompraFilePath().isBlank()) {
                throw new IllegalArgumentException("El archivo de la Orden de Compra es obligatorio para ventas a Gobierno.");
            }

            // Formato exacto solicitado por la Sra. Mirna
            String governmentObservation = String.format(
                "Orden de Compra: %s OC %s /SIAF %s / UE %s / monto S/ %.2f // comision %s%% + S/ %.2f",
                client.getBusinessName(),
                Objects.toString(request.getOcNumber(), ""),
                request.getSiafCode(),
                request.getUnidadEjecutora(),
                request.getTotalAmount(),
                Objects.toString(request.getCommissionPercentage(), "0"),
                Objects.toString(request.getCommissionAmount(), "0.00")
            );
            finalObservations = governmentObservation + "\n" + finalObservations;
        }

        Invoice invoice = Invoice.builder()
                .businessEntity(client)
                .user(createdBy)
                .invoiceNumber(request.getInvoiceNumber())
                .issueDate(request.getIssueDate())
                .dueDate(request.getDueDate())
                .totalAmount(request.getTotalAmount())
                .currentBalance(request.getTotalAmount())
                .isDelivered(true)
                .isPaid(false)
                .type(Invoice.InvoiceType.VENTA)
                .siafCode(request.getSiafCode())
                .unidadEjecutora(request.getUnidadEjecutora())
                .observations(finalObservations.trim())
                .build();

        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice approveUtility(Long invoiceId, User approver) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + invoiceId));

        if (invoice.getUtilidadAprobadaPor() != null) {
            throw new IllegalStateException("La utilidad de esta factura ya fue aprobada.");
        }

        invoice.setUtilidadAprobadaPor(approver);
        invoice.setUtilidadAprobadaAt(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice approveCredit(Long invoiceId, User approver) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + invoiceId));

        if (invoice.getUtilidadAprobadaPor() == null) {
            throw new IllegalStateException("No se puede aprobar el crédito. Falta la aprobación de utilidad por parte de Gerencia.");
        }

        if (invoice.getCreditoAprobadoPor() != null) {
            throw new IllegalStateException("El crédito para esta factura ya fue aprobado.");
        }

        invoice.setCreditoAprobadoPor(approver);
        invoice.setCreditoAprobadoAt(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice registerFinalPurchase(Long invoiceId, RegisterPurchaseRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura de compra no encontrada con ID: " + invoiceId));

        if (invoice.getType() != Invoice.InvoiceType.COMPRA) {
            throw new IllegalStateException("Esta operación solo es válida para facturas de tipo COMPRA.");
        }

        invoice.setOrdenVentaCode(request.getOrdenVentaCode());
        invoice.setHasOrdenCompra(request.isHasOrdenCompra());
        invoice.setHasGuiaRemision(request.isHasGuiaRemision());
        invoice.setHasFactura(request.isHasFactura());

        if (request.getFinalObservations() != null && !request.getFinalObservations().isBlank()) {
            String existingObservations = invoice.getObservations() != null ? invoice.getObservations() : "";
            String newObservations = existingObservations + "\n\n--- OBSERVACIÓN DE CIERRE (FINANZAS) ---\n" + request.getFinalObservations();
            invoice.setObservations(newObservations.trim());
        }

        return invoiceRepository.save(invoice);
    }
}
