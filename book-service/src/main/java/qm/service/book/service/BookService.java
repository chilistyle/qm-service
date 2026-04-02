package qm.service.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qm.service.book.dto.BookRequestDTO;
import qm.service.book.exception.ResourceNotFoundException;
import qm.service.book.model.Book;
import qm.service.book.repo.BookRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * BookService -
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    public Page<Book> findAll(String author, Pageable pageable) {
        if (author != null && !author.isBlank()) {
            return bookRepository.findByAuthorContainingIgnoreCase(author, pageable);
        }
        return bookRepository.findAll(pageable);
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    public Book findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "isbn", isbn));
    }

    @Transactional
    public Book create(BookRequestDTO dto) {
        if (bookRepository.existsByIsbn(dto.isbn())) {
            throw new IllegalArgumentException("Book with ISBN " + dto.isbn() + " already exists");
        }

        Book book = new Book();
        mapDtoToEntity(dto, book);
        return bookRepository.save(book);
    }

    @Transactional
    public Book update(Long id, BookRequestDTO dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        mapDtoToEntity(dto, book);
        return bookRepository.save(book);
    }

    @Transactional
    public Book updatePrice(Long id, BigDecimal newPrice) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        book.setPrice(newPrice);
        return bookRepository.save(book);
    }

    @Transactional
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book", "id", id);
        }
        bookRepository.deleteById(id);
    }

    public Map<String, Long> countBooksByAuthor() {
        return bookRepository.findAll().stream()
                .collect(Collectors.groupingBy(Book::getAuthor, Collectors.counting()));
    }

    private void mapDtoToEntity(BookRequestDTO dto, Book book) {
        book.setTitle(dto.title());
        book.setAuthor(dto.author());
        book.setIsbn(dto.isbn());
        book.setPrice(dto.price());
    }
}
