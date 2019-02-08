package com.meowbox.fourpillars

enum class Star(val pinyin: String, val english: String, val description: String, val Rank: Int) {
    ZiWei("Zi Wei", "Emperor", "The Center, ruler of all, Yang Power", 1) {
        override fun findIn(chartParameters: ChartParameters) =
            Branch.num(ziweiBranches[chartParameters.dayOfMonth - 1][chartParameters.innerElement.ordinal])

        private val ziweiBranches = arrayOf(
            arrayOf(4, 9, 6, 11, 1), arrayOf(1, 6, 11, 4, 2), arrayOf(2, 11, 4, 1, 2),
            arrayOf(5, 4, 1, 2, 3), arrayOf(2, 1, 2, 0, 3), arrayOf(3, 2, 7, 5, 4), arrayOf(6, 10, 0, 2, 4),
            arrayOf(3, 7, 5, 3, 5), arrayOf(4, 0, 2, 1, 5), arrayOf(7, 5, 3, 6, 6), arrayOf(4, 2, 8, 3, 6),
            arrayOf(5, 3, 1, 4, 7), arrayOf(8, 11, 6, 2, 7), arrayOf(5, 8, 3, 7, 8), arrayOf(6, 1, 4, 4, 8),
            arrayOf(9, 6, 9, 5, 9), arrayOf(6, 3, 2, 3, 9), arrayOf(7, 4, 7, 8, 10), arrayOf(10, 0, 4, 5, 10),
            arrayOf(7, 9, 5, 6, 11), arrayOf(8, 2, 10, 4, 11), arrayOf(11, 7, 3, 9, 0), arrayOf(8, 4, 8, 6, 0),
            arrayOf(9, 5, 5, 7, 1), arrayOf(0, 1, 6, 5, 1), arrayOf(9, 10, 11, 10, 2), arrayOf(10, 3, 4, 7, 2),
            arrayOf(1, 8, 9, 8, 3), arrayOf(10, 5, 6, 6, 3), arrayOf(11, 6, 7, 11, 4)
        )
    },
    TianFu("Tian Fu", "Empress", "Primary access to everything, Yin power", 1) {
        override fun findIn(chartParameters: ChartParameters) = Branch.Dragon - ZiWei.findIn(chartParameters).ordinal
    },
    TianJi("Tian Ji", "Oracle", "Intuition, unusual knowledge", 1) {
        override fun findIn(chartParameters: ChartParameters) = ZiWei.findIn(chartParameters) - 1
    },
    TaiYang("Tai Yang", "Sun", "Prince, possibility to take it all", 2) {
        override fun findIn(chartParameters: ChartParameters) = ZiWei.findIn(chartParameters) - 3
    },
    WuQu("Wu Qu", "General", "The 'old man' and advisor in tactics", 2) {
        override fun findIn(chartParameters: ChartParameters) = ZiWei.findIn(chartParameters) - 4
    },
    TianTong("Tian Tong", "Vassal", "Power from supporting the leader", 3) {
        override fun findIn(chartParameters: ChartParameters) = ZiWei.findIn(chartParameters) - 5
    },
    LianZhen("Lian Zhen", "Concubine", "Chastity, close to power if beautiful", 4) {
        override fun findIn(chartParameters: ChartParameters) = ZiWei.findIn(chartParameters) + 4
    },
    TianXiang("Tian Xiang", "Tutor", "Power behind throne, access to knowledge", 1) {
        override fun findIn(chartParameters: ChartParameters) = TianFu.findIn(chartParameters) + 4
    },
    TaiYin("Tai Yin", "Moon", "Princess, the link to other realms", 2) {
        override fun findIn(chartParameters: ChartParameters) = TianFu.findIn(chartParameters) + 1
    },
    JuMen("Ju Men", "Great Gate", "Availability of anything", 2) {
        override fun findIn(chartParameters: ChartParameters) = TianFu.findIn(chartParameters) + 3
    },
    TianLiang("Tian Liang", "Roof Beam", "'Feng Shui', feelings of place", 3) {
        override fun findIn(chartParameters: ChartParameters) = TianFu.findIn(chartParameters) + 5
    },
    TanLang("Tan Lang", "Greedy Wolf", "Devourer, takes all", 4) {
        override fun findIn(chartParameters: ChartParameters) = TianFu.findIn(chartParameters) + 2
    },
    QiSha("Qi Sha", "7 Killings", "Death, ruin, bad luck", 4) {
        override fun findIn(chartParameters: ChartParameters) = TianFu.findIn(chartParameters).diametric
    },
    PoJun("Po Jun", "Rebel", "The one who causes trouble", 4) {
        override fun findIn(chartParameters: ChartParameters) = TianFu.findIn(chartParameters) - 2
    },
    //    Based on Hour Branch
    WenChang("Wen Chang", "Magistrate", "The judge, rule of law", 3) {
        override fun findIn(chartParameters: ChartParameters) = Branch.Dog - chartParameters.hourPillar.branch.ordinal
    },
    WenQu("Wen Qu", "Priest", "Access to the divine, rituals", 3) {
        override fun findIn(chartParameters: ChartParameters) = chartParameters.hourPillar.branch + 4
    },
    HuoXing("Huo Xing", "Fire Star", "", 5) {
        override fun findIn(chartParameters: ChartParameters) = Branch.num(
            branchLookup[chartParameters.hourPillar.branch.ordinal][chartParameters.yearPillar.branch.ordinal]
        )

        private val branchLookup = arrayOf(
            arrayOf(2, 3, 1, 9, 2, 3, 1, 9, 2, 3, 1, 9), arrayOf(3, 4, 2, 10, 3, 4, 2, 10, 3, 4, 2, 10),
            arrayOf(4, 5, 3, 11, 4, 5, 3, 11, 4, 5, 3, 11), arrayOf(5, 6, 4, 0, 5, 6, 4, 0, 5, 6, 4, 0),
            arrayOf(6, 7, 5, 1, 6, 7, 5, 1, 6, 7, 5, 1), arrayOf(7, 8, 6, 2, 7, 8, 6, 2, 7, 8, 6, 2),
            arrayOf(8, 9, 7, 3, 8, 9, 7, 3, 8, 9, 7, 3), arrayOf(9, 10, 8, 4, 9, 10, 8, 4, 9, 10, 8, 4),
            arrayOf(10, 11, 9, 5, 10, 11, 9, 5, 10, 11, 9, 5), arrayOf(11, 0, 10, 6, 11, 0, 10, 6, 11, 0, 10, 6),
            arrayOf(0, 1, 11, 7, 0, 1, 11, 7, 0, 1, 11, 7), arrayOf(1, 2, 0, 8, 1, 2, 0, 8, 1, 2, 0, 8)
        )

    },
    LingXing("Ling Xing", "Water Star", "", 5) {
        override fun findIn(chartParameters: ChartParameters) = Branch.num(
            branchLookup[chartParameters.hourPillar.branch.ordinal][chartParameters.yearPillar.branch.ordinal]
        )

        private val branchLookup = arrayOf(
            arrayOf(10, 10, 3, 10, 10, 10, 3, 10, 10, 10, 3, 10), arrayOf(11, 11, 4, 11, 11, 11, 4, 11, 11, 11, 4, 11),
            arrayOf(0, 0, 5, 0, 0, 0, 5, 0, 0, 0, 5, 0), arrayOf(1, 1, 6, 1, 1, 1, 6, 1, 1, 1, 6, 1),
            arrayOf(2, 2, 7, 2, 2, 2, 7, 2, 2, 2, 7, 2), arrayOf(3, 3, 8, 3, 3, 3, 8, 3, 3, 3, 8, 3),
            arrayOf(4, 4, 9, 4, 4, 4, 9, 4, 4, 4, 9, 4), arrayOf(5, 5, 10, 5, 5, 5, 10, 5, 5, 5, 10, 5),
            arrayOf(6, 6, 11, 6, 6, 6, 11, 6, 6, 6, 11, 6), arrayOf(7, 7, 0, 7, 7, 7, 0, 7, 7, 7, 0, 7),
            arrayOf(8, 8, 1, 8, 8, 8, 1, 8, 8, 8, 1, 8), arrayOf(9, 9, 2, 9, 9, 9, 2, 9, 9, 9, 2, 9)
        )
    },
    YangRen("Yang Ren", "Goat Blade", "Injuries, but also competition", 5) {
        private val branchLookup = arrayOf(3, 4, 6, 7, 6, 7, 9, 10, 0, 1)

        override fun findIn(chartParameters: ChartParameters) =
            Branch.num(branchLookup[chartParameters.yearPillar.stem.ordinal])
    },
    TuoLuo("Tuo Luo", "Hump Back", "", 5) {
        private val branchLookup = arrayOf(1, 2, 4, 5, 4, 5, 7, 8, 10, 11)

        override fun findIn(chartParameters: ChartParameters) =
            Branch.num(branchLookup[chartParameters.yearPillar.stem.ordinal])
    },
    YouBi("You Bi", "Right Assistant", "", 6) {
        override fun findIn(chartParameters: ChartParameters) = Branch.Rat - chartParameters.monthPillar.branch.ordinal
    },
    ZuoFu("Zuo Fu", "Left Assistant", "", 6) {
        override fun findIn(chartParameters: ChartParameters) = chartParameters.monthPillar.branch + 2
    },
    TianCun("Tian Cun", "Storehouse", "", 6) {
        private val branchLookup = arrayOf(2, 3, 5, 6, 5, 6, 8, 9, 11, 0)
        override fun findIn(chartParameters: ChartParameters) =
            Branch.num(branchLookup[chartParameters.yearPillar.stem.ordinal])
    },
    TianYao("Tian Yao", "Beauty", "", 6) {
        override fun findIn(chartParameters: ChartParameters) = chartParameters.monthPillar.branch - 1
    },
    TianKui("Tian Kui", "Leader", "", 7) {
        private val branchLookup = arrayOf(1, 0, 11, 9, 7, 8, 7, 6, 5, 3)
        override fun findIn(chartParameters: ChartParameters) =
            Branch.num(branchLookup[chartParameters.yearPillar.stem.ordinal])
    },
    TianXi("Tian Xi", "Happiness", "", 7) {
        override fun findIn(chartParameters: ChartParameters) =
            Branch.Rooster - chartParameters.yearPillar.branch.ordinal
    },
    DiGong("Di Gong", "Void", "", 7) {
        override fun findIn(chartParameters: ChartParameters) = Branch.Pig - chartParameters.hourPillar.branch.ordinal
    },
    TianYue("Tian Yue", "Halberd", "", 7) {
        private val branchLookup = arrayOf(7, 8, 9, 11, 1, 0, 1, 2, 3, 5)

        override fun findIn(chartParameters: ChartParameters) =
            Branch.num(branchLookup[chartParameters.yearPillar.stem.ordinal])
    },
    TianXing("Tian Xing", "Punishment", "", 7) {
        override fun findIn(chartParameters: ChartParameters) = chartParameters.monthPillar.branch + 7
    },
    DiJie("Di Jie", "Loss", "", 7) {
        override fun findIn(chartParameters: ChartParameters) = chartParameters.hourPillar.branch - 1
    };

    abstract fun findIn(chartParameters: ChartParameters): Branch

    companion object {
        val all = Star.values()
    }
}
