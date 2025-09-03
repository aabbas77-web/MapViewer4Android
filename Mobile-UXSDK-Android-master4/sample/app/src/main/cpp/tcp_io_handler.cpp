//
// Created by AZUS on 10/26/2021.
//

#include "tcp_io_handler.h"
#include "tcp_lib.h"
#include <jni.h>
#include <vector>
#include <fstream>
#include "HH.h"
#include "adam.h"
#include <string>
#include "Protection.h"
#include "utils.h"
//---------------------------------------------------------------------------
//#define MV_DEBUG
//---------------------------------------------------------------------------
bool checkExceptions(JNIEnv* env) {
    try{
        if(env->ExceptionCheck()) {
            env->ExceptionDescribe(); // writes to logcat
            env->ExceptionClear();

            jclass newExcCls = env->FindClass("java/lang/IllegalArgumentException");
            if (newExcCls == nullptr) { /* Unable to find the new exception class, give up. */
                return true;
            }
            env->ThrowNew(newExcCls, "thrown from C code");
            return true;
        }
    }
    catch (...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return false;
    }
    return false;
}
//---------------------------------------------------------------------------
// sendFileNative
namespace sfn{
    static const std::string c_str1 = "uvp";// out
    static const std::string c_str2 = "<sfujsXuojsQ0pj0bwbkM";// Ljava/io/PrintWriter;

    static const std::string c_str3 = "omuojsq";// println
    static const std::string c_str4 = "W*<hojsuT0hobm0bwbkM)";// (Ljava/lang/String;)V

    static const std::string c_str5 = "itvmg";// flush
    static const std::string c_str6 = "W*)";// ()V

    static std::string c_dstr1 = mv_dm(c_str1);
    static std::string c_dstr2 = mv_dm(c_str2);
    static std::string c_dstr3 = mv_dm(c_str3);
    static std::string c_dstr4 = mv_dm(c_str4);
    static std::string c_dstr5 = mv_dm(c_str5);
    static std::string c_dstr6 = mv_dm(c_str6);
}
//---------------------------------------------------------------------------
// receiveFileNative
namespace rfn{
    static const std::string c_str1 = "oj";// in
    static const std::string c_str2 = "<sfebfSefsfggvC0pj0bwbkM";// Ljava/io/BufferedReader;

    static const std::string c_str3 = "fojMebfs";// readLine
    static const std::string c_str4 = "<hojsuT0hobm0bwbkM*)";// ()Ljava/lang/String;

