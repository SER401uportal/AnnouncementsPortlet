# Unicon uPortal Announcement Portlet Update
The portlet was tested with uPortal 5.0.0 (latest stable version).

### Required Items
*   Java JDK
*   uPortal Start
*   GIT client

### Build
Install a copy of the local repository (should be on same machine where uPortal is installed):

```sh
$ ./gradlew install
```
Then in uPortal directory issue a following command to deploy Announcements overlay:
```sh
$ ./gradlew :overlays:Announcements:tomcatDeploy
```