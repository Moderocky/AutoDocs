package mx.kenzie.autodoc.impl.site;

import mx.kenzie.autodoc.api.note.Description;
import mx.kenzie.autodoc.api.note.Ignore;
import mx.kenzie.autodoc.api.schema.WritableElement;

import java.io.IOException;
import java.io.OutputStream;

@Description("""
    Used for writing common elements (blocks, etc.)
    """)
public interface PageElement extends WritableElement {
    
    default void printTo(OutputStream stream) throws IOException {
        this.start(stream);
        this.write(stream);
        this.end(stream);
    }
    
    @Description("""
        This is called first, to write block opening tags.
        """)
    void start(OutputStream stream) throws IOException;
    
    @Description("""
        This is called in the middle, to write block content.
        """)
    void write(OutputStream stream) throws IOException;
    
    @Description("""
        This is called last, to write block closing tags.
        """)
    void end(OutputStream stream) throws IOException;
    
}

@Description("""
    A small detail block for the right-middle column.
    """)
@FunctionalInterface
interface TitleArea extends PageElement {
    
    @Ignore
    @Override
    default void start(OutputStream stream) throws IOException {
        this.write(stream, "\n<div class=\"col col-lg-12 pt-4 px-4 pb-0 m-0\">");
    }
    
    @Ignore
    @Override
    default void end(OutputStream stream) throws IOException {
        this.write(stream, "</div>");
    }
}

@Description("""
    A small detail block for the right-middle column.
    """)
abstract class RightDetail implements PageElement {
    
    @Ignore
    protected final String title;
    
    public RightDetail(String title) {
        this.title = title;
    }
    
    @Ignore
    @Override
    public void start(OutputStream stream) throws IOException {
        this.write(stream, "<div class=\"card m-2 bg-body border-0 rounded shadow-sm\">");
        this.write(stream, "<div class=\"card-header\">" + title + "</div>");
        this.write(stream, "<div class=\"card-body\">");
    }
    
    @Ignore
    @Override
    public void end(OutputStream stream) throws IOException {
        this.write(stream, "</div>");
        this.write(stream, "</div>");
        
    }
}

abstract class RightBox implements PageElement {
    
    public RightBox() {
    }
    
    @Ignore
    @Override
    public void start(OutputStream stream) throws IOException {
        this.write(stream, "\n<div class=\"col-lg-4 col-md-12 card-deck\">");
    }
    
    @Ignore
    @Override
    public void end(OutputStream stream) throws IOException {
        this.write(stream, "</div>");
        
    }
}

@Description("""
    A small text block for the right-middle column.
    """)
class RightTextDetail extends RightDetail {
    
    protected String text;
    
    public RightTextDetail(String title, String text) {
        super(title);
        this.text = text;
    }
    
    @Ignore
    @Override
    public void write(OutputStream stream) throws IOException {
        this.write(stream, "<p class=\"pb-3 mb-0 small lh-sm\">");
        this.write(stream, text);
        this.write(stream, "</p>");
    }
}
