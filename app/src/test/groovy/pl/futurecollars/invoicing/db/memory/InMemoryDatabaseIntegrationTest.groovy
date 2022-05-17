package pl.futurecollars.invoicing.db.memory

import pl.futurecollars.invoicing.db.AbstractDatabaseTest
import pl.futurecollars.invoicing.db.Database

class InMemoryDatabaseIntegrationTest extends AbstractDatabaseTest {

    @Override
    Database getDatabaseInstance() {
        new InMemoryDatabase()
    }
}

//    private InMemoryDatabase database
//    private Invoice invoice1
//    private Invoice invoice2
//    private Invoice invoice3
//
//
//    void setup() {
//        database = new InMemoryDatabase()
//        invoice1 = TestHelpers.invoice(1)
//        invoice2 = TestHelpers.invoice(2)
//        invoice3 = TestHelpers.invoice(3)
//    }


//    def "should save invoice into a database with correct id"() {
//        when:
//        database.save(invoice2)
//        database.save(invoice1)
//        then:
//        invoice2.getId() == 1
//        invoice1.getId() == 2
//    }
//
//    def "should return correct invoice given the id and return a list of all invoices when getAll is called"() {
//        when:
//        database.save(invoice3)
//        database.save(invoice1)
//        database.save(invoice2)
//        def list = [invoice3, invoice1, invoice2]
//        then:
//        database.getById(1) == Optional.of(invoice3)
//        database.getById(2) == Optional.of(invoice1)
//        database.getById(3) == Optional.of(invoice2)
//        database.getAll() == list
//    }
//
//    def "should throw an Exception if id of invoice being updated doesn't exist"() {
//        when:
//        database.save(invoice1)
//        database.save(invoice2)
//        database.update(3, invoice3)
//        then:
//        thrown(InvoiceNotFoundException)
//    }
//
//    def "should update an invoice in database under given id"() {
//        when:
//        database.save(invoice1)
//        database.save(invoice2)
//        database.update(2, invoice3)
//        then:
//        database.getById(2) == Optional.of(invoice3)
//    }
//
//    def "should delete correct invoice from database"() {
//        when:
//        database.save(invoice3)
//        database.save(invoice1)
//        database.save(invoice2)
//        database.delete(3)
//        then:
//        database.getAll().size() == 2
//        database.getAll() == [invoice3, invoice1]
//    }
//
//    def "should throw an exception if an invoice with given id does not exist while deleting"(){
//        when:
//        database.delete(1)
//        then:
//        thrown(InvoiceNotFoundException)
//    }
//}
