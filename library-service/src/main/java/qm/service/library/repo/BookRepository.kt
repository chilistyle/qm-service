package qm.service.library.repo

import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepository
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import qm.service.library.model.Book

/**
 * BookRepository -
 */
@ApplicationScoped
class BookRepository : PanacheRepository<Book> {

    fun findByIdReactive(id: Long): Uni<Book> {
        return findById(id)
    }

    fun findByAuthor(authorName: String): Uni<List<Book>> =
        find("author", authorName).list()

    fun findByIsbn(isbn: String): Uni<Book?> {
        return find("isbn", isbn).firstResult()
    }
}