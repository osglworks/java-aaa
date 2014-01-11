package org.osgl.aaa0.utils;

import org.osgl.aaa0.*;
import play.Logger;
import play.Play;

public class AAAFactory implements ConfigConstants {
    private static IAccount acc_ = null;

    public static IAccount account() {
        if (acc_ == null) {
            String cn = Play.configuration.getProperty(ACCOUNT_IMPL);
            if (null != cn) {
                try {
                    Class<?> c = Class.forName(cn);
                    acc_ = (IAccount) c.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                String impl = Play.configuration.getProperty(AAA_IMPL);
                if (null == impl) {
                    Logger.warn("No configuration for account implementation found, use default morphia impl");
                    impl = AAA_IMPL_MORPHIA;
                }
                if (AAA_IMPL_MORPHIA.equals(impl)) {
                    acc_ = play.modules.aaa.morphia.Factory.account();
                } else {
                    throw new RuntimeException(
                            "AAA implementation not supported: " + impl);
                }
            }
        }
        return acc_;
    }
    
    private static IRole role_ = null;
    public static IRole role() {
        if (null == role_) {
            String cn = Play.configuration.getProperty(ROLE_IMPL);
            if (null != cn) {
                try {
                    Class<?> c = Class.forName(cn);
                    role_ = (IRole) c.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                String impl = Play.configuration.getProperty(AAA_IMPL);
                if (null == impl) {
                    Logger.warn("No configuration for role implementation found, use default morphia impl");
                    impl = AAA_IMPL_MORPHIA;
                }
                if (AAA_IMPL_MORPHIA.equals(impl)) {
                    role_ = play.modules.aaa.morphia.Factory.role();
                } else {
                    throw new RuntimeException(
                            "AAA implementation not supported: " + impl);
                }
            }
        }
        return role_;
    }

    private static IRight right_ = null;

    public static IRight right() {
        if (right_ == null) {
            String cn = Play.configuration.getProperty(RIGHT_IMPL);
            if (null != cn) {
                try {
                    Class<?> c = Class.forName(cn);
                    right_ = (IRight) c.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                String impl = Play.configuration.getProperty(AAA_IMPL);
                if (null == impl) {
                    Logger.warn("No configuration for right implementation found, use default morphia impl");
                    impl = AAA_IMPL_MORPHIA;
                }
                if (AAA_IMPL_MORPHIA.equals(impl)) {
                    right_ = play.modules.aaa.morphia.Factory.right();
                } else {
                    throw new RuntimeException(
                            "AAA implementation not supported: " + impl);
                }
            }
        }
        return right_;
    }

    private static IPrivilege priv_ = null;

    public static IPrivilege privilege() {
        if (null == priv_) {
            String cn = Play.configuration.getProperty(PRIVILEGE_IMPL);
            if (null != cn) {
                try {
                    Class<?> c = Class.forName(cn);
                    priv_ = (IPrivilege) c.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                String impl = Play.configuration.getProperty(AAA_IMPL);
                if (null == impl) {
                    Logger.warn("No configuration for privilege implementation found, use default morphia impl");
                    impl = AAA_IMPL_MORPHIA;
                }
                if (AAA_IMPL_MORPHIA.equals(impl)) {
                    priv_ = play.modules.aaa.morphia.Factory.privilege();
                } else {
                    throw new RuntimeException(
                            "AAA implementation not supported: " + impl);
                }
            }
        }
        return priv_;
    }

    private static ILog log_ = null;

    public static ILog log() throws Exception {
        if (null == log_) {
            String cn = Play.configuration.getProperty(LOG_IMPL);
            if (null != cn) {
                Class<?> c = Class.forName(cn);
                log_ = (ILog) c.newInstance();
            } else {
                String impl = Play.configuration.getProperty(AAA_IMPL);
                if (null == impl) {
                    Logger.warn("No configuration for log implementation found, use default morphia impl");
                    impl = AAA_IMPL_MORPHIA;
                }
                if (AAA_IMPL_MORPHIA.equals(impl)) {
                    log_ = play.modules.aaa.morphia.Factory.log();
                } else {
                    throw new RuntimeException(
                            "AAA implementation not supported: " + impl);
                }
            }
        }
        return log_;
    }

}
