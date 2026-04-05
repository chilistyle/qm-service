package qm.service.library.rest

import io.smallrye.mutiny.Uni
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import qm.service.library.repo.BookRepository
import qm.service.library.model.Book

/**
 * BookResource -
 */
@Path("/api/v1/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class BookResource(val bookRepository: BookRepository) {

    @GET
    @Path("/{isbn}")
    fun getByIsbn(isbn: String): Uni<Book?> {
        return bookRepository.findByIsbn(isbn)
    }

    @GET
    fun search(@QueryParam("author") author: String?): Uni<List<Book>> {
        return if (author != null) {
            bookRepository.findByAuthor(author)
        } else {
            bookRepository.listAll()
        }
    }
}