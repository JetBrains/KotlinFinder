interface CellProxy {
    fun reset()
    fun setStrength(value: Int, found: Boolean)
}

data class CellModel(val proxy: CellProxy, var found: Boolean = false) {
    init {
        reset()
    }

    fun reset() {
        found = false
        proxy.reset()
        setStrength(Int.MIN_VALUE)
    }

    fun setStrength(value: Int) {
        if (value >= -55) {
            found = true
        }
        proxy.setStrength(value, found)
    }
}

class GameModel(labels: List<CellProxy>) {
    val cells = labels.map { CellModel(it) }

    fun reset() {
        cells.forEach { it.reset() }
    }

    fun detected(name: String, strength: Int) {
        val cell = toFind.get(name)
        if (cell != null) {
            cells[cell].setStrength(strength)
        }
    }

    val toFind = mapOf(
        "Steel HR 8F" to 0,
        "Apple Watch — Nikolay" to 1,
        "LE-Bose QC35 II" to 2,
        "Amazfit Cor" to 3,
        "Gear S3 (F17D) LE" to 4
    )

}
