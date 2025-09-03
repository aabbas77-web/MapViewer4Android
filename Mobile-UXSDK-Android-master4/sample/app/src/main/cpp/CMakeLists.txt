# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_VERBOSE_MAKEFILE on)

#include_directories(src/main/c)
include_directories( SFML/ )

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
                native-lib

                # Sets the library as a shared library.
                SHARED

                # Provides a relative path to your source file(s).
                native-lib.cpp
                LatLong-UTMconversion.cpp
                GeoLocationLib.cpp
                mission.cpp
                iniFile.cpp
                Protection.cpp
                tcp_lib.cpp
                tcp_server.cpp
                tcp_client.cpp
                tcp_io_handler.cpp
                hosien.cpp
                hasan.cpp
                adam.cpp
                utils.cpp


                SFML/System/Activity.cpp
                SFML/System/Clock.cpp
                SFML/System/ClockImpl.cpp
                SFML/System/Err.cpp
                SFML/System/FileInputStream.cpp
                SFML/System/Lock.cpp
                SFML/System/MemoryInputStream.cpp
                SFML/System/Mutex.cpp
                SFML/System/MutexImpl.cpp
                SFML/System/NativeActivity.cpp
                SFML/System/ResourceStream.cpp
                SFML/System/Sleep.cpp
                SFML/System/SleepImpl.cpp
                SFML/System/String.cpp
                SFML/System/Thread.cpp
                SFML/System/ThreadImpl.cpp
                SFML/System/ThreadLocal.cpp
                SFML/System/ThreadLocalImpl.cpp
                SFML/System/Time.cpp


                SFML/Network/Ftp.cpp
                SFML/Network/Http.cpp
                SFML/Network/IpAddress.cpp
                SFML/Network/Packet.cpp
                SFML/Network/Socket.cpp
                SFML/Network/SocketImpl.cpp
                SFML/Network/SocketSelector.cpp
                SFML/Network/TcpListener.cpp
                SFML/Network/TcpSocket.cpp
                SFML/Network/UdpSocket.cpp

#                ClientSocket.cpp
#                ServerSocket.cpp
#                Sockets/Ajp13Socket.cpp
#                Sockets/AjpBaseSocket.cpp
#                Sockets/Base64.cpp
#                Sockets/Debug.cpp
#                Sockets/Event.cpp
#                Sockets/EventHandler.cpp
#                Sockets/EventTime.cpp
#                Sockets/Exception.cpp
#                Sockets/File.cpp
#                Sockets/FileStream.cpp
#                Sockets/HttpBaseSocket.cpp
#                Sockets/HttpClientSocket.cpp
#                Sockets/HttpdCookies.cpp
#                Sockets/HttpDebugSocket.cpp
#                Sockets/HttpdForm.cpp
#                Sockets/HttpdSocket.cpp
#                Sockets/HttpGetSocket.cpp
#                Sockets/HttpPostSocket.cpp
#                Sockets/HttpPutSocket.cpp
#                Sockets/HttpRequest.cpp
#                Sockets/HttpResponse.cpp
#                Sockets/HTTPSocket.cpp
#                Sockets/HttpTransaction.cpp
#                Sockets/IEventOwner.cpp
#                Sockets/Ipv4Address.cpp
#                Sockets/Ipv6Address.cpp
#                Sockets/Json.cpp
#                Sockets/Lock.cpp
#                Sockets/MemFile.cpp
#                Sockets/Mutex.cpp
#                Sockets/Parse.cpp
#                Sockets/ResolvServer.cpp
#                Sockets/ResolvSocket.cpp
#                Sockets/SctpSocket.cpp
#                Sockets/Semaphore.cpp
#                Sockets/SmtpdSocket.cpp
#                Sockets/Socket.cpp
#                Sockets/SocketHandler.cpp
#                Sockets/SocketHandlerEp.cpp
#                Sockets/SocketHandlerThread.cpp
##                Sockets/Sockets-config.cpp
#                Sockets/SocketStream.cpp
#                Sockets/SocketThread.cpp
##                Sockets/socket_include.cpp
#                Sockets/SSLInitializer.cpp
#                Sockets/StdoutLog.cpp
#                Sockets/StreamSocket.cpp
#                Sockets/StreamWriter.cpp
#                Sockets/TcpSocket.cpp
#                Sockets/Thread.cpp
#                Sockets/UdpSocket.cpp
#                Sockets/Utility.cpp
#                Sockets/XmlDocument.cpp
#                Sockets/XmlException.cpp
#                Sockets/XmlNode.cpp

             )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log
        )

find_library( # Sets the name of the path variable.
        android-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        android
        )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       native-lib

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib}
                       ${android-lib})