    static std::string c_dstr1 = mv_dm(c_str1);
    static std::string c_dstr2 = mv_dm(c_str2);
    static std::string c_dstr3 = mv_dm(c_str3);
    static std::string c_dstr4 = mv_dm(c_str4);
}
//---------------------------------------------------------------------------
#define CBC 1
#define CTR 1
#define ECB 1
static struct ADAM_STRUCT ctx;
static const int AHhha_SIZE = 16;
//static uint8_t AHhha[] = {0xaf, 0x84, 0x34, 0x98, 0x23, 0xef, 0xd3, 0x98, 0xba, 0x7f, 0x15, 0x43, 0x92, 0xc2, 0xef, 0xd4 };
//static uint8_t AHhha[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
static uint8_t AHhha[] = {(uint8_t)'0', (uint8_t)'1', (uint8_t)'2', (uint8_t)'3', (uint8_t)'4', (uint8_t)'5', (uint8_t)'6', (uint8_t)'7', (uint8_t)'8', (uint8_t)'9', (uint8_t)'a', (uint8_t)'b', (uint8_t)'c', (uint8_t)'d', (uint8_t)'e', (uint8_t)'f' };
static uint8_t iv[]  = { 0xff, 0xa1, 0xb2, 0xc3, 0xd4, 0xe5, 0xf6, 0x97, 0x88, 0x79, 0x6a, 0x5b, 0x4c, 0x3d, 0x2e, 0x1f };
static bool is_initialized = false;
//---------------------------------------------------------------------------
void init_HH(bool force){
    try{
        if(force)   is_initialized = false;
        if(!is_initialized){
            ADAM_init_struct(&ctx, AHhha);
//            ADAM_init_struct_iv(&ctx, AHhha, iv);
            is_initialized = true;
        }
    }
    catch (...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
void hosien_do(uint8_t* buf){
    try{
        init_HH(false);
        ADAM_1_hosien(&ctx, buf);
    }
    catch (...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
void hasan_do(uint8_t* buf){
    try{
        init_HH(false);
        ADAM_1_hasan(&ctx, buf);
    }
    catch (...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
#define MV_COPY  0
#define MV_ENCODE  1
#define MV_DECODE  2
//---------------------------------------------------------------------------
void adam_buffer(const char *in_buf, size_t size, int nEnc) {
    try{
//        if(nEnc == MV_ENCODE){
//            init_HH(false);
//            ADAM_2_hosien_buffer(&ctx, (uint8_t *) in_buf, size);
//        }
//        else if(nEnc == MV_DECODE){
//            init_HH(false);
//            ADAM_2_hasan_buffer(&ctx, (uint8_t *)in_buf, size);
//        }

        uint8_t buf[AES_BLOCKLEN];
        auto *pInBuf = (uint8_t *)in_buf;
        uint8_t n = size / (size_t)AES_BLOCKLEN;
        uint8_t r = size - n * AES_BLOCKLEN;
        for(int i=0;i<n;i++)
        {
            memcpy(buf, pInBuf, AES_BLOCKLEN);
            if(nEnc == MV_ENCODE){
                hosien_do(buf);
            }
            else if(nEnc == MV_DECODE){
                hasan_do(buf);
            }
            memcpy(pInBuf, buf, AES_BLOCKLEN);
            pInBuf += AES_BLOCKLEN;
        }
        if(r > 0){
            memset(buf,0,AES_BLOCKLEN);
            memcpy(buf, pInBuf, r);
            if(nEnc == MV_ENCODE){
                hosien_do(buf);
            }
            else if(nEnc == MV_DECODE){
                hasan_do(buf);
            }
            memcpy(pInBuf, buf, r);
        }
    }
    catch (...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
long file_size(const char *filename){
    try{
        std::ifstream file( filename, std::ios::binary | std::ios::ate);
        long size = (long)file.tellg();
        file.close();
        return size;
    }
    catch (...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return 0;
    }
}
//---------------------------------------------------------------------------
void adam_file(const char *in_filename, const char *out_filename, int nEnc) {
    try{
        long size = file_size(in_filename);
        char *buf = new char[size];

        std::ifstream rf(in_filename, std::ios::in | std::ios::binary);
        rf.read(buf, size);
        rf.close();

        if(nEnc != MV_COPY){
            adam_buffer(buf, size, nEnc);
        }

        std::ofstream wf(out_filename, std::ios::out | std::ios::binary);
        if(!wf) {
#ifdef MV_DEBUG
            addError("Cannot create file!");
#endif
            return;
        }
        wf.write(buf, size);
        wf.flush();
        wf.close();
        delete[] buf;
        if(!wf.good()) {
#ifdef MV_DEBUG
            addError("Error occurred at writing time!");
#endif
            return;
        }
    }
    catch (...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
void mv_l_key(char *filename)
{
    try
    {
        FILE *file = fopen(filename,"rb");
        if(file != nullptr)
        {
            memset(AHhha,0,AHhha_SIZE);
            fread(AHhha,AHhha_SIZE,sizeof(uint8_t),file);
            fclose(file);
        }
        init_HH(true);
    }
    catch(...)
    {
    }
}
//---------------------------------------------------------------------------
void mv_s_key(char *filename)
{
    try
    {
        FILE *file = fopen(filename,"wb");
        if(file != nullptr)
        {
            fwrite(AHhha,AHhha_SIZE,sizeof(uint8_t),file);
            fclose(file);
        }
    }
    catch(...)
    {
    }
}
//---------------------------------------------------------------------------
void update_encryption_key(const char *new_key) {
    try{
        for(int i=0;i<AHhha_SIZE;i++){
            AHhha[i] = (uint8_t)new_key[i];
        }
        init_HH(true);

        // just for test
//        std::string str;
//        for(unsigned char value : AHhha){
//            str += (char)value;
//        }
//        mv_log(__FILE__, __LINE__,"MapViewer LOG");
//        mv_log(__FILE__, __LINE__,str.c_str());
    }
    catch (...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
static char strAHhha[AHhha_SIZE];
const char *get_encryption_key() {
    try{
//        std::string str;
//        for(uint8_t value : AHhha){
//            str += (char)value;
//        }
//        return const_cast<char *>(str.c_str());

        memset(strAHhha,0,AHhha_SIZE);
        for(int i=0;i<AHhha_SIZE;i++){
            strAHhha[i] = (char)AHhha[i];
        }
        return strAHhha;
    }
    catch (...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return nullptr;
    }
}
//---------------------------------------------------------------------------
void adam_file_incremental(const char *in_filename, const char *out_filename, int nEnc) {
    try{
        size_t bytes_read;
        uint8_t buf[AES_BLOCKLEN];
        FILE *ifp = fopen(in_filename, "rb");
        FILE *ofp = fopen(out_filename, "wb");
        while (true) {
            bytes_read = fread(buf, 1, AES_BLOCKLEN, ifp);
            if(bytes_read <= 0) break;
            if (bytes_read < AES_BLOCKLEN){
                for(int i=bytes_read;i<AES_BLOCKLEN;i++){
                    buf[i] = 0;
                }
            }
            if(nEnc == MV_ENCODE){
                hosien_do(buf);
            }
            else if(nEnc == MV_DECODE){
                hasan_do(buf);
            }
            fwrite(buf, 1, AES_BLOCKLEN, ofp);
            if (bytes_read < AES_BLOCKLEN)  break;
        }
        fclose(ifp);
        fflush(ofp);
        fclose(ofp);
    }
    catch (...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_oghab_mapviewer_mapviewer_tcp_1io_1handler_sendFileNative(JNIEnv *env, jobject handlerObject,
                                                                   jstring command,
                                                                   jstring filename,
                                                                   jstring new_filename,
                                                                   jstring file_name,
                                                                   jlong filesize,
                                                                   jboolean save, jboolean is_encoded) {
//    jclass exception_cls = (env)->FindClass("java/lang/IllegalArgumentException");
//    env->ThrowNew(exception_cls, "Error in sendFileNative");

    try{
#ifdef MV_DEBUG
        addError(sfn::c_dstr1);
    addError(sfn::c_dstr2);
    addError(sfn::c_dstr3);
    addError(sfn::c_dstr4);
    addError(sfn::c_dstr5);
    addError(sfn::c_dstr6);
#endif

        std::string strFile;
        try{
            const char* temp_in_file = env->GetStringUTFChars(filename, nullptr);
            if(checkExceptions(env)){
                return false;
            }
            strFile = temp_in_file;
            env->ReleaseStringUTFChars(filename, temp_in_file);  // release resources
            if(checkExceptions(env)){
                return false;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return false;
        }

        std::string strNewFile;
        try{
            const char* temp_out_file = env->GetStringUTFChars(new_filename, nullptr);
            if(checkExceptions(env)){
                return false;
            }
            strNewFile = temp_out_file;
            env->ReleaseStringUTFChars(new_filename, temp_out_file);  // release resources
            if(checkExceptions(env)){
                return false;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return false;
        }

        std::string fname;
        try{
            const char* temp_file_name = env->GetStringUTFChars(file_name, nullptr);
            if(checkExceptions(env)){
                return false;
            }
            fname = temp_file_name;
            env->ReleaseStringUTFChars(file_name, temp_file_name);  // release resources
            if(checkExceptions(env)){
                return false;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return false;
        }

        jclass handlerClass;
        try{
            // tcp_io_handler handlerObject;
            handlerClass = env->GetObjectClass(handlerObject);
            if(checkExceptions(env)){
                return false;
            }
            if(handlerClass == nullptr){
#ifdef MV_DEBUG
                addError("handlerClass == nullptr");
#endif
                return false;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return false;
        }

        jfieldID outFieldID;
        try{
            // PrintWriter out;
#ifdef MV_DEBUG
            outFieldID = env->GetFieldID(handlerClass, "out", "Ljava/io/PrintWriter;");
#else
            outFieldID = env->GetFieldID(handlerClass, sfn::c_dstr1.c_str(), sfn::c_dstr2.c_str());
#endif
            if(checkExceptions(env)){
                return false;
            }
            if(outFieldID == nullptr){
#ifdef MV_DEBUG
                addError("outFieldID == nullptr");
#endif
                return false;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return false;
        }

        jobject outObject;
        try{
            outObject = env->GetObjectField(handlerObject, outFieldID);
            if(checkExceptions(env)){
                return false;
            }
            if(outObject == nullptr){
#ifdef MV_DEBUG
                addError("outObject == nullptr");
#endif
                return false;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return false;
        }

        jclass outClass;
        try{
            outClass = env->GetObjectClass(outObject);
            if(checkExceptions(env)){
                return false;
            }
            if(outClass == nullptr){
#ifdef MV_DEBUG
                addError("outClass == nullptr");
#endif
                return false;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return false;
        }

        jmethodID printlnID;
        try{
#ifdef MV_DEBUG
            printlnID = env->GetMethodID(outClass, "println", "(Ljava/lang/String;)V");
#else
            printlnID = env->GetMethodID(outClass, sfn::c_dstr3.c_str(), sfn::c_dstr4.c_str());
#endif
            if(checkExceptions(env)){
                return false;
            }
            if(printlnID == nullptr){
#ifdef MV_DEBUG
                addError("printlnID == nullptr");
#endif
                return false;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return false;
        }

        jmethodID flushID;
        try{
#ifdef MV_DEBUG
            flushID = env->GetMethodID(outClass, "flush", "()V");
#else
            flushID = env->GetMethodID(outClass, sfn::c_dstr5.c_str(), sfn::c_dstr6.c_str());
#endif
            if(checkExceptions(env)){
                return false;
            }
            if(flushID == nullptr){
#ifdef MV_DEBUG
                addError("flushID == nullptr");
#endif
                return false;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return false;
        }

        unsigned long inlen,new_len;
        unsigned long outlen;
        std::vector<char> inBuf(1,0);
        std::vector<char> outBuf(1,0);
        try{
            FILE * pFile = fopen (strFile.c_str(), "rb");
            if (pFile == nullptr){
#ifdef MV_DEBUG
                addError("File error");
#endif
                return false;
            }

            // obtain file size:
//            fseek (pFile , 0 , SEEK_END);
//            inlen = ftell (pFile);
//            rewind (pFile);
            inlen = filesize;

            unsigned long N,R;
            N = inlen/16;
            R = inlen - 16 * N;
            if(R > 0)
                new_len = 16*N+16;
            else
                new_len = 16*N;

            // allocate memory to contain the whole file:
            inBuf.resize(new_len + 1);
//            char * buffer = (char*) malloc(sizeof(char)*new_len);
//            if (buffer == nullptr){
//#ifdef MV_DEBUG
//                addError("Memory error");
//#endif
//                return false;
//            }
//            memset(buffer,0,new_len);
            memset(inBuf.data(),0,new_len);

            // copy the file into the buffer:
//            size_t result = fread (buffer,1, inlen, pFile);
            size_t result = fread (inBuf.data(),1, inlen, pFile);
            fclose(pFile);
            if (result != inlen){
#ifdef MV_DEBUG
                addError ("Reading error");
#endif
                return false;
            }

            // encode
            if(is_encoded){
//                adam_buffer(buffer, new_len, MV_ENCODE);
                adam_buffer(inBuf.data(), new_len, MV_ENCODE);
            }

//            unsigned long n,r;
//            n = new_len/4;
//            r = new_len - 4 * n;
//            if(r > 0)
//                outlen = 4 + 4 * n;
//            else
//                outlen = 4 * n;
            outlen = 4 + 4 * ((new_len + 2) / 3);

            outBuf.resize(outlen + 1);
//            hosien(reinterpret_cast<const unsigned char *>(buffer), new_len,
            hosien(reinterpret_cast<const unsigned char *>(inBuf.data()), new_len,
                   reinterpret_cast<unsigned char *>(outBuf.data()), &outlen);

            // terminate
//            free(buffer);
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return false;
        }

        // calls
        try{
            env->CallVoidMethod(outObject, printlnID, command);
            if(checkExceptions(env)){
                return false;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return false;
        }

        try{
            env->CallVoidMethod(outObject, printlnID, env->NewStringUTF(fname.c_str()));
            if(checkExceptions(env)){
                return false;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return false;
        }

        try{
            std::string file_size = std::to_string(inlen);
            env->CallVoidMethod(outObject, printlnID, env->NewStringUTF(file_size.c_str()));
            if(checkExceptions(env)){
                return false;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return false;
        }

        try{
            std::string new_file_size = std::to_string(outlen);
            env->CallVoidMethod(outObject, printlnID, env->NewStringUTF(new_file_size.c_str()));
            if(checkExceptions(env)){
                return false;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return false;
        }

        try{
            env->CallVoidMethod(outObject, printlnID, env->NewStringUTF(outBuf.data()));
            if(checkExceptions(env)){
                return false;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return false;
        }

        try{
            env->CallVoidMethod(outObject, flushID);
            if(checkExceptions(env)){
                return false;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return false;
        }

        try{
            if(save){
                adam_file(strFile.c_str(), strNewFile.c_str(), MV_COPY);
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return false;
        }

        return true;
    }catch(...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        jclass exception_cls = (env)->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(exception_cls, "Error in sendFileNative");
//        throw std::runtime_error("Error in sendFileNative");
//        addError("Error in sendFileNative");
        return false;
    }
}
//---------------------------------------------------------------------------
const char *get_filename_ext(const char *filename) {
    const char *dot = strrchr(filename, '.');
    if(!dot || dot == filename) return "";
    return dot + 1;
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jstring JNICALL
Java_com_oghab_mapviewer_mapviewer_tcp_1io_1handler_receiveFileNative(JNIEnv *env, jobject handlerObject, jstring new_dir, jstring new_section, jboolean is_encoded) {
    try{
#ifdef MV_DEBUG
        addError(rfn::c_dstr1);
    addError(rfn::c_dstr2);
    addError(rfn::c_dstr3);
    addError(rfn::c_dstr4);
#endif

        jclass handlerClass;
        try{
            // tcp_io_handler handlerObject;
            handlerClass = env->GetObjectClass(handlerObject);
            if(checkExceptions(env)){
                return nullptr;
            }
            if(handlerClass == nullptr){
#ifdef MV_DEBUG
                addError("handlerClass == nullptr");
#endif
                return nullptr;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return nullptr;
        }

        jfieldID inFieldID;
        try{
            // BufferedReader in;
#ifdef MV_DEBUG
            inFieldID = env->GetFieldID(handlerClass, "in", "Ljava/io/BufferedReader;");
#else
            inFieldID = env->GetFieldID(handlerClass, rfn::c_dstr1.c_str(), rfn::c_dstr2.c_str());
#endif
            if(checkExceptions(env)){
                return nullptr;
            }
            if(inFieldID == nullptr){
#ifdef MV_DEBUG
                addError("inFieldID == nullptr");
#endif
                return nullptr;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return nullptr;
        }

        jobject inObject;
        try{
            inObject = env->GetObjectField(handlerObject, inFieldID);
            if(checkExceptions(env)){
                return nullptr;
            }
            if(inObject == nullptr){
#ifdef MV_DEBUG
                addError("inObject == nullptr");
#endif
                return nullptr;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return nullptr;
        }

        jclass inClass;
        try{
            inClass = env->GetObjectClass(inObject);
            if(checkExceptions(env)){
                return nullptr;
            }
            if(inClass == nullptr){
#ifdef MV_DEBUG
                addError("inClass == nullptr");
#endif
                return nullptr;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return nullptr;
        }

        jmethodID readLineID;
        try{
#ifdef MV_DEBUG
            readLineID = env->GetMethodID(inClass, "readLine", "()Ljava/lang/String;");
#else
            readLineID = env->GetMethodID(inClass, rfn::c_dstr3.c_str(), rfn::c_dstr4.c_str());
#endif
            if(checkExceptions(env)){
                return nullptr;
            }
            if(readLineID == nullptr){
#ifdef MV_DEBUG
                addError("readLineID == nullptr");
#endif
                return nullptr;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return nullptr;
        }

        jstring command;
        try{
            command = (jstring)env->CallObjectMethod(inObject, readLineID);
            if(checkExceptions(env)){
                return nullptr;
            }
            if(command == nullptr){
#ifdef MV_DEBUG
                addError("filename == nullptr");
#endif
                return nullptr;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return nullptr;
        }

        jboolean isCopy = false;
        try{
            const char *cmd = env->GetStringUTFChars(command, &isCopy);
            if(checkExceptions(env)){
                return nullptr;
            }
            std::string strCMD = cmd;
            env->ReleaseStringUTFChars(command, cmd);
            if(checkExceptions(env)){
                return nullptr;
            }

            if(strCMD != "file"){
                return env->NewStringUTF(strCMD.c_str());
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return nullptr;
        }

        jstring filename;
        try{
            filename = (jstring)env->CallObjectMethod(inObject, readLineID);
            if(checkExceptions(env)){
                return nullptr;
            }
            if(filename == nullptr){
#ifdef MV_DEBUG
                addError("filename == nullptr");
#endif
                return nullptr;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return nullptr;
        }

        std::string fname;
        try{
            isCopy = false;
            const char *fileName = env->GetStringUTFChars(filename, &isCopy);
            if(checkExceptions(env)){
                return nullptr;
            }
            const char *ext = get_filename_ext(fileName);
            if(strcmp(ext,"") == 0){
#ifdef MV_DEBUG
                addError("ext == nullptr");
#endif
                env->ReleaseStringUTFChars(filename, fileName);
                if(checkExceptions(env)){
                    return nullptr;
                }
                return nullptr;
            }
//        std::string strFilename = fileName;
//        std::string fname = strFilename.substr(strFilename.find_last_of("/\\") + 1);
            fname = fileName;
            env->ReleaseStringUTFChars(filename, fileName);
            if(checkExceptions(env)){
                return nullptr;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return nullptr;
        }

        std::string strDirPath;
        try{
            isCopy = false;
            const char *newDir = env->GetStringUTFChars(new_dir, &isCopy);
            if(checkExceptions(env)){
                return nullptr;
            }
            strDirPath = newDir;
            env->ReleaseStringUTFChars(new_dir, newDir);
            if(checkExceptions(env)){
                return nullptr;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return nullptr;
        }

        std::string strSectionPath;
        try{
            isCopy = false;
            const char *newSection = env->GetStringUTFChars(new_section, &isCopy);
            if(checkExceptions(env)){
                return nullptr;
            }
            strSectionPath = newSection;
            env->ReleaseStringUTFChars(new_section, newSection);
            if(checkExceptions(env)){
                return nullptr;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return nullptr;
        }

        std::string newFilename;
        try{
            if (fname.find("frame.jpg") != std::string::npos) {
                newFilename = strDirPath + fname;
            }else{
                newFilename = strDirPath + strSectionPath + fname;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return nullptr;
        }

        jstring file_size;
        try{
            file_size = (jstring)env->CallObjectMethod(inObject, readLineID);
            if(checkExceptions(env)){
                return nullptr;
            }
            if(file_size == nullptr){
#ifdef MV_DEBUG
                addError("file_size == nullptr");
#endif
                return nullptr;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return nullptr;
        }

        std::string fileSize;
        try{
            isCopy = false;
            const char *filesize = env->GetStringUTFChars(file_size, &isCopy);
            if(checkExceptions(env)){
                return nullptr;
            }
            fileSize = filesize;
            env->ReleaseStringUTFChars(file_size, filesize);
            if(checkExceptions(env)){
                return nullptr;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return nullptr;
        }

        unsigned long fsize;
        try{
            char *ptr;
            fsize = strtol(fileSize.c_str(), &ptr, 10);
            if(fsize <= 0){
#ifdef MV_DEBUG
                addError("fsize == 0");
#endif
                return nullptr;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return nullptr;
        }

        jstring new_file_size;
        try{
            new_file_size = (jstring)env->CallObjectMethod(inObject, readLineID);
            if(checkExceptions(env)){
                return nullptr;
            }
            if(new_file_size == nullptr){
#ifdef MV_DEBUG
                addError("new_file_size == nullptr");
#endif
                return nullptr;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return nullptr;
        }

        std::string new_fileSize;
        try{
            isCopy = false;
            const char *new_filesize = env->GetStringUTFChars(new_file_size, &isCopy);
            if(checkExceptions(env)){
                return nullptr;
            }
            new_fileSize = new_filesize;
            env->ReleaseStringUTFChars(new_file_size, new_filesize);
            if(checkExceptions(env)){
                return nullptr;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return nullptr;
        }

        unsigned long new_fsize;
        try{
            char *ptr;
            new_fsize = strtol(new_fileSize.c_str(), &ptr, 10);
            if(new_fsize <= 0){
#ifdef MV_DEBUG
                addError("new_fsize == 0");
#endif
                return nullptr;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return nullptr;
        }

        jstring encoded;
        try{
            encoded = (jstring)env->CallObjectMethod(inObject, readLineID);
            if(checkExceptions(env)){
                return nullptr;
            }
            if(encoded == nullptr){
#ifdef MV_DEBUG
                addError("encoded == nullptr");
#endif
                return nullptr;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return nullptr;
        }

        std::vector<char> outBuf(1, 0);
        try{
            // decode
            isCopy = false;
            const char *inBuf = env->GetStringUTFChars(encoded, &isCopy);
            if(checkExceptions(env)){
                return nullptr;
            }

            unsigned long inlen;
            unsigned long outlen;
            inlen = strlen(inBuf);
            if(inlen <= 0){
                env->ReleaseStringUTFChars(encoded, inBuf);
                if(checkExceptions(env)){
                    return nullptr;
                }
                return nullptr;
            }

//            unsigned long n,r;
//            n = inlen/4;
//            r = inlen - 4 * n;
//            if(r > 0)
//                outlen = 4 + 4 * n;
//            else
//                outlen = 4 * n;
            outlen = 4 + 3*inlen/4;

            outBuf.resize(outlen + 1);

            hasan(reinterpret_cast<const unsigned char *>(inBuf), inlen,
                  reinterpret_cast<unsigned char *>(outBuf.data()), &outlen);

//            if(fabs(outlen - new_fsize) > 32){
//                env->ReleaseStringUTFChars(encoded, inBuf);
//                return nullptr;
//            }

            if(is_encoded){
                adam_buffer(outBuf.data(), outlen, MV_DECODE);
            }

            env->ReleaseStringUTFChars(encoded, inBuf);
            if(checkExceptions(env)){
                return nullptr;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return nullptr;
        }

        try{
            // delete old version
            std::remove(newFilename.c_str());

            // save file
            std::ofstream wf(newFilename, std::ios::out | std::ios::binary);
            if(!wf) {
#ifdef MV_DEBUG
                addError("Cannot create file!");
#endif
                return nullptr;
            }
            wf.write(outBuf.data(), fsize);
            wf.flush();
            wf.close();
            if(!wf.good()) {
                std::remove(newFilename.c_str());
#ifdef MV_DEBUG
                addError("Error occurred at writing time!");
#endif
                return nullptr;
            }
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return nullptr;
        }

        return env->NewStringUTF(newFilename.c_str());
    }catch(...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        jclass exception_cls = (env)->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(exception_cls, "Error in sendFileNative");
        if(checkExceptions(env)){
            return nullptr;
        }
//        throw std::runtime_error("Error in receiveFileNative");
    //        addError("Error in receiveFileNative");
        return nullptr;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT void JNICALL
Java_com_oghab_mapviewer_mapviewer_tcp_1io_1handler_encode_1file(JNIEnv *env, jclass clazz,
                                                                 jstring in_file,
                                                                 jstring out_file) {
#ifdef MV_DEBUG
    const char* temp_in_file = env->GetStringUTFChars(in_file, NULL);
    const char* temp_out_file = env->GetStringUTFChars(out_file, NULL);
    adam_file(temp_in_file, temp_out_file, MV_ENCODE);
    env->ReleaseStringUTFChars(in_file, temp_in_file);  // release resources
    env->ReleaseStringUTFChars(out_file, temp_out_file);  // release resources
#endif
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT void JNICALL
Java_com_oghab_mapviewer_mapviewer_tcp_1io_1handler_decode_1file(JNIEnv *env, jclass clazz,
                                                                 jstring in_file,
                                                                 jstring out_file) {
#ifdef MV_DEBUG
    const char* temp_in_file = env->GetStringUTFChars(in_file, NULL);
    const char* temp_out_file = env->GetStringUTFChars(out_file, NULL);
    adam_file(temp_in_file, temp_out_file, MV_DECODE);
    env->ReleaseStringUTFChars(in_file, temp_in_file);  // release resources
    env->ReleaseStringUTFChars(out_file, temp_out_file);  // release resources
#endif
}
//---------------------------------------------------------------------------
