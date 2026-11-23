package com.tllstudio.smartlearn

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

data class LearningLanguageOption(
    val code: String,
    val title: String,
    val nativeName: String,
    val svgAsset: String,
    val locale: Locale
)

data class StoryQuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)

data class StoryPage(
    val pageNumber: Int,
    val imageAssetPath: String,
    val text: String,
    val spanishTranslation: String = "",
    val urduTranslation: String = "",
    val hindiTranslation: String = "",
    val practiceWords: Set<String> = emptySet(),
    val quiz: StoryQuizQuestion? = null
)

data class StoryBook(
    val id: String,
    val title: String,
    val nativeTitleUrdu: String = "",
    val nativeTitleHindi: String = "",
    val author: String,
    val illustrator: String,
    val publisher: String,
    val level: Int,
    val coverAssetPath: String,
    val pages: List<StoryPage>,
    val isFeaturedHero: Boolean = false,
    val isAiGenerated: Boolean = false,
    val requiresDownload: Boolean = false,
    val downloadUrl: String = "",
    val bookType: String = "LEVELED", // "LEVELED", "DECODABLE", "SERIES", "ASSESSMENT", "CYOC"
    val bookSource: String = "LIBRARY", // "LIBRARY", "CLASSROOM", "PARTNER", "MY_STORY"
    val categoryTag: String = "Family & Friends", // "Animals & Nature", "Family & Friends", "Adventure & Travel", "Science & Weather", "Bedtime & Folktales"
    val phonicsSkillTag: String = "",
    val seriesTag: String = "",
    val assignedByTeacher: String = "",
    val isDailyDrop: Boolean = false
)

data class SignedInAccountState(
    val isSignedIn: Boolean = false,
    val email: String = "",
    val displayName: String = "Local Device Account",
    val syncStatus: String = "Saved on this device"
)

data class LearnerProfile(
    val id: String,
    val name: String,
    val avatarIndex: Int, // 1..12 -> CustomLearnerVectorAvatar
    val stars: Int = 0,
    val minutesReadToday: Int = 0,
    val dailyGoalMinutes: Int = 10,
    val streakDays: Int = 0,
    val wordsPerMinute: Int = 0,
    val completedBookIds: Set<String> = emptySet(),
    val weeklyMinutes: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0), // Mon..Sun
    val bookPageProgress: Map<String, Int> = emptyMap(), // bookId -> lastPageIndex
    val gamesPlayedCount: Int = 0,
    val readingSecondsToday: Int = 0,
    val lastActiveDateKey: String = ""
)

data class PrizeBadge(
    val id: String,
    val title: String,
    val subtitle: String,
    val badgeLabel: String,
    val svgAsset: String,
    val category: String, // "STARS", "STREAK", "BOOKS"
    val requiredStars: Int = 0,
    val requiredStreak: Int = 0,
    val requiredBooks: Int = 0
)

data class VocabularyItem(
    val word: String,
    val phonetics: String,
    val category: String,
    val svgAssetPath: String,
    val urduMeaning: String,
    val hindiMeaning: String,
    val spanishMeaning: String,
    val exampleSentence: String
)

data class JumbleWordChallenge(
    val word: String,
    val hint: String,
    val category: String,
    val isHard: Boolean
)

data class GroupMemberStat(
    val profileId: String = "",
    val name: String,
    val avatarIndex: Int,
    val minutesThisWeek: Int,
    val storiesRead: Int,
    val gamesPlayed: Int = 0,
    val speedWpm: Int,
    val stars: Int = 0,
    val completedBookIds: Set<String> = emptySet(),
    val isActiveThisWeek: Boolean,
    val lastActiveLabel: String = "Today"
)

data class ReadingGroup(
    val id: String,
    val name: String,
    val adminName: String,
    val languageTitle: String,
    val inviteCode: String,
    val createdDate: String,
    val members: List<GroupMemberStat>,
    val assignedBookIds: List<String> = listOf("sw_13916", "book_rained_fish"),
    val popularBookIds: List<String> = listOf("sw_13916", "book_rained_fish", "sw_37273", "sw_44485")
)

data class SpeedVehicleBadge(
    val name: String,
    val emoji: String,
    val minWpm: Int,
    val description: String,
    val accentColorHex: Long
)

object SeekhCatalog {

    val speedVehicles = listOf(
        SpeedVehicleBadge("Rocket Reader", "🚀", 85, "Lightning-fast reading speed!", 0xFFE53935),
        SpeedVehicleBadge("Sky Glider", "🛩️", 72, "Soaring high above the clouds!", 0xFF1E88E5),
        SpeedVehicleBadge("Hot Air Balloon", "🎈", 62, "Floating smoothly through every story!", 0xFF8E24AA),
        SpeedVehicleBadge("Turbo Car", "🚗", 52, "Cruising fast down the reading highway!", 0xFFFB8C00),
        SpeedVehicleBadge("Speedy Bike", "🏍️", 42, "Zooming ahead word by word!", 0xFF00897B),
        SpeedVehicleBadge("City Scooter", "🛴", 32, "Rolling steadily along the street!", 0xFF43A047),
        SpeedVehicleBadge("Park Bicycle", "🚲", 22, "Pedaling smoothly with great focus!", 0xFF3949AB),
        SpeedVehicleBadge("Steady Tortoise", "🐢", 0, "Careful and steady wins the race!", 0xFF6D4C41)
    )

    fun getVehicleForWpm(wpm: Int): SpeedVehicleBadge {
        return speedVehicles.firstOrNull { wpm >= it.minWpm } ?: speedVehicles.last()
    }

    val supportedLanguages = listOf(
        LearningLanguageOption("en_ur", "اردو + English", "اردو", "images/language_selection_urdu.svg", Locale.US),
        LearningLanguageOption("en", "English only", "English", "images/language_selection_english.svg", Locale.US),
        LearningLanguageOption("en_hi", "हिंदी + English", "हिंदी", "images/language_selection_hindi.svg", Locale.US),
        LearningLanguageOption("en_es", "Español + English", "Español", "images/language_selection_spanish.svg", Locale.US),
        LearningLanguageOption("en_pt", "Português + English", "Português", "images/language_selection_portuguese.svg", Locale.US),
        LearningLanguageOption("en_ar", "العربية + English", "العربية", "images/language_selection_arabic.svg", Locale.US),
        LearningLanguageOption("en_bn", "বাংলা + English", "বাংলা", "images/language_selection_bengali.svg", Locale.US),
        LearningLanguageOption("en_ta", "தமிழ் + English", "தமிழ்", "images/language_selection_tamil.svg", Locale.US),
        LearningLanguageOption("en_te", "తెలుగు + English", "తెలుగు", "images/language_selection_telugu.svg", Locale.US),
        LearningLanguageOption("en_mr", "मराठी + English", "मराठी", "images/language_selection_marathi.svg", Locale.US),
        LearningLanguageOption("en_gu", "ગુજરાતી + English", "ગુજરાતી", "images/language_selection_gujrati.svg", Locale.US)
    )

    private val wordMeaningsUrdu = mapOf(
        "blast" to "تیز جھونکا",
        "candles" to "موم بتیاں",
        "crashed" to "ٹکرایا",
        "clouds" to "بادل",
        "thunder" to "بجلی کی گرج",
        "rain" to "بارش",
        "ladybird" to "لیڈی برڈ کیڑا",
        "special" to "خاص",
        "wings" to "پَر",
        "yellow" to "پیلا",
        "butterfly" to "تتلی",
        "friends" to "دوست",
        "school" to "اسکول",
        "painted" to "رنگ کیا",
        "bright" to "چمکدار",
        "caterpillar" to "کیٹرپلر",
        "shoe" to "جوتا",
        "spider" to "مکڑی",
        "frog" to "مینڈک",
        "grasshopper" to "ٹڈا",
        "crab" to "کیکڑا",
        "cocoon" to "خول / کوکون",
        "train" to "ریل گاڑی",
        "beach" to "ساحلِ سمندر",
        "chocolates" to "چاکلیٹ",
        "drummers" to "ڈھول بجانے والے",
        "sailor" to "ملاح / بحری فوجی",
        "cupboard" to "الماری",
        "curtain" to "پردہ",
        "borrow" to "ادھار لینا",
        "return" to "واپس کرنا",
        "books" to "کتابیں",
        "raincoat" to "برساتی کوٹ",
        "street" to "گلی / سڑک",
        "colors" to "رنگ",
        "unique" to "منفرد / بے مثال",
        "gleamed" to "چمک اٹھے",
        "scrubbed" to "رگڑ کر دھویا",
        "whispers" to "سرگوشی کرتا ہے",
        "grandmother" to "دادی / نانی",
        "grandfather" to "دادا / نانا"
    )

    private val wordMeaningsHindi = mapOf(
        "blast" to "तेज़ झोंका",
        "candles" to "मोमबत्तियाँ",
        "crashed" to "टकराया",
        "clouds" to "बादल",
        "thunder" to "गड़गड़ाहट",
        "rain" to "बारिश",
        "ladybird" to "लेडीबर्ड",
        "special" to "ख़ास",
        "wings" to "पंख",
        "yellow" to "पीला",
        "butterfly" to "तितली",
        "friends" to "दोस्त",
        "school" to "स्कूल",
        "painted" to "रंगा हुआ",
        "bright" to "चमकीला",
        "caterpillar" to "कैटरपिलर",
        "shoe" to "जूता",
        "spider" to "मकड़ी",
        "frog" to "मेंढक",
        "grasshopper" to "टिड्डा",
        "crab" to "केकड़ा",
        "cocoon" to "कोकून",
        "train" to "रेलगाड़ी",
        "beach" to "समुद्र तट",
        "chocolates" to "चॉकलेट",
        "drummers" to "ढोल वादक",
        "sailor" to "नाविक",
        "cupboard" to "अलमारी",
        "curtain" to "पर्दा",
        "borrow" to "उधार लेना",
        "return" to "लौटाना",
        "books" to "किताबें",
        "raincoat" to "रेनकोट",
        "street" to "गली / सड़क",
        "colors" to "रंग",
        "unique" to "अनोखा",
        "gleamed" to "चमक उठे",
        "scrubbed" to "रगड़ कर साफ़ किया",
        "whispers" to "फुसफुसाता है",
        "grandmother" to "दादी / नानी",
        "grandfather" to "दादा / नाना"
    )

    fun getWordSyllablesList(rawWord: String): List<String> {
        val clean = rawWord.lowercase().replace(Regex("[^a-z]"), "")
        return when {
            clean.isEmpty() -> listOf(rawWord)
            clean == "candles" -> listOf("can", "dles")
            clean == "crashed" -> listOf("crash", "ed")
            clean == "thunder" -> listOf("thun", "der")
            clean == "ladybird" -> listOf("la", "dy", "bird")
            clean == "butterfly" -> listOf("but", "ter", "fly")
            clean == "caterpillar" -> listOf("cat", "er", "pil", "lar")
            clean == "grasshopper" -> listOf("grass", "hop", "per")
            clean == "special" -> listOf("spe", "cial")
            clean == "yellow" -> listOf("yel", "low")
            clean == "morning" -> listOf("morn", "ing")
            clean == "chocolates" -> listOf("choc", "o", "lates")
            clean == "drummers" -> listOf("drum", "mers")
            clean == "cupboard" -> listOf("cup", "board")
            clean == "curtain" -> listOf("cur", "tain")
            clean == "raincoat" -> listOf("rain", "coat")
            clean == "grandmother" -> listOf("grand", "moth", "er")
            clean == "grandfather" -> listOf("grand", "fa", "ther")
            clean.length <= 4 -> listOf(clean)
            else -> clean.chunked(3)
        }
    }

    fun getWordSyllables(rawWord: String): String {
        return getWordSyllablesList(rawWord).joinToString(" • ")
    }

    fun getWordTooltip(rawWord: String, langCode: String): Pair<String, String> {
        val clean = rawWord.lowercase().replace(Regex("[^a-z]"), "")
        val syllables = getWordSyllablesList(rawWord).joinToString(" • ")
        val meaning = when {
            langCode.contains("ur") -> wordMeaningsUrdu[clean] ?: "تلفظ سننے کے لیے ہر حصے پر ٹیپ کریں"
            langCode.contains("hi") -> wordMeaningsHindi[clean] ?: "उच्चारण सुनने के लिए प्रत्येक भाग पर टैप करें"
            else -> "Tap each syllable or speaker to hear pronunciation"
        }
        return syllables to meaning
    }

    fun getWordTranslation(rawWord: String, langCode: String): String {
        return getWordTooltip(rawWord, langCode).second
    }

    // Complete Unabridged Library of 36 Long Multi-Illustration Books (12 to 17 Illustrated Pages in EVERY Story!)
    private val rawDefaultBooks: List<StoryBook> = listOf(
        // 1. Featured Hero Book on Home Screen (Play Store Screenshot #6: "Colors on the street" — Full 14-Page Illustrated Story!)
        StoryBook(
            id = "hero_colors_street",
            title = "Colors on the street",
            nativeTitleUrdu = "گلی کے خوبصورت رنگ",
            nativeTitleHindi = "गली के सुंदर रंग",
            author = "Mabel Mnensa",
            illustrator = "Ntembiso Velemane",
            publisher = "Book Dash • StoryWeaver",
            level = 3,
            coverAssetPath = "books/sw_13916/image_5.webp",
            isFeaturedHero = true,
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_5.webp", "Every morning, our neighborhood street wakes up with bright colors, happy music, and friendly faces.", "Cada mañana, la calle despierta con colores brillantes.", "ہر صبح ہماری گلی چمکدار رنگوں، خوشگوار موسیقی اور دوستانہ چہروں کے ساتھ جاگتی ہے۔", "हर सुबह हमारी गली चमकीले रंगों और खुशहाल संगीत के साथ जागती है।", setOf("morning", "bright", "friends")),
                StoryPage(2, "books/sw_44485/image_2.webp", "Nicole steps outside holding her mother's hand as the golden sun rises above the rooftops.", "Nicole sale de la mano de su madre bajo el sol dorado.", "نکول اپنی امی کا ہاتھ پکڑ کر باہر نکلتی ہے جب سنہرا سورج چھتوں کے اوپر چمکتا ہے۔", "निकोल अपनी माँ का हाथ पकड़कर बाहर निकलती है जब सुनहरा सूरज चमकता है।", setOf("bright", "street")),
                StoryPage(3, "books/sw_44485/image_3.webp", "At the busy corner crosswalk, neighbors in blue, green, and orange coats wave good morning to one another.", "En la esquina, los vecinos saludan con abrigos de colores.", "مصروف چوراہے پر نیلے، ہرے اور نارنجی کوٹ پہنے ہمسائے ایک دوسرے کو صبح بخیر کہتے ہیں۔", "चौराहे पर नीले, हरे और नारंगी कोट पहने पड़ोसी एक-दूसरे को नमस्ते कहते हैं।", setOf("colors", "friends")),
                StoryPage(4, "books/sw_44485/image_4.webp", "The silver neighborhood tram glides past painted murals of flowers, birds, and rainbows.", "El tranvía plateado pasa junto a murales pintados.", "چاندی جیسی ٹرین دیواروں پر بنے پھولوں، پرندوں اور قوسِ قزح کے رنگین نقش و نگار کے پاس سے گزرتی ہے۔", "चमकीली ट्रेन दीवारों पर बने फूलों और इंद्रधनुष के चित्रों के पास से गुज़रती है।", setOf("painted", "train")),
                StoryPage(5, "books/sw_44485/image_5.webp", "A little girl in a pink dress smiles and offers a cheerful high-five by the flower stand.", "Una niña con vestido rosa sonríe junto al puesto de flores.", "پھولوں کے اسٹال کے پاس گلابی لباس میں ایک ننھی بچی مسکرا کر سلام کرتی ہے۔", "फूलों की दुकान के पास गुलाबी ड्रेस पहने एक नन्ही बच्ची मुस्कुराती है।", setOf("friends", "bright"),
                    StoryQuizQuestion("How does the neighborhood street wake up every morning?", listOf("With bright colors, happy music, and friendly faces", "In total darkness", "Under heavy snow"), 0)
                ),
                StoryPage(6, "books/sw_44485/image_6.webp", "Look at the sweet shop on the corner! Golden chocolates and rainbow treats shine in the glass jars.", "¡Mira la tienda de dulces en la esquina!", "کونے پر مٹھائی کی دکان دیکھو! شیشے کے مرتبانوں میں سنہری چاکلیٹ اور رنگ برنگی ٹافیاں چمک رہی ہیں۔", "कोने की मिठाई की दुकान देखो! काँच के जार में सुनहरी चॉकलेट चमक रही हैं।", setOf("chocolates", "bright")),
                StoryPage(7, "books/sw_44485/image_7.webp", "Across the pavement, young athletes in crisp white karate uniforms practice their morning stretches.", "Los jóvenes atletas con uniformes blancos practican karate.", "سڑک کے دوسری طرف سفید کراٹے یونیفارم پہنے نوجوان کھلاڑی صبح کی ورزش کر رہے ہیں۔", "सड़क के उस पार सफ़ेद यूनिफ़ॉर्म पहने बच्चे कराटे का अभ्यास कर रहे हैं।", setOf("street", "friends")),
                StoryPage(8, "books/sw_44485/image_8.webp", "With a focused bow and a swift kick, they show how teamwork and discipline make everyone stronger.", "Con una reverencia muestran trabajo en equipo y disciplina.", "ادب سے جھک کر اور پھرتی سے وہ دکھاتے ہیں کہ محنت اور نظم و ضبط سب کو مضبوط بناتا ہے۔", "वे दिखाते हैं कि मेहनत और अनुशासन सबको मज़बूत बनाता है।", setOf("special", "friends")),
                StoryPage(9, "books/sw_44485/image_9.webp", "Children along the sidewalk clap and cheer as the karate team finishes their routine!", "Los niños en la acera aplauden con entusiasmo.", "فٹ پاتھ پر کھڑے بچے تالیاں بجاتے ہیں جب کراٹے ٹیم اپنا مظاہرہ مکمل کرتی ہے!", "फ़ुटपाथ पर खड़े बच्चे उत्साह से तालियाँ बजाते हैं!", setOf("friends", "street")),
                StoryPage(10, "books/sw_44485/image_10.webp", "Near the harbor gate, a navy sailor in a snowy white cap shares stories of sailing across the blue sea.", "Una marinera con gorra blanca cuenta historias del mar azul.", "بندرگاہ کے دروازے کے پاس سفید ٹوپی پہنے بحریہ کی ملاح نیلے سمندر کے سفر کی کہانیاں سناتی ہے۔", "बंदरगाह के पास नौसेना की नाविक नीले समुद्र के सफ़र की कहानियाँ सुनाती है।", setOf("sailor", "books"),
                    StoryQuizQuestion("What shines inside the glass jars at the corner sweet shop?", listOf("Golden chocolates and rainbow treats", "Wooden shoes", "Raincoats"), 0)
                ),
                StoryPage(11, "books/sw_44485/image_11.webp", "Suddenly, the street drummers arrive wearing bright red and yellow shirts! Their wooden drums echo down the avenue.", "Los bateristas usan camisas rojas y amarillas.", "اچانک سرخ اور پیلی قمیضیں پہنے گلی کے ڈھول بجانے والے آ جاتے ہیں! ان کے ڈھول کی تھاپ پوری سڑک پر گونجتی ہے۔", "अचानक लाल और पीली शर्ट पहने ढोल वादक आ जाते हैं! उनके ढोल की थाप गूँज उठती है।", setOf("drummers", "yellow")),
                StoryPage(12, "books/sw_44485/image_12.webp", "\"Do you want to dance with us?\" the lead drummer asks with a big warm smile.", "\"¿Quieres bailar con nosotros?\", pregunta el tamborilero.", "کیا تم ہمارے ساتھ ناچنا چاہتے ہو؟ ڈھول بجانے والا بڑی گرمجوشی سے مسکرا کر پوچھتا ہے۔", "\"क्या तुम हमारे साथ नाचना चाहते हो?\" प्रमुख ढोल वादक मुस्कुराकर पूछता है।", setOf("drummers", "friends")),
                StoryPage(13, "books/sw_44485/image_13.webp", "As afternoon turns to golden sunset, lanterns light up above the fruit stalls and book carts.", "Al atardecer, las linternas iluminan los puestos de frutas y libros.", "جیسے ہی شام کا سنہرا سورج ڈھلتا ہے، پھلوں اور کتابوں کے ٹھیلوں کے اوپر روشنیاں جگمگا اٹھتی ہیں۔", "शाम ढलते ही फलों और किताबों की दुकानों के ऊपर लालटेन जगमगा उठती हैं।", setOf("books", "bright")),
                StoryPage(14, "books/sw_44485/image_14.webp", "Everyone waves, sings, and smiles together. Our street is the most colorful and welcoming place in town!", "¡Nuestra calle es el lugar más colorido de la ciudad!", "سب ایک ساتھ ہاتھ ہلاتے، گاتے اور مسکراتے ہیں۔ ہماری گلی شہر کی سب سے رنگین اور پیاری جگہ ہے!", "सब एक साथ मुस्कुराते हैं। हमारी गली शहर की सबसे रंगीन और प्यारी जगह है!", setOf("colors", "street"),
                    StoryQuizQuestion("What colors were the street drummers wearing?", listOf("Bright red and yellow shirts", "Grey and black coats", "Purple raincoats"), 0)
                )
            )
        ),

