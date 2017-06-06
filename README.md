# BlueSIDE



#### Download project

    git clone https://github.com/Febraiz/BlueSIDE.git
    
** Don't forget to download jar files here (put in /app/libs): 

[Jar Files - Sources](https://drive.google.com/open?id=0B2sYsdlhPpABZVVwWDBNQXhtaVE)

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

    compile files('libs/mail.jar')
    compile files('libs/activation.jar')
    compile files('libs/additionnal.jar')
}
</code></pre>
