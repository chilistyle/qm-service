package qm.service.library.repo

import io.quarkus.test.TestReactiveTransaction
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.vertx.RunOnVertxContext
import io.quarkus.test.vertx.UniAsserter
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import qm.service.library.model.Book

@QuarkusTest
class BookRepositoryTest {

    @Inject
    lateinit var bookRepository: BookRepository

    private lateinit var testBook: Book

    @BeforeEach
    fun setup() {
        testBook = Book().apply {
            title = "Modern Quarkus Architecture"
            author = "Kotlin Expert"
            isbn = "123-456-789"
        }
    }

    @Test
    @RunOnVertxContext
    @TestReactiveTransaction
    fun `should save and find book`(asserter: UniAsserter) {
        val book = Book().apply {
            title = "Modern Quarkus"
            author = "Kotlin Dev"
            isbn = "555-666"
        }

        asserter.execute<Book> { bookRepository.persistAndFlush(book) }

        asserter.assertThat(
            { bookRepository.findByAuthor("Kotlin Dev") },
            { list ->
                assertEquals(1, list.size)
                assertEquals("Modern Quarkus", list[0].title)
            }
        )
    }

    @Test
    @RunOnVertxContext
    @TestReactiveTransaction
    fun `should save and find book by ID`(asserter: UniAsserter) {
        asserter.execute<Book> { bookRepository.persistAndFlush(testBook) }

        asserter.assertThat(
            { bookRepository.findByIdReactive(testBook.id!!) },
            { foundBook ->
                assertNotNull(foundBook)
                assertEquals(testBook.title, foundBook.title)
            }
        )
    }

    @Test
    @RunOnVertxContext
    @TestReactiveTransaction
    fun `should find books by author`(asserter: UniAsserter) {
        asserter.execute<Book> { bookRepository.persistAndFlush(testBook) }

        asserter.assertThat(
            { bookRepository.findByAuthor("Kotlin Expert") },
            { list ->
                assertEquals(1, list.size)
                assertEquals("Kotlin Expert", list[0].author)
            }
        )
    }

    @Test
    @RunOnVertxContext
    @TestReactiveTransaction
    fun `should find book by ISBN`(asserter: UniAsserter) {
        asserter.execute<Book> { bookRepository.persistAndFlush(testBook) }

        asserter.assertThat(
            { bookRepository.findByIsbn("123-456-789") },
            { foundBook ->
                assertNotNull(foundBook)
                assertEquals("123-456-789", foundBook?.isbn)
            }
        )
    }
}