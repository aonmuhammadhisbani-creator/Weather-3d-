package com.example.ui.engine3d

enum class FamousLandmark(
    val landmarkId: String,
    val landmarkName: String,
    val countryName: String,
    val emoji: String,
    val culturalSnippet: String
) {
    MOUNT_FUJI_PAGODA(
        landmarkId = "mount_fuji",
        landmarkName = "Mount Fuji & Pagoda",
        countryName = "Japan",
        emoji = "🗻",
        culturalSnippet = "Japan's sacred stratovolcano standing behind the traditional Chureito Pagoda."
    ),
    STATUE_OF_LIBERTY_SKYLINE(
        landmarkId = "statue_of_liberty",
        landmarkName = "Statue of Liberty & Skyline",
        countryName = "United States",
        emoji = "🗽",
        culturalSnippet = "Lady Liberty standing proudly in New York Harbor before the Manhattan skyscraper skyline."
    ),
    EIFFEL_TOWER(
        landmarkId = "eiffel_tower",
        landmarkName = "Eiffel Tower",
        countryName = "France",
        emoji = "🗼",
        culturalSnippet = "The quintessential wrought-iron lattice monument rising majestically above the Seine in Paris."
    ),
    BIG_BEN_WESTMINSTER(
        landmarkId = "big_ben",
        landmarkName = "Big Ben & Westminster",
        countryName = "United Kingdom",
        emoji = "🕰️",
        culturalSnippet = "The iconic neo-Gothic clock tower of the Palace of Westminster beside London's River Thames."
    ),
    BURJ_KHALIFA(
        landmarkId = "burj_khalifa",
        landmarkName = "Burj Khalifa Spire",
        countryName = "United Arab Emirates",
        emoji = "🏙️",
        culturalSnippet = "The world's tallest architectural marvel soaring 828 meters above Dubai's futuristic skyline."
    ),
    SYDNEY_OPERA_HOUSE(
        landmarkId = "sydney_opera",
        landmarkName = "Sydney Opera House",
        countryName = "Australia",
        emoji = "⛵",
        culturalSnippet = "Jørn Utzon's expressionist masterpiece with interlocking white sail shells overlooking Sydney Harbour."
    ),
    COLOSSEUM(
        landmarkId = "colosseum",
        landmarkName = "The Colosseum",
        countryName = "Italy",
        emoji = "🏛️",
        culturalSnippet = "The monumental Flavian amphitheatre in the heart of Rome, enduring through centuries of history."
    ),
    TAJ_MAHAL(
        landmarkId = "taj_mahal",
        landmarkName = "Taj Mahal",
        countryName = "India",
        emoji = "🕌",
        culturalSnippet = "The ivory-white marble mausoleum with symmetrical minarets and reflecting pool in Agra."
    ),
    PYRAMIDS_OF_GIZA(
        landmarkId = "pyramids_giza",
        landmarkName = "Great Pyramids of Giza",
        countryName = "Egypt",
        emoji = "🏜️",
        culturalSnippet = "The ancient Wonder of the World rising from the golden desert sands beside the Sphinx."
    ),
    CHRIST_THE_REDEEMER(
        landmarkId = "christ_redeemer",
        landmarkName = "Christ the Redeemer",
        countryName = "Brazil",
        emoji = "⛰️",
        culturalSnippet = "The iconic Art Deco statue atop Mount Corcovado embracing the skyline and bay of Rio de Janeiro."
    ),
    GREAT_WALL_PAGODA(
        landmarkId = "great_wall",
        landmarkName = "Great Wall & Imperial Pagoda",
        countryName = "China",
        emoji = "🏯",
        culturalSnippet = "The historic stone fortifications snaking across dramatic mountain ridges with imperial pavilions."
    ),
    BRANDENBURG_GATE(
        landmarkId = "brandenburg_gate",
        landmarkName = "Brandenburg Gate",
        countryName = "Germany",
        emoji = "🏛️",
        culturalSnippet = "The 18th-century neoclassical monument and symbol of unity in Berlin, crowned by the Quadriga."
    ),
    GLOBAL_ICONIC_CITY(
        landmarkId = "global_city",
        landmarkName = "Iconic Metropolis & Peaks",
        countryName = "World Landmark",
        emoji = "🌉",
        culturalSnippet = "A stunning 3D architectural landscape blending modern architectural towers with scenic mountain ranges."
    );

    companion object {
        fun forLocation(country: String?, cityName: String?): FamousLandmark {
            val c = country?.lowercase() ?: ""
            val city = cityName?.lowercase() ?: ""

            return when {
                c.contains("japan") || city.contains("tokyo") || city.contains("kyoto") || city.contains("osaka") -> MOUNT_FUJI_PAGODA
                c.contains("united states") || c.contains("usa") || city.contains("new york") || city.contains("chicago") || city.contains("angeles") || city.contains("francisco") -> STATUE_OF_LIBERTY_SKYLINE
                c.contains("france") || city.contains("paris") || city.contains("lyon") || city.contains("marseille") || city.contains("nice") -> EIFFEL_TOWER
                c.contains("united kingdom") || c.contains("uk") || c.contains("britain") || c.contains("england") || city.contains("london") || city.contains("manchester") -> BIG_BEN_WESTMINSTER
                c.contains("emirates") || c.contains("uae") || city.contains("dubai") || city.contains("abu dhabi") || c.contains("saudi") || c.contains("qatar") -> BURJ_KHALIFA
                c.contains("australia") || city.contains("sydney") || city.contains("melbourne") || c.contains("zealand") -> SYDNEY_OPERA_HOUSE
                c.contains("italy") || city.contains("rome") || city.contains("milan") || city.contains("venice") || city.contains("florence") -> COLOSSEUM
                c.contains("india") || city.contains("delhi") || city.contains("mumbai") || city.contains("agra") || city.contains("bangalore") -> TAJ_MAHAL
                c.contains("egypt") || city.contains("cairo") || city.contains("giza") || c.contains("morocco") || city.contains("casablanca") -> PYRAMIDS_OF_GIZA
                c.contains("brazil") || city.contains("rio") || city.contains("paulo") || c.contains("argentina") || city.contains("buenos aires") -> CHRIST_THE_REDEEMER
                c.contains("china") || city.contains("beijing") || city.contains("shanghai") || city.contains("hong kong") || city.contains("taipei") -> GREAT_WALL_PAGODA
                c.contains("germany") || city.contains("berlin") || city.contains("munich") || c.contains("austria") || city.contains("vienna") || c.contains("switzerland") || city.contains("zurich") -> BRANDENBURG_GATE
                else -> GLOBAL_ICONIC_CITY
            }
        }
    }
}
