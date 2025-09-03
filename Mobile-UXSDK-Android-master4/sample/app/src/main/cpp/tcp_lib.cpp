//
// Created by AZUS on 10/25/2021.
//
//---------------------------------------------------------------------------
#include <cstdint>
#include <cstring>
#include <endian.h>
#include <exception>
#include <jni.h>
#include <cassert>
#include <new>
#include <filesystem>
//---------------------------------------------------------------------------
#include "tcp_lib.h"
#include "tcp_server.h"
#include "tcp_client.h"
//---------------------------------------------------------------------------
//#include <thread>
#include <pthread.h>
//---------------------------------------------------------------------------
//#include "ServerSocket.h"
//#include "ClientSocket.h"
//#include "Sockets/ListenSocket.h"
//#include "Sockets/SocketHandler.h"
//---------------------------------------------------------------------------
#include "SFML/Network.hpp"
#include <iostream>
//---------------------------------------------------------------------------
static tcp_server *server = nullptr;
static tcp_client *client = nullptr;
//---------------------------------------------------------------------------
static JavaVM *jvm = nullptr;
static jobject tcp_callbacks_object = nullptr;
//---------------------------------------------------------------------------
// Serializes (msg) into a flat array of bytes, and returns the number of bytes written
// Note that (outBuf) must be big enough to hold any Message you might have, or there will
// be a buffer overrun!  Modifying this function to check for that problem and
// error out instead is left as an exercise for the reader.
int SerializeMessage(const struct Message & msg, char * outBuf)
{
    char * outPtr = outBuf;

    int32_t sendID = htonl(msg.id);   // htonl will make sure it gets sent in big-endian form
    memcpy(outPtr, &sendID, sizeof(sendID));
    outPtr += sizeof(sendID);

    int32_t sendLen = htonl(msg.message_length);
    memcpy(outPtr, &sendLen, sizeof(sendLen));
    outPtr += sizeof(sendLen);

    memcpy(outPtr, msg.message_str, msg.message_length);  // I'm assuming message_length=strlen(message_str)+1 here
    outPtr += msg.message_length;

    return (outPtr-outBuf);
}

