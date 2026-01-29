package com.example.myapplication

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object PersistenceManager {
    private const val PREFS_NAME = "app_data"
    private const val KEY_RESOURCES = "resources_list"
    private const val KEY_CATEGORIES = "categories_list"

    fun saveResources(context: Context, resources: List<Resource>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        resources.forEach { resource ->
            val jsonObject = JSONObject().apply {
                put("id", resource.id)
                put("type", resource.type.name)
                put("title", resource.title)
                put("description", resource.description)
                put("dateCreated", resource.dateCreated)
                put("category", resource.category ?: "")
                put("url", resource.url ?: "")
                put("content", resource.content ?: "")
                put("dueDate", resource.dueDate ?: "")
                put("time", resource.time ?: "")
                put("priority", resource.priority ?: "")
                put("isRepeatReminder", resource.isRepeatReminder)
                put("assetCount", resource.assetCount)
                put("isFavorite", resource.isFavorite)
                put("isArchived", resource.isArchived)
                put("isDeleted", resource.isDeleted)
            }
            jsonArray.put(jsonObject)
        }
        prefs.edit().putString(KEY_RESOURCES, jsonArray.toString()).apply()
    }

    fun loadResources(context: Context): MutableList<Resource> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(KEY_RESOURCES, null) ?: return mutableListOf()
        
        val resources = mutableListOf<Resource>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                resources.add(Resource(
                    id = obj.optString("id"),
                    type = ResourceType.valueOf(obj.optString("type", "Link")),
                    title = obj.optString("title"),
                    description = obj.optString("description"),
                    dateCreated = obj.optString("dateCreated"),
                    category = obj.optString("category").takeIf { it.isNotEmpty() },
                    url = obj.optString("url").takeIf { it.isNotEmpty() },
                    content = obj.optString("content").takeIf { it.isNotEmpty() },
                    dueDate = obj.optString("dueDate").takeIf { it.isNotEmpty() },
                    time = obj.optString("time").takeIf { it.isNotEmpty() },
                    priority = obj.optString("priority").takeIf { it.isNotEmpty() },
                    isRepeatReminder = obj.optBoolean("isRepeatReminder"),
                    assetCount = obj.optInt("assetCount"),
                    isFavorite = obj.optBoolean("isFavorite"),
                    isArchived = obj.optBoolean("isArchived"),
                    isDeleted = obj.optBoolean("isDeleted")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return resources
    }

    fun saveCategories(context: Context, categories: List<Category>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        categories.forEach { category ->
            val jsonObject = JSONObject().apply {
                put("name", category.name)
                put("color", category.color.value.toLong().toString()) // Store color as ULong string
            }
            jsonArray.put(jsonObject)
        }
        prefs.edit().putString(KEY_CATEGORIES, jsonArray.toString()).apply()
    }

    fun loadCategories(context: Context): MutableList<Category> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(KEY_CATEGORIES, null) ?: return mutableListOf()

        val categories = mutableListOf<Category>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val colorVal = obj.optString("color").toULongOrNull() ?: 0xFF2E65F3UL 
                categories.add(Category(
                    name = obj.optString("name"),
                    color = androidx.compose.ui.graphics.Color(colorVal)
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return categories
    }
}
