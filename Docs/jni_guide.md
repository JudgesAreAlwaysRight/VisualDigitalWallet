# 如何在安卓中调用C++代码 - JNI使用指南

简单的说，由于java的JVM是一个虚拟机，同时java不存在指针类型，所以需要JNI(Java Native Interface)来进行内存数据的转换。将java中的类型转换到内存中可以直接访问的指针，同时允许外部代码直接操作java开辟的内存空间（实际原理略有出入，但是无关紧要）

对于接口的java端而言，只需要声明好需要调用的函数即可，函数的参数直接用java自己的类型书写即可（比如int数组写成 `int[]` 等）

对于接口的c++端而言，需要使用JNI规定的几种类型作为传入参数类型，其余的操作没有限制。

以下是将c++代码嵌入安卓java代码的步骤：

1. 在安卓的java代码中合适位置添加函数的java声明。
2. 使用 `javah` 指令，利用前面的java声明自动生成c++函数头文件，这一步jni将会自动把参数的数据类型进行转换。
3. 根据刚刚生成的`.h`头文件，编写对应的`.cpp`文件将其实现
4. 在java中进行静态链接
5. 在android的编译生成脚本中将动态链接库附加到项目

在我们的项目中，步骤4和5已经完成，后续不需要进行任何改动。步骤1、2和3在下面详细说明

### 1) 添加声明
本项目中，所有java调用c++函数的c++实现均位于`<项目根目录>/app/src/main/cpp`目录。
所有java声明均位于`<项目根目录>/app/src/main/java/com/visualwallet/cpplib/CppLib.java`。

java的函数声明形式与c++类似。这里以之前的本地校验分存图的函数声明为例。
``` java
    public static native boolean detect(byte[] androidID, int n, int splitID, int[][] s0, int[][] s1, int sRows, int sCols, int[][] toDetect, int detectSize);
```
`public`是类的访问控制符，一律使用`public`，`static`表示这是类的静态函数，`native`表示这是本地链接函数（使用c++的）。这三个控制符一般不动，直接照抄。函数参数方面，java中的基本数据类型与c++类似，但是c++`bool`类型的java名字是`boolean`，c++的`char*`类型应当使用java的`byte[]`类型，所有数组类型使用`<类型名>[]`进行声明，几维数组加几对中括号。此外，数组不能使用sizeof获取长度，务必手动传入长度。

### 2) 自动生成头文件
接下来，在 `<项目根目录>/app/src/main/java` 目录下，使用以下命令自动生成java函数对应的c++头文件
```bash
javah -jni com.visualwallet.cpplib.CppLib
```
运行该命令后，在 `<项目根目录>/app/src/main/java` 中会出现一个.h文件，将其移动到 `<项目根目录>/app/src/main/cpp` 中。

### 3) 完善c++代码
接下来是工作的重点，将原本的c++代码改成使用JNI类型。这个blog的指南十分详细：https://blog.csdn.net/u011208984/article/details/105222438/ 务必先阅读该blog再写代码

在转换c++代码使用jni类型时，除了基本数据类型（int、bool等）不需要转换，其余的都要使用jni库进行转换。所以尽量少用string，map等高级对象，它们的转换相对麻烦，传入参数时尽量用数组，返回值务必先封装再返回（基本类型不需要封装直接返回）。如果你必须传入复杂类型或返回复杂类型，上面的blog有十分详细的说明，比如string、map等类型如何传入传出。

javah生成的c++头文件函数中，会先在参数表前面加上两个参数，env和jcls，它们分别是jni提供的数据访问和转换库，以及调用cpp函数的java类指针。这里面env尤其重要，所有的`j某某`复杂类型转换到c++可以直接访问的类型都需要通过这个对象来进行。参数表的后面是java函数声明中每个参数转换出的jni参数。

具体每一种类型如何转换在上面的blog中有详细说明，这里仅仅举例之前的detect函数的编写过程。注意，当你在本项目中新增函数时候，不要建立新的cpp文件，只需要在`<项目根目录>/app/src/main/jni/com_visualwallet_cpplib_CppLib.cpp`文件中新增函数的实现即可。
```c++
#include "com_visualwallet_cpplib_CppLib.h"
#include <iostream>
using namespace std;

JNIEXPORT jboolean JNICALL Java_com_visualwallet_cpplib_CppLib_detect(
        JNIEnv *env,
        jclass jcls,
        jbyteArray androidID,
        jint n,
        jint splitID,
        jobjectArray s0,
        jobjectArray s1,
        jint sRows,
        jint sCols,
        jobjectArray toDetect,
        jint detectSize) {
    // 转换java数组到c++指针类型
    jbyte *c_AndroidID = env->GetByteArrayElements(androidID, NULL);
    jint **c_s0 = new jint *[sRows];
    jint **c_s1 = new jint *[sRows];
    for (int i = 0; i < sRows; i++) {
        c_s0[i] = env->GetIntArrayElements((jintArray) env->GetObjectArrayElement(s0, i), NULL);
        c_s1[i] = env->GetIntArrayElements((jintArray) env->GetObjectArrayElement(s1, i), NULL);
    }
    jint **c_toDetect = new jint *[detectSize];
    for (int i = 0; i < detectSize; i++) {
        c_toDetect[i] = env->GetIntArrayElements(
                (jintArray) env->GetObjectArrayElement(toDetect, i), NULL);
    }

    // 后面的代码是c++原本的代码，一句都没改（好像改了几个变量名避免冲突）
    bool isCheater = false;
    // 这里省略50行
    return isCheater;
}

```

看上面的代码，这里将没有改动的代码省略了，java的二维数组传入c++是需要将其封装成jobjectArray形式，在这个array对象中的每一项都是一个jintArray。你需要手动指定array的起始地址与偏移量等等来进行转换。转换后的`jint**`是基础类型的二维数组，可以直接被c++读取使用，用法和`int**`完全一致，但是不能直接作为返回值，如果你需要返回二维数组，需要将其重新包装回jobjectArray。具体方法见blog。
