package mx.kenzie.autodoc.impl.site;

import java.util.List;

public record WebsiteDetails(String title, List<Class<?>> classes) {
}
