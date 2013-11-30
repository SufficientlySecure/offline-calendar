# Offline Calendar

Lets you add calendars to the Calendar app, which are not synchronized and are accessible only on the device itself.
Create the calendar in this app and it will then be available in the Calendar app.

Works on Android >= 2.2 using https://github.com/dschuermann/android-calendar-compatibility

# Installation/Translations

see http://sufficientlysecure.org/android-apps/

# Build with Gradle

1. Have Android SDK "tools", "platform-tools", and "build-tools" directories in your PATH (http://developer.android.com/sdk/index.html)
2. Export ANDROID_HOME pointing to your Android SDK
3. Download Android Support Repository, and Google Repository using Android SDK Manager
4. Execute ``./gradlew build``

# Contribute

Fork Offline Calendar and do a Pull Request. I will merge your changes back into the main project.

# Libraries

All Android Library projects are under "libraries".

# Coding Style

## Code
* Indentation: 4 spaces, no tabs
* Maximum line width for code and comments: 100
* Opening braces don't go on their own line
* Field names: Non-public, non-static fields start with m.
* Acronyms are words: Treat acronyms as words in names, yielding !XmlHttpRequest, getUrl(), etc.

See http://source.android.com/source/code-style.html

## XML
* XML Maximum line width 999
* XML: Split multiple attributes each on a new line (Eclipse: Properties -> XML -> XML Files -> Editor)
* XML: Indent using spaces with Indention size 4 (Eclipse: Properties -> XML -> XML Files -> Editor)

See http://www.androidpolice.com/2009/11/04/auto-formatting-android-xml-files-with-eclipse/

# Licenses
Offline Calendar is licensed under the GPLv3+.  
The file LICENSE includes the full license text.

## Details
Offline Calendar is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Offline Calendar is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Offline Calendar.  If not, see <http://www.gnu.org/licenses/>.

## Libraries

* Offline Calendar is based on "Private Calendar" by Harald Seltner  
  http://code.google.com/p/private-calendar  
  GPLv3+

* Holo ColorPicker  
  https://github.com/LarsWerkman/HoloColorPicker  
  Apache License v2

* android-calendar-compatibility  
  https://github.com/dschuermann/android-calendar-compatibility  
  Apache License v2

## Images

* icon.svg  
  Based on Tango Icon Library  
  http://tango.freedesktop.org/  
  Public Domain (Tango Icon Library)