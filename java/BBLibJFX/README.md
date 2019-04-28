
## BBLIB open source files JFX

### Author

Author : Boris Borgobello

### Build process

#### JAR archive

Last version of BBLibJFX is found in store/bblib.jar.
It is built by running the build target AAA-bblib. It packages all the dependencies into a single JAR.

#### Applications

It is possible to generate Windows and MacOS X user friendly and obfuscated apps.

Obfuscation is done via proguard. The proguard config is found at the root of the project alongside a copy of proguard.jar binary.

Creation shell scripts are inside ./exports/ folder. Output will be zipped and moved to ./exports/bin dir.

- For Mac Application, you need jar2app https://github.com/Jorl17/jar2app : ./build_bblibapp_mac.sh


```
./build_bblibapp_mac.sh 
ProGuard, version 6.0.3
Reading program jar [/Users/borisborgobello/project/perso/bblib-open-source-utils/java/BBLibJFX/store/bblib.jar]
Reading library directory [/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib] (filtered)
Reading library directory [/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext] (filtered)
Preparing output jar [/Users/borisborgobello/project/perso/bblib-open-source-utils/java/BBLibJFX/store/bblib_enc.jar]
  Copying resources from program jar [/Users/borisborgobello/project/perso/bblib-open-source-utils/java/BBLibJFX/store/bblib.jar]
Create MAC app for BBLib 3.
mkdir: bin: File exists
jar2app 1.0.1, João Ricardo Lourenço, 2015-2017 <jorl17.8@gmail.com>.
Github page: https://github.com/Jorl17/jar2app/
Packing ../store/bblib_enc.jar into /Users/borisborgobello/project/perso/bblib-open-source-utils/java/BBLibJFX/exports/BBLibApp M3.0.app
CFBundleIconFile: bblibapp.icns
CFBundleIdentifier: com.jar2app.example.BBLibApp M3.0
CFBundleDisplayName: BBLibApp M3.0
CFBundleName: BBLibApp M3.0
CFBundleShortVersionString: 1.0.0
CFBundleSignature: ????
CFBundleVersion: 1.0.0
Retina support enabled.
---
JVMOptions: -Dprism.order=sw -Dprism.verbose=true
JVMMainClassName: com.borisborgobello.jfx.asamples.a
JAR Working directory: $APP_ROOT/Contents

../store/bblib_enc.jar packaged to /Users/borisborgobello/project/perso/bblib-open-source-utils/java/BBLibJFX/exports/BBLibApp M3.0.app.
Zipping JAR
  adding: BBLibApp M3.0.app/ (stored 0%)
  adding: BBLibApp M3.0.app/Contents/ (stored 0%)
  adding: BBLibApp M3.0.app/Contents/MacOS/ (stored 0%)
  adding: BBLibApp M3.0.app/Contents/MacOS/JavaAppLauncher (deflated 70%)
  adding: BBLibApp M3.0.app/Contents/PlugIns/ (stored 0%)
  adding: BBLibApp M3.0.app/Contents/Resources/ (stored 0%)
  adding: BBLibApp M3.0.app/Contents/Resources/en.lproj/ (stored 0%)
  adding: BBLibApp M3.0.app/Contents/Resources/en.lproj/Localizable.strings (deflated 30%)
  adding: BBLibApp M3.0.app/Contents/Resources/bblibapp.icns (deflated 47%)
  adding: BBLibApp M3.0.app/Contents/Java/ (stored 0%)
  adding: BBLibApp M3.0.app/Contents/Java/bblib_enc.jar (deflated 9%)
  adding: BBLibApp M3.0.app/Contents/Info.plist (deflated 64%)
```

- For Windows Application, you need launch4j. A copy is already inside exports/ dir. Use : ./build_bblibapp_win.sh

```
./build_bblibapp_win.sh 
ProGuard, version 6.0.3
Reading program jar [/Users/borisborgobello/project/perso/bblib-open-source-utils/java/BBLibJFX/store/bblib.jar]
Reading library directory [/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib] (filtered)
Reading library directory [/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext] (filtered)
Preparing output jar [/Users/borisborgobello/project/perso/bblib-open-source-utils/java/BBLibJFX/store/bblib_enc.jar]
  Copying resources from program jar [/Users/borisborgobello/project/perso/bblib-open-source-utils/java/BBLibJFX/store/bblib.jar]
launch4j: Compiling resources
launch4j: Linking
launch4j: Wrapping
launch4j: Successfully created /Users/borisborgobello/project/perso/bblib-open-source-utils/java/BBLibJFX/exports/../store/BBLibApp.exe
Zipping JAR
  adding: BBLibApp.exe (deflated 9%)
DONE
```

### Library

Samples and use cases are found into asamples package.

Only a few critical classes have really been optimized. Performance was never the goal for this lib. 
Some critical pieces have been accelerated to reach O(log2n) complexity instead of O(n).
Few pieces are only PoC.
Most classes are also absolete and have few years, more than half the features is no longer being used.
This lib has also many dependencies, which sometimes has been improved using reflexivity.
Tests are limited to critical classes and have no been moved to the lib yet.

To use this lib you need to use JavaFX8, Java8, netbeans. SceneBuilder might work. I also work with SceneBuilder.

