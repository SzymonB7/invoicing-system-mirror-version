package pl.futurecollars.invoicing.db.file

import pl.futurecollars.invoicing.exceptions.InvoiceNotFoundException
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class FileBasedDatabaseTest extends Specification {

    private Path databasePath
    private Path idFilePath

    private FileService fileService
    private JsonService jsonService
    private IdService idService
    private FileBasedDatabase fileBasedDatabase

    void setup() {
        databasePath = File.createTempFile('lines', '.txt').toPath()
        idFilePath = File.createTempFile('idfile', '.txt').toPath()
        fileService = new FileService()
        jsonService = new JsonService()
        idService = new IdService(idFilePath, fileService)
        fileBasedDatabase = new FileBasedDatabase(fileService, jsonService, idService, databasePath)
    }

    def 'should save invoice with correct id in a correct file'() {
        given:
        Invoice invoice = TestHelpers.invoice(1)
        when:
        fileBasedDatabase.save(invoice)
        then:
        1 == invoice.getId()
        1 == Files.readAllLines(databasePath).size()
        Optional.of(invoice) == fileBasedDatabase.getById(1)
    }

    def 'should get correct invoice by id and return empty optional if there is no invoice with given id'() {
        given:
        Invoice invoice1 = TestHelpers.invoice1
        Invoice invoice2 = TestHelpers.invoice2
        when:
        fileBasedDatabase.save(invoice1)
        fileBasedDatabase.save(invoice2)
        then:
        Optional.of(invoice1) == fileBasedDatabase.getById(1)
        Optional.of(invoice2) == fileBasedDatabase.getById(2)
        and:
        fileBasedDatabase.getById(4) == Optional.empty()

    }

    def 'should return list of invoices when getAll method is called'() {
        given:
        Invoice invoice1 = TestHelpers.invoice1
        Invoice invoice2 = TestHelpers.invoice2
        Invoice invoice3 = TestHelpers.invoice3
        when:
        fileBasedDatabase.save(invoice3)
        fileBasedDatabase.save(invoice2)
        fileBasedDatabase.save(invoice1)
        then:
        [invoice3, invoice2, invoice1] == fileBasedDatabase.getAll()
    }

    def 'should update correct invoice in database'() {
        given:
        Invoice invoice1 = TestHelpers.invoice1
        Invoice invoice2 = TestHelpers.invoice2
        Invoice invoice3 = TestHelpers.invoice3
        when:
        fileBasedDatabase.save(invoice1)
        fileBasedDatabase.save(invoice2)
        and:
        fileBasedDatabase.update(2, invoice3)
        then:
        Optional.of(invoice3) == fileBasedDatabase.getById(2)
        fileBasedDatabase.getAll().size() == 2
    }

    def 'should throw an exception if given id does not exist when updating invoice'() {
        when:
        fileBasedDatabase.update(2, TestHelpers.invoice1)
        then:
        thrown(InvoiceNotFoundException)
    }

    def 'should delete correct invoice'() {
        given:
        Invoice invoice1 = TestHelpers.invoice1
        Invoice invoice2 = TestHelpers.invoice2
        Invoice invoice3 = TestHelpers.invoice3
        when:
        fileBasedDatabase.save(invoice1)
        fileBasedDatabase.save(invoice2)
        fileBasedDatabase.save(invoice3)
        and:
        fileBasedDatabase.delete(2)
        then:
        [invoice1, invoice3] == fileBasedDatabase.getAll()

    }

    def 'should throw an exception if given id does not exist when deleting invoice'() {
        when:
        fileBasedDatabase.delete(2)
        then:
        thrown(InvoiceNotFoundException)
    }
}