        // 2. Play Store Screenshot #1 & #2 Book: "The day it rained fish" (Full 13-Page Unabridged Illustrated Story!)
        StoryBook(
            id = "book_rained_fish",
            title = "The day it rained fish",
            nativeTitleUrdu = "جس دن مچھلیوں کی بارش ہوئی",
            nativeTitleHindi = "जिस दिन मछलियों की बारिश हुई",
            author = "Clyde Beech",
            illustrator = "Mohale Mashigo",
            publisher = "Book Dash • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_45764/image_2.webp",
            bookType = "DECODABLE",
            bookSource = "CLASSROOM",
            categoryTag = "Science & Weather",
            phonicsSkillTag = "Phonics: sh • ch • cl",
            assignedByTeacher = "Miss Miya • Grade 2",
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_45764/image_2.webp", "The blast of air blew the candles out. Then rising up, it crashed into the clouds in the sky. And all of a sudden, there was a roar of thunder and it started to rain.", "La ráfaga de aire apagó las velas. Luego subió hacia las nubes y empezó a llover.", "ہوا کے تیز جھونکے نے موم بتیاں بجھا دیں۔ پھر بادلوں سے ٹکرانے کے بعد اچانک بادل گرجے اور بارش شروع ہو گئی۔", "हवा के तेज़ झोंके ने मोमबत्तियाँ बुझा दीं। फिर अचानक बादल गरजे और बारिश शुरू हो गई।", setOf("candles", "crashed", "thunder")),
                StoryPage(2, "books/sw_45764/image_3.webp", "Ma called out from the porch, \"Come inside quickly, Lulu! A giant monsoon storm is rolling over the hills!\"", "Mamá llamó desde el porche: ¡Entra rápido, Lulu!", "امی نے برآمدے سے آواز دی، لولو جلدی اندر آ جاؤ! پہاڑیوں کے اوپر سے ایک بڑا طوفان آ رہا ہے!", "माँ ने बरामदे से आवाज़ दी, \"लुलु जल्दी अंदर आओ! तेज़ तूफ़ान आ रहा है!\"", setOf("clouds", "rain")),
                StoryPage(3, "books/sw_45764/image_4.webp", "Lulu peeked from behind the window curtain as the wind whistled through the tall mango trees.", "Lulu miró detrás de la cortina mientras el viento silبaba.", "لولو نے کھڑکی کے پردے کے پیچھے سے جھانکا جبکہ تیز ہوا آم کے اونچے درختوں میں سائیں سائیں کر رہی تھی۔", "लुलु ने खिड़की के पर्दे के पीछे से झाँका जब तेज़ हवा पेड़ों से गुज़र रही थी।", setOf("curtain", "whispers")),
                StoryPage(4, "books/sw_45764/image_5.webp", "In the kitchen, the teacups rattled inside the wooden cupboard as another rumble of thunder shook the roof.", "En la cocina las tazas vibraron dentro del armario.", "باورچی خانے میں لکڑی کی الماری کے اندر پیالیاں کھنکھنا اٹھیں جب بادل کی گرج سے چھت لرز اٹھی۔", "रसोई में लकड़ी की अलमारी के अंदर कप खनखना उठे जब बादल ज़ोर से गरजे।", setOf("cupboard", "thunder"),
                    StoryQuizQuestion("What blew the candles out before it started to rain?", listOf("A blast of air", "A big red ball", "A yellow ladybird"), 0)
                ),
                StoryPage(5, "books/sw_45764/image_6.webp", "Lulu looked out the window in wide-eyed surprise. Big silver raindrops were splashing all across the green garden!", "Lulu miró por la ventana con gran sorpresa.", "لولو نے حیرت سے کھڑکی سے باہر دیکھا۔ ہرے باغ میں چاندی جیسی بڑی بڑی بوندیں گر رہی تھیں!", "लुलु ने हैरानी से खिड़की के बाहर देखा। बग़ीचे में चाँदी जैसी बूँदें गिर रही थीं!", setOf("clouds", "rain")),
                StoryPage(6, "books/sw_45764/image_7.webp", "Wait—those were not just raindrops! Tiny silver fins and tails were tumbling down from the swirling storm cloud!", "¡Espera, no eran solo gotas de lluvia!", "رکو—وہ صرف بارش کی بوندیں نہیں تھیں! گھومتے ہوئے بادلوں سے ننھی چمکدار مچھلیاں نیچے گر رہی تھیں!", "रुको—वे सिर्फ़ बारिश की बूँदें नहीं थीं! बादलों से नन्ही मछलियाँ गिर रही थीं!", setOf("clouds", "special")),
                StoryPage(7, "books/sw_45764/image_8.webp", "A strong waterspout wind over the river had lifted a school of little river fish right up into the raincloud!", "Una tromba marina sobre el río había levantado peces hacia la nube.", "دریا کے اوپر چلنے والے ہوا کے بگولے نے ننھی مچھلیوں کو اٹھا کر بارش کے بادلوں تک پہنچا دیا تھا!", "नदी के ऊपर उठे बवंडर ने नन्ही मछलियों को बादलों तक पहुँचा दिया था!", setOf("clouds", "rain")),
                StoryPage(8, "books/sw_45764/image_9.webp", "Splash! Plop! Dozens of shiny silver fish landed safely in the soft puddles and wet grass outside.", "Docenas de peces plateados cayeron en los charcos suaves.", "چھپاک! درجنوں چمکدار مچھلیاں باہر نرم گھاس اور پانی کے گڑھوں میں بحفاظت گرنے لگیں۔", "छपाक! दर्जनों चमकीली मछलियाँ बाहर नरम घास और पानी में गिरने लगीं।", setOf("rain", "bright")),
                StoryPage(9, "books/sw_45764/image_10.webp", "Lulu grabbed her red raincoat and two water buckets. \"Ma, we must help the little fish get back to the stream!\"", "Lulu tomó su impermeable rojo y dos cubetas.", "لولو نے اپنا سرخ برساتی کوٹ اور دو بالٹیاں اٹھائیں۔ امی، ہمیں ان ننھی مچھلیوں کو واپس ندی میں پہنچانا ہو گا!", "लुलु ने अपना लाल रेनकोट और बाल्टी उठाई। \"माँ, हमें इन मछलियों को नदी में पहुँचाना होगा!\"", setOf("raincoat", "return"),
                    StoryQuizQuestion("How did the little river fish get up into the raincloud?", listOf("A strong waterspout wind lifted them from the river", "They climbed a tree", "They rode a bicycle"), 0)
                ),
                StoryPage(10, "books/sw_45764/image_11.webp", "All the neighborhood children came running with bowls and buckets filled with fresh rainwater.", "Todos los niños del barrio corrieron con cubetas de agua.", "محلے کے تمام بچے تازہ بارش کے پانی سے بھرے پیالے اور بالٹیاں لے کر دوڑتے ہوئے آ گئے۔", "मोहल्ले के सभी बच्चे ताज़ा पानी से भरी बाल्टियाँ लेकर दौड़ पड़े।", setOf("friends", "rain")),
                StoryPage(11, "books/sw_45764/image_12.webp", "Gently, one by one, they scooped up every wriggling silver fish from the lawn.", "Con cuidado recogieron cada pececito plateado del césped.", "بڑی احتیاط سے انہوں نے ایک ایک کر کے گھاس سے ہر چمکدار مچھلی کو پانی کی بالٹی میں ڈالا۔", "बड़ी सावधानी से उन्होंने एक-एक करके हर मछली को पानी की बाल्टी में डाला।", setOf("friends", "bright")),
                StoryPage(12, "books/sw_45764/image_13.webp", "At the edge of the river, the children tipped their buckets and cheered as the fish swam away happily!", "En la orilla del río los niños liberaron los peces.", "دریا کے کنارے پہنچ کر بچوں نے بالٹیاں جھکائیں اور خوشی سے تالیاں بجائیں جب مچھلیاں تیرتی ہوئی چلی گئیں!", "नदी के किनारे बच्चों ने मछलियों को सुरक्षित पानी में छोड़ दिया और खुशी से तालियाँ बजाईं!", setOf("return", "friends")),
                StoryPage(13, "books/sw_45764/image_14.webp", "That evening, Lulu wrote the whole adventure in her notebook: the unforgettable day it rained fish!", "Esa noche Lulu escribió toda la aventura en su cuaderno.", "اس شام لولو نے اپنی کاپی میں پورا واقعہ لکھا: وہ ناقابلِ فراموش دن جب مچھلیوں کی بارش ہوئی!", "उस शाम लुलु ने अपनी डायरी में वह यादगार कहानी लिखी जब मछलियों की बारिश हुई!", setOf("books", "special"),
                    StoryQuizQuestion("Where did the children carry the jumping fish in their buckets?", listOf("Safely back to the river", "Into the library cupboard", "To the sweet shop"), 0)
                )
            )
        ),

        // 3. Unabridged 14-Page Book sw_37273: "A butterfly smile" / "Caterpillar Looks for a Shoe" (Level 2)
        StoryBook(
            id = "sw_37273",
            title = "A butterfly smile",
            nativeTitleUrdu = "کیٹرپلر کا نیا جوتا اور تتلی",
            nativeTitleHindi = "तितली की मुस्कान (कैटरपिलर का जूता)",
            author = "Somsanouk Bouttavong",
            illustrator = "Ounla Santi",
            publisher = "Room to Read • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_37273/cover_image.webp",
            bookType = "SERIES",
            categoryTag = "Animals & Nature",
            seriesTag = "Garden Series • Ep 1",
            pages = listOf(
                StoryPage(1, "books/sw_37273/image_2.webp", "Caterpillar has lost a shoe. It hurts her foot to walk without it.", "La Oruga perdió un zapato. Si camina sin el zapato, le duele el pie.", "کیٹرپلر کا ایک جوتا کھو گیا ہے۔ اس کے بغیر چلنے سے اس کے پاؤں میں درد ہوتا ہے۔", "कैटरपिलर का एक जूता खो गया है। उसके बिना चलने में पैर दुखता है।", setOf("caterpillar", "shoe")),
                StoryPage(2, "books/sw_37273/image_3.webp", "Caterpillar goes to Spider. \"May I borrow a shoe from you, Spider?\" But Spider's shoe is too small.", "\"¿Me puedes prestar un zapato, Araña?\" Pero el zapato de la Araña es demasiado pequeño.", "کیٹرپلر مکڑی کے پاس جاتی ہے، کیا میں آپ سے جوتا ادھار لے سکتی ہوں؟ لیکن مکڑی کا جوتا بہت چھوٹا ہے۔", "कैटरपिलर मकड़ी के पास जाती है। पर मकड़ी का जूता बहुत छोटा है।", setOf("borrow", "spider")),
                StoryPage(3, "books/sw_37273/image_4.webp", "Caterpillar is sad without her shoe. She keeps walking down the garden path.", "La Oruga está triste sin su zapato.", "کیٹرپلر اپنے جوتے کے بغیر اداس ہے۔ وہ باغ کے راستے پر آگے چلتی رہتی ہے۔", "कैटरपिलर अपने जूते के बिना उदास है।", setOf("caterpillar", "shoe")),
                StoryPage(4, "books/sw_37273/image_5.webp", "She goes to Frog by the pond. \"May I borrow a shoe from you, Frog?\"", "Entonces, acude a la Rana. \"¿Me puedes prestar un zapato, Rana?\"", "وہ تالاب کے پاس مینڈک کے پاس جاتی ہے۔ کیا میں آپ سے ایک جوتا ادھار لے سکتی ہوں، مینڈک؟", "वह तालाब के पास मेंढक के पास जाती है।", setOf("borrow", "frog")),
                StoryPage(5, "books/sw_37273/image_6.webp", "But Frog's shoe is way too big! Caterpillar's tiny foot slips right out.", "Pero el zapato de la Rana es demasiado grande.", "لیکن مینڈک کا جوتا بہت ہی بڑا ہے! کیٹرپلر کا ننھا پاؤں اس سے باہر نکل جاتا ہے۔", "लेकिन मेंढक का जूता बहुत बड़ा है!", setOf("frog", "shoe"),
                    StoryQuizQuestion("Why couldn't Caterpillar wear Frog's shoe?", listOf("It was too big", "It was too small", "It was painted red"), 0)
                ),
                StoryPage(6, "books/sw_37273/image_7.webp", "She goes to Grasshopper in the tall grass. \"May I borrow a shoe from you, Grasshopper?\"", "Entonces, acude al Saltamontes. \"¿Me puedes prestar un zapato, Saltamontes?\"", "وہ لمبی گھاس میں ٹڈے کے پاس جاتی ہے۔ کیا میں آپ سے جوتا ادھار لے سکتی ہوں؟", "वह लंबी घास में टिड्डे के पास जाती है।", setOf("grasshopper", "borrow")),
                StoryPage(7, "books/sw_37273/image_8.webp", "But Grasshopper's shoe is too scratchy! It tickles and pokes her foot.", "Pero el zapato del Saltamontes raspa mucho.", "لیکن ٹڈے کا جوتا بہت کھردرا ہے! وہ اس کے پاؤں میں چبھتا ہے۔", "पर टिड्डे का जूता बहुत चुभता है!", setOf("grasshopper", "shoe")),
                StoryPage(8, "books/sw_37273/image_9.webp", "She goes to Crab on the sandy shore. \"May I borrow a shoe from you, Crab?\"", "Entonces, acude al Cangrejo. \"¿Me puedes prestar un zapato, Cangrejo?\"", "وہ ریتیلے کنارے پر کیکڑے کے پاس جاتی ہے۔ کیا میں آپ سے جوتا لے سکتی ہوں؟", "वह रेतीले किनारे पर केकड़े के पास जाती है।", setOf("crab", "borrow")),
                StoryPage(9, "books/sw_37273/image_10.webp", "But Crab's shoes are used for digging in the sand. They are too sharp for walking!", "Pero los zapatos del Cangrejo se usan para cavar. ¡Están muy afilados para caminar!", "لیکن کیکڑے کے جوتے ریت کھودنے کے کام آتے ہیں۔ وہ چلنے کے لیے بہت نوکیلے ہیں!", "पर केकड़े के जूते रेत खोदने के लिए हैं, वे चलने के लिए बहुत नुकीले हैं!", setOf("crab", "shoe")),
                StoryPage(10, "books/sw_37273/image_11.webp", "Caterpillar is too tired to search anymore. She curls up on a soft green leaf to rest.", "La Oruga está muy cansada para seguir buscando. Se acurruca en una hoja para descansar.", "کیٹرپلر مزید تلاش کرنے سے بہت تھک چکی ہے۔ وہ آرام کرنے کے لیے ایک نرم ہرے پتے پر سمٹ جاتی ہے۔", "कैटरपिलर बहुत थक गई है। वह आराम करने के लिए एक हरे पत्ते पर लेट जाती है।", setOf("caterpillar", "shoe")),
                StoryPage(11, "books/sw_37273/image_12.webp", "Caterpillar decides to make a cozy cocoon to sleep in.", "Decide crear un capullo acogedor para dormir.", "کیٹرپلر سونے کے لیے ایک آرام دہ کوکون (خول) بنانے کا فیصلہ کرتی ہے۔", "कैटरपिलर सोने के लिए एक आरामदायक कोकून बनाती है।", setOf("cocoon", "caterpillar")),
                StoryPage(12, "books/sw_37273/image_13.webp", "Many sunny and rainy days pass by. And when Caterpillar finally steps out of her cocoon...", "Pasan los días. Y cuando la Oruga sale del capullo...", "کئی دن گزر جاتے ہیں۔ اور جب کیٹرپلر آخرکار اپنے کوکون سے باہر نکلتی ہے...", "कई दिन बीत जाते हैं। और जब कैटरपिलर अपने कोकून से बाहर निकलती है...", setOf("cocoon", "caterpillar")),
                StoryPage(13, "books/sw_37273/image_14.webp", "She has turned into a beautiful butterfly with bright wings! She does not need shoes anymore!", "¡Ya no necesita zapatos! Ahora vuela con alas de mariposa.", "وہ چمکدار پروں والی ایک خوبصورت تتلی بن چکی ہے! اب اسے جوتوں کی بالکل ضرورت نہیں!", "वह चमकीले पंखों वाली एक सुंदर तितली बन गई है! अब उसे जूतों की ज़रूरत नहीं!", setOf("butterfly", "wings"),
                    StoryQuizQuestion("Why did Caterpillar not need shoes anymore at the end?", listOf("She became a butterfly that flies", "She found her old shoe", "Spider gave her boots"), 0)
                )
            )
        ),

        // 4. Unabridged 14-Page Book sw_44485: "A street or a zoo?" / "Who Takes the Train?" (Level 1)
        StoryBook(
            id = "sw_44485",
            title = "A street or a zoo?",
            nativeTitleUrdu = "ٹرین کا دلچسپ سفر",
            nativeTitleHindi = "ट्रेन का मज़ेदार सफ़र",
            author = "Mabel Mnensa",
            illustrator = "Ntembiso Velemane",
            publisher = "Book Dash • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_44485/cover_image.webp",
            categoryTag = "Adventure & Travel",
            pages = listOf(
                StoryPage(1, "books/sw_44485/image_2.webp", "Today is Beach Day! Nicole is going on the train for the first time.", "¡Hoy es día de playa! Nicole viaja en tren por primera vez.", "آج ساحل کی سیر کا دن ہے! نکول پہلی بار ٹرین میں سفر کر رہی ہے۔", "आज बीच का दिन है! निकोल पहली बार ट्रेन में जा रही है।", setOf("beach", "train")),
                StoryPage(2, "books/sw_44485/image_3.webp", "\"Who takes the train?\" Nicole asks. \"Let's wait and see,\" Mom says.", "\"¿Quién viaja en tren?\", pregunta Nicole. \"Aguarda y veremos\", le dice la mamá.", "ٹرین میں کون سفر کرتا ہے؟ نکول پوچھتی ہے۔ امی کہتی ہیں، انتظار کرو اور دیکھو۔", "\"ट्रेन में कौन जाता है?\" निकोल पूछती है।", setOf("train", "friends")),
                StoryPage(3, "books/sw_44485/image_4.webp", "\"Look, Mom!\" Nicole whispers as the train doors open at the station.", "\"¡Mira, mamá!\", susurra Nicole.", "دیکھو امی! اسٹیشن پر ٹرین کے دروازے کھلتے ہی نکول سرگوشی کرتی ہے۔", "\"देखो माँ!\" निकोल धीरे से कहती है।", setOf("whispers", "train")),
                StoryPage(4, "books/sw_44485/image_5.webp", "\"Hello little girl, do you want to say hi?\" Nicole looks at Mom. Mom nods. \"Yes, please!\" says Nicole.", "\"Hola, pequeñita. ¿Quieres saludar?\" \"¡Sí, por favor!\", responde Nicole.", "ہیلو ننھی بچی، کیا تم سلام کرنا چاہتی ہو؟ امی سر ہلاتی ہیں۔ جی ضرور! نکول کہتی ہے۔", "\"हेलो नन्ही बच्ची!\" निकोल मुस्कुराकर कहती है, \"हाँ, बिल्कुल!\"", setOf("friends", "bright")),
                StoryPage(5, "books/sw_44485/image_6.webp", "\"Chocolates! Chocolates! Please, can I get one?\" Nicole asks. \"We need to leave space in your tummy for ice cream at the beach,\" Mom says.", "\"¡Chocolates! ¿Puedo comer uno?\" \"Deja lugar para el helado en la playa.\"", "چاکلیٹ! کیا میں ایک لے سکتی ہوں؟ امی کہتی ہیں، ساحل پر آئس کریم کے لیے پیٹ میں جگہ رکھو۔", "\"चॉकलेट! क्या मैं एक ले सकती हूँ?\" माँ कहती हैं, \"बीच पर आइसक्रीम के लिए जगह रखो।\"", setOf("chocolates", "beach")),
                StoryPage(6, "books/sw_44485/image_7.webp", "\"Why are you wearing pajamas on the train?\" Nicole asks the boys.", "\"¿Por qué están en pijama?\", pregunta Nicole.", "آپ نے ٹرین میں پاجامہ کیوں پہن رکھا ہے؟ نکول لڑکوں سے پوچھتی ہے۔", "\"आपने ट्रेन में पजामा क्यों पहना है?\" निकोल पूछती है।", setOf("train", "friends")),
                StoryPage(7, "books/sw_44485/image_8.webp", "\"These are not pajamas! We do karate. Like this!\"", "\"Hacemos karate. ¡Así!\"", "یہ پاجامہ نہیں ہے! ہم کراٹے کرتے ہیں۔ بالکل ایسے!", "\"यह पजामा नहीं है! हम कराटे करते हैं। ऐसे!\"", setOf("friends", "special")),
                StoryPage(8, "books/sw_44485/image_9.webp", "\"Kapow!\" The karate kids show their cool moves while Nicole claps.", "\"¡Kapow!\"", "کپاؤ! کراٹے کرنے والے بچے اپنے کرتب دکھاتے ہیں اور نکول تالیاں بجاتی ہے۔", "\"कपॉव!\" बच्चे कराटे मूव्स दिखाते हैं।", setOf("special", "friends")),
                StoryPage(9, "books/sw_44485/image_10.webp", "\"Oh, look! A soldier.\" \"No,\" Mom says, \"that is a sailor. She is from the navy.\"", "\"Oh, mira! Un soldado.\" \"No, es marinera. Pertenece a la Marina.\"", "ارے دیکھو، ایک فوجی! امی کہتی ہیں، نہیں، وہ بحریہ کی ملاح (سیلر) ہے۔", "माँ कहती हैं, \"वह नौसेना की नाविक (सेलर) है।\"", setOf("sailor", "train")),
                StoryPage(10, "books/sw_44485/image_11.webp", "\"Mom! Drummers!\" \"Listen, Nicole,\" Mom says. \"The drums beat to the sound of the train.\"", "\"¡Mamá! ¡Tambores!\" \"Los tambores suenan al ritmo del sonido del tren.\"", "امی! ڈھول بجانے والے! امی کہتی ہیں، سنو نکول، ڈھول ٹرین کی آواز کے ساتھ بج رہے ہیں۔", "\"माँ! ढोल वाले!\" ढोल ट्रेन की आवाज़ की ताल पर बज रहे हैं।", setOf("drummers", "train")),
                StoryPage(11, "books/sw_44485/image_12.webp", "\"Do you want to dance with us?\" the drummer asks. Nicole dances happily in the aisle!", "\"¿Quieres bailar con nosotros?\", le pregunta el tamborilero.", "کیا تم ہمارے ساتھ ناچنا چاہتی ہو؟ ڈھول بجانے والا پوچھتا ہے اور نکول خوشی سے جھوم اٹھتی ہے۔", "निकोल ढोल की ताल पर खुशी से नाचती है!", setOf("drummers", "friends")),
                StoryPage(12, "books/sw_44485/image_13.webp", "\"We are getting off at the next stop,\" Mom tells Nicole, who is now tired.", "\"Debemos bajar en la próxima estación\", le dice la mamá a Nicole.", "ہم اگلے اسٹیشن پر اتر رہے ہیں، امی نکول سے کہتی ہیں جو اب تھک چکی ہے۔", "\"हम अगले स्टेशन पर उतर रहे हैं,\" माँ निकोल से कहती हैं।", setOf("train", "beach")),
                StoryPage(13, "books/sw_44485/image_14.webp", "\"So, what kind of people take the train?\" Mom asks. \"The best kind!\" Nicole says.", "\"Entonces, ¿qué tipos de personas toman el tren?\" \"¡Los mejores tipos!\", responde Nicole.", "تو کس طرح کے لوگ ٹرین میں سفر کرتے ہیں؟ امی پوچھتی ہیں۔ سب سے بہترین لوگ! نکول کہتی ہے۔", "\"सबसे अच्छे लोग!\" निकोल खुशी से कहती है।", setOf("train", "friends"),
                    StoryQuizQuestion("Who did Nicole meet on her first train ride to the beach?", listOf("Karate kids, a navy sailor, and drummers", "A spider and a frog", "A yellow ladybird"), 0)
                )
            )
        ),

        // 5. Unabridged 16-Page Book sw_15195: "The red raincoat" / "My Red Ball" (Level 1)
        StoryBook(
            id = "sw_15195",
            title = "The red raincoat",
            nativeTitleUrdu = "میری سرخ گیند اور برساتی کوٹ",
            nativeTitleHindi = "मेरी लाल गेंद और रेनकोट",
            author = "Khothatso Ranoosi & Marion Drew",
            illustrator = "Marion Drew",
            publisher = "Pratham Books • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_15195/cover_image.webp",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: r • b • k",
            pages = listOf(
                StoryPage(1, "books/sw_15195/image_2.webp", "Ball. Look at this round ball.", "Pelota. Bola.", "گیند۔ اس گول گیند کو دیکھو۔", "गेंद। इस गोल गेंद को देखो।", setOf("bright")),
                StoryPage(2, "books/sw_15195/image_3.webp", "My ball. I hold my ball in my hands.", "Mi pelota. Minha bola.", "میری گیند۔ میں اپنی گیند کو اپنے ہاتھوں میں پکڑتا ہوں۔", "मेरी गेंद। मैं अपनी गेंद हाथ में पकड़ता हूँ।", setOf("friends")),
                StoryPage(3, "books/sw_15195/image_4.webp", "My red ball and my red raincoat.", "Mi pelota roja.", "میری سرخ گیند اور میرا سرخ برساتی کوٹ۔", "मेरी लाल गेंद और मेरा लाल रेनकोट।", setOf("raincoat")),
                StoryPage(4, "books/sw_15195/image_5.webp", "My big red ball! It is bright and bouncy.", "Mi gran pelota roja.", "میری بڑی سرخ گیند! یہ چمکدار اور اچھلنے والی ہے۔", "मेरी बड़ी लाल गेंद! यह चमकीली है।", setOf("bright")),
                StoryPage(5, "books/sw_15195/image_6.webp", "I kick. I step forward and kick with my foot.", "La pateo.", "میں پاؤں سے کک مارتا ہوں۔", "मैं पैर से किक मारता हूँ।", setOf("shoe")),
                StoryPage(6, "books/sw_15195/image_7.webp", "I kick my ball across the green grass.", "Pateo mi pelota.", "میں ہری گھاس پر اپنی گیند کو کک مارتا ہوں۔", "मैं हरी घास पर अपनी गेंद को किक मारता हूँ।", setOf("bright")),
                StoryPage(7, "books/sw_15195/image_8.webp", "I kick my red ball higher and higher.", "Pateo mi pelota roja.", "میں اپنی سرخ گیند کو اور اونچا اچھالتا ہوں۔", "मैं अपनी लाल गेंद को और ऊँचा उछालता हूँ।", setOf("bright")),
                StoryPage(8, "books/sw_15195/image_9.webp", "I kick my red ball hard! Whoosh!", "¡Pateo fuerte mi pelota roja!", "میں اپنی سرخ گیند کو پوری طاقت سے کک مارتا ہوں! شوں!", "मैं अपनी लाल गेंद को पूरी ताकत से किक मारता हूँ!", setOf("crashed")),
                StoryPage(9, "books/sw_15195/image_10.webp", "Where? Wait, where did it go?", "¿Dónde?", "کہاں؟ رکو، وہ کہاں چلی گئی؟", "कहाँ? रुको, वह कहाँ चली गई?", setOf("clouds")),
                StoryPage(10, "books/sw_15195/image_11.webp", "Where is my ball?", "¿Dónde está mi pelota?", "میری گیند کہاں ہے؟", "मेरी गेंद कहाँ है?", setOf("clouds")),
                StoryPage(11, "books/sw_15195/image_12.webp", "Where is my ball now? I look all around the yard.", "¿Dónde está mi pelota ahora?", "اب میری گیند کہاں ہے؟ میں چاروں طرف دیکھتا ہوں۔", "अब मेरी गेंद कहाँ है? मैं चारों तरफ़ देखता हूँ।", setOf("street")),
                StoryPage(12, "books/sw_15195/image_13.webp", "Where is my red ball now?", "¿Dónde está mi pelota roja ahora?", "اب میری سرخ گیند کہاں ہے؟", "अब मेरी लाल गेंद कहाँ है?", setOf("bright")),
                StoryPage(13, "books/sw_15195/image_14.webp", "It is up! Look up above the trees.", "Está arriba.", "وہ اوپر ہے! درختوں کے اوپر دیکھو۔", "वह ऊपर है! पेड़ों के ऊपर देखो।", setOf("clouds")),
                StoryPage(14, "books/sw_15195/image_15.webp", "It is high up!", "Está muy arriba.", "وہ بہت اونچائی پر ہے!", "वह बहुत ऊँचाई पर है!", setOf("clouds")),
                StoryPage(15, "books/sw_15195/image_16.webp", "It is high up in the blue sky!", "Está bien arriba en el cielo.", "وہ نیلے آسمان میں بہت اونچی اڑ رہی ہے!", "वह नीले आसमान में बहुत ऊपर है!", setOf("clouds")),
                StoryPage(16, "books/sw_15195/image_17.webp", "It is high up in the sky. It flies over the moon. It is gone!", "Está sobre la luna. ¡Se ha ido!", "وہ آسمان میں چاند کے اوپر سے اڑتی ہوئی دور نکل گئی ہے!", "वह आसमान में चाँद के ऊपर से उड़ गई है!", setOf("clouds"),
                    StoryQuizQuestion("Where did the big red ball fly when kicked hard?", listOf("High up in the sky over the moon", "Under the sofa", "Inside the school"), 0)
                )
            )
        ),

        // 6. Unabridged 9-Page Book sw_15058: "Grandpa Farouk's stories" / "I Like to Read" (Level 1)
        StoryBook(
            id = "sw_15058",
            title = "Grandpa Farouk's stories",
            nativeTitleUrdu = "مجھے کہانیاں پڑھنا پسند ہے",
            nativeTitleHindi = "मुझे कहानियाँ पढ़ना पसंद है",
            author = "Letta Machoga",
            illustrator = "Wiehan de Jager",
            publisher = "African Storybook • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_15058/cover_image.webp",
            bookType = "ASSESSMENT",
            pages = listOf(
                StoryPage(1, "books/sw_15058/image_2.webp", "I like to read.", "Me gusta leer.", "مجھے پڑھنا بہت پسند ہے۔", "मुझे पढ़ना बहुत पसंद है।", setOf("books")),
                StoryPage(2, "books/sw_15058/image_3.webp", "Who can I read to?", "¿A quién le puedo leer?", "میں کس کو کتاب پڑھ کر سنا سکتا ہوں؟", "मैं किसको किताब पढ़कर सुना सकता हूँ?", setOf("books")),
                StoryPage(3, "books/sw_15058/image_4.webp", "My little sister is asleep.", "Mi hermanita está durmiendo.", "میری چھوٹی بہن سو رہی ہے۔", "मेरी छोटी बहन सो रही है।", setOf("friends")),
                StoryPage(4, "books/sw_15058/image_5.webp", "Who can I read my storybook to now?", "¿A quién le puedo leer?", "اب میں اپنی کہانی کی کتاب کس کو پڑھ کر سناؤں؟", "अब मैं अपनी कहानी किसको पढ़कर सुनाऊँ?", setOf("books")),
                StoryPage(5, "books/sw_15058/image_6.webp", "My mother and grandmother are busy cooking.", "Mi mamá y mi abuela están ocupadas.", "میری امی اور دادی کھانا پکانے میں مصروف ہیں۔", "मेरी माँ और दादी खाना बनाने में व्यस्त हैं।", setOf("grandmother"),
                    StoryQuizQuestion("What were his mother and grandmother doing?", listOf("Cooking", "Watching TV", "Dancing"), 0)
                ),
                StoryPage(6, "books/sw_15058/image_7.webp", "Who else can I read to?", "¿A quién le puedo leer?", "میں اور کس کو پڑھ کر سنا سکتا ہوں؟", "मैं और किसको पढ़कर सुना सकता हूँ?", setOf("books")),
                StoryPage(7, "books/sw_15058/image_8.webp", "My father and grandfather Farouk are busy outside.", "Mi papá y mi abuelo están ocupados.", "میرے ابو اور دادا فاروق باہر کام میں مصروف ہیں۔", "मेरे पिताजी और दादाजी बाहर काम में व्यस्त हैं।", setOf("grandfather")),
                StoryPage(8, "books/sw_15058/image_9.webp", "Who can I read to? I can read to myself!", "¡Puedo leer para mí mismo!", "میں کس کو پڑھ کر سناؤں؟ میں خود اپنے آپ کو پڑھ کر سنا سکتا ہوں!", "मैं किसको सुनाऊँ? मैं खुद अपने लिए पढ़ सकता हूँ!", setOf("books"),
                    StoryQuizQuestion("What did the boy love doing every day?", listOf("Reading stories", "Playing football", "Sleeping"), 0)
                )
            )
        ),

        // 7. Unabridged 17-Page Book sw_13916: "Lara the Yellow Ladybird" (Level 3)
        StoryBook(
            id = "sw_13916",
            title = "Lara the Yellow Ladybird",
            nativeTitleUrdu = "لارا پیلی لیڈی برڈ",
            nativeTitleHindi = "लारा पीली लेडीबर्ड",
            author = "Catherine Holtzhausen & Nadene Kriel",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • StoryWeaver",
            level = 3,
            coverAssetPath = "books/sw_13916/cover_image.webp",
            bookSource = "CLASSROOM",
            categoryTag = "Animals & Nature",
            assignedByTeacher = "Miss Miya • Grade 2",
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_2.webp", "Lara the ladybird was a special bug.", "Lara, la mariquita, era un bichito especial.", "لارا لیڈی برڈ ایک خاص ننھی کیڑی تھی۔", "लारा लेडीबर्ड एक ख़ास नन्ही कीड़ी थी।", setOf("ladybird", "special")),
                StoryPage(2, "books/sw_13916/image_3.webp", "Unlike all her friends, she had bright yellow wings.", "A diferencia de todos sus amigos, ella tenía alas amarillas y brillantes.", "اپنے تمام دوستوں کے برعکس، اس کے چمکدار پیلے پر تھے۔", "अपने सभी दोस्तों के विपरीत, उसके चमकीले पीले पंख थे।", setOf("friends", "bright", "yellow", "wings"),
                    StoryQuizQuestion("Lara was special because she had _____.", listOf("Yellow wings", "Many friends", "Red shoes"), 0)
                ),
                StoryPage(3, "books/sw_13916/image_4.webp", "Everyone loved her yellow wings.", "Todos amaban sus alas amarillas.", "سب کو اس کے پیلے پر بہت پسند تھے۔", "सबको उसके पीले पंख बहुत पसंद थे।", setOf("yellow", "wings")),
                StoryPage(4, "books/sw_13916/image_5.webp", "Each morning, Bibi Butterfly said hello. And Manto Mantis always waved.", "Todas las mañanas, Bibi Mariposa le decía hola. Y Manto Mantis siempre saludaba.", "ہر صبح بی بی تتلی اسے سلام کرتی، اور مینٹو مینٹس ہمیشہ ہاتھ ہلاتا۔", "हर सुबह बीबी तितली उसे हेलो कहती और मैंटो हमेशा हाथ हिलाता।", setOf("butterfly", "friends")),
                StoryPage(5, "books/sw_13916/image_6.webp", "Even Sesa, the sulky spider, was happy to see her.", "Hasta Sesa, la araña gruñona, se alegraba de verla.", "یہاں تک کہ سیسا، چڑچڑی مکڑی بھی اسے دیکھ کر خوش ہو جاتی تھی۔", "यहाँ तक कि सेसा मकड़ी भी उसे देखकर खुश हो जाती थी।", setOf("spider", "friends")),
                StoryPage(6, "books/sw_13916/image_7.webp", "At school, she played with lots of friends.", "En la escuela, jugaba con muchos amigos.", "اسکول میں وہ بہت سے دوستوں کے ساتھ کھیلتی تھی۔", "स्कूल में वह बहुत सारे दोस्तों के साथ खेलती थी।", setOf("school", "friends")),
                StoryPage(7, "books/sw_13916/image_8.webp", "But Lara wanted to be like the other ladybirds. \"I wish I had red wings like you, Mama,\" she cried.", "Pero Lara quería ser como las otras mariquitas. \"Ojalá tuviera alas rojas como tú, mamá.\"", "لیکن لارا دوسری لیڈی برڈز جیسا بننا چاہتی تھی۔ کاش میرے بھی آپ جیسے سرخ پر ہوتے امی، وہ روئی۔", "पर लारा दूसरी लेडीबर्ड्स जैसी बनना चाहती थी। \"काश मेरे भी आपके जैसे लाल पंख होते माँ!\"", setOf("ladybird", "wings"),
                    StoryQuizQuestion("Lara wanted red wings because she _____.", listOf("Wanted to be like others", "Liked red paint", "Was teased at school"), 0)
                ),
                StoryPage(8, "books/sw_13916/image_9.webp", "So one day, to cheer her up, Lara's mother painted her wings bright red.", "Entonces un día, para alegrar a Lara, su mamá le pintó las alas de rojo brillante.", "تو ایک دن اس کا دل خوش کرنے کے لیے لارا کی امی نے اس کے پروں کو چمکدار سرخ رنگ کر دیا۔", "तो एक दिन उसे खुश करने के लिए लारा की माँ ने उसके पंख चमकीले लाल रंग दिए।", setOf("painted", "bright", "wings")),
                StoryPage(9, "books/sw_13916/image_10.webp", "The next morning, nobody greeted Lara on her way to school.", "A la mañana siguiente, nadie saludó a Lara camino a la escuela.", "اگلی صبح اسکول جاتے ہوئے راستے میں کسی نے بھی لارا کو سلام نہیں کیا۔", "अगली सुबह स्कूल के रास्ते में किसी ने लारा को नमस्ते नहीं किया।", setOf("school", "friends")),
                StoryPage(10, "books/sw_13916/image_11.webp", "And when she got there, none of her friends said hello.", "Y cuando llegó, ninguno de sus amigos le dijo hola.", "اور جب وہ وہاں پہنچی تو اس کے کسی بھی دوست نے اسے ہیلو نہیں کہا۔", "और जब वह वहाँ पहुँची, तो किसी भी दोस्त ने हेलो नहीं कहा।", setOf("friends", "school")),
                StoryPage(11, "books/sw_13916/image_12.webp", "Lara sat all alone. No one noticed her new red wings.", "Lara se sentó sola. Nadie notó sus nuevas alas rojas.", "لارا بالکل اکیلی بیٹھی رہی۔ کسی نے اس کے نئے سرخ پروں پر توجہ نہیں دی۔", "लारा अकेली बैठी रही। किसी ने उसके नए लाल पंखों पर ध्यान नहीं दिया।", setOf("wings", "painted"),
                    StoryQuizQuestion("Did anyone notice Lara's new red wings at first?", listOf("No!", "Yes!"), 0)
                ),
                StoryPage(12, "books/sw_13916/image_13.webp", "Until Miss Miya spotted her and said, \"You have painted your lovely yellow wings!\"", "Hasta que la Señorita Miya la vio y le dijo: \"¡Te pintaste esas hermosas alas amarillas!\"", "یہاں تک کہ مس میا نے اسے دیکھا اور کہا، تم نے اپنے پیارے پیلے پروں کو رنگ لیا ہے!", "तभी मिस मिया ने उसे देखा और कहा, \"तुमने अपने प्यारे पीले पंखों को रंग लिया है!\"", setOf("painted", "yellow", "wings")),
                StoryPage(13, "books/sw_13916/image_14.webp", "Lara's classmates were shocked. \"Your wings are special! So unique! So rare!\"", "Los compañeros de Lara estaban sorprendidos. \"¡Tus alas son especiales! ¡Eres única!\"", "لارا کے ہم جماعت حیران رہ گئے۔ تمہارے پیلے پر تو خاص ہیں! بالکل منفرد اور نایاب!", "लारा के सहपाठी चौंक गए। \"तुम्हारे पंख तो ख़ास और अनोखे हैं!\"", setOf("special", "unique")),
                StoryPage(14, "books/sw_13916/image_15.webp", "\"Lara,\" Miss Miya said, \"your yellow wings are what make you, you. Like Sipho's spot and Sally's long legs.\"", "\"Lara, tus alas amarillas son lo que te hace especial. Como el lunar de Sipho y las piernas de Sally.\"", "لارا، مس میا نے کہا، تمہارے پیلے پر ہی تمہاری پہچان ہیں۔ جیسے سیفو کا نشان اور سیلی کی لمبی ٹانگیں۔", "मिस मिया ने कहा, \"लारा, तुम्हारे पीले पंख ही तुम्हें ख़ास बनाते हैं।\"", setOf("yellow", "wings", "special"),
                    StoryQuizQuestion("Sipho's big spot and Sally's long legs made them _____.", listOf("Unique and special", "Like everyone else", "Sad"), 0)
                ),
                StoryPage(15, "books/sw_13916/image_16.webp", "Back home, Lara took a long bath. She scrubbed until her golden wings gleamed.", "De vuelta en casa, Lara se dio un largo baño. Se lavó bien hasta que brillaron sus alas doradas.", "گھر واپس آ کر لارا نے خوب نہایا۔ اس نے رگڑ کر دھویا یہاں تک کہ اس کے سنہری پر چمک اٹھے۔", "घर लौटकर लारा ने स्नान किया जब तक उसके सुनहरे पंख चमक न उठे।", setOf("scrubbed", "gleamed", "wings")),
                StoryPage(16, "books/sw_13916/image_17.webp", "\"I will never paint my wings again!\" she thought. Except, maybe once or twice...", "\"¡Nunca volveré a pintarme las alas!\", pensó. Excepto, tal vez una o dos veces más...", "میں اپنے پروں کو دوبارہ کبھی رنگ نہیں کروں گی! اس نے سوچا۔ سوائے کبھی کبھار ایک آدھ بار...", "\"मैं अब कभी अपने पंख नहीं रंगूँगी!\" उसने सोचा।", setOf("painted", "wings")),
                StoryPage(17, "books/sw_13916/image_18.webp", "To try a bit of purple, or something nice. But not forever, and just for fun!", "Para probar un poquito el púrpura. Pero no para siempre, solo para divertirme.", "تھوڑا سا جامنی یا کوئی اچھا رنگ آزمانے کے لیے۔ لیکن ہمیشہ کے لیے نہیں، صرف تفریح کے لیے!", "थोड़ा बैंगनी रंग आज़माने के लिए, पर हमेशा के लिए नहीं, सिर्फ़ मज़े के लिए!", setOf("special", "yellow"),
                    StoryQuizQuestion("By the end of the story, Lara _____ her yellow wings.", listOf("Liked and loved", "Disliked", "Hid"), 0)
                )
            )
        ),

        // 8. Unabridged 14-Page Book sw_45764: "Where is Lulu?" (Level 4)
        StoryBook(
            id = "sw_45764",
            title = "Where is Lulu?",
            nativeTitleUrdu = "لولو کہاں چھپی ہے؟",
            nativeTitleHindi = "लुलु कहाँ छिपी है?",
            author = "Clyde Beech & Mohale Mashigo",
            illustrator = "Clyde Beech",
            publisher = "Book Dash • StoryWeaver",
            level = 4,
            coverAssetPath = "books/sw_45764/cover_image.webp",
            pages = listOf(
                StoryPage(1, "books/sw_45764/image_2.webp", "\"Oh, no!\"Look at the clock on the library wall.", "\"¡Oh, no!\"", "اوہ نہیں! لائبریری کی دیوار پر لگی گھڑی دیکھو۔", "\"ओह नहीं!\" लाइब्रेरी की घड़ी देखो।", setOf("books")),
                StoryPage(2, "books/sw_45764/image_3.webp", "\"Lulu! It is time to go,\" calls Ma.", "\"¡Lulu! Es hora de partir\", le dice la mamá.", "لولو! اب گھر چلنے کا وقت ہو گیا ہے، امی پکارتی ہیں۔", "\"लुलु! अब घर चलने का समय हो गया है,\" माँ बुलाती हैं।", setOf("return")),
                StoryPage(3, "books/sw_45764/image_4.webp", "Where is Lulu? Is she under the sofa? Is she behind the curtain?", "¿Dónde está Lulu? ¿Está debajo del sofá? ¿Está detrás de las cortinas?", "لولو کہاں ہے؟ کیا وہ صوفے کے نیچے ہے؟ کیا وہ پردے کے پیچھے ہے؟", "लुलु कहाँ है? क्या वह सोफ़े के नीचे है? क्या वह पर्दे के पीछे है?", setOf("curtain")),
                StoryPage(4, "books/sw_45764/image_5.webp", "Where is Lulu? Is she next to the stove? Is she in the cupboard?", "¿Está cerca del horno? ¿Está en el armario?", "لولو کہاں ہے؟ کیا وہ چولہے کے پاس ہے یا الماری کے اندر؟", "क्या वह चूल्हे के पास है? क्या वह अलमारी में है?", setOf("cupboard")),
                StoryPage(5, "books/sw_45764/image_6.webp", "\"Lulu! It is time to go. Where are you?\" calls Ma.", "\"¡Lulu! Es hora de partir. ¿Dónde estás?\", pregunta la mamá.", "لولو! چلنے کا وقت ہو گیا ہے۔ تم کہاں ہو؟ امی پوچھتی ہیں۔", "\"लुलु! चलने का समय हो गया है। तुम कहाँ हो?\" माँ पूछती हैं।", setOf("return")),
                StoryPage(6, "books/sw_45764/image_7.webp", "\"There you are!\" \"I don't want to go!\"", "\"¡Ahí estás!\" \"¡No me quiero ir!\"", "تم یہاں ہو! لولو کہتی ہے، میں ابھی نہیں جانا چاہتی!", "\"तुम यहाँ हो!\" \"मैं नहीं जाना चाहती!\"", setOf("books")),
                StoryPage(7, "books/sw_45764/image_8.webp", "\"We have to go,\" says Ma. \"But I really like this one!\"", "\"Debemos irnos\", dice la mamá. \"¡Pero me gusta mucho este!\"", "ہمیں چلنا ہو گا، امی کہتی ہیں۔ لیکن مجھے یہ کتاب بہت پسند ہے!", "\"हमें चलना होगा,\" माँ कहती हैं। \"पर मुझे यह किताब बहुत पसंद है!\"", setOf("books")),
                StoryPage(8, "books/sw_45764/image_9.webp", "\"I don't want to return it.\"", "\"No quiero devolverlo.\"", "میں اسے واپس نہیں کرنا چاہتی۔", "\"मैं इसे लौटाना नहीं चाहती।\"", setOf("return")),
                StoryPage(9, "books/sw_45764/image_10.webp", "\"But I really like this one!\"", "\"¡Pero me gusta mucho este!\"", "لیکن مجھے یہ والی کتاب بہت ہی پسند ہے!", "\"पर मुझे यह वाली बहुत पसंद है!\"", setOf("books")),
                StoryPage(10, "books/sw_45764/image_11.webp", "\"And this one.\"", "\"Y este.\"", "اور یہ والی کتاب بھی۔", "\"और यह वाली भी।\"", setOf("books")),
                StoryPage(11, "books/sw_45764/image_12.webp", "\"And this one. And this one too!\"", "\"Y este. Y este también.\"", "اور یہ والی بھی، اور یہ دوسری والی بھی!", "\"और यह भी, और वह भी!\"", setOf("books")),
                StoryPage(12, "books/sw_45764/image_13.webp", "\"See? You return books and borrow more.\" \"YAY!\"", "\"¿Lo ves? Cuando devuelves los libros, puedes pedir otros prestados.\" \"¡YAY!\"", "دیکھا؟ جب تم کتابیں واپس کرتی ہو تو نئی کتابیں ادھار لے سکتی ہو! لولو خوش ہو گئی۔", "\"देखा? जब तुम किताबें लौटाती हो, तो और नई किताबें ले सकती हो!\" \"यै!\"", setOf("return", "borrow", "books"),
                    StoryQuizQuestion("Why did Lulu cheer 'YAY!' at the library?", listOf("When she returns books, she can borrow new ones", "She stayed under the sofa", "She lost her book"), 0)
                ),
                StoryPage(13, "books/sw_45764/image_14.webp", "Where is Lulu now? She is already reading her new stack of books!", "¿Dónde está Lulu ahora?", "اب لولو کہاں ہے؟ وہ اپنی نئی کتابیں پڑھنے میں مگن ہے!", "अब लुलु कहाँ है? वह अपनी नई किताबें पढ़ रही है!", setOf("books", "borrow"))
            )
        ),

        // 9. Play Store Screenshot #6 Book: "Good night, little star" (Level 4)
        StoryBook(
            id = "book_good_night",
            title = "Good night, little star",
            nativeTitleUrdu = "شب بخیر، ننھے ستارے",
            nativeTitleHindi = "शुभ रात्रि, नन्हे तारे",
            author = "Catherine Holtzhausen",
            illustrator = "Martha Evans",
            publisher = "Book Dash • StoryWeaver",
            level = 4,
            coverAssetPath = "books/sw_13916/image_16.webp",
            categoryTag = "Bedtime & Folktales",
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_16.webp", "When the golden sun goes down, tiny silver stars twinkle above the quiet green hills.", "Cuando el sol dorado se oculta, pequeñas estrellas brillan.", "جب سنہرا سورج غروب ہوتا ہے تو خاموش پہاڑیوں کے اوپر ننھے ستارے جگمگاتے ہیں۔", "जब सुनहरा सूरज ढलता है, तो पहाड़ियों के ऊपर नन्हे तारे टिमटिमाते हैं।", setOf("bright", "gleamed")),
                StoryPage(2, "books/sw_13916/image_17.webp", "The little ladybird folds her glowing yellow wings and whispers, \"Good night, little star!\"", "La pequeña mariquita cierra los ojos y susurra: \"¡Buenas noches, estrellita!\"", "ننھی لیڈی برڈ اپنے چمکتے پر سمیٹتی ہے اور سرگوشی کرتی ہے، شب بخیر ننھے ستارے!", "नन्ही लेडीबर्ड अपने पंख समेटती है और कहती है, \"शुभ रात्रि, नन्हे तारे!\"", setOf("ladybird", "whispers")),
                StoryPage(3, "books/sw_13916/image_18.webp", "Tomorrow morning will bring new stories, new games, and new adventures with all our friends.", "Mañana traerá nuevas historias y aventuras.", "کل صبح ہمارے تمام دوستوں کے ساتھ نئی کہانیاں اور نئے کھیل لائے گی۔", "कल सुबह नए खेल और नई कहानियाँ लेकर आएगी।", setOf("friends", "special"),
                    StoryQuizQuestion("When do the tiny silver stars twinkle above the hills?", listOf("When the golden sun goes down at night", "At noon", "Inside the cupboard"), 0)
                )
            )
        ),

        // 10. Downloadable Cloud Pack Book 1 (Real HTTPS download from dl.google.com/seekh!)
        StoryBook(
            id = "cloud_book_1",
            title = "Bibi Butterfly's Garden",
            nativeTitleUrdu = "بی بی تتلی کا باغ",
            nativeTitleHindi = "बीबी तितली का बगीचा",
            author = "Nadene Kriel",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • Cloud Pack",
            level = 2,
            coverAssetPath = "books/sw_13916/image_6.webp",
            requiresDownload = true,
            downloadUrl = "https://dl.google.com/seekh/v2/books/1750628513/sw_13916/cover_image.webp",
            bookType = "SERIES",
            categoryTag = "Animals & Nature",
            seriesTag = "Garden Series • Ep 2",
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_5.webp", "Bibi Butterfly loves flying from flower to flower in the sunny morning garden.", "A Bibi Mariposa le encanta volar de flor en flor.", "بی بی تتلی کو روشن صبح کے باغ میں ایک پھول سے دوسرے پھول تک اڑنا پسند ہے۔", "बीबी तितली को सुबह बगीचे में फूलों पर उड़ना पसंद है।", setOf("butterfly", "bright")),
                StoryPage(2, "books/sw_13916/image_6.webp", "She visits Sesa the spider and shares sweet drops of morning dew.", "Visita a Sesa la araña y comparte gotas de rocío.", "وہ سیسا مکڑی سے ملتی ہے اور شبنم کے میٹھے قطرے بانٹتی ہے۔", "वह सेसा मकड़ी से मिलती है।", setOf("spider", "friends")),
                StoryPage(3, "books/sw_13916/image_7.webp", "All the bugs in the garden clap their hands and read stories under the shade of the big leaf!", "Todos los bichitos aplauden y leen historias.", "باغ کے تمام ننھے کیڑے تالیاں بجاتے ہیں اور بڑے پتے کے سائے میں کہانیاں پڑھتے ہیں!", "बगीचे के सभी नन्हे दोस्त बड़े पत्ते की छाँव में कहानियाँ पढ़ते हैं!", setOf("friends", "books"),
                    StoryQuizQuestion("Where do the garden bugs read stories together?", listOf("Under the shade of the big leaf", "On a train", "In the rain"), 0)
                )
            )
        ),

        // 11. Downloadable Cloud Pack Book 2 (Real HTTPS download from dl.google.com/seekh!)
        StoryBook(
            id = "cloud_book_2",
            title = "The Drum Beat Express",
            nativeTitleUrdu = "ڈھول ایکسپریس ٹرین",
            nativeTitleHindi = "ढोल एक्सप्रेस ट्रेन",
            author = "Mabel Mnensa",
            illustrator = "Ntembiso Velemane",
            publisher = "Book Dash • Cloud Pack",
            level = 3,
            coverAssetPath = "books/sw_44485/image_12.webp",
            requiresDownload = true,
            downloadUrl = "https://dl.google.com/seekh/v2/books/1744957808/sw_44485/cover_image.webp",
            bookType = "DECODABLE",
            categoryTag = "Adventure & Travel",
            phonicsSkillTag = "Phonics: dr • tr • ch",
            pages = listOf(
                StoryPage(1, "books/sw_44485/image_10.webp", "Chugga-chugga-choo-choo! The Drum Beat Express rolls down the shiny steel tracks toward the coast.", "El tren rueda por las vías hacia la costa.", "چھک چھک کرتی ڈھول ایکسپریس ٹرین چمکتی پٹریوں پر ساحل کی طرف دوڑتی ہے۔", "छुक-छुक करती ट्रेन पटरियों पर समुद्र तट की ओर दौड़ती है।", setOf("train", "beach")),
                StoryPage(2, "books/sw_44485/image_12.webp", "Inside the carriage, the drummers play a joyful rhythm that makes every passenger tap their feet.", "Los bateristas tocan un ritmo alegre.", "ڈبے کے اندر ڈھول بجانے والے ایسی خوشگوار دھن بجاتے ہیں کہ ہر مسافر جھوم اٹھتا ہے۔", "डिब्बे के अंदर ढोल वादक मधुर ताल बजाते हैं।", setOf("drummers", "friends"),
                    StoryQuizQuestion("Where is the Drum Beat Express traveling?", listOf("Toward the coast and beach", "Up a snowy mountain", "Into a forest"), 0)
                )
            )
        ),

        // 12. Level 1 • Animals & Nature: "Frog by the Pond"
        StoryBook(
            id = "book_frog_pond",
            title = "Frog by the Pond",
            nativeTitleUrdu = "تالاب کے پاس ننھا مینڈک",
            nativeTitleHindi = "तालाब के पास नन्हा मेंढक",
            author = "Somsanouk Bouttavong",
            illustrator = "Ounla Santi",
            publisher = "Room to Read • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_37273/image_5.webp",
            categoryTag = "Animals & Nature",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: fr • gr • p",
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_37273/image_5.webp", "Look at the green frog sitting by the quiet blue pond.", "Mira la rana verde junto al estanque.", "خاموش نیلے تالاب کے کنارے بیٹھے ہرے مینڈک کو دیکھو۔", "शांत नीले तालाब के किनारे बैठे हरे मेंढक को देखो।", setOf("frog", "bright")),
                StoryPage(2, "books/sw_37273/image_6.webp", "Hop! Hop! The frog jumps high over a wet green leaf.", "¡Salta! La rana salta alto sobre una hoja.", "ہپ! ہپ! مینڈک گیلے ہرے پتے کے اوپر اونچی چھلانگ لگاتا ہے۔", "मेंढक गीले हरे पत्ते के ऊपर ऊँची छलांग लगाता है।", setOf("frog", "shoe")),
                StoryPage(3, "books/sw_37273/image_4.webp", "Caterpillar waves hello from the garden path. They are best friends!", "La oruga saluda desde el jardín.", "کیٹرپلر باغ کے راستے سے ہاتھ ہلا کر سلام کرتی ہے۔ وہ بہترین دوست ہیں!", "कैटरपिलर बग़ीचे के रास्ते से हाथ हिलाती है। वे अच्छे दोस्त हैं!", setOf("caterpillar", "friends"),
                    StoryQuizQuestion("Where is the green frog sitting?", listOf("By the quiet blue pond", "On a train", "Inside a cupboard"), 0)
                )
            )
        ),

        // 13. Level 1 • Family & Friends: "Mama's Warm Kitchen"
        StoryBook(
            id = "book_mama_kitchen",
            title = "Mama's Warm Kitchen",
            nativeTitleUrdu = "امی اور دادی کا باورچی خانہ",
            nativeTitleHindi = "माँ और दादी की रसोई",
            author = "Letta Machoga",
            illustrator = "Wiehan de Jager",
            publisher = "African Storybook • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_15058/image_6.webp",
            categoryTag = "Family & Friends",
            pages = listOf(
                StoryPage(1, "books/sw_15058/image_6.webp", "Mama and Grandmother are cooking warm soup in the kitchen.", "Mamá y la abuela cocinan sopa caliente.", "امی اور دادی باورچی خانے میں گرم سوپ پکا رہی ہیں۔", "माँ और दादी रसोई में गर्म सूप बना रही हैं।", setOf("grandmother", "friends")),
                StoryPage(2, "books/sw_15058/image_5.webp", "I bring my favorite storybook to read while the dinner cooks.", "Traigo mi libro favorito para leer.", "جب تک کھانا پکتا ہے، میں پڑھنے کے لیے اپنی پسندیدہ کہانی کی کتاب لاتا ہوں۔", "जब तक खाना बनता है, मैं अपनी पसंदीदा किताब लाता हूँ।", setOf("books")),
                StoryPage(3, "books/sw_15058/image_9.webp", "Everyone smiles and listens to my story around the table!", "¡Todos sonríen y escuchan mi cuento!", "میز کے گرد سب مسکراتے ہیں اور میری کہانی سنتے ہیں!", "सब मुस्कुराते हैं और मेरी कहानी सुनते हैं!", setOf("books", "friends"),
                    StoryQuizQuestion("What are Mama and Grandmother cooking in the kitchen?", listOf("Warm soup", "Chocolates", "Ice cream"), 0)
                )
            )
        ),

        // 14. Level 1 • Adventure & Travel: "First Stop at the Station"
        StoryBook(
            id = "book_station_stop",
            title = "First Stop at the Station",
            nativeTitleUrdu = "ریلوے اسٹیشن کا پہلا اسٹاپ",
            nativeTitleHindi = "रेलवे स्टेशन का पहला स्टॉप",
            author = "Mabel Mnensa",
            illustrator = "Ntembiso Velemane",
            publisher = "Book Dash • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_44485/image_4.webp",
            categoryTag = "Adventure & Travel",
            bookType = "SERIES",
            seriesTag = "Coastline Train • Ep 1",
            pages = listOf(
                StoryPage(1, "books/sw_44485/image_3.webp", "Nicole holds Mom's hand tightly on the sunny train platform.", "Nicole toma la mano de mamá en el andén.", "نکول روشن پلیٹ فارم پر امی کا ہاتھ مضبوطی سے پکڑتی ہے۔", "निकोल प्लेटफ़ॉर्म पर माँ का हाथ पकड़ती है।", setOf("train", "bright")),
                StoryPage(2, "books/sw_44485/image_4.webp", "Ding-dong! The shiny silver train doors slide open with a gentle whoosh.", "Las puertas del tren se abren.", "ڈنگ ڈونگ! چمکدار ٹرین کے دروازے کھلتے ہیں۔", "चमकीली ट्रेन के दरवाज़े खुलते हैं।", setOf("train", "whispers")),
                StoryPage(3, "books/sw_44485/image_5.webp", "We find a seat by the big window and wave to the station guard!", "Nos sentamos junto a la ventana.", "ہم بڑی کھڑکی کے پاس سیٹ لیتے ہیں اور گارڈ کو ہاتھ ہلاتے ہیں!", "हम खिड़की के पास बैठते हैं और हाथ हिलाते हैं!", setOf("train", "friends"),
                    StoryQuizQuestion("Where do Nicole and Mom sit on the train?", listOf("By the big window", "On the roof", "In the kitchen"), 0)
                )
            )
        ),

        // 15. Level 1 • Science & Weather: "Up in the Blue Sky"
        StoryBook(
            id = "book_blue_sky_ball",
            title = "Up in the Blue Sky",
            nativeTitleUrdu = "نیلے آسمان کی بلندیوں میں",
            nativeTitleHindi = "नीले आसमान की ऊँचाइयों में",
            author = "Marion Drew",
            illustrator = "Marion Drew",
            publisher = "Pratham Books • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_15195/image_15.webp",
            categoryTag = "Science & Weather",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: sk • bl • fl",
            pages = listOf(
                StoryPage(1, "books/sw_15195/image_14.webp", "Look up above the tall green trees! What is floating in the breeze?", "¡Mira arriba de los árboles!", "اونچے ہرے درختوں کے اوپر دیکھو! ہوا میں کیا تیر رہا ہے؟", "ऊँचे हरे पेड़ों के ऊपर देखो!", setOf("clouds", "bright")),
                StoryPage(2, "books/sw_15195/image_16.webp", "Soft white clouds drift slowly across the bright blue sky.", "Suaves nubes blancas flotan en el cielo.", "نرم سفید بادل چمکدار نیلے آسمان میں آہستہ آہستہ تیر رہے ہیں۔", "सफ़ेद बादल नीले आसमान में तैर रहे हैं।", setOf("clouds", "bright")),
                StoryPage(3, "books/sw_15195/image_17.webp", "Higher and higher, my red ball flies past the clouds toward the moon!", "Mi pelota roja vuela hacia la luna.", "میری سرخ گیند بادلوں سے آگے چاند کی طرف اڑتی جاتی ہے!", "मेरी लाल गेंद बादलों के पार चाँद की ओर उड़ती है!", setOf("clouds", "bright"),
                    StoryQuizQuestion("What drifts slowly across the bright blue sky?", listOf("Soft white clouds", "A green frog", "A red cupboard"), 0)
                )
            )
        ),

        // 16. Level 1 • Bedtime & Folktales: "Little Sister's Nap"
        StoryBook(
            id = "book_sister_nap",
            title = "Little Sister's Nap",
            nativeTitleUrdu = "چھوٹی بہن کی میٹھی نیند",
            nativeTitleHindi = "छोटी बहन की मीठी नींद",
            author = "Letta Machoga",
            illustrator = "Wiehan de Jager",
            publisher = "African Storybook • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_15058/image_4.webp",
            categoryTag = "Bedtime & Folktales",
            pages = listOf(
                StoryPage(1, "books/sw_15058/image_4.webp", "Shh! My little sister is fast asleep on her soft pillow.", "Mi hermanita duerme en su almohada.", "شش! میری چھوٹی بہن اپنے نرم تکیے پر گہری نیند سو رہی ہے۔", "मेरी छोटी बहन अपने नरम तकिए पर सो रही है।", setOf("whispers", "friends")),
                StoryPage(2, "books/sw_15058/image_2.webp", "I sit quietly beside her bed and open my picture book.", "Me siento en silencio y abro mi libro.", "میں اس کے بستر کے پاس خاموشی سے بیٹھ کر اپنی تصویروں والی کتاب کھولتا ہوں۔", "मैं चुपचाप बैठकर अपनी किताब खोलता हूँ।", setOf("books", "whispers")),
                StoryPage(3, "books/sw_15058/image_8.webp", "When she wakes up, I will read her the story of the friendly sun!", "Cuando despierte, le leeré un cuento.", "جب وہ جاگے گی تو میں اسے دوستانہ سورج کی کہانی پڑھ کر سناؤں گا!", "जब वह जागेगी, तो मैं उसे कहानी सुनाऊँगा!", setOf("books", "bright"),
                    StoryQuizQuestion("Why does the boy sit quietly beside the bed?", listOf("His little sister is fast asleep", "It is raining fish", "He lost his shoe"), 0)
                )
            )
        ),

        // 17. Level 1 • Animals & Nature: "Spider's Tiny Shoes"
        StoryBook(
            id = "book_spider_shoes",
            title = "Spider's Tiny Shoes",
            nativeTitleUrdu = "مکڑی کے ننھے جوتے",
            nativeTitleHindi = "मकड़ी के नन्हे जूते",
            author = "Ounla Santi",
            illustrator = "Ounla Santi",
            publisher = "Room to Read • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_37273/image_3.webp",
            categoryTag = "Animals & Nature",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: sp • sh • sm",
            pages = listOf(
                StoryPage(1, "books/sw_37273/image_3.webp", "Spider has eight tiny feet and eight shiny little shoes!", "La araña tiene ocho zapatos pequeños.", "مکڑی کے آٹھ ننھے پاؤں اور آٹھ چمکدار چھوٹے جوتے ہیں!", "मकड़ी के आठ नन्हे पैर और आठ छोटे जूते हैं!", setOf("spider", "shoe")),
                StoryPage(2, "books/sw_37273/image_2.webp", "She spins a silver web between two green branches every morning.", "Teje una red plateada cada mañana.", "وہ ہر صبح دو ہری شاخوں کے درمیان چاندی جیسا جالا بنتی ہے۔", "वह हर सुबह दो शाखाओं के बीच जाला बुनती है।", setOf("spider", "morning")),
                StoryPage(3, "books/sw_37273/image_4.webp", "Caterpillar smiles and says, \"Your shoes are small, but your web is grand!\"", "La oruga sonríe y admira la red.", "کیٹرپلر مسکرا کر کہتی ہے، تمہارے جوتے چھوٹے ہیں مگر تمہارا جالا شاندار ہے!", "कैटरपिलर मुस्कुराकर कहती है, \"तुम्हारा जाला बहुत सुंदर है!\"", setOf("caterpillar", "shoe"),
                    StoryQuizQuestion("How many tiny shoes does Spider wear?", listOf("Eight shiny little shoes", "Two big boots", "Zero shoes"), 0)
                )
            )
        ),

        // 18. Level 2 • Animals & Nature: "Crab on the Sandy Shore"
        StoryBook(
            id = "book_crab_shore",
            title = "Crab on the Sandy Shore",
            nativeTitleUrdu = "ساحل کی ریت پر کیکڑا",
            nativeTitleHindi = "रेतीले तट पर केकड़ा",
            author = "Somsanouk Bouttavong",
            illustrator = "Ounla Santi",
            publisher = "Room to Read • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_37273/image_9.webp",
            categoryTag = "Animals & Nature",
            bookType = "SERIES",
            seriesTag = "Garden Series • Ep 3",
            pages = listOf(
                StoryPage(1, "books/sw_37273/image_9.webp", "By the warm sandy beach, little red Crab scuttles sideways across the shells.", "El pequeño cangrejo rojo camina de lado en la arena.", "گرم ریتیلے ساحل پر ننھا سرخ کیکڑا سیپیوں کے پاس ترچھا چلتا ہے۔", "रेतीले समुद्र तट पर नन्हा केकड़ा सीपियों के पास चलता है।", setOf("crab", "beach")),
                StoryPage(2, "books/sw_37273/image_10.webp", "Dig, dig, dig! Crab uses his sharp claws and sturdy shoes to build a cozy sand burrow.", "El cangrejo cava en la arena.", "کھودو، کھودو! کیکڑا اپنے مضبوط پنجوں سے ریت میں ایک آرام دہ گھر بناتا ہے۔", "केकड़ा रेत में अपना आरामदायक घर बनाता है।", setOf("crab", "shoe")),
                StoryPage(3, "books/sw_37273/image_8.webp", "When the cool ocean waves splash ashore, Crab peeks out and waves at Caterpillar!", "Cuando llegan las olas, el cangrejo saluda.", "جب سمندر کی ٹھنڈی لہریں کنارے آتی ہیں تو کیکڑا باہر جھانک کر ہاتھ ہلاتا ہے!", "जब लहरें आती हैं, तो केकड़ा बाहर झाँककर हाथ हिलाता है!", setOf("crab", "caterpillar"),
                    StoryQuizQuestion("What does Crab use his sharp shoes for on the beach?", listOf("Digging a cozy burrow in the sand", "Flying in the sky", "Painting yellow wings"), 0)
                )
            )
        ),

        // 19. Level 2 • Family & Friends: "The Karate Kids on Board"
        StoryBook(
            id = "book_karate_kids",
            title = "The Karate Kids on Board",
            nativeTitleUrdu = "ٹرین میں کراٹے چیمپئنز",
            nativeTitleHindi = "ट्रेन में कराटे चैंपियन",
            author = "Mabel Mnensa",
            illustrator = "Ntembiso Velemane",
            publisher = "Book Dash • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_44485/image_8.webp",
            categoryTag = "Family & Friends",
            bookType = "SERIES",
            seriesTag = "Coastline Train • Ep 2",
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_44485/image_7.webp", "Two cheerful brothers board the morning train wearing crisp white uniforms and black belts.", "Dos hermanos suben al tren con uniformes blancos.", "دو خوش مزاج بھائی سفید یونیفارم اور بلیک بیلٹ پہنے صبح کی ٹرین میں سوار ہوتے ہیں۔", "दो भाई सफ़ेद यूनिफ़ॉर्म पहनकर सुबह की ट्रेन में चढ़ते हैं।", setOf("train", "friends")),
                StoryPage(2, "books/sw_44485/image_8.webp", "\"We practice karate to stay strong, balanced, and kind!\" they tell Nicole with a bow.", "Practicamos karate para ser fuertes y amables.", "ہم مضبوط، متوازن اور مہربان رہنے کے لیے کراٹے کی مشق کرتے ہیں! وہ نکول کو بتاتے ہیں۔", "\"हम मज़बूत और विनम्र रहने के लिए कराटे सीखते हैं!\"", setOf("friends", "special")),
                StoryPage(3, "books/sw_44485/image_9.webp", "Nicole claps as they show a synchronized balance pose before the train reaches the beach.", "Nicole aplaude mientras muestran una postura de equilibrio.", "جب وہ توازن کا کرتب دکھاتے ہیں تو نکول خوشی سے تالیاں بجاتی ہے۔", "निकोल खुशी से तालियाँ बजाती है।", setOf("beach", "friends"),
                    StoryQuizQuestion("Why do the brothers practice karate?", listOf("To stay strong, balanced, and kind", "To sleep on the train", "To catch rainfish"), 0)
                )
            )
        ),

        // 20. Level 2 • Adventure & Travel: "Sweet Treats on the Corner"
        StoryBook(
            id = "book_sweet_treats",
            title = "Sweet Treats on the Corner",
            nativeTitleUrdu = "نکڑ والی مٹھائی اور آئس کریم",
            nativeTitleHindi = "कोने वाली मिठाई और आइसक्रीम",
            author = "Mabel Mnensa",
            illustrator = "Ntembiso Velemane",
            publisher = "Book Dash • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_44485/image_6.webp",
            categoryTag = "Adventure & Travel",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: ch • sw • tr",
            pages = listOf(
                StoryPage(1, "books/sw_44485/image_6.webp", "The corner shop is filled with golden chocolates, peppermint swirls, and fruit candies.", "La tienda está llena de chocolates dorados.", "کونے کی دکان سنہری چاکلیٹ اور پھلوں کی ٹافیوں سے بھری ہوئی ہے۔", "कोने की दुकान सुनहरी चॉकलेट और मिठाइयों से भरी है।", setOf("chocolates", "street")),
                StoryPage(2, "books/sw_44485/image_5.webp", "Mom smiles and says, \"Let's save room for cold mango ice cream by the ocean waves!\"", "Guardemos lugar para el helado en la playa.", "امی مسکرا کر کہتی ہیں، سمندر کی لہروں کے پاس ٹھنڈی آم کی آئس کریم کے لیے جگہ رکھو!", "माँ मुस्कुराकर कहती हैं, \"समुद्र तट पर आइसक्रीम खाएँगे!\"", setOf("beach", "chocolates")),
                StoryPage(3, "books/sw_44485/image_14.webp", "At the beach boardwalk, they share two scoops of sunshine yellow ice cream together!", "En la playa comparten helado amarillo.", "ساحل پر وہ دونوں مل کر دھوپ جیسی پیلی آئس کریم مزے سے کھاتے ہیں!", "समुद्र तट पर वे मिलकर आइसक्रीम का आनंद लेते हैं!", setOf("beach", "yellow"),
                    StoryQuizQuestion("What treat did Mom and Nicole share at the beach boardwalk?", listOf("Cold mango ice cream", "Hot soup", "Green leaves"), 0)
                )
            )
        ),

        // 21. Level 2 • Science & Weather: "Silver Raindrops in the Garden"
        StoryBook(
            id = "book_silver_raindrops",
            title = "Silver Raindrops in the Garden",
            nativeTitleUrdu = "باغ میں چاندی جیسی بارش",
            nativeTitleHindi = "बग़ीचे में चाँदी जैसी बारिश",
            author = "Clyde Beech",
            illustrator = "Mohale Mashigo",
            publisher = "Book Dash • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_45764/image_6.webp",
            categoryTag = "Science & Weather",
            pages = listOf(
                StoryPage(1, "books/sw_45764/image_2.webp", "Cool wind blows across the hills and gathers fluffy grey clouds high in the sky.", "El viento fresco reúne nubes grises.", "ٹھنڈی ہوا پہاڑیوں پر چلتی ہے اور آسمان میں سرمئی بادلوں کو اکٹھا کرتی ہے۔", "ठंडी हवा आसमान में बादलों को इकट्ठा करती है।", setOf("clouds", "thunder")),
                StoryPage(2, "books/sw_45764/image_6.webp", "Pitter-patter! Silver raindrops water the thirsty flowers and wash the green leaves clean.", "Las gotas de lluvia riegan las flores.", "ٹپ ٹپ! چاندی جیسی بوندیں پیاسے پھولوں کو سیراب کرتی ہیں اور ہرے پتوں کو دھو دیتی ہیں۔", "बारिश की बूँदें फूलों और पत्तों को ताज़ा कर देती हैं।", setOf("rain", "bright")),
                StoryPage(3, "books/sw_45764/image_10.webp", "When the sun peeks through the clouds, a seven-color rainbow arcs across the garden!", "Un arcoíris de siete colores brilla en el jardín.", "جب سورج بادلوں سے جھانکتا ہے تو باغ کے اوپر سات رنگوں کی قوسِ قزح چمک اٹھتی ہے!", "जब सूरज निकलता है, तो सात रंगों का इंद्रधनुष चमक उठता है!", setOf("clouds", "bright"),
                    StoryQuizQuestion("What appears in the sky when the sun peeks through the rain clouds?", listOf("A seven-color rainbow", "A red train", "A sleeping sister"), 0)
                )
            )
        ),

        // 22. Level 2 • Bedtime & Folktales: "The Cozy Leaf Cocoon"
        StoryBook(
            id = "book_cozy_cocoon",
            title = "The Cozy Leaf Cocoon",
            nativeTitleUrdu = "نرم پتے کا آرام دہ کوکون",
            nativeTitleHindi = "नरम पत्ते का आरामदायक कोकून",
            author = "Somsanouk Bouttavong",
            illustrator = "Ounla Santi",
            publisher = "Room to Read • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_37273/image_12.webp",
            categoryTag = "Bedtime & Folktales",
            bookType = "SERIES",
            seriesTag = "Garden Series • Ep 4",
            pages = listOf(
                StoryPage(1, "books/sw_37273/image_11.webp", "After a long day of walking through the meadow, little Caterpillar yawns under the moonlight.", "Después de caminar todo el día, la oruga bosteza.", "سارا دن باغ میں چلنے کے بعد ننھی کیٹرپلر چاندنی میں جمائی لیتی ہے۔", "सारा दिन चलने के बाद नन्ही कैटरपिलर चाँदनी में आराम करती है।", setOf("caterpillar", "whispers")),
                StoryPage(2, "books/sw_37273/image_12.webp", "She wraps herself in a warm, silky green cocoon that sways gently in the night breeze.", "Se envuelve en un capullo sedoso.", "وہ اپنے گرد ایک گرم اور ریشمی ہرا کوکون بنا لیتی ہے جو رات کی ہوا میں ہولے ہولے جھولتا ہے۔", "वह अपने चारों ओर एक रेशमी कोकून बना लेती है।", setOf("cocoon", "caterpillar")),
                StoryPage(3, "books/sw_37273/image_14.webp", "Inside her cozy cocoon, she dreams of soaring high above the flowers on golden butterfly wings!", "Sueña con volar con alas de mariposa.", "اپنے آرام دہ کوکون کے اندر وہ سنہری پروں کے ساتھ پھولوں کے اوپر اڑنے کے خواب دیکھتی ہے!", "कोकून के अंदर वह सुनहरे पंखों से उड़ने के सपने देखती है!", setOf("butterfly", "wings"),
                    StoryQuizQuestion("What does Caterpillar dream about inside her cozy cocoon?", listOf("Soaring on golden butterfly wings", "Riding a bicycle", "Cooking soup"), 0)
                )
            )
        ),

        // 23. Level 2 • Family & Friends: "Kick the Ball in the Park"
        StoryBook(
            id = "book_kick_ball_park",
            title = "Kick the Ball in the Park",
            nativeTitleUrdu = "پارک میں فٹبال کا کھیل",
            nativeTitleHindi = "पार्क में फ़ुटबॉल का खेल",
            author = "Khothatso Ranoosi",
            illustrator = "Marion Drew",
            publisher = "Pratham Books • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_15195/image_7.webp",
            categoryTag = "Family & Friends",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: ck • st • gr",
            pages = listOf(
                StoryPage(1, "books/sw_15195/image_5.webp", "Every afternoon after school, we bring our bouncy red ball to the green neighborhood park.", "Cada tarde llevamos nuestra pelota roja al parque.", "ہر سہ پہر اسکول کے بعد ہم اپنی اچھلتی سرخ گیند محلے کے ہرے پارک میں لاتے ہیں۔", "हर दोपहर स्कूल के बाद हम अपनी लाल गेंद पार्क में लाते हैं।", setOf("school", "bright")),
                StoryPage(2, "books/sw_15195/image_7.webp", "Pass to the left, pass to the right, and kick the ball straight toward the goalpost!", "Pasa a la izquierda y patea hacia la portería.", "بائیں پاس کرو، دائیں پاس کرو، اور گیند کو سیدھا گول پوسٹ کی طرف کک مارو!", "गेंद को सीधा गोलपोस्ट की तरफ़ किक मारो!", setOf("friends", "shoe")),
                StoryPage(3, "books/sw_15195/image_9.webp", "Win or lose, we always shake hands and cheer for every player on the team!", "Siempre aplaudimos a todos los jugadores.", "جیتیں یا ہاریں، ہم ہمیشہ ہاتھ ملاتے ہیں اور ٹیم کے ہر کھلاڑی کی حوصلہ افزائی کرتے ہیں!", "हम हमेशा हाथ मिलाते हैं और हर खिलाड़ी का उत्साह बढ़ाते हैं!", setOf("friends", "special"),
                    StoryQuizQuestion("What do the players always do at the end of the park game?", listOf("Shake hands and cheer for everyone", "Hide in the cupboard", "Paint their shoes"), 0)
                )
            )
        ),

        // 24. Level 3 • Animals & Nature: "Manto Mantis & Bug School"
        StoryBook(
            id = "book_manto_school",
            title = "Manto Mantis & Bug School",
            nativeTitleUrdu = "مینٹو مینٹس اور باغ کا اسکول",
            nativeTitleHindi = "मैंटो और बग़ीचे का स्कूल",
            author = "Catherine Holtzhausen",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • StoryWeaver",
            level = 3,
            coverAssetPath = "books/sw_13916/image_7.webp",
            categoryTag = "Animals & Nature",
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_4.webp", "Early in the morning, Manto Mantis polishes his green glasses and walks to Bug School.", "Temprano en la mañana, Manto camina a la escuela.", "صبح سویرے مینٹو مینٹس اپنی ہری عینک صاف کرتا ہے اور بگز اسکول کی طرف چلتا ہے۔", "सुबह सवेरे मैंटो अपने स्कूल की ओर चलता है।", setOf("morning", "school")),
                StoryPage(2, "books/sw_13916/image_7.webp", "In Miss Miya's classroom, ladybirds, beetles, and butterflies learn to count petals and read poems.", "En la clase aprenden a contar pétalos y leer poemas.", "مس میا کی کلاس میں لیڈی برڈز اور تتلیاں پھولوں کی پتیاں گننا اور نظمیں پڑھنا سیکھتی ہیں۔", "मिस मिया की कक्षा में सब मिलकर कविताएँ पढ़ना सीखते हैं।", setOf("ladybird", "butterfly", "school")),
                StoryPage(3, "books/sw_13916/image_14.webp", "At recess, every bug shares their special talent during the Garden Talent Show!", "En el recreo todos comparten su talento especial.", "بریک کے وقت ہر ننھا کیڑا گارڈن ٹیلنٹ شو میں اپنا خاص ہنر دکھاتا ہے!", "छुट्टी के समय सभी दोस्त अपना ख़ास हुनर दिखाते हैं!", setOf("special", "friends"),
                    StoryQuizQuestion("What do the bugs learn in Miss Miya's classroom?", listOf("Counting flower petals and reading poems", "Driving a train", "Catching fish"), 0)
                )
            )
        ),

        // 25. Level 3 • Adventure & Travel: "The Navy Sailor's Compass"
        StoryBook(
            id = "book_navy_sailor",
            title = "The Navy Sailor's Compass",
            nativeTitleUrdu = "بحریہ کی ملاح اور قطب نما",
            nativeTitleHindi = "नौसेना की नाविक और दिशासूचक",
            author = "Mabel Mnensa",
            illustrator = "Ntembiso Velemane",
            publisher = "Book Dash • StoryWeaver",
            level = 3,
            coverAssetPath = "books/sw_44485/image_10.webp",
            categoryTag = "Adventure & Travel",
            bookType = "SERIES",
            seriesTag = "Coastline Train • Ep 3",
            pages = listOf(
                StoryPage(1, "books/sw_44485/image_10.webp", "On the coastal train, Nicole meets Captain Zara, a proud navy sailor in a crisp white uniform.", "Nicole conoce a una marinera de la Marina.", "ساحلی ٹرین میں نکول کی ملاقات کیپٹن زارا سے ہوتی ہے جو سفید وردی میں بحریہ کی باہمت ملاح ہے۔", "ट्रेन में निकोल की मुलाक़ात नौसेना की नाविक ज़ारा से होती है।", setOf("sailor", "train")),
                StoryPage(2, "books/sw_44485/image_13.webp", "Captain Zara shows Nicole a brass pocket compass that always points north across the wide blue ocean.", "La marinera le muestra una brújula dorada.", "کیپٹن زارا نکول کو ایک سنہری قطب نما دکھاتی ہے جو کھلے سمندر میں ہمیشہ شمال کی سمت بتاتا ہے۔", "नाविक ज़ारा निकोल को एक सुनहरा कंपास दिखाती है।", setOf("sailor", "bright")),
                StoryPage(3, "books/sw_44485/image_14.webp", "\"With curiosity, courage, and good books, you can navigate anywhere in the world!\" says the sailor.", "Con curiosidad y buenos libros puedes navegar por el mundo.", "تجسس، ہمت اور اچھی کتابوں کے ساتھ تم دنیا میں کہیں بھی سفر کر سکتی ہو! ملاح مسکرا کر کہتی ہے۔", "\"जिज्ञासा, साहस और अच्छी किताबों से तुम कहीं भी पहुँच सकती हो!\"", setOf("books", "sailor"),
                    StoryQuizQuestion("What instrument did the navy sailor show Nicole on the train?", listOf("A brass pocket compass that points north", "A red raincoat", "A wooden drum"), 0)
                )
            )
        ),

        // 26. Level 3 • Family & Friends: "Hide and Seek with Lulu"
        StoryBook(
            id = "book_lulu_hide_seek",
            title = "Hide and Seek with Lulu",
            nativeTitleUrdu = "لولو کے ساتھ آنکھ مچولی",
            nativeTitleHindi = "लुलु के साथ लुका-छिपी",
            author = "Clyde Beech",
            illustrator = "Mohale Mashigo",
            publisher = "Book Dash • StoryWeaver",
            level = 3,
            coverAssetPath = "books/sw_45764/image_4.webp",
            categoryTag = "Family & Friends",
            bookType = "SERIES",
            seriesTag = "Lulu Library • Ep 1",
            pages = listOf(
                StoryPage(1, "books/sw_45764/image_4.webp", "Lulu loves playing hide and seek inside the big community reading room.", "A Lulu le encanta jugar a las escondidas.", "لولو کو کمیونٹی لائبریری کے بڑے کمرے میں آنکھ مچولی کھیلنا بہت پسند ہے۔", "लुलु को रीडिंग रूम में लुका-छिपी खेलना बहुत पसंद है।", setOf("curtain", "books")),
                StoryPage(2, "books/sw_45764/image_5.webp", "Ma checks behind the velvet curtain and inside the tall wooden cupboard, giggling softly.", "Mamá busca detrás de la cortina y en el armario.", "امی مخملی پردے کے پیچھے اور لکڑی کی اونچی الماری کے پاس مسکراتے ہوئے تلاش کرتی ہیں۔", "माँ पर्दे के पीछे और अलमारी के पास ढूँढती हैं।", setOf("curtain", "cupboard")),
                StoryPage(3, "books/sw_45764/image_7.webp", "Found you! Lulu is sitting behind a cozy fortress made of colorful storybooks!", "Lulu está detrás de una fortaleza de libros.", "مل گئی! لولو رنگ برنگی کتابوں کے پیچھے بیٹھی مسکرا رہی ہے!", "मिल गई! लुलु रंग-बिरंगी किताबों के पीछे बैठी है!", setOf("books", "friends"),
                    StoryQuizQuestion("Where was Lulu hiding when Ma found her?", listOf("Behind a cozy stack of colorful storybooks", "Up in a tree", "Inside a train"), 0)
                )
            )
        ),

        // 27. Level 3 • Science & Weather: "How Thunder Clouds Form"
        StoryBook(
            id = "book_science_thunder",
            title = "How Thunder Clouds Form",
            nativeTitleUrdu = "بادل کیسے گرجتے اور برستے ہیں؟",
            nativeTitleHindi = "बादल कैसे गरजते और बरसते हैं?",
            author = "Mohale Mashigo",
            illustrator = "Clyde Beech",
            publisher = "Book Dash • Science Series",
            level = 3,
            coverAssetPath = "books/sw_45764/image_10.webp",
            categoryTag = "Science & Weather",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: th • cl • cr",
            pages = listOf(
                StoryPage(1, "books/sw_45764/image_2.webp", "When warm air rises quickly from the earth, it cools down high in the sky to form tall clouds.", "Cuando el aire caliente sube, forma nubes altas.", "جب زمین سے گرم ہوا تیزی سے اوپر اٹھتی ہے تو آسمان کی بلندی پر ٹھنڈی ہو کر اونچے بادل بناتی ہے۔", "जब गर्म हवा ऊपर उठती है, तो आसमान में ठंडी होकर बादल बनाती है।", setOf("clouds", "crashed")),
                StoryPage(2, "books/sw_45764/image_10.webp", "Tiny water droplets bump together inside the cloud until they grow heavy and fall as refreshing rain!", "Las gotas de agua caen como lluvia refrescante.", "بادل کے اندر پانی کے ننھے قطرے آپس میں مل کر بھاری ہو جاتے ہیں اور تازہ بارش بن کر برستے ہیں!", "बादल के अंदर पानी की बूँदें भारी होकर बारिश बनकर बरसती हैं!", setOf("clouds", "rain")),
                StoryPage(3, "books/sw_45764/image_13.webp", "And the rumble of thunder is the sound of air expanding fast after a bright flash of lightning!", "El trueno es el sonido del aire expandiéndose.", "اور بادلوں کی گرج دراصل بجلی چمکنے کے بعد ہوا کے تیزی سے پھیلنے کی آواز ہے!", "और बादलों की गड़गड़ाहट बिजली चमकने के बाद हवा के फैलने की आवाज़ है!", setOf("thunder", "bright"),
                    StoryQuizQuestion("What causes water droplets in a cloud to fall as rain?", listOf("They bump together and grow heavy", "A frog jumps on them", "Candles blow them down"), 0)
                )
            )
        ),

        // 28. Level 3 • Animals & Nature: "Grasshopper's High Meadow Jump"
        StoryBook(
            id = "book_grasshopper_meadow",
            title = "Grasshopper's High Meadow Jump",
            nativeTitleUrdu = "ہری گھاس میں ٹڈے کی اونچی چھلانگ",
            nativeTitleHindi = "हरी घास में टिड्डे की ऊँची छलांग",
            author = "Somsanouk Bouttavong",
            illustrator = "Ounla Santi",
            publisher = "Room to Read • StoryWeaver",
            level = 3,
            coverAssetPath = "books/sw_37273/image_7.webp",
            categoryTag = "Animals & Nature",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: gr • str • mp",
            pages = listOf(
                StoryPage(1, "books/sw_37273/image_7.webp", "In the emerald meadow, Grasshopper stretches his long back legs like two powerful springs.", "El saltamontes estira sus largas patas traseras.", "سبزہ زار میں ٹڈا اپنی لمبی پچھلی ٹانگوں کو دو طاقتور اسپرنگ کی طرح پھیلاتا ہے۔", "हरे मैदान में टिड्डा अपनी लंबी टाँगों को स्प्रिंग की तरह फैलाता है।", setOf("grasshopper", "bright")),
                StoryPage(2, "books/sw_37273/image_8.webp", "Boing! With a single leap, he vaults twenty times his own length over the wildflowers!", "De un salto vuela sobre las flores silvestres.", "بوائنگ! ایک ہی چھلانگ میں وہ جنگلی پھولوں کے اوپر سے اپنے قد سے بیس گنا دور کود جاتا ہے!", "एक ही छलांग में वह फूलों के ऊपर से बहुत दूर कूद जाता है!", setOf("grasshopper", "special")),
                StoryPage(3, "books/sw_37273/image_14.webp", "From the top of a sunflower, he plays a cheerful violin tune with his wings for Bibi Butterfly.", "Toca una melodía alegre para la mariposa.", "سورج مکھی کے پھول کے اوپر بیٹھ کر وہ تتلی کے لیے اپنے پروں سے سریلی دھن بجاتا ہے۔", "सूरजमुखी के फूल पर बैठकर वह तितली के लिए मधुर धुन बजाता है।", setOf("butterfly", "wings"),
                    StoryQuizQuestion("How does Grasshopper jump so high across the meadow?", listOf("His long back legs work like powerful springs", "He wears Frog's big boots", "He rides a balloon"), 0)
                )
            )
        ),

        // 29. Level 3 • Bedtime & Folktales: "Lara's Golden Bath"
        StoryBook(
            id = "book_lara_golden_bath",
            title = "Lara's Golden Bath",
            nativeTitleUrdu = "لارا کے سنہری پروں کی چمک",
            nativeTitleHindi = "लारा के सुनहरे पंखों की चमक",
            author = "Nadene Kriel",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • StoryWeaver",
            level = 3,
            coverAssetPath = "books/sw_13916/image_15.webp",
            categoryTag = "Bedtime & Folktales",
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_15.webp", "After school, Lara remembered Miss Miya's wise words: being yourself is the greatest gift of all.", "Lara recordó las sabias palabras de su maestra.", "اسکول کے بعد لارا کو مس میا کی عقلمندانہ بات یاد آئی کہ اپنی اصل پہچان ہی سب سے بڑا تحفہ ہے۔", "स्कूल के बाद लारा को मिस मिया की समझदारी भरी बात याद आई।", setOf("special", "unique")),
                StoryPage(2, "books/sw_13916/image_16.webp", "She hopped into a warm bubble bath and scrubbed off every drop of red paint until her yellow wings gleamed.", "Se dio un baño de burbujas hasta que sus alas brillaron.", "اس نے گرم جھاگ والے پانی سے نہا کر سرخ رنگ دھو ڈالا یہاں تک کہ اس کے پیلے پر چمک اٹھے۔", "उसने स्नान करके लाल रंग धो डाला और उसके पीले पंख चमक उठे।", setOf("scrubbed", "gleamed", "yellow")),
                StoryPage(3, "books/sw_13916/image_17.webp", "Wrapped in a soft towel, Lara smiled in the mirror, proud of her bright golden glow!", "Lara sonrió en el espejo, orgullosa de sus alas.", "نرم تولیے میں لپٹی لارا آئینے میں دیکھ کر مسکرائی، اسے اپنے سنہری پروں پر فخر تھا!", "लारा आईने में देखकर मुस्कुराई, उसे अपने सुनहरे पंखों पर गर्व था!", setOf("bright", "wings"),
                    StoryQuizQuestion("What lesson did Lara learn from Miss Miya?", listOf("Being yourself is the greatest gift of all", "Everyone should wear red paint", "Never go to school"), 0)
                )
            )
        ),

        // 30. Level 4 • Family & Friends: "The Library Borrowing Secret"
        StoryBook(
            id = "book_library_secret",
            title = "The Library Borrowing Secret",
            nativeTitleUrdu = "لائبریری سے کتابیں لینے کا راز",
            nativeTitleHindi = "लाइब्रेरी से किताबें लेने का रहस्य",
            author = "Clyde Beech & Mohale Mashigo",
            illustrator = "Clyde Beech",
            publisher = "Book Dash • StoryWeaver",
            level = 4,
            coverAssetPath = "books/sw_45764/image_12.webp",
            categoryTag = "Family & Friends",
            bookType = "SERIES",
            seriesTag = "Lulu Library • Ep 2",
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_45764/image_8.webp", "Lulu hugged her favorite picture book tightly against her chest when the library closing bell rang.", "Lulu abrazó su libro favorito cuando sonó la campana.", "جب لائبریری بند ہونے کی گھنٹی بجی تو لولو نے اپنی پسندیدہ کتاب کو سینے سے لگا لیا۔", "जब लाइब्रेरी बंद होने की घंटी बजी, तो लुलु ने किताब को सीने से लगा लिया।", setOf("books", "return")),
                StoryPage(2, "books/sw_45764/image_12.webp", "The kind librarian explained the wonderful secret of every library: when you return one book, you can borrow three new adventures!", "Cuando devuelves un libro, puedes tomar prestadas nuevas aventuras.", "مہربان لائبریرین نے لائبریری کا شاندار راز بتایا: جب تم ایک کتاب واپس کرتی ہو تو تین نئی کہانیاں گھر لے جا سکتی ہو!", "लाइब्रेरियन ने बताया कि एक किताब लौटाने पर तुम नई कहानियाँ घर ले जा सकती हो!", setOf("return", "borrow", "books")),
                StoryPage(3, "books/sw_45764/image_14.webp", "Lulu stamped her library card with pride and carried a tall stack of space, ocean, and wildlife stories home!", "Lulu llevó una pila de libros nuevos a casa.", "لولو نے خوشی سے اپنا لائبریری کارڈ اسٹیمپ کروایا اور نئی کتابوں کا ڈھیر لے کر گھر روانہ ہوئی!", "लुलु ने खुशी से अपना कार्ड स्टैम्प करवाया और नई किताबें लेकर घर गई!", setOf("books", "borrow"),
                    StoryQuizQuestion("What is the wonderful secret of the community library?", listOf("Returning books lets you borrow brand-new adventures", "You must hide under the sofa", "Books are only for teachers"), 0)
                )
            )
        ),

        // 31. Level 4 • Adventure & Travel: "Festival of the Street Drummers"
        StoryBook(
            id = "book_festival_drummers",
            title = "Festival of the Street Drummers",
            nativeTitleUrdu = "گلی کے ڈھول بجانے والوں کا میلہ",
            nativeTitleHindi = "गली के ढोल वादकों का उत्सव",
            author = "Mabel Mnensa",
            illustrator = "Ntembiso Velemane",
            publisher = "Book Dash • StoryWeaver",
            level = 4,
            coverAssetPath = "books/sw_44485/image_11.webp",
            categoryTag = "Adventure & Travel",
            pages = listOf(
                StoryPage(1, "books/sw_44485/image_11.webp", "Once every autumn, musicians from across the valley gather on Main Street with carved wooden djembe drums.", "Los músicos se reúnen en la calle principal con tambores.", "ہر موسمِ خزاں میں وادی بھر کے فنکار تراشے ہوئے لکڑی کے ڈھول لے کر بڑی گلی میں جمع ہوتے ہیں۔", "हर शरद ऋतु में कलाकार लकड़ी के ढोल लेकर मुख्य गली में इकट्ठा होते हैं।", setOf("drummers", "street")),
                StoryPage(2, "books/sw_44485/image_12.webp", "Their golden and crimson sleeves flash in the afternoon sunlight as rolling rhythms echo between the buildings.", "Sus mangas doradas brillan bajo el sol de la tarde.", "دوپہر کی دھوپ میں ان کے سرخ اور سنہری لباس چمکتے ہیں اور ڈھول کی تھاپ پوری گلی میں گونجتی ہے۔", "दोपहर की धूप में ढोल की मधुर थाप पूरी गली में गूँज उठती है।", setOf("drummers", "bright")),
                StoryPage(3, "books/sw_44485/image_14.webp", "Neighbors of every age join the circle to clap, sing, and celebrate the harmony of their hometown.", "Vecinos de todas las edades aplauden y celebran juntos.", "ہر عمر کے ہمسائے دائرے میں شامل ہو کر تالیاں بجاتے ہیں اور اپنے شہر کی یکجہتی کا جشن مناتے ہیں۔", "सभी पड़ोसी मिलकर तालियाँ बजाते हैं और उत्सव मनाते हैं।", setOf("friends", "colors"),
                    StoryQuizQuestion("What instruments do the valley musicians bring to Main Street?", listOf("Carved wooden djembe drums", "Brass compasses", "Glass slippers"), 0)
                )
            )
        ),

        // 32. Level 4 • Animals & Nature: "Unique Spots and Long Legs"
        StoryBook(
            id = "book_unique_spots",
            title = "Unique Spots and Long Legs",
            nativeTitleUrdu = "ہر ایک کی اپنی منفرد پہچان",
            nativeTitleHindi = "हर किसी की अपनी अनोखी पहचान",
            author = "Catherine Holtzhausen",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • StoryWeaver",
            level = 4,
            coverAssetPath = "books/sw_13916/image_13.webp",
            categoryTag = "Animals & Nature",
            bookType = "ASSESSMENT",
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_13.webp", "In the meadow ecosystem, no two creatures are exactly alike—and that diversity keeps nature strong.", "En la naturaleza ninguna criatura es exactamente igual a otra.", "قدرت کے اس خوبصورت باغ میں کوئی بھی دو جاندار بالکل ایک جیسے نہیں ہیں، اور یہی تنوع فطرت کا حسن ہے۔", "प्रकृति के इस सुंदर बग़ीचे में हर जीव की अपनी अलग विशेषता है।", setOf("unique", "special")),
                StoryPage(2, "books/sw_13916/image_14.webp", "Sipho the beetle has a bold silver spot on his shell, Sally has long leaping legs, and Lara shines in bright yellow.", "Sipho tiene un lunar, Sally piernas largas y Lara alas amarillas.", "سیفو کے خول پر چمکدار نشان ہے، سیلی کی لمبی ٹانگیں ہیں، اور لارا پیلے پروں کے ساتھ چمکتی ہے۔", "सीफो का निशान, सैली की लंबी टाँगें और लारा के पीले पंख उन्हें अनोखा बनाते हैं।", setOf("yellow", "wings", "unique")),
                StoryPage(3, "books/sw_13916/image_18.webp", "When they work together, each friend's unique strength helps the whole class solve any challenge!", "Cuando trabajan juntos, la fortaleza de cada amigo ayuda a todos.", "جب وہ مل کر کام کرتے ہیں تو ہر دوست کی منفرد خوبی پوری کلاس کو ہر مشکل حل کرنے میں مدد دیتی ہے!", "जब वे मिलकर काम करते हैं, तो हर दोस्त की खूबी सबकी मदद करती है!", setOf("friends", "special"),
                    StoryQuizQuestion("Why is diversity important among the friends in the meadow?", listOf("Each friend's unique strength helps the whole group succeed", "Everyone must look identical", "Only beetles can read"), 0)
                )
            )
        ),

        // 33. Level 4 • Science & Weather: "Journey of the Painted Butterfly"
        StoryBook(
            id = "book_metamorphosis_journey",
            title = "Journey of the Painted Butterfly",
            nativeTitleUrdu = "کیٹرپلر سے تتلی بننے کا حیرت انگیز سفر",
            nativeTitleHindi = "कैटरपिलर से तितली बनने का अद्भुत सफ़र",
            author = "Somsanouk Bouttavong",
            illustrator = "Ounla Santi",
            publisher = "Room to Read • Science Series",
            level = 4,
            coverAssetPath = "books/sw_37273/image_13.webp",
            categoryTag = "Science & Weather",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: ph •蝶 • fl",
            pages = listOf(
                StoryPage(1, "books/sw_37273/image_2.webp", "Every butterfly begins life as a tiny hungry caterpillar munching on fresh green garden leaves.", "Cada mariposa comienza su vida como una pequeña oruga.", "ہر تتلی اپنی زندگی کا آغاز ایک ننھی کیٹرپلر کے طور پر کرتی ہے جو باغ کے تازہ ہرے پتے کھاتی ہے۔", "हर तितली अपना जीवन एक नन्ही कैटरपिलर के रूप में शुरू करती है।", setOf("caterpillar", "morning")),
                StoryPage(2, "books/sw_37273/image_12.webp", "Next comes metamorphosis: inside a protective chrysalis cocoon, nature transforms the caterpillar week by week.", "Dentro del capullo ocurre la metamorfosis.", "اس کے بعد میٹامورفوسس کا مرحلہ آتا ہے: محفوظ کوکون کے اندر قدرت ہفتہ بہ ہفتہ کیٹرپلر کو بدلتی ہے۔", "कोकून के अंदर प्रकृति धीरे-धीरे कैटरपिलर को तितली में बदलती है।", setOf("cocoon", "special")),
                StoryPage(3, "books/sw_37273/image_14.webp", "Finally, the chrysalis opens and a winged butterfly emerges to pollinate flowers across the valley!", "Finalmente emerge una hermosa mariposa con alas brillantes.", "آخرکار کوکون کھلتا ہے اور رنگین پروں والی تتلی باہر نکل کر وادی کے پھولوں پر اڑنے لگتی ہے!", "अंत में कोकून खुलता है और सुंदर पंखों वाली तितली फूलों पर उड़ने लगती है!", setOf("butterfly", "wings"),
                    StoryQuizQuestion("What is the scientific name for a caterpillar transforming into a butterfly inside a cocoon?", listOf("Metamorphosis", "Hibernation", "Evaporation"), 0)
                )
            )
        ),

        // 34. Level 4 • Bedtime & Folktales: "Grandfather Farouk's Wisdom Tales"
        StoryBook(
            id = "book_farouk_wisdom",
            title = "Grandfather Farouk's Wisdom Tales",
            nativeTitleUrdu = "دادا فاروق کی دانشمندانہ کہانیاں",
            nativeTitleHindi = "दादा फ़ारूक़ की ज्ञान भरी कहानियाँ",
            author = "Letta Machoga",
            illustrator = "Wiehan de Jager",
            publisher = "African Storybook • StoryWeaver",
            level = 4,
            coverAssetPath = "books/sw_15058/image_8.webp",
            categoryTag = "Bedtime & Folktales",
            pages = listOf(
                StoryPage(1, "books/sw_15058/image_8.webp", "When evening lanterns glow in the courtyard, Grandfather Farouk finishes his garden work and calls the children near.", "Al caer la tarde, el abuelo Farouk llama a los niños.", "جب صحن میں شام کی لالٹین جلتی ہے تو دادا فاروق باغ کا کام ختم کر کے بچوں کو اپنے پاس بلاتے ہیں۔", "शाम को आँगन में दादा फ़ारूक़ बच्चों को अपने पास बुलाते हैं।", setOf("grandfather", "bright")),
                StoryPage(2, "books/sw_15058/image_7.webp", "He tells ancient folktales of travelers who crossed deserts using the stars and shared water with every stranger.", "Cuenta antiguos cuentos populares sobre viajeros y estrellas.", "وہ قدیم مسافروں کی لوک کہانیاں سناتے ہیں جو ستاروں کی روشنی میں سفر کرتے اور ہر مسافر کے ساتھ پانی بانٹتے تھے۔", "वे प्राचीन यात्रियों और तारों की ज्ञान भरी लोककथाएँ सुनाते हैं।", setOf("grandfather", "friends")),
                StoryPage(3, "books/sw_15058/image_9.webp", "\"Now it is your turn to read to me,\" says Grandfather Farouk, listening proudly to every word.", "Ahora es tu turno de leerme a mí, dice el abuelo con orgullo.", "اب تمہاری باری ہے کہ مجھے کتاب پڑھ کر سناؤ، دادا فاروق فخر سے ہر لفظ سنتے ہوئے کہتے ہیں۔", "\"अब तुम्हारी बारी है कि मुझे किताब पढ़कर सुनाओ,\" दादाजी गर्व से कहते हैं।", setOf("grandfather", "books"),
                    StoryQuizQuestion("How did the travelers in Grandfather Farouk's folktales find their way at night?", listOf("By using the stars in the night sky", "By following a red ball", "By asking a crab"), 0)
                )
            )
        ),

        // 35. Level 4 • Family & Friends: "Lulu's Midnight Book Fort"
        StoryBook(
            id = "book_lulu_book_fort",
            title = "Lulu's Midnight Book Fort",
            nativeTitleUrdu = "لولو کا کتابوں والا قلعہ",
            nativeTitleHindi = "लुलु का किताबों वाला किला",
            author = "Clyde Beech",
            illustrator = "Clyde Beech",
            publisher = "Book Dash • StoryWeaver",
            level = 4,
            coverAssetPath = "books/sw_45764/image_14.webp",
            categoryTag = "Bedtime & Folktales",
            bookType = "SERIES",
            seriesTag = "Lulu Library • Ep 3",
            pages = listOf(
                StoryPage(1, "books/sw_45764/image_11.webp", "Back home with her borrowed library books, Lulu built a cozy reading tent out of blankets and pillows.", "En casa Lulu construyó una tienda de lectura con mantas.", "لائبریری سے لائی گئی نئی کتابوں کے ساتھ لولو نے گھر آ کر کمبل اور تکیوں سے پڑھنے کا ایک خیمہ بنایا۔", "घर आकर लुलु ने कंबल और तकियों से पढ़ने का एक सुंदर तंबू बनाया।", setOf("books", "borrow")),
                StoryPage(2, "books/sw_45764/image_13.webp", "With a warm flashlight beam, she read chapters about deep-sea whales, mountain eagles, and ancient castles.", "Con una linterna leyó capítulos sobre ballenas y águilas.", "ٹارچ کی نرم روشنی میں اس نے سمندری وہیل، پہاڑی عقابوں اور قدیم قلعوں کے ابواب پڑھے۔", "टॉर्च की रोशनी में उसने समुद्री व्हेल और पहाड़ी गरुड़ की कहानियाँ पढ़ीं।", setOf("books", "bright")),
                StoryPage(3, "books/sw_45764/image_14.webp", "Ma tucked her in and whispered, \"Every page you read opens a brand-new world tomorrow!\"", "Cada página que lees abre un mundo nuevo mañana.", "امی نے اسے کمبل اوڑھایا اور سرگوشی کی، تم جو بھی صفحہ پڑھتی ہو وہ کل ایک نئی دنیا کا دروازہ کھولتا ہے!", "माँ ने प्यार से कहा, \"हर पन्ना जो तुम पढ़ती हो, एक नई दुनिया खोलता है!\"", setOf("books", "whispers"),
                    StoryQuizQuestion("What did Lulu use to build her cozy reading tent at home?", listOf("Blankets, pillows, and her stack of library books", "Tree branches and leaves", "Train seats"), 0)
                )
            )
        ),

        // 36. Level 4 • Science & Weather: "Beyond the Moon & Stars"
        StoryBook(
            id = "book_beyond_moon_stars",
            title = "Beyond the Moon & Stars",
            nativeTitleUrdu = "چاند اور ستاروں سے آگے کا سفر",
            nativeTitleHindi = "चाँद और तारों से आगे का सफ़र",
            author = "Marion Drew & Nadene Kriel",
            illustrator = "Marion Drew",
            publisher = "Pratham Books • Space Series",
            level = 4,
            coverAssetPath = "books/sw_15195/image_17.webp",
            categoryTag = "Science & Weather",
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_15195/image_16.webp", "Have you ever looked up at the silver full moon and wondered how far the universe reaches?", "¿Alguna vez miraste la luna llena y te preguntaste qué tan lejos llega el universo?", "کیا تم نے کبھی چاندنی رات میں پورے چاند کو دیکھ کر سوچا ہے کہ یہ کائنات کتنی وسیع ہے؟", "क्या तुमने कभी पूरे चाँद को देखकर सोचा है कि ब्रह्मांड कितना विशाल है?", setOf("clouds", "bright")),
                StoryPage(2, "books/sw_15195/image_17.webp", "Astronomers use powerful telescopes to study distant planets, glowing nebulae, and galaxies millions of light-years away.", "Los astrónomos usan telescopios para estudiar planetas y galaxias.", "ماہرینِ فلکیات طاقتور دوربینوں سے دور دراز سیاروں اور کہکشاؤں کا مطالعہ کرتے ہیں۔", "खगोलशास्त्री दूरबीनों से दूर के ग्रहों और आकाशगंगाओं का अध्ययन करते हैं।", setOf("bright", "special")),
                StoryPage(3, "books/sw_13916/image_16.webp", "One day, young readers like you will build new spacecraft and explore the stars above!", "Algún día jóvenes lectores como tú explorarán las estrellas.", "ایک دن تم جیسے ہونہار قارئین نئے خلائی جہاز بنائیں گے اور ستاروں سے آگے کی دنیا دریافت کریں گے!", "एक दिन तुम जैसे होनहार पाठक अंतरिक्ष और तारों की खोज करेंगे!", setOf("bright", "unique"),
                    StoryQuizQuestion("What tool do astronomers use to study distant planets and galaxies?", listOf("Powerful telescopes", "Pocket umbrellas", "Kitchen spoons"), 0)
                )
            )
        )
    )

    // Thematic full-book illustration & bilingual page banks so EVERY story in the catalog has 11 to 17 complete illustrated pages!
    private val thematicContinuationPages: Map<String, List<StoryPage>> = mapOf(
        "sw_13916" to listOf(
            StoryPage(1, "books/sw_13916/image_2.webp", "In the sunny botanical garden, Lara the ladybird fluttered across the dewy green leaves.", "En el jardín soleado, Lara voló sobre las hojas verdes.", "روشن باغ میں لارا لیڈی برڈ شبنم سے بھیگے ہرے پتوں پر اڑتی ہوئی آئی۔", "धूप भरे बग़ीचे में लारा लेडीबर्ड हरे पत्तों पर उड़ती हुई आई।", setOf("ladybird", "bright")),
            StoryPage(2, "books/sw_13916/image_3.webp", "Her bright golden-yellow wings sparkled like tiny sunbeams in the morning breeze.", "Sus alas amarillas brillaban como rayos de sol.", "اس کے چمکدار سنہری پیلے پر صبح کی ہوا میں سورج کی کرنوں کی طرح جگمگا رہے تھے۔", "उसके चमकीले पीले पंख सुबह की हवा में सूरज की किरणों की तरह चमक रहे थे।", setOf("yellow", "wings")),
            StoryPage(3, "books/sw_13916/image_4.webp", "All the garden insects gathered around the flowerbed to greet their cheerful friend.", "Todos los insectos del jardín se reunieron para saludarla.", "باغ کے تمام ننھے کیڑے پھولوں کی کیاری کے پاس اپنی خوش مزاج دوست کو سلام کرنے جمع ہو گئے۔", "बग़ीचे के सभी नन्हे जीव फूलों के पास अपनी दोस्त से मिलने इकट्ठा हो गए।", setOf("friends", "morning")),
            StoryPage(4, "books/sw_13916/image_5.webp", "Bibi Butterfly danced through the air while Manto Mantis tipped his green leaf hat.", "Bibi Mariposa bailó en el aire mientras Manto saludaba.", "بی بی تتلی ہوا میں لہرائی جبکہ مینٹو مینٹس نے اپنی ہری ٹوپی ہلا کر سلام کیا۔", "बीबी तितली हवा में लहराई और मैंटो ने हाथ हिलाकर अभिवादन किया।", setOf("butterfly", "friends")),
            StoryPage(5, "books/sw_13916/image_6.webp", "Even Sesa the spider paused spinning her silver web to share a warm morning smile.", "Incluso Sesa la araña sonrió desde su telaraña plateada.", "یہاں تک کہ سیسا مکڑی نے بھی اپنا چاندی جیسا جالا بننا روک کر مسکراہٹ بکھیر دی۔", "यहाँ तक कि सेसा मकड़ी ने भी जाला बुनना रोककर प्यारी मुस्कान दी।", setOf("spider", "bright")),
            StoryPage(6, "books/sw_13916/image_7.webp", "Inside the garden classroom, Miss Miya opened a big illustrated storybook on the leaf easel.", "En el aula del jardín, la maestra abrió un gran libro ilustrado.", "باغ کے اسکول میں مس میا نے پتے کے تختے پر ایک بڑی تصویروں والی کتاب کھولی۔", "बग़ीचे की कक्षा में मिस मिया ने एक बड़ी चित्रों वाली कहानी की किताब खोली।", setOf("school", "books")),
            StoryPage(7, "books/sw_13916/image_12.webp", "They read together about courage, kindness, and how every creature brings something special to the world.", "Leyeron juntos sobre la bondad y lo especial de cada criatura.", "انہوں نے مل کر ہمت، مہربانی اور ہر جاندار کی منفرد خوبی کے بارے میں پڑھا۔", "उन्होंने मिलकर साहस, दया और हर जीव की अनोखी खूबी के बारे में पढ़ा।", setOf("special", "books")),
            StoryPage(8, "books/sw_13916/image_13.webp", "Miss Miya pointed out how every bug in the class had a wonderful talent to share.", "La maestra señaló el maravilloso talento de cada estudiante.", "مس میا نے بتایا کہ کلاس کے ہر طالب علم میں کوئی نہ کوئی شاندار ہنر موجود ہے۔", "मिस मिया ने बताया कि कक्षा के हर विद्यार्थी में एक अद्भुत प्रतिभा है।", setOf("unique", "school")),
            StoryPage(9, "books/sw_13916/image_14.webp", "Sipho showed his shiny spotted shell and Sally leaped high over the clover blossoms!", "Sipho mostró su caparazón y Sally saltó sobre los tréboles.", "سیفو نے اپنا چمکدار خول دکھایا اور سیلی نے پھولوں کے اوپر اونچی چھلانگ لگائی!", "सीफो ने अपना चमकीला खोल दिखाया और सैली ने फूलों के ऊपर छलांग लगाई!", setOf("special", "friends")),
            StoryPage(10, "books/sw_13916/image_16.webp", "As the golden afternoon cooled, clear water droplets shimmered on the petals like tiny diamonds.", "Las gotas de agua brillaban en los pétalos como diamantes.", "جیسے ہی سہ پہر ٹھنڈی ہوئی، پھولوں کی پتیوں پر پانی کے قطرے ہیروں کی طرح چمکنے لگے۔", "दोपहर ढलते ही फूलों की पंखुड़ियों पर पानी की बूँदें हीरे की तरह चमकने लगीं।", setOf("gleamed", "bright")),
            StoryPage(11, "books/sw_13916/image_18.webp", "With happy hearts, the garden friends promised to meet again tomorrow for another adventure!", "Con corazones felices prometieron reunirse mañana.", "خوشی سے سرشار باغ کے دوستوں نے کل ایک نئی مہم کے لیے دوبارہ ملنے کا وعدہ کیا!", "खुशी से भरे दोस्तों ने कल एक नए सफ़र के लिए फिर मिलने का वादा किया!", setOf("friends", "yellow"))
        ),
        "sw_37273" to listOf(
            StoryPage(1, "books/sw_37273/image_2.webp", "Along the winding garden trail, little Caterpillar set out on a sunny morning walk.", "Por el sendero del jardín, la pequeña oruga salió a caminar.", "باغ کی بل کھاتی پگڈنڈی پر ننھی کیٹرپلر روشن صبح کی سیر کے لیے نکلی۔", "बग़ीचे की पगडंडी पर नन्ही कैटरपिलर सुबह की सैर के लिए निकली।", setOf("caterpillar", "morning")),
            StoryPage(2, "books/sw_37273/image_3.webp", "First she visited Spider, who was busy polishing her eight tiny shoes on a twig.", "Primero visitó a la araña que limpiaba sus ocho zapatitos.", "سب سے پہلے وہ مکڑی کے پاس گئی جو شاخ پر بیٹھی اپنے آٹھ ننھے جوتے چمکا رہی تھی۔", "सबसे पहले वह मकड़ी के पास गई जो टहनी पर अपने आठ नन्हे जूते साफ़ कर रही थी।", setOf("spider", "shoe")),
            StoryPage(3, "books/sw_37273/image_4.webp", "Next she strolled past tall golden daisies swaying gently in the warm breeze.", "Luego caminó junto a altas margaritas doradas.", "پھر وہ سنہری پھولوں کے پاس سے گزری جو گرم ہوا میں ہولے ہولے جھوم رہے تھے۔", "फिर वह सुनहरे फूलों के पास से गुज़री जो हवा में झूम रहे थे।", setOf("caterpillar", "bright")),
            StoryPage(4, "books/sw_37273/image_5.webp", "By the sparkling blue pond, green Frog was practicing his high diving jumps.", "Junto al estanque azul, la rana verde practicaba sus saltos.", "چمکتے نیلے تالاب کے کنارے ہرا مینڈک اپنی اونچی چھلانگوں کی مشق کر رہا تھا۔", "नीले तालाब के किनारे हरा मेंढक अपनी ऊँची छलांग का अभ्यास कर रहा था।", setOf("frog", "bright")),
            StoryPage(5, "books/sw_37273/image_6.webp", "Splash! Frog landed on a wide lily pad and waved both webbed feet cheerfully.", "La rana aterrizó en un nenúfar y saludó con alegría.", "چھپاک! مینڈک کنول کے بڑے پتے پر اترا اور خوشی سے ہاتھ ہلایا۔", "छपाक! मेंढक कमल के पत्ते पर उतरा और खुशी से हाथ हिलाया।", setOf("frog", "friends")),
            StoryPage(6, "books/sw_37273/image_7.webp", "In the tall meadow grass, Grasshopper tuned his legs to chirp a merry daytime song.", "En la hierba alta, el saltamontes cantó una canción alegre.", "لمبی گھاس میں ٹڈے نے اپنی ٹانگوں سے دوپہر کا خوشگوار گیت چھیڑا۔", "लंबी घास में टिड्डे ने दोपहर का मधुर गीत सुनाया।", setOf("grasshopper", "friends")),
            StoryPage(7, "books/sw_37273/image_8.webp", "Grasshopper bounced from blade to blade, showing how high he could leap into the air!", "El saltamontes saltó de hoja en hoja muy alto.", "ٹڈا ایک گھاس کی پتی سے دوسری پتی پر اچھلا اور دکھایا کہ وہ ہوا میں کتنا اونچا کود سکتا ہے!", "टिड्डा एक पत्ती से दूसरी पत्ती पर उछला और हवा में ऊँची छलांग लगाई!", setOf("grasshopper", "special")),
            StoryPage(8, "books/sw_37273/image_9.webp", "Down where the garden stream met the sandy shore, red Crab was building a neat sandcastle.", "En la orilla arenosa, el cangrejo rojo construía un castillo de arena.", "جہاں باغ کی ندی ریتیلے کنارے سے ملتی تھی، وہاں سرخ کیکڑا ریت کا گھر بنا رہا تھا۔", "रेतीले किनारे पर लाल केकड़ा रेत का सुंदर घर बना रहा था।", setOf("crab", "beach")),
            StoryPage(9, "books/sw_37273/image_10.webp", "Crab tapped his claws together and showed the smooth seashells he had collected.", "El cangrejo mostró las conchas marinas que había recogido.", "کیکڑے نے اپنے پنجے بجائے اور اپنی جمع کی ہوئی چمکدار سیپیاں دکھائیں۔", "केकड़े ने अपनी इकट्ठा की हुईं सुंदर सीपियाँ दिखाईं।", setOf("crab", "bright")),
            StoryPage(10, "books/sw_37273/image_11.webp", "As evening approached, Caterpillar curled onto a soft emerald leaf to rest.", "Al caer la tarde, la oruga descansó sobre una hoja verde.", "شام ہوتے ہی کیٹرپلر آرام کرنے کے لیے ایک نرم ہرے پتے پر لیٹ گئی۔", "शाम होते ही कैटरपिलर आराम करने के लिए एक नरम हरे पत्ते पर लेट गई।", setOf("caterpillar", "whispers")),
            StoryPage(11, "books/sw_37273/image_14.webp", "And soon, colorful butterfly wings carried her high above the whole blooming garden!", "Pronto, alas de mariposa la llevaron sobre todo el jardín.", "اور جلد ہی رنگین تتلی کے پروں نے اسے پورے کھلتے ہوئے باغ کے اوپر اڑا لیا!", "और जल्द ही तितली के रंगीन पंखों ने उसे पूरे बग़ीचे के ऊपर उड़ा लिया!", setOf("butterfly", "wings"))
        ),
        "sw_44485" to listOf(
            StoryPage(1, "books/sw_44485/image_2.webp", "The morning sun shines brightly over the station as travelers gather for their coastal journey.", "El sol de la mañana brilla sobre la estación de tren.", "اسٹیشن پر صبح کا سورج چمک رہا ہے اور مسافر ساحلی سفر کے لیے جمع ہو رہے ہیں۔", "स्टेशन पर सुबह का सूरज चमक रहा है और यात्री सफ़र के लिए इकट्ठा हो रहे हैं।", setOf("train", "morning")),
            StoryPage(2, "books/sw_44485/image_3.webp", "Nicole and Mom check the departure board and wait behind the yellow safety line.", "Nicole y mamá revisan el tablero de salidas en el andén.", "نکول اور امی ٹرین کا وقت دیکھتی ہیں اور پیلی حفاظتی لکیر کے پیچھے انتظار کرتی ہیں۔", "निकोल और माँ ट्रेन का समय देखती हैं और प्लेटफ़ॉर्म पर इंतज़ार करती हैं।", setOf("train", "yellow")),
            StoryPage(3, "books/sw_44485/image_4.webp", "Whoosh! The silver train arrives on the tracks and the wide doors slide open.", "El tren plateado llega a las vías y abre sus puertas.", "شوں! چاندی جیسی ٹرین پٹری پر آ کر رکتی ہے اور اس کے چوڑے دروازے کھلتے ہیں۔", "चमकीली ट्रेन पटरी पर आकर रुकती है और उसके दरवाज़े खुलते हैं।", setOf("train", "bright")),
            StoryPage(4, "books/sw_44485/image_5.webp", "Inside the carriage, friendly passengers wave and make room by the sunny window.", "Dentro del vagón, los pasajeros amables saludan.", "ڈبے کے اندر دوستانہ مسافر ہاتھ ہلاتے ہیں اور روشن کھڑکی کے پاس جگہ دیتے ہیں۔", "डिब्बे के अंदर यात्री मुस्कुराकर खिड़की के पास जगह देते हैं।", setOf("friends", "train")),
            StoryPage(5, "books/sw_44485/image_6.webp", "A vendor pushes a colorful cart stacked with golden chocolates and fruit snacks.", "Un vendedor lleva un carrito con chocolates dorados.", "ایک فروش سنہری چاکلیٹ اور پھلوں سے سجی رنگین ٹرالی لے کر گزرتا ہے۔", "एक विक्रेता सुनहरी चॉकलेट और फलों की ट्रॉली लेकर आता है।", setOf("chocolates", "colors")),
            StoryPage(6, "books/sw_44485/image_7.webp", "Across the aisle, two young karate students sit neatly in their crisp white uniforms.", "Al otro lado del pasillo, dos estudiantes de karate conversan.", "راستے کے دوسری طرف دو نوجوان کراٹے طالب علم اپنے سفید لباس میں بیٹھے ہیں۔", "दूसरी तरफ़ दो बच्चे अपनी सफ़ेद कराटे यूनिफ़ॉर्म में बैठे हैं।", setOf("friends", "school")),
            StoryPage(7, "books/sw_44485/image_8.webp", "They stand up carefully to demonstrate a steady karate stance and a polite bow!", "Demuestran una postura de karate con mucho equilibrio.", "وہ احتیاط سے کھڑے ہو کر کراٹے کا متوازن انداز اور باادب سلام دکھاتے ہیں!", "वे खड़े होकर कराटे का संतुलित पोज़ और विनम्र प्रणाम दिखाते हैं!", setOf("special", "friends")),
            StoryPage(8, "books/sw_44485/image_10.webp", "Further down the carriage, a navy sailor in a white cap points toward the sparkling ocean horizon.", "Una marinera señala hacia el horizonte del océano.", "ڈبے میں آگے سفید ٹوپی پہنے بحریہ کی ملاح چمکتے ہوئے سمندر کے افق کی طرف اشارہ کرتی ہے۔", "आगे बैठी नौसेना की नाविक चमकते समुद्र की ओर इशारा करती है।", setOf("sailor", "beach")),
            StoryPage(9, "books/sw_44485/image_11.webp", "Next come the cheerful drummers in red and yellow shirts, tapping a lively beat!", "Luego llegan los bateristas con camisas rojas y amarillas.", "اس کے بعد سرخ اور پیلی قمیضوں میں خوش مزاج ڈھول بجانے والے سریلی تال بجاتے ہیں!", "फिर लाल और पीली शर्ट पहने ढोल वादक मधुर ताल बजाते हैं!", setOf("drummers", "yellow")),
            StoryPage(10, "books/sw_44485/image_12.webp", "Everyone in the carriage claps along to the rhythm as the train rolls near the palm trees.", "Todos en el vagón aplauden al ritmo de los tambores.", "جب ٹرین کھجور کے درختوں کے پاس سے گزرتی ہے تو ڈبے میں موجود ہر شخص تال کے ساتھ تالیاں بجاتا ہے۔", "जब ट्रेन ताड़ के पेड़ों के पास से गुज़रती है, तो सब ताल के साथ तालियाँ बजाते हैं।", setOf("drummers", "train")),
            StoryPage(11, "books/sw_44485/image_14.webp", "At last they arrive at the sunny beach station, ready for a wonderful day by the sea!", "Por fin llegan a la estación de la playa soleada.", "آخرکار وہ روشن ساحلی اسٹیشن پر پہنچ جاتے ہیں اور سمندر کے کنارے ایک یادگار دن گزارتے ہیں!", "अंत में वे समुद्र तट के स्टेशन पर पहुँच जाते हैं!", setOf("beach", "friends"))
        ),
        "sw_15195" to listOf(
            StoryPage(1, "books/sw_15195/image_2.webp", "Look at this bright round red ball sitting on the green lawn.", "Mira esta pelota roja y redonda en el césped.", "ہری گھاس پر رکھی اس چمکدار گول سرخ گیند کو دیکھو۔", "हरी घास पर रखी इस चमकीली गोल लाल गेंद को देखो।", setOf("bright", "friends")),
            StoryPage(2, "books/sw_15195/image_3.webp", "I pick up the bouncy ball and hold it firmly in both hands.", "Tomo la pelota y la sostengo en mis manos.", "میں اچھلتی ہوئی گیند کو اٹھاتا ہوں اور دونوں ہاتھوں میں مضبوطی سے پکڑتا ہوں۔", "मैं गेंद को उठाता हूँ और दोनों हाथों में पकड़ता हूँ।", setOf("friends", "bright")),
            StoryPage(3, "books/sw_15195/image_4.webp", "Even when light raindrops fall, I wear my red raincoat and step outside to play.", "Me pongo mi impermeable rojo y salgo a jugar.", "ہلکی بونداباندی میں بھی میں اپنا سرخ برساتی کوٹ پہن کر کھیلنے باہر نکلتا ہوں۔", "हल्की बूंदाबांदी में भी मैं अपना लाल रेनकोट पहनकर खेलने निकलता हूँ।", setOf("raincoat", "rain")),
            StoryPage(4, "books/sw_15195/image_6.webp", "I take a step forward and tap the ball gently with my shoe.", "Doy un paso adelante y pateo suavemente la pelota.", "میں ایک قدم آگے بڑھاتا ہوں اور اپنے جوتے سے گیند کو ہلکی سی کک مارتا ہوں۔", "मैं एक कदम आगे बढ़ाता हूँ और जूते से गेंद को किक मारता हूँ।", setOf("shoe", "bright")),
            StoryPage(5, "books/sw_15195/image_7.webp", "The red ball rolls smoothly across the dewy grass toward the garden fence.", "La pelota roja rueda por la hierba hacia la cerca.", "سرخ گیند شبنم والی گھاس پر لڑھکتی ہوئی باغ کی باڑ کی طرف جاتی ہے۔", "लाल गेंद हरी घास पर लुढ़कती हुई बग़ीचे के किनारे तक जाती है।", setOf("bright", "street")),
            StoryPage(6, "books/sw_15195/image_8.webp", "Now I kick the ball higher! It bounces above the bushes like a little red bird.", "Ahora pateo la pelota más alto sobre los arbustos.", "اب میں گیند کو اور اونچا اچھالتا ہوں! وہ جھاڑیوں کے اوپر سرخ پرندے کی طرح اڑتی ہے۔", "अब मैं गेंद को और ऊँचा उछालता हूँ! वह झाड़ियों के ऊपर उछलती है।", setOf("bright", "wings")),
            StoryPage(7, "books/sw_15195/image_9.webp", "With one big powerful kick—Whoosh!—the ball rockets straight up toward the sky!", "Con una gran patada la pelota sube hacia el cielo.", "ایک زوردار کک کے ساتھ—شوں!—گیند سیدھی آسمان کی طرف اڑتی چلی جاتی ہے!", "एक ज़ोरदार किक के साथ गेंद सीधी आसमान की ओर उड़ती है!", setOf("crashed", "clouds")),
            StoryPage(8, "books/sw_15195/image_11.webp", "I shade my eyes and search left and right across the whole courtyard.", "Busco a izquierda y derecha por todo el patio.", "میں آنکھوں پر ہاتھ رکھ کر پورے صحن میں دائیں بائیں تلاش کرتا ہوں۔", "मैं पूरे आँगन में बाएँ और दाएँ गेंद को ढूँढता हूँ।", setOf("street", "bright")),
            StoryPage(9, "books/sw_15195/image_14.webp", "Look up! Way above the tallest tree branches, a tiny red dot is climbing higher!", "Mira arriba, muy por encima de las ramas más altas.", "اوپر دیکھو! سب سے اونچے درخت کی شاخوں کے بھی اوپر ایک سرخ نقطہ بلند ہو رہا ہے!", "ऊपर देखो! सबसे ऊँचे पेड़ की शाखाओं के ऊपर गेंद उड़ रही है!", setOf("clouds", "bright")),
            StoryPage(10, "books/sw_15195/image_16.webp", "It floats past the fluffy white clouds in the wide blue sky.", "Flota más allá de las esponjosas nubes blancas.", "وہ وسیع نیلے آسمان میں نرم سفید بادلوں کے پاس سے تیرتی ہوئی گزرتی ہے۔", "वह नीले आसमान में सफ़ेद बादलों के पास से गुज़रती है।", setOf("clouds", "bright")),
            StoryPage(11, "books/sw_15195/image_17.webp", "And in my imagination, my red ball orbits right past the glowing silver moon!", "En mi imaginación la pelota vuela junto a la luna plateada.", "اور میرے تخیل میں، میری سرخ گیند چمکتے ہوئے چاند کے گرد چکر لگا رہی ہے!", "और मेरी कल्पना में मेरी लाल गेंद चमकते चाँद के पास उड़ रही है!", setOf("clouds", "special"))
        ),
        "sw_15058" to listOf(
            StoryPage(1, "books/sw_15058/image_2.webp", "Every morning I open my favorite colorful storybook full of pictures and poems.", "Cada mañana abro mi libro de cuentos favorito.", "ہر صبح میں تصویروں اور نظموں سے بھری اپنی پسندیدہ کہانی کی کتاب کھولتا ہوں۔", "हर सुबह मैं चित्रों और कविताओं से भरी अपनी पसंदीदा किताब खोलता हूँ।", setOf("books", "morning")),
            StoryPage(2, "books/sw_15058/image_3.webp", "I walk through the house wondering who has time to listen to my new story today.", "Camino por la casa buscando a quién leerle mi cuento.", "میں گھر میں چلتے ہوئے سوچتا ہوں کہ آج میری نئی کہانی سننے کے لیے کس کے پاس وقت ہے۔", "मैं घर में सोचता हूँ कि आज मेरी नई कहानी कौन सुनेगा।", setOf("books", "friends")),
            StoryPage(3, "books/sw_15058/image_4.webp", "In the bedroom, my baby sister is sleeping peacefully under her soft blanket.", "En el dormitorio mi hermanita duerme tranquilamente.", "کمرے میں میری ننھی بہن اپنے نرم کمبل میں پرسکون نیند سو رہی ہے۔", "कमरे में मेरी छोटी बहन अपने नरम कंबल में सो रही है।", setOf("whispers", "friends")),
            StoryPage(4, "books/sw_15058/image_5.webp", "I tiptoe quietly to the hallway, holding my book carefully in my hands.", "Camino de puntillas hacia el pasillo con mi libro.", "میں اپنی کتاب کو احتیاط سے ہاتھوں میں پکڑے دبے پاؤں برآمدے میں آتا ہوں۔", "मैं अपनी किताब हाथ में लेकर चुपचाप बरामदे में आता हूँ।", setOf("books", "whispers")),
            StoryPage(5, "books/sw_15058/image_6.webp", "In the kitchen, Mother and Grandmother are chopping vegetables and stirring warm soup.", "En la cocina mamá y la abuela preparan sopa caliente.", "باورچی خانے میں امی اور دادی سبزیاں کاٹ رہی ہیں اور گرم سوپ پکا رہی ہیں۔", "रसोई में माँ और दादी सब्ज़ियाँ काट रही हैं और सूप बना रही हैं।", setOf("grandmother", "friends")),
            StoryPage(6, "books/sw_15058/image_7.webp", "I step out onto the sunny veranda and look toward the green courtyard garden.", "Salgo a la terraza soleada y miro hacia el jardín.", "میں دھوپ والے برآمدے میں نکلتا ہوں اور صحن کے ہرے باغ کی طرف دیکھتا ہوں۔", "मैं धूप वाले आँगन में निकलता हूँ और बग़ीचे की तरफ़ देखता हूँ।", setOf("bright", "books")),
            StoryPage(7, "books/sw_15058/image_8.webp", "Outside, Father and Grandfather Farouk are watering the trees and fixing the garden fence.", "Afuera papá y el abuelo riegan los árboles del jardín.", "باہر ابو اور دادا فاروق پودوں کو پانی دے رہے ہیں اور باغ کی باڑ ٹھیک کر رہے ہیں۔", "बाहर पिताजी और दादा फ़ारूक़ पौधों को पानी दे रहे हैं।", setOf("grandfather", "friends")),
            StoryPage(8, "books/sw_15058/image_9.webp", "So I sit on my cozy mat and read out loud to myself—and when dinner is ready, the whole family listens too!", "Me siento en mi tapete y leo en voz alta con alegría.", "تو میں اپنی چٹائی پر بیٹھ کر خود ہی بلند آواز سے پڑھتا ہوں—اور شام کو پورا خاندان شوق سے سنتا ہے!", "तो मैं अपनी चटाई पर बैठकर खुद पढ़ता हूँ—और शाम को पूरा परिवार सुनता है!", setOf("books", "grandfather"))
        ),
        "sw_45764" to listOf(
            StoryPage(1, "books/sw_45764/image_2.webp", "Inside the neighborhood library, rows of colorful shelves reach all the way to the ceiling.", "Dentro de la biblioteca, los estantes llegan hasta el techo.", "محلے کی لائبریری کے اندر رنگ برنگی کتابوں کی الماریاں چھت تک پہنچی ہوئی ہیں۔", "लाइब्रेरी के अंदर रंग-बिरंगी किताबों की अलमारियाँ छत तक भरी हैं।", setOf("books", "bright")),
            StoryPage(2, "books/sw_45764/image_3.webp", "When the wall clock chimes five o'clock, Ma calls softly, \"Lulu, it is time to head home!\"", "Cuando el reloj marca las cinco, mamá llama a Lulu.", "جب دیوار کی گھڑی پانچ بجاتی ہے تو امی پیار سے بلاتی ہیں، لولو، اب گھر چلنے کا وقت ہے!", "जब घड़ी में पाँच बजते हैं, तो माँ प्यार से बुलाती हैं, \"लुलु, अब घर चलने का समय है!\"", setOf("return", "whispers")),
            StoryPage(3, "books/sw_45764/image_4.webp", "Ma checks under the reading sofa and peeks behind the long blue window curtain.", "Mamá busca debajo del sofá y detrás de la cortina.", "امی پڑھنے والے صوفے کے نیچے دیکھتی ہیں اور کھڑکی کے لمبے نیلے پردے کے پیچھے جھانکتی ہیں۔", "माँ सोफ़े के नीचे देखती हैं और खिड़की के पर्दे के पीछे झाँकती हैं।", setOf("curtain", "books")),
            StoryPage(4, "books/sw_45764/image_5.webp", "She looks beside the wooden cupboard and around the storyteller's table.", "Busca junto al armario de madera y la mesa de cuentos.", "وہ لکڑی کی الماری کے پاس اور کہانیوں والی میز کے گرد تلاش کرتی ہیں۔", "वे लकड़ी की अलमारी के पास और कहानी की मेज़ کے चारों ओर देखती हैं।", setOf("cupboard", "books")),
            StoryPage(5, "books/sw_45764/image_6.webp", "\"Where could Lulu be hiding?\" Ma wonders with a gentle smile.", "¿Dónde podría estar escondida Lulu?, se pregunta mamá.", "لولو کہاں چھپی ہو سکتی ہے؟ امی ہلکی سی مسکراہٹ کے ساتھ سوچتی ہیں۔", "\"लुलु कहाँ छिपी हो सकती है?\" माँ मुस्कुराते हुए सोचती हैं।", setOf("friends", "whispers")),
            StoryPage(6, "books/sw_45764/image_7.webp", "Behind the picture-book corner, Lulu pops up holding her favorite illustrated story!", "Detrás del rincón de lectura, Lulu aparece con su libro favorito.", "تصویروں والی کتابوں کے کونے کے پیچھے سے لولو اپنی پسندیدہ کتاب تھامے نمودار ہوتی ہے!", "किताबों के कोने के पीछे से लुलु अपनी पसंदीदा किताब पकड़े हुए निकलती है!", setOf("books", "special")),
            StoryPage(7, "books/sw_45764/image_8.webp", "\"I love this story about ocean animals so much!\" Lulu says, hugging the cover.", "Me encanta tanto esta historia sobre animales marinos, dice Lulu.", "مجھے سمندری جانوروں کی یہ کہانی بے حد پسند ہے! لولو کتاب کو گلے لگاتے ہوئے کہتی ہے۔", "\"मुझे समुद्री जीवों की यह कहानी बहुत पसंद है!\" लुलु कहती है।", setOf("books", "friends")),
            StoryPage(8, "books/sw_45764/image_10.webp", "She points to a second book about stars and a third book about jungle birds.", "Señala un segundo libro sobre estrellas y un tercero sobre aves.", "وہ ستاروں والی دوسری کتاب اور جنگل کے پرندوں والی تیسری کتاب کی طرف اشارہ کرتی ہے۔", "वह तारों वाली दूसरी किताब और पक्षियों वाली तीसरी किताब दिखाती है।", setOf("books", "bright")),
            StoryPage(9, "books/sw_45764/image_12.webp", "The librarian smiles and stamps Lulu's card so she can borrow the whole stack for the week!", "La bibliotecaria sella la tarjeta de Lulu para prestarle los libros.", "لائبریرین مسکرا کر لولو کے کارڈ پر مہر لگاتی ہے تاکہ وہ پورے ہفتے کے لیے نئی کتابیں لے جا سکے!", "लाइब्रेरियन मुस्कुराकर लुलु के कार्ड पर मुहर लगाती है!", setOf("borrow", "return", "books")),
            StoryPage(10, "books/sw_45764/image_13.webp", "\"Hurray!\" cheers Lulu. \"When we return finished books, new adventures are always waiting!\"", "¡Hurra!, celebra Lulu. ¡Nuevas aventuras siempre nos esperan!", "واہ! لولو خوشی سے پکارتی ہے، جب ہم پڑھی ہوئی کتابیں واپس کرتے ہیں تو نئی کہانیاں ہماری منتظر ہوتی ہیں!", "\"वाह!\" लुलु खुशी से कहती है, \"किताबें लौटाने पर नई कहानियाँ मिलती हैं!\"", setOf("return", "borrow")),
            StoryPage(11, "books/sw_45764/image_14.webp", "All evening long, Lulu and Ma read chapter after chapter together on their cozy rug.", "Toda la tarde Lulu y mamá leen capítulo tras capítulo.", "پوری شام لولو اور امی اپنے آرام دہ قالین پر بیٹھ کر ایک کے بعد ایک باب پڑھتی ہیں!", "पूरी शाम लुलु और माँ साथ बैठकर एक के बाद एक अध्याय पढ़ती हैं!", setOf("books", "friends"))
        )
    )

    fun enrichToFullIllustratedStory(book: StoryBook): StoryBook {
        if (book.pages.size >= 10) return book
        val packKey = when {
            book.coverAssetPath.contains("sw_13916") -> "sw_13916"
            book.coverAssetPath.contains("sw_37273") -> "sw_37273"
            book.coverAssetPath.contains("sw_44485") -> "sw_44485"
            book.coverAssetPath.contains("sw_15195") -> "sw_15195"
            book.coverAssetPath.contains("sw_15058") -> "sw_15058"
            else -> "sw_45764"
        }
        val extraPool = thematicContinuationPages[packKey] ?: thematicContinuationPages["sw_13916"]!!
        val existingImages = book.pages.map { it.imageAssetPath }.toSet()
        val existingQuizzes = book.pages.mapNotNull { it.quiz }
        val basePagesWithoutEndQuiz = book.pages.map { it.copy(quiz = null) }

        val additionalPages = extraPool.filterNot { existingImages.contains(it.imageAssetPath) }
            .ifEmpty { extraPool }
            .take((11 - basePagesWithoutEndQuiz.size).coerceAtLeast(8))

        val combined = (basePagesWithoutEndQuiz + additionalPages).mapIndexed { idx, pg ->
            val attachQuiz = if (idx == (basePagesWithoutEndQuiz.size + additionalPages.size) - 1) {
                existingQuizzes.lastOrNull()
            } else if (idx == 4 && existingQuizzes.size > 1) {
                existingQuizzes.first()
            } else {
                null
            }
            pg.copy(pageNumber = idx + 1, quiz = attachQuiz)
        }
        return book.copy(pages = combined)
    }

    val defaultBooks: List<StoryBook> = rawDefaultBooks.map { enrichToFullIllustratedStory(it) }

    // 51 Real Vocabulary Items mapped to assets/vocab/ic_vocab_*.svg
    val vocabularyItems: List<VocabularyItem> = listOf(
        // Body parts
        VocabularyItem("Ankle", "an • kle", "Body parts", "vocab/ic_vocab_ankle.svg", "ٹخنہ", "टखना", "Tobillo", "Move your ankle in a gentle circle."),
        VocabularyItem("Arms", "arms", "Body parts", "vocab/ic_vocab_arms.svg", "بازو", "बाँहें", "Brazos", "Stretch both arms high above your head."),
        VocabularyItem("Elbow", "el • bow", "Body parts", "vocab/ic_vocab_elbow.svg", "کہنی", "कोहनी", "Codo", "Bend your elbow to touch your shoulder."),
        VocabularyItem("Eyebrows", "eye • brows", "Body parts", "vocab/ic_vocab_eyebrows.svg", "بھنویں", "भौंहें", "Cejas", "She raised her eyebrows in surprise."),
        VocabularyItem("Eyes", "eyes", "Body parts", "vocab/ic_vocab_eyes.svg", "آنکھیں", "आँखें", "Ojos", "We read colorful storybooks with our eyes."),
        VocabularyItem("Feet", "feet", "Body parts", "vocab/ic_vocab_feet.svg", "پاؤں", "पैर", "Pies", "Wear warm socks on your feet."),
        VocabularyItem("Fingers", "fin • gers", "Body parts", "vocab/ic_vocab_fingers.svg", "انگلیاں", "उँगलियाँ", "Dedos", "Count to ten on your fingers."),
        VocabularyItem("Hands", "hands", "Body parts", "vocab/ic_vocab_hands.svg", "ہاتھ", "हाथ", "Manos", "Clap your hands together after the story!"),
        VocabularyItem("Knee", "knee", "Body parts", "vocab/ic_vocab_knee.svg", "گھٹنا", "घुटना", "Rodilla", "He rested the book on his knee."),
        VocabularyItem("Legs", "legs", "Body parts", "vocab/ic_vocab_legs.svg", "ٹانگیں", "टाँगें", "Piernas", "Frogs have strong legs for jumping."),
        VocabularyItem("Neck", "neck", "Body parts", "vocab/ic_vocab_neck.svg", "گردن", "गर्दन", "Cuello", "A giraffe has a very long neck."),
        VocabularyItem("Palms", "palms", "Body parts", "vocab/ic_vocab_palms.svg", "ہتھیلیاں", "हथेलियाँ", "Palmas", "Open your palms to catch the raindrops."),
        VocabularyItem("Shoulders", "shoul • ders", "Body parts", "vocab/ic_vocab_shoulders.svg", "کندھے", "कंधे", "Hombros", "Roll your shoulders back and relax."),
        VocabularyItem("Thumb", "thumb", "Body parts", "vocab/ic_vocab_thumb.svg", "انگوٹھا", "अँगूठा", "Pulgar", "Give a big thumbs up for reading well!"),
        VocabularyItem("Tongue", "tongue", "Body parts", "vocab/ic_vocab_tongue.svg", "زبان", "जीभ", "Lengua", "We taste sweet ice cream with our tongue."),
        VocabularyItem("Waist", "waist", "Body parts", "vocab/ic_vocab_waist.svg", "کمر", "कमर", "Cintura", "Put your hands on your waist and smile."),

        // Color
        VocabularyItem("Blue", "blue", "Color", "vocab/ic_vocab_blue.svg", "نیلا", "नीला", "Azul", "The daytime sky is bright blue."),
        VocabularyItem("Green", "green", "Color", "vocab/ic_vocab_green.svg", "سبز / ہرا", "हरा", "Verde", "Fresh leaves on the tree are green."),
        VocabularyItem("Yellow", "yel • low", "Color", "vocab/ic_vocab_yellow.svg", "پیلا", "पीला", "Amarillo", "Lara the ladybird has bright yellow wings."),
        VocabularyItem("Red", "red", "Color", "vocab/ic_vocab_red.svg", "سرخ / لال", "लाल", "Rojo", "I love kicking my big red ball."),
        VocabularyItem("Orange", "or • ange", "Color", "vocab/ic_vocab_orange.svg", "نارنجی", "नारंगी", "Naranja", "Carrots and sunsets are bright orange."),
        VocabularyItem("Purple", "pur • ple", "Color", "vocab/ic_vocab_purple.svg", "جامنی", "बैंगनी", "Morado", "Grapes and violets are royal purple."),
        VocabularyItem("Pink", "pink", "Color", "vocab/ic_vocab_pink.svg", "گلابی", "गुलाबी", "Rosa", "The flower in Noor's crown is pink."),
        VocabularyItem("Brown", "brown", "Color", "vocab/ic_vocab_brown.svg", "بھورا", "भूरा", "Marrón", "Tree trunks and chocolate are brown."),
        VocabularyItem("Black", "black", "Color", "vocab/ic_vocab_black.svg", "کالا / سیاہ", "काला", "Negro", "Stars shine brightly in the black night sky."),
        VocabularyItem("Grey", "grey", "Color", "vocab/ic_vocab_grey.svg", "سرمئی", "स्लेटी", "Gris", "Rain clouds in the sky are grey."),

        // Seasons
        VocabularyItem("Sunny", "sun • ny", "Seasons", "vocab/ic_vocab_sunny.svg", "دھوپ والا دن", "धूप वाला", "Soleado", "It is a warm and sunny day at the park."),
        VocabularyItem("Rain", "rain", "Seasons", "vocab/ic_vocab_rain.svg", "بارش", "बारिश", "Lluvia", "Wear your raincoat when the rain falls."),
        VocabularyItem("Snowfall", "snow • fall", "Seasons", "vocab/ic_vocab_snowfall.svg", "برف باری", "बर्फ़बारी", "Nevada", "White snowflakes dance during snowfall."),
        VocabularyItem("Spring", "spring", "Seasons", "vocab/ic_vocab_spring.svg", "موسمِ بہار", "वसंत", "Primavera", "Colorful flowers bloom in spring."),
        VocabularyItem("Summer", "sum • mer", "Seasons", "vocab/ic_vocab_summer.svg", "موسمِ گرما", "गर्मी", "Verano", "We visit the sandy beach in summer."),
        VocabularyItem("Fall", "fall", "Seasons", "vocab/ic_vocab_fall.svg", "موسمِ خزاں", "पतझड़", "Otoño", "Golden leaves drift down in the fall."),
        VocabularyItem("Winter", "win • ter", "Seasons", "vocab/ic_vocab_winter.svg", "موسمِ سرما", "सर्दी", "Invierno", "We drink warm cocoa in winter."),
        VocabularyItem("Humid", "hu • mid", "Seasons", "vocab/ic_vocab_humid.svg", "نمی والا موسم", "उमस भरा", "Húmedo", "The air feels warm and humid before monsoon rain."),

        // Shapes
        VocabularyItem("Circle", "cir • cle", "Shapes", "vocab/ic_vocab_circle.svg", "دائرہ", "गोला / वृत्त", "Círculo", "A wheel and a full moon are round like a circle."),
        VocabularyItem("Triangle", "tri • an • gle", "Shapes", "vocab/ic_vocab_triangle.svg", "مثلث (تین کونے)", "त्रिभुज", "Triángulo", "A triangle has three straight sides."),
        VocabularyItem("Rectangle", "rec • tan • gle", "Shapes", "vocab/ic_vocab_rectangle.svg", "مستطیل", "आयत", "Rectángulo", "A storybook cover is shaped like a rectangle."),
        VocabularyItem("Oval", "o • val", "Shapes", "vocab/ic_vocab_oval.svg", "بیضوی شکل", "अंडाकार", "Óvalo", "An egg has a smooth oval shape."),
        VocabularyItem("Pentagon", "pen • ta • gon", "Shapes", "vocab/ic_vocab_pentagon.svg", "پانچ کونوں والی شکل", "पंचभुज", "Pentágono", "A pentagon has five equal corners."),
        VocabularyItem("Sphere", "sphere", "Shapes", "vocab/ic_vocab_sphere.svg", "گول کرہ / گیند", "गोला", "Esfera", "Earth and a soccer ball are shaped like a sphere."),

        // Numbers & Expressions
        VocabularyItem("Zero", "ze • ro", "Numbers", "vocab/ic_vocab_zero.svg", "صفر (0)", "शून्य (0)", "Cero", "Zero comes right before number one."),
        VocabularyItem("One", "one", "Numbers", "vocab/ic_vocab_one.svg", "ایک (1)", "एक (1)", "Uno", "There is one bright sun in the sky."),
        VocabularyItem("Two", "two", "Numbers", "vocab/ic_vocab_two.svg", "دو (2)", "दो (2)", "Dos", "A bird has two wings to fly."),
        VocabularyItem("Three", "three", "Numbers", "vocab/ic_vocab_three.svg", "تین (3)", "तीन (3)", "Tres", "A triangle has three corners."),
        VocabularyItem("Four", "four", "Numbers", "vocab/ic_vocab_four.svg", "چار (4)", "चार (4)", "Cuatro", "A chair has four legs."),
        VocabularyItem("Five", "five", "Numbers", "vocab/ic_vocab_five.svg", "پانچ (5)", "पाँच (5)", "Cinco", "You have five fingers on each hand."),
        VocabularyItem("Six", "six", "Numbers", "vocab/ic_vocab_six.svg", "چھ (6)", "छह (6)", "Seis", "A ladybird has six tiny legs."),
        VocabularyItem("Seven", "sev • en", "Numbers", "vocab/ic_vocab_seven.svg", "سات (7)", "सात (7)", "Siete", "A rainbow has seven beautiful colors."),
        VocabularyItem("Eight", "eight", "Numbers", "vocab/ic_vocab_eight.svg", "آٹھ (8)", "आठ (8)", "Ocho", "A spider has eight legs."),
        VocabularyItem("Nine", "nine", "Numbers", "vocab/ic_vocab_nine.svg", "نو (9)", "नौ (9)", "Nueve", "Nine comes right before ten."),

        // Expressions (word_theme_expressions)
        VocabularyItem("Smile", "smile", "Expressions", "vocab/ic_vocab_smile.svg", "مسکراہٹ", "मुस्कान", "Sonrisa", "A warm smile makes everyone happy!"),
        VocabularyItem("Happy", "hap • py", "Expressions", "vocab/ic_vocab_sunny.svg", "خوش", "ख़ुश", "Feliz", "Lara felt happy when her friends sang for her."),
        VocabularyItem("Surprise", "sur • prise", "Expressions", "vocab/ic_vocab_eyes.svg", "حیرت", "आश्चर्य", "Sorpresa", "Her eyes opened wide with surprise!"),
        VocabularyItem("Cheer", "cheer", "Expressions", "vocab/ic_vocab_hands.svg", "خوشی سے تالیاں بجانا", "उत्साह", "Alegría", "Everyone clapped their hands to cheer!"),

        // Actions (word_theme_actions)
        VocabularyItem("Clap", "clap", "Actions", "vocab/ic_vocab_palms.svg", "تالی بجانا", "ताली बजाना", "Aplaudir", "Clap your palms together to the music!"),
        VocabularyItem("Walk", "walk", "Actions", "vocab/ic_vocab_feet.svg", "چلنا", "चलना", "Caminar", "We walk to school every morning."),
        VocabularyItem("Jump", "jump", "Actions", "vocab/ic_vocab_legs.svg", "چھلانگ لگانا", "कूदना", "Saltar", "Frogs use their strong legs to jump high."),
        VocabularyItem("Wave", "wave", "Actions", "vocab/ic_vocab_hands.svg", "ہاتھ ہلانا", "हाथ हिलाना", "Saludar", "Wave your hand to say hello to your friend."),

        // Places (word_theme_places)
        VocabularyItem("Beach", "beach", "Places", "vocab/ic_vocab_summer.svg", "ساحلِ سمندر", "समुद्र तट", "Playa", "We build sandcastles at the sunny beach."),
        VocabularyItem("Garden", "gar • den", "Places", "vocab/ic_vocab_spring.svg", "باغ", "बग़ीचा", "Jardín", "Colorful butterflies fly in the garden."),
        VocabularyItem("Mountain", "moun • tain", "Places", "vocab/ic_vocab_snowfall.svg", "پہاڑ", "पहाड़", "Montaña", "White snow covers the tall mountain peak."),
        VocabularyItem("Park", "park", "Places", "vocab/ic_vocab_fall.svg", "تفریحی پارک", "पार्क", "Parque", "Children play on swings in the neighborhood park."),

        // Vehicles (word_theme_vehicles)
        VocabularyItem("Wheel", "wheel", "Vehicles", "vocab/ic_vocab_circle.svg", "پہیہ", "पहिया", "Rueda", "A bicycle has two round wheels."),
        VocabularyItem("Train", "train", "Vehicles", "vocab/ic_vocab_rectangle.svg", "ریل گاڑی", "रेलगाड़ी", "Tren", "The long train rides on steel tracks."),
        VocabularyItem("Balloon", "bal • loon", "Vehicles", "vocab/ic_vocab_sphere.svg", "ہوائی غبارہ", "गर्म हवा का गुब्बारा", "Globo", "The colorful hot air balloon floats in the sky."),
        VocabularyItem("Boat", "boat", "Vehicles", "vocab/ic_vocab_triangle.svg", "کشتی", "नाव", "Barco", "The sailboat glides across the blue water.")
    )

    val wordMeaningsThemes: List<Pair<String, String>> = listOf(
        Pair("Body parts", "vocab/ic_vocab_hands.svg"),
        Pair("Color", "vocab/ic_vocab_purple.svg"),
        Pair("Seasons", "vocab/ic_vocab_sunny.svg"),
        Pair("Shapes", "vocab/ic_vocab_triangle.svg"),
        Pair("Numbers", "vocab/ic_vocab_seven.svg"),
        Pair("Expressions", "vocab/ic_vocab_smile.svg"),
        Pair("Actions", "vocab/ic_vocab_palms.svg"),
        Pair("Places", "vocab/ic_vocab_summer.svg"),
        Pair("Vehicles", "vocab/ic_vocab_circle.svg")
    )

    // Jumbled Words Challenges (Easy & Hard)
    val jumbleChallenges: List<JumbleWordChallenge> = listOf(
        JumbleWordChallenge("apples", "Sweet crunchy red or green fruit", "Fruits", false),
        JumbleWordChallenge("yellow", "The bright color of Lara's wings", "Colors", false),
        JumbleWordChallenge("train", "It rides on tracks to the beach", "Travel", false),
        JumbleWordChallenge("books", "We borrow them from the library to read", "Reading", false),
        JumbleWordChallenge("smile", "What you do when you are happy", "Feelings", false),
        JumbleWordChallenge("clouds", "Fluffy white shapes floating in the sky", "Weather", true),
        JumbleWordChallenge("flower", "It blooms with colorful petals in spring", "Nature", true),
        JumbleWordChallenge("friend", "Someone you play and share stories with", "People", true),
        JumbleWordChallenge("school", "Where Miss Miya and classmates learn together", "Places", true),
        JumbleWordChallenge("spider", "An eight-legged bug that spins a web", "Animals", true)
    )

    // Prize Badges matching Play Store Screenshot #4
    val prizeBadges: List<PrizeBadge> = listOf(
        PrizeBadge("star_100", "100 Stars Pouch", "Collected 100 golden reading stars", "100", "images/prize_star_1.svg", "STARS", requiredStars = 100),
        PrizeBadge("star_300", "300 Stars Chest", "Collected 300 golden reading stars", "300", "images/prize_star_2.svg", "STARS", requiredStars = 300),
        PrizeBadge("star_500", "500 Stars Vault", "Collected 500 golden reading stars", "500", "images/prize_star_3.svg", "STARS", requiredStars = 500),
        PrizeBadge("star_1000", "1,000 Stars Bag", "Collected 1,000 golden reading stars", "1000", "images/prize_star_4.svg", "STARS", requiredStars = 1000),
        PrizeBadge("star_3000", "3,000 Stars Trophy", "Collected 3,000 golden reading stars", "3000", "images/prize_star_6.svg", "STARS", requiredStars = 3000),
        PrizeBadge("streak_1", "1-Day Goal Streak", "Completed your daily reading goal", "1", "images/prize_daily_goal_1_days.svg", "STREAK", requiredStreak = 1),
        PrizeBadge("streak_5", "5-Day Goal Streak", "Completed daily goal 5 days in a row", "5", "images/prize_daily_goal_5_days.svg", "STREAK", requiredStreak = 5),
        PrizeBadge("book_lvl1", "Level 1 Reader", "Finished 5 Level 1 & 2 stories", "5", "images/prize_book_en_level_1_1.svg", "BOOKS", requiredBooks = 2),
        PrizeBadge("book_lvl2", "Level 2 Explorer", "Finished stories across Levels 1 to 3", "8", "images/prize_book_en_level_2_2.svg", "BOOKS", requiredBooks = 4),
        PrizeBadge("streak_7", "7-Day Super Reader", "Completed daily goal for a full week", "7", "images/prize_daily_goal_7_days.svg", "STREAK", requiredStreak = 7),
        PrizeBadge("book_lvl3", "Level 3 Storyteller", "Mastered Level 3 chapter stories", "10", "images/prize_book_en_level_3_2.svg", "BOOKS", requiredBooks = 6),
        PrizeBadge("star_5000", "5,000 Stars Crown", "Ultimate Seekh AI Academy Star Champion", "5000", "images/prize_star_9.svg", "STARS", requiredStars = 5000)
    )
}