Overall it's a soup of PoC and utils.

Core features and utils :

#### Image

- Integrate AsyncScalr and Scalr and builder patterns
- Image manipulation like rotations, base64, BigBufferedImage (ARGB only) for images over 10k x 10k (OOM), painting image on image, ...

#### Heavy calculations

- Image segmentation using simple threshold algorithm on alpha channel resilient against StackOverflows (includes unit tests). Returns all objects of a picture and filters small objects.

```java
// Finds all PNGs inside temp folder, splits them 
// into smaller images/objects with minimum size of 10
// based on a transparency level (alpha) of 50%
// and saves all the smaller objects to temp directory
BBImageSplitter.segmentateByTransparency(new File("./temp"), 0.5, 10);
```

- Apache machine learning encapsulation and implementation

```java
	private static class PixelGroup extends ArrayList<Pixel> {
        int id;
    }
    private static class Pixel {
        int x;
        int y;
        int value;
        PixelGroup group = null;
        Pixel(int x, int y, int value) { this.x = x; this.y = y; this.value = value; }
    }
    
    void apacheMLTest() {
        final BBImageApacheMLBridge.ISFilterBase filter = new BBImageApacheMLBridge.FKmeansAp();
        final BBImageApacheMLBridge.BBDistances dist = BBImageApacheMLBridge.BBDistances.DIST_EUCLI;
        final int w = 119, h = 319;
        
        final int K = 15; // desired nb clusters (K means)
        final int fuzzy = 0;
        final int attempts = 3;
        
        ArrayList<Pixel> points = new ArrayList<>(h*w);
        
        // Typical case of an image
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                points.add(new Pixel(i, j, (int) (Math.random()*255)));
            }
        }
        
        ArrayList<BBImageApacheMLBridge.BBFilterGroup<Pixel>> result = filter.doFilter(points, dist, new BBImageApacheMLBridge.PixelConverter<Pixel>() {
            @Override public int x(Pixel t) { return t.x; }
            @Override public int y(Pixel t) { return t.y; }
            @Override public boolean shouldCloneData() { return true; }
            @Override
            public double[] getDistanceComponents(Pixel t) {
                Color c = new Color(t.value);
                return new double[] { c.getRed()/255.0, c.getGreen()/255.0, c.getBlue()/255.0 };
            }
        }, K, fuzzy, attempts);
        
        // All pixels have now been put in K = 15 groups based on euclidean distance of their colors
    }
```

#### IO

- DualPrint to duplicate streams. Useful to save STDOUT and STDERR to a file based log.
- Very old java homemade path functions
- Transfer delegates for streams including HttpEntity wrapper for Apache from http://stackoverflow.com/a/7319110/268795
- Very old network lib

#### Threading

- BBTaskHandler for easy fork and join (unit tests included). Have space for improvement : stacking stacks and ordering
- Handler system extracted from Android

#### UI

- Powerful dialogs & alerts
- Many functions for JavaFX : view to image, etc...
- BBSuperController, a powerful wrapper to the FXController accessible using a builder pattern. BBSuperControllers include a lifecycle, convenient methods like thread safe blocking progress display, notifications, criticalErrors handling, etc...

```java
// Showing a BBSuperController controller is very easy
BBSuperContBuilder.show(this, BBLibFXMLSampleTableViewController.class, 
                "TableView sample", Modality.APPLICATION_MODAL, StageStyle.DECORATED);

// Highly customizable using builder pattern
BBSuperContBuilder.inst(c, FXMLProgressDialogController.class, title)
                .setModality(Modality.APPLICATION_MODAL)
                .setStageStyle(StageStyle.UTILITY)
                .setData("anyobject") // send data to the controller
                // link a callback to that controller when it is finished
                .setCallback((BBSuperController vc, Object toReturn) -> {})) 
                .setFXML(_) // Custom FXML file to be loaded
                .setLanguage(_) // Language override
                .setTheme(_) // Theme override
                .build().showAndWait();
```

- Powerful BBTableView for easy tableview implementation, dynamic cells including images, buttons, notification center listener, auto search, etc... Very little boilerplate. Data is fetched, then filtered, then sorted, then displayed. Just implement table callbacks.

#### Logging

- BBLog for easy logging
- BBLogController for log display

#### Utils

- JSON parsing using jackson + crypto
- BBApplication and BBBuildSettings. Override fo more powerful app.
- Homemade CSV parser (trash/obsolete)
- BBCollections including powerful functions for manipulating/creating lists, maps, etc. No dependency besides java 8.
- BBColor, BBColorUtils, BBColorsForStitching, for color handling, spectrum transformation, etc.
- BBConversions for conversions
- BBDateUtils
- BBGeometry including changing bases
- BBPDF using PDF box to generate a PDF from a list of files
- BBRes for handling resources embedded in the JAR
- BBSharedPref used to store a map of String to Json to a file. Might require some unit testing.
- BBTools, comparators, regex (email, etc)...
- BBZipper for zipping/unzipping
- A dark CSS theme

### Next step

Soon (undetermined future), I'll integrate a subset of more advanced algorithms including :
- Guillotine algorithm
- Combinatory
- 2D convolutions
- Custom image processing (filters)

