
package com.marcinmoskala.generatebuilder.processor.internal

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

internal class GenerateBuilderParamSpec(
    val imports: List<GenerateBuilderImportSpec>,
    val properties: List<PropertySpec>,
    val functions: List<FunSpec>,
    val types: List<TypeSpec>
)