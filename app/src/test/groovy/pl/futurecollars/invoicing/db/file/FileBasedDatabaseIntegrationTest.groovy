package pl.futurecollars.invoicing.db.file

import pl.futurecollars.invoicing.db.AbstractDatabaseTest
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.exceptions.InvoiceNotFoundException
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class FileBasedDatabaseIntegrationTest extends AbstractDatabaseTest {

    private Path databasePath

    @Override
    Database getDatabaseInstance(){
        def fileService = new FileService()
        def jsonService = new JsonService()
        def databasePath = File.createTempFile('lines', '.txt').toPath()
        def idFilePath = File.createTempFile('idfile', '.txt').toPath()
        def idService = new IdService(idFilePath, fileService)
        new FileBasedDatabase(fileService, jsonService, idService, databasePath)
    }


    def 'should save invoice with correct id in a correct file'() {
        given:
        Invoice invoice = TestHelpers.invoice(1)
        when:
        getDatabaseInstance().save(invoice)
        then:
        1 == invoice.getId()
        1 == Files.readAllLines(databasePath).size()
        Optional.of(invoice) == getDatabaseInstance().getById(1)
    }
}
