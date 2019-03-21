package eu.slicky.pomesljajcek.util.timber

import timber.log.Timber
import java.util.regex.Pattern


class LinkingDebugTree : Timber.DebugTree() {

    companion object {
        private const val CALL_STACK_INDEX = 5
        private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")
    }

    override fun log(priority: Int, tag: String?, message: String, th: Throwable?) {
        // DO NOT switch this to Thread.getCurrentThread().getStackTrace(). The test will pass
        // because Robolectric runs them on the JVM but on Android the elements are different.
        val stackTrace = Throwable().stackTrace
        if (stackTrace.size <= CALL_STACK_INDEX)
            throw IllegalStateException("Synthetic stacktrace didn't have enough elements - are you using proguard?")
        val clazz = stackTrace[CALL_STACK_INDEX].fileName
        val lineNumber = stackTrace[CALL_STACK_INDEX].lineNumber
        val linkedMessage = "$tag .( $clazz:$lineNumber )"
        super.log(priority, linkedMessage, message, th)
    }

    /**
     * Extract the class name without any anonymous class suffixes (e.g., `Foo$1`
     * becomes `Foo`).
     */
    private fun extractClassName(element: StackTraceElement): String {
        var tag = element.className
        val matcher = ANONYMOUS_CLASS.matcher(tag)
        if (matcher.find())
            tag = matcher.replaceAll("")
        return tag.substring(tag.lastIndexOf('.') + 1)
    }

}