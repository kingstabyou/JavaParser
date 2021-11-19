package kingstabyou.javaparser.ClassParser;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.util.Map;
import java.util.Set;

public class BottomUpParser extends ClassParser {
    ClassOrInterfaceDeclaration class_info;
    Map<String, ClassOrInterfaceDeclaration> classes;
    String receiverType;
    String methodName;
    String[] argumentTypes;
    Set<String> result;
    Boolean returnFlag;

    public BottomUpParser(ClassOrInterfaceDeclaration class_info, Map<String, ClassOrInterfaceDeclaration> classes, String receiverType, String methodName, String[] argumentTypes, Set<String> result) {
        this.class_info = class_info;
        this.classes = classes;
        this.receiverType = receiverType;
        this.methodName = methodName;
        this.argumentTypes = argumentTypes;
        this.result = result;
        this.returnFlag=false;
    }

    public void evaluate(){
        if (class_info.getMethodsBySignature(methodName,argumentTypes).isEmpty()){
            String resultfromextended= get_extended_method(classes,class_info,methodName,argumentTypes);
            if (!resultfromextended.isEmpty()){
                if (return_result[0].equals("abstract") || return_result[0].equals("private") || return_result[0].equals("static") ){
//                    return result;
                    returnFlag=true;
                }
                else {
                    result.add(return_result[1]);
                }
            }
        }
        else {
            check_Abstract_Static_Private(class_info, methodName, argumentTypes);
            switch (return_result[0]) {
                case "abstract":
//                    return result;
                    returnFlag=true;
                case "private":
                case "static":
                    result.add(return_result[1]);
//                    return result;
                    returnFlag=true;
                default:
                    result.add(return_result[1]);
            }
        }
    }

    public String get_extended_method(Map<String, ClassOrInterfaceDeclaration> classes, ClassOrInterfaceDeclaration class_info, String methodName, String... argumentTypes) {
        if (!class_info.getExtendedTypes().isEmpty()) {
            ClassOrInterfaceType extendedclass = class_info.getExtendedTypes(0);
            if (extendedclass.getNameAsString().equals("Object")) {
                temp_extended = classes.get("java.lang.Object");
            } else {
                temp_extended = classes.get(extendedclass.getNameAsString());
            }

            if (!temp_extended.getMethodsBySignature(methodName, argumentTypes).isEmpty()) {
                check_Abstract_Static_Private(temp_extended, methodName, argumentTypes);
                return temp_extended.getNameAsString();
            } else {
                return get_extended_method(classes, temp_extended, methodName, argumentTypes);
            }
        }
        return empty;
    }

    public String[] check_Abstract_Static_Private(ClassOrInterfaceDeclaration class_info, String methodName, String... argumentTypes){
        if (class_info.getMethodsBySignature(methodName,argumentTypes).get(0).isAbstract()){
            return_result[0]="abstract";
            return_result[1]="";
            return return_result;
        }
        else if (class_info.getMethodsBySignature(methodName,argumentTypes).get(0).isPrivate()){
            return_result[0]="private";
            return_result[1]=class_info.getNameAsString();
            return return_result;
        }
        else if (class_info.getMethodsBySignature(methodName,argumentTypes).get(0).isStatic()){
            return_result[0]="static";
            return_result[1]=class_info.getNameAsString();
            return return_result;
        }
        else{
            return_result[0]="";
            return_result[1]=class_info.getNameAsString();
            return return_result;
        }
    }



}
