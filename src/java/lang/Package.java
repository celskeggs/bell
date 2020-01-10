package java.lang;

import java.lang.annotation.Annotation;

import com.celskeggs.bell.support.IncompleteImplementationError;

public class Package implements AnnotatedElement {

    private final String name;

    Package(String name) {
        this.name = name;
    }

    public <T extends Annotation> T getAnnotation(java.lang.Class<T> annotationClass) {
        throw new IncompleteImplementationError();
    }

    public Annotation[] getAnnotations() {
        throw new IncompleteImplementationError();
    }

    public Annotation[] getDeclaredAnnotations() {
        throw new IncompleteImplementationError();
    }

    public boolean isAnnotationPresent(java.lang.Class<? extends Annotation> annotationClass) {
        throw new IncompleteImplementationError();
    }
    
    public String getName() {
        return name;
    }
}
