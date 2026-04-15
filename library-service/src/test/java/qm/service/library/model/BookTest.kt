package qm.service.library.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import java.math.BigDecimal
import kotlin.test.Test

class BookTest {

    @Test
    fun `should be equal when ids match`() {
        val book1 = Book().apply { id = 1L }
        val book2 = Book().apply { id = 1L }

        assertEquals(book1, book2)
        assertEquals(book1.hashCode(), book2.hashCode())
    }

    @Test
    fun `should not be equal when ids differ`() {
        val book1 = Book().apply { id = 1L }
        val book2 = Book().apply { id = 2L }

        assertNotEquals(book1, book2)
    }

    @Test
    fun `should not be equal when id is null`() {
        // The implementation requires id != null for equality
        val book1 = Book()
        val book2 = Book()

        assertNotEquals(book1, book2)
    }

    @Test
    fun `should correctly store and retrieve fields`() {
        val expectedTitle = "Kotlin in Action"
        val expectedAuthor = "Dmitry Jemerov"
        val expectedIsbn = "978-1617293290"
        val expectedPrice = BigDecimal("45.00")

        val book = Book().apply {
            title = expectedTitle
            author = expectedAuthor
            isbn = expectedIsbn
            price = expectedPrice
        }

        assertEquals(expectedTitle, book.title)
        assertEquals(expectedAuthor, book.author)
        assertEquals(expectedIsbn, book.isbn)
        assertEquals(expectedPrice, book.price)
    }

    @Test
    fun `hashCode should be consistent with class type`() {
        val book = Book().apply { id = 10L }

        // Your implementation returns javaClass.hashCode()
        assertEquals(Book::class.java.hashCode(), book.hashCode())

        val anotherBook = Book().apply { id = 20L }
        assertEquals(book.hashCode(), anotherBook.hashCode(), "Hash code should be the same for all instances of the same class per implementation")
    }
}