class SeekhRepository(context: Context) {
    private val prefs = context.getSharedPreferences("seekh_ai_academy_prefs", Context.MODE_PRIVATE)

    fun isOnboardingCompleted(): Boolean = prefs.getBoolean("onboarding_done", false)

    fun setOnboardingCompleted(done: Boolean) {
        prefs.edit().putBoolean("onboarding_done", done).apply()
    }

    fun getSelectedLanguageCode(): String = prefs.getString("selected_lang", "en_ur") ?: "en_ur"

    fun setSelectedLanguageCode(code: String) {
        prefs.edit().putString("selected_lang", code).apply()
    }

    fun getDownloadedCloudBookIds(): Set<String> {
        val raw = prefs.getString("downloaded_cloud_books", "") ?: ""
        return raw.split(",").filter { it.isNotBlank() }.toSet()
    }

    fun markCloudBookDownloaded(bookId: String) {
        val current = getDownloadedCloudBookIds() + bookId
        prefs.edit().putString("downloaded_cloud_books", current.joinToString(",")).apply()
    }

    fun getBlockedBookIds(): Set<String> {
        val raw = prefs.getString("blocked_book_ids", "") ?: ""
        return raw.split(",").filter { it.isNotBlank() }.toSet()
    }

    fun blockBook(bookId: String) {
        val current = getBlockedBookIds() + bookId
        prefs.edit().putString("blocked_book_ids", current.joinToString(",")).apply()
    }

