package groowt.view.web.tools

import groowt.view.web.transpile.util.GroovyUtil
import org.codehaus.groovy.ast.ImportNode

import static groowt.view.web.transpile.util.GroovyUtil.formatGroovy

def src = '''
import some.Thing
def myVar = 3
class Helper { }
'''

def quickConvertResult = GroovyUtil.convert(src, 'MyTemplate')
println formatGroovy(quickConvertResult.blockStatement())

quickConvertResult. classNodes().each { println formatGroovy(it) }

quickConvertResult. moduleNode().with {
    println "Module: ${it}"
    def indentTimes = 1
    def getIndent = { '  '.repeat(indentTimes) }
    it.properties.each { key, value ->
        if (key instanceof String && key in ['imports', 'starImports']) {
            def importNodes = it[key] as List<ImportNode>
            if (importNodes.size() > 0) {
                println getIndent() + "$key: "
                indentTimes++
                importNodes.each { println getIndent() + it.text }
                indentTimes--
                return
            }
        } else if (key instanceof String && key in ['staticImports', 'staticStarImports']) {
            def staticImports = it[key] as Map<String, ImportNode>
            if (staticImports.size() > 0) {
                println getIndent() + "$key: "
                indentTimes++
                staticImports.each { alias, importNode ->
                    println getIndent() + "$alias: $importNode"
                }
                indentTimes--
                return
            }
        }
        println getIndent() +"$key: $value"
    }
}
