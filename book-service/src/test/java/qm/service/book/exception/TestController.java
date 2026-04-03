package qm.service.book.exception;

import org.springframework.web.bind.annotation.*;
import qm.service.book.dto.BookRequestDTO;

/**
 * TestController -
 */
@RestController
@RequestMapping("/test-errors")
class TestController {
    @GetMapping("/not-found")
    public void throwNotFound() {
        throw new ResourceNotFoundException("Not found");
    }

    @GetMapping("/conflict")
    public void throwConflict() {
        throw new IllegalArgumentException("Conflict occurred");
    }

    @PostMapping("/validate")
    public void validate(@jakarta.validation.Valid @RequestBody BookRequestDTO dto) {
    }
}