// Deserializes a flat array of bytes back into a Message object.  Returns 0 on success, or -1 on failure.
int DeserializeMessage(const char * inBuf, int numBytes, struct Message & msg)
{
    const char * inPtr = inBuf;

    if (numBytes < sizeof(int32_t)) return -1;  // buffer was too short!
    int32_t recvID = ntohl(*((int32_t *)inPtr));
    inPtr += sizeof(int32_t);
    numBytes -= sizeof(int32_t);
    msg.id = recvID;

    if (numBytes < sizeof(int32_t)) return -1;   // buffer was too short!
    int32_t recvLen = ntohl(*((int32_t *)inPtr));
    inPtr += sizeof(int32_t);
    numBytes -= sizeof(int32_t);
    msg.message_length = recvLen;       if (msg.message_length > 1024) return -1;  /* Sanity check, just in case something got munged we don't want to allocate a giant array */

    msg.message_str = new char[msg.message_length];
    memcpy(msg.message_str, inPtr, numBytes);
    return 0;
}
//---------------------------------------------------------------------------
void runTcpServer(int port)
{
    // Create a server socket to accept new connections
    sf::TcpListener listener;

    // Listen to the given port for incoming connections
    if (listener.listen(port) != sf::Socket::Done)
        return;
    addError("Server is listening to port " + std::to_string(port) + ", waiting for connections... ");

    // Wait for a connection
    sf::TcpSocket socket;
    if (listener.accept(socket) != sf::Socket::Done)
        return;
    addError("Client connected: " + socket.getRemoteAddress().toString());

    // Send a message to the connected client
    const char out[] = "Hi, I'm the server";
    if (socket.send(out, sizeof(out)) != sf::Socket::Done)
        return;
    addError("Message sent to the client: " + std::string(out));

    // Receive a message back from the client
    char in[128];
    std::size_t received;
    if (socket.receive(in, sizeof(in), received) != sf::Socket::Done)
        return;
    addError("Answer received from the client: " + std::string(in));
}
//---------------------------------------------------------------------------
void runTcpClient(std::string ip, int port)
{
    // Ask for the server address
    sf::IpAddress server(ip);

    // Create a socket for communicating with the server
    sf::TcpSocket socket;

    // Connect to the server
    if (socket.connect(server, port) != sf::Socket::Done)
        return;
    addError("Connected to server " + server.toString());

    // Receive a message from the server
    char in[128];
    std::size_t received;
    if (socket.receive(in, sizeof(in), received) != sf::Socket::Done)
        return;
    addError("Message received from the server: " + std::string(in));

    // Send an answer to the server
    const char out[] = "Hi, I'm a client";
    if (socket.send(out, sizeof(out)) != sf::Socket::Done)
        return;
    addError("Message sent to the server: " + std::string(out));
}
//---------------------------------------------------------------------------
typedef struct{
    std::string ip;
    int port;
} mv_addr;
//---------------------------------------------------------------------------
//void server_callback(const int port)
void *server_callback(void *arg)
{
    addError("Start Server Listening...");
    mv_addr *addr = (mv_addr *)arg;
    if(addr == nullptr) return nullptr;
//    addError("Start Server Listening on Port: "+std::to_string(port));
    runTcpServer(addr->port);

//    SocketHandler hServer;
//    ListenSocket<ServerSocket> listenSocket(hServer);
//    if (!listenSocket.Bind(port,10))
//    {
//        hServer.Add(&listenSocket);
//        hServer.Select(1,0);
//        while (hServer.GetCount())
//        {
//            hServer.Select(1,0);
//        }
//    }

//    serverThread.detach();

    // exit the current thread
//    pthread_exit(nullptr);
    return nullptr;
}
//---------------------------------------------------------------------------
//void client_callback(const std::string &ip, const int port)
void *client_callback(void *arg)
{
    addError("Start Client Connecting...");
    mv_addr *addr = (mv_addr *)arg;
    if(addr == nullptr) return nullptr;
//    addError("Start Client Connecting on IP: "+ip+", and Port: "+std::to_string(port));
    runTcpClient(addr->ip, addr->port);

//    SocketHandler hClient;
//    ClientSocket clientSocket(hClient,"TIME");
//    clientSocket.Open(ip,port);
//    // Add after Open
//    hClient.Add(&clientSocket);
//    hClient.Select(1,0);
//    while (hClient.GetCount())
//    {
//        hClient.Select(1,0);
//    }

//    clientThread.detach();
    // exit the current thread
//    pthread_exit(nullptr);
    return nullptr;
}
//---------------------------------------------------------------------------
int server_main(int port)
{
//    addError("Start Server Listening...");

//    std::thread serverThread(server_callback,port);

    mv_addr addr;
    addr.ip = "0.0.0.0";
    addr.port = port;

    pthread_t thread = 0;
    pthread_create(&thread, nullptr, server_callback, &addr);
//    pthread_join(thread, nullptr);
    return 0;
}
//---------------------------------------------------------------------------
int client_main(const std::string& ip, int port)
{
//    addError("Start Client Connecting...");

//    std::thread clientThread(client_callback, ip, port);

    mv_addr addr;
    addr.ip = ip;
    addr.port = port;

    pthread_t thread = 0;
    pthread_create(&thread, nullptr, client_callback, &addr);
//    pthread_join(thread, nullptr);
    return 0;
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jint JNICALL
Java_com_oghab_mapviewer_mapviewer_Tab_1Messenger_tcp_1server_1listen(JNIEnv *env, jclass clazz,
                                                                      jint port) {
    try{
        int res = 0;
//        addError("Start Server Listening...");
//        if(server == nullptr){
//            server = new tcp_server();
//        }
//        if(server != nullptr){
//            res = server->listen(port);
//        }

        try{
//            if(jvm == nullptr)  return res;
//            jint rs = jvm->AttachCurrentThread(&env, nullptr);
//            assert (rs == JNI_OK);

            server_main(port);

//            jvm->DetachCurrentThread();
        }catch(...){
    //        return false;
//            jclass exception_cls = (env)->FindClass("java/lang/IllegalArgumentException");
//            env->ThrowNew(exception_cls, "Error in server_main");
            //        throw std::runtime_error("Error in sendFileNative");
            //        addError("Error in sendFileNative");
        }

        return res;
    }catch(...) {
        rethrow_cpp_exception_as_java_exception();
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jint JNICALL
Java_com_oghab_mapviewer_mapviewer_Tab_1Messenger_tcp_1server_1stop(JNIEnv *env, jclass clazz) {
    try{
        if(server != nullptr){
            server->stop();
        }
    }catch(...) {
        rethrow_cpp_exception_as_java_exception();
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jint JNICALL
Java_com_oghab_mapviewer_mapviewer_Tab_1Messenger_tcp_1client_1connect(JNIEnv *env, jclass clazz,
                                                                       jstring ip, jint port) {
    try{
        int res = 0;

        const char* temp_ip = env->GetStringUTFChars(ip, NULL);
        std::string IP = temp_ip;

//        if(client == nullptr){
//            client = new tcp_client();
//        }
//        if(client != nullptr){
//            res = client->connect(temp_ip, port);
//        }

        env->ReleaseStringUTFChars(ip, temp_ip);  // release resources

        try{
//            if(jvm == nullptr)  return res;
//            jint rs = jvm->AttachCurrentThread(&env, nullptr);
//            assert (rs == JNI_OK);

            client_main(IP, port);

//            jvm->DetachCurrentThread();
        }catch(...){
            //        return false;
//            jclass exception_cls = (env)->FindClass("java/lang/IllegalArgumentException");
//            env->ThrowNew(exception_cls, "Error in client_main");
            //        throw std::runtime_error("Error in sendFileNative");
            //        addError("Error in sendFileNative");
        }

        return res;
    }catch(...) {
        rethrow_cpp_exception_as_java_exception();
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jint JNICALL
Java_com_oghab_mapviewer_mapviewer_Tab_1Messenger_tcp_1client_1stop(JNIEnv *env, jclass clazz) {
    try{
        if(client != nullptr){
            client->stop();
        }
    }catch(...) {
        rethrow_cpp_exception_as_java_exception();
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT void JNICALL
Java_com_oghab_mapviewer_mapviewer_Tab_1Messenger_JNIMethodWithParameter(JNIEnv *env, jclass clazz,
                                                                         jobject main_java_class,
                                                                         jint param) {
    try{
        jclass cls = env->GetObjectClass(main_java_class);
        jmethodID method = env->GetMethodID(cls, "javaMethodTobeCalledInJNIWithParameter", "(I)V");
        env->CallVoidMethod(main_java_class, method, param);
    }catch(...) {
        rethrow_cpp_exception_as_java_exception();
    }
}
//---------------------------------------------------------------------------
void rethrow_cpp_exception_as_java_exception()
{
//    if(jvm == nullptr)  return;
//    JNIEnv *env;
//    jint rs = jvm->AttachCurrentThread(&env, nullptr);
//    assert (rs == JNI_OK);
//    try
//    {
//        throw; // This allows to determine the type of the exception
//    }
//    catch (const std::bad_alloc& e) {
//        jclass jc = env->FindClass("java/lang/OutOfMemoryError");
//        if(jc) env->ThrowNew (jc, e.what());
//    }
//    catch (const std::ios_base::failure& e) {
//        jclass jc = env->FindClass("java/io/IOException");
//        if(jc) env->ThrowNew (jc, e.what());
//    }
//    catch (const std::exception& e) {
//        /* unknown exception (may derive from std::exception) */
//        jclass jc = env->FindClass("java/lang/Error");
//        if(jc) env->ThrowNew (jc, e.what());
//    }
//    catch (...) {
//        /* Oops I missed identifying this exception! */
//        jclass jc = env->FindClass("java/lang/Error");
//        if(jc) env->ThrowNew (jc, "Unidentified exception => "
//                                  "Improve rethrow_cpp_exception_as_java_exception()" );
//    }
}
//---------------------------------------------------------------------------
//void addError(const char *msg){
//    try{
//        if(msg == nullptr)  return;
//        if(jvm == nullptr)  return;
//        JNIEnv *env;
//        jint rs = jvm->AttachCurrentThread(&env, nullptr);
//        assert (rs == JNI_OK);
//        if(tcp_callbacks_object == nullptr)  return;
//        if(env == nullptr)  return;
//        jclass callbacks_class = env->GetObjectClass(tcp_callbacks_object);
//        if(callbacks_class == nullptr)  return;
//        jmethodID addError_ID = env->GetMethodID(callbacks_class, "addError", "(Ljava/lang/String;)V");
//        if(addError_ID == nullptr)  return;
//        env->CallVoidMethod(tcp_callbacks_object, addError_ID, env->NewStringUTF(msg));
//    }catch(...) {
//        rethrow_cpp_exception_as_java_exception();
//    }
//}
//---------------------------------------------------------------------------
void addError(std::string msg){
    try{
        if(jvm == nullptr)  return;
        JNIEnv *env;
        jint rs = jvm->AttachCurrentThread(&env, nullptr);
        assert (rs == JNI_OK);
        if(tcp_callbacks_object == nullptr)  return;
        if(env == nullptr)  return;
        jclass callbacks_class = env->GetObjectClass(tcp_callbacks_object);
        if(callbacks_class == nullptr)  return;
        jmethodID addError_ID = env->GetMethodID(callbacks_class, "addError", "(Ljava/lang/String;)V");
        if(addError_ID == nullptr)  return;
        env->CallVoidMethod(tcp_callbacks_object, addError_ID, env->NewStringUTF(msg.c_str()));
//        jvm->DetachCurrentThread();
    }catch(...) {
        rethrow_cpp_exception_as_java_exception();
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT void JNICALL
Java_com_oghab_mapviewer_mapviewer_Tab_1Messenger_set_1jni_1callbacks(JNIEnv *env, jclass clazz,
                                                                      jobject callbacks_object) {
    try{
        jint rs = env->GetJavaVM(&jvm);
        assert (rs == JNI_OK);

        int status = env->GetJavaVM(&jvm);
        if(status != 0) {
            // Fail!
        }

        tcp_callbacks_object = env->NewGlobalRef(callbacks_object);
    }catch(...) {
        rethrow_cpp_exception_as_java_exception();
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT void JNICALL
Java_com_oghab_mapviewer_mapviewer_Tab_1Messenger_remove_1jni_1callbacks(JNIEnv *env,
                                                                         jclass clazz) {
    try{
        if (jvm == nullptr) {
            return;
        }

        env->DeleteGlobalRef(tcp_callbacks_object);
        tcp_callbacks_object = nullptr;
    }catch(...) {
        rethrow_cpp_exception_as_java_exception();
    }
}
//---------------------------------------------------------------------------
