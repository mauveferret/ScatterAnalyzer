# ISInCa - Ion Surface Interaction Calculator
ISInCA - is a small JAVA tool dedicated to postprocess SCATTER, TRIM, SDTrimSP calculations. 
[![N|Solid](https://i.ibb.co/0cQrTDm/Croco-Logo.png)](https://github.com/mauveferret)

# Basic outputs

It can calculate:
 - Energy distributions dN/dE(E) for different for any solid angles **(backscattered primary particles, sputtered and transmitted particles)***
 - Angle distributions dN/dTheta(Theta) **(backscattered primary particles, sputtered and transmitted particles)**
 - Depth distributions for primary particles **NOT FINISHED YET**
 - Polar Maps for ane solid angles
 - Cartesian Maps 
 - Integral coefficients:
   - Scattered coefficient
   - Sputtered coefficient
   - Implanted coefficient
   - Transmitted coefficient
   - Energy recoil coefficient
   - Displaced coefficient
   - ...
- fff

# Install
ISInCa is fully JAVA program so it can be launched in any OS on every architecture!  
Executable files are located in *[`/out`](https://github.com/mauveferret/ISInCa/tree/master/.idea)* directory:
- For Windows OS you can download file executable file *[`ISInCa.exe`](https://github.com/mauveferret/ISInCa/tree/master/.idea)*
- For other OS you should try *[`isinca.jar`](https://github.com/mauveferret/ISInCa/tree/master/.idea)*
- For console mode you can also download config file *[`isinca`](https://github.com/mauveferret/ISInCa/tree/master/.idea)*

In any case you need to have a **Java Virtual Machine** (version **11** or newer) installed on your device. To check whether the JVM was already installed on the device, open the console and type `java -version`... If the output looks like 
> Java is not recognized as an internal or external command

or the version is less then 11, just download JWM. For example, from the [Oracle](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) (the company, that supports Java). Installation of the JDK (which includes JVM + libraries) seems not to be tricky.
ISInCa executables don't need special installation, so you can run ISInCa right after downloading

# Launch

For your convinienced **CONSOLE** and **GUI** modes were made in ISInCA. GUI is based on the **JavaFX** package which is included in the executable (as from the 11-th version it was removed from the JDK)

## GUI

GUI is the default mode so there is no need in any launch arguments, so just run it... Main window of the GUI mode looks like:
[![N|Solid](https://i.ibb.co/ckSBLs9/10-10-2020-180648.png)](https://github.com/mauveferret)
The interface seems to be user-friendly. In case of troubles check `help` tab. 
The main feature of the GUI mode os the posibility to visualize postprocessed data. For this ISInCa was equipped with **JFreeChart** library. It allows to create, modify and safe plots. Look at the example for a polar distribution:

[![N|Solid](https://i.ibb.co/Ykvbk2N/10-10-2020-180630.png)](https://github.com/mauveferret)

## Console

Console mode is launched by the argument `-c`. For example:

` java -jar isinca.jar -c isinca.xml`

where `java` is the call to the JVM, `-jar` is command to execute file `isinca.jar`, and `isinca.xml` is the path to the config file with instructions to the ISInCa.... **Not finished, to lazy. will end later...** 
