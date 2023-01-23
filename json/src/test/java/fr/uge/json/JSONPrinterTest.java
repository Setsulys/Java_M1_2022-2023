package fr.uge.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("static-method")
public class JSONPrinterTest {
  private static Map<String, Object> parse(String input) {
    var typeRef = new TypeReference<LinkedHashMap<String, Object>>() {};
    var mapper = new ObjectMapper();
    try {
      return mapper.readValue(input, typeRef);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e.getMessage() + "\n while parsing " + input, e);
    }
  }

  @Nested
  public class Q1 {
    @Test
    public void testToJSONPersonPartial() {
      var person = new Person("John", "Doe");
      var personJSON = JSONPrinter.toJSON(person);
      assertEquals("John", parse(personJSON).get("firstName"));
      assertEquals("Doe", parse(personJSON).get("lastName"));
    }
    
    @Test
    public void testToJSONPerson() {
      var person = new Person("John", "Doe");
      var personJSON = JSONPrinter.toJSON(person);
      assertEquals(
          Map.of("firstName", "John", "lastName", "Doe"), parse(personJSON));
    }

    @Test
    public void testToJSONAlienPartial() {
      var alien = new Alien(100, "Saturn");
      var alienJSON = JSONPrinter.toJSON(alien);
      assertEquals("Saturn", parse(alienJSON).get("planet"));
      assertEquals(100, parse(alienJSON).get("age"));
    }
    @Test
    public void testToJSONAlien() {
      var alien = new Alien(100, "saturn");
      var alienJSON = JSONPrinter.toJSON(alien);
      assertEquals(Map.of("age", 100, "planet", "saturn"), parse(alienJSON));
    }
  }

  @Nested
  public class Q2 {
    @Test
    public void testToJSONPropertyWithAName() {
      record Book(@JSONProperty("book-title") String title,
                  @JSONProperty("book-author") String author,
                  @JSONProperty("book-price") int price) { }

      var book = new Book("The Girl with The Dragon Tattoo", "Stieg Larsson", 100);
      var bookJSON = JSONPrinter.toJSON(book);
      assertEquals(
          Map.of("book-title", "The Girl with The Dragon Tattoo", "book-author", "Stieg Larsson", "book-price", 100),
          parse(bookJSON));
    }
  }

  @Nested
  public class Q3 {
    @Test
    public void testToJSONPropertyEmpty() {
      record Book(@JSONProperty String book_title, String an_author, int price) { }

      var book = new Book("The Girl with The Dragon Tattoo", "Stieg Larsson", 100);
      var bookJSON = JSONPrinter.toJSON(book);
      assertEquals(
          Map.of("book-title", "The Girl with The Dragon Tattoo", "an_author", "Stieg Larsson", "price", 100),
          parse(bookJSON));
    }
  }
}
