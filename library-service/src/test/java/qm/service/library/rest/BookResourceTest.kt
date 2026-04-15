package qm.service.library.rest

import io.quarkus.hibernate.reactive.panache.kotlin.Panache
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.vertx.RunOnVertxContext
import io.quarkus.test.vertx.UniAsserter
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.smallrye.mutiny.Uni
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test
import jakarta.inject.Inject
import qm.service.library.model.Book
import qm.service.library.repo.BookRepository
import java.math.BigDecimal

/**
 * BookResourceTest - 
 */
@QuarkusTest
class BookResourceTest {

    @Inject
    lateinit var bookRepository: BookRepository

    @Test
    @RunOnVertxContext
    fun `should get book by isbn`(asserter: UniAsserter) {
        val book = Book().apply {
            title = "Reactive Systems"
            author = "Quarkus Team"
            isbn = "999-888"
            price = BigDecimal("29.99")
        }

        asserter.execute<Book> {
            Panache.withTransaction { bookRepository.persist(book) }
        }

        asserter.execute<Unit> {
            given()
                .`when`().get("/api/v1/books/999-888")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("title", `is`("Reactive Systems"))

            Uni.createFrom().item(Unit)
        }

        asserter.execute<Long> {
            Panache.withTransaction { bookRepository.deleteAll() }
        }
    }

    @Test
    @RunOnVertxContext
    fun `should search books by author`(asserter: UniAsserter) {
        val book = Book().apply {
            title = "Kotlin in Action"
            author = "Hadley"
            isbn = "111-222"
        }

        asserter.execute<Book> {
            Panache.withTransaction { bookRepository.persist(book) }
        }

        asserter.execute<Unit> {
            given()
                .queryParam("author", "Hadley")
                .`when`().get("/api/v1/books")
                .then()
                .statusCode(200)
                .body("size()", `is`(1))
                .body("[0].title", `is`("Kotlin in Action"))

            Uni.createFrom().item(Unit)
        }

        asserter.execute<Long> {
            Panache.withTransaction { bookRepository.deleteAll() }
        }
    }

    @Test
    @RunOnVertxContext
    fun `should list all books when no author provided`(asserter: UniAsserter) {
        val book1 = Book().apply { title = "Book 1"; author = "A"; isbn = "1" }
        val book2 = Book().apply { title = "Book 2"; author = "B"; isbn = "2" }

        asserter.execute<Void> {
            Panache.withTransaction { bookRepository.persist(listOf(book1, book2)).replaceWithVoid() }
        }

        asserter.execute<Unit> {
            given()
                .`when`().get("/api/v1/books")
                .then()
                .statusCode(200)
                .body("size()", `is`(2))

            Uni.createFrom().item(Unit)
        }

        asserter.execute<Long> {
            Panache.withTransaction { bookRepository.deleteAll() }
        }
    }
}