#!/usr/bin/env python3
"""
Build Quran SQLite database from AlQuran.cloud API.
"""
import sqlite3
import time
import os
import sys

try:
    import requests
    def fetch_url(url):
        r = requests.get(url, timeout=30)
        r.raise_for_status()
        return r.json()
except ImportError:
    import urllib.request
    import json
    def fetch_url(url):
        with urllib.request.urlopen(url, timeout=30) as r:
            return json.loads(r.read().decode('utf-8'))

DB_PATH = "C:/Users/om_hi/claude_playground/muslim_bro/core/data/src/main/assets/quran_data.db"

# Surah metadata: (number, nameArabic, nameTranslation, revelationType, versesCount)
SURAH_META = [
    (1, "Al-Fatihah", "الفاتحة", "The Opening", "Meccan", 7),
    (2, "Al-Baqarah", "البقرة", "The Cow", "Medinan", 286),
    (3, "Ali 'Imran", "آل عمران", "The Family of Imran", "Medinan", 200),
    (4, "An-Nisa", "النساء", "The Women", "Medinan", 176),
    (5, "Al-Ma'idah", "المائدة", "The Table Spread", "Medinan", 120),
    (6, "Al-An'am", "الأنعام", "The Cattle", "Meccan", 165),
    (7, "Al-A'raf", "الأعراف", "The Heights", "Meccan", 206),
    (8, "Al-Anfal", "الأنفال", "The Spoils of War", "Medinan", 75),
    (9, "At-Tawbah", "التوبة", "The Repentance", "Medinan", 129),
    (10, "Yunus", "يونس", "Jonah", "Meccan", 109),
    (11, "Hud", "هود", "Hud", "Meccan", 123),
    (12, "Yusuf", "يوسف", "Joseph", "Meccan", 111),
    (13, "Ar-Ra'd", "الرعد", "The Thunder", "Medinan", 43),
    (14, "Ibrahim", "إبراهيم", "Abraham", "Meccan", 52),
    (15, "Al-Hijr", "الحجر", "The Rocky Tract", "Meccan", 99),
    (16, "An-Nahl", "النحل", "The Bee", "Meccan", 128),
    (17, "Al-Isra", "الإسراء", "The Night Journey", "Meccan", 111),
    (18, "Al-Kahf", "الكهف", "The Cave", "Meccan", 110),
    (19, "Maryam", "مريم", "Mary", "Meccan", 98),
    (20, "Ta-Ha", "طه", "Ta-Ha", "Meccan", 135),
    (21, "Al-Anbya", "الأنبياء", "The Prophets", "Meccan", 112),
    (22, "Al-Hajj", "الحج", "The Pilgrimage", "Medinan", 78),
    (23, "Al-Mu'minun", "المؤمنون", "The Believers", "Meccan", 118),
    (24, "An-Nur", "النور", "The Light", "Medinan", 64),
    (25, "Al-Furqan", "الفرقان", "The Criterion", "Meccan", 77),
    (26, "Ash-Shu'ara", "الشعراء", "The Poets", "Meccan", 227),
    (27, "An-Naml", "النمل", "The Ant", "Meccan", 93),
    (28, "Al-Qasas", "القصص", "The Stories", "Meccan", 88),
    (29, "Al-'Ankabut", "العنكبوت", "The Spider", "Meccan", 69),
    (30, "Ar-Rum", "الروم", "The Romans", "Meccan", 60),
    (31, "Luqman", "لقمان", "Luqman", "Meccan", 34),
    (32, "As-Sajdah", "السجدة", "The Prostration", "Meccan", 30),
    (33, "Al-Ahzab", "الأحزاب", "The Combined Forces", "Medinan", 73),
    (34, "Saba", "سبأ", "Sheba", "Meccan", 54),
    (35, "Fatir", "فاطر", "Originator", "Meccan", 45),
    (36, "Ya-Sin", "يس", "Ya Sin", "Meccan", 83),
    (37, "As-Saffat", "الصافات", "Those who set the Ranks", "Meccan", 182),
    (38, "Sad", "ص", "The Letter Sad", "Meccan", 88),
    (39, "Az-Zumar", "الزمر", "The Troops", "Meccan", 75),
    (40, "Ghafir", "غافر", "The Forgiver", "Meccan", 85),
    (41, "Fussilat", "فصلت", "Explained in Detail", "Meccan", 54),
    (42, "Ash-Shuraa", "الشورى", "The Consultation", "Meccan", 53),
    (43, "Az-Zukhruf", "الزخرف", "The Ornaments of Gold", "Meccan", 89),
    (44, "Ad-Dukhan", "الدخان", "The Smoke", "Meccan", 59),
    (45, "Al-Jathiyah", "الجاثية", "The Crouching", "Meccan", 37),
    (46, "Al-Ahqaf", "الأحقاف", "The Wind-Curved Sandhills", "Meccan", 35),
    (47, "Muhammad", "محمد", "Muhammad", "Medinan", 38),
    (48, "Al-Fath", "الفتح", "The Victory", "Medinan", 29),
    (49, "Al-Hujurat", "الحجرات", "The Rooms", "Medinan", 18),
    (50, "Qaf", "ق", "The Letter Qaf", "Meccan", 45),
    (51, "Adh-Dhariyat", "الذاريات", "The Winnowing Winds", "Meccan", 60),
    (52, "At-Tur", "الطور", "The Mount", "Meccan", 49),
    (53, "An-Najm", "النجم", "The Star", "Meccan", 62),
    (54, "Al-Qamar", "القمر", "The Moon", "Meccan", 55),
    (55, "Ar-Rahman", "الرحمن", "The Beneficent", "Medinan", 78),
    (56, "Al-Waqi'ah", "الواقعة", "The Inevitable", "Meccan", 96),
    (57, "Al-Hadid", "الحديد", "The Iron", "Medinan", 29),
    (58, "Al-Mujadila", "المجادلة", "The Pleading Woman", "Medinan", 22),
    (59, "Al-Hashr", "الحشر", "The Exile", "Medinan", 24),
    (60, "Al-Mumtahanah", "الممتحنة", "She that is to be examined", "Medinan", 13),
    (61, "As-Saf", "الصف", "The Ranks", "Medinan", 14),
    (62, "Al-Jumu'ah", "الجمعة", "The Congregation, Friday", "Medinan", 11),
    (63, "Al-Munafiqun", "المنافقون", "The Hypocrites", "Medinan", 11),
    (64, "At-Taghabun", "التغابن", "The Mutual Disillusion", "Medinan", 18),
    (65, "At-Talaq", "الطلاق", "The Divorce", "Medinan", 12),
    (66, "At-Tahrim", "التحريم", "The Prohibition", "Medinan", 12),
    (67, "Al-Mulk", "الملك", "The Sovereignty", "Meccan", 30),
    (68, "Al-Qalam", "القلم", "The Pen", "Meccan", 52),
    (69, "Al-Haqqah", "الحاقة", "The Reality", "Meccan", 52),
    (70, "Al-Ma'arij", "المعارج", "The Ascending Stairways", "Meccan", 44),
    (71, "Nuh", "نوح", "Noah", "Meccan", 28),
    (72, "Al-Jinn", "الجن", "The Jinn", "Meccan", 28),
    (73, "Al-Muzzammil", "المزمل", "The Enshrouded One", "Meccan", 20),
    (74, "Al-Muddaththir", "المدثر", "The Cloaked One", "Meccan", 56),
    (75, "Al-Qiyamah", "القيامة", "The Resurrection", "Meccan", 40),
    (76, "Al-Insan", "الإنسان", "The Man", "Medinan", 31),
    (77, "Al-Mursalat", "المرسلات", "The Emissaries", "Meccan", 50),
    (78, "An-Naba", "النبأ", "The Tidings", "Meccan", 40),
    (79, "An-Nazi'at", "النازعات", "Those who drag forth", "Meccan", 46),
    (80, "Abasa", "عبس", "He Frowned", "Meccan", 42),
    (81, "At-Takwir", "التكوير", "The Overthrowing", "Meccan", 29),
    (82, "Al-Infitar", "الانفطار", "The Cleaving", "Meccan", 19),
    (83, "Al-Mutaffifin", "المطففين", "The Defrauding", "Meccan", 36),
    (84, "Al-Inshiqaq", "الانشقاق", "The Sundering", "Meccan", 25),
    (85, "Al-Buruj", "البروج", "The Mansions of the Stars", "Meccan", 22),
    (86, "At-Tariq", "الطارق", "The Nightcommer", "Meccan", 17),
    (87, "Al-Ala", "الأعلى", "The Most High", "Meccan", 19),
    (88, "Al-Ghashiyah", "الغاشية", "The Overwhelming", "Meccan", 26),
    (89, "Al-Fajr", "الفجر", "The Dawn", "Meccan", 30),
    (90, "Al-Balad", "البلد", "The City", "Meccan", 20),
    (91, "Ash-Shams", "الشمس", "The Sun", "Meccan", 15),
    (92, "Al-Layl", "الليل", "The Night", "Meccan", 21),
    (93, "Ad-Duha", "الضحى", "The Morning Hours", "Meccan", 11),
    (94, "Ash-Sharh", "الشرح", "The Relief", "Meccan", 8),
    (95, "At-Tin", "التين", "The Fig", "Meccan", 8),
    (96, "Al-Alaq", "العلق", "The Clot", "Meccan", 19),
    (97, "Al-Qadr", "القدر", "The Power", "Meccan", 5),
    (98, "Al-Bayyinah", "البينة", "The Clear Proof", "Medinan", 8),
    (99, "Az-Zalzalah", "الزلزلة", "The Earthquake", "Medinan", 8),
    (100, "Al-Adiyat", "العاديات", "The Courser", "Meccan", 11),
    (101, "Al-Qari'ah", "القارعة", "The Calamity", "Meccan", 11),
    (102, "At-Takathur", "التكاثر", "The Rivalry in world increase", "Meccan", 8),
    (103, "Al-Asr", "العصر", "The Declining Day", "Meccan", 3),
    (104, "Al-Humazah", "الهمزة", "The Traducer", "Meccan", 9),
    (105, "Al-Fil", "الفيل", "The Elephant", "Meccan", 5),
    (106, "Quraysh", "قريش", "Quraysh", "Meccan", 4),
    (107, "Al-Ma'un", "الماعون", "The Small Kindnesses", "Meccan", 7),
    (108, "Al-Kawthar", "الكوثر", "The Abundance", "Meccan", 3),
    (109, "Al-Kafirun", "الكافرون", "The Disbelievers", "Meccan", 6),
    (110, "An-Nasr", "النصر", "The Divine Support", "Medinan", 3),
    (111, "Al-Masad", "المسد", "The Palm Fibre", "Meccan", 5),
    (112, "Al-Ikhlas", "الإخلاص", "The Sincerity", "Meccan", 4),
    (113, "Al-Falaq", "الفلق", "The Daybreak", "Meccan", 5),
    (114, "An-Nas", "الناس", "Mankind", "Meccan", 6),
]

