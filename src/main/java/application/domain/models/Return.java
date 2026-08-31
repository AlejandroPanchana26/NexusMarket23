package application.domain.models;

import application.domain.valueobjects.ReturnStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Return {

    private String identifier;

    // Pedido sobre el cual se solicita la devolución.
    private Order order;

    // Motivo de la devolución.
    private String reason;

    private ReturnStatus status;

    private LocalDateTime requestDate;
}
