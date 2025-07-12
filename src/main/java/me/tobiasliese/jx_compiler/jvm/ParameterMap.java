package me.tobiasliese.jx_compiler.jvm;

import java.lang.constant.ClassDesc;
import java.util.List;

public record ParameterMap(List<ClassDesc> classDescs, List<String> parameterNames) {

}
