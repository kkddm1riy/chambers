package io.github.aquabtww.utils

inline fun <reified E : Enum<E>> valueOfOrNull(name: String?): E? {
    return enumValues<E>().firstOrNull { it.name.equals(name, true) }
}