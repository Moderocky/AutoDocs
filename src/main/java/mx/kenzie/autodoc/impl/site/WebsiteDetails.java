package mx.kenzie.autodoc.impl.site;

import mx.kenzie.autodoc.api.note.Description;

import java.util.List;

@Description("""
    The title and class list for this documentation site.
    The title is used in the navigation bar.
    
    The class list is used for generating URLs locally to get between pages.
    It is also used to determine whether this site covers an element.
    """)
public record WebsiteDetails(String title, List<Class<?>> classes) {
}
