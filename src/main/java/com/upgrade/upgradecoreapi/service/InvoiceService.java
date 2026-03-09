package com.upgrade.upgradecoreapi.service;

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
}
