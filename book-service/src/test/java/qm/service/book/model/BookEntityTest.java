package qm.service.book.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BookEntityTest -
 */
@DataJpaTest
@ActiveProfiles("test")
class BookEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldPersistBookWithAllFields() {
        // Given
        Book book = new Book();
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");
        book.setIsbn("978-0134685991");
        book.setPrice(new BigDecimal("45.00"));

        // When
        Book savedBook = entityManager.persistAndFlush(book);

        // Then
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Effective Java");
        assertThat(savedBook.getPrice()).isEqualByComparingTo("45.00");
    }
}

