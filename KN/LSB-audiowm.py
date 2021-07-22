import wave
import struct
import sys


# 这个函数需要改,目的是将分存内容转换成bit
def int2bit(data, bitlen=8):
    bits = []
    for byte in data:
        for i in range(0, bitlen):
            bits.append((byte & (2 ** i)) >> i)
    return bits


# 添加水印，输入源wav路径，水印数据，结果wav输出路径
def wmadd(src_path, wm_data, res_path):
    # wmdata对应的bit 格式为tuple
    wm_bit = struct.unpack("%dB" % len(wm_data), wm_data.encode())
    wm_size = len(wm_bit)
    # 在wm_bit前添加32位的长度标识,逆序
    wm_bit_fin = []
    wm_bit_fin.extend(int2bit((wm_size,), bitlen=32))
    # print(wm_bit)
    # print(wm_bit_fin)
    wm_bit_fin.extend(int2bit(wm_bit))
    # print(wm_bit_fin)

    src_audio = wave.open(src_path, "rb")

    nchannels = src_audio.getnchannels()  # 声道数
    sampwidth = src_audio.getsampwidth()  # 采样大小
    framerate = src_audio.getframerate()  # 采样率
    nframenum = src_audio.getnframes()    # 采样点数
    ncomptype = src_audio.getcomptype()
    ncompname = src_audio.getcompname()

    frames = src_audio.readframes(nframenum * nchannels)  # 读取声音数据
    samples = struct.unpack_from("%dh" % nframenum*nchannels, frames)

    if len(samples) < len(wm_bit_fin):
        print("ERROR watermark overflow")
        exit(-1)

    samples_marked = []
    wm_position = 0

    for sample in samples:
        sample_marked = sample
        if wm_position < len(wm_bit_fin):
            if wm_bit_fin[wm_position] == 1:
                sample_marked = sample | wm_bit_fin[wm_position]
            else:
                sample_marked = sample
                if sample & 1 != 0:
                    sample_marked = sample - 1
            wm_position += 1

        samples_marked.append(sample_marked)

    res_audio = wave.open(res_path, 'wb')
    res_audio.setparams((nchannels, sampwidth, framerate, nframenum, ncomptype, ncompname))
    res_audio.writeframes(struct.pack("%dh" % len(samples_marked), *samples_marked))


# 提取水印信息
def wmget(marked_path):
    audio_marked = wave.open(marked_path, "rb")

    nchannels = audio_marked.getnchannels()  # 声道数
    sampwidth = audio_marked.getsampwidth()  # 采样大小
    framerate = audio_marked.getframerate()  # 采样率
    nframenum = audio_marked.getnframes()  # 采样点数
    ncomptype = audio_marked.getcomptype()
    ncompname = audio_marked.getcompname()

    frames = audio_marked.readframes(nframenum * nchannels)  # 读取声音数据
    samples = struct.unpack_from("%dh" % nframenum*nchannels, frames)

    wm_size = 0
    for (sample, i) in zip(samples[0:32], range(0,32)):
        wm_size += (sample & 1) * (2 ** i)

    wm_data = []

    for n in range(0, wm_size):
        wm_samples = samples[32 + (n*8): 32 + (n+1) * 8]
        temp = 0
        for (sample, i) in zip(wm_samples, range(0, 8)):
            temp += (sample & 1) * (2 ** i)  # 这步还原也需要改,将bit还原成编码
        wm_data.append(temp)

    return wm_data


if __name__ == "__main__":
    src = "src.wav"
    res = "res.wav"
    # data直接把分存图转个格式之后还原吧
    data = "testmark"

    # 用于自定义输入
    # if len(sys.argv) > 1:
    #     data = sys.argv[1]
    #     if len(sys.argv) > 2:
    #         src = sys.argv[2]
    #         if len(sys.argv) > 3:
    #             res = sys.argv[3]

    # 添加水印
    wmadd(src, data, res)
    print(wmget(res))