
package com.marcinmoskala.generatebuilder.processor

import com.marcinmoskala.generatebuilder.annotation.GenerateBuilder
import com.marcinmoskala.generatebuilder.processor.internal.TargetConstructor
import com.marcinmoskala.generatebuilder.processor.internal.TargetType
import com.marcinmoskala.generatebuilder.processor.internal.generateClass
import com.google.auto.service.AutoService
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.isDataClass
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedOptions
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement


/**
 * GenerateBuilder Processor
 */
@AutoService(Processor::class)
@SupportedOptions("kapt.kotlin.generated")
class GenerateBuilder : AbstractProcessor() {

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(GenerateBuilder::class.java)
            .asSequence()
            .filterIsInstance<TypeElement>()
            .forEach(::processClass)
        return true
    }

    private fun processClass(element: TypeElement) {
        val metadata = element.kotlinMetadata as? KotlinClassMetadata

        if (metadata == null || metadata.data.classProto.isDataClass) {
            processingEnv.error(element, "@GenerateBuilder must be used in a Kotlin data class, cannot be used in $element")
            return
        }

        try {
            val proto = metadata.data.classProto
            val typeVariables = TargetType.genericTypeNames(proto, metadata.data.nameResolver)
            val constructor = TargetConstructor.from(metadata, processingEnv.elementUtils)
            val targetType = TargetType(
                proto,
                element,
                constructor,
                typeVariables
            )

            processingEnv.generateClass(targetType, metadata)
        } catch (pe: ProcessingException) {
            processingEnv.error(pe)
            return
        } catch (e: Throwable) {
            processingEnv.error(
                element,
                "There was an error while processing your annotated classes. error = ${e.message.orEmpty()}"
            )
            return
        }
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf(GenerateBuilder::class.java.canonicalName)

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()
}