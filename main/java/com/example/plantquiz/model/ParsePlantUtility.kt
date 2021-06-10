                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            package com.example.plantquiz.model

import org.json.JSONArray
import org.json.JSONObject

class ParsePlantUtility {

    fun parsePlantObjectsFromJSONData() : List<Plant> {

        var allPlantObjects: ArrayList<Plant> = ArrayList()
        var downloadingObjects = DownloadingObjects()
        var topLevelPlantJSONData = downloadingObjects.downloadJSONDataFromLink(
                "http://plantplaces.com/perl/mobile/flashcard.pl")

        var topLevelJSONObject : JSONObject = JSONObject(topLevelPlantJSONData)
        var plantObjectsArray : JSONArray = topLevelJSONObject.getJSONArray("values")

        var index : Int = 0
        while (index < plantObjectsArray.length()) {

            var plantObject : Plant = Plant()
            var jsonObject = plantObjectsArray.getJSONObject(index)

            with(jsonObject) {
                plantObject.genus = getString("genus")
                plantObject.species = getString("species")
                plantObject.cultivar = getString("cultivar")
                plantObject.common = getString("common")
                plantObject.picture_name = getString("picture_name")
                plantObject.description = getString("description")
                plantObject.difficulty = getInt("difficulty")
                plantObject.id = getInt("id")

            }
            index++

            allPlantObjects.add(plantObject)
        }

        return allPlantObjects
    }
}