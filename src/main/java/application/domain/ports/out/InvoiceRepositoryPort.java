package application.domain.ports.out;

import application.domain.models.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepositoryPort {

    Invoice save(Invoice invoice);

    Optional<Invoice> findByOrderIdentifier(String orderIdentifier);

    List<Invoice> findAll();
}
