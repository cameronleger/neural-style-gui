# Overview
Neural Style GUI is designed for rapid prototyping art from Neural Style. To that end, it supports all Neural Style settings, a GUI designed around the process, immediate iterative feedback of results, session history, and saving and loading of style settings and images.

# Screenshots
![Inputs Tab of Neural-Style-GUI](/screenshots/inputs.png?raw=true "Inputs Tab of Neural-Style-GUI")
![Layers Tab of Neural-Style-GUI](/screenshots/layers.png?raw=true "Layers Tab of Neural-Style-GUI")
![Output Tab of Neural-Style-GUI](/screenshots/output.png?raw=true "Output Tab of Neural-Style-GUI")
![Log Tab of Neural-Style-GUI](/screenshots/log.png?raw=true "Log Tab of Neural-Style-GUI")

# Requirements
* [Neural-Style](https://github.com/jcjohnson/neural-style)
* Java 8
* JavaFX 8 (included with Oracle, separate package for OpenJDK)

# Features
* All Neural Style settings are supported, and most are grouped in a settings pane
* Image lists for quick preview and selection of Style & Content images
* Style image list supports single selection or multiple selections and weights
* Layer selections, including parsing available layers from chosen Model/Proto files and adding/removing layers
* Saving/Loading of all style settings, including input images, to a JSON format
* Shortcuts for common operations
* Immediate image feedback during iterations supports zoom/pan
* Quick permanent saving of feedback images
* View session output image history
* Load recently used settings or initialize with recently created images
* vRAM usage gauge (NVIDIA only)
* Log of Neural Style command output

# Starting
Unzip the release to a folder of your choice (or build from source and check the **target/jfx/app** folder). Run the neural-style-gui.sh file or run **java -jar neural-style-gui-*-jfx.jar** with any additional options you desire.

# Minimum Required Steps
1. Set the Neural Style path
2. Modify settings based on your machine, i.e., CPU or GPU and backend
3. Set the Style Folder path to a folder containing style images
4. Select a style image in the Style Image List
5. Set the Content Folder path to a folder containing content images
6. Select a content image in the Content Image List
7. Press Start!

# Usage
After you have performed the Minimum Required Steps and chosen an Output Folder path, you may want to use the Save button in the Settings Pane to save all of this information into a 'default' settings file that you can load next time you run the application.

The Progress Update and Image Update inputs allow you to configure how often the Progress Bar and Output Images update. The rest of the settings are better explained in the [Neural-Style](https://github.com/jcjohnson/neural-style) readme, but each input has helpful information in a tooltip if you hover over it for long enough. Numeric inputs can be controlled by their slider or input field; the input fields have unrestricted ranges while the sliders have reasonable ranges for each setting.

Choosing different Proto & Model Files will additionally try to parse the layer information and update the Layers tab with the appropriate layers.

The Input Tab and Style List can be quickly accessed with the shortcut Ctrl+S. In the default Single Selection mode, selecting a Style image will suffice. For Multiple Selection mode, pressing the space-bar on any selected Style image will toggle the Selected checkbox for that item. Weights can be adjusted by double-clicking the numerical value if using Multiple Selection mod. I find the best way to work with this list is to use the arrow keys for navigation and the space-bar for selection.

The Input Tab and Content List can be quickly accessed with the shortcut Ctrl+C. Because only one image can be selected here, the list's selection is used for image selection, so no checkbox is necessary.

The Layers Tab and Lists are normally filled with the default values for the default Proto & Model files, but they will update based on other Proto & Model file selections. It's possible to add or remove layers from these lists with the relevant buttons. Layers can be renamed by double-clicking their names.

The Output Tab can be quickly accessed with the shortcut Ctrl+O. Additionally, Ctrl+Enter will start the Neural Style process, and any time this process is started the Output tab is focused. The Fit to View and Actual Size buttons are quick ways to view the image. The mouse scroll-wheel zooms the image, and clicking-and-dragging pans. If an image with a different aspect ratio is loaded, the view is reset in the Fit to View mode. The Save Image button (and optional file name) will save the currently shown image to the selected Output Folder.

In the Output Tab and next to the Image is the Output History Tree. Any started process will show up here, and items are grouped by each process run. The top-level items contain a Load button to re-load the style settings used for that process. The child items are each iteration image. With no selection, the Image will always show the most recent iteration processed. Selecting a top-level style will continuously update the Image with the most recent iteration from that process. Selecting a child image will only show that selection in the Image.

The Neural Style Log Tab will show the output from the most recent running process. This is rarely used, but it's helpful to understand why a process might have failed, usually because of out-of-memory errors.

# Changelog
### 1.2.0
* Support for recent neural-style features:
  * Initialization Image
  * Multi-GPU Setups
  * lbfgs nCorrection
* Simple/Advanced Tabs in the Settings Pane
* The most recently started Style is loaded when the app starts
* Ability to quickly initialize with any previously generated images
* Significantly improved performance when running many iterations repeatedly
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