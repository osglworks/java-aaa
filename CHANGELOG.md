# CHANGELOG - osgl-aaa

1.9.0
* update to osgl-tool-1.24.0
* Use `org.osgl.exceptions.AccessDeniedException` to replace `org.osgl.aaa.NoAccessException`

1.8.0 - 03/Nov/2019
* update to osgl-tool-1.21.0

1.7.0 - 19/Apr/2019
* update to osgl-tool-1.19.2

1.6.0 - 30/Oct/2018
* update to osgl-tool-1.18.0

1.5.1
* Allow specify privilege as any integer value #5

1.5.0 - 19/Jun/2018
* update to osgl-tool 1.16.0
* Remove `Principal.getAllPermissions()` method #4 (broken change) 

1.4.0 - 14/Jun/2018
* update osgl-tool to 1.15.1

1.3.3 - 19/May/2018
* update osgl-tool to 1.13.1
* update osgl-cache to 1.4.0
* update osgl-logging to 1.1.4

1.3.3 - 02/Apr/2018
* update osgl-tool to 1.9.0

1.3.2 - 25/Mar/2018
* update osgl-tool to 1.8.1

1.3.1 - 25/Mar/2018
* update to osgl-tool-1.8.0

1.3.0 - 19/Dec/2017
* update to osgl-tool-1.5.0

1.2.0
* Rework on AAA facade, added a lot of convenient APIs #1
* AAA.requirePermission() API supports passing no guarded resource when permission is not dynamic
* AAA and AAAPersistenceService now has APIs to return all roles, permissions and privileges #2

1.1.1
* take out version range. see (https://issues.apache.org/jira/browse/MNG-3092)

1.1.0
* update osgl-tool to 1.0.0

1.0.0-SNAPSHOT
* Add `findPrivilege(int)` to `AAAPersistentService`
* Add `AuthenticationService.authenticate(String, char[])` method
* Add `requirePrivilege(int, ...) to `AAA` facade

0.9.1-SNAPSHOT
* support accounting
* Add createSuperUser() on AAA
* Support implied permission
* update osgl tool to 0.9
* Add AAA.setDefaultContext() method in case it needs to check permission of a principal on an
  guarded object in a thread where the "current" context is not set by framework

0.9.0-SNAPSHOT
* update osgl tool to 0.8

0.8.1-SNAPSHOT
* @NoAuthenticate deprecated, @NoAuthentication is created to replace it
* @RequireAuthenticate deprecated, @RequireAuthentication is created to replace it

0.8.0-SNAPSHOT
* AAAContext is now an abstract class
* Added requireXxx methods to AAAContext class

0.7.0-SNAPSHOT
* Add removeAll() to AAAPersistenceService
* Add copy constructor to SimplePrincipal.Builder
* Method signature changes in SimplePrincipal.Builder
* Method signature changes in SimpleRole.Builder

0.6.0-SNAPSHOT
* Update OSGL Tool version

0.5.0-SNAPSHOT
* DynamicPermissionHelperChecker now could be stick to permissions

0.4.2-SNAPSHOT
* Fix NPE in AAA.searchForDynamicPermissionCheckHelper
* Fix logic error in AAA.searchDPCHfromInterfaces

0.4.1-SNAPSHOT
* overload AAA with new set of methods that allow passing in AAAContext as parameter
  thus allow AAA to be used in a scenario without ThreadLocal storage
* Remove unnecessary thread local usage in AAAContextBase
* expose context() to public on AAA facade

0.3-SNAPSHOT - upgrade to osgl-tool 0.4-SNAPSHOT

0.2-SNAPSHOT - base version when history log started
