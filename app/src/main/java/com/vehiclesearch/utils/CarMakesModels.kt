package com.vehiclesearch.utils

object CarMakesModels {
    private val makesAndModels = mapOf(
        "Toyota" to listOf("Camry", "Corolla", "RAV4", "Highlander", "4Runner", "Tacoma", "Tundra", "Sienna", "Prius", "Avalon", "Sequoia", "Land Cruiser", "Venza", "Crown", "GR86", "Supra"),
        "Honda" to listOf("Civic", "Accord", "CR-V", "Pilot", "Odyssey", "Ridgeline", "HR-V", "Passport", "Fit", "Insight"),
        "Ford" to listOf("F-150", "Escape", "Explorer", "Edge", "Expedition", "Mustang", "Ranger", "Bronco", "Maverick", "EcoSport", "Transit"),
        "Chevrolet" to listOf("Silverado", "Equinox", "Traverse", "Tahoe", "Suburban", "Malibu", "Blazer", "Colorado", "Trax", "Corvette", "Camaro"),
        "Nissan" to listOf("Altima", "Rogue", "Sentra", "Pathfinder", "Frontier", "Titan", "Murano", "Kicks", "Armada", "Versa", "Maxima", "Z"),
        "Hyundai" to listOf("Elantra", "Sonata", "Tucson", "Santa Fe", "Palisade", "Kona", "Accent", "Venue", "Ioniq", "Genesis"),
        "Kia" to listOf("Forte", "Optima", "Sorento", "Sportage", "Telluride", "Soul", "Seltos", "Stinger", "Carnival", "Rio", "Niro"),
        "Jeep" to listOf("Wrangler", "Grand Cherokee", "Cherokee", "Compass", "Renegade", "Gladiator", "Wagoneer", "Grand Wagoneer"),
        "Ram" to listOf("1500", "2500", "3500", "ProMaster"),
        "GMC" to listOf("Sierra", "Acadia", "Terrain", "Yukon", "Canyon", "Savana"),
        "Subaru" to listOf("Outback", "Forester", "Crosstrek", "Ascent", "Impreza", "Legacy", "WRX", "BRZ"),
        "Mazda" to listOf("CX-5", "CX-9", "CX-30", "Mazda3", "Mazda6", "CX-50", "MX-5 Miata"),
        "Volkswagen" to listOf("Jetta", "Passat", "Tiguan", "Atlas", "ID.4", "Taos", "Golf", "Arteon"),
        "BMW" to listOf("3 Series", "5 Series", "X3", "X5", "X1", "X7", "7 Series", "4 Series", "M3", "M5", "i4", "iX"),
        "Mercedes-Benz" to listOf("C-Class", "E-Class", "GLE", "GLC", "A-Class", "S-Class", "GLB", "GLS", "CLA", "EQS"),
        "Audi" to listOf("A4", "Q5", "A3", "Q7", "A6", "Q3", "e-tron", "A5", "Q8", "RS6"),
        "Lexus" to listOf("RX", "ES", "NX", "IS", "GX", "UX", "LS", "LX", "RC"),
        "Acura" to listOf("MDX", "RDX", "TLX", "ILX", "NSX", "Integra"),
        "Tesla" to listOf("Model 3", "Model Y", "Model S", "Model X"),
        "Porsche" to listOf("911", "Cayenne", "Macan", "Panamera", "Taycan", "718"),
        "Land Rover" to listOf("Range Rover", "Range Rover Sport", "Discovery", "Defender", "Evoque", "Velar"),
        "Volvo" to listOf("XC90", "XC60", "XC40", "S60", "S90", "V60", "V90", "C40"),
        "Chrysler" to listOf("Pacifica", "300", "Voyager"),
        "Dodge" to listOf("Charger", "Challenger", "Durango", "Hornet"),
        "Buick" to listOf("Enclave", "Encore", "Envision", "Encore GX"),
        "Cadillac" to listOf("Escalade", "XT5", "CT5", "XT4", "XT6", "Lyriq"),
        "Genesis" to listOf("G70", "G80", "G90", "GV70", "GV80"),
        "Infiniti" to listOf("Q50", "QX60", "QX80", "Q60", "QX50", "QX55"),
        "Lincoln" to listOf("Navigator", "Aviator", "Corsair", "Nautilus"),
        "Mitsubishi" to listOf("Outlander", "Eclipse Cross", "Outlander Sport", "Mirage"),
        "Mini" to listOf("Cooper", "Countryman", "Clubman", "Convertible"),
        "Alfa Romeo" to listOf("Giulia", "Stelvio", "Tonale"),
        "Fiat" to listOf("500", "500X"),
        "Jaguar" to listOf("F-PACE", "E-PACE", "I-PACE", "XF", "F-TYPE"),
        "Maserati" to listOf("Ghibli", "Levante", "Quattroporte", "GranTurismo"),
        "Rivian" to listOf("R1T", "R1S"),
        "Lucid" to listOf("Air")
    )

    fun getAllMakes(): List<String> {
        return makesAndModels.keys.sorted()
    }

    fun getModelsForMake(make: String): List<String> {
        return makesAndModels[make]?.sorted() ?: emptyList()
    }

    fun isValidMake(make: String): Boolean {
        return makesAndModels.containsKey(make)
    }

    fun isValidModel(make: String, model: String): Boolean {
        return makesAndModels[make]?.contains(model) ?: false
    }

    fun searchMakeModel(query: String): List<Pair<String, String>> {
        val results = mutableListOf<Pair<String, String>>()
        val lowerQuery = query.lowercase()

        makesAndModels.forEach { (make, models) ->
            models.forEach { model ->
                if (make.lowercase().contains(lowerQuery) ||
                    model.lowercase().contains(lowerQuery)) {
                    results.add(Pair(make, model))
                }
            }
        }

        return results
    }
}
