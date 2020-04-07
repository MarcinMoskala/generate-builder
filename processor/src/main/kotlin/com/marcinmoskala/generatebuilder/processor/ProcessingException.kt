package com.marcinmoskala.generatebuilder.processor

import javax.lang.model.element.Element

internal class ProcessingException(
    val element: Element,
    msg: String,
    vararg args: String
) : Exception(String.format(msg, args))