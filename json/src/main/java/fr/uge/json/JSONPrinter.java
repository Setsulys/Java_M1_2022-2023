package fr.uge.json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class JSONPrinter {
  
  private final static ClassValue<RecordComponent[]> cache = new ClassValue<RecordComponent[]>() {

    @Override
    protected RecordComponent[] computeValue(Class<?> type) {
      return type.getRecordComponents();
    }};
  
  private static String escape(Object o) {
    return o instanceof String s ? "\"" + s + "\"": "" + o;
  }
  
  public static String toJSON(Object obj) {
    return Arrays.stream(cache.get(obj.getClass()))
        .map(component ->  escape(getNameBis(component)) + ": " + escape(invokeBis(component.getAccessor(),obj)))
        .collect(Collectors.joining(",\n","{\n","\n}"));
  }     
          
  private static String getNameBis(RecordComponent component) { 
    return component.getAccessor().isAnnotationPresent(JSONProperty.class)? 
        component.getAccessor().getAnnotation(JSONProperty.class).value().equals("_")? 
            component.getName().replace("_", "-") : 
              component.getAccessor().getAnnotation(JSONProperty.class).value() : 
                component.getName();
  }
  
  private static Object invokeBis(Method met, Object rec) {
    try {
    return met.invoke(rec);
  } catch (InvocationTargetException | IllegalAccessException er) {
     var cause = er.getCause();
     if (cause instanceof RuntimeException exception) {
       throw exception;
     }
     if (cause instanceof Error error) {
       throw error;
     }
     throw new UndeclaredThrowableException(er);
    }
  }
}
  

