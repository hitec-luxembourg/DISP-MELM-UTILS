package lu.hitec.pssu.melm.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import lu.hitec.pssu.melm.exceptions.LibraryValidatorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public final class LibraryValidator {

  public static final String XSD_PATH = "/lu/hitec/pssu/melm/utils/xsd/mapelement-hierarchy.xsd";

  private static final Logger LOGGER = LoggerFactory.getLogger(LibraryValidator.class);

  private LibraryValidator() {
  }

  public static File buildDirectoryForLibraryVersion(@Nonnull final String baseDirectory, @Nonnull final String libraryName,
      @Nonnull final String version) {
    assert baseDirectory != null : "Base directory is null";
    assert libraryName != null : "Library name is null";
    assert version != null : "Version is null";
    final File libDir = buildDirectoryForLibrary(baseDirectory, libraryName);
    if (libDir.isDirectory() || libDir.mkdir()) {
      final File versionDir = new File(libDir, version);
      if (versionDir.isDirectory() || versionDir.mkdir()) {
        LOGGER.debug(String.format("Directory for libraryName : %s, version : %s is : %s", libraryName, version,
            versionDir.getAbsolutePath()));
        return versionDir;
      }
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(String.format("Failed to get or build the Directory for libraryName : %s, version : %s", libraryName, version));
    }

    return null;
  }

  public static void validateLibrary(@Nonnull final String xsdPath, @Nonnull final String baseDirectory, @Nonnull final String libraryName,
      @Nonnull final String version) throws LibraryValidatorException {
    assert xsdPath != null : "XSD path is null";
    assert baseDirectory != null : "Base directory is null";
    assert libraryName != null : "Library name is null";
    assert version != null : "Version is null";

    class OnlyExt implements FilenameFilter {
      String ext;

      public OnlyExt(@Nonnull final String ext) {
        assert ext != null : "Ext is null";
        this.ext = "." + ext;
      }

      @Override
      public boolean accept(@Nonnull final File dir, @Nonnull final String name) {
        assert dir != null : "Dir is null";
        assert name != null : "Name is null";
        return name.endsWith(ext);
      }
    }

    final File unzippedFolder = getUnzippedDirectoryForLibraryVersion(baseDirectory, libraryName, version);
    if (!unzippedFolder.isDirectory()) {
      final String msg = "Unzipped Folder does not exist / is not a directory";
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(msg);
      }
      throw new LibraryValidatorException(msg);
    }

    final String[] children = unzippedFolder.list(new OnlyExt("xml"));
    if (children.length != 1) {
      final String msg = String.format("There are %d xml files in the folder, one and only one is allowed", children.length);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(msg);
      }
      throw new LibraryValidatorException(msg);
    }

    final File xmlFile = new File(unzippedFolder, children[0]);
    try {
      final File xsd = new File(xsdPath);
      validateXMLwithXSD(xmlFile, xsd);
      final String iconFilePath = extractIconFilePathFromXML(xmlFile);
      final File iconFile = new File(unzippedFolder, iconFilePath);
      if (!iconFile.exists()) {
        throw new LibraryValidatorException(String.format("Icon file not found with local path : %s", iconFilePath));
      }
      validateNameAndVersion(xmlFile, libraryName, version);
    } catch (final Exception e) {
      throw new LibraryValidatorException(e.getMessage(), e);
    }
  }

  static File buildDirectoryForLibrary(@Nonnull final String baseDirectory, @Nonnull final String libraryName) {
    assert baseDirectory != null : "Base directory is null";
    assert libraryName != null : "Library name is null";
    return new File(baseDirectory, libraryName);
  }

  static String extractIconFilePathFromXML(@Nonnull final File xmlFile) throws ParserConfigurationException, SAXException,
      IOException, XPathExpressionException {
    assert xmlFile != null : "Xml file is null";
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder builder = factory.newDocumentBuilder();
    final Document doc = builder.parse(xmlFile);
    final XPathFactory xPathfactory = XPathFactory.newInstance();
    final XPath xpath = xPathfactory.newXPath();
    final XPathExpression expr = xpath.compile("/elements/description/library-icon/@file");
    final String result = (String) expr.evaluate(doc, XPathConstants.STRING);
    return result;
  }

  static String extractLibraryTypePathFromXML(final File xmlFile) throws ParserConfigurationException, SAXException, IOException,
      XPathExpressionException {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder builder = factory.newDocumentBuilder();
    final Document doc = builder.parse(xmlFile);
    final XPathFactory xPathfactory = XPathFactory.newInstance();
    final XPath xpath = xPathfactory.newXPath();
    final XPathExpression expr = xpath.compile("/elements/description/library-type");
    final String result = (String) expr.evaluate(doc, XPathConstants.STRING);
    return result;
  }

  static File getUnzippedDirectoryForLibraryVersion(@Nonnull final String baseDirectory, @Nonnull final String libraryName,
      @Nonnull final String version) throws LibraryValidatorException {
    assert baseDirectory != null : "Base directory is null";
    assert libraryName != null : "Library name is null";
    assert version != null : "Version is null";
    final File libraryRoot = buildDirectoryForLibraryVersion(baseDirectory, libraryName, version);
    return new File(libraryRoot, libraryName + "-" + version);
  }

  static void validateNameAndVersion(@Nonnull final File xmlFile, @Nonnull final String libraryName, @Nonnull final String version)
      throws Exception {
    assert xmlFile != null : "Xml file is null";
    assert libraryName != null : "Library name is null";
    assert version != null : "Version is null";
    final SAXParserFactory factory = SAXParserFactory.newInstance();
    final SAXParser saxParser = factory.newSAXParser();

    final DefaultHandler handler = new DefaultHandler() {
      String currentlyProcessedNode = "";

      @Override
      public void characters(final char ch[], final int start, final int length) throws SAXException {
        if ("library-version".equals(currentlyProcessedNode)) {
          final String libraryVersionInXML = new String(ch, start, length);
          if (!version.endsWith(libraryVersionInXML)) {
            throw new RuntimeException("Incorrect Library version in xml");
          }
        } else if ("library-name".equals(currentlyProcessedNode)) {
          final String libraryNameInXML = new String(ch, start, length);
          if (!libraryName.endsWith(libraryNameInXML)) {
            throw new RuntimeException("Incorrect Library name in xml");
          }
        }
      }

      @Override
      public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        currentlyProcessedNode = "";
      }

      @Override
      public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
          throws SAXException {
        currentlyProcessedNode = qName;
      }
    };

    saxParser.parse(xmlFile, handler);
  }

  static void validateXMLwithXSD(@Nonnull final File xmlFile, @Nonnull final File schemaFile) throws LibraryValidatorException {
    assert xmlFile != null : "XML file is null";
    assert schemaFile != null : "Schema file is null";
    final String schemaLang = "http://www.w3.org/2001/XMLSchema";

    // get validation driver:
    final SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

    // create schema by reading it from an XSD file
    try {
      final Schema schema = factory.newSchema(schemaFile);
      final Validator validator = schema.newValidator();

      // at last perform validation:
      validator.validate(new StreamSource(xmlFile));
    } catch (final Exception e) {
      final String msg = String.format("Error in validateXML %s", e.getMessage());
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(msg, e);
      }
      throw new LibraryValidatorException(msg, e);
    }
  }

}
