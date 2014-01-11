package org.osgl.aaa0.utils;

public interface ConfigConstants {
   public static final String SYSTEM_PERMISSION_CHECK = "aaa.system-permission-check";

   public static final String DISABLE = "aaa.disable";

   public static final String SUPERUSER = "aaa.superuser";

   /*
    * Control whether build auth registry automatically. Default: true
    * could be disabled if user want to programatically setup privileges
    * and rights first and call
    */
   public static final String BUILD_AUTH_REGISTRY = "aaa.buildAuthRegistry";

   public static final String SYS_ACCOUNT = "aaa.account.system";

   public static final String AAA_IMPL = "aaa.impl";

   public static final String AAA_IMPL_MORPHIA = "morphia";
   public static final String AAA_IMPL_JPA = "jpa";

   public static final String PRIVILEGE_IMPL = "aaa.impl.privilege";
   public static final String ACCOUNT_IMPL = "aaa.impl.account";
   public static final String ROLE_IMPL = "aaa.impl.role";
   public static final String RIGHT_IMPL = "aaa.impl.right";
   public static final String LOG_IMPL = "aaa.impl.log";
   public static final String AUTHENTICATOR_IMPL = "aaa.impl.authenticator";

   public static final String AUTHENTICATE_PROVIDER_URL = "aaa.authenticate.server";
   public static final String AUTHENTICATE_METHOD = "aaa.authentication.method";
}

