# 🏢 PROYECTO: UPGRADE ERP (Core API)

## 🎯 Objetivo Principal
Crear un backend robusto en Spring Boot para reemplazar los Excels de Finanzas (Cuentas por cobrar, Compras, Gobierno y Retail) de la empresa Upgrade, gestionado por Karlangela y supervisado por la Sra. Mirna.

## 🛠️ Stack Tecnológico (Antigravity Architecture)
- **Java 17** + **Spring Boot 3**
- **Base de Datos:** PostgreSQL 16
- **Migraciones:** Flyway
- **Seguridad:** JWT (Roles: GERENCIA, FINANZAS, LOGISTICA, VENTAS)

## 🗄️ Esquema Principal de BD y Lógica
1. `users`: Usuarios (Mirna, Karlangela, Ronald, Vendedores).
2. `entities`: Clientes y Proveedores (RUC/DNI único). Tipos: GOBIERNO, RETAIL, CORPORATIVO, PROVEEDOR.
3. `invoices`: Facturas. Lógica clave: El `current_balance` (saldo) se calcula en el backend.
4. `payments`: Abonos que restan al saldo de `invoices`.
5. `recurring_services`: Para el CronJob de alertas de pago (ej. Internet).

## 📜 REGLAS DE NEGOCIO ESTRICTAS (Historias de Usuario)
El agente de IA debe programar el backend respetando obligatoriamente estas trampas/validaciones:

* **Regla 1 (Compras):** Para guardar una compra (`POST /purchases`), el Payload DEBE exigir confirmación booleana de 3 documentos (OC, Guía, Factura) y estar vinculada a una OV. Si falta uno, devolver HTTP 400.
* **Regla 2 (Saldos y Entregas):** No se puede facturar a crédito si el estado "Descargado" es falso. El backend debe rechazar la petición.
* **Regla 3 (Gobierno):** Si la venta es a "Gobierno", el DTO debe exigir obligatoriamente los campos `SIAF`, `Unidad Ejecutora` y un archivo de evidencia adjunto.
* **Regla 4 (Retail):** Ventas a crédito a "Retail" están bloqueadas por defecto. Requieren aprobación previa (`status = APPROVED`) por parte del rol `GERENCIA`. Si está aprobado, exige ruta de archivo de Letra y DNI.
* **Regla 5 (Cron Jobs):** Se requiere un `@Scheduled` para alertar servicios recurrentes 3 días antes de vencer, y otro para calcular penalidades del 3.9% en facturas mayores a 61 días.

## 🧑‍💻 Instrucciones de Código para el Agente IA
- Usa **Lombok** (`@Data`, `@Builder`).
- Sigue el principio **"Divide y Vencerás"**:
  1. `Controller` (Solo recibe y responde, usa HTTP Status correctos).
  2. `Service` (Contiene las reglas de negocio, las "Trampas").
  3. `Repository` (Solo Spring Data JPA).
- Los DTOs deben usar validaciones como `@NotNull`, `@NotBlank`, `@Min`.
-------------------------------------------
📋 Historias de Usuario — Proceso: Orden de Compra y Registro

Este documento describe las historias de usuario y reglas de negocio del sistema relacionadas con:

Orden de Compra

Validación documental

Ventas corporativas

Ventas al gobierno

Control de crédito

Servicios recurrentes

Incluye criterios de aceptación y datos de prueba para facilitar el desarrollo y testing.

📖 Historia 1A — Aprobación de Solicitud de Compra
Descripción

Como encargada de Finanzas (Karlangela),
quiero recibir la Orden de Venta (OV) generada luego del proceso de cotización del vendedor y aprobar la solicitud en el sistema,
para que Ronald (Logística) pueda generar la Orden de Compra real al proveedor y la Sra. Mirna proceda con el pago.

✅ Criterios de Aceptación

Prueba de Flujo

Dado que el vendedor sube una Orden de Venta (OV),
cuando Karlangela entra a su bandeja,
entonces el sistema muestra un botón para:

Aprobar utilidad (validado por la Sra. Mirna)

Aprobar crédito en la OV (validado por Karlangela)

Una vez aprobado, el sistema notifica automáticamente a Ronald (Logística).

Prueba de Bloqueo a Logística

Dado que Karlangela aún no aprueba la solicitud,
cuando Ronald intenta generar la Orden de Compra oficial,
entonces el sistema lo bloquea mostrando el mensaje:

Falta aprobación de Finanzas
📖 Historia 1B — Registro Final y Validación de Compra
Descripción

Como encargada de Finanzas (Karlangela),
quiero registrar la compra final en el sistema exigiendo la existencia de tres documentos obligatorios (OC, Guía firmada y Factura firmada) y que las Órdenes de Venta estén correctamente asociadas,
para dejar de usar el Excel borrador y asegurar que todo el registro esté completo y validado.

✅ Criterios de Aceptación

Prueba de los 3 documentos

Dado que Karlangela inicia el registro de compra en el sistema,
cuando la pantalla carga,
entonces el sistema exige confirmar o adjuntar:

Orden de Compra (OC)

Guía de Remisión firmada (subida obligatoria por Almacén)

Factura firmada

Prueba de Asociación de OV

Dado que se registró la compra para un cliente,
cuando Karlangela intenta guardar,
entonces el sistema verifica que la Orden de Venta (OV) esté asociada correctamente a una Orden de Compra.

Prueba de Observaciones del Vendedor

Dado que el vendedor olvidó incluir observaciones en la OV,
cuando Karlangela revisa la OV en el sistema,
entonces el sistema resalta en rojo la falta de observaciones para exigir su corrección.

