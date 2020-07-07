@file:Suppress("UNCHECKED_CAST")

package org.jetbrains.kotlin.abicmp

abstract class Entity(val id: String) {
    val data = HashMap<EntityProperty<*, *>, Any?>()
    val annotations = HashMap<AnnotationsProperty<*, *>, List<AnnotationEntry>>()
}

abstract class EntityProperty<S, T>(val index: Int, val parse: (S) -> T) {
    var name = ""

    var valueToString: (T) -> String = { it.toString() }

    var valueToHtml: (T) -> String = { it.toHtmlString() }

    var diff: (T, T) -> String? = { x1: T, x2: T -> if (x1 == x2) null else "" }
}

fun <T, P : EntityProperty<*, T>> P.withHtml(fn: (T) -> String) =
        apply { valueToHtml = fn }
