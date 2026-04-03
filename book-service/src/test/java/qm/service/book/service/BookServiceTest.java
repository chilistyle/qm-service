package qm.service.book.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qm.service.book.dto.BookRequestDTO;
import qm.service.book.exception.ResourceNotFoundException;
import qm.service.book.model.Book;
import qm.service.book.repo.BookRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * BookServiceTest -
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void findByIsbn_ShouldReturnBook_WhenExists() {
        String isbn = "9781234567890";
        Book book = new Book();
        book.setIsbn(isbn);
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));

        Book result = bookService.findByIsbn(isbn);

        assertThat(result.getIsbn()).isEqualTo(isbn);
    }

    @Test
    void findByIsbn_ShouldThrowException_WhenNotFound() {
        String isbn = "000";
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findByIsbn(isbn))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book not found with isbn : '000'");
    }

    @Test
    void create_ShouldThrowException_WhenIsbnExists() {
        BookRequestDTO dto = new BookRequestDTO("Title", "Author", "123", BigDecimal.TEN);
        when(bookRepository.existsByIsbn("123")).thenReturn(true);

        assertThatThrownBy(() -> bookService.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(bookRepository, never()).save(any());
    }

    @Test
    void create_ShouldSaveBook_WhenIsbnIsUnique() {
        BookRequestDTO dto = new BookRequestDTO("Title", "Author", "123", BigDecimal.TEN);
        when(bookRepository.existsByIsbn("123")).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));

        Book result = bookService.create(dto);

        assertThat(result.getTitle()).isEqualTo("Title");
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void updatePrice_ShouldUpdate_WhenBookExists() {
        Long id = 1L;
        BigDecimal newPrice = new BigDecimal("99.99");
        Book book = new Book();
        book.setId(id);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Book result = bookService.updatePrice(id, newPrice);

        assertThat(result.getPrice()).isEqualTo(newPrice);
    }

    @Test
    void countBooksByAuthor_ShouldReturnCorrectMap() {
        Book b1 = new Book(); b1.setAuthor("Martin");
        Book b2 = new Book(); b2.setAuthor("Martin");
        Book b3 = new Book(); b3.setAuthor("Bloch");

        when(bookRepository.findAll()).thenReturn(List.of(b1, b2, b3));

        Map<String, Long> stats = bookService.countBooksByAuthor();

        assertThat(stats).hasSize(2);
        assertThat(stats.get("Martin")).isEqualTo(2L);
        assertThat(stats.get("Bloch")).isEqualTo(1L);
    }
}
