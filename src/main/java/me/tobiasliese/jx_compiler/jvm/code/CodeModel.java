package me.tobiasliese.jx_compiler.jvm.code;

import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeModel {
    private final Map<String, MethodTypeDesc> methods = new HashMap<>();
    private final Map<String, ClassDesc> fields = new HashMap<>();

    public static CodeModel of(ClassDesc classDesc) throws ClassNotFoundException {

        CodeModel codeModel = new CodeModel();

        if (classDesc.isPrimitive() || classDesc.isArray()) {
            return codeModel;
        }
        var clazz = Class.forName(classDesc.packageName() + "." + classDesc.displayName());

        for (var field: clazz.getDeclaredFields()) {
            String fieldName = field.getName();
            ClassDesc fieldDesc = ClassDesc.ofDescriptor(field.getType().descriptorString());
            if (! TypeGraph.contains(fieldDesc)) {
                TypeGraph.addEntry(fieldDesc, null);
                TypeGraph.addEntry(fieldDesc, CodeModel.of(fieldDesc));
            }
            codeModel.fields.put(fieldName, fieldDesc);
        }

        for (var method: clazz.getDeclaredMethods()) {
            String methodName = method.getName();
            List<ClassDesc> methodParams = new ArrayList<>();

            for (var param: method.getParameters()) {
                ClassDesc paramDesc = ClassDesc.ofDescriptor(param.getType().descriptorString());
                methodParams.add(paramDesc);
            }

            ClassDesc returnType = ClassDesc.ofDescriptor(method.getReturnType().descriptorString());
            if (! TypeGraph.contains(returnType)) {

                TypeGraph.addEntry(returnType, null);
                TypeGraph.addEntry(returnType, CodeModel.of(returnType));
            }
            MethodTypeDesc methodTypeDesc = MethodTypeDesc.of(returnType, methodParams);
            codeModel.methods.put(methodName, methodTypeDesc);
        }

        return codeModel;
    }

    public Map<String, ClassDesc> getFields() {
        return Map.copyOf(fields);
    }

    public Map<String, MethodTypeDesc> getMethods() {
        return Map.copyOf(methods);
    }
}
