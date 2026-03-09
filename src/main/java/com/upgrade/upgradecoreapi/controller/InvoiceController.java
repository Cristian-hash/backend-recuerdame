package com.upgrade.upgradecoreapi.controller;

import com.upgrade.upgradecoreapi.model.Invoice;
import com.upgrade.upgradecoreapi.model.User;
import com.upgrade.upgradecoreapi.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PatchMapping("/{id}/approve-utility")
    @PreAuthorize("hasRole('GERENCIA')")
    public ResponseEntity<String> approveUtility(@PathVariable Long id, @AuthenticationPrincipal User approver) {
        invoiceService.approveUtility(id, approver);
        return ResponseEntity.ok("Aprobación de utilidad registrada por Gerencia.");
    }

    @PatchMapping("/{id}/approve-credit")
    @PreAuthorize("hasRole('FINANZAS')")
    public ResponseEntity<String> approveCredit(@PathVariable Long id, @AuthenticationPrincipal User approver) {
        invoiceService.approveCredit(id, approver);
        return ResponseEntity.ok("Aprobación de crédito registrada por Finanzas.");
    }
}
