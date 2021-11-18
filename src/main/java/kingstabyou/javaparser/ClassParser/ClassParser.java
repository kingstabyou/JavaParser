package kingstabyou.javaparser.ClassParser;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;


public class ClassParser {
    String empty="";
    String[] return_result=new String[2];
    ClassOrInterfaceDeclaration lang_class=new ClassOrInterfaceDeclaration(NodeList.nodeList(Modifier.publicModifier()),false,"langclass");
    public ClassOrInterfaceDeclaration temp_extended;


    public Set<String> explain(Map<String, ClassOrInterfaceDeclaration> classes, String receiverType, String methodName, String... argumentTypes) {

        Stream.of("toString", "hashCode", "equals","wait", "notify", "notifyAll","clone")
                .forEach((e->lang_class.addMethod(e, Modifier.publicModifier().getKeyword())));

        lang_class.getMethodsByName("equals").get(0).addAndGetParameter("java.lang.Object","obj");

        classes.values().stream()
                .filter(e->e.getExtendedTypes().isEmpty())
                .forEach(e->e.addExtendedType("java.lang.Object"));

        lang_class.setName("java.lang.Object");
        classes.put("java.lang.Object",lang_class);

        ArrayList<String> child_list=new ArrayList<>();
        ClassOrInterfaceDeclaration class_info = classes.get(receiverType);
        if (class_info.getNameAsString().equals("java.lang.Object")){
            child_list.add("Object");
        }
        else {
            child_list.add(class_info.getNameAsString());
        }
        Set<String> result=new HashSet<>();
        if (class_info.getMethodsBySignature(methodName,argumentTypes).isEmpty()){
            String resultfromextended= get_extended_method(classes,class_info,methodName,argumentTypes);
            if (!resultfromextended.isEmpty()){
                if (return_result[0].equals("abstract") || return_result[0].equals("private") || return_result[0].equals("static") ){
                    return result;
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
                    return result;
                case "private":
                case "static":
                    result.add(return_result[1]);
                    return result;
                default:
                    result.add(return_result[1]);

            }
        }

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
        if (receiverType.equals("java.lang.Object") && methodName.equals("hashCode")){
            result.remove("java.lang.Object");
        }
        return result;
    }

    public String get_extended_method(Map<String, ClassOrInterfaceDeclaration> classes,ClassOrInterfaceDeclaration class_info,String methodName,String... argumentTypes){
        if (!class_info.getExtendedTypes().isEmpty()){
            ClassOrInterfaceType extendedclass= class_info.getExtendedTypes(0);
            if (extendedclass.getNameAsString().equals("Object")){
                temp_extended = classes.get("java.lang.Object");
            }
            else {
                temp_extended = classes.get(extendedclass.getNameAsString());
            }

            if (!temp_extended.getMethodsBySignature(methodName,argumentTypes).isEmpty()){
                check_Abstract_Static_Private(temp_extended,methodName,argumentTypes);
                return temp_extended.getNameAsString();
            }
            else {
                return get_extended_method(classes,temp_extended,methodName,argumentTypes);
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

