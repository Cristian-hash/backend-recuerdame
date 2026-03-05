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