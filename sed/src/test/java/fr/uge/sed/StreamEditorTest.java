package fr.uge.sed;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StreamEditorTest {
  @Nested
  public class Q1 {
    @Test
    public void transform() throws IOException {
      var text = """
          foo
          bar
          baz
          """;
      var writer = new StringWriter();
      var editor = new StreamEditor();
      editor.transform(new LineNumberReader(new StringReader(text)), writer);
      assertEquals("""
          foo
          bar
          baz
          """, writer.toString());
    }

    @Test
    public void transformWindows() throws IOException {
      var text = """
          foo\r
          bar\r
          baz\r
          """;
      var writer = new StringWriter();
      var editor = new StreamEditor();
      editor.transform(new LineNumberReader(new StringReader(text)), writer);
      assertEquals("""
          foo
          bar
          baz
          """, writer.toString());
    }

    @Test
    public void transformEmpty() throws IOException {
      var text = "";
      var writer = new StringWriter();
      var editor = new StreamEditor();
      editor.transform(new LineNumberReader(new StringReader(text)), writer);
      assertEquals("", writer.toString());
    }

    @Test
    public void transformPreconditions() throws IOException {
      var editor = new StreamEditor();
      assertAll(
          () -> assertThrows(NullPointerException.class, () -> editor.transform(null, new StringWriter())),
          () -> assertThrows(NullPointerException.class, () -> editor.transform(new LineNumberReader(new StringReader("")), null))
      );
    }

    @Test
    public void defaultConstructorAndMethodTransformMustBePublic() throws NoSuchMethodException {
      var constructor = StreamEditor.class.getConstructor();
      var transforms = Arrays.stream(StreamEditor.class.getDeclaredMethods())
          .filter(m -> m.getName().equals("transform"))
          .toList();
      assertAll(
          () -> assertTrue(Modifier.isPublic(constructor.getModifiers())),
          () -> assertTrue(transforms.stream().allMatch(m -> Modifier.isPublic(m.getModifiers())))
      );
    }
  }


  @Nested
  public class Q2 {
    @Test
    public void lineDelete() throws IOException {
      var text = """
          foo
          bar
          baz
          """;
      var writer = new StringWriter();
      var command = StreamEditor.lineDelete(2);
      var editor = new StreamEditor(command);
      editor.transform(new LineNumberReader(new StringReader(text)), writer);
      assertEquals("""
          foo
          baz
          """, writer.toString());
    }

    @Test
    public void lineDeletePrecondition() throws IOException {
      assertThrows(IllegalArgumentException.class, () -> StreamEditor.lineDelete(-1));
    }

    @Test
    public void methodLineDeleteMethodAndReturnTypeMustBePublic() throws NoSuchMethodException {
      var lineDelete = StreamEditor.class.getMethod("lineDelete", int.class);
      assertAll(
          () -> assertTrue(Modifier.isPublic(lineDelete.getModifiers())),
          () -> assertTrue(Modifier.isPublic(lineDelete.getReturnType().getModifiers()))
      );
    }

    @Test
    public void lineDeleteRecordPrecondition() throws NoSuchMethodException, IllegalAccessException {
      var recordType = StreamEditor.class.getMethod("lineDelete", int.class).getReturnType();
      if (!recordType.isRecord()) {
        return;
      }
      var lookup = MethodHandles.lookup();
      var constructor = lookup.findConstructor(recordType, MethodType.methodType(void.class, int.class));
      assertThrows(IllegalArgumentException.class, () -> constructor.invoke(-1));
    }

    @Test
    public void fieldsMustBePrivateFinal() {
      assertTrue(Arrays.stream(StreamEditor.class.getDeclaredFields())
          .filter(field -> !Modifier.isStatic(field.getModifiers()))
          .allMatch(f -> Modifier.isPrivate(f.getModifiers()) && Modifier.isFinal(f.getModifiers())));
    }
  }


  @Nested
  public class Q3 {
    @Test
    public void main() {
      var output = new ByteArrayOutputStream();
      var oldOut = System.out;
      System.setOut(new PrintStream(output, true, UTF_8));
      try {
        Path tmp;
        try {
          tmp = Files.createTempFile("stream-editor-", "");
        } catch (IOException e) {
          throw new AssertionError(e);
        }
        try {
          try {
            Files.writeString(tmp, """
                bar
                foo
                whizz
                fuzz
                """);
          } catch (IOException e) {
            throw new AssertionError(e);
          }

          StreamEditor.main(new String[] { tmp.toString() });

        } finally {
          try {
            Files.delete(tmp);
          } catch (IOException e) {
            throw new AssertionError(e);
          }
        }

      } finally {
        System.setOut(oldOut);
      }

      assertEquals("""
          bar
          whizz
          fuzz
          """, output.toString(UTF_8));
    }
  }


  @Nested
  public class Q4 {
    @Test
    public void actionShouldBePublic() {
      var action = Arrays.stream(StreamEditor.class.getDeclaredClasses())
          .filter(clazz -> clazz.getSimpleName().equals("Action"))
          .findFirst()
          .orElseThrow();
      assertTrue(Modifier.isPublic(action.getModifiers()));
    }
  }

  @Nested
  public class Q5 {
    @Test
    public void findAndDelete() throws IOException {
      var text = """
          foo
          bar
          baz
          """;
      var writer = new StringWriter();
      var command = StreamEditor.findAndDelete(Pattern.compile("foo|bar"));
      var editor = new StreamEditor(command);
      editor.transform(new LineNumberReader(new StringReader(text)), writer);
      assertEquals("""
          baz
          """, writer.toString());
    }

    @Test
    public void findAndDelete2() throws IOException {
      var text = """
          foo
          bar hello whizz
          baz
          """;
      var writer = new StringWriter();
      var command = StreamEditor.findAndDelete(Pattern.compile("hello"));
      var editor = new StreamEditor(command);
      editor.transform(new LineNumberReader(new StringReader(text)), writer);
      assertEquals("""
          foo
          baz
          """, writer.toString());
    }

    @Test
    public void findAndDeletePrecondition() throws IOException {
      assertThrows(NullPointerException.class, () -> StreamEditor.findAndDelete(null));
    }

    @Test
    public void findAndDeleteRecordPrecondition() throws NoSuchMethodException, IllegalAccessException {
      var recordType = StreamEditor.class.getMethod("findAndDelete", Pattern.class).getReturnType();
      if (!recordType.isRecord()) {
        return;
      }
      var lookup = MethodHandles.lookup();
      var constructor = lookup.findConstructor(recordType, MethodType.methodType(void.class, Pattern.class));
      assertThrows(NullPointerException.class, () -> constructor.invoke(null));
    }

    @Test
    public void methodFindAndDeleteMethodAndReturnTypeMustBePublic() throws NoSuchMethodException {
      var findAndDelete = StreamEditor.class.getMethod("findAndDelete", Pattern.class);
      assertAll(
          () -> assertTrue(Modifier.isPublic(findAndDelete.getModifiers())),
          () -> assertTrue(Modifier.isPublic(findAndDelete.getReturnType().getModifiers()))
      );
    }
  }

  @Nested
  public class Q6 {
    @Test
    public void methodLineDeleteMustReturnALambda() {
      var command = StreamEditor.lineDelete(10);
      assertTrue(command.getClass().isHidden());
    }

    @Test
    public void methodFindAndDeleteMustReturnALambda() {
      var command = StreamEditor.findAndDelete(Pattern.compile("hello"));
      assertTrue(command.getClass().isHidden());
    }

    @Test
    public void commandMustBeAnnotatedWithFunctionalInterface() {
      assertTrue(StreamEditor.Command.class.isAnnotationPresent(FunctionalInterface.class));
    }
  }

  @Nested
  public class Q7 {
    @Test
    public void actionShouldBeSealed() {
      var action = Arrays.stream(StreamEditor.class.getDeclaredClasses())
          .filter(clazz -> clazz.getSimpleName().equals("Action"))
          .findFirst()
          .orElseThrow();
      assertTrue(action.isSealed());
    }
  }


  @Nested
  public class Q8 {
    @Test
    public void substitute() throws IOException {
      var text = """
          foo
          bar baz
          whizz
          """;
      var writer = new StringWriter();
      var command = StreamEditor.substitute(Pattern.compile("bar|baz"), "hello");
      var editor = new StreamEditor(command);
      editor.transform(new LineNumberReader(new StringReader(text)), writer);
      assertEquals("""
          foo
          hello hello
          whizz
          """, writer.toString());
    }

    @Test
    public void substitutePrecondition() {
      assertAll(
          () -> assertThrows(NullPointerException.class, () -> StreamEditor.substitute(null, "hello")),
          () -> assertThrows(NullPointerException.class, () -> StreamEditor.substitute(Pattern.compile("hello"), null))
      );
    }

    @Test
    public void methodSubstituteMethodAndReturnTypeMustBePublic() throws NoSuchMethodException {
      var substitute = StreamEditor.class.getMethod("substitute", Pattern.class, String.class);
      assertAll(
          () -> assertTrue(Modifier.isPublic(substitute.getModifiers())),
          () -> assertTrue(Modifier.isPublic(substitute.getReturnType().getModifiers()))
      );
    }
  }


//  @Nested
//  public class Q9 {
//    @Test
//    public void actionShouldBeSealed() {
//      var action = Arrays.stream(StreamEditor.class.getDeclaredClasses())
//          .filter(clazz -> clazz.getSimpleName().equals("Action"))
//          .findFirst()
//          .orElseThrow();
//      var deleteAction = Arrays.stream(action.getDeclaredClasses())
//          .filter(clazz -> clazz.getSimpleName().equals("DeleteAction"))
//          .findFirst()
//          .orElseThrow();
//      assertTrue(deleteAction.isEnum());
//    }
//  }
//
//
//  @Nested
//  public class Q10 {
//    @Test
//    public void andThen() throws IOException {
//      var text = """
//          foo
//          bar
//          baz
//          whizz
//          """;
//      var writer = new StringWriter();
//      var command1 = StreamEditor.substitute(Pattern.compile("foo"), "hello");
//      var command2 = StreamEditor.findAndDelete(Pattern.compile("baz"));
//      var editor = new StreamEditor(command1.andThen(command2));
//      editor.transform(new LineNumberReader(new StringReader(text)), writer);
//      assertEquals("""
//          hello
//          bar
//          whizz
//          """, writer.toString());
//    }
//
//    @Test
//    public void andThenCombined() throws IOException {
//      var text = """
//          foo
//          bar
//          """;
//      var writer = new StringWriter();
//      var command1 = StreamEditor.substitute(Pattern.compile("foo"), "foo2");
//      var command2 = StreamEditor.substitute(Pattern.compile("foo2"), "foo3");
//      var editor = new StreamEditor(command1.andThen(command2));
//      editor.transform(new LineNumberReader(new StringReader(text)), writer);
//      assertEquals("""
//          foo3
//          bar
//          """, writer.toString());
//    }
//
//    @Test
//    public void andThenNotCombined() throws IOException {
//      var text = """
//          foo
//          bar
//          """;
//      var writer = new StringWriter();
//      var command1 = StreamEditor.substitute(Pattern.compile("foo2"), "foo3");
//      var command2 = StreamEditor.substitute(Pattern.compile("foo"), "foo2");
//      var editor = new StreamEditor(command1.andThen(command2));
//      editor.transform(new LineNumberReader(new StringReader(text)), writer);
//      assertEquals("""
//          foo2
//          bar
//          """, writer.toString());
//    }
//
//    @Test
//    public void andThenButWasDeleted() throws IOException {
//      var text = """
//          foo
//          bar
//          """;
//      var writer = new StringWriter();
//      var command1 = StreamEditor.findAndDelete(Pattern.compile("foo"));;
//      var command2 = StreamEditor.substitute(Pattern.compile("foo"), "hello");
//      var editor = new StreamEditor(command1.andThen(command2));
//      editor.transform(new LineNumberReader(new StringReader(text)), writer);
//      assertEquals("""
//          bar
//          """, writer.toString());
//    }
//
//    @Test
//    public void andThenPrecondition() {
//      assertThrows(NullPointerException.class, () -> StreamEditor.lineDelete(10).andThen(null));
//    }
//  }

}