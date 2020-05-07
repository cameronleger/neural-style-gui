# Overview
Neural Style GUI is designed for rapid prototyping art from Neural Style. To that end, it supports all Neural Style settings, a GUI designed around the process, immediate iterative feedback of results, session history, and saving and loading of style settings and images.

# Screenshots
![Inputs Tab of Neural-Style-GUI](/screenshots/inputs.png?raw=true "Inputs Tab of Neural-Style-GUI")
![Layers Tab of Neural-Style-GUI](/screenshots/layers.png?raw=true "Layers Tab of Neural-Style-GUI")
![Output Tab of Neural-Style-GUI](/screenshots/output.png?raw=true "Output Tab of Neural-Style-GUI")
![Log Tab of Neural-Style-GUI](/screenshots/log.png?raw=true "Log Tab of Neural-Style-GUI")

# Requirements
* AND/OR
    * [neural-style](https://github.com/jcjohnson/neural-style) which requires Torch and is probably Linux-specific
    * [neural-style-pt](https://github.com/ProGamerGov/neural-style-pt) which requires PyTorch and may work on Windows
* Java 11
* JavaFX 11 (included with Oracle, separate package for OpenJDK)

# Features
* All Neural Style settings are supported, and most are grouped in a settings pane
* Image grids for quick preview and selection of Style & Content images
* Style image list supports single selection or multiple selections and weights
* Layer selections, including parsing available layers from chosen Model/Proto files and adding/removing layers
* Saving/Loading of all style settings, including input images, to a JSON format
* Shortcuts for common operations
* Immediate image feedback during iterations supports zoom/pan
* Easy chaining of multiple Neural Style outputs for better large image quality, tweaking almost any parameter across the different runs
* Quick permanent saving of feedback images
* Output Queue for batching of style outputs
* View session output image history
* Load recently used settings or initialize with recently created images
* vRAM usage gauge (NVIDIA only)
* Log of Neural Style command output

# Starting
1. Unzip the release to a folder of your choice (or build from source and check the **shade** folder).
2. Run the neural-style-gui.sh or neural-style-gui.bat file. Alternatively, run **java -jar neural-style-gui-*.jar** with any additional options you desire.

# Minimum Required Steps
1. Set the Runner and Neural Style paths in the Files Tab.
2. Modify Advanced settings based on your machine, i.e., CPU or GPU and backend
3. Set the Style Folder path to a folder containing style images
4. Select a style image in the Style Image Grid
5. Set the Content Folder path to a folder containing content images
6. Select a content image in the Content Image Grid
7. Press Queue
8. Press Start!

# Usage
After you have performed the Minimum Required Steps and chosen an Output Folder path, you may want to use the Save button in the Settings Pane to save all of this information into a 'default' settings file that you can load next time you run the application.

If you're getting a File Not Found exception for the 'th' command because it's not properly in your PATH, you can set the path to 'th' above the Neural Style path.

The Chaining parameter in the Basic Settings Pane allows you to quickly run multiple Neural Style commands that feed into each other with the previous result (i.e. the first chain will use the Initialization Image setting you chose, but each chain after that will use the last iteration image of the previous chain). Each numeric Parameter has a "Ratio" field that adjusts the Parameter for each Chain run. The normal value you specify is for the _last_ or _final_ run; the ratio value multiplies with that value for each chain by _walking backwards_. For example, with a 0.5 Size Ratio over 3 runs for a final result of 1200px, the runs will start at 300px before going to 600px and finally 1200px. This can allow you to make higher quality and larger images than with a single run with single settings, and almost always faster because less iterations are typically used with this.

The Progress Update and Image Update inputs under the Advanced Tab allow you to configure how often the Progress Bar and Output Images update.

The rest of the settings are better explained in the [Neural-Style](https://github.com/jcjohnson/neural-style) readme, but each input has helpful information in a tooltip if you hover over it for long enough. Numeric inputs can be controlled by their slider or input field; the input fields have unrestricted ranges while the sliders have reasonable ranges for each setting. All inputs also have a "Reset" button for using the default value.

Choosing different Proto & Model Files will additionally try to parse the layer information and update the Layers tab with the appropriate layers.

For both Grids in the Input Tab, Middle-Clicking a Style/Content Image will open a larger version in a new tab.

The Input Tab and Style Grid can be quickly accessed with the shortcut Ctrl+S. In the default Single Selection mode, selecting a Style image will suffice. For Multiple Selection mode, pressing the space-bar on any selected Style image will toggle the Selected checkbox for that item. Weights can be adjusted by double-clicking the numerical value if using Multiple Selection mod.

The Input Tab and Content Grid can be quickly accessed with the shortcut Ctrl+C. Because only one image can be selected here, the grid's selection is used for image selection, so no checkbox is necessary.

The Layers Tab and Lists are normally filled with the default values for the default Proto & Model files, but they will update based on other Proto & Model file selections. It's possible to add or remove layers from these lists with the relevant buttons. Layers can be renamed by double-clicking their names.

The Output Tab can be quickly accessed with the shortcut Ctrl+O. Ctrl+Enter will Queue the current settings. Additionally, Ctrl+Shift+Enter will start the Neural Style Queue, and any time this process is started the Output tab is focused. The Fit to View and Actual Size buttons are quick ways to view the image. The mouse scroll-wheel zooms the image, and clicking-and-dragging pans. If an image with a different aspect ratio is loaded, the view is reset in the Fit to View mode. The Save Image button (and optional file name) will save the currently shown image to the selected Output Folder.

In the Output Tab and next to the Image is the Output Queue Tree. Any started process will show up here, and items are grouped by each process run. The top-level items contain a Load button to re-load the style settings used for that process. The child items are each iteration image, and can quickly set the Initialize With Image setting. With no selection, the Image will always show the most recent iteration processed. Selecting a top-level style will continuously update the Image with the most recent iteration from that process. Selecting a child image will only show that selection in the Image.

The Neural Style Log Tab will show the output from the most recent running process. This is rarely used, but it's helpful to understand why a process might have failed, usually because of out-of-memory errors.

# Changelog
### 1.4.0
* Support for [neural-style-pt](https://github.com/ProGamerGov/neural-style-pt)
* Cross-platform JAR (since neural-style-pt works on Windows!)
* Logging improvements
### 1.3.0
* JDK 11
* Overhauled Parameters
  * All number-based Parameters can be adjusted while Chaining
  * All Parameters can be reset to their default
* Wrapping-Grid of Images instead of a List
* Support for changing the working directory (was unique-temporary for each launch)
* Preview of Initialization Image
* Middle-Clicking a Style/Content Image will open it in a Tab
* Right-Clicking a Style/Content Image will set it as the Initialization Image
* Large performance improvements when loading Styles
* Large performance improvements for Output monitoring
* Fixed a bug with Output-related Buttons loading Styles improperly
* Layout Tweaks
* Icons
### 1.2.1
* Fixed an issue where 'nvidia-smi' was not available
* New optional path to set 'th' in case it cannot be found
### 1.2.0
* Support for recent neural-style features:
  * Initialization Image
  * Multi-GPU Setups
  * lbfgs nCorrection
* Simple/Advanced Tabs in the Settings Pane
* The most recently started Style is loaded when the app starts
* Ability to quickly initialize with any previously generated images
* Significantly improved performance when running many iterations repeatedly
* History Tree is now a Queue Tree to show past and future outputs
* The Neural Service now runs until all Queued items are finished
* Chained runs allow larger images and better results
* Bugfixes
### 1.1.1
* Prototxt parsing supports current version of Loadcaffe Layers
### 1.1.0
* Saving an Output Image also saves the Style with it
* Style and Content Images are sorted by their name
* Fixed an issue with the Output View and History not always recognizing the latest iteration

# Concerns
This GUI Application takes a small amount of the precious vRAM necessary for Neural Style, or even more if using CPU mode because the larger amounts of RAM used. If you find this inhibiting, consider using it for quick previews and using the generated command to run Neural Style without the GUI for final results. I have an advanced setup with bumblebee and vfio-pci, so this is not an issue for me.

# Known Issues
* Stopping/Cancelling a running Neural Style won't actually kill the process until the next output line is received, which can be a particularly long time if using infrequent progress updates or on slow computers.

# Contributing
Feature requests and development efforts for bug fixes and new features are certainly welcome. This project is developed using IntelliJ IDEA and the basic project for it is included in the repo.
## IntelliJ IDEA Code Insight Issue
By default, IntelliJ IDEA restricts Code Insight to files of a small size, and the Loadcaffe.java file is too large for this to work. This results in some files showing errors where Loadcaffe is used because it cannot parse the file. To get rid of these errors, you must follow [the instructions in this Stack Overflow answer](http://stackoverflow.com/questions/23057988/file-size-exceeds-configured-limit-2560000-code-insight-features-not-availabl/23058324#23058324).
## Logging
To see logging levels other than the default of just the NeuralService output, you can add the following option to the 'VM Options' setting in your Run Configuration (be sure to update the path to the included [logger.properties](/logger.properties) file):
    -Djava.util.logging.config.file=/path/to/logger.properties
