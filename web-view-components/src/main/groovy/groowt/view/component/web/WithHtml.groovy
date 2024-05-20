package groowt.view.component.web

trait WithHtml {

    abstract Map getAttr()

    /**
     * @param writer A {@link java.io.Writer}, a {@link groowt.view.component.runtime.ComponentWriter},
     * or anything which has {@code leftShift(String | Object)} as a method.
     */
    void formatAttr(writer) {
        def iter = attr.iterator()
        while (iter.hasNext()) {
            def entry = iter.next()
            writer << entry.key
            def value = entry.value
            if (value instanceof Boolean) {
                // no-op, because we already wrote the key
            } else {
                writer << '="'
                writer << value
                writer << '"'
            }
            if (iter.hasNext()) {
                writer << ' '
            }
        }
    }

}
