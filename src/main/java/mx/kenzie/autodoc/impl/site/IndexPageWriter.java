package mx.kenzie.autodoc.impl.site;

import mx.kenzie.autodoc.api.schema.WritableElement;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;

public record IndexPageWriter(String root, WebsiteDetails details, String title, String description, String[] metas, String[] scripts,
                              String[] keywords) {
    
    public IndexPageWriter(String root, WebsiteDetails details, String title, String description,
                           String... keywords) {
        this(root, details, title, description, new String[0], new String[0], keywords);
    }
    
    public boolean write(OutputStream target, WritableElement... elements) throws IOException {
        this.writeHeader(target);
        this.write(target, """
            
            <main id="body" class="container">
            """);
        this.writeNavbar(target);
        for (final WritableElement element : elements) {
            element.write(target);
        }
        this.write(target, """
            
            </main>
            """);
        this.writeFooter(target);
        return true;
    }
    
    private void writeNavbar(OutputStream target) throws IOException {
        this.write(target, "<div class=\"row\">");
        this.write(target, "<div class=\"col-lg-12\">");
        this.write(target, "<div class=\"col-lg-12 flex-md-row mt-4 mb-4 p-4 border rounded shadow-sm h-md-250 position-relative\">");
        {
            this.write(target, "<div class=\"row\">");
            this.write(target, "<div class=\"col-md-8 col-sm-12\">");
            this.write(target, "<a class=\"navbar-brand text-dark mr-4\" href=\"" + Utils.getTopPath(root) + "\">");
            this.write(target, details.title());
            this.write(target, "</a>");
            this.writeBreadcrumbs(target);
            this.write(target, "</div>");
            this.write(target, "<div class=\"col-md-4 col-sm-12\">");
            this.write(target, "</div>");
            this.write(target, "</div>");
        }
        this.write(target, "</div>");
        this.write(target, "</div>");
        this.write(target, "</div>");
    }
    
    private void writeBreadcrumbs(OutputStream target) throws IOException {
        this.write(target, "<div class=\"navbar-collapse offcanvas-collapse\">");
        this.write(target, "<nav class=\"navbar navbar-expand-lg\">");
        this.write(target, "<ol class=\"breadcrumb navbar-nav me-auto mb-2 mb-lg-0\">");
        this.writeBreadcrumbPieces(target);
        this.write(target, "</ol>");
        this.write(target, "</nav>");
        this.write(target, "</div>");
    }
    
    private void writeBreadcrumbPieces(OutputStream target) throws IOException {
        StringBuilder url = new StringBuilder(Utils.getTopPath(root));
        String part;
        String name = root;
        int index;
        while ((index = name.indexOf('.')) > -1) {
            part = name.substring(0, index);
            name = name.substring(index+1);
            url.append(part).append("/");
            this.write(target, "<li class=\"breadcrumb-item\">");
            this.write(target, "<a href=\"" + url + "\">");
            this.write(target, part);
            this.write(target, "</a>");
            this.write(target, "</li>");
        }
    }
    
    private void writeHeader(OutputStream target) throws IOException {
        this.write(target, """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <meta name="description" content=\"""");
        this.write(target, description);
        this.write(target, """
            ">
                <meta name="keywords" content=\"""");
        this.write(target, String.join(", ", keywords));
        this.write(target, """
            ">
                <title>""");
        this.write(target, title);
        this.write(target, """
            </title>
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.4.0/styles/github-dark.min.css" crossorigin="anonymous">
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
                <link href="https://cdnjs.cloudflare.com/ajax/libs/github-markdown-css/4.0.0/github-markdown.min.css" rel="stylesheet" crossorigin="anonymous">""");
        for (final String meta : metas) {
            this.write(target, meta);
        }
        this.write(target, "\n</head>\n<body>");
    }
    
    @Deprecated
    private void writeGap(OutputStream target) throws IOException {
        this.write(target, """
            
            <!--  todo -->
              <br>
              <br>
              <br>
            <!--  todo -->""");
    }
    
    void write(final OutputStream stream, final String content) throws IOException {
        stream.write(content.getBytes(StandardCharsets.UTF_8));
    }
    
    private void writeFooter(OutputStream target) throws IOException {
        for (final String script : scripts) {
            this.write(target, script);
        }
        this.write(target, """
            
            <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.10.2/dist/umd/popper.min.js" integrity="sha384-7+zCNj/IqJ95wo16oMtfsKbZ9ccEh31eOz1HGyDuCQ6wgnyJNSYdrPa03rtR1zdB" crossorigin="anonymous"></script>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.min.js" integrity="sha384-QJHtvGhmr9XOIpI6YVutG+2QOK9T+ZnN4kzFN1RtK3zEFEIsxhlmWl5/YESvpZ13" crossorigin="anonymous"></script>
            <script src="https://use.fontawesome.com/releases/v5.13.1/js/all.js" type="text/javascript" crossorigin="anonymous"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.4.0/highlight.min.js" crossorigin="anonymous"></script>
            <script>
               document.addEventListener('DOMContentLoaded', (event) => {
                 document.querySelectorAll('pre code').forEach((el) => {
                   hljs.highlightElement(el);
                   el.parentElement.classList.add('rounded');
                   el.classList.add('rounded');
                 });
               });
             </script>
            </body>
            </html>""");
        
    }
}
