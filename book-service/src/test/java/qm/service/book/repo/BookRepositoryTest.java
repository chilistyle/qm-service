package qm.service.book.repo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import qm.service.book.model.Book;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BookRepositoryTest -
 */
@DataJpaTest
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setTitle("Clean Code");
        testBook.setAuthor("Robert Martin");
        testBook.setIsbn("978-0132350884");
        testBook.setPrice(new BigDecimal("40.00"));
        bookRepository.save(testBook);
    }

    @Test
    void findByIsbn_ShouldReturnBook_WhenIsbnExists() {
        Optional<Book> found = bookRepository.findByIsbn("978-0132350884");

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Clean Code");
    }

    @Test
    void existsByIsbn_ShouldReturnTrue_WhenIsbnExists() {
        boolean exists = bookRepository.existsByIsbn("978-0132350884");
        assertThat(exists).isTrue();
    }

    @Test
    void findByAuthorContainingIgnoreCase_ShouldReturnPagedBooks() {
        Book anotherBook = new Book();
        anotherBook.setTitle("Clean Architecture");
        anotherBook.setAuthor("Robert Martin");
        anotherBook.setIsbn("978-0134494166");
        bookRepository.save(anotherBook);

        Page<Book> page = bookRepository.findByAuthorContainingIgnoreCase("robert", PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Clean Code", "Clean Architecture");
    }

    @Test
    void findByIsbn_ShouldReturnEmpty_WhenIsbnDoesNotExist() {
        Optional<Book> found = bookRepository.findByIsbn("000-0000000000");
        assertThat(found).isEmpty();
    }
}
