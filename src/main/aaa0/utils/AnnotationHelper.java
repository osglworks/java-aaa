package org.osgl.aaa0.utils;


import org.osgl.aaa0.IPrivilege;
import org.osgl.aaa0.IRight;
import org.osgl.aaa0.RequirePrivilege;
import org.osgl.aaa0.RequireRight;

import java.lang.reflect.Method;

public class AnnotationHelper {
    
    /**
     * Return required right from the annotation of the given class
     * 
     * @param clazz
     * @return required right or <code>null<code> if any exception encountered
     */
    public static IRight getRequiredRight(Class<?> clazz) {
        return getRequiredRight(clazz.getAnnotation(RequireRight.class), clazz);
    }

    /**
     * Return required right from the annotation of the given method
     * 
     * @param clazz
     * @return required right or <code>null<code> if any exception encountered
     */
    public static IRight getRequiredRight(Method method) {
        return getRequiredRight(method.getAnnotation(RequireRight.class),
                method);
    }

    public static IRight getRequiredRight(RequireRight rr, Object subject) {
        IRight r = null;
        if (null != rr) {
            String s = rr.value();
            if (null != s && s.startsWith("aaa.")) {
                s = Play.configuration.getProperty(s);
            }
            if (null != s) {
                try {
                    r = AAAFactory.right().getByName(s);
                } catch (Exception e) {
                    Logger.error(e, "Error locating IRight implementation");
                }
            }
        }
        if (null == r) {
            Logger.warn("Right info not found for: %1$s", subject);
        }
        return r;
    }

    /**
     * Return required privilege from the annotation of the given class
     * 
     * @param clazz
     * @return required privilege or
     *         <code>null<code> if any exception encountered
     */
    public static IPrivilege getRequiredPrivilege(Class<?> clazz) {
        return getRequiredPrivilege(
                clazz.getAnnotation(RequirePrivilege.class), clazz);
    }

    /**
     * Return required privilege from the annotation of the given method
     * 
     * @param clazz
     * @return required privilege or
     *         <code>null<code> if any exception encountered
     */
    public static IPrivilege getRequiredPrivilege(Method method) {
        return getRequiredPrivilege(
                method.getAnnotation(RequirePrivilege.class), method);
    }

    public static IPrivilege getRequiredPrivilege(RequirePrivilege rp,
            Object subject) {
        IPrivilege p = null;
        if (null != rp) {
            String s = rp.value();
            if (null != s && s.startsWith("aaa.")) {
                s = Play.configuration.getProperty(s);
            }
            if (null != s) {
                try {
                    p = AAAFactory.privilege().getByName(s);
                } catch (Exception e) {
                    Logger.warn(
                            e,
                            "Error loading IPrivilege implementation from %1$s",
                            subject);
                }
            }
        }
        if (null == p) {
            Logger.warn("Privilege info not found for : %1$s", subject);
        }
        return p;
    }
}
