package com.marcinmoskala.generatebuilder

import com.marcinmoskala.generatebuilder.processor.GenerateBuilder
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import org.intellij.lang.annotations.Language
import org.junit.Test
import kotlin.test.assertEquals

class GenerationTests {

    @Test
    fun `Empty class generation test`() {
        checkGeneration(
            name = "EmptyClass",
            from = """
package a

import com.marcinmoskala.generatebuilder.annotation.GenerateBuilder

@GenerateBuilder
class EmptyClass 
            """,
            to = """
// Code generated by GenerateBuilder. Do not edit.
package a

class EmptyClassBuilder() {
    constructor(instance: EmptyClass) : this()

    fun build(): EmptyClass = a.EmptyClass()
}
            """
        )
    }

    @Test
    fun `Single nullable value class generation test`() {
        checkGeneration(
            name = "ValueClass",
            from = """
package a

import com.marcinmoskala.generatebuilder.annotation.GenerateBuilder

@GenerateBuilder
data class ValueClass(val value: String?) 
            """,
            to = """
// Code generated by GenerateBuilder. Do not edit.
package a

import kotlin.String

class ValueClassBuilder() {
    var value: String? = null

    constructor(instance: ValueClass) : this() {
        this.value = instance.value
    }

    constructor(value: String?) : this() {
        this.value = value
    }

    fun withValue(newValue: String?): ValueClassBuilder = this.apply { this.value = newValue }

    fun build(): ValueClass = a.ValueClass(value)
}
            """
        )
    }

    @Test
    fun `Single not-nullable value class generation test`() {
        checkGeneration(
            name = "ValueClass",
            from = """
package a

import com.marcinmoskala.generatebuilder.annotation.GenerateBuilder

@GenerateBuilder
class ValueClass(val value: String) 
            """,
            to = """
// Code generated by GenerateBuilder. Do not edit.
package a

import kotlin.String
import kotlin.properties.Delegates

class ValueClassBuilder() {
    var value: String by Delegates.notNull()

    constructor(instance: ValueClass) : this() {
        this.value = instance.value
    }

    constructor(value: String) : this() {
        this.value = value
    }

    fun withValue(newValue: String): ValueClassBuilder = this.apply { this.value = newValue }

    fun build(): ValueClass = a.ValueClass(value)
}
            """
        )
    }

    @Test
    fun `Multiple required values class generation test`() {
        checkGeneration(
            name = "User",
            from = """
package com.generatebuilder

import com.marcinmoskala.generatebuilder.annotation.GenerateBuilder
import com.marcinmoskala.generatebuilder.GenerationTests.Id
import java.math.BigDecimal

@GenerateBuilder
class User(
    val userId: Id,
    val name: String,
    val surname: String,
    val money: BigDecimal?
)
            """,
            to = """
// Code generated by GenerateBuilder. Do not edit.
package com.generatebuilder

import com.marcinmoskala.generatebuilder.GenerationTests
import java.math.BigDecimal
import kotlin.String
import kotlin.properties.Delegates

class UserBuilder() {
    var userId: GenerationTests.Id by Delegates.notNull()

    var name: String by Delegates.notNull()

    var surname: String by Delegates.notNull()

    var money: BigDecimal? = null

    constructor(instance: User) : this() {
        this.userId = instance.userId
        this.name = instance.name
        this.surname = instance.surname
        this.money = instance.money
    }

    constructor(
        userId: GenerationTests.Id,
        name: String,
        surname: String,
        money: BigDecimal?
    ) : this() {
        this.userId = userId
        this.name = name
        this.surname = surname
        this.money = money
    }

    fun withUserId(newUserId: GenerationTests.Id): UserBuilder = this.apply { this.userId = newUserId }

    fun withName(newName: String): UserBuilder = this.apply { this.name = newName }

    fun withSurname(newSurname: String): UserBuilder = this.apply { this.surname = newSurname }

    fun withMoney(newMoney: BigDecimal?): UserBuilder = this.apply { this.money = newMoney }

    fun build(): User = com.generatebuilder.User(userId, name, surname, money)
}
            """
        )
    }

    fun checkGeneration(name: String, @Language("kotlin") from: String, @Language("kotlin") to: String) {
        val kotlinSource = SourceFile.kotlin("$name.kt", from.trimIndent())

        val result = KotlinCompilation().apply {
            sources = listOf(kotlinSource)

            // pass your own instance of an annotation processor
            annotationProcessors = listOf(GenerateBuilder())

            inheritClassPath = true
            messageOutputStream = System.out // see diagnostics in real time
        }.compile()

        assertEquals(ExitCode.OK, result.exitCode)

        val generatedFiles = result.generatedFiles
        val generated = generatedFiles.firstOrNull() { it.name == "${name}Builder.kt" }
            ?: error("No element with expected name ${name}Builder.kt. Generates classes are: ${generatedFiles.map { it.name }}")
        assertEquals(
            to.trimIndent().trimStart().trimEnd(),
            generated.readText().trimIndent().trimStart().trimEnd()
        )
    }

    class Id(val id: String)
}