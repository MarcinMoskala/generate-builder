
package com.marcinmoskala.generatebuilder.processor.internal

import com.marcinmoskala.generatebuilder.processor.asTypeName
import com.sun.tools.javac.code.Symbol
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.NameResolver

internal class GenerateBuilderProperty(
    val name: String,
    param: TargetParameter,
    protoClass: ProtoBuf.Class,
    nameResolver: NameResolver
) {
    val element = param.element
    val typeInfo =
        GenerateBuilderParamType(element as Symbol.VarSymbol)
    val typeName = param.proto.type.asTypeName(nameResolver, protoClass::getTypeParameter)

    fun isNullable() = typeName.nullable
}

class GenerateBuilderParamType(
    param: Symbol.VarSymbol
) {
    val element: Symbol.TypeSymbol = param.asType().asElement()
    val name = element.simpleName.toString()
}