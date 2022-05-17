package pl.futurecollars.invoicing.db

import pl.futurecollars.invoicing.exceptions.InvoiceNotFoundException
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

abstract class AbstractDatabaseTest extends Specification{

    Invoice invoice1 = TestHelpers.invoice(1)
    Invoice invoice2 = TestHelpers.invoice(2)
    Invoice invoice3 = TestHelpers.invoice(3)
    abstract Database getDatabaseInstance()
    Database database

    def setup() {

        database = getDatabaseInstance()
        database.reset()

        assert database.getAll().isEmpty()

    }

    def "should save invoice into a database with correct id"() {
        when:
        database.save(invoice2)
        database.save(invoice1)
        then:
        invoice2.getId() == 1
        invoice1.getId() == 2
    }

    def 'should get correct invoice by id and return empty optional if there is no invoice with given id'() {
        when:
        database.save(invoice1)
        database.save(invoice2)
        then:
        Optional.of(invoice1) == database.getById(1)
        Optional.of(invoice2) == database.getById(2)
        and:
        database.getById(4) == Optional.empty()

    }

    def 'should return list of invoices when getAll method is called'() {
        when:
        database.save(invoice3)
        database.save(invoice2)
        database.save(invoice1)
        then:
        [invoice3, invoice2, invoice1] == database.getAll()
    }

    def 'should update correct invoice in database'() {
        when:
        database.save(invoice1)
        database.save(invoice2)
        and:
        database.update(2, invoice3)
        then:
        Optional.of(invoice3) == database.getById(2)
        database.getAll().size() == 2
    }

    def 'should throw an exception if given id does not exist when updating invoice'() {
        when:
        database.update(2, invoice1)
        then:
        thrown(InvoiceNotFoundException)
    }

    def 'should delete correct invoice'() {
        when:
        database.save(invoice1)
        database.save(invoice2)
        database.save(invoice3)
        and:
        database.delete(2)
        then:
        [invoice1, invoice3] == database.getAll()

    }

    def 'should throw an exception if given id does not exist when deleting invoice'() {
        when:
        database.delete(2)
        then:
        thrown(InvoiceNotFoundException)
    }


}
