package kingstabyou.javaparser;
//import ClassParser

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import kingstabyou.javaparser.ClassParser.ClassParser;
import kingstabyou.javaparser.ObjectInspector.ObjectInspector;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static ClassParser getExplainer() {
        return new ClassParser();
    }
    public static ObjectInspector getInspector() {
        return new ObjectInspector();
    }

    private Map<String, ClassOrInterfaceDeclaration> classes = new HashMap<>();
    private Map<String, List<MethodDeclaration>> methods = new HashMap<>();

    public void processClass(String className) {
        // Get file for target class
        Path baseDir = Paths.get(System.getProperty("user.dir"));
        Path testFolder = Paths.get(baseDir.toAbsolutePath().toString(), "src", "test", "java");
        Path filePath = Paths.get(testFolder.toAbsolutePath().toString(), className.replace(".", File.separator) + ".java");

        // Parse Java file
        CompilationUnit cu;
        try {
            cu = new JavaParser().parse(filePath).getResult().orElseThrow();
        } catch (IOException e) {
            throw new Error(e);
        }


        // Get nested classes
        Map<String, ClassOrInterfaceDeclaration> classes = new HashMap<>();
        Map<String, List<MethodDeclaration>> methods = new HashMap<>();

        for (TypeDeclaration<?> t : cu.getTypes()) {
            for (BodyDeclaration<?> b : t.getMembers()) {
                if (b.isClassOrInterfaceDeclaration()) {
                    ClassOrInterfaceDeclaration c = b.asClassOrInterfaceDeclaration();
                    this.classes.put(c.getNameAsString(), c);
                    this.methods.put(c.getNameAsString(),c.getMethods());
                }
            }
        }
    }

    public Map<String, ClassOrInterfaceDeclaration> getClasses() {
        if (!new Exception().getStackTrace()[1].getClassName().startsWith("ClassParser.Test"))
            throw new Error("Calling getClasses is not allowed in code");
        return this.classes;
    }


    public void setClasses(Map<String, ClassOrInterfaceDeclaration> classes) {
        if (!new Exception().getStackTrace()[1].getClassName().startsWith("ClassParser.Test"))
            throw new Error("Calling getClasses is not allowed in code");
        this.classes = classes;
    }

    public Set<String> explain(String receiverType, String methodName, String ... argumentTypes) {

        return getExplainer().explain(this.classes, receiverType, methodName, argumentTypes);
    }

    public Map<String, String> describeObject(Object o) {
        return getInspector().describeObject(o);
    }

    public void parseFull(){
        for (Map.Entry<String, ClassOrInterfaceDeclaration> c: this.classes.entrySet()){
            Map<String, ClassOrInterfaceDeclaration> tempclasses=new HashMap<>(classes);
            System.out.println("------------------Class "+c.getKey()+"----------------------");
            System.out.println("------------------Fields----------------------");
            Class<?> tempClass= c.getClass();
            for (Field f: tempClass.getDeclaredFields()){
                System.out.println(f.getName());
            }
            System.out.println("------------------Methods----------------------");
            for (MethodDeclaration m: this.methods.get(c.getKey())){
                System.out.println(m.getNameAsString()+m.getParameters());
                System.out.println("Classes that can call - "+getExplainer().explain(tempclasses, c.getKey(),m.getNameAsString()));
                System.out.println();

            }
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.processClass("TestCode");
        main.parseFull();
    }
}
