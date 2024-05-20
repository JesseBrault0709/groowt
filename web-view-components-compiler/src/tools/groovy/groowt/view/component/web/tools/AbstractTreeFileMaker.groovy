package groowt.view.component.web.tools

import java.util.regex.Pattern

abstract class AbstractTreeFileMaker implements SourceFileProcessor {

    private static final Pattern withoutExtension = ~/(?<name>.*)\.(?<ext>.+)/

    private final Scanner scanner = new Scanner(System.in)

    boolean dryRun
    String suffix
    String extension
    File outputDirectory
    boolean autoYes
    boolean verbose

    protected String getNameWithoutExtension(File file) {
        def m = withoutExtension.matcher(file.name)
        if (m.matches()) {
            return m.group('name')
        } else {
            throw new IllegalArgumentException("Could not determine file name without extension for ${file}")
        }
    }

    protected boolean getYesNoInput(String prompt, boolean force = false) {
        if (this.autoYes && !force) {
            return true
        } else {
            print prompt + ' '
            while (true) {
                if (this.scanner.hasNextLine()) {
                    def input = this.scanner.nextLine()
                    if (input in ['y', 'n']) {
                        return input == 'y'
                    }
                }
            }
        }
    }

}
