package lu.hitec.pssu.melm.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import lu.hitec.pssu.melm.exceptions.LibraryValidatorException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class LibraryValidatorTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testBuildDirectoryForLibraryNOK() {
    final String baseDirectory = this.getClass().getResource("/sample/libraries").getPath();
    final File directoryForLibrary = LibraryValidator.buildDirectoryForLibrary(baseDirectory, "emergency.fr");
    assertNotNull(directoryForLibrary);
    assertTrue(directoryForLibrary.exists());
    directoryForLibrary.delete();
  }

  @Test
  public void testBuildDirectoryForLibraryOK() {
    final String baseDirectory = this.getClass().getResource("/sample/libraries").getPath();
    final File directoryForLibrary = LibraryValidator.buildDirectoryForLibrary(baseDirectory, "emergency.lu");
    assertNotNull(directoryForLibrary);
    assertTrue(directoryForLibrary.exists());
  }

  public void testBuildDirectoryForLibraryVersionNOK() throws LibraryValidatorException {
    final String baseDirectory = this.getClass().getResource("/sample/libraries").getPath();
    final File directoryForLibraryVersion = LibraryValidator.buildDirectoryForLibraryVersion(baseDirectory, "emergency.lu", "1.0");
    assertNull(directoryForLibraryVersion);
  }

  @Test
  public void testBuildDirectoryForLibraryVersionOK() throws LibraryValidatorException {
    final String baseDirectory = this.getClass().getResource("/sample/libraries").getPath();
    final File directoryForLibrary = LibraryValidator.buildDirectoryForLibraryVersion(baseDirectory, "emergency.lu", "1.1");
    assertNotNull(directoryForLibrary);
    assertTrue(directoryForLibrary.exists());
  }

  @Test
  public void testExtractIconFilePathFromXML() throws IOException, XPathExpressionException, ParserConfigurationException, SAXException,
      URISyntaxException {
    final File xmlFile = new File(this.getClass().getResource("/sample/xml/1-EMERGENCYLU_HITEC_PSSU_V1.1.xml").toURI());
    final String iconFilePath = LibraryValidator.extractIconFilePathFromXML(xmlFile);
    assertEquals("icon.png", iconFilePath);
    final String libraryType = LibraryValidator.extractLibraryTypePathFromXML(xmlFile);
    assertEquals("points", libraryType);
  }

  public void testGetUnzippedDirectoryForLibraryVersionNOK() throws LibraryValidatorException {
    final String baseDirectory = this.getClass().getResource("/sample/libraries").getPath();
    final File directoryForLibrary = LibraryValidator.getUnzippedDirectoryForLibraryVersion(baseDirectory, "emergency.lu", "1.0");
    assertNull(directoryForLibrary);
  }

  @Test
  public void testGetUnzippedDirectoryForLibraryVersionOK() throws LibraryValidatorException {
    final String baseDirectory = this.getClass().getResource("/sample/libraries").getPath();
    final File directoryForLibrary = LibraryValidator.getUnzippedDirectoryForLibraryVersion(baseDirectory, "emergency.lu", "1.1");
    assertNotNull(directoryForLibrary);
    assertTrue(directoryForLibrary.exists());
  }

  @Test(expected = LibraryValidatorException.class)
  public void testValidateAreasXML() throws IOException, URISyntaxException, LibraryValidatorException {
    final File xml = new File(this.getClass().getResource("/sample/xml/areas-1.1.xml").toURI());
    final File xsd = new File(this.getClass().getResource(LibraryValidator.XSD_PATH).toURI());
    LibraryValidator.validateXMLwithXSD(xml, xsd);
  }

  @Test(expected = LibraryValidatorException.class)
  public void testValidateLibraryBrokenXML() throws IOException, LibraryValidatorException {
    final String baseDirectory = this.getClass().getResource("/sample/libraries").getPath();
    final String xsdPath = this.getClass().getResource(LibraryValidator.XSD_PATH).getPath();
    LibraryValidator.validateLibrary(xsdPath, baseDirectory, "emergency.lu.broken.xml", "1.0");
  }

  @Test(expected = LibraryValidatorException.class)
  public void testValidateLibraryIncorrectIconPath() throws IOException, LibraryValidatorException {
    final String baseDirectory = this.getClass().getResource("/sample/libraries").getPath();
    final String xsdPath = this.getClass().getResource(LibraryValidator.XSD_PATH).getPath();
    LibraryValidator.validateLibrary(xsdPath, baseDirectory, "emergency.lu.incorrect.icon.path", "1.0");
  }

  @Test(expected = LibraryValidatorException.class)
  public void testValidateLibraryNOK() throws IOException, LibraryValidatorException {
    final String baseDirectory = this.getClass().getResource("/sample/libraries").getPath();
    final String xsdPath = this.getClass().getResource(LibraryValidator.XSD_PATH).getPath();
    LibraryValidator.validateLibrary(xsdPath, baseDirectory, "emergency.fr", "1.1");
  }

  @Test
  public void testValidateLibraryOK() throws IOException, LibraryValidatorException {
    final String baseDirectory = this.getClass().getResource("/sample/libraries").getPath();
    final String xsdPath = this.getClass().getResource(LibraryValidator.XSD_PATH).getPath();
    LibraryValidator.validateLibrary(xsdPath, baseDirectory, "emergency.lu", "1.1");
  }

  @Test(expected = LibraryValidatorException.class)
  public void testValidateLibraryWrongLibraryType() throws IOException, LibraryValidatorException {
    final String baseDirectory = this.getClass().getResource("/sample/libraries").getPath();
    final String xsdPath = this.getClass().getResource(LibraryValidator.XSD_PATH).getPath();
    LibraryValidator.validateLibrary(xsdPath, baseDirectory, "emergency.lu.wrong.library.type", "1.0");
  }

  @Test(expected = LibraryValidatorException.class)
  public void testValidateLibraryWrongName() throws IOException, LibraryValidatorException {
    final String baseDirectory = this.getClass().getResource("/sample/libraries").getPath();
    final String xsdPath = this.getClass().getResource(LibraryValidator.XSD_PATH).getPath();
    LibraryValidator.validateLibrary(xsdPath, baseDirectory, "emergency.lu.wrong.name", "1.0");
  }

  @Test
  public void testValidateNameAndVersion() throws Exception {
    final File xmlFile = new File(this.getClass().getResource("/sample/xml/NATO_AirTrack-1.0.xml").toURI());
    LibraryValidator.validateNameAndVersion(xmlFile, "NATO_AirTrack", "1.0");
  }

  @Test(expected = RuntimeException.class)
  public void testValidateNameAndVersion2() throws Exception {
    final File xmlFile = new File(this.getClass().getResource("/sample/xml/1-EMERGENCYLU_HITEC_PSSU_V1.1.xml").toURI());
    LibraryValidator.validateNameAndVersion(xmlFile, "NATO_AirTrack", "1.0");
  }

  @Test
  public void testValidateNATOXML() throws IOException, URISyntaxException, LibraryValidatorException {
    final File xml = new File(this.getClass().getResource("/sample/xml/NATO_AirTrack-1.0.xml").toURI());
    final File xsd = new File(this.getClass().getResource(LibraryValidator.XSD_PATH).toURI());
    LibraryValidator.validateXMLwithXSD(xml, xsd);
  }

  @Test
  public void testValidateXML() throws IOException, URISyntaxException, LibraryValidatorException {
    final File xml = new File(this.getClass().getResource("/sample/xml/1-EMERGENCYLU_HITEC_PSSU_V1.1.xml").toURI());
    final File xsd = new File(this.getClass().getResource(LibraryValidator.XSD_PATH).toURI());
    LibraryValidator.validateXMLwithXSD(xml, xsd);
  }

  @Test
  public void testValidateXMLWithMissingDescription() throws IOException, URISyntaxException, LibraryValidatorException {
    final File xml = new File(this.getClass().getResource("/sample/xml/1-EMERGENCYLU_MISSING_DESCRIPTION.xml").toURI());
    final File xsd = new File(this.getClass().getResource(LibraryValidator.XSD_PATH).toURI());
    LibraryValidator.validateXMLwithXSD(xml, xsd);
  }

  @Test(expected = LibraryValidatorException.class)
  public void testValidateXMLWithMissingUniqueCode() throws IOException, URISyntaxException, LibraryValidatorException {
    final File xml = new File(this.getClass().getResource("/sample/xml/1-EMERGENCYLU_MISSING_UNIQUE_CODE.xml").toURI());
    final File xsd = new File(this.getClass().getResource(LibraryValidator.XSD_PATH).toURI());
    LibraryValidator.validateXMLwithXSD(xml, xsd);
  }

  @Test(expected = LibraryValidatorException.class)
  public void testValidateXMLWithWrongChoiceValue() throws IOException, URISyntaxException, LibraryValidatorException {
    final File xml = new File(this.getClass().getResource("/sample/xml/1-EMERGENCYLU_WRONG_CHOICE_VALUE.xml").toURI());
    final File xsd = new File(this.getClass().getResource(LibraryValidator.XSD_PATH).toURI());
    LibraryValidator.validateXMLwithXSD(xml, xsd);
  }

  @Test(expected = LibraryValidatorException.class)
  public void testValidateXMLWithWrongDescription() throws IOException, URISyntaxException, LibraryValidatorException {
    final File xml = new File(this.getClass().getResource("/sample/xml/1-EMERGENCYLU_WRONG_DESCRIPTION.xml").toURI());
    final File xsd = new File(this.getClass().getResource(LibraryValidator.XSD_PATH).toURI());
    LibraryValidator.validateXMLwithXSD(xml, xsd);
  }

  @Test(expected = LibraryValidatorException.class)
  public void testValidateXMLWithWrongHierarchyCode() throws IOException, URISyntaxException, LibraryValidatorException {
    final File xml = new File(this.getClass().getResource("/sample/xml/1-EMERGENCYLU_WRONG_HIERARCHY_CODE.xml").toURI());
    final File xsd = new File(this.getClass().getResource(LibraryValidator.XSD_PATH).toURI());
    LibraryValidator.validateXMLwithXSD(xml, xsd);
  }

  @Test(expected = LibraryValidatorException.class)
  public void testValidateXMLWithWrongLibraryDisplayName() throws IOException, URISyntaxException, LibraryValidatorException {
    final File xml = new File(this.getClass().getResource("/sample/xml/1-EMERGENCYLU_WRONG_LIBRARY_DISPLAY_NAME.xml").toURI());
    final File xsd = new File(this.getClass().getResource(LibraryValidator.XSD_PATH).toURI());
    LibraryValidator.validateXMLwithXSD(xml, xsd);
  }

  @Test(expected = LibraryValidatorException.class)
  public void testValidateXMLWithWrongLibraryName() throws IOException, URISyntaxException, LibraryValidatorException {
    final File xml = new File(this.getClass().getResource("/sample/xml/1-EMERGENCYLU_WRONG_LIBRARY_NAME.xml").toURI());
    final File xsd = new File(this.getClass().getResource(LibraryValidator.XSD_PATH).toURI());
    LibraryValidator.validateXMLwithXSD(xml, xsd);
  }

  @Test(expected = LibraryValidatorException.class)
  public void testValidateXMLWithWrongLibraryType() throws IOException, URISyntaxException, LibraryValidatorException {
    final File xml = new File(this.getClass().getResource("/sample/xml/1-EMERGENCYLU_WRONG_LIBRARY_TYPE.xml").toURI());
    final File xsd = new File(this.getClass().getResource(LibraryValidator.XSD_PATH).toURI());
    LibraryValidator.validateXMLwithXSD(xml, xsd);
  }

  @Test(expected = LibraryValidatorException.class)
  public void testValidateXMLWithWrongLibraryVersion() throws IOException, URISyntaxException, LibraryValidatorException {
    final File xml = new File(this.getClass().getResource("/sample/xml/1-EMERGENCYLU_WRONG_LIBRARY_VERSION.xml").toURI());
    final File xsd = new File(this.getClass().getResource(LibraryValidator.XSD_PATH).toURI());
    LibraryValidator.validateXMLwithXSD(xml, xsd);
  }

  @Test(expected = LibraryValidatorException.class)
  public void testValidateXMLWithWrongUniqueCode() throws IOException, URISyntaxException, LibraryValidatorException {
    final File xml = new File(this.getClass().getResource("/sample/xml/1-EMERGENCYLU_WRONG_UNIQUE_CODE.xml").toURI());
    final File xsd = new File(this.getClass().getResource(LibraryValidator.XSD_PATH).toURI());
    LibraryValidator.validateXMLwithXSD(xml, xsd);
  }

  @Test
  public void testValidatOchaXML() throws IOException, URISyntaxException, LibraryValidatorException {
    final File xml = new File(this.getClass().getResource("/sample/xml/ocha_activity-1.0.xml").toURI());
    final File xsd = new File(this.getClass().getResource(LibraryValidator.XSD_PATH).toURI());
    LibraryValidator.validateXMLwithXSD(xml, xsd);
  }

}
