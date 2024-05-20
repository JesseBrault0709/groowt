package groowt.view.component.web.tools

abstract class AbstractOutputFileMaker implements SourceFileProcessor {

    private final Scanner scanner = new Scanner(System.in)

    boolean dryRun
    String suffix
    String extension
    File outputDirectory
    boolean autoYes
    boolean verbose

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

    protected void writeToDisk(String name, String formatted) {
        this.outputDirectory.mkdirs()
        def out = new File(this.outputDirectory, name + this.suffix + this.extension)
        if (out.exists()) {
            if (this.getYesNoInput("${out} already exists. Write over? (y/n)")) {
                println "Writing to $out..."
                out.write(formatted)
            } else {
                println "Skipping writing to $out."
            }
        } else {
            println "Writing to $out..."
            out.write(formatted)
        }
    }

}