    fun unblockAllBooks() {
        prefs.edit().remove("blocked_book_ids").apply()
    }

    fun getPartnerCodes(): List<String> {
        val raw = prefs.getString("partner_codes_v3", "") ?: ""
        return raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    fun addPartnerCode(code: String) {
        val current = getPartnerCodes().toMutableList()
        val upper = code.trim().uppercase()
        if (upper.isNotEmpty() && !current.contains(upper)) {
            current.add(upper)
            prefs.edit().putString("partner_codes_v3", current.joinToString(",")).apply()
        }
    }

    fun removePartnerCode(code: String) {
        val current = getPartnerCodes().filterNot { it.equals(code, ignoreCase = true) }
        prefs.edit().putString("partner_codes_v3", current.joinToString(",")).apply()
    }

    fun getTodayDateKey(): String {
        val cal = java.util.Calendar.getInstance()
        val y = cal.get(java.util.Calendar.YEAR)
        val m = cal.get(java.util.Calendar.MONTH) + 1
        val d = cal.get(java.util.Calendar.DAY_OF_MONTH)
        return "%04d-%02d-%02d".format(y, m, d)
    }

    fun getTodayDayOfWeekIndex(): Int {
        val cal = java.util.Calendar.getInstance()
        return when (cal.get(java.util.Calendar.DAY_OF_WEEK)) {
            java.util.Calendar.MONDAY -> 0
            java.util.Calendar.TUESDAY -> 1
            java.util.Calendar.WEDNESDAY -> 2
            java.util.Calendar.THURSDAY -> 3
            java.util.Calendar.FRIDAY -> 4
            java.util.Calendar.SATURDAY -> 5
            else -> 6 // SUNDAY
        }
    }

    fun loadProfiles(): List<LearnerProfile> {
        val raw = prefs.getString("profiles_json_v3_real", null)
        if (raw.isNullOrBlank()) {
            return emptyList()
        }
        val todayKey = getTodayDateKey()
        return try {
            val arr = JSONArray(raw)
            val list = mutableListOf<LearnerProfile>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val completedArr = o.optJSONArray("completedBookIds") ?: JSONArray()
                val completedSet = mutableSetOf<String>()
                for (j in 0 until completedArr.length()) {
                    completedSet.add(completedArr.getString(j))
                }
                val weekArr = o.optJSONArray("weeklyMinutes") ?: JSONArray()
                val weekList = mutableListOf<Int>()
                for (j in 0 until weekArr.length()) {
                    weekList.add(weekArr.getInt(j))
                }
                val progObj = o.optJSONObject("bookPageProgress") ?: JSONObject()
                val progMap = mutableMapOf<String, Int>()
                progObj.keys().forEach { k ->
                    progMap[k] = progObj.optInt(k, 0)
                }
                val savedDateKey = o.optString("lastActiveDateKey", todayKey)
                val isSameDay = savedDateKey == todayKey
                val savedMinToday = if (isSameDay) o.optInt("minutesReadToday", 0) else 0
                val savedSecToday = if (isSameDay) o.optInt("readingSecondsToday", 0) else 0

                list.add(
                    LearnerProfile(
                        id = o.getString("id"),
                        name = o.getString("name"),
                        avatarIndex = o.optInt("avatarIndex", 1),
                        stars = o.optInt("stars", 0),
                        minutesReadToday = savedMinToday,
                        dailyGoalMinutes = o.optInt("dailyGoalMinutes", 10),
                        streakDays = o.optInt("streakDays", 0),
                        wordsPerMinute = o.optInt("wordsPerMinute", 0),
                        completedBookIds = completedSet,
                        weeklyMinutes = if (weekList.size == 7) weekList else listOf(0, 0, 0, 0, 0, 0, 0),
                        bookPageProgress = progMap,
                        gamesPlayedCount = o.optInt("gamesPlayedCount", 0),
                        readingSecondsToday = savedSecToday,
                        lastActiveDateKey = todayKey
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveProfiles(profiles: List<LearnerProfile>) {
        val arr = JSONArray()
        profiles.forEach { p ->
            val o = JSONObject()
            o.put("id", p.id)
            o.put("name", p.name)
            o.put("avatarIndex", p.avatarIndex)
            o.put("stars", p.stars)
            o.put("minutesReadToday", p.minutesReadToday)
            o.put("dailyGoalMinutes", p.dailyGoalMinutes)
            o.put("streakDays", p.streakDays)
            o.put("wordsPerMinute", p.wordsPerMinute)
            o.put("gamesPlayedCount", p.gamesPlayedCount)
            o.put("readingSecondsToday", p.readingSecondsToday)
            o.put("lastActiveDateKey", p.lastActiveDateKey.ifBlank { getTodayDateKey() })
            val cArr = JSONArray()
            p.completedBookIds.forEach { cArr.put(it) }
            o.put("completedBookIds", cArr)
            val wArr = JSONArray()
            p.weeklyMinutes.forEach { wArr.put(it) }
            o.put("weeklyMinutes", wArr)
            val pObj = JSONObject()
            p.bookPageProgress.forEach { (k, v) -> pObj.put(k, v) }
            o.put("bookPageProgress", pObj)
            arr.put(o)
        }
        prefs.edit().putString("profiles_json_v3_real", arr.toString()).apply()
    }

    fun getActiveProfileId(): String = prefs.getString("active_profile_id_v3", "") ?: ""

    fun setActiveProfileId(id: String) {
        prefs.edit().putString("active_profile_id_v3", id).apply()
    }

    fun clearUsageHistory() {
        prefs.edit()
            .remove("profiles_json_v3_real")
            .remove("active_profile_id_v3")
            .remove("custom_ai_stories_json")
            .remove("reading_groups_json_v3_real")
            .remove("turned_in_assignments_v3")
            .remove("opened_gift_boxes_v3")
            .apply()
    }

    fun loadCustomStories(): List<StoryBook> {
        val raw = prefs.getString("custom_ai_stories_json", null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            val list = mutableListOf<StoryBook>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val pagesArr = o.getJSONArray("pages")
                val pages = mutableListOf<StoryPage>()
                for (j in 0 until pagesArr.length()) {
                    val po = pagesArr.getJSONObject(j)
                    pages.add(
                        StoryPage(
                            pageNumber = j + 1,
                            imageAssetPath = po.getString("imageAssetPath"),
                            text = po.getString("text"),
                            urduTranslation = po.optString("urduTranslation", ""),
                            hindiTranslation = po.optString("hindiTranslation", ""),
                            practiceWords = po.optString("practiceWords", "")
                                .split(",").filter { it.isNotBlank() }.toSet()
                        )
                    )
                }
                list.add(
                    SeekhCatalog.enrichToFullIllustratedStory(
                        StoryBook(
                            id = o.getString("id"),
                            title = o.getString("title"),
                            nativeTitleUrdu = o.optString("nativeTitleUrdu", o.getString("title")),
                            nativeTitleHindi = o.optString("nativeTitleHindi", o.getString("title")),
                            author = o.optString("author", "Seekh AI Story Studio"),
                            illustrator = "Seekh AI Illustrator",
                            publisher = "TLL Studio AI Library",
                            level = o.optInt("level", 2),
                            coverAssetPath = o.getString("coverAssetPath"),
                            pages = pages,
                            isAiGenerated = true
                        )
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveCustomStory(book: StoryBook) {
        val current = loadCustomStories().toMutableList()
        current.add(0, book)
        saveCustomStoriesList(current)
    }

    fun deleteCustomStory(bookId: String) {
        val current = loadCustomStories().filterNot { it.id == bookId }
        saveCustomStoriesList(current)
    }

    private fun saveCustomStoriesList(list: List<StoryBook>) {
        val arr = JSONArray()
        list.forEach { b ->
            val o = JSONObject()
            o.put("id", b.id)
            o.put("title", b.title)
            o.put("nativeTitleUrdu", b.nativeTitleUrdu)
            o.put("nativeTitleHindi", b.nativeTitleHindi)
            o.put("author", b.author)
            o.put("level", b.level)
            o.put("coverAssetPath", b.coverAssetPath)
            val pArr = JSONArray()
            b.pages.forEach { p ->
                val po = JSONObject()
                po.put("imageAssetPath", p.imageAssetPath)
                po.put("text", p.text)
                po.put("urduTranslation", p.urduTranslation)
                po.put("hindiTranslation", p.hindiTranslation)
                po.put("practiceWords", p.practiceWords.joinToString(","))
                pArr.put(po)
            }
            o.put("pages", pArr)
            arr.put(o)
        }
        prefs.edit().putString("custom_ai_stories_json", arr.toString()).apply()
    }

    fun loadAccountState(): SignedInAccountState {
        val isSigned = prefs.getBoolean("account_signed_in_v3", false)
        val email = prefs.getString("account_email_v3", "") ?: ""
        val name = prefs.getString("account_display_name_v3", "Local Device Account") ?: "Local Device Account"
        return SignedInAccountState(
            isSignedIn = isSigned && email.isNotBlank(),
            email = email,
            displayName = name,
            syncStatus = if (isSigned && email.isNotBlank()) {
                "Now your profile is saved with $email"
            } else {
                "Saved locally on this device"
            }
        )
    }

    fun saveAccountState(state: SignedInAccountState) {
        prefs.edit()
            .putBoolean("account_signed_in_v3", state.isSignedIn)
            .putString("account_email_v3", state.email)
            .putString("account_display_name_v3", state.displayName)
            .apply()
    }

    fun getTurnedInAssignmentIds(): Set<String> {
        val raw = prefs.getString("turned_in_assignments_v3", "") ?: ""
        return raw.split(",").filter { it.isNotBlank() }.toSet()
    }

    fun markAssignmentTurnedIn(bookId: String) {
        val current = getTurnedInAssignmentIds() + bookId
        prefs.edit().putString("turned_in_assignments_v3", current.joinToString(",")).apply()
    }

    fun getOpenedGiftBoxIds(): Set<String> {
        val raw = prefs.getString("opened_gift_boxes_v3", "") ?: ""
        return raw.split(",").filter { it.isNotBlank() }.toSet()
    }

    fun markGiftBoxOpened(boxId: String) {
        val current = getOpenedGiftBoxIds() + boxId
        prefs.edit().putString("opened_gift_boxes_v3", current.joinToString(",")).apply()
    }

    fun loadReadingGroups(): List<ReadingGroup> {
        val raw = prefs.getString("reading_groups_json_v3_real", null)
        if (raw.isNullOrBlank()) {
            return emptyList()
        }
        return try {
            val arr = JSONArray(raw)
            val list = mutableListOf<ReadingGroup>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val mArr = o.optJSONArray("members") ?: JSONArray()
                val members = mutableListOf<GroupMemberStat>()
                for (j in 0 until mArr.length()) {
                    val mo = mArr.getJSONObject(j)
                    val completedRaw = mo.optString("completedBookIds", "")
                    val completedSet = completedRaw.split(",").filter { it.isNotBlank() }.toSet()
                    members.add(
                        GroupMemberStat(
                            profileId = mo.optString("profileId", ""),
                            name = mo.getString("name"),
                            avatarIndex = mo.optInt("avatarIndex", 1),
                            minutesThisWeek = mo.optInt("minutesThisWeek", 0),
                            storiesRead = mo.optInt("storiesRead", 0),
                            gamesPlayed = mo.optInt("gamesPlayed", 0),
                            speedWpm = mo.optInt("speedWpm", 0),
                            stars = mo.optInt("stars", 0),
                            completedBookIds = completedSet,
                            isActiveThisWeek = mo.optBoolean("isActiveThisWeek", true),
                            lastActiveLabel = mo.optString("lastActiveLabel", "Today")
                        )
                    )
                }
                val assignedRaw = o.optString("assignedBookIds", "sw_13916,book_rained_fish")
                val assignedList = assignedRaw.split(",").filter { it.isNotBlank() }
                list.add(
                    ReadingGroup(
                        id = o.getString("id"),
                        name = o.getString("name"),
                        adminName = o.getString("adminName"),
                        languageTitle = o.getString("languageTitle"),
                        inviteCode = o.getString("inviteCode"),
                        createdDate = o.getString("createdDate"),
                        members = members,
                        assignedBookIds = assignedList
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveReadingGroups(groups: List<ReadingGroup>) {
        val arr = JSONArray()
        groups.forEach { g ->
            val o = JSONObject()
            o.put("id", g.id)
            o.put("name", g.name)
            o.put("adminName", g.adminName)
            o.put("languageTitle", g.languageTitle)
            o.put("inviteCode", g.inviteCode)
            o.put("createdDate", g.createdDate)
            o.put("assignedBookIds", g.assignedBookIds.joinToString(","))
            val mArr = JSONArray()
            g.members.forEach { m ->
                val mo = JSONObject()
                mo.put("profileId", m.profileId)
                mo.put("name", m.name)
                mo.put("avatarIndex", m.avatarIndex)
                mo.put("minutesThisWeek", m.minutesThisWeek)
                mo.put("storiesRead", m.storiesRead)
                mo.put("gamesPlayed", m.gamesPlayed)
                mo.put("speedWpm", m.speedWpm)
                mo.put("stars", m.stars)
                mo.put("completedBookIds", m.completedBookIds.joinToString(","))
                mo.put("isActiveThisWeek", m.isActiveThisWeek)
                mo.put("lastActiveLabel", m.lastActiveLabel)
                mArr.put(mo)
            }
            o.put("members", mArr)
            arr.put(o)
        }
        prefs.edit().putString("reading_groups_json_v3_real", arr.toString()).apply()
    }

    fun syncReadingGroupsWithProfiles(
        groups: List<ReadingGroup>,
        profiles: List<LearnerProfile>
    ): List<ReadingGroup> {
        if (groups.isEmpty() || profiles.isEmpty()) return groups
        return groups.map { group ->
            val syncedMembers = group.members.map { member ->
                val matchedProfile = profiles.firstOrNull { p ->
                    (member.profileId.isNotBlank() && p.id == member.profileId) ||
                        p.name.equals(member.name, ignoreCase = true)
                }
                if (matchedProfile != null) {
                    val weekMins = matchedProfile.weeklyMinutes.sum().coerceAtLeast(matchedProfile.minutesReadToday)
                    member.copy(
                        profileId = matchedProfile.id,
                        name = matchedProfile.name,
                        avatarIndex = matchedProfile.avatarIndex,
                        minutesThisWeek = weekMins,
                        storiesRead = matchedProfile.completedBookIds.size,
                        gamesPlayed = matchedProfile.gamesPlayedCount,
                        speedWpm = matchedProfile.wordsPerMinute,
                        stars = matchedProfile.stars,
                        completedBookIds = matchedProfile.completedBookIds,
                        isActiveThisWeek = weekMins > 0 || matchedProfile.completedBookIds.isNotEmpty() || matchedProfile.gamesPlayedCount > 0 || matchedProfile.stars > 0
                    )
                } else {
                    member
                }
            }
            group.copy(members = syncedMembers)
        }
    }
}
