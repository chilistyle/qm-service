package qm.service.book.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import qm.service.book.config.RequiresRole;
import qm.service.book.dto.BookRequestDTO;
import qm.service.book.exception.ResourceNotFoundException;
import qm.service.book.model.Book;
import qm.service.book.service.BookService;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;

/**
 * BookController -
 */
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Validated
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<Page<Book>> getAllBooks(
            @RequestParam(name = "author", required = false) String author,
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        return ResponseEntity.ok(bookService.findAll(author, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable("id") Long id) {
        return bookService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    @PostMapping
    @RequiresRole("ADMIN")
    public ResponseEntity<Book> createBook(@Valid @RequestBody BookRequestDTO dto) {
        Book savedBook = bookService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedBook.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedBook);
    }

    @PutMapping("/{id}")
    @RequiresRole("ADMIN")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequestDTO dto) {
        return ResponseEntity.ok(bookService.update(id, dto));
    }

    @PatchMapping("/{id}/price")
    @RequiresRole("ADMIN")
    public ResponseEntity<Book> updatePrice(
            @PathVariable("id") Long id,
            @Positive @RequestParam("newPrice") BigDecimal newPrice
    ) {
        return ResponseEntity.ok(bookService.updatePrice(id, newPrice));
    }

    @DeleteMapping("/{id}")
    @RequiresRole("ADMIN")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Book> getByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.findByIsbn(isbn));
    }

    @GetMapping("/stats/count-by-author")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(bookService.countBooksByAuthor());
    }
}
