package com.marcinmoskala.generatebuilder.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.tools.Diagnostic

internal fun ProcessingEnvironment.getClassName(element: Element, className: String): ClassName {
    return ClassName(elementUtils.getPackageOf(element).toString(), className)
}

internal fun ProcessingEnvironment.error(e: ProcessingException) {
    this.error(e.element, e.message ?: "There was an error processing this element.")
}

internal fun ProcessingEnvironment.error(e: Element, msg: String, vararg args: String) {
    messager?.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e)
}

internal fun ProcessingEnvironment.getGeneratedSourcesRoot(): String {
    return this.options["kapt.kotlin.generated"]
        ?: throw IllegalStateException("No source root for generated file")
}

internal fun TypeName.asNullableIf(condition: Boolean): TypeName {
    return if (condition) asNullable() else this
}