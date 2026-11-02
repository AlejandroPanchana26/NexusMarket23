# Global Exception Handler

## 1. Purpose

`GlobalExceptionHandler` (`adapters/rest/exception`) catches the exceptions of every controller and turns them into clear HTTP responses. Thanks to it, no controller needs `try/catch`, and every error has the same shape.

It is annotated with `@RestControllerAdvice`, which makes it apply to all controllers. Each method annotated with `@ExceptionHandler` handles one kind of exception.

---

## 2. Standard Error Response

```json
{
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Order not found.",
  "timestamp": "2026-10-29T21:30:00.17"
}
```

Defined by the record `ErrorResponse(int status, String error, String message, LocalDateTime timestamp)`.

---

## 3. Exception Mapping

| Exception | Origin | HTTP Status |
| --------- | ------ | ----------- |
| `EntityNotFoundException` | Domain | `404 Not Found` |
| `UnauthorizedOperationException` | Domain | `403 Forbidden` |
| `DuplicateEntityException` | Domain | `409 Conflict` |
| `BusinessRuleViolationException` | Domain | `400 Bad Request` |
| `InvalidStatusTransitionException` | Domain | `400 Bad Request` |
| `DomainException` (any other) | Domain | `400 Bad Request` |
| `MethodArgumentNotValidException` | DTO validation (`@Valid`) | `400 Bad Request`, listing the invalid fields |
| `HttpMessageNotReadableException` | Missing or malformed JSON body | `400 Bad Request` |
| `HttpRequestMethodNotSupportedException` | Wrong HTTP method | `405 Method Not Allowed` |
| `HttpMediaTypeNotSupportedException` | Body not sent as JSON | `415 Unsupported Media Type` |
| `NoResourceFoundException` | Address that does not exist | `404 Not Found` |
| `Exception` (anything else) | Unexpected error | `500 Internal Server Error` |

The domain exception hierarchy designed in the first delivery is what makes this mapping simple: each kind of business problem has its own exception class.

---

## 4. Rules

* **No internal details for unexpected errors.** A `500` response always says "An unexpected error occurred."; the real cause is written to the server log with `log.error`. This avoids showing internal information to users.
* **Request errors are not server errors.** A wrong method, a body that is not JSON or a wrong address are mistakes in the request, so they return `405`, `415` and `404`, never `500`.
* **Authentication errors happen before the controllers.** A missing or invalid token is rejected by Spring Security with `401`, and an endpoint of another role with `403` (see `Rest-security-cors.md`).
