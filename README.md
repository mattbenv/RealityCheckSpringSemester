# Reality Check DevTeam15
# This Project is a tool used for data collection within a mock social media platform.
<!--
 Install and download the .zip file.
Go to: file/settings/gradle gradle jdk switch from 1.8 to 11, just to make sure that everything is setting up.
Go to: local properties in the project manager and add your sdk path.
    -you will need to find the path in your file folder, but the format for other team memembers is shown. 
       -Analogous to sdk.dir=C\:\\Users\\mbenv\\AppData\\Local\\Android\\Sdk
    -comment out every other path except your newly added path.

Go to: tools, avd manager, create virtual device
    -selct a pixel 3xl. select the latest, and download that version. 
    -Name the device[Next]

Potential Error: 
Emulator Error Description: If anyone has problem getting started with the emulator surrounding issue intel haxm, simply download the windows or mac version found website https://github.com/intel/haxm/releases 
and if an error page is found when trying to get to that website just remove the “%5C” in the link.

Build project

Make sure app is the configuration and the emulator you created is selected.

Run. -->

Each page in the application is called a fragment:
 -you can get an overall picture of how the app works by looking at the navgraph.
     -app/source/main/res/navigation/nav_graph

Each fragment can be found as a .xml file
 --app/source/main/res/layout
 WelcomePage, Login, Signup, SignupContinue, ActivityResetPassword, Activity Post, Profile, comment, createpost, etc.

Then we have our objects:
--app/source/main/java
Users, otherUserProfile, WelcomePage, SignupPage, SignUpPageContinue, Posts, TextPost, Webpost, ImagePost, VideoPost, PostAdapter, SearchAdapter.

