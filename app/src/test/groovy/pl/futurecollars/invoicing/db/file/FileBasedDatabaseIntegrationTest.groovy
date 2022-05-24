package pl.futurecollars.invoicing.db.file

import io.swagger.v3.oas.models.Paths
import pl.futurecollars.invoicing.db.AbstractDatabaseTest
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.exceptions.InvoiceNotFoundException
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class FileBasedDatabaseIntegrationTest extends AbstractDatabaseTest {

//    private Path databasePath
    def fileService = new FileService()
    def jsonService = new JsonService()
    def databasePath = File.createTempFile("database","txt")
    def idFilePath = File.createTempFile("ids","txt")
    def idService = new IdService(Path.of(idFilePath.getPath()), fileService)
//    def fileBasedDatabase =

    @Override
    Database getDatabaseInstance(){
        new FileBasedDatabase(fileService, jsonService, idService, Path.of(databasePath.getPath()))

    }


    def 'should save invoice with correct id in a correct file'() {
        given:
        Invoice invoice = TestHelpers.invoice(1)
        when:
        getDatabaseInstance().save(invoice)
        then:
        1 == invoice.getId()
        1 == Files.readAllLines(Path.of(databasePath.getPath())).size()
        Optional.of(invoice) == getDatabaseInstance().getById(1)
    }
}
