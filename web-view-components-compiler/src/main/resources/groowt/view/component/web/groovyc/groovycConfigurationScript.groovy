package groowt.view.component.web.groovyc

import org.codehaus.groovy.control.CompilerConfiguration

(configuration as CompilerConfiguration).pluginFactory = new WvcParserPluginFactory()
