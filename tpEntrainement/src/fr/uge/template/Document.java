package fr.uge.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Document<T> {
  
  private final List<Function<? super T,?>> functions;
  private final Template template;
  
  public Document(Template template, List<? extends Function<? super T,?>> functions) {
    Objects.requireNonNull(template);
    Objects.requireNonNull(functions);
    if(functions.size() !=template.fragments.size()-1) {
      throw new IllegalArgumentException();
    }
    this.template= template;
    this.functions=List.copyOf(functions);
()  }
  
  
 protected String applyTemplate(T record) {
    Objects.requireNonNull(record);
    return this.template.interpolate(functions.stream().map(e -> e.apply(record)).toList());
  }
  
 public String generate(List<? extends T> instances, String string) {
   Objects.requireNonNull(string);
   Objects.requireNonNull(instances);
   return instances.stream().map(e-> applyTemplate(e)).collect(Collectors.joining(string)) ;
 }
  
  public record Template(List<String> fragments) {

    public Template(List<String> fragments){
      Objects.requireNonNull(fragments);
      if(fragments.isEmpty()) {
        throw new IllegalArgumentException();
      }
      this.fragments = List.copyOf(fragments);
    }
    
    public <E> String interpolate(List<E> list) {
      Objects.requireNonNull(list);
      if(list.size()+1 != fragments.size()) {
        throw new IllegalArgumentException();
      }
      var builder = new StringBuilder();
      var iter = list.iterator();
      var iter2 = fragments.iterator();
      while(iter.hasNext()) {
        builder.append(iter2.next()).append(iter.next());
      }
      return builder.append(iter2.next()).toString();
    }
    
    public static Template of(String string) {
      List<String> template = new ArrayList<>();
      var start =0;
      for(var i = 0; i < string.length();i++) {
        if(string.charAt(i) =='@') {
          template.add(string.substring(start,i));
          start=i+1;
        }
      }
      template.add(string.substring(start,string.length()));
      return new Template(template);
    }

    @SafeVarargs
    public final <T> Document<T> toDocument(Function<T,?>... args) {
      return new Document<T>(this,List.of(args));  
    }
    
    public <T> Template bind(T value) {
      var val = Stream.concat(Stream.of(
          fragments.get(0).concat(String.valueOf(value))
          .concat(fragments.get(1))),fragments.subList(2, fragments.size())
          .stream()).toList();
      return new Template(val);
      
    }
    
    @Override
    public String toString() {
      return fragments.stream().collect(Collectors.joining("@")).toString();
    }
  }




}
