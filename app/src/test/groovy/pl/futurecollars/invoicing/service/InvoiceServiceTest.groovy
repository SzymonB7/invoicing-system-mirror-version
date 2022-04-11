package pl.futurecollars.invoicing.service

import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

class InvoiceServiceTest extends Specification {


    private InvoiceService service
    private Database database
    private Invoice invoice

    def setup() {
        database = Mock()
        service = new InvoiceService(database)
        invoice = Mock()
    }

    def "calling save() should delegate to database save() method"() {
        when:
        service.save(invoice)
        then:
        1 * database.save(invoice)
    }

    def "calling delete() should delegate to database delete() method"() {
        given:
        def invoiceId = 123
        when:
        service.delete(invoiceId)
        then:
        1 * database.delete(invoiceId)
    }

    def "calling getById() should delegate to database getById() method"() {
        given:
        def invoiceId = 321
        when:
        service.getById(invoiceId)
        then:
        1 * database.getById(invoiceId)
    }

    def "calling getAll() should delegate to database getAll() method"() {
        when:
        service.getAll()
        then:
        1 * database.getAll()
    }

    def "calling update() should delegate to database update() method"() {
        when:
        service.update(invoice.getId(), invoice)
        then:
        1 * database.update(invoice.getId(), invoice)
    }
}

