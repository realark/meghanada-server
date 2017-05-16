package meghanada.formatter;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.google.googlejavaformat.java.JavaFormatterOptions;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

public class JavaFormatter {

  private static final Pattern NEW_LINE = Pattern.compile("\n");
  private static final Formatter googleFormatter =
      new Formatter(JavaFormatterOptions.defaultOptions());

  private JavaFormatter() {}

  public static Properties getPropertiesFromXML(File xmlFile) {
    final XMLInputFactory factory = XMLInputFactory.newInstance();
    final Properties properties = new Properties();

    try (final InputStream in = new FileInputStream(xmlFile)) {
      final XMLStreamReader reader = factory.createXMLStreamReader(in);
      while (reader.hasNext()) {
        if (reader.isStartElement() && reader.getLocalName().equals("setting")) {
          final String id = reader.getAttributeValue(0);
          final String value = reader.getAttributeValue(1);
          properties.put(id, value);
        }
        reader.next();
      }
      reader.close();
    } catch (IOException | XMLStreamException e) {
      throw new RuntimeException(e);
    }
    return properties;
  }

  public static String formatGoogleStyle(final String content) {
    try {
      return googleFormatter.formatSource(content);
    } catch (FormatterException e) {
      throw new RuntimeException(e);
    }
  }

  public static String formatEclipseStyle(final Properties prop, final String content) {
    final CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(prop);
    final IDocument document = new Document(content);
    try {
      final TextEdit textEdit =
          codeFormatter.format(
              CodeFormatter.K_COMPILATION_UNIT | CodeFormatter.F_INCLUDE_COMMENTS,
              content,
              0,
              content.length(),
              0,
              null);
      if (textEdit != null) {
        textEdit.apply(document);
      } else {
        return content;
      }
    } catch (final BadLocationException e) {
      throw new RuntimeException(e);
    }

    return ensureCorrectNewLines(document.get());
  }

  private static String ensureCorrectNewLines(final String content) {
    final String newLine = System.getProperty("line.separator");

    if (content.contains("\n") && !content.contains(newLine)) {
      return NEW_LINE.matcher(content).replaceAll(newLine);
    }
    return content;
  }
}