# Juz boundaries: (surah, verse) -> juz number (approximate)
JUZ_STARTS = [
    (1,1), (2,142), (2,253), (3,93), (4,24), (4,148), (5,82), (6,111),
    (7,88), (8,41), (9,93), (11,6), (12,53), (15,1), (17,1), (18,75),
    (21,1), (23,1), (25,21), (27,56), (29,46), (33,31), (36,28), (39,32),
    (41,47), (46,1), (51,31), (58,1), (67,1), (78,1)
]

def get_juz(surah, verse):
    juz = 1
    for i, (s, v) in enumerate(JUZ_STARTS):
        if surah > s or (surah == s and verse >= v):
            juz = i + 1
        else:
            break
    return juz

def get_hizb(surah, verse, total_verses_before):
    # Approximate: 60 hizbs total, ~1000 verses each rough division
    # Simple approximation based on position in Quran (6236 total verses)
    pos = total_verses_before + verse
    return min(60, max(1, (pos * 60) // 6236 + 1))

# Verses with sajda (prostration)
SAJDA_VERSES = {
    (7, 206), (13, 15), (16, 50), (17, 109), (19, 58), (22, 18),
    (22, 77), (25, 60), (27, 26), (32, 15), (38, 24), (41, 38),
    (53, 62), (84, 21), (96, 19)
}

def main():
    os.makedirs(os.path.dirname(DB_PATH), exist_ok=True)

    if os.path.exists(DB_PATH):
        os.remove(DB_PATH)
        print(f"Removed existing database at {DB_PATH}")

    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()

    # Create tables
    cur.executescript("""
        CREATE TABLE surahs (
            number INTEGER PRIMARY KEY,
            name TEXT NOT NULL,
            nameArabic TEXT NOT NULL,
            nameTranslation TEXT NOT NULL,
            revelationType TEXT NOT NULL,
            versesCount INTEGER NOT NULL
        );

        CREATE TABLE verses (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            surahNumber INTEGER NOT NULL,
            verseNumber INTEGER NOT NULL,
            textUthmani TEXT NOT NULL,
            textSimple TEXT NOT NULL,
            juz INTEGER NOT NULL,
            hizb INTEGER NOT NULL,
            sajda INTEGER NOT NULL DEFAULT 0
        );

        CREATE INDEX idx_verses_surah ON verses(surahNumber);
    """)
    conn.commit()
    print("Tables created.")

    # Insert surah metadata
    for s in SURAH_META:
        cur.execute(
            "INSERT INTO surahs (number, name, nameArabic, nameTranslation, revelationType, versesCount) VALUES (?,?,?,?,?,?)",
            (s[0], s[1], s[2], s[3], s[4], s[5])
        )
    conn.commit()
    print("Surah metadata inserted.")

    # Fetch verses from API
    total_verses_before = 0
    verse_id = 0

    for surah_num in range(1, 115):
        meta = SURAH_META[surah_num - 1]
        name = meta[1]
        verses_count = meta[5]

        print(f"Fetching surah {surah_num}/114: {name}...", flush=True)

        max_retries = 3
        data = None
        for attempt in range(max_retries):
            try:
                url = f"https://api.alquran.cloud/v1/surah/{surah_num}/quran-uthmani"
                data = fetch_url(url)
                if data.get('code') == 200:
                    break
                else:
                    print(f"  API error: {data.get('status')} - retrying...")
                    time.sleep(2)
            except Exception as e:
                print(f"  Error (attempt {attempt+1}): {e}")
                if attempt < max_retries - 1:
                    time.sleep(3)
                else:
                    print(f"  FAILED to fetch surah {surah_num}, using placeholder")
                    data = None

        if data and data.get('code') == 200:
            ayahs = data['data']['ayahs']
            for ayah in ayahs:
                verse_num = ayah['numberInSurah']
                text_uthmani = ayah['text']
                text_simple = text_uthmani  # Use same text for both fields
                juz = ayah.get('juz', get_juz(surah_num, verse_num))
                hizb_quarter = ayah.get('hizbQuarter', 0)
                hizb = (hizb_quarter + 1) // 2 if hizb_quarter else get_hizb(surah_num, verse_num, total_verses_before)
                sajda = 1 if (surah_num, verse_num) in SAJDA_VERSES else 0

                verse_id += 1
                cur.execute(
                    "INSERT INTO verses (id, surahNumber, verseNumber, textUthmani, textSimple, juz, hizb, sajda) VALUES (?,?,?,?,?,?,?,?)",
                    (verse_id, surah_num, verse_num, text_uthmani, text_simple, juz, hizb, sajda)
                )
        else:
            # Insert placeholder verses if API failed
            for verse_num in range(1, verses_count + 1):
                verse_id += 1
                juz = get_juz(surah_num, verse_num)
                hizb = get_hizb(surah_num, verse_num, total_verses_before)
                sajda = 1 if (surah_num, verse_num) in SAJDA_VERSES else 0
                placeholder = f"[Surah {surah_num}, Verse {verse_num}]"
                cur.execute(
                    "INSERT INTO verses (id, surahNumber, verseNumber, textUthmani, textSimple, juz, hizb, sajda) VALUES (?,?,?,?,?,?,?,?)",
                    (verse_id, surah_num, verse_num, placeholder, placeholder, juz, hizb, sajda)
                )

        total_verses_before += verses_count
        conn.commit()
        time.sleep(0.3)  # Rate limiting

    # Create FTS table
    print("Creating FTS virtual table...", flush=True)
    cur.executescript("""
        CREATE VIRTUAL TABLE verses_fts USING fts5(
            text_simple,
            content=verses,
            content_rowid=id
        );
        INSERT INTO verses_fts(rowid, text_simple) SELECT id, textSimple FROM verses;
    """)
    conn.commit()

    # Print stats
    cur.execute("SELECT COUNT(*) FROM surahs")
    surah_count = cur.fetchone()[0]
    cur.execute("SELECT COUNT(*) FROM verses")
    verse_count = cur.fetchone()[0]

    conn.close()

    db_size = os.path.getsize(DB_PATH)
    print(f"\nDatabase created successfully!")
    print(f"  Surahs: {surah_count}")
    print(f"  Verses: {verse_count}")
    print(f"  File size: {db_size:,} bytes ({db_size/1024/1024:.2f} MB)")
    print(f"  Path: {DB_PATH}")

if __name__ == "__main__":
    main()
