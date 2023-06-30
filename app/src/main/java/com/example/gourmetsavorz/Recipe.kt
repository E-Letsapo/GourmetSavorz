package com.example.gourmetsavorz

class Recipe(val name: String, val ingredients: List<String>, val instructions: List<String>, val image: String?) {
    constructor() : this("", emptyList(), emptyList(), "")
}