Ejemplo de observación esperada:

Orden de Compra: JLBy Rivero
OC 67 / SIAF 491 / UE 300359 / monto S/ 440.00
comisión 10% + S/ 50.00
🧪 Datos de Prueba para el Programador

Trampa 1

Intentar guardar la compra marcando solo:

Factura

OC

Pero sin marcar la Guía de Remisión.

Resultado esperado:

El sistema debe bloquear el guardado

Trampa 2

Intentar guardar la compra sin ninguna OV vinculada.

Resultado esperado:

El sistema debe bloquear el registro
📖 Historia 2 — Ventas Corporativas (Control de Entrega y Saldos)
Descripción

Como encargada de Finanzas,
quiero que el sistema calcule automáticamente los saldos según los abonos registrados, aplique intereses a partir del día 61 y bloquee la facturación si el producto no fue entregado,
para dejar de depender del Excel de cobranzas y asegurar cálculos correctos.

✅ Criterios de Aceptación

Prueba de Entrega

Dado que el campo Descargado = NO,
cuando Karlangela intenta generar la factura a crédito,
entonces el sistema bloquea la operación solicitando confirmar la entrega del producto.

Prueba de Abonos

Dado que una factura es de S/ 1,000,
cuando se registra un pago de S/ 300,
entonces el sistema calcula automáticamente:

Saldo restante: S/ 700

Prueba de Intereses

Dado que la deuda llega al día 61,
cuando el reloj del sistema marca la medianoche,
entonces el monto pendiente aumenta automáticamente en 3.9%.

(Aplicación sujeta a aprobación de la gerencia.)

Prueba de Categorías (Canjes)

Dado que una factura se marca como Canje,
cuando la gerencia revisa el reporte de ganancias,
entonces esa factura no suma ingresos al total financiero.

🧪 Datos de Prueba

Factura: S/ 500

Abono: S/ 200

Resultado esperado:

Saldo: S/ 300
📖 Historia 3 — Venta al Gobierno (Validación de Documentos)
Descripción

Como encargada de Finanzas,
quiero que el sistema exija registrar el SIAF, la Unidad Ejecutora y subir la Orden de Compra,
para asegurar que las ventas al gobierno tengan toda la documentación necesaria.

✅ Criterios de Aceptación

Prueba de Campos Obligatorios

Dado que el cliente es de tipo Gobierno,
cuando se carga el formulario,
entonces aparecen obligatoriamente los campos:

SIAF

Unidad Ejecutora

Prueba de Evidencia

Dado que Karlangela intenta guardar la venta,
cuando no adjunta el archivo de la Orden de Compra,
entonces el sistema bloquea el guardado.

Ejemplo de Caso Real

Cuando Upgrade vende a PC Smart,
y luego PC Smart vende a la Universidad Católica,
en las observaciones debe registrarse:

Orden de Compra: JLBy Rivero
OC 67 / SIAF 491 / UE 300359 / monto S/ 440.00
comisión 10% + S/ 50.00
🧪 Datos de Prueba

Unidad Ejecutora:

06 — Municipalidad José Luis Bustamante y Rivero

Trampa

Intentar guardar sin subir la Orden de Compra.

Resultado esperado:

El sistema bloquea el guardado
📖 Historia 4 — Servicios Recurrentes (Alertas de Pago)
Descripción

Como encargada de Finanzas,
quiero recibir alertas visuales antes del vencimiento de servicios recurrentes,
para evitar cortes de servicios importantes como internet o hosting.

✅ Criterios de Aceptación

Prueba de Aviso

Dado que el pago vence el día 30,
cuando el sistema llega al día 27,
entonces aparece:

alerta amarilla (recordatorio)

alerta roja si el pago está vencido

Prueba de Reinicio

Dado que Karlangela marca el servicio como Pagado,
cuando se confirma la acción,
entonces:

la alerta desaparece

el sistema programa automáticamente el siguiente ciclo mensual.

Ejemplo de Caso
1 enero → no se paga
1 febrero → no se paga
13 febrero → se paga todo

Resultado:
El sistema reinicia el ciclo para el 1 de marzo
🧪 Datos de Prueba

Crear un servicio que venza en 3 días.

Resultado esperado:

El sistema muestra alerta hoy
📖 Historia 5 — Venta a Crédito Retail (Excepción Gerencial)
Descripción

Como encargada de Finanzas,
quiero que el sistema bloquee por defecto las ventas a crédito para clientes Retail, requiriendo autorización gerencial y documentos de respaldo,
para evitar deudas incobrables y mantener respaldo legal.

✅ Criterios de Aceptación

Prueba de Bloqueo

Dado que el cliente es tipo Retail/Tienda,
cuando el vendedor intenta seleccionar Pago a Crédito,
entonces el sistema bloquea la opción mostrando:

Crédito denegado: Requiere autorización gerencial

Prueba de Autorización

Dado que la gerencia autoriza la excepción,
cuando Karlangela registra la venta,
entonces el sistema habilita el crédito pero exige los documentos de garantía.

Prueba de Documentos

Dado que la venta fue autorizada,
cuando Karlangela intenta guardar la factura,
entonces el sistema exige:

Confirmación de Letra física firmada

Adjuntar foto del DNI del cliente

🧪 Datos de Prueba

Trampa 1

Intentar registrar crédito sin autorización gerencial.

Resultado esperado:

El sistema rechaza la operación

Trampa 2

Intentar guardar la venta autorizada sin subir la letra firmada.

Resultado esperado:

El sistema bloquea el registro