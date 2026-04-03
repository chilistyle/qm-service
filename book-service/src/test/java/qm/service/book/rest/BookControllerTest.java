package qm.service.book.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import qm.service.book.dto.BookRequestDTO;
import qm.service.book.exception.GlobalExceptionHandler;
import qm.service.book.model.Book;
import qm.service.book.service.BookService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * BookControllerTest -
 */
@WebMvcTest(BookController.class)
@Import(GlobalExceptionHandler.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllBooks_ShouldReturnPage() throws Exception {
        Book book = new Book();
        book.setTitle("Test Book");
        PageImpl<Book> page = new PageImpl<>(Collections.singletonList(book));

        when(bookService.findAll(any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/books")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Book"));
    }

    @Test
    void getBookById_ShouldReturnBook_WhenExists() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Java 21");

        when(bookService.findById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/api/v1/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java 21"));
    }

    @Test
    void getBookById_ShouldReturn404_WhenNotFound() throws Exception {
        when(bookService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/books/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void createBook_ShouldReturnCreated() throws Exception {
        BookRequestDTO dto = new BookRequestDTO("Title", "Author", "9781234567890", BigDecimal.TEN);
        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle("Title");

        when(bookService.create(any(BookRequestDTO.class))).thenReturn(savedBook);

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updatePrice_ShouldReturnOk_WhenPricePositive() throws Exception {
        Book book = new Book();
        book.setPrice(BigDecimal.valueOf(100));

        when(bookService.updatePrice(eq(1L), any(BigDecimal.class))).thenReturn(book);

        mockMvc.perform(patch("/api/v1/books/1/price")
                        .param("newPrice", "100.50"))
                .andExpect(status().isOk());
    }

    @Test
    void updatePrice_ShouldReturn400_WhenPriceNegative() throws Exception {
        mockMvc.perform(patch("/api/v1/books/1/price")
                        .param("newPrice", "-1.00"))
                .andExpect(status().isBadRequest());
    }
}
