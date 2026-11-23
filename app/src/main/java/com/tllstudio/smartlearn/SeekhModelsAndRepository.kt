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

    // Complete Unabridged Library of Illustrated Books (All 86 real unpacked WebP pages!)
    val defaultBooks: List<StoryBook> = listOf(
        // 1. Featured Hero Book on Home Screen (Play Store Screenshot #6: "Colors on the street")
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
                StoryPage(1, "books/sw_13916/image_5.webp", "Every morning, the street wakes up with bright colors, happy music, and friendly faces.", "Cada mañana, la calle despierta con colores brillantes.", "ہر صبح گلی چمکدار رنگوں، خوشگوار موسیقی اور دوستانہ چہروں کے ساتھ جاگتی ہے۔", "हर सुबह गली चमकीले रंगों और खुशहाल संगीत के साथ जागती है।", setOf("morning", "bright", "friendly")),
                StoryPage(2, "books/sw_44485/image_6.webp", "Look at the sweet shop on the corner! Golden chocolates and rainbow treats shine in the sun.", "¡Mira la tienda de dulces en la esquina!", "کونے پر مٹھائی کی دکان دیکھو! سنہری چاکلیٹ دھوپ میں چمک رہے ہیں۔", "कोने की मिठाई की दुकान देखो! सुनहरी चॉकलेट धूप में चमक रही हैं।", setOf("chocolates", "rainbow")),
                StoryPage(3, "books/sw_44485/image_11.webp", "The drummers wear red and yellow shirts. Their drums beat to the rhythm of the busy street.", "Los bateristas usan camisas rojas y amarillas.", "ڈھول بجانے والوں نے سرخ اور پیلی قمیضیں پہن رکھی ہیں۔", "ढोल वादकों ने लाल और पीली शर्ट पहनी है।", setOf("drummers", "street")),
                StoryPage(4, "books/sw_44485/image_12.webp", "\"Do you want to dance with us?\" the drummer asks with a big warm smile.", "\"¿Quieres bailar con nosotros?\", pregunta el tamborilero.", "کیا تم ہمارے ساتھ ناچنا چاہتے ہو؟ ڈھول بجانے والا مسکرا کر پوچھتا ہے۔", "\"क्या तुम हमारे साथ नाचना चाहते हो?\" ढोल वादक मुस्कुराकर पूछता है।", setOf("dance", "drummer"),
                    StoryQuizQuestion("What colors were the drummers wearing on the street?", listOf("Red and yellow", "Blue and black", "Green and grey"), 0)
                ),
                StoryPage(5, "books/sw_44485/image_14.webp", "Everyone waves and smiles together. Our street is the most colorful place in town!", "¡Nuestra calle es el lugar más colorido de la ciudad!", "سب ایک ساتھ ہاتھ ہلاتے اور مسکراتے ہیں۔ ہماری گلی شہر کی سب سے رنگین جگہ ہے!", "हमारी गली शहर की सबसे रंगीन जगह है!", setOf("colors", "street"))
            )
        ),

        // 2. Play Store Screenshot #1 & #2 Book: "The day it rained fish"
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
            phonicsSkillTag = "Phonics: sh • ch • cl",
            assignedByTeacher = "Miss Miya • Grade 2",
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_45764/image_2.webp", "The blast of air blew the candles out. Then rising up, it crashed into the clouds in the sky. And all of a sudden, there was a roar of thunder and it started to rain.", "La ráfaga de aire apagó las velas. Luego subió hacia las nubes y empezó a llover.", "ہوا کے تیز جھونکے نے موم بتیاں بجھا دیں۔ پھر بادلوں سے ٹکرانے کے بعد اچانک بادل گرجے اور بارش شروع ہو گئی۔", "हवा के तेज़ झोंके ने मोमबत्तियाँ बुझा दीं। फिर अचानक बादल गरजे और बारिश शुरू हो गई।", setOf("candles", "crashed", "thunder")),
                StoryPage(2, "books/sw_45764/image_6.webp", "Lulu looked out the window in surprise. Big silver raindrops were splashing all across the green garden!", "Lulu miró por la ventana con sorpresa.", "لولو نے حیرت سے کھڑکی سے باہر دیکھا۔ ہرے باغ میں چاندی جیسی بوندیں گر رہی تھیں!", "लुलु ने हैरानी से खिड़की के बाहर देखा।", setOf("clouds", "rain")),
                StoryPage(3, "books/sw_45764/image_10.webp", "Splash! Plop! Shiny silver fish began falling from the dark grey clouds right onto the soft grass.", "¡Peces plateados comenzaron a caer de las nubes!", "چھپاک! سرمئی بادلوں سے چمکدار مچھلیاں ہری گھاس پر گرنے لگیں۔", "छपाक! बादलों से चमकीली मछलियाँ हरी घास पर गिरने लगीं।", setOf("clouds", "bright")),
                StoryPage(4, "books/sw_45764/image_13.webp", "The children laughed and caught the jumping fish in buckets to carry them safely back to the river!", "Los niños rieron y llevaron los peces de regreso al río.", "بچے ہنسے اور اچھلتی مچھلیوں کو بالٹیوں میں پکڑ کر بحفاظت واپس دریا میں چھوڑ آئے!", "बच्चे हँसे और मछलियों को सुरक्षित वापस नदी में छोड़ आए!", setOf("friends", "rain"),
                    StoryQuizQuestion("What blew the candles out before it started to rain?", listOf("A blast of air", "A big red ball", "A yellow ladybird"), 0)
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
            phonicsSkillTag = "Phonics: dr • tr • ch",
            pages = listOf(
                StoryPage(1, "books/sw_44485/image_10.webp", "Chugga-chugga-choo-choo! The Drum Beat Express rolls down the shiny steel tracks toward the coast.", "El tren rueda por las vías hacia la costa.", "چھک چھک کرتی ڈھول ایکسپریس ٹرین چمکتی پٹریوں پر ساحل کی طرف دوڑتی ہے۔", "छुक-छुक करती ट्रेन पटरियों पर समुद्र तट की ओर दौड़ती है।", setOf("train", "beach")),
                StoryPage(2, "books/sw_44485/image_12.webp", "Inside the carriage, the drummers play a joyful rhythm that makes every passenger tap their feet.", "Los bateristas tocan un ritmo alegre.", "ڈبے کے اندر ڈھول بجانے والے ایسی خوشگوار دھن بجاتے ہیں کہ ہر مسافر جھوم اٹھتا ہے۔", "डिब्बे के अंदर ढोल वादक मधुर ताल बजाते हैं।", setOf("drummers", "friends"),
                    StoryQuizQuestion("Where is the Drum Beat Express traveling?", listOf("Toward the coast and beach", "Up a snowy mountain", "Into a forest"), 0)
                )
            )
        )
    )

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
