package com.example.gourmetsavorz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide

class AllrecipesActivity : AppCompatActivity() {
    private lateinit var recipeList: MutableList<Recipe>
    private lateinit var scrollView: ScrollView
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.allrecipes)

        scrollView = findViewById(R.id.scrollView)
        recipeList = mutableListOf()
        searchView = findViewById(R.id.search)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchRecipes(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        // load all available recipes
        loadRecipes()
    }

    private fun searchRecipes(query: String?) {
        val filteredRecipes = recipeList.filter { it.recipeName.contains(query?:"", ignoreCase = true) }
        recipeList.clear()
        recipeList.addAll(filteredRecipes)
        populateScrollView()
    }

    data class Recipe(var recipeName: String = "", var recipeId: Int = 0, var recipeImageUrl: String = "", var ingredients: String = "", var instructions: String = "")

    private fun loadRecipes() {
        val apiKey = "a1d0d1e323af4b46a068a05c6df3fd41"
        val url = "https://api.spoonacular.com/recipes/complexSearch?apiKey=$apiKey&number=100"
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                Log.d("AllrecipesActivity", "Response: $response") // log the response for debugging
                recipeList.clear()
                val results = response.getJSONArray("results")
                for (i in 0 until results.length()) {
                    val result = results.getJSONObject(i)
                    val recipeName = result.getString("title")
                    val recipeId = result.getInt("id")
                    val recipeImageUrl = result.getString("image")
                    val recipe = Recipe(recipeName, recipeId, recipeImageUrl)
                    recipeList.add(recipe)
                }
                populateScrollView()
            },
            { error ->
                Log.e("AllrecipesActivity", "Error loading recipes: ${error.message}")
                Toast.makeText(this, "Error loading recipes. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show()
            })
        Volley.newRequestQueue(this).add(request)
    }

    private fun populateScrollView() {
        scrollView.removeAllViews()
        for (recipe in recipeList) {
            try {
                val recipeView = layoutInflater.inflate(R.layout.recipe, scrollView, false)
                recipeView.findViewById<TextView>(R.id.recipe_name).text = recipe.recipeName
                recipeView.findViewById<TextView>(R.id.recipe_ingredients).text = recipe.ingredients
                recipeView.findViewById<TextView>(R.id.recipe_instructions).text = recipe.instructions
                val imageView = recipeView.findViewById<ImageView>(R.id.recipe_image)
                Glide.with(this).load(recipe.recipeImageUrl).into(imageView)
                scrollView.addView(recipeView)
            } catch (e: Exception) {
                Log.e("AllrecipesActivity", "Error populating recipe view: ${e.message}")
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }
}





/*GIVE A MODIFIED CODE SNIPPET TO RESOLVE ALL THE  SUGGESTIONS IN KOTLIN:
Add error handling for network requests: The code currently assumes that the network requests will always succeed. However, network requests can fail for various reasons, such as poor internet connectivity or server errors. It would be a good idea to add error handling to inform the user if a request fails and provide suggestions for what they can do to resolve the issue.
Add pagination support for recipe results: The code currently loads a maximum of 100 recipes at once, which may not be sufficient if there are many recipes available. A better approach would be to add support for pagination, which would allow the user to load more recipes as they scroll down the screen. This would improve the user experience and reduce the amount of data that needs to be loaded at once.

Improve the search functionality: The code currently searches for recipes based on the recipe name only. It would be more useful to allow users to search for recipes based on other criteria, such as ingredients, cuisine, or dietary restrictions. Additionally, the search functionality could be improved by providing auto-complete suggestions as the user types, and by sorting the search results based on relevance or user ratings.

Add caching support for recipe data: The code currently loads all recipe data from the API every time the app is launched or the user performs a search. This can be slow and inefficient, especially if the user performs multiple searches or navigates back and forth between screens. A better approach would be to cache recipe data locally, either in a local database or in memory, so that it can be quickly retrieved and displayed without having to make another network request.

Improve the UI design: The code currently uses a simple ScrollView to display recipe data. While this is functional, it may not be the most visually appealing or user-friendly approach. Consider using a RecyclerView or a more sophisticated layout to display recipe data in a more organized and visually appealing way. Additionally, consider adding more visual cues, such as icons or images, to make the app more engaging and intuitive to use.*/