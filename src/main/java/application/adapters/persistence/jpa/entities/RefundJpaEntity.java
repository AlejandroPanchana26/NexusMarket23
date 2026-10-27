package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "refunds")
@Getter
@Setter
@NoArgsConstructor
public class RefundJpaEntity {

    @Id
    @Column(name = "identifier", length = 60)
    private String identifier;

    // Una devolución tiene un solo reembolso.
    @Column(name = "return_identifier", nullable = false, unique = true, length = 60)
    private String returnIdentifier;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "status", length = 40)
    private String status;

    @Column(name = "refund_date")
    private LocalDateTime refundDate;
}
