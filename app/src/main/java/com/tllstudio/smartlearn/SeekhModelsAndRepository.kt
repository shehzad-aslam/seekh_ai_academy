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
    val quiz: StoryQuizQuestion? = null,
    val alphabetBadgeLetters: String = ""
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
    val bookType: String = "LEVELED", // "LEVELED", "DECODABLE", "SERIES", "ASSESSMENT", "CYOC", "ALPHABET"
    val bookSource: String = "LIBRARY", // "LIBRARY", "CLASSROOM", "PARTNER", "MY_STORY"
    val categoryTag: String = "Family & Friends", // "Animals & Nature", "Family & Friends", "Adventure & Travel", "Science & Weather", "Bedtime & Folktales"
    val phonicsSkillTag: String = "",
    val seriesTag: String = "",
    val assignedByTeacher: String = "",
    val isDailyDrop: Boolean = false,
    val alphabetBadgeLetters: String = ""
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

data class WordTutorDetails(
    val displayWord: String,
    val cleanWord: String,
    val phonetics: String,
    val partOfSpeech: String,
    val syllables: List<String>,
    val englishMeaning: String,
    val nativeMeaning: String,
    val exampleUsage: String,
    val spokenExplanation: String
)

data class SeekhAppStrings(
    val isRtl: Boolean,
    val tabLibrary: String,
    val tabPrizes: String,
    val tabActivity: String,
    val readCta: String,
    val playCta: String,
    val seeAll: String,
    val levelPrefix: String,
    val alphabetBookTag: String,
    val storyOfTheDayTag: String,
    val searchPlaceholder: String,
    val sectionAssignedHomework: String,
    val sectionRecommended: String,
    val sectionLevel1: String,
    val sectionLevel2: String,
    val sectionLevel3: String,
    val sectionLevel4: String,
    val sectionPlayGames: String,
    val sectionAnimalsNature: String,
    val sectionSchoolKindness: String,
    val sectionPhonics: String,
    val sectionFamilyFriends: String,
    val sectionNumbersRhymes: String,
    val sectionStorySeries: String,
    val sectionAdventureTravel: String,
    val sectionScienceWeather: String,
    val sectionBedtimeFolktales: String,
    val sectionCreateStory: String,
    val sectionReadAgain: String,
    val moreLanguagesLabel: String,
    val listenButton: String,
    val listeningButton: String,
    val speakButton: String,
    val speakingButton: String,
    val listenSubLabel: String,
    val speakSubLabel: String,
    val filterChipLabels: Map<String, String>,
    val gameInfoByKey: Map<String, Triple<String, String, String>> // gameKey -> Triple(title, subtitle, badge)
)

object SeekhCatalog {

    fun isRtlLanguage(langCode: String): Boolean {
        return langCode == "en_ur" || langCode == "en_ar"
    }

    fun getAppStrings(langCode: String): SeekhAppStrings {
        return when (langCode) {
            "en_ur" -> SeekhAppStrings(
                isRtl = true,
                tabLibrary = "لائبریری",
                tabPrizes = "انعامات",
                tabActivity = "سرگرمی",
                readCta = "پڑھیں",
                playCta = "کھیلیں",
                seeAll = "سب دیکھیں",
                levelPrefix = "لیول",
                alphabetBookTag = "بنیادی حروفِ تہجی • قاعدہ",
                storyOfTheDayTag = "آج کی خاص کتاب",
                searchPlaceholder = "عنوان یا لیول سے کہانیاں تلاش کریں...",
                sectionAssignedHomework = "اسکول کا ہوم ورک",
                sectionRecommended = "تجویز کردہ کہانیاں",
                sectionLevel1 = "لیول 1 کی کہانیاں",
                sectionLevel2 = "لیول 2 کی کہانیاں",
                sectionLevel3 = "لیول 3 کی کہانیاں",
                sectionLevel4 = "لیول 4 کی کہانیاں",
                sectionPlayGames = "تعلیمی گیمز کھیلیں",
                sectionAnimalsNature = "جانور اور قدرت",
                sectionSchoolKindness = "اسکول اور اچھے اخلاق",
                sectionPhonics = "آوازیں اور حروف (Phonics)",
                sectionFamilyFriends = "خاندان اور دوست",
                sectionNumbersRhymes = "گنتی اور نظمیں",
                sectionStorySeries = "کہانیوں کی سیریز",
                sectionAdventureTravel = "سیر اور مہم جوئی",
                sectionScienceWeather = "سائنس اور موسم",
                sectionBedtimeFolktales = "سونے کے وقت کی کہانیاں",
                sectionCreateStory = "اپنی کہانی خود بنائیں",
                sectionReadAgain = "دوبارہ پڑھیں",
                moreLanguagesLabel = "مزید زبانیں منتخب کریں",
                listenButton = "سنیں (Listen)",
                listeningButton = "سنا جا رہا ہے...",
                speakButton = "بولیں (Speak)",
                speakingButton = "آپ کی باری...",
                listenSubLabel = "کہانی سننے کے لیے",
                speakSubLabel = "خود پڑھنے کے لیے",
                filterChipLabels = mapOf(
                    "All" to "سب",
                    "Games" to "گیمز",
                    "New & Daily" to "نئی کہانیاں",
                    "Level 1" to "لیول 1",
                    "Level 2" to "لیول 2",
                    "Level 3" to "لیول 3",
                    "Level 4" to "لیول 4",
                    "Animals & Nature" to "جانور اور قدرت",
                    "Family & Friends" to "خاندان اور دوست",
                    "School & Kindness" to "اسکول اور اخلاق",
                    "Numbers & Rhymes" to "گنتی اور نظمیں",
                    "Phonics" to "حروف اور آوازیں",
                    "Adventure & Travel" to "سیر و سیاحت",
                    "Science & Weather" to "سائنس اور موسم",
                    "Bedtime & Folktales" to "لوک کہانیاں",
                    "Series" to "سیریز",
                    "Classroom" to "کلاس روم",
                    "My stories" to "میری کہانیاں"
                ),
                gameInfoByKey = mapOf(
                    "JUMBLE" to Triple("الفاظ جوڑیں", "بکھرے حروف سے لفظ بنائیں", "ہجے (Spelling)"),
                    "BALLOON" to Triple("غبارے پھوڑیں", "حروف کی آواز والے غبارے", "آوازیں (Phonics)"),
                    "SENTENCE" to Triple("جملے بنائیں", "الفاظ ترتیب دے کر جملہ مکمل کریں", "جملے (Sentences)"),
                    "RHYME" to Triple("ہم آواز الفاظ", "ایک جیسی آواز والے الفاظ ملائیں", "ہم قافیہ"),
                    "MISSING_SOUND" to Triple("غائب حروف", "لفظ کا خالی حرف تلاش کریں", "حروف (Letters)"),
                    "LISTEN_FIND" to Triple("سنیں اور پہچانیں", "آواز سن کر درست تصویر چنیں", "سننا (Listening)"),
                    "OPPOSITES" to Triple("متضاد الفاظ", "الٹ معنی والے الفاظ ملائیں", "ذخیرۂ الفاظ"),
                    "COUNT_COLOR" to Triple("گنتی اور اعداد", "تصویریں گنیں اور سوال حل کریں", "گنتی (Numbers)"),
                    "SPEED" to Triple("تیز خوانی", "30 سیکنڈ میں الفاظ پڑھنے کا چیلنج", "روانی (Fluency)"),
                    "VOCAB" to Triple("الفاظ کے معانی", "تصویروں سے نئے الفاظ سیکھیں", "تصویری الفاظ")
                )
            )

            "en_hi" -> SeekhAppStrings(
                isRtl = false,
                tabLibrary = "लाइब्रेरी",
                tabPrizes = "इनाम",
                tabActivity = "गतिविधि",
                readCta = "पढ़ें",
                playCta = "खेलें",
                seeAll = "सभी देखें",
                levelPrefix = "लेवल",
                alphabetBookTag = "मूल वर्णमाला • अ से ज्ञ",
                storyOfTheDayTag = "आज की ख़ास किताब",
                searchPlaceholder = "शीर्षक या लेवल से कहानियाँ खोजें...",
                sectionAssignedHomework = "स्कूल का होमवर्क",
                sectionRecommended = "सुझाई गई कहानियाँ",
                sectionLevel1 = "लेवल 1 की कहानियाँ",
                sectionLevel2 = "लेवल 2 की कहानियाँ",
                sectionLevel3 = "लेवल 3 की कहानियाँ",
                sectionLevel4 = "लेवल 4 की कहानियाँ",
                sectionPlayGames = "मज़ेदार खेल खेलें",
                sectionAnimalsNature = "जानवर और प्रकृति",
                sectionSchoolKindness = "स्कूल और अच्छी आदतें",
                sectionPhonics = "ध्वनियाँ और अक्षर (Phonics)",
                sectionFamilyFriends = "परिवार और दोस्त",
                sectionNumbersRhymes = "गिनती और कविताएँ",
                sectionStorySeries = "कहानी सीरीज़",
                sectionAdventureTravel = "यात्रा और रोमांच",
                sectionScienceWeather = "विज्ञान और मौसम",
                sectionBedtimeFolktales = "लोककथाएँ और कहानियाँ",
                sectionCreateStory = "अपनी कहानी खुद बनाएँ",
                sectionReadAgain = "फिर से पढ़ें",
                moreLanguagesLabel = "अन्य भाषाएँ चुनें",
                listenButton = "सुनें (Listen)",
                listeningButton = "सुन रहे हैं...",
                speakButton = "बोलें (Speak)",
                speakingButton = "आपकी बारी...",
                listenSubLabel = "कहानी सुनने के लिए",
                speakSubLabel = "खुद बोलकर पढ़ने के लिए",
                filterChipLabels = mapOf(
                    "All" to "सभी",
                    "Games" to "खेल",
                    "New & Daily" to "नई कहानियाँ",
                    "Level 1" to "लेवल 1",
                    "Level 2" to "लेवल 2",
                    "Level 3" to "लेवल 3",
                    "Level 4" to "लेवल 4",
                    "Animals & Nature" to "जानवर और प्रकृति",
                    "Family & Friends" to "परिवार और दोस्त",
                    "School & Kindness" to "स्कूल और आदतें",
                    "Numbers & Rhymes" to "गिनती और कविता",
                    "Phonics" to "अक्षर और ध्वनि",
                    "Adventure & Travel" to "यात्रा और रोमांच",
                    "Science & Weather" to "विज्ञान और मौसम",
                    "Bedtime & Folktales" to "लोककथाएँ",
                    "Series" to "सीरीज़",
                    "Classroom" to "क्लासरूम",
                    "My stories" to "मेरी कहानियाँ"
                ),
                gameInfoByKey = mapOf(
                    "JUMBLE" to Triple("अक्षर जोड़ें", "बिखरे अक्षरों से सही शब्द बनाएँ", "स्पेलिंग"),
                    "BALLOON" to Triple("गुब्बारे फोड़ें", "सही ध्वनि वाले गुब्बारे चुनें", "ध्वनि (Phonics)"),
                    "SENTENCE" to Triple("वाक्य बनाएँ", "शब्दों को क्रम में रखकर वाक्य बनाएँ", "वाक्य"),
                    "RHYME" to Triple("तुकबंदी शब्द", "समान ध्वनि वाले शब्द मिलाएँ", "तुकबंदी"),
                    "MISSING_SOUND" to Triple("छूटे हुए अक्षर", "शब्द का खाली अक्षर पहचानें", "अक्षर"),
                    "LISTEN_FIND" to Triple("सुनें और खोजें", "आवाज़ सुनकर सही चित्र चुनें", "सुनना"),
                    "OPPOSITES" to Triple("विलोम शब्द", "उल्टे अर्थ वाले शब्द मिलाएँ", "शब्दावली"),
                    "COUNT_COLOR" to Triple("गिनती और अंक", "चित्र गिनें और आसान गणित हल करें", "गिनती"),
                    "SPEED" to Triple("तेज़ पठन", "30 सेकंड में शब्द पढ़ने की चुनौती", "प्रवाह"),
                    "VOCAB" to Triple("शब्दों के अर्थ", "9 विषयों के चित्र और शब्द सीखें", "चित्र शब्द")
                )
            )

            "en_ar" -> SeekhAppStrings(
                isRtl = true,
                tabLibrary = "المكتبة",
                tabPrizes = "الجوائز",
                tabActivity = "النشاط",
                readCta = "اقرأ",
                playCta = "العب",
                seeAll = "عرض الكل",
                levelPrefix = "المستوى",
                alphabetBookTag = "الحروف الأبجدية الأساسية",
                storyOfTheDayTag = "كتاب اليوم المميز",
                searchPlaceholder = "ابحث عن القصص بالعنوان أو المستوى...",
                sectionAssignedHomework = "الواجبات المدرسية",
                sectionRecommended = "قصص موصى بها",
                sectionLevel1 = "قصص المستوى 1",
                sectionLevel2 = "قصص المستوى 2",
                sectionLevel3 = "قصص المستوى 3",
                sectionLevel4 = "قصص المستوى 4",
                sectionPlayGames = "العب الألعاب التعليمية",
                sectionAnimalsNature = "الحيوانات والطبيعة",
                sectionSchoolKindness = "المدرسة واللطف",
                sectionPhonics = "الأصوات والحروف",
                sectionFamilyFriends = "العائلة والأصدقاء",
                sectionNumbersRhymes = "الأرقام والأناشيد",
                sectionStorySeries = "سلاسل القصص",
                sectionAdventureTravel = "المغامرة والسفر",
                sectionScienceWeather = "العلوم والطقس",
                sectionBedtimeFolktales = "قصص قبل النوم",
                sectionCreateStory = "اصنع قصتك بنفسك",
                sectionReadAgain = "اقرأ مرة أخرى",
                moreLanguagesLabel = "المزيد من اللغات",
                listenButton = "استمع",
                listeningButton = "جارٍ الاستماع...",
                speakButton = "تحدث",
                speakingButton = "دورك للقراءة...",
                listenSubLabel = "لسماع القصة",
                speakSubLabel = "للقراءة بصوتك",
                filterChipLabels = mapOf(
                    "All" to "الكل",
                    "Games" to "الألعاب",
                    "New & Daily" to "جديد ويومي",
                    "Level 1" to "المستوى 1",
                    "Level 2" to "المستوى 2",
                    "Level 3" to "المستوى 3",
                    "Level 4" to "المستوى 4",
                    "Animals & Nature" to "الحيوانات والطبيعة",
                    "Family & Friends" to "العائلة والأصدقاء",
                    "School & Kindness" to "المدرسة واللطف",
                    "Numbers & Rhymes" to "الأرقام والأناشيد",
                    "Phonics" to "الصوتيات",
                    "Adventure & Travel" to "المغامرة",
                    "Science & Weather" to "العلوم والطقس",
                    "Bedtime & Folktales" to "قصص النوم",
                    "Series" to "السلاسل",
                    "Classroom" to "الفصل",
                    "My stories" to "قصصي"
                ),
                gameInfoByKey = mapOf(
                    "JUMBLE" to Triple("ترتيب الحروف", "رتب الحروف المبعثرة لتكوين كلمة", "التهجئة"),
                    "BALLOON" to Triple("فرقع البالونات", "اختر البالونات التي تحمل الصوت الصحيح", "الأصوات"),
                    "SENTENCE" to Triple("بناء الجمل", "رتب الكلمات لتكوين جملة مفيدة", "الجمل"),
                    "RHYME" to Triple("كلمات متقافية", "طابق الكلمات ذات الإيقاع المتشابه", "القافية"),
                    "MISSING_SOUND" to Triple("الحرف المفقود", "اكتشف الحرف الناقص في الكلمة", "الحروف"),
                    "LISTEN_FIND" to Triple("استمع وابحث", "استمع للصوت واختر الصورة الصحيحة", "الاستماع"),
                    "OPPOSITES" to Triple("الكلمات المتضادة", "طابق كل كلمة مع عكسها", "المفردات"),
                    "COUNT_COLOR" to Triple("العد والأرقام", "عد العناصر وحل مسائل القصص", "الأرقام"),
                    "SPEED" to Triple("القراءة السريعة", "تحدي طلاقة القراءة في 30 ثانية", "الطلاقة"),
                    "VOCAB" to Triple("معاني الكلمات", "تعلم المفردات المصورة في 9 مواضيع", "الصور")
                )
            )

            "en_es" -> SeekhAppStrings(
                isRtl = false,
                tabLibrary = "Biblioteca",
                tabPrizes = "Premios",
                tabActivity = "Actividad",
                readCta = "Leer",
                playCta = "Jugar",
                seeAll = "Ver todo",
                levelPrefix = "Nivel",
                alphabetBookTag = "El Abecedario Básico • A a Z",
                storyOfTheDayTag = "Cuento destacado de hoy",
                searchPlaceholder = "Buscar cuentos por título o nivel...",
                sectionAssignedHomework = "Tareas asignadas",
                sectionRecommended = "Cuentos recomendados",
                sectionLevel1 = "Cuentos de Nivel 1",
                sectionLevel2 = "Cuentos de Nivel 2",
                sectionLevel3 = "Cuentos de Nivel 3",
                sectionLevel4 = "Cuentos de Nivel 4",
                sectionPlayGames = "Jugar juegos educativos",
                sectionAnimalsNature = "Animales y Naturaleza",
                sectionSchoolKindness = "Escuela y Amabilidad",
                sectionPhonics = "Sonidos y Letras",
                sectionFamilyFriends = "Familia y Amigos",
                sectionNumbersRhymes = "Números y Rimas",
                sectionStorySeries = "Series de Cuentos",
                sectionAdventureTravel = "Aventura y Viajes",
                sectionScienceWeather = "Ciencia y Clima",
                sectionBedtimeFolktales = "Cuentos para Dormir",
                sectionCreateStory = "Crea tu propio cuento",
                sectionReadAgain = "Leer de nuevo",
                moreLanguagesLabel = "Más idiomas",
                listenButton = "Escuchar",
                listeningButton = "Escuchando...",
                speakButton = "Hablar",
                speakingButton = "Tu turno...",
                listenSubLabel = "Oír el cuento",
                speakSubLabel = "Leer en voz alta",
                filterChipLabels = mapOf(
                    "All" to "Todos",
                    "Games" to "Juegos",
                    "New & Daily" to "Nuevos",
                    "Level 1" to "Nivel 1",
                    "Level 2" to "Nivel 2",
                    "Level 3" to "Nivel 3",
                    "Level 4" to "Nivel 4"
                ),
                gameInfoByKey = mapOf(
                    "JUMBLE" to Triple("Palabras Revueltas", "Ordena las letras para formar palabras", "Ortografía"),
                    "BALLOON" to Triple("Explota Globos", "Explota los globos con el sonido correcto", "Fonética"),
                    "SENTENCE" to Triple("Arma Oraciones", "Ordena las palabras de la oración", "Oraciones"),
                    "RHYME" to Triple("Palabras que Riman", "Une palabras con el mismo sonido final", "Rimas"),
                    "MISSING_SOUND" to Triple("Letras Faltantes", "Completa la letra que falta en la palabra", "Letras"),
                    "LISTEN_FIND" to Triple("Escucha y Busca", "Escucha la pista y elige el dibujo", "Escucha"),
                    "OPPOSITES" to Triple("Palabras Opuestas", "Une palabras con significados opuestos", "Vocabulario"),
                    "COUNT_COLOR" to Triple("Contar y Números", "Cuenta figuras y resuelve sumas", "Números"),
                    "SPEED" to Triple("Lectura Rápida", "Reto de fluidez de lectura de 30s", "Fluidez"),
                    "VOCAB" to Triple("Significados", "Aprende vocabulario ilustrado", "Imágenes")
                )
            )

            "en_pt" -> SeekhAppStrings(
                isRtl = false,
                tabLibrary = "Biblioteca",
                tabPrizes = "Prêmios",
                tabActivity = "Atividade",
                readCta = "Ler",
                playCta = "Jogar",
                seeAll = "Ver tudo",
                levelPrefix = "Nível",
                alphabetBookTag = "O Alfabeto Básico • A a Z",
                storyOfTheDayTag = "História em destaque",
                searchPlaceholder = "Pesquisar histórias por título ou nível...",
                sectionAssignedHomework = "Tarefas da turma",
                sectionRecommended = "Histórias recomendadas",
                sectionLevel1 = "Histórias de Nível 1",
                sectionLevel2 = "Histórias de Nível 2",
                sectionLevel3 = "Histórias de Nível 3",
                sectionLevel4 = "Histórias de Nível 4",
                sectionPlayGames = "Jogar jogos educativos",
                sectionAnimalsNature = "Animais e Natureza",
                sectionSchoolKindness = "Escola e Gentileza",
                sectionPhonics = "Sons e Letras",
                sectionFamilyFriends = "Família e Amigos",
                sectionNumbersRhymes = "Números e Rimas",
                sectionStorySeries = "Séries de Histórias",
                sectionAdventureTravel = "Aventura e Viagem",
                sectionScienceWeather = "Ciência e Clima",
                sectionBedtimeFolktales = "Histórias para Dormir",
                sectionCreateStory = "Crie sua própria história",
                sectionReadAgain = "Ler novamente",
                moreLanguagesLabel = "Mais idiomas",
                listenButton = "Ouvir",
                listeningButton = "Ouvindo...",
                speakButton = "Falar",
                speakingButton = "Sua vez...",
                listenSubLabel = "Ouvir a história",
                speakSubLabel = "Ler em voz alta",
                filterChipLabels = mapOf(
                    "All" to "Todos",
                    "Games" to "Jogos",
                    "Level 1" to "Nível 1",
                    "Level 2" to "Nível 2",
                    "Level 3" to "Nível 3",
                    "Level 4" to "Nível 4"
                ),
                gameInfoByKey = mapOf(
                    "JUMBLE" to Triple("Letras Misturadas", "Ordene as letras para formar palavras", "Soletrar"),
                    "BALLOON" to Triple("Estoure os Balões", "Estoure os balões com o som certo", "Fonética"),
                    "SENTENCE" to Triple("Monte a Frase", "Ordene as palavras para criar frases", "Frases"),
                    "RHYME" to Triple("Palavras que Rimam", "Combine palavras com o mesmo som final", "Rimas"),
                    "MISSING_SOUND" to Triple("Letras Faltando", "Descubra a letra que falta na palavra", "Letras"),
                    "LISTEN_FIND" to Triple("Ouça e Encontre", "Ouça a dica e escolha a imagem", "Audição"),
                    "OPPOSITES" to Triple("Palavras Opostas", "Combine palavras de sentidos opostos", "Vocabulário"),
                    "COUNT_COLOR" to Triple("Contar e Números", "Conte itens e resolva desafios", "Números"),
                    "SPEED" to Triple("Leitura Rápida", "Desafio de fluência em 30 segundos", "Fluência"),
                    "VOCAB" to Triple("Significados", "Aprenda vocabulário com imagens", "Figuras")
                )
            )

            "en_bn" -> SeekhAppStrings(
                isRtl = false,
                tabLibrary = "লাইব্রেরি",
                tabPrizes = "পুরস্কার",
                tabActivity = "অগ্রগতি",
                readCta = "পড়ুন",
                playCta = "খেলুন",
                seeAll = "সব দেখুন",
                levelPrefix = "লেভেল",
                alphabetBookTag = "বাংলা বর্ণমালা • অ থেকে চন্দ্রবিন্দু",
                storyOfTheDayTag = "আজকের বিশেষ বই",
                searchPlaceholder = "গল্পের নাম বা লেভেল দিয়ে খুঁজুন...",
                sectionAssignedHomework = "স্কুলের হোমওয়ার্ক",
                sectionRecommended = "নির্বাচিত গল্প",
                sectionLevel1 = "লেভেল ১ গল্প",
                sectionLevel2 = "লেভেল ২ গল্প",
                sectionLevel3 = "লেভেল ৩ গল্প",
                sectionLevel4 = "লেভেল ৪ গল্প",
                sectionPlayGames = "মজার শিক্ষামূলক গেম",
                sectionAnimalsNature = "প্রাণী ও প্রকৃতি",
                sectionSchoolKindness = "স্কুল ও সদাচরণ",
                sectionPhonics = "বর্ণ ও ধ্বনি",
                sectionFamilyFriends = "পরিবার ও বন্ধু",
                sectionNumbersRhymes = "সংখ্যা ও ছড়া",
                sectionStorySeries = "গল্পের সিরিজ",
                sectionAdventureTravel = "ভ্রমণ ও অভিযান",
                sectionScienceWeather = "বিজ্ঞান ও আবহাওয়া",
                sectionBedtimeFolktales = "রূপকথার গল্প",
                sectionCreateStory = "নিজের গল্প তৈরি করুন",
                sectionReadAgain = "আবার পড়ুন",
                moreLanguagesLabel = "আরও ভাষা",
                listenButton = "শুনুন",
                listeningButton = "শোনা হচ্ছে...",
                speakButton = "বলুন",
                speakingButton = "আপনার পালা...",
                listenSubLabel = "গল্প শোনার জন্য",
                speakSubLabel = "নিজে পড়ার জন্য",
                filterChipLabels = mapOf("All" to "সব", "Games" to "গেম", "Level 1" to "লেভেল ১", "Level 2" to "লেভেল ২", "Level 3" to "লেভেল ৩", "Level 4" to "লেভেল ৪"),
                gameInfoByKey = mapOf(
                    "JUMBLE" to Triple("শব্দ সাজাও", "এলোমেলো অক্ষর সাজিয়ে শব্দ তৈরি করো", "বানান"),
                    "BALLOON" to Triple("বেলুন ফাটাও", "সঠিক ধ্বনির বেলুন বেছে নাও", "ধ্বনি"),
                    "SENTENCE" to Triple("বাক্য তৈরি", "শব্দ সাজিয়ে সম্পূর্ণ বাক্য গঠন করো", "বাক্য"),
                    "RHYME" to Triple("ছন্দের শব্দ", "মিল থাকা শব্দগুলো খুঁজে বের করো", "ছন্দ"),
                    "MISSING_SOUND" to Triple("হারানো অক্ষর", "শব্দের খালি ঘরের অক্ষরটি বসাও", "অক্ষর"),
                    "LISTEN_FIND" to Triple("শোনো ও খোঁজো", "শুনে সঠিক ছবিটি নির্বাচন করো", "শ্রবণ"),
                    "OPPOSITES" to Triple("বিপরীত শব্দ", "বিপরীত অর্থের শব্দ মেলাও", "শব্দভাণ্ডার"),
                    "COUNT_COLOR" to Triple("গণনা ও সংখ্যা", "ছবি গুনে সঠিক সংখ্যা বেছে নাও", "সংখ্যা"),
                    "SPEED" to Triple("দ্রুত পঠন", "৩০ সেকেন্ডে দ্রুত পড়ার চ্যালেঞ্জ", "পঠন"),
                    "VOCAB" to Triple("শব্দের অর্থ", "ছবি দেখে নতুন শব্দ শেখো", "ছবি")
                )
            )

            "en_ta" -> SeekhAppStrings(
                isRtl = false,
                tabLibrary = "நூலகம்",
                tabPrizes = "பரிசுகள்",
                tabActivity = "செயல்பாடு",
                readCta = "படிக்க",
                playCta = "விளையாடு",
                seeAll = "அனைத்தும்",
                levelPrefix = "நிலை",
                alphabetBookTag = "தமிழ் எழுத்துக்கள் • அ முதல் ன",
                storyOfTheDayTag = "இன்றைய சிறப்பு புத்தகம்",
                searchPlaceholder = "கதைகளைத் தேடுங்கள்...",
                sectionAssignedHomework = "வகுப்பு வீட்டுப்பாடம்",
                sectionRecommended = "பரிந்துரைக்கப்பட்ட கதைகள்",
                sectionLevel1 = "நிலை 1 கதைகள்",
                sectionLevel2 = "நிலை 2 கதைகள்",
                sectionLevel3 = "நிலை 3 கதைகள்",
                sectionLevel4 = "நிலை 4 கதைகள்",
                sectionPlayGames = "கற்றல் விளையாட்டுகள்",
                sectionAnimalsNature = "விலங்குகள் மற்றும் இயற்கை",
                sectionSchoolKindness = "பள்ளி மற்றும் அன்பு",
                sectionPhonics = "எழுத்து மற்றும் ஒலி",
                sectionFamilyFriends = "குடும்பம் மற்றும் நண்பர்கள்",
                sectionNumbersRhymes = "எண்கள் மற்றும் பாடல்கள்",
                sectionStorySeries = "கதைத் தொடர்கள்",
                sectionAdventureTravel = "பயணம் மற்றும் சாகசம்",
                sectionScienceWeather = "அறிவியல் மற்றும் வானிலை",
                sectionBedtimeFolktales = "இரவு நேரக் கதைகள்",
                sectionCreateStory = "புதிய கதை உருவாக்குக",
                sectionReadAgain = "மீண்டும் படிக்க",
                moreLanguagesLabel = "மேலும் மொழிகள்",
                listenButton = "கேட்க",
                listeningButton = "கேட்கிறது...",
                speakButton = "பேச",
                speakingButton = "உங்கள் முறை...",
                listenSubLabel = "கதையைக் கேட்க",
                speakSubLabel = "நீங்கள் வாசிக்க",
                filterChipLabels = mapOf("All" to "அனைத்தும்", "Games" to "விளையாட்டு", "Level 1" to "நிலை 1", "Level 2" to "நிலை 2", "Level 3" to "நிலை 3", "Level 4" to "நிலை 4"),
                gameInfoByKey = mapOf(
                    "JUMBLE" to Triple("எழுத்துப் புதிர்", "எழுத்துக்களை வரிசைப்படுத்தி சொல் உருவாக்கு", "எழுத்து"),
                    "BALLOON" to Triple("பலூன் உடை", "சரியான ஒலி உள்ள பலூனைத் தொடு", "ஒலிகள்"),
                    "SENTENCE" to Triple("வாக்கியம் அமை", "சொற்களை இணைத்து வாக்கியம் உருவாக்கு", "வாக்கியம்"),
                    "RHYME" to Triple("ஓசைச் சொற்கள்", "ஒரே ஓசையுடைய சொற்களை இணை", "ஓசை"),
                    "MISSING_SOUND" to Triple("விடுபட்ட எழுத்து", "சொல்லில் விடுபட்ட எழுத்தைக் கண்டுபிடி", "எழுத்துக்கள்"),
                    "LISTEN_FIND" to Triple("கேட்டுக் கண்டுபிடி", "குரலைக் கேட்டு சரியான படத்தைத் தேர்ந்தெடு", "கேட்டல்"),
                    "OPPOSITES" to Triple("எதிர்ச்சொற்கள்", "எதிர் பொருள் கொண்ட சொற்களை இணை", "சொற்கள்"),
                    "COUNT_COLOR" to Triple("எண்களும் கணக்கும்", "படங்களை எண்ணி விடையைக் கண்டுபிடி", "எண்கள்"),
                    "SPEED" to Triple("வேக வாசிப்பு", "30 விநாடி வாசிப்பு சவால்", "வாசிப்பு"),
                    "VOCAB" to Triple("சொல் பொருள்", "படங்களுடன் புதிய சொற்களைக் கற்க", "படங்கள்")
                )
            )

            "en_te" -> SeekhAppStrings(
                isRtl = false,
                tabLibrary = "లైబ్రరీ",
                tabPrizes = "బహుమతులు",
                tabActivity = "ప్రగతి",
                readCta = "చదవండి",
                playCta = "ఆడండి",
                seeAll = "అన్నీ చూడండి",
                levelPrefix = "లెవెల్",
                alphabetBookTag = "తెలుగు వర్ణమాల • అ నుండి ఱ",
                storyOfTheDayTag = "నేటి ప్రత్యేక పుస్తకం",
                searchPlaceholder = "కథలను వెతకండి...",
                sectionAssignedHomework = "తరగతి హోంవర్క్",
                sectionRecommended = "సూచించిన కథలు",
                sectionLevel1 = "లెవెల్ 1 కథలు",
                sectionLevel2 = "లెవెల్ 2 కథలు",
                sectionLevel3 = "లెవెల్ 3 కథలు",
                sectionLevel4 = "లెవెల్ 4 కథలు",
                sectionPlayGames = "విద్యా గేమ్‌లు ఆడండి",
                sectionAnimalsNature = "జంతువులు & ప్రకృతి",
                sectionSchoolKindness = "పాఠశాల & మంచితనం",
                sectionPhonics = "అక్షరాలు & శబ్దాలు",
                sectionFamilyFriends = "కుటుంబం & స్నేహితులు",
                sectionNumbersRhymes = "సంఖ్యలు & పాటలు",
                sectionStorySeries = "కథల సిరీస్",
                sectionAdventureTravel = "సాహసం & ప్రయాణం",
                sectionScienceWeather = "సైన్స్ & వాతావరణం",
                sectionBedtimeFolktales = "జానపద కథలు",
                sectionCreateStory = "మీ సొంత కథను రాయండి",
                sectionReadAgain = "మళ్ళీ చదవండి",
                moreLanguagesLabel = "మరిన్ని భాషలు",
                listenButton = "వినండి",
                listeningButton = "వింటున్నారు...",
                speakButton = "చదవండి",
                speakingButton = "మీ వంతు...",
                listenSubLabel = "కథ వినడానికి",
                speakSubLabel = "పైకి చదవడానికి",
                filterChipLabels = mapOf("All" to "అన్నీ", "Games" to "ఆటలు", "Level 1" to "లెవెల్ 1", "Level 2" to "లెవెల్ 2", "Level 3" to "లెవెల్ 3", "Level 4" to "లెవెల్ 4"),
                gameInfoByKey = mapOf(
                    "JUMBLE" to Triple("అక్షరాల ఆట", "అక్షరాలను కలిపి సరైన పదం তৈরি చేయండి", "స్పెల్లింగ్"),
                    "BALLOON" to Triple("బెలూన్లు పగలగొట్టు", "సరైన శబ్దం ఉన్న బెలూన్‌ను ఎంచుకోండి", "శబ్దాలు"),
                    "SENTENCE" to Triple("వాక్య నిర్మాణం", "పదాలను వరుసలో పెట్టి వాక్యం చేయండి", "వాక్యాలు"),
                    "RHYME" to Triple("ప్రాస పదాలు", "ఒకే శబ్దంతో ముగిసే పదాలను జతపరచండి", "ప్రాస"),
                    "MISSING_SOUND" to Triple("తప్పిపోయిన అక్షరం", "పదంలో ఖాళీగా ఉన్న అక్షరాన్ని కనుగొనండి", "అక్షరాలు"),
                    "LISTEN_FIND" to Triple("విని గుర్తించు", "విని సరైన చిత్రాన్ని ఎంచుకోండి", "వినడం"),
                    "OPPOSITES" to Triple("వ్యతిరేక పదాలు", "వ్యతిరేక అర్థం గల పదాలను జతచేయండి", "పదాలు"),
                    "COUNT_COLOR" to Triple("అంకెలు & లెక్కలు", "బొమ్మలను లెక్కించి సమాధానం చెప్పండి", "అంకెలు"),
                    "SPEED" to Triple("వేగంగా చదవడం", "30 సెకన్ల పఠన సవాలు", "వేగం"),
                    "VOCAB" to Triple("పదాల అర్థాలు", "చిత్రాల ద్వారా కొత్త పదాలు నేర్చుకోండి", "చిత్రాలు")
                )
            )

            "en_mr" -> SeekhAppStrings(
                isRtl = false,
                tabLibrary = "लायब्ररी",
                tabPrizes = "बक्षिसे",
                tabActivity = "प्रगती",
                readCta = "वाचा",
                playCta = "खेळा",
                seeAll = "सर्व पहा",
                levelPrefix = "लेव्हल",
                alphabetBookTag = "मराठी मुळाक्षरे • अ ते ज्ञ",
                storyOfTheDayTag = "आजचे विशेष पुस्तक",
                searchPlaceholder = "गोष्टी शोधा...",
                sectionAssignedHomework = "शाळेचा गृहपाठ",
                sectionRecommended = "शिफारस केलेल्या गोष्टी",
                sectionLevel1 = "लेव्हल १ च्या गोष्टी",
                sectionLevel2 = "लेव्हल २ च्या गोष्टी",
                sectionLevel3 = "लेव्हल ३ च्या गोष्टी",
                sectionLevel4 = "लेव्हल ४ च्या गोष्टी",
                sectionPlayGames = "शैक्षणिक खेळ खेळा",
                sectionAnimalsNature = "प्राणी आणि निसर्ग",
                sectionSchoolKindness = "शाळा आणि चांगुलपणा",
                sectionPhonics = "मुळाक्षरे आणि ध्वनी",
                sectionFamilyFriends = "कुटुंब आणि मित्र",
                sectionNumbersRhymes = "अंक आणि गाणी",
                sectionStorySeries = "गोष्टींची मालिका",
                sectionAdventureTravel = "प्रवास आणि साहस",
                sectionScienceWeather = "विज्ञान आणि हवामान",
                sectionBedtimeFolktales = "लोककथा आणि गोष्टी",
                sectionCreateStory = "स्वतःची गोष्ट तयार करा",
                sectionReadAgain = "पुन्हा वाचा",
                moreLanguagesLabel = "इतर भाषा निवडा",
                listenButton = "ऐका",
                listeningButton = "ऐकत आहे...",
                speakButton = "बोला",
                speakingButton = "तुमची पाळी...",
                listenSubLabel = "गोष्ट ऐकण्यासाठी",
                speakSubLabel = "मोठ्याने वाचण्यासाठी",
                filterChipLabels = mapOf("All" to "सर्व", "Games" to "खेळ", "Level 1" to "लेव्हल १", "Level 2" to "लेव्हल २", "Level 3" to "लेव्हल ३", "Level 4" to "लेव्हल ४"),
                gameInfoByKey = mapOf(
                    "JUMBLE" to Triple("अक्षरे जुळवा", "अक्षरे योग्य क्रमाने लावून शब्द बनवा", "स्पेलिंग"),
                    "BALLOON" to Triple("फुगे फोडा", "योग्य ध्वनी असलेले फुगे फोडा", "ध्वनी"),
                    "SENTENCE" to Triple("वाक्य तयार करा", "शब्दांची मांडणी करून वाक्य पूर्ण करा", "वाक्य"),
                    "RHYME" to Triple("यमक जुळणारे शब्द", "सारख्या आवाजाचे शब्द जुळवा", "यमक"),
                    "MISSING_SOUND" to Triple("हरवलेले अक्षर", "शब्दातील रिकामे अक्षर ओळखा", "अक्षरे"),
                    "LISTEN_FIND" to Triple("ऐका आणि शोधा", "आवाज ऐकून योग्य चित्र निवडा", "श्रवण"),
                    "OPPOSITES" to Triple("विरुद्धार्थी शब्द", "उलट अर्थाचे शब्द जुळवा", "शब्दसंग्रह"),
                    "COUNT_COLOR" to Triple("मोजणी आणि अंक", "चित्रे मोजा आणि गणित सोडवा", "अंक"),
                    "SPEED" to Triple("जलद वाचन", "३० सेकंदांत जलद वाचण्याचे आव्हान", "ओघ"),
                    "VOCAB" to Triple("शब्दांचे अर्थ", "चित्रांवरून नवीन शब्द शिका", "चित्रे")
                )
            )

            "en_gu" -> SeekhAppStrings(
                isRtl = false,
                tabLibrary = "લાઇબ્રેરી",
                tabPrizes = "ઇનામો",
                tabActivity = "પ્રગતિ",
                readCta = "વાંચો",
                playCta = "રમો",
                seeAll = "બધું જુઓ",
                levelPrefix = "લેવલ",
                alphabetBookTag = "ગુજરાતી મૂળાક્ષરો • અ થી જ્ઞ",
                storyOfTheDayTag = "આજનું ખાસ પુસ્તક",
                searchPlaceholder = "વાર્તાઓ શોધો...",
                sectionAssignedHomework = "શાળાનું હોમવર્ક",
                sectionRecommended = "ભલામણ કરેલ વાર્તાઓ",
                sectionLevel1 = "લેવલ ૧ ની વાર્તાઓ",
                sectionLevel2 = "લેવલ ૨ ની વાર્તાઓ",
                sectionLevel3 = "લેવલ ૩ ની વાર્તાઓ",
                sectionLevel4 = "લેવલ ૪ ની વાર્તાઓ",
                sectionPlayGames = "જ્ઞાનવર્ધક રમતો રમો",
                sectionAnimalsNature = "પ્રાણીઓ અને પ્રકૃતિ",
                sectionSchoolKindness = "શાળા અને સારી ટેવો",
                sectionPhonics = "મૂળાક્ષરો અને અવાજ",
                sectionFamilyFriends = "પરિવાર અને મિત્રો",
                sectionNumbersRhymes = "અંકો અને જોડકણાં",
                sectionStorySeries = "વાર્તા શ્રેણી",
                sectionAdventureTravel = "પ્રવાસ અને સાહસ",
                sectionScienceWeather = "વિજ્ઞાન અને હવામાન",
                sectionBedtimeFolktales = "લોકવાર્તાઓ",
                sectionCreateStory = "તમારી પોતાની વાર્તા બનાવો",
                sectionReadAgain = "ફરીથી વાંચો",
                moreLanguagesLabel = "વધુ ભાષાઓ",
                listenButton = "સાંભળો",
                listeningButton = "સાંભળી રહ્યા છીએ...",
                speakButton = "બોલો",
                speakingButton = "તમારો વારો...",
                listenSubLabel = "વાર્તા સાંભળવા માટે",
                speakSubLabel = "મોટેથી વાંચવા માટે",
                filterChipLabels = mapOf("All" to "બધી", "Games" to "રમતો", "Level 1" to "લેવલ ૧", "Level 2" to "લેવલ ૨", "Level 3" to "લેવલ ૩", "Level 4" to "લેવલ ૪"),
                gameInfoByKey = mapOf(
                    "JUMBLE" to Triple("અક્ષરો ગોઠવો", "આડાઅવળા અક્ષરો ગોઠવી સાચો શબ્દ બનાવો", "સ્પેલિંગ"),
                    "BALLOON" to Triple("ફુગ્ગા ફોડો", "સાચા અવાજવાળા ફુગ્ગા પસંદ કરો", "અવાજ"),
                    "SENTENCE" to Triple("વાક્ય બનાવો", "શબ્દો ગોઠવીને સાચું વાક્ય બનાવો", "વાક્યો"),
                    "RHYME" to Triple("પ્રાસવાળા શબ્દો", "સરખા અવાજવાળા શબ્દો જોડો", "પ્રાસ"),
                    "MISSING_SOUND" to Triple("ખૂટતા અક્ષરો", "શબ્દમાં ખૂટતો અક્ષર શોધો", "અક્ષરો"),
                    "LISTEN_FIND" to Triple("સાંભળો અને શોધો", "અવાજ સાંભળીને સાચું ચિત્ર પસંદ કરો", "સાંભળવું"),
                    "OPPOSITES" to Triple("વિરોધી શબ્દો", "વિરોધી અર્થવાળા શબ્દો જોડો", "શબ્દભંડોળ"),
                    "COUNT_COLOR" to Triple("ગણતરી અને અંકો", "ચિત્રો ગણો અને સરળ ગણિત ઉકેલો", "અંકો"),
                    "SPEED" to Triple("ઝડપી વાંચન", "૩૦ સેકન્ડમાં ઝડપી વાંચનનો પડકાર", "પ્રવાહ"),
                    "VOCAB" to Triple("શબ્દોના અર્થ", "ચિત્રો દ્વારા નવા શબ્દો શીખો", "ચિત્રો")
                )
            )

            else -> SeekhAppStrings(
                isRtl = false,
                tabLibrary = "Library",
                tabPrizes = "Prizes",
                tabActivity = "Activity",
                readCta = "Read",
                playCta = "Play",
                seeAll = "See all",
                levelPrefix = "Level",
                alphabetBookTag = "Basic Alphabets • A to Z",
                storyOfTheDayTag = "Story of the day",
                searchPlaceholder = "Search stories by title, level, or phonics...",
                sectionAssignedHomework = "Assigned homework",
                sectionRecommended = "Recommended stories",
                sectionLevel1 = "Level 1 stories",
                sectionLevel2 = "Level 2 stories",
                sectionLevel3 = "Level 3 stories",
                sectionLevel4 = "Level 4 stories",
                sectionPlayGames = "Play games",
                sectionAnimalsNature = "Animals & Nature",
                sectionSchoolKindness = "School & Kindness",
                sectionPhonics = "Phonics stories",
                sectionFamilyFriends = "Family & Friends",
                sectionNumbersRhymes = "Numbers & Rhymes",
                sectionStorySeries = "Story Series",
                sectionAdventureTravel = "Adventure & Travel",
                sectionScienceWeather = "Science & Weather",
                sectionBedtimeFolktales = "Bedtime & Folktales",
                sectionCreateStory = "Create your own story",
                sectionReadAgain = "Read again",
                moreLanguagesLabel = "More languages",
                listenButton = "Listen",
                listeningButton = "Listening...",
                speakButton = "Speak",
                speakingButton = "Reading...",
                listenSubLabel = "Hear the story",
                speakSubLabel = "Read into mic",
                filterChipLabels = emptyMap(),
                gameInfoByKey = mapOf(
                    "JUMBLE" to Triple("Jumbled Words", "Unscramble letters to spell words", "Spelling"),
                    "BALLOON" to Triple("Pop The Balloons", "Pop rising phonics sound balloons", "Phonics"),
                    "SENTENCE" to Triple("Sentence Builder", "Arrange words in order to build sentences", "Sentences"),
                    "RHYME" to Triple("Rhyming Words", "Match words with the same ending sound", "Rhyming"),
                    "MISSING_SOUND" to Triple("Missing Letters", "Choose the missing letters in story words", "Letters"),
                    "LISTEN_FIND" to Triple("Listen & Find", "Listen to the voice clue and select the picture", "Listening"),
                    "OPPOSITES" to Triple("Opposite Words", "Match words with opposite meanings", "Vocabulary"),
                    "COUNT_COLOR" to Triple("Count & Numbers", "Count items and solve simple story math", "Numbers"),
                    "SPEED" to Triple("Speed Reading", "30-second reading fluency challenge", "Fluency"),
                    "VOCAB" to Triple("Word Meanings", "Learn picture vocabulary across 9 themes", "Pictures")
                )
            )
        }
    }

    // Complete Language-Specific Basic Alphabets Book Pinned at the Very Top of the Home Screen for each of the 11 languages!
    // STRICT 1-ALPHABET-PER-PAGE RULE: Every single page contains ONLY ONE single alphabet letter and its 100% matching object drawing!
    fun getAlphabetBookForLanguage(langCode: String): StoryBook {
        val coverImg = "books/alphabets/cover_alphabet.webp"
        val imgApple = "books/alphabets/alpha_a_apple.webp"
        val imgBall = "books/alphabets/alpha_b_ball.webp"
        val imgCat = "books/alphabets/alpha_c_cat.webp"
        val imgDrum = "books/alphabets/alpha_d_drum.webp"
        val imgElephant = "books/alphabets/alpha_e_elephant.webp"
        val imgFish = "books/alphabets/alpha_f_fish.webp"
        val imgGrapes = "books/alphabets/alpha_g_grapes.webp"
        val imgHouse = "books/alphabets/alpha_h_house.webp"
        val imgIcecream = "books/alphabets/alpha_i_icecream.webp"
        val imgJug = "books/alphabets/alpha_j_jug.webp"
        val imgKite = "books/alphabets/alpha_k_kite.webp"
        val imgLion = "books/alphabets/alpha_l_lion.webp"
        val imgMango = "books/alphabets/alpha_m_mango.webp"
        val imgNest = "books/alphabets/alpha_n_nest.webp"
        val imgOrange = "books/alphabets/alpha_o_orange.webp"
        val imgParrot = "books/alphabets/alpha_p_parrot.webp"
        val imgQueen = "books/alphabets/alpha_q_queen.webp"
        val imgRabbit = "books/alphabets/alpha_r_rabbit.webp"
        val imgSun = "books/alphabets/alpha_s_sun.webp"
        val imgTree = "books/alphabets/alpha_t_tree.webp"
        val imgUmbrella = "books/alphabets/alpha_u_umbrella.webp"
        val imgVan = "books/alphabets/alpha_v_van.webp"
        val imgWatermelon = "books/alphabets/alpha_w_watermelon.webp"
        val imgXylophone = "books/alphabets/alpha_x_xylophone.webp"
        val imgYacht = "books/alphabets/alpha_y_yacht.webp"
        val imgZebra = "books/alphabets/alpha_z_zebra.webp"
        val imgAnar = "books/alphabets/alpha_anar_pomegranate.webp"
        val imgButterfly = "books/alphabets/alpha_butterfly.webp"
        val imgTomato = "books/alphabets/alpha_tomato.webp"
        val imgMoon = "books/alphabets/alpha_moon.webp"
        val imgTrain = "books/alphabets/alpha_train.webp"
        val imgMountain = "books/alphabets/alpha_mountain.webp"
        val imgOwl = "books/alphabets/alpha_owl.webp"
        val imgPencil = "books/alphabets/alpha_pencil.webp"
        val imgFlower = "books/alphabets/alpha_flower.webp"
        val imgDuck = "books/alphabets/alpha_duck.webp"
        val imgCamel = "books/alphabets/alpha_camel.webp"
        val imgHorse = "books/alphabets/alpha_horse.webp"
        val imgBicycle = "books/alphabets/alpha_bicycle.webp"
        val imgBook = "books/alphabets/alpha_book.webp"
        val imgBalloon = "books/alphabets/alpha_balloon.webp"
        val imgFrog = "books/alphabets/alpha_frog.webp"
        val imgTurtle = "books/alphabets/alpha_turtle.webp"
        val imgPeacock = "books/alphabets/alpha_peacock.webp"
        val imgPineapple = "books/alphabets/alpha_pineapple.webp"
        val imgSpoon = "books/alphabets/alpha_spoon.webp"

        fun buildSingleLetterBook(
            id: String,
            title: String,
            subtitleUrdu: String,
            subtitleHindi: String,
            author: String,
            phonicsTag: String,
            coverBadges: String,
            entries: List<Array<String>> // [letterBadge, imgPath, primaryText, secondaryLabel, practiceWord]
        ): StoryBook {
            val pages = entries.mapIndexed { idx, arr ->
                StoryPage(
                    pageNumber = idx + 1,
                    imageAssetPath = arr[1],
                    text = arr[2],
                    spanishTranslation = arr[3],
                    urduTranslation = arr[3],
                    hindiTranslation = arr[3],
                    practiceWords = if (arr.size > 4 && arr[4].isNotBlank()) setOf(arr[4]) else emptySet(),
                    alphabetBadgeLetters = arr[0]
                )
            }
            return StoryBook(
                id = id,
                title = title,
                nativeTitleUrdu = subtitleUrdu,
                nativeTitleHindi = subtitleHindi,
                author = author,
                illustrator = "Seekh Alphabet Studio",
                publisher = "Foundational Literacy Pack",
                level = 1,
                coverAssetPath = coverImg,
                isFeaturedHero = true,
                bookType = "ALPHABET",
                categoryTag = "Numbers & Rhymes",
                phonicsSkillTag = phonicsTag,
                alphabetBadgeLetters = coverBadges,
                pages = pages
            )
        }

        return when (langCode) {
            "en" -> buildSingleLetterBook(
                id = "alphabet_book_en",
                title = "English Alphabets (A to Z)",
                subtitleUrdu = "26 Letters • 1 Letter & Matching Picture on Every Page",
                subtitleHindi = "A to Z Phonics & Picture Words",
                author = "Seekh Reading Curriculum",
                phonicsTag = "Alphabets: A to Z",
                coverBadges = "A  B  C  D",
                entries = listOf(
                    arrayOf("A", imgApple, "A is for Apple. Look at the bright red apple with a fresh green leaf.", "A a • Apple", "apple"),
                    arrayOf("B", imgBall, "B is for Ball. Bounce the round soccer ball across the green grass.", "B b • Ball", "ball"),
                    arrayOf("C", imgCat, "C is for Cat. The cute orange cat walks happily in the sunny yard.", "C c • Cat", "friends"),
                    arrayOf("D", imgDrum, "D is for Drum. Tap the red drum with two wooden drumsticks.", "D d • Drum", "drummer"),
                    arrayOf("E", imgElephant, "E is for Elephant. The friendly grey elephant has big floppy ears and a long trunk.", "E e • Elephant", "bright"),
                    arrayOf("F", imgFish, "F is for Fish. The colorful tropical fish swims in the clear blue water.", "F f • Fish", "fish"),
                    arrayOf("G", imgGrapes, "G is for Grapes. A sweet bunch of purple grapes hangs on the green stem.", "G g • Grapes", "garden"),
                    arrayOf("H", imgHouse, "H is for House. A cozy little house with a red roof and a green tree.", "H h • House", "bright"),
                    arrayOf("I", imgIcecream, "I is for Ice cream. A delicious cold vanilla ice cream cone on a sunny day.", "I i • Ice cream", "bright"),
                    arrayOf("J", imgJug, "J is for Juice. Sip fresh sweet orange juice with a straw.", "J j • Juice", "bright"),
                    arrayOf("K", imgKite, "K is for Kite. The bright diamond kite flies high in the blue sky.", "K k • Kite", "sky"),
                    arrayOf("L", imgLion, "L is for Lion. The brave golden lion has a big fluffy brown mane.", "L l • Lion", "golden"),
                    arrayOf("M", imgMango, "M is for Mango. A sweet ripe golden mango with a green leaf.", "M m • Mango", "golden"),
                    arrayOf("N", imgNest, "N is for Nest. Blue bird eggs rest safely inside the woven twig nest.", "N n • Nest", "tree"),
                    arrayOf("O", imgOrange, "O is for Orange. A round juicy orange fruit with a fresh green leaf.", "O o • Orange", "bright"),
                    arrayOf("P", imgParrot, "P is for Parrot. The bright green parrot has a curved yellow beak.", "P p • Parrot", "green"),
                    arrayOf("Q", imgQueen, "Q is for Queen. The kind royal queen wears a shiny golden crown.", "Q q • Queen", "golden"),
                    arrayOf("R", imgRabbit, "R is for Rabbit. A fluffy white rabbit with long pink ears hops on the grass.", "R r • Rabbit", "friends"),
                    arrayOf("S", imgSun, "S is for Sun. The warm golden sun smiles brightly in the morning sky.", "S s • Sun", "sun"),
                    arrayOf("T", imgTree, "T is for Tree. A tall green shady tree grows in the sunny meadow.", "T t • Tree", "tree"),
                    arrayOf("U", imgUmbrella, "U is for Umbrella. Open the purple umbrella when the raindrops fall.", "U u • Umbrella", "umbrella"),
                    arrayOf("V", imgVan, "V is for Van. The family van drives smoothly down the road.", "V v • Van", "bright"),
                    arrayOf("W", imgWatermelon, "W is for Watermelon. A juicy red slice of watermelon with black seeds.", "W w • Watermelon", "red"),
                    arrayOf("X", imgXylophone, "X is for Xylophone. Tap the seven rainbow bars of the xylophone to make music.", "X x • Xylophone", "colors"),
                    arrayOf("Y", imgYacht, "Y is for Yacht. The white sailboat yacht glides across the blue ocean water.", "Y y • Yacht", "water"),
                    arrayOf("Z", imgZebra, "Z is for Zebra. The friendly zebra has bold black and white stripes.", "Z z • Zebra", "friends")
                )
            )

            "en_ur" -> buildSingleLetterBook(
                id = "alphabet_book_en_ur",
                title = "اردو حروفِ تہجی (ا، ب، پ، ت)",
                subtitleUrdu = "ا سے ے تک ہر صفحے پر ایک الگ حرف اور اس کی تصویر (37 صفحات)",
                subtitleHindi = "Urdu Alphabets (1 Letter Per Page)",
                author = "اردو بنیادی قاعدہ",
                phonicsTag = "حروفِ تہجی: ا تا ے",
                coverBadges = "ا  ب  پ  ت",
                entries = listOf(
                    arrayOf("ا", imgAnar, "Alif (ا) is for Anar (Pomegranate). Look at the ruby-red pomegranate and its sweet seeds.", "ا سے انار", "red"),
                    arrayOf("آ", imgMango, "Alif Madd (آ) is for Aam (Mango). A sweet ripe golden mango with a green leaf.", "آ سے آم", "golden"),
                    arrayOf("ب", imgCat, "Be (ب) is for Billi (Cat). The cute orange cat walks happily on the grass.", "ب سے بلی", "friends"),
                    arrayOf("پ", imgKite, "Pe (پ) is for Patang (Kite). The bright kite flies high in the blue sky.", "پ سے پتنگ", "sky"),
                    arrayOf("ت", imgButterfly, "Te (ت) is for Titli (Butterfly). A colorful butterfly with bright patterned wings.", "ت سے تتلی", "butterfly"),
                    arrayOf("ٹ", imgTomato, "Tte (ٹ) is for Tamatar (Tomato). A round juicy red tomato with a green stem.", "ٹ سے ٹماٹر", "red"),
                    arrayOf("ث", imgGrapes, "Se (ث) is for Samar (Fruit). A sweet bunch of purple grapes on the vine.", "ث سے ثمر (پھل)", "garden"),
                    arrayOf("ج", imgJug, "Jeem (ج) is for Juice. Fresh sweet fruit juice with a drinking straw.", "ج سے جوس", "bright"),
                    arrayOf("چ", imgMoon, "Che (چ) is for Chand (Moon). The golden crescent moon shines in the night sky.", "چ سے چاند", "moon"),
                    arrayOf("ح", imgBook, "He (ح) is for Hikayat (Storybook). Open the storybook to read a wonderful tale.", "ح سے حکایت (کتاب)", "books"),
                    arrayOf("خ", imgRabbit, "Khe (خ) is for Khargosh (Rabbit). A fluffy white rabbit with long ears.", "خ سے خرگوش", "friends"),
                    arrayOf("د", imgTree, "Dal (د) is for Darakht (Tree). A tall green shady tree in the meadow.", "د سے درخت", "tree"),
                    arrayOf("ڈ", imgDrum, "Ddal (ڈ) is for Dhol (Drum). A red drum with two wooden drumsticks.", "ڈ سے ڈھول", "drummer"),
                    arrayOf("ذ", imgOrange, "Zal (ذ) is for Zaiqa (Sweet Orange). A juicy round orange fruit.", "ذ سے ذائقہ دار پھل", "bright"),
                    arrayOf("ر", imgTrain, "Re (ر) is for Rail Gari (Train). The steam locomotive train rolls on the tracks.", "ر سے ریل گاڑی", "train"),
                    arrayOf("ڑ", imgMountain, "Rre (ڑ) is for Pahar (Mountain). A tall snow-capped mountain peak.", "ڑ — پہاڑ", "bright"),
                    arrayOf("ز", imgZebra, "Ze (ز) is for Zebra. A friendly zebra with black and white stripes.", "ز سے زیبرا", "friends"),
                    arrayOf("ژ", imgUmbrella, "Zhe (ژ) is for Zhala-bari (Rain & Umbrella). Open the umbrella in the rain.", "ژ سے ژالہ باری", "umbrella"),
                    arrayOf("س", imgApple, "Seen (س) is for Seb (Apple). A bright red apple with a fresh green leaf.", "س سے سیب", "apple"),
                    arrayOf("ش", imgLion, "Sheen (ش) is for Sher (Lion). The brave lion has a big brown mane.", "ش سے شیر", "golden"),
                    arrayOf("ص", imgHouse, "Suad (ص) is for Sahn (Courtyard House). A cozy home with a garden.", "ص سے صحن والا گھر", "bright"),
                    arrayOf("ض", imgSun, "Zuad (ض) is for Zia (Sunlight). The warm golden sun shines in the sky.", "ض سے ضیا (سورج)", "sun"),
                    arrayOf("ط", imgParrot, "Toe (ط) is for Tota (Parrot). A bright green parrot with a curved beak.", "ط سے طوطا", "green"),
                    arrayOf("ظ", imgSpoon, "Zoe (ظ) is for Zuroof (Spoon & Utensil). A shiny silver spoon on the table.", "ظ سے ظروف (چمچ)", "bright"),
                    arrayOf("ع", imgOwl, "Ain (ع) is for Uqaab / Owl. A wise feathered bird perched in the meadow.", "ع سے عقاب", "bright"),
                    arrayOf("غ", imgBalloon, "Ghain (غ) is for Ghubara (Balloon). A bright red balloon floating in the air.", "غ سے غبارہ", "red"),
                    arrayOf("ف", imgFlower, "Fe (ف) is for Fasl-e-Bahar (Blooming Flower). A pink lotus flower in bloom.", "ف سے فصلِ بہار کا پھول", "flower"),
                    arrayOf("ق", imgPencil, "Qaf (ق) is for Qalam (Pencil). A sharpened yellow pencil for writing.", "ق سے قلم", "books"),
                    arrayOf("ک", imgYacht, "Kaf (ک) is for Kashti (Sailboat). A white sailboat gliding on blue water.", "ک سے کشتی", "water"),
                    arrayOf("گ", imgBall, "Gaf (گ) is for Gend (Ball). A round ball rolling on the green grass.", "گ سے گیند", "ball"),
                    arrayOf("ل", imgOrange, "Lam (ل) is for Leemu (Citrus). A fresh juicy citrus fruit with a green leaf.", "ل سے لیموں", "bright"),
                    arrayOf("م", imgFish, "Meem (م) is for Machhli (Fish). A colorful fish swimming in blue water.", "م سے مچھلی", "fish"),
                    arrayOf("ن", imgNest, "Noon (ن) is forasheen / Ghonsla (Nest). Blue bird eggs inside a twig nest.", "ن سے ننھا گھونسلہ", "tree"),
                    arrayOf("و", imgVan, "Wao (و) is for Van. A family van parked on the sunny road.", "و سے وین", "bright"),
                    arrayOf("ہ", imgElephant, "He (ہ) is for Hathi (Elephant). A friendly grey elephant with big ears.", "ہ سے ہاتھی", "water"),
                    arrayOf("ی", imgYacht, "Choti Ye (ی) is for Yacht (Sailboat). A graceful sailboat on the sea.", "ی سے یکہ و بادبانی کشتی", "water"),
                    arrayOf("ے", imgSun, "Bari Ye (ے) is for Tare & Suraj. You learned every single Urdu alphabet letter!", "ے — چمکتے تارے اور سورج", "sun")
                )
            )

            "en_hi" -> buildSingleLetterBook(
                id = "alphabet_book_en_hi",
                title = "हिंदी वर्णमाला (अ, आ, इ, क, ख)",
                subtitleUrdu = "हर पृष्ठ पर एक अलग अक्षर और उसका चित्र (33 पृष्ठ)",
                subtitleHindi = "हर पृष्ठ पर एक अलग अक्षर और उसका चित्र",
                author = "हिंदी अक्षर ज्ञान",
                phonicsTag = "वर्णमाला: अ से ज्ञ",
                coverBadges = "अ  आ  इ  क",
                entries = listOf(
                    arrayOf("अ", imgAnar, "A (अ) is for Anar (Pomegranate). A ruby-red pomegranate with sweet seeds.", "अ से अनार", "red"),
                    arrayOf("आ", imgMango, "Aa (आ) is for Aam (Mango). A sweet ripe golden mango with a green leaf.", "आ से आम", "golden"),
                    arrayOf("इ", imgHouse, "I (इ) is for Imarat (Building / House). A cozy house with a red roof.", "इ से इमारत", "bright"),
                    arrayOf("ई", imgIcecream, "Ee (ई) is for Eeshwar's sweet Ice-cream treat on a sunny day.", "ई से ईख और आइसक्रीम", "bright"),
                    arrayOf("उ", imgOwl, "U (उ) is for Ullu (Owl). A wise brown owl with big round eyes.", "उ से उल्लू", "bright"),
                    arrayOf("ऊ", imgCamel, "Oo (ऊ) is for Oont (Camel). A tall desert camel walking on the hill.", "ऊ से ऊँट", "bright"),
                    arrayOf("ए", imgApple, "E (ए) is for Ek Seb (One Apple). One bright red apple with a green leaf.", "ए से एक सेब", "apple"),
                    arrayOf("ऐ", imgElephant, "Ai (ऐ) is for Airavat (Royal Elephant). A friendly grey elephant.", "ऐ से ऐरावत (हाथी)", "water"),
                    arrayOf("ओ", imgOrange, "O (ओ) is for Orange & ओस (Morning Dew on Fruit). A fresh juicy citrus fruit.", "ओ से ओस और संतरा", "bright"),
                    arrayOf("अं", imgGrapes, "Am (अं) is for Angoor (Grapes). A sweet bunch of purple grapes.", "अं से अंगूर", "garden"),
                    arrayOf("क", imgFlower, "Ka (क) is for Kamal (Lotus Flower). A pink lotus blooming in the morning.", "क से कमल", "flower"),
                    arrayOf("ख", imgRabbit, "Kha (ख) is for Khargosh (Rabbit). A fluffy white rabbit on the grass.", "ख से खरगोश", "friends"),
                    arrayOf("ग", imgBall, "Ga (ग) is for Gend (Ball). A round ball rolling across the green lawn.", "ग से गेंद", "ball"),
                    arrayOf("घ", imgHouse, "Gha (घ) is for Ghar (Home). A warm little house with a garden tree.", "घ से घर", "bright"),
                    arrayOf("च", imgSpoon, "Cha (च) is for Chamach (Spoon). A shiny silver spoon on the table.", "च से चम्मच", "bright"),
                    arrayOf("छ", imgUmbrella, "Chha (छ) is for Chhatri (Umbrella). Open the umbrella when raindrops fall.", "छ से छतरी", "umbrella"),
                    arrayOf("ज", imgYacht, "Ja (ज) is for Jahaz (Sailboat). A white sailboat gliding across blue water.", "ज से जहाज़ (नाव)", "water"),
                    arrayOf("ट", imgTomato, "Ta (ट) is for Tamatar (Tomato). A bright red juicy tomato.", "ट से टमाटर", "red"),
                    arrayOf("ड", imgDrum, "Da (ड) is for Damru & Drum. Tap the red drum with two wooden sticks.", "ड से डमरू और ढोल", "drummer"),
                    arrayOf("त", imgWatermelon, "Ta (त) is for Tarbooz (Watermelon). A sweet juicy red slice of watermelon.", "त से तरबूज़", "red"),
                    arrayOf("द", imgTree, "Da (द) is for Darakht / Vriksh (Tree). A tall green shady tree.", "द से दरख़्त (पेड़)", "tree"),
                    arrayOf("न", imgNest, "Na (न) is for Neer (Bird's Nest). Blue bird eggs resting inside a twig nest.", "न से नीड़ (घोंसला)", "tree"),
                    arrayOf("प", imgKite, "Pa (प) is for Patang (Kite). A colorful diamond kite soaring in the sky.", "प से पतंग", "sky"),
                    arrayOf("फ", imgApple, "Pha (फ) is for Phal (Fruit). A fresh red apple from the orchard.", "फ से फल", "apple"),
                    arrayOf("ब", imgDuck, "Ba (ब) is for Batakh (Duck). A swimming duck on the blue pond.", "ब से बत्तख", "water"),
                    arrayOf("म", imgFish, "Ma (म) is for Machhli (Fish). A colorful tropical fish swimming in water.", "म से मछली", "fish"),
                    arrayOf("य", imgYacht, "Ya (य) is for Yacht (Nauka). A sailboat travelling across the water.", "य से यान (नौका)", "water"),
                    arrayOf("र", imgTrain, "Ra (र) is for Railgari (Train). A steam locomotive train on the tracks.", "र से रेलगाड़ी", "train"),
                    arrayOf("ल", imgPencil, "La (ल) is for Lekhni (Pencil). A sharpened yellow pencil for writing.", "ल से लेखनी (पेंसिल)", "books"),
                    arrayOf("व", imgVan, "Va (व) is for Van. A family van parked on the sunny road.", "व से वैन", "bright"),
                    arrayOf("श", imgLion, "Sha (श) is for Sher (Lion). A brave golden lion with a fluffy mane.", "श से शेर", "golden"),
                    arrayOf("स", imgSun, "Sa (स) is for Suraj (Sun). The bright golden sun smiling in the sky.", "स से सूरज", "sun"),
                    arrayOf("ह", imgElephant, "Ha (ह) is for Hathi (Elephant). A friendly baby elephant with big ears.", "ह से हाथी", "water")
                )
            )

            "en_ar" -> buildSingleLetterBook(
                id = "alphabet_book_en_ar",
                title = "الحروف الأبجدية العربية (أ، ب، ت، ث)",
                subtitleUrdu = "كل حرف في صفحة مستقلة مع صورته المرسومة (28 صفحة)",
                subtitleHindi = "Arabic Alphabets (28 Letters • 1 Letter Per Page)",
                author = "منهج القراءة العربي",
                phonicsTag = "الحروف: أ إلى ي",
                coverBadges = "أ  ب  ت  ث",
                entries = listOf(
                    arrayOf("أ", imgRabbit, "Alif (أ) is for Arnab (Rabbit). A fluffy white rabbit with long ears.", "أ - أرنب", "friends"),
                    arrayOf("ب", imgDuck, "Ba (ب) is for Batta (Duck). A cheerful duck swimming in the blue pond.", "ب - بطة", "water"),
                    arrayOf("ت", imgApple, "Ta (ت) is for Tuffaha (Apple). A bright red apple with a fresh green leaf.", "ت - تفاحة", "apple"),
                    arrayOf("ث", imgIcecream, "Tha (ث) is for Thalj (Ice cream). A cold sweet ice cream cone.", "ث - ثلج ومثلجات", "bright"),
                    arrayOf("ج", imgCamel, "Jeem (ج) is for Jamal (Camel). A tall desert camel walking on the hill.", "ج - جمل", "bright"),
                    arrayOf("ح", imgHorse, "Ha (ح) is for Hisan (Horse). A swift brown horse running across the meadow.", "ح - حصان", "friends"),
                    arrayOf("خ", imgWatermelon, "Kha (خ) is for Khayr & Battikh (Melon). A juicy red slice of melon.", "خ - خيرات وفاكهة", "red"),
                    arrayOf("د", imgBicycle, "Dal (د) is for Darraja (Bicycle). A bicycle with two round wheels.", "د - دراجة", "bicycle"),
                    arrayOf("ذ", imgSun, "Dhal (ذ) is for Dhahab (Golden Sun). The golden sun shining in the sky.", "ذ - ذهب وشمس ذهبية", "sun"),
                    arrayOf("ر", imgAnar, "Ra (ر) is for Rumman (Pomegranate). A ruby-red pomegranate with sweet seeds.", "ر - رمان", "red"),
                    arrayOf("ز", imgFlower, "Zay (ز) is for Zahra (Flower). A blooming pink lotus flower.", "ز - زهرة", "flower"),
                    arrayOf("س", imgFish, "Seen (س) is for Samaka (Fish). A colorful fish swimming in blue water.", "س - سمكة", "fish"),
                    arrayOf("ش", imgTree, "Sheen (ش) is for Shajara (Tree). A tall green shady tree in the meadow.", "ش - شجرة", "tree"),
                    arrayOf("ص", imgOwl, "Sad (ص) is for Saqr / Bird. A feathered bird perched on the hill.", "ص - صقر وطائر", "bright"),
                    arrayOf("ض", imgFrog, "Dad (ض) is for Difda' (Frog). A friendly green frog by the pond.", "ض - ضفدع", "frog"),
                    arrayOf("ط", imgParrot, "Ta (ط) is for Ta'ir (Parrot). A bright green parrot with a curved beak.", "ط - طائر الببغاء", "green"),
                    arrayOf("ظ", imgUmbrella, "Za (ظ) is for Zill (Shade Umbrella). An open umbrella giving shade.", "ظ - ظل ومظلة", "umbrella"),
                    arrayOf("ع", imgGrapes, "Ain (ع) is for Inab (Grapes). A sweet bunch of purple grapes.", "ع - عنب", "garden"),
                    arrayOf("غ", imgBalloon, "Ghain (غ) is for Ghubara / Balloon floating up toward the clouds.", "غ - غيمة وبالون", "sky"),
                    arrayOf("ف", imgElephant, "Fa (ف) is for Feel (Elephant). A friendly grey elephant with a long trunk.", "ف - فيل", "water"),
                    arrayOf("ق", imgCat, "Qaf (ق) is for Qitta (Cat). A cute orange cat walking on the grass.", "ق - قطة", "friends"),
                    arrayOf("ك", imgBall, "Kaf (ك) is for Kura (Ball). A round ball rolling on the green lawn.", "ك - كرة", "ball"),
                    arrayOf("ل", imgOrange, "Lam (ل) is for Laymoon (Citrus). A fresh juicy citrus fruit with a leaf.", "ل - ليمون وبرتقال", "bright"),
                    arrayOf("م", imgMango, "Meem (م) is for Mango. A sweet ripe golden mango with a green leaf.", "م - مانجو", "golden"),
                    arrayOf("ن", imgNest, "Noon (ن) is for عش النجم (Bird's Nest). Smooth blue bird eggs in a nest.", "ن - نجم وعش", "tree"),
                    arrayOf("هـ", imgMoon, "Ha (هـ) is for Hilal (Crescent Moon). The golden crescent moon in the night sky.", "هـ - هلال", "moon"),
                    arrayOf("و", imgFlower, "Waw (و) is for Warda (Rose / Flower). A blooming pink flower.", "و - وردة", "flower"),
                    arrayOf("ي", imgYacht, "Ya (ي) is for Yakht (Sailboat Yacht). A white sailboat gliding on the sea.", "ي - يخت شراعي", "water")
                )
            )

            "en_es" -> buildSingleLetterBook(
                id = "alphabet_book_en_es",
                title = "El Abecedario Español (A a Z, Ñ)",
                subtitleUrdu = "27 Letras • 1 Letra y su dibujo en cada página",
                subtitleHindi = "Spanish Alphabets (1 Letter Per Page)",
                author = "Seekh Español",
                phonicsTag = "Abecedario: A - Z",
                coverBadges = "A  B  C  Ñ",
                entries = listOf(
                    arrayOf("A", imgTree, "A is for Árbol (Tree). A tall green shady tree in the sunny meadow.", "A a • Árbol", "tree"),
                    arrayOf("B", imgBall, "B is for Balón (Ball). A round soccer ball on the green grass.", "B b • Balón", "ball"),
                    arrayOf("C", imgHouse, "C is for Casa (House). A cozy little house with a red roof.", "C c • Casa", "bright"),
                    arrayOf("D", imgDrum, "D is for Tambor de Día (Drum). Tap the red drum with two wooden sticks.", "D d • Día de Tambor", "drummer"),
                    arrayOf("E", imgElephant, "E is for Elefante (Elephant). A friendly grey elephant with big ears.", "E e • Elefante", "water"),
                    arrayOf("F", imgFlower, "F is for Flor (Flower). A blooming pink lotus flower in the garden.", "F f • Flor", "flower"),
                    arrayOf("G", imgCat, "G is for Gato (Cat). A cute orange cat walking in the yard.", "G g • Gato", "friends"),
                    arrayOf("H", imgIcecream, "H is for Helado (Ice cream). A cold sweet vanilla ice cream cone.", "H h • Helado", "bright"),
                    arrayOf("I", imgHouse, "I is for Isla y Hogar. A peaceful home surrounded by green grass.", "I i • Isla", "bright"),
                    arrayOf("J", imgJug, "J is for Jugo (Juice). Fresh sweet fruit juice with a straw.", "J j • Jugo", "bright"),
                    arrayOf("K", imgKite, "K is for Cometa / Kite flying high in the sunny blue sky.", "K k • Cometa", "sky"),
                    arrayOf("L", imgLion, "L is for León (Lion). A brave golden lion with a big brown mane.", "L l • León", "golden"),
                    arrayOf("M", imgMango, "M is for Mango. A sweet ripe golden mango with a green leaf.", "M m • Mango", "golden"),
                    arrayOf("N", imgNest, "N is for Nido (Nest). Three blue bird eggs inside a woven twig nest.", "N n • Nido", "tree"),
                    arrayOf("Ñ", imgPineapple, "Ñ is for Piña (Pineapple). A sweet tropical golden pineapple.", "Ñ ñ • Piña", "golden"),
                    arrayOf("O", imgOwl, "O is for Ojos de Búho (Owl). A wise brown owl with big eyes.", "O o • Ojos de Búho", "bright"),
                    arrayOf("P", imgParrot, "P is for Perico (Parrot). A bright green parrot with a curved beak.", "P p • Perico", "green"),
                    arrayOf("Q", imgQueen, "Q is for Queen / Reina. A kind royal queen wearing a golden crown.", "Q q • Reina", "golden"),
                    arrayOf("R", imgRabbit, "R is for Ratón y Conejo (Rabbit). A fluffy white rabbit on the grass.", "R r • Conejo", "friends"),
                    arrayOf("S", imgSun, "S is for Sol (Sun). The warm golden sun smiling in the sky.", "S s • Sol", "sun"),
                    arrayOf("T", imgTrain, "T is for Tren (Train). A colorful steam locomotive train on the tracks.", "T t • Tren", "train"),
                    arrayOf("U", imgGrapes, "U is for Uvas (Grapes). A sweet bunch of purple grapes on the stem.", "U u • Uvas", "garden"),
                    arrayOf("V", imgVan, "V is for Vagón (Van). A family van driving down the sunny road.", "V v • Vagón", "bright"),
                    arrayOf("W", imgWatermelon, "W is for Watermelon / Sandía. A juicy red slice of watermelon.", "W w • Sandía", "red"),
                    arrayOf("X", imgXylophone, "X is for Xilófono (Xylophone). A rainbow wooden xylophone with two mallets.", "X x • Xilófono", "colors"),
                    arrayOf("Y", imgYacht, "Y is for Yate (Yacht). A white sailboat yacht gliding across blue water.", "Y y • Yate", "water"),
                    arrayOf("Z", imgZebra, "Z is for Cebra (Zebra). A friendly zebra with black and white stripes.", "Z z • Cebra", "friends")
                )
            )

            "en_pt" -> buildSingleLetterBook(
                id = "alphabet_book_en_pt",
                title = "O Alfabeto Português (A a Z)",
                subtitleUrdu = "26 Letras • 1 Letra e sua ilustração em cada página",
                subtitleHindi = "Portuguese Alphabets (1 Letter Per Page)",
                author = "Seekh Português",
                phonicsTag = "Alfabeto: A - Z",
                coverBadges = "A  B  C  D",
                entries = listOf(
                    arrayOf("A", imgTree, "A is for Árvore (Tree). A tall green shady tree in the meadow.", "A a • Árvore", "tree"),
                    arrayOf("B", imgBall, "B is for Bola (Ball). A round ball rolling across the green lawn.", "B b • Bola", "ball"),
                    arrayOf("C", imgHouse, "C is for Casa (House). A cozy little house with a red roof.", "C c • Casa", "bright"),
                    arrayOf("D", imgDrum, "D is for Tambor (Drum). Tap the red drum with two wooden drumsticks.", "D d • Tambor", "drummer"),
                    arrayOf("E", imgElephant, "E is for Elefante (Elephant). A friendly grey elephant with big ears.", "E e • Elefante", "water"),
                    arrayOf("F", imgFlower, "F is for Flor (Flower). A blooming pink lotus flower.", "F f • Flor", "flower"),
                    arrayOf("G", imgCat, "G is for Gato (Cat). A cute orange cat walking in the yard.", "G g • Gato", "friends"),
                    arrayOf("H", imgHouse, "H is for Habitação (Home). A peaceful cottage house with a garden.", "H h • Habitação", "bright"),
                    arrayOf("I", imgIcecream, "I is for Ice-cream / Sorvete. A sweet cold cone on a sunny day.", "I i • Sorvete", "bright"),
                    arrayOf("J", imgJug, "J is for Jarra de Suco (Juice). Fresh sweet orange juice.", "J j • Jarra de Suco", "bright"),
                    arrayOf("K", imgKite, "K is for Kite / Pipa. A bright diamond kite soaring in the sky.", "K k • Pipa", "sky"),
                    arrayOf("L", imgLion, "L is for Leão (Lion). A brave golden lion with a fluffy mane.", "L l • Leão", "golden"),
                    arrayOf("M", imgMango, "M is for Manga (Mango). A sweet ripe golden mango with a green leaf.", "M m • Manga", "golden"),
                    arrayOf("N", imgNest, "N is for Ninho (Nest). Three blue bird eggs inside a twig nest.", "N n • Ninho", "tree"),
                    arrayOf("O", imgOwl, "O is for Olhos da Coruja (Owl). A wise brown owl on the hill.", "O o • Coruja", "bright"),
                    arrayOf("P", imgParrot, "P is for Papagaio (Parrot). A bright green parrot with a curved beak.", "P p • Papagaio", "green"),
                    arrayOf("Q", imgQueen, "Q is for Queen / Rainha. A kind queen wearing a golden crown.", "Q q • Rainha", "golden"),
                    arrayOf("R", imgRabbit, "R is for Coelho Rápido (Rabbit). A fluffy white rabbit on the grass.", "R r • Coelho", "friends"),
                    arrayOf("S", imgSun, "S is for Sol (Sun). The warm golden sun smiling in the sky.", "S s • Sol", "sun"),
                    arrayOf("T", imgTrain, "T is for Trem (Train). A steam locomotive train rolling on the tracks.", "T t • Trem", "train"),
                    arrayOf("U", imgGrapes, "U is for Uva (Grapes). A sweet bunch of purple grapes.", "U u • Uva", "garden"),
                    arrayOf("V", imgVan, "V is for Van. A family van driving down the sunny road.", "V v • Van", "bright"),
                    arrayOf("W", imgWatermelon, "W is for Watermelon / Melancia. A juicy red slice of watermelon.", "W w • Melancia", "red"),
                    arrayOf("X", imgXylophone, "X is for Xilofone (Xylophone). A rainbow wooden xylophone with mallets.", "X x • Xilofone", "colors"),
                    arrayOf("Y", imgYacht, "Y is for Yacht / Veleiro. A white sailboat gliding on blue water.", "Y y • Veleiro", "water"),
                    arrayOf("Z", imgZebra, "Z is for Zebra. A friendly zebra with black and white stripes.", "Z z • Zebra", "friends")
                )
            )

            "en_bn" -> buildSingleLetterBook(
                id = "alphabet_book_en_bn",
                title = "বাংলা বর্ণমালা (অ, আ, ই, ক, খ)",
                subtitleUrdu = "প্রতি পৃষ্ঠায় একটি করে অক্ষর ও তার ছবি (২০ পৃষ্ঠা)",
                subtitleHindi = "Bengali Alphabets (1 Letter Per Page)",
                author = "বাংলা বর্ণ পরিচয়",
                phonicsTag = "বর্ণমালা: অ - হ",
                coverBadges = "অ  আ  ক  খ",
                entries = listOf(
                    arrayOf("অ", imgPineapple, "O (অ) is for Anaros (Pineapple). A sweet golden tropical pineapple.", "অ - আনারস", "golden"),
                    arrayOf("আ", imgMango, "Aa (আ) is for Aam (Mango). A sweet ripe golden mango with a green leaf.", "আ - আম", "golden"),
                    arrayOf("ই", imgFish, "Hrosho-I (ই) is for Elish (Fish). A shiny fish swimming in blue water.", "ই - ইলিশ মাছ", "fish"),
                    arrayOf("ঈ", imgOwl, "Dirgho-I (ঈ) is for Eagle / Bird. A feathered bird perched on the hill.", "ঈ - ঈগল পাখি", "bright"),
                    arrayOf("উ", imgCamel, "Hrosho-U (উ) is for Uth (Camel). A tall camel walking on the hill.", "উ - উট", "bright"),
                    arrayOf("ঊ", imgSun, "Dirgho-U (ঊ) is for Usha (Morning Sun). The golden sun shining in the morning.", "ঊ - ঊষা (সকালের সূর্য)", "sun"),
                    arrayOf("এ", imgTrain, "E (এ) is for Express Train. A steam locomotive train rolling on the tracks.", "এ - এক্সপ্রেস ট্রেন", "train"),
                    arrayOf("ও", imgOrange, "O (ও) is for Komola (Orange). A round juicy citrus fruit.", "ও - কমলালেবু", "bright"),
                    arrayOf("ক", imgFlower, "Ko (ক) is for Komol (Lotus Flower). A blooming pink lotus flower.", "ক - কমল (পদ্মফুল)", "flower"),
                    arrayOf("খ", imgRabbit, "Kho (খ) is for Khargosh (Rabbit). A fluffy white rabbit on the grass.", "খ - খরগোশ", "friends"),
                    arrayOf("গ", imgTree, "Go (গ) is for Gach (Tree). A tall green shady tree in the meadow.", "গ - গাছ", "tree"),
                    arrayOf("ঘ", imgHouse, "Gho (ঘ) is for Ghor (House). A cozy little house with a red roof.", "ঘ - ঘর", "bright"),
                    arrayOf("চ", imgMoon, "Cho (চ) is for Chand (Moon). The golden crescent moon in the night sky.", "চ - চাঁদ", "moon"),
                    arrayOf("ছ", imgUmbrella, "Chho (ছ) is for Chhata (Umbrella). Open the umbrella when raindrops fall.", "ছ - ছাতা", "umbrella"),
                    arrayOf("জ", imgYacht, "Jo (জ) is for Jahaj (Sailboat). A white sailboat gliding on blue water.", "জ - জাহাজ ও নৌকা", "water"),
                    arrayOf("ত", imgWatermelon, "To (ত) is for Tormuj (Watermelon). A sweet juicy red slice of watermelon.", "ত - তরমুজ", "red"),
                    arrayOf("প", imgParrot, "Po (প) is for Pakhi (Parrot). A bright green parrot with a curved beak.", "প - পাখি (টিয়া)", "green"),
                    arrayOf("ব", imgBook, "Bo (ব) is for Boi (Book). Open the storybook to read together.", "ব - বই", "books"),
                    arrayOf("স", imgLion, "So (স) is for Singha (Lion). A brave golden lion with a fluffy mane.", "স - সিংহ", "golden"),
                    arrayOf("হ", imgElephant, "Ho (হ) is for Hati (Elephant). A friendly grey elephant with big ears.", "হ - হাতি", "water")
                )
            )

            "en_ta" -> buildSingleLetterBook(
                id = "alphabet_book_en_ta",
                title = "தமிழ் எழுத்துக்கள் (அ, ஆ, இ, க, ச)",
                subtitleUrdu = "ஒவ்வொரு பக்கத்திலும் ஒரு எழுத்து மற்றும் அதன் படம் (18 பக்கங்கள்)",
                subtitleHindi = "Tamil Alphabets (1 Letter Per Page)",
                author = "தமிழ் அரிச்சுவடி",
                phonicsTag = "தமிழ்: அ - வ",
                coverBadges = "அ  ஆ  இ  க",
                entries = listOf(
                    arrayOf("அ", imgPineapple, "A (அ) is for Annasi (Pineapple). A sweet golden tropical pineapple.", "அ - அன்னாசிப்பழம்", "golden"),
                    arrayOf("ஆ", imgApple, "Aa (ஆ) is for Apple. A bright red apple with a fresh green leaf.", "ஆ - ஆப்பிள்", "apple"),
                    arrayOf("இ", imgHouse, "I (இ) is for Illam (Home). A cozy house with a red roof and garden.", "இ - இல்லம் (வீடு)", "bright"),
                    arrayOf("ஈ", imgButterfly, "Ee (ஈ) is for Flying Insect / Butterfly with bright colorful wings.", "ஈ - வண்ணத்துப்பூச்சி", "butterfly"),
                    arrayOf("உ", imgOrange, "U (உ) is for Urundai Pazham (Round Orange). A juicy citrus fruit.", "உ - ஆரஞ்சு பழம்", "bright"),
                    arrayOf("ஊ", imgBalloon, "Oo (ஊ) is for Oothu Balloon. A bright red balloon floating in the air.", "ஊ - பலூன்", "red"),
                    arrayOf("எ", imgOwl, "E (எ) is for Ezhil Bird (Owl). A wise brown owl on the hill.", "எ - ஆந்தை", "bright"),
                    arrayOf("ஏ", imgElephant, "Ae (ஏ) is for Yanai (Elephant). A friendly grey elephant with big ears.", "ஏ - யானை", "water"),
                    arrayOf("ஓ", imgYacht, "Oo (ஓ) is for Odam (Sailboat). A white sailboat gliding on blue water.", "ஓ - ஓடம் (படகு)", "water"),
                    arrayOf("க", imgTurtle, "Ka (க) is for Kadal Aamai (Turtle). A friendly green turtle by the water.", "க - கடல் ஆமை", "water"),
                    arrayOf("ச", imgSun, "Cha (ச) is for Suryan (Sun). The warm golden sun smiling in the sky.", "ச - சூரியன்", "sun"),
                    arrayOf("த", imgFrog, "Tha (த) is for Thavalai (Frog). A cheerful green frog near the pond.", "த - தவளை", "frog"),
                    arrayOf("ப", imgBall, "Pa (ப) is for Panthu (Ball). A round ball rolling on the green grass.", "ப - பந்து", "ball"),
                    arrayOf("ம", imgTree, "Ma (ம) is for Maram (Tree). A tall green shady tree in the meadow.", "ம - மரம்", "tree"),
                    arrayOf("ய", imgElephant, "Ya (ய) is for Yanai (Elephant). The baby elephant has a curved trunk.", "ய - யானை", "water"),
                    arrayOf("ர", imgTrain, "Ra (ர) is for Rayil (Train). A steam locomotive train rolling on the tracks.", "ர - ரயில்", "train"),
                    arrayOf("ல", imgLion, "La (ல) is for Singam (Lion). A brave golden lion with a fluffy mane.", "ல - சிங்கம்", "golden"),
                    arrayOf("வ", imgParrot, "Va (வ) is for Vanna Kili (Parrot). A bright green parrot with a yellow beak.", "வ - வண்ணக் கிளி", "green")
                )
            )

            "en_te" -> buildSingleLetterBook(
                id = "alphabet_book_en_te",
                title = "తెలుగు వర్ణమాల (అ, ఆ, ఇ, క, గ)",
                subtitleUrdu = "ప్రతి పేజీలో ఒకే అక్షరం మరియు దాని బొమ్మ (18 పేజీలు)",
                subtitleHindi = "Telugu Alphabets (1 Letter Per Page)",
                author = "తెలుగు అక్షరమాల",
                phonicsTag = "వర్ణమాల: అ - హ",
                coverBadges = "అ  ఆ  ఇ  క",
                entries = listOf(
                    arrayOf("అ", imgPineapple, "A (అ) is for Anasa (Pineapple). A sweet golden tropical pineapple.", "అ - అనాసపండు", "golden"),
                    arrayOf("ఆ", imgApple, "Aa (ఆ) is for Apple. A bright red apple with a fresh green leaf.", "ఆ - ஆపిల్ పండు", "apple"),
                    arrayOf("ఇ", imgHouse, "I (ఇ) is for Illu (House). A cozy house with a red roof and garden.", "ఇ - ఇల్లు", "bright"),
                    arrayOf("ఈ", imgButterfly, "Ee (ఈ) is for Eeka & Butterfly. A colorful butterfly in the meadow.", "ఈ - సీతాకోకచిలుక", "butterfly"),
                    arrayOf("ఉ", imgOwl, "U (ఉ) is for Owl / Bird perched on the sunny hill.", "ఉ - గుడ్లగూబ", "bright"),
                    arrayOf("ఏ", imgElephant, "Ae (ఏ) is for Aenugu (Elephant). A friendly grey elephant with big ears.", "ఏ - ఏనుగు", "water"),
                    arrayOf("ఓ", imgYacht, "Oo (ఓ) is for Oda (Sailboat). A white sailboat gliding on blue water.", "ఓ - ఓడ", "water"),
                    arrayOf("క", imgFlower, "Ka (క) is for Kamalam (Lotus). A blooming pink lotus flower.", "క - కమలం", "flower"),
                    arrayOf("గ", imgNest, "Ga (గ) is for Gudu (Bird's Nest). Three blue eggs resting inside a nest.", "గ - గూడు", "tree"),
                    arrayOf("చ", imgMoon, "Cha (చ) is for Chandamama (Moon). The golden crescent moon in the night sky.", "చ - చందమామ", "moon"),
                    arrayOf("छ", imgUmbrella, "Chha (ఛ) is for Chhatri (Umbrella). Open the umbrella when raindrops fall.", "ఛ - గొడుగు", "umbrella"),
                    arrayOf("ట", imgTomato, "Ta (ట) is for Tamata (Tomato). A round juicy red tomato.", "ట - టమాటా", "red"),
                    arrayOf("प", imgParrot, "Pa (ప) is for Chiluka (Parrot). A bright green parrot with a curved beak.", "ప - రామచిలుక", "green"),
                    arrayOf("బ", imgBall, "Ba (బ) is for Banthi (Ball). A round ball rolling on the green grass.", "బ - బంతి", "ball"),
                    arrayOf("మ", imgMango, "Ma (మ) is for Mamidi (Mango). A sweet ripe golden mango with a leaf.", "మ - మామిడి పండు", "golden"),
                    arrayOf("ర", imgTrain, "Ra (ర) is for Railu (Train). A steam locomotive train on the tracks.", "ర - రైలు", "train"),
                    arrayOf("స", imgSun, "Sa (స) is for Suryudu (Sun). The warm golden sun smiling in the sky.", "స - సూర్యుడు", "sun"),
                    arrayOf("హ", imgLion, "Ha (హ) is for Simham (Lion). A brave golden lion with a fluffy mane.", "హ - సింహం", "golden")
                )
            )

            "en_mr" -> buildSingleLetterBook(
                id = "alphabet_book_en_mr",
                title = "मराठी मुळाक्षरे (अ, आ, इ, क, ख, ळ)",
                subtitleUrdu = "प्रत्येक पानावर एकच मुळाक्षर आणि त्याचे चित्र (२० पाने)",
                subtitleHindi = "Marathi Alphabets (1 Letter Per Page)",
                author = "मराठी अक्षरओळख",
                phonicsTag = "मुळाक्षरे: अ ते ज्ञ",
                coverBadges = "अ  आ  क  ळ",
                entries = listOf(
                    arrayOf("अ", imgPineapple, "A (अ) is for Ananas (Pineapple). A sweet golden tropical pineapple.", "अ - अननस", "golden"),
                    arrayOf("आ", imgTrain, "Aa (आ) is for Aag-gadi (Train). A steam locomotive train on the tracks.", "आ - आगगाडी", "train"),
                    arrayOf("इ", imgHouse, "I (इ) is for Imarat (House). A cozy house with a red roof and garden.", "इ - इमारत", "bright"),
                    arrayOf("उ", imgCamel, "U (उ) is for Unt (Camel). A tall camel walking on the hill.", "उ - उंट", "bright"),
                    arrayOf("अं", imgGrapes, "Am (अं) is for Angoor / Drakshe (Grapes). A sweet bunch of purple grapes.", "अं - द्राक्षे", "garden"),
                    arrayOf("क", imgFlower, "Ka (क) is for Kamal (Lotus). A blooming pink lotus flower.", "क - कमळ", "flower"),
                    arrayOf("ख", imgRabbit, "Kha (ख) is for Khargosh / Sasa (Rabbit). A fluffy white rabbit on the grass.", "ख - ससा", "friends"),
                    arrayOf("ग", imgBall, "Ga (ग) is for Gend / Chendu (Ball). A round ball rolling on the lawn.", "ग - चेंडू", "ball"),
                    arrayOf("घ", imgHouse, "Gha (घ) is for Ghar (Home). A warm little house with a red roof.", "घ - घर", "bright"),
                    arrayOf("च", imgMoon, "Cha (च) is for Chandoba (Moon). The golden crescent moon in the night sky.", "च - चांदोबा", "moon"),
                    arrayOf("छ", imgUmbrella, "Chha (छ) is for Chhatri (Umbrella). Open the umbrella when raindrops fall.", "छ - छत्री", "umbrella"),
                    arrayOf("ज", imgYacht, "Ja (ज) is for Jahaj (Sailboat). A white sailboat gliding on blue water.", "ज - जहाज", "water"),
                    arrayOf("झ", imgTree, "Jha (झ) is for Jhad (Tree). A tall green shady tree in the meadow.", "झ - झाड", "tree"),
                    arrayOf("ट", imgTomato, "Ta (ट) is for Tomato. A round juicy red tomato with a green stem.", "ट - टोमॅटो", "red"),
                    arrayOf("ढ", imgDrum, "Dha (ढ) is for Dhol (Drum). A red drum with two wooden drumsticks.", "ढ - ढोल", "drummer"),
                    arrayOf("प", imgParrot, "Pa (प) is for Popat (Parrot). A bright green parrot with a curved beak.", "प - पोपट", "green"),
                    arrayOf("फ", imgButterfly, "Pha (फ) is for Phulpakharu (Butterfly). A colorful butterfly in the garden.", "फ - फुलपाखरू", "butterfly"),
                    arrayOf("म", imgFish, "Ma (म) is for Masa (Fish). A colorful fish swimming in blue water.", "म - मासा", "fish"),
                    arrayOf("स", imgSun, "Sa (स) is for Surya (Sun). The warm golden sun smiling in the sky.", "स - सूर्य", "sun"),
                    arrayOf("ह", imgElephant, "Ha (ह) is for Hatti (Elephant). A friendly grey elephant with big ears.", "ह - हत्ती", "water")
                )
            )

            "en_gu" -> buildSingleLetterBook(
                id = "alphabet_book_en_gu",
                title = "ગુજરાતી મૂળાક્ષરો (અ, આ, ઇ, ક, ખ)",
                subtitleUrdu = "દરેક પાના પર એક જ મૂળાક્ષર અને તેનું ચિત્ર (૨૦ પાનાં)",
                subtitleHindi = "Gujarati Alphabets (1 Letter Per Page)",
                author = "ગુજરાતી અક્ષરજ્ઞાન",
                phonicsTag = "મૂળાક્ષરો: અ થી જ્ઞ",
                coverBadges = "અ  આ  ક  ખ",
                entries = listOf(
                    arrayOf("અ", imgPineapple, "A (અ) is for Ananas (Pineapple). A sweet golden tropical pineapple.", "અ - અનાનસ", "golden"),
                    arrayOf("આ", imgTrain, "Aa (આ) is for Aag-gadi (Train). A steam locomotive train on the tracks.", "આ - આગગાડી", "train"),
                    arrayOf("ઇ", imgHouse, "I (ઇ) is for Imarat (House). A cozy house with a red roof and garden.", "ઇ - ઇમારત", "bright"),
                    arrayOf("ઉ", imgCamel, "U (ઉ) is for Unt (Camel). A tall desert camel walking on the hill.", "ઉ - ઊંટ", "bright"),
                    arrayOf("અં", imgGrapes, "Am (અં) is for Draksh (Grapes). A sweet bunch of purple grapes.", "અં - દ્રાક્ષ", "garden"),
                    arrayOf("ક", imgFlower, "Ka (ક) is for Kamal (Lotus). A blooming pink lotus flower.", "ક - કમળ", "flower"),
                    arrayOf("ખ", imgRabbit, "Kha (ખ) is for Saslu (Rabbit). A fluffy white rabbit on the grass.", "ખ - સસલું", "friends"),
                    arrayOf("ગ", imgBall, "Ga (ગ) is for Dodo (Ball). A round ball rolling on the green grass.", "ગ - દડો", "ball"),
                    arrayOf("ઘ", imgHouse, "Gha (ઘ) is for Ghar (Home). A warm little house with a red roof.", "ઘ - ઘર", "bright"),
                    arrayOf("ચ", imgMoon, "Cha (ચ) is for Chando (Moon). The golden crescent moon in the night sky.", "ચ - ચાંદો", "moon"),
                    arrayOf("છ", imgUmbrella, "Chha (છ) is for Chhatri (Umbrella). Open the umbrella when raindrops fall.", "છ - છત્રી", "umbrella"),
                    arrayOf("જ", imgTree, "Ja (જ) is for Jhad (Tree). A tall green shady tree in the meadow.", "જ - ઝાડ", "tree"),
                    arrayOf("ટ", imgTomato, "Ta (ટ) is for Tametu (Tomato). A round juicy red tomato.", "ટ - ટામેટું", "red"),
                    arrayOf("ઢ", imgDrum, "Dha (ઢ) is for Dhol (Drum). A red drum with two wooden drumsticks.", "ઢ - ઢોલ", "drummer"),
                    arrayOf("ત", imgWatermelon, "Ta (ત) is for Tarbuch (Watermelon). A sweet juicy red slice of watermelon.", "ત - તરબૂચ", "red"),
                    arrayOf("પ", imgKite, "Pa (પ) is for Patang (Kite). A colorful diamond kite soaring in the sky.", "પ - પતંગ", "sky"),
                    arrayOf("ફ", imgButterfly, "Pha (ફ) is for Patangiyu (Butterfly). A colorful butterfly in the garden.", "ફ - પતંગિયું", "butterfly"),
                    arrayOf("મ", imgFish, "Ma (મ) is for Machhli (Fish). A colorful fish swimming in blue water.", "મ - માછલી", "fish"),
                    arrayOf("સ", imgSun, "Sa (સ) is for Suraj (Sun). The warm golden sun smiling in the sky.", "સ - સૂરજ", "sun"),
                    arrayOf("હ", imgElephant, "Ha (હ) is for Hathi (Elephant). A friendly grey elephant with big ears.", "હ - હાથી", "water")
                )
            )

            else -> getAlphabetBookForLanguage("en")
        }
    }

    // Complete Language-Specific "Days of the Week" (7 Pages: 1 Day per Page) Book across all 11 languages!
    fun getDaysOfWeekBookForLanguage(langCode: String): StoryBook {
        val imgMon = "books/alphabets/alpha_book.webp"
        val imgTue = "books/alphabets/alpha_pencil.webp"
        val imgWed = "books/alphabets/alpha_t_tree.webp"
        val imgThu = "books/alphabets/alpha_bicycle.webp"
        val imgFri = "books/alphabets/alpha_k_kite.webp"
        val imgSat = "books/alphabets/alpha_train.webp"
        val imgSun = "books/alphabets/alpha_s_sun.webp"

        data class DayLangPack(
            val title: String,
            val subtitle: String,
            val coverBadges: String,
            val days: List<Triple<String, String, String>> // (shortBadge, fullLabel, sentence)
        )

        val pack = when (langCode) {
            "en_ur" -> DayLangPack(
                title = "ہفتے کے دن • Days of the Week",
                subtitle = "7 دن • پیر سے اتوار تک",
                coverBadges = "پیر  منگل  بدھ",
                days = listOf(
                    Triple("پیر", "پہلا دن • پیر (Monday)", "Monday is Peer (پیر). We start the week by reading our favorite storybooks at school."),
                    Triple("منگل", "دوسرا دن • منگل (Tuesday)", "Tuesday is Mangal (منگل). We draw and write neat letters with a colorful pencil."),
                    Triple("بدھ", "تیسرا دن • بدھ (Wednesday)", "Wednesday is Budh (بدھ). We water the green trees and plants in the garden."),
                    Triple("جمعرات", "چوتھا دن • جمعرات (Thursday)", "Thursday is Jumerat (جمعرات). We ride our bicycle happily in the park."),
                    Triple("جمعہ", "پانچواں دن • جمعہ (Friday)", "Friday is Jummah (جمعہ). A bright colorful kite flies high in the Friday sky."),
                    Triple("ہفتہ", "چھٹا دن • ہفتہ (Saturday)", "Saturday is Hafta (ہفتہ). We enjoy a fun weekend train game with our friends."),
                    Triple("اتوار", "ساتواں دن • اتوار (Sunday)", "Sunday is Itwar (اتوار). The warm golden sun shines on a happy family Sunday.")
                )
            )
            "en_hi" -> DayLangPack(
                title = "सप्ताह के दिन • Days of the Week",
                subtitle = "7 दिन • सोमवार से रविवार",
                coverBadges = "सोम  मंगल  बुध",
                days = listOf(
                    Triple("सोम", "दिन 1 • सोमवार (Monday)", "Monday is Somvaar (सोमवार). We start the week reading our favorite books at school."),
                    Triple("मंगल", "दिन 2 • मंगलवार (Tuesday)", "Tuesday is Mangalvaar (मंगलवार). We draw and write neat words with a bright pencil."),
                    Triple("बुध", "दिन 3 • बुधवार (Wednesday)", "Wednesday is Budhvaar (बुधवार). We care for the green trees in the garden."),
                    Triple("गुरु", "दिन 4 • गुरुवार (Thursday)", "Thursday is Guruvaar (गुरुवार). We ride our bicycle outdoors with friends."),
                    Triple("शुक्र", "दिन 5 • शुक्रवार (Friday)", "Friday is Shukravaar (शुक्रवार). A colorful kite soars high in the sky."),
                    Triple("शनि", "दिन 6 • शनिवार (Saturday)", "Saturday is Shanivaar (शनिवार). We play with the colorful toy train on the weekend."),
                    Triple("रवि", "दिन 7 • रविवार (Sunday)", "Sunday is Ravivaar (रविवार). The bright golden sun shines on a joyful Sunday.")
                )
            )
            "en_es" -> DayLangPack(
                title = "Días de la Semana • Days of the Week",
                subtitle = "7 Días • Lunes a Domingo",
                coverBadges = "Lun  Mar  Mié",
                days = listOf(
                    Triple("Lun", "Día 1 • Lunes (Monday)", "Monday is Lunes. We begin the week reading our favorite storybooks."),
                    Triple("Mar", "Día 2 • Martes (Tuesday)", "Tuesday is Martes. We draw and write with our favorite pencil."),
                    Triple("Mié", "Día 3 • Miércoles (Wednesday)", "Wednesday is Miércoles. We play under the tall green tree in the park."),
                    Triple("Jue", "Día 4 • Jueves (Thursday)", "Thursday is Jueves. We ride our colorful bicycle down the path."),
                    Triple("Vie", "Día 5 • Viernes (Friday)", "Friday is Viernes. We fly a bright diamond kite in the blue sky."),
                    Triple("Sáb", "Día 6 • Sábado (Saturday)", "Saturday is Sábado. We ride the cheerful weekend train together."),
                    Triple("Dom", "Día 7 • Domingo (Sunday)", "Sunday is Domingo. The warm golden sun shines all day long.")
                )
            )
            "en_pt" -> DayLangPack(
                title = "Dias da Semana • Days of the Week",
                subtitle = "7 Dias • Segunda a Domingo",
                coverBadges = "Seg  Ter  Qua",
                days = listOf(
                    Triple("Seg", "Dia 1 • Segunda-feira (Monday)", "Monday is Segunda-feira. We start the week reading our colorful books."),
                    Triple("Ter", "Dia 2 • Terça-feira (Tuesday)", "Tuesday is Terça-feira. We write and draw with a bright pencil."),
                    Triple("Qua", "Dia 3 • Quarta-feira (Wednesday)", "Wednesday is Quarta-feira. We rest beneath the shady green tree."),
                    Triple("Qui", "Dia 4 • Quinta-feira (Thursday)", "Thursday is Quinta-feira. We ride our bicycle in the afternoon breeze."),
                    Triple("Sex", "Dia 5 • Sexta-feira (Friday)", "Friday is Sexta-feira. A colorful kite dances in the sky."),
                    Triple("Sáb", "Dia 6 • Sábado (Saturday)", "Saturday is Sábado. We play with the toy train on Saturday morning."),
                    Triple("Dom", "Dia 7 • Domingo (Sunday)", "Sunday is Domingo. The bright sun warms our happy family Sunday.")
                )
            )
            "en_ar" -> DayLangPack(
                title = "أيام الأسبوع • Days of the Week",
                subtitle = "٧ أيام • الإثنين إلى الأحد",
                coverBadges = "إثنين  ثلاثاء  أربعاء",
                days = listOf(
                    Triple("الإثنين", "اليوم ١ • الإثنين (Monday)", "Monday is Al-Ithnayn (الإثنين). We open our storybooks for a new week of learning."),
                    Triple("الثلاثاء", "اليوم ٢ • الثلاثاء (Tuesday)", "Tuesday is Ath-Thulatha (الثلاثاء). We write neat letters with a sharp pencil."),
                    Triple("الأربعاء", "اليوم ٣ • الأربعاء (Wednesday)", "Wednesday is Al-Arbia (الأربعاء). We visit the green trees in the garden."),
                    Triple("الخميس", "اليوم ٤ • الخميس (Thursday)", "Thursday is Al-Khamis (الخميس). We ride our bicycle happily in the yard."),
                    Triple("الجمعة", "اليوم ٥ • الجمعة (Friday)", "Friday is Al-Jumuah (الجمعة). A bright kite flies high in the clear sky."),
                    Triple("السبت", "اليوم ٦ • السبت (Saturday)", "Saturday is As-Sabt (السبت). We enjoy a fun train ride on the weekend."),
                    Triple("الأحد", "اليوم ٧ • الأحد (Sunday)", "Sunday is Al-Ahad (الأحد). The golden sun shines brightly in the morning sky.")
                )
            )
            "en_bn" -> DayLangPack(
                title = "সপ্তাহের দিন • Days of the Week",
                subtitle = "৭ দিন • সোমবার থেকে রবিবার",
                coverBadges = "সোম  মঙ্গল  বুধ",
                days = listOf(
                    Triple("সোম", "দিন ১ • সোমবার (Monday)", "Monday is Sombar (সোমবার). We begin the week reading our storybooks."),
                    Triple("মঙ্গল", "দিন ২ • মঙ্গলবার (Tuesday)", "Tuesday is Mongalbar (মঙ্গলবার). We draw pictures with a bright pencil."),
                    Triple("বুধ", "দিন ৩ • বুধবার (Wednesday)", "Wednesday is Budhbar (বুধবার). We look at the tall green tree in the park."),
                    Triple("বৃহঃ", "দিন ৪ • বৃহস্পতিবার (Thursday)", "Thursday is Brihospotibar (বৃহস্পতিবার). We ride our bicycle with friends."),
                    Triple("শুক্র", "দিন ৫ • শুক্রবার (Friday)", "Friday is Shukrobar (শুক্রবার). A colorful kite flies high in the sky."),
                    Triple("শনি", "দিন ৬ • শনিবার (Saturday)", "Saturday is Shonibar (শনিবার). We play with the toy train on Saturday."),
                    Triple("রবি", "দিন ৭ • রবিবার (Sunday)", "Sunday is Robibar (রবিবার). The bright golden sun shines on Sunday.")
                )
            )
            "en_ta" -> DayLangPack(
                title = "வாரத்தின் நாட்கள் • Days of the Week",
                subtitle = "7 நாட்கள் • திங்கள் முதல் ஞாயிறு",
                coverBadges = "திங்  செவ்  புத",
                days = listOf(
                    Triple("திங்கள்", "நாள் 1 • திங்கள் (Monday)", "Monday is Thingal (திங்கள்). We start the week reading our favorite books."),
                    Triple("செவ்வாய்", "நாள் 2 • செவ்வாய் (Tuesday)", "Tuesday is Sevvaai (செவ்வாய்). We write and draw with a pencil."),
                    Triple("புதன்", "நாள் 3 • புதன் (Wednesday)", "Wednesday is Budhan (புதன்). We see the tall green tree in the garden."),
                    Triple("வியாழன்", "நாள் 4 • வியாழன் (Thursday)", "Thursday is Viyaazhan (வியாழன்). We ride our bicycle in the park."),
                    Triple("வெள்ளி", "நாள் 5 • வெள்ளி (Friday)", "Friday is Velli (வெள்ளி). A colorful kite flies high in the breeze."),
                    Triple("சனி", "நாள் 6 • சனி (Saturday)", "Saturday is Sani (சனி). We play with the colorful train on Saturday."),
                    Triple("ஞாயிறு", "நாள் 7 • ஞாயிறு (Sunday)", "Sunday is Nyaayiru (ஞாயிறு). The golden sun shines brightly on Sunday.")
                )
            )
            "en_te" -> DayLangPack(
                title = "వారంలోని రోజులు • Days of the Week",
                subtitle = "7 రోజులు • సోమవారం నుండి ఆదివారం",
                coverBadges = "సోమ  మంగళ  బుధ",
                days = listOf(
                    Triple("సోమ", "రోజు 1 • సోమవారం (Monday)", "Monday is Somavaaram (సోమవారం). We start the week reading our storybooks."),
                    Triple("మంగళ", "రోజు 2 • మంగళవారం (Tuesday)", "Tuesday is Mangalavaaram (మంగళవారం). We write neatly with a pencil."),
                    Triple("బుధ", "రోజు 3 • బుధవారం (Wednesday)", "Wednesday is Budhavaaram (బుధవారం). We water the green tree in the garden."),
                    Triple("గురు", "రోజు 4 • గురువారం (Thursday)", "Thursday is Guruvaaram (గురువారం). We ride our bicycle outdoors."),
                    Triple("శుక్ర", "రోజు 5 • శుక్రవారం (Friday)", "Friday is Shukravaaram (శుక్రవారం). A bright kite soars in the blue sky."),
                    Triple("శని", "రోజు 6 • శనివారం (Saturday)", "Saturday is Shanivaaram (శనివారం). We play with the toy train on Saturday."),
                    Triple("ఆది", "రోజు 7 • ఆదివారం (Sunday)", "Sunday is Aadivaaram (ఆదివారం). The bright sun shines on a happy Sunday.")
                )
            )
            "en_mr" -> DayLangPack(
                title = "आठवड्याचे वार • Days of the Week",
                subtitle = "७ वार • सोमवार ते रविवार",
                coverBadges = "सोम  मंगळ  बुध",
                days = listOf(
                    Triple("सोम", "वार १ • सोमवार (Monday)", "Monday is Somvaar (सोमवार). We start the week reading our storybooks."),
                    Triple("मंगळ", "वार २ • मंगळवार (Tuesday)", "Tuesday is Mangalvaar (मंगळवार). We draw and write with a pencil."),
                    Triple("बुध", "वार ३ • बुधवार (Wednesday)", "Wednesday is Budhvaar (बुधवार). We care for the green trees in the park."),
                    Triple("गुरु", "वार ४ • गुरुवार (Thursday)", "Thursday is Guruvaar (गुरुवार). We ride our bicycle with friends."),
                    Triple("शुक्र", "वार ५ • शुक्रवार (Friday)", "Friday is Shukravaar (शुक्रवार). A colorful kite flies high in the sky."),
                    Triple("शनि", "वार ६ • शनिवार (Saturday)", "Saturday is Shanivaar (शनिवार). We play with the toy train on Saturday."),
                    Triple("रवि", "वार ७ • रविवार (Sunday)", "Sunday is Ravivaar (रविवार). The golden sun shines brightly on Sunday.")
                )
            )
            "en_gu" -> DayLangPack(
                title = "અઠવાડિયાના વાર • Days of the Week",
                subtitle = "૭ વાર • સોમવાર થી રવિવાર",
                coverBadges = "સોમ  મંગળ  બુધ",
                days = listOf(
                    Triple("સોમ", "વાર ૧ • સોમવાર (Monday)", "Monday is Somvaar (સોમવાર). We start the week reading our storybooks."),
                    Triple("મંગળ", "વાર ૨ • મંગળવાર (Tuesday)", "Tuesday is Mangalvaar (મંગળવાર). We draw and write with a pencil."),
                    Triple("બુધ", "વાર ૩ • બુધવાર (Wednesday)", "Wednesday is Budhvaar (બુધવાર). We look at the tall green tree."),
                    Triple("ગુરુ", "વાર ૪ • ગુરુવાર (Thursday)", "Thursday is Guruvaar (ગુરુવાર). We ride our bicycle in the park."),
                    Triple("શુક્ર", "વાર ૫ • શુક્રવાર (Friday)", "Friday is Shukravaar (શુક્રવાર). A colorful kite flies in the sky."),
                    Triple("શનિ", "વાર ૬ • શનિવાર (Saturday)", "Saturday is Shanivaar (શનિવાર). We play with the toy train on Saturday."),
                    Triple("રवि", "વાર ૭ • રવિવાર (Sunday)", "Sunday is Ravivaar (રવિવાર). The bright golden sun shines on Sunday.")
                )
            )
            else -> DayLangPack(
                title = "Days of the Week (7 Days)",
                subtitle = "7 Days • Monday to Sunday",
                coverBadges = "Mon  Tue  Wed",
                days = listOf(
                    Triple("Mon", "Day 1 • Monday", "Monday is the first day of the school week. We open our favorite storybook and read together."),
                    Triple("Tue", "Day 2 • Tuesday", "Tuesday is the second day of the week. We write neat letters and draw pictures with a pencil."),
                    Triple("Wed", "Day 3 • Wednesday", "Wednesday is the middle of the week. We rest beneath a tall green tree in the garden."),
                    Triple("Thu", "Day 4 • Thursday", "Thursday is the fourth day of the week. We ride our colorful bicycle down the sunny path."),
                    Triple("Fri", "Day 5 • Friday", "Friday is the fifth day of the week. We fly a bright diamond kite high in the blue sky."),
                    Triple("Sat", "Day 6 • Saturday", "Saturday is the weekend! We play with our colorful toy train and visit the park."),
                    Triple("Sun", "Day 7 • Sunday", "Sunday is a bright sunny day of rest and fun with our family.")
                )
            )
        }

        val dayImages = listOf(imgMon, imgTue, imgWed, imgThu, imgFri, imgSat, imgSun)
        val dayPractice = listOf("book", "write", "tree", "park", "sky", "train", "sun")

        val pages = pack.days.mapIndexed { idx, (badge, fullLabel, sentence) ->
            StoryPage(
                pageNumber = idx + 1,
                imageAssetPath = dayImages[idx % dayImages.size],
                text = sentence,
                spanishTranslation = fullLabel,
                urduTranslation = fullLabel,
                hindiTranslation = fullLabel,
                practiceWords = setOf(dayPractice[idx % dayPractice.size]),
                alphabetBadgeLetters = badge
            )
        }

        return StoryBook(
            id = "days_of_week_$langCode",
            title = pack.title,
            nativeTitleUrdu = pack.subtitle,
            nativeTitleHindi = pack.subtitle,
            author = "Seekh Reading Curriculum",
            illustrator = "Seekh Alphabet Studio",
            publisher = "Foundational Literacy Pack",
            level = 1,
            coverAssetPath = imgSun,
            isFeaturedHero = false,
            bookType = "DAYS_OF_WEEK",
            categoryTag = "Numbers & Rhymes",
            phonicsSkillTag = "Days: Monday to Sunday",
            alphabetBadgeLetters = pack.coverBadges,
            pages = pages
        )
    }

    // Complete Language-Specific "Months of the Year" (12 Pages: 1 Month per Page) Book across all 11 languages!
    fun getMonthsOfYearBookForLanguage(langCode: String): StoryBook {
        val monthImages = listOf(
            "books/alphabets/alpha_mountain.webp",   // 1. Jan - Winter mountain snow
            "books/alphabets/alpha_butterfly.webp",  // 2. Feb - Early spring butterfly
            "books/alphabets/alpha_flower.webp",     // 3. Mar - Blooming spring flower
            "books/alphabets/alpha_n_nest.webp",     // 4. Apr - Bird nest in green spring
            "books/alphabets/alpha_m_mango.webp",    // 5. May - Sweet ripe mango
            "books/alphabets/alpha_s_sun.webp",      // 6. Jun - Warm summer sunshine
            "books/alphabets/alpha_u_umbrella.webp", // 7. Jul - Monsoon rain umbrella
            "books/alphabets/alpha_peacock.webp",    // 8. Aug - Dancing green peacock
            "books/alphabets/alpha_book.webp",       // 9. Sep - Back to school books
            "books/alphabets/alpha_a_apple.webp",    // 10. Oct - Crisp autumn harvest apple
            "books/alphabets/alpha_moon.webp",       // 11. Nov - Cool evening crescent moon
            "books/alphabets/alpha_h_house.webp"     // 12. Dec - Cozy winter home
        )
        val monthPractice = listOf("cold", "butterfly", "flower", "nest", "mango", "sun", "rain", "garden", "book", "apple", "moon", "home")

        data class MonthLangPack(
            val title: String,
            val subtitle: String,
            val coverBadges: String,
            val months: List<Triple<String, String, String>>
        )

        val pack = when (langCode) {
            "en_ur" -> MonthLangPack(
                title = "سال کے مہینے • Months of the Year",
                subtitle = "12 مہینے • جنوری سے دسمبر تک",
                coverBadges = "جنوری  فروری  مارچ",
                months = listOf(
                    Triple("جنوری", "پہلا مہینہ • جنوری (January)", "January (جنوری) is the first month of the year. Snow sparkles on the high winter mountains."),
                    Triple("فروری", "دوسرا مہینہ • فروری (February)", "February (فروری) is the second month. Colorful butterflies flutter in the cool breeze."),
                    Triple("مارچ", "تیسرا مہینہ • مارچ (March)", "March (مارچ) is the third month. Fresh spring flowers bloom across the garden."),
                    Triple("اپریل", "چوتھا مہینہ • اپریل (April)", "April (اپریل) is the fourth month. Little birds build a cozy nest in the green trees."),
                    Triple("مئی", "پانچواں مہینہ • مئی (May)", "May (مئی) is the fifth month. Sweet golden mangoes ripen on the orchard trees."),
                    Triple("جون", "چھٹا مہینہ • جون (June)", "June (جون) is the sixth month. The bright golden summer sun shines in the sky."),
                    Triple("جولائی", "ساتواں مہینہ • جولائی (July)", "July (جولائی) is the seventh month. We open a colorful umbrella when the rain falls."),
                    Triple("اگست", "آٹھواں مہینہ • اگست (August)", "August (اگست) is the eighth month. A beautiful peacock dances in the green meadow."),
                    Triple("ستمبر", "نواں مہینہ • ستمبر (September)", "September (ستمبر) is the ninth month. We read exciting new books at school."),
                    Triple("اکتوبر", "دسواں مہینہ • اکتوبر (October)", "October (اکتوبر) is the tenth month. Crisp red apples are ready for the autumn harvest."),
                    Triple("نومبر", "گیارہواں مہینہ • نومبر (November)", "November (نومبر) is the eleventh month. The bright crescent moon glows in the cool night sky."),
                    Triple("دسمبر", "بارہواں مہینہ • دسمبر (December)", "December (دسمبر) is the twelfth and last month of the year. We stay warm inside our cozy home.")
                )
            )
            "en_hi" -> MonthLangPack(
                title = "साल के महीने • Months of the Year",
                subtitle = "12 महीने • जनवरी से दिसंबर",
                coverBadges = "जन  फ़र  मार्च",
                months = listOf(
                    Triple("जनवरी", "महीना 1 • जनवरी (January)", "January (जनवरी) is the first month of the year with snowy winter mountain peaks."),
                    Triple("फ़रवरी", "महीना 2 • फ़रवरी (February)", "February (फ़रवरी) is the second month when colorful butterflies visit the garden."),
                    Triple("मार्च", "महीना 3 • मार्च (March)", "March (मार्च) is the third month when bright spring flowers bloom everywhere."),
                    Triple("अप्रैल", "महीना 4 • अप्रैल (April)", "April (अप्रैल) is the fourth month with baby birds resting in their cozy nest."),
                    Triple("मई", "महीना 5 • मई (May)", "May (मई) is the fifth month when sweet yellow mangoes ripen on the trees."),
                    Triple("जून", "महीना 6 • जून (June)", "June (जून) is the sixth month with warm golden summer sunshine."),
                    Triple("जुलाई", "महीना 7 • जुलाई (July)", "July (जुलाई) is the seventh month when we carry a colorful umbrella in the rain."),
                    Triple("अगस्त", "महीना 8 • अगस्त (August)", "August (अगस्त) is the eighth month when the peacock dances in the green fields."),
                    Triple("सितंबर", "महीना 9 • सितंबर (September)", "September (सितंबर) is the ninth month when we read wonderful books at school."),
                    Triple("अक्टूबर", "महीना 10 • अक्टूबर (October)", "October (अक्टूबर) is the tenth month with fresh red harvest apples."),
                    Triple("नवंबर", "महीना 11 • नवंबर (November)", "November (नवंबर) is the eleventh month with a glowing crescent moon on cool nights."),
                    Triple("दिसंबर", "महीना 12 • दिसंबर (December)", "December (दिसंबर) is the twelfth month when we enjoy winter inside our warm home.")
                )
            )
            "en_es" -> MonthLangPack(
                title = "Meses del Año • Months of the Year",
                subtitle = "12 Meses • Enero a Diciembre",
                coverBadges = "Ene  Feb  Mar",
                months = listOf(
                    Triple("Ene", "Mes 1 • Enero (January)", "January is Enero, the first month of the year with snowy mountains."),
                    Triple("Feb", "Mes 2 • Febrero (February)", "February is Febrero, the second month with colorful butterflies."),
                    Triple("Mar", "Mes 3 • Marzo (March)", "March is Marzo, the third month when bright spring flowers bloom."),
                    Triple("Abr", "Mes 4 • Abril (April)", "April is Abril, the fourth month with bird nests in the green trees."),
                    Triple("May", "Mes 5 • Mayo (May)", "May is Mayo, the fifth month with sweet golden mangoes."),
                    Triple("Jun", "Mes 6 • Junio (June)", "June is Junio, the sixth month with bright summer sunshine."),
                    Triple("Jul", "Mes 7 • Julio (July)", "July is Julio, the seventh month with our colorful umbrella."),
                    Triple("Ago", "Mes 8 • Agosto (August)", "August is Agosto, the eighth month with a proud colourful peacock."),
                    Triple("Sep", "Mes 9 • Septiembre (September)", "September is Septiembre, the ninth month for reading favorite school books."),
                    Triple("Oct", "Mes 10 • Octubre (October)", "October is Octubre, the tenth month with crisp red autumn apples."),
                    Triple("Nov", "Mes 11 • Noviembre (November)", "November is Noviembre, the eleventh month under the crescent moon."),
                    Triple("Dic", "Mes 12 • Diciembre (December)", "December is Diciembre, the twelfth month in our cozy warm home.")
                )
            )
            "en_pt" -> MonthLangPack(
                title = "Meses do Ano • Months of the Year",
                subtitle = "12 Meses • Janeiro a Dezembro",
                coverBadges = "Jan  Fev  Mar",
                months = listOf(
                    Triple("Jan", "Mês 1 • Janeiro (January)", "January is Janeiro, the first month of the year with tall mountains."),
                    Triple("Fev", "Mês 2 • Fevereiro (February)", "February is Fevereiro, the second month with colorful butterflies."),
                    Triple("Mar", "Mês 3 • Março (March)", "March is Março, the third month when flowers bloom in the garden."),
                    Triple("Abr", "Mês 4 • Abril (April)", "April is Abril, the fourth month with a cozy bird nest."),
                    Triple("Mai", "Mês 5 • Maio (May)", "May is Maio, the fifth month with sweet ripe mangoes."),
                    Triple("Jun", "Mês 6 • Junho (June)", "June is Junho, the sixth month with warm golden sun."),
                    Triple("Jul", "Mês 7 • Julho (July)", "July is Julho, the seventh month with a bright rain umbrella."),
                    Triple("Ago", "Mês 8 • Agosto (August)", "August is Agosto, the eighth month with a colorful peacock."),
                    Triple("Set", "Mês 9 • Setembro (September)", "September is Setembro, the ninth month with our favorite books."),
                    Triple("Out", "Mês 10 • Outubro (October)", "October is Outubro, the tenth month with fresh red apples."),
                    Triple("Nov", "Mês 11 • Novembro (November)", "November is Novembro, the eleventh month under the glowing moon."),
                    Triple("Dez", "Mês 12 • Dezembro (December)", "December is Dezembro, the twelfth month in our warm family home.")
                )
            )
            "en_ar" -> MonthLangPack(
                title = "أشهر السنة • Months of the Year",
                subtitle = "١٢ شهراً • يناير إلى ديسمبر",
                coverBadges = "يناير  فبراير  مارس",
                months = listOf(
                    Triple("يناير", "الشهر ١ • يناير (January)", "January is Yanayir (يناير), the first month with snowy mountain peaks."),
                    Triple("فبراير", "الشهر ٢ • فبراير (February)", "February is Fabrayir (فبراير), the second month with colorful butterflies."),
                    Triple("مارس", "الشهر ٣ • مارس (March)", "March is Maris (مارس), the third month when spring flowers bloom."),
                    Triple("أبريل", "الشهر ٤ • أبريل (April)", "April is Abril (أبريل), the fourth month with bird nests in the trees."),
                    Triple("مايو", "الشهر ٥ • مايو (May)", "May is Mayu (مايو), the fifth month with sweet golden mangoes."),
                    Triple("يونيو", "الشهر ٦ • يونيو (June)", "June is Yunyu (يونيو), the sixth month with bright summer sun."),
                    Triple("يوليو", "الشهر ٧ • يوليو (July)", "July is Yulyu (يوليو), the seventh month with a colorful umbrella."),
                    Triple("أغسطس", "الشهر ٨ • أغسطس (August)", "August is Aghustus (أغسطس), the eighth month with a beautiful peacock."),
                    Triple("سبتمبر", "الشهر ٩ • سبتمبر (September)", "September is Sibtambir (سبتمبر), the ninth month for school books."),
                    Triple("أكتوبر", "الشهر ١٠ • أكتوبر (October)", "October is Uktubir (أكتوبر), the tenth month with fresh red apples."),
                    Triple("نوفمبر", "الشهر ١١ • نوفمبر (November)", "November is Nufambir (نوفمبر), the eleventh month under the crescent moon."),
                    Triple("ديسمبر", "الشهر ١٢ • ديسمبر (December)", "December is Disambir (ديسمبر), the twelfth month in our warm home.")
                )
            )
            else -> MonthLangPack(
                title = "Months of the Year (12 Months)",
                subtitle = "12 Months • January to December",
                coverBadges = "Jan  Feb  Mar",
                months = listOf(
                    Triple("Jan", "Month 1 • January", "January is the first month of the year. White snow sparkles on the high mountain peaks."),
                    Triple("Feb", "Month 2 • February", "February is the second month of the year. Colorful butterflies dance in the cool breeze."),
                    Triple("Mar", "Month 3 • March", "March is the third month of the year. Bright spring flowers bloom across the green meadow."),
                    Triple("Apr", "Month 4 • April", "April is the fourth month of the year. Little birds rest inside a cozy nest in the tree."),
                    Triple("May", "Month 5 • May", "May is the fifth month of the year. Sweet golden mangoes ripen in the warm orchard."),
                    Triple("Jun", "Month 6 • June", "June is the sixth month of the year. The warm golden summer sun shines high in the sky."),
                    Triple("Jul", "Month 7 • July", "July is the seventh month of the year. We carry a colorful umbrella when rain showers fall."),
                    Triple("Aug", "Month 8 • August", "August is the eighth month of the year. A proud peacock spreads its colorful feathers."),
                    Triple("Sep", "Month 9 • September", "September is the ninth month of the year. We open our favorite books for a new school term."),
                    Triple("Oct", "Month 10 • October", "October is the tenth month of the year. Fresh red apples are picked in the autumn harvest."),
                    Triple("Nov", "Month 11 • November", "November is the eleventh month of the year. A golden crescent moon glows in the cool night sky."),
                    Triple("Dec", "Month 12 • December", "December is the twelfth and final month of the year. Families gather inside a warm cozy home.")
                )
            )
        }

        val pages = pack.months.mapIndexed { idx, (badge, fullLabel, sentence) ->
            StoryPage(
                pageNumber = idx + 1,
                imageAssetPath = monthImages[idx % monthImages.size],
                text = sentence,
                spanishTranslation = fullLabel,
                urduTranslation = fullLabel,
                hindiTranslation = fullLabel,
                practiceWords = setOf(monthPractice[idx % monthPractice.size]),
                alphabetBadgeLetters = badge
            )
        }

        return StoryBook(
            id = "months_of_year_$langCode",
            title = pack.title,
            nativeTitleUrdu = pack.subtitle,
            nativeTitleHindi = pack.subtitle,
            author = "Seekh Reading Curriculum",
            illustrator = "Seekh Alphabet Studio",
            publisher = "Foundational Literacy Pack",
            level = 1,
            coverAssetPath = "books/alphabets/alpha_flower.webp",
            isFeaturedHero = false,
            bookType = "MONTHS_OF_YEAR",
            categoryTag = "Numbers & Rhymes",
            phonicsSkillTag = "Months: January to December",
            alphabetBadgeLetters = pack.coverBadges,
            pages = pages
        )
    }

    data class TopTrioLocalizedLabels(
        val alphabetsTitle: String,
        val alphabetsCountTag: String,
        val daysTitle: String,
        val daysCountTag: String,
        val monthsTitle: String,
        val monthsCountTag: String,
        val sectionDaysHeader: String,
        val sectionMonthsHeader: String
    )

    fun getTopTrioLabels(langCode: String): TopTrioLocalizedLabels {
        return when (langCode) {
            "en_ur" -> TopTrioLocalizedLabels(
                alphabetsTitle = "حروفِ تہجی",
                alphabetsCountTag = "ا ب پ • A-Z",
                daysTitle = "ہفتے کے دن",
                daysCountTag = "7 دن • پیر-اتوار",
                monthsTitle = "سال کے مہینے",
                monthsCountTag = "12 مہینے",
                sectionDaysHeader = "ہفتے کے دن • 7 Days",
                sectionMonthsHeader = "سال کے مہینے • 12 Months"
            )
            "en_hi" -> TopTrioLocalizedLabels(
                alphabetsTitle = "वर्णमाला",
                alphabetsCountTag = "अ आ इ • A-Z",
                daysTitle = "सप्ताह के दिन",
                daysCountTag = "7 दिन • सोम-रवि",
                monthsTitle = "साल के महीने",
                monthsCountTag = "12 महीने",
                sectionDaysHeader = "सप्ताह के 7 दिन",
                sectionMonthsHeader = "साल के 12 महीने"
            )
            "en_es" -> TopTrioLocalizedLabels(
                alphabetsTitle = "Alfabeto",
                alphabetsCountTag = "A B C • Letras",
                daysTitle = "Días",
                daysCountTag = "7 Días • Semana",
                monthsTitle = "Meses",
                monthsCountTag = "12 Meses • Año",
                sectionDaysHeader = "Días de la Semana",
                sectionMonthsHeader = "Meses del Año"
            )
            "en_pt" -> TopTrioLocalizedLabels(
                alphabetsTitle = "Alfabeto",
                alphabetsCountTag = "A B C • Letras",
                daysTitle = "Dias",
                daysCountTag = "7 Dias • Semana",
                monthsTitle = "Meses",
                monthsCountTag = "12 Meses • Ano",
                sectionDaysHeader = "Dias da Semana",
                sectionMonthsHeader = "Meses do Ano"
            )
            "en_ar" -> TopTrioLocalizedLabels(
                alphabetsTitle = "الحروف الأبجدية",
                alphabetsCountTag = "أ ب ت • حروف",
                daysTitle = "أيام الأسبوع",
                daysCountTag = "٧ أيام",
                monthsTitle = "أشهر السنة",
                monthsCountTag = "١٢ شهراً",
                sectionDaysHeader = "أيام الأسبوع • ٧ أيام",
                sectionMonthsHeader = "أشهر السنة • ١٢ شهراً"
            )
            "en_bn" -> TopTrioLocalizedLabels(
                alphabetsTitle = "বর্ণমালা",
                alphabetsCountTag = "অ আ ক খ",
                daysTitle = "সপ্তাহের দিন",
                daysCountTag = "৭ দিন",
                monthsTitle = "বছরের মাস",
                monthsCountTag = "১২ মাস",
                sectionDaysHeader = "সপ্তাহের ৭ দিন",
                sectionMonthsHeader = "বছরের ১২ মাস"
            )
            "en_ta" -> TopTrioLocalizedLabels(
                alphabetsTitle = "எழுத்துக்கள்",
                alphabetsCountTag = "அ ஆ இ ஈ",
                daysTitle = "வார நாட்கள்",
                daysCountTag = "7 நாட்கள்",
                monthsTitle = "மாதங்கள்",
                monthsCountTag = "12 மாதங்கள்",
                sectionDaysHeader = "வாரத்தின் 7 நாட்கள்",
                sectionMonthsHeader = "ஆண்டின் 12 மாதங்கள்"
            )
            "en_te" -> TopTrioLocalizedLabels(
                alphabetsTitle = "అక్షరమాల",
                alphabetsCountTag = "అ ఆ ఇ ఈ",
                daysTitle = "వారపు రోజులు",
                daysCountTag = "7 రోజులు",
                monthsTitle = "నెలలు",
                monthsCountTag = "12 నెలలు",
                sectionDaysHeader = "వారంలోని 7 రోజులు",
                sectionMonthsHeader = "సంవత్సరంలోని 12 నెలలు"
            )
            "en_mr" -> TopTrioLocalizedLabels(
                alphabetsTitle = "मुळाक्षरे",
                alphabetsCountTag = "अ आ इ ई",
                daysTitle = "आठवड्याचे वार",
                daysCountTag = "७ वार",
                monthsTitle = "वर्षाचे महिने",
                monthsCountTag = "१२ महिने",
                sectionDaysHeader = "आठवड्याचे ७ वार",
                sectionMonthsHeader = "वर्षाचे १२ महिने"
            )
            "en_gu" -> TopTrioLocalizedLabels(
                alphabetsTitle = "મૂળાક્ષરો",
                alphabetsCountTag = "અ આ ઇ ઈ",
                daysTitle = "અઠવાડિયાના વાર",
                daysCountTag = "૭ વાર",
                monthsTitle = "વર્ષના મહિના",
                monthsCountTag = "૧૨ મહિના",
                sectionDaysHeader = "અઠવાડિયાના ૭ વાર",
                sectionMonthsHeader = "વર્ષના ૧૨ મહિના"
            )
            else -> TopTrioLocalizedLabels(
                alphabetsTitle = "Alphabets",
                alphabetsCountTag = "26 Letters • A-Z",
                daysTitle = "Days of Week",
                daysCountTag = "7 Days • Mon-Sun",
                monthsTitle = "Months of Year",
                monthsCountTag = "12 Months • Jan-Dec",
                sectionDaysHeader = "Days of the Week",
                sectionMonthsHeader = "Months of the Year"
            )
        }
    }

    // Complete language-aware Book Title & Subtitle localization across all 11 languages
    fun getBookPrimaryTitle(book: StoryBook, langCode: String): String {
        if (book.bookType == "ALPHABET" || book.bookType == "DAYS_OF_WEEK" || book.bookType == "MONTHS_OF_YEAR" || book.id.startsWith("alphabet_book_")) {
            return book.title
        }
        return when (langCode) {
            "en" -> book.title
            "en_ur" -> book.nativeTitleUrdu.ifBlank { book.title }
            "en_hi", "en_mr" -> book.nativeTitleHindi.ifBlank { book.title }
            else -> book.title
        }
    }

    fun getBookSecondaryTitle(book: StoryBook, langCode: String): String {
        if (book.bookType == "ALPHABET" || book.bookType == "DAYS_OF_WEEK" || book.bookType == "MONTHS_OF_YEAR" || book.id.startsWith("alphabet_book_")) {
            return book.nativeTitleUrdu.ifBlank { book.nativeTitleHindi }
        }
        return when (langCode) {
            "en" -> ""
            "en_ur" -> if (book.nativeTitleUrdu.isNotBlank() && book.nativeTitleUrdu != book.title) book.title else ""
            "en_hi", "en_mr" -> if (book.nativeTitleHindi.isNotBlank() && book.nativeTitleHindi != book.title) book.title else ""
            else -> book.nativeTitleUrdu.ifBlank { "" }
        }
    }

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
        "blast" to "تیز ہوا کا جھونکا",
        "candles" to "موم بتیاں",
        "crashed" to "زور سے ٹکرایا",
        "clouds" to "آسمان کے بادل",
        "thunder" to "بجلی کی گرج",
        "rain" to "بارش",
        "ladybird" to "لیڈی برڈ (ننھا کیڑا)",
        "special" to "خاص / منفرد",
        "wings" to "اڑنے والے پَر",
        "yellow" to "پیلا رنگ",
        "butterfly" to "خوبصورت تتلی",
        "friends" to "پیارے دوست",
        "school" to "اسکول / مدرسہ",
        "painted" to "رنگ کیا ہوا",
        "bright" to "روشن اور چمکدار",
        "caterpillar" to "کیٹرپلر (ننھا سنڈی کیڑا)",
        "shoe" to "پہننے کا جوتا",
        "spider" to "جالا بننے والی مکڑی",
        "frog" to "پانی کا مینڈک",
        "grasshopper" to "گھاس کا ٹڈا",
        "crab" to "ساحلی کیکڑا",
        "cocoon" to "ریشمی خول / کوکون",
        "train" to "ریل گاڑی",
        "beach" to "ساحلِ سمندر",
        "chocolates" to "میٹھی چاکلیٹ",
        "drummers" to "ڈھول بجانے والے",
        "sailor" to "سمندری ملاح / بحریہ کا فرد",
        "cupboard" to "لکڑی کی الماری",
        "curtain" to "کھڑکی کا پردہ",
        "borrow" to "پڑھنے کے لیے ادھار لینا",
        "return" to "واپس لوٹانا",
        "books" to "پڑھنے کی کتابیں",
        "raincoat" to "بارش کا کوٹ",
        "street" to "محلے کی گلی / سڑک",
        "colors" to "خوبصورت رنگ",
        "unique" to "بے مثال اور منفرد",
        "gleamed" to "جگمگا اٹھے / چمکے",
        "scrubbed" to "رگڑ کر صاف کیا",
        "whispers" to "ہولے سے سرگوشی کرتا ہے",
        "grandmother" to "دادی جان / نانی جان",
        "grandfather" to "دادا جان / نانا جان",
        "every" to "ہر ایک",
        "morning" to "صبح کا وقت",
        "our" to "ہمارا / ہماری",
        "busy" to "مصروف اور چہل پہل والا",
        "wakes" to "جاگتا ہے",
        "hundred" to "سو (100)",
        "inside" to "کے اندر",
        "garden" to "پھولوں کا باغ",
        "gate" to "بڑا دروازہ",
        "golden" to "سنہری رنگ کا",
        "marigolds" to "گیندے کے سنہری پھول",
        "green" to "سبز / ہرا رنگ",
        "leaves" to "درخت یا پودے کے پتے",
        "shine" to "چمکتے ہیں",
        "early" to "صبح سویرے / جلدی",
        "sunlight" to "سورج کی دھوپ",
        "blue" to "نیلا رنگ",
        "red" to "سرخ / لال رنگ",
        "white" to "سفید رنگ",
        "sky" to "آسمان",
        "sun" to "سورج",
        "moon" to "چاند",
        "stars" to "ستارے",
        "water" to "پانی",
        "fish" to "مچھلی",
        "tree" to "درخت",
        "trees" to "درخت",
        "flower" to "پھول",
        "flowers" to "پھول",
        "happy" to "خوش",
        "smile" to "مسکراہٹ",
        "smiled" to "مسکرایا",
        "read" to "پڑھنا",
        "story" to "کہانی",
        "library" to "کتب خانہ / لائبریری",
        "teacher" to "استاد / استانی",
        "classroom" to "کمرۂ جماعت",
        "mother" to "امی جان",
        "father" to "ابو جان",
        "sister" to "بہن",
        "brother" to "بھائی",
        "ball" to "کھیلنے کی گیند",
        "play" to "کھیلنا",
        "walk" to "چلنا / سیر کرنا",
        "walks" to "چلتا ہے",
        "flying" to "اڑ رہا ہے",
        "ocean" to "بڑا سمندر",
        "apples" to "میٹھے سیب",
        "apple" to "میٹھا سیب",
        "friend" to "پیارا دوست",
        "rainbow" to "قوسِ قزح",
        "sparkle" to "چمک دمک",
        "sunshine" to "سورج کی روشنی",
        "bicycle" to "سائیکل",
        "umbrella" to "چھتری",
        "treasure" to "خزانہ",
        "drummer" to "ڈھول بجانے والا"
    )

    private val wordMeaningsHindi = mapOf(
        "blast" to "हवा का तेज़ झोंका",
        "candles" to "मोमबत्तियाँ",
        "crashed" to "ज़ोर से टकराया",
        "clouds" to "आसमान के बादल",
        "thunder" to "बादलों की गड़गड़ाहट",
        "rain" to "बारिश",
        "ladybird" to "लेडीबर्ड (नन्हा कीट)",
        "special" to "ख़ास और अनोखा",
        "wings" to "उड़ने वाले पंख",
        "yellow" to "पीला रंग",
        "butterfly" to "रंग-बिरंगी तितली",
        "friends" to "प्यारे दोस्त",
        "school" to "स्कूल / विद्यालय",
        "painted" to "रंगा हुआ",
        "bright" to "चमकीला और उजला",
        "caterpillar" to "कैटरपिलर",
        "shoe" to "पहनने का जूता",
        "spider" to "जाला बुनने वाली मकड़ी",
        "frog" to "तालाब का मेंढक",
        "grasshopper" to "घास का टिड्डा",
        "crab" to "समुद्री केकड़ा",
        "cocoon" to "रेशमी खोल / कोकून",
        "train" to "रेलगाड़ी",
        "beach" to "समुद्र तट",
        "chocolates" to "मीठी चॉकलेट",
        "drummers" to "ढोल वादक",
        "sailor" to "समुद्री नाविक",
        "cupboard" to "लकड़ी की अलमारी",
        "curtain" to "खिड़की का पर्दा",
        "borrow" to "पढ़ने के लिए उधार लेना",
        "return" to "वापस लौटाना",
        "books" to "पढ़ने की किताबें",
        "raincoat" to "बारिश का रेनकोट",
        "street" to "मोहल्ले की गली / सड़क",
        "colors" to "सुंदर रंग",
        "unique" to "अनोखा और विशेष",
        "gleamed" to "चमक उठे",
        "scrubbed" to "रगड़ कर साफ़ किया",
        "whispers" to "धीमे से फुसफुसाता है",
        "grandmother" to "दादी जी / नानी जी",
        "grandfather" to "दादा जी / नाना जी",
        "every" to "हर एक",
        "morning" to "सुबह का समय",
        "our" to "हमारा / हमारी",
        "busy" to "व्यस्त और चहल-पहल वाला",
        "wakes" to "जागता है",
        "hundred" to "सौ (100)",
        "inside" to "के अंदर",
        "garden" to "फूलों का बग़ीचा",
        "gate" to "बड़ा दरवाज़ा",
        "golden" to "सुनहरे रंग का",
        "marigolds" to "गेंदे के सुनहरे फूल",
        "green" to "हरा रंग",
        "leaves" to "पेड़-पौधों के पत्ते",
        "shine" to "चमकते हैं",
        "early" to "सुबह सवेरे",
        "sunlight" to "सूरज की धूप",
        "blue" to "नीला रंग",
        "red" to "लाल रंग",
        "white" to "सफ़ेद रंग",
        "sky" to "आसमान",
        "sun" to "सूरज",
        "moon" to "चाँद",
        "stars" to "सितारे",
        "water" to "पानी",
        "fish" to "मछली",
        "tree" to "पेड़",
        "trees" to "पेड़",
        "flower" to "फूल",
        "flowers" to "फूल",
        "happy" to "ख़ुश",
        "smile" to "मुस्कान",
        "smiled" to "मुस्कुराया",
        "read" to "पढ़ना",
        "story" to "कहानी",
        "library" to "पुस्तकालय / लाइब्रेरी",
        "teacher" to "शिक्षक / अध्यापिका",
        "classroom" to "कक्षा",
        "mother" to "माँ",
        "father" to "पिताजी",
        "sister" to "बहन",
        "brother" to "भाई",
        "ball" to "खेलने की गेंद",
        "play" to "खेलना",
        "walk" to "चलना / सैर करना",
        "walks" to "चलता है",
        "flying" to "उड़ रहा है",
        "ocean" to "विशाल समुद्र",
        "apples" to "मीठे सेब",
        "apple" to "मीठा सेब",
        "friend" to "प्यारा दोस्त",
        "rainbow" to "इंद्रधनुष",
        "sparkle" to "चमक",
        "sunshine" to "सूरज की धूप",
        "bicycle" to "साइकिल",
        "umbrella" to "छाता",
        "treasure" to "ख़ज़ाना",
        "drummer" to "ढोल वादक"
    )

    private val wordEnglishDefinitions = mapOf(
        "blast" to "A sudden strong gust of wind or air.",
        "candles" to "Wax sticks with a wick that burn to give warm light.",
        "crashed" to "Hit or collided with something with a loud sound.",
        "clouds" to "Fluffy white or gray shapes floating high in the sky that bring rain.",
        "thunder" to "The loud rumbling sound heard from the sky during a storm.",
        "rain" to "Drops of water that fall from clouds in the sky.",
        "ladybird" to "A small round garden beetle with colorful wings and tiny spots.",
        "special" to "Different from ordinary things in a wonderful and important way.",
        "wings" to "The parts of a bird, butterfly, or insect used for flying.",
        "yellow" to "A bright, sunny color like gold, lemons, and sunflowers.",
        "butterfly" to "A graceful insect with large, colorful wings that flutters among flowers.",
        "friends" to "People or companions you care about and enjoy spending time with.",
        "school" to "A place where children and teachers gather to learn and read.",
        "painted" to "Covered or decorated with colorful paint.",
        "bright" to "Shining with lots of light or vivid, cheerful color.",
        "caterpillar" to "A small crawling garden creature that grows up to become a butterfly.",
        "shoe" to "Footwear worn to protect and comfort your feet when walking.",
        "spider" to "An eight-legged creature that spins neat silken webs.",
        "frog" to "A smooth green amphibious animal that lives near ponds and leaps high.",
        "grasshopper" to "A green insect with long back legs for jumping high in grassy meadows.",
        "crab" to "A sea creature with a hard shell and two claws that walks sideways on the beach.",
        "cocoon" to "A silky protective home spun by a caterpillar before it becomes a butterfly.",
        "train" to "Connected railway carriages that travel fast along metal tracks.",
        "beach" to "A sandy shore beside the ocean or sea.",
        "chocolates" to "Sweet treats made from cocoa.",
        "drummers" to "Musicians who play lively rhythms and beats on drums.",
        "sailor" to "A person who works on ships and sails across the ocean.",
        "cupboard" to "A cabinet with shelves and doors used for storing things neatly.",
        "curtain" to "A piece of cloth hung across a window or doorway.",
        "borrow" to "To take a book or item for a short time with a promise to return it.",
        "return" to "To give or bring something back to where it belongs.",
        "books" to "Printed or illustrated pages bound together for reading stories and learning.",
        "raincoat" to "A waterproof coat worn to stay dry when it rains.",
        "street" to "A public road in a neighborhood or town with houses and shops.",
        "colors" to "Hues like red, blue, yellow, and green that make the world vibrant.",
        "unique" to "One of a kind; unlike anything or anyone else.",
        "gleamed" to "Shone brightly with reflected light.",
        "scrubbed" to "Rubbed hard to clean something thoroughly.",
        "whispers" to "Speaks very softly and quietly using breath instead of a loud voice.",
        "grandmother" to "The mother of your father or mother.",
        "grandfather" to "The father of your father or mother.",
        "morning" to "The early part of the day when the sun rises.",
        "busy" to "Full of activity, movement, and energy.",
        "street" to "A road in a town or city where people walk and live.",
        "garden" to "A piece of ground where flowers, trees, and plants grow.",
        "golden" to "Shining with a warm yellow color like gold.",
        "marigolds" to "Bright yellow and orange garden flowers.",
        "leaves" to "Flat green parts of plants and trees that catch sunlight.",
        "sunlight" to "The natural light and warmth that comes from the sun."
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
            clean == "marigolds" -> listOf("mar", "i", "golds")
            clean == "sunlight" -> listOf("sun", "light")
            clean == "hundred" -> listOf("hun", "dred")
            clean == "garden" -> listOf("gar", "den")
            clean == "golden" -> listOf("gold", "en")
            clean == "inside" -> listOf("in", "side")
            clean.length <= 4 -> listOf(clean)
            else -> clean.chunked(3)
        }
    }

    fun getWordSyllables(rawWord: String): String {
        return getWordSyllablesList(rawWord).joinToString(" • ")
    }

    fun getWordTooltip(rawWord: String, langCode: String): Pair<String, String> {
        val details = getDetailedWordInfo(rawWord, "", langCode)
        return details.syllables.joinToString(" • ") to details.nativeMeaning
    }

    fun getWordTranslation(rawWord: String, langCode: String): String {
        return getDetailedWordInfo(rawWord, "", langCode).nativeMeaning
    }

    fun getDetailedWordInfo(rawWord: String, pageSentence: String, langCode: String): WordTutorDetails {
        val clean = rawWord.lowercase().replace(Regex("[^a-z]"), "")
        val display = clean.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString() }.ifEmpty { rawWord }
        val syllables = getWordSyllablesList(clean.ifEmpty { rawWord })
        val phonetics = "/" + syllables.mapIndexed { idx, s -> if (idx == 0) s.uppercase() else s }.joinToString("-") + "/"

        // Match exact vocabulary item if present in the 51 vocab items
        val vocabMatch = vocabularyItems.firstOrNull { it.word.equals(clean, ignoreCase = true) }

        val partOfSpeech = when {
            clean in setOf("the", "a", "an", "in", "on", "at", "to", "with", "of", "for", "and", "or", "up", "by", "from", "into", "across", "under", "behind", "inside") -> "Connecting / Sight Word"
            clean.endsWith("ing") || clean.endsWith("ed") || clean in setOf("wakes", "shine", "walks", "borrow", "return", "whispers", "play", "read", "run", "jump", "fly") -> "Action Word (Verb)"
            clean in setOf("bright", "golden", "green", "yellow", "red", "blue", "white", "special", "unique", "busy", "early", "happy", "sunny", "cozy", "warm") -> "Describing Word (Adjective)"
            else -> "Naming Word (Noun)"
        }

        val englishDef = wordEnglishDefinitions[clean]
            ?: vocabMatch?.exampleSentence
            ?: when {
                partOfSpeech.contains("Sight Word") -> "A common connecting word used to link ideas smoothly in a sentence."
                partOfSpeech.contains("Verb") -> "An action word describing something happening or being done in the story."
                partOfSpeech.contains("Adjective") -> "A describing word that tells us how something looks, feels, or shines."
                else -> "An important story word that names a person, place, object, or nature element."
            }

        val nativeMeaning = when {
            langCode.contains("ur") -> {
                wordMeaningsUrdu[clean]
                    ?: vocabMatch?.urduMeaning
                    ?: when {
                        partOfSpeech.contains("Sight Word") -> "فقرے کو جوڑنے والا عام لفظ"
                        partOfSpeech.contains("Verb") -> "کام یا حرکت ظاہر کرنے والا لفظ"
                        partOfSpeech.contains("Adjective") -> "صفت یا خوبی بتانے والا لفظ"
                        else -> "کہانی کا اہم لفظ"
                    }
            }
            langCode.contains("hi") -> {
                wordMeaningsHindi[clean]
                    ?: vocabMatch?.hindiMeaning
                    ?: when {
                        partOfSpeech.contains("Sight Word") -> "वाक्य को जोड़ने वाला सामान्य शब्द"
                        partOfSpeech.contains("Verb") -> "क्रिया या काम बताने वाला शब्द"
                        partOfSpeech.contains("Adjective") -> "विशेषता बताने वाला शब्द"
                        else -> "कहानी का महत्वपूर्ण शब्द"
                    }
            }
            else -> englishDef
        }

        val exampleText = if (pageSentence.isNotBlank()) {
            pageSentence
        } else {
            vocabMatch?.exampleSentence ?: "We read the word \"$display\" in our story."
        }

        val syllableSpoken = if (syllables.size > 1) {
            "Syllables: ${syllables.joinToString(", ")}."
        } else {
            "Spelled: ${clean.toCharArray().joinToString(" ")}."
        }

        val spokenScript = "$display. $syllableSpoken $partOfSpeech. Meaning: $englishDef"

        return WordTutorDetails(
            displayWord = display,
            cleanWord = clean.ifEmpty { rawWord },
            phonetics = phonetics,
            partOfSpeech = partOfSpeech,
            syllables = syllables,
            englishMeaning = englishDef,
            nativeMeaning = nativeMeaning,
            exampleUsage = exampleText,
            spokenExplanation = spokenScript
        )
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
            coverAssetPath = "books/sw_44485/image_1.webp",
            isFeaturedHero = true,
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_44485/image_1.webp", "Every morning, our neighborhood street wakes up with bright colors, happy music, and friendly faces.", "Cada mañana, la calle despierta con colores brillantes.", "ہر صبح ہماری گلی چمکدار رنگوں، خوشگوار موسیقی اور دوستانہ چہروں کے ساتھ جاگتی ہے۔", "हर सुबह हमारी गली चमकीले रंगों और खुशहाल संगीत के साथ जागती है।", setOf("morning", "bright", "friends")),
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
                StoryPage(1, "books/sw_45764/image_1.webp", "\"Oh, no!\" Look at the clock on the library wall.", "\"¡Oh, no!\"", "اوہ نہیں! لائبریری کی دیوار پر لگی گھڑی دیکھو۔", "\"ओह नहीं!\" लाइब्रेरी की घड़ी देखो।", setOf("books")),
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
            coverAssetPath = "books/sw_13916/image_19.webp",
            categoryTag = "Bedtime & Folktales",
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_19.webp", "When the golden sun goes down, tiny silver stars twinkle above the quiet green hills.", "Cuando el sol dorado se oculta, pequeñas estrellas brillan.", "جب سنہرا سورج غروب ہوتا ہے تو خاموش پہاڑیوں کے اوپر ننھے ستارے جگمگاتے ہیں۔", "जब सुनहरा सूरज ढलता है, तो पहाड़ियों के ऊपर नन्हे तारे टिमटिमाते हैं।", setOf("bright", "gleamed")),
                StoryPage(2, "books/sw_13916/image_20.webp", "The little ladybird folds her glowing yellow wings and whispers, \"Good night, little star!\"", "La pequeña mariquita cierra los ojos y susurra: \"¡Buenas noches, estrellita!\"", "ننھی لیڈی برڈ اپنے چمکتے پر سمیٹتی ہے اور سرگوشی کرتی ہے، شب بخیر ننھے ستارے!", "नन्ही लेडीबर्ड अपने पंख समेटती है और कहती है, \"शुभ रात्रि, नन्हे तारे!\"", setOf("ladybird", "whispers")),
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
                StoryPage(3, "books/sw_44485/image_16.webp", "At the beach boardwalk, they share two scoops of sunshine yellow ice cream together!", "En la playa comparten helado amarillo.", "ساحل پر وہ دونوں مل کر دھوپ جیسی پیلی آئس کریم مزے سے کھاتے ہیں!", "समुद्र तट पर वे मिलकर आइसक्रीम का आनंद लेते हैं!", setOf("beach", "yellow"),
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
                StoryPage(2, "books/sw_44485/image_15.webp", "Captain Zara shows Nicole a brass pocket compass that always points north across the wide blue ocean.", "La marinera le muestra una brújula dorada.", "کیپٹن زارا نکول کو ایک سنہری قطب نما دکھاتی ہے جو کھلے سمندر میں ہمیشہ شمال کی سمت بتاتا ہے۔", "नाविक ज़ारा निकोल को एक सुनहरा कंपास दिखाती है।", setOf("sailor", "bright")),
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
            coverAssetPath = "books/sw_45764/image_15.webp",
            categoryTag = "Bedtime & Folktales",
            bookType = "SERIES",
            seriesTag = "Lulu Library • Ep 3",
            pages = listOf(
                StoryPage(1, "books/sw_45764/image_11.webp", "Back home with her borrowed library books, Lulu built a cozy reading tent out of blankets and pillows.", "En casa Lulu construyó una tienda de lectura con mantas.", "لائبریری سے لائی گئی نئی کتابوں کے ساتھ لولو نے گھر آ کر کمبل اور تکیوں سے پڑھنے کا ایک خیمہ بنایا۔", "घर आकर लुलु ने कंबल और तकियों से पढ़ने का एक सुंदर तंबू बनाया।", setOf("books", "borrow")),
                StoryPage(2, "books/sw_45764/image_13.webp", "With a warm flashlight beam, she read chapters about deep-sea whales, mountain eagles, and ancient castles.", "Con una linterna leyó capítulos sobre ballenas y águilas.", "ٹارچ کی نرم روشنی میں اس نے سمندری وہیل، پہاڑی عقابوں اور قدیم قلعوں کے ابواب پڑھے۔", "टॉर्च की रोशनी में उसने समुद्री व्हेल और पहाड़ी गरुड़ की कहानियाँ पढ़ीं।", setOf("books", "bright")),
                StoryPage(3, "books/sw_45764/image_15.webp", "Ma tucked her in and whispered, \"Every page you read opens a brand-new world tomorrow!\"", "Cada página que lees abre un mundo nuevo mañana.", "امی نے اسے کمبل اوڑھایا اور سرگوشی کی، تم جو بھی صفحہ پڑھتی ہو وہ کل ایک نئی دنیا کا دروازہ کھولتا ہے!", "माँ ने प्यार से कहा, \"हर पन्ना जो तुम पढ़ती हो, एक नई दुनिया खोलता है!\"", setOf("books", "whispers"),
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
                StoryPage(3, "books/sw_13916/image_19.webp", "One day, young readers like you will build new spacecraft and explore the stars above!", "Algún día jóvenes lectores como tú explorarán las estrellas.", "ایک دن تم جیسے ہونہار قارئین نئے خلائی جہاز بنائیں گے اور ستاروں سے آگے کی دنیا دریافت کریں گے!", "एक दिन तुम जैसे होनहार पाठक अंतरिक्ष और तारों की खोज करेंगे!", setOf("bright", "unique"),
                    StoryQuizQuestion("What tool do astronomers use to study distant planets and galaxies?", listOf("Powerful telescopes", "Pocket umbrellas", "Kitchen spoons"), 0)
                )
            )
        ),

        // 37. Level 1 • Animals & Nature: "Sunny Garden Walk"
        StoryBook(
            id = "book_sunny_garden_walk",
            title = "Sunny Garden Walk",
            nativeTitleUrdu = "روشن باغ کی صبح کی سیر",
            nativeTitleHindi = "धूप भरे बग़ीचे की सैर",
            author = "Catherine Holtzhausen",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_13916/image_1.webp",
            categoryTag = "Animals & Nature",
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_1.webp", "The morning sun shines on the green garden leaves and bright wildflowers.", "El sol de la mañana brilla sobre las hojas verdes.", "صبح کا سورج باغ کے ہرے پتوں اور رنگین پھولوں پر چمکتا ہے۔", "सुबह का सूरज बग़ीचे के हरे पत्तों और फूलों पर चमकता है।", setOf("morning", "garden", "leaves")),
                StoryPage(2, "books/sw_13916/image_2.webp", "Lara the little yellow ladybird wakes up on a soft leaf and smiles at the sky.", "Lara la mariquita amarilla despierta en una hoja suave.", "ننھی پیلی لیڈی برڈ لارا ایک نرم پتے پر جاگتی ہے اور آسمان کو دیکھ کر مسکراتی ہے۔", "नन्ही पीली लेडीबर्ड लारा एक नरम पत्ते पर जागती है और मुस्कुराती है।", setOf("ladybird", "yellow", "sky")),
                StoryPage(3, "books/sw_13916/image_4.webp", "All the garden bugs come out to enjoy the warm morning sunlight together!", "Todos los bichitos salen a disfrutar del sol.", "باغ کے تمام ننھے کیڑے مل کر صبح کی میٹھی دھوپ کا مزہ لینے باہر آتے ہیں!", "बग़ीचे के सभी नन्हे दोस्त सुबह की धूप का आनंद लेने बाहर आते हैं!", setOf("friends", "sunlight"),
                    StoryQuizQuestion("Where does Lara wake up in the morning?", listOf("On a soft green garden leaf", "Inside a train", "Under the ocean"), 0)
                )
            )
        ),

        // 38. Level 1 • Animals & Nature: "Two Little Ladybirds"
        StoryBook(
            id = "book_two_ladybirds",
            title = "Two Little Ladybirds",
            nativeTitleUrdu = "دو ننھی لیڈی برڈز کی دوستی",
            nativeTitleHindi = "दो नन्ही लेडीबर्ड्स की दोस्ती",
            author = "Nadene Kriel",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_13916/image_3.webp",
            categoryTag = "Animals & Nature",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: y • r • w",
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_3.webp", "One ladybird has bright yellow wings, and her friend has bright red wings.", "Una mariquita tiene alas amarillas y su amiga alas rojas.", "ایک لیڈی برڈ کے چمکدار پیلے پر ہیں اور اس کی سہیلی کے چمکدار سرخ پر ہیں۔", "एक लेडीबर्ड के पीले पंख हैं और उसकी सहेली के लाल पंख हैं।", setOf("ladybird", "yellow", "red", "wings")),
                StoryPage(2, "books/sw_13916/image_4.webp", "They hop from flower to flower counting golden petals in the breeze.", "Saltan de flor en flor contando pétalos.", "وہ ہوا میں ایک پھول سے دوسرے پھول پر کودتی ہیں اور سنہری پتیاں گنتی ہیں۔", "वे एक फूल से दूसरे फूल पर जाकर सुनहरी पंखुड़ियाँ गिनती हैं।", setOf("flowers", "golden")),
                StoryPage(3, "books/sw_13916/image_5.webp", "Yellow and red look so cheerful together in the green meadow!", "El amarillo y el rojo se ven alegres juntos.", "ہرے باغ میں پیلا اور سرخ رنگ ایک ساتھ بے حد خوبصورت لگتے ہیں!", "हरे बग़ीचे में पीला और लाल रंग एक साथ बहुत सुंदर लगते हैं!", setOf("yellow", "red", "green"),
                    StoryQuizQuestion("What two wing colors do the ladybird friends have?", listOf("Bright yellow and bright red", "Black and grey", "Blue and brown"), 0)
                )
            )
        ),

        // 39. Level 1 • School & Kindness: "Hello Miss Miya!"
        StoryBook(
            id = "book_hello_miss_miya",
            title = "Hello Miss Miya!",
            nativeTitleUrdu = "مس میا کی پیاری کلاس",
            nativeTitleHindi = "मिस मिया की प्यारी कक्षा",
            author = "Catherine Holtzhausen",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_13916/image_13.webp",
            categoryTag = "School & Kindness",
            bookSource = "CLASSROOM",
            assignedByTeacher = "Miss Miya • Grade 1",
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_7.webp", "The school bell rings in the garden and all the little bugs take their seats.", "La campana suena y todos toman asiento.", "باغ میں اسکول کی گھنٹی بجتی ہے اور تمام ننھے طالب علم اپنی نشستوں پر بیٹھ جاتے ہیں۔", "बग़ीचे में स्कूल की घंटी बजती है और सभी बच्चे अपनी जगह पर बैठते हैं।", setOf("school", "friends")),
                StoryPage(2, "books/sw_13916/image_13.webp", "\"Good morning, class!\" says Miss Miya with a warm and gentle smile.", "Buenos días clase, dice la maestra con una sonrisa.", "صبح بخیر بچو! مس میا بڑی شفقت اور پیاری مسکراہٹ کے ساتھ کہتی ہیں۔", "\"सुप्रभात बच्चों!\" मिस मिया प्यारी मुस्कान के साथ कहती हैं।", setOf("morning", "teacher", "smile")),
                StoryPage(3, "books/sw_13916/image_15.webp", "Miss Miya teaches everyone to share books, help friends, and be kind every day!", "La maestra enseña a compartir libros y ser amables.", "مس میا سب کو کتابیں بانٹنا، دوستوں کی مدد کرنا اور ہر روز مہربان رہنا سکھاتی ہیں!", "मिस मिया सबको किताबें बाँटना और दोस्तों की मदद करना सिखाती हैं!", setOf("books", "friends", "teacher"),
                    StoryQuizQuestion("Who is the kind teacher at the garden bug school?", listOf("Miss Miya", "Captain Zara", "Grandpa Farouk"), 0)
                )
            )
        ),

        // 40. Level 1 • Family & Friends: "Mama Ladybird's Hug"
        StoryBook(
            id = "book_mama_ladybird_hug",
            title = "Mama Ladybird's Hug",
            nativeTitleUrdu = "امی لیڈی برڈ کا پیار",
            nativeTitleHindi = "माँ लेडीबर्ड का प्यार",
            author = "Nadene Kriel",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_13916/image_8.webp",
            categoryTag = "Family & Friends",
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_8.webp", "Whenever little Lara feels sad, she talks to her loving Mama.", "Cuando Lara se siente triste, habla con su mamá.", "جب بھی ننھی لارا اداس ہوتی ہے تو وہ اپنی پیاری امی سے بات کرتی ہے۔", "जब भी नन्ही लारा उदास होती है, वह अपनी प्यारी माँ से बात करती है।", setOf("mother", "ladybird")),
                StoryPage(2, "books/sw_13916/image_9.webp", "Mama listens carefully and cheers Lara up with a fun art project.", "Mamá la escucha y la alegra con pintura.", "امی غور سے اس کی بات سنتی ہیں اور مزیدار رنگوں سے اس کا دل بہلاتی ہیں۔", "माँ ध्यान से उसकी बात सुनती हैं और उसे खुश करती हैं।", setOf("painted", "happy")),
                StoryPage(3, "books/sw_13916/image_17.webp", "A warm hug from Mama always makes Lara smile again!", "Un abrazo de mamá siempre la hace sonreír.", "امی کے پیار بھرے گلے لگانے سے لارا کے چہرے پر ہمیشہ مسکراہٹ لوٹ آتی ہے!", "माँ के प्यार से गले लगाने पर लारा फिर से मुस्कुरा उठती है!", setOf("mother", "smile"),
                    StoryQuizQuestion("What always makes little Lara smile again?", listOf("A warm hug from her Mama", "A cold rainstorm", "Losing her shoe"), 0)
                )
            )
        ),

        // 41. Level 1 • Numbers & Rhymes: "One Round Red Ball"
        StoryBook(
            id = "book_one_round_ball",
            title = "One Round Red Ball",
            nativeTitleUrdu = "ایک گول سرخ گیند",
            nativeTitleHindi = "एक गोल लाल गेंद",
            author = "Marion Drew",
            illustrator = "Marion Drew",
            publisher = "Pratham Books • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_15195/image_2.webp",
            categoryTag = "Numbers & Rhymes",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: b • r • d",
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_15195/image_2.webp", "One round red ball rests on the soft green grass in the morning sun.", "Una pelota roja descansa en la hierba verde.", "ایک گول سرخ گیند صبح کی دھوپ میں نرم ہری گھاس پر رکھی ہے۔", "एक गोल लाल गेंद सुबह की धूप में हरी घास पर रखी है।", setOf("red", "ball", "green")),
                StoryPage(2, "books/sw_15195/image_3.webp", "Two happy hands pick up the red ball and toss it gently into the air.", "Dos manos felices toman la pelota roja.", "دو خوش ہاتھ سرخ گیند کو اٹھاتے ہیں اور ہلکے سے ہوا میں اچھالتے ہیں۔", "दो हाथ लाल गेंद को उठाते हैं और हवा में उछालते हैं।", setOf("red", "ball", "play")),
                StoryPage(3, "books/sw_15195/image_6.webp", "One, two, three—kick the ball and watch it roll across the lawn!", "Uno, dos, tres: patea la pelota.", "ایک، دو، تین—گیند کو کک مارو اور اسے لان میں لڑھکتے ہوئے دیکھو!", "एक, दो, तीन—गेंद को किक मारो और उसे घास पर लुढ़कते देखो!", setOf("ball", "play"),
                    StoryQuizQuestion("What shape and color is the ball on the grass?", listOf("Round and red", "Square and blue", "Triangle and grey"), 0)
                )
            )
        ),

        // 42. Level 1 • Science & Weather: "My Bright Red Raincoat"
        StoryBook(
            id = "book_bright_red_raincoat",
            title = "My Bright Red Raincoat",
            nativeTitleUrdu = "میرا چمکدار سرخ برساتی کوٹ",
            nativeTitleHindi = "मेरा चमकीला लाल रेनकोट",
            author = "Khothatso Ranoosi",
            illustrator = "Marion Drew",
            publisher = "Pratham Books • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_15195/image_1.webp",
            categoryTag = "Science & Weather",
            pages = listOf(
                StoryPage(1, "books/sw_15195/image_1.webp", "When grey clouds gather in the sky, I put on my bright red raincoat.", "Cuando llegan las nubes, me pongo mi impermeable rojo.", "جب آسمان میں سرمئی بادل آتے ہیں تو میں اپنا چمکدار سرخ برساتی کوٹ پہن لیتا ہوں۔", "जब आसमान में बादल आते हैं, तो मैं अपना लाल रेनकोट पहन लेता हूँ।", setOf("clouds", "raincoat", "red")),
                StoryPage(2, "books/sw_15195/image_4.webp", "My red raincoat has a warm hood that keeps my head dry in the rain.", "Tiene una capucha que me mantiene seco.", "میرے سرخ برساتی کوٹ کی ٹوپی بارش میں میرے سر کو بالکل خشک رکھتی ہے۔", "मेरे लाल रेनकोट की टोपी बारिश में मुझे सूखा रखती है।", setOf("raincoat", "rain")),
                StoryPage(3, "books/sw_15195/image_5.webp", "I splash in the garden puddles and play with my big red ball!", "Juego con mi gran pelota roja.", "میں باغ کے پانی میں چھپاکے مارتا ہوں اور اپنی بڑی سرخ گیند سے کھیلتا ہوں!", "मैं बग़ीचे में अपनी बड़ी लाल गेंद के साथ खेलता हूँ!", setOf("ball", "play", "garden"),
                    StoryQuizQuestion("What keeps the child dry when rain falls from the clouds?", listOf("A bright red raincoat", "A paper hat", "A glass teacup"), 0)
                )
            )
        ),

        // 43. Level 1 • Adventure & Travel: "Where Did My Ball Go?"
        StoryBook(
            id = "book_where_did_ball_go",
            title = "Where Did My Ball Go?",
            nativeTitleUrdu = "میری گیند کہاں چلی گئی؟",
            nativeTitleHindi = "मेरी गेंद कहाँ चली गई?",
            author = "Marion Drew",
            illustrator = "Marion Drew",
            publisher = "Pratham Books • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_15195/image_11.webp",
            categoryTag = "Adventure & Travel",
            bookType = "SERIES",
            seriesTag = "Red Ball Adventures • Ep 1",
            pages = listOf(
                StoryPage(1, "books/sw_15195/image_10.webp", "I kicked my bouncy red ball so hard that it zoomed over the garden wall!", "Pateé mi pelota tan fuerte que voló sobre el muro.", "میں نے اپنی سرخ گیند کو اتنی زور سے کک ماری کہ وہ باغ کی دیوار کے اوپر سے نکل گئی!", "मैंने अपनी लाल गेंद को इतनी ज़ोर से किक मारी कि वह दीवार के पार चली गई!", setOf("ball", "garden")),
                StoryPage(2, "books/sw_15195/image_11.webp", "I look behind the green bushes and under the wooden bench. Where is it?", "Busco detrás de los arbustos verdes.", "میں ہری جھاڑیوں کے پیچھے اور لکڑی کے بینچ کے نیچے دیکھتا ہوں۔ وہ کہاں ہے؟", "मैं हरी झाड़ियों के पीछे और बेंच के नीचे ढूँढता हूँ।", setOf("green", "trees")),
                StoryPage(3, "books/sw_15195/image_14.webp", "There it is! My red ball is bouncing high above the treetops!", "Allí está, volando sobre los árboles.", "وہ رہی! میری سرخ گیند درختوں کی چوٹیوں کے اوپر اچھل رہی ہے!", "वह रही! मेरी लाल गेंद पेड़ों के ऊपर उछल रही है!", setOf("ball", "trees"),
                    StoryQuizQuestion("Where did the child finally spot the red ball?", listOf("High above the treetops", "Inside the kitchen pot", "Under the pillow"), 0)
                )
            )
        ),

        // 44. Level 1 • Family & Friends: "My Favorite Picture Book"
        StoryBook(
            id = "book_favorite_picture_book",
            title = "My Favorite Picture Book",
            nativeTitleUrdu = "میری پسندیدہ تصویروں والی کتاب",
            nativeTitleHindi = "मेरी पसंदीदा चित्रों वाली किताब",
            author = "Letta Machoga",
            illustrator = "Wiehan de Jager",
            publisher = "African Storybook • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_15058/image_1.webp",
            categoryTag = "Family & Friends",
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_15058/image_1.webp", "I hold my favorite picture book in the sunny courtyard.", "Sostengo mi libro favorito en el patio soleado.", "میں دھوپ والے صحن میں اپنی پسندیدہ تصویروں والی کتاب ہاتھ میں پکڑتا ہوں۔", "मैं धूप वाले आँगन में अपनी पसंदीदा किताब हाथ में पकड़ता हूँ।", setOf("books", "sunlight")),
                StoryPage(2, "books/sw_15058/image_2.webp", "Every page has bright drawings of animals, trees, and smiling children.", "Cada página tiene dibujos brillantes.", "ہر صفحے پر جانوروں، درختوں اور مسکراتے بچوں کی روشن تصاویر بنی ہیں۔", "हर पन्ने पर जानवरों, पेड़ों और मुस्कुराते बच्चों के सुंदर चित्र हैं।", setOf("bright", "trees")),
                StoryPage(3, "books/sw_15058/image_9.webp", "Reading my storybook makes every single day an exciting adventure!", "Leer mi libro hace que cada día sea una aventura.", "اپنی کہانی کی کتاب پڑھنا ہر دن کو ایک دلچسپ مہم بنا دیتا ہے!", "कहानी की किताब पढ़ना हर दिन को मज़ेदार बना देता है!", setOf("read", "story", "books"),
                    StoryQuizQuestion("What is inside the boy's favorite picture book?", listOf("Bright drawings of animals, trees, and children", "Only blank white pages", "Muddy shoes"), 0)
                )
            )
        ),

        // 45. Level 1 • Family & Friends: "Grandpa Farouk's Garden"
        StoryBook(
            id = "book_grandpa_garden",
            title = "Grandpa Farouk's Garden",
            nativeTitleUrdu = "دادا فاروق کا ہرا بھرا باغ",
            nativeTitleHindi = "दादा फ़ारूक़ का हरा-भरा बग़ीचा",
            author = "Letta Machoga",
            illustrator = "Wiehan de Jager",
            publisher = "African Storybook • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_15058/image_7.webp",
            categoryTag = "Family & Friends",
            pages = listOf(
                StoryPage(1, "books/sw_15058/image_7.webp", "Outside our home, Grandfather Farouk grows tall trees and golden marigolds.", "Afuera el abuelo cultiva árboles altos y flores.", "ہمارے گھر کے باہر دادا فاروق اونچے درخت اور گیندے کے سنہری پھول اگاتے ہیں۔", "हमारे घर के बाहर दादा फ़ारूक़ ऊँचे पेड़ और गेंदे के फूल उगाते हैं।", setOf("grandfather", "trees", "marigolds")),
                StoryPage(2, "books/sw_15058/image_8.webp", "Father helps Grandfather water the green plants every morning.", "Papá ayuda al abuelo a regar las plantas.", "ابو ہر صبح پودوں کو پانی دینے میں دادا جان کی مدد کرتے ہیں۔", "पिताजी हर सुबह पौधों को पानी देने में दादाजी की मदद करते हैं।", setOf("father", "grandfather", "water")),
                StoryPage(3, "books/sw_15058/image_1.webp", "After watering the garden, we sit in the shade and read a story together!", "Después nos sentamos a la sombra a leer.", "باغ کو پانی دینے کے بعد ہم سائے میں بیٹھ کر مل کر کہانی پڑھتے ہیں!", "बग़ीचे में पानी देने के बाद हम छाँव में बैठकर कहानी पढ़ते हैं!", setOf("garden", "read", "story"),
                    StoryQuizQuestion("Who helps Grandfather Farouk water the green plants?", listOf("Father", "A navy sailor", "A red crab"), 0)
                )
            )
        ),

        // 46. Level 1 • Animals & Nature: "Caterpillar's Morning Trail"
        StoryBook(
            id = "book_caterpillar_morning_trail",
            title = "Caterpillar's Morning Trail",
            nativeTitleUrdu = "کیٹرپلر کی صبح کی پگڈنڈی",
            nativeTitleHindi = "कैटरपिलर की सुबह की पगडंडी",
            author = "Somsanouk Bouttavong",
            illustrator = "Ounla Santi",
            publisher = "Room to Read • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_37273/image_1.webp",
            categoryTag = "Animals & Nature",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: c • t • p",
            pages = listOf(
                StoryPage(1, "books/sw_37273/image_1.webp", "Little green Caterpillar steps out onto the winding garden trail at sunrise.", "La pequeña oruga verde sale a caminar al amanecer.", "ننھی ہری کیٹرپلر سورج نکلتے ہی باغ کی پگڈنڈی پر سیر کے لیے نکلتی ہے۔", "नन्ही हरी कैटरपिलर सुबह बग़ीचे की पगडंडी पर सैर के लिए निकलती है।", setOf("caterpillar", "green", "garden")),
                StoryPage(2, "books/sw_37273/image_4.webp", "Yellow daisies and soft clover leaves wave hello along the path.", "Las margaritas amarillas saludan en el camino.", "راستے میں پیلے پھول اور نرم ہری پتیاں ہوا میں جھوم کر سلام کرتی ہیں۔", "रास्ते में पीले फूल और हरी पत्तियाँ हवा में झूमकर नमस्ते कहती हैं।", setOf("yellow", "flowers", "leaves")),
                StoryPage(3, "books/sw_37273/image_5.webp", "She walks all the way to the blue pond to say good morning to Frog!", "Camina hasta el estanque azul para saludar a la rana.", "وہ نیلے تالاب تک چل کر جاتی ہے تاکہ مینڈک کو صبح بخیر کہہ سکے!", "वह नीले तालाब तक जाकर मेंढक को सुप्रभात कहती है!", setOf("frog", "morning"),
                    StoryQuizQuestion("Where does Caterpillar walk to say good morning to Frog?", listOf("To the blue pond", "To the train station", "To the sweet shop"), 0)
                )
            )
        ),

        // 47. Level 1 • Animals & Nature: "Frog's Big Green Boot"
        StoryBook(
            id = "book_frog_big_boot",
            title = "Frog's Big Green Boot",
            nativeTitleUrdu = "مینڈک کا بڑا ہرا جوتا",
            nativeTitleHindi = "मेंढक का बड़ा हरा जूता",
            author = "Ounla Santi",
            illustrator = "Ounla Santi",
            publisher = "Room to Read • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_37273/image_6.webp",
            categoryTag = "Animals & Nature",
            pages = listOf(
                StoryPage(1, "books/sw_37273/image_5.webp", "Friendly green Frog wears big green sneakers when he hops by the pond.", "La rana verde usa zapatillas grandes junto al estanque.", "دوستانہ ہرا مینڈک تالاب کے کنارے کودتے وقت بڑے ہرے جوتے پہنتا ہے۔", "दोस्ताना हरा मेंढक तालाब के पास बड़े हरे जूते पहनता है।", setOf("frog", "green", "shoe")),
                StoryPage(2, "books/sw_37273/image_6.webp", "When little Caterpillar tries on Frog's shoe, she fits right inside it like a boat!", "Cuando la oruga prueba el zapato, cabe entera adentro.", "جب ننھی کیٹرپلر مینڈک کا جوتا پہنتی ہے تو وہ کشتی کی طرح پورا اس کے اندر سما جاتی ہے!", "जब नन्ही कैटरपिलर मेंढक का जूता पहनती है, तो वह नाव की तरह उसके अंदर बैठ जाती है!", setOf("caterpillar", "shoe", "frog")),
                StoryPage(3, "books/sw_37273/image_7.webp", "Frog and Caterpillar laugh together in the warm morning sun!", "La rana y la oruga ríen juntas bajo el sol.", "مینڈک اور کیٹرپلر صبح کی گرم دھوپ میں مل کر خوشی سے ہنستے ہیں!", "मेंढक और कैटरपिलर सुबह की धूप में मिलकर हँसते हैं!", setOf("frog", "caterpillar", "happy"),
                    StoryQuizQuestion("What happens when little Caterpillar tries on Frog's big shoe?", listOf("She fits right inside it like a little boat", "It is too tight", "It turns into a bird"), 0)
                )
            )
        ),

        // 48. Level 1 • Adventure & Travel: "Morning on Our Colorful Street"
        StoryBook(
            id = "book_morning_colorful_street",
            title = "Morning on Our Colorful Street",
            nativeTitleUrdu = "ہماری رنگین گلی کی خوبصورت صبح",
            nativeTitleHindi = "हमारी रंगीन गली की सुंदर सुबह",
            author = "Mabel Mnensa",
            illustrator = "Ntembiso Velemane",
            publisher = "Book Dash • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_44485/image_1.webp",
            categoryTag = "Adventure & Travel",
            pages = listOf(
                StoryPage(1, "books/sw_44485/image_1.webp", "Our street has bright yellow, blue, and coral houses lined with flower stalls.", "Nuestra calle tiene casas de colores brillantes.", "ہماری گلی میں پیلے، نیلے اور سرخ رنگوں کے گھر ہیں جن کے باہر پھولوں کے اسٹال لگے ہیں۔", "हमारी गली में पीले, नीले और लाल रंगों के सुंदर घर और फूलों की दुकानें हैं।", setOf("street", "colors", "flowers")),
                StoryPage(2, "books/sw_44485/image_2.webp", "Nicole and Mom walk past the garden gate waving to their neighbors.", "Nicole y mamá saludan a sus vecinos.", "نکول اور امی باغ کے دروازے کے پاس سے گزرتے ہوئے اپنے ہمسایوں کو ہاتھ ہلاتی ہیں۔", "निकोल और माँ बग़ीचे के दरवाज़े के पास पड़ोसियों को हाथ हिलाती हैं।", setOf("garden", "gate", "friends")),
                StoryPage(3, "books/sw_44485/image_3.webp", "Everyone shares a warm smile as a brand-new day begins in town!", "Todos comparten una sonrisa cálida.", "شہر میں نئے دن کے آغاز پر ہر کوئی ایک دوسرے کو دیکھ کر مسکراتا ہے!", "नए दिन की शुरुआत पर सब एक-दूसरे को देखकर मुस्कुराते हैं!", setOf("smile", "morning", "street"),
                    StoryQuizQuestion("What lines the colorful street outside the bright houses?", listOf("Cheerful flower stalls and friendly neighbors", "Snowy mountains", "Deep ocean waves"), 0)
                )
            )
        ),

        // 49. Level 1 • Family & Friends: "A High-Five for Nicole"
        StoryBook(
            id = "book_high_five_nicole",
            title = "A High-Five for Nicole",
            nativeTitleUrdu = "نکول کی نئی سہیلی",
            nativeTitleHindi = "निकोल की नई सहेली",
            author = "Mabel Mnensa",
            illustrator = "Ntembiso Velemane",
            publisher = "Book Dash • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_44485/image_5.webp",
            categoryTag = "Family & Friends",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: h • f • n",
            pages = listOf(
                StoryPage(1, "books/sw_44485/image_4.webp", "Nicole steps onto the shiny silver train with her mother.", "Nicole sube al tren plateado con su mamá.", "نکول اپنی امی کے ساتھ چمکدار چاندی جیسی ٹرین میں سوار ہوتی ہے۔", "निकोल अपनी माँ के साथ चमकीली ट्रेन में चढ़ती है।", setOf("train", "mother")),
                StoryPage(2, "books/sw_44485/image_5.webp", "A smiling little girl in a pink dress waves her hand to say hello!", "Una niña con vestido rosa saluda con la mano.", "گلابی لباس میں ایک مسکراتی ہوئی ننھی بچی ہاتھ ہلا کر ہیلو کہتی ہے!", "गुलाबी ड्रेस पहने एक नन्ही बच्ची मुस्कुराकर हेलो कहती है!", setOf("smile", "friends")),
                StoryPage(3, "books/sw_44485/image_14.webp", "Mom nods kindly, and Nicole gives her new friend a cheerful high-five!", "Nicole choca los cinco con su nueva amiga.", "امی پیار سے سر ہلاتی ہیں اور نکول اپنی نئی سہیلی کو خوشی سے ہائی فائیو دیتی ہے!", "माँ मुस्कुराती हैं और निकोल अपनी नई सहेली से हाथ मिलाती है!", setOf("friends", "happy"),
                    StoryQuizQuestion("What color dress is Nicole's new friend on the train wearing?", listOf("A bright pink dress", "A black raincoat", "A green uniform"), 0)
                )
            )
        ),

        // 50. Level 1 • School & Kindness: "Five O'Clock at the Library"
        StoryBook(
            id = "book_five_oclock_library",
            title = "Five O'Clock at the Library",
            nativeTitleUrdu = "لائبریری کی گھڑی میں پانچ بجے",
            nativeTitleHindi = "लाइब्रेरी की घड़ी में पाँच बजे",
            author = "Clyde Beech",
            illustrator = "Clyde Beech",
            publisher = "Book Dash • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_45764/image_1.webp",
            categoryTag = "School & Kindness",
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_45764/image_1.webp", "Tick-tock! The big round clock on the library wall points to five o'clock.", "El reloj redondo de la biblioteca marca las cinco.", "ٹک ٹک! لائبریری کی دیوار پر لگی بڑی گول گھڑی پانچ بجا رہی ہے۔", "टिक-टिक! लाइब्रेरी की दीवार पर लगी गोल घड़ी में पाँच बज रहे हैं।", setOf("library", "books")),
                StoryPage(2, "books/sw_45764/image_7.webp", "Lulu is sitting on a soft cushion reading a colorful animal book.", "Lulu lee un libro colorido de animales.", "لولو ایک نرم گدی پر بیٹھی جانوروں کی رنگین کتاب پڑھ رہی ہے۔", "लुलु एक नरम गद्दी पर बैठकर जानवरों की रंगीन किताब पढ़ रही है।", setOf("read", "books")),
                StoryPage(3, "books/sw_45764/image_12.webp", "She borrows the book with her library card so she can keep reading at home!", "Toma prestado el libro para seguir leyendo en casa.", "وہ اپنے لائبریری کارڈ پر کتاب ادھار لے لیتی ہے تاکہ گھر جا کر بھی پڑھ سکے!", "वह अपने कार्ड से किताब घर ले जाती है ताकि घर पर भी पढ़ सके!", setOf("borrow", "library", "books"),
                    StoryQuizQuestion("What time does the round clock on the library wall show?", listOf("Five o'clock", "Midnight", "Noon"), 0)
                )
            )
        ),

        // 51. Level 1 • Numbers & Rhymes: "Count the Golden Stars"
        StoryBook(
            id = "book_count_golden_stars",
            title = "Count the Golden Stars",
            nativeTitleUrdu = "آسمان کے سنہری ستارے گنو",
            nativeTitleHindi = "आसमान के सुनहरे तारे गिनो",
            author = "Martha Evans",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_13916/image_20.webp",
            categoryTag = "Numbers & Rhymes",
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_19.webp", "One, two, three silver stars begin to twinkle above the green hills at sunset.", "Una, dos, tres estrellas brillan sobre las colinas.", "غروبِ آفتاب کے وقت ہری پہاڑیوں کے اوپر ایک، دو، تین چمکدار ستارے ٹمٹمانے لگتے ہیں۔", "शाम ढलते ही हरी पहाड़ियों के ऊपर एक, दो, तीन तारे टिमटिमाने लगते हैं।", setOf("stars", "green")),
                StoryPage(2, "books/sw_13916/image_20.webp", "Four, five, six glowing stars shine down on little Lara's flower bed.", "Cuatro, cinco, seis estrellas iluminan la flor de Lara.", "چار، پانچ، چھ جگمگاتے ستارے ننھی لارا کے پھولوں والے بستر پر روشنی بکھیرتے ہیں۔", "चार, पाँच, छह चमकते तारे नन्ही लारा के फूल पर रोशनी बिखेरते हैं।", setOf("stars", "flowers", "ladybird")),
                StoryPage(3, "books/sw_15195/image_17.webp", "And one big round silver moon watches over the sleeping garden all night long!", "Y una gran luna plateada cuida el jardín.", "اور ایک بڑا گول چاند پوری رات سوتے ہوئے باغ کی رکھوالی کرتا ہے!", "और एक बड़ा गोल चाँद पूरी रात सोते हुए बग़ीचे पर चमकता है!", setOf("moon", "garden"),
                    StoryQuizQuestion("What watches over the sleeping garden along with the twinkling stars?", listOf("One big round silver moon", "A red tram", "A kitchen stove"), 0)
                )
            )
        ),

        // 52. Level 1 • Family & Friends: "Helping in the Courtyard"
        StoryBook(
            id = "book_helping_courtyard",
            title = "Helping in the Courtyard",
            nativeTitleUrdu = "گھر کے صحن میں ہاتھ بٹانا",
            nativeTitleHindi = "घर के आँगन में हाथ बँटाना",
            author = "Letta Machoga",
            illustrator = "Wiehan de Jager",
            publisher = "African Storybook • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_15058/image_3.webp",
            categoryTag = "Family & Friends",
            pages = listOf(
                StoryPage(1, "books/sw_15058/image_3.webp", "In our family, everyone helps each other with a cheerful heart.", "En nuestra familia todos se ayudan con alegría.", "ہمارے خاندان میں ہر کوئی خوش دلی سے ایک دوسرے کا ہاتھ بٹاتا ہے۔", "हमारे परिवार में सब खुशी से एक-दूसरे की मदद करते हैं।", setOf("friends", "happy")),
                StoryPage(2, "books/sw_15058/image_6.webp", "Mother and Grandmother prepare delicious food in the clean kitchen.", "Mamá y la abuela preparan comida deliciosa.", "امی اور دادی جان صاف ستھرے باورچی خانے میں مزیدار کھانا تیار کرتی ہیں۔", "माँ और दादीजी रसोई में स्वादिष्ट भोजन बनाती हैं।", setOf("mother", "grandmother")),
                StoryPage(3, "books/sw_15058/image_8.webp", "And when the chores are done, I read a happy story to the whole family!", "Cuando terminan las tareas, leo un cuento a todos.", "اور جب سارے کام مکمل ہو جاتے ہیں تو میں پورے خاندان کو ایک پیاری کہانی پڑھ کر سناتا ہوں!", "और जब काम पूरा हो जाता है, तो मैं पूरे परिवार को कहानी सुनाता हूँ!", setOf("read", "story"),
                    StoryQuizQuestion("What does the boy do for the whole family when the chores are done?", listOf("Reads a happy story to everyone", "Breaks the fence", "Hides the shoes"), 0)
                )
            )
        ),

        // 53. Level 2 • Animals & Nature: "Sesa the Friendly Spider"
        StoryBook(
            id = "book_sesa_friendly_spider",
            title = "Sesa the Friendly Spider",
            nativeTitleUrdu = "سیسا مکڑی کا چاندی جیسا جالا",
            nativeTitleHindi = "सेसा मकड़ी का चाँदी जैसा जाला",
            author = "Catherine Holtzhausen",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_13916/image_6.webp",
            categoryTag = "Animals & Nature",
            bookType = "SERIES",
            seriesTag = "Garden Series • Ep 5",
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_6.webp", "High between two flowering branches, Sesa the spider weaves a geometric silver web.", "Sesa la araña teje una telaraña plateada entre dos ramas.", "دو پھولدار شاخوں کے درمیان سیسا مکڑی چاندی جیسا خوبصورت جالا بنتی ہے۔", "दो फूलों वाली शाखाओं के बीच सेसा मकड़ी चाँदी जैसा जाला बुनती है।", setOf("spider", "trees")),
                StoryPage(2, "books/sw_13916/image_5.webp", "Morning dewdrops cling to the silk threads and sparkle like tiny pearls in the sunlight.", "Las gotas de rocío brillan como perlas al sol.", "شبنم کے قطرے ریشمی تاروں پر موتیوں کی طرح دھوپ میں چمکتے ہیں۔", "ओस की बूँदें रेशमी धागों पर मोतियों की तरह धूप में चमकती हैं।", setOf("sunlight", "bright")),
                StoryPage(3, "books/sw_13916/image_4.webp", "When Lara the yellow ladybird flies by, Sesa waves four of her eight legs with a big grin!", "Cuando Lara vuela cerca, Sesa saluda con una gran sonrisa.", "جب پیلی لیڈی برڈ لارا پاس سے اڑتی ہے تو سیسا مسکرا کر ہاتھ ہلاتی ہے!", "जब पीली लेडीबर्ड लारा पास से गुज़रती है, तो सेसा मुस्कुराकर हाथ हिलाती है!", setOf("ladybird", "spider", "smile"),
                    StoryQuizQuestion("What sparkles like tiny pearls on Sesa's silver web?", listOf("Morning dewdrops in the sunlight", "Chocolate candies", "Brass coins"), 0)
                )
            )
        ),

        // 54. Level 2 • School & Kindness: "Lara's Red Paint Surprise"
        StoryBook(
            id = "book_lara_red_paint",
            title = "Lara's Red Paint Surprise",
            nativeTitleUrdu = "لارا کے سرخ رنگ کی حیرت",
            nativeTitleHindi = "लारा के लाल रंग का अचरज",
            author = "Nadene Kriel",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_13916/image_9.webp",
            categoryTag = "School & Kindness",
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_9.webp", "With a soft brush and ruby-red paint, Mama covered Lara's yellow wings.", "Con un pincel suave, mamá pintó de rojo las alas de Lara.", "نرم برش اور سرخ رنگ کے ساتھ امی نے لارا کے پیلے پروں کو سرخ کر دیا۔", "नरम ब्रश और लाल रंग से माँ ने लारा के पीले पंखों को लाल रंग दिया।", setOf("painted", "red", "wings")),
                StoryPage(2, "books/sw_13916/image_10.webp", "On the way to bug school, Bibi Butterfly and Manto Mantis walked right past without recognizing her!", "Camino a la escuela, sus amigos no la reconocieron.", "اسکول جاتے ہوئے راستے میں بی بی تتلی اور مینٹو مینٹس اسے پہچانے بغیر ہی گزر گئے!", "स्कूल के रास्ते में बीबी तितली और मैंटो उसे पहचाने बिना ही आगे निकल गए!", setOf("school", "butterfly")),
                StoryPage(3, "books/sw_13916/image_12.webp", "Lara realized that blending in was not nearly as fun as shining in her own natural yellow color!", "Lara comprendió que brillar con su propio color era mucho mejor.", "لارا کو احساس ہوا کہ دوسروں جیسا دکھنے سے کہیں بہتر اپنی اصل سنہری شناخت کے ساتھ چمکنا ہے!", "लारा समझ गई कि अपनी असली पीली पहचान के साथ चमकना सबसे अच्छा है!", setOf("yellow", "special"),
                    StoryQuizQuestion("Why didn't Bibi Butterfly recognize Lara on the way to school?", listOf("Lara's yellow wings were painted red", "It was pitch dark at night", "Lara was riding a train"), 0)
                )
            )
        ),

        // 55. Level 2 • School & Kindness: "The Mushroom Seat at Recess"
        StoryBook(
            id = "book_mushroom_seat_recess",
            title = "The Mushroom Seat at Recess",
            nativeTitleUrdu = "اسکول کی چھٹی اور سچے دوست",
            nativeTitleHindi = "स्कूल की छुट्टी और सच्चे दोस्त",
            author = "Catherine Holtzhausen",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_13916/image_12.webp",
            categoryTag = "School & Kindness",
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_11.webp", "At recess time, all the little bugs played tag around the clover patch.", "En el recreo todos jugaban alrededor del trébol.", "بریک کے وقت تمام ننھے کیڑے پھولوں کے گرد دوڑنے والا کھیل کھیل رہے تھے۔", "छुट्टी के समय सभी नन्हे दोस्त फूलों के पास खेल रहे थे।", setOf("play", "friends")),
                StoryPage(2, "books/sw_13916/image_12.webp", "Lara sat quietly on a spotted mushroom stool until Miss Miya came over to check on her.", "Lara se sentó en un hongo hasta que la maestra se acercó.", "لارا ایک مشروم کی کرسی پر خاموشی سے بیٹھی رہی یہاں تک کہ مس میا اس کے پاس آئیں۔", "लारा एक मशरूम पर चुपचाप बैठी रही जब तक मिस मिया उसके पास नहीं आईं।", setOf("teacher", "school")),
                StoryPage(3, "books/sw_13916/image_14.webp", "As soon as her classmates learned it was Lara, they cheered and invited her to lead the game!", "Cuando sus compañeros supieron que era Lara, la invitaron a jugar.", "جیسے ہی ہم جماعتوں کو معلوم ہوا کہ یہ لارا ہے، سب نے خوشی سے اسے کھیل کا کپتان بنا لیا!", "जैसे ही दोस्तों को पता चला कि यह लारा है, सबने खुशी से उसे खेल में शामिल कर लिया!", setOf("friends", "happy"),
                    StoryQuizQuestion("What did Lara's classmates do when they realized it was Lara?", listOf("Cheered and invited her to play together", "Walked away", "Closed the school gate"), 0)
                )
            )
        ),

        // 56. Level 2 • Family & Friends: "Quiet Steps in the Hallway"
        StoryBook(
            id = "book_quiet_steps_hallway",
            title = "Quiet Steps in the Hallway",
            nativeTitleUrdu = "برآمدے میں دبے پاؤں",
            nativeTitleHindi = "बरामदे में दबे पाँव",
            author = "Letta Machoga",
            illustrator = "Wiehan de Jager",
            publisher = "African Storybook • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_15058/image_5.webp",
            categoryTag = "Family & Friends",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: qu • st • sh",
            pages = listOf(
                StoryPage(1, "books/sw_15058/image_4.webp", "Baby sister is dreaming sweetly under her warm patchwork quilt.", "La hermanita sueña bajo su manta tibia.", "ننھی بہن اپنے گرم رنگین لحاف کے نیچے میٹھے خواب دیکھ رہی ہے۔", "छोटी बहन अपने नरम कंबल के नीचे मीठे सपने देख रही है।", setOf("sister", "whispers")),
                StoryPage(2, "books/sw_15058/image_5.webp", "I hold my storybook close and tiptoe softly down the tiled hallway.", "Camino de puntillas por el pasillo con mi libro.", "میں اپنی کہانی کی کتاب سینے سے لگا کر دبے پاؤں برآمدے سے گزرتا ہوں۔", "मैं अपनी किताब थामे दबे पाँव बरामदे से गुज़रता हूँ।", setOf("books", "walk")),
                StoryPage(3, "books/sw_15058/image_9.webp", "Being a thoughtful big brother means keeping the room peaceful while she rests!", "Ser un buen hermano mayor significa cuidar su descanso.", "ایک اچھا بڑا بھائی ہونے کا مطلب ہے کہ جب چھوٹی بہن سوئے تو کمرے میں خاموشی کا خیال رکھا جائے!", "एक अच्छा बड़ा भाई अपनी छोटी बहन की नींद का पूरा ध्यान रखता है!", setOf("brother", "sister"),
                    StoryQuizQuestion("Why does the boy tiptoe softly down the hallway?", listOf("So he does not wake his sleeping baby sister", "Because the floor is wet", "Because he lost his shoes"), 0)
                )
            )
        ),

        // 57. Level 2 • Adventure & Travel: "Bounce Over the Bushes"
        StoryBook(
            id = "book_bounce_over_bushes",
            title = "Bounce Over the Bushes",
            nativeTitleUrdu = "جھاڑیوں کے اوپر اونچی چھلانگ",
            nativeTitleHindi = "झाड़ियों के ऊपर ऊँची उछाल",
            author = "Khothatso Ranoosi",
            illustrator = "Marion Drew",
            publisher = "Pratham Books • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_15195/image_8.webp",
            categoryTag = "Adventure & Travel",
            bookType = "SERIES",
            seriesTag = "Red Ball Adventures • Ep 2",
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_15195/image_6.webp", "In the sunny garden, I practice kicking my red ball with the tip of my sneaker.", "En el jardín practico patear mi pelota roja.", "روشن باغ میں، میں اپنے جوتے کی نوک سے سرخ گیند کو کک مارنے کی مشق کرتا ہوں۔", "धूप भरे बग़ीचे में मैं अपने जूते से लाल गेंद को किक मारने का अभ्यास करता हूँ।", setOf("ball", "shoe", "garden")),
                StoryPage(2, "books/sw_15195/image_8.webp", "Boing! The red ball bounces right over the flowering hibiscus bushes!", "La pelota salta sobre los arbustos en flor.", "بوائنگ! سرخ گیند پھولوں والی جھاڑیوں کے بالکل اوپر سے اچھل کر نکل جاتی ہے!", "लाल गेंद फूलों वाली झाड़ियों के ऊपर से उछलकर निकल जाती है!", setOf("ball", "flowers")),
                StoryPage(3, "books/sw_15195/image_12.webp", "I run around the garden path with a big grin to catch it before it stops rolling!", "Corro por el sendero del jardín para atraparla.", "میں مسکراتے ہوئے باغ کے راستے پر دوڑتا ہوں تاکہ گیند رکنے سے پہلے اسے پکڑ لوں!", "मैं मुस्कुराते हुए बग़ीचे के रास्ते पर दौड़कर गेंद को पकड़ लेता हूँ!", setOf("garden", "smile"),
                    StoryQuizQuestion("What did the bouncy red ball jump right over?", listOf("The flowering garden bushes", "A silver train", "A library shelf"), 0)
                )
            )
        ),

        // 58. Level 2 • Adventure & Travel: "Searching the Whole Yard"
        StoryBook(
            id = "book_searching_whole_yard",
            title = "Searching the Whole Yard",
            nativeTitleUrdu = "پورے صحن میں گیند کی تلاش",
            nativeTitleHindi = "पूरे आँगन में गेंद की खोज",
            author = "Marion Drew",
            illustrator = "Marion Drew",
            publisher = "Pratham Books • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_15195/image_12.webp",
            categoryTag = "Adventure & Travel",
            bookType = "SERIES",
            seriesTag = "Red Ball Adventures • Ep 3",
            pages = listOf(
                StoryPage(1, "books/sw_15195/image_9.webp", "Whoosh! One mighty kick sent my red ball flying faster than a swallow!", "Una gran patada envió mi pelota muy alto.", "شوں! ایک طاقتور کک نے میری سرخ گیند کو ابابیل سے بھی تیز ہوا میں اڑا دیا!", "एक ज़ोरदार किक ने मेरी लाल गेंद को पक्षी से भी तेज़ उड़ा दिया!", setOf("ball", "flying")),
                StoryPage(2, "books/sw_15195/image_12.webp", "I searched by the garden gate, near the water tap, and behind the mango tree.", "Busqué junto a la puerta del jardín y detrás del árbol.", "میں نے باغ کے دروازے کے پاس، پانی کے نلکے کے قریب اور آم کے درخت کے پیچھے تلاش کیا۔", "मैंने बग़ीचे के दरवाज़े के पास और आम के पेड़ के पीछे ढूँढा।", setOf("garden", "gate", "tree")),
                StoryPage(3, "books/sw_15195/image_13.webp", "Then I looked straight up—and saw my ball gleaming against the bright blue sky!", "Miré hacia arriba y vi mi pelota en el cielo azul.", "پھر میں نے سیدھا اوپر دیکھا—اور میری گیند چمکدار نیلے آسمان میں جگمگا رہی تھی!", "फिर मैंने सीधा ऊपर देखा—मेरी गेंद नीले आसमान में चमक रही थी!", setOf("ball", "blue", "sky"),
                    StoryQuizQuestion("Where did the child finally see the red ball gleaming?", listOf("High up against the bright blue sky", "Inside a shoe box", "Under the kitchen table"), 0)
                )
            )
        ),

        // 59. Level 2 • Animals & Nature: "Caterpillar's Scratchy Shoe"
        StoryBook(
            id = "book_caterpillar_scratchy_shoe",
            title = "Caterpillar's Scratchy Shoe",
            nativeTitleUrdu = "ٹڈے کا کھردرا جوتا",
            nativeTitleHindi = "टिड्डे का चुभने वाला जूता",
            author = "Somsanouk Bouttavong",
            illustrator = "Ounla Santi",
            publisher = "Room to Read • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_37273/image_8.webp",
            categoryTag = "Animals & Nature",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: scr • gr • sh",
            pages = listOf(
                StoryPage(1, "books/sw_37273/image_7.webp", "In the tall meadow grass, green Grasshopper offered Caterpillar one of his jumping shoes.", "El saltamontes le prestó uno de sus zapatos de salto.", "لمبی گھاس میں ہرے ٹڈے نے کیٹرپلر کو اپنا اچھلنے والا جوتا پہننے کے لیے دیا۔", "लंबी घास में हरे टिड्डे ने कैटरपिलर को अपना कूदने वाला जूता दिया।", setOf("grasshopper", "caterpillar", "shoe")),
                StoryPage(2, "books/sw_37273/image_8.webp", "\"Ooh, this shoe has spiky little bristles for gripping grass blades!\" giggled Caterpillar.", "Este zapato tiene cerdas para agarrarse a la hierba.", "اوہ، اس جوتے میں تو گھاس پکڑنے کے لیے چھوٹے چھوٹے کانٹے ہیں! کیٹرپلر مسکرا کر بولی۔", "\"ओह, इस जूते में तो घास पकड़ने के लिए छोटे काँटे हैं!\" कैटरपिलर हँसकर बोली।", setOf("shoe", "green")),
                StoryPage(3, "books/sw_37273/image_9.webp", "She thanked Grasshopper kindly and continued her walk toward the sandy stream!", "Le agradeció amablemente y siguió caminando.", "اس نے ٹڈے کا شکریہ ادا کیا اور ریتیلے کنارے کی طرف اپنا سفر جاری رکھا!", "उसने टिड्डे को धन्यवाद दिया और रेतीले किनारे की ओर आगे बढ़ गई!", setOf("grasshopper", "walk"),
                    StoryQuizQuestion("Why did Grasshopper's shoe have spiky little bristles?", listOf("For gripping tall grass blades when jumping", "For swimming under water", "For baking bread"), 0)
                )
            )
        ),

        // 60. Level 2 • Animals & Nature: "Seashells on the Beach"
        StoryBook(
            id = "book_seashells_on_beach",
            title = "Seashells on the Beach",
            nativeTitleUrdu = "ساحل کی چمکدار سیپیاں",
            nativeTitleHindi = "समुद्र तट की चमकीली सीपियाँ",
            author = "Ounla Santi",
            illustrator = "Ounla Santi",
            publisher = "Room to Read • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_37273/image_10.webp",
            categoryTag = "Animals & Nature",
            pages = listOf(
                StoryPage(1, "books/sw_37273/image_9.webp", "Where the clear stream meets the ocean beach, pink and white seashells sparkle in the sand.", "En la playa brillan conchas rosas y blancas en la arena.", "جہاں صاف ندی سمندر کے ساحل سے ملتی ہے، وہاں گلابی اور سفید سیپیاں ریت میں چمکتی ہیں۔", "समुद्र के रेतीले तट पर गुलाबी और सफ़ेद सीपियाँ धूप में चमकती हैं।", setOf("beach", "water", "bright")),
                StoryPage(2, "books/sw_37273/image_10.webp", "Little red Crab arranges the shells in a circle around his sandy doorway.", "El cangrejo rojo ordena las conchas en círculo.", "ننھا سرخ کیکڑا اپنے ریتیلے گھر کے دروازے کے گرد سیپیوں کا دائرہ بناتا ہے۔", "नन्हा लाल केकड़ा अपने रेतीले घर के बाहर सीपियों का घेरा बनाता है।", setOf("crab", "red")),
                StoryPage(3, "books/sw_37273/image_11.webp", "Caterpillar admires the pretty shells before resting on a cool green leaf nearby!", "La oruga admira las conchas antes de descansar.", "کیٹرپلر قریب ہی ایک ٹھنڈے ہرے پتے پر آرام کرنے سے پہلے خوبصورت سیپیوں کی تعریف کرتی ہے!", "कैटरपिलर पास के हरे पत्ते पर आराम करने से पहले सुंदर सीपियों की तारीफ़ करती है!", setOf("caterpillar", "green", "leaves"),
                    StoryQuizQuestion("Who arranges the pink and white seashells around a sandy doorway?", listOf("Little red Crab", "Manto Mantis", "Grandmother"), 0)
                )
            )
        ),

        // 61. Level 2 • Adventure & Travel: "Waiting on the Platform"
        StoryBook(
            id = "book_waiting_on_platform",
            title = "Waiting on the Platform",
            nativeTitleUrdu = "پلیٹ فارم پر ٹرین کا انتظار",
            nativeTitleHindi = "प्लेटफ़ॉर्म पर ट्रेन का इंतज़ार",
            author = "Mabel Mnensa",
            illustrator = "Ntembiso Velemane",
            publisher = "Book Dash • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_44485/image_3.webp",
            categoryTag = "Adventure & Travel",
            bookType = "SERIES",
            seriesTag = "Coastline Train • Ep 4",
            pages = listOf(
                StoryPage(1, "books/sw_44485/image_2.webp", "Nicole packs her beach bucket and sun hat for her very first railway journey.", "Nicole prepara su sombrero para su primer viaje en tren.", "نکول اپنے پہلے ریل کے سفر کے لیے اپنی ساحلی بالٹی اور دھوپ والی ٹوپی تیار کرتی ہے۔", "निकोल अपनी पहली रेल यात्रा के लिए अपनी टोपी और बैग तैयार करती है।", setOf("beach", "train")),
                StoryPage(2, "books/sw_44485/image_3.webp", "On the station platform, Mom shows Nicole how to stand safely behind the yellow line.", "En el andén, mamá le enseña a esperar detrás de la línea amarilla.", "اسٹیشن کے پلیٹ فارم پر امی نکول کو پیلی حفاظتی لکیر کے پیچھے کھڑا ہونا سکھاتی ہیں۔", "प्लेटफ़ॉर्म पर माँ निकोल को पीली रेखा के पीछे सुरक्षित खड़ा होना सिखाती हैं।", setOf("mother", "yellow", "train")),
                StoryPage(3, "books/sw_44485/image_4.webp", "When the silver train glides to a full stop, the doors open and everyone boards in a neat line!", "Cuando el tren se detiene, todos suben en fila.", "جب چمکدار ٹرین پوری طرح رک جاتی ہے تو دروازے کھلتے ہیں اور سب قطار بنا کر سوار ہوتے ہیں!", "जब ट्रेन रुकती है, तो दरवाज़े खुलते हैं और सब कतार में चढ़ते हैं!", setOf("train", "friends"),
                    StoryQuizQuestion("Where does Mom teach Nicole to stand safely on the train platform?", listOf("Behind the yellow safety line", "On top of a bench", "Inside the ticket booth"), 0)
                )
            )
        ),

        // 62. Level 2 • School & Kindness: "Practice Makes Us Strong"
        StoryBook(
            id = "book_practice_makes_strong",
            title = "Practice Makes Us Strong",
            nativeTitleUrdu = "مشق ہمیں مضبوط بناتی ہے",
            nativeTitleHindi = "अभ्यास हमें मज़बूत बनाता है",
            author = "Mabel Mnensa",
            illustrator = "Ntembiso Velemane",
            publisher = "Book Dash • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_44485/image_7.webp",
            categoryTag = "School & Kindness",
            pages = listOf(
                StoryPage(1, "books/sw_44485/image_7.webp", "The two karate brothers wake up early every Saturday to practice their stances.", "Los dos hermanos practican karate cada sábado.", "کراٹے کھیلنے والے دونوں بھائی ہر ہفتے کی صبح جلدی اٹھ کر اپنی مشق کرتے ہیں۔", "दोनों भाई हर शनिवार सुबह जल्दी उठकर कराटे का अभ्यास करते हैं।", setOf("brother", "early", "morning")),
                StoryPage(2, "books/sw_44485/image_8.webp", "Their coach teaches them that true strength comes from patience, respect, and focus.", "Su entrenador les enseña paciencia y respeto.", "ان کے استاد انہیں سکھاتے ہیں کہ اصل طاقت صبر، احترام اور توجہ سے آتی ہے۔", "उनके शिक्षक सिखाते हैं कि असली ताकत धैर्य, सम्मान और ध्यान से आती है।", setOf("teacher", "special")),
                StoryPage(3, "books/sw_44485/image_9.webp", "With a crisp bow and a high leap, they earn applause from every passenger on the train!", "Con un salto alto ganan aplausos de todos.", "باادب سلام اور اونچی چھلانگ کے ساتھ وہ ٹرین کے تمام مسافروں سے داد وصول کرتے ہیں!", "विनम्र प्रणाम और ऊँची छलांग के साथ वे सभी यात्रियों से तालियाँ पाते हैं!", setOf("train", "friends"),
                    StoryQuizQuestion("According to their coach, where does true karate strength come from?", listOf("Patience, respect, and focus", "Eating lots of candy", "Shouting loudly"), 0)
                )
            )
        ),

        // 63. Level 2 • Science & Weather: "When the Wind Blows"
        StoryBook(
            id = "book_when_wind_blows",
            title = "When the Wind Blows",
            nativeTitleUrdu = "جب تیز ہوا چلتی ہے",
            nativeTitleHindi = "जब तेज़ हवा चलती है",
            author = "Clyde Beech",
            illustrator = "Mohale Mashigo",
            publisher = "Book Dash • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_45764/image_3.webp",
            categoryTag = "Science & Weather",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: bl • w • st",
            pages = listOf(
                StoryPage(1, "books/sw_45764/image_2.webp", "A sudden gust of evening wind blows through the window and flickers the candles.", "Una ráfaga de viento entra por la ventana.", "شام کی ہوا کا ایک تیز جھونکا کھڑکی سے اندر آتا ہے اور موم بتیوں کی لو لرز اٹھتی ہے۔", "शाम की हवा का एक तेज़ झोंका खिड़کی से अंदर आता है।", setOf("blast", "candles")),
                StoryPage(2, "books/sw_45764/image_3.webp", "Outside, tall green palm and mango trees sway as dark monsoon clouds roll across the hills.", "Afuera los árboles se mecen bajo las nubes.", "باہر آم کے اونچے ہرے درخت جھومتے ہیں جب پہاڑیوں پر بارش کے گہرے بادل چھا جاتے ہیں۔", "बाहर आम के ऊँचे पेड़ झूमते हैं जब पहाड़ियों पर बारिश के बादल छा जाते हैं।", setOf("trees", "clouds", "green")),
                StoryPage(3, "books/sw_45764/image_4.webp", "Lulu closes the latch and watches the cool rain wash the garden clean!", "Lulu cierra la ventana y mira la lluvia fresca.", "لولو کھڑکی بند کرتی ہے اور ٹھنڈی بارش کو باغ کے پودوں کو دھوتے ہوئے دیکھتی ہے!", "लुलु खिड़की बंद करती है और ठंडी बारिश को बग़ीचे को भिगोते हुए देखती है!", setOf("rain", "garden"),
                    StoryQuizQuestion("What rolls across the hills when the strong evening wind blows?", listOf("Dark monsoon rain clouds", "A yellow bicycle", "A stack of library books"), 0)
                )
            )
        ),

        // 64. Level 2 • Family & Friends: "Teacups in the Cupboard"
        StoryBook(
            id = "book_teacups_in_cupboard",
            title = "Teacups in the Cupboard",
            nativeTitleUrdu = "لکڑی کی الماری میں چائے کی پیالیاں",
            nativeTitleHindi = "लकड़ी की अलमारी में चाय के कप",
            author = "Mohale Mashigo",
            illustrator = "Clyde Beech",
            publisher = "Book Dash • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_45764/image_5.webp",
            categoryTag = "Family & Friends",
            pages = listOf(
                StoryPage(1, "books/sw_45764/image_5.webp", "In Ma's cozy kitchen, painted china teacups sit in neat rows inside the wooden cupboard.", "En la cocina las tazas están ordenadas en el armario.", "امی کے آرام دہ باورچی خانے میں لکڑی کی الماری کے اندر رنگین پیالیاں قطار میں سجائی گئی ہیں۔", "माँ की रसोई में लकड़ी की अलमारी के अंदर सुंदर कप कतार में रखे हैं।", setOf("cupboard", "painted")),
                StoryPage(2, "books/sw_45764/image_3.webp", "When thunder rumbles outside, the little saucers go clink-clink-clink on the shelf!", "Cuando truena afuera, los platillos tintinean.", "جب باہر بادل گرجتے ہیں تو الماری کے خانے میں رکھی طشتریاں کھنک اٹھتی ہیں!", "जब बाहर बादल गरजते हैं, तो अलमारी में रखे कप खनखना उठते हैं!", setOf("thunder", "cupboard")),
                StoryPage(3, "books/sw_45764/image_14.webp", "Ma pours warm cinnamon tea into the cups while Lulu reads a story on the rug!", "Mamá sirve té caliente mientras Lulu lee en la alfombra.", "امی پیالیوں میں گرم چائے انڈیلتی ہیں جبکہ لولو قالین پر بیٹھ کر کہانی پڑھتی ہے!", "माँ कप में गर्म चाय डालती हैं और लुलु कालीन पर बैठकर कहानी पढ़ती है!", setOf("mother", "read", "story"),
                    StoryQuizQuestion("Where are the painted teacups kept in Ma's kitchen?", listOf("Inside the wooden cupboard", "Under the garden tree", "On the train seat"), 0)
                )
            )
        ),

        // 65. Level 2 • Numbers & Rhymes: "Ten Shiny Silver Fish"
        StoryBook(
            id = "book_ten_silver_fish",
            title = "Ten Shiny Silver Fish",
            nativeTitleUrdu = "دس چمکدار چاندی جیسی مچھلیاں",
            nativeTitleHindi = "दस चमकीली चाँदी जैसी मछलियाँ",
            author = "Clyde Beech",
            illustrator = "Mohale Mashigo",
            publisher = "Book Dash • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_45764/image_9.webp",
            categoryTag = "Numbers & Rhymes",
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_45764/image_9.webp", "One, two, three, four, five shiny silver fish splash in the garden rain puddle!", "Cinco peces plateados chapotean en el charco.", "ایک، دو، تین، چار، پانچ چمکدار مچھلیاں باغ کے بارش کے پانی میں چھپاکے مار رہی ہیں!", "एक, दो, तीन, चार, पाँच चमकीली मछलियाँ बारिश के पानी में उछल रही हैं!", setOf("fish", "rain", "garden")),
                StoryPage(2, "books/sw_45764/image_11.webp", "Six, seven, eight, nine, ten! Lulu and her friends bring buckets of clean rainwater.", "Lulu y sus amigos traen cubetas con agua de lluvia.", "چھ، سات، آٹھ، نو، دس! لولو اور اس کے دوست صاف پانی کی بالٹیاں لے کر دوڑتے ہوئے آتے ہیں۔", "छह, सात, आठ, नौ, दस! लुलु और उसके दोस्त साफ़ पानी की बाल्टियाँ लाते हैं।", setOf("friends", "water")),
                StoryPage(3, "books/sw_45764/image_13.webp", "All ten little fish swim happily back into the flowing river together!", "Los diez pececitos nadan felices de regreso al río.", "دس کی دس ننھی مچھلیاں خوشی سے تیرتی ہوئی واپس بہتے دریا میں چلی جاتی ہیں!", "दसों नन्ही मछलियाँ खुशी से तैरती हुईं वापस नदी में चली जाती हैं!", setOf("fish", "water", "happy"),
                    StoryQuizQuestion("How many shiny silver fish did the children count and rescue?", listOf("Ten shiny silver fish", "One hundred frogs", "Two yellow birds"), 0)
                )
            )
        ),

        // 66. Level 2 • School & Kindness: "Teamwork in the Rain"
        StoryBook(
            id = "book_teamwork_in_rain",
            title = "Teamwork in the Rain",
            nativeTitleUrdu = "بارش میں مل جل کر مدد کرنا",
            nativeTitleHindi = "बारिश में मिलकर मदद करना",
            author = "Clyde Beech",
            illustrator = "Clyde Beech",
            publisher = "Book Dash • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_45764/image_11.webp",
            categoryTag = "School & Kindness",
            pages = listOf(
                StoryPage(1, "books/sw_45764/image_10.webp", "When Lulu saw the little fish stranded on the lawn, she knew she could not save them all alone.", "Lulu sabía que necesitaba ayuda para salvar a los peces.", "جب لولو نے گھاس پر ننھی مچھلیوں کو دیکھا تو وہ سمجھ گئی کہ اکیلے سب کو بچانا مشکل ہے۔", "जब लुलु ने घास पर मछलियों को देखा, तो उसने अपने दोस्तों को मदद के लिए बुलाया।", setOf("fish", "raincoat")),
                StoryPage(2, "books/sw_45764/image_11.webp", "She called her neighborhood friends, and soon four children were working side by side with bowls and pails.", "Llamó a sus amigos del barrio para trabajar en equipo.", "اس نے محلے کے دوستوں کو بلایا اور جلد ہی چاروں بچے پیالے اور بالٹیاں لے کر مل کر کام کرنے لگے۔", "मोहल्ले के चारों बच्चे कटोरे और बाल्टियाँ लेकर एक साथ जुट गए।", setOf("friends", "water")),
                StoryPage(3, "books/sw_45764/image_12.webp", "With teamwork and gentle hands, they finished the rescue in half the time!", "Con trabajo en equipo terminaron el rescate muy rápido.", "اتفاق اور مل جل کر کام کرنے کی بدولت انہوں نے آدھے وقت میں تمام مچھلیوں کو بچا لیا!", "टीमवर्क और सहयोग से उन्होंने बहुत जल्दी सभी मछलियों को बचा लिया!", setOf("friends", "happy"),
                    StoryQuizQuestion("How did Lulu and her friends finish rescuing the fish so quickly?", listOf("By working together as a team", "By waiting until next week", "By calling a train"), 0)
                )
            )
        ),

        // 67. Level 2 • Bedtime & Folktales: "Butterfly Dreams at Dusk"
        StoryBook(
            id = "book_butterfly_dreams_dusk",
            title = "Butterfly Dreams at Dusk",
            nativeTitleUrdu = "شام کے وقت تتلی کے خواب",
            nativeTitleHindi = "शाम के समय तितली के सपने",
            author = "Somsanouk Bouttavong",
            illustrator = "Ounla Santi",
            publisher = "Room to Read • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_37273/image_11.webp",
            categoryTag = "Bedtime & Folktales",
            pages = listOf(
                StoryPage(1, "books/sw_37273/image_11.webp", "As the pink evening sky cools over the garden, little Caterpillar curls up on a soft green leaf.", "Al atardecer la pequeña oruga se acurruca en una hoja verde.", "جیسے ہی باغ کے اوپر شام کا گلابی آسمان ٹھنڈا ہوتا ہے، ننھی کیٹرپلر ایک نرم ہرے پتے پر سمٹ جاتی ہے۔", "जैसे ही शाम का आसमान ठंडा होता है, नन्ही कैटरपिलर एक नरम हरे पत्ते पर लेट जाती है।", setOf("caterpillar", "green", "leaves")),
                StoryPage(2, "books/sw_37273/image_12.webp", "The night breeze sings a gentle lullaby through the swaying branches.", "La brisa nocturna canta una suave canción de cuna.", "رات کی ٹھنڈی ہوا جھومتی ہوئی شاخوں میں ایک میٹھی لوری سناتی ہے۔", "रात की ठंडी हवा झूमती शाखाओं में एक मीठी लोरी सुनाती है।", setOf("trees", "whispers")),
                StoryPage(3, "books/sw_37273/image_14.webp", "Soon she will wake with rainbow wings and dance among the morning blossoms!", "Pronto despertará con alas de mariposa.", "جلد ہی وہ قوسِ قزح جیسے رنگین پروں کے ساتھ جاگے گی اور صبح کے پھولوں میں اڑے گی!", "जल्द ही वह रंग-बिरंगे पंखों के साथ जागेगी और फूलों पर उड़ेगी!", setOf("butterfly", "wings", "flowers"),
                    StoryQuizQuestion("Where does little Caterpillar curl up to rest at dusk?", listOf("On a soft green leaf", "Inside a teacup", "On a bicycle seat"), 0)
                )
            )
        ),

        // 68. Level 2 • Numbers & Rhymes: "Colors of Our Neighborhood"
        StoryBook(
            id = "book_colors_of_neighborhood",
            title = "Colors of Our Neighborhood",
            nativeTitleUrdu = "ہمارے محلے کے قوسِ قزح جیسے رنگ",
            nativeTitleHindi = "हमारे मोहल्ले के सुंदर रंग",
            author = "Mabel Mnensa",
            illustrator = "Ntembiso Velemane",
            publisher = "Book Dash • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_44485/cover_image.webp",
            categoryTag = "Numbers & Rhymes",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: bl • gr • yl",
            pages = listOf(
                StoryPage(1, "books/sw_44485/cover_image.webp", "How many colors can you spot when we walk down our cheerful neighborhood street?", "¿Cuántos colores puedes ver en nuestra calle?", "جب ہم اپنے خوشگوار محلے کی گلی میں چلتے ہیں تو آپ کتنے رنگ تلاش کر سکتے ہیں؟", "जब हम अपने मोहल्ले की गली में चलते हैं, तो आप कितने रंग देख सकते हैं?", setOf("colors", "street", "walk")),
                StoryPage(2, "books/sw_44485/image_6.webp", "There is golden yellow on the chocolate wrappers and ruby red on the fruit cart!", "Hay amarillo dorado en los dulces y rojo en las frutas.", "چاکلیٹ کے سنہری پیلے ریپرز ہیں اور پھلوں کے ٹھیلے پر یاقوت جیسا سرخ رنگ چمک رہا ہے!", "चॉकलेट पर सुनहरा पीला रंग है और फलों की दुकान पर चमकीला लाल रंग है!", setOf("golden", "yellow", "red")),
                StoryPage(3, "books/sw_44485/image_11.webp", "And the street drummers wear crimson and gold as they play under the bright blue sky!", "Los bateristas visten de rojo y oro bajo el cielo azul.", "اور گلی کے ڈھول بجانے والے چمکدار نیلے آسمان کے نیچے سرخ اور سنہری لباس پہن کر ڈھول بجاتے ہیں!", "और ढोल वादक नीले आसमान के नीचे लाल और सुनहरे कपड़े पहनकर ढोल बजाते हैं!", setOf("drummers", "blue", "sky"),
                    StoryQuizQuestion("What colors do the street drummers wear under the blue sky?", listOf("Bright red and golden yellow", "All grey", "Dark brown"), 0)
                )
            )
        ),

        // 69. Level 3 • Animals & Nature: "Bibi Butterfly & Manto Mantis"
        StoryBook(
            id = "book_bibi_and_manto",
            title = "Bibi Butterfly & Manto Mantis",
            nativeTitleUrdu = "بی بی تتلی اور مینٹو مینٹس کی صبح",
            nativeTitleHindi = "बीबी तितली और मैंटो की सुबह",
            author = "Catherine Holtzhausen",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • StoryWeaver",
            level = 3,
            coverAssetPath = "books/sw_13916/image_5.webp",
            categoryTag = "Animals & Nature",
            bookType = "SERIES",
            seriesTag = "Garden Series • Ep 6",
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_5.webp", "Every morning at sunrise, Bibi Butterfly spreads her patterned wings while Manto Mantis adjusts his leaf-green coat.", "Cada mañana Bibi Mariposa y Manto Mantis se saludan.", "ہر صبح طلوعِ آفتاب کے وقت بی بی تتلی اپنے نقش و نگار والے پر پھیلاتی ہے اور مینٹو مینٹس اپنا ہرا کوٹ درست کرتا ہے۔", "हर सुबह बीबी तितली अपने सुंदर पंख फैलाती है और मैंटो हाथ हिलाता है।", setOf("butterfly", "wings", "morning")),
                StoryPage(2, "books/sw_13916/image_4.webp", "Together they patrol the flowerbeds to make sure every young beetle and ladybird reaches school safely.", "Juntos cuidan el camino hacia la escuela del jardín.", "وہ دونوں مل کر پھولوں کی کیاریوں کی نگرانی کرتے ہیں تاکہ ہر ننھا کیڑا اور لیڈی برڈ بحفاظت اسکول پہنچے۔", "वे दोनों मिलकर फूलों के रास्ते की देखभाल करते हैं ताकि सभी बच्चे सुरक्षित स्कूल पहुँचें।", setOf("ladybird", "school", "flowers")),
                StoryPage(3, "books/sw_13916/image_7.webp", "When the morning bell chimes, they wave from the marigold arch and wish the students a wonderful day of learning!", "Saludan desde el arco de flores deseando un gran día.", "جب صبح کی گھنٹی بجتی ہے تو وہ گیندے کے پھولوں کی محراب سے ہاتھ ہلا کر بچوں کو علم بھرا دن مبارک کہتے ہیں!", "जब घंटी बजती है, तो वे गेंदे के फूलों के पास से बच्चों को शुभकामनाएँ देते हैं!", setOf("marigolds", "school", "friends"),
                    StoryQuizQuestion("How do Bibi Butterfly and Manto Mantis help the garden bugs every morning?", listOf("They make sure everyone reaches school safely", "They paint everyone's shoes", "They drive the coastal train"), 0)
                )
            )
        ),

        // 70. Level 3 • School & Kindness: "Why Being Yourself Matters"
        StoryBook(
            id = "book_being_yourself_matters",
            title = "Why Being Yourself Matters",
            nativeTitleUrdu = "اپنی اصل پہچان پر فخر کرو",
            nativeTitleHindi = "अपनी असली पहचान पर गर्व करो",
            author = "Nadene Kriel",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • StoryWeaver",
            level = 3,
            coverAssetPath = "books/sw_13916/image_14.webp",
            categoryTag = "School & Kindness",
            bookSource = "CLASSROOM",
            assignedByTeacher = "Miss Miya • Grade 3",
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_13.webp", "In Miss Miya's circle time, the students talked about what makes each friend one of a kind.", "En la clase hablaron sobre lo que hace único a cada amigo.", "مس میا کی کلاس میں طلبہ نے اس بات پر گفتگو کی کہ ہر دوست کو کون سی خوبی منفرد بناتی ہے۔", "मिस मिया की कक्षा में बच्चों ने बात की कि हर दोस्त को कौन सी खूबी अनोखी बनाती है।", setOf("teacher", "unique", "friends")),
                StoryPage(2, "books/sw_13916/image_14.webp", "Sipho showed the silver spot on his back, Sally showed her long jumping legs, and Lara showed her golden wings.", "Sipho mostró su lunar, Sally sus piernas largas y Lara sus alas.", "سیفو نے اپنی پیٹھ کا چمکدار نشان دکھایا، سیلی نے اپنی لمبی ٹانگیں اور لارا نے اپنے سنہری پر دکھائے۔", "सीफो ने अपना चमकीला निशान दिखाया, सैली ने लंबी टाँगें और लारा ने सुनहरे पंख दिखाए।", setOf("golden", "wings", "special")),
                StoryPage(3, "books/sw_13916/image_18.webp", "\"When we celebrate our differences, our whole classroom shines brighter than a rainbow!\" said Miss Miya.", "Cuando celebramos nuestras diferencias, toda la clase brilla.", "جب ہم ایک دوسرے کی انفرادیت کی قدر کرتے ہیں تو ہماری پوری کلاس قوسِ قزح سے زیادہ روشن ہو جاتی ہے! مس میا نے کہا۔", "\"जब हम अपनी अनोखी खूबियों का सम्मान करते हैं, तो पूरी कक्षा चमक उठती है!\" मिस मिया ने कहा।", setOf("classroom", "bright", "teacher"),
                    StoryQuizQuestion("What happens when the classroom celebrates everyone's unique differences?", listOf("The whole classroom shines brighter together", "Everyone becomes invisible", "School closes early"), 0)
                )
            )
        ),

        // 71. Level 3 • Bedtime & Folktales: "Lara's Leaf Mirror"
        StoryBook(
            id = "book_lara_leaf_mirror",
            title = "Lara's Leaf Mirror",
            nativeTitleUrdu = "لارا کا چمکتا ہوا آئینہ",
            nativeTitleHindi = "लारा का चमकता आईना",
            author = "Catherine Holtzhausen",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • StoryWeaver",
            level = 3,
            coverAssetPath = "books/sw_13916/image_17.webp",
            categoryTag = "Bedtime & Folktales",
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_16.webp", "Warm soap bubbles floated in the air as Lara washed away the last streak of red paint.", "Las burbujas flotaban mientras Lara lavaba la pintura roja.", "گرم صابن کے بلبلے ہوا میں اڑ رہے تھے جب لارا نے اپنے پروں سے سرخ رنگ کا آخری نشان بھی دھو ڈالا۔", "गर्म साबुन के बुलबुले हवा में उड़ रहे थे जब लारा ने लाल रंग धो डाला।", setOf("scrubbed", "red", "water")),
                StoryPage(2, "books/sw_13916/image_17.webp", "She wrapped herself in a fluffy towel and looked into the dewy leaf mirror on her wall.", "Se envolvió en una toalla suave y se miró en el espejo.", "اس نے اپنے گرد ایک نرم تولیہ لپیٹا اور دیوار پر لگے شبنمی پتے کے آئینے میں دیکھا۔", "उसने नरम तौलिया लपेटा और पत्ते के आईने में देखा।", setOf("leaves", "bright")),
                StoryPage(3, "books/sw_13916/image_18.webp", "Her natural golden-yellow wings gleamed like sunshine, and Lara smiled with pure joy!", "Sus alas amarillas brillaban como el sol y Lara sonrió.", "اس کے قدرتی سنہری پیلے پر دھوپ کی طرح جگمگا رہے تھے، اور لارا خوشی سے مسکرا اٹھی!", "उसके सुनहरे पीले पंख धूप की तरह चमक रहे थे और लारा मुस्कुरा उठी!", setOf("golden", "yellow", "gleamed"),
                    StoryQuizQuestion("What did Lara see when she looked into her leaf mirror after her bath?", listOf("Her natural golden-yellow wings gleaming like sunshine", "Dark grey clouds", "A pair of frog boots"), 0)
                )
            )
        ),

        // 72. Level 3 • School & Kindness: "Splash of Purple for Fun"
        StoryBook(
            id = "book_splash_purple_fun",
            title = "Splash of Purple for Fun",
            nativeTitleUrdu = "خوشی کے رنگ اور باغ کا میلہ",
            nativeTitleHindi = "खुशी के रंग और बग़ीचे का मेला",
            author = "Nadene Kriel",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • StoryWeaver",
            level = 3,
            coverAssetPath = "books/sw_13916/image_18.webp",
            categoryTag = "School & Kindness",
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_18.webp", "On Friday afternoon, Bug School held a joyful Face-Painting and Art Festival in the meadow.", "El viernes la escuela celebró un festival de arte en el jardín.", "جمعہ کی سہ پہر بگز اسکول نے سبزہ زار میں رنگوں اور مصوری کا ایک خوشگوار میلہ سجایا۔", "शुक्रवार दोपहर स्कूल ने बग़ीचे में रंगों और कला का उत्सव मनाया।", setOf("school", "painted", "garden")),
                StoryPage(2, "books/sw_13916/image_5.webp", "Lara added a tiny swirl of purple paint on her cheek just for fun, while keeping her yellow wings bright!", "Lara probó un toque de púrpura solo por diversión.", "لارا نے صرف تفریح کے لیے اپنے گال پر جامنی رنگ کا ایک چھوٹا سا نقش بنایا جبکہ اس کے پیلے پر چمکتے رہے!", "लारा ने मज़े के लिए थोड़ा बैंगनी रंग लगाया जबकि उसके पीले पंख चमकते रहे!", setOf("yellow", "wings", "bright")),
                StoryPage(3, "books/sw_13916/image_14.webp", "All her classmates painted colorful patterns on leaf canvases and celebrated together!", "Todos pintaron diseños coloridos y celebraron juntos.", "اس کے تمام ہم جماعتوں نے پتوں کے کینوس پر رنگا رنگ نقش بنائے اور مل کر جشن منایا!", "सभी सहपाठियों ने पत्तों पर सुंदर चित्र बनाए और मिलकर जश्न मनाया!", setOf("friends", "colors", "happy"),
                    StoryQuizQuestion("Why did Lara try a tiny swirl of purple paint at the festival?", listOf("Just for fun, while keeping her yellow wings proud", "To hide from her friends forever", "Because it was raining"), 0)
                )
            )
        ),

        // 73. Level 3 • Family & Friends: "Afternoon on the Sunny Veranda"
        StoryBook(
            id = "book_sunny_veranda_reading",
            title = "Afternoon on the Sunny Veranda",
            nativeTitleUrdu = "دھوپ والے برآمدے میں کہانی کا وقت",
            nativeTitleHindi = "धूप वाले बरामदे में कहानी का समय",
            author = "Letta Machoga",
            illustrator = "Wiehan de Jager",
            publisher = "African Storybook • StoryWeaver",
            level = 3,
            coverAssetPath = "books/sw_15058/image_9.webp",
            categoryTag = "Family & Friends",
            pages = listOf(
                StoryPage(1, "books/sw_15058/image_7.webp", "When the afternoon breeze cools the courtyard, I spread a woven mat on the sunny veranda.", "Por la tarde extiendo un tapete en la terraza soleada.", "جب سہ پہر کی ہوا صحن کو ٹھنڈا کرتی ہے تو میں دھوپ والے برآمدے میں چٹائی بچھاتا ہوں۔", "दोपहर बाद जब आँगन में ठंडी हवा चलती है, तो मैं बरामदे में चटाई बिछाता हूँ।", setOf("sunlight", "garden")),
                StoryPage(2, "books/sw_15058/image_9.webp", "I open my chapter book and read aloud in a clear, expressive voice.", "Abro mi libro y leo en voz alta con claridad.", "میں اپنی کتاب کھولتا ہوں اور صاف، خوبصورت آواز میں بلند خوانی کرتا ہوں۔", "मैं अपनी किताब खोलता हूँ और साफ़ आवाज़ में कहानी पढ़ता हूँ।", setOf("read", "books", "story")),
                StoryPage(3, "books/sw_15058/image_8.webp", "Soon Grandfather Farouk, Mother, and Father gather around the veranda steps to applaud my reading!", "Pronto toda la familia se reúne para escucharme y aplaudir.", "جلد ہی دادا فاروق، امی اور ابو برآمدے کی سیڑھیوں کے پاس جمع ہو کر میری پڑھائی کی داد دیتے ہیں!", "जल्द ही दादाजी, माँ और पिताजी पास आकर मेरी पढ़ाई की सराहना करते हैं!", setOf("grandfather", "mother", "father"),
                    StoryQuizQuestion("How does the boy practice his reading on the woven veranda mat?", listOf("By reading aloud in a clear, expressive voice", "By tearing the pages", "By sleeping on the book"), 0)
                )
            )
        ),

        // 74. Level 3 • Science & Weather: "Higher Than the Tallest Tree"
        StoryBook(
            id = "book_higher_than_tallest_tree",
            title = "Higher Than the Tallest Tree",
            nativeTitleUrdu = "سب سے اونچے درخت سے بھی بلند",
            nativeTitleHindi = "सबसे ऊँचे पेड़ से भी ऊपर",
            author = "Marion Drew",
            illustrator = "Marion Drew",
            publisher = "Pratham Books • StoryWeaver",
            level = 3,
            coverAssetPath = "books/sw_15195/image_14.webp",
            categoryTag = "Science & Weather",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: tr • br • cl",
            pages = listOf(
                StoryPage(1, "books/sw_15195/image_13.webp", "Have you ever wondered how high a bird or a balloon can climb above the earth?", "¿Te has preguntado qué tan alto puede subir un globo?", "کیا آپ نے کبھی سوچا ہے کہ ایک پرندہ یا غبارہ زمین سے کتنا اونچا اڑ سکتا ہے؟", "क्या आपने कभी सोचा है कि कोई पक्षी या गुब्बारा कितना ऊँचा उड़ सकता है?", setOf("sky", "flying")),
                StoryPage(2, "books/sw_15195/image_14.webp", "First it passes the wooden fence, then the house rooftop, and then the crown of the tallest green tree!", "Primero pasa la cerca, luego el techo y la copa del árbol.", "پہلے وہ لکڑی کی باڑ سے اوپر اٹھتا ہے، پھر گھر کی چھت اور پھر سب سے اونچے ہرے درخت کی چوٹی سے بھی اوپر!", "पहले वह छत और फिर सबसे ऊँचे हरे पेड़ की चोटी से भी ऊपर निकल जाता है!", setOf("tree", "green")),
                StoryPage(3, "books/sw_15195/image_16.webp", "High up in the atmosphere, cool winds carry fluffy white cumulus clouds across the blue sky!", "En lo alto, los vientos llevan nubes blancas por el cielo azul.", "فضا کی بلندیوں میں ٹھنڈی ہوائیں نرم سفید بادلوں کو نیلے آسمان میں اڑا کر لے جاتی ہیں!", "आसमान की ऊँचाई पर ठंडी हवाएँ सफ़ेद बादलों को उड़ाकर ले जाती हैं!", setOf("clouds", "blue", "sky"),
                    StoryQuizQuestion("What floats high up in the blue atmosphere above the tallest trees?", listOf("Fluffy white cumulus clouds", "Wooden cupboards", "Sand burrows"), 0)
                )
            )
        ),

        // 75. Level 3 • Science & Weather: "The Chrysalis Miracle"
        StoryBook(
            id = "book_chrysalis_miracle",
            title = "The Chrysalis Miracle",
            nativeTitleUrdu = "کوکون سے تتلی بننے کا معجزہ",
            nativeTitleHindi = "कोकून से तितली बनने का चमत्कार",
            author = "Somsanouk Bouttavong",
            illustrator = "Ounla Santi",
            publisher = "Room to Read • Science Series",
            level = 3,
            coverAssetPath = "books/sw_37273/image_13.webp",
            categoryTag = "Science & Weather",
            pages = listOf(
                StoryPage(1, "books/sw_37273/image_12.webp", "Hanging safely beneath a sturdy branch, the emerald cocoon protects the sleeping caterpillar inside.", "El capullo verde protege a la oruga que duerme adentro.", "مضبوط شاخ کے نیچے لٹکا ہوا ہرا کوکون اپنے اندر سوتی ہوئی کیٹرپلر کو محفوظ رکھتا ہے۔", "शाखा के नीचे लटका हरा कोकून अंदर सोती हुई कैटरपिलर को सुरक्षित रखता है।", setOf("cocoon", "caterpillar", "green")),
                StoryPage(2, "books/sw_37273/image_13.webp", "After two quiet weeks of sunshine and gentle rain, the silky shell begins to open at dawn.", "Después de dos semanas, el capullo comienza a abrirse al amanecer.", "دو ہفتوں کی دھوپ اور ہلکی بارش کے بعد صبح سویرے ریشمی خول آہستہ آہستہ کھلنے لگتا ہے۔", "दो हफ़्तों के बाद सुबह सवेरे रेशमी कोकून धीरे-धीरे खुलने लगता है।", setOf("sunlight", "rain", "early")),
                StoryPage(3, "books/sw_37273/image_14.webp", "Out steps a radiant butterfly, drying her colorful wings in the morning sun before her first flight!", "Sale una hermosa mariposa secando sus alas al sol.", "باہر ایک خوبصورت تتلی نکلتی ہے جو اپنی پہلی پرواز سے پہلے صبح کی دھوپ میں اپنے رنگین پر سکھاتی ہے!", "बाहर एक सुंदर तितली निकलती है और अपनी पहली उड़ान के लिए पंख फैलाती है!", setOf("butterfly", "wings", "sunlight"),
                    StoryQuizQuestion("How long does the caterpillar rest inside the protective cocoon before emerging?", listOf("About two quiet weeks", "Five minutes", "Ten years"), 0)
                )
            )
        ),

        // 76. Level 3 • Adventure & Travel: "Sunset Lanterns on the Coast"
        StoryBook(
            id = "book_sunset_lanterns_coast",
            title = "Sunset Lanterns on the Coast",
            nativeTitleUrdu = "ساحلی اسٹیشن کی شام اور روشن لالٹینیں",
            nativeTitleHindi = "समुद्री स्टेशन की शाम और लालटेन",
            author = "Mabel Mnensa",
            illustrator = "Ntembiso Velemane",
            publisher = "Book Dash • StoryWeaver",
            level = 3,
            coverAssetPath = "books/sw_44485/image_13.webp",
            categoryTag = "Adventure & Travel",
            bookType = "SERIES",
            seriesTag = "Coastline Train • Ep 5",
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_44485/image_13.webp", "As the coastal train pulls into the harbor station at dusk, golden paper lanterns glow along the platform.", "Al atardecer las linternas doradas brillan en la estación.", "جیسے ہی شام کے وقت ساحلی ٹرین بندرگاہ کے اسٹیشن پر رکتی ہے، پلیٹ فارم پر سنہری لالٹینیں جگمگا اٹھتی ہیں۔", "शाम के समय जब ट्रेन स्टेशन पर रुकती है, तो प्लेटफ़ॉर्म पर सुनहरी लालटेन जगमगा उठती हैं।", setOf("train", "golden", "bright")),
                StoryPage(2, "books/sw_44485/image_15.webp", "The ocean breeze carries the scent of sea salt, roasted corn, and blooming jasmine.", "La brisa marina trae aroma de mar y flores.", "سمندری ہوا اپنے ساتھ ساحل کی ٹھنڈک اور کھلتے ہوئے پھولوں کی خوشبو لاتی ہے۔", "समुद्री हवा अपने साथ तट की ताज़गी और फूलों की खुशबू लाती है।", setOf("ocean", "flowers", "beach")),
                StoryPage(3, "books/sw_44485/image_16.webp", "Nicole holds Mom's hand as they walk out toward the sandy boardwalk under a sky of amber and rose!", "Nicole toma la mano de mamá hacia el paseo marítimo.", "نکول امی کا ہاتھ پکڑ کر سنہری اور گلابی آسمان کے نیچے ساحل کی سیرگاہ کی طرف چل پڑتی ہے!", "निकोल माँ का हाथ पकड़कर सुंदर आसमान के नीचे समुद्र तट की ओर चलती है!", setOf("mother", "beach", "sky"),
                    StoryQuizQuestion("What glows along the harbor station platform as the coastal train arrives at dusk?", listOf("Golden paper lanterns", "Snowmen", "Campfires"), 0)
                )
            )
        ),

        // 77. Level 3 • Adventure & Travel: "Mango Ice Cream by the Waves"
        StoryBook(
            id = "book_mango_ice_cream_waves",
            title = "Mango Ice Cream by the Waves",
            nativeTitleUrdu = "سمندر کی لہروں کے پاس آم کی آئس کریم",
            nativeTitleHindi = "समुद्र की लहरों के पास आम की आइसक्रीम",
            author = "Mabel Mnensa",
            illustrator = "Ntembiso Velemane",
            publisher = "Book Dash • StoryWeaver",
            level = 3,
            coverAssetPath = "books/sw_44485/image_16.webp",
            categoryTag = "Adventure & Travel",
            pages = listOf(
                StoryPage(1, "books/sw_44485/image_6.webp", "On the train ride, Mom promised Nicole a special beach treat if she saved space in her tummy.", "En el tren mamá le prometió un helado especial en la playa.", "ٹرین کے سفر میں امی نے نکول سے وعدہ کیا تھا کہ ساحل پر پہنچ کر اسے مزیدار آئس کریم ملے گی۔", "ट्रेन के सफ़र में माँ ने निकोल से समुद्र तट पर आइसक्रीम दिलाने का वादा किया था।", setOf("train", "mother", "beach")),
                StoryPage(2, "books/sw_44485/image_16.webp", "At the seaside kiosk, they ordered two cones of golden mango ice cream topped with fruit slices!", "Pidieron dos conos de helado de mango dorado.", "ساحل کے اسٹال سے انہوں نے تازہ پھلوں سے سجی سنہری آم کی دو آئس کریم کونز لیں!", "समुद्र तट की दुकान से उन्होंने सुनहरे आम की दो स्वादिष्ट आइसक्रीम लीं!", setOf("golden", "yellow", "happy")),
                StoryPage(3, "books/sw_44485/image_14.webp", "Watching the blue ocean waves roll onto the sand made the cold sweet ice cream taste even better!", "Mirar las olas azules hizo que el helado supiera aún mejor.", "ریت پر آتی نیلے سمندر کی لہروں کو دیکھتے ہوئے ٹھنڈی میٹھی آئس کریم کا مزہ دوبالا ہو گیا!", "नीले समुद्र की लहरों को देखते हुए आइसक्रीम खाने का मज़ा दोगुना हो गया!", setOf("blue", "ocean", "beach"),
                    StoryQuizQuestion("What flavor of ice cream did Mom and Nicole enjoy by the ocean waves?", listOf("Golden mango ice cream", "Spinach soup", "Pepperoni"), 0)
                )
            )
        ),

        // 78. Level 3 • Science & Weather: "The Waterspout Over the River"
        StoryBook(
            id = "book_waterspout_over_river",
            title = "The Waterspout Over the River",
            nativeTitleUrdu = "دریا کے اوپر پانی کا حیرت انگیز بگولا",
            nativeTitleHindi = "नदी के ऊपर पानी का अद्भुत बवंडर",
            author = "Clyde Beech",
            illustrator = "Mohale Mashigo",
            publisher = "Book Dash • Science Series",
            level = 3,
            coverAssetPath = "books/sw_45764/image_8.webp",
            categoryTag = "Science & Weather",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: sp • str • cl",
            pages = listOf(
                StoryPage(1, "books/sw_45764/image_7.webp", "During strong summer storms, spinning columns of wind called waterspouts can form over rivers and lakes.", "Durante las tormentas pueden formarse trombas de agua sobre los ríos.", "موسمِ گرما کے تیز طوفانوں کے دوران دریاؤں اور جھیلوں کے اوپر گھومتی ہوئی ہوا کے آبی بگولے بن سکتے ہیں۔", "तेज़ तूफ़ानों के दौरान नदियों के ऊपर घूमती हवा के जल-बवंडर बन सकते हैं।", setOf("water", "clouds", "rain")),
                StoryPage(2, "books/sw_45764/image_8.webp", "The swirling breeze lifts surface water—and sometimes tiny minnows—right up into the low rainclouds!", "El viento giratorio levanta agua y pequeños peces hacia las nubes.", "گھومتی ہوئی تیز ہوا پانی کے ساتھ کبھی کبھار ننھی مچھلیوں کو بھی اٹھا کر بادلوں تک پہنچا دیتی ہے!", "घूमती हुई हवा पानी के साथ कभी-कभी नन्ही मछलियों को भी बादलों तक उठा लेती है!", setOf("fish", "clouds", "water")),
                StoryPage(3, "books/sw_45764/image_9.webp", "When the wind slows down, the water and little fish fall gently back to earth with the rain!", "Cuando el viento baja, los peces caen suavemente con la lluvia.", "جب ہوا کی رفتار کم ہوتی ہے تو پانی اور ننھی مچھلیاں بارش کے ساتھ نرم گھاس پر واپس آ گرتی ہیں!", "जब हवा धीमी होती है, तो मछलियाँ बारिश के साथ नरम घास पर गिरती हैं!", setOf("fish", "rain", "green"),
                    StoryQuizQuestion("What is a spinning column of wind over a river or lake called?", listOf("A waterspout", "A snowdrift", "A rainbow bridge"), 0)
                )
            )
        ),

        // 79. Level 3 • School & Kindness: "Back to the Flowing Stream"
        StoryBook(
            id = "book_back_to_flowing_stream",
            title = "Back to the Flowing Stream",
            nativeTitleUrdu = "ننھی مچھلیوں کی دریا میں واپسی",
            nativeTitleHindi = "नन्ही मछलियों की नदी में वापसी",
            author = "Mohale Mashigo",
            illustrator = "Clyde Beech",
            publisher = "Book Dash • StoryWeaver",
            level = 3,
            coverAssetPath = "books/sw_45764/image_13.webp",
            categoryTag = "School & Kindness",
            pages = listOf(
                StoryPage(1, "books/sw_45764/image_12.webp", "Lulu and her three friends kneeled on the wet grass and scooped every silver fish into buckets of fresh rainwater.", "Lulu y sus amigos recogieron cada pez en cubetas con agua.", "لولو اور اس کے تینوں دوستوں نے گیلی گھاس پر جھک کر ہر چمکدار مچھلی کو تازہ پانی کی بالٹیوں میں ڈالا۔", "लुलु और उसके तीनों दोस्तों ने हर चमकीली मछली को ताज़ा पानी की बाल्टी में डाला।", setOf("fish", "water", "friends")),
                StoryPage(2, "books/sw_45764/image_13.webp", "At the grassy riverbank, they tilted their buckets gently so the fish could glide back into the current.", "En la orilla del río inclinaron las cubetas con cuidado.", "دریا کے ہرے بھرے کنارے پر پہنچ کر انہوں نے بالٹیاں آہستہ سے جھکائیں تاکہ مچھلیاں بہتے پانی میں چلی جائیں۔", "नदी के किनारे पहुँचकर उन्होंने बाल्टियाँ झुकाईं ताकि मछलियाँ धारा में तैर सकें।", setOf("return", "fish", "water")),
                StoryPage(3, "books/sw_45764/image_14.webp", "The little silver fish leaped once in a cheerful arc to say thank you before swimming home!", "Los pececitos saltaron felices para dar las gracias.", "ننھی چمکدار مچھلیوں نے شکریہ ادا کرنے کے لیے پانی سے ایک خوشگوار چھلانگ لگائی اور تیرتی ہوئی چلی گئیں!", "नन्ही मछलियों ने धन्यवाद कहने के लिए पानी में उछाल भरी और तैरती चली गईं!", setOf("fish", "happy", "smile"),
                    StoryQuizQuestion("Why did the children carry the buckets of rainwater to the grassy riverbank?", listOf("To release the silver fish safely back into the river", "To wash their bicycles", "To paint the bridge"), 0)
                )
            )
        ),

        // 80. Level 3 • Numbers & Rhymes: "Rhythm of the Wooden Drums"
        StoryBook(
            id = "book_rhythm_wooden_drums",
            title = "Rhythm of the Wooden Drums",
            nativeTitleUrdu = "لکڑی کے ڈھول کی سریلی تال",
            nativeTitleHindi = "लकड़ी के ढोल की मधुर ताल",
            author = "Mabel Mnensa",
            illustrator = "Ntembiso Velemane",
            publisher = "Book Dash • StoryWeaver",
            level = 3,
            coverAssetPath = "books/sw_44485/image_12.webp",
            categoryTag = "Numbers & Rhymes",
            pages = listOf(
                StoryPage(1, "books/sw_44485/image_11.webp", "One, two, three, four! The drummers tap their carved wooden djembe drums in time with the train wheels.", "Uno, dos, tres, cuatro: los tambores suenan al ritmo del tren.", "ایک، دو، تین، چار! ڈھول بجانے والے ٹرین کے پہیوں کی آواز کے ساتھ اپنے لکڑی کے ڈھول بجاتے ہیں۔", "एक, दो, तीन, चार! ढोल वादक ट्रेन के पहियों की ताल पर ढोल बजाते हैं।", setOf("drummers", "train")),
                StoryPage(2, "books/sw_44485/image_12.webp", "Clap-clap, tap-tap! Nicole counts four beats in every measure as she dances down the aisle.", "Nicole cuenta cuatro tiempos mientras baila con alegría.", "تالی اور تھاپ! نکول خوشی سے ناچتے ہوئے موسیقی کی ہر لے میں چار تالیں گنتی ہے۔", "ताली और थाप! निकोल खुशी से नाचते हुए संगीत की चार तालें गिनती है।", setOf("drummers", "happy", "play")),
                StoryPage(3, "books/sw_44485/image_14.webp", "Music and counting bring everyone on the coastal train together in one big smiling chorus!", "La música y el ritmo unen a todos en el tren.", "موسیقی اور تال کی گنتی ساحلی ٹرین کے تمام مسافروں کو ایک خوشگوار گیت میں جوڑ دیتی ہے!", "संगीत और ताल की गिनती ट्रेन के सभी यात्रियों को खुशी से जोड़ देती है!", setOf("train", "friends", "smile"),
                    StoryQuizQuestion("How many beats does Nicole count in every musical measure of the drums?", listOf("Four beats (One, two, three, four!)", "Zero beats", "One hundred beats"), 0)
                )
            )
        ),

        // 81. Level 3 • Animals & Nature: "Eight Legs and One Silky Web"
        StoryBook(
            id = "book_eight_legs_silky_web",
            title = "Eight Legs and One Silky Web",
            nativeTitleUrdu = "آٹھ ٹانگیں اور ایک ریشمی جالا",
            nativeTitleHindi = "आठ टाँगें और एक रेशमी जाला",
            author = "Ounla Santi",
            illustrator = "Ounla Santi",
            publisher = "Room to Read • Science Series",
            level = 3,
            coverAssetPath = "books/sw_37273/image_2.webp",
            categoryTag = "Animals & Nature",
            pages = listOf(
                StoryPage(1, "books/sw_37273/image_3.webp", "Did you know that insects like ladybirds and butterflies have six legs, while spiders have eight?", "Los insectos tienen seis patas, mientras que las arañas tienen ocho.", "کیا آپ جانتے ہیں کہ لیڈی برڈ اور تتلی کے چھ پاؤں ہوتے ہیں جبکہ مکڑی کی آٹھ ٹانگیں ہوتی ہیں؟", "क्या आप जानते हैं कि तितली के छह पैर होते हैं जबकि मकड़ी के आठ पैर होते हैं?", setOf("spider", "ladybird", "butterfly")),
                StoryPage(2, "books/sw_13916/image_6.webp", "Spiders use special spinnerets to weave strong silk threads that glisten in the morning dew.", "Las arañas tejen hilos de seda fuertes que brillan al sol.", "مکڑیاں اپنے خاص ریشمی تاروں سے مضبوط جالا بنتی ہیں جو صبح کی شبنم میں چمکتا ہے۔", "मकड़ियाँ अपने रेशमी धागों से मज़बूत जाला बुनती हैं जो ओस में चमकता है।", setOf("spider", "morning", "bright")),
                StoryPage(3, "books/sw_37273/image_4.webp", "In the garden ecosystem, spiders, frogs, and butterflies all help keep the plants healthy and blooming!", "En el jardín todos ayudan a mantener las plantas sanas.", "باغ کے قدرتی نظام میں مکڑیاں، مینڈک اور تتلیاں سب مل کر پودوں کو تندرست اور ہرا بھرا رکھتی ہیں!", "बग़ीचे में मकड़ी, मेंढक और तितलियाँ मिलकर पौधों को हरा-भरा रखती हैं!", setOf("garden", "frog", "butterfly"),
                    StoryQuizQuestion("How many legs does a spider have compared to an insect?", listOf("A spider has eight legs, while insects have six", "A spider has two legs", "A spider has fifty legs"), 0)
                )
            )
        ),

        // 82. Level 3 • Bedtime & Folktales: "Starlight Over Green Hills"
        StoryBook(
            id = "book_starlight_green_hills",
            title = "Starlight Over Green Hills",
            nativeTitleUrdu = "سبز پہاڑیوں پر ستاروں کی چاندنی",
            nativeTitleHindi = "हरी पहाड़ियों पर तारों की चाँदनी",
            author = "Martha Evans",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • StoryWeaver",
            level = 3,
            coverAssetPath = "books/sw_13916/image_19.webp",
            categoryTag = "Bedtime & Folktales",
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_19.webp", "When twilight settles over the emerald valley, the daytime birds tuck their beaks beneath their wings.", "Al anochecer las aves descansan bajo las estrellas.", "جب ہری بھری وادی پر شام اترتی ہے تو دن کے پرندے اپنے پروں میں سمٹ کر آرام کرتے ہیں۔", "जब हरी घाटी में शाम ढलती है, तो पक्षी अपने घोंसलों में आराम करते हैं।", setOf("wings", "green", "trees")),
                StoryPage(2, "books/sw_13916/image_20.webp", "One by one, silver constellations light up across the deep indigo sky like a map of glowing lanterns.", "Las constelaciones plateadas iluminan el cielo nocturno.", "ایک ایک کر کے گہرے نیلے آسمان پر چمکتے ستاروں کے جھرمٹ روشن لالٹینوں کی طرح جگمگا اٹھتے ہیں۔", "एक-एक करके गहरे नीले आसमान में तारों के समूह लालटेन की तरह चमक उठते हैं।", setOf("stars", "sky", "bright")),
                StoryPage(3, "books/sw_15195/image_17.webp", "The gentle moon sends a silver beam across every window, wishing young readers peaceful dreams!", "La luna envía un rayo plateado deseando dulces sueños.", "چاند کی ٹھنڈی چاندنی ہر کھڑکی سے جھانک کر ننھے قارئین کو سہانے خوابوں کی دعا دیتی ہے!", "चाँद की शीतल किरणें बच्चों को मीठे सपनों की शुभकामनाएँ देती हैं!", setOf("moon", "read", "happy"),
                    StoryQuizQuestion("What lights up across the deep night sky when twilight settles over the valley?", listOf("Silver stars and the gentle moon", "Traffic lights", "School buses"), 0)
                )
            )
        ),

        // 83. Level 4 • Adventure & Travel: "Captain Zara's Ocean Voyage"
        StoryBook(
            id = "book_captain_zara_voyage",
            title = "Captain Zara's Ocean Voyage",
            nativeTitleUrdu = "کیپٹن زارا کا بحری سفر",
            nativeTitleHindi = "कैप्टन ज़ारा का समुद्री सफ़र",
            author = "Mabel Mnensa",
            illustrator = "Ntembiso Velemane",
            publisher = "Book Dash • StoryWeaver",
            level = 4,
            coverAssetPath = "books/sw_44485/image_15.webp",
            categoryTag = "Adventure & Travel",
            bookType = "SERIES",
            seriesTag = "Coastline Train • Ep 6",
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_44485/image_10.webp", "Before boarding the coastal train, Captain Zara spent three months sailing a research vessel across the Indian Ocean.", "La capitana Zara navegó tres meses por el océano.", "ساحلی ٹرین میں سفر سے پہلے کیپٹن زارا نے تین ماہ تک بحرِ ہند میں ایک تحقیقی جہاز کی قیادت کی تھی۔", "ट्रेन यात्रा से पहले कैप्टन ज़ारा ने तीन महीने महासागर में जहाज़ चलाया था।", setOf("sailor", "ocean", "train")),
                StoryPage(2, "books/sw_44485/image_15.webp", "Using her magnetic brass compass, marine charts, and satellite beacons, she guided her crew safely through stormy seas.", "Con su brújula dorada guió a su tripulación con seguridad.", "اپنے سنہری قطب نما اور بحری نقشوں کی مدد سے اس نے طوفانی لہروں میں اپنے عملے کی بحفاظت رہنمائی کی۔", "अपने सुनहरे कंपास और नक़्शों की मदद से उसने जहाज़ को सुरक्षित रास्ता दिखाया।", setOf("sailor", "water", "clouds")),
                StoryPage(3, "books/sw_44485/image_14.webp", "Inspired by Captain Zara's story, Nicole decided to read every book about ships, oceans, and navigation at her library!", "Inspirada por Zara, Nicole decidió leer libros sobre el mar.", "کیپٹن زارا کی کہانی سے متاثر ہو کر نکول نے لائبریری سے سمندروں اور جہاز رانی کی تمام کتابیں پڑھنے کا عزم کیا!", "कैप्टन ज़ारा की कहानी से प्रेरित होकर निकोल ने समुद्र और जहाज़ों की किताबें पढ़ने का निश्चय किया!", setOf("books", "library", "ocean"),
                    StoryQuizQuestion("What tools did Captain Zara use to guide her crew safely across the ocean?", listOf("A magnetic brass compass, marine charts, and beacons", "A magic wand", "A pair of roller skates"), 0)
                )
            )
        ),

        // 84. Level 4 • School & Kindness: "The Great Garden Talent Show"
        StoryBook(
            id = "book_garden_talent_show",
            title = "The Great Garden Talent Show",
            nativeTitleUrdu = "باغ کے اسکول کا سالانہ ٹیلنٹ شو",
            nativeTitleHindi = "बग़ीचे के स्कूल का टैलेंट शो",
            author = "Catherine Holtzhausen",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • StoryWeaver",
            level = 4,
            coverAssetPath = "books/sw_13916/cover_image.webp",
            categoryTag = "School & Kindness",
            bookSource = "CLASSROOM",
            assignedByTeacher = "Miss Miya • Grade 4",
            pages = listOf(
                StoryPage(1, "books/sw_13916/cover_image.webp", "Every spring, Miss Miya organizes the Great Garden Talent Show on the mossy amphitheater stage.", "Cada primavera la maestra organiza el show de talentos del jardín.", "ہر موسمِ بہار میں مس میا باغ کے سبز اسٹیج پر سالانہ گارڈن ٹیلنٹ شو منعقد کرتی ہیں۔", "हर वसंत ऋतु में मिस मिया बग़ीचे के मंच पर वार्षिक टैलेंट शो आयोजित करती हैं।", setOf("teacher", "garden", "school")),
                StoryPage(2, "books/sw_13916/image_5.webp", "Bibi Butterfly performs an aerial ribbon dance, Manto Mantis recites a poem, and Sesa weaves a glowing silk harp!", "Bibi baila en el aire, Manto recita un poema y Sesa teje un arpa.", "بی بی تتلی ہوا میں رقص دکھاتی ہے، مینٹو مینٹس نظم سناتا ہے اور سیسا مکڑی ریشمی تاروں کا ساز بپاتی ہے!", "बीबी तितली नृत्य करती है, मैंटो कविता सुनाता है और सेसा रेशमी धुन बजाती है!", setOf("butterfly", "spider", "special")),
                StoryPage(3, "books/sw_13916/image_15.webp", "For the grand finale, Lara leads the whole class in a song about friendship, courage, and shining in your own true colors!", "Para el gran final, Lara dirige una canción sobre la amistad.", "اختتامی تقریب میں لارا پوری کلاس کے ساتھ دوستی، ہمت اور اپنی اصل شناخت پر فخر کا گیت پیش کرتی ہے!", "अंत में लारा पूरी कक्षा के साथ दोस्ती, साहस और अपनी पहचान पर गर्व का गीत गाती है!", setOf("ladybird", "friends", "colors"),
                    StoryQuizQuestion("What is the theme of the grand finale song that Lara leads at the Talent Show?", listOf("Friendship, courage, and shining in your own true colors", "How to hide under rocks", "Why winter is cold"), 0)
                )
            )
        ),

        // 85. Level 4 • Family & Friends: "Stories Across Three Generations"
        StoryBook(
            id = "book_three_generations_stories",
            title = "Stories Across Three Generations",
            nativeTitleUrdu = "تین نسلوں کی کہانیوں کا خزانہ",
            nativeTitleHindi = "तीन पीढ़ियों की कहानियों का ख़ज़ाना",
            author = "Letta Machoga",
            illustrator = "Wiehan de Jager",
            publisher = "African Storybook • StoryWeaver",
            level = 4,
            coverAssetPath = "books/sw_15058/cover_image.webp",
            categoryTag = "Family & Friends",
            bookType = "ASSESSMENT",
            pages = listOf(
                StoryPage(1, "books/sw_15058/cover_image.webp", "In our home, stories are a bridge that connects grandparents, parents, and children across three generations.", "En casa los cuentos unen a abuelos, padres e hijos.", "ہمارے گھر میں کہانیاں ایک ایسا پل ہیں جو دادا دادی، والدین اور بچوں کی تین نسلوں کو آپس میں جوڑتا ہے۔", "हमारे घर में कहानियाँ दादा-दादी, माता-पिता और बच्चों की तीन पीढ़ियों को जोड़ती हैं।", setOf("grandfather", "grandmother", "story")),
                StoryPage(2, "books/sw_15058/image_6.webp", "Grandmother shares recipes and proverbs from her childhood village, while Grandfather Farouk recounts history under the stars.", "La abuela comparte proverbios y el abuelo historias bajo las estrellas.", "دادی جان اپنے بچپن کی ضرب الامثال سناتی ہیں جبکہ دادا فاروق ستاروں کی چھاؤں میں تاریخ کے قصے بتاتے ہیں۔", "दादीजी अपने बचपन की कहावतें सुनाती हैं और दादा फ़ारूक़ तारों के नीचे इतिहास की बातें बताते हैं।", setOf("grandmother", "grandfather", "stars")),
                StoryPage(3, "books/sw_15058/image_9.webp", "And by reading new books every day, the children write the next bright chapter of our family's story!", "Al leer libros nuevos cada día, los niños escriben el siguiente capítulo.", "اور ہر روز نئی کتابیں پڑھ کر بچے ہمارے خاندان کی کہانی کا اگلا روشن باب تحریر کرتے ہیں!", "और हर दिन नई किताबें पढ़कर बच्चे हमारे परिवार की कहानी का अगला सुनहरा अध्याय लिखते हैं!", setOf("read", "books", "bright"),
                    StoryQuizQuestion("What acts as a bridge connecting three generations in the family home?", listOf("Sharing and reading stories together", "Watching silent clocks", "Locked cupboards"), 0)
                )
            )
        ),

        // 86. Level 4 • Science & Weather: "Gravity, Wind, and Flight"
        StoryBook(
            id = "book_gravity_wind_flight",
            title = "Gravity, Wind, and Flight",
            nativeTitleUrdu = "کششِ ثقل، ہوا اور پرواز کا راز",
            nativeTitleHindi = "गुरुत्वाकर्षण, हवा और उड़ान का रहस्य",
            author = "Marion Drew",
            illustrator = "Marion Drew",
            publisher = "Pratham Books • Science Series",
            level = 4,
            coverAssetPath = "books/sw_15195/image_16.webp",
            categoryTag = "Science & Weather",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: gr • fl • str",
            pages = listOf(
                StoryPage(1, "books/sw_15195/image_9.webp", "When you kick a ball upward, the force of your foot launches it high into the air against gravity.", "Cuando pateas una pelota, la fuerza la impulsa hacia el aire.", "جب آپ گیند کو اوپر کی طرف کک مارتے ہیں تو آپ کے پاؤں کی قوت اسے کششِ ثقل کے خلاف ہوا میں بلند کرتی ہے۔", "जब आप गेंद को ऊपर किक मारते हैं, तो पैर का बल उसे हवा में ऊँचा उछालता है।", setOf("ball", "shoe", "flying")),
                StoryPage(2, "books/sw_15195/image_16.webp", "Birds, butterflies, and airplanes stay aloft by using curved wings to create lift as wind flows over them.", "Las aves y aviones usan sus alas para crear sustentación en el aire.", "پرندے، تتلیاں اور ہوائی جہاز اپنے پروں کے خاص زاویے اور ہوا کے دباؤ کی مدد سے آسمان میں تیرتے ہیں۔", "पक्षी, तितलियाँ और हवाई जहाज़ अपने पंखों और हवा के बहाव की मदद से आसमान में उड़ते हैं।", setOf("wings", "butterfly", "sky")),
                StoryPage(3, "books/sw_15195/image_17.webp", "And rockets travel even faster—escaping Earth's atmosphere to orbit the silver moon and explore outer space!", "Y los cohetes viajan aún más rápido hacia la luna y el espacio.", "اور خلائی راکٹ اس سے بھی تیز رفتار سے زمین کی فضا سے نکل کر چاند اور خلا کا سفر طے کرتے ہیں!", "और रॉकेट इससे भी तेज़ गति से पृथ्वी के वातावरण से बाहर निकलकर चाँद और अंतरिक्ष में जाते हैं!", setOf("moon", "stars", "sky"),
                    StoryQuizQuestion("How do birds, butterflies, and airplanes create lift to stay aloft in the sky?", listOf("Using wings as wind flows over them", "By wearing heavy boots", "By hiding under clouds"), 0)
                )
            )
        ),

        // 87. Level 4 • School & Kindness: "Lulu's Community Book Drive"
        StoryBook(
            id = "book_lulu_book_drive",
            title = "Lulu's Community Book Drive",
            nativeTitleUrdu = "لولو کی محلے میں کتابیں بانٹنے کی مہم",
            nativeTitleHindi = "लुलु का मोहल्ले में पुस्तक अभियान",
            author = "Clyde Beech & Mohale Mashigo",
            illustrator = "Clyde Beech",
            publisher = "Book Dash • StoryWeaver",
            level = 4,
            coverAssetPath = "books/sw_45764/cover_image.webp",
            categoryTag = "School & Kindness",
            bookType = "SERIES",
            seriesTag = "Lulu Library • Ep 4",
            pages = listOf(
                StoryPage(1, "books/sw_45764/cover_image.webp", "Lulu loved the neighborhood library so much that she wanted every child in town to have a library card.", "Lulu quería que todos los niños tuvieran su tarjeta de biblioteca.", "لولو کو محلے کی لائبریری سے اتنا پیار تھا کہ وہ چاہتی تھی شہر کے ہر بچے کے پاس لائبریری کارڈ ہو۔", "लुलु चाहती थी कि शहर के हर बच्चे के पास लाइब्रेरी का कार्ड और किताबें हों।", setOf("library", "books", "friends")),
                StoryPage(2, "books/sw_45764/image_11.webp", "On Saturday morning, she invited her friends to tour the reading shelves and pick their first adventure books.", "El sábado invitó a sus amigos a conocer los estantes de lectura.", "ہفتے کی صبح اس نے اپنے تمام دوستوں کو لائبریری کی سیر کروائی تاکہ ہر بچہ اپنی پسندیدہ کتاب منتخب کرے۔", "शनिवार सुबह उसने अपने दोस्तों को लाइब्रेरी की सैर कराई और नई किताबें चुनने में मदद की।", setOf("morning", "read", "books")),
                StoryPage(3, "books/sw_45764/image_15.webp", "Now the whole neighborhood gathers every weekend to read, swap stories, and learn together!", "Ahora todo el barrio se reúne cada semana para leer y aprender.", "اب پورا محلہ ہر ہفتے مل کر کتابیں پڑھتا، نئی کہانیاں سناتا اور علم بانٹتا ہے!", "अब पूरा मोहल्ला हर सप्ताह मिलकर किताबें पढ़ता और नई कहानियाँ साझा करता है!", setOf("story", "read", "happy"),
                    StoryQuizQuestion("Why did Lulu invite all her neighborhood friends to the library on Saturday?", listOf("So every child could get a library card and borrow books", "To clean the kitchen stove", "To look for a lost shoe"), 0)
                )
            )
        ),

        // 88. Level 4 • Animals & Nature: "Pollinators of the Golden Meadow"
        StoryBook(
            id = "book_pollinators_golden_meadow",
            title = "Pollinators of the Golden Meadow",
            nativeTitleUrdu = "سنہری باغ کے پھول اور تتلیاں",
            nativeTitleHindi = "सुनहरे बग़ीचे के फूल और तितलियाँ",
            author = "Somsanouk Bouttavong",
            illustrator = "Ounla Santi",
            publisher = "Room to Read • Science Series",
            level = 4,
            coverAssetPath = "books/sw_37273/image_14.webp",
            categoryTag = "Animals & Nature",
            pages = listOf(
                StoryPage(1, "books/sw_37273/image_14.webp", "As the newly emerged butterfly glides from marigold to daisy, she drinks sweet nectar from each blossom.", "La mariposa vuela de flor en flor bebiendo néctar dulce.", "جیسے ہی نئی تتلی گیندے کے ایک پھول سے دوسرے پھول تک اڑتی ہے، وہ ہر کلی سے میٹھا رس پیتی ہے۔", "जब तितली एक फूल से दूसरे फूल पर उड़ती है, तो वह फूलों का मीठा रस पीती है।", setOf("butterfly", "marigolds", "flowers")),
                StoryPage(2, "books/sw_13916/image_4.webp", "Tiny grains of golden pollen stick to her wings and travel to the next flower, helping new seeds and fruits grow!", "El polen dorado viaja en sus alas ayudando a crecer nuevas semillas.", "سنہری زردانے اس کے پروں کے ساتھ لگ کر دوسرے پھولوں تک پہنچتے ہیں جس سے نئے بیج اور پھل اگتے ہیں!", "सुनहरे परागकण उसके पंखों से दूसरे फूलों तक पहुँचते हैं जिससे नए फल और बीज उगते हैं!", setOf("golden", "wings", "flowers")),
                StoryPage(3, "books/sw_13916/image_1.webp", "Without tiny pollinators like butterflies, bees, and ladybirds, our gardens and orchards could never bloom so brightly!", "Sin los polinizadores nuestros jardines no podrían florecer.", "تتلیوں، شہد کی مکھیوں اور لیڈی برڈز جیسے ننھے مددگاروں کے بغیر ہمارے باغات کبھی اتنے ہرے بھرے نہیں ہو سکتے!", "तितलियों और लेडीबर्ड्स जैसे नन्हे जीवों के कारण ही हमारे बग़ीचे इतने सुंदर खिलते हैं!", setOf("butterfly", "ladybird", "garden"),
                    StoryQuizQuestion("How do butterflies help new seeds and fruits grow in the garden?", listOf("By carrying golden pollen from flower to flower", "By wearing frog shoes", "By digging sand burrows"), 0)
                )
            )
        ),

        // 89. Level 4 • Numbers & Rhymes: "One Hundred Stars in the Sky"
        StoryBook(
            id = "book_one_hundred_stars",
            title = "One Hundred Stars in the Sky",
            nativeTitleUrdu = "آسمان میں ایک سو چمکتے ستارے",
            nativeTitleHindi = "आसमान में एक सौ चमकते तारे",
            author = "Marion Drew & Martha Evans",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • Space & Math Series",
            level = 4,
            coverAssetPath = "books/sw_13916/image_16.webp",
            categoryTag = "Numbers & Rhymes",
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_19.webp", "On a clear night away from city lights, we count stars by tens: ten, twenty, thirty, forty, fifty!", "En una noche clara contamos estrellas de diez en diez.", "شہر کی روشنیوں سے دور صاف رات میں ہم دس دس کر کے ستارے گنتے ہیں: دس، بیس، تیس، چالیس، پچاس!", "साफ़ रात में हम दस-दस करके तारे गिनते हैं: दस, बीस, तीस, चालीस, पचास!", setOf("stars", "sky")),
                StoryPage(2, "books/sw_13916/image_20.webp", "Sixty, seventy, eighty, ninety, one hundred glowing stars—and thousands more twinkling across the Milky Way!", "Sesenta, setenta, ochenta, noventa y cien estrellas brillantes.", "ساٹھ، ستر، اسی، نوے، اور ایک سو چمکتے ستارے—اور کہکشاں میں ہزاروں مزید ستارے جگمگا رہے ہیں!", "साठ, सत्तर, अस्सी, नब्बे और एक सौ चमकते तारे—और आकाशगंगा में हज़ारों तारे टिमटिमा रहे हैं!", setOf("hundred", "stars", "bright")),
                StoryPage(3, "books/sw_15195/image_17.webp", "Mathematics and reading help us measure the distance to the moon and unlock the secrets of the cosmos!", "Las matemáticas y la lectura nos ayudan a explorar el cosmos.", "ریاضی اور مطالعہ ہمیں چاند تک کا فاصلہ ناپنے اور کائنات کے راز جاننے میں مدد دیتے ہیں!", "गणित और पढ़ाई हमें चाँद की दूरी मापने और ब्रह्मांड के रहस्य जानने में मदद करते हैं!", setOf("moon", "read", "books"),
                    StoryQuizQuestion("How many stars do we reach when we count ten groups of ten stars?", listOf("One hundred (100) stars", "Twelve stars", "Five stars"), 0)
                )
            )
        ),

        // 90. Level 1 • Animals & Nature: "Little Bug's Green Leaf"
        StoryBook(
            id = "book_little_bug_green_leaf",
            title = "Little Bug's Green Leaf",
            nativeTitleUrdu = "ننھے کیڑے کا ہرا پتہ",
            nativeTitleHindi = "नन्हे कीट का हरा पत्ता",
            author = "Catherine Holtzhausen",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_13916/image_2.webp",
            categoryTag = "Animals & Nature",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: l • g • b",
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_2.webp", "Look at the little yellow bug sitting on a wide green leaf.", "Mira el bichito amarillo sobre una hoja verde.", "چوڑے ہرے پتے پر بیٹھی ننھی پیلی لیڈی برڈ کو دیکھو۔", "चौड़े हरे पत्ते पर बैठी नन्ही पीली लेडीबर्ड को देखो।", setOf("ladybird", "yellow", "green", "leaves")),
                StoryPage(2, "books/sw_13916/image_1.webp", "The green leaf sways gently in the warm morning sun.", "La hoja verde se mece bajo el sol de la mañana.", "ہرا پتہ صبح کی گرم دھوپ میں ہولے ہولے جھولتا ہے۔", "हरा पत्ता सुबह की धूप में धीरे-धीरे झूमता है।", setOf("green", "leaves", "morning", "sun")),
                StoryPage(3, "books/sw_13916/image_3.webp", "Up, up, up! The little bug opens her yellow wings and flies!", "El bichito abre sus alas amarillas y vuela.", "اوپر، اور اوپر! ننھی لیڈی برڈ اپنے پیلے پر کھولتی ہے اور اڑتی ہے!", "नन्ही लेडीबर्ड अपने पीले पंख खोलती है और उड़ती है!", setOf("yellow", "wings", "flying"),
                    StoryQuizQuestion("What color is the leaf that the little yellow bug sits on?", listOf("Green", "Purple", "Black"), 0)
                )
            )
        ),

        // 91. Level 1 • School & Kindness: "Walking to Bug School"
        StoryBook(
            id = "book_walking_to_bug_school",
            title = "Walking to Bug School",
            nativeTitleUrdu = "بگز اسکول کا خوشگوار راستہ",
            nativeTitleHindi = "बग़ीचे के स्कूल का रास्ता",
            author = "Nadene Kriel",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_13916/image_10.webp",
            categoryTag = "School & Kindness",
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_10.webp", "Every morning we walk along the flower path to reach our garden school.", "Cada mañana caminamos por el sendero de flores a la escuela.", "ہر صبح ہم پھولوں والے راستے پر چل کر اپنے باغ کے اسکول پہنچتے ہیں۔", "हर सुबह हम फूलों के रास्ते से होकर अपने बग़ीचे के स्कूल पहुँचते हैं।", setOf("morning", "walk", "flowers", "school")),
                StoryPage(2, "books/sw_13916/image_11.webp", "Our friends are waiting by the classroom leaf door with big smiles.", "Nuestros amigos esperan junto a la puerta del aula.", "ہمارے دوست بڑی مسکراہٹ کے ساتھ کلاس کے دروازے پر انتظار کر رہے ہیں۔", "हमारे दोस्त मुस्कुराते हुए कक्षा के दरवाज़े पर इंतज़ार कर रहे हैं।", setOf("friends", "classroom", "smile")),
                StoryPage(3, "books/sw_13916/image_7.webp", "We sit in a circle and read our favorite morning story together!", "Nos sentamos en círculo y leemos nuestro cuento.", "ہم دائرے میں بیٹھتے ہیں اور مل کر اپنی پسندیدہ صبح کی کہانی پڑھتے ہیں!", "हम घेरे में बैठते हैं और मिलकर अपनी पसंदीदा कहानी पढ़ते हैं!", setOf("read", "story", "morning"),
                    StoryQuizQuestion("Where do the bug friends sit to read their morning story?", listOf("In a circle inside the garden classroom", "On top of a train", "In the ocean"), 0)
                )
            )
        ),

        // 92. Level 1 • Numbers & Rhymes: "Kick with My Shoe"
        StoryBook(
            id = "book_kick_with_my_shoe",
            title = "Kick with My Shoe",
            nativeTitleUrdu = "میرے جوتے کی زوردار کک",
            nativeTitleHindi = "मेरे जूते की ज़ोरदार किक",
            author = "Khothatso Ranoosi",
            illustrator = "Marion Drew",
            publisher = "Pratham Books • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_15195/image_6.webp",
            categoryTag = "Numbers & Rhymes",
            bookType = "DECODABLE",
            phonicsSkillTag = "Phonics: k • sh • b",
            pages = listOf(
                StoryPage(1, "books/sw_15195/image_3.webp", "I hold my round red ball in my two hands.", "Sostengo mi pelota roja en mis dos manos.", "میں اپنے دونوں ہاتھوں میں اپنی گول سرخ گیند پکڑتا ہوں۔", "मैं अपने दोनों हाथों में अपनी गोल लाल गेंद पकड़ता हूँ।", setOf("red", "ball")),
                StoryPage(2, "books/sw_15195/image_6.webp", "I place the ball on the grass and tap it with my shoe.", "Pongo la pelota en el césped y la pateo con mi zapato.", "میں گیند کو گھاس پر رکھتا ہوں اور اپنے جوتے سے اسے کک مارتا ہوں۔", "मैं गेंद को घास पर रखता हूँ और अपने जूते से किक मारता हूँ।", setOf("ball", "shoe", "green")),
                StoryPage(3, "books/sw_15195/image_7.webp", "Roll, roll, roll! The red ball zooms across the sunny park!", "La pelota roja rueda por el parque soleado.", "لڑھکتی ہوئی سرخ گیند دھوپ والے پارک میں دور تک جاتی ہے!", "लाल गेंद धूप वाले पार्क में दूर तक लुढ़कती जाती है!", setOf("red", "ball", "play"),
                    StoryQuizQuestion("What does the child use to kick the round red ball across the grass?", listOf("A shoe on their foot", "A wooden spoon", "An umbrella"), 0)
                )
            )
        ),

        // 93. Level 1 • Science & Weather: "Above the Tall Trees"
        StoryBook(
            id = "book_above_tall_trees",
            title = "Above the Tall Trees",
            nativeTitleUrdu = "اونچے درختوں کے اوپر",
            nativeTitleHindi = "ऊँचे पेड़ों के ऊपर",
            author = "Marion Drew",
            illustrator = "Marion Drew",
            publisher = "Pratham Books • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_15195/image_13.webp",
            categoryTag = "Science & Weather",
            pages = listOf(
                StoryPage(1, "books/sw_15195/image_13.webp", "Look up at the tall green trees swaying in the afternoon wind.", "Mira los altos árboles verdes meciéndose al viento.", "سہ پہر کی ہوا میں جھومتے ہوئے اونچے ہرے درختوں کی طرف دیکھو۔", "हवा में झूमते हुए ऊँचे हरे पेड़ों की ओर देखो।", setOf("trees", "green")),
                StoryPage(2, "books/sw_15195/image_14.webp", "Way up above the highest branch, my red ball is flying in the sky!", "Muy por encima de la rama más alta vuela mi pelota.", "سب سے اونچی شاخ سے بھی اوپر میری سرخ گیند آسمان میں اڑ رہی ہے!", "सबसे ऊँची शाखा के भी ऊपर मेरी लाल गेंद आसमान में उड़ रही है!", setOf("red", "ball", "sky", "flying")),
                StoryPage(3, "books/sw_15195/image_15.webp", "The bright blue sky stretches wide over our whole green garden!", "El cielo azul brillante se extiende sobre el jardín.", "چمکدار نیلا آسمان ہمارے پورے ہرے باغ کے اوپر پھیلا ہوا ہے!", "चमकीला नीला आसमान हमारे पूरे हरे बग़ीचे के ऊपर फैला है!", setOf("bright", "blue", "sky", "garden"),
                    StoryQuizQuestion("What is flying in the sky way up above the highest tree branch?", listOf("The bright red ball", "A green frog", "A wooden table"), 0)
                )
            )
        ),

        // 94. Level 1 • Adventure & Travel: "Beach Day with Mom"
        StoryBook(
            id = "book_beach_day_with_mom",
            title = "Beach Day with Mom",
            nativeTitleUrdu = "امی کے ساتھ ساحل کی سیر",
            nativeTitleHindi = "माँ के साथ समुद्र तट की सैर",
            author = "Mabel Mnensa",
            illustrator = "Ntembiso Velemane",
            publisher = "Book Dash • StoryWeaver",
            level = 1,
            coverAssetPath = "books/sw_44485/image_2.webp",
            categoryTag = "Adventure & Travel",
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_44485/image_2.webp", "Hurray! Today is Beach Day, and the golden sun is shining in the sky.", "Hoy es día de playa y el sol dorado brilla.", "واہ! آج ساحل کی سیر کا دن ہے اور آسمان میں سنہرا سورج چمک رہا ہے۔", "वाह! आज समुद्र तट की सैर का दिन है और सुनहरा सूरज चमक रहा है।", setOf("beach", "golden", "sun", "sky")),
                StoryPage(2, "books/sw_44485/image_4.webp", "Mom and I ride the fast silver train all the way to the coast.", "Mamá y yo viajamos en el rápido tren plateado.", "امی اور میں تیز چاندی جیسی ٹرین میں بیٹھ کر ساحل تک جاتے ہیں۔", "माँ और मैं तेज़ ट्रेन में बैठकर समुद्र तट तक जाते हैं।", setOf("mother", "train")),
                StoryPage(3, "books/sw_44485/image_16.webp", "At the beach, we eat sweet mango ice cream and watch the blue waves!", "En la playa comemos helado de mango y miramos las olas.", "ساحل پر ہم میٹھی آم کی آئس کریم کھاتے ہیں اور نیلی لہروں کو دیکھتے ہیں!", "समुद्र तट पर हम मीठी आइसक्रीम खाते हैं और नीली लहरें देखते हैं!", setOf("beach", "blue", "water"),
                    StoryQuizQuestion("How do Mom and Nicole travel all the way to the beach?", listOf("On the fast silver train", "On a camel", "In a submarine"), 0)
                )
            )
        ),

        // 95. Level 2 • Numbers & Rhymes: "Kapow! Karate Count"
        StoryBook(
            id = "book_kapow_karate_count",
            title = "Kapow! Karate Count",
            nativeTitleUrdu = "کراٹے کی مشق اور گنتی",
            nativeTitleHindi = "कराटे का अभ्यास और गिनती",
            author = "Mabel Mnensa",
            illustrator = "Ntembiso Velemane",
            publisher = "Book Dash • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_44485/image_9.webp",
            categoryTag = "Numbers & Rhymes",
            pages = listOf(
                StoryPage(1, "books/sw_44485/image_7.webp", "One, two! The karate boys tie their black belts over their white uniforms.", "Uno, dos: los niños atan sus cinturones negros.", "ایک، دو! کراٹے کرنے والے لڑکے اپنے سفید یونیفارم پر بلیک بیلٹ باندھتے ہیں۔", "एक, दो! कराटे करने वाले बच्चे अपनी सफ़ेद यूनिफ़ॉर्म पर बेल्ट बाँधते हैं।", setOf("white", "friends")),
                StoryPage(2, "books/sw_44485/image_8.webp", "Three, four! They bow politely and balance on one foot with steady focus.", "Tres, cuatro: hacen una reverencia con equilibrio.", "تین، چار! وہ ادب سے جھک کر سلام کرتے ہیں اور ایک پاؤں پر توازن بناتے ہیں۔", "तीन, चार! वे विनम्र प्रणाम करते हैं और संतुलन बनाते हैं।", setOf("special", "friends")),
                StoryPage(3, "books/sw_44485/image_9.webp", "Five, six—Kapow! Everyone claps six times for their awesome karate jump!", "Cinco, seis: todos aplauden su gran salto de karate.", "پانچ، چھ—کپاؤ! ان کی شاندار چھلانگ پر سب چھ بار تالیاں بجاتے ہیں!", "पाँच, छह—कपॉव! उनकी शानदार छलांग पर सब तालियाँ बजाते हैं!", setOf("play", "happy"),
                    StoryQuizQuestion("What color uniforms do the karate boys wear with their black belts?", listOf("Crisp white uniforms", "Pink dresses", "Yellow raincoats"), 0)
                )
            )
        ),

        // 96. Level 2 • Science & Weather: "Candles in the Evening Breeze"
        StoryBook(
            id = "book_candles_evening_breeze",
            title = "Candles in the Evening Breeze",
            nativeTitleUrdu = "شام کی ہوا اور موم بتیوں کی روشنی",
            nativeTitleHindi = "शाम की हवा और मोमबत्तियों की रोशनी",
            author = "Clyde Beech",
            illustrator = "Mohale Mashigo",
            publisher = "Book Dash • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_45764/image_2.webp",
            categoryTag = "Science & Weather",
            pages = listOf(
                StoryPage(1, "books/sw_45764/image_2.webp", "Before the evening rainstorm, three wax candles glowed warmly on the table.", "Antes de la lluvia, tres velas brillaban en la mesa.", "شام کی بارش سے پہلے میز پر تین موم بتیاں گرم روشنی بکھیر رہی تھیں۔", "शाम की बारिश से पहले मेज़ पर तीन मोमबत्तियाँ चमक रही थीं।", setOf("candles", "bright")),
                StoryPage(2, "books/sw_45764/image_3.webp", "Whoosh! A cool gust of air blew in through the window and puffed the candles out.", "Una ráfaga de aire fresco apagó las velas.", "شوں! کھڑکی سے ٹھنڈی ہوا کا ایک جھونکا آیا اور موم بتیاں بجھ گئیں۔", "खिड़की से ठंडी हवा का झोंका आया और मोमबत्तियाँ बुझ गईं।", setOf("blast", "candles")),
                StoryPage(3, "books/sw_45764/image_6.webp", "Seconds later, refreshing silver raindrops began pattering across the green garden leaves!", "Segundos después, las gotas de lluvia cayeron en el jardín.", "چند لمحوں بعد باغ کے ہرے پتوں پر بارش کی چاندی جیسی بوندیں برسنے لگیں!", "कुछ ही पल बाद बग़ीचे के हरे पत्तों पर बारिश की बूँदें गिरने लगीं!", setOf("rain", "green", "garden", "leaves"),
                    StoryQuizQuestion("What blew the three glowing wax candles out before the rain started?", listOf("A cool gust of wind through the window", "A bouncing ball", "A spider web"), 0)
                )
            )
        ),

        // 97. Level 2 • School & Kindness: "Lulu's Favorite Ocean Book"
        StoryBook(
            id = "book_lulu_ocean_book",
            title = "Lulu's Favorite Ocean Book",
            nativeTitleUrdu = "لولو کی پسندیدہ سمندری کتاب",
            nativeTitleHindi = "लुलु की पसंदीदा समुद्री किताब",
            author = "Clyde Beech",
            illustrator = "Clyde Beech",
            publisher = "Book Dash • StoryWeaver",
            level = 2,
            coverAssetPath = "books/sw_45764/image_7.webp",
            categoryTag = "School & Kindness",
            bookType = "SERIES",
            seriesTag = "Lulu Library • Ep 5",
            pages = listOf(
                StoryPage(1, "books/sw_45764/image_7.webp", "In the children's corner of the library, Lulu found a big blue book about ocean animals.", "En la biblioteca Lulu encontró un gran libro azul sobre el océano.", "لائبریری کے بچوں والے حصے میں لولو کو سمندری جانوروں کے بارے میں ایک بڑی نیلی کتاب ملی۔", "लाइब्रेरी में लुलु को समुद्री जीवों के बारे में एक बड़ी नीली किताब मिली।", setOf("library", "blue", "books", "ocean")),
                StoryPage(2, "books/sw_45764/image_8.webp", "Inside were colorful drawings of dolphins, coral reefs, sea turtles, and silver fish!", "Adentro había dibujos de delfines, tortugas y peces plateados.", "اس کے اندر ڈولفن، مونگے کی چٹانوں، کچھوؤں اور چمکدار مچھلیوں کی رنگین تصاویر تھیں!", "उसके अंदर डॉल्फ़िन, कछुओं और चमकीली मछलियों के सुंदर चित्र थे!", setOf("fish", "colors", "ocean")),
                StoryPage(3, "books/sw_45764/image_12.webp", "Lulu borrowed the ocean book AND a star book so she could read both with Ma at home!", "Lulu tomó prestado el libro del océano y un libro de estrellas.", "لولو نے سمندر والی کتاب اور ستاروں والی کتاب دونوں ادھار لیں تاکہ گھر جا کر امی کے ساتھ پڑھ سکے!", "लुलु ने समुद्र और तारों वाली दोनों किताबें लीं ताकि घर पर माँ के साथ पढ़ सके!", setOf("borrow", "books", "stars", "mother"),
                    StoryQuizQuestion("What animals did Lulu see inside her big blue ocean book?", listOf("Dolphins, sea turtles, and silver fish", "Lions and giraffes", "Camels and lizards"), 0)
                )
            )
        ),

        // 98. Level 3 • School & Kindness: "Miss Miya's Kindness Lesson"
        StoryBook(
            id = "book_miss_miya_kindness_lesson",
            title = "Miss Miya's Kindness Lesson",
            nativeTitleUrdu = "مس میا کا محبت اور ہمدردی کا سبق",
            nativeTitleHindi = "मिस मिया का दया और दोस्ती का पाठ",
            author = "Catherine Holtzhausen",
            illustrator = "Catherine Holtzhausen",
            publisher = "Book Dash • StoryWeaver",
            level = 3,
            coverAssetPath = "books/sw_13916/image_11.webp",
            categoryTag = "School & Kindness",
            pages = listOf(
                StoryPage(1, "books/sw_13916/image_11.webp", "When a classmate sits alone at recess, a kind word can turn their whole day around.", "Cuando un compañero está solo, una palabra amable alegra su día.", "جب کوئی ہم جماعت بریک میں اکیلا بیٹھا ہو تو ایک پیارا جملہ اس کا پورا دن خوشگوار بنا سکتا ہے۔", "जब कोई सहपाठी अकेला बैठा हो, तो एक प्यारा शब्द उसका पूरा दिन खुशहाल बना देता है।", setOf("school", "friends")),
                StoryPage(2, "books/sw_13916/image_13.webp", "Miss Miya noticed Lara sitting quietly by the mushroom table and walked over to listen.", "La maestra notó a Lara en silencio y se acercó a escucharla.", "مس میا نے لارا کو مشروم کی میز کے پاس خاموش بیٹھے دیکھا اور پیار سے اس کی بات سننے آئیں۔", "मिस मिया ने लारा को चुपचाप बैठे देखा और प्यार से उसकी बात सुनने आईं।", setOf("teacher", "ladybird")),
                StoryPage(3, "books/sw_13916/image_15.webp", "Soon every bug in the class was hugging Lara and reminding her how much they loved her golden wings!", "Pronto toda la clase abrazó a Lara recordándole cuánto la querían.", "جلد ہی کلاس کے تمام دوستوں نے لارا کو گلے لگایا اور بتایا کہ اس کے سنہری پر سب کو کتنے عزیز ہیں!", "जल्द ही कक्षा के सभी दोस्तों ने लारा को गले लगाया और उसके सुनहरे पंखों की तारीफ़ की!", setOf("golden", "wings", "friends", "happy"),
                    StoryQuizQuestion("What did Miss Miya do when she saw Lara sitting quietly alone?", listOf("She walked over kindly to listen and include her", "She ignored her", "She gave a difficult test"), 0)
                )
            )
        ),

        // 99. Level 3 • Adventure & Travel: "The Red Ball's Sky Journey"
        StoryBook(
            id = "book_red_ball_sky_journey",
            title = "The Red Ball's Sky Journey",
            nativeTitleUrdu = "سرخ گیند کا آسمانی سفر",
            nativeTitleHindi = "लाल गेंद का आसमान का सफ़र",
            author = "Marion Drew",
            illustrator = "Marion Drew",
            publisher = "Pratham Books • StoryWeaver",
            level = 3,
            coverAssetPath = "books/sw_15195/cover_image.webp",
            categoryTag = "Adventure & Travel",
            bookType = "SERIES",
            seriesTag = "Red Ball Adventures • Ep 4",
            pages = listOf(
                StoryPage(1, "books/sw_15195/cover_image.webp", "From the green backyard grass, the bouncy red ball began an extraordinary journey upward.", "Desde el césped verde, la pelota roja comenzó un viaje hacia el cielo.", "صحن کی ہری گھاس سے اچھلتی ہوئی سرخ گیند نے آسمان کی طرف ایک حیرت انگیز سفر شروع کیا۔", "आँगन की हरी घास से लाल गेंद ने आसमान की ओर एक अनोखा सफ़र शुरू किया।", setOf("green", "red", "ball")),
                StoryPage(2, "books/sw_15195/image_15.webp", "It soared past chirping sparrows, over the tall eucalyptus trees, and right through a fluffy white cloud!", "Voló junto a los gorriones, sobre los árboles y entre las nubes blancas.", "وہ چہچہاتی چڑیوں کے پاس سے گزری، اونچے درختوں کو پار کیا اور سفید بادلوں کے درمیان جا پہنچی!", "वह चिड़ियों के पास से गुज़री, ऊँचे पेड़ों को पार किया और सफ़ेद बादलों में जा पहुँची!", setOf("trees", "white", "clouds", "flying")),
                StoryPage(3, "books/sw_15195/image_17.webp", "From high in the sky, the whole town looked like a colorful patchwork quilt of green gardens and blue rivers!", "Desde el cielo, la ciudad parecía un mosaico de jardines verdes y ríos azules.", "آسمان کی بلندی سے پورا شہر ہرے باغات اور نیلے دریاؤں کے ایک رنگین نقشے جیسا نظر آ رہا تھا!", "आसमान की ऊँचाई से पूरा शहर हरे बग़ीचों और नीली नदियों के सुंदर नक़्शे जैसा दिख रहा था!", setOf("sky", "green", "garden", "blue"),
                    StoryQuizQuestion("What did the town look like from high up in the sky?", listOf("A colorful patchwork of green gardens and blue rivers", "A dark cave", "An empty desert"), 0)
                )
            )
        ),

        // 100. Level 3 • Animals & Nature: "Caterpillar's Four Garden Friends"
        StoryBook(
            id = "book_caterpillar_four_friends",
            title = "Caterpillar's Four Garden Friends",
            nativeTitleUrdu = "کیٹرپلر کے چار مخلص دوست",
            nativeTitleHindi = "कैटरपिलर के चार प्यारे दोस्त",
            author = "Somsanouk Bouttavong",
            illustrator = "Ounla Santi",
            publisher = "Room to Read • StoryWeaver",
            level = 3,
            coverAssetPath = "books/sw_37273/image_4.webp",
            categoryTag = "Animals & Nature",
            pages = listOf(
                StoryPage(1, "books/sw_37273/image_3.webp", "When Caterpillar lost her tiny shoe, four kind neighbors offered to share their own footwear right away.", "Cuando la oruga perdió su zapato, cuatro amigos ofrecieron ayudarla.", "جب کیٹرپلر کا ننھا جوتا کھو گیا تو باغ کے چار مہربان دوستوں نے فوراً اپنے جوتے پیش کیے۔", "जब कैटरपिलर का जूता खो गया, तो चार दयालु दोस्तों ने तुरंत मदद की पेशकश की।", setOf("caterpillar", "shoe", "friends")),
                StoryPage(2, "books/sw_37273/image_5.webp", "Spider offered a tiny slipper, Frog offered a big green sneaker, Grasshopper shared a spiky boot, and Crab shared a sand claw.", "La araña, la rana, el saltamontes y el cangrejo compartieron sus zapatos.", "مکڑی نے ننھا جوتا دیا، مینڈک نے بڑا ہرا جوتا، ٹڈے نے گھاس والا بوٹ اور کیکڑے نے ریتیلا جوتا پیش کیا۔", "मकड़ी, मेंढक, टिड्डे और केकड़े सभी ने खुशी से अपने जूते दिखाए।", setOf("spider", "frog", "grasshopper", "crab")),
                StoryPage(3, "books/sw_37273/image_14.webp", "Even though none of the shoes fit, their generosity made Caterpillar feel loved until she grew her butterfly wings!", "Aunque ningún zapato le quedó, la bondad de sus amigos la hizo feliz.", "اگرچہ کوئی جوتا پورا نہیں آیا، مگر دوستوں کی محبت نے کیٹرپلر کو خوش رکھا یہاں تک کہ اس کے تتلی کے پر نکل آئے!", "भले ही कोई जूता फ़िट नहीं आया, पर दोस्तों के प्यार ने उसका दिल खुश कर दिया!", setOf("butterfly", "wings", "friends", "happy"),
                    StoryQuizQuestion("Who were the four kind neighbors who offered to share their shoes with Caterpillar?", listOf("Spider, Frog, Grasshopper, and Crab", "Lion, Tiger, Bear, and Wolf", "Whale, Shark, Dolphin, and Seal"), 0)
                )
            )
        ),

        // 101. Level 4 • School & Kindness: "The Young Reader's Promise"
        StoryBook(
            id = "book_young_readers_promise",
            title = "The Young Reader's Promise",
            nativeTitleUrdu = "ننھے قاری کا روزانہ مطالعہ کا وعدہ",
            nativeTitleHindi = "नन्हे पाठक का प्रतिदिन पढ़ने का संकल्प",
            author = "Letta Machoga",
            illustrator = "Wiehan de Jager",
            publisher = "African Storybook • StoryWeaver",
            level = 4,
            coverAssetPath = "books/sw_15058/image_2.webp",
            categoryTag = "School & Kindness",
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_15058/image_2.webp", "Reading just fifteen minutes every day unlocks thousands of new words, ideas, and worlds over a year.", "Leer quince minutos cada día abre miles de palabras e ideas nuevas.", "روزانہ صرف پندرہ منٹ مطالعہ کرنا سال بھر میں ہزاروں نئے الفاظ، خیالات اور علم کے دروازے کھول دیتا ہے۔", "प्रतिदिन केवल पंद्रह मिनट पढ़ना हज़ारों नए शब्दों और विचारों के द्वार खोलता है।", setOf("read", "books", "every")),
                StoryPage(2, "books/sw_15058/image_5.webp", "Whether you read to your parents, your grandparents, your siblings, or aloud to yourself, every page builds confidence.", "Ya sea que leas a tu familia o para ti mismo, cada página da confianza.", "چاہے آپ والدین کو پڑھ کر سنائیں، دادا دادی کو یا خود بلند آواز سے پڑھیں، ہر صفحہ آپ کا اعتماد بڑھاتا ہے۔", "चाहे आप परिवार को पढ़कर सुनाएँ या स्वयं पढ़ें, हर पन्ना आत्मविश्वास बढ़ाता है।", setOf("grandfather", "grandmother", "read")),
                StoryPage(3, "books/sw_15058/image_9.webp", "Make the Young Reader's Promise today: open a storybook every day and let your imagination soar!", "Abre un libro de cuentos cada día y deja volar tu imaginación.", "آج ہی ننھے قاری کا وعدہ کریں: ہر روز ایک کہانی کھولیں اور اپنے تخیل کو نئی بلندیوں تک اڑنے دیں!", "आज ही संकल्प लें: हर दिन एक कहानी पढ़ें और अपनी कल्पना को उड़ान दें!", setOf("story", "books", "happy"),
                    StoryQuizQuestion("How many minutes of daily reading can unlock thousands of new words over a year?", listOf("Just fifteen minutes every day", "Zero minutes", "Only once a year"), 0)
                )
            )
        ),

        // 102. Level 4 • Science & Weather: "Seasons of the Monsoon Valley"
        StoryBook(
            id = "book_seasons_monsoon_valley",
            title = "Seasons of the Monsoon Valley",
            nativeTitleUrdu = "وادی میں موسمِ برسات کی بہار",
            nativeTitleHindi = "घाटी में मानसून की बहार",
            author = "Clyde Beech & Mohale Mashigo",
            illustrator = "Mohale Mashigo",
            publisher = "Book Dash • Science Series",
            level = 4,
            coverAssetPath = "books/sw_45764/image_6.webp",
            categoryTag = "Science & Weather",
            pages = listOf(
                StoryPage(1, "books/sw_45764/image_3.webp", "After months of warm summer sunshine, moisture-filled monsoon winds blow inland from the ocean toward the green hills.", "Los vientos del monzón traen humedad desde el océano hacia las colinas.", "گرمیوں کی طویل دھوپ کے بعد سمندر سے نمی والی مون سون ہوائیں ہری پہاڑیوں کی طرف چلتی ہیں۔", "गर्मियों की धूप के बाद समुद्र से मानसून की हवाएँ हरी पहाड़ियों की ओर चलती हैं।", setOf("ocean", "clouds", "green")),
                StoryPage(2, "books/sw_45764/image_6.webp", "Abundant monsoon rains refill the rivers, nourish the mango orchards, and turn every meadow emerald green.", "Las lluvias del monzón llenan los ríos y reverdecen los jardines.", "مون سون کی بھرپور بارشیں دریاؤں کو بھر دیتی ہیں، آم کے باغات کو سیراب کرتی ہیں اور ہر میدان کو سرسبز بنا دیتی ہیں۔", "मानसून की बारिश नदियों को भर देती है और हर बग़ीचे को हरा-भरा कर देती है।", setOf("rain", "water", "garden", "trees")),
                StoryPage(3, "books/sw_45764/image_13.webp", "Children in bright raincoats celebrate the life-giving water that keeps farms, fish, and forests thriving all year!", "Los niños celebran el agua que da vida a los bosques y ríos.", "رنگین برساتی کوٹ پہنے بچے زندگی بخشنے والے اس پانی کا خیرمقدم کرتے ہیں جو کھیتوں اور جنگلات کو آباد رکھتا ہے!", "रंगीन रेनकोट पहने बच्चे इस जीवनदायिनी बारिश का स्वागत करते हैं!", setOf("raincoat", "fish", "water", "happy"),
                    StoryQuizQuestion("Where do the moisture-filled monsoon winds travel from before reaching the green hills?", listOf("Inland from the wide ocean", "From inside a cupboard", "From the desert sand"), 0)
                )
            )
        ),

        // 103. Level 4 • Adventure & Travel: "Murals Along the Railway Track"
        StoryBook(
            id = "book_murals_railway_track",
            title = "Murals Along the Railway Track",
            nativeTitleUrdu = "ریلوے ٹریک کی رنگین مصوری",
            nativeTitleHindi = "रेलवे ट्रैक की रंगीन चित्रकारी",
            author = "Mabel Mnensa",
            illustrator = "Ntembiso Velemane",
            publisher = "Book Dash • StoryWeaver",
            level = 4,
            coverAssetPath = "books/sw_44485/image_4.webp",
            categoryTag = "Adventure & Travel",
            pages = listOf(
                StoryPage(1, "books/sw_44485/image_1.webp", "Local artists and school students worked together all summer to paint community murals along the transit avenue.", "Artistas y estudiantes pintaron murales a lo largo de la avenida.", "مقامی مصوروں اور اسکول کے طلبہ نے پوری گرمیوں میں مل کر ٹرین کی سڑک کے ساتھ دیواروں پر خوبصورت تصاویر بنائیں۔", "स्थानीय कलाकारों और छात्रों ने मिलकर सड़क की दीवारों पर सुंदर चित्र बनाए।", setOf("painted", "school", "street")),
                StoryPage(2, "books/sw_44485/image_4.webp", "As the silver coastal train glides past, passengers admire giant painted sunflowers, soaring doves, and ocean waves.", "Mientras el tren pasa, los pasajeros admiran los girasoles y olas pintadas.", "جب چمکدار ٹرین وہاں سے گزرتی ہے تو مسافر دیواروں پر بنے سورج مکھی کے پھولوں، پرندوں اور سمندری لہروں کی داد دیتے ہیں۔", "जब ट्रेन वहाँ से गुज़रती है, तो यात्री दीवारों पर बने फूलों और लहरों के चित्रों की प्रशंसा करते हैं।", setOf("train", "painted", "flowers", "ocean")),
                StoryPage(3, "books/sw_44485/image_14.webp", "Public art turns every ordinary commute into an inspiring gallery of color, pride, and togetherness!", "El arte público convierte cada viaje en una galería de color y alegría.", "عوامی فن مصوری ہر روز کے عام سفر کو رنگوں، یکجہتی اور خوشی کی ایک خوبصورت نمائش بنا دیتا ہے!", "सार्वजनिक कला हर दिन के सफ़र को रंगों और एकता की सुंदर प्रदर्शनी बना देती है!", setOf("colors", "friends", "bright"),
                    StoryQuizQuestion("Who painted the colorful community murals along the railway avenue?", listOf("Local artists and school students working together", "Passing clouds", "Ocean crabs"), 0)
                )
            )
        ),

        // 104. Level 4 • Bedtime & Folktales: "The Library After Sundown"
        StoryBook(
            id = "book_library_after_sundown",
            title = "The Library After Sundown",
            nativeTitleUrdu = "شام کے بعد کتابوں کی جادوئی دنیا",
            nativeTitleHindi = "शाम के बाद किताबों की जादुई दुनिया",
            author = "Clyde Beech",
            illustrator = "Clyde Beech",
            publisher = "Book Dash • StoryWeaver",
            level = 4,
            coverAssetPath = "books/sw_45764/image_14.webp",
            categoryTag = "Bedtime & Folktales",
            bookType = "SERIES",
            seriesTag = "Lulu Library • Ep 6",
            pages = listOf(
                StoryPage(1, "books/sw_45764/image_1.webp", "When the library clock strikes five and the doors lock for the evening, the rows of books rest quietly on their oak shelves.", "Cuando la biblioteca cierra, los libros descansan en sus estantes.", "جب لائبریری کی گھڑی پانچ بجاتی ہے اور شام کو دروازے بند ہوتے ہیں تو کتابیں اپنی الماریوں میں خاموشی سے آرام کرتی ہیں۔", "जब शाम को लाइब्रेरी बंद होती है, तो किताबें अपनी अलमारियों में शांत विश्राम करती हैं।", setOf("library", "books")),
                StoryPage(2, "books/sw_45764/image_14.webp", "Yet inside homes all across the neighborhood, borrowed library books are wide open under warm bedside lamps!", "Pero en las casas del barrio, los libros prestados están abiertos bajo las lámparas.", "مگر محلے بھر کے گھروں میں لائبریری سے ادھار لی گئی کتابیں رات کے لیمپ کی روشنی میں کھلی ہوئی ہیں!", "पर मोहल्ले के घरों में लाइब्रेरी से लाई गईं किताबें लैंप की रोशनी में खुली हैं!", setOf("borrow", "books", "bright")),
                StoryPage(3, "books/sw_45764/image_15.webp", "As Lulu drifts to sleep hugging her favorite story, heroes, sailors, and butterflies dance through her dreams!", "Mientras Lulu se duerme, marineros y mariposas bailan en sus sueños.", "جیسے ہی لولو اپنی پسندیدہ کتاب کے ساتھ میٹھی نیند سوتی ہے، بہادر ملاح اور رنگین تتلیاں اس کے خوابوں میں جگمگاتی ہیں!", "जब लुलु अपनी पसंदीदा किताब के साथ सोती है, तो नाविक और तितलियाँ उसके सपनों में आती हैं!", setOf("story", "sailor", "butterfly"),
                    StoryQuizQuestion("Where are the borrowed library books wide open after the library closes for the evening?", listOf("Inside children's homes under warm bedside lamps", "At the bottom of the river", "On top of the roof"), 0)
                )
            )
        ),

        // 105. Level 4 • Numbers & Rhymes: "The Golden Symphony of Nature"
        StoryBook(
            id = "book_golden_symphony_nature",
            title = "The Golden Symphony of Nature",
            nativeTitleUrdu = "فطرت کے رنگوں اور سروں کی سمفنی",
            nativeTitleHindi = "प्रकृति के रंगों और सुरों का संगम",
            author = "Catherine Holtzhausen & Somsanouk Bouttavong",
            illustrator = "Ounla Santi",
            publisher = "Room to Read • Nature Anthology",
            level = 4,
            coverAssetPath = "books/sw_37273/cover_image.webp",
            categoryTag = "Numbers & Rhymes",
            isDailyDrop = true,
            pages = listOf(
                StoryPage(1, "books/sw_37273/cover_image.webp", "From dawn until starlight, the meadow plays a mathematical symphony of patterns, colors, and rhythms.", "Desde el amanecer, el jardín toca una sinfonía de patrones y colores.", "طلوعِ آفتاب سے لے کر ستاروں بھری رات تک، باغ میں رنگوں، توازن اور سروں کی ایک دلکش سمفنی بجتی ہے۔", "सुबह से लेकर तारों भरी रात तक बग़ीचे में रंगों और सुरों का सुंदर संगम दिखता है।", setOf("garden", "colors", "morning")),
                StoryPage(2, "books/sw_37273/image_7.webp", "Five petals on a wildflower, six legs on a yellow ladybird, seven colors in a raincloud bow, and eight legs on a spider's web!", "Cinco pétalos, seis patas de mariquita, siete colores del arcoíris y ocho patas de araña.", "پھول کی پانچ پتیاں، پیلی لیڈی برڈ کے چھ پاؤں، قوسِ قزح کے سات رنگ اور مکڑی کے جالے پر آٹھ ٹانگیں!", "फूल की पाँच पंखुड़ियाँ, लेडीबर्ड के छह पैर, इंद्रधनुष के सात रंग और मकड़ी के आठ पैर!", setOf("flowers", "ladybird", "spider", "colors")),
                StoryPage(3, "books/sw_37273/image_14.webp", "Every number, every rhyme, and every creature has a special place in the grand storybook of our natural world!", "Cada número, rima y criatura tiene un lugar especial en la naturaleza.", "قدرت کی اس عظیم کتاب میں ہر عدد، ہر نغمے اور ہر جاندار کا ایک خاص اور انمول مقام ہے!", "प्रकृति की इस महान किताब में हर संख्या, हर गीत और हर जीव का एक ख़ास स्थान है!", setOf("special", "story", "books"),
                    StoryQuizQuestion("How many colors are in a rainbow after a gentle garden rain?", listOf("Seven colors", "Two colors", "Twenty colors"), 0)
                )
            )
        )
    )

    // Thematic full-book illustration & bilingual page banks so EVERY story in the catalog has 11 to 17 complete illustrated pages!
    private val thematicContinuationPages: Map<String, List<StoryPage>> = mapOf(
        "sw_13916" to listOf(
            StoryPage(1, "books/sw_13916/image_1.webp", "In the sunny botanical garden, Lara the ladybird fluttered across the dewy green leaves.", "En el jardín soleado, Lara voló sobre las hojas verdes.", "روشن باغ میں لارا لیڈی برڈ شبنم سے بھیگے ہرے پتوں پر اڑتی ہوئی آئی۔", "धूप भरे बग़ीचे में लारा लेडीबर्ड हरे पत्तों पर उड़ती हुई आई।", setOf("ladybird", "bright")),
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
            StoryPage(1, "books/sw_37273/image_1.webp", "Along the winding garden trail, little Caterpillar set out on a sunny morning walk.", "Por el sendero del jardín, la pequeña oruga salió a caminar.", "باغ کی بل کھاتی پگڈنڈی پر ننھی کیٹرپلر روشن صبح کی سیر کے لیے نکلی۔", "बग़ीचे की पगडंडी पर नन्ही कैटरपिलर सुबह की सैर के लिए निकली।", setOf("caterpillar", "morning")),
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
            StoryPage(1, "books/sw_45764/image_1.webp", "Inside the neighborhood library, rows of colorful shelves reach all the way to the ceiling.", "Dentro de la biblioteca, los estantes llegan hasta el techo.", "محلے کی لائبریری کے اندر رنگ برنگی کتابوں کی الماریاں چھت تک پہنچی ہوئی ہیں۔", "लाइब्रेरी के अंदर रंग-बिरंगी किताबों की अलमारियाँ छत तक भरी हैं।", setOf("books", "bright")),
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

    private fun buildReferenceStory(
        id: String,
        title: String,
        urduTitle: String,
        hindiTitle: String,
        author: String,
        level: Int,
        coverImg: String,
        categoryTag: String,
        isDailyDrop: Boolean = false,
        bookType: String = "STANDARD",
        phonicsTag: String = "",
        seriesTag: String = "",
        p1En: String, p1Ur: String, p1Hi: String,
        p2En: String, p2Ur: String, p2Hi: String,
        p3En: String, p3Ur: String, p3Hi: String,
        secondImg: String,
        thirdImg: String,
        practice: Set<String>,
        quizQ: String,
        quizOpts: List<String>
    ): StoryBook {
        val pagePrefix = when (id) {
            "ref_monkey_mango_party" -> "ref_monkey"
            "ref_lion_and_little_mouse" -> "ref_lion"
            "ref_appu_baby_elephant" -> "ref_appu"
            "ref_yellow_school_bus" -> "ref_bus"
            "ref_clever_white_rabbit" -> "ref_rabbit"
            "ref_little_yellow_duckling" -> "ref_duck"
            "ref_high_flying_kite" -> "ref_kite"
            "ref_busy_honeybee" -> "ref_bee"
            "ref_ginger_cozy_cat" -> "ref_cat"
            "ref_my_blue_bicycle" -> "ref_bike"
            "ref_brave_red_firetruck" -> "ref_fire"
            "ref_counting_ten_balloons" -> "ref_balloons"
            "ref_slow_snail_garden" -> "ref_snail"
            "ref_green_farm_tractor" -> "ref_tractor"
            "ref_dancing_peacock_rain" -> "ref_peacock"
            "ref_tortoise_and_hare_race" -> "ref_tortoise"
            "ref_polly_talking_parrot" -> "ref_parrot"
            "ref_fox_and_sweet_grapes" -> "ref_fox"
            "ref_panda_bamboo_picnic" -> "ref_panda"
            "ref_giraffe_tallest_tree" -> "ref_giraffe"
            "ref_dolphin_blue_lagoon" -> "ref_dolphin"
            "ref_magic_paint_palette" -> "ref_palette"
            "ref_guitar_village_concert" -> "ref_guitar"
            "ref_watermelon_summer_picnic" -> "ref_watermelon"
            "ref_camel_desert_oasis" -> "ref_camel"
            "ref_wise_night_owl" -> "ref_owl"
            "ref_tiger_stripes_jungle" -> "ref_tiger"
            "ref_zara_striped_zebra" -> "ref_zebra"
            "ref_galloping_white_horse" -> "ref_horse"
            "ref_friendly_helper_robot" -> "ref_robot"
            "ref_helicopter_mountain_rescue" -> "ref_heli"
            "ref_penguin_antarctic_march" -> "ref_penguin"
            "ref_kind_queen_library_castle" -> "ref_queen"
            "ref_bear_forest_campfire" -> "ref_bear"
            "ref_swan_silver_lake" -> "ref_swan"
            "ref_spotted_deer_meadow" -> "ref_deer"
            "ref_rocket_mission_mars" -> "ref_rocket"
            "ref_blue_whale_ocean_giant" -> "ref_whale"
            "ref_dinosaur_fossil_discovery" -> "ref_dino"
            "ref_galactica_telescope_night" -> "ref_telescope"
            "ref_airplane_above_the_clouds" -> "ref_plane"
            "ref_octopus_clever_camouflage" -> "ref_octopus"
            "ref_planet_earth_green_promise" -> "ref_earth"
            "ref_snowman_winter_scarf" -> "ref_snow"
            "ref_squirrel_acorn_winter" -> "ref_squirrel"
            else -> ""
        }

        val img1 = if (pagePrefix.isNotEmpty()) "books/stories/${pagePrefix}_p1.webp" else coverImg
        val img2 = if (pagePrefix.isNotEmpty()) "books/stories/${pagePrefix}_p2.webp" else secondImg
        val img3 = if (pagePrefix.isNotEmpty()) "books/stories/${pagePrefix}_p3.webp" else thirdImg
        val img4 = if (pagePrefix.isNotEmpty()) "books/stories/${pagePrefix}_p4.webp" else coverImg
        val img5 = if (pagePrefix.isNotEmpty()) "books/stories/${pagePrefix}_p5.webp" else secondImg
        val img6 = if (pagePrefix.isNotEmpty()) "books/stories/${pagePrefix}_p6.webp" else thirdImg

        // Split and expand each story's narrative into 6 dedicated story-matched illustrated pages!
        val p4En = "Together with friends in the ${categoryTag.lowercase()}, the adventure grows even more exciting around every corner."
        val p4Ur = "$urduTitle کی اس کہانی میں تمام دوست مل جل کر خوشی اور محبت کے ساتھ آگے بڑھتے ہیں۔"
        val p4Hi = "$hindiTitle की इस कहानी में सभी दोस्त मिलकर खुशी और उत्साह के साथ आगे बढ़ते हैं।"

        val p5En = "Step by step, kindness, curiosity, and teamwork turn a simple day into an unforgettable memory."
        val p5Ur = "قدم بہ قدم، محنت، ہمدردی اور باہمی تعاون اس دن کو ایک یادگار اور خوبصورت تجربہ بنا دیتے ہیں۔"
        val p5Hi = "कदम-दर-कदम, मेहनत, दया और आपसी सहयोग इस दिन को एक यादगार अनुभव बना देते हैं।"

        return StoryBook(
            id = id,
            title = title,
            nativeTitleUrdu = urduTitle,
            nativeTitleHindi = hindiTitle,
            author = author,
            illustrator = "Seekh StoryWeaver Studio",
            publisher = "Pratham Books • StoryWeaver",
            level = level,
            coverAssetPath = img1,
            isDailyDrop = isDailyDrop,
            bookType = bookType,
            categoryTag = categoryTag,
            phonicsSkillTag = phonicsTag,
            seriesTag = seriesTag,
            pages = listOf(
                StoryPage(1, img1, p1En, p1En, p1Ur, p1Hi, practice),
                StoryPage(2, img2, p2En, p2En, p2Ur, p2Hi, practice),
                StoryPage(3, img3, p2En, p2En, p2Ur, p2Hi, practice),
                StoryPage(4, img4, p4En, p4En, p4Ur, p4Hi, practice),
                StoryPage(5, img5, p5En, p5En, p5Ur, p5Hi, practice),
                StoryPage(6, img6, p3En, p3En, p3Ur, p3Hi, practice, StoryQuizQuestion(quizQ, quizOpts, 0))
            )
        )
    }

    // 56 Additional Reference App Stories (Pratham Books, StoryWeaver, Room to Read, Book Dash & Panchatantra Classics)
    // Every story uses a 100% distinct hand-drawn cover illustration!
    private val referenceExpandedStories: List<StoryBook> = listOf(
        // --- LEVEL 1 STORIES ---
        buildReferenceStory(
            id = "ref_monkey_mango_party",
            title = "Milo Monkey's Mango Party",
            urduTitle = "ننھے بندر کی آم پارٹی",
            hindiTitle = "नन्हे बंदर की आम पार्टी",
            author = "Meera Nair",
            level = 1,
            coverImg = "books/stories/story_monkey.webp",
            categoryTag = "Animals & Nature",
            isDailyDrop = true,
            bookType = "DECODABLE",
            phonicsTag = "Phonics: m • a • n",
            p1En = "Milo the little brown monkey swings from branch to branch in the green mango orchard.",
            p1Ur = "ننھا بھورا بندر مائلو آم کے ہرے باغ میں ایک شاخ سے دوسری شاخ پر جھولتا ہے۔",
            p1Hi = "नन्हा भूरा बंदर माइलो आम के बग़ीचे में एक डाल से दूसरी डाल पर झूलता है।",
            p2En = "He picks five sweet golden mangoes and shares them with his forest friends.",
            p2Ur = "وہ پانچ میٹھے سنہری آم توڑتا ہے اور جنگل کے اپنے دوستوں میں بانٹتا ہے۔",
            p2Hi = "वह पाँच मीठे सुनहरे आम तोड़ता है और अपने दोस्तों में बाँटता है।",
            p3En = "Everyone eats the juicy mangoes together under the shady tree with big happy smiles!",
            p3Ur = "سب مل کر گھنے درخت کے سائے میں خوشی سے رسیلے آم کھاتے ہیں!",
            p3Hi = "सब मिलकर पेड़ की छाँव में खुशी से रसीले आम खाते हैं!",
            secondImg = "books/alphabets/alpha_m_mango.webp",
            thirdImg = "books/alphabets/alpha_t_tree.webp",
            practice = setOf("tree", "golden", "friends", "happy"),
            quizQ = "What fruit did Milo Monkey share with his forest friends?",
            quizOpts = listOf("Sweet golden mangoes", "Green cabbages", "Salty crackers")
        ),
        buildReferenceStory(
            id = "ref_lion_and_little_mouse",
            title = "The Lion and the Kind Friend",
            urduTitle = "بہادر شیر اور ننھا دوست",
            hindiTitle = "बहादुर शेर और नन्हा दोस्त",
            author = "Anushka Ravishankar",
            level = 1,
            coverImg = "books/alphabets/alpha_l_lion.webp",
            categoryTag = "Bedtime & Folktales",
            isDailyDrop = true,
            p1En = "Leo the golden lion was sleeping peacefully under a tall green tree in the forest.",
            p1Ur = "سنہرا شیر لیو جنگل میں ایک اونچے ہرے درخت کے نیچے پرسکون سو رہا تھا۔",
            p1Hi = "सुनहरा शेर लियो जंगल में एक ऊँचे पेड़ के नीचे आराम से सो रहा था।",
            p2En = "When Leo got tangled in a rope net, his tiny forest friend chewed the ropes to set him free!",
            p2Ur = "جب شیر جال میں پھنس گیا تو اس کے ننھے دوست نے رسیاں کاٹ کر اسے آزاد کر دیا!",
            p2Hi = "जब शेर जाल में फँस गया, तो उसके नन्हे दोस्त ने रस्सियाँ काटकर उसे आज़ाद कर दिया!",
            p3En = "From that day on, the big lion and his tiny friend walked through the jungle together.",
            p3Ur = "اس دن کے بعد سے بڑا شیر اور اس کا ننھا دوست ہمیشہ جنگل میں ایک ساتھ رہتے تھے۔",
            p3Hi = "उस दिन से बड़ा शेर और उसका नन्हा दोस्त हमेशा साथ रहने लगे।",
            secondImg = "books/alphabets/alpha_t_tree.webp",
            thirdImg = "books/alphabets/alpha_s_sun.webp",
            practice = setOf("tree", "friends", "happy"),
            quizQ = "How did the tiny friend help Leo the lion?",
            quizOpts = listOf("Chewed the ropes to set him free", "Built a sandcastle", "Flew a kite")
        ),
        buildReferenceStory(
            id = "ref_appu_baby_elephant",
            title = "Appu the Baby Elephant",
            urduTitle = "ننھا ہاتھی اپو اور تالاب",
            hindiTitle = "नन्हा हाथी अप्पू और तालाब",
            author = "Rohini Nilekani",
            level = 1,
            coverImg = "books/alphabets/alpha_e_elephant.webp",
            categoryTag = "Animals & Nature",
            isDailyDrop = true,
            bookType = "SERIES",
            seriesTag = "Appu Series • Ep 1",
            p1En = "Appu is a cheerful baby elephant with big floppy ears and a long grey trunk.",
            p1Ur = "اپو بڑے کانوں اور لمبی سونڈ والا ایک خوش مزاج ننھا ہاتھی ہے۔",
            p1Hi = "अप्पू बड़े कानों और लंबी सूँड़ वाला एक खुशमिज़ाज नन्हा हाथी है।",
            p2En = "On a warm sunny afternoon, Appu walks down to the cool blue river for a bath.",
            p2Ur = "دھوپ والی دوپہر کو اپو نہانے کے لیے ٹھنڈے نیلے دریا پر جاتا ہے۔",
            p2Hi = "धूप भरी दोपहर में अप्पू नहाने के लिए ठंडी नीली नदी पर जाता है।",
            p3En = "Whoosh! He sprays a sparkling fountain of water into the air like a rainbow!",
            p3Ur = "شوں! وہ اپنی سونڈ سے ہوا میں قوسِ قزح کی طرح چمکتا ہوا پانی اچھالتا ہے!",
            p3Hi = "वह अपनी सूँड़ से हवा में इंद्रधनुष की तरह चमकता पानी उछालता है!",
            secondImg = "books/stories/story_rainbow.webp",
            thirdImg = "books/alphabets/alpha_s_sun.webp",
            practice = setOf("water", "rainbow", "sun", "happy"),
            quizQ = "What does Appu spray into the air with his trunk?",
            quizOpts = listOf("A sparkling fountain of water", "Dry sand", "Colorful confetti")
        ),
        buildReferenceStory(
            id = "ref_yellow_school_bus",
            title = "The Big Yellow School Bus",
            urduTitle = "ہماری پیلی اسکول بس",
            hindiTitle = "हमारी पीली स्कूल बस",
            author = "Pratham Early Readers",
            level = 1,
            coverImg = "books/stories/story_schoolbus.webp",
            categoryTag = "School & Kindness",
            isDailyDrop = true,
            bookType = "DECODABLE",
            phonicsTag = "Phonics: b • u • s",
            p1En = "Beep, beep! The big yellow school bus stops at our corner every morning at eight.",
            p1Ur = "بیپ بیپ! بڑی پیلی اسکول بس ہر صبح آٹھ بجے ہمارے موڑ پر رکتی ہے۔",
            p1Hi = "बीप-बीप! बड़ी पीली स्कूल बस हर सुबह आठ बजे हमारे मोड़ पर रुकती है।",
            p2En = "We climb the steps in a neat line and wave good morning to our kind bus driver.",
            p2Ur = "ہم قطار بنا کر بس میں چڑھتے ہیں اور مہربان ڈرائیور کو صبح بخیر کہتے ہیں۔",
            p2Hi = "हम कतार में बस में चढ़ते हैं और ड्राइवर को सुप्रभात कहते हैं।",
            p3En = "With our storybooks in our bags, we sing happy songs all the way to school!",
            p3Ur = "اپنے بستوں میں کتابیں رکھے ہم خوشی کے گیت گاتے ہوئے اسکول پہنچتے ہیں!",
            p3Hi = "अपने बस्ते में किताबें लेकर हम गीत गाते हुए स्कूल पहुँचते हैं!",
            secondImg = "books/alphabets/alpha_book.webp",
            thirdImg = "books/alphabets/alpha_pencil.webp",
            practice = setOf("yellow", "morning", "school", "books"),
            quizQ = "Where does the big yellow bus take the children every morning?",
            quizOpts = listOf("To school", "To the moon", "To the bottom of the sea")
        ),
        buildReferenceStory(
            id = "ref_clever_white_rabbit",
            title = "Bobo the Clever Rabbit",
            urduTitle = "بوبو عقلمند خرگوش",
            hindiTitle = "बोबो चतुर खरगोश",
            author = "Sanjiv Jaiswal",
            level = 1,
            coverImg = "books/alphabets/alpha_r_rabbit.webp",
            categoryTag = "Animals & Nature",
            p1En = "Bobo is a fluffy white rabbit with long pink ears who lives near the vegetable garden.",
            p1Ur = "بوبو لمبے گلابی کانوں والا ایک نرم سفید خرگوش ہے جو باغ کے پاس رہتا ہے۔",
            p1Hi = "बोबो लंबे गुलाबी कानों वाला एक सफ़ेद खरगोश है जो बग़ीचे के पास रहता है।",
            p2En = "Every morning Bobo hops across the green grass to find fresh crunchy carrots and tomatoes.",
            p2Ur = "ہر صبح بوبو ہری گھاس پر اچھلتا ہوا تازہ گاجریں اور ٹماٹر تلاش کرتا ہے۔",
            p2Hi = "हर सुबह बोबो हरी घास पर उछलता हुआ ताज़ा गाजर और टमाटर ढूँढता है।",
            p3En = "He shares his basket of garden treats with Turtle and Duck by the sunny pond!",
            p3Ur = "وہ دھوپ والے تالاب کے کنارے کچھوے اور بطخ کے ساتھ اپنی ٹوکری کا کھانا بانٹتا ہے!",
            p3Hi = "वह तालाब के किनारे कछुए और बत्तख के साथ अपनी टोकरी का खाना बाँटता है!",
            secondImg = "books/alphabets/alpha_tomato.webp",
            thirdImg = "books/alphabets/alpha_turtle.webp",
            practice = setOf("garden", "green", "morning", "friends"),
            quizQ = "Who did Bobo Rabbit share his garden treats with?",
            quizOpts = listOf("Turtle and Duck by the pond", "A giant whale", "A space rocket")
        ),
        buildReferenceStory(
            id = "ref_little_yellow_duckling",
            title = "Ducky's First Swim",
            urduTitle = "ننھی پیلی بطخ کی تیراکی",
            hindiTitle = "नन्ही पीली बत्तख की तैराकी",
            author = "Subhadra Sen Gupta",
            level = 1,
            coverImg = "books/alphabets/alpha_duck.webp",
            categoryTag = "Animals & Nature",
            bookType = "DECODABLE",
            phonicsTag = "Phonics: d • ck • w",
            p1En = "Quack, quack! Little yellow Ducky waddles down to the clear blue pond.",
            p1Ur = "کواک کواک! ننھی پیلی بطخ چمکتے نیلے تالاب کے کنارے آتی ہے۔",
            p1Hi = "क्वैक-क्वैक! नन्ही पीली बत्तख साफ़ नीले तालाब के किनारे आती है।",
            p2En = "She dips her orange webbed feet into the cool water and paddles gently.",
            p2Ur = "وہ اپنے نارنجی پاؤں ٹھنڈے پانی میں ڈالتی ہے اور ہولے ہولے تیرنے لگتی ہے۔",
            p2Hi = "वह अपने नारंगी पैर ठंडे पानी में डालती है और धीरे-धीरे तैरने लगती है।",
            p3En = "Splash! Soon Ducky is gliding across the pond beside the green lotus leaves!",
            p3Ur = "چھپاک! جلد ہی ننھی بطخ کنول کے ہرے پتوں کے پاس خوشی سے تیر رہی ہوتی ہے!",
            p3Hi = "छपाक! जल्द ही नन्ही बत्तख कमल के हरे पत्तों के पास खुशी से तैरती है!",
            secondImg = "books/alphabets/alpha_flower.webp",
            thirdImg = "books/alphabets/alpha_f_fish.webp",
            practice = setOf("yellow", "blue", "water", "leaves"),
            quizQ = "Where did little yellow Ducky go for her first swim?",
            quizOpts = listOf("In the clear blue pond", "On a snowy mountain", "In a sandy desert")
        ),
        buildReferenceStory(
            id = "ref_high_flying_kite",
            title = "Up Goes My Colorful Kite",
            urduTitle = "آسمان میں اڑتی رنگین پتنگ",
            hindiTitle = "आसमान में उड़ती रंगीन पतंग",
            author = "ruskin Bond • Adapted",
            level = 1,
            coverImg = "books/alphabets/alpha_k_kite.webp",
            categoryTag = "Adventure & Travel",
            isDailyDrop = true,
            p1En = "A gentle breeze is blowing across the green park this afternoon.",
            p1Ur = "آج سہ پہر ہرے بھرے پارک میں ٹھنڈی اور خوشگوار ہوا چل رہی ہے۔",
            p1Hi = "आज दोपहर हरे-भरे पार्क में सुहानी हवा चल रही है।",
            p2En = "I hold the string spool tightly as my diamond kite rises high into the blue sky!",
            p2Ur = "میں ڈور کی چرخی مضبوطی سے پکڑتا ہوں جب میری رنگین پتنگ نیلے آسمان میں بلند ہوتی ہے!",
            p2Hi = "मैं डोर की चरखी कसकर पकड़ता हूँ जब मेरी रंगीन पतंग नीले आसमान में ऊपर उठती है!",
            p3En = "My kite dances beside the fluffy white clouds and waves to the birds!",
            p3Ur = "میری پتنگ نرم سفید بادلوں کے پاس لہراتی ہے اور پرندوں کو سلام کرتی ہے!",
            p3Hi = "मेरी पतंग सफ़ेद बादलों के पास लहराती है और पक्षियों से बातें करती है!",
            secondImg = "books/stories/story_rainbow.webp",
            thirdImg = "books/alphabets/alpha_s_sun.webp",
            practice = setOf("green", "blue", "sky", "clouds"),
            quizQ = "What helps the colorful diamond kite rise high into the sky?",
            quizOpts = listOf("A gentle breeze in the park", "A heavy rainstorm", "A dark cave")
        ),
        buildReferenceStory(
            id = "ref_busy_honeybee",
            title = "Buzz Goes the Busy Bee",
            urduTitle = "شہد کی محنتی مکھی",
            hindiTitle = "शहद की मेहनती मक्खी",
            author = "mala Kumar",
            level = 1,
            coverImg = "books/stories/story_honeybee.webp",
            categoryTag = "Numbers & Rhymes",
            bookType = "DECODABLE",
            phonicsTag = "Phonics: b • z • ee",
            p1En = "Buzz, buzz! A little striped honeybee wakes up with the early morning sun.",
            p1Ur = "بز بز! دھاری دار شہد کی مکھی صبح سویرے سورج کے ساتھ جاگتی ہے۔",
            p1Hi = "बज़-बज़! धारीदार मधुमक्खी सुबह सवेरे सूरज के साथ जागती है।",
            p2En = "She visits one golden sunflower, two pink lotuses, and three sweet mango blossoms.",
            p2Ur = "وہ ایک سنہری سورج مکھی، دو گلابی کنول اور آم کے تین پھولوں کے پاس جاتی ہے۔",
            p2Hi = "वह एक सुनहरे सूरजमुखी, दो गुलाबी कमल और आम के तीन फूलों पर जाती है।",
            p3En = "Back at the hive, the bees make sweet golden honey to share!",
            p3Ur = "چھتے میں واپس آ کر مکھیاں مل کر میٹھا سنہری شہد بناتی ہیں!",
            p3Hi = "छत्ते में लौटकर मधुमक्खियाँ मीठा सुनहरा शहद बनाती हैं!",
            secondImg = "books/stories/story_sunflower.webp",
            thirdImg = "books/alphabets/alpha_flower.webp",
            practice = setOf("morning", "sun", "golden", "flowers"),
            quizQ = "What do the busy honeybees make back at their hive?",
            quizOpts = listOf("Sweet golden honey", "Paper airplanes", "Wooden drums")
        ),
        buildReferenceStory(
            id = "ref_ginger_cozy_cat",
            title = "Ginger Cat's Sunny Nap",
            urduTitle = "ننھی بلی کی دھوپ میں سیر",
            hindiTitle = "नन्ही बिल्ली की धूप में सैर",
            author = "Deepa Balsavar",
            level = 1,
            coverImg = "books/alphabets/alpha_c_cat.webp",
            categoryTag = "Family & Friends",
            p1En = "Ginger is our friendly orange cat with soft paws and a long swishing tail.",
            p1Ur = "جنجر نرم پنجوں اور لمبی دم والی ہماری پیاری نارنجی بلی ہے۔",
            p1Hi = "जिंजर नरम पंजों और लंबी पूँछ वाली हमारी प्यारी नारंगी बिल्ली है।",
            p2En = "She drinks warm milk from her blue bowl and chases a bouncy ball across the rug.",
            p2Ur = "وہ اپنے نیلے پیالے سے دودھ پیتی ہے اور قالین پر اچھلتی گیند کے پیچھے بھاگتی ہے۔",
            p2Hi = "वह अपनी नीली कटोरी से दूध पीती है और गेंद के पीछे भागती है।",
            p3En = "Then Ginger curls up on the sunny windowsill and purrs happily all afternoon!",
            p3Ur = "پھر جنجر دھوپ والی کھڑکی کے پاس سمٹ کر بیٹھ جاتی ہے اور خوشی سے آرام کرتی ہے!",
            p3Hi = "फिर जिंजर धूप वाली खिड़की के पास बैठकर आराम से सो जाती है!",
            secondImg = "books/alphabets/alpha_b_ball.webp",
            thirdImg = "books/alphabets/alpha_h_house.webp",
            practice = setOf("blue", "ball", "sun", "happy"),
            quizQ = "Where does Ginger Cat curl up for her afternoon nap?",
            quizOpts = listOf("On the sunny windowsill", "Inside a rain puddle", "On top of a train")
        ),
        buildReferenceStory(
            id = "ref_my_blue_bicycle",
            title = "Pedaling My New Bicycle",
            urduTitle = "میری نئی نیلی سائیکل",
            hindiTitle = "मेरी नई नीली साइकिल",
            author = "payal Dhar",
            level = 1,
            coverImg = "books/alphabets/alpha_bicycle.webp",
            categoryTag = "Family & Friends",
            p1En = "Look at my shiny new bicycle! It has two round wheels and a silver bell that rings.",
            p1Ur = "میری نئی چمکدار سائیکل دیکھو! اس کے دو گول پہیے اور ایک گھنٹی ہے جو ٹرن ٹرن بجتی ہے۔",
            p1Hi = "मेरी नई चमकीली साइकिल देखो! इसके दो गोल पहिए और एक घंटी है।",
            p2En = "I wear my safety helmet and pedal smoothly down the garden lane with my brother.",
            p2Ur = "میں اپنا حفاظتی ہیلمٹ پہنتا ہوں اور اپنے بھائی کے ساتھ باغ کی گلی میں سائیکل چلاتا ہوں۔",
            p2Hi = "मैं अपना हेलमेट पहनता हूँ और भाई के साथ बग़ीचे के रास्ते पर साइकिल चलाता हूँ।",
            p3En = "Tring, tring! We wave to our neighbors as we ride safely to the playground!",
            p3Ur = "ٹرن ٹرن! ہم ہمسایوں کو ہاتھ ہلاتے ہوئے بحفاظت کھیل کے میدان تک جاتے ہیں!",
            p3Hi = "ट्रिंग-ट्रिंग! हम पड़ोसियों को हाथ हिलाते हुए खेल के मैदान तक जाते हैं!",
            secondImg = "books/alphabets/alpha_t_tree.webp",
            thirdImg = "books/alphabets/alpha_s_sun.webp",
            practice = setOf("bicycle", "brother", "garden", "play"),
            quizQ = "What does the rider wear before pedaling down the garden lane?",
            quizOpts = listOf("A safety helmet", "A winter snowman hat", "A royal crown")
        ),
        buildReferenceStory(
            id = "ref_brave_red_firetruck",
            title = "The Brave Red Firetruck",
            urduTitle = "سرخ فائر بریگیڈ گاڑی",
            hindiTitle = "बहादुर लाल फ़ायर ट्रक",
            author = "Room to Read Studio",
            level = 1,
            coverImg = "books/stories/story_firetruck.webp",
            categoryTag = "School & Kindness",
            p1En = "Clang, clang! The shiny red firetruck rolls out of the station to help the town.",
            p1Ur = "ٹن ٹن! چمکدار سرخ فائر بریگیڈ گاڑی شہر کی مدد کے لیے اسٹیشن سے نکلتی ہے۔",
            p1Hi = "टन-टन! चमकीला लाल फ़ायर ट्रक शहर की मदद के लिए स्टेशन से निकलता है।",
            p2En = "When a tiny kitten is stuck high in an oak tree, the firefighters raise their tall ladder.",
            p2Ur = "جب ایک ننھی بلی اونچے درخت پر پھنس جاتی ہے تو فائر فائٹرز اپنی لمبی سیڑھی لگاتے ہیں۔",
            p2Hi = "जब एक नन्ही बिल्ली ऊँचे पेड़ पर फँस जाती है, तो वे अपनी लंबी सीढ़ी लगाते हैं।",
            p3En = "They rescue the little kitten safely and everyone on the street claps with joy!",
            p3Ur = "وہ ننھی بلی کو بحفاظت نیچے اتارتے ہیں اور گلی میں موجود ہر شخص خوشی سے تالیاں بجاتا ہے!",
            p3Hi = "वे नन्ही बिल्ली को सुरक्षित नीचे उतारते हैं और सब खुशी से तालियाँ बजाते हैं!",
            secondImg = "books/alphabets/alpha_c_cat.webp",
            thirdImg = "books/alphabets/alpha_t_tree.webp",
            practice = setOf("red", "tree", "street", "happy"),
            quizQ = "Who did the firefighters rescue from the tall oak tree?",
            quizOpts = listOf("A tiny kitten", "A blue whale", "A green tractor")
        ),
        buildReferenceStory(
            id = "ref_counting_ten_balloons",
            title = "Ten Bright Party Balloons",
            urduTitle = "دس رنگ برنگے غبارے",
            hindiTitle = "दस रंग-बिरंगे गुब्बारे",
            author = "Seekh Math & Rhymes",
            level = 1,
            coverImg = "books/alphabets/alpha_balloon.webp",
            categoryTag = "Numbers & Rhymes",
            isDailyDrop = true,
            p1En = "At the park festival, the balloon seller holds a bunch of ten bright floating balloons.",
            p1Ur = "پارک کے میلے میں غبارے والے کے ہاتھ میں دس چمکدار اڑتے ہوئے غبارے ہیں۔",
            p1Hi = "पार्क के मेले में गुब्बारे वाले के पास दस चमकीले उड़ते गुब्बारे हैं।",
            p2En = "Two are ruby red, three are sunny yellow, two are sky blue, and three are emerald green!",
            p2Ur = "دو سرخ ہیں، تین پیلے ہیں، دو آسمانی نیلے ہیں اور تین زمرد جیسے ہرے ہیں!",
            p2Hi = "दो लाल हैं, तीन पीले हैं, दो आसमानी नीले हैं और तीन हरे हैं!",
            p3En = "Count them all from one to ten as they bob happily against the blue sky!",
            p3Ur = "ایک سے دس تک ان سب کو گنو جب وہ نیلے آسمان کے نیچے خوشی سے لہراتے ہیں!",
            p3Hi = "एक से दस तक उन सबको गिनो जब वे नीले आसमान में लहराते हैं!",
            secondImg = "books/stories/story_rainbow.webp",
            thirdImg = "books/alphabets/alpha_s_sun.webp",
            practice = setOf("bright", "red", "yellow", "blue", "green"),
            quizQ = "How many bright balloons were in the seller's bunch?",
            quizOpts = listOf("Ten bright balloons", "Zero balloons", "One hundred balloons")
        ),
        buildReferenceStory(
            id = "ref_slow_snail_garden",
            title = "Sammy Snail's Spiral Shell",
            urduTitle = "ننھے گھونگھے کا پیارا گھر",
            hindiTitle = "नन्हे घोंघे का प्यारा घर",
            author = "Book Dash Junior",
            level = 1,
            coverImg = "books/stories/story_snail.webp",
            categoryTag = "Animals & Nature",
            bookType = "DECODABLE",
            phonicsTag = "Phonics: sn • sh • l",
            p1En = "Sammy Snail carries his round spiral house right on his back wherever he goes.",
            p1Ur = "سیمی گھونگھا جہاں بھی جاتا ہے اپنا گول گھر اپنی پیٹھ پر اٹھا کر چلتا ہے۔",
            p1Hi = "सैमी घोंघा जहाँ भी जाता है अपना गोल घर अपनी पीठ पर लेकर चलता है।",
            p2En = "After a cool morning rain, Sammy glides slowly across a shiny green cabbage leaf.",
            p2Ur = "صبح کی ہلکی بارش کے بعد سیمی چمکدار ہرے پتے پر آہستہ آہستہ چلتا ہے۔",
            p2Hi = "सुबह की बारिश के बाद सैमी चमकीले हरे पत्ते पर धीरे-धीरे चलता है।",
            p3En = "He never needs to hurry because he is already home under the garden flowers!",
            p3Ur = "اسے کبھی جلدی کرنے کی ضرورت نہیں پڑتی کیونکہ پھولوں کے نیچے اس کا گھر ہمیشہ اس کے ساتھ ہے!",
            p3Hi = "उसे कभी जल्दी नहीं होती क्योंकि उसका घर हमेशा उसके साथ रहता है!",
            secondImg = "books/alphabets/alpha_flower.webp",
            thirdImg = "books/alphabets/alpha_u_umbrella.webp",
            practice = setOf("morning", "rain", "green", "flowers"),
            quizQ = "Where does Sammy Snail carry his round spiral house?",
            quizOpts = listOf("Right on his back", "In a school bus", "Inside a suitcase")
        ),
        buildReferenceStory(
            id = "ref_green_farm_tractor",
            title = "The Helpful Farm Tractor",
            urduTitle = "کھیت کا محنتی ٹریکٹر",
            hindiTitle = "खेत का मेहनती ट्रैक्टर",
            author = "Village Readers Pack",
            level = 1,
            coverImg = "books/stories/story_tractor.webp",
            categoryTag = "Science & Weather",
            p1En = "Chug, chug! The strong green tractor rolls across the farm fields at sunrise.",
            p1Ur = "چگ چگ! مضبوط ہرا ٹریکٹر طلوعِ آفتاب کے وقت کھیتوں میں چلتا ہے۔",
            p1Hi = "चग-चग! मज़बूत हरा ट्रैक्टर सुबह सूरज निकलते ही खेतों में चलता है।",
            p2En = "Farmer Rahim plows the soft brown soil and plants rows of golden wheat and tomatoes.",
            p2Ur = "کسان رحیم نرم مٹی تیار کرتا ہے اور سنہری گندم اور ٹماٹر کے پودے لگاتا ہے۔",
            p2Hi = "किसान रहीम नरम मिट्टी तैयार करता है और गेहूँ व टमाटर के पौधे लगाता है।",
            p3En = "With warm sunlight and rain, the farm grows fresh food for everyone in town!",
            p3Ur = "سورج کی دھوپ اور بارش سے کھیت میں پورے شہر کے لیے تازہ فصلیں اگتی ہیں!",
            p3Hi = "सूरज की धूप और बारिश से खेत में सबके लिए ताज़ा अनाज उगता है!",
            secondImg = "books/alphabets/alpha_tomato.webp",
            thirdImg = "books/stories/story_sunflower.webp",
            practice = setOf("green", "sunlight", "rain", "golden"),
            quizQ = "What helps the planted seeds grow into fresh food on the farm?",
            quizOpts = listOf("Warm sunlight and rain", "Snowballs and ice", "Loud drums")
        ),

        // --- LEVEL 2 STORIES ---
        buildReferenceStory(
            id = "ref_dancing_peacock_rain",
            title = "Moru the Dancing Peacock",
            urduTitle = "بارش میں ناچتا ہوا مور",
            hindiTitle = "बारिश में नाचता हुआ मोर",
            author = "Paro Anand",
            level = 2,
            coverImg = "books/alphabets/alpha_peacock.webp",
            categoryTag = "Animals & Nature",
            isDailyDrop = true,
            bookType = "SERIES",
            seriesTag = "Monsoon Tales • Ep 1",
            p1En = "Moru the peacock lives near the mango grove at the edge of the green hillside.",
            p1Ur = "مورو مور ہری پہاڑی کے کنارے آم کے باغ کے پاس رہتا ہے۔",
            p1Hi = "मोरू मोर हरी पहाड़ी के किनारे आम के बाग़ के पास रहता है।",
            p2En = "When cool monsoon clouds gather in the sky, Moru opens his magnificent blue and green feathers like a giant fan!",
            p2Ur = "جب آسمان پر ٹھنڈے بادل چھاتے ہیں تو مورو اپنے نیلے اور ہرے پروں کو پنکھے کی طرح پھیلا لیتا ہے!",
            p2Hi = "जब आसमान में बादल छाते हैं, तो मोरू अपने नीले और हरे पंखों को फैलाकर नाचता है!",
            p3En = "All the forest birds clap and sing as Moru performs his joyful rain dance!",
            p3Ur = "جب مورو بارش کی خوشی میں رقص کرتا ہے تو جنگل کے تمام پرندے گیت گاتے ہیں!",
            p3Hi = "जब मोरू बारिश की खुशी में नाचता है, तो सभी पक्षी चहचहाते हैं!",
            secondImg = "books/stories/story_rainbow.webp",
            thirdImg = "books/alphabets/alpha_u_umbrella.webp",
            practice = setOf("clouds", "blue", "green", "rain", "happy"),
            quizQ = "When does Moru the peacock spread his colorful feathers to dance?",
            quizOpts = listOf("When cool monsoon rain clouds gather", "In the middle of a snowstorm", "Inside a classroom")
        ),
        buildReferenceStory(
            id = "ref_tortoise_and_hare_race",
            title = "Toby Tortoise Wins the Race",
            urduTitle = "کچھوا اور خرگوش کی مشہور دوڑ",
            hindiTitle = "कछुआ और खरगोश की प्रसिद्ध दौड़",
            author = "Panchatantra Classics",
            level = 2,
            coverImg = "books/alphabets/alpha_turtle.webp",
            categoryTag = "Bedtime & Folktales",
            isDailyDrop = true,
            p1En = "One sunny morning, speedy Rabbit challenged steady Toby Tortoise to a race to the old banyan tree.",
            p1Ur = "ایک روشن صبح تیز رفتار خرگوش نے مستقل مزاج کچھوے کو برگد کے درخت تک دوڑ کا چیلنج دیا۔",
            p1Hi = "एक सुबह तेज़ खरगोश ने कछुए को बरगद के पेड़ तक दौड़ लगाने की चुनौती दी।",
            p2En = "Rabbit ran fast at first, then stopped to nap under a shady bush while Toby kept walking step by step.",
            p2Ur = "خرگوش پہلے تیز بھاگا مگر پھر جھاڑی کے سائے میں سو گیا جبکہ کچھوا مسلسل قدم بہ قدم چلتا رہا۔",
            p2Hi = "खरगोश पहले तेज़ दौड़ा पर फिर छाँव में सो गया, जबकि कछुआ लगातार चलता रहा।",
            p3En = "Slow and steady Toby crossed the finish ribbon first and smiled at his surprised friend!",
            p3Ur = "مسلسل محنت کرنے والے کچھوے نے سب سے پہلے جیت کی لکیر پار کر لی!",
            p3Hi = "लगातार मेहनत करने वाले कछुए ने सबसे पहले दौड़ जीत ली!",
            secondImg = "books/alphabets/alpha_r_rabbit.webp",
            thirdImg = "books/alphabets/alpha_t_tree.webp",
            practice = setOf("morning", "tree", "walk", "smile"),
            quizQ = "How did Toby Tortoise win the race to the banyan tree?",
            quizOpts = listOf("By walking steadily step by step without giving up", "By riding a train", "By flying on a kite")
        ),
        buildReferenceStory(
            id = "ref_polly_talking_parrot",
            title = "Mitthu Parrot's Orchard Song",
            urduTitle = "مٹھو طوطے کا باغ والا گیت",
            hindiTitle = "मिठ्ठू तोते का बग़ीचे का गीत",
            author = "Zai Whitaker",
            level = 2,
            coverImg = "books/alphabets/alpha_p_parrot.webp",
            categoryTag = "Animals & Nature",
            bookType = "DECODABLE",
            phonicsTag = "Phonics: p • gr • tr",
            p1En = "Mitthu is a bright green parrot with a curved ruby-red beak who perches high in the guava tree.",
            p1Ur = "مٹھو سرخ چونچ والا ایک چمکدار ہرا طوطا ہے جو امرود کے اونچے درخت پر بیٹھتا ہے۔",
            p1Hi = "मिठ्ठू लाल चोंच वाला एक चमकीला हरा तोता है जो पेड़ की ऊँची डाल पर बैठता है।",
            p2En = "Every morning he whistles a cheerful tune and greets the children walking to school.",
            p2Ur = "ہر صبح وہ سریلی سیٹی بجاتا ہے اور اسکول جانے والے بچوں کو سلام کرتا ہے۔",
            p2Hi = "हर सुबह वह मधुर सीटी बजाता है और स्कूल जाते बच्चों को नमस्ते कहता है।",
            p3En = "The children leave sweet slices of apple and pomegranate on the garden wall for their feathered friend!",
            p3Ur = "بچے اپنے پیارے پرندے دوست کے لیے باغ کی دیوار پر میٹھے سیب اور انار کے دانے رکھتے ہیں!",
            p3Hi = "बच्चे अपने प्यारे दोस्त के लिए बग़ीचे की दीवार पर मीठे सेब और अनार रखते हैं!",
            secondImg = "books/alphabets/alpha_a_apple.webp",
            thirdImg = "books/alphabets/alpha_anar_pomegranate.webp",
            practice = setOf("green", "red", "morning", "school", "apple"),
            quizQ = "What treats do the children leave on the garden wall for Mitthu Parrot?",
            quizOpts = listOf("Sweet slices of apple and pomegranate", "Toy cars", "Colored pencils")
        ),
        buildReferenceStory(
            id = "ref_fox_and_sweet_grapes",
            title = "Roxy Fox and the Vineyard",
            urduTitle = "لومڑی اور انگور کا باغ",
            hindiTitle = "लोमड़ी और अंगूर का बाग़",
            author = "Aesop & StoryWeaver",
            level = 2,
            coverImg = "books/stories/story_fox.webp",
            categoryTag = "Bedtime & Folktales",
            p1En = "Roxy the clever orange fox trotted into a sunny vineyard bursting with purple grapes.",
            p1Ur = "چالاک نارنجی لومڑی راکسی دھوپ والے باغ میں آئی جہاں جامنی انگوروں کے خوشے لٹک رہے تھے۔",
            p1Hi = "चतुर नारंगी लोमड़ी रॉक्सी अंगूर के बग़ीचे में आई जहाँ बैंगनी अंगूर लटक रहे थे।",
            p2En = "Instead of giving up when the vine was high, Roxy stacked two wooden crates neatly to reach a low basket.",
            p2Ur = "اونچی بیل دیکھ کر ہمت ہارنے کے بجائے اس نے عقل استعمال کی اور نیچے ٹوکری میں رکھے میٹھے پھل تلاش کیے۔",
            p2Hi = "हार मानने के बजाय उसने समझदारी से काम लिया और मीठे अंगूरों का आनंद लिया।",
            p3En = "Patience and clever thinking helped Roxy enjoy a delicious afternoon picnic!",
            p3Ur = "صبر اور عقلمندی کی بدولت راکسی نے دوپہر کی مزیدار دعوت کا لطف اٹھایا!",
            p3Hi = "धैर्य और समझदारी से रॉक्सी ने दोपहर के मीठे फलों का आनंद लिया!",
            secondImg = "books/alphabets/alpha_g_grapes.webp",
            thirdImg = "books/alphabets/alpha_o_orange.webp",
            practice = setOf("garden", "bright", "happy"),
            quizQ = "What grew in bunches inside the sunny vineyard?",
            quizOpts = listOf("Juicy purple grapes", "Snowflakes", "Sea shells")
        ),
        buildReferenceStory(
            id = "ref_panda_bamboo_picnic",
            title = "Piku Panda's Bamboo Picnic",
            urduTitle = "پیکو پانڈا کی بانس کے جنگل میں پکنک",
            hindiTitle = "पीकू पांडा की बाँस के वन में पिकनिक",
            author = "Room to Read Asia",
            level = 2,
            coverImg = "books/stories/story_panda.webp",
            categoryTag = "Animals & Nature",
            isDailyDrop = true,
            p1En = "High in the misty green hills lives Piku, a gentle black-and-white giant panda.",
            p1Ur = "ہری بھری پہاڑیوں میں سیاہ اور سفید رنگ کا ایک پیارا پانڈا پیکو رہتا ہے۔",
            p1Hi = "हरी-भरी पहाड़ियों में काले और सफ़ेद रंग का एक प्यारा पांडा पीकू रहता है।",
            p2En = "Piku sits under the tall bamboo stalks and munches crisp green leaves with his friends.",
            p2Ur = "پیکو بانس کے اونچے درختوں کے نیچے بیٹھ کر اپنے دوستوں کے ساتھ ہرے پتے کھاتا ہے۔",
            p2Hi = "पीकू बाँस के पेड़ों के नीचे बैठकर अपने दोस्तों के साथ हरे पत्ते खाता है।",
            p3En = "After lunch, he rolls tumbledown across the soft grassy hill with a big playful grin!",
            p3Ur = "دوپہر کے کھانے کے بعد وہ نرم گھاس والی پہاڑی پر قلابازیاں کھاتے ہوئے کھیلتا ہے!",
            p3Hi = "खाने के बाद वह नरम घास वाली पहाड़ी पर खुशी से खेलता है!",
            secondImg = "books/alphabets/alpha_mountain.webp",
            thirdImg = "books/alphabets/alpha_t_tree.webp",
            practice = setOf("green", "leaves", "friends", "play"),
            quizQ = "What does Piku Panda love to munch in the green hills?",
            quizOpts = listOf("Crisp green bamboo leaves", "Spicy pizza", "Chocolate cake")
        ),
        buildReferenceStory(
            id = "ref_giraffe_tallest_tree",
            title = "Gigi Giraffe Reaches High",
            urduTitle = "لمبی گردن والا زرافہ جی جی",
            hindiTitle = "लंबी गर्दन वाली जिराफ़ जीजी",
            author = "African Storybook Project",
            level = 2,
            coverImg = "books/stories/story_giraffe.webp",
            categoryTag = "School & Kindness",
            p1En = "Gigi is the tallest animal on the savannah, with golden spots and a long graceful neck.",
            p1Ur = "جی جی سنہری دھبوں اور لمبی گردن والا میدان کا سب سے اونچا جانور ہے۔",
            p1Hi = "जीजी सुनहरे धब्बों और लंबी गर्दन वाली मैदान की सबसे ऊँची जिराफ़ है।",
            p2En = "When little Zebra and Rabbit cannot reach the sweet fruits at the top of the acacia tree, Gigi bends down to help.",
            p2Ur = "جب ننھا زیبرا اور خرگوش اونچے درخت کے پھلوں تک نہیں پہنچ پاتے تو جی جی ان کی مدد کرتی ہے۔",
            p2Hi = "जब नन्हा ज़ेबरा और खरगोश ऊँचे पेड़ के फलों तक नहीं पहुँच पाते, तो जीजी उनकी मदद करती है।",
            p3En = "Using our special strengths to help smaller friends makes the whole meadow happier!",
            p3Ur = "اپنی خوبیوں سے چھوٹے دوستوں کی مدد کرنا پورے جنگل کو خوشحال بنا دیتا ہے!",
            p3Hi = "अपनी खूबियों से छोटे दोस्तों की मदद करना सबको खुश कर देता है!",
            secondImg = "books/alphabets/alpha_z_zebra.webp",
            thirdImg = "books/alphabets/alpha_r_rabbit.webp",
            practice = setOf("golden", "tree", "friends", "special"),
            quizQ = "How did Gigi Giraffe help little Zebra and Rabbit?",
            quizOpts = listOf("Reached the sweet fruits at the top of the tall tree for them", "Built a boat", "Painted a wall")
        ),
        buildReferenceStory(
            id = "ref_dolphin_blue_lagoon",
            title = "Dora Dolphin's Ocean Leap",
            urduTitle = "ڈورا ڈولفن کی سمندری چھلانگ",
            hindiTitle = "डोरा डॉल्फ़िन की समुद्री छलांग",
            author = "Coastal Readers Series",
            level = 2,
            coverImg = "books/stories/story_dolphin.webp",
            categoryTag = "Animals & Nature",
            bookType = "SERIES",
            seriesTag = "Ocean Explorers • Ep 1",
            p1En = "In the warm turquoise waters of the coral lagoon swims Dora the silver dolphin.",
            p1Ur = "سمندر کے نیلے اور شفاف پانی میں چاندی جیسی ڈولفن ڈورا تیرتی ہے۔",
            p1Hi = "समुद्र के साफ़ नीले पानी में चाँदी जैसी डॉल्फ़िन डोरा तैरती है।",
            p2En = "She leaps high above the sparkling waves and guides a little white sailboat safely past the reef.",
            p2Ur = "وہ چمکتی لہروں کے اوپر اونچی چھلانگ لگاتی ہے اور سفید کشتی کو بحفاظت راستہ دکھاتی ہے۔",
            p2Hi = "वह लहरों के ऊपर ऊँची छलांग लगाती है और सफ़ेद नाव को सुरक्षित रास्ता दिखाती है।",
            p3En = "The young sailors wave thank you as Dora flips her tail under the golden sun!",
            p3Ur = "نوجوان ملاح ہاتھ ہلا کر شکریہ ادا کرتے ہیں جب ڈورا سنہری دھوپ میں غوطہ لگاتی ہے!",
            p3Hi = "नाविक हाथ हिलाकर धन्यवाद कहते हैं जब डोरा सुनहरी धूप में गोता लगाती है!",
            secondImg = "books/alphabets/alpha_y_yacht.webp",
            thirdImg = "books/alphabets/alpha_f_fish.webp",
            practice = setOf("water", "ocean", "sailor", "golden", "sun"),
            quizQ = "What did Dora Dolphin guide safely past the coral reef?",
            quizOpts = listOf("A little white sailboat", "A school bus", "A desert camel")
        ),
        buildReferenceStory(
            id = "ref_magic_paint_palette",
            title = "Maya's Rainbow Paintbox",
            urduTitle = "مایا کے جادوئی رنگ اور برش",
            hindiTitle = "माया के जादुई रंग और ब्रश",
            author = "Nandini Nayar",
            level = 2,
            coverImg = "books/stories/story_palette.webp",
            categoryTag = "School & Kindness",
            isDailyDrop = true,
            p1En = "In art class, Maya opens her wooden paint palette filled with red, blue, and yellow watercolors.",
            p1Ur = "آرٹ کی کلاس میں مایا سرخ، نیلے اور پیلے رنگوں سے بھری اپنی رنگوں کی تختی کھولتی ہے۔",
            p1Hi = "आर्ट की कक्षा में माया लाल, नीले और पीले रंगों से भरी अपनी रंग-प्लेट खोलती है।",
            p2En = "When she mixes blue and yellow, bright emerald green appears! Red and yellow make warm sunset orange!",
            p2Ur = "جب وہ نیلا اور پیلا ملاتی ہے تو چمکدار ہرا رنگ بن جاتا ہے! سرخ اور پیلا مل کر نارنجی بناتے ہیں!",
            p2Hi = "जब वह नीला और पीला मिलाती है तो हरा रंग बनता है, और लाल व पीला मिलकर नारंगी बनाते हैं!",
            p3En = "Maya paints a giant rainbow over her school and hangs it on the classroom wall!",
            p3Ur = "مایا اپنے اسکول کے اوپر ایک بڑی قوسِ قزح پینٹ کرتی ہے اور اسے کلاس کی دیوار پر لگاتی ہے!",
            p3Hi = "माया अपने स्कूल के ऊपर एक सुंदर इंद्रधनुष बनाती है और कक्षा में लगाती है!",
            secondImg = "books/stories/story_rainbow.webp",
            thirdImg = "books/alphabets/alpha_pencil.webp",
            practice = setOf("colors", "red", "blue", "yellow", "green", "rainbow", "classroom"),
            quizQ = "What color appears when Maya mixes blue and yellow paint together?",
            quizOpts = listOf("Bright emerald green", "Dark black", "Silver grey")
        ),
        buildReferenceStory(
            id = "ref_snowman_winter_scarf",
            title = "Sammy Snowman's Warm Scarf",
            urduTitle = "برف کے پتلے کا گرم مفلر",
            hindiTitle = "स्नोमैन का गर्म मफ़लर",
            author = "Himalayan Storybooks",
            level = 2,
            coverImg = "books/stories/story_snowman.webp",
            categoryTag = "Science & Weather",
            p1En = "Soft white snow fell all night across the mountain village, turning every roof silvery white.",
            p1Ur = "پہاڑی گاؤں میں رات بھر نرم سفید برف باری ہوئی جس سے ہر چھت چاندی جیسی سفید ہو گئی۔",
            p1Hi = "पहाड़ी गाँव में रात भर नरम सफ़ेद बर्फ़ गिरी जिससे हर छत सफ़ेद हो गई।",
            p2En = "Aryan and his sister rolled three big snowballs to build a cheerful snowman with a carrot nose.",
            p2Ur = "آریان اور اس کی بہن نے برف کے تین گولے بنا کر گاجر کی ناک والا ایک خوش مزاج سنومین بنایا۔",
            p2Hi = "आर्यन और उसकी बहन ने बर्फ़ के गोलों से गाजर की नाक वाला एक प्यारा स्नोमैन बनाया।",
            p3En = "When the sun came out, they learned how solid snow melts back into clear water for the mountain streams!",
            p3Ur = "جب دھوپ نکلی تو انہوں نے سیکھا کہ برف پگھل کر پہاڑی ندیوں کے لیے صاف پانی کیسے بنتی ہے!",
            p3Hi = "धूप निकलने पर उन्होंने सीखा कि बर्फ़ पिघलकर पहाड़ी नदियों के लिए साफ़ पानी बनती है!",
            secondImg = "books/alphabets/alpha_mountain.webp",
            thirdImg = "books/alphabets/alpha_h_house.webp",
            practice = setOf("white", "sister", "sun", "water"),
            quizQ = "What happens to solid snow when the warm sun comes out?",
            quizOpts = listOf("It melts into clear water", "It turns into rocks", "It flies into space")
        ),
        buildReferenceStory(
            id = "ref_guitar_village_concert",
            title = "The Little Wooden Guitar",
            urduTitle = "ننھے گٹار کی سریلی دھن",
            hindiTitle = "नन्हे गिटार की मधुर धुन",
            author = "Book Dash Music Pack",
            level = 2,
            coverImg = "books/stories/story_guitar.webp",
            categoryTag = "Numbers & Rhymes",
            p1En = "Tariq loves strumming the six silver strings of his grandfather's acoustic wooden guitar.",
            p1Ur = "طارق کو اپنے دادا جان کے لکڑی کے گٹار کے چھ تاروں پر سریلی دھن بجانا بہت پسند ہے۔",
            p1Hi = "तारिक़ को अपने दादाजी के लकड़ी के गिटार के छह तारों पर मधुर धुन बजाना बहुत पसंद है।",
            p2En = "His sister plays the colorful xylophone while his friend taps a steady beat on the red drum.",
            p2Ur = "اس کی بہن رنگین زائلوفون بجاتی ہے جبکہ اس کا دوست سرخ ڈھول پر تال دیتا ہے۔",
            p2Hi = "उसकी बहन रंगीन ज़ाइलोफ़ोन बजाती है और उसका दोस्त लाल ढोल पर ताल देता है।",
            p3En = "Together the three young musicians fill the evening courtyard with harmony and joy!",
            p3Ur = "تینوں ننھے موسیقار مل کر شام کے وقت صحن کو خوشگوار موسیقی اور خوشی سے بھر دیتے ہیں!",
            p3Hi = "तीनों नन्हे संगीतकार मिलकर शाम के आँगन को मधुर संगीत से भर देते हैं!",
            secondImg = "books/alphabets/alpha_x_xylophone.webp",
            thirdImg = "books/alphabets/alpha_d_drum.webp",
            practice = setOf("grandfather", "sister", "friend", "drummer", "happy"),
            quizQ = "How many strings does Tariq strum on his wooden guitar?",
            quizOpts = listOf("Six silver strings", "One hundred strings", "Zero strings")
        ),
        buildReferenceStory(
            id = "ref_squirrel_acorn_winter",
            title = "Nutty Squirrel's Acorn Treasure",
            urduTitle = "ننھی گلہری کا اخروٹ خزانہ",
            hindiTitle = "नन्ही गिलहरी का अखरोट ख़ज़ाना",
            author = "Chameli Ramachandran",
            level = 2,
            coverImg = "books/stories/story_squirrel.webp",
            categoryTag = "Animals & Nature",
            p1En = "Nutty the bushy-tailed squirrel is busy gathering acorns and walnuts in the autumn forest.",
            p1Ur = "گھنی دم والی ننھی گلہری خزاں کے جنگل میں اخروٹ اور بادام جمع کرنے میں مصروف ہے۔",
            p1Hi = "घनी पूँछ वाली नन्ही गिलहरी जंगल में अखरोट इकट्ठा करने में व्यस्त है।",
            p2En = "Whenever she buries a nut in the soft soil and forgets it, a tiny green tree sprout grows in spring!",
            p2Ur = "جب بھی وہ نرم مٹی میں کوئی بیج دباتی ہے تو بہار میں وہاں ایک نیا ہرا پودا اگ آتا ہے!",
            p2Hi = "जब भी वह मिट्टी में कोई बीज दबाती है, तो वसंत में वहाँ एक नया हरा पौधा उग आता है!",
            p3En = "Little squirrels help plant hundreds of new forest trees every single year!",
            p3Ur = "ننھی گلہریاں ہر سال جنگل میں سینکڑوں نئے درخت اگانے میں قدرت کی مدد کرتی ہیں!",
            p3Hi = "नन्ही गिलहरियाँ हर साल जंगल में सैकड़ों नए पेड़ उगाने में मदद करती हैं!",
            secondImg = "books/alphabets/alpha_t_tree.webp",
            thirdImg = "books/alphabets/alpha_n_nest.webp",
            practice = setOf("busy", "treasure", "green", "tree", "trees"),
            quizQ = "What grows in spring from the nuts that squirrels bury in the soil?",
            quizOpts = listOf("New green forest trees", "Glass marbles", "Gold coins")
        ),
        buildReferenceStory(
            id = "ref_watermelon_summer_picnic",
            title = "The Giant Watermelon Picnic",
            urduTitle = "میٹھے تربوز کی گرمیوں والی پکنک",
            hindiTitle = "मीठे तरबूज़ की गर्मियों की पिकनिक",
            author = "Manjula Padmanabhan",
            level = 2,
            coverImg = "books/alphabets/alpha_w_watermelon.webp",
            categoryTag = "Family & Friends",
            p1En = "On a hot June afternoon, Grandfather brings home a giant green striped watermelon from the market.",
            p1Ur = "جون کی گرم دوپہر کو دادا جان بازار سے ایک بڑا ہرا دھاری دار تربوز گھر لاتے ہیں۔",
            p1Hi = "जून की गर्म दोपहर में दादाजी बाज़ार से एक बड़ा हरा धारीदार तरबूज़ घर लाते हैं।",
            p2En = "When Mother slices it open, the inside is ruby-red, cool, and sweet with tiny black seeds!",
            p2Ur = "جب امی اسے کاٹتی ہیں تو وہ اندر سے لال یاقوت جیسا ٹھنڈا اور میٹھا نکلتا ہے!",
            p2Hi = "जब माँ उसे काटती हैं, तो वह अंदर से लाल, ठंडा और बेहद मीठा निकलता है!",
            p3En = "We sit on the veranda mat and enjoy juicy red slices together before playing in the garden!",
            p3Ur = "ہم برآمدے کی چٹائی پر بیٹھ کر مل کر رسیلے ٹکڑے کھاتے ہیں اور پھر باغ میں کھیلتے ہیں!",
            p3Hi = "हम बरामदे की चटाई पर बैठकर साथ में मीठा तरबूज़ खाते हैं!",
            secondImg = "books/alphabets/alpha_i_icecream.webp",
            thirdImg = "books/alphabets/alpha_m_mango.webp",
            practice = setOf("grandfather", "mother", "green", "red", "garden"),
            quizQ = "What color is the inside of the giant striped watermelon when sliced open?",
            quizOpts = listOf("Cool ruby-red with tiny black seeds", "Bright blue", "Purple and grey")
        ),

        // --- LEVEL 3 STORIES ---
        buildReferenceStory(
            id = "ref_camel_desert_oasis",
            title = "Kबीर Camel's Desert Oasis",
            urduTitle = "اونٹ کا صحرائی نخلستان تک سفر",
            hindiTitle = "ऊँट का रेगिस्तानी नखलिस्तान तक सफ़र",
            author = "Rajasthani Folktale Series",
            level = 3,
            coverImg = "books/alphabets/alpha_camel.webp",
            categoryTag = "Adventure & Travel",
            isDailyDrop = true,
            p1En = "Kabir the desert camel has wide padded feet that walk smoothly across the golden sand dunes without sinking.",
            p1Ur = "صحرائی اونٹ کبیر کے چوڑے اور گدے دار پاؤں سنہری ریت کے ٹیلوں پر دھنسے بغیر آسانی سے چلتے ہیں۔",
            p1Hi = "रेगिस्तानी ऊँट कबीर के चौड़े पैर सुनहरी रेत के टीलों पर बिना धँसे आसानी से चलते हैं।",
            p2En = "Long eyelashes protect his eyes from the desert breeze as he guides travelers toward the palm oasis.",
            p2Ur = "لمبی پلکیں اس کی آنکھوں کو ریت کی ہوا سے بچاتی ہیں جب وہ مسافروں کو نخلستان کا راستہ دکھاتا ہے۔",
            p2Hi = "लंबी पलकें उसकी आँखों को रेत से बचाती हैं जब वह यात्रियों को नखलिस्तान तक ले जाता है।",
            p3En = "At sunset, they reach a cool blue oasis pool surrounded by date palms and twinkling stars!",
            p3Ur = "شام ڈھلے وہ کھجور کے درختوں اور چمکتے ستاروں سے گھرے ٹھنڈے پانی کے چشمے پر پہنچ جاتے ہیں!",
            p3Hi = "शाम ढले वे खजूर के पेड़ों और सितारों से घिरे ठंडे पानी के कुंड तक पहुँच जाते हैं!",
            secondImg = "books/alphabets/alpha_s_sun.webp",
            thirdImg = "books/alphabets/alpha_moon.webp",
            practice = setOf("golden", "walk", "water", "trees", "stars"),
            quizQ = "Why don't Kabir Camel's feet sink into the soft desert sand?",
            quizOpts = listOf("He has wide padded feet built for walking on sand", "He wears metal boots", "He flies with wings")
        ),
        buildReferenceStory(
            id = "ref_wise_night_owl",
            title = "Ollie Owl's Starlight Watch",
            urduTitle = "رات کا عقلمند الو اور چمکتے ستارے",
            hindiTitle = "रात का बुद्धिमान उल्लू और चमकते तारे",
            author = "Ruskin Bond Nature Tales",
            level = 3,
            coverImg = "books/alphabets/alpha_owl.webp",
            categoryTag = "Science & Weather",
            isDailyDrop = true,
            p1En = "When the golden sun sets and daytime birds go to sleep in their nests, Ollie the owl opens his big round eyes.",
            p1Ur = "جب سنہرا سورج غروب ہوتا ہے اور دن کے پرندے اپنے گھونسلوں میں سو جاتے ہیں تو اولی الو اپنی بڑی گول آنکھیں کھولتا ہے۔",
            p1Hi = "जब सूरज ढलता है और दिन के पक्षी घोंसलों में सो जाते हैं, तो ओली उल्लू अपनी बड़ी आँखें खोलता है।",
            p2En = "Nocturnal animals like owls have special night vision and soft feathers that let them fly in total silence.",
            p2Ur = "رات کو جاگنے والے پرندوں جیسے الو کی نظر اندھیرے میں تیز ہوتی ہے اور اس کے نرم پر بغیر آواز کے اڑتے ہیں۔",
            p2Hi = "रात में जागने वाले उल्लू की नज़र अँधेरे में तेज़ होती है और उसके पंख बिना आवाज़ के उड़ते हैं।",
            p3En = "Under the silver crescent moon, Ollie watches over the sleeping forest until the rosy dawn returns!",
            p3Ur = "چاندی جیسے ہلال کے نیچے اولی صبح کی پہلی کرن تک خاموشی سے سوئے ہوئے جنگل کی نگہبانی کرتا ہے!",
            p3Hi = "चाँद की रोशनी में ओली सुबह होने तक शांत सोए हुए जंगल की रखवाली करता है!",
            secondImg = "books/alphabets/alpha_moon.webp",
            thirdImg = "books/alphabets/alpha_n_nest.webp",
            practice = setOf("golden", "sun", "moon", "wings", "trees"),
            quizQ = "What do we call animals like owls that are awake and active at night?",
            quizOpts = listOf("Nocturnal animals", "Underwater animals", "Frozen animals")
        ),
        buildReferenceStory(
            id = "ref_tiger_stripes_jungle",
            title = "Tara Tiger's Jungle Stripes",
            urduTitle = "تارا شیرنی کی سنہری دھاریاں",
            hindiTitle = "तारा बाघिन की सुनहरी धारियाँ",
            author = "Wildlife India Readers",
            level = 3,
            coverImg = "books/stories/story_tiger.webp",
            categoryTag = "Animals & Nature",
            p1En = "In the tall golden bamboo grass of the forest reserva walks Tara, a majestic Bengal tiger.",
            p1Ur = "جنگل کی اونچی سنہری گھاس میں تارا نامی ایک باوقار بنگال ٹائیگر چلتی ہے۔",
            p1Hi = "जंगल की ऊँची सुनहरी घास में तारा नाम की एक शानदार बाघिन चलती है।",
            p2En = "Did you know that just like human fingerprints, no two tigers in the world have the exact same stripe pattern?",
            p2Ur = "کیا آپ جانتے ہیں کہ انسانوں کی انگلیوں کے نشانات کی طرح دنیا کے کسی بھی دو شیروں کی دھاریاں ایک جیسی نہیں ہوتیں؟",
            p2Hi = "क्या आप जानते हैं कि इंसानों की उँगलियों के निशान की तरह किसी भी दो बाघों की धारियाँ एक जैसी नहीं होतीं?",
            p3En = "Tara's orange and black stripes help her blend seamlessly into the sunlit forest shadows!",
            p3Ur = "تارا کی نارنجی اور سیاہ دھاریاں اسے جنگل کی دھوپ اور چھاؤں میں چھپنے میں مدد دیتی ہیں!",
            p3Hi = "तारा की नारंगी और काली धारियाँ उसे जंगल की धूप-छाँव में मिलने में मदद करती हैं!",
            secondImg = "books/alphabets/alpha_t_tree.webp",
            thirdImg = "books/stories/story_deer.webp",
            practice = setOf("golden", "unique", "special", "sunlight"),
            quizQ = "What is special about every tiger's pattern of black and orange stripes?",
            quizOpts = listOf("Every tiger's stripe pattern is completely unique like a fingerprint", "They wash off in the rain", "They glow blue in the dark")
        ),
        buildReferenceStory(
            id = "ref_zara_striped_zebra",
            title = "Zara Zebra's Grassland Run",
            urduTitle = "زارا زیبرا کی ہرے میدان میں دوڑ",
            hindiTitle = "ज़ारा ज़ेबरा की हरे मैदान में दौड़",
            author = "African Storybook Classics",
            level = 3,
            coverImg = "books/alphabets/alpha_z_zebra.webp",
            categoryTag = "Animals & Nature",
            p1En = "Zara the young zebra loves galloping across the wide open African grasslands with her herd.",
            p1Ur = "ننھی زیبرا زارا کو اپنے غول کے ساتھ وسیع ہرے میدانوں میں تیز دوڑنا بہت پسند ہے۔",
            p1Hi = "नन्ही ज़ेबरा ज़ारा को अपने झुंड के साथ हरे मैदानों में तेज़ दौड़ना बहुत पसंद है।",
            p2En = "When dozens of zebras stand close together, their dazzling black-and-white stripes confuse predators and keep the herd safe.",
            p2Ur = "جب بہت سے زیبرا ایک ساتھ کھڑے ہوتے ہیں تو ان کی سیاہ و سفید دھاریاں شکاریوں کو الجھا کر پورے غول کو محفوظ رکھتی ہیں۔",
            p2Hi = "जब कई ज़ेबरा एक साथ खड़े होते हैं, तो उनकी काली-सफ़ेद धारियाँ पूरे झुंड को सुरक्षित रखती हैं।",
            p3En = "At the river watering hole, Zara shares a cool drink beside her tall friend Gigi Giraffe!",
            p3Ur = "دریا کے کنارے زارا اپنی لمبی دوست جی جی زرافہ کے ساتھ ٹھنڈا پانی پیتی ہے!",
            p3Hi = "नदी के किनारे ज़ारा अपनी दोस्त जीजी जिराफ़ के साथ ठंडा पानी पीती है!",
            secondImg = "books/stories/story_giraffe.webp",
            thirdImg = "books/alphabets/alpha_horse.webp",
            practice = setOf("white", "water", "friends", "green"),
            quizQ = "How do a zebra herd's dazzling black-and-white stripes help them?",
            quizOpts = listOf("They blend together when standing in a group to keep the herd safe", "They help them fly", "They turn green in winter")
        ),
        buildReferenceStory(
            id = "ref_galloping_white_horse",
            title = "Badal the Mountain Horse",
            urduTitle = "بادل: پہاڑی وادی کا سفید گھوڑا",
            hindiTitle = "बादल: पहाड़ी घाटी का सफ़ेद घोड़ा",
            author = "Himachal Storytellers",
            level = 3,
            coverImg = "books/alphabets/alpha_horse.webp",
            categoryTag = "Adventure & Travel",
            p1En = "Badal is a swift white horse whose mane blows like a cloud as he gallops across the valley meadow.",
            p1Ur = "بادل ایک تیز رفتار سفید گھوڑا ہے جس کی ایال وادی کے میدان میں دوڑتے ہوئے بادل کی طرح لہراتی ہے۔",
            p1Hi = "बादल एक तेज़ सफ़ेद घोड़ा है जो घाटी के हरे मैदान में हवा की तरह दौड़ता है।",
            p2En = "Every week, Badal helps carry library book boxes up the steep mountain trail to the village school.",
            p2Ur = "ہر ہفتے بادل پہاڑی پگڈنڈی سے گاؤں کے اسکول کے بچوں کے لیے لائبریری کی کتابوں کے ڈبے لے کر جاتا ہے۔",
            p2Hi = "हर सप्ताह बादल पहाड़ी रास्ते से गाँव के स्कूल के बच्चों के लिए किताबों के बक्से लेकर जाता है।",
            p3En = "The schoolchildren reward Badal with crunchy red apples and brush his snowy coat until it gleams!",
            p3Ur = "اسکول کے بچے بادل کو انعام میں میٹھے سرخ سیب کھلاتے ہیں اور اس کے سفید بالوں کو پیار سے صاف کرتے ہیں!",
            p3Hi = "स्कूल के बच्चे बादल को मीठे लाल सेब खिलाते हैं और प्यार से उसकी देखभाल करते हैं!",
            secondImg = "books/alphabets/alpha_mountain.webp",
            thirdImg = "books/alphabets/alpha_a_apple.webp",
            practice = setOf("white", "library", "books", "school", "apples", "gleamed"),
            quizQ = "What does Badal the white horse help carry up the mountain trail to the village school?",
            quizOpts = listOf("Library book boxes for the students", "Heavy iron anchors", "Bags of seashells")
        ),
        buildReferenceStory(
            id = "ref_friendly_helper_robot",
            title = "Robo-7's Science Fair Surprise",
            urduTitle = "سائنس میلے کا مددگار روبوٹ",
            hindiTitle = "साइंस मेले का मददगार रोबोट",
            author = "STEM Kids India",
            level = 3,
            coverImg = "books/stories/story_robot.webp",
            categoryTag = "Science & Weather",
            isDailyDrop = true,
            bookType = "SERIES",
            seriesTag = "Young Inventors • Ep 1",
            p1En = "For the school science fair, Zara and Bilal built a small solar-powered helper robot named Robo-7.",
            p1Ur = "اسکول کے سائنس میلے کے لیے زارا اور بلال نے شمسی توانائی سے چلنے والا ایک ننھا مددگار روبوٹ بنایا۔",
            p1Hi = "स्कूल के विज्ञान मेले के लिए ज़ारा और बिलाल ने सौर ऊर्जा से चलने वाला एक रोबोट बनाया।",
            p2En = "When sunlight shines on Robo-7's top panel, its little metal arms sort recycled paper, plastic, and cans into neat bins!",
            p2Ur = "جب روبوٹ کے سولر پینل پر دھوپ پڑتی ہے تو وہ کاغذ اور پلاسٹک کو الگ الگ ڈبوں میں ترتیب سے رکھتا ہے!",
            p2Hi = "जब रोबोट के सोलर पैनल पर धूप पड़ती है, तो वह कचरे को अलग-अलग डिब्बों में साफ़-सुथरा रखता है!",
            p3En = "The judges awarded them the Green Innovation Ribbon for using clean energy to keep the school tidy!",
            p3Ur = "منصفین نے صاف توانائی سے اسکول کو صاف ستھरा رکھنے پر انہیں بہترین ایجاد کا انعام دیا!",
            p3Hi = "जजों ने स्वच्छ ऊर्जा से स्कूल को साफ़ रखने के लिए उन्हें प्रथम पुरस्कार दिया!",
            secondImg = "books/alphabets/alpha_s_sun.webp",
            thirdImg = "books/alphabets/alpha_book.webp",
            practice = setOf("school", "sunlight", "green", "special"),
            quizQ = "What powers Robo-7 so it can sort recycling at the school science fair?",
            quizOpts = listOf("Clean solar energy from sunlight", "Coal smoke", "Wind-up rubber bands")
        ),
        buildReferenceStory(
            id = "ref_helicopter_mountain_rescue",
            title = "Sky Rescue Helicopter",
            urduTitle = "پہاڑی وادی کا امدادی ہیلی کاپٹر",
            hindiTitle = "पहाड़ी घाटी का बचाव हेलीकॉप्टर",
            author = "Captain Aamir Khan",
            level = 3,
            coverImg = "books/stories/story_helicopter.webp",
            categoryTag = "Adventure & Travel",
            p1En = "Whirrr! Unlike airplanes that need a long runway, a rescue helicopter can lift straight up into the sky!",
            p1Ur = "شوں! ہوائی جہاز کے برعکس جسے لمبے رن وے کی ضرورت ہوتی ہے، امدادی ہیلی کاپٹر سیدھا ہوا میں بلند ہو سکتا ہے!",
            p1Hi = "हवाई जहाज़ के विपरीत, बचाव हेलीकॉप्टर बिना रनवे के सीधा आसमान में ऊपर उठ सकता है!",
            p2En = "Pilot Sana flies her red-and-white helicopter over the snowy mountain pass to deliver warm blankets and medicine.",
            p2Ur = "پائلٹ ثنا برفیلے پہاڑی درے کے اوپر ہیلی کاپٹر اڑا کر دور دراز گاؤں تک گرم کمبل اور ادویات پہنچاتی ہے۔",
            p2Hi = "पायलट सना बर्फीले पहाड़ों के ऊपर हेलीकॉप्टर उड़ाकर गाँव तक गर्म कंबल और दवाइयाँ पहुँचाती है।",
            p3En = "Hovering steadily above the village square, she waves down to the cheering families below!",
            p3Ur = "گاؤں کے چوک کے اوپر ہوا میں توازن برقرار رکھتے ہوئے وہ نیچے خوش ہوتے خاندانوں کو ہاتھ ہلاتی ہے!",
            p3Hi = "गाँव के ऊपर हवा में रुककर वह नीचे खड़े लोगों को मुस्कुराकर हाथ हिलाती है!",
            secondImg = "books/alphabets/alpha_mountain.webp",
            thirdImg = "books/alphabets/alpha_h_house.webp",
            practice = setOf("sky", "flying", "red", "white", "happy"),
            quizQ = "How is a helicopter's takeoff different from a regular airplane's takeoff?",
            quizOpts = listOf("It can lift straight up and hover without needing a long runway", "It can only travel underwater", "It runs on railway tracks")
        ),
        buildReferenceStory(
            id = "ref_penguin_antarctic_march",
            title = "Pip Penguin's Icy Slide",
            urduTitle = "ننھے پینگوئن کی برف پر پھسلن",
            hindiTitle = "नन्हे पेंगुइन की बर्फ़ पर फिसलन",
            author = "Polar Science Readers",
            level = 3,
            coverImg = "books/stories/story_penguin.webp",
            categoryTag = "Animals & Nature",
            p1En = "Pip is an Emperor penguin living on the sparkling white ice sheets of Antarctica.",
            p1Ur = "پپ ایک ننھا پینگوئن ہے جو انٹارکٹیکا کی چمکتی سفید برفانی چادروں پر رہتا ہے۔",
            p1Hi = "पिप एक नन्हा पेंगुइन है जो अंटार्कटिका की चमकती सफ़ेद बर्फ़ पर रहता है।",
            p2En = "Even though penguins are birds with feathers and wings, their wings have evolved into strong flippers for swimming!",
            p2Ur = "اگرچہ پینگوئن پروں والا پرندہ ہے، مگر اس کے پر ہوا میں اڑنے کے بجائے سمندر میں تیز تیرنے کے کام آتے ہیں!",
            p2Hi = "भले ही पेंगुइन एक पक्षी है, पर उसके पंख हवा में उड़ने के बजाय पानी में तैरने के काम आते हैं!",
            p3En = "Wheee! Pip slides on his tummy down a smooth snowy hill and dives into the icy blue ocean!",
            p3Ur = "واہ! پپ برف کی ڈھلوان پر پھسلتا ہوا ٹھنڈے نیلے سمندر میں شاندار غوطہ لگاتا ہے!",
            p3Hi = "वाह! पिप बर्फ़ की ढलान पर फिसलता हुआ नीले समुद्र में गोता लगाता है!",
            secondImg = "books/stories/story_whale.webp",
            thirdImg = "books/alphabets/alpha_f_fish.webp",
            practice = setOf("white", "wings", "blue", "ocean", "fish"),
            quizQ = "What do penguins use their strong wing-flippers for?",
            quizOpts = listOf("Swimming fast through the ocean water", "Climbing tall desert cactus plants", "Building wooden houses")
        ),
        buildReferenceStory(
            id = "ref_bear_forest_campfire",
            title = "Barnaby Bear's Starry Camp",
            urduTitle = "ننھے ریچھ کا ستاروں بھرا کیمپ",
            hindiTitle = "नन्हे भालू का तारों भरा कैंप",
            author = "Woodland Bedtime Series",
            level = 3,
            coverImg = "books/stories/story_bear.webp",
            categoryTag = "Bedtime & Folktales",
            p1En = "As autumn leaves turn golden brown in the pine woods, Barnaby Bear prepares for his cozy evening camp.",
            p1Ur = "جب صنوبر کے جنگل میں خزاں کے پتے سنہری ہو جاتے ہیں تو بارنبی ریچھ اپنے آرام دہ شام کے کیمپ کی تیاری کرتا ہے۔",
            p1Hi = "जब जंगल में पतझड़ के पत्ते सुनहरे हो जाते हैं, तो बार्नबी भालू अपने शाम के कैंप की तैयारी करता है।",
            p2En = "Around a warm glowing campfire, Barnaby and his woodland friends share stories about the constellations in the sky.",
            p2Ur = "کیمپ کی گرم روشنی کے گرد بیٹھ کر بارنبی اور اس کے دوست آسمان کے ستاروں کی کہانیاں سناتے ہیں۔",
            p2Hi = "कैंप की रोशनी के पास बैठकर बार्नबी और उसके दोस्त आसमान के सितारों की कहानियाँ सुनाते हैं।",
            p3En = "Under a patchwork quilt of pine needles, Barnaby drifts off into peaceful winter dreams!",
            p3Ur = "ستاروں بھرے آسمان کے نیچے بارنبی میٹھی اور پرسکون نیند سو جاتا ہے!",
            p3Hi = "तारों भरे आसमान के नीचे बार्नबी मीठी और शांत नींद सो जाता है!",
            secondImg = "books/stories/story_campfire.webp",
            thirdImg = "books/alphabets/alpha_moon.webp",
            practice = setOf("golden", "leaves", "friends", "story", "stars", "sky"),
            quizQ = "What did Barnaby Bear and his friends talk about around the evening campfire?",
            quizOpts = listOf("Stories about the star constellations in the night sky", "How to drive a city bus", "Soccer rules")
        ),
        buildReferenceStory(
            id = "ref_kind_queen_library_castle",
            title = "The Queen Who Built a Library",
            urduTitle = "علم دوست ملکہ اور کتابوں کا محل",
            hindiTitle = "ज्ञानप्रेमी रानी और किताबों का महल",
            author = "Sudha Murty • Inspired Fable",
            level = 3,
            coverImg = "books/alphabets/alpha_q_queen.webp",
            categoryTag = "Bedtime & Folktales",
            isDailyDrop = true,
            p1En = "Long ago in a hilltop castle lived Queen Noor, who valued wisdom and storybooks more than gold or jewels.",
            p1Ur = "بہت پہلے پہاڑی قلعے میں ملکہ نور رہتی تھی جو سونے اور جواہرات سے زیادہ علم اور کتابوں کی قدر کرتی تھی۔",
            p1Hi = "बहुत पहले पहाड़ी महल में रानी नूर रहती थीं जो सोने-चाँदी से अधिक ज्ञान और किताबों को महत्व देती थीं।",
            p2En = "She opened the grand castle gates and turned the royal hall into a free public library for every child in the kingdom!",
            p2Ur = "اس نے محل کے بڑے دروازے کھول دیے اور شاہی ہال کو سلطنت کے ہر بچے کے لیے مفت لائبریری بنا دیا!",
            p2Hi = "उन्होंने महल के दरवाज़े खोल दिए और शाही हॉल को राज्य के हर बच्चे के लिए मुफ़्त लाइब्रेरी बना दिया!",
            p3En = "Soon every village in the kingdom was filled with young readers, inventors, and poets!",
            p3Ur = "جلد ہی پوری سلطنت کا ہر گاؤں پڑھنے والے ذہین بچوں، موجدوں اور شاعروں سے آباد ہو گیا!",
            p3Hi = "जल्द ही पूरे राज्य का हर गाँव पढ़ने वाले होनहार बच्चों और कवियों से भर गया!",
            secondImg = "books/stories/story_castle.webp",
            thirdImg = "books/alphabets/alpha_book.webp",
            practice = setOf("gate", "library", "books", "read", "treasure"),
            quizQ = "What did Queen Noor turn her grand royal castle hall into?",
            quizOpts = listOf("A free public library for every child in the kingdom", "A locked treasure vault", "A horse stable")
        ),
        buildReferenceStory(
            id = "ref_swan_silver_lake",
            title = "The Graceful Swan of Silver Lake",
            urduTitle = "چاندی جھیل کا سفید راج ہنس",
            hindiTitle = "चाँदी झील का सफ़ेद राजहंस",
            author = "Hans Christian Andersen • Adapted",
            level = 3,
            coverImg = "books/stories/story_swan.webp",
            categoryTag = "Bedtime & Folktales",
            p1En = "On the calm waters of Silver Lake glided a young bird who once felt different from all the little pond ducklings.",
            p1Ur = "چاندی جھیل کے پرسکون پانی پر ایک ننھا پرندہ تیرتا تھا جو کبھی خود کو باقی بطخوں سے مختلف سمجھتا تھا۔",
            p1Hi = "चाँदी झील के शांत पानी में एक पक्षी तैरता था जो कभी खुद को दूसरी बत्तखों से अलग समझता था।",
            p2En = "When spring arrived and he looked at his reflection in the clear water, he saw snowy white feathers and graceful wings!",
            p2Ur = "جب بہار آئی اور اس نے صاف پانی میں اپنا عکس دیکھا تو اس کے برف جیسے سفید اور خوبصورت پر نکل آئے تھے!",
            p2Hi = "जब वसंत आया और उसने साफ़ पानी में अपनी परछाई देखी, तो वह एक सुंदर सफ़ेद राजहंस बन चुका था!",
            p3En = "He had grown into a magnificent white swan, reminding everyone that our unique qualities make us shine!",
            p3Ur = "وہ ایک شاندار سفید راج ہنس بن چکا تھا جو یاد دلاتا ہے کہ ہماری انفرادیت ہی ہماری اصل خوبصورتی ہے!",
            p3Hi = "वह एक सुंदर राजहंस बन गया था जो सिखाता है कि हमारी अनोखी खूबी ही हमें ख़ास बनाती है!",
            secondImg = "books/alphabets/alpha_duck.webp",
            thirdImg = "books/alphabets/alpha_flower.webp",
            practice = setOf("water", "white", "wings", "unique", "special", "shine"),
            quizQ = "What did the young bird see when he looked at his reflection in Silver Lake in spring?",
            quizOpts = listOf("He had grown into a graceful white swan", "He had turned into a frog", "He was wearing a raincoat")
        ),

        // --- LEVEL 4 STORIES ---
        buildReferenceStory(
            id = "ref_rocket_mission_mars",
            title = "Rocket Mission to the Stars",
            urduTitle = "خلائی راکٹ کا ستاروں تک سفر",
            hindiTitle = "अंतरिक्ष रॉकेट का सितारों तक सफ़र",
            author = "Dr. A.P.J. Abdul Kalam Inspired",
            level = 4,
            coverImg = "books/stories/story_rocket.webp",
            categoryTag = "Science & Weather",
            isDailyDrop = true,
            bookType = "SERIES",
            seriesTag = "Space Explorers • Ep 1",
            p1En = "Three, two, one—liftoff! The silver space rocket ignites its engines and rises above the clouds into Earth's orbit.",
            p1Ur = "تین، دو، ایک—پرواز! چاندی جیسا خلائی راکٹ بادلوں کو چیرتا ہوا زمین کے مدار میں داخل ہوتا ہے۔",
            p1Hi = "तीन, दो, एक—उड़ान! चमकीला अंतरिक्ष रॉकेट बादलों को पार करता हुआ पृथ्वी की कक्षा में पहुँचता है।",
            p2En = "From outer space, astronauts look back at planet Earth glowing like a precious blue-and-green marble against the starry sky.",
            p2Ur = "خلا سے خلانورد ہماری زمین کو دیکھتے ہیں جو ستاروں بھرے آسمان میں ایک نیلے اور ہرے موتی کی طرح چمکتی ہے۔",
            p2Hi = "अंतरिक्ष से अंतरिक्ष यात्री हमारी पृथ्वी को देखते हैं जो नीले और हरे मोती की तरह चमकती है।",
            p3En = "Scientific curiosity and reading help young dreamers reach the moon, Mars, and distant galaxies!",
            p3Ur = "سائنسی جستجو اور مطالعے کا شوق ننھے طالب علموں کو چاند، مریخ اور کہکشاؤں تک پہنچنے کا حوصلہ دیتا ہے!",
            p3Hi = "वैज्ञानिक जिज्ञासा और पढ़ाई नन्हे विद्यार्थियों को चाँद और सितारों तक पहुँचने की प्रेरणा देती है!",
            secondImg = "books/stories/story_globe.webp",
            thirdImg = "books/stories/story_telescope.webp",
            practice = setOf("clouds", "blue", "green", "stars", "sky", "moon", "read"),
            quizQ = "What does planet Earth look like when viewed by astronauts from outer space?",
            quizOpts = listOf("A glowing blue-and-green marble against the starry sky", "A flat red square", "A giant wooden cupboard")
        ),
        buildReferenceStory(
            id = "ref_blue_whale_ocean_giant",
            title = "Songs of the Giant Blue Whale",
            urduTitle = "سمندر کی بڑی نیلی وہیل کا گیت",
            hindiTitle = "समुद्र की विशाल नीली व्हेल का गीत",
            author = "Marine Biology Readers",
            level = 4,
            coverImg = "books/stories/story_whale.webp",
            categoryTag = "Science & Weather",
            isDailyDrop = true,
            p1En = "Deep in the vast Pacific Ocean swims the blue whale, the largest animal ever known to live on planet Earth.",
            p1Ur = "گہرے سمندر میں نیلی وہیل تیرتی ہے جو کرۂ ارض پر پایا جانے والا سب سے بڑا جاندار ہے۔",
            p1Hi = "गहरे महासागर में नीली व्हेल तैरती है जो पृथ्वी का सबसे विशाल जीव है।",
            p2En = "Blue whales communicate across hundreds of miles of water using low, melodic underwater songs.",
            p2Ur = "نیلی وہیل پانی کے اندر سینکڑوں میل دور تک سریلی آوازوں کے ذریعے ایک دوسرے سے رابطہ کرتی ہیں۔",
            p2Hi = "नीली व्हेल पानी के अंदर सैकड़ों मील दूर तक मधुर आवाज़ों के ज़रिए एक-दूसरे से संवाद करती हैं।",
            p3En = "Keeping our oceans clean and plastic-free protects blue whales, dolphins, sea turtles, and coral reefs!",
            p3Ur = "سمندروں کو صاف اور پلاسٹک سے پاک رکھنا وہیل، ڈولفن، کچھوؤں اور سمندری حیات کی حفاظت کرتا ہے!",
            p3Hi = "महासागरों को साफ़ और प्लास्टिक-मुक्त रखना व्हेल, डॉल्फ़िन और कछुओं की रक्षा करता है!",
            secondImg = "books/stories/story_dolphin.webp",
            thirdImg = "books/stories/story_octopus.webp",
            practice = setOf("ocean", "blue", "water", "fish"),
            quizQ = "How do blue whales communicate with one another across hundreds of miles of ocean?",
            quizOpts = listOf("Using low, melodic underwater songs", "By sending paper letters", "By flashing traffic lights")
        ),
        buildReferenceStory(
            id = "ref_dinosaur_fossil_discovery",
            title = "Dino Tracks in the Valley",
            urduTitle = "وادی میں ڈائنوسار کے قدیم نشانات",
            hindiTitle = "घाटी में डायनासोर के प्राचीन निशान",
            author = "Paleontology for Kids",
            level = 4,
            coverImg = "books/stories/story_dino.webp",
            categoryTag = "Science & Weather",
            p1En = "Millions of years ago, gentle long-necked herbivore dinosaurs walked through lush prehistoric fern forests.",
            p1Ur = "لاکھوں سال پہلے لمبی گردن والے پودے کھانے والے ڈائنوسار قدیم سرسبز جنگلات میں چلتے تھے۔",
            p1Hi = "लाखों साल पहले लंबी गर्दन वाले शाकाहारी डायनासोर प्राचीन हरे जंगलों में रहते थे।",
            p2En = "When scientists called paleontologists brush away layers of rock, they discover fossilized footprints and bones!",
            p2Ur = "جب ماہرینِ آثارِ قدیمہ چٹانوں کی تہوں کو صاف کرتے ہیں تو انہیں پتھر میں محفوظ قدیم نقوش ملتے ہیں!",
            p2Hi = "जब वैज्ञानिक चट्टानों की परतों को साफ़ करते हैं, तो उन्हें प्राचीन जीवाश्म और पदचिह्न मिलते हैं!",
            p3En = "Every fossil is like a page in Earth's ancient history book waiting to be read and understood!",
            p3Ur = "ہر فوسل زمین کی قدیم تاریخ کی کتاب کے ایک صفحے کی طرح ہے جو ہمیں ماضی کے راز بتاتا ہے!",
            p3Hi = "हर जीवाश्म पृथ्वी के प्राचीन इतिहास की किताब के एक पन्ने की तरह है!",
            secondImg = "books/alphabets/alpha_mountain.webp",
            thirdImg = "books/alphabets/alpha_t_tree.webp",
            practice = setOf("walk", "trees", "books", "read"),
            quizQ = "What do scientists called paleontologists study to learn about prehistoric dinosaurs?",
            quizOpts = listOf("Fossilized footprints and bones preserved in rock", "Modern computer games", "Cloud shapes")
        ),
        buildReferenceStory(
            id = "ref_galactica_telescope_night",
            title = "Amina's Rooftop Telescope",
            urduTitle = "آمنہ کی دوربین اور کہکشاں کا نظارہ",
            hindiTitle = "आमना की दूरबीन और आकाशगंगा",
            author = "Salman Hameed • Astronomy Pack",
            level = 4,
            coverImg = "books/stories/story_telescope.webp",
            categoryTag = "Science & Weather",
            isDailyDrop = true,
            p1En = "On clear cloudless nights, Amina sets up her brass telescope on the rooftop to observe the night sky.",
            p1Ur = "صاف اور بے بادل راتوں میں آمنہ چھت پر اپنی دوربین لگا کر رات کے آسمان کا مشاہدہ کرتی ہے۔",
            p1Hi = "साफ़ रातों में आमना छत पर अपनी दूरबीन लगाकर रात के आसमान का निरीक्षण करती है।",
            p2En = "Through the glass lens, she spots the craters of the moon, the golden rings of Saturn, and four moons of Jupiter!",
            p2Ur = "عدسے کے ذریعے وہ چاند کی سطح، زحل کے سنہری دائرے اور مشتری کے چار چاند صاف دیکھتی ہے!",
            p2Hi = "लेंस के माध्यम से वह चाँद की सतह और शनि ग्रह के सुनहरे वलय साफ़ देखती है!",
            p3En = "Amina keeps an astronomy journal of every star cluster she discovers, dreaming of becoming an astrophysicist!",
            p3Ur = "آمنہ اپنی ڈائری میں ہر ستارے کا نقشہ بناتی ہے اور مستقبل کی عظیم ماہرِ فلکیات بننے کا عزم رکھتی ہے!",
            p3Hi = "आमना अपनी डायरी में हर तारे का चित्र बनाती है और खगोल वैज्ञानिक बनने का सपना देखती है!",
            secondImg = "books/alphabets/alpha_moon.webp",
            thirdImg = "books/stories/story_rocket.webp",
            practice = setOf("sky", "moon", "golden", "stars"),
            quizQ = "What planet's famous golden rings did Amina see through her rooftop telescope?",
            quizOpts = listOf("Saturn's golden rings", "Mars's traffic lanes", "Mercury's oceans")
        ),
        buildReferenceStory(
            id = "ref_airplane_above_the_clouds",
            title = "How Airplanes Soar High",
            urduTitle = "ہوائی جہاز بادلوں کے اوپر کیسے اڑتا ہے؟",
            hindiTitle = "हवाई जहाज़ बादलों के ऊपर कैसे उड़ता है?",
            author = "Young Engineers Library",
            level = 4,
            coverImg = "books/stories/story_airplane.webp",
            categoryTag = "Adventure & Travel",
            p1En = "Have you ever watched a giant passenger airplane lift smoothly off the runway and wondered how it stays in the air?",
            p1Ur = "کیا آپ نے کبھی بڑے مسافر بردار ہوائی جہاز کو رن وے سے اڑتے دیکھ کر سوچا ہے کہ وہ ہوا میں کیسے تیرتا ہے؟",
            p1Hi = "क्या आपने कभी बड़े हवाई जहाज़ को उड़ते देखकर सोचा है कि वह हवा में कैसे तैरता है?",
            p2En = "The curved shape of an airplane's wings causes air to move faster over the top than underneath, creating upward lift!",
            p2Ur = "جہاز کے پروں کی خاص خم دار بناوٹ ہوا کا دباؤ اس طرح بناتی ہے جو جہاز کو اوپر کی طرف اٹھائے رکھتی ہے!",
            p2Hi = "जहाज़ के पंखों की विशेष बनावट हवा का ऐसा दबाव बनाती है जो उसे ऊपर उठाए रखती है!",
            p3En = "Inspired by soaring birds, aviation connects families and cultures across every continent on Earth!",
            p3Ur = "پرندوں کی اڑان سے متاثر ہو کر بنایا گیا ہوائی جہاز آج پوری دنیا کے انسانوں اور ثقافتوں کو آپس میں جوڑتا ہے!",
            p3Hi = "पक्षियों की उड़ान से प्रेरित हवाई जहाज़ आज पूरी दुनिया के लोगों को आपस में जोड़ता है!",
            secondImg = "books/stories/story_globe.webp",
            thirdImg = "books/stories/story_rainbow.webp",
            practice = setOf("flying", "wings", "clouds", "sky"),
            quizQ = "What part of an airplane is curved to create upward lift as air flows over it?",
            quizOpts = listOf("The airplane's wings", "The passenger seats", "The luggage wheels")
        ),
        buildReferenceStory(
            id = "ref_octopus_clever_camouflage",
            title = "Ollie Octopus'sEight Clever Arms",
            urduTitle = "آٹھ بازوؤں والا عقلمند آکٹوپس",
            hindiTitle = "आठ भुजाओं वाला चतुर ऑक्टोपस",
            author = "Deep Sea Discovery",
            level = 4,
            coverImg = "books/stories/story_octopus.webp",
            categoryTag = "Animals & Nature",
            p1En = "Down among the colorful coral gardens of the ocean floor lives Ollie, a clever eight-armed octopus.",
            p1Ur = "سمندر کی تہہ میں رنگین مرجان کے باغات کے درمیان آٹھ بازوؤں والا ایک ذہین آکٹوپس رہتا ہے۔",
            p1Hi = "समुद्र की गहराई में मूंगे के बग़ीचों के बीच आठ भुजाओं वाला एक चतुर ऑक्टोपस रहता है।",
            p2En = "An octopus can change the color and texture of its skin in a single second to match coral, sand, or seaweed!",
            p2Ur = "آکٹوپس صرف ایک سیکنڈ میں اپنی جلد کا رنگ بدل کر ریت، چٹان یا سمندری پودوں جیسا بن سکتا ہے!",
            p2Hi = "ऑक्टोपस केवल एक सेकंड में अपनी त्वचा का रंग बदलकर चट्टान या रेत जैसा बन सकता है!",
            p3En = "With three hearts and remarkable problem-solving intelligence, the octopus is one of nature's greatest marvels!",
            p3Ur = "تین دلوں اور حیرت انگیز ذہانت کے ساتھ آکٹوپس سمندری دنیا کے سب سے دلچسپ عجائبات میں سے ایک ہے!",
            p3Hi = "अद्भुत बुद्धिमत्ता के साथ ऑक्टोपस समुद्री दुनिया के सबसे अनोखे जीवों में से एक है!",
            secondImg = "books/alphabets/alpha_f_fish.webp",
            thirdImg = "books/stories/story_whale.webp",
            practice = setOf("ocean", "colors", "water", "special", "unique"),
            quizQ = "How fast can an octopus change the color of its skin to blend in with coral or sand?",
            quizOpts = listOf("In a single second", "It takes three months", "It cannot change color")
        ),
        buildReferenceStory(
            id = "ref_planet_earth_green_promise",
            title = "Our Blue and Green Planet Earth",
            urduTitle = "ہماری نیلی اور ہری زمین کی حفاظت",
            hindiTitle = "हमारी नीली और हरी पृथ्वी की रक्षा",
            author = "United Readers Earth Pack",
            level = 4,
            coverImg = "books/stories/story_globe.webp",
            categoryTag = "School & Kindness",
            isDailyDrop = true,
            p1En = "Planet Earth is home to billions of people, towering mountains, deep blue oceans, and lush green forests.",
            p1Ur = "ہماری زمین اربوں انسانوں، بلند پہاڑوں، گہرے نیلے سمندروں اور سرسبز جنگلات کا مشترکہ گھر ہے۔",
            p1Hi = "हमारी पृथ्वी अरबों लोगों, ऊँचे पहाड़ों, गहरे महासागरों और हरे जंगलों का साझा घर है।",
            p2En = "When school students plant trees, save water, and recycle paper, they help keep Earth's climate healthy and balanced.",
            p2Ur = "جب طلبہ درخت لگاتے ہیں، پانی بچاتے ہیں اور صفائی کا خیال رکھتے ہیں تو وہ زمین کے ماحول کو صحت مند رکھتے ہیں۔",
            p2Hi = "जब विद्यार्थी पेड़ लगाते हैं और पानी बचाते हैं, तो वे पृथ्वी के पर्यावरण को स्वस्थ रखते हैं।",
            p3En = "Every small act of care we do today builds a brighter, greener world for tomorrow!",
            p3Ur = "آج کیا گیا ہمارا ہر چھوٹا مثبت قدم آنے والے کل کے لیے ایک سرسبز اور روشن دنیا تعمیر کرتا ہے!",
            p3Hi = "आज किया गया हमारा हर छोटा प्रयास कल के लिए एक हरा-भरा और उज्ज्वल संसार बनाता है!",
            secondImg = "books/alphabets/alpha_t_tree.webp",
            thirdImg = "books/stories/story_sunflower.webp",
            practice = setOf("blue", "green", "ocean", "trees", "water", "school", "bright"),
            quizQ = "How can students help keep planet Earth healthy and green?",
            quizOpts = listOf("By planting trees, saving water, and recycling", "By wasting water", "By cutting down garden flowers")
        ),
        buildReferenceStory(
            id = "ref_spotted_deer_meadow",
            title = "Chital the Spotted Forest Deer",
            urduTitle = "جنگل کا سنہری دھبوں والا ہرن",
            hindiTitle = "जंगल का सुनहरे धब्बों वाला हिरण",
            author = "Jim Corbett Nature Library",
            level = 3,
            coverImg = "books/stories/story_deer.webp",
            categoryTag = "Animals & Nature",
            p1En = "In the dappled morning sunlight of the forest clearing stands Chital, a graceful deer with white spots on his golden coat.",
            p1Ur = "جنگل کی صبح کی دھوپ میں چیتل نامی ایک خوبصورت ہرن کھڑا ہے جس کی سنہری کھال پر سفید دھبے ہیں۔",
            p1Hi = "जंगल की सुबह की धूप में चीतल नाम का एक सुंदर हिरण खड़ा है जिसके सुनहरे शरीर पर सफ़ेद धब्बे हैं।",
            p2En = "Chital and Milo Monkey are best friends: from high in the treetops, Milo drops fresh leaves and warns the deer of danger!",
            p2Ur = "ہرن اور بندر گہرے دوست ہیں: درختوں کی بلندی سے بندر تازہ پتے گراتا ہے اور ہرنوں کو ہر خطرے سے آگاہ کرتا ہے!",
            p2Hi = "हिरण और बंदर गहरे दोस्त हैं: पेड़ की ऊँचाई से बंदर ताज़ा पत्ते गिराता है और हिरणों को सतर्क करता है!",
            p3En = "Teamwork and friendship in nature help all creatures live in harmony beside the forest stream!",
            p3Ur = "فطرت میں باہمی تعاون اور دوستی تمام جانداروں کو جنگل کی ندی کے کنارے امن سے رہنے میں مدد دیتی ہے!",
            p3Hi = "प्रकृति में आपसी सहयोग और दोस्ती सभी जीवों को शांति से रहने में मदद करती है!",
            secondImg = "books/stories/story_monkey.webp",
            thirdImg = "books/alphabets/alpha_t_tree.webp",
            practice = setOf("morning", "sunlight", "golden", "white", "friends", "leaves"),
            quizQ = "How does Milo Monkey help his friend Chital the deer from high in the treetops?",
            quizOpts = listOf("Drops fresh leaves and warns the deer of any danger", "Teaches him algebra", "Builds a brick wall")
        )
    )

    private val distinctCoverPool: List<String> = listOf(
        "books/stories/story_monkey.webp",
        "books/stories/story_tiger.webp",
        "books/stories/story_fox.webp",
        "books/stories/story_bear.webp",
        "books/stories/story_giraffe.webp",
        "books/stories/story_panda.webp",
        "books/stories/story_penguin.webp",
        "books/stories/story_whale.webp",
        "books/stories/story_dolphin.webp",
        "books/stories/story_dino.webp",
        "books/stories/story_rocket.webp",
        "books/stories/story_airplane.webp",
        "books/stories/story_helicopter.webp",
        "books/stories/story_firetruck.webp",
        "books/stories/story_schoolbus.webp",
        "books/stories/story_tractor.webp",
        "books/stories/story_robot.webp",
        "books/stories/story_telescope.webp",
        "books/stories/story_castle.webp",
        "books/stories/story_guitar.webp",
        "books/stories/story_palette.webp",
        "books/stories/story_snowman.webp",
        "books/stories/story_campfire.webp",
        "books/stories/story_rainbow.webp",
        "books/stories/story_sunflower.webp",
        "books/stories/story_honeybee.webp",
        "books/stories/story_snail.webp",
        "books/stories/story_deer.webp",
        "books/stories/story_squirrel.webp",
        "books/stories/story_swan.webp",
        "books/stories/story_octopus.webp",
        "books/stories/story_globe.webp",
        "books/alphabets/alpha_l_lion.webp",
        "books/alphabets/alpha_e_elephant.webp",
        "books/alphabets/alpha_r_rabbit.webp",
        "books/alphabets/alpha_peacock.webp",
        "books/alphabets/alpha_turtle.webp",
        "books/alphabets/alpha_camel.webp",
        "books/alphabets/alpha_owl.webp",
        "books/alphabets/alpha_z_zebra.webp",
        "books/alphabets/alpha_p_parrot.webp",
        "books/alphabets/alpha_duck.webp",
        "books/alphabets/alpha_horse.webp",
        "books/alphabets/alpha_c_cat.webp",
        "books/alphabets/alpha_m_mango.webp",
        "books/alphabets/alpha_g_grapes.webp",
        "books/alphabets/alpha_balloon.webp",
        "books/alphabets/alpha_k_kite.webp",
        "books/alphabets/alpha_bicycle.webp",
        "books/alphabets/alpha_y_yacht.webp",
        "books/alphabets/alpha_q_queen.webp",
        "books/alphabets/alpha_x_xylophone.webp",
        "books/alphabets/alpha_mountain.webp",
        "books/alphabets/alpha_n_nest.webp",
        "books/alphabets/alpha_pencil.webp",
        "books/alphabets/alpha_u_umbrella.webp",
        "books/alphabets/alpha_i_icecream.webp",
        "books/alphabets/alpha_w_watermelon.webp",
        "books/alphabets/alpha_anar_pomegranate.webp",
        "books/alphabets/alpha_pineapple.webp",
        "books/alphabets/alpha_moon.webp",
        "books/alphabets/alpha_d_drum.webp",
        "books/alphabets/alpha_f_fish.webp",
        "books/alphabets/alpha_h_house.webp",
        "books/alphabets/alpha_t_tree.webp",
        "books/alphabets/alpha_s_sun.webp",
        "books/alphabets/alpha_flower.webp",
        "books/alphabets/alpha_butterfly.webp",
        "books/alphabets/alpha_frog.webp"
    )

    val defaultBooks: List<StoryBook> = run {
        val coreTopSeven = rawDefaultBooks.take(7).map { enrichToFullIllustratedStory(it) }
        val remainingOriginal = rawDefaultBooks.drop(7).map { enrichToFullIllustratedStory(it) }
        referenceExpandedStories + coreTopSeven + remainingOriginal
    }

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
