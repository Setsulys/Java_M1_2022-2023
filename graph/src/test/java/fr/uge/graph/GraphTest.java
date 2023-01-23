package fr.uge.graph;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import fr.uge.graph.Graph.EdgeConsumer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("static-method")
public class GraphTest {
  @Nested
  public class Q2 {
    @Test
    public void createGraph() {
      var graph = new MatrixGraph<Integer>(50);
      assertNotNull(graph);
    }

    @Test
    public void createGraphSignature() {
      Graph<String> graph = new MatrixGraph<String>(50);
      assertNotNull(graph);
    }

    @Test
    public void createEmptyGraph() {
      var graph = new MatrixGraph<>(0);
      assertNotNull(graph);
    }

    @Test
    public void invalidNodeCount() {
      assertThrows(IllegalArgumentException.class, () -> new MatrixGraph<>(-17));
    }
  }


  interface GraphFactory {
    <T> Graph<T> createGraph(int vertexCount);
  }
  static Stream<GraphFactory> graphFactoryProvider() {
    return Stream.of(Graph::createMatrixGraph/*, Graph::createNodeMapGraph*/);
  }

  @Nested
  public class Q3 {
    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void createGraph(GraphFactory graphFactory) {
      var graph = graphFactory.createGraph(50);
      assertNotNull(graph);
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void createGraphSignature(GraphFactory graphFactory) {
      Graph<String> graph = graphFactory.<String>createGraph(50);
      assertNotNull(graph);
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void createEmptyGraph(GraphFactory graphFactory) {
      var graph = graphFactory.createGraph(0);
      assertNotNull(graph);
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void invalidNodeCount(GraphFactory graphFactory) {
      assertThrows(IllegalArgumentException.class, () -> graphFactory.createGraph(-17));
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void implementationHidden(GraphFactory factory) {
      var graph = factory.createGraph(10);
      var modifiers = graph.getClass().getModifiers();
      assertFalse(Modifier.isPublic(modifiers));
      assertTrue(Modifier.isFinal(modifiers));
    }

    @Test
    public void implementationHidden2() throws NoSuchMethodException {
      var method = Graph.class.getMethod("createMatrixGraph", int.class);
      var returnType = method.getReturnType();
      assertTrue(Modifier.isInterface(returnType.getModifiers()));
    }
  }


  @Nested
  public class Q4 {

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void getWeightEmpty(GraphFactory factory) {
      var nodeCount = 20;
      var graph = factory.createGraph(nodeCount);
      for (var i = 0; i < nodeCount; i++) {
        for (var j = 0; j < nodeCount; j++) {
          assertTrue(graph.getWeight(i, j).isEmpty());
        }
      }
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void hasEdgeValid(GraphFactory factory) {
      var graph = factory.createGraph(5);
      assertAll(
          () -> assertThrows(IndexOutOfBoundsException.class, () -> graph.getWeight(-1, 3)),
          () -> assertThrows(IndexOutOfBoundsException.class, () -> graph.getWeight(2, -1)),
          () -> assertThrows(IndexOutOfBoundsException.class, () -> graph.getWeight(5, 2)),
          () -> assertThrows(IndexOutOfBoundsException.class, () -> graph.getWeight(3, 5))
      );
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void addEdge(GraphFactory factory) {
      var graph = factory.<Integer>createGraph(7);
      graph.addEdge(3, 4, 2);
      assertEquals(2, (int) graph.getWeight(3, 4).orElseThrow());
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void addEdgeWithAString(GraphFactory factory) {
      var graph = factory.<String>createGraph(10);
      graph.addEdge(7, 8, "hello");
      assertAll(
          () -> assertEquals("hello", graph.getWeight(7, 8).orElseThrow()),
          () -> assertFalse(graph.getWeight(4, 3).isPresent())
      );
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void addEdgeNullWeight(GraphFactory factory) {
      var graph = factory.<Integer>createGraph(7);
      assertThrows(NullPointerException.class, () -> graph.addEdge(3, 4, null));
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void addEdgeTwice(GraphFactory factory) {
      var graph = factory.<String>createGraph(7);
      graph.addEdge(3, 4, "foo");
      graph.addEdge(3, 4, "bar");
      assertEquals("bar", graph.getWeight(3, 4).orElseThrow());
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void addEdgeValid(GraphFactory factory) {
      var graph = factory.createGraph(5);
      assertAll(
          () -> assertThrows(IndexOutOfBoundsException.class, () -> graph.addEdge(-1, 3, 7)),
          () -> assertThrows(IndexOutOfBoundsException.class, () -> graph.addEdge(2, -1, 8)),
          () -> assertThrows(IndexOutOfBoundsException.class, () -> graph.addEdge(5, 2, 9)),
          () -> assertThrows(IndexOutOfBoundsException.class, () -> graph.addEdge(3, 5, 10)));
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void addEdgeALot(GraphFactory factory) {
      var graph = factory.createGraph(17);
      var random = ThreadLocalRandom.current();
      for (var index = 0; index < 1000; index++) {
        var i = random.nextInt(17);
        var j = random.nextInt(17);
        var value = random.nextInt(10_000) - 5_000;
        graph.addEdge(i, j, value);
        assertEquals(value, (int) graph.getWeight(i, j).orElseThrow());
      }
    }
  }

  @Nested
  public class Q5 {

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void edgesOneEdge(GraphFactory factory) {
      var graph = factory.createGraph(3);
      graph.addEdge(1, 0, 2);
      graph.edges(0, (src, dst, weight) -> {
        assertEquals(1, src);
        assertEquals(0, dst);
        assertEquals(2, (int) weight);
      });
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void edgeConsumerIsCalled(GraphFactory factory) {
      var graph = factory.<Integer>createGraph(20);
      graph.addEdge(13, 0, 42);
      graph.addEdge(0, 13, 747);
      var box = new Object() { int weight; };
      graph.edges(13, (src, dst, weight) -> box.weight = weight);
      assertEquals(42, box.weight);
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void edgesOnEdge(GraphFactory factory) {
      var graph = factory.<String>createGraph(13);
      graph.addEdge(3, 7, "foo");
      graph.edges(3, (src, dst, weight) -> {
        assertEquals(3, src);
        assertEquals(7, dst);
        assertEquals("foo", weight);
      });
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void edgesNoEdge(GraphFactory factory) {
      var graph = factory.<Integer>createGraph(17);
      graph.edges(0, new EdgeConsumer<Object>() {
        @Override
        public void edge(int src, int dst, Object weight) {
          fail("should not be called");
        }
      });
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void edgesPreconditions(GraphFactory factory) {
      assertAll(
          () -> assertThrows(IndexOutOfBoundsException.class, () -> factory.createGraph(3).edges(-1, (_1, _2, _3) -> {})),
          () -> assertThrows(IndexOutOfBoundsException.class, () -> factory.createGraph(3).edges(3, (_1, _2, _3) -> {})),
          () -> assertThrows(NullPointerException.class, () -> factory.createGraph(3).edges(0, null))
      );

    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void testEdgesALot(GraphFactory factory) {
      var nodeCount = 200;
      var graph = factory.createGraph(nodeCount);
      for (var i = 0; i < nodeCount; i++) {
        for (var j = 0; j < nodeCount; j++) {
          graph.addEdge(i, j, i % 10 + j);
        }
      }
      assertTimeout(Duration.ofMillis(2_000), () -> {
        for (var i = 0; i < nodeCount; i++) {
          graph.edges(i, (src, dst, weight) -> {
            assertEquals(src % 10 + dst, (int) weight);
          });
        }
      });
    }
  }


  @Nested
  public class Q6 {

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void neighborsSimple(GraphFactory factory) {
      var graph = factory.<Integer>createGraph(6);
      graph.addEdge(1, 2, 222);
      graph.addEdge(1, 5, 555);

      var iterator = graph.neighborIterator(1);
      assertEquals(2, (int) iterator.next());
      assertEquals(5, (int) iterator.next());
      assertThrows(NoSuchElementException.class, iterator::next);
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void neighborsSignature(GraphFactory factory) {
      Graph<Double> graph1 = factory.<Double>createGraph(6);
      graph1.addEdge(1, 2, 3.0);
      Iterator<Integer> iterator1 = graph1.neighborIterator(1);
      assertEquals(2, iterator1.next());

      Graph<String> graph2 = factory.<String>createGraph(6);
      graph2.addEdge(1, 3, "graph");
      Iterator<Integer> iterator2 = graph2.neighborIterator(1);
      assertEquals(3, iterator2.next());
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void neighborsEmptyHasNext(GraphFactory factory) {
      var graph = factory.createGraph(6);
      var iterator = graph.neighborIterator(0);
      assertFalse(iterator.hasNext());
      assertFalse(iterator.hasNext());
      assertFalse(iterator.hasNext());
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void neighborsOutOfRange(GraphFactory factory) {
      var graph = factory.<Integer>createGraph(6);
      assertAll(
          () -> assertThrows(IndexOutOfBoundsException.class, () -> graph.neighborIterator(10)),
          () -> assertThrows(IndexOutOfBoundsException.class, () -> graph.neighborIterator(-2))
      );
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void neighborsEmptyNext(GraphFactory factory) {
      var graph = factory.createGraph(6);
      assertThrows(NoSuchElementException.class, () -> graph.neighborIterator(0).next());
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void neighborsOneEdge(GraphFactory factory) {
      var graph = factory.<String>createGraph(6);
      graph.addEdge(1, 2, "hello");
      var iterator = graph.neighborIterator(1);
      assertTrue(iterator.hasNext());
      assertEquals(2, (int) iterator.next());
      assertFalse(iterator.hasNext());
      assertThrows(NoSuchElementException.class, iterator::next);
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void neighborsNoHasNext(GraphFactory factory) {
      var graph = factory.createGraph(10);
      for (int i = 0; i < 10; i++) {
        graph.addEdge(5, i, -1);
      }

      var result = new HashSet<Integer>();
      var expected = new HashSet<Integer>();
      var iterator = graph.neighborIterator(5);
      for (int i = 0; i < 10; i++) {
        expected.add(i);
        result.add(iterator.next());
      }
      assertEquals(expected, result);

      assertFalse(iterator.hasNext());
      assertThrows(NoSuchElementException.class, iterator::next);
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void neighborsNonDestructive(GraphFactory factory) {
      var graph = factory.createGraph(12);
      for (int i = 0; i < 12; i++) {
        graph.addEdge(5, i, 67);
      }
      assertTimeout(Duration.ofMillis(1_000), () -> {
        var neighbors = graph.neighborIterator(5);
        while (neighbors.hasNext()) {
          assertNotNull(neighbors.next());
        }
      });
      for (int i = 0; i < 12; i++) {
        assertEquals(67, (int) graph.getWeight(5, i).orElseThrow());
      }
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void neighborSeveralHasNext(GraphFactory factory) {
      var graph = factory.createGraph(14);
      graph.addEdge(3, 7, 2);
      graph.addEdge(3, 5, 3);
      graph.addEdge(7, 3, 4);

      assertTimeout(Duration.ofMillis(1_000), () -> {
        var neighbors = graph.neighborIterator(3);
        assertTrue(neighbors.hasNext());
        var vertex1 = neighbors.next();
        for (var i = 0; i < 5; i++) {
          assertTrue(neighbors.hasNext());
        }
        var vertex2 = neighbors.next();
        assertFalse(neighbors.hasNext());
        assertTrue((vertex1 == 5 && vertex2 == 7) || (vertex1 == 7 && vertex2 == 5));
      });
    }

    @Test
    public void neighborLazy() {
      var graph = new MatrixGraph<>(3);
      var iterator = graph.neighborIterator(0);
      var packageName = iterator.getClass().getPackageName();
      assertAll(
          () -> assertNotEquals("java.util", packageName),
          () -> assertNotEquals("java.util.stream", packageName)
      );
    }
  }


  @Nested
  public class Q8 {

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void neighborsStreamSimple(GraphFactory factory) {
      var graph = factory.<String>createGraph(6);
      graph.addEdge(1, 2, "bar");
      graph.addEdge(1, 5, "foo");

      var set = graph.neighborStream(1).boxed().collect(toSet());
      assertEquals(Set.of(2, 5), set);
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void neighborStream(GraphFactory factory) {
      var graph = factory.createGraph(17);
      assertEquals(0, graph.neighborStream(0).count());
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void neighborStreamOutOfRange(GraphFactory factory) {
      var graph = factory.<Integer>createGraph(6);
      assertAll(
          () -> assertThrows(IndexOutOfBoundsException.class, () -> graph.neighborStream(10)),
          () -> assertThrows(IndexOutOfBoundsException.class, () -> graph.neighborStream(-2))
      );
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void neighborStreamOneEdge(GraphFactory factory) {
      var graph = factory.createGraph(3);
      graph.addEdge(1, 2, 3);
      assertEquals(Set.of(2), graph.neighborStream(1).boxed().collect(toSet()));
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void neighborStreamALot(GraphFactory factory) {
      var nodeCount = 2_000;
      var graph = factory.<Boolean>createGraph(nodeCount);
      for (var i = 0; i < nodeCount; i++) {
        for (var j = 0; j < nodeCount; j++) {
          graph.addEdge(i, j, true);
        }
      }
      assertTimeout(Duration.ofMillis(2_000), () -> {
        for (var i = 0; i < nodeCount; i++) {
          assertEquals(nodeCount, graph.neighborStream(i).distinct().count());
        }
      });
    }
  }


  @Nested
  public class Q10 {

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void iteratorRemove(GraphFactory factory) {
      var graph = factory.createGraph(11);
      graph.addEdge(3, 10, 13);
      var neighbors = graph.neighborIterator(3);
      assertEquals(10, (int) neighbors.next());
      neighbors.remove();
      assertFalse(graph.getWeight(3, 10).isPresent());
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void iteratorRemoveInvalid(GraphFactory factory) {
      var graph = factory.createGraph(21);
      graph.addEdge(20, 19, 20);
      var neighbors = graph.neighborIterator(20);
      assertThrows(IllegalStateException.class, neighbors::remove);
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void iteratorRemoveTwiceInvalid(GraphFactory factory) {
      var graph = factory.createGraph(21);
      graph.addEdge(20, 19, 20);
      var neighbors = graph.neighborIterator(20);
      neighbors.next();
      neighbors.remove();
      assertFalse(graph.getWeight(20, 19).isPresent());
      assertThrows(IllegalStateException.class, neighbors::remove);
    }

    @ParameterizedTest
    @MethodSource("fr.uge.graph.GraphTest#graphFactoryProvider")
    public void iteratorRemoveALot(GraphFactory factory) {
      var graph = factory.createGraph(50);
      for (var i = 0; i < 50; i++) {
        for (var j = 0; j < i; j++) {
          graph.addEdge(i, j, i + j);
        }
      }

      for (int i = 0; i < 50; i++) {
        var neighbors = graph.neighborIterator(i);
        for (var j = 0; j < i; j++) {
          assertTrue(neighbors.hasNext());
          neighbors.next();
          neighbors.remove();
        }
        assertFalse(neighbors.hasNext());
      }

      for (var i = 0; i < 50; i++) {
        for (var j = 0; j < 50; j++) {
          assertTrue(graph.getWeight(i, j).isEmpty());
        }
      }
    }
  }
}
