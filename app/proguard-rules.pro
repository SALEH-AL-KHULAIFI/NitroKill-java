# NitroKill — proguard/R8 rules
-keep class com.isx3i.nitrokill.service.SpeedMonitorService { *; }
# Activities/Services declared in the manifest are already protected by
# proguard-android-optimize.txt's default Android-component keep rules;
# the line above just makes the service's start()/stop() entry points extra-safe.
