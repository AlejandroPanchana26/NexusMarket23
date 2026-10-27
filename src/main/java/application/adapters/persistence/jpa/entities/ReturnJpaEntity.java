package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// La tabla se llama return_requests porque "return" es una palabra reservada en SQL.
@Entity
@Table(name = "return_requests")
@Getter
@Setter
@NoArgsConstructor
public class ReturnJpaEntity {

    @Id
    @Column(name = "identifier", length = 60)
    private String identifier;

    @Column(name = "order_identifier", nullable = false, length = 60)
    private String orderIdentifier;

    @Column(name = "reason", nullable = false, length = 300)
    private String reason;

    @Column(name = "status", length = 40)
    private String status;

    @Column(name = "request_date")
    private LocalDateTime requestDate;
}
