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
    AlphabetItem("H", "Heart", "💖", "H is for sweet Heart!", 0xFFFF6B8B),
    AlphabetItem("I", "Ice Cream", "🍦", "I is for yummy Ice Cream!", 0xFFFF9FF3),
    AlphabetItem("J", "Jellyfish", "🪼", "J is for floating Jellyfish!", 0xFF54A0FF),
    AlphabetItem("K", "Kangaroo", "🦘", "K is for hopping Kangaroo!", 0xFFFF9F43),
    AlphabetItem("L", "Lion", "🦁", "L is for brave Lion!", 0xFFFECA57),
    AlphabetItem("M", "Monkey", "🐵", "M is for cheeky Monkey!", 0xFF10AC84),
    AlphabetItem("N", "Nest", "🪺", "N is for cozy Nest!", 0xFF8395A7),
    AlphabetItem("O", "Owl", "🦉", "O is for wise Owl!", 0xFF5F27CD),
    AlphabetItem("P", "Penguin", "🐧", "P is for waddling Penguin!", 0xFF0ABDE3),
    AlphabetItem("Q", "Queen", "👑", "Q is for royal Queen!", 0xFFFFC048),
    AlphabetItem("R", "Rainbow", "🌈", "R is for magic Rainbow!", 0xFFFF6B6B),
    AlphabetItem("S", "Star", "⭐", "S is for bright Star!", 0xFFFFD32A),
    AlphabetItem("T", "Turtle", "🐢", "T is for gentle Turtle!", 0xFF1DD1A1),
    AlphabetItem("U", "Umbrella", "☂️", "U is for colorful Umbrella!", 0xFF48DBFB),
    AlphabetItem("V", "Volcano", "🌋", "V is for glowing Volcano!", 0xFFFF4757),
    AlphabetItem("W", "Whale", "🐳", "W is for giant friendly Whale!", 0xFF2E86DE),
    AlphabetItem("X", "Xylophone", "🎼", "X is for musical Xylophone!", 0xFFEE5253),
    AlphabetItem("Y", "Yacht", "⛵", "Y is for sailing Yacht!", 0xFF00D2D3),
    AlphabetItem("Z", "Zebra", "🦓", "Z is for striped Zebra!", 0xFF341F97)
)
