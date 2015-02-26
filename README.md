java-aaa
========

A simple and easy to use authentication/authorization/accounting library for Java. At the moment `java-aaa` implemented the Authentication and Authorization part. Accounting is yet TBD.

### Concepts

**<a name="principal"></a>Principal**

Identifies a specific entity that interact with the current system and needs to access a certain resource of the system. A principal could be a end user or a third party system. 

A `principal` could have

* zero or more [role(s)](#role)
* zero or one [privilege](#privilege)
* zero or more [permission(s)](#permission)

***<a name="sys"></a>System principal***

`aaa` has predefined a name `__sys` to mark a special principal: system. For all overloaded authorization methods there is one method has the signature passing `boolean` argument: allowSystem. Which means if the current principal is not provided, should it assume it's system that is doing the interaction. This is useful when your application has background thread running that needs to access guarded resources. And yes of course `aaa` allows developer to configure `__sys` principal's permission and privilege. 
 
**<a name="guarded"></a>Guarded object**

`Guarded` encapsulates [permission](#permission) or [privilege](#privilege) required to access a certain resource. It could have either permission or privilege, or both. If both permission and privilege is specified in a guarded, then if the principal's credential matches any one of them, the access is granted.

**<a name="role"></a>Role**

A `role` aggregate [permission(s)](#permission) so that they can be grant to or revoked from a principal as a group

**<a name="permission"></a>Permission**

A `permission` means the power to do a certain task or access a certain resource in the system. Permission is identified by a literal String - the name of the permission. When aaa checking if a user has certain access to the resource, it will check the permission required on accessing the resource against the permission the user has been granted (either directly or via role). The checking is done by matching the name of both permissions. For example, if a resource access needs permission named "edit-account", and the principal that trying to access the resouce only has permission named "view-account", then the access is denied.

***<a name="dyna-perm"></a>Dynamic permission***

A permission could be specified to be `dynamic`, so that when aaa is checking the authority it does not only check permission name match, but also check if the current principal is associated with the guarded object. For example, if a user has dynamic permission "view-account", when user try to access a resource require that permission, aaa will check if user is associated with that account; while another permission "create-account" is not dynamic, thus aaa will just let the user go ahead to create account if the user has the "create-account" permission.

**<a name="privilege"></a>Privilege**

Unlike [permission](#permission) that implements a fine graind authorization scheme. `Privilege` implements a coarse grained authorization. While permission based authorization needs exactly match the name of the permission, privilege is doing a number comparison to check principal's authority. Say if access to a resource A require a privilege of level 10, and the current principal has been granted with a privilege of level 11, the access is granted.

`aaa` has a predefined privilege `9999` which is treated as super user level privilege, and any privilege with level equals to or bigger than `9999` will automatically gain access to any resource in the system.

