package fr.uge;

import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.module.ModuleFinder;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;


public class Cloning {
  
  private final LinkedHashMap<Integer,String> map = new LinkedHashMap<>();
  private int index = 0;

  public String Searching(String pat) {
    
    var finder = ModuleFinder.of(Path.of(pat+".jar"));
    var moduleReference = finder.findAll().stream().findFirst().orElseThrow();
    try(var reader = moduleReference.open()) {
      for(var filename: (Iterable<String>) reader.list()::iterator) {
        if (!filename.endsWith(".class")) {
          continue;
        }
        try(var inputStream = reader.open(filename).orElseThrow()) {
          var classReader = new ClassReader(inputStream);
          classReader.accept(new ClassVisitor(Opcodes.ASM9) {

              private static String modifier(int access) {
                if (Modifier.isPublic(access)) {
                  return "public";
                }
                if (Modifier.isPrivate(access)) {
                  return "private";
                }
                if (Modifier.isProtected(access)) {
                  return "protected";
                }
                return "";
              }

              public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                System.err.println("class " + modifier(access) + " " + name + " " + superName + " " + (interfaces != null? Arrays.toString(interfaces): ""));
                index++;
                map.put(index,"class " + modifier(access) + " " + name + " " + superName + " " + (interfaces != null? Arrays.toString(interfaces): ""));
              }

              public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
                System.err.println("  component " + name + " " + ClassDesc.ofDescriptor(descriptor).displayName());
                index++;
                map.put(index,"  component " + name + " " + ClassDesc.ofDescriptor(descriptor).displayName());
                return null;
              }

              public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                System.err.println("  field " + modifier(access) + " " + name + " " + ClassDesc.ofDescriptor(descriptor).displayName() + " " + signature);
                index++;
                map.put(index,"  field " + modifier(access) + " " + name + " " + ClassDesc.ofDescriptor(descriptor).displayName() + " " + signature);
                return null;
              }

              public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                System.err.println("  method " + modifier(access) + " " + name + " " + MethodTypeDesc.ofDescriptor(descriptor).displayDescriptor() + " " + signature);
                index++;
                map.put(index,"  method " + modifier(access) + " " + name + " " + MethodTypeDesc.ofDescriptor(descriptor).displayDescriptor() + " " + signature);
                return new MethodVisitor(Opcodes.ASM9) {
                  public void visitInsn(int opcode) {
                    System.err.println("    opcode " + opcode);
                    index++;
                    map.put(index, "    opcode " + opcode);
                  }

                  public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                    System.err.println("    opcode " + opcode + " " + owner+ "." + name + descriptor);
                    index++;
                    map.put(index,"    opcode " + opcode + " " + owner+ "." + name + descriptor);
                  }

                  // + the other visit methods to get all the opcodes
                };
              }
          }, 0);
        }
      }
    }
  }
  
  public static void main(String[] args) {
    var clone = new Cloning();
    System.out.println(clone);
  }
}
