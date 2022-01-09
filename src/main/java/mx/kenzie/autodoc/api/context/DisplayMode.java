package mx.kenzie.autodoc.api.context;

import mx.kenzie.autodoc.api.note.Description;

@Description("""
    The display schema to use when parsing content.
    """)
public enum DisplayMode {
    MARKDOWN,
    HTML,
    OTHER
}
