package com.upgrade.upgradecoreapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "entities")
public class BusinessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ruc_dni", nullable = false, unique = true)
    private String rucDni;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private EntityType type;

    public enum EntityType {
        GOBIERNO,
        RETAIL,
        CORPORATIVO,
        PROVEEDOR
    }
}
