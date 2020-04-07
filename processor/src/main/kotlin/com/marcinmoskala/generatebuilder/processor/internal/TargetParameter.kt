
package com.marcinmoskala.generatebuilder.processor.internal

import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf.ValueParameter
import javax.lang.model.element.VariableElement

internal data class TargetParameter(
    val name: String,
    val proto: ValueParameter,
    val index: Int,
    val element: VariableElement
)
