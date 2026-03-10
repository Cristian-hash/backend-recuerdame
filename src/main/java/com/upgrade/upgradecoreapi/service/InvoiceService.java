package com.upgrade.upgradecoreapi.service;

import com.upgrade.upgradecoreapi.dto.RegisterPurchaseRequest;
import com.upgrade.upgradecoreapi.model.Invoice;
import com.upgrade.upgradecoreapi.model.User;
import com.upgrade.upgradecoreapi.repository.InvoiceRepository;
import com.upgrade.upgradecoreapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;

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
