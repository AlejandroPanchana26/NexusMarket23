package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.LoginRequest;
import application.adapters.rest.dtos.requests.RegisterBuyerRequest;
import application.adapters.rest.dtos.responses.ProductResponse;
import application.adapters.rest.dtos.responses.UserResponse;
import application.adapters.rest.mappers.ProductRestMapper;
import application.adapters.rest.mappers.UserRestMapper;
import application.domain.models.User;
import application.domain.ports.in.PublicAccessPort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Operaciones que no requieren iniciar sesión. Solo convierte datos y llama al puerto de entrada.
@RestController
@RequestMapping("/api/public")
public class PublicRestController {

    private final PublicAccessPort publicAccessPort;

    public PublicRestController(PublicAccessPort publicAccessPort) {
        this.publicAccessPort = publicAccessPort;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerBuyer(@Valid @RequestBody RegisterBuyerRequest request) {
        User user = publicAccessPort.registerBuyer(UserRestMapper.toBuyer(request), request.username(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserRestMapper.toResponse(user));
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest request) {
        return UserRestMapper.toResponse(publicAccessPort.login(request.username(), request.password()));
    }

    @GetMapping("/products")
    public List<ProductResponse> listPublishedProducts() {
        return ProductRestMapper.toPublicResponseList(publicAccessPort.listPublishedProducts());
    }
}
