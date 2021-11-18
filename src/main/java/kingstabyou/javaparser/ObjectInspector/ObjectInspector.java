package kingstabyou.javaparser.ObjectInspector;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ObjectInspector {
    String key;
    String value;
    Class<?> ClassOfObject;
    Set<Field> tempset;
    Set<Field> tempset2;
    Map<String,String> result;
    Boolean isStaticTestcase=false;
    Object fieldType;


    public Map<String, String> describeObject(Object o) {
        tempset= new HashSet<>();
        tempset2=new HashSet<>();
        result=new HashMap<>();
        ClassOfObject =o.getClass();
        if (ClassOfObject.getName().equals("java.lang.Class")){
            ClassOfObject = (Class<?>) o;
            isStaticTestcase=true;
        }

        tempset.addAll(Stream.concat(Arrays.stream(ClassOfObject.getFields()),Arrays.stream(ClassOfObject.getDeclaredFields())).collect(Collectors.toSet()));

        if (!ClassOfObject.isInterface()){
            tempset=getFieldOfClassHierarchy(ClassOfObject.getSuperclass(),tempset);
        }
        if (isStaticTestcase.equals(true)){
            for (Field f:tempset){
                if (Modifier.isStatic(f.getModifiers())){
                    tempset2.add(f);
                }
            }
            tempset=tempset2;
        }
        for (Field f:tempset){
            try {
                if (Modifier.isStatic(f.getModifiers())){
                    key=(String) f.getDeclaringClass().getSimpleName()+"."+f.getName();
                }
                else {
                    key=f.getName();
                }
                value = GetValueAccordingToType(o,f);
                result.put(key,value);
            }
            catch (Error error){
                key=f.getName();
                value="Raised error: "+error.getClass().getName();
            }
            catch (RuntimeException e){
                key=f.getName();
                value="Thrown exception: "+e.getClass().getName();
            }
            catch (Exception e)
            {
                key=f.getName();
                value="Thrown checked exception: "+e.getClass().getName();
            }
            finally {
                result.put(key,value);
            }
        }
        return result;
    }

    public void updateObject(Object o, Map<String, Object> fields) {
        ClassOfObject =o.getClass();
        for (String f: fields.keySet()){
            try {
                setFieldOnDeclaringClass(ClassOfObject,f,o,fields);
            }
            catch (NoSuchFieldException | IllegalAccessException e){
                System.out.println("Error in updating");
            }
        }
    }

    public Set<Field> getFieldOfClassHierarchy(Class<?> getClassOfObject,Set<Field> tempset){
        if (getClassOfObject.getName().equals("java.lang.Object")){
            return tempset;
        }
        else {
            for (Field f:List.of(getClassOfObject.getDeclaredFields())){
                if (!Modifier.isPrivate(f.getModifiers()) && !tempset.contains(f)){
                    tempset.add(f);
                }
            }
            return  getFieldOfClassHierarchy(getClassOfObject.getSuperclass(),tempset);
        }
    }

    public void setFieldOnDeclaringClass(Class<?> getClassOfObject, String f,Object o,Map<String, Object> fields) throws NoSuchFieldException, IllegalAccessException {
        try {
            getClassOfObject.getDeclaredField(f);
            Field tempf =getClassOfObject.getDeclaredField(f);
            tempf.set(o,fields.get(f));
        }
        catch (NoSuchFieldException e){
            setFieldOnDeclaringClass(getClassOfObject.getSuperclass(),f,o,fields);
        }
        catch (IllegalAccessException e){
            Field tempf =getClassOfObject.getDeclaredField(f);
            tempf.setAccessible(true);
            tempf.set(o,fields.get(f));
        }
    }

    public String GetValueAccordingToType(Object o, Field f) throws Exception {
        f.setAccessible(true);
        if (isStaticTestcase.equals(true)){
            fieldType=f.get(null);
        }
        else {
            fieldType =f.get(o);
        }
        if (fieldType==null){
            return "null";
        }
        switch (fieldType.getClass().getName()){
            case "java.lang.Integer":
                if (f.getType().getName().equals("int")){
                    value=Integer.toString((Integer) f.get(o));
                } else {
                    value="Boxed "+Integer.toString((Integer) f.get(o));
                }
                return value;
            case "java.lang.Long":
                if (f.getType().getName().equals("long")){
                    value= Long.toString((Long) f.get(o));
                } else {
                    value="Boxed "+Long.toString((Long) f.get(o));
                }
                return value+ "#L";
            case "java.lang.Float":
                if (f.getType().getName().equals("float")){
                    value= Float.toString((Float) f.get(o));
                } else {
                    value="Boxed "+Float.toString((Float) f.get(o));
                }
                return value+ "#F";
            case "java.lang.Double":
                if (f.getType().getName().equals("double")){
                    value= Double.toString((Double) f.get(o));
                } else {
                    value="Boxed "+Double.toString((Double) f.get(o));
                }
                return value+ "#D";
            case "java.lang.Short":
                if (f.getType().getName().equals("short")){
                    value= "0" +Integer.toOctalString((Short) f.get(o));
                } else {
                    value="Boxed 0"+Integer.toOctalString((Short) f.get(o));
                }
                return value;
            case "java.lang.Byte":
                if (f.getType().getName().equals("byte")){
                    value= "0x" +Integer.toHexString((Byte) f.get(o));
                } else {
                    value="Boxed 0x"+Integer.toHexString((Byte) f.get(o));
                }
                return value;
            case "java.lang.Boolean":
                if (f.getType().getName().equals("boolean")){
                    value= Boolean.toString((Boolean) f.get(o));
                } else {
                    value="Boxed "+Boolean.toString((Boolean) f.get(o));
                }
                return value;
            case "java.lang.Character":
                if (f.getType().getName().equals("char")){
                    value= Character.toString((Character) f.get(o));
                } else {
                    value="Boxed "+Character.toString((Character) f.get(o));
                }
                return value;
            default:
                Method[] getAllMethods =f.get(o).getClass().getMethods();
                boolean debug=false;
                ArrayList<Parameter> p=new ArrayList<>();
                for (Method m:getAllMethods){
                    if (m.getName().toString().equals("debug")){
                        try {
                            if(Modifier.isStatic(m.getModifiers())) {
                                if(m.getParameters()[0].getType().isAssignableFrom(f.get(o).getClass()))
                                {
                                    value = (String) m.invoke(null,f.get(o));
                                    debug=true;
                                    break;
                                }
                            }
                            else {
                                value= (String) m.invoke(f.get(o),null);
                                debug=true;
                                break;
                            }
                        } catch (InvocationTargetException e){
                            throw  (Exception)e.getTargetException();}
                    }
                }
                if(!debug) {
                    value= fieldType.toString();
                }

                return value;
        }
    }

}
