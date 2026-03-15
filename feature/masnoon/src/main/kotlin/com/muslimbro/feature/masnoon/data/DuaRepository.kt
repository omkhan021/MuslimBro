package com.muslimbro.feature.masnoon.data

import com.muslimbro.feature.masnoon.model.Dua
import com.muslimbro.feature.masnoon.model.DuaCategory

object DuaRepository {

    val categories: List<DuaCategory> = listOf(

        DuaCategory(
            id = 1,
            name = "Morning Remembrance",
            arabicName = "أذكار الصباح",
            emoji = "🌅",
            duas = listOf(
                Dua(
                    arabic = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
                    transliteration = "Asbahna wa asbahal-mulku lillah, walhamdu lillah, la ilaha illallahu wahdahu la sharika lah, lahul-mulku wa lahul-hamdu wa huwa 'ala kulli shay'in qadir",
                    translation = "We have entered the morning and the whole kingdom of Allah has entered the morning. Praise is for Allah. None has the right to be worshipped except Allah, alone without associate. To Him belongs dominion and all praise and He is over all things omnipotent.",
                    reference = "Muslim 4:2088"
                ),
                Dua(
                    arabic = "اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ وَإِلَيْكَ النُّشُورُ",
                    transliteration = "Allahumma bika asbahna, wa bika amsayna, wa bika nahya, wa bika namutu wa ilaykan-nushur",
                    translation = "O Allah, by You we have entered the morning, by You we have entered the evening, by You we live and by You we die, and to You is the resurrection.",
                    reference = "Abu Dawud 4:317, Tirmidhi 5:466"
                ),
                Dua(
                    arabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي، فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
                    transliteration = "Allahumma anta rabbi la ilaha illa ant, khalaqtani wa ana 'abduk, wa ana 'ala 'ahdika wa wa'dika mastata't, a'udhu bika min sharri ma sana't, abu'u laka bini'matika 'alayya wa abu'u laka bidhanbi faghfir li, fa'innahu la yaghfirudhdhunuba illa ant",
                    translation = "O Allah, You are my Lord. None has the right to be worshipped except You. You created me and I am Your servant. I abide by Your covenant and promise as best I can. I seek refuge in You from the evil of what I have done. I acknowledge Your blessings upon me and I acknowledge my sin. So forgive me, for none forgives sins except You.",
                    reference = "Bukhari 7:150 — Sayyid al-Istighfar"
                )
            )
        ),

        DuaCategory(
            id = 2,
            name = "Evening Remembrance",
            arabicName = "أذكار المساء",
            emoji = "🌙",
            duas = listOf(
                Dua(
                    arabic = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
                    transliteration = "Amsayna wa amsal-mulku lillah, walhamdu lillah, la ilaha illallahu wahdahu la sharika lah, lahul-mulku wa lahul-hamdu wa huwa 'ala kulli shay'in qadir",
                    translation = "We have entered the evening and the whole kingdom of Allah has entered the evening. Praise is for Allah. None has the right to be worshipped except Allah, alone without associate. To Him belongs dominion and all praise and He is over all things omnipotent.",
                    reference = "Muslim 4:2088"
                ),
                Dua(
                    arabic = "اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ وَإِلَيْكَ الْمَصِيرُ",
                    transliteration = "Allahumma bika amsayna, wa bika asbahna, wa bika nahya, wa bika namutu wa ilaykal-masir",
                    translation = "O Allah, by You we have entered the evening, by You we have entered the morning, by You we live and by You we die, and to You is the journeying.",
                    reference = "Abu Dawud 4:317, Tirmidhi 5:466"
                )
            )
        ),

        DuaCategory(
            id = 3,
            name = "Before Eating",
            arabicName = "دعاء قبل الطعام",
            emoji = "🍽️",
            duas = listOf(
                Dua(
                    arabic = "بِسْمِ اللَّهِ",
                    transliteration = "Bismillah",
                    translation = "In the name of Allah.",
                    reference = "Bukhari, Muslim"
                ),
                Dua(
                    arabic = "اللَّهُمَّ بَارِكْ لَنَا فِيمَا رَزَقْتَنَا وَقِنَا عَذَابَ النَّارِ",
                    transliteration = "Allahumma barik lana fima razaqtana wa qina 'adhaban-nar",
                    translation = "O Allah, bless us in what You have provided us and protect us from the punishment of the fire.",
                    reference = "Ibn al-Sunni"
                )
            )
        ),

        DuaCategory(
            id = 4,
            name = "After Eating",
            arabicName = "دعاء بعد الطعام",
            emoji = "✨",
            duas = listOf(
                Dua(
                    arabic = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنِي هَذَا، وَرَزَقَنِيهِ، مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ",
                    transliteration = "Alhamdu lillahil-ladhi at'amani hadha, wa razaqanihi, min ghayri hawlin minni wa la quwwah",
                    translation = "All praise is for Allah Who has fed me this and provided it for me without any power or effort from me.",
                    reference = "Abu Dawud 4:41, Tirmidhi 5:507"
                ),
                Dua(
                    arabic = "الْحَمْدُ لِلَّهِ حَمْداً كَثِيراً طَيِّباً مُبَارَكاً فِيهِ، غَيْرَ مَكْفِيٍّ وَلَا مُوَدَّعٍ، وَلَا مُسْتَغْنًى عَنْهُ رَبَّنَا",
                    transliteration = "Alhamdu lillahi hamdan kathiran tayyiban mubarakan fih, ghayra makfiyyin wa la muwadda'in, wa la mustaghnan 'anhu rabbana",
                    translation = "All praise is for Allah, praise that is abundant, pure and blessed. It cannot be forsaken, relinquished or dispensed with, O our Lord.",
                    reference = "Bukhari 7:78"
                )
            )
        ),

        DuaCategory(
            id = 5,
            name = "Before Sleep",
            arabicName = "دعاء النوم",
            emoji = "😴",
            duas = listOf(
                Dua(
                    arabic = "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
                    transliteration = "Bismika Allahumma amutu wa ahya",
                    translation = "In Your name, O Allah, I die and I live.",
                    reference = "Bukhari 11:113"
                ),
                Dua(
                    arabic = "اللَّهُمَّ قِنِي عَذَابَكَ يَوْمَ تَبْعَثُ عِبَادَكَ",
                    transliteration = "Allahumma qini 'adhabaka yawma tab'athu 'ibadak",
                    translation = "O Allah, protect me from Your punishment on the day You resurrect Your servants.",
                    reference = "Abu Dawud 4:311, Tirmidhi 5:464"
                ),
                Dua(
                    arabic = "اللَّهُمَّ أَسْلَمْتُ نَفْسِي إِلَيْكَ، وَفَوَّضْتُ أَمْرِي إِلَيْكَ، وَوَجَّهْتُ وَجْهِي إِلَيْكَ، وَأَلْجَأْتُ ظَهْرِي إِلَيْكَ، رَغْبَةً وَرَهْبَةً إِلَيْكَ، لَا مَلْجَأَ وَلَا مَنْجَا مِنْكَ إِلَّا إِلَيْكَ، آمَنْتُ بِكِتَابِكَ الَّذِي أَنْزَلْتَ، وَبِنَبِيِّكَ الَّذِي أَرْسَلْتَ",
                    transliteration = "Allahumma aslamtu nafsi ilayk, wa fawwadtu amri ilayk, wa wajjahtu wajhi ilayk, wa alja'tu zahri ilayk, raghbatan wa rahbatan ilayk, la malja'a wa la manja minka illa ilayk, amantu bikitabikalla dhi anzalt, wa binabiyyikalla dhi arsalt",
                    translation = "O Allah, I submit my soul to You, I entrust my affairs to You, I turn my face to You, I lay myself down relying upon You, in hope and fear of You. There is no refuge nor escape from You except to You. I believe in Your Book which You revealed and in Your Prophet whom You sent.",
                    reference = "Bukhari 11:113"
                )
            )
        ),

        DuaCategory(
            id = 6,
            name = "After Waking Up",
            arabicName = "دعاء الاستيقاظ",
            emoji = "☀️",
            duas = listOf(
                Dua(
                    arabic = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
                    transliteration = "Alhamdu lillahil-ladhi ahyana ba'da ma amatana wa ilayhin-nushur",
                    translation = "All praise is for Allah Who has given us life after causing us to die and to Him is the resurrection.",
                    reference = "Bukhari 11:113"
                ),
                Dua(
                    arabic = "لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، سُبْحَانَ اللهِ، وَالْحَمْدُ لِلَّهِ، وَلَا إِلَهَ إِلَّا اللهُ، وَاللهُ أَكْبَرُ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللهِ",
                    transliteration = "La ilaha illallahu wahdahu la sharika lah, lahul-mulku wa lahul-hamd, wa huwa 'ala kulli shay'in qadir, subhanallah, walhamdu lillah, wa la ilaha illallah, wallahu akbar, wa la hawla wa la quwwata illa billah",
                    translation = "None has the right to be worshipped except Allah, alone, without partner. To Him belongs dominion and all praise, and He is over all things omnipotent. Glory be to Allah. All praise is for Allah. None has the right to be worshipped except Allah. Allah is the greatest. There is no power nor might except with Allah.",
                    reference = "Bukhari 8:75"
                )
            )
        ),

        DuaCategory(
            id = 7,
            name = "Entering Home",
            arabicName = "دعاء دخول المنزل",
            emoji = "🏠",
            duas = listOf(
                Dua(
                    arabic = "بِسْمِ اللَّهِ وَلَجْنَا، وَبِسْمِ اللَّهِ خَرَجْنَا، وَعَلَى اللَّهِ رَبِّنَا تَوَكَّلْنَا",
                    transliteration = "Bismillahi walajna, wa bismillahi kharajna, wa 'alallahi rabbina tawakkalna",
                    translation = "In the name of Allah we enter, in the name of Allah we leave, and upon Allah our Lord we rely.",
                    reference = "Abu Dawud 4:325"
                )
            )
        ),

        DuaCategory(
            id = 8,
            name = "Leaving Home",
            arabicName = "دعاء الخروج من المنزل",
            emoji = "🚪",
            duas = listOf(
                Dua(
                    arabic = "بِسْمِ اللَّهِ، تَوَكَّلْتُ عَلَى اللَّهِ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
                    transliteration = "Bismillah, tawakkaltu 'alallah, wa la hawla wa la quwwata illa billah",
                    translation = "In the name of Allah, I place my trust in Allah, and there is no might nor power except with Allah.",
                    reference = "Abu Dawud 4:325, Tirmidhi 5:490"
                ),
                Dua(
                    arabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ أَنْ أَضِلَّ أَوْ أُضَلَّ، أَوْ أَزِلَّ أَوْ أُزَلَّ، أَوْ أَظْلِمَ أَوْ أُظْلَمَ، أَوْ أَجْهَلَ أَوْ يُجْهَلَ عَلَيَّ",
                    transliteration = "Allahumma inni a'udhu bika an adilla aw udall, aw azilla aw uzall, aw azlima aw uzlam, aw ajhala aw yujhala 'alayy",
                    translation = "O Allah, I seek refuge in You from leading others astray or being led astray, from causing others to slip or slipping myself, from oppressing others or being oppressed, from behaving foolishly or having foolishness done to me.",
                    reference = "Abu Dawud 4:325, Tirmidhi 5:491, Ibn Majah 2:1282"
                )
            )
        ),

        DuaCategory(
            id = 9,
            name = "Entering the Mosque",
            arabicName = "دعاء دخول المسجد",
            emoji = "🕌",
            duas = listOf(
                Dua(
                    arabic = "اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ",
                    transliteration = "Allahumma aftah li abwaba rahmatik",
                    translation = "O Allah, open for me the gates of Your mercy.",
                    reference = "Muslim 1:494, Ibn Majah 1:129"
                )
            )
        ),

        DuaCategory(
            id = 10,
            name = "Leaving the Mosque",
            arabicName = "دعاء الخروج من المسجد",
            emoji = "🕌",
            duas = listOf(
                Dua(
                    arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ",
                    transliteration = "Allahumma inni as'aluka min fadlik",
                    translation = "O Allah, I ask You from Your bounty.",
                    reference = "Muslim 1:494, Abu Dawud 1:126"
                )
            )
        ),

        DuaCategory(
            id = 11,
            name = "Seeking Forgiveness",
            arabicName = "الاستغفار",
            emoji = "🤲",
            duas = listOf(
                Dua(
                    arabic = "أَسْتَغْفِرُ اللَّهَ",
                    transliteration = "Astaghfirullah",
                    translation = "I seek forgiveness from Allah.",
                    reference = "Bukhari, Muslim"
                ),
                Dua(
                    arabic = "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ الَّذِي لَا إِلَهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ وَأَتُوبُ إِلَيْهِ",
                    transliteration = "Astaghfirullaha al-'azimal-ladhi la ilaha illa huwal-hayyul-qayyumu wa atubu ilayh",
                    translation = "I seek forgiveness from Allah, the Magnificent, there is no god but He, the Living, the Self-Subsisting, and I repent to Him.",
                    reference = "Abu Dawud 2:85, Tirmidhi 5:569"
                ),
                Dua(
                    arabic = "رَبَّنَا ظَلَمْنَا أَنْفُسَنَا وَإِنْ لَمْ تَغْفِرْ لَنَا وَتَرْحَمْنَا لَنَكُونَنَّ مِنَ الْخَاسِرِينَ",
                    transliteration = "Rabbana zalamna anfusana wa in lam taghfir lana wa tarhamna lanakunanna minal-khasirin",
                    translation = "Our Lord, we have wronged ourselves, and if You do not forgive us and have mercy upon us, we will surely be among the losers.",
                    reference = "Quran 7:23 — Du'a of Prophet Adam (AS)"
                )
            )
        ),

        DuaCategory(
            id = 12,
            name = "For Parents",
            arabicName = "الدعاء للوالدين",
            emoji = "❤️",
            duas = listOf(
                Dua(
                    arabic = "رَّبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
                    transliteration = "Rabbir-hamhuma kama rabbayani saghira",
                    translation = "My Lord, have mercy upon them as they brought me up when I was small.",
                    reference = "Quran 17:24"
                ),
                Dua(
                    arabic = "رَبَّنَا اغْفِرْ لِي وَلِوَالِدَيَّ وَلِلْمُؤْمِنِينَ يَوْمَ يَقُومُ الْحِسَابُ",
                    transliteration = "Rabbanaghfir li wa liwalidayya wa lil-mu'minina yawma yaqumul-hisab",
                    translation = "Our Lord, forgive me and my parents and the believers the Day the account is established.",
                    reference = "Quran 14:41 — Du'a of Prophet Ibrahim (AS)"
                )
            )
        ),

        DuaCategory(
            id = 13,
            name = "When in Distress",
            arabicName = "دعاء الكرب",
            emoji = "🌊",
            duas = listOf(
                Dua(
                    arabic = "لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ",
                    transliteration = "La ilaha illa anta subhanaka inni kuntu minaz-zalimin",
                    translation = "None has the right to be worshipped except You. Exalted are You. Indeed, I have been of the wrongdoers.",
                    reference = "Quran 21:87 — Du'a of Prophet Yunus (AS)"
                ),
                Dua(
                    arabic = "لَا إِلَهَ إِلَّا اللهُ الْعَظِيمُ الْحَلِيمُ، لَا إِلَهَ إِلَّا اللهُ رَبُّ الْعَرْشِ الْعَظِيمِ، لَا إِلَهَ إِلَّا اللهُ رَبُّ السَّمَوَاتِ وَرَبُّ الْأَرْضِ وَرَبُّ الْعَرْشِ الْكَرِيمِ",
                    transliteration = "La ilaha illallahul-'azimul-halim, la ilaha illallahu rabbul-'arshil-'azim, la ilaha illallahu rabbus-samawati wa rabbul-ardi wa rabbul-'arshil-karim",
                    translation = "None has the right to be worshipped except Allah, the Magnificent, the Forbearing. None has the right to be worshipped except Allah, Lord of the Magnificent Throne. None has the right to be worshipped except Allah, Lord of the heavens and Lord of the earth and Lord of the Noble Throne.",
                    reference = "Bukhari 8:154, Muslim 4:2092"
                )
            )
        ),

        DuaCategory(
            id = 14,
            name = "Traveling",
            arabicName = "دعاء السفر",
            emoji = "✈️",
            duas = listOf(
                Dua(
                    arabic = "اللَّهُ أَكْبَرُ، اللَّهُ أَكْبَرُ، اللَّهُ أَكْبَرُ، سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ، وَإِنَّا إِلَى رَبِّنَا لَمُنْقَلِبُونَ، اللَّهُمَّ إِنَّا نَسْأَلُكَ فِي سَفَرِنَا هَذَا الْبِرَّ وَالتَّقْوَى، وَمِنَ الْعَمَلِ مَا تَرْضَى، اللَّهُمَّ هَوِّنْ عَلَيْنَا سَفَرَنَا هَذَا وَاطْوِ عَنَّا بُعْدَهُ",
                    transliteration = "Allahu akbar, Allahu akbar, Allahu akbar, subhanal-ladhi sakhkhara lana hadha wa ma kunna lahu muqrinin, wa inna ila rabbina lamunqalibun. Allahumma inna nas'aluka fi safarina hadhal-birra wat-taqwa, wa minal-'amali ma tarda. Allahumma hawwin 'alayna safarana hadha watwi 'anna bu'dah",
                    translation = "Allah is the greatest (×3). Glory be to Him Who has made this subservient to us and we ourselves would not have been capable of it. Surely, to our Lord we are returning. O Allah, we ask You for righteousness and piety in this journey of ours, and we ask You for deeds which please You. O Allah, facilitate our journey and make its distance short for us.",
                    reference = "Muslim 2:978, Abu Dawud 3:34"
                )
            )
        )
    )

    fun getCategoryById(id: Int): DuaCategory? = categories.find { it.id == id }
}
