package qm.service.library.model

import io.quarkus.hibernate.reactive.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntityBase
import jakarta.persistence.*
import java.math.BigDecimal

/**
 * Book -
 */
@Entity
@Table(name = "books")
class Book : PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Column(nullable = false)
    lateinit var title: String
    lateinit var author: String

    @Column(unique = true, length = 20)
    lateinit var isbn: String

    @Column(precision = 10, scale = 2)
    lateinit var price: BigDecimal

    companion object : PanacheCompanion<Book>

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Book) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}