package pl.futurecollars.invoicing.db.memory

import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.model.Vat
import spock.lang.Specification

import java.time.LocalDate

class InMemoryDatabaseTest extends Specification {

    private InMemoryDatabase database
    private Company company1
    private Company company2
    private Company company3
    private InvoiceEntry invoiceEntry1
    private InvoiceEntry invoiceEntry2
    private InvoiceEntry invoiceEntry3
    private Invoice invoice1
    private Invoice invoice2
    private Invoice invoice3


    void setup() {
        database = new InMemoryDatabase()
        company1 = new Company(1, 11, "Koncertowa 6/8, 80-301 Gdańśk")
        company2 = new Company(2, 22, "Słowackiego 6/8, 80-302 Gdańśk")
        company3 = new Company(3, 33, "Czarnogórska 6/8, 80-303 Gdańśk")
        invoiceEntry1 = new InvoiceEntry("Bułka", BigDecimal.valueOf(100), BigDecimal.valueOf(100 * 0.08), Vat.VAT_8)
        invoiceEntry2 = new InvoiceEntry("Chleb", BigDecimal.valueOf(200), BigDecimal.valueOf(200 * 0.05), Vat.VAT_5)
        invoiceEntry3 = new InvoiceEntry("Kakao", BigDecimal.valueOf(300), BigDecimal.valueOf(300 * 0.23), Vat.VAT_23)
        invoice1 = new Invoice(LocalDate.of(2020, 10, 11), company1, company2, List.of(invoiceEntry1, invoiceEntry2, invoiceEntry3));
        invoice2 = new Invoice(LocalDate.of(2019, 11, 15), company3, company2, List.of(invoiceEntry1, invoiceEntry2, invoiceEntry3));
        invoice3 = new Invoice(LocalDate.of(2020, 12, 13), company2, company3, List.of(invoiceEntry1, invoiceEntry2,));
    }


    def "should save invoice into a database with correct id"() {
        when:
        database.save(invoice2)
        database.save(invoice1)
        then:
        invoice2.getId() == 1
        invoice1.getId() == 2
    }

    def "should return correct invoice given the id and return a list of all invoices when getAll is called"() {
        when:
        database.save(invoice3)
        database.save(invoice1)
        database.save(invoice2)
        def list = [invoice3, invoice1, invoice2]
        then:
        database.getById(1) == invoice3
        database.getById(2) == invoice1
        database.getById(3) == invoice2
        database.getAll() == list
    }

    def "should throw an Exception if id of invoice being updated doesn't exist"() {
        when:
        database.save(invoice1)
        database.save(invoice2)
        database.update(3, invoice3)
        then:
        thrown(IllegalArgumentException)
    }

    def "should update an invoice in database under given id"() {
        when:
        database.save(invoice1)
        database.save(invoice2)
        database.update(2, invoice3)
        then:
        database.getById(2) == invoice3
    }

    def "Delete"() {
        when:
        database.save(invoice3)
        database.save(invoice1)
        database.save(invoice2)
        database.delete(3)
        then:
        database.getAll().size() == 2
        database.getAll() == [invoice3, invoice1]
    }
}