package com.example.model

import androidx.compose.ui.graphics.Color

enum class GameType(val id: String, val title: String, val subtitle: String, val icon: String, val themeColor: Long) {
    COLOR_MATCH("color_match", "Color Pop & Match", "Learn colors with bouncy balloons!", "🎈", 0xFFFF5252),
    NUMBER_COLOR("number_color", "Number Paint & Count", "Paint by numbers 1-5 to reveal secrets!", "🔢", 0xFFFFD32A),
    SHAPE_DETECTIVE("shape_detective", "Shape Detective", "Match geometric shapes & puzzles!", "⭐", 0xFF2ED573),
    ALPHABET_COLOR("alphabet_color", "ABC Phonics & Animals", "Trace letters & discover animal friends!", "🔤", 0xFF1E90FF)
}

data class ColorOption(val name: String, val color: Color, val hex: Long)

val KidGameColors = listOf(
    ColorOption("Red", Color(0xFFFF3838), 0xFFFF3838),
    ColorOption("Blue", Color(0xFF1E90FF), 0xFF1E90FF),
    ColorOption("Yellow", Color(0xFFFFD32A), 0xFFFFD32A),
    ColorOption("Green", Color(0xFF2ED573), 0xFF2ED573),
    ColorOption("Orange", Color(0xFFFF793F), 0xFFFF793F),
    ColorOption("Purple", Color(0xFF9B59B6), 0xFF9B59B6),
    ColorOption("Pink", Color(0xFFFF6B8B), 0xFFFF6B8B)
)

data class NumberColorSection(
    val id: Int,
    val number: Int,
    val label: String,
    val targetColorHex: Long,
    var isColored: Boolean = false
)

enum class KidShape(val label: String, val emoji: String) {
    CIRCLE("Circle", "⚪"),
    SQUARE("Square", "⬛"),
    TRIANGLE("Triangle", "🔺"),
    STAR("Star", "⭐"),
    HEART("Heart", "❤️")
}

data class AlphabetItem(
    val letter: String,
    val word: String,
    val emoji: String,
    val prompt: String,
    val strokeColor: Long
)

val AlphabetCatalog = listOf(
    AlphabetItem("A", "Apple", "🍎", "A is for crunchy Apple!", 0xFFFF3838),
    AlphabetItem("B", "Bear", "🐻", "B is for cuddly Bear!", 0xFF795548),
    AlphabetItem("C", "Cat", "🐱", "C is for playful Cat!", 0xFFFF793F),
    AlphabetItem("D", "Dinosaur", "🦖", "D is for mighty Dinosaur!", 0xFF2ED573),
    AlphabetItem("E", "Elephant", "🐘", "E is for friendly Elephant!", 0xFF70A1FF),
    AlphabetItem("F", "Fish", "🐠", "F is for swimming Fish!", 0xFF00D2D3),
    AlphabetItem("G", "Giraffe", "🦒", "G is for tall Giraffe!", 0xFFFFD32A),
    AlphabetItem("H", "Heart", "💖", "H is for sweet Heart!", 0xFFFF6B8B)
)
