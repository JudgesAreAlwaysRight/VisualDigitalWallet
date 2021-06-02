package com.blockcypher.utils.annotation;

import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Util to read @Path annotation on a class
 * @author <a href="mailto:seb.auvray@gmail.com">Sebastien Auvray</a>
 */
public class AnnotationUtils {

    /**
     * Get the @Path of a class
     * @param transactionClass
     * @return
     */
    public static String getPath(Class transactionClass) {
        for (Annotation annotation : transactionClass.getAnnotations()) {
            Class<Annotation> type = (Class<Annotation>) annotation.annotationType();
            for (Method method : type.getDeclaredMethods()) {
                if ("javax.ws.rs.Path".equals(type.getName()) && "value".equals(method.getName())) {
                    try {
                        return (String) method.invoke(annotation);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        Log.e("AnnotationUtils", "Error while reading @Path", e);
                    }
                }
            }
        }
        return null;
    }

}
