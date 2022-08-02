package pl.futurecollars.invoicing.db

import pl.futurecollars.invoicing.exceptions.InvoiceNotFoundException
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

abstract class AbstractDatabaseTest extends Specification{

    Invoice invoice1 = TestHelpers.invoice(1L)
    Invoice invoice2 = TestHelpers.invoice(2L)
    Invoice invoice3 = TestHelpers.invoice(3L)
    abstract Database getDatabaseInstance()
    Database database

    def setup() {

        database = getDatabaseInstance()
        database.reset()

        assert database.getAll().isEmpty()

    }

    def "should save invoice into a database with correct id"() {
        database.reset()
        when:
        def id1 = database.save(invoice2)
        def id2 = database.save(invoice1)
        then:
        id1 == 1
        id2 == 2
    }

    def 'should get correct invoice by id and return empty optional if there is no invoice with given id'() {
        database.reset()
        when:
        database.save(invoice1)
        database.save(invoice2)
        invoice2.getBuyer().setId(3)
        invoice2.getSeller().setId(4)
        then:
        Optional.of(invoice1) == database.getById(1)
        Optional.of(invoice2) == database.getById(2)
        and:
        database.getById(4) == Optional.empty()

    }

    def 'should return list of invoices when getAll method is called'() {
        database.reset()
        when:
        database.save(invoice1)
        database.save(invoice2)
        database.save(invoice3)
        invoice2.getBuyer().setId(3)
        invoice2.getSeller().setId(4)
        invoice3.getBuyer().setId(5)
        invoice3.getSeller().setId(6)
        then:
        [invoice1, invoice2, invoice3] == database.getAll()
    }

    def 'should update correct invoice in database'() {
        database.reset()
        when:
        database.save(invoice1)
        database.save(invoice2)
        and:
        database.update(2, invoice3)
        invoice3.setId(2)
        then:
        Optional.of(invoice3) == database.getById(2)
        database.getAll().size() == 2
    }

    def 'should throw an exception if given id does not exist when updating invoice'() {
        database.reset()
        when:
        database.update(2, invoice1)
        then:
        thrown(InvoiceNotFoundException)
    }

    def 'should delete correct invoice'() {
        database.reset()
        when:
        database.save(invoice1)
        database.save(invoice2)
        database.save(invoice3)
        invoice3.getBuyer().setId(5)
        invoice3.getSeller().setId(6)
        and:
        database.delete(2)
        then:
        [invoice1, invoice3] == database.getAll()

    }

    def 'should throw an exception if given id does not exist when deleting invoice'() {
        database.reset()
        when:
        database.delete(2)
        then:
        thrown(InvoiceNotFoundException)
    }


}
