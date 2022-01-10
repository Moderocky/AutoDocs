package mx.kenzie.autodoc.impl.site;

import mx.kenzie.autodoc.api.schema.WritableElement;

import java.io.IOException;
import java.io.OutputStream;

public interface PageElement extends WritableElement {
    
    default void printTo(OutputStream stream) throws IOException {
        this.start(stream);
        this.write(stream);
        this.end(stream);
    }
    
    void start(OutputStream stream) throws IOException;
    
    void write(OutputStream stream) throws IOException;
    
    void end(OutputStream stream) throws IOException;
    
}
abstract class RightDetail implements PageElement {
    
    protected final String title;
    
    public RightDetail(String title) {
        this.title = title;
    }
    
    @Override
    public void start(OutputStream stream) throws IOException {
        this.write(stream, "<div class=\"col-md-6 col-lg-12 my-3 p-3 bg-body rounded shadow-sm\">");
        this.write(stream, "<h6 class=\"border-bottom pb-2 mb-0\">" + title + "</h6>");
    }
    
    @Override
    public void end(OutputStream stream) throws IOException {
        this.write(stream, "</div>");
        
    }
}
class RightTextDetail extends RightDetail {
    
    protected String text;
    
    public RightTextDetail(String title, String text) {
        super(title);
        this.text = text;
    }
    
    @Override
    public void write(OutputStream stream) throws IOException {
        this.write(stream, "<p class=\"pb-3 mb-0 small lh-sm\">");
        this.write(stream, text);
        this.write(stream, "</p>");
    }
}
