package application.domain.models;

import application.domain.valueobjects.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Order {

    private String identifier;

    // Comprador que realiza el pedido.
    private Buyer buyer;

    // Líneas del pedido. Vacía por defecto.
    private List<OrderDetail> details = new ArrayList<>();

    // Estado actual dentro del ciclo de vida del pedido.
    private OrderStatus status;

    private LocalDateTime creationDate;

    // Valor total del pedido.
    private BigDecimal total;
}
