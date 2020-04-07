
package com.marcinmoskala.generatebuilder.processor.internal

const val BLOCK_FUN_NAME = "block"

internal fun String.toGenerateBuilderBuilderName() = "${this}Builder"