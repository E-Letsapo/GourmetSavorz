package com.example.gourmetsavorz

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FooddrinkActivity : AppCompatActivity() {
    private var selectedFoods = mutableListOf<String>()
    private var selectedDrinks = mutableListOf<String>()
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fooddrink)

        sharedPreferences = getSharedPreferences("SELECTED_FOOD", Context.MODE_PRIVATE)
        sharedPreferences = getSharedPreferences("SELECTED_DRINKS", Context.MODE_PRIVATE)

        // Load the selected foods and drinks from shared preferences
        selectedFoods = sharedPreferences.getStringSet("SELECTED_FOODS", mutableSetOf())?.toMutableList() ?: mutableListOf()
        selectedDrinks = sharedPreferences.getStringSet("SELECTED_DRINKS", mutableSetOf())?.toMutableList() ?: mutableListOf()

        val foodList = listOf("Breakfast", "Lunch", "Dinner", "Side-Dish", "Dessert")
        val drinkList = listOf("Cocktails", "Smoothies", "Juice", "Hot Chocolate", "Beer Cocktails", "Coffee Drinks", "Shots", "Mulled Wine", "Mocktails", "Lemonade", "Slushes", "Tea Drinks")

        // Add food items to food_view LinearLayout
        foodList.forEach { food ->
            val foodItem = TextView(this)
            foodItem.text = food
            foodItem.textSize = 25f
            foodItem.setPadding(25,0,16,0)
            foodItem.setOnClickListener {
                if (selectedFoods.contains(food)) {
                    selectedFoods.remove(food)
                    foodItem.setBackgroundColor(Color.TRANSPARENT)
                } else {
                    selectedFoods.add(food)
                    foodItem.setBackgroundColor(Color.parseColor("#004152"))
                }
                sharedPreferences.edit()
                    .putStringSet("SELECTED_FOODS", selectedFoods.toSet())
                    .apply()
            }
            findViewById<LinearLayout>(R.id.food_view).addView(foodItem)
        }

        // Add drink items to drinks_view LinearLayout
        drinkList.forEach { drink ->
            val drinkItem = TextView(this)
            drinkItem.text = drink
            drinkItem.textSize = 25f
            drinkItem.setPadding(25,0,16,0)
            drinkItem.setOnClickListener {
                if (selectedDrinks.contains(drink)) {
                    selectedDrinks.remove(drink)
                    drinkItem.setBackgroundColor(Color.TRANSPARENT)
                } else {
                    selectedDrinks.add(drink)
                    drinkItem.setBackgroundColor(Color.parseColor("#004152"))
                }
                sharedPreferences.edit()
                    .putStringSet("SELECTED_DRINKS", selectedDrinks.toSet())
                    .apply()
            }
            findViewById<LinearLayout>(R.id.drinks_view).addView(drinkItem)
        }
        val cancel = findViewById<Button>(R.id.cancel)
        val next = findViewById<ImageView>(R.id.next)

        cancel.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        next.setOnClickListener {
            startActivity(Intent(this, RestrictionsActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }
}