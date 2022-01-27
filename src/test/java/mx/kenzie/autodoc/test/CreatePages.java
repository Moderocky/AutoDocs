package mx.kenzie.autodoc.test;

import mx.kenzie.autodoc.AutoDocs;

import java.io.File;
import java.io.IOException;

public class CreatePages {
    
    public static void main(String[] args) throws IOException {
        AutoDocs.generateDocumentation("AutoDocs", """
                The modern, clean and adaptable documentation generator.
                """,
            """
                ## AutoDocs
                
                AutoDocs provides a modern, clean and easy-to-adapt alternative to the JDK's JavaDocs.
                
                Rather than relying on the outdated, gate-kept JavaDoc API, AutoDocs has an entirely separate implementation.
                
                This has three major advantages over JavaDocs.
                1. The documentation can be extracted from a compiled jar. \\
                JavaDocs are only present in the source files, making it impossible to find documentation unless the source is public. \\
                AutoDocs includes the basic data in the compiled Jar, so that documentation sites can be generated from library Jars or even compiled proprietary software.
                2. The documentation format is controllable. \\
                While the default output is a bootstrap website, AutoDocs is designed to support other output schemas like JSON files. \\
                The default website is easy to customise and add to using 'Annotation Handlers' which allow for custom documentation elements.
                3. Multiple languages are supported. \\
                While AutoDocs is designed for extracting documentation from Java classes, other elements can be fed to the writing system. \\
                This allows for procedurally-generated documentation from non-Java sources.
                
                ### Using AutoDocs
                
                Basic documentation is controlled through a set of simple annotations.
                
                Most IDEs support folding/collapsing annotations to avoid taking up screen space.
                
                #### @Description
                This is used to add a multi-line description.
                It supports `MARKDOWN` (which parses the content as markdown), `HTML` (which leaves the content raw) and `OTHER` (which sanitises basic HTML tags but will otherwise leave it untouched.)
                The default mode is `MARKDOWN`.
                
                ```java
                import mx.kenzie.autodoc.api.note.Description;
                
                @Description(""\"
                  This is my cool class!
                  Markdown is supported here, so I can use `code` stuff.
                  \s
                  This also supports basic language highlighting in code blocks.
                  ""\")
                public class MyClass {
                
                }\s
                ```
                
                #### @Example
                This is used to add multi-line examples.
                This annotation can be used multiple times.
                
                The example language will default to `java`, but supports all basic `highlight.js` languages.
                
                ```java
                import mx.kenzie.autodoc.api.note.Example;
                
                @Example(""\"
                  new MyClass().whatever();
                  ""\")
                @Example(""\"
                  final MyClass thing = new MyClass();
                  ""\")
                public class MyClass {
                
                }\s
                ```
                
                #### @Warning
                This is used to add a multi-line warning message to an element.
                This annotation can be used multiple times.
                It supports `MARKDOWN` (which parses the content as markdown), `HTML` (which leaves the content raw) and `OTHER` (which sanitises basic HTML tags but will otherwise leave it untouched.)
                The default mode is `MARKDOWN`.
                
                ```java
                import mx.kenzie.autodoc.api.note.Warning;
                
                @Warning(""\"
                  This class will be removed in version 8.5.1! :(
                  ""\")
                public class MyClass {
                
                }\s
                ```
                
                #### @Ignore
                This causes the element to be skipped in any documentation.
                Private elements will be skipped by default, since they would be inaccessible to third-party code.
                
                ```java
                import mx.kenzie.autodoc.api.note.Ignore;
                
                @Ignore
                public class MyClass {
                
                }\s
                ```
                """
            , new File("docs/"), new File("target/AutoDocs.jar"), new File("src/main/java/"), "mx.kenzie.autodoc.api", "mx.kenzie.autodoc.impl", "mx.kenzie.autodoc.AutoDocs");
    }
    
}
