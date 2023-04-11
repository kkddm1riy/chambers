package io.github.aquabtww.utils

import io.github.aquabtww.mm
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration

fun String.parseMini(): Component =
    Component.empty().decoration(TextDecoration.ITALIC, false).append(mm.deserialize(this))
