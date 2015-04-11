Android SDK Updater
===================

This program helps you to automatically update the android sdk for example on a build server. You can run this every day, or every week.

To run the application:

```
export ANDROID_HOME=/path/to/android-sdk

# download sdk if not yet downloaded
if [ ! -f $ANDROID_HOME/tools/android ]; then
    wget http://dl.google.com/android/android-sdk_r24.1.2-linux.tgz
    mkdir $ANDROID_HOME
    tar xvfz android-sdk_r24.1.2-linux.tgz --strip-components 1 -C $ANDROID_HOME
    rm android-sdk_r24.1.2-linux.tgz
fi

./run.sh
```