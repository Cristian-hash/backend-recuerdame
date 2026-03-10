package com.upgrade.upgradecoreapi.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para el requerimiento de la Historia 1B: La trampa de los 3 papeles.
 * Exige la confirmación de los 3 documentos y la asociación a una OV.
 */
@Data
public class RegisterPurchaseRequest {

    @NotBlank(message = "El código de la Orden de Venta (OV) asociada es obligatorio.")
    private String ordenVentaCode;

    @AssertTrue(message = "Debe confirmar la recepción de la Orden de Compra.")
    private boolean hasOrdenCompra;

    @AssertTrue(message = "Debe confirmar la recepción de la Guía de Remisión firmada.")
    private boolean hasGuiaRemision;

    @AssertTrue(message = "Debe confirmar la recepción de la Factura.")
    private boolean hasFactura;

    private String finalObservations;
}
