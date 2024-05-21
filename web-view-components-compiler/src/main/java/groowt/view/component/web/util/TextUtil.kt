@file:JvmName("TextUtil")
package groowt.view.component.web.util

fun excerpt(s: String, startLength: Int = 30, endLength: Int = 7, ellipsis: String = "..."): String {
    if (s.length > startLength + endLength + ellipsis.length) {
        val start = s.substring(0..<startLength)
        val end = s.substring((s.length - endLength)..<s.length)
        return start + ellipsis + end
    } else {
        return s
    }
}
