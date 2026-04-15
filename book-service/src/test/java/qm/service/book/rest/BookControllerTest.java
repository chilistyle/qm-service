package qm.service.book.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import qm.service.book.dto.BookRequestDTO;
import qm.service.book.exception.ResourceNotFoundException;
import qm.service.book.model.Book;
import qm.service.book.service.BookService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void getAllBooks_ShouldReturnPageOfBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(new Book()));
        when(bookService.findAll(eq("Author"), any(Pageable.class))).thenReturn(bookPage);

        ResponseEntity<Page<Book>> response = bookController.getAllBooks("Author", pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(bookPage);
    }

    @Test
    void getBookById_WhenExists_ShouldReturnBook() {
        Book book = new Book();
        book.setId(1L);
        when(bookService.findById(1L)).thenReturn(Optional.of(book));

        ResponseEntity<Book> response = bookController.getBookById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(book);
    }

    @Test
    void getBookById_WhenNotExists_ShouldThrowException() {
        when(bookService.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookController.getBookById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book not found with id: 1");
    }

    @Test
    void createBook_ShouldReturnCreatedStatusAndLocation() {
        BookRequestDTO dto = new BookRequestDTO("Title", "Author", "1234567890", BigDecimal.TEN);
        Book savedBook = new Book();
        savedBook.setId(1L);
        when(bookService.create(any(BookRequestDTO.class))).thenReturn(savedBook);

        ResponseEntity<Book> response = bookController.createBook(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getBody()).isEqualTo(savedBook);
    }

    @Test
    void updateBook_ShouldReturnUpdatedBook() {
        BookRequestDTO dto = new BookRequestDTO("New Title", "Author", "1234567890", BigDecimal.TEN);
        Book updatedBook = new Book();
        when(bookService.update(eq(1L), any(BookRequestDTO.class))).thenReturn(updatedBook);

        ResponseEntity<Book> response = bookController.updateBook(1L, dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedBook);
    }

    @Test
    void updatePrice_ShouldReturnUpdatedBook() {
        BigDecimal newPrice = new BigDecimal("19.99");
        Book updatedBook = new Book();
        when(bookService.updatePrice(1L, newPrice)).thenReturn(updatedBook);

        ResponseEntity<Book> response = bookController.updatePrice(1L, newPrice);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedBook);
    }

    @Test
    void deleteBook_ShouldReturnNoContent() {
        doNothing().when(bookService).delete(1L);

        ResponseEntity<Void> response = bookController.deleteBook(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(bookService, times(1)).delete(1L);
    }

    @Test
    void getByIsbn_ShouldReturnBook() {
        String isbn = "1234567890";
        Book book = new Book();
        when(bookService.findByIsbn(isbn)).thenReturn(book);

        ResponseEntity<Book> response = bookController.getByIsbn(isbn);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(book);
    }

    @Test
    void getStats_ShouldReturnAuthorCounts() {
        Map<String, Long> stats = Collections.singletonMap("Author", 5L);
        when(bookService.countBooksByAuthor()).thenReturn(stats);

        ResponseEntity<Map<String, Long>> response = bookController.getStats();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("Author", 5L);
    }
}