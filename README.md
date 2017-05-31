# BlueSIDE



#### Download project

    git clone https://github.com/Febraiz/BlueSIDE.git
    
** Don't forget to download jsch file here (put in /app/libs): 

<a href="https://sourceforge.net/projects/jsch/files/jsch.jar/0.1.54/jsch-0.1.54.jar/download" target="_blank">jsch-0.1.54.jar</a> <br>
<a href="http://mirrors.ircam.fr/pub/apache//commons/io/binaries/commons-io-2.5-bin.zip" target="_blank">commons-io-2.5.jar</a>

## Team members
- Jeremy **MEZHOUD**
- Wilfried **NGOUANA**
- Coralie **RODRIGUES**

## Gradle file
<pre><code>
apply plugin: 'com.android.application'

android {
    compileSdkVersion 23
    buildToolsVersion '25.0.0'

    splits {
        abi {
            enable true
            reset()
            include 'x86', 'armeabi-v7a'
            universalApk true
        }
    }

    defaultConfig {
        applicationId "com.example.isit_mp3c.projet"
        minSdkVersion 21
        targetSdkVersion 23
        versionCode 1
        versionName "1.0"

        ndk{
            moduleName = "app-jni"
        }
        sourceSets.main {
            jniLibs.srcDir 'src/main/libs'
            sourceSets.main.jni.srcDirs = []
        }
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }

    }
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    testCompile 'junit:junit:4.12'
    compile 'com.android.support:appcompat-v7:23.2.0'
    compile 'com.android.support:design:23.2.0'
    compile project(':openCVLibrary300')
    compile files('libs/jsch-0.1.54.jar')
    compile files('libs/commons-io-2.5.jar')
}
</code></pre>
