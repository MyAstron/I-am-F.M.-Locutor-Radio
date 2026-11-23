package locutor.cristopher.sic.data.phrases

import java.util.UUID

data class RadioPhrase(
    val id: String = UUID.randomUUID().toString(),
    val template: String,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
