package java.lang;

import java.lang.annotation.Annotation;

public interface AnnotatedElement {
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass);
    public Annotation[] getAnnotations();
    public Annotation[] getDeclaredAnnotations();
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass);
}
