
package com.marcinmoskala.generatebuilder.processor.internal

import com.marcinmoskala.generatebuilder.processor.asKModifier
import com.marcinmoskala.generatebuilder.processor.asTypeName
import com.squareup.kotlinpoet.*
import me.eugeniomarletti.kotlin.metadata.*
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf.Class
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf.TypeParameter
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf.Visibility.INTERNAL
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.NameResolver
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

internal data class TargetType(
    val proto: Class,
    val element: TypeElement,
    val constructor: TargetConstructor,
    val typeVariables: List<TypeVariableName>
) {
    val builderName = element.simpleName.toString().toGenerateBuilderBuilderName()
    val isInternal = proto.visibility == INTERNAL

    companion object {

        fun genericTypeNames(proto: Class, nameResolver: NameResolver): List<TypeVariableName> {
            return proto.typeParameterList.map {
                val possibleBounds = it.upperBoundList
                    .map { it.asTypeName(nameResolver, proto::getTypeParameter, false) }
                val typeVar = if (possibleBounds.isEmpty()) {
                    TypeVariableName(
                        name = nameResolver.getString(it.name),
                        variance = it.varianceModifier
                    )
                } else {
                    TypeVariableName(
                        name = nameResolver.getString(it.name),
                        bounds = *possibleBounds.toTypedArray(),
                        variance = it.varianceModifier
                    )
                }
                return@map typeVar.reified(it.reified)
            }
        }

        private val TypeParameter.varianceModifier: KModifier?
            get() {
                return variance.asKModifier().let {
                    // We don't redeclare out variance here
                    if (it == KModifier.OUT) {
                        null
                    } else {
                        it
                    }
                }
            }
    }
}
