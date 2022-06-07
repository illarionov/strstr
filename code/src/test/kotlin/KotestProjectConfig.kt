import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.names.DuplicateTestNameMode

object KotestProjectConfig : AbstractProjectConfig() {
    override val parallelism: Int = 4
    override val duplicateTestNameMode = DuplicateTestNameMode.Silent
}
