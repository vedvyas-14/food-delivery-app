package com.example.fooddeliveryapp.data.model.parser

import com.example.fooddeliveryapp.data.model.MenuCategory
import com.example.fooddeliveryapp.data.model.MenuItem
import org.json.JSONObject

object MenuParser {

    fun parseMenu(json: String): List<MenuCategory> {

        val categories = mutableListOf<MenuCategory>()

        val root = JSONObject(json)

        val cards = root
            .getJSONObject("data")
            .getJSONArray("cards")

        for (i in 0 until cards.length()) {

            val card = cards.getJSONObject(i)

            if (!card.has("groupedCard")) continue

            val regularCards = card
                .getJSONObject("groupedCard")
                .getJSONObject("cardGroupMap")
                .getJSONObject("REGULAR")
                .getJSONArray("cards")

            for (j in 0 until regularCards.length()) {

                val regularCard = regularCards
                    .getJSONObject(j)
                    .getJSONObject("card")
                    .getJSONObject("card")

                if (!regularCard.has("@type")) continue

                val type = regularCard.getString("@type")

                if (!type.contains("ItemCategory")) continue

                val title = regularCard.optString("title")

                val items = mutableListOf<MenuItem>()

                if (regularCard.has("itemCards")) {

                    val itemCards = regularCard.getJSONArray("itemCards")

                    for (k in 0 until itemCards.length()) {

                        val info = itemCards
                            .getJSONObject(k)
                            .getJSONObject("card")
                            .getJSONObject("info")

                        val id = info.optString("id")
                        val name = info.optString("name")
                        val description = info.optString("description")

                        val price =
                            if (info.has("price"))
                                info.getInt("price") / 100
                            else
                                info.optInt("defaultPrice") / 100

                        val imageId = info.optString("imageId")

                        val vegClassifier = info
                            .optJSONObject("itemAttribute")
                            ?.optString("vegClassifier", "NONVEG")

                        val isVeg = when {
                            vegClassifier == "VEG" -> true
                            vegClassifier == "NONVEG" -> false
                            else -> info.optInt("isVeg") == 1 // fallback
                        }

                        items.add(
                            MenuItem(
                                id = id,
                                name = name,
                                price = price,
                                description = description,
                                imageId = imageId,
                                isVeg = isVeg
                            )
                        )
                    }
                }

                if (items.isNotEmpty()) {
                    categories.add(
                        MenuCategory(
                            title = title,
                            items = items
                        )
                    )
                }
            }
        }

        return categories
    }
}