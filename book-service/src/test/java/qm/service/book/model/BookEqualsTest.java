package qm.service.book.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BookEqualsTest -
 */
class BookEqualsTest {

    @Test
    void testEqualsBasedOnId() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("A");

        Book book2 = new Book();
        book2.setId(1L);
        book2.setTitle("B");

        assertThat(book1).isEqualTo(book2);
    }

    @Test
    void testHashCodeStability() {
        Book book = new Book();
        int initialHashCode = book.hashCode();

        book.setId(100L);

        assertThat(book.hashCode()).isEqualTo(initialHashCode);
    }
}
