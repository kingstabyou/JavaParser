package kingstabyou.javaparser.ClassParser;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class TopDownParser extends ClassParser{
    ClassOrInterfaceDeclaration class_info;
    Map<String, ClassOrInterfaceDeclaration> classes;
    String receiverType;
    String methodName;
    String[] argumentTypes;
    Set<String> result;
    ArrayList<String> child_list;

    public TopDownParser(ClassOrInterfaceDeclaration class_info, Map<String, ClassOrInterfaceDeclaration> classes, String receiverType, String methodName, String[] argumentTypes, Set<String> result, ArrayList<String> child_list) {
        this.class_info = class_info;
        this.classes = classes;
        this.receiverType = receiverType;
        this.methodName = methodName;
        this.argumentTypes = argumentTypes;
        this.result = result;
        this.child_list = child_list;
    }

    public void evaluate(){
        for(String class_fromclass:classes.keySet()){
            ClassOrInterfaceDeclaration temp_class = classes.get(class_fromclass);
            if (!temp_class.getExtendedTypes().isEmpty()){
                ClassOrInterfaceType extendedclass= temp_class.getExtendedTypes(0);
                if (extendedclass.getNameAsString().equals(receiverType) || child_list.contains(extendedclass.getNameAsString())){
                    temp_extended = classes.get(class_fromclass);
                    child_list.add(temp_extended.getNameAsString());
                    if (!temp_extended.getMethodsBySignature(methodName,argumentTypes).isEmpty()){
                        result.add(temp_extended.getNameAsString());
                    }
                }
                else {

                    while (!temp_class.getExtendedTypes().isEmpty()){
                        extendedclass= temp_class.getExtendedTypes(0);
                        if (child_list.contains(extendedclass.getNameAsString())){
                            temp_extended = classes.get(class_fromclass);
                            child_list.add(temp_extended.getNameAsString());
                            if (!temp_extended.getMethodsBySignature(methodName,argumentTypes).isEmpty()){
                                result.add(temp_extended.getNameAsString());
                            }
                        }
                        if (extendedclass.getNameAsString().equals("Object")){
                            break;
                        }
                        temp_class=classes.get(extendedclass.getNameAsString());
                    }
                }
            }
        }

    }
}
