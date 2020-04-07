
package com.marcinmoskala.generatebuilder.processor.internal

import com.marcinmoskala.generatebuilder.processor.ProcessingException
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf.Constructor
import me.eugeniomarletti.kotlin.metadata.visibility
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.util.Elements

internal data class TargetConstructor(
    val element: ExecutableElement,
    val proto: Constructor,
    val parameters: Map<String, TargetParameter>
) {
    companion object {
        fun from(metadata: KotlinClassMetadata, elements: Elements): TargetConstructor {
            val (nameResolver, classProto) = metadata.data

            val constructorElements = classProto.fqName
                .let(nameResolver::getString)
                .replace('/', '.')
                .let(elements::getTypeElement)
                .enclosedElements
                .mapNotNull { element ->
                    element.takeIf { it.kind == ElementKind.CONSTRUCTOR }?.let { it as ExecutableElement }
                }

            val proto: ProtoBuf.Constructor = classProto.constructorList.first()
            val element: ExecutableElement = constructorElements.first()

            if (proto.visibility != ProtoBuf.Visibility.INTERNAL && proto.visibility != ProtoBuf.Visibility.PUBLIC) {
                throw ProcessingException(
                    element, "@Auto can't be applied to $element: " +
                            "constructor is not internal or public"
                )
            }

            val parameters = mutableMapOf<String, TargetParameter>()
            for (parameter in proto.valueParameterList) {
                val name = nameResolver.getString(parameter.name)
                val index = proto.valueParameterList.indexOf(parameter)
                parameters[name] =
                    TargetParameter(
                        name,
                        parameter,
                        index,
                        element.parameters[index]
                    )
            }

            return TargetConstructor(
                element,
                proto,
                parameters
            )
        }
    }
}
