# FlashBang
## Automatic Android distance calculator using time delay between light and sound

### Introduction

When an event happens (for example, a lightning strike), it can produce light and sound.  Since light travels much faster than sound, you see the event before you hear it.  Using the time difference, you can actually calculate the distance.

### Motivation and Features

Many apps exist that purport to calculate distance to an event in this way.  Unfortunately, these are universally moronic affairs, with a button you press when you see the event and another when you hear it.  This is horrendously inaccurate and can be confusing, especially for nearby events.

By contrast, FlashBang does the detection and correlation automatically.  When possible, FlashBang also corrects for pressure, temperature, and relative humidity.  This allows for more accurate derived quantities, including the refractive index of air, the speed of light, and the speed of sound.

### Usage

To use, simply point your Android device's light sensor at the event and observe.  FlashBang will attempt to discern an event's anomalous sound and light and, on detection, will automatically produce the readout of the distance.  The associated readouts tell you which factors were used in the calculation.

### Contribute / Contact

FlashBang is developed by Ian Mallett and is both free and open-source. The source and project pages can be found at:

> [https://github.com/imallett/FlashBang](https://github.com/imallett/FlashBang)

To help recoup my $25 Android developer fee, consider donating on my website ([geometrian.com](http://geometrian.com/index.php)).  If you would like to contact me, or contribute in another way (for example, by reporting bugs or adding/improving translations), send email to:

> ian [^at^] geometrian